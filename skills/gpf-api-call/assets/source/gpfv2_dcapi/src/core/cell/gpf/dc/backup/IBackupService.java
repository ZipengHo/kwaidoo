package cell.gpf.dc.backup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Cnd;

import com.leavay.client.util.CmnEvent.EThreadPool;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cell.cmn.IJson;
import cell.function.CConsumer;
import cmn.dto.Progress;
import gpf.adur.action.Function;
import gpf.adur.action.FunctionLib;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.category.Category;
import gpf.dc.config.PDF;
import gpf.dc.expimp.ExportSetting;
import gpf.dc.expimp.PDCFormDataExcelExpImp;
import gpf.dc.intf.ExportImportIntf;
import gpf.dc.intf.FormOpObserver;
import gpf.model.observer.ImportModelOpObserver;
import web.dto.Pair;

public interface IBackupService extends ServiceCellIntf{
	
	public static IBackupService get() {
		return Cells.get(IBackupService.class);
	}
	/**
	 * 获取备份数据的Json数据转换工具类，使用说明:
	 * 转文本：
	 * try(IJson json = getIJson()){
	 * 	String text = json.toJson(obj);
	 * }
	 * 转对象：
	 * try(IJson json = getIJson()){
	 * 	Object obj = json.fromJson(text,Object.class);
	 * }
	 * @return
	 */
	public IJson getIJson();
	
	/**
	 * 添加关联视图的导出配置
	 * @param pdfUuid
	 * @return
	 * @throws Exception
	 */
	public ExportSetting buildPDFExportSetting(String pdfUuid)throws Exception;
	/**
	 * 添加引用动作实例的导入配置
	 * @param setting
	 * @return
	 * @throws Exception
	 */
	public ExportSetting buildRefActionExportSetting(ExportSetting setting,Set<String> analyzeModelIds)throws Exception;
	
	public Pair<String, byte[]> exportDataPackage(Progress prog, ExportImportIntf expImpIntf,ExportSetting setting)throws Exception;
	
	public Pair<String, byte[]> exportDataPackage(Progress prog, ExportImportIntf expImpIntf)throws Exception;
	
	public void importDataPackage(Progress prog, ExportImportIntf expImpIntf,Pair<String, byte[]> zipFile,boolean forceUpdateModel)throws Exception;
	
	public Pair<String, byte[]> exportFormToExcel(Progress prog, ExportImportIntf expImpIntf,String formModelId, Cnd cnd)throws Exception;
	
	public Pair<String, byte[]> exportFormModel(Progress prog,ExportImportIntf expImpIntf,List<String> formModelIds) throws Exception; 
	/**
	 * 预导入表单Excel数据,只生成code和子表的uuid
	 * @param prog
	 * @param dao
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> preImportFormFormExcel(Progress prog, IDao dao,ExportImportIntf expImpIntf, String formModelId,
			Pair<String, byte[]> zipFile,FormOpObserver observer) throws Exception;
	/**
	 * 导入表单Excel数据
	 * @param prog
	 * @param dao
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> importFormFormExcel(Progress prog, IDao dao,ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile,FormOpObserver observer)throws Exception;
	
	/**
	 * 导入表单Excel数据
	 * @param prog
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> importFormFormExcel(Progress prog, ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile,FormOpObserver observer)throws Exception;

	public Pair<String, byte[]> exportFormToJson(Progress prog, ExportImportIntf expImpIntf,String formModelId, Cnd cnd)throws Exception;
	
	/**
	 * 预导入表单Excel数据,只生成code和子表的uuid
	 * @param prog
	 * @param dao
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> preImportFormFormJson(Progress prog, IDao dao,ExportImportIntf expImpIntf, String formModelId,
			Pair<String, byte[]> zipFile,FormOpObserver observer) throws Exception;
	/**
	 * 导入表单Excel数据
	 * @param prog
	 * @param dao
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> importFormFormJson(Progress prog, IDao dao,ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile,FormOpObserver observer)throws Exception;
	
	/**
	 * 导入表单Excel数据
	 * @param prog
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @param observer
	 * @return
	 * @throws Exception
	 */
	public List<String> importFormFormJson(Progress prog, ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile,FormOpObserver observer)throws Exception;

