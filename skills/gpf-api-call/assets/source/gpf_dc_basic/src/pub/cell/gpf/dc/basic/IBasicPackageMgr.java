package cell.gpf.dc.basic;

import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.dto.Progress;
import gpf.adur.action.ActionModel;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.dc.basic.excel.ModelConvertorIntf;
import gpf.dc.intf.FormOpObserver;
import gpf.dto.model.relation.ModelDataRelation;
import gpf.dto.model.relation.ModelDataViewInfo;
import web.dto.Pair;
/**
 * 基础包服务接口
 * @author chenxb
 *
 */
public interface IBasicPackageMgr extends CellIntf {
	
	public static IBasicPackageMgr get() {
		return Cells.get(IBasicPackageMgr.class);
	}
	
	public ActionModel queryRootViewActionModel() throws Exception ;
	
	public List<ActionModel> queryAllViewActionModel()throws Exception;
	
	public Map<String,List<Pair<FormField, FormField>>> queryAllRelateViewModelRelation(Progress prog)throws Exception;
	
	public void analyzeRelateViewModelRelation(Map<String,List<Pair<FormField, FormField>>> map,FormModel formModel)throws Exception;
	
	public boolean isApplicationModel(String modelId)throws Exception;

	public Map<String,List<String>> queryRelateViewCodes(Progress prog,List<String> viewModelClasses,List<String> relateModelIds) throws Exception;
	
	public Map<String,ModelDataViewInfo> queryEffectModelDataInfoOfView(Progress prog,ModelDataRelation dataRelation,Map<String,List<String>> relateViewCodes,Map<String,List<Pair<FormField,FormField>>> relateViewModels)throws Exception;
	
	public Pair<String,byte[]> exportPDFExcel(Progress prog, String pdfUuid,ModelConvertorIntf convertor)throws Exception;
	
	public void importPDFExcel(Progress prog,Pair<String,byte[]> file,ModelConvertorIntf convertor)throws Exception;
	
	public Pair<String, byte[]> exportOrgExcelDatas(Progress prog, String orgModelId, String userModelId,Cnd cnd)throws Exception;
	
	public List<String> importOrgExcelDatas(Progress prog, String orgModelId, String userModelId,FormOpObserver observer,Pair<String, byte[]> zipFile)throws Exception;
	
}
