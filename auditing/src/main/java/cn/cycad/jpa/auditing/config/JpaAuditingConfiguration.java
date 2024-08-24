package cn.cycad.jpa.auditing.config;

import cn.cycad.jpa.auditing.support.DomainAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sucl
 * @date 2024/6/15 18:23
 * @since 1.0.0
 */
@Configuration
public class JpaAuditingConfiguration {

    @Bean
    public DomainAuditorAware domainAuditorAware(){
        return new DomainAuditorAware();
    }

}
