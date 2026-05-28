package cmn.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cmn.anotation.FieldDeclare;
import cmn.exception.VerifyException;

/**
 * 基于 {@link FieldDeclare} 的 DTO 反射校验器。
 */
public final class DtoFieldValidator {

	private DtoFieldValidator() {
	}

	public static void validate(Object dto) throws VerifyException {
		if (dto == null) {
			return;
		}
		IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
		validateValue(dto, dto.getClass().getSimpleName(), visited);
	}

	private static void validateValue(Object value, String path, IdentityHashMap<Object, Boolean> visited) throws VerifyException {
		if (value == null) {
			return;
		}
		if (isSimpleValueType(value.getClass())) {
			return;
		}
		if (visited.containsKey(value)) {
			return;
		}
		visited.put(value, Boolean.TRUE);

		if (value instanceof Collection<?>) {
			int index = 0;
			for (Object item : (Collection<?>) value) {
				validateValue(item, path + "[" + index + "]", visited);
				index++;
			}
			return;
		}
		if (value instanceof Map<?, ?>) {
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
				String childPath = path + "[" + String.valueOf(entry.getKey()) + "]";
				validateValue(entry.getValue(), childPath, visited);
			}
			return;
		}
		if (value.getClass().isArray()) {
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				validateValue(Array.get(value, i), path + "[" + i + "]", visited);
			}
			return;
		}

		for (Field field : getAllFields(value.getClass())) {
			if (shouldSkipField(field)) {
				continue;
			}
			field.setAccessible(true);
			Object fieldValue;
			try {
				fieldValue = field.get(value);
			} catch (IllegalAccessException e) {
				throw new VerifyException("读取字段[" + path + "." + field.getName() + "]失败！", e);
			}
			validateField(field, fieldValue, path, visited);
		}
	}

	private static void validateField(Field field, Object fieldValue, String parentPath, IdentityHashMap<Object, Boolean> visited) throws VerifyException {
		FieldDeclare rule = field.getAnnotation(FieldDeclare.class);
		String fieldPath = parentPath + "." + field.getName();
		String fieldLabel = getFieldLabel(field);

		if (rule != null) {
			validateNullRule(rule, fieldValue, fieldLabel, fieldPath);
		}
		if (fieldValue == null) {
			return;
		}
		if (rule != null) {
			validateStringRule(rule, field, fieldValue, fieldLabel, fieldPath);
			validateNumberRule(rule, fieldValue, fieldLabel, fieldPath);
			validateEnumRule(rule, fieldValue, fieldLabel, fieldPath);
			validateCollectionRule(rule, fieldValue, fieldLabel, fieldPath);
		}
		validateValue(fieldValue, fieldPath, visited);
	}

	private static void validateNullRule(FieldDeclare rule, Object fieldValue, String fieldLabel, String fieldPath) throws VerifyException {
		if (!rule.nullable() && fieldValue == null) {
			throw new VerifyException("字段[" + fieldLabel + "]不能为空！path=" + fieldPath);
		}
	}

	private static void validateStringRule(FieldDeclare rule, Field field, Object fieldValue, String fieldLabel, String fieldPath) throws VerifyException {
		if (!(fieldValue instanceof CharSequence)) {
			return;
		}
		String value = fieldValue.toString();
		if (rule.minLength() >= 0 && value.length() < rule.minLength()) {
			throw new VerifyException("字段[" + fieldLabel + "]长度不能小于" + rule.minLength() + "！path=" + fieldPath);
		}
		if (rule.maxLength() >= 0 && value.length() > rule.maxLength()) {
			throw new VerifyException("字段[" + fieldLabel + "]长度不能大于" + rule.maxLength() + "！path=" + fieldPath);
		}
		if (rule.pattern() != null && !rule.pattern().isEmpty() && !Pattern.matches(rule.pattern(), value)) {
			throw new VerifyException("字段[" + fieldLabel + "]格式不合法！path=" + fieldPath + ", pattern=" + rule.pattern());
		}
		if (!isStringField(field.getType()) && (rule.minLength() >= 0 || rule.maxLength() >= 0 || !rule.pattern().isEmpty())) {
			throw new VerifyException("字段[" + fieldLabel + "]不是字符串类型，不能使用字符串约束！path=" + fieldPath);
		}
	}

	private static void validateNumberRule(FieldDeclare rule, Object fieldValue, String fieldLabel, String fieldPath) throws VerifyException {
		if (Double.isNaN(rule.minimum()) && Double.isNaN(rule.maximum())) {
			return;
		}
		if (!(fieldValue instanceof Number)) {
			throw new VerifyException("字段[" + fieldLabel + "]不是数值类型，不能使用数值范围约束！path=" + fieldPath);
		}
		BigDecimal value = new BigDecimal(String.valueOf(fieldValue));
		if (!Double.isNaN(rule.minimum())) {
			BigDecimal minimum = BigDecimal.valueOf(rule.minimum());
			if (value.compareTo(minimum) < 0) {
				throw new VerifyException("字段[" + fieldLabel + "]不能小于" + rule.minimum() + "！path=" + fieldPath);
			}
		}
		if (!Double.isNaN(rule.maximum())) {
			BigDecimal maximum = BigDecimal.valueOf(rule.maximum());
			if (value.compareTo(maximum) > 0) {
				throw new VerifyException("字段[" + fieldLabel + "]不能大于" + rule.maximum() + "！path=" + fieldPath);
			}
		}
	}

	private static void validateEnumRule(FieldDeclare rule, Object fieldValue, String fieldLabel, String fieldPath) throws VerifyException {
		String[] enums = rule.enums();
		if (enums == null || enums.length == 0) {
			return;
		}
		String value = String.valueOf(fieldValue);
		for (String enumValue : enums) {
			if (enumValue != null && enumValue.equals(value)) {
				return;
			}
		}
		throw new VerifyException("字段[" + fieldLabel + "]取值不合法！path=" + fieldPath + ", value=" + value);
	}

	private static void validateCollectionRule(FieldDeclare rule, Object fieldValue, String fieldLabel, String fieldPath) throws VerifyException {
		int size = -1;
		if (fieldValue instanceof Collection<?>) {
			size = ((Collection<?>) fieldValue).size();
		} else if (fieldValue.getClass().isArray()) {
			size = Array.getLength(fieldValue);
		}
		if (size < 0) {
			if (rule.minItems() >= 0 || rule.maxItems() >= 0) {
				throw new VerifyException("字段[" + fieldLabel + "]不是集合或数组类型，不能使用集合数量约束！path=" + fieldPath);
			}
			return;
		}
		if (rule.minItems() >= 0 && size < rule.minItems()) {
			throw new VerifyException("字段[" + fieldLabel + "]元素个数不能小于" + rule.minItems() + "！path=" + fieldPath);
		}
		if (rule.maxItems() >= 0 && size > rule.maxItems()) {
			throw new VerifyException("字段[" + fieldLabel + "]元素个数不能大于" + rule.maxItems() + "！path=" + fieldPath);
		}
	}

	private static Field[] getAllFields(Class<?> clazz) {
		java.util.List<Field> fields = new java.util.ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			for (Field field : current.getDeclaredFields()) {
				fields.add(field);
			}
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	private static boolean shouldSkipField(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic();
	}

	private static boolean isStringField(Class<?> type) {
		return CharSequence.class.isAssignableFrom(type);
	}

	private static boolean isSimpleValueType(Class<?> type) {
		if (type.isPrimitive()) {
			return true;
		}
		if (Number.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type) || Boolean.class == type
				|| Character.class == type || java.util.Date.class.isAssignableFrom(type)
				|| java.time.temporal.Temporal.class.isAssignableFrom(type) || Enum.class.isAssignableFrom(type)
				|| Class.class == type) {
			return true;
		}
		Package pkg = type.getPackage();
		return pkg != null && "java.lang".equals(pkg.getName());
	}

	private static String getFieldLabel(Field field) {
		FieldDeclare rule = field.getAnnotation(FieldDeclare.class);
		if (rule != null && rule.label() != null && !rule.label().isEmpty()) {
			return rule.label();
		}
		return field.getName();
	}
}
