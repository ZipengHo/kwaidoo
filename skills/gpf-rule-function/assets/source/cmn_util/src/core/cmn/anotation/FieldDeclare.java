package cmn.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 属性声明
 * @What: 标识需要暴露给外部输入的属性
 * @Why: 
 * @How: 在 类上需要暴露给外部的属性上添加此注解
 * @Author 陈晓斌
 * @CreateTime : 2024年11月8日
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface FieldDeclare {
	/**
	 * 参数名称
	 * @return
	 */
	String label() default "";
	/**
	 * 参数声明
	 * @return
	 */
	String desc() default "";
	/**
	 * 参数是否可为空。
	 * @return
	 */
	boolean nullable() default false;
	/**
	 * 允许值枚举。
	 * @return
	 */
	String[] enums() default {};
	/**
	 * 数值最小值。
	 * @return
	 */
	double minimum() default Double.NaN;
	/**
	 * 数值最大值。
	 * @return
	 */
	double maximum() default Double.NaN;
	/**
	 * 字符串最小长度。
	 * @return
	 */
	int minLength() default -1;
	/**
	 * 字符串最大长度。
	 * @return
	 */
	int maxLength() default -1;
	/**
	 * 正则表达式。
	 * @return
	 */
	String pattern() default "";
	/**
	 * 集合最小元素数量。
	 * @return
	 */
	int minItems() default -1;
	/**
	 * 集合最大元素数量。
	 * @return
	 */
	int maxItems() default -1;
}
