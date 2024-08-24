## jpa-spring-boot-auditing

实现通用字段的自定填充，比如创建人、创建时间、修改人、修改时间

### 实现方式

在实体类上添加注解

1. 定义通用实体，比如`Domain`类，我们会将通用字段在这里定义
```java
@MappedSuperclass
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
```

2. 添加Auditing相关注解
```java
// ...
@EntityListeners(AuditingEntityListener.class)
public class Domain implements Serializable {
    // ...
}
```

3. 添加自动填充属性实现，主要通过实现`AuditorAware`接口，并将实现注入到spring容器
```java
public class DomainAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("我是操作人");
    }
}

```
4. 定义spring自动配置
```java
@Configuration
public class JpaAuditingConfiguration {

    @Bean
    public DomainAuditorAware domainAuditorAware(){
        return new DomainAuditorAware();
    }

}
```

在resource目录添加自动注入配置`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，这样通过引入jar就可以自动使用该配置
```
cn.cycad.jpa.auditing.config.JpaAuditingConfiguration
```

### 应用示例

1. 比如现在有一个`User`实体，我们通过继承基类
```java
@Entity
@Table(name = "t_user")
@Data
public class User extends Domain {

    @Id
    private String id;

    private String caption;

}
```

2. 定义用户对应的`Repository`
```java
public interface UserRepository extends JpaRepository<User,String> {
    
}
```

3. 用户的创建与修改基于`UserRepository`来实现
```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserRepository userRepository;
    
    @PostMapping
    public User saveUser(@RequestBody User user){
        return userRepository.save(user);
    }

}
```

4. 服务
```java
@EnableJpaAuditing
@EntityScan({"cn.cycad.jpa.auditing.sample.entity"})
@EnableJpaRepositories(basePackages="cn.cycad.jpa.auditing.sample.repository")
@SpringBootApplication
public class SampleApplication{

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
```
通过注解`@EnableJpaAuditing`启用

5. 编写测试用例
```http request
### 新增用户
POST http://localhost:8080/user
Content-Type: application/json

{
  "id": "1",
  "caption": "tom"
}
```

这样每次调用用户新增请求时，默认会将`DomainAuditorAware`的返回值填充到`@CreatedBy`与`@LastModifiedBy`修饰的字段上去

###  实现原理

可以看到，实现该效果狐妖有以下几个关键点：
1. 实体需要添加`@EntityListeners(AuditingEntityListener.class)`，并且需要再对应字段上标识出需要注入的操作人、操作时间等
2. 需要编写自己的实现`AuditorAware<String>`，这里只用关注创建人，时间没必要处理，当然也可以通过实现接口`DateTimeProvider`来扩展
3. 需要基于`JpaRepository`接口实现用户的新增或修改
4. 需要`@EnableJpaAuditing`开启


### 实现扩展

