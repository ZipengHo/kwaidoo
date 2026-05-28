package cell.gpf.study.nadur.pdf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.config.IPDCMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.study.nadur.cdc.IStudyCDCOp;
import cell.gpf.study.nadur.cdc.StudyCDCMockConst;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cmn.anotation.ClassDeclare;
import cmn.dto.Progress;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.data.CColor;
import fe.cmn.res.JDFICons;
import gpf.adur.action.Action;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.dc.concrete.DCAction;
import gpf.dc.concrete.EntryRouter;
import gpf.dc.concrete.ExitRouter;
import gpf.dc.concrete.FlowLink;
import gpf.dc.concrete.RefFormField;
import gpf.dc.concrete.Router;
import gpf.dc.concrete.TriggerRule;
import gpf.dc.config.OperateLogStatusHookDto;
import gpf.dc.config.PDC;
import gpf.dc.config.PDF;
import gpf.dc.config.RefPDCNode;
import gpf.dc.fe.dto.FlowNodeStyle;
import gpf.dc.fe.dto.RefNodeExtendDto;
import gpf.enums.NodeActiveMode;
import gpf.enums.NodeLeaveType;
@ClassDeclare(label = "PDF操作代码样例"
,what="PDF操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyPDFOp extends CellIntf {
	static IStudyPDFOp get() {
		return Cells.get(IStudyPDFOp.class);
	}
	
	default void testPDFOp() throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		String rootUuid = IPDFMgr.get().getRootPDFId();
		PDF pdf = IPDFMgr.get().queryPDF(StudyPDFMockConst.PdfUuid);
		if(pdf == null) {
			pdf = new PDF();
			pdf.setParentId(rootUuid);//父模型ID
			pdf.setName(StudyPDFMockConst.PdfName);//模型名称
			pdf.setLabel(StudyPDFMockConst.PdfLabel);//模型中文名
			pdf.setPackagePath(StudyFormMockConst.PackagePath);//包路径
			pdf = IPDFMgr.get().createPDF(pdf);
			tracer.info("PDF:"+pdf.getUuid());
		}
		
		List<RefPDCNode> nodes = new ArrayList<>();
		List<FlowLink> links = new ArrayList<>();
		List<Router> routerList = new ArrayList<>();
		List<TriggerRule> triggerRuleList = new ArrayList<>();
		try(IDao dao = IDaoService.newIDao()){
			for(int i =0;i<10;i++) {
				RefPDCNode node = new RefPDCNode();
				node.setName("节点"+(i+1));
				//设置节点实例
				String pdcCode = pdf.getUuid()+"_"+node.getName();
				node.setData(getOrNewPDC(dao,pdcCode));
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
		}
		//设置节点连线和路由
		for(int i =1;i<10;i++) {
			RefPDCNode srcNode = nodes.get(i-1);
			RefPDCNode dstNode = nodes.get(i);
			FlowLink link = new FlowLink();
			//端口取值 ： s(下) n(上) w(左) e(右)，与画布相关
			link.setSrc(srcNode.getKey()).setSrcPort("s").setDst(dstNode.getKey()).setDstPort("n").setLabel(StudyPDFMockConst.TestAction);
			links.add(link);
			Router router = new Router();
			//进入路由有三种模型，任一节点、所有节点、自定义
			router.setEntryRouter(new EntryRouter().setActiveMode(NodeActiveMode.Any.name()));
			//离开路由，指定每条连线的路由规则，指定动作、自定义
			List<ExitRouter> exitRouterList = new ArrayList<>();
			ExitRouter exitRouter = new ExitRouter()
					.setNodeKey(srcNode.getKey()).setNodeKey(srcNode.getName())//当前节点
					.setTargetNodeKey(dstNode.getKey()).setTargetNodeName(dstNode.getName())//目标节点
					.setLeaveType(NodeLeaveType.Option)//离开方式，Option为匹配动作
					.setActionNames(Arrays.asList(StudyPDFMockConst.TestAction));//动作列表
			exitRouterList.add(exitRouter);
			router.setExitRouterList(exitRouterList);
			routerList.add(router);
		}
		//设置开始节点
		pdf.setStartNode(nodes.get(0).getKey());
		pdf.setNodes(nodes).setLinks(links);
		pdf.setRouterList(routerList);
		pdf.setTriggerRuleList(triggerRuleList);
		
		List<OperateLogStatusHookDto> operateLogStatusHooks = new ArrayList<>();
		pdf.setOperateLogStatusHooks(operateLogStatusHooks);
		Progress prog = null;
		IPDFMgr.get().updatePDF(prog, pdf);
	}
	
	default void testDeletePdf() throws Exception {
		IPDFMgr.get().deletePDF(StudyPDFMockConst.PdfUuid);
	}
	
	public default PDC getOrNewPDC(IDao dao,String pdcCode) throws Exception {
		String cdcId = StudyCDCMockConst.ModelId;
		PDC pdc = IPDCMgr.get().queryPDCByCode(dao, cdcId, pdcCode);
		if(pdc == null)
			pdc = IPDCMgr.get().newPDC(cdcId);
		List<DCAction> actionList = new ArrayList<>();
		actionList.add(IStudyCDCOp.get().newDCAction(dao, pdc.getUuid(), StudyPDFMockConst.TestAction));
		pdc.setActionList(actionList);
		List<RefFormField> refFieldList = new ArrayList<>();
		FormModel formModel = IFormMgr.get().queryFormModel(StudyFormMockConst.ModelId);
		Map<String,FormField> fieldMap = formModel.getFieldNameMap();
		refFieldList.add(IStudyCDCOp.get().newRefFormFields(formModel.getId(), fieldMap.get(StudyFormMockConst.TextField)));
		pdc.setRefFieldList(refFieldList);
//		pdc.setPrivilegeSettings(privilegeFunctions);
		return pdc;
	}
	
	public default Action newAction(IDao dao,String actionCode) throws Exception {
		Action action = IStudyCDCOp.get().queryOrNewAction(dao, actionCode);
		return action;
	}
}
