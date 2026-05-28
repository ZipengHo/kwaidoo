package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cmn.anotation.ClassDeclare;
import fe.cmn.data.PairDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.BaseDataViewParam;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单视图组件初始化动作干预样例"
,what="演示通过表单视图组件封装的界面初始化接口干预表单视图界面的构建，在表单视图初始化时，可以干预表单上按钮、属性的显隐控制"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-21"
,updateTime = "2025-02-21")
public interface IStudyFormViewInitOp <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		if(component instanceof AbsFormView) {
			AbsFormView formView = (AbsFormView) component;
			BaseDataViewParam viewParam = (BaseDataViewParam) formView.getWidgetParam();
			//1.控制表单属性必填项
			Map<String,Boolean> requireOverride = new LinkedHashMap<>();
			requireOverride.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), true);
			viewParam.mergeRequireOverride(requireOverride);
			//2.控制表单属性可见项
			Map<String,Boolean> visibleOverride = new LinkedHashMap<>();
			visibleOverride.put(IFormMgr.get().getFieldCode(StudyFormMockConst.LongField), false);
			viewParam.mergeVisibleOverride(visibleOverride);
			//3.控制表单属性可写项
			Map<String,Boolean> writableOverride = new LinkedHashMap<>();
			writableOverride.put(IFormMgr.get().getFieldCode(StudyFormMockConst.DoubleField), false);
			writableOverride.put(IFormMgr.get().getFieldCode("结束时间"), false);
			viewParam.mergeWritableOverride(writableOverride);
			//4.控制按钮显示隐藏，是否可操作
			Map<String,Boolean> buttonVisibleOverride = new LinkedHashMap<>();
			buttonVisibleOverride.put("按钮1", false);
			viewParam.mergeButtonVisibleOverride(buttonVisibleOverride);
			Map<String,Boolean> buttonWritableOverride = new LinkedHashMap<>();
			buttonWritableOverride.put("按钮1", false);
			viewParam.mergeButtonWritableOverride(buttonWritableOverride);
			//5.对于界面组件中有定义标签组的组件，可通过以下方法批量控制显示隐藏
//			viewParam.megerGroupWritableOverride(groupWritableOverride);
//			viewParam.megerGroupRequireOverride(groupRequireOverride);
//			viewParam.megerGroupVisibleOverride(groupVisibleOverride);
			//6.在初始化界面缓存变量
			String cacheKey = "cacheItems";
			List<PairDto> cacheItems = new ArrayList<>();
			cacheItems.add(new PairDto<>("选项1","选项1"));
			formView.initPanelCacheValue(panelContext, viewParam.getPanelGlobalKey(), cacheKey, cacheItems);
			formView.getPanelCacheValue(panelContext, cacheKey);
			formView.setPanelCacheValue(panelContext, cacheKey, cacheItems);
			//7.注意：修改完初始化参数配置后，要将参数重新设置回组件上
			formView.setWidgetParam(viewParam);
			//8.云调试需要将组件重新设置回rtx中，否则不然真正更新
			input.getRtx().setParam(BaseFeActionParameter.FeActionParameter_CurrentComponent, formView);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}