package cell.gpf.study.agent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cmn.util.TraceUtil;
import cmn.util.Tracer;
import com.leavay.common.util.cjson.test.TestCJson;
import gpf.dc.enums.AgentStatus;
import org.nutz.dao.Cnd;

import com.leavay.dfc.gui.LvUtil;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.dc.config.IAgentMgr;
import cmn.anotation.ClassDeclare;
import gpf.adur.data.ResultSet;
import gpf.dc.config.Agent;
import gpf.dc.config.AgentDaoConfig;
import gpf.dc.config.AgentInfo;
import gpf.dc.config.AgentStartItem;
import gpf.dc.enums.AgentDeployMode;
@ClassDeclare(label = "代理操作代码样例"
,what="代理操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyAgentOp extends CellIntf{

	static IStudyAgentOp get() {
		return Cells.get(IStudyAgentOp.class);
	}
	
	default void testCrud() throws Exception {
		String code = "ws://192.168.1.200:2020";
		//部署包路径，需要在GPF根目录下的warehouse目录由对应的部署包文件和xml文件
		String pkgPath = "GpfAgent.zip";
		Agent agent = IAgentMgr.get().queryAgentByCode(code);
		if(agent == null) {
			agent = new Agent();
			agent.setCode(code);
			agent.setName("agent");
			//禁用部署
//			agent.setDeployType(AgentDeployMode.Disable.getValue());
			//本地部署
			agent.setDeployType(AgentDeployMode.Local.getValue());
			agent.setPkgPath(pkgPath);
			//设置JVM的最小内存和最大内存
			agent.setMinMem(200).setMaxMem(4096);
			//远程部署
//			agent.setDeployType(AgentDeployMode.Ssh.getValue());
//			agent.setPkgPath(pkgPath);
//			agent.setMinMem(200).setMaxMem(4096);
//			//部署目录
//			String sshPath = "/root/agent";
//			//JDK目录
//			String sshJdkPath = "/root/jdk";
//			agent.setSshPort(22).setSshUser("root").setSshPwd(new Password().setValue("123456")).setSshPath(sshPath).setSshJdkPath(sshJdkPath);
			//DAO配置
			AgentDaoConfig daoConfig = new AgentDaoConfig();
			//跟随标准总控
			daoConfig.setFollowMasterUri(AgentDaoConfig.FOLLOW_ROOT);
			daoConfig.setMinConn(1).setMaxConn(400);
			agent.setConfigTable(daoConfig);
//			//自主次级中心
//			daoConfig.setFollowMasterUri(AgentDaoConfig.SUB_MASTER);
//			daoConfig.setMinConn(1).setMaxConn(400);
//			String dbDriver = "org.postgresql.Driver";
//			String dbUrl = "jdbc:postgresql://127.0.0.1:5740/database";
//			String dbUser = "postgres";
//			String dbPwd = "123456";
//			daoConfig.setDbDriver(dbDriver).setDbUrl(dbUrl).setDbUserName(dbUser).setDbPwd(new Password().setValue(dbPwd));
//			agent.setConfigTable(daoConfig);
//			//跟随次级群随从
//			String subMasterAgentCode = "ws://192.168.1.202:2021";
//			daoConfig.setFollowMasterUri(subMasterAgentCode);
//			daoConfig.setMinConn(1).setMaxConn(400);
//			agent.setConfigTable(daoConfig);
			//配置启动参数
			Map<String,String> mapParam = new LinkedHashMap<>();
			mapParam.put("gpf.start.pluginCells", "cell.fe.gpf.dc.basic.IApplicationService");
			List<AgentStartItem> startItems = IAgentMgr.get().loadAgentStartConfigs(pkgPath, mapParam);
			agent.setStarterConf(startItems);
			//新增代理，不直接部署代理
			agent = IAgentMgr.get().createAgent(agent);
			
		}else {
			//修改代理，不直接部署代理
			agent = IAgentMgr.get().updateAgent(agent);
		}
		//删除代理
		IAgentMgr.get().deleteAgentByCode(code);
		//查询代理运行情况
		Cnd cnd = Cnd.NEW();
		ResultSet<Agent> rs = IAgentMgr.get().queryAgentPage(cnd, 1, 20);
		Set<String> agentCodes = rs.getDataList().stream().map(v->v.getCode()).collect(Collectors.toSet());
		Map<String,AgentInfo> agentInfos = IAgentMgr.get().queryAgentInfos(agentCodes);
		for(AgentInfo agentInfo : agentInfos.values()) {
			LvUtil.trace(agentInfo);
		}
		//启动代理
		IAgentMgr.get().startAgent(code);
		//关闭代理
		IAgentMgr.get().shutdownAgent(code);
		//重建代理
		IAgentMgr.get().rebuildAgent(agent);
	}

	/**
	 * 查询代理状态
	 * @throws Exception
	 */
	static void testQueryAgentStatus() throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		IAgentMgr agentMgr = IAgentMgr.get();
		//查询代理运行情况
		Cnd cnd = Cnd.NEW();
		//查询代理列表
		ResultSet<Agent> rs = agentMgr.queryAgentPage(cnd, 1, Integer.MAX_VALUE);
		Set<String> agentCodes = rs.getDataList().stream().map(v->v.getCode()).collect(Collectors.toSet());
		//查询代理状态
		Map<String,AgentInfo> agentInfos = agentMgr.queryAgentInfos(agentCodes);
		for(AgentInfo agentInfo : agentInfos.values()) {
			AgentStatus status = agentInfo.getStatus();
			if(status == AgentStatus.OK) {
				tracer.info("代理"+agentInfo.getName()+"状态正常");
			}else if(status == AgentStatus.WARN || status == AgentStatus.DANGER) {
				tracer.info("代理"+agentInfo.getName()+"状态不健康");
			}else if(status == AgentStatus.LOST){
				tracer.info("代理"+agentInfo.getName()+"已失联");
			}
		}
	}

}
