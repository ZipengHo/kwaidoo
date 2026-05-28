package cmn.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @What: 对方法中的返回值进行声明
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月13日
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface OuputDeclare {
	/**
	 * 参数名称
	 * @return
	 */
	String label();
	/**
	 * 参数声明
	 * @return
	 */
	String desc();
}