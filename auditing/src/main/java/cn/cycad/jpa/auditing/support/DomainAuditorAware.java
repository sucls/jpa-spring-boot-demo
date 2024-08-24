package cn.cycad.jpa.auditing.support;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author sucl
 * @date 2024/6/15 18:17
 * @since 1.0.0
 */
public class DomainAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("张某");
    }
}
