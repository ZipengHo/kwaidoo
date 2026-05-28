package cmn.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;

import cn.hutool.core.util.StrUtil;

public class StringUtil extends StrUtil{

	public static String replaceAll(String sSrc,Map<String,String> regexMap)throws Exception{
		return ToolUtilities.replaceAll(sSrc, regexMap);
	}
	
	/**
	 * 对目标对象中的文本属性进行去空格处理
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static <T> T trimObject(T object)throws Exception{
		if(object == null)
			return null;
		if(object instanceof String)
			return (T) StringUtil.trim(object.toString());
		else if(object.getClass().isPrimitive() || ClassUtil.isPrimitiveWrapper(object.getClass()))
			return object;
		List<Field> fields = ToolUtilities.getAllField(object.getClass(), false);
		for(Field field : fields) {
			Class type = field.getType();
			if(type == String.class) {
				String value = (String) ToolUtilities.getFieldValue(object, field);
				if(value != null) {
					String trimValue = StringUtil.trim(value);
					ToolUtilities.setFieldValue(object, field, trimValue);
				}
			}else if(CmnUtil.isInheritFrom(type, List.class)) {
				List list = (List)ToolUtilities.getFieldValue(object, field);
				if(list != null) {
					List copyList = new ArrayList<>(list);
					for(int i =0;i<copyList.size();i++) {
						Object ele = copyList.get(i);
						Object trimEle = trimObject(ele);
						list.set(i, trimEle);
					}
				}
			}else if(type.isArray()) {
				// 遍历数组
                Object array = ToolUtilities.getFieldValue(object, field);
                if (array != null)
                {
                    int len = Array.getLength(array);
                    for (int i = 0; i < len; i++)
                    {
                    	Object ele = Array.get(array, i);
                    	Object trimEle = trimObject(ele);
                    	Array.set(array, i, trimEle);
                    }
                }
			} else if (CmnUtil.isInheritFrom(type, Map.class))
            {
                Map map = (Map) ToolUtilities.getFieldValue(object, field);
                if(map!= null) {
	                Map copyMap = new LinkedHashMap(map);
	                for (Object key : copyMap.keySet())
	                {
	                	Object value = copyMap.get(key);
	                	Object trimValue = trimObject(value);
	                	Object trimKey = trimObject(key);
	                	map.remove(key);
	                	map.put(trimKey, trimValue);
	                }
                }
            } 
		}
		return object;
	}
}
