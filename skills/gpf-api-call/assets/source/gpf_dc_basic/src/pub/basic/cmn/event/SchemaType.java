package basic.cmn.event;

public enum SchemaType {
    /**
     * 对象类型，包含多个属性
     */
    Object("object"),
    /**
     * 数组类型，包含多个元素
     */
    Array("array"),
    /**
     * 字符串类型，包含多个字符
     */
    String("string"),
    /**
     * 数值类型，包括整数和浮点数
     */
    Number("number"),
    /**
     * 整数类型
     */
    Integer("integer"),
    /**
     * 布尔类型，只有 true 和 false 两个值
     */
    Boolean("boolean"),
    /**
     * 空类型，只有 null 一个值
     */
    Null("null");

    private String value;
    SchemaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
