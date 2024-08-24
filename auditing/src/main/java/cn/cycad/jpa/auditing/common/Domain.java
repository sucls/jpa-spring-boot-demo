package cn.cycad.jpa.auditing.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sucl
 * @date 2024/6/15 18:08
 * @since 1.0.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class Domain implements Serializable {

    @CreatedBy
    @Column(name = "creator", length = 56)
    private String creator;

    @CreatedDate
    @Column(name = "create_time", length = 12)
    private Date createTime;

    @LastModifiedBy
    @Column(name = "modifier", length = 56)
    private String modifier;

    @LastModifiedDate
    @Column(name = "modified_time", length = 12)
    private Date modifiedTime;

}
