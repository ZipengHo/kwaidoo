package cmn.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @What: 方法元数据声明
 * @Why: 
 * @How: 在需要暴露给外部使用的方法上添加此注解
 * @Author 陈晓斌
 * @CreateTime : 2024年11月8日
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MethodDeclare {

	/**
	 * 标签
	 * @return
	 */
	String label();
	/**
	 * 描述是什么，方法的用途
	 * @return
	 */
	String what();
	/**
	 * 描述为什么，需求的由来
	 * @return
	 */
	String why();
	/**
	 * 描述如何使用，使用场景和调用代码样例
	 * @return
	 */
	String how();
	/**
	 * 填写开发人
	 * @return
	 */
	String developer() default "";
	/**
	 * 填写版本，如：1.0，
	 * 第一位版本代表大版本，变更表示功能不能向下兼容，
	 * 第二位代表对bug修复或补充新特性后更新的版本号，功能可向下兼容
	 * @return
	 */
	String version() default "";
	/**
	 * 填写方法的创建时间，格式：yyyy-MM-dd
	 * @return
	 */
	String createTime() default "";
	/**
	 * 填写方法的更新时间，格式：yyyy-MM-dd
	 * @return
	 */
	String updateTime() default "";
	/**
	 * 填写方法的输入参数声明
	 * @return
	 */
	InputDeclare[] inputs() ;
	/**
	 * 填写方法的返回结果声明
	 * @return
	 */
	OuputDeclare output() default @OuputDeclare(label="",desc="");
	
}
