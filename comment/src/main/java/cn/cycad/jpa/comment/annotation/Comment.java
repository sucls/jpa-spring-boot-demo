package cn.cycad.jpa.comment.annotation;

import java.lang.annotation.*;

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
