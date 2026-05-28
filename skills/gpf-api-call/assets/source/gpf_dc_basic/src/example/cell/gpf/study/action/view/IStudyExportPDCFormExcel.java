package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.ToolUtilities;

import cell.CellIntf;
import cell.cmn.io.IFiles;
import cell.fe.IFileService;
import cell.fe.progress.CFeProgressCtrlWithTextArea;
import cell.gpf.dc.backup.IBackupService;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.dto.Progress;
import cn.hutool.core.collection.CollUtil;
import fe.cmn.data.BeFile;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.DownloadFile;
import fe.cmn.widget.ListenerDto;
import fe.util.component.ProgressDialog;
import gpf.adur.data.FormField;
import gpf.dc.basic.expimp.PDCFormDataExcelExpImpExt;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.BaseTableViewParam;
import gpf.dc.basic.fe.component.view.PDFFormTableView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.TableViewSetting;
import gpf.dc.config.PDF;
import gpf.dc.runtime.PDFForm;
import web.dto.Pair;

public interface IStudyExportPDCFormExcel  <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		PDFFormTableView component = (PDFFormTableView) input.getCurrentComponent();
		BaseTableViewParam widgetParam = (BaseTableViewParam) component.getWidgetParam();
		String pdfUuid = widgetParam.getModelId();
		PDF pdf = IPDFMgr.get().queryPDF(pdfUuid);
		String user = panelContext.getCurrentUser();
		Map<String,String> assocDataExpSettingMap = new LinkedHashMap<>();
		assocDataExpSettingMap.put("gpf.md.DocumentType", "编号");
		CFeProgressCtrlWithTextArea prog = ProgressDialog.showProgressDialog(panelContext, "导出数据...",true,true);
		List<String> exportFieldNames = CollUtil.newArrayList("文档编号","文本标题","文档内容","附加说明","上传时间","预设文档类型","用户自定义类型","审批记录");
		List<FormField> fields = IPDFRuntimeMgr.get().queryPDFFormFields(pdfUuid);
		List<FormField> exportFields = new ArrayList<>();
		for(FormField field : fields) {
			if(exportFieldNames.contains(field.getName())) {
				exportFields.add(field);
			}
		}
		Cnd cnd = Cnd.NEW();
		cnd = component.buildCondition(panelContext, input.getRtx().getDao(), cnd);
		SqlExpressionGroup defaultExpr = null;
		TableViewSetting setting = widgetParam.getSetting(TableViewSetting.class);
		//是否启用默认查询条件
		if(setting.isEnableDefaultDataPrivilege())
			defaultExpr = new SqlExpressionGroup().orEquals(PDFForm.Creator, user).orEquals(PDFForm.StepOperator, user).orLike(PDFForm.Assignee, "[-]" + user + "[-]");
		if(defaultExpr != null)
			cnd.and(defaultExpr);
		String sql = cnd.toString();
		try {
			PDCFormDataExcelExpImpExt expImpInst = new PDCFormDataExcelExpImpExt();
			expImpInst.setAssocDataExpSettingMap(assocDataExpSettingMap);
			Pair<String, byte[]> pair = IBackupService.get().exportPDCFormToExcel(Progress.wrap(prog), expImpInst, pdfUuid, exportFields, user, cnd);
			BeFile file = new BeFile();
			file.setBytes(pair.right);
			file.setName(pair.left);
			file.setStorPath("./uploadTemp/"+panelContext.getCurrentUser()+"/"+file.getName());
			file.setLength(pair.right.length);
			try {
				IFiles.get().saveFile(file.getStorPath(), file.getBytes(), false);
				DownloadFile.start(panelContext, IFileService.class, file.getStorPath(), "数据-"+pdf.getLabel()+".zip");
			}finally {
				IFiles.get().deleteFile(file.getStorPath());
			}
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
