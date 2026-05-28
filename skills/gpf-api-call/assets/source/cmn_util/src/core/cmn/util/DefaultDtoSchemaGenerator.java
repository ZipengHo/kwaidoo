package cmn.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cmn.anotation.FieldDeclare;
import cmn.dto.schema.DtoSchema;
import cmn.dto.schema.DtoSchemaNode;

/**
 * 默认 DTO 结构生成器。
 * 默认输出风格接近 JsonSchema，但接口本身不绑定具体协议。
 */
public class DefaultDtoSchemaGenerator implements DtoSchemaGeneratorIntf {

	@Override
	public DtoSchema generateSchema(Class<?> dtoClass) throws Exception {
		LinkedHashMap<Class<?>, String> visiting = new LinkedHashMap<>();
		LinkedHashMap<String, DtoSchemaNode> definitions = new LinkedHashMap<>();
		DtoSchemaNode rootNode = buildSchema(dtoClass, visiting, definitions, buildDefinitionName(dtoClass), false);

		DtoSchema schema = new DtoSchema();
		schema.setSchemaName(buildDefinitionName(dtoClass));
		schema.setRootJavaType(dtoClass == null ? null : dtoClass.getName());
		schema.setRoot(rootNode);
		schema.setDefinitions(definitions.isEmpty() ? null : definitions);
		return schema;
	}

	private DtoSchemaNode buildSchema(Class<?> clazz, LinkedHashMap<Class<?>, String> visiting,
									  LinkedHashMap<String, DtoSchemaNode> definitions, String definitionName, boolean asDefinition) throws Exception {
		if (clazz == null) {
			return new DtoSchemaNode();
		}
		if (isSimpleValueType(clazz)) {
			return buildSimpleTypeSchema(clazz);
		}
		if (clazz.isArray()) {
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("array");
			schema.setItems(buildSchema(clazz.getComponentType(), visiting, definitions, buildDefinitionName(clazz.getComponentType()), false));
			return schema;
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("array");
			schema.setItems(new DtoSchemaNode());
			return schema;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("object");
			schema.setAdditionalPropertiesEnabled(true);
			return schema;
		}
		if (visiting.containsKey(clazz)) {
			DtoSchemaNode refSchema = new DtoSchemaNode();
			refSchema.setRef(visiting.get(clazz));
			return refSchema;
		}

		String definitionRef = "#/definitions/" + definitionName;
		visiting.put(clazz, definitionRef);
		DtoSchemaNode objectSchema = new DtoSchemaNode();
		objectSchema.setType("object");
		objectSchema.setJavaType(clazz.getName());

		Map<String, DtoSchemaNode> properties = new LinkedHashMap<>();
		List<String> requiredFields = new ArrayList<>();
		for (Field field : getAllFields(clazz)) {
			if (shouldSkipField(field)) {
				continue;
			}
			DtoSchemaNode fieldSchema = buildFieldSchema(field, visiting, definitions);
			properties.put(field.getName(), fieldSchema);

			FieldDeclare declare = field.getAnnotation(FieldDeclare.class);
			if (declare != null && !declare.nullable()) {
				requiredFields.add(field.getName());
			}
		}
		objectSchema.setProperties(properties.isEmpty() ? null : properties);
		objectSchema.setRequired(requiredFields.isEmpty() ? null : requiredFields);

		visiting.remove(clazz);
		if (asDefinition) {
			return objectSchema;
		}
		if (!definitions.containsKey(definitionName)) {
			definitions.put(definitionName,
					buildSchema(clazz, visiting, definitions, definitionName, true));
		}
		DtoSchemaNode refSchema = new DtoSchemaNode();
		refSchema.setRef(definitionRef);
		return refSchema;
	}

	private DtoSchemaNode buildFieldSchema(Field field, LinkedHashMap<Class<?>, String> visiting,
										   LinkedHashMap<String, DtoSchemaNode> definitions) throws Exception {
		DtoSchemaNode fieldSchema = buildSchemaByType(field.getGenericType(), visiting, definitions,
				buildDefinitionName(field.getType()));
		FieldDeclare declare = field.getAnnotation(FieldDeclare.class);
		if (declare == null) {
			return fieldSchema;
		}
		if (!isEmpty(declare.label())) {
			fieldSchema.setTitle(declare.label());
		}
		if (!isEmpty(declare.desc())) {
			fieldSchema.setDescription(declare.desc());
		}
		fieldSchema.setNullable(declare.nullable());
		if (declare.enums().length > 0) {
			List<String> enumValues = new ArrayList<>();
			for (String enumValue : declare.enums()) {
				enumValues.add(enumValue);
			}
			fieldSchema.setEnumValues(enumValues);
		}
		if (!Double.isNaN(declare.minimum())) {
			fieldSchema.setMinimum(declare.minimum());
		}
		if (!Double.isNaN(declare.maximum())) {
			fieldSchema.setMaximum(declare.maximum());
		}
		if (declare.minLength() >= 0) {
			fieldSchema.setMinLength(declare.minLength());
		}
		if (declare.maxLength() >= 0) {
			fieldSchema.setMaxLength(declare.maxLength());
		}
		if (!isEmpty(declare.pattern())) {
			fieldSchema.setPattern(declare.pattern());
		}
		if (declare.minItems() >= 0) {
			fieldSchema.setMinItems(declare.minItems());
		}
		if (declare.maxItems() >= 0) {
			fieldSchema.setMaxItems(declare.maxItems());
		}
		return fieldSchema;
	}

