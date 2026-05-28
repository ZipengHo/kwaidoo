package cell.gpf.study.action;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.component.view.PDCFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.runtime.PDCForm;
import gpf.dc.runtime.PDFInstance;
import gpf.exception.ActionNodeRuntimeException;
import gpf.exception.IllegalSubmitException;
@ClassDeclare(label = "界面按钮托管流程提交动作"
,what="界面按钮托管流程提交动作"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowSubmitOnGui<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		PDCFormView formView = (PDCFormView) input.getCurrentComponent();
		//从界面获取数据，并做必填校验
		PDCForm form = (PDCForm) input.getForm(true);
		PanelContext panelContext = input.getPanelContext();
		IDCRuntimeContext rtx = input.getRtx();
		String stepUuid = form.getOpLogUuid();
		String nodeKey = form.getNodeKey();
		boolean success = false;
		try{
			rtx.setPdfUuid(form.getPdfUuid());
			rtx.setPdcForm(form);
			String pdfInstUuid = form.getPdfInstUuid();
			if(CmnUtil.isStringEmpty(stepUuid)) {
				//新建流程实例
				String pdfUuid = form.getPdfUuid();
				ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
				if(appSetting != null) {
					rtx.setUserModelId(appSetting.getUserModelId());
					rtx.setOrgModelId(appSetting.getOrgModelId());
				}
				FormOpObserver observer = AppCacheUtil.getFormOpObserver(panelContext);
				System.out.println("observer : " + observer);
				PDFInstance runCfg = IPDFRuntimeMgr.get().createPDFInstance(rtx,pdfUuid,form,observer);
				pdfInstUuid = runCfg.getUuid();
				nodeKey = runCfg.getStartNode();
				form = rtx.getPdcForm();
			}else {
				//提交流程实例
				FormOpObserver observer = AppCacheUtil.getFormOpObserver(panelContext);
				form = IPDFRuntimeMgr.get().submitPDCForm(pdfInstUuid, nodeKey, rtx, form,observer);
			}
			success = true;
		}catch(Exception e) {
			ActionNodeRuntimeException exp = ToolUtilities.getCauseExcpetion(e, ActionNodeRuntimeException.class);
			Exception throwExp = e;
			if(exp != null) {
				Throwable thr = exp.getCause();
				if(thr instanceof Exception)
					throwExp = (Exception)thr;
				else
					throwExp = exp;
			}
			IllegalSubmitException exp2 = ToolUtilities.getCauseExcpetion(e, IllegalSubmitException.class);
			if(exp2 != null) {
				throwExp = exp2;
			}
			throw throwExp;
		}
		if(success) {
			//触发提交回调指令
			formView.fireCommandListener(panelContext, panelContext.getCurrentPanelWidgetId(), PDCFormView.CMD_CONFIRM, form);
			return form;
		}else {
			return null;
		}
		
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
