package gpf.adur.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.kwaidoo.ms.tool.ToolUtilities;

/**
 * 数据属性
 * @author chenxb
 *
 */
public class FormField implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8414913616953693394L;
	
	public final static String Code = "code";
	public final static String Name = "name";
	public final static String Description = "description";
	public final static String DataType = "dataType";
	public final static String Length = "length";
	public final static String Precision = "precision";
	public final static String IsNotNull = "isNotNull";
	public final static String DefaultValue = "defaultValue";
	public final static String ExtendInfo = "extendInfo";
	public final static String AssocFormModel = "assocFormModel";
	public final static String IsAssocMultiSelect = "isAssocMultiSelect";
	public final static String DependFormModel = "dependFormModel";
	public final static String IsDependInheritable = "isDependInheritable";
	public final static String TableFormModel = "tableFormModel";
	/**
	 * 属性编号
	 */
	String code;
	/**
	 * 属性名称
	 */
	public String name;
	/**
	 * 属性描述
	 */
	public String description;
	/**
	 * 属性类型
	 */
	String dataType;
	/**
	 * 属性长度，当属性类型为Text、Long、Decimal、Password时设置
	 */
	Integer length;
	/**
	 * 属性进度，当属性类型为Decimal时设置
	 */
	Integer precision;

	/**
	 * 是否可为空
	 */
	boolean isNotNull;
	/**
	 * 默认值
	 */
	String defaultValue;
	/**
	 * 附加信息
	 */
	BaseFormFieldExtend extendInfo;
	/**
	 * 关联表单模型
	 */
	String assocFormModel;
	
	/**
	 * 依赖模型
	 */
	List<String> dependFormModel;
	/**
	 * 依赖数据是否可依赖子模型数据
	 */
	Boolean isDependInheritable;
	/**
	 * 是否多选
	 */
	Boolean isAssocMultiSelect;
	/**
	 * 引用表单模型
	 */
	String tableFormModel;
	/**
	 * 是否重写
	 */
	boolean override = false;
	
	public FormField() {
	}
	
	public FormField(String code) {
		this.code = code;
	}
	/**
	 * 属性编号
	 * @return
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 在分配属性code之后，不允许修改
	 * @param code
	 * @return
	 */
	public FormField setCode(String code) {
		this.code = code;
		return this;
	}
	/**
	 * 属性名称
	 * @return
	 */
	public String getName() {
		return name;
	}

	public FormField setName(String name) {
		this.name = name;
		return this;
	}
	/**
	 * 属性描述
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	public FormField setDescription(String description) {
		this.description = description;
		return this;
	}
	/**
	 * 属性类型
	 * @return
	 */
	public String getDataType() {
		return dataType;
	}
	
	public DataType getDataTypeEnum() {
		return gpf.adur.data.DataType.fromValue(dataType);
	}

	public FormField setDataType(DataType dataType) {
		this.dataType = dataType.name();
		return this;
	}

	public FormField setDataType(String dataType) {
		this.dataType = dataType;
		return this;
	}
	/**
	 * 字段长度
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	public FormField setLength(Integer length) {
		this.length = length;
		return this;
	}
	/**
	 * 字段精度
	 * @return
	 */
	public Integer getPrecision() {
		return precision;
	}

	public FormField setPrecision(Integer precision) {
		this.precision = precision;
		return this;
	}
	/**
	 * 是否可为空
	 * @return
	 */
	public boolean isNotNull() {
		return isNotNull;
	}
	
	public FormField setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
		return this;
	}
	/**
	 * 默认值
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	public FormField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	/**
	 * 属性扩展配置信息
	 * @return
	 */
	public BaseFormFieldExtend getExtendInfo() {
		return extendInfo;
	}

	public FormField setExtendInfo(BaseFormFieldExtend extendInfo) {
		this.extendInfo = extendInfo;
		return this;
	}
	/**
	 * 关联表单模型
	 * @return
	 */
	public String getAssocFormModel() {
		return assocFormModel;
	}
	public FormField setAssocFormModel(String assocFormModel) {
		this.assocFormModel = assocFormModel;
		return this;
	}
	/**
	 * 是否允许多选
	 * @return
	 */
	public boolean isAssocMultiSelect() {
		return isAssocMultiSelect != null && isAssocMultiSelect;
	}

	public FormField setAssocMultiSelect(Boolean isAssocMultiSelect) {
		this.isAssocMultiSelect = isAssocMultiSelect;
		return this;
	}
	/**
	 * 引用表单模型
	 * @return
	 */
	public String getTableFormModel() {
		return tableFormModel;
	}

	public FormField setTableFormModel(String tableFormModel) {
		this.tableFormModel = tableFormModel;
		return this;
	}
	public List<String> getDependFormModel() {
		return dependFormModel;
	}
	public FormField setDependFormModel(List<String> dependFormModel) {
		this.dependFormModel = dependFormModel;
		return this;
	}
	
	public boolean isDependInheritable() {
		return isDependInheritable != null && isDependInheritable;
	}
	public FormField setIsDependInheritable(Boolean isDependInheritable) {
		this.isDependInheritable = isDependInheritable;
		return this;
	}
	
	public boolean isOverride() {
		return override;
	}
	public FormField setOverride(boolean override) {
		this.override = override;
		return this;
	}
	
	public FormField clone() {
		if(getClass() == FormField.class) {
			FormField copyFormField = new FormField(this.code);
			copyFormField.setName(this.name)
					.setDescription(this.description)
					.setDataType(this.dataType)
					.setLength(this.length)
					.setPrecision(this.precision)
					.setNotNull(this.isNotNull)
					.setDefaultValue(this.defaultValue)
					.setExtendInfo(this.extendInfo)
					.setAssocFormModel(this.assocFormModel)
					.setAssocMultiSelect(this.isAssocMultiSelect)
					.setTableFormModel(this.tableFormModel)
					.setDependFormModel(this.dependFormModel)
					.setIsDependInheritable(this.isDependInheritable)
					.setOverride(this.override);
			return copyFormField;
		}else {
			try {
				return ToolUtilities.clone(this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	@Override
	public String toString() {
		return "code : "+getCode() + ",name : " + getName() + "";
	}

}
