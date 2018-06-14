package cn.xj.project.model;

import cn.xj.common.annotation.Meta;
import cn.xj.common.annotation.Model;
import cn.xj.common.model.BaseModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "emp")
@Model(table = "emp")
public class Emp extends BaseModel {

    @Column(name = "name")
    @Meta(column = "name")
    private String name;

    @Column(name = "dept_no")
    @Meta(column = "dept_no")
    private String deptNo;

    @Column(name = "age")
    @Meta(column = "age", defaultValue = "20")
    private Integer age;

    @Column(name = "email")
    @Meta(column = "email", defaultValue = "123@abc.com")
    private String email;

    @Column(name = "address")
    @Meta(column = "address")
    private String address;

    @Column(name = "date")
    @Meta(column = "date")
    private Date date;

}
