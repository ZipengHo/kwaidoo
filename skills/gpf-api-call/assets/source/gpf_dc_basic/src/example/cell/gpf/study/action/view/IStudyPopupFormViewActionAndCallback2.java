package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.List;

import com.leavay.dfc.gui.LvUtil;

import cell.CellIntf;
import cell.fe.gpf.dc.basic.IGpfDCBasicFeService;
import cell.gpf.adur.action.IActionMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.QuitPopup;
import fe.cmn.widget.ButtonDto;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WidgetDto;
import fe.util.component.AbsComponent;
import fe.util.exception.VerifyException;
import fe.util.style.FeStyleConst;
import gpf.adur.action.Action;
import gpf.adur.data.Form;
import gpf.dc.basic.dto.view.PDCFormViewDto;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.PopupCallbackParam;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.fe.component.view.BaseFormEditView;
import gpf.dc.basic.fe.intf.PopupCallback;
import gpf.dc.basic.fe.util.GpfViewActionUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "界面表单弹窗并在确认后回调代码样例2"
,what="界面表单弹窗并在确认后回调代码样例, 回调走的是阻塞等待弹窗关闭后的调用逻辑"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-14"
,updateTime = "2025-02-14")
public interface IStudyPopupFormViewActionAndCallback2 <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		String title = "弹窗样例";
		//PDC表单视图模型ID
		String actionModelId = PDCFormViewDto.FormModelId;
		//PDC表单视图编号
		String actionCode = "基础包_服务参数配置";
		Action viewAction = IActionMgr.get().queryActionByCode(input.getRtx().getDao(), actionModelId, actionCode);
		//获取弹窗表单的数据
		Form form = new Form("cmn.md.ServerConfig");
		//是否新增表单，新增表单会执行初始值设置
		boolean isAdd = true;
		//表单是否可修改
		boolean isWritable = true;
		//设置弹窗的底部栏按钮，可选，设置底部栏将覆盖视图上的底部栏
		List<ButtonDto> buttons = new ArrayList<>();
		ButtonDto button1 = component.buildButton("确定", null, "确定", null);;
		button1.setConfirmStyle();
		ButtonDto button2 = component.buildButton("重试", null, "重试", null);
		button2.setConfirmStyle();
		ButtonDto button3 = component.buildButton("取消", null, "取消", null);
		button3.setCancelStyle();
		buttons.add(button1);
		buttons.add(button2);
		buttons.add(button3);
		String bottomBarStyle = FeStyleConst.common_form_bottom_bar_center;
		
		//通过设置表单弹窗的底部按钮栏和回调函数,在当前方法获取提交后的表单并继续执行
		StudyPopupCallback callback = new StudyPopupCallback(component, panelContext);
		PopupCallbackParam callbackParam = new PopupCallbackParam();
		//可选，设置底部栏将覆盖视图上的底部栏
//		callbackParam.setButtons(buttons).setBottomBarStyle(bottomBarStyle);
		callbackParam.setCallback(callback).setServiceProxy(IGpfDCBasicFeService.class);
		//这里要重新拿到回调，才能拿到按钮监听返回的数据
		callback = (StudyPopupCallback) GpfViewActionUtil.popupFormViewAndCallback(listener, panelContext, component, title, form, isAdd, isWritable, viewAction, callbackParam);
		Form resultForm = callback.getResult();
		if(resultForm != null)
			LvUtil.trace("确认回调后获取弹窗界面数据：" + resultForm);
		else {
			//直接点击了界面外部或右上角的关闭按钮退出弹窗
			LvUtil.trace("取消、关闭或界面外部退出弹窗：" + form);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	/**
	 * 演示实现弹窗回调处理，并将处理结果写到result上
	 */
	public static class StudyPopupCallback extends PopupCallback<Form>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1172518088044790419L;

		public StudyPopupCallback(AbsComponent srcCmp, PanelContext srcPanelContext) {
			super(srcCmp, srcPanelContext);
		}

		@Override
		public Object onListener(ListenerDto listener, PanelContext panelContext, WidgetDto source)
				throws Exception {
			if(listener.isServiceCommand("确定")) {
				AbsComponent popupCmp = getPopupComponent();
				if(popupCmp instanceof AbsFormView) {
					Form targetForm = ((AbsFormView) popupCmp).getDataFormGui(panelContext, false, null);
					//将处理结果写到result
					setResult(targetForm);
					//退出弹窗
					QuitPopup.quit(panelContext);
				}
			}else if(listener.isServiceCommand("重试")) {
				AbsComponent popupCmp = getPopupComponent();
				if(popupCmp instanceof AbsFormView) {
					Form targetForm = ((AbsFormView) popupCmp).getDataFormGui(panelContext, false, null);
					//校验提示
					throw new VerifyException("输入值非法，请重试");
				}
			}else if(listener.isServiceCommand("取消")) {
				//退出弹窗
				QuitPopup.quit(panelContext);
			}else if(listener.isServiceCommand(BaseFormEditView.CMD_CONFIRM)) {
				//使用默认底部栏按钮时触发的确认指令
				//退出弹窗
				QuitPopup.quit(panelContext);
			}else if(listener.isServiceCommand(BaseFormEditView.CMD_CANCEL)) {
				//使用默认底部栏按钮时触发的取消指令
				//退出弹窗
				QuitPopup.quit(panelContext);
			}
			return null;
		}
		
	}
	
}