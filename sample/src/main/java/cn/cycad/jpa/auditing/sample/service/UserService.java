package cn.cycad.jpa.auditing.sample.service;

import cn.cycad.jpa.auditing.sample.entity.User;
import cn.cycad.jpa.auditing.sample.repository.UserRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sucl
 * @date 2024/6/19 18:39
 * @since 1.0.0
 */
@Transactional
@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    @PrePersist
    public void PrePersist(User user){
        System.out.println("保存前" + user);
    }

    @PreUpdate
    public void PreUpdate(User user){
        System.out.println("更新前"+ user);
    }
}
