package cn.cycad.jpa.auditing.sample.web;

import cn.cycad.jpa.auditing.sample.entity.User;
import cn.cycad.jpa.auditing.sample.repository.UserRepository;
import cn.cycad.jpa.auditing.sample.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sucl
 * @date 2024/5/8 21:38
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @param user
     * @return
     */
    @PostMapping
    public User saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

}
