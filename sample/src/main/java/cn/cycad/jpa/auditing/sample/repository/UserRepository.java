package cn.cycad.jpa.auditing.sample.repository;

import cn.cycad.jpa.auditing.sample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sucl
 * @date 2024/6/15 18:10
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User,String> {

}
