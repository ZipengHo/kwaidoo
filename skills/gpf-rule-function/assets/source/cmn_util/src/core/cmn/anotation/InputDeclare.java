package cmn.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @What: 对方法中的参数进行声明
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月13日
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface InputDeclare {
	/**
	 * 参数名
	 * @return
	 */
	String name();
	/**
	 * 参数标签
	 * @return
	 */
	String label();
	/**
	 * 参数声明
	 * @return
	 */
	String desc();
	/**
	 * 参数样例值
	 * @return
	 */
	String exampleValue() default "";
	/**
	 * 参数是否可为空
	 * @return
	 */
	boolean nullable() default false;
}
