package gpf.dc.runtime;

import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.i18n.GpfConst;
/**
 * 流程表单列表数据,由流程总表单+当前状态记录数据组成
 * 当前状态记录数据包括：当前节点、发起人、接收人、操作人，上一步节点信息等，其中OpLogUuid为操作记录的唯一标识
 */
public class PDFForm extends Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304297096583961358L;
	public static final String PdfInstUuid = "processUuid";
	public static final String ParentFormUuid = "parentFormUuid";
	public static final String Creator = "creator";
	public static final String CreatorCnName = "creatorCnName";
	public static final String CreateTime = "createTime";
	public static final String UpdateTime = "updateTime";
	public static final String Assignee = "assignee";
	public static final String AssigneeCnName = "assigneeCnName";
	public static final String StepOperator = "stepOperator";
	public static final String StepOperatorCnName = "stepOperatorCnName";
	public static final String ExecuteTime = "executeTime";
	public static final String NodeKey = "nodeKey";
	public static final String StepName = "stepName";
	public static final String NodeName = "nodeName";
	public static final String ActionName = "actionName";
	public static final String LastStepName = "lastStepName";
	public static final String LastStepOperator = "lastStepOperator";
	public static final String LastStepOperatorCnName = "lastStepOperatorCnName";
	public static final String LastStepTag = "lastStepTag";
	public static final String LastNodeKey = "lastNodeKey";
	public static final String LastNodeName = "lastNodeName";
	public static final String StepTag = "stepTag";
	public static final String NextNodeKeys = "nextNodeKeys";
	public static final String ErrorMsg = "errorMsg";
	public static final String ErrorDetail = "errorDetail";
	public static final String Status = "status";
	public static final String OpLogUuid = GpfConst.STEP_UUID;
	public static final String Closed = "closed";
	
	public PDFForm() {
		super();
	}
	public PDFForm(String formModelId) {
		super(formModelId);
	}
	public PDFForm(String formModelId,String uuid) {
		super(formModelId,uuid);
	}

	String pdfUuid;

	public String getPdfUuid() {
		return pdfUuid;
	}

	public void setPdfUuid(String pdfUuid) {
		this.pdfUuid = pdfUuid;
	}
	
	public String getOpLogUuid() throws Exception {
		return (String) getAttrValueByCode(OpLogUuid);
	}

	public PDFForm setOpLogUuid(String v) {
		setAttrValueByCode(OpLogUuid, v);
		return this;
	}

	public String getPdfInstUuid() throws Exception {
		return (String) getAttrValueByCode(PdfInstUuid);
	}

	public PDFForm setPdfInstUuid(String v) {
		setAttrValueByCode(PdfInstUuid, v);
		return this;
	}

	public String getParentFormUuid() {
		return (String) getAttrValueByCode(ParentFormUuid);
	}

	public PDFForm setParentFormUuid(String v) {
		setAttrValueByCode(ParentFormUuid, v);
		return this;
	}

	public AssociationData getCreator() {
		return (AssociationData) getAttrValueByCode(Creator);
	}

	public PDFForm setCreator(AssociationData v) {
		setAttrValueByCode(Creator, v);
		return this;
	}
	
	public String getCreatorCnName() throws Exception {
		return getStringByCode(CreatorCnName);
	}

	public PDFForm setCreatorCnName(String v) {
		setAttrValueByCode(CreatorCnName, v);
		return this;
	}

	public Long getCreateTime() {
		return (Long) getAttrValueByCode(CreateTime);
	}

	public PDFForm setCreateTime(Long v) {
		setAttrValueByCode(CreateTime, v);
		return this;
	}

	public Long getUpdateTime() {
		return (Long) getAttrValueByCode(UpdateTime);
	}

	public PDFForm setUpdateTime(Long v) {
		setAttrValueByCode(UpdateTime, v);
		return this;
	}

	public String getAssignee() {
		return (String) getAttrValueByCode(Assignee);
	}

	public PDFForm setAssignee(String v) {
		setAttrValueByCode(Assignee, v);
		return this;
	}
	
	public String getAssigneeCnName() throws Exception {
		return getStringByCode(AssigneeCnName);
	}

	public PDFForm setAssigneeCnName(String v) {
		setAttrValueByCode(AssigneeCnName, v);
		return this;
	}

	public String getStepOperator() {
		return (String) getAttrValueByCode(StepOperator);
	}

	public PDFForm setStepOperator(String v) {
		setAttrValueByCode(StepOperator, v);
		return this;
	}
	
	public String getStepOperatorCnName() throws Exception {
		return getStringByCode(StepOperatorCnName);
	}

	public PDFForm setStepOperatorCnName(String v) {
		setAttrValueByCode(StepOperatorCnName, v);
		return this;
	}

	public Long getExecuteTime() {
		return (Long) getAttrValueByCode(ExecuteTime);
	}

	public PDFForm setExecuteTime(Long v) {
		setAttrValueByCode(ExecuteTime, v);
		return this;
	}

	public String getNodeKey() {
		return (String) getAttrValueByCode(NodeKey);
	}

	public PDFForm setNodeKey(String v) {
		setAttrValueByCode(NodeKey, v);
		return this;
	}

	public String getStepName() {
		return (String) getAttrValueByCode(StepName);
	}

	public PDFForm setStepName(String v) {
		setAttrValueByCode(StepName, v);
		return this;
	}

	public String getNodeName() {
		return (String) getAttrValueByCode(NodeName);
	}

	public PDFForm setNodeName(String v) {
		setAttrValueByCode(NodeName, v);
		return this;
	}

	public String getActionName() {
		return (String) getAttrValueByCode(ActionName);
	}

	public PDFForm setActionName(String v) {
		setAttrValueByCode(ActionName, v);
		return this;
	}

	public String getLastStepName() {
		return (String) getAttrValueByCode(LastStepName);
	}

	public PDFForm setLastStepName(String v) {
		setAttrValueByCode(LastStepName, v);
		return this;
	}

	public String getLastStepOperator() {
		return (String) getAttrValueByCode(LastStepOperator);
	}

	public PDFForm setLastStepOperator(String v) {
		setAttrValueByCode(LastStepOperator, v);
		return this;
	}
	
	public String getLastStepOperatorCnName() throws Exception {
		return getStringByCode(LastStepOperatorCnName);
	}

	public PDFForm setLastStepOperatorCnName(String v) {
		setAttrValueByCode(LastStepOperatorCnName, v);
		return this;
	}

	public String getLastStepTag() {
		return (String) getAttrValueByCode(LastStepTag);
	}

	public PDFForm setLastStepTag(String v) {
		setAttrValueByCode(LastStepTag, v);
		return this;
	}

	public String getLastNodeKey() {
		return (String) getAttrValueByCode(LastNodeKey);
	}

	public PDFForm setLastNodeKey(String v) {
		setAttrValueByCode(LastNodeKey, v);
		return this;
	}

	public String getLastNodeName() {
		return (String) getAttrValueByCode(LastNodeName);
	}

	public PDFForm setLastNodeName(String v) {
		setAttrValueByCode(LastNodeName, v);
		return this;
	}

	public String getStepTag() {
		return (String) getAttrValueByCode(StepTag);
	}

	public PDFForm setStepTag(String v) {
		setAttrValueByCode(StepTag, v);
		return this;
	}

	public String getNextNodeKeys() {
		return (String) getAttrValueByCode(NextNodeKeys);
	}

	public PDFForm setNextNodeKeys(String v) {
		setAttrValueByCode(NextNodeKeys, v);
		return this;
	}

	public String getErrorMsg() {
		return (String) getAttrValueByCode(ErrorMsg);
	}

	public PDFForm setErrorMsg(String errorMsg) {
		setAttrValueByCode(ErrorMsg, errorMsg);
		return this;
	}

	public String getErrorDetail() {
		return (String) getAttrValueByCode(ErrorDetail);
	}

	public PDFForm setErrorDetail(String errorDetail) {
		setAttrValueByCode(ErrorDetail, errorDetail);
		return this;
	}

	public String getStatus() {
		return (String) getAttrValueByCode(Status);
	}

	public PDFForm setStatus(String status) {
		setAttrValueByCode(Status, status);
		return this;
	}
	
	public boolean isColsed() throws Exception {
		return getBooleanByCode(Closed);
	}
	
	public PDFForm setClosed(boolean closed)throws Exception{
		setAttrValueByCode(Closed, closed);
		return this;
	}

}
