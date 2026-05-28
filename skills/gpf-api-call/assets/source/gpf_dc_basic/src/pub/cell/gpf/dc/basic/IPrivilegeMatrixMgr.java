package cell.gpf.dc.basic;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Workbook;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.dto.Progress;
import gpf.adur.data.Form;
import gpf.dc.basic.dto.privilege.ActionPrivilegeSolution;
import gpf.dc.basic.dto.privilege.DataPrivilegeSolution;
import gpf.dc.basic.dto.privilege.IdentifyMatchParam;
import gpf.dc.basic.dto.privilege.JoinViewInfo;
import gpf.dc.basic.dto.privilege.PrivilegeMatrix;
import gpf.dc.basic.dto.privilege.PrivilegeMatrixPackage;
import gpf.dc.basic.dto.privilege.ResultSetQueryParam;
import gpf.dc.intf.FormOpObserver;
import gpf.dto.model.data.FormPrivilegeDto;
import web.dto.Pair;

public interface IPrivilegeMatrixMgr extends ServiceCellIntf{

	public static IPrivilegeMatrixMgr get(){
		return Cells.get(IPrivilegeMatrixMgr.class);
	}
	/**
	 * 查询权限矩阵
	 * @param dao
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public PrivilegeMatrix queryPrivilegeMatrixByCode(IDao dao,String code)throws Exception;
	
	public PrivilegeMatrix queryPrivilegeMatrixCache(IDao dao,String code)throws Exception;
	
	public Form convertPrivilegeMatrix2Form(PrivilegeMatrix privilegeMatrix)throws Exception;
	
	public DataPrivilegeSolution queryDataPrivilegeSolution(IDao dao,String code)throws Exception;
	
	public DataPrivilegeSolution queryDataPrivilegeSolutionCache(IDao dao,String code)throws Exception;
	
	public Form convertDataPrivilegeSolution2Form(DataPrivilegeSolution dataSolution)throws Exception;
	
	public ActionPrivilegeSolution queryActionPrivilegeSolution(IDao dao,String code)throws Exception;
	
	public ActionPrivilegeSolution queryActionPrivilegeSolutionCache(IDao dao,String code)throws Exception;
	
	public Form convertActionPrivilegeSolution2Form(ActionPrivilegeSolution dataSolution)throws Exception;
	
	public IdentifyMatchParam calculateMatchUserRule(String rule, String ruleParams,Set<String> namespaces, Map<String, Object> env)throws Exception;
	/**
	 * 根据身份匹配规则构建追加的关联查询视图和身份匹配表达式
	 * @param identifyMap
	 * @param appendAliasCnt
	 * @param useFieldName
	 * @param regexMap
	 * @param modelAliasMap
	 * @param allJoinViews
	 * @return
	 * @throws Exception
	 */
	public ResultSetQueryParam _doBuildApppendViewsByIdentifyMatchQueryParam(Map<String, IdentifyMatchParam> identifyMap,AtomicInteger appendAliasCnt,boolean useFieldName,Map<String,String> regexMap,Map<String,String> modelAliasMap,Map<String,JoinViewInfo> allJoinViews)throws Exception;
	
	public String buildMatchUserQuerySql(IDCRuntimeContext rtx, String orgModelId,String userModelId,Set<String> namespaces, String privilegeMatrixCode,
			String nodeName,Form form)throws Exception;
	
	public String quoteAsVariable(String name);
	/**
	 * 计算表单权限
	 * @param env	规则表达式运行环境
	 * @param dataSolution	数据权限方案
	 * @param dataCategory	数据权限分类
	 * @param actionSolution	动作权限方案
	 * @param actionCategoy	动作权限分类
	 * @param useDefaultCategory	是否使用默认分类
	 * @return
	 * @throws Exception
	 */
	public FormPrivilegeDto caculateFormPrivilege(Set<String> namespaces,Map<String,Object> env,DataPrivilegeSolution dataSolution, String dataCategory,
			ActionPrivilegeSolution actionSolution, String actionCategoy,boolean useDefaultCategory)throws Exception;
	
	public Map<String,Map<String,IdentifyMatchParam>> buildAllMatchUserExpressions(PrivilegeMatrix matrix,Set<String> nodeNames,String user,Set<String> namespaces,Map<String,Object> env)throws Exception;

	public Pair<String, byte[]> exportPrivilegeMatrixToExcel(String privilegeMatrixCode)throws Exception;
	
	public void writePrivilegeMatrixPackage(Progress prog,String destFilePath,Workbook wb,PrivilegeMatrixPackage privilegeMatrixPack)throws Exception;
	
	public PrivilegeMatrixPackage queryPrivilegeMatrixPackage(IDao dao,String uuid)throws Exception;
	
	public PrivilegeMatrixPackage queryPrivilegeMatrixPackageByCode(IDao dao,String code)throws Exception;
	/**
	 * 导入权限矩阵配置
	 * @param dao
	 * @param privilegeMatrixCode	导入后要构建的权限矩阵配置编号
	 * @param excel excel文件
	 * @return
	 * @throws Exception
	 */
	public PrivilegeMatrixPackage importPrivilegeMatrixFormExcel(IDao dao,String privilegeMatrixCode,byte[] excel,FormOpObserver observer)throws Exception;
	
	public PrivilegeMatrixPackage readPrivilegeMatrixPackageFormExcel(IDao dao,String privilegeMatrixCode,Workbook workbook)throws Exception;

	public PrivilegeMatrixPackage savePrivilegeMatrixPackage(IDao dao,PrivilegeMatrixPackage pack,FormOpObserver observer)throws Exception;
	/**
	 * 构建未授权的表单权限，整合所有数据权限方案的数据项和动作权限方案的操作项
	 * @return
	 * @throws Exception 
	 */
	public FormPrivilegeDto buildUnauthorizedFormPrivilege(PrivilegeMatrix matrix) throws Exception;
	
}
