package cn.xj.common.container;

import cn.xj.common.annotation.Meta;
import cn.xj.common.annotation.Model;
import cn.xj.common.model.MetaInfo;
import cn.xj.common.util.ConvertUtil;
import cn.xj.common.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "contextContainer")
public class ContextContainer extends BaseContainer {

    //类容器
    private Map<String, Class> classContainer = Maps.newHashMap();
    //域容器
    private Map<String, Model> modelContainer = Maps.newHashMap();
    //字段数据容器
    private Map<String, Map<String, MetaInfo>> fieldContainer = Maps.newHashMap();
    //列容器
    private Map<String, Map<String, String>> columnContainer = Maps.newHashMap();
    //指令容器
    private Map<String, Map<String, StringBuilder>> instructionContainer = Maps.newHashMap();
    //节点信息容器
    private Map<String, List<Map<String, List<String>>>> nodeInfoContainer = Maps.newHashMap();

    public void loadContext(String className) throws Exception {
        Class clazz = Class.forName(this.getBaseProperties().getBeanPath() + "." + className);
        this.loadContext(clazz);
    }

    public void loadContext(Class<?> clazz) throws Exception {
        String key = clazz.getSimpleName();
        if (this.classContainer.get(key) != null) {
            return;
        }
        this.classContainer.put(key, clazz);
        //获取类上的注解
        Model model = clazz.getAnnotation(Model.class);
        this.modelContainer.put(key, model);
        //获取字段上的注解
        List<Field> fieldList = Lists.newArrayList();
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
        Field[] fields = clazz.getDeclaredFields();
        fieldList.addAll(Arrays.asList(superFields));
        fieldList.addAll(Arrays.asList(fields));
        String field, type, column, pKey;
        Meta meta;
        MetaInfo info;
        Map<String, MetaInfo> fieldMap = Maps.newHashMap();
        Map<String, String> columnMap = Maps.newHashMap();
        Map<String, StringBuilder> instructionMap = Maps.newHashMap();
        StringBuilder prefix = new StringBuilder(), suffix = new StringBuilder();
        for (Field f : fieldList) {
            field = f.getName();
            meta = f.getAnnotation(Meta.class);
            type = f.getType().getSimpleName();
            if (meta != null) {
                if ("".equals(meta.column())) {
                    column = field;
                } else {
                    column = meta.column();
                }
                info = MetaInfo.builder()
                        .pKey(meta.pKey())
                        .nullable(meta.nullable())
                        .column(meta.column())
                        .type(type.substring(type.lastIndexOf(".") + 1))
                        .generator(meta.generator())
                        .defaultValue(meta.defaultValue())
                        .defaultCheck(meta.defaultCheck()).build();
                fieldMap.put(field, info);
                columnMap.put(column, field);
                if (info.isPKey() && !"idWorker".equals(info.getGenerator())) {
                    pKey = "".equals(meta.column()) ? field : meta.column();
                    StringBuilder update = new StringBuilder("update " + this.modelContainer.get(key).table() + " set @Update where " + pKey + "={" + field + "}");
                    StringBuilder delete = new StringBuilder("delete from " + this.modelContainer.get(key).table() + " where " + pKey + "={" + field + "}");
                    instructionMap.put("update", update);
                    instructionMap.put("delete", delete);
                } else {
                    prefix.append(",").append(column);
                    suffix.append(",{").append(field).append("}");
                }
            }
        }
        StringBuilder insert = new StringBuilder("insert into " + this.modelContainer.get(key).table() + " (" + prefix.substring(1) + ") values (" + suffix.substring(1) + ")");
        instructionMap.put("insert", insert);
        this.fieldContainer.put(key, fieldMap);
        this.columnContainer.put(key, columnMap);
        this.instructionContainer.put(key, instructionMap);
    }

    @SuppressWarnings("unchecked")
    public Map map2map(Map map, String className) {
        Map<String, MetaInfo> fieldMap = this.getFieldContainer().get(className);
        for (Map.Entry<String, MetaInfo> entry : fieldMap.entrySet()) {
            map.putIfAbsent(entry.getKey(), map.get(entry.getValue().getColumn()));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public <T> T map2bean(Map map, String className) throws InstantiationException, IllegalAccessException {
        Map<String, MetaInfo> metaInfoMap = this.getFieldContainer().get(className);
        for (Map.Entry<String, MetaInfo> entry : metaInfoMap.entrySet()) {
            if (entry.getValue().getType().equals("Date")) {
                map.put(entry.getKey(), DateUtil.timestamp2Date(ConvertUtil.obj2Long(map.get(entry.getKey()))));
            } else {
                map.put(entry.getKey(), ConvertUtil.convertByType(map.get(entry.getKey()), entry.getValue().getType()));
            }
        }
        return (T) ConvertUtil.map2bean(map, this.getClassContainer().get(className));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> maps2beans(List<Map> maps, String className) throws InstantiationException, IllegalAccessException {
        Map<String, MetaInfo> metaInfoMap;
        for (Map map : maps) {
            metaInfoMap = this.getFieldContainer().get(className);
            for (Map.Entry<String, MetaInfo> entry : metaInfoMap.entrySet()) {
                if (entry.getValue().getType().equals("Date")) {
                    map.put(entry.getKey(), DateUtil.timestamp2Date(ConvertUtil.obj2Long(map.get(entry.getKey()))));
                } else {
                    map.put(entry.getKey(), ConvertUtil.convertByType(map.get(entry.getKey()), entry.getValue().getType()));
                }
            }
        }
        List<T> list = ConvertUtil.maps2beans(maps, this.getClassContainer().get(className));
        return list;
    }

}
