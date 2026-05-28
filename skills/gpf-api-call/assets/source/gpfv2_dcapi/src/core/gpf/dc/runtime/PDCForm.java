package gpf.dc.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.util.NullUtil;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.dto.model.data.FormPrivilegeDto;
import gpf.i18n.GpfConst;
/**
 * 流程节点表单，由流程总表单（取当前节点引用属性部分）+当前节点状态信息组成
 */
@ClassDeclare(label = "流程节点表单"
,what = "由流程总表单（取当前节点引用的模型属性）+当前节点状态信息组成"
, why = ""
,how = ""
, developer = "陈晓斌"
, createTime = "2025-03-14"
,updateTime = "2025-03-14"
, version = "1.0" )
public class PDCForm extends Form implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1693741489240386542L;

	public static final String OpLogUuid = GpfConst.STEP_UUID;
	public static final String Code = "code";
	public static final String PdfInstUuid = "processUuid";
	public static final String ParentFormUuid = "parentFormUuid";
	public static final String Creator = "creator";
	public static final String CreateTime = "createTime";
	public static final String UpdateTime = "updateTime";
	public static final String Closed = "closed";
	public static final String Assignee = "assignee";
	public static final String StepOperator = "stepOperator";
	public static final String ExecuteTime = "executeTime";
	public static final String StepName = "stepName";
	public static final String NodeName = "nodeName";
	public static final String NodeKey = "nodeKey";
	public static final String Status = "status";
	public static final String StepError = "stepError";
	
	
//	String nodeKey;
//	String nodeName;
	@FieldDeclare(label = "PDF的唯一标识",desc = "")
	String pdfUuid;
	@FieldDeclare(label = "表单元数据",desc = "")
	List<FormField> meta = new ArrayList<>();
	@FieldDeclare(label = "表单提供的操作动作",desc = "")
	List<String> actions = new ArrayList<>();
	@FieldDeclare(label = "表单的权限",desc = "")
	FormPrivilegeDto formPrivilege;
	/**
	 * 数据是否发生修改
	 */
	boolean modified = false;
//	/**
//	 * 节点Key
//	 * @return
//	 */
//	public String getNodeKey() {
//		return nodeKey;
//	}
//	public PDCForm setNodeKey(String nodeKey) {
//		this.nodeKey = nodeKey;
//		return this;
//	}
//	/**
//	 * 节点名称
//	 * @return
//	 */
//	public String getNodeName() {
//		return nodeName;
//	}
//	public PDCForm setNodeName(String nodeName) {
//		this.nodeName = nodeName;
//		return this;
//	}
	public PDCForm() {
		super("");
	}
	public String getPdfUuid() {
		return pdfUuid;
	}
	public void setPdfUuid(String pdfUuid) {
		this.pdfUuid = pdfUuid;
	}
	/**
	 * 数据列表
	 * @return
	 */
	public List<FormField> getMeta() {
		return meta;
	}
	public void setMeta(List<FormField> meta) {
		this.meta = meta;
	}
	
	public Map<String,FormField> getFieldMap(){
		Map<String,FormField> map = new LinkedHashMap<String, FormField>();
		for(FormField field : NullUtil.get(meta)) {
			map.put(field.getCode(), field);
		}
		return map;
	}
	/**
	 * 动作列表
	 * @return
	 */
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	/**
	 * 表单权限
	 * @return
	 */
	@Override
	public FormPrivilegeDto getFormPrivilege() {
		return formPrivilege;
	}
	@Override
	public PDCForm setFormPrivilege(FormPrivilegeDto formPrivilege) {
		this.formPrivilege = formPrivilege;
		return this;
	}
	
	public String getOpLogUuid() throws Exception {
    	return getString(OpLogUuid);
    }
    
    public void setOpLogUuid(String stepUuid) {
    	setAttrValueByCode(OpLogUuid, stepUuid);
    }
	

	public String getPdfInstUuid() throws Exception {
		return getString(PdfInstUuid);
	}
	
	public void setPdfInstUuid(String v) {
		setAttrValueByCode(PdfInstUuid, v);
	}
	
	public String getParentFormUuid() throws Exception {
		return getString(ParentFormUuid);
	}
	
	public void setParentFormUuid(String v) {
		setAttrValueByCode(ParentFormUuid, v);
	}
	
	public AssociationData getCreator() throws Exception {
		return getAssociationByCode(Creator);
	}

	public void setCreator(AssociationData v) {
		setAttrValueByCode(Creator, v);
	}
	
	public Long getCreateTime() throws Exception {
		return getLong(CreateTime);
	}

	public void setCreateTime(Long v) {
		setAttrValueByCode(CreateTime, v);
	}
	

	public Long getUpdateTime() throws Exception {
		return getLong(UpdateTime);
	}

	public void setUpdateTime(Long v) {
		setAttrValueByCode(UpdateTime, v);
	}
	
    public Boolean getClosed() throws Exception
    {
        return getBoolean(Closed);
    }

    public void setClosed(Boolean v)
    {
    	setAttrValueByCode(Closed, v);
    }

    public boolean isClosed() throws Exception
    {
        return getClosed() != null ? getClosed() : false;
    }
    
    public String getStepName() throws Exception {
		return getString(StepName);
	}

	public void setStepName(String v) {
		setAttrValueByCode(StepName, v);
	}

	public String getNodeName(){
		try {
			return getString(NodeName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setNodeName(String v) {
		setAttrValueByCode(NodeName, v);
	}
	
	public String getNodeKey(){
		try {
			return getString(NodeKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setNodeKey(String v) {
		setAttrValueByCode(NodeKey, v);
	}
    
    public List<AssociationData> getAssignee() throws Exception {
		return getAssociationsByCode(Assignee);
	}

	public void setAssignee(List<AssociationData> v) {
		setAttrValueByCode(Assignee, v);
	}
	
	public AssociationData getStepOperator() throws Exception {
		return getAssociationByCode(StepOperator);
	}

	public void setStepOperator(AssociationData v) {
		setAttrValueByCode(StepOperator, v);
	}
	
	public Long getExecuteTime() throws Exception {
		return getLong(ExecuteTime);
	}

	public void setExecuteTime(Long v) {
		setAttrValueByCode(ExecuteTime, v);
	}
	
	public String getStatus() throws Exception
    {
        return getString(Status);
    }

    public void setStatus(String status)
    {
    	setAttrValueByCode(Status, status);
    }
    
    public String getStepError() throws Exception
    {
        return getString(StepError);
    }

    public void setStepError(String stepError)
    {
    	setAttrValueByCode(StepError, stepError);
    }
    
    public boolean isModified() {
		return modified;
	}
    public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	@Override
	public String toString() {
		return getFormModelId()+"("+getUuid()+"),node["+getNodeName()+"("+getNodeKey()+")]";
	}
}
