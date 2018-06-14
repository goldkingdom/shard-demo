package cn.xj.common.container;

import cn.xj.common.property.BaseProperties;
import cn.xj.common.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
public class BaseContainer implements Serializable {

    @Autowired
    private BaseProperties baseProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Stream stream;

}
