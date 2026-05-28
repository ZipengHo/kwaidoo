package gpf.adur.data;
/**
 * 表单模型
 * @author chenxb
 *
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.util.NullUtil;
@ClassDeclare(
	    label = "业务模型",
	    what = "用于表示表单的模型信息",
	    why = "",
	    how = "",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2024-12-05",
	    updateTime = "2024-12-05"
	)
public class FormModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1721730707824043509L;
	public final static String Uuid = "uuid";
	public final static String Id = "id";
	public final static String Name = "name";
	public final static String Label = "label";
	public final static String Description = "description";
	public final static String TableName = "tableName";
	public final static String ParentId = "parentId";
	//只读区，不需要设值
	public final static String ReadOnlyInfo = "readOnlyInfo";
	public final static String PackagePath = "packagePath";
	public final static String FieldList = "fieldList";
	public final static String ForeignList = "foreignList";
	public final static String IndexList = "indexList";
	public final static String ExtendInfo = "extendInfo";
	public final static String InheritFields = "inheritFields";
	public final static String HiddenFields = "hiddenFields";
	public final static String IsSytemModel = "isSytemModel";
	@FieldDeclare(label = "唯一标识符", desc = "业务模型的唯一标识符")
    private String uuid;

    @FieldDeclare(label = "模型ID", desc = "业务模型的ID")
    private String id;

    @FieldDeclare(label = "所属包名", desc = "业务模型所属的包名")
    private String packagePath;

    @FieldDeclare(label = "数据库表名", desc = "业务模型对应的数据库表名")
    private String tableName;

    @FieldDeclare(label = "模型名称", desc = "业务模型的名称")
    private String name;

    @FieldDeclare(label = "模型中文名称", desc = "业务模型的中文名称")
    private String label;

    @FieldDeclare(label = "描述说明", desc = "业务模型的描述说明")
    private String description;

    @FieldDeclare(label = "上级业务模型ID", desc = "上级业务模型的ID")
    private String parentId;

    @FieldDeclare(label = "数据属性列表", desc = "业务模型的数据属性列表")
    private List<FormField> fieldList = new ArrayList<>();

    @FieldDeclare(label = "索引配置", desc = "业务模型的索引配置列表")
    private List<TableIndex> indexList = new ArrayList<>();

    @FieldDeclare(label = "外键列表", desc = "业务模型的外键列表")
    private List<ForeignModel> foreignList = new ArrayList<>();

    @FieldDeclare(label = "附加信息", desc = "业务模型的附加信息")
    private FormModelExtendIntf extendInfo;

    @FieldDeclare(label = "只读信息", desc = "业务模型的只读信息")
    private FormModelReadOnly readOnlyInfo;

    @FieldDeclare(label = "继承的属性列表", desc = "业务模型继承的属性列表")
    private List<String> inheritFields = new ArrayList<>();

    @FieldDeclare(label = "隐藏的属性列表", desc = "业务模型隐藏的属性列表")
    private List<String> hiddenFields = new ArrayList<>();

    @FieldDeclare(label = "是否系统模型", desc = "标识业务模型是否为系统模型")
    private boolean isSytemModel = false;
	/**
	 * 模型ID
	 * @return
	 */
	public String getId() {
		return id;
	}
	public FormModel setId(String id) {
		this.id = id;
		return this;
	}
	public String getUuid() {
		return uuid;
	}
	public FormModel setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	/**
	 * 所属包名
	 * @return
	 */
	public String getPackagePath() {
		return packagePath;
	}
	public FormModel setPackagePath(String packagePath) {
		this.packagePath = packagePath;
		return this;
	}
	/**
	 * 数据库表名
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}
	public FormModel setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
	/**
	 * 模型名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	public FormModel setName(String name) {
		this.name = name;
		return this;
	}
	/**
	 * 模型中文
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	public FormModel setLabel(String label) {
		this.label = label;
		return this;
	}
	/**
	 * 描述说明
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	public FormModel setDescription(String description) {
		this.description = description;
		return this;
	}
	/**
	 * 上级表单模型ID
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}
	public FormModel setParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}
	/**
	 * 下级表单模型
	 * @return
	 * @throws Exception
	 */
	public List<FormModel> getChildModels() throws Exception{
		return IFormMgr.get().queryChildModels(id);
	}
	/**
	 * 表单属性
	 * @return
	 */
	public List<FormField> getFieldList() {
		return fieldList;
	}
	public FormModel setFieldList(List<FormField> attributeList) {
		this.fieldList = attributeList;
		return this;
	}
	/**
	 * 表单模型扩展信息
	 * @return
	 */
	public FormModelExtendIntf getExtendInfo() {
		return extendInfo;
	}
	public FormModel setExtendInfo(FormModelExtendIntf extendInfo) {
		this.extendInfo = extendInfo;
		return this;
	}
	public FormModelReadOnly getReadOnlyInfo() {
		return readOnlyInfo;
	}
	public void setReadOnlyInfo(FormModelReadOnly readOnlyInfo) {
		this.readOnlyInfo = readOnlyInfo;
	}
	/**
	 * 继承的属性列表
	 * @return
	 */
	public List<String> getInheritFields() {
		return inheritFields;
	}
	public FormModel setInheritFields(List<String> inheritFields) {
		this.inheritFields = inheritFields;
		return this;
	}
	/**
	 * 隐藏的属性列表
	 * @return
	 */
	public List<String> getHiddenFields() {
		return hiddenFields;
	}
	/**
	 * 获取非隐藏的属性雷彪
	 * @return
	 */
	public List<FormField> getNotHiddenFieldList(){
		return NullUtil.get(fieldList).stream().filter(v->!hiddenFields.contains(v.getCode())).collect(Collectors.toList());
	}
	public FormModel setHiddenFields(List<String> hiddenFields) {
		this.hiddenFields = hiddenFields;
		return this;
	}
	/**
	 * 是否系统模型
	 * @return
	 */
	public boolean isSytemModel() {
		return isSytemModel;
	}
	public FormModel setSytemModel(boolean isSytemModel) {
		this.isSytemModel = isSytemModel;
		return this;
	}
	public List<TableIndex> getIndexList() {
		return indexList;
	}
	public FormModel setIndexList(List<TableIndex> indexList) {
		this.indexList = indexList;
		return this;
	}
	public List<ForeignModel> getForeignList() {
		return foreignList;
	}
	public FormModel setForeignList(List<ForeignModel> foreignList) {
		this.foreignList = foreignList;
		return this;
	}
	/**
	 * 获取属性Code和属性的map集合
	 * @return
	 */
	public Map<String,FormField> getFieldMap(){
		Map<String,FormField> fieldMap = new LinkedHashMap<>();
		for(FormField field : NullUtil.get(fieldList)) {
			fieldMap.put(field.getCode(), field);
		}
		return fieldMap;
	}
	
	public Map<String,FormField> getFieldNameMap(){
		Map<String,FormField> fieldMap = new LinkedHashMap<>();
		for(FormField field : NullUtil.get(fieldList)) {
			fieldMap.put(field.getName(), field);
		}
		return fieldMap;
	}
	/**
	 * 根据属性中文名查找属性
	 * @param fieldName
	 * @return
	 */
	public FormField getFieldByName(String fieldName) {
		for(FormField field : NullUtil.get(fieldList)) {
			if(CmnUtil.isStringEqual(field.getName(), fieldName))
				return field;
		}
		return null;
	}
	@Override
	public String toString() {
		return "id="+id+",name="+name+",label="+label;
	}
	public String getNameText() {
		return label + "(" + id +")";
	}
	
}
