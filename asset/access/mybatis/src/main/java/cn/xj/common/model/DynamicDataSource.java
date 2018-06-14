package cn.xj.common.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dataSource")
public class DynamicDataSource {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

}
