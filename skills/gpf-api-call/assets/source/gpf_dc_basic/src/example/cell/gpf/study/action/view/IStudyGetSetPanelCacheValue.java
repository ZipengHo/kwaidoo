package cell.gpf.study.action.view;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import fe.util.component.param.WidgetParam;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "面板缓存的使用样例"
,what="面板缓存可用于在面板以及子面板、弹窗面板间公共参数的读写，使用时需要在父面板上初始化缓存，之后可在子面板或者弹窗面板对缓存读写，"
		+ "注意：需要在视图初始化时调用initPanelCacheValue，一旦子面板对同一个缓存key重新初始化，缓存key的读写只在子面板内有效，且不影响父面板的缓存"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-17"
,updateTime = "2025-03-17")
public interface IStudyGetSetPanelCacheValue <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		WidgetParam widgetParam = component.getWidgetParam();
		String panelGlobalKey = widgetParam.getPanelGlobalKey();
		String cacheKey = "CustomCache";
		Object value = null;
		//注意：init只能在视图初始化时调用，value可以为null，一旦子面板对同一个缓存key重新初始化，缓存key的读写只在子面板内有效，且不影响父面板的缓存
		component.initPanelCacheValue(panelContext, panelGlobalKey, cacheKey, value);
		//在父面板或子面板的监听中调用getPanelCacheValue、setPanelCacheValue对缓存读取和设置
		Tracer tracer = newTracer();
		value = component.getPanelCacheValue(panelContext, cacheKey);
		tracer.info(value);
		value = "设置后的缓存值";
		component.setPanelCacheValue(panelContext, cacheKey, value);
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}