package cmn.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.leavay.common.util.MppContext;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.common.util.javac.ClassNameInfo;

import bap.BapClassScanner;

public class ClassUtil extends cn.hutool.core.util.ClassUtil{
	
	public static boolean UseClassGraphUtil = MppContext.getBoolean("UseClassGraphUtil", true);
	/**
	 * 获取类资源文件的字节数据
	 * @param path 包文件路径/res/xxx/xxx.png
	 * @return
	 * @throws IOException 
	 */
	public static byte[] getResourceBytes(String path) throws IOException {
		return ClassFactory.readReasource(path, true);
	}
	// 建议给定包过滤，否则会较慢
	@SuppressWarnings("rawtypes")
	public static Set<Class> searchSubClass(Class<?> superClass, String pkgFilter) throws IOException{
		return searchSubClass(superClass, pkgFilter, ClassFactory.getValidClassLoader());
	}
	
	@SuppressWarnings("rawtypes")
	public static Set<Class> searchSubClass(Class<?> superClass, String pkgFilter,ClassLoader... classloader) throws IOException{
		if(UseClassGraphUtil) {
			Map<String,ClassNameInfo> classNameInfoMap = BapClassScanner.searchSubClassInfo(superClass.getName());
			Set<Class> classes = new LinkedHashSet<>();
			for(ClassNameInfo classInfo : classNameInfoMap.values()) {
				try {
					Class<?> clazz = ClassFactory.loadClass(classInfo.getClassFullName());
					classes.add(clazz);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return classes;
		}else {
			Set<Class> classes = ClassFactory.searchSubClass(superClass, false);
			return classes;
		}
	}

	/**
	 * 数据是否基础类型
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static boolean isBasicType(Object object) throws Exception {
		if(object == null)
			return false;
		return object.getClass().isPrimitive() || cn.hutool.core.util.ClassUtil.isPrimitiveWrapper(object.getClass()) 
				|| object.getClass() == String.class;
	}
	/**
	 * 属性是否基础类型
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static boolean isBasicType(Field field) throws Exception {
		return field.getType().isPrimitive() || cn.hutool.core.util.ClassUtil.isPrimitiveWrapper(field.getType())
				|| field.getType() == String.class;
	}
	/**
	 * 对象是否派生于指定类
	 * @param clazz
	 * @param value
	 * @return
	 */
	public static boolean isAssignableFrom(Class clazz,Object value) {
		if(value == null)
			return false;
		return clazz.isAssignableFrom(value.getClass());
	}
	
	 /** 从 clazz 的 fieldName 字段（形如 List<T>）解析出 T 的 Class；解析失败返回 Object.class */
    public static Class<?> getListElementType(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            Type generic = field.getGenericType();
            if (!(generic instanceof ParameterizedType)) return Object.class;

            Type arg = ((ParameterizedType) generic).getActualTypeArguments()[0];

            if (arg instanceof Class<?>) {
                return (Class<?>) arg;                           // 例如 List<PDCNodeExcelRowDto>
            } else if (arg instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) arg).getRawType(); // 例如 List<Map<String, X>>
            } else if (arg instanceof WildcardType) {            // 例如 List<? extends Base>
                WildcardType wt = (WildcardType) arg;
                if (wt.getUpperBounds().length > 0 && wt.getUpperBounds()[0] instanceof Class<?>)
                    return (Class<?>) wt.getUpperBounds()[0];
                if (wt.getLowerBounds().length > 0 && wt.getLowerBounds()[0] instanceof Class<?>)
                    return (Class<?>) wt.getLowerBounds()[0];
            } else if (arg instanceof TypeVariable) {            // 例如 List<T extends Base>
                TypeVariable<?> tv = (TypeVariable<?>) arg;
                if (tv.getBounds().length > 0 && tv.getBounds()[0] instanceof Class<?>)
                    return (Class<?>) tv.getBounds()[0];
            }
        } catch (NoSuchFieldException ignore) {}
        return Object.class;
    }
}