	private DtoSchemaNode buildSchemaByType(Type type, LinkedHashMap<Class<?>, String> visiting,
											LinkedHashMap<String, DtoSchemaNode> definitions, String definitionName) throws Exception {
		if (type instanceof Class<?>) {
			return buildSchema((Class<?>) type, visiting, definitions, definitionName, false);
		}
		if (type instanceof ParameterizedType) {
			return buildParameterizedSchema((ParameterizedType) type, visiting, definitions, definitionName);
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType arrayType = (GenericArrayType) type;
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("array");
			schema.setItems(buildSchemaByType(arrayType.getGenericComponentType(), visiting, definitions, definitionName + "Item"));
			return schema;
		}
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (upperBounds != null && upperBounds.length > 0) {
				return buildSchemaByType(upperBounds[0], visiting, definitions, definitionName);
			}
		}
		return new DtoSchemaNode();
	}

	private DtoSchemaNode buildParameterizedSchema(ParameterizedType type, LinkedHashMap<Class<?>, String> visiting,
												   LinkedHashMap<String, DtoSchemaNode> definitions, String definitionName) throws Exception {
		Type rawType = type.getRawType();
		if (!(rawType instanceof Class<?>)) {
			return new DtoSchemaNode();
		}
		Class<?> rawClass = (Class<?>) rawType;
		if (Collection.class.isAssignableFrom(rawClass)) {
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("array");
			Type itemType = Object.class;
			Type[] actualTypes = type.getActualTypeArguments();
			if (actualTypes != null && actualTypes.length > 0) {
				itemType = actualTypes[0];
			}
			schema.setItems(buildSchemaByType(itemType, visiting, definitions, definitionName + "Item"));
			return schema;
		}
		if (Map.class.isAssignableFrom(rawClass)) {
			DtoSchemaNode schema = new DtoSchemaNode();
			schema.setType("object");
			schema.setAdditionalPropertiesEnabled(true);
			Type[] actualTypes = type.getActualTypeArguments();
			if (actualTypes != null && actualTypes.length >= 2) {
				schema.setAdditionalProperties(
						buildSchemaByType(actualTypes[1], visiting, definitions, definitionName + "Value"));
			}
			return schema;
		}
		return buildSchema(rawClass, visiting, definitions, definitionName, false);
	}

	private DtoSchemaNode buildSimpleTypeSchema(Class<?> clazz) {
		DtoSchemaNode schema = new DtoSchemaNode();
		if (String.class == clazz || CharSequence.class.isAssignableFrom(clazz) || Character.class == clazz || char.class == clazz) {
			schema.setType("string");
			return schema;
		}
		if (Boolean.class == clazz || boolean.class == clazz) {
			schema.setType("boolean");
			return schema;
		}
		if (Byte.class == clazz || byte.class == clazz || Short.class == clazz || short.class == clazz
				|| Integer.class == clazz || int.class == clazz || Long.class == clazz || long.class == clazz
				|| BigInteger.class == clazz) {
			schema.setType("integer");
			return schema;
		}
		if (Float.class == clazz || float.class == clazz || Double.class == clazz || double.class == clazz
				|| BigDecimal.class == clazz) {
			schema.setType("number");
			return schema;
		}
		if (java.util.Date.class.isAssignableFrom(clazz) || Temporal.class.isAssignableFrom(clazz)) {
			schema.setType("string");
			schema.setFormat("date-time");
			return schema;
		}
		if (Enum.class.isAssignableFrom(clazz)) {
			schema.setType("string");
			Object[] constants = clazz.getEnumConstants();
			if (constants != null) {
				List<String> enumValues = new ArrayList<>();
				for (Object constant : constants) {
					enumValues.add(String.valueOf(constant));
				}
				schema.setEnumValues(enumValues);
			}
			return schema;
		}
		schema.setType("object");
		return schema;
	}

	private boolean isSimpleValueType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}
		if (String.class == clazz || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz)
				|| Boolean.class == clazz || Character.class == clazz || java.util.Date.class.isAssignableFrom(clazz)
				|| Temporal.class.isAssignableFrom(clazz) || Enum.class.isAssignableFrom(clazz)
				|| BigDecimal.class == clazz || BigInteger.class == clazz) {
			return true;
		}
		Package pkg = clazz.getPackage();
		return pkg != null && "java.lang".equals(pkg.getName());
	}

	private Field[] getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			for (Field field : current.getDeclaredFields()) {
				fields.add(field);
			}
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	private boolean shouldSkipField(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic();
	}

	private String buildDefinitionName(Class<?> clazz) {
		if (clazz == null) {
			return "Anonymous";
		}
		if (clazz.isArray()) {
			return buildDefinitionName(clazz.getComponentType()) + "Array";
		}
		String simpleName = clazz.getSimpleName();
		return isEmpty(simpleName) ? clazz.getName().replace('.', '_') : simpleName;
	}

	private boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}
}
