package cmn.util;

import java.lang.reflect.InvocationTargetException;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.javac.ClassFactory;
/**
 * 反射工具类
 * @author chenxb
 *
 */
public class ReflectUtil {

	public static Class loadClass(String className) throws ClassNotFoundException {
		return ClassFactory.getValidClassLoader().loadClass(className);
	}
	
	public static <T> T newInstance(Class<T> clazz) throws Exception {
		return ClassFactory.newInstance(clazz);
	}
	
	public static <T> T newInstance(Class<T> clazz,Object... params) throws Exception {
		return ClassFactory.newInstance(clazz,params);
	}
	
	public static Object callFunction(Object object,String function,Object... params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return ToolUtilities.callFunction(object, function, params);
	}
	
	public static Object asynCallFunction(Object object,String function,Object... params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return ToolUtilities.asynCallFunction(object, function, params);
	}
	
}
