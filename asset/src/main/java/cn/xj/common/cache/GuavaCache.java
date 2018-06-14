package cn.xj.common.cache;

import cn.xj.common.model.BaseInfo;
import cn.xj.common.model.CacheInfo;
import cn.xj.common.stream.Stream;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Monitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GuavaCache {

    private CacheInfo cacheInfo;

    public CacheInfo getCacheInfo() {
        return cacheInfo;
    }

    public void setCacheInfo(CacheInfo cacheInfo) {
        this.cacheInfo = cacheInfo;
    }

    private Cache<Long, List> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public void put(Long key, List value) {
        cache.put(key, value);
    }

    private Monitor monitor = new Monitor();

    /**
     * 消息接收时的判断依据
     */
    private final Monitor.Guard valueAbsent = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
//            System.out.println(">>>>valueAbsent====" + GuavaCache.this.getCacheInfo().getKey());
            if ("query".equals(GuavaCache.this.getCacheInfo().getType())) {
                if ("queryById".equals(GuavaCache.this.getCacheInfo().getMethod())) {
                    boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() == 0;
                    return flag;
                }
                boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() < GuavaCache.this.getCacheInfo().getGroupCount();
                return flag;
            }
            boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() < GuavaCache.this.getCacheInfo().getNodeCount();
            return flag;
        }
    };

    @StreamListener(value = Stream.QUERIEDIN, condition = "headers['node']=='${baseProperties.node}'")
    @SuppressWarnings("unchecked")
    public void queriedInReceive(BaseInfo info) throws InterruptedException {
        this.setCacheInfo(info.getCacheInfo());
        monitor.enterWhen(valueAbsent);
//        System.out.println(">>>>receive====" + info.getCacheInfo().getKey());
        try {
//            System.out.println("====stop 1s");
//            Thread.sleep(1000);
            cache.getIfPresent(info.getId()).add(info.getQueryResult());
        } finally {
            monitor.leave();
        }
    }

    @StreamListener(value = Stream.MODIFIEDIN, condition = "headers['node']=='${baseProperties.node}'")
    @SuppressWarnings("unchecked")
    public void modifiedInReceive(BaseInfo info) throws InterruptedException {
        this.setCacheInfo(info.getCacheInfo());
        monitor.enterWhen(valueAbsent);
//        System.out.println(">>>>receive====" + info.getCacheInfo().getKey());
        try {
//            System.out.println("====stop 1s");
//            Thread.sleep(1000);
            cache.getIfPresent(info.getId()).add(info.getModifyResult());
        } finally {
            monitor.leave();
        }
    }

    /**
     * 获取结果时的判断依据
     */
    private Monitor.Guard valuePresent = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
//            System.out.println(">>>>valuePresent====" + GuavaCache.this.getCacheInfo().getKey());
            if ("query".equals(GuavaCache.this.getCacheInfo().getType())) {
                if ("queryById".equals(GuavaCache.this.getCacheInfo().getMethod())) {
                    boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() == 1;
                    return flag;
                }
                boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() < GuavaCache.this.getCacheInfo().getGroupCount();
                return flag;
            }
            boolean flag = cache.getIfPresent(GuavaCache.this.getCacheInfo().getKey()).size() < GuavaCache.this.getCacheInfo().getNodeCount();
            return flag;
        }
    };

    public List loadResult(CacheInfo info) throws InterruptedException {
        this.setCacheInfo(info);
        monitor.enterWhen(valuePresent);
//        System.out.println(">>>>load====" + info.getKey());
        try {
            return cache.getIfPresent(info.getKey());
        } finally {
            monitor.leave();
        }
    }

}
