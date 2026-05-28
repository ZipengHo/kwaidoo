package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.List;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.fe.gpf.dc.basic.CommandCallbackIntf;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import fe.cmn.app.ability.PopToast;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.QuitPopup;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WidgetDto;
import fe.util.component.AbsComponent;
import fe.util.component.dto.FeDeliverData;
import fe.util.component.extlistener.CommandCallbackListener;
import fe.util.i18n.FeI18n;
import gpf.adur.action.Action;
import gpf.adur.data.Form;
import gpf.dc.basic.dto.view.PDCFormViewDto;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.BaseFormEditView;
import gpf.dc.basic.fe.util.GpfViewActionUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "界面表单弹窗并在确认后回调代码样例"
,what="界面表单弹窗并在确认后回调代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-14"
,updateTime = "2025-02-14")
public interface IStudyPopupFormViewActionAndCallback <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>,CommandCallbackIntf{

	public final static String CMD_CONFIRM = "CMD_CONFIRM";
	public final static String CMD_CANCEL = "CMD_CANCEL";
	
	@Override
	default Object execute(T input) throws Exception {
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
		//设置表单回调监听器，用来监听弹窗后表单上的按钮点击操作
		List<CommandCallbackListener> callbackLsnrs = new ArrayList<>();
		callbackLsnrs.add(newPopupPanelCallbackListener(IStudyPopupFormViewActionAndCallback.class, BaseFormEditView.CMD_CONFIRM, CMD_CONFIRM, null));
		callbackLsnrs.add(newPopupPanelCallbackListener(IStudyPopupFormViewActionAndCallback.class, BaseFormEditView.CMD_CANCEL, CMD_CANCEL, null));
		GpfViewActionUtil.popupFormView(listener, panelContext, component, title, form, isAdd, isWritable, viewAction, callbackLsnrs);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	
	@Override
	default Object onListener(ListenerDto listener, PanelContext panelContext, WidgetDto source) throws Exception {
		if(listener.isServiceCommand(CMD_CONFIRM)) {
			FeDeliverData<Form> feData = (FeDeliverData<Form>) listener.getBinaryData();
			Form data = feData.getData();
			//当前组件实例为调用弹窗动作的组件面板
			AbsComponent component = (AbsComponent) getCurrentPanel(panelContext);
			try(IDao dao = IDaoService.get().newDao()){
				if(!IFormMgr.get().isFormExist(dao, data.getFormModelId(), data.getUuid())) {
					IFormMgr.get().createForm(dao, data);
					PopToast.success(panelContext.getChannel(), component.getI18nString(panelContext,FeI18n.CREATE_SUCCESS));
				}else {
					IFormMgr.get().updateForm(dao, data);
					PopToast.success(panelContext.getChannel(), component.getI18nString(panelContext,FeI18n.UPDATE_SUCCESS));
				}
				dao.commit();
				QuitPopup.quit(panelContext);
			}
		}else if(listener.isServiceCommand(CMD_CANCEL)) {
			QuitPopup.quit(panelContext);
		}
		return null;
	}
}