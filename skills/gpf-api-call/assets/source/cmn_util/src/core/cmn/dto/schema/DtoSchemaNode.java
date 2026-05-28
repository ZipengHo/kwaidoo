package cmn.dto.schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO 字段结构节点。
 * 用于描述一个字段、一个数组元素、一个对象结构或一个引用节点的元数据。
 */
public class DtoSchemaNode implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 引用路径。
	 * 当当前节点不是内联定义而是引用 definitions 中的其他结构时使用。
	 * 例如：#/definitions/UserDto
	 */
	private String ref;
	/**
	 * 节点类型。
	 * 常见值：object、array、string、integer、number、boolean。
	 */
	private String type;
	/**
	 * 节点对应的 Java 全限定类名。
	 * 主要用于保留结构来源信息。
	 */
	private String javaType;
	/**
	 * 字段标题。
	 * 一般来源于 FieldDeclare.label。
	 */
	private String title;
	/**
	 * 字段描述。
	 * 一般来源于 FieldDeclare.desc。
	 */
	private String description;
	/**
	 * 是否允许为空。
	 * 语义与 FieldDeclare.nullable 一致。
	 */
	private Boolean nullable;
	/**
	 * 格式信息。
	 * 常用于 string 类型的补充格式说明，例如 date-time。
	 */
	private String format;
	/**
	 * 必填字段列表。
	 * 仅在 type=object 时生效，表示对象下哪些属性不能为空。
	 */
	private List<String> required;
	/**
	 * 允许值枚举列表。
	 */
	private List<String> enumValues;
	/**
	 * 数值最小值。
	 */
	private Double minimum;
	/**
	 * 数值最大值。
	 */
	private Double maximum;
	/**
	 * 字符串最小长度。
	 */
	private Integer minLength;
	/**
	 * 字符串最大长度。
	 */
	private Integer maxLength;
	/**
	 * 正则表达式约束。
	 */
	private String pattern;
	/**
	 * 集合最小元素数量。
	 */
	private Integer minItems;
	/**
	 * 集合最大元素数量。
	 */
	private Integer maxItems;
	/**
	 * 对象属性定义集合。
	 * key 为属性名，value 为属性的结构节点。
	 */
	private Map<String, DtoSchemaNode> properties;
	/**
	 * 数组或集合的元素结构定义。
	 * 仅在 type=array 时生效。
	 */
	private DtoSchemaNode items;
	/**
	 * 是否允许存在动态属性。
	 * 主要用于 Map 类型或开放对象结构。
	 */
	private Boolean additionalPropertiesEnabled;
	/**
	 * 动态属性的值结构定义。
	 * 当 additionalPropertiesEnabled=true 且能识别值类型时生效。
	 */
	private DtoSchemaNode additionalProperties;

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getNullable() {
		return nullable;
	}

	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}

	public Double getMinimum() {
		return minimum;
	}

	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}

	public Double getMaximum() {
		return maximum;
	}

	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public Map<String, DtoSchemaNode> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, DtoSchemaNode> properties) {
		this.properties = properties;
	}

	public DtoSchemaNode getItems() {
		return items;
	}

	public void setItems(DtoSchemaNode items) {
		this.items = items;
	}

	public Boolean getAdditionalPropertiesEnabled() {
		return additionalPropertiesEnabled;
	}

	public void setAdditionalPropertiesEnabled(Boolean additionalPropertiesEnabled) {
		this.additionalPropertiesEnabled = additionalPropertiesEnabled;
	}

	public DtoSchemaNode getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(DtoSchemaNode additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
