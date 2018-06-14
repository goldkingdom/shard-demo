package cn.xj.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheInfo extends BaseBean {

    private Long key;
    private String type;
    private String method;
    private int groupCount;
    private int nodeCount;

}
