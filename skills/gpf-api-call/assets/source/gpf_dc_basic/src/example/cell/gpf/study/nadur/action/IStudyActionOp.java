package cell.gpf.study.nadur.action;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.WidgetDto;
import fe.util.component.Component;
import gpf.adur.action.Action;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.FormParameter;
import gpf.dc.basic.param.view.FormTableParameter;
import gpf.dc.basic.param.view.dto.FilterDto;
@ClassDeclare(label = "动作数据操作代码样例"
,what="动作数据操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyActionOp extends CellIntf{

	static IStudyActionOp get() {
		return Cells.get(IStudyActionOp.class);
	}
	
	
	default void testActionOp() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String code = "测试动作001";
		try(IDao dao = IDaoService.newIDao()){
			Action action = IActionMgr.get().queryActionByCode(dao, StudyActionMockConst.ModelId, code);
			if(action == null) {
				action = new Action(StudyActionMockConst.ModelId);
				action.setCode(code);
				//设置属性值与数据模型一样，具体见IStudyFormOps
				action = IActionMgr.get().createAction(dao, action);
			}else{
				action = IActionMgr.get().updateAction(dao, action);
			}
			Cnd cnd = Cnd.NEW();
			cnd.where(new SqlExpressionGroup().andEquals(Action.Code, code));
			ResultSet<Action> rs = IActionMgr.get().queryActionPage(dao, StudyActionMockConst.ModelId, cnd, 1, 20);
			tracer.info("查询记录数："+ rs.getTotalCount());
//			IActionMgr.get().deleteActionByCnd(dao, StudyActionMockConst.ModelId, cnd);
			dao.commit();
		}
	}
	
	default void executeAction() throws Exception {
		String code = "测试动作001";
		try(IDao dao = IDaoService.newIDao()){
			Action action = IActionMgr.get().queryActionByCode(dao, StudyActionMockConst.ModelId, code);
			IDCRuntimeContext rtx  = IPDFRuntimeMgr.get().newRuntimeContext();
			rtx.setDao(dao);
			IActionMgr.get().executeAction(action, rtx);
		}
	}
	
	default void executeViewAction(PanelContext panelContext,Component currComponent) throws Exception {
		String code = "测试动作001";
		try(IDao dao = IDaoService.newIDao()){
			Action action = IActionMgr.get().queryActionByCode(dao, StudyActionMockConst.ModelId, code);
			IDCRuntimeContext rtx  = IPDFRuntimeMgr.get().newRuntimeContext();
			rtx.setDao(dao);
			BaseFeActionParameter.prepareFeActionParameter(rtx, panelContext, currComponent);
			IActionMgr.get().executeAction(action, rtx);
		}
	}
	
	default void executeView(PanelContext panelContext,Component currComponent) throws Exception {
		String code = "测试动作001";
		try(IDao dao = IDaoService.newIDao()){
			Action action = IActionMgr.get().queryActionByCode(dao, StudyActionMockConst.ModelId, code);
			IDCRuntimeContext rtx  = IPDFRuntimeMgr.get().newRuntimeContext();
			rtx.setDao(dao);
			BaseFeActionParameter.prepareFeActionParameter(rtx, panelContext, currComponent);
			
			//表格视图上下文参数
			//默认过滤条件表达式
			SqlExpressionGroup expression = new SqlExpressionGroup();
			FormTableParameter.setDefaultFilter(rtx, expression);
			//初级过滤配置
			FilterDto filterDto = new FilterDto();
			FormTableParameter.setInitFilterDto(rtx, filterDto);
			
			//表单视图上下文参数
			Form data = null;
			FormParameter.setData(rtx, data);
			//是否新增数据
			boolean initDataValue = true;
			FormParameter.setInitDataValue(rtx, initDataValue);
			WidgetDto widget = (WidgetDto) IActionMgr.get().executeAction(action, rtx);
		}
	}
	
}
