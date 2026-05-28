package gpf.adur.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.cdao.model.CDimension;
import com.cdao.model.CDoBasic;
import com.cdao.model.type.KeyValue;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.javac.ClassFactory;

import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.util.NullUtil;
import gpf.dto.model.data.FormPrivilegeDto;
import gpf.md.slave.NestingData;

@ClassDeclare(
	    label = "数据表单",
	    what = "数据模型的具象实例数据，通过IFormMgr提供增删改查操作\r\n" + 
	    		"",
	    why = "",
	    how = "表单上根据属性的不同类型，设值和取值的方法有所不同，其中表单的属性类型包括：\r\n" + 
	    		"文本(Text)、布尔值(Boolean)、整数(Long)、小数(Decimal)、密码(Password)、时间(Date)、附件(Attach)、属性表(KeyValue)、关联(Relate)、强依赖(Depend)、嵌套(NestingModel)\r\n" + 
	    		"\r\n" + 
	    		"表单设值：\r\n" + 
	    		"//系统属性：编号\r\n" + 
	    		"String codeValue = \"001\";\r\n" + 
	    		"form.setAttrValueByCode(Form.Code,codeValue);\r\n" + 
	    		"//用户自定义属性\r\n" + 
	    		"//1.文本类型属性设值\r\n" + 
	    		"String textFieldCode = IFormMgr.get().getFieldCode(\"文本属性\");\r\n" + 
	    		"String textValue = \"文本值\";\r\n" + 
	    		"form.setAttrValueByCode(textFieldCode, textValue);\r\n" + 
	    		"//2.布尔值类型属性设值\r\n" + 
	    		"String boolFieldCode = IFormMgr.get().getFieldCode(\"布尔值属性\");\r\n" + 
	    		"String boolValue = true;\r\n" + 
	    		"form.setAttrValueByCode(\"布尔值属性\", boolValue);\r\n" + 
	    		"//3.整数类型属性设值\r\n" + 
	    		"String longFieldCode = IFormMgr.get().getFieldCode(\"整数属性\");\r\n" + 
	    		"Long longValue = 100L;\r\n" + 
	    		"form.setAttrValueByCode(longFieldCode, longValue);\r\n" + 
	    		"//3.小数类型属性设值\r\n" + 
	    		"String doubleFieldCode = IFormMgr.get().getFieldCode(\"小数属性\");\r\n" + 
	    		"Double doubleValue = 100.1D;\r\n" + 
	    		"form.setAttrValueByCode(doubleFieldCode, doubleValue);\r\n" + 
	    		"//4.密码类型属性设值\r\n" + 
	    		"String passwordFieldCode = IFormMgr.get().getFieldCode(\"密码属性\");\r\n" + 
	    		"Password password = new Password().setValue(\"123456\");\r\n" + 
	    		"form.setAttrValueByCode(passwordFieldCode, password);\r\n" + 
	    		"//5.时间类型属性设值\r\n" + 
	    		"String timeFieldCode = IFormMgr.get().getFieldCode(\"时间属性\");\r\n" + 
	    		"Long timeValue = System.currentTimeMillis();\r\n" + 
	    		"form.setAttrValueByCode(timeFieldCode, timeValue);\r\n" + 
	    		"//6.附件类型属性设值\r\n" + 
	    		"String attachFieldCode = IFormMgr.get().getFieldCode(\"附件属性\");\r\n" + 
	    		"List<AttachData> attaches = new ArrayList<>();\r\n" + 
	    		"String  fileName = \"test.txt\";\r\n" + 
	    		"byte[] content = \"测试附件内容\".getBytes();\r\n" + 
	    		"AttachData attach = new AttachData(fileName,content);\r\n" + 
	    		"attaches.add(attach);\r\n" + 
	    		"form.setAttrValueByCode(attachFieldCode, attaches);\r\n" + 
	    		"//7.属性表类型属性设值\r\n" + 
	    		"String keyvalueFieldCode = IFormMgr.get().getFieldCode(\"属性表属性\");\r\n" + 
	    		"List<Map<String,String>> keyValues = new ArrayList<>();\r\n" + 
	    		"//属性表每行记录需要设置key、label和value\r\n" + 
	    		"keyValues.add(ImmutableMap.of(Form.KeyValue_Key, \"AAA\", Form.KeyValue_Label, \"BBB\",Form.KeyValue_Value , \"CCC\"));\r\n" + 
	    		"form.setAttrValueByCode(keyvalueFieldCode, keyValues);\r\n" + 
	    		"//8.关联类型属性设值，分为单选关联和多选关联，通过关联模型ID和关联数据编号构建AssociationData对象\r\n" + 
	    		"String assocModelId = \"gpf.md.test.AssocData\";//关联模型ID\r\n" + 
	    		"String assocFormCode = \"001\";//关联数据编号\r\n" + 
	    		"AssociationData assocData = new AssociationData(assocModelId, assocFormCode);\r\n" + 
	    		"//8.1单选关联\r\n" + 
	    		"String assocFieldCode = IFormMgr.get().getFieldCode(\"关联属性\");\r\n" + 
	    		"form.setAttrValueByCode(assocFieldCode, assocData);\r\n" + 
	    		"//8.2多选关联\r\n" + 
	    		"String multiAssocFieldCode = IFormMgr.get().getFieldCode(\"多选关联属性\");\r\n" + 
	    		"List<AssociationData> assocDatas = new ArrayList<>();\r\n" + 
	    		"assocDatas.add(assocData）；\r\n" + 
	    		"form.setAttrValueByCode(multiAssocFieldCode, assocDatas);\r\n" + 
	    		"//9.嵌套类型属性设值\r\n" + 
	    		"String nestingFieldCode = IFormMgr.get().getFieldCode(\"嵌套属性\");\r\n" + 
	    		"String nestingModelId = \"gpf.md.test.slave.NestingTable\";//嵌套模型ID\r\n" + 
	    		"TableData tableData = new TableData();\r\n" + 
	    		"Form nestingForm = new Form(nestingModelId);\r\n" + 
	    		"tableData.add(nestingForm);\r\n" + 
	    		"form.setAttrValueByCode(nestingFieldCode, tableData);\r\n" + 
	    		"\r\n" + 
	    		"表单取值：\r\n" + 
	    		"//系统属性：编号\r\n" + 
	    		"String code = form.getStringByCode(Form.Code);\r\n" + 
	    		"//用户自定义属性\r\n" + 
	    		"//1.文本属性取值\r\n" + 
	    		"String textFieldCode = IFormMgr.get().getFieldCode(\"文本属性\");\r\n" + 
	    		"String text = form.getStringByCode(textFieldCode);\r\n" + 
	    		"//2.布尔值属性取值\r\n" + 
	    		"String boolFieldCode = IFormMgr.get().getFieldCode(\"布尔值属性\");\r\n" + 
	    		"Boolean bool = form.getBooleanByCode(boolFieldCode);\r\n" + 
	    		"//3.整数属性取值\r\n" + 
	    		"String longFieldCode = IFormMgr.get().getFieldCode(\"整数属性\");\r\n" + 
	    		"Long longValue = form.getLongByCode(longFieldCode);\r\n" + 
	    		"//4.小数属性取值\r\n" + 
	    		"String doubleFieldCode = IFormMgr.get().getFieldCode(\"小数属性\");\r\n" + 
	    		"Double doubleValue = form.getDoubleByCode(doubleFieldCode);\r\n" + 
	    		"//5.密码属性取值\r\n" + 
	    		"String passwordFieldCode = IFormMgr.get().getFieldCode(\"密码属性\");\r\n" + 
	    		"Password password = form.getPasswordByCode(passwordFieldCode);\r\n" + 
	    		"//6.时间属性取值\r\n" + 
	    		"String dateFieldCode = IFormMgr.get().getFieldCode(\"时间属性\");\r\n" + 
	    		"Long dateValue = form.getLongByCode(dateFieldCode);\r\n" + 
	    		"//7.附件属性取值\r\n" + 
	    		"String attachFieldCode = IFormMgr.get().getFieldCode(\"附件属性\");\r\n" + 
	    		"List<AttachData> lstAttach = form.getAttachmentsByCode(attachFieldCode);\r\n" + 
	    		"//8.属性表属性取值\r\n" + 
	    		"String keyvalueFieldCode = IFormMgr.get().getFieldCode(\"属性表属性\");\r\n" + 
	    		"List<Map<String,String>> keyValues = form.getPropTableByCode(keyvalueFieldCode);\r\n" + 
	    		"//9.关联属性取值\r\n" + 
	    		"//9.1单选关联\r\n" + 
	    		"String assocFieldCode = IFormMgr.get().getFieldCode(\"关联属性\");\r\n" + 
	    		"AssociationData assocData = form.getAssociationByCode(assocFieldCode);\r\n" + 
	    		"//关联数据的编号\r\n" + 
	    		"String assocCode = assocData.getValue();\r\n" + 
	    		"//9.2多选关联\r\n" + 
	    		"String multiAssocFieldCode = IFormMgr.get().getFieldCode(\"多选关联属性\");\r\n" + 
	    		"List<AssociationData> assocDatas = form.getAssociationsByCode(multiAssocFieldCode);\r\n" + 
	    		"//10.嵌套属性取值\r\n" + 
	    		"String nestingFieldCode = IFormMgr.get().getFieldCode(\"嵌套属性\");\r\n" + 
	    		"TableData tableData = form.getTableByCode(nestingFieldCode);\r\n" + 
	    		"if(tableData!= null){\r\n" + 
	    		"	List<Form> nestingForms = tableData.getRows();\r\n" + 
	    		"}",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class Form implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -868752986193775877L;
	public final static String UUID = CDoBasic.UUID;
	public final static String Owner = CDoBasic.Owner;
	public final static String ForeignClass = CDoBasic.ForeignClass;
	public final static String ForeignKey = CDoBasic.ForeignKey;
	public final static String Code = CDimension.Code;
	/**
	 * 嵌套模型的系统属性-继承的模板uuid
	 */
	public final static String Nesting_InheritTmplt = NestingData.InheritTmplt;
	
	//附带保存的其他表单数据
	public final static String EXT_KEY_ADDITIONAL_SAVE_FORMS = "ADDITIONAL_SAVE_FORMS";
	
	public final static String KeyValue_Key = KeyValue.Key;
	public final static String KeyValue_Label = KeyValue.Label;
	public final static String KeyValue_Value = KeyValue.Value;
	
	public final static String FormModelId = "formModelId";
	public final static String Data = "data";
	public final static String ExtField_FormPrivilege = "$FormPrivilege$";
	String uuid;
	String formModelId;
	@FieldDeclare(label = "表单属性元数据",desc = "查询数据的时候会携带")
	transient List<FormField> meta;
	
	Map<String, Object> data = new LinkedHashMap<>();
	Map<String,Object> extFields = new LinkedHashMap<>();
	
	public Form() {
		setUuid(ToolUtilities.allockUUIDWithUnderline());
	}
	
	public Form(String formModelId) {
		this.formModelId = formModelId;
		setUuid(ToolUtilities.allockUUIDWithUnderline());
	}
	
	public Form(String formModelId,String uuid) {
		this.formModelId = formModelId;
		setUuid(this.uuid); 
	}
	/**
	 * 表单数据uuid
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * 方法已弃用，如需设置uuid，使用resetUuid
	 * @param uuid
	 * @return
	 */
	@Deprecated
	public Form setUuid(String uuid) {
		this.uuid = uuid;
		this.data.put(UUID, uuid);
		return this;
	}
	public Form resetUuid(String uuid){
		this.uuid = uuid;
		this.data.put(UUID, uuid);
		return this;
	}
	/**
	 * 表单模型ID
	 * @return
	 */
	public String getFormModelId() {
		return formModelId;
	}

	/**
	 * 方法已弃用，如需设置，使用resetModelIdAndUuid
	 * @param formModelId
	 * @return
	 */
	@Deprecated
	public Form setFormModelId(String formModelId) {
		this.formModelId = formModelId;
		return this;
	}

	/**
	 * 重新设置模型ID和uuid
	 * @param formModelId
	 * @param uuid
	 * @return
	 */
	public Form resetFormModelId(String formModelId,String uuid) {
		this.formModelId = formModelId;
		if(uuid != null)
			resetUuid(uuid);
		return this;
	}
	/**
	 * 表单数据
	 * @return
	 */
	@Deprecated
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * 获取当前设置的属性键集合
	 * @return
	 */
	public Set<String> getAttrKeySet() {
		return data.keySet();
	}
	/**
	 * 已弃用，如需设置，请使用keysSet遍历属性设置
	 * @param data
	 * @return
	 */
	@Deprecated
	public Form setData(Map<String, Object> data) {
		this.data = data;
		return this;
	}
	/**
	 * 读取表单数据
	 * @param fieldCode
	 * @return
	 */
	public Object getAttrValueByCode(String fieldCode) {
		return data.get(fieldCode);
	}
	/**
	 * 读取表单数据
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public Object getAttrValue(String fieldName) throws Exception {
		FormField field = getFormFieldByName(fieldName);
		if(field != null) {
			return getAttrValueByCode(field.getCode());
		}else {
			String fieldCode = getFieldCode(fieldName);
			return getAttrValueByCode(fieldCode);
		}
	}
	/**
	 * 设置表单数据
	 * @param fieldCode
	 * @param attrValue
	 * @return
	 * @throws Exception 
	 */
	public Form setAttrValueByCode(String fieldCode,Object attrValue){
		assertFormModelId();
		if(attrValue instanceof Date) {
			data.put(fieldCode, ((Date) attrValue).getTime());
		}else {
			data.put(fieldCode, attrValue);
		}
		return this;
	}
	/**
	 * 设置表单数据
	 * @param fieldName
	 * @param attrValue
	 * @return
	 * @throws Exception
	 */
	public Form setAttrValue(String fieldName,Object attrValue) throws Exception {
		FormField field = getFormFieldByName(fieldName);
		if(field != null) {
			setAttrValueByCode(field.getCode(), attrValue);
		}else {
			String fieldCode = getFieldCode(fieldName);
			setAttrValueByCode(fieldCode, attrValue);
		}
		return this;
	}
	
	public String getString(String fieldName) throws Exception {
		Object value = getAttrValue(fieldName);
		if(value == null)
			return null;
		if(value instanceof String) {
			return (String) value;
		}else {
			throw new Exception("属性["+fieldName+"]的值不是文本！value="+value+",Class = " + value.getClass());
		}
	}
	
	public String getStringByCode(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof String) {
			return (String) value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是文本！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Password getPassword(String fieldName)throws Exception{
		Object value = getAttrValue(fieldName);
		if(value == null)
			return null;
		if(value instanceof Password) {
			return (Password) value;
		}else {
			throw new Exception("属性["+fieldName+"]的值不是密码！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Password getPasswordByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof Password) {
			return (Password) value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是密码！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Boolean getBoolean(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof Boolean) {
			return (Boolean) value;
		}else {
			throw new Exception("属性["+attrName+"]的值不是布尔值！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Boolean getBooleanByCode(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof Boolean) {
			return (Boolean) value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是布尔值！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Long getLong(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof Long) {
			return (Long) value;
		}else {
			throw new Exception("属性["+attrName+"]的值不是长整数！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Long getLongByCode(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof Long) {
			return (Long) value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是长整数！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Double getDouble(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof Double) {
			return (Double) value;
		}else {
			throw new Exception("属性["+attrName+"]的值不是浮点数！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Double getDoubleByCode(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof Double) {
			return (Double) value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是浮点数！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Date getTime(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof Long) {
			return new Date((Long)value);
		}else {
			throw new Exception("属性["+attrName+"]的值不是长整数！value="+value+",Class = " + value.getClass());
		}
	}
	
	public Date getTimeByCoide(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof Long) {
			return new Date((Long)value);
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是长整数！value="+value+",Class = " + value.getClass());
		}
	}
	/**
	 * 获取属性表数据
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> getPropTable(String attrName)throws Exception{
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof Map) {
				return (List<Map<String, String>>) value;
			}
		}
		throw new Exception("属性["+attrName+"]的值不是键值对！value="+value+",Class = " + value.getClass());
	}
	
	public List<Map<String,String>> getPropTableByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof Map) {
				return (List<Map<String, String>>) value;
			}
		}
		throw new Exception("属性["+fieldCode+"]的值不是键值对！value="+value+",Class = " + value.getClass());
	}
	/**
	 * 获取属性表的键值对数据
	 * @param fieldName
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getPropKeyValueMap(String fieldName)throws Exception{
		List<Map<String,String>> props = getPropTable(fieldName);
		Map<String,String> map = new LinkedHashMap<>();
		if(props != null) {
			for(Map<String,String> row : props) {
				String key = row.get(KeyValue_Key);
				String value = row.get(KeyValue_Value);
				map.put(key, value);
			}
		}
		return map;
	}
	public Map<String,String> getPropKeyValueMapByCode(String fieldCode)throws Exception{
		List<Map<String,String>> props = getPropTableByCode(fieldCode);
		Map<String,String> map = new LinkedHashMap<>();
		if(props != null) {
			for(Map<String,String> row : props) {
				String key = row.get(KeyValue_Key);
				String value = row.get(KeyValue_Value);
				map.put(key, value);
			}
		}
		return map;
	}
	/**
	 * 获取关联数据
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public AssociationData getAssociation(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof AssociationData) {
			return (AssociationData)value;
		}else {
			throw new Exception("属性["+attrName+"]的值不是关联数据！value="+value+",Class = " + value.getClass());
		}
	}
	
	public AssociationData getAssociationByCode(String fieldCode) throws Exception {
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof AssociationData) {
			return (AssociationData)value;
		}else {
			throw new Exception("属性["+fieldCode+"]的值不是关联数据！value="+value+",Class = " + value.getClass());
		}
	}
	
	public List<AssociationData> getAssociations(String attrName) throws Exception {
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof AssociationData) {
				return (List<AssociationData>)value;
			}
		}
		throw new Exception("属性["+attrName+"]的值不是关联数据！value="+value+",Class = " + value.getClass());
	}
	
	public List<AssociationData> getAssociationsByCode(String fielCode) throws Exception {
		Object value = getAttrValueByCode(fielCode);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof AssociationData) {
				return (List<AssociationData>)value;
			}
		}
		throw new Exception("属性"+fielCode+"的值不是关联数据！value="+value+",Class = " + value.getClass());
	}
	
	public List<AttachData> getAttachments(String attrName)throws Exception{
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof AttachData) {
				return (List<AttachData>)value;
			}
		}
		throw new Exception("属性["+attrName+"]的值不是附件！value="+value+",Class = " + value.getClass());
	}
	
	public Map<String,AttachData> getAttachmentMap(String attrName)throws Exception{
		List<AttachData> attachs = getAttachments(attrName);
		return collectAttachmentMap(attachs);
	}
	
	public Map<String,AttachData> getAttachmentMapByCode(String attrCode)throws Exception{
		List<AttachData> attachs = getAttachmentsByCode(attrCode);
		return collectAttachmentMap(attachs);
	}
	
	public static Map<String,AttachData> collectAttachmentMap(List<AttachData> attachs){
		if(CmnUtil.isCollectionEmpty(attachs))
			return new LinkedHashMap<>();
		return attachs.stream().collect(Collectors.toMap(AttachData::getName, v->v, (e,r)->r, LinkedHashMap::new));

	}
	
	public List<AttachData> getAttachmentsByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof AttachData) {
				return (List<AttachData>)value;
			}
		}
		throw new Exception("属性["+fieldCode+"]的值不是附件！value="+value+",Class = " + value.getClass());
	}
	
	public void addOrReplaceAttachments(String attrName,List<AttachData> attachs) throws Exception {
		List<AttachData> orgAttachFiles = getAttachments(attrName);
		List<AttachData> newAttachFiles = mergeAttachments(orgAttachFiles, attachs);
		setAttrValue(attrName, newAttachFiles);
	}
	
	public void addOrReplaceAttachmentsByCode(String fieldCode,List<AttachData> attachs) throws Exception {
		List<AttachData> orgAttachFiles = getAttachmentsByCode(fieldCode);
		List<AttachData> newAttachFiles = mergeAttachments(orgAttachFiles, attachs);
		setAttrValueByCode(fieldCode, newAttachFiles);
	}
	
	public static List<AttachData> mergeAttachments(List<AttachData> orgAttachs,List<AttachData> attachs) {
		return mergeAttachments(orgAttachs, attachs, (org,curr)-> curr);
	}
	
	public static List<AttachData> mergeAttachments(List<AttachData> orgAttachs,List<AttachData> attachs,BiFunction<AttachData, AttachData, AttachData> func) {
		Map<String,AttachData> orgAttachMap = collectAttachmentMap(orgAttachs);
		if(attachs != null) {
			for(AttachData attach : attachs) {
				orgAttachMap.put(attach.getName(), func.apply(orgAttachMap.get(attach.getName()), attach));
			}
		}
		return new ArrayList<>(orgAttachMap.values());
	}
	
	public void deleteAttachments(String attrName,Set<String> fileNames) throws Exception {
		if(CmnUtil.isCollectionEmpty(fileNames))
			return;
		List<AttachData> attachs = getAttachments(attrName);
		deleteAttachments(attachs, fileNames);
	}
	
	public void deleteAttachmentsByCode(String fieldCode,Set<String> fileNames) throws Exception {
		if(CmnUtil.isCollectionEmpty(fileNames))
			return;
		List<AttachData> attachs = getAttachmentsByCode(fieldCode);
		deleteAttachments(attachs, fileNames);
	}
	
	public static void deleteAttachments(List<AttachData> attachs,Set<String> fileNames)throws Exception{
		if(CmnUtil.isCollectionEmpty(attachs))
			return;
		for(Iterator<AttachData> it = attachs.iterator();it.hasNext();) {
			AttachData attach = it.next();
			if(fileNames.contains(attach.getName())) {
				it.remove();
			}
		}
	}
	
	public AttachData getAttachment(String attrName)throws Exception{
		List<AttachData> list = getAttachments(attrName);
		if(CmnUtil.isCollectionEmpty(list))
			return null;
		return list.get(0);
	}
	
	public AttachData getAttachmentByCode(String fieldCode)throws Exception{
		List<AttachData> list = getAttachmentsByCode(fieldCode);
		if(CmnUtil.isCollectionEmpty(list))
			return null;
		return list.get(0);
	}
	
	public List<WebAttachData> getWebAttachs(String attrName)throws Exception{
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof WebAttachData) {
				return (List<WebAttachData>)value;
			}
		}
		throw new Exception("属性["+attrName+"]的值不是网络附件！value="+value+",Class = " + value.getClass());
	}
	
	public List<WebAttachData> getWebAttachsByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof List) {
			List list = (List) value;
			if(CmnUtil.isCollectionEmpty(list))
				return list;
			if(list.get(0) instanceof WebAttachData) {
				return (List<WebAttachData>)value;
			}
		}
		throw new Exception("属性["+fieldCode+"]的值不是网络附件！value="+value+",Class = " + value.getClass());
	}
	/**
	 * 表格数据
	 * @param attrName
	 * @return
	 * @throws Exception
	 */
	public TableData getTable(String attrName)throws Exception{
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof TableData) {
			return (TableData) value;
		}
		throw new Exception("属性["+attrName+"]的值不是嵌套数据！value="+value+",Class = " + value.getClass());
	}
	
	public TableData getTableByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof TableData) {
			return (TableData) value;
		}
		throw new Exception("属性["+fieldCode+"]的值不是嵌套数据！value="+value+",Class = " + value.getClass());
	}
	
	public Map<String,Form> getTableRowMap(String attrName)throws Exception{
		TableData tableData = getTable(attrName);
		if(tableData == null)
			return Collections.emptyMap();
		return tableData.getRowUuidMap();
	}
	
	public Map<String,Form> getTableRowMapByCode(String fieldCode)throws Exception{
		TableData tableData = getTableByCode(fieldCode);
		if(tableData == null)
			return Collections.emptyMap();
		return tableData.getRowUuidMap();
	}
	
	public byte[] getByteArray(String attrName)throws Exception{
		Object value = getAttrValue(attrName);
		if(value == null)
			return null;
		if(value instanceof byte[]) {
			return (byte[]) value;
		}
		throw new Exception("属性["+attrName+"]的值不是字节数据！value="+value+",Class = " + value.getClass());
	}
	
	public byte[] getByteArrayByCode(String fieldCode)throws Exception{
		Object value = getAttrValueByCode(fieldCode);
		if(value == null)
			return null;
		if(value instanceof byte[]) {
			return (byte[]) value;
		}
		throw new Exception("属性["+fieldCode+"]的值不是字节数据！value="+value+",Class = " + value.getClass());
	}
	
	private void assertFormModelId(){
		if(formModelId == null) {
			throw new RuntimeException("表单模型ID不能为空");
		}
	}
	
	public String getFieldCode(String fieldName)throws Exception{
		if(CmnUtil.isStringEmpty(fieldName))
			throw new Exception("属性名不能为空！");
		if(fieldName.equals("编号")) {
			assertFormModelId();
			Class clazz = ClassFactory.getValidClassLoader().loadClass(formModelId);
			if(CDimension.class.isAssignableFrom(clazz)) {
				return CDimension.Code;
			}
		}
		return IFormMgr.get().getFieldCode(fieldName);
	}
	
	public Map<String, Object> getExtFields() {
		return extFields;
	}
	public Form setExtFields(Map<String, Object> extFields) {
		this.extFields = extFields;
		return this;
	}
	public Object getExtField(String extField) {
		for(String key : extFields.keySet()) {
			if(CmnUtil.isStringEqual(extField, key,true)) {
				return extFields.get(key);
			}
		}
		return null;
	}
	public Form setExtField(String extField,Object value) {
		extFields.put(extField, value);
		return this;
	}
	/**
	 * 获取附加保存的表单，key： uuid，value: Form
	 * @return
	 */
	public Map<String,Form> getAdditionalSaveForm(){
		Map<String,Form> additionalForms = (Map<String, Form>) getExtField(EXT_KEY_ADDITIONAL_SAVE_FORMS);
		if(additionalForms == null) {
			additionalForms = new LinkedHashMap<>();
			setExtField(EXT_KEY_ADDITIONAL_SAVE_FORMS, additionalForms);
		}
		return additionalForms;
	}
	public void addAdditionalSaveForm(Form form) {
		getAdditionalSaveForm().put(form.getUuid(), form);
	}
	
	public FormPrivilegeDto getFormPrivilege() {
		return (FormPrivilegeDto) getExtField(ExtField_FormPrivilege);
	}
	public Form setFormPrivilege(FormPrivilegeDto formPrivilege) {
		setExtField(ExtField_FormPrivilege, formPrivilege);
		return this;
	}
	
	public List<FormField> getMeta() throws Exception {
		List<FormField> meta = _getCacheMeta();
		List<FormField> copyFields  = new ArrayList<>();
		for(FormField formField : meta) {
			copyFields.add(formField.clone());
		}
		return copyFields;
	}
	
	private List<FormField> _getCacheMeta() throws Exception {
		if(meta != null)
			return meta;
		if(!CmnUtil.isStringEmpty(formModelId)) {
			FormModel formModel = IFormMgr.get().queryFormModel(formModelId,false);
			if(formModel != null) {
				meta = formModel.getFieldList();
			}
		}
		return meta;
	}
	
	public void setMeta(List<FormField> meta) {
		this.meta = meta;
	}
	
	protected Map<String,FormField> getMetaMap() throws Exception{
		List<FormField> fileds = _getCacheMeta();
		if(fileds == null)
			return new LinkedHashMap<>();
		return fileds.stream().collect(Collectors.toMap(FormField::getCode, v->v,(e,r)->r,LinkedHashMap::new));
	}
	
	protected FormField getFormFieldByName(String fieldName) throws Exception {
		for(FormField field : NullUtil.get(_getCacheMeta())) {
			if(CmnUtil.isStringEqual(field.getName(), fieldName)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * 复制数据作为新表单数据，并重新分配uuid
	 * @return
	 */
	public Form copyAsNew() throws Exception {
        return IFormMgr.get().copyForm(this);
	}

	/**
	 * 复制数据作为其他模型的新表单数据
	 * @param targetModelId
	 * @return
	 * @throws Exception
	 */
	public Form copyAsNew(String targetModelId) throws Exception {
        return IFormMgr.get().copyForm(this,targetModelId);
	}
	
	@Override
	public String toString() {
		return formModelId+"("+uuid+")";
	}
}
