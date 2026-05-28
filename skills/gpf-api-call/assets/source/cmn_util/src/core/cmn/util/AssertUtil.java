package cmn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.kwaidoo.ms.tool.CmnUtil;

import cmn.dto.model.FieldDataType;
import cmn.exception.VerifyException;
import cmn.i18n.CmnI18nConst;

public class AssertUtil {

	public static void isNull2(String name,Object value)throws VerifyException{
		if(value == null) {
			String errorMsg = CmnI18nConst.getString(CmnI18nConst.CANNOT_BE_NULL,name);
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 不可为null
	 * @param value
	 * @param errorMsg
	 * @throws VerifyException
	 */
	
	public static void isNull(Object value,String errorMsg)throws VerifyException{
		if(value == null) {
			throw new VerifyException(errorMsg);
		}
	}
	public static void isEmpty2(String name,Object value)throws VerifyException{
		if(CmnUtil.isObjectEmpty(value)) {
			String errorMsg = CmnI18nConst.getString(CmnI18nConst.CANNOT_BE_NULL,name);
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 不可为空，文本不可为空字符，集合不可是空集合
	 * @param value
	 * @param errorMsg
	 * @throws VerifyException
	 */
	public static void isEmpty(Object value,String errorMsg)throws VerifyException{
		if(CmnUtil.isObjectEmpty(value)) {
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 不在枚举值范围
	 * @param value
	 * @param enumClass
	 * @param errorMsg
	 * @throws VerifyException
	 */
	public static void isNotInEnum(String value,Class<? extends Enum> enumClass,String errorMsg)throws VerifyException{
		boolean match = false;
		List<String> names = new ArrayList<>();
		for(Enum enum1 : enumClass.getEnumConstants()) {
			if(CmnUtil.isStringEqual(enum1.name(), value)) {
				match = true;
			}
			names.add(enum1.name());
		}
		if(!match) {
			errorMsg = CmnUtil.isStringEmpty(errorMsg) ? CmnI18nConst.getString(CmnI18nConst.IS_NOT_IN_RANGE,value,String.join(",", names)) : errorMsg;
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 不在值范围
	 * @param value
	 * @param ranges
	 * @param errorMsg
	 * @throws Exception
	 */
	public static void isNotInValueRange(Object value,List<Object> ranges,String errorMsg)throws Exception{
		isEmpty(value, errorMsg);
		boolean match = false;
		List<String> names = new ArrayList<>();
		for(Object item : ranges) {
			if(value instanceof Integer) {
				if(CmnUtil.getInteger(value) == CmnUtil.getInteger(item)){
					match = true;
				}
			}else if(value instanceof Long) {
				if(CmnUtil.getLong(value) == CmnUtil.getLong(item)){
					match = true;
				}
			}else if(value instanceof Double) {
				if(CmnUtil.getDouble(value) == CmnUtil.getDouble(item)){
					match = true;
				}
			}else if(value instanceof Float) {
				if(CmnUtil.getFloat(value) == CmnUtil.getFloat(item)){
					match = true;
				}
			}else if(value instanceof Boolean){
				if(CmnUtil.getBoolean(value) == CmnUtil.getBoolean(item)){
					match = true;
				}
			}else  if(value instanceof Character) {
				if(((Character)value).charValue() == ((Character)item).charValue()){
					match = true;
				}
			}else if( value instanceof String) {
				if(value.toString().equals(item.toString())) {
					match = true;
				}
			}else {
				if(value.equals(item)) {
					match = true;
				}
			}
			names.add(""+item);
		}
		if(!match) {
			errorMsg = CmnUtil.isStringEmpty(errorMsg) ? CmnI18nConst.getString(CmnI18nConst.IS_NOT_IN_RANGE,value,String.join(",", names)) : errorMsg;
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 
	 * @param value
	 * @param regex
	 * @param errorMsg
	 * @throws Exception
	 */
	public static void isNotMatchRegex(String value,String regex,String errorMsg)throws VerifyException{
		isEmpty((Object)value, errorMsg);
		boolean match = value.matches(regex);
		if(!match) {
			throw new VerifyException(errorMsg);
		}
	}
	/**
	 * 值重复
	 * @param values
	 * @param errorMsg
	 * @throws Exception
	 */
	public static void isDuplicate(List<Object> values)throws Exception{
		Set<Object> duplicateValues = new LinkedHashSet<>();
		Set<Object> allValue = new LinkedHashSet<>();
		for(Object value : values) {
			if(allValue.contains(value)) {
				duplicateValues.add(value);
			}
			allValue.add(value);
		}
		if(duplicateValues.size() > 0)
			throw new VerifyException(CmnI18nConst.getString(CmnI18nConst.VALUE_IS_DUPLICATE, duplicateValues));
	}
	/**
	 * 断言给定集合是否存在重复值
	 * @param values
	 * @param errorMsg
	 * @throws VerifyException
	 */
	public static void isDuplicate(List<String> values,String errorMsg)throws VerifyException{
		Set<String> duplicateValues = new LinkedHashSet<>();
		Set<String> allValue = new LinkedHashSet<>();
		for(String value : values) {
			if(allValue.contains(value)) {
				duplicateValues.add(value);
			}
			allValue.add(value);
		}
		if(duplicateValues.size() > 0)
			throw new VerifyException(errorMsg+duplicateValues);
	}
	
	public static void isTrue(boolean value,String errorMsg) {
		if(value) {
			throw new VerifyException(errorMsg);
		}
	}
	
	public static void main(String[] args) throws Exception {
//		isEmpty(null, "sss");
//		isEmpty2("DD", null);
		isNotInEnum("Text", FieldDataType.class, null);
		isNotInValueRange(1, Arrays.asList(1), null);
		isNotInValueRange(1L, Arrays.asList(1L), null);
		isNotInValueRange(1.0f, Arrays.asList(1.0f), null);
		isNotInValueRange(1.0f, Arrays.asList(1.0d), null);
		isNotInValueRange(true, Arrays.asList(true), null);
		isNotInValueRange('b', Arrays.asList('b'), null);
		isNotInValueRange("bb", Arrays.asList("bb"), null);
		isDuplicate(Arrays.asList(1,2,1));
	}
}
