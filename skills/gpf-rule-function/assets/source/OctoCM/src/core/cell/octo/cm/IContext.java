package cell.octo.cm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cell.ResourceCellIntf;
import cell.cdao.IDao;
import cmn.dto.Progress;
import crpc.CRpcContainerIntf;
import gpf.adur.data.Form;
import gpf.adur.user.User;
import octo.cm.dto.ContextModel;
import octo.cm.intf.SystemVarKeyIntf;
/**
 * DC构建时的上下文
 */
public interface IContext extends ResourceCellIntf,CRpcContainerIntf{
	
	public Progress getProgress();
	public IContext setProgress(Progress prog);
	public IDao getDao();
	public IContext setDao(IDao dao);
	public User getOperator();
	public IContext setOperator(User operator);
	/**
	 * 设置上下文模型
	 * @return
	 */
	public ContextModel getCm();
	/**
	 * 设置上下文模型
	 * @return
	 */
	public IContext setCm(ContextModel cm);
	/**
	 * 获取驱动
	 * @return
	 */
	public String getDriverName();
	/**
	 * 设置驱动
	 * @return
	 */
	public IContext setDriverName(String driverName);
	/**
	 * 获取执行方法
	 * @return
	 */
	public String getMethodName();
	/**
	 * 设置执行方法
	 * @param driverName
	 * @return
	 */
	public IContext setMethodName(String driverName);
	/**
	 * 获取方法参数映射
	 * @return
	 */
	public Map<String,String> getParameterMappings();
	/**
	 * 设置方法参数映射
	 * @param parameterMappings
	 * @return
	 */
	public IContext setParameterMappings(Map<String,String> parameterMappings);
	/**
	 * 获取当前上下文处理的实例
	 * @return
	 * @
	 */
	public Form getCmInstance();
	/**
	 * 设置当前上下文处理的实例
	 * @param form
	 * @
	 */
	public IContext setCmInstance(Form cmInstance);
	/**
	 * 获取当前上下文处理的实例列表
	 * @return
	 */
	public List<Form>  getCmInstanceList();
	public IContext setCmInstanceList(List<Form> cmInstanceList);
	
//	public Set<String> getInputKeys();
//	public IContext setInput(String key,Object value);
//	public IContext putAllInput(Map<String,Object> input);
//	public Object getInput(String key);
//	public Map<String,Object> getAllInput();
	/**
	 * 获取上下文里的所有变量key
	 * @return
	 */
	public Set<String> getParamKeys();
	
	public IContext setParam(String key,Object value);
	
	public <T> IContext setParam(SystemVarKeyIntf<T> key,T value);
	
	public IContext putAllParam(Map<String,Object> params);

	public Object getParam(String key);
	
	public <T> T getParam(SystemVarKeyIntf<T> key);
	
	public Map<String,Object> getAllParams();
	/**
	 * 是否有前端执行环境
	 * @return
	 */
	public boolean hasFrontEndEnv();
	/**
	 * 克隆一个上下文对象
	 * @return
	 */
	public IContext cloneContext();
	/**
	 * 克隆一个上下文对象，并切换上下文模型
	 * @param cm
	 * @return
	 */
	public IContext cloneContext(ContextModel cm);

}
 