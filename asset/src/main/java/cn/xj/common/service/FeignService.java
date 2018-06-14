package cn.xj.common.service;

import cn.xj.common.api.FeignApi;
import cn.xj.common.container.ContextContainer;
import cn.xj.common.model.BaseInfo;
import cn.xj.common.property.BaseProperties;
import com.google.common.collect.Lists;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.ribbon.LBClient;
import feign.ribbon.RibbonClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeignService {

    @Autowired
    private ContextContainer contextContainer;

    @Autowired
    private BaseProperties baseProperties;

    private FeignApi feignApi(String business) throws Exception {
        RibbonClient client = RibbonClient.builder().lbClientFactory(clientName -> {
            IClientConfig config = ClientFactory.getNamedConfig(clientName);
            ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
            ZoneAwareLoadBalancer zb = (ZoneAwareLoadBalancer) lb;
            zb.setRule(new RandomRule());
            return LBClient.create(lb, config);
        }).build();
        return Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(FeignApi.class, "http://" + business);
    }

    public List query(BaseInfo info) throws Exception {
        List list = this.feignApi(baseProperties.getBusiness()).query(info);
        return list;
    }

    public List queryOnes(BaseInfo info) throws Exception {
        List list = this.feignApi(baseProperties.getBusiness()).queryOnes(info);
        return list;
    }

    public Object queryOne(BaseInfo info) throws Exception {
        Object obj = this.feignApi(baseProperties.getBusiness()).queryOne(info);
        return obj;
    }

    public boolean save(BaseInfo info) throws Exception {
        boolean flag = this.feignApi(baseProperties.getBusiness()).save(info);
        return flag;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> queryBeans(BaseInfo info) throws Exception {
        List<Map> maps = this.query(info);
        List<Map> list = Lists.newArrayList();
        list.addAll(list.stream().map(map -> contextContainer.map2map(map, info.getClassName())).collect(Collectors.toList()));
        List<T> beans = contextContainer.maps2beans(maps, info.getClassName());
        return beans;
    }

    @SuppressWarnings("unchecked")
    public Map queryById(BaseInfo info) throws Exception {
        Map map = this.feignApi(baseProperties.getBusiness()).queryById(info);
        return map;
    }

    @SuppressWarnings("unchecked")
    public <T> T queryBeanById(BaseInfo info) throws Exception {
        Map map = this.queryById(info);
        if (map == null) {
            return null;
        }
        Map result = contextContainer.map2map(map, info.getClassName());
        return contextContainer.map2bean(result, info.getClassName());
    }

    @SuppressWarnings("unchecked")
    public boolean saveBean(BaseInfo info) throws Exception {
        boolean flag = this.feignApi(baseProperties.getBusiness()).saveBean(info);
        return flag;
    }

}
