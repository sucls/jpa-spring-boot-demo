package cn.cycad.jpa.auditing.sample.entity;

import cn.cycad.jpa.auditing.common.Domain;
import cn.cycad.jpa.comment.annotation.Comment;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author sucl
 * @date 2024/5/8 21:47
 * @since 1.0.0
 */
@Entity
@Table(name = "t_user")
@Data
public class User extends Domain {

    @Id
    @Comment("业务主键")
    private String id;

    @Comment("用户名称")
    private String caption;

    @Comment("用户年龄")
    private Integer age;

}
