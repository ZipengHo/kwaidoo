package cell.gpf.study.action.view;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;

import cell.CellIntf;
import cell.fe.IFileService;
import cell.fe.progress.CFeProgressCtrlWithTextArea;
import cell.gpf.dc.backup.IBackupService;
import cell.gpf.dc.config.IPDFMgr;
import cmn.dto.Progress;
import fe.cmn.data.BeFile;
import fe.cmn.data.ByteArrayDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.FeFileUtil;
import fe.util.component.ProgressDialog;
import gpf.dc.basic.expimp.PDCFormDataExcelExpImpExt;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.component.param.BaseTableViewParam;
import gpf.dc.basic.fe.component.view.PDFFormTableView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.config.PDF;
import web.dto.Pair;

public interface IStudyImportPDCFormExcel  <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		List<BeFile> files = FeFileUtil.uploadFile(panelContext, false, null, false, Arrays.asList("zip"));
		if(CmnUtil.isCollectionEmpty(files))
			return null;
		ByteArrayDto byteArray = IFileService.get().getResource(files.get(0).getStorPath());
		Pair<String, byte[]> zipFile = new Pair<String, byte[]>(files.get(0).getName(), byteArray.getData());
		//界面组件对象
		PDFFormTableView component = (PDFFormTableView) input.getCurrentComponent();
		BaseTableViewParam widgetParam = (BaseTableViewParam) component.getWidgetParam();
		String pdfUuid = widgetParam.getModelId();
		PDF pdf = IPDFMgr.get().queryPDF(pdfUuid);
		CFeProgressCtrlWithTextArea prog = ProgressDialog.showProgressDialog(panelContext, "导入数据...",true,true);
		ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
		String userModelId = appSetting.getUserModelId();
		String orgModelId = appSetting.getOrgModelId();
		String user = panelContext.getCurrentUser();
		String actionName = "暂存";
		//关联项导入配置
		Map<String,String> assocDataExpSettingMap = new LinkedHashMap<>();
		assocDataExpSettingMap.put("gpf.md.DocumentType", "编号");
		try {
			Progress prog2 = Progress.wrap(prog);
			PDCFormDataExcelExpImpExt expImpInst = new PDCFormDataExcelExpImpExt();
			expImpInst.setAssocDataExpSettingMap(assocDataExpSettingMap);
			IBackupService.get().submitPDCFormFormExcel(prog2, expImpInst, pdfUuid, user, actionName, zipFile, userModelId, orgModelId);
			prog2.finish();
		}catch (Exception e) {
			prog.finishError(ToolUtilities.getFullExceptionStack(e));
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
