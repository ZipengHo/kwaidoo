package cell.gpf.dc.runtime;

import java.io.Serializable;
import java.util.List;

import com.leavay.nio.crpc.RpcMap;

import cell.ResourceCellIntf;
import cell.cdao.IDao;
import cell.gpf.cfg.AutoSubmitCallback;
import cell.gpf.cfg.SaveTotalFormCallback;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.dto.Progress;
import cmn.i18n.I18nIntf;
import crpc.CRpcContainer;
import crpc.CRpcContainerIntf;
import gpf.adur.data.Form;
import gpf.dc.concrete.RefActionNode;
import gpf.dc.config.PDC;
import gpf.dc.config.RefPDCNode;
import gpf.dc.runtime.ActionFlowInstance;
import gpf.dc.runtime.OperateLog;
import gpf.dc.runtime.PDCForm;
import gpf.dc.runtime.PDFInstance;
import gpf.dto.cfg.runtime.RuntimeContextIntf;
import gpf.translate.assist.RuntimeContextWrapper;

@ClassDeclare(label = "动作运行上下文"
,what = "存放动作运行时的上下文参数，包括：\r\n" + 
		"1.通过input.getRtx().getDao()获取事务对象(IDao)：可以在动作内部保持同个事务对数据进行增删改查操作，在不调用dao.commit()情况下，动作执行报错时可自动回滚事务;\r\n" + 
		"2.通过input.getRtx().getProcess()获取进度通知对象(Progress)：可以向调用方反馈进度信息，默认为null，需要在运行上下文筹备时设值;\r\n" + 
		"3.通过input.getRtx().getI18nIntf()获取国际化资源对象(I18nIntf)：可以获取文本对应的国际化文本，一般用于抛出异常时的文本国际化处理；\r\n" + 
		"4.通过input.getRtx().getUserModelId()获取用户模型ID：一般用于鉴权或在当前用户模型对用户数据增删改查；\r\n" + 
		"5.通过input.getRtx().getOrgModelId()获取组织模型ID：一般用于鉴权或在当前组织模型对组织数据增删改查；\r\n" + 
		"6.通过input.getRtx().getOperator()获取当前操作人编号：当前动作执行人，一般用于鉴权，根据操作人计算对数据的读写权限和动作操作权限；\r\n" + 
		"7.通过input.getRtx().getPdcForm()获取PDC表单(PDCForm)：PDCForm就是流程节点的表单，每个流程节点都可以定义一个表单结构，当运行到该节点时通常需要用户填写该表单并提交到流程中，这个表单就叫PDC表单，只有运行在流程节点上的动作才能拿到，通过它可以对提交的PDC表单进行校验/访问/操作等，通过setPdcForm()可以在事务中修改当前表单;\r\n" + 
		"8.通过input.getRtx().getCurrOpLog()获取当前操作记录(OperateLog):只有运行在流程节点上的动作才能拿到，需要设置接收人时，在流程节点的开始阶段调用OperateLog.setAssignee设置关联接收人的对象\r\n" + 
		"9.通过input.getRtx().getTotalForm()获取流程总表单(Form):只有运行在流程节点上的动作才能拿到，一般不需要使用\r\n" + 
		"10.通过input.getRtx().getParam(key)获取其他附加的运行上下文参数：需要在运行上下文筹备时通过setParam(key,value)设置后才能取到；"
, why = ""
,how = "另外：在一个动作中经常会需要调用其它动作或提交非当前运行流程时，那么就需要构建一个新的运行上下文传过去供其它action使用，一般可以用自己当前的上下文直接传过去，也可以克隆一个新的并做一些修改后再传递过去，样例代码如下：\r\n"
		+ "//获取当前运行上下文\r\n" + 
		"IDCRuntimeContext rtx = input.getRtx();\r\n" + 
		"//1.构建新的运行上下文并调用界面构建动作并弹窗\r\n" + 
		"IDCRuntimeContext newRtx = rtx.cloneRtx();\r\n" + 
		"//表格视图模型ID\r\n" + 
		"String actionModelId = PDFInstanceTableViewDto.FormModelId;\r\n" + 
		"//表格视图编号\r\n" + 
		"String actionCode = \"基础包_服务参数配置\";\r\n" + 
		"Action viewAction = IActionMgr.get().queryActionByCode(input.getRtx().getDao(), actionModelId, actionCode);\r\n" + 
		"PanelDto panel = (PanelDto) IActionMgr.get().executeAction(viewAction, newRtx);\r\n" + 
		"//设置窗口大小并弹窗\r\n" + 
		"panel.setPreferSize(WindowSizeDto.all(0.8, 0.8));\r\n" + 
		"PopDialog.show(panelContext, title, panel);\r\n" + 
		"\r\n" + 
		"//2.提交非当前运行流程的表单\r\n" + 
		"//调用其他流程执行，需要克隆一个不带当前流程上下文参数的上下文对象\r\n" + 
		"IDCRuntimeContext newRtx = rtx.cloneRtxWithoutPDF();\r\n" + 
		"String userCode = \"User_001\";//其他流程提交要传入上下文的用户编号，一般不需要重新设置\r\n" + 
		"String otherPdfUuid = \"gpf.md.DocumentLib\";//其他流程uuid或数据模型ID\r\n" + 
		"String otherActionName = \"提交\";//其他流程开始节点提交动作流名称\r\n" + 
		"try(IDao dao = IDaoService.newIDao()){\r\n" + 
		"	//构建开始节点的流程表单\r\n" + 
		"	PDCForm otherPdcForm = IPDFRuntimeMgr.get().newStartForm(newRtx, otherPdfUuid);\r\n" + 
		"	//必要的设置参数\r\n" + 
		"	newRtx.setDao(dao);//其他流程的提交一般与当前流程节点的提交不在一个事务上，需要重新设置新的事务\r\n" + 
		"	newRtx.setActionName(otherActionName);\r\n" + 
		"	//可选的设置参数\r\n" + 
		"	newRtx.setOperator(userCode);//一般不需要重新设置提交操作人，只有在需要重新指定时设置\r\n" + 
		"	newRtx.setParam(key, value);//其他需要传入上下文的运行参数\r\n" + 
		"	//创建并提交开始节点表单动作\r\n" + 
		"	IPDFRuntimeMgr.get().createAndSubmitPDCForm(otherPdfUuid, newRtx, otherPdcForm);\r\n" + 
		"}"
		+ "以下是在动作中使用上下文参数的样例，\r\n"
		+ "示例1.流程节点调用动作，对当前运行上下文中的流程节点表单进行校验和填值，以动作入参BaseActionParameter为例：\r\n"
		+ "BaseActionParameter input;\r\n"
		+ "//获取当前运行上下文\r\n" + 
		"		IDCRuntimeContext rtx = input.getRtx();\r\n" + 
		"		//获取流程节点表单\r\n" + 
		"		PDCForm pdcForm = rtx.getPdcForm();\r\n" + 
		"		String textField = pdcForm.getString(\"文本\");\r\n" + 
		"		//对流程节点表单值进行校验\r\n" + 
		"		if(CmnUtil.isStringEqual(textField, \"测试\"))\r\n" + 
		"			throw new VerifyException(\"表单属性值不合法：\" + textField);//抛出VerifyException指定为校验异常\r\n" + 
		"		//对流程节点表单值进行设置，通过setAttrValue为用户定义的模型属性设置，如果是系统属性，直接通过setAttrValueByCode设值，\r\n" + 
		"		pdcForm.setAttrValueByCode(Form.Code, \"001\");\r\n" + 
		"		pdcForm.setAttrValue(\"文本\", \"测试值\");\r\n" + 
		"		//表单值设置后，不同于模型数据的更新操作，这里将数据设置会上下文中，由流程流转时自动更新流程总表单数据\r\n" + 
		"		rtx.setPdcForm(pdcForm);\r\n" 
		+ "示例2.界面调用动作，以动作入参BaseFeActionParameter为例：\r\n"
		+ "BaseFeActionParameter input;\r\n"
		+ "//界面动作监听传入对象\r\n" + 
		"		ListenerDto listener = input.getListener();\r\n" + 
		"		//界面上下文对象\r\n" + 
		"		PanelContext panelContext = input.getPanelContext();\r\n" + 
		"		//界面组件对象\r\n" + 
		"		AbsComponent component = (AbsComponent) input.getCurrentComponent();\r\n" + 
		"		//继承于BaseFeActionParameter的参数类，可以直接通过getForm获取到界面的表单数据\r\n" + 
		"		if(component instanceof AbsFormView) {\r\n" + 
		"			//界面的表单值获取与流程内部的流程节点表单值获取略有不同，需要通过界面拿到用户输入值，这里已经通过input.getForm()封装\r\n" + 
		"			Form form = input.getForm();\r\n" + 
		"			//需要更新的属性值map，属性值类型为表单定义的属性类型，其中key是属性的code，除了模型上的系统属性直接通过常量获取外，其他用户定义的模型属性通过IFormMgr.get().getFieldCode()可获取属性名称对应的code\r\n" + 
		"			Map<String,Object> fieldValueMap = new LinkedHashMap<>();\r\n" + 
		"			//设置表单属性值的方式具体见IStudyFormOp\r\n" + 
		"			//设置模型编号，模型编号是系统属性，直接用常量获取\r\n" + 
		"			fieldValueMap.put(Form.Code, \"001\");\r\n" + 
		"			//设置用户定义的模型属性，\r\n" + 
		"			String fieldCode = IFormMgr.get().getFieldCode(\"文本属性\");\r\n" + 
		"			fieldValueMap.put(fieldCode, \"自定义输入的文本值\");\r\n" + 
		"			//最后将计算好的表单值重新写到界面上，通过封装好的AbsFormView.setEditorValues方法设置\r\n" + 
		"			((AbsFormView) component).setEditorValues(panelContext, form, fieldValueMap);\r\n" + 
		"		}"
, developer = "陈晓斌"
, createTime = "2025-03-14"
,updateTime = "2025-03-14"
, version = "1.0" )
@CRpcContainer
public interface IDCRuntimeContext extends ResourceCellIntf, RuntimeContextIntf,CRpcContainerIntf, Serializable {
	
