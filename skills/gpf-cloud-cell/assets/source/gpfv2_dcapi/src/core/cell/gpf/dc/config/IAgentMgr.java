package cell.gpf.dc.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;

import com.leavay.ms.warehouse.WhPackage;

import bap.cells.Cells;
import cell.CellPreloadIntf;
import cell.ServiceCellIntf;
import gpf.adur.data.ResultSet;
import gpf.dc.config.Agent;
import gpf.dc.config.AgentDaoConfig;
import gpf.dc.config.AgentInfo;
import gpf.dc.config.AgentStartItem;

public interface IAgentMgr extends ServiceCellIntf,CellPreloadIntf{

	static IAgentMgr get() {
		return Cells.get(IAgentMgr.class);
	}
	
	
	/**
	 * 是否启用代理服务
	 * @return
	 * @throws Exception
	 */
	public boolean isEnableAgentService()throws Exception;
	/**
	 * 加载所有代理包
	 * @return
	 * @throws Exception
	 */
	public List<WhPackage> loadAllWarehousePackage()throws Exception;
	/**
	 * 加载服务包启动模块配置
	 * @param selectPackage
	 * @return
	 * @throws Exception
	 */
	public List<AgentStartItem> loadAgentStartConfigs(String selectPackage,Map<String, String> mapParam)throws Exception;
	/**
	 * 测试数据库连接
	 * @param agentDaoConfig
	 * @throws Exception
	 */
	public void testConnectDB(AgentDaoConfig agentDaoConfig)throws Exception;
	/**
	 * 构建默认代理包
	 * @throws Exception
	 */
	public void buildDefaultAgentPackage()throws Exception;
	
	public void verifyAgent(Agent agent)throws Exception;
	/**
	 * 创建代理
	 * @param agent
	 * @return
	 * @throws Exception
	 */
	public Agent createAgent(Agent agent)throws Exception;
	/**
	 * 更新代理
	 * @param agent
	 * @return
	 * @throws Exception
	 */
	public Agent updateAgent(Agent agent)throws Exception;
	/**
	 * 查询代理
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Agent queryAgent(String uuid)throws Exception;
	/**
	 * 根据websocket地址查询代理
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public Agent queryAgentByCode(String code)throws Exception;
	/**
	 * 删除代理
	 * @param uuid
	 * @throws Exception
	 */
	public void deleteAgent(String uuid)throws Exception;
	
	public void deleteAgentByCode(String code)throws Exception;
	/**
	 * 查询代理分页
	 * @param cnd
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Agent> queryAgentPage(Cnd cnd,int pageNo,int pageSize)throws Exception;
	/**
	 * 查询代理运行信息
	 * @param codes
	 * @return
	 * @throws Exception
	 */
	public Map<String, AgentInfo> queryAgentInfos(Set<String> codes)throws Exception;
	/**
	 * 查询代理运行信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public AgentInfo queryAgentInfo(String code)throws Exception;
	/**
	 * 重建代理
	 * @param agent
	 * @throws Exception
	 */
	public void rebuildAgent(Agent agent)throws Exception;
	/**
	 * 启动代理
	 * @param code
	 * @throws Exception
	 */
	public void startAgent(String code)throws Exception;
	/**
	 * 关闭代理
	 * @param code
	 * @throws Exception
	 */
	public void shutdownAgent(String code)throws Exception;
	/**
	 * 读取控制台信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String readConsole(String code)throws Exception;

	/**
	 * 生成代理的机器码
	 * @param agent
	 * @return
	 * @throws Exception
	 */
	public String printSystemId(Agent agent)throws Exception;
}
