package cell.gpf.study.action.view;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.app.ability.PopToast;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.QueryPanelContextNodeResult;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.fe.util.GpfViewActionUtil;
import gpf.dc.basic.fe.util.PanelUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "通过路径查找面板组件"
,what="演示通过路径查找面板组件实例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-18"
,updateTime = "2025-02-18")
public interface IStudyGetComponentByPath  <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		if(true) {
			PopToast.info(panelContext.getChannel(), "触发值改变监听"+listener.getSourceWidgetId());
			return null;
		}
		tracer.logStart();
		QueryPanelContextNodeResult panelCtxResult = (QueryPanelContextNodeResult) listener.getServiceCallbackResult(AbsFormView.CallbackKey_QueryPanelContextNode);
		if(panelCtxResult != null) {
			PanelUtil panelContextUtil = new PanelUtil(panelContext,panelCtxResult);
			tracer.info(panelContextUtil.toTreeString());
			String path = "..";
			AbsComponent targetCmp = panelContextUtil.getComponentByPath(panelContext, path);
			tracer.info(path+":"+targetCmp);
			//获取上上层级面板
			path = "../..";
			targetCmp = panelContextUtil.getComponentByPath(panelContext, path);
			tracer.info(path+":"+targetCmp);
//			获取上层级面板下的属性表面板
			path = "../"+IFormMgr.get().getFieldCode("属性表");
			targetCmp = panelContextUtil.getComponentByPath(panelContext, path);
			tracer.info(path+":"+targetCmp);
			//获取上上层级面板下的属性表面板
			path = "../../"+IFormMgr.get().getFieldCode("属性表");
			targetCmp = panelContextUtil.getComponentByPath(panelContext, path);
			tracer.info(path+":"+targetCmp);
			//获取当前层级面板下的属性表面板
			path = IFormMgr.get().getFieldCode("属性表");
			targetCmp = panelContextUtil.getComponentByPath(panelContext, path);
			tracer.info(path+":"+targetCmp);
//			if(true)
//				return null;
		}
		tracer.infoCost("", "PanelUtil执行耗时");
		tracer.logStart();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
//		组件相对路径 ..表示上一层，属性编号表示当前层级下的指定属性的面板，表单上的表格、子表单、列表、弹窗表单为每一级路径对应的面板，例如
//		A表单下的B表格，触发弹窗显示了C表单下，C面板下有一个D表格，通过D表格的按钮触发查询A表单下的E表格，路径表示为：
//		../../../E	: 第一个.. 取到 C面板，第二个.. 取到B面板，第三个..取到A面板，最后的E取到E面板
		//此方法需要多次与前端交互获取父面板和子面板信息，耗时较长，需要谨慎使用
		//获取上一层级面板
		String path = "..";
		getComponent(tracer, panelContext, path);
		//获取上上层级面板
		path = "../../..";
		getComponent(tracer, panelContext, path);
//		获取上层级面板下的属性表面板
		path = "../"+IFormMgr.get().getFieldCode("属性表");
		getComponent(tracer, panelContext, path);
		//获取上上层级面板下的属性表面板
		path = "../../"+IFormMgr.get().getFieldCode("属性表");
		getComponent(tracer, panelContext, path);
		//获取当前层级面板下的属性表面板
		path = IFormMgr.get().getFieldCode("属性表");
		getComponent(tracer, panelContext, path);
		tracer.infoCost("", "GpfViewActionUtil执行耗时");
		return null;
	}
	
	default void getComponent(Tracer tracer,PanelContext panelContext,String path) throws Exception {
		AbsComponent targetCmp = GpfViewActionUtil.getComponentByPath(panelContext, path);
		tracer.info(path +"="+targetCmp);
		if(targetCmp != null) {
			tracer.info("widgetId="+targetCmp.getWidgetParam().getWidgetId());
			if(targetCmp instanceof AbsFormView) {
				//获取表单面板界面的数据
				PanelContext targetPanelCtx = targetCmp.getPanelContext();
				Form targetForm = ((AbsFormView) targetCmp).getDataFormGui(targetPanelCtx, false, null);
				tracer.info("targetFormModelId="+targetForm.getFormModelId());
				tracer.info("targetForm="+targetForm.getData());
			}
		}else {
			tracer.info("widgetId=null");
		}
		PopToast.info(panelContext.getChannel(),path +"="+targetCmp);
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	
}