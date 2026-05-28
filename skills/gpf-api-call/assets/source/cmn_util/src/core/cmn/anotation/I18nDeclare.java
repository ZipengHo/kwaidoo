package cmn.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @What: 国际化资源注解声明
 * @Why: 支撑需要国际化的类及属性声明
 * @How: 在类和静态属性上填写此注解
 * @Author 陈晓斌
 * @CreateTime : 2024年11月6日
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface I18nDeclare {
	/**
	 * 是否缺省组资源
	 * @return
	 */
	boolean defaultGroup() default false;
}