	public final static String ContextKey_CurrentActionInstance = "$CurrentActionInstance$";
	@Override
	default void onClose() {
		// TODO Auto-generated method stub

	}
	public RuntimeContextWrapper getWrapper()throws Exception;
	public void setWrapper(RuntimeContextWrapper wrapper)throws Exception;
	
	@MethodDeclare(label = "获取DAO事务对象",
	        what = "可以在动作内部保持同个事务对数据进行增删改查操作，在不调用dao.commit()情况下，动作执行报错时可自动回滚事务",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public IDao getDao() throws Exception;
	
	@MethodDeclare(label = "设置DAO事务对象",
	        what = "设置DAO事务对象，只在筹备运行上下文时设置",
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "dao", label = "DAO事务对象", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setDao(IDao dao) throws Exception;
	
	@MethodDeclare(label = "获取进度通知对象",
	        what = "获取进度通知对象，可以向调用方反馈进度信息，默认为null，需要在运行上下文筹备时设值",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public Progress getProgress() throws Exception;
	
	@MethodDeclare(label = "设置进度通知对象",
	        what = "设置进度通知对象，只在筹备运行上下文时设值",
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "prog", label = "进度通知对象", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setProgress(Progress prog) throws Exception;
	
	@MethodDeclare(label = "向调用方反馈进度信息",
	        what = "向调用方反馈进度信息，当进度通知对象为null时不反馈",
	        why = "",
	        how = "示例：rtx.sendProcess(30,\"当前进度在xxx阶段\",true);",
	        inputs = {
	                @InputDeclare(name = "iProcess", label = "进度百分比(1-100)", desc = ""),
	                @InputDeclare(name = "sMsg", label = "反馈的信息", desc = ""),
	                @InputDeclare(name = "blNewLine", label = "反馈信息是否换行", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void sendProcess(int iProcess, String sMsg, boolean blNewLine) throws Exception;
	
	@MethodDeclare(label = "向调用方反馈信息",
	        what = "向调用方反馈信息，当进度通知对象为null时不反馈",
	        why = "",
	        how = "示例：rtx.sendProcess(\"当前进度在xxx阶段\",true);",
	        inputs = {
	                @InputDeclare(name = "sMsg", label = "反馈的信息", desc = ""),
	                @InputDeclare(name = "blNewLine", label = "反馈信息是否换行", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setMessage(String sMsg, boolean blNewLine) throws Exception;
	
	public String getCdcId() throws Exception;

	public void setCdcId(String cdcId) throws Exception;

	public PDC getPdc() throws Exception;

	public void setPdc(PDC pdc) throws Exception;

	@MethodDeclare(label = "获取流程节点表单",
	        what = "获取PDC表单（流程节点表单），只有运行在流程节点上的动作才能拿到，可以对提交的PDC表单进行校验，通过setPdcForm()可以在事务中修改数据",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public PDCForm getPdcForm() throws Exception;
	
	@MethodDeclare(label = "设置流程节点表单",
	        what = "设置PDC表单(流程节点表单)，只有运行在流程节点上的动作才能生效，可在事务中修改数据",
	        why = "",
	        how = "示例：//获取流程节点表单\r\n" + 
	        		"		PDCForm pdcForm = rtx.getPdcForm();\r\n" + 
	        		"		//获取属性值的方式与表单一样，具体见IStudyFormOp\r\n" + 
	        		"		String textField = pdcForm.getString(\"文本\");\r\n" + 
	        		"		//对流程节点表单值进行校验\r\n" + 
	        		"		if(CmnUtil.isStringEqual(textField, \"测试\"))\r\n" + 
	        		"			throw new VerifyException(\"表单属性值不合法：\" + textField);//抛出VerifyException指定为校验异常\r\n" + 
	        		"		//对流程节点表单值进行设置，通过setAttrValue为用户定义的模型属性设置，如果是系统属性，直接通过setAttrValueByCode设值，\r\n" + 
	        		"		pdcForm.setAttrValueByCode(Form.Code, \"001\");\r\n" + 
	        		"		pdcForm.setAttrValue(\"文本\", \"测试值\");\r\n" + 
	        		"		//设置属性值的方式与表单一样，具体见IStudyFormOp\r\n" + 
	        		"		//表单值设置后，不同于模型数据的更新操作，这里将数据设置会上下文中，由流程流转时自动更新流程总表单数据\r\n" + 
	        		"		rtx.setPdcForm(pdcForm);\r\n",
	        inputs = {
	                @InputDeclare(name = "pdcForm", label = "流程节点表单", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setPdcForm(PDCForm pdcForm) throws Exception;
	public boolean isPdcFormModified();
	
	/**
	 * 交互时的表单
	 * @return
	 * @throws Exception
	 */
	public Form getInteractiveForm()throws Exception;
	/**
	 * 设置交互时的表单
	 * @param form
	 * @throws Exception
	 */
	public void setInteractiveForm(Form form)throws Exception;
	@MethodDeclare(label = "流程总表单",
	        what = "流程总表单，只有运行在流程节点上的动作才能拿到，一般不需要使用", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public Form getTotalForm() throws Exception;
	
	public void setTotalForm(Form totalForm) throws Exception;

	public boolean isTotalFormModified() ;
	
	public void saveTotalForm()throws Exception;

	@MethodDeclare(label = "流程uuid或数据模型ID",
	        what = "当前运行上下文所使用的流程或数据模型", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getPdfUuid() throws Exception;
	@MethodDeclare(label = "设置流程uuid或数据模型ID",
	        what = "流程uuid或数据模型ID，筹备运行上下文时设置，指定当前运行上下文所使用的流程或数据模型", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setPdfUuid(String pdfUuid) throws Exception;

	@MethodDeclare(label = "提交要执行的流程节点动作流名称",
	        what = "提交要执行的流程节点动作流名称，只有运行在流程节点上的动作才能拿到，动作中可根据动作名称做分支处理，路由决策", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getActionName() throws Exception;
	
	@MethodDeclare(label = "设置提交的流程节点动作流名称",
	        what = "提交要执行的流程节点动作流名称，只有运行在流程节点上的动作才能拿到，动作中可根据动作名称做分支处理，路由决策", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "actionName", label = "流程节点动作流名称", desc = "")
	    	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setActionName(String actionName) throws Exception;

	@MethodDeclare(label = "操作人",
	        what = "操作人，一般用于鉴权，根据操作人计算对数据的读写权限和动作操作权限", 
	        why = "",
	        how = "示例通过获取用户编号进行鉴权：\r\n"
	        		+ "IDCRuntimeContext rtx = input.getRtx();\r\n" + 
	        		"		IDao dao = rtx.getDao();\r\n" + 
	        		"		String userModelId = input.getUserModelId();\r\n" + 
	        		"		String operator = rtx.getOperator();\r\n" + 
	        		"		//获取输入参数中指定的组织路径，如：组织1/部门1/小组1\r\n" + 
	        		"		String specificOrgPath = input.getSpecificOrgPath();\r\n" + 
	        		"		//获取输入参数中指定的组织名称\r\n" + 
	        		"		String orgName = input.getOrgName();\r\n" + 
	        		"		List<String> orgNames = Arrays.asList(orgName.split(\",\"));\r\n" + 
	        		"		String orgModelId = input.getOrgModelId();\r\n" + 
	        		"		//查询用户编号对应的用户对象\r\n" + 
	        		"		User user = IUserMgr.get().queryUserByCode(dao, userModelId, operator);\r\n" + 
	        		"		//查询用户所在的组织结果集\r\n" + 
	        		"		ResultSet<Org> rs = IRoleMgr.get().queryOrgPageOfUser(dao, orgModelId, null, userModelId, user.getUuid(), 1, Integer.MAX_VALUE);\r\n" + 
	        		"		if(rs.isEmpty())\r\n" + 
	        		"			return false;\r\n" + 
	        		"		//过滤掉不匹配组织名称的组织\r\n" + 
	        		"		List<Org> orgList = rs.getDataList();\r\n" + 
	        		"		orgList = orgList.stream().filter(v->{\r\n" + 
	        		"			try {\r\n" + 
	        		"				return orgNames.contains(v.getLabel());\r\n" + 
	        		"			} catch (Exception e) {\r\n" + 
	        		"				return false;\r\n" + 
	        		"			}\r\n" + 
	        		"		}).collect(Collectors.toList());\r\n" + 
	        		"		//查询组织的路径信息，并判定是否以传入的组织路径作为前缀，是表示匹配，否则不匹配\r\n" + 
	        		"		Map<String,Org> orgMap = IRoleMgr.get().queryPathOfOrg(dao, orgList);\r\n" + 
	        		"		for(String key : orgMap.keySet()) {\r\n" + 
	        		"			if(key.startsWith(specificOrgPath))\r\n" + 
	        		"				return true;\r\n" + 
	        		"		}\r\n" + 
	        		"		return false;",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getOperator() throws Exception;
	/**
	 * 设置上下文中的操作人，只在筹备运行上下文时设置
	 */
	@MethodDeclare(label = "设置上下文中的操作人",
	        what = "设置上下文中的操作人，只在筹备运行上下文时设置", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "operator", label = "操作人编号", desc = "")
	    	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setOperator(String operator) throws Exception;

	/**
	 * PDF实例
	 * 
	 * @return
	 */
	public PDFInstance getPdfInstance() throws Exception;

	public void setPdfInstance(PDFInstance pdfInstance) throws Exception;
	
	public PDFInstance savePdfInstance(PDFInstance pdfInstance)throws Exception;
	
	public ActionFlowInstance getActionFlowInstance() throws Exception;

	public void setActionFlowInstance(ActionFlowInstance actionFlow) throws Exception;

	public RefActionNode getRefActionNode() throws Exception;

	public void setRefActionNode(RefActionNode refAction) throws Exception;

	public RefPDCNode getRefPDCNode() throws Exception;
	

	public void setRefPDCNode(RefPDCNode refPdc) throws Exception;
	
	@MethodDeclare(label = "获取当前流程节点的操作记录",
	        what = "获取当前流程节点的操作记录，只有运行在流程节点上的动作才能拿到，需要设置接收人时，在流程节点的开始阶段调用OperateLog.setAssignee设置关联接收人的对象",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public OperateLog getCurrOpLog() throws Exception;
	
	@MethodDeclare(label = "设置当前节点的操作记录",
	        what = "设置当前节点的操作记录，只在筹备运行上下文或流程节点开始阶段设置接收人时设置",
	        why = "",
	        how = "示例通过计算当前节点接收人后设置在操作记录上，注意接收人设置只在节点启动时有效：\r\n"
	        		+ "IDCRuntimeContext rtx = input.getRtx();\r\n" + 
	        		"		List<User> assignees = getAssigneeList(input);\r\n" + 
	        		"		OperateLog opLog = rtx.getCurrOpLog();\r\n" + 
	        		"		List<AssociationData> names = new ArrayList<>();\r\n" + 
	        		"		List<String> cnNames = new ArrayList<>();\r\n" + 
	        		"		for(User assignee : assignees) {\r\n" + 
	        		"			AssociationData assocData = new AssociationData(CDoUser.class.getName(), assignee.getCode());\r\n" + 
	        		"			names.add(assocData);\r\n" + 
	        		"			cnNames.add(assignee.getFullName());\r\n" + 
	        		"		}\r\n" + 
	        		"		opLog.setAssignee(names);\r\n" + 
	        		"		opLog.setAssigneeCnName(String.join(\",\", ToolUtilities.toStringArray(cnNames)));\r\n" + 
	        		"		rtx.setCurrOpLog(opLog);",
	        inputs = {
	                @InputDeclare(name = "currOpLog", label = "当前节点的操作记录", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setCurrOpLog(OperateLog currOpLog) throws Exception;
	
	public boolean isCurrOpLogModified() ;

	public RpcMap getInput() throws Exception;

	public void setInput(RpcMap input) throws Exception;

	public RpcMap getResultMap() throws Exception;

	public void setResultMap(RpcMap resultMap) throws Exception;
	
	@MethodDeclare(label = "获取运行上下文中的附加参数",
	        what = "获取运行上下文中的附加参数，在筹备运行上下文或动作运行时通过setParam设置", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public Object getParam(String key) throws Exception;
	
	@MethodDeclare(label = "设置运行上下文中的附加参数",
	        what = "设置运行上下文中的附加参数，可在动作运行时设置", 
	        why = "",
	        how = "示例在界面上触发调用动作时设置附加的运行上下文参数：\r\n"
	        		+ "//设置当前面板上下文\r\n"
	        		+ "rtx.setParam(FeActionParameter_PanelContext, panelContext);\r\n"
	        		+ "//设置当前面板对象\r\n" + 
	        		"rtx.setParam(FeActionParameter_CurrentComponent, currComponent);",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setParam(String key, Object value) throws Exception;

	public void savePdfInstanceContext(String key,Object value)throws Exception;
	
	public Object getPdfInstanceContext(String key)throws Exception;
	
	@MethodDeclare(label = "运行上下文中的用户模型ID",
	        what = "运行上下文中的用户模型ID，一般用于鉴权或在当前用户模型对用户数据增删改查", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getUserModelId()throws Exception;
	
	@MethodDeclare(label = "设置用户模型ID",
	        what = "设置用户模型ID，只在筹备运行上下文时设置", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "userModelId", label = "用户模型ID", desc = ""),
	    	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setUserModelId(String userModelId)throws Exception;

	@MethodDeclare(label = "运行上下文中的组织模型ID",
	        what = "运行上下文中的组织模型ID，一般用于鉴权或在当前组织模型对组织数据增删改查", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getOrgModelId()throws Exception;
	@MethodDeclare(label = "设置组织模型ID",
	        what = "设置组织模型ID，只在筹备运行上下文时设置", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "orgModelId", label = "组织模型ID", desc = ""),
	    	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setOrgModelId(String orgModelId)throws Exception;

	@MethodDeclare(label = "获取国际化资源接口",
	        what = "", 
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public I18nIntf getI18n()throws Exception;

	@MethodDeclare(label = "设置国际化资源接口",
	        what = "设置国际化资源接口，只在筹备运行上下文时设置", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "i18n", label = "国际化资源接口", desc = ""),
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public void setI18n(I18nIntf i18n)throws Exception;

	@MethodDeclare(label = "获取国际化资源文本",
	        what = "支持传入参数进行文本格式化输出，如：属性{1}值{2}不合法，其中{1}表示拿传入的params的第一个元素，{2}表示拿传入的params的第二个元素", 
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "key", label = "需要国际化的文本", desc = ""),
	                @InputDeclare(name = "params", label = "文本中需要格式化替换的参数值", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getI18nString(String key,Object... params)throws Exception;
	
	@MethodDeclare(label = "获取分组中的国际化资源文本",
	        what = "获取分组中的国际化资源文本，分组中找不到时会查找默认分组下的国际化文本，当国际化资源接口为null时返回原文本"
	        		+ "支持传入参数进行文本格式化输出，如：属性{1}值{2}不合法，其中{1}表示拿传入的params的第一个元素，{2}表示拿传入的params的第二个元素",
	        why = "",
	        how = "",
	        inputs = {
	                @InputDeclare(name = "key", label = "需要国际化的文本", desc = ""),
	                @InputDeclare(name = "group", label = "获取国际化文本的分组名称", desc = ""),
	                @InputDeclare(name = "params", label = "文本中需要格式化替换的参数值", desc = "")
	        },
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public String getI18nStringInGroup(String key,String group,Object... params)throws Exception;
	/**
	 * 设置是否允许异步自动提交
	 * @param enable
	 * @throws Exception
	 */
	public void setEnableAsyncAutoSubmit(boolean enable);
	/**
	 * 是否允许异步自动提交
	 * @return
	 */
	public boolean isEnableAysncAutoSubmit();
	/**
	 * 提交失败时是否记录日志
	 * @return
	 */
	public boolean isEnableLogSubmitFailed();
	/**
	 * 设置提交失败时是否记录日志
	 * @param enable
	 */
	public void setEnableLogSubmitFailed(boolean enable);
	
	public RpcMap<Object> getActionRcKeepParams();
	/**
	 * 获取总表单更新回调
	 * @return
	 */
	public List<Class<? extends SaveTotalFormCallback>> getSaveTotalFormCallbacks();
	public void setSaveTotalFormCallbacks(List<Class<? extends SaveTotalFormCallback>> callbacks);
	public void addSaveTotalFormCallback(Class<? extends SaveTotalFormCallback> callback);
	public RpcMap<Object> getSaveTotalFormCallbackParam();
	public void setSaveTotalFormCallbackParam(RpcMap<Object> param);
	public void addSaveTotalFormCallbackParam(String key,Object value);
	
	
	public Class<? extends AutoSubmitCallback> getAutoSubmitCallback();
	public void setAutoSubmitCallback(Class<? extends AutoSubmitCallback> callback);
	public RpcMap<Object> getAutoSubmitCallbackParam();
	public void setAutoSubmitCallbackParam(RpcMap<Object> param);
	public void addAutoSubmitCallbackParam(String key,Object value);
	
	/**
	 * 只有当子流程结束时反向触发父流程准备进行下一跳时，才会将自身当前的上下文设置在这里，从而便于父流程中的监听器处理
	 * @return
	 */
	public IDCRuntimeContext getSubFlowContext() throws Exception;
	
	@MethodDeclare(label = "克隆一个新的运行上下文对象",
	        what = "在动作代码运行时，当需要调用到其他动作执行时，可以通过clone方法构建新的运行上下文，再设置调用其他动作所需的运行参数",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public IDCRuntimeContext cloneRtx();
	@MethodDeclare(label = "克隆一个新的运行上下文对象,不携带流程相关参数",
	        what = "在动作代码运行时，当需要调用到其他流程提交执行时，可以通过cloneWithoutPDF方法构建新的运行上下文",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public IDCRuntimeContext cloneRtxWithoutPDF();
	
}
