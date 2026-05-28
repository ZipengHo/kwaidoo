package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.study.nadur.form.IStudyFormOp;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cmn.anotation.ClassDeclare;
import cmn.exception.MultiException;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cson.core.CsonPojo;
import fe.cmn.app.ability.BatchExecuteCallback;
import fe.cmn.data.PairDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.QueryPanelContextNodeResult;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.SizeDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.adur.data.TableData;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.fe.intf.AppCacheMgrIntf;
import gpf.dc.basic.fe.util.PanelUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
@ClassDeclare(label = "表单视图组件操作接口样例"
,what="演示通过表单视图组件封装的界面交互接口与前端交互"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public interface IStudyFormViewOp<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面动作监听传入对象
		Tracer tracer = TraceUtil.getCurrentTracer();
		ListenerDto listener = input.getListener();
		if(true){
			MultiException exception = new MultiException();
			exception.add(new Exception("错误1"));
			exception.add(new Exception("错误2"));
			exception.ifExceptionThrow();
		}
//		QueryPanelContextNodeResult panelCtxResult = (QueryPanelContextNodeResult) listener.getServiceCallbackResult(AbsFormView.CallbackKey_QueryPanelContextNode);
//		if(panelCtxResult != null) {
//			PanelUtil panelContextUtil = new PanelUtil(panelContext,panelCtxResult);
//			tracer.info(panelContextUtil.toTreeString());
//		}
		tracer.info("","动作：" + input.getRtx().getActionName());
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		if(component instanceof AbsFormView) {
			if(true) {
				//需要更新的属性值map，属性值类型为表单定义的属性类型
				Map<String,Object> fieldValueMap = new LinkedHashMap<>();
				//设置表单属性值的方式具体见IStudyFormOp
				fieldValueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), null);
				AbsFormView formView = (AbsFormView) component;
				Form form = input.getForm();
				formView.setEditorValues(panelContext, form, fieldValueMap,false);
				return null;
			}
			
			tracer.info("", "获取界面表单");
			tracer.logStart();
			Form form = input.getForm();
			tracer.infoCost("", "获取界面表单耗时：" + form);
			AbsFormView formView = (AbsFormView) component;
			//1.控制表单属性必填项
			Map<String,Boolean> mapRequire = new LinkedHashMap<>();
			mapRequire.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), true);
			formView.setFieldRequire(panelContext, mapRequire);
			//2.控制表单属性可见项
			Map<String,Boolean> mapVisible = new LinkedHashMap<>();
			mapVisible.put(IFormMgr.get().getFieldCode(StudyFormMockConst.LongField), false);
			mapVisible.put(IFormMgr.get().getFieldCode("单选关联"), false);
			formView.setFieldVisible(panelContext, mapVisible);
			//3.控制表单属性可写项
			Map<String,Boolean> mapWritable = new LinkedHashMap<>();
			mapWritable.put(IFormMgr.get().getFieldCode(StudyFormMockConst.DoubleField), false);
			mapWritable.put(IFormMgr.get().getFieldCode("单选关联"), false);
			mapWritable.put(IFormMgr.get().getFieldCode("多选关联"), false);
			formView.setFieldWritable(panelContext, mapWritable);
			
			//4.修改界面上的表单属性值
//			Form form = input.getForm();
			//需要更新的属性值map，属性值类型为表单定义的属性类型
			Map<String,Object> fieldValueMap = new LinkedHashMap<>();
			//设置表单属性值的方式具体见IStudyFormOp
			IStudyFormOp.get().setAttrValue(fieldValueMap, false);
			fieldValueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), "");
			fieldValueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.DateField), null);
			List<Form> rows = new ArrayList<>();
			rows.add(new Form("gpf.md.slave.TestNotNull").setAttrValue("AAA", ""+System.currentTimeMillis()));
			TableData tableData = new TableData(rows);
			
			fieldValueMap.put(IFormMgr.get().getFieldCode("嵌套"), tableData);
			formView.setEditorValues(panelContext, form, fieldValueMap);
			
	//		//5.批处理方式控制表单属性可见、可写、必填、更新属性值的前端回调，设置比单独调用减少了与前端通信的开销
			List<CsonPojo> callbacks = new ArrayList<>();
			callbacks.addAll(formView.buildSetFieldRequireCallback(panelContext, mapRequire));
			callbacks.addAll(formView.buildSetFieldVisibleCallback(panelContext, mapVisible));
			callbacks.addAll(formView.buildSetFieldWritableCallback(panelContext, mapWritable));
			callbacks.addAll(formView.buildSetEditorValuesCallback(panelContext, form, fieldValueMap));
			//批处理执行前端回调
			if(!callbacks.isEmpty())
				BatchExecuteCallback.batchExecute(panelContext.getChannel(), callbacks);
			
			String cacheKey = "cacheItems";
			List<PairDto> cacheItems = new ArrayList<>();
			cacheItems.add(new PairDto<>("选项1","选项1"));
			cacheItems.add(new PairDto<>("选项2","选项2"));
			cacheItems.add(new PairDto<>("选项3","选项3"));
			formView.setPanelCacheValue(panelContext, cacheKey, cacheItems);
			
			formView.verifyRequire(panelContext, listener,form, null);
//			formView.fireDeleteCommand(panelContext);
	//		//6.刷新界面，所有界面的修改值都会被重置
	//		component.onRefresh(panelContext);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
