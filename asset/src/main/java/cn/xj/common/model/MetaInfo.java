package cn.xj.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaInfo {

    private boolean pKey;
    private boolean nullable;
    private String column;
    private String type;
    private String generator;
    private String defaultValue;
    private String defaultCheck;

}
