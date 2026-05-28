package gpf.dc.basic.param.view;

import java.util.List;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.javac.ClassFactory;

import cell.cdao.IDao;
import cell.fe.gpf.dc.basic.IGpfDCBasicFeService;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.anotation.MethodDeclare;
import cmn.util.NullUtil;
import cn.hutool.core.collection.CollUtil;
import fe.cmn.app.Context;
import fe.cmn.event.EventDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WidgetDto;
import fe.util.component.Component;
import fe.util.component.dto.FeCmnEvent;
import fe.util.component.extlistener.CommandCallbackListener;
import fe.util.component.param.WidgetParam;
import gpf.action.parameter.SystemVaribleInfo;
import gpf.adur.action.Action;
import gpf.adur.data.Form;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.component.param.BaseDataViewParam;
import gpf.dc.basic.fe.component.param.ViewBriefInfo;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.fe.enums.TriggerTime;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.basic.param.view.dto.MenuNodeDto;
import gpf.dc.basic.param.view.dto.ViewTabItemDto;
import gpf.dc.fe.annotation.IgnoreParamMapping;

@ClassDeclare(label = "界面动作模型参数基类"
,what = "界面动作模型参数基类，所有界面自定义的动作模型参数都需要继承此类，使用动作模型参数时需在动作模型代码中的getInputParamClass()中声明"
, why = ""
,how = "以界面动作模型代码使用为例：\r\n"
		+ "//界面动作模型代码需要继承CellIntf和BaseFeActionIntf接口或继承BaseFeActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型\r\n" + 
		"public interface IStudyBaseFeActionDefine <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{\r\n" + 
		"	@Override\r\n" + 
		"	default Object execute(T input) throws Exception {\r\n" + 
		"		//这里编写界面动作模型代码\r\n" + 
		"		return null;\r\n" + 
		"	}\r\n" + 
		"	@Override\r\n" + 
		"	default Class<? extends T> getInputParamClass() {\r\n" + 
		"		//填写类上泛型T声明的动作模型参数类\r\n" + 
		"		return (Class<? extends T>) BaseFeActionParameter.class;\r\n" + 
		"	}\r\n" + 
		"}"
, developer = "陈晓斌"
, createTime = "2025-03-14"
,updateTime = "2025-03-14"
, version = "1.0" )
public class BaseFeActionParameter extends BaseActionParameter implements ViewActionParameterIntf<IDCRuntimeContext>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6575080435301709321L;
	@FieldDeclare(label = "附加参数—应用配置",desc = "")
	public final static String FeActionParameter_ApplicationSetting = "$appSetting$";
