package cn.cycad.jpa.comment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author byw
 * @Date 2021/12/13 14:27
 */
@Configuration
public class CommentConfig {

//	@Bean
//	public CommentIntegrator commentIntegrator(){
//		return new CommentIntegrator();
//	}

	@Bean
	public HibernateProperties hibernateProperties(){
		return new HibernateProperties();
	}
}
