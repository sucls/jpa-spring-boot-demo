package cn.cycad.jpa.auditing.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sucl
 * @date 2024/5/8 21:40
 * @since 1.0.0
 */
@EnableTransactionManagement
@EnableJpaAuditing
@EntityScan({"cn.cycad.jpa.auditing.sample.entity"})
@EnableJpaRepositories(basePackages="cn.cycad.jpa.auditing.sample.repository")
@SpringBootApplication
public class SampleApplication{

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
