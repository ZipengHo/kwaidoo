package basic.cmn.event;

import gpf.dc.basic.fe.enums.EnumUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class JsonSchemaDto implements Serializable {
    /**
     * 声明该 Schema 遵循哪个版本的 JSON Schema 规范（如 Draft-07）。 这是必须的。
     */
    String $schema;
    /**
     * 为该 Schema 提供一个全局唯一的 URI 标识符。
     */
    String $id;
    /**
     * Schema 的可读名称
     */
    String title;
    /**
     * Schema 的详细描述
     */
    String description;
    /**
     * Schema 的类型，通常是 "object"
     * 类型包括 "object", "array", "string", "number", "integer", "boolean", "null"
     */
    String type;
    /**
     * Schema 的格式，例如 "email", "date-time" 等
     */
    String format;
    /**
     * 仅用于 type: "string"。定义字符串的最小长度。
     */
    Integer minLength;
    /**
     * 仅用于 type: "string"。定义字符串的最大长度。
     */
    Integer maxLength;
    /**
     * 仅用于 type: "string"。定义字符串必须匹配的正则表达式模式。
     */
    String pattern;
    /**
     * Schema 的属性，当 type 为 "object" 时使用
     */
    LinkedHashMap<String,JsonSchemaDto> properties;
    /**
     * 仅用于 type: "array"。定义数组中元素的 Schema。
     */
    List<JsonSchemaDto> items;
    /**
     * Schema 中必填的属性列表，当 type 为 "object" 时使用
     */
    List<String> required;
    /**
     * 仅用于 type: "object"。设置为 false 时，禁止出现未在 properties 中定义的额外字段。
     */
    Boolean additionalProperties;

    public JsonSchemaDto(){

    }
    public JsonSchemaDto(boolean setSchema){
        this.$schema = "http://json-schema.org/draft-07/schema#";
    }

    public String get$schema() {
        return $schema;
    }

    public JsonSchemaDto set$schema(String $schema) {
        this.$schema = $schema;
        return this;
    }

    public String get$id() {
        return $id;
    }

    public JsonSchemaDto set$id(String $id) {
        this.$id = $id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonSchemaDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JsonSchemaDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public JsonSchemaDto setType(String type) {
        this.type = type;
        return this;
    }

    public SchemaType getTypeEnum() {
        return EnumUtil.getEnumByField(SchemaType.class,"value",type);
    }

    public JsonSchemaDto setTypeEnum(SchemaType type) {
        if(type == null) {
            this.type = null;
        }else {
            this.type = type.getValue();
        }
        return this;
    }

    public String getFormat() {
        return format;
    }

    public JsonSchemaDto setFormat(String format) {
        this.format = format;
        return this;
    }

    public LinkedHashMap<String, JsonSchemaDto> getProperties() {
        return properties;
    }

    public JsonSchemaDto setProperties(LinkedHashMap<String, JsonSchemaDto> properties) {
        this.properties = properties;
        return this;
    }

    public List<JsonSchemaDto> getItems() {
        return items;
    }

    public JsonSchemaDto setItems(List<JsonSchemaDto> items) {
        this.items = items;
        return this;
    }

    public List<String> getRequired() {
        return required;
    }

    public JsonSchemaDto setRequired(List<String> required) {
        this.required = required;
        return this;
    }

    public boolean isAdditionalProperties() {
        return additionalProperties != null && additionalProperties;
    }

    public JsonSchemaDto setAdditionalProperties(boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public JsonSchemaDto setMinLength(Integer minLength) {
        this.minLength = minLength;
        return this;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public JsonSchemaDto setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public JsonSchemaDto setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
}
