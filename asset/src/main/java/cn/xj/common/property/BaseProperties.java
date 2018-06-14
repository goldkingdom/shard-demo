package cn.xj.common.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "baseProperties")
public class BaseProperties {

    private String version;
    private String beanPath;
    private String business;
    private String group;
    private String node;
    private int groupIndex;
    private int nodeIndex;
    private int groupCount;
    private int nodeCount;
    private List<String> groups;

}
