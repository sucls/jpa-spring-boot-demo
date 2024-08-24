## jpa-spring-boot-comment

  在使用`spring-boot-starter-data-jpa`时，通过这样的配置可以在程序启动后实现在指定数据库自动建表

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```
  但是这种方式建表后没办法为每一列增加对应的中文注释，有什么办法可以实现这一需求呢？


后面再网上找到了实现方法：
```xml
        <dependency>
            <groupId>com.github.biyanwen</groupId>
            <artifactId>jpa-comment-spring-boot-starter</artifactId>
            <version>1.0.2</version>
        </dependency>
```
但是在当前项目中无效，后面发现部分依赖已经改变，应该是对高版本JPA不支持导致。今天基于该jar重新梳理实现过程


### 实现方式

基于自定义注解以及Spring自动配置实现。

1. 定义注解`Comment`，该注解定义在字段上，定义该列的中文描述
```java
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Comment {
    /**
     * 注释的值
     *
     * @return {@link String}
     */
    String value() default "";
}

```

2. spring jpa是基于Habernate实现，同样我们需要基于接口`org.hibernate.integrator.spi.Integrator`，在生成ddl时进行扩展
```java
public class CommentIntegrator implements Integrator {
    public static final CommentIntegrator INSTANCE = new CommentIntegrator();

    public CommentIntegrator() {
        super();
    }

    @Override
    public void integrate(Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
        processComment(metadata);
    }

    /**
     * Not used.
     *
     * @param sessionFactoryImplementor     The session factory being closed.
     * @param sessionFactoryServiceRegistry That session factory's service registry
     */
    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
    }

    /**
     * 生成注释代码
     *
     * @param metadata process annotation of this {@code Metadata}.
     */
    protected void processComment(Metadata metadata) {
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            Class<?> clz = persistentClass.getMappedClass();
            if (clz.isAnnotationPresent(Comment.class)) {
                Comment comment = clz.getAnnotation(Comment.class);
                persistentClass.getTable().setComment(comment.value());
            }
            Property identifierProperty = persistentClass.getIdentifierProperty();
            if (identifierProperty != null) {
                propertyComment(persistentClass, identifierProperty.getName());
            } else {
                org.hibernate.mapping.Component component = persistentClass.getIdentifierMapper();
                if (component != null) {
                    Iterator<Property> iterator = component.getPropertyIterator();
                    while (iterator.hasNext()) {
                        propertyComment(persistentClass, iterator.next().getName());
                    }
                }
            }
            Iterator<Property> iterator = persistentClass.getProperties().iterator();
            while (iterator.hasNext()) {
                propertyComment(persistentClass, iterator.next().getName());
            }
        }
    }

    /**
     * 为属性生成注释
     *
     * @param persistentClass Hibernate {@code PersistentClass}
     * @param columnName      name of field
     */
    private void propertyComment(PersistentClass persistentClass, String columnName) {
        try {
            String comment = getPropertyComment(persistentClass, columnName);
            Value value = persistentClass.getProperty(columnName).getValue();
            if( value.getColumns().iterator().hasNext() ){
                String sqlColumnName = value.getColumns().iterator().next().getText();
                Iterator<org.hibernate.mapping.Column> columnIterator = persistentClass.getTable().getColumns().iterator();
                while (columnIterator.hasNext()) {
                    org.hibernate.mapping.Column column = columnIterator.next();
                    if (sqlColumnName.equalsIgnoreCase(column.getName())) {
                        column.setComment(comment);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPropertyComment(PersistentClass persistentClass, String columnName) throws Exception {
        String comment = null;

        Field field = ReflectionUtils.findField(persistentClass.getMappedClass(), columnName);
        if (field != null) {
            if (field.isAnnotationPresent(Comment.class)) {
                comment = field.getAnnotation(Comment.class).value();
            } else {
                PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), persistentClass.getMappedClass());
                Method readMethod = descriptor.getReadMethod();
                Comment comment1 = readMethod.getAnnotation(Comment.class);
                if (comment1 != null) {
                    comment = comment1.value();
                }
            }
        }
        return comment;
    }
}
```

3. 定义配置类，对`HibernatePropertiesCustomizer`进行扩展
```java
public class HibernateProperties implements HibernatePropertiesCustomizer {
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.integrator_provider",    (IntegratorProvider) () -> Collections.singletonList(CommentIntegrator.INSTANCE));
    }
}
```
4. 定义spring配置，实现自动装配

在resource目录添加自动注入配置`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，这样通过引入jar就可以自动使用该配置
```
cn.cycad.jpa.comment.config.CommentConfig
```

### 应用示例

1. 比如现在有一个`User`实体，我们通过继承基类
```java
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
```

2. 启动服务后，可以看到控制台输出的建表语句信息
```
Hibernate: 
    create table t_user (
        id varchar(255) not null,
        create_time timestamp(6),
        creator varchar(56),
        modified_time timestamp(6),
        modifier varchar(56),
        age integer,
        caption varchar(255),
        primary key (id)
    )
Hibernate: 
    comment on column t_user.id is
        '业务主键'
Hibernate: 
    comment on column t_user.age is
        '用户年龄'
Hibernate: 
    comment on column t_user.caption is
        '用户名称'
```

###  实现原理


### 实现扩展

