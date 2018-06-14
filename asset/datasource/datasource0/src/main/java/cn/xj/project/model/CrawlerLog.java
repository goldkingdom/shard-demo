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
@Table(name = "t_scf_crawler_log")
@Model(table = "t_scf_crawler_log")
public class CrawlerLog extends BaseModel {

    @Column(name = "enterprise_id")
    @Meta(column = "enterprise_id")
    private Long enterpriseId;
    @Column(name = "platform_code")
    @Meta(column = "platform_code")
    private String platformCode;
    @Column(name = "platform_name")
    @Meta(column = "platform_name")
    private String platformName;
    @Column(name = "username")
    @Meta(column = "username")
    private String username;
    @Column(name = "create_time")
    @Meta(column = "create_time")
    private String createTime;
    @Column(name = "start_time")
    @Meta(column = "start_time")
    private String startTime;
    @Column(name = "end_time")
    @Meta(column = "end_time")
    private String endTime;
    @Column(name = "status")
    @Meta(column = "status")
    private Boolean status;

}