//	@IgnoreParamMapping
//	String widgetId;
//	IFeRuntimeContext rtx;
//	@Override
//	public IFeRuntimeContext getRtx() {
//		return rtx;
//	}
//
//	@Override
//	public void setRtx(IFeRuntimeContext rtx) {
//		this.rtx = rtx;
//	}
	@IgnoreParamMapping
	private Form form = null;
	@MethodDeclare(label = "获取界面动作运行时的表单",
	        what = "",
	        why = "",
	        how = "",
	        inputs = {},
	        developer = "",
	        createTime = "",
	        updateTime = "",
	        version = "")
	public Form getForm()throws Exception{
		return getForm(false);
	}
	public Form getForm(boolean checkRequire)throws Exception{
		if(form == null) {
			String triggerTime = getTriggerTime();
			if(CmnUtil.isStringEqual(triggerTime, TriggerTime.TriggerBeforeInit.name())) {
				form = getRtx().getPdcForm();
				if(form == null) {
					form = getRtx().getInteractiveForm();
				}
			}else if(CmnUtil.isStringEqual(triggerTime, TriggerTime.TriggerAfterSubmit.name())) {
				form = getRtx().getPdcForm();
				if(form == null) {
					form = getRtx().getInteractiveForm();
				}
			}else {
				Component component = getCurrentComponent();
				ListenerDto listener = getListener();
				PanelContext panelContext = getPanelContext();
				if(panelContext == null) {
					form = getRtx().getPdcForm();
					if(form == null) {
						form = getRtx().getInteractiveForm();
					}
				}else {
					if(component instanceof AbsFormView) {
						form = ((AbsFormView) component).getDataFormGui(listener,panelContext, checkRequire,null);
						return form;
					}else {
						WidgetParam widgetParam = null;
						try {
							widgetParam  = IGpfDCBasicFeService.get().getPanelWidgetParam(panelContext, panelContext.getCurrentPanelWidgetId());//(WidgetParam) QueryBinaryData.queryOne(panelContext);
						}catch (Exception e) {
						}
						if(widgetParam instanceof BaseDataViewParam) {
							String invokeClass = widgetParam.getInvokeClass();
							if(CmnUtil.isStringEmpty(invokeClass))
								return null;
							Class<? extends Component> clazz = (Class<? extends Component>) ClassFactory.getValidClassLoader().loadClass(invokeClass);
							Component inst = clazz.newInstance();
							inst.setWidgetParam(widgetParam);
							if(inst instanceof AbsFormView) {
								form = ((AbsFormView) inst).getDataFormGui(listener,panelContext, checkRequire,null);
								return form;
							}
						}
					}
				}
			}
		}
		return form;
	}
	
	public String getWidgetId() throws Exception {
		return (String) getRtx().getParam(FeActionParameter_WidgetId);
	}
	public static void setWidgetId(IDCRuntimeContext rtx,String widgetId) throws Exception {
		rtx.setParam(FeActionParameter_WidgetId, widgetId);
	}
	
	public static void prepareFeActionParameter(IDCRuntimeContext rtx,Context panelContext,Component currComponent) throws Exception {
		rtx.setOperator(panelContext.getCurrentUser());
		rtx.setParam(FeActionParameter_PanelContext, panelContext);
		rtx.setParam(FeActionParameter_CurrentComponent, currComponent);
		ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
		if(appSetting != null) {
			setAppSetting(rtx, appSetting);
		}
	}
	
	public static void prepareFeActionParameter(IDCRuntimeContext rtx,Context panelContext,ListenerDto listener,Component currComponent) throws Exception {
		rtx.setOperator(panelContext.getCurrentUser());
		rtx.setParam(FeActionParameter_PanelContext, panelContext);
		rtx.setParam(FeActionParameter_Listener, listener);
		rtx.setParam(FeActionParameter_CurrentComponent, currComponent);
		ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
		if(appSetting != null) {
			setAppSetting(rtx, appSetting);
		}
	}
	
	public static void prepareFeActionParameter(IDCRuntimeContext rtx,Context panelContext,FeCmnEvent event,Component currComponent) throws Exception {
		rtx.setOperator(panelContext.getCurrentUser());
		rtx.setParam(FeActionParameter_PanelContext, panelContext);
		rtx.setParam(FeActionParameter_Event, event);
		rtx.setParam(FeActionParameter_CurrentComponent, currComponent);
		ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
		if(appSetting != null) {
			setAppSetting(rtx, appSetting);
		}
	}
	
	public static void setTriggerTime(IDCRuntimeContext rtx,TriggerTime triggerTime) throws Exception {
		rtx.setParam(FeActionParameter_TriggerTime, triggerTime.name());
	}
	/**
	 * 传递回调监听触发器
	 * @param rtx
	 * @param callbackLsnrs
	 * @throws Exception
	 */
	public static void setCommandCallbackListener(IDCRuntimeContext rtx,List<CommandCallbackListener> callbackLsnrs)throws Exception{
		rtx.setParam(FeActionParameter_CommandCallbackListener, callbackLsnrs);
	}
	/**
	 * 设置初始化后的界面组件对象
	 * @param rtx
	 * @param widget
	 * @throws Exception
	 */
	public static void setInitedWidget(IDCRuntimeContext rtx,WidgetDto widget) throws Exception {
		rtx.setParam(FeActionParameter_InitedWidget, widget);
	}
	/**
	 * 获取初始化后的界面组件对象
	 * @return
	 * @throws Exception
	 */
	public WidgetDto getInitedWidget() throws Exception {
		return (WidgetDto) getRtx().getParam(FeActionParameter_InitedWidget);
	}

	@SuppressWarnings("unchecked")
	public List<CommandCallbackListener> getCommandCallbackListeners() throws Exception{
		return (List<CommandCallbackListener>) NullUtil.get((List)getRtx().getParam(FeActionParameter_CommandCallbackListener));
	}
	/**
	 * 获取上下文中的应用配置信息
	 * @return
	 * @throws Exception
	 */
	public ApplicationSetting getAppSetting() throws Exception {
		return (ApplicationSetting) getRtx().getParam(FeActionParameter_ApplicationSetting);
	}
	/**
	 * 设置上下文中的应用配置信息
	 * @param appSetting
	 * @throws Exception
	 */
	public static void setAppSetting(IDCRuntimeContext rtx,ApplicationSetting appSetting)throws Exception{
		rtx.setParam(FeActionParameter_ApplicationSetting, appSetting);
		rtx.setUserModelId(appSetting.getUserModelId());
		rtx.setOrgModelId(appSetting.getOrgModelId());
	}
	/**
	 * 设置构建视图的摘要信息
	 * @param rtx
	 * @param briefInfo
	 * @throws Exception
	 */
	public static void setViewBriefInfo(IDCRuntimeContext rtx,ViewTabItemDto viewInfo) throws Exception {
		ViewBriefInfo briefInfo = new ViewBriefInfo();
		Action viewAction = viewInfo.getViewAction();
		if(viewAction != null)
			briefInfo.setBuildActionModelId(viewAction.getActionModelId()).setBuildActionCode(viewAction.getCode());
		briefInfo.setCategorys(CollUtil.newArrayList(viewInfo.getItemName())).setMenuUuid(viewInfo.getTabId());
		rtx.setParam(FeActionParameter_ViewBriefInfo, briefInfo);
	}
	public static void setViewBriefInfo(IDCRuntimeContext rtx,MenuNodeDto node) throws Exception {
		ViewBriefInfo briefInfo = new ViewBriefInfo();
		Action viewAction = node.getViewAction();
		if(viewAction != null)
			briefInfo.setBuildActionModelId(viewAction.getActionModelId()).setBuildActionCode(viewAction.getCode());
		briefInfo.setCategorys(CollUtil.newArrayList(node.getLabel())).setMenuUuid(node.getKey());
		rtx.setParam(FeActionParameter_ViewBriefInfo, briefInfo);
	}
	@Override
	public List<SystemVaribleInfo> getSystemVariableInfos() {
		List<SystemVaribleInfo> list = super.getSystemVariableInfos();
		list.add(new SystemVaribleInfo().setVarName(FeActionParameter_PanelContext).setLabel("面板上下文").setType(PanelContext.class));
		list.add(new SystemVaribleInfo().setVarName(FeActionParameter_Listener).setLabel("面板监听器").setType(ListenerDto.class));
		list.add(new SystemVaribleInfo().setVarName(FeActionParameter_Event).setLabel("面板事件").setType(EventDto.class));
		list.add(new SystemVaribleInfo().setVarName(FeActionParameter_CurrentComponent).setLabel("当前组件").setType(Component.class));
		return list;
	}
	/**
	 * 构建运行上下文
	 * @param dao
	 * @param panelContext
	 * @param currComponent
	 * @return
	 * @throws Exception
	 */
	public static IDCRuntimeContext prepareRtx(IDao dao,PanelContext panelContext,Component currComponent) throws Exception {
		IDCRuntimeContext rtx = IPDFRuntimeMgr.get().newRuntimeContext();
		rtx.setDao(dao);
		prepareFeActionParameter(rtx, panelContext, currComponent);
		return rtx;
	}
	
}
