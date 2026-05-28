package gpf.study.fe.component;

import java.util.List;

import cell.fe.gpf.dc.basic.IGpfDCBasicFeService;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import fe.cmn.data.KeyboardDto;
import fe.cmn.event.EventDto;
import fe.cmn.event.EventInterface;
import fe.cmn.panel.BoxDto;
import fe.cmn.panel.ContainerDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.SinglePanelDto;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.ListenerInterface;
import fe.cmn.widget.WidgetDto;
import fe.util.component.AbsComponent;
import fe.util.intf.ServiceIntf;
import gpf.dc.basic.dto.view.FeEventSubscribeDto;
import gpf.dc.basic.dto.view.TimerConfigDto;
import gpf.dc.basic.fe.component.fieldextend.editor.WidgetLayoutUtil;
import gpf.dc.basic.fe.component.view.ViewActionIntf;
import gpf.dc.basic.fe.enums.ListenerApplyLocation;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ButtonDefine;
import gpf.dc.basic.param.view.dto.ListenerDefine;
import gpf.dc.concrete.RefActionConfig;
import gpf.study.fe.component.param.StudyCustomViewParam;
import web.dto.Pair;

@ClassDeclare(label = "自定义视图组件代码样例"
,what="演示如何实现一个自定义视图组件代码，GPF所有的自定义视图模型都是派生于视图根模型下，<br>"
		+ "带有视图都包含的属性定义,如：监听事件、动作定义、动作权限、定时器、事件订阅定义、页面布局，<br>"
		+ "通过实现ViewActionIntf接口，可拥有视图带有的基本操作接口，如：<br>"
		+ "页面初始化前动作：doBeforeInit <br>"
		+ "页面初始化后动作：doAfterInited <br>"
		+ "初始化定时器：initTimer <br>"
		+ "初始化事件订阅：subscribEvent <br>"
		+ "监听动作响应执行：findListenerDefineAndRun <br>"
		+ "实现自定义组件代码的步骤主要分两步： <br>"
		+ "1. 实现getWidget方法，构建返回前端展示的WidgetDto <br>"
		+ "2.实现onListener和onEvent方法，在界面触发监听时调用相应的动作定义执行"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public class StudyCustomView extends AbsComponent<StudyCustomViewParam> implements ViewActionIntf<StudyCustomViewParam>,ListenerInterface,EventInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1239132062951001513L;
	/**
	 * 初始化面板的信道缓存，所有需要在面板上使用的缓存，都需要预先初始化，分配缓存key
	 * 使用缓存时：
	 * getPanelCacheValue(panelContext, cacheKey);获取缓存
	 * setPanelCacheValue(panelContext, cacheKey, value);更新缓存
	 * @param panelContext
	 * @param panelGlobalKey	面板全局标识
	 * @throws Exception
	 */
	@Override
	public void initPanelCache(PanelContext panelContext,String panelGlobalKey) throws Exception {
		ViewActionIntf.super.initPanelCache(panelContext, panelGlobalKey);
		
	}
	/**
	 * 1.界面初始化调用入口，通过getWidget获取要返回前端展示的组件Dto对象
	 */
	@Override
	public WidgetDto getWidget(PanelContext panelContext) throws Exception {
		return ViewActionIntf.super.getWidget(panelContext);
	}
	@Override
	public WidgetDto doGetWidget(PanelContext panelContext) throws Exception {
		//1.3 根据组件参数构建界面Dto
		BoxDto mainBox = new BoxDto().setVertical(false).setExpandInBox(true).setScrollable(true);
		BoxDto fieldBox = wrapEditor("文本", getDefaultFieldLabelWidth(), newTextEditor("text", ""));
		//界面上不可修改拆分的组件，可通过布局设计容器包裹，在布局时才不会弄乱，且属于可布局的元素
		ContainerDto fieldContainer = WidgetLayoutUtil.wrapDesignContainer("文本", fieldBox);
		mainBox.addChild(fieldContainer);
		
		//1.4 构建最终的面板，设置面板全局标识和widgetId
		SinglePanelDto panel = SinglePanelDto.wrap(mainBox);
		panel.setPanelGlobalKey(widgetParam.getPanelGlobalKey()).setWidgetId(widgetParam.getWidgetId());
		//1.5 应用布局，界面需要支持自定义布局时，加入此段代码
		WidgetDto layout = widgetParam.getLayout();
		if(layout != null) {
			//替换布局上的容器元素，并应用到最终面板上
			panel = (SinglePanelDto) WidgetLayoutUtil.setRealWidget2Layout(panel, layout,getI18n(panelContext),widgetParam.getModelId());
		}
		//组件参数要设置在面板的二进制数据上，前端接口请求到后端服务时，在接口代理类ServerIntf通过组件参数的invokeClass反射调用到当前类的相应接口，如onListener
		panel.setBinaryData(widgetParam);
		return panel;
	}

	@Override
	public Class<? extends ServiceIntf> getService() {
		// 填写项目上的前端接口服务代理类
		return IGpfDCBasicFeService.class;
	}

	@Override
	public IDCRuntimeContext getOrPrepareRtx(PanelContext panelContext, String panelGlobalKey) throws Exception {
		IDCRuntimeContext rtx = IPDFRuntimeMgr.get().newRuntimeContext();
		//将当前界面信息设置在运行上下文中
		BaseFeActionParameter.prepareFeActionParameter(rtx, panelContext, this);
		return rtx;
	}

	@Override
	public boolean isLayoutMode() {
		//是否布局模式，布局模式下不执行初始化动作
		return widgetParam.isLayoutMode();
	}

	@Override
	public ButtonDefine getButtonDefineByCommand(String command) throws Exception {
		// 根据command获取组件参数上的按钮定义
		return null;
	}

	@Override
	public List<ListenerDefine> getViewInitListenerDefines() throws Exception {
		//获取组件上定义的界面初始化监听事件
		//监听事件的数据一般来源于视图模型的监听事件表格数据
		//界面初始化动作会在getWidget方法中的doBeforeInit()调用执行
		return widgetParam.getViewInitListenerDefines(ListenerApplyLocation.Panel);
	}

	@Override
	public List<ListenerDefine> getViewInitedListenerDefines() throws Exception {
		//获取组件上定义的界面初始化后的监听事件
		//监听事件的数据一般来源于视图模型的监听事件表格数据
		//界面初始化动作会在getWidget方法中的doAfterInited()调用执行
		return widgetParam.getViewInitedListenerDefines(ListenerApplyLocation.Panel);
	}

	@Override
	public ListenerDefine getListenerDefineByKeyBoard(KeyboardDto keyboard) throws Exception {
		// 获取键盘事件的监听事件
		return widgetParam.getListenerDefineByKeyboard(keyboard);
	}

	@Override
	public ListenerDefine getListenerDefineByCommand(String command) throws Exception {
		// 根据command获取相应的监听事件
		return widgetParam.getListenerDefineByCommand(command);
	}

	@Override
	public List<RefActionConfig> getActionDefines() throws Exception {
		// 获取组件参数上的动作定义
		return widgetParam.getActionDefines();
	}

	@Override
	public List<FeEventSubscribeDto> getEventSubscribes() throws Exception {
		// 获取组件参数上的事件订阅定义
		return widgetParam.getEventSubscribes();
	}
	
	@Override
	public List<TimerConfigDto> getTimerConfigs() {
		// 获取组件参数上的定时器设置
		return widgetParam.getTimerConfigs();
	}
	
	
	@Override
	public Object onListener(ListenerDto listener, PanelContext panelContext, WidgetDto source)
			throws Exception {
		//查找监听器对应的监听事件并执行
		Pair<Boolean, Object> resultPair = findListenerDefineAndRun(panelContext, listener);
		if(resultPair.left) {
			//找到后返回执行结果
			return resultPair;
		}
		//自定义的监听处理逻辑
		return null;
	}
	
	@Override
	public void onEvent(EventDto eventDto, PanelContext panelContext, WidgetDto source) throws Exception {
		// 事件订阅监听逻辑，根据事件订阅定义找到匹配的动作定义并执行
		ViewActionIntf.super.onEvent(eventDto, panelContext, source);
	}

}
