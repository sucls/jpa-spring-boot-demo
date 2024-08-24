package cn.cycad.jpa.comment.bean;

import cn.cycad.jpa.comment.annotation.Comment;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * CommentIntegrator
 *
 * @author byw
 * @date 2021/12/13
 */
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
