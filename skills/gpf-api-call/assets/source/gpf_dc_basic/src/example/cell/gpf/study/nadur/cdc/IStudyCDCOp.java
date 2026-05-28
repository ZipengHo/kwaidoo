package cell.gpf.study.nadur.cdc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.concrete.ICDCMgr;
import cell.gpf.study.nadur.action.StudyActionMockConst;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cell.gpf.study.nadur.pdf.StudyPDFMockConst;
import cell.gpf.study.nadur.role.StudyOrgMockConst;
import cmn.anotation.ClassDeclare;
import cmn.dto.Progress;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import fe.cmn.data.CColor;
import fe.cmn.res.JDFICons;
import gpf.adur.action.Action;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.dc.concrete.CDC;
import gpf.dc.concrete.DCAction;
import gpf.dc.concrete.DCActionFlow;
import gpf.dc.concrete.FlowLink;
import gpf.dc.concrete.PrivilegeSetting;
import gpf.dc.concrete.RefActionConfig;
import gpf.dc.concrete.RefActionNode;
import gpf.dc.concrete.RefFormField;
import gpf.dc.fe.dto.FlowNodeStyle;
import gpf.dc.fe.dto.RefNodeExtendDto;
import gpf.enums.NodeTriggerTime;
@ClassDeclare(label = "CDC操作代码样例"
,what="CDC操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyCDCOp extends CellIntf{
	
	static IStudyCDCOp get() {
		return Cells.get(IStudyCDCOp.class);
	}
	
	default void testActionModelOp()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		String rootModelId = ICDCMgr.get().getRootCdcId();
		CDC cdc = ICDCMgr.get().queryCDC(StudyCDCMockConst.ModelId);
		if(cdc == null) {
			cdc = new CDC();
			cdc.setParentId(rootModelId);//父模型ID
			cdc.setName(StudyCDCMockConst.ModelName)//模型名称
			.setLabel(StudyCDCMockConst.ModelLabel)//模型中文名
			.setPackagePath(StudyCDCMockConst.PackagePath);//包路径
			//构建属性与表单操作一样，具体见IStudyFormModelOp;
			cdc = ICDCMgr.get().createCDC(cdc);
		}
		Progress prog = null;
		try(IDao dao = IDaoService.newIDao()){
			List<DCAction> actionList = new ArrayList<>();
			actionList.add(newDCAction(dao,cdc.getId(),StudyCDCMockConst.TestAction));
			List<RefFormField> dcFieldList = new ArrayList<>();
			FormModel formModel = IFormMgr.get().queryFormModel(StudyFormMockConst.ModelId);
			Map<String,FormField> fieldMap = formModel.getFieldNameMap();
			dcFieldList.add(newRefFormFields(formModel.getId(), fieldMap.get(StudyFormMockConst.TextField)));
			List<PrivilegeSetting> privlegeSettings = new ArrayList<>();
			privlegeSettings.add(newPrivilegeSetting(dao, cdc.getId(), StudyOrgMockConst.TestRoleCode));
			cdc.setActionList(actionList);
			cdc.setRefFieldList(dcFieldList);
			cdc.setPrivilegeSettings(privlegeSettings);
			ICDCMgr.get().updateCDC(prog, cdc);
		}
	}
	
	default DCAction newDCAction(IDao dao,String cdcId,String actionName) throws Exception {
		DCAction dcAction = new DCAction();
		dcAction.setName(actionName);
		dcAction.setExcuteTiming(NodeTriggerTime.ExternalInput);
		dcAction.setFlow(newDCActionFlow(dao,cdcId,actionName));
		return dcAction;
	}
	
	default DCActionFlow newDCActionFlow(IDao dao,String cdcId,String flowName) throws Exception {
		DCActionFlow flow = new DCActionFlow();
		flow.setName(flowName);
		List<RefActionNode> nodes = new ArrayList<>();
		List<FlowLink> links = new ArrayList<>();
		for(int i =0;i<10;i++) {
			RefActionNode node = new RefActionNode();
			node.setName("节点"+(i+1));
			//设置节点实例
			node.setData(queryOrNewAction(dao,cdcId+"_"+flowName+"_"+node.getName()));
			RefNodeExtendDto extendInfo = new RefNodeExtendDto();
			FlowNodeStyle nodeStyle = new FlowNodeStyle();
			nodeStyle.setColor(CColor.fromColor(Color.GREEN));
			nodeStyle.setIcon(JDFICons.document);
			nodeStyle.setShape(FlowNodeStyle.SHAPE_RECTANGLE);
//			nodeStyle.setX(x);//与画布相关，这里不设置
//			nodeStyle.setY(y);//与画布相关，这里不设置
			extendInfo.setStyle(nodeStyle);
			node.setExtendInfo(extendInfo);
			nodes.add(node);
		}
		//设置节点连线
		for(int i =1;i<10;i++) {
			RefActionNode srcNode = nodes.get(i-1);
			RefActionNode dstNode = nodes.get(i);
			FlowLink link = new FlowLink();
			//端口取值 ： s(下) n(上) w(左) e(右)，与画布相关
			link.setSrc(srcNode.getKey()).setSrcPort("s").setDst(dstNode.getKey()).setDstPort("n").setLabel(StudyPDFMockConst.TestAction);
			links.add(link);
		}
		flow.setNodes(nodes);
		flow.setLinks(links);
		flow.setStartNode(nodes.get(0).getKey());
		return flow;
	}
	
	default Action queryOrNewAction(IDao dao,String actionCode) throws Exception {
		Action action = IActionMgr.get().queryActionByCode(dao, StudyActionMockConst.ModelId, actionCode);
		if(action == null) {
			action = new Action(StudyActionMockConst.ModelId);
			action.setCode(actionCode);
		}
		return action;
	}
	
	default RefFormField newRefFormFields(String formModelId,FormField formField)throws Exception{
		RefFormField refFormField = new RefFormField();
		refFormField.setName(formField.getName()).setFormModelID(formModelId).setFormFieldCode(formField.getCode());
		refFormField.setNotNull(false);
		return refFormField;
	}
	
	default PrivilegeSetting newPrivilegeSetting(IDao dao,String cdcId,String roleCode)throws Exception{
		PrivilegeSetting privilegeSetting = new PrivilegeSetting();
		privilegeSetting.setRoleCode(roleCode);
		List<RefActionConfig> matchUserFunctions = new ArrayList<>();
		matchUserFunctions.add(newRefAction(dao, cdcId, roleCode, "鉴权"));
		List<RefActionConfig> privilegeFunctions=  new ArrayList<>();
		privilegeFunctions.add(newRefAction(dao, cdcId, roleCode, "授权"));
		privilegeSetting.setMatchUserFunctions(matchUserFunctions).setPrivilegeFunctions(privilegeFunctions);
		return privilegeSetting;
	}
	
	default RefActionConfig newRefAction(IDao dao,String cdcId,String roleCode,String name) throws Exception {
		RefActionConfig refConfig = new RefActionConfig();
		refConfig.setName(name).setAction(queryOrNewAction(dao, cdcId+"_"+roleCode+"_"+name));
		return refConfig;
	}
	
	
	default void deleteCdc()throws Exception{
		String formModelID = StudyCDCMockConst.ModelId;
		ICDCMgr.get().deleteCDC(formModelID);
	}
	
	default void queryCdcPage()throws Exception{
		List<String> parentIds = CollUtil.newArrayList(ICDCMgr.get().getRootCdcId());
		String packagePath = null;
		String keyword = null;
		int pageNo = 1;
		int pageSize = 20;
		ICDCMgr.get().queryCDCPage(parentIds, packagePath, keyword, pageNo, pageSize);
	}
}
