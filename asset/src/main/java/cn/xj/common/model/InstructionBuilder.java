package cn.xj.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructionBuilder extends BaseBean {

    private String handle; // 句柄
    private Object param; // 参数

}
