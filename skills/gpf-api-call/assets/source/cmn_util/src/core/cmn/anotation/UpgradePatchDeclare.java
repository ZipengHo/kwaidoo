package cmn.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @What: 补丁类声明注解，描述补丁类的类型、版本和变更信息
 * @Why: 
 * @How: 在类上添加此注解
 * @Author 陈晓斌
 * @CreateTime : 2025-01-08
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface UpgradePatchDeclare {
	/**
	 * 补丁版本，使用日期格式：如20250108
	 * @return
	 */
	long version();
	/**
	 * 补丁类型，用于区分不同的补丁
	 * @return
	 */
	String type();
	/**
	 * 补丁说明
	 * @return
	 */
	String desc();
}
