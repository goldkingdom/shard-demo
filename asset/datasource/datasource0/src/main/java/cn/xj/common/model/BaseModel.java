package cn.xj.common.model;

import cn.xj.common.annotation.Meta;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class BaseModel extends BaseBean {

    @Id
    @Column(name = "id", nullable = false)
    @Meta(column = "id", pKey = true, generator = "idWorker")
    private Long id;

}