		/**
	 * 导入表单数据前的预处理操作，传入观察者干预预处理前后处理逻辑
	 * @param prog
	 * @param dao
	 * @param list
	 * @param observer
	 * @param preImportHandler	表单数据导入预处理逻辑
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Form> preImportForms(Progress prog,IDao dao,ArrayList<Form> list,FormOpObserver observer,CConsumer<ArrayList<Form>> preImportHandler)throws Exception;
	/**
	 * 导入表单数据的处理逻辑，传入观察者干预导入操作前后的处理逻辑
	 * @param prog
	 * @param dao
	 * @param list
	 * @param observer
	 * @param importHandler	表单数据导入处理逻辑
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Form> importForms(Progress prog,IDao dao,ArrayList<Form> list,FormOpObserver observer,CConsumer<ArrayList<Form>> importHandler)throws Exception;
	/**
	 * 导出表单数据的处理逻辑，传入观察者干预导出操作前后的处理逻辑
	 * @param prog
	 * @param dao
	 * @param list
	 * @param observer
	 * @param exportHandler	表单数据导出处理逻辑
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Form> exportForms(Progress prog,IDao dao,ArrayList<Form> list,FormOpObserver observer,CConsumer<ArrayList<Form>> exportHandler)throws Exception;
	
	/**
	 * 导出用户数据
	 * @param prog
	 * @param expImpIntf
	 * @param formModelId
	 * @param cnd
	 * @return
	 * @throws Exception
	 */
	public Pair<String, byte[]> exportUserToExcel(Progress prog, ExportImportIntf expImpIntf,String formModelId, Cnd cnd)throws Exception;
	/**
	 * 导入用户数据
	 * @param prog
	 * @param expImpIntf
	 * @param formModelId
	 * @param zipFile
	 * @throws Exception
	 */
	public List<String> importUserFormExcel(Progress prog, ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile)throws Exception;
	/**
	 * 导出身份定义
	 * @param prog
	 * @param expImpIntf
	 * @param cnd
	 * @return
	 * @throws Exception
	 */
	public Pair<String, byte[]> exportIdentifyToExcel(Progress prog, ExportImportIntf expImpIntf,Cnd cnd)
			throws Exception;
	/**
	 * 导入身份定义
	 * @param prog
	 * @param expImpIntf
	 * @param xlsxFile
	 * @throws Exception
	 */
	public List<String> importIdentifyFormExcel(Progress prog, ExportImportIntf expImpIntf,
			Pair<String, byte[]> xlsxFile) throws Exception ;
	
	/**
	 * 导入动作实例数据，忽略owner属性关系
	 * @param prog
	 * @param xlsxFile
	 * @throws Exception
	 */
	public void importActionDataIgnoreOwner(Progress prog,Pair<String, byte[]> zipFile) throws Exception ;
	
	public Pair<String, byte[]> exportPDCFormToExcel(Progress prog, PDCFormDataExcelExpImp expImpIntf, String pdfUuid,List<FormField> formFields,
			String user, Cnd cnd)throws Exception;
	
	public void submitPDCFormFormExcel(Progress prog, PDCFormDataExcelExpImp expImpIntf, String pdfUuid, String user,String actionName,
			Pair<String, byte[]> zipFile,String userModelId,String orgModelId)throws Exception;
	/**
	 * 批量导入模型或流程
	 * @param prog
	 * @param formModels
	 * @param pdfs
	 * @param observer
	 * @throws Exception
	 */
	public void importFormModels(Progress prog, List<FormModel> formModels,List<PDF> pdfs, ImportModelOpObserver observer,boolean forceUpdate)throws Exception;
	
	public void doImportFormModels(Progress prog, List<FormModel> formModels,List<PDF> pdfs, ImportModelOpObserver observer,boolean forceUpdate) throws Exception ;
	/**
	 * 添加任务到生效队列
	 * @param prog
	 * @param formModels
	 * @param pdfs
	 * @param observer
	 * @param forceUpdate
	 * @return
	 * @throws Exception
	 */
	public String addImportModelTaskToPool(Progress prog, List<FormModel> formModels,List<PDF> pdfs, ImportModelOpObserver observer,boolean forceUpdate)throws Exception;

	/**
	 * 获取生效任务线程池
	 * @return
	 */
	public EThreadPool getImportModelPool();
	/**
	 * 获取生效任务
	 * @param key
	 * @return
	 */
	public ImportModelTask getImportModelTask(String key);
	
	public Pair<String, byte[]> exportPDFInstances(Progress prog,List<PDF> pdfs)throws Exception;
	
	public Pair<String, byte[]> exportUserDatas(Progress prog, String userModelId,Cnd cnd)throws Exception;
	
	public Pair<String, byte[]> exportOrgDatas(Progress prog, String orgModelId,Cnd cnd)throws Exception;
	
	public List<String> importOrgDatas(Progress prog, String orgModelId,Pair<String, byte[]> zipFile,FormOpObserver observer)throws Exception;
	
//	public Pair<String, byte[]> exportOrgExcelDatas(Progress prog, String orgModelId, String userModelId,Cnd cnd)throws Exception;
//	
//	public List<String> importOrgExcelDatas(Progress prog, String orgModelId, String userModelId,FormOpObserver observer,Pair<String, byte[]> zipFile)throws Exception;
	/**
	 * 导出视图配置以及关联动作
	 * @param prog
	 * @param viewModelId
	 * @param cnd
	 * @return
	 * @throws Exception
	 */
	public Pair<String, byte[]> exportViewAndRelateAction(Progress prog, String viewModelId,Cnd cnd)throws Exception;
	
	public Pair<String, byte[]> exportCategorys(Progress prog, List<Category> categorys)throws Exception;
	public Pair<String, byte[]> exportFunctionLibs(Progress prog, List<FunctionLib> functionLibs)throws Exception;
	public Pair<String, byte[]> exportFunctions(Progress prog, List<Function> functions) throws Exception;
	
}
