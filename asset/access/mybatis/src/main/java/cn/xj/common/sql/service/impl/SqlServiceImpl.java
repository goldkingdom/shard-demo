package cn.xj.common.sql.service.impl;

import cn.xj.common.cache.GuavaCache;
import cn.xj.common.container.ContextContainer;
import cn.xj.common.generator.IdWorker;
import cn.xj.common.model.BaseInfo;
import cn.xj.common.model.CacheInfo;
import cn.xj.common.model.MetaInfo;
import cn.xj.common.property.BaseProperties;
import cn.xj.common.sql.mapper.SqlMapper;
import cn.xj.common.sql.service.SqlService;
import cn.xj.common.stream.Stream;
import cn.xj.common.util.ConvertUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SqlServiceImpl implements SqlService {

    @Autowired
    private ContextContainer contextContainer;

    @Autowired
    private BaseProperties baseProperties;

    @Autowired
    private GuavaCache guavaCache;

    @Autowired
    private SqlMapper sqlMapper;

    private IdWorker getIdWorker() {
        IdWorker idWorker = new IdWorker(baseProperties.getGroupIndex() % 31, baseProperties.getNodeIndex() % 31);
        return idWorker;
    }

    private void initialize(BaseInfo info) {
        IdWorker idWorker = this.getIdWorker();
        info.setId(idWorker.nextId());
        info.setNode(baseProperties.getNode());
        info.load();
    }

    //保存时的校验
    private void saveCheck(Map<String, Object> params, String className) throws Exception {
        if (Strings.isNullOrEmpty(className)) {
            throw new RuntimeException("类名不能为空");
        }
        Map<String, MetaInfo> fieldMap = contextContainer.getFieldContainer().get(className);
        for (Map.Entry<String, MetaInfo> entry : fieldMap.entrySet()) {
            if (params.get(entry.getKey()) == null && entry.getValue() != null) {
                if (!entry.getValue().isNullable()) {
                    throw new RuntimeException(entry.getKey() + "不能为空");
                }
                if ("".equals(entry.getValue().getDefaultValue())) {
                    if (entry.getValue().isPKey()) {
                        if ("".equals(entry.getValue().getGenerator())) {
                            throw new RuntimeException("主键不能为空");
                        } else if ("idWorker".equals(entry.getValue().getGenerator())) {
                            params.put(entry.getKey(), this.getIdWorker().nextId());
                        }
                    }
                } else {
                    params.put(entry.getKey(), entry.getValue().getDefaultValue());
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List query(BaseInfo info) throws Exception {
        this.initialize(info);
        CacheInfo cacheInfo = CacheInfo.builder().key(info.getId()).type("query").groupCount(baseProperties.getGroupCount()).build();
        info.setCacheInfo(cacheInfo);
        guavaCache.put(info.getId(), Lists.newArrayList());
        contextContainer.getStream().queryOut().send(MessageBuilder.withPayload(info).setHeader("method", "query").build());
        List<List<Map>> list = guavaCache.loadResult(cacheInfo);
        List result = Lists.newArrayList();
        list.forEach(result::addAll);
        return result;
    }

    @StreamListener(value = Stream.QUERYIN, condition = "headers['method']=='query'")
    private void queryReceive(BaseInfo info) {
        info.setQueryResult(sqlMapper.query(info));
        contextContainer.getStream().queriedOut().send(MessageBuilder.withPayload(info).setHeader("node", info.getNode()).build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List queryOnes(BaseInfo info) throws Exception {
        this.initialize(info);
        CacheInfo cacheInfo = CacheInfo.builder().key(info.getId()).type("query").groupCount(baseProperties.getGroupCount()).build();
        info.setCacheInfo(cacheInfo);
        guavaCache.put(info.getId(), Lists.newArrayList());
        contextContainer.getStream().queryOut().send(MessageBuilder.withPayload(info).setHeader("method", "queryOnes").build());
        List<List<Object>> list = guavaCache.loadResult(cacheInfo);
        List result = Lists.newArrayList();
        list.forEach(result::addAll);
        return result;
    }

    @StreamListener(value = Stream.QUERYIN, condition = "headers['method']=='queryOnes'")
    private void queryOnesReceive(BaseInfo info) {
        info.setQueryResult(sqlMapper.queryOnes(info));
        contextContainer.getStream().queriedOut().send(MessageBuilder.withPayload(info).setHeader("node", info.getNode()).build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object queryOne(BaseInfo info) throws Exception {
        this.initialize(info);
        CacheInfo cacheInfo = CacheInfo.builder().key(info.getId()).type("query").groupCount(baseProperties.getGroupCount()).build();
        info.setCacheInfo(cacheInfo);
        guavaCache.put(info.getId(), Lists.newArrayList());
        contextContainer.getStream().queryOut().send(MessageBuilder.withPayload(info).setHeader("method", "queryOne").build());
        List<List<Object>> result = guavaCache.loadResult(cacheInfo);
        for (List<Object> list : result) {
            if (list.get(0) != null) {
                return list.get(0);
            }
        }
        return null;
    }

    @StreamListener(value = Stream.QUERYIN, condition = "headers['method']=='queryOne'")
    @SuppressWarnings("unchecked")
    private void queryOneReceive(BaseInfo info) {
        Object obj = sqlMapper.queryOne(info);
        List list = Lists.newArrayList();
        list.add(obj == null ? null : obj);
        info.setQueryResult(list);
        contextContainer.getStream().queriedOut().send(MessageBuilder.withPayload(info).setHeader("node", info.getNode()).build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean save(BaseInfo info) throws Exception {
        try {
            this.initialize(info);
            Long id = ConvertUtil.obj2Long(info.getParams().get("id"));
            if (id == null) {
                throw new RuntimeException("主键不能为空");
            }
            //根据id求模获取定位的数据节点，定向发送消息
            int index = ConvertUtil.obj2Int(id % baseProperties.getGroupCount());
            CacheInfo cacheInfo = CacheInfo.builder().key(info.getId()).nodeCount(baseProperties.getNodeCount()).build();
            info.setCacheInfo(cacheInfo);
            guavaCache.put(info.getId(), Lists.newArrayList());
            System.out.println(">>>>" + baseProperties.getGroup() + ": " + info.getParams().get("id"));
            contextContainer.getStream().modifyOut().send(MessageBuilder.withPayload(info).setHeader("method", "save")
                    .setHeader("group", baseProperties.getGroups().get(index)).build());
            guavaCache.loadResult(info.getCacheInfo());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @StreamListener(value = Stream.MODIFYIN, condition = "headers['method']=='save'&&headers['group']=='${baseProperties.group}'")
    private void saveReceive(BaseInfo info) {
//        System.out.println("====" + baseProperties.getGroup() + ": " + info.getParams().get("id"));
//        System.out.println(">>>>接收的group: " + baseProperties.getGroup());
        this.insert(info);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private void insert(BaseInfo info) {
        try {
            System.out.println("====id: " + info.getParams().get("id"));
            int i = sqlMapper.save(info);
            info.setModifyResult(i > 0);
            contextContainer.getStream().modifiedOut().send(MessageBuilder.withPayload(info).setHeader("node", info.getNode()).build());
            List<Boolean> list = guavaCache.loadResult(info.getCacheInfo());
            for (Boolean flag : list) {
                if (!flag) {
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(BaseInfo info) throws Exception {
        info.load();
        int i = sqlMapper.update(info);
        return i > 0;
    }

    @Override
    public boolean remove(BaseInfo info) throws Exception {
        info.load();
        int i = sqlMapper.remove(info);
        return i > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map queryById(BaseInfo info) throws Exception {
        Long id = ConvertUtil.obj2Long(info.getParams().get("id"));
        if (id == null) {
            return null;
        }
        this.initialize(info);
        //根据id求模获取定位的数据节点，定向发送消息
        int index = ConvertUtil.obj2Int(id % baseProperties.getGroupCount());
        CacheInfo cacheInfo = CacheInfo.builder().key(info.getId()).type("query").method("queryById").groupCount(baseProperties.getGroupCount()).build();
        info.setCacheInfo(cacheInfo);
        guavaCache.put(info.getId(), Lists.newArrayList());
        contextContainer.getStream().queryOut().send(MessageBuilder.withPayload(info).setHeader("method", "queryById")
                .setHeader("group", baseProperties.getGroups().get(index)).build());
        List<List<Map>> result = guavaCache.loadResult(cacheInfo);
        return result.get(0).size() == 0 ? null : result.get(0).get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean saveBean(BaseInfo info) throws Exception {
        Map<String, Object> params = ConvertUtil.bean2map(info.getBean());
        if (params == null) {
            throw new RuntimeException("插入对象为空");
        }
        this.saveCheck(params, info.getClassName());
        String sql = contextContainer.getInstructionContainer().get(info.getClassName()).get("insert").toString();
        info.setInstruction(new StringBuilder(sql));
        info.setParams(params);
        boolean flag = this.save(info);
        return flag;
    }

    @StreamListener(value = Stream.QUERYIN, condition = "headers['method']=='queryById'&&headers['group']=='${baseProperties.group}'")
    @SuppressWarnings("unchecked")
    private void queryByIdReceive(BaseInfo info) {
        info.setQueryResult(sqlMapper.query(info));
        contextContainer.getStream().queriedOut().send(MessageBuilder.withPayload(info).setHeader("node", info.getNode()).build());
    }

}
