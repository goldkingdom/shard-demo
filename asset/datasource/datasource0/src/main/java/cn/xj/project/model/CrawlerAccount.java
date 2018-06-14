package cn.xj.project.model;

import cn.xj.common.annotation.Meta;
import cn.xj.common.annotation.Model;
import cn.xj.common.model.BaseModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "t_scf_crawler_account")
@Model(table = "t_scf_crawler_account")
public class CrawlerAccount extends BaseModel {

    @Column(name = "platform_code")
    @Meta(column = "platform_code")
    private String platformCode;
    @Column(name = "platform_name")
    @Meta(column = "platform_name")
    private String platformName;
    @Column(name = "areaCode")
    @Meta(column = "areaCode")
    private String areaCode;
    @Column(name = "enterprise_id")
    @Meta(column = "enterprise_id")
    private Long enterpriseId;
    @Column(name = "username")
    @Meta(column = "username")
    private String username;
    @Column(name = "password")
    @Meta(column = "password")
    private String password;

}
