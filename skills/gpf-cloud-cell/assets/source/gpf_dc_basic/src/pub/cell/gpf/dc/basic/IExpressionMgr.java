package cell.gpf.dc.basic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.utils.Env;

import bap.cells.Cells;
import cell.CellPreloadIntf;
import cell.ServiceCellIntf;

public interface IExpressionMgr extends ServiceCellIntf,CellPreloadIntf{

	public static IExpressionMgr get() {
		return Cells.get(IExpressionMgr.class);
	}
	
	public Map<String, Env> getNameSpaceMap() throws Exception;
	
	public Map<String, Map<String, Throwable>> getRegistErrorMap() ;
	/**
	 * 解析表单式中的变量
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public List<String> parseVariableNames(String expression)throws Exception;
	/**
	 * 执行单条规则
	 * @param envMap
	 * @param expression
	 * @return
	 * @throws Exception
	 */
    public Object execute(Map<String,Object> envMap, String expression) throws Exception;
    
    
    /**
	 * 指定规则的命名空间集，执行单条规则
	 * @param envMap
	 * @param expression
	 * @return
	 * @throws Exception
	 */
    public Object execute(Set<String> nameSapces,Map<String,Object> envMap, String expression) throws Exception;
    
    //----------------------------------------------------------内部支撑函数----------------------------------------------------------
   
    public void registerFun(String formModelId)throws Exception;
    /**
     * 注册自定义函数
     * @param formModelId
     * @throws Exception
     */
    public void registerFun(List<AviatorFunction> functions) throws Exception;
    /**
     * 注册自定义函数
     * @param formModelId
     * @throws Exception
     */
    public void registerFun(String nameSpace,List<AviatorFunction> functions) throws Exception;
    /**
     * 记录注册函数出错信息
     * @param nameSpace
     * @param funcName
     * @param exception
     */
    public void addRegistFunError(String nameSpace,String funcName,Throwable exception) ;
}
