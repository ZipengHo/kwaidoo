package cell.cmn.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;

import com.cdao.dto.CPager;
import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.MppContext;
import com.leavay.common.util.ToolUtilities;

import bap.cells.BasicServiceCell;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.model.IDataService;
import cmn.consts.ServerConfigConst;
import cmn.enums.NestingTableUpdateMode;
import cmn.md.ServerConfig;
import web.dto.MultiPageResultDto;

public class CServerConfig extends BasicServiceCell implements IServerConfig{
	
	Map<String,String> configCache = new ConcurrentHashMap<>();
	Map<String,String> errorHandlerCache = new ConcurrentHashMap<>();
	Long localServerConfigTag = System.currentTimeMillis();
	@Override
	public void refreshCenterServerConfigTag(){
		long tag = System.currentTimeMillis();
		IDaoService.get().putCenterCache(CServerConfig.class.getSimpleName(), tag);
	}
	@Override
	public Long getCenterServerConfigTag() {
		return (Long) IDaoService.get().getCenterCache(CServerConfig.class.getSimpleName());
	}
	@Override
	public void putCenterServerConfigTag(Long tag) {
		IDaoService.get().putCenterCache(CServerConfig.class.getSimpleName(), tag);
	}
	@Override
	public ServerConfig create(IDao dao, ServerConfig serverConfig) throws Exception {
		serverConfig = IDataService.get().createTransCtrl(dao, serverConfig);
		refreshCenterServerConfigTag();
		return serverConfig;
	}
	@Override
	public ServerConfig update(IDao dao, ServerConfig serverConfig) throws Exception {
		serverConfig = IDataService.get().updateTransCtrl(dao, serverConfig, NestingTableUpdateMode.Nothing, null, null);
		refreshCenterServerConfigTag();
		return serverConfig;
	}
	
	@Override
	public ServerConfig save(IDao dao, ServerConfig serverConfig) throws Exception {
		ServerConfig existConfig = queryByCode(dao, serverConfig.getCode());
		if(existConfig == null) {
			serverConfig = create(dao, serverConfig);
		}else {
			serverConfig = update(dao, serverConfig);
		}
		return serverConfig;
	}
	
	@Override
	public ServerConfig query(IDao dao, String uuid) throws Exception {
		return IDataService.get().queryTransCtrl(dao, ServerConfig.class, uuid, false);
	}
	@Override
	public ServerConfig queryByCode(IDao dao, String code) throws Exception {
		return IDataService.get().queryDimensionByCodeTransCtrl(dao, ServerConfig.class, code, false);
	}
	@Override
	public MultiPageResultDto<ServerConfig> queryPage(IDao dao, Cnd cnd, CPager pager) throws Exception {
		return IDataService.get().queryPageTransCtrl(dao, ServerConfig.class, cnd, pager, false);
	}
	@Override
	public void delete(IDao dao, String uuid) throws Exception {
		IDataService.get().deleteTransCtrl(dao, ServerConfig.class, uuid);
		refreshCenterServerConfigTag();
	}
	@Override
	public void deleteByCode(IDao dao, String code) throws Exception {
		IDataService.get().deleteByCndTransCtrl(dao, ServerConfig.class, Cnd.where("code", "=", code));
		refreshCenterServerConfigTag();
	}
	
	//----------------------------异常处理接口配置-----------------------------------
	@Override
	public String getGlobalErrorHandlerClass() {
		return getString(ServerConfigConst.GlobalErrorHandler,null);
	}
	
	@Override
	public String getErrorHandlerClass(String serviceClass) {
		if(serviceClass.endsWith("EmptyImplement")) {
			serviceClass = serviceClass.replaceAll("EmptyImplement", "");
		}
		return errorHandlerCache.get(serviceClass);
	}
	
	@Override
	public String getString(String key,String publicValue) {
		if(configCache.containsKey(key))
			return configCache.get(key);
		return MppContext.getString(key,publicValue);
	}
	@Override
	public long getLong(String key,long publicValue) {
		if(configCache.containsKey(key)) {
			try {
				return CmnUtil.getLong(configCache.get(key));
			}catch (Exception e) {
			}
		}
		return MppContext.getLong(key, publicValue);
	}
	@Override
	public int getInteger(String key,int publicValue) {
		if(configCache.containsKey(key)) {
			try {
				return CmnUtil.getInteger(configCache.get(key));
			}catch (Exception e) {
			}
		}
		return MppContext.getInt(key, publicValue);
	}
	@Override
	public boolean getBoolean(String key,boolean publicValue) {
		if(configCache.containsKey(key)) {
			try {
				return CmnUtil.getBoolean(configCache.get(key));
			}catch (Exception e) {
			}
		}
		return MppContext.getBoolean(key, publicValue);
	}
	@Override
	public int getHttpPort() {
		return MppContext.getInt("jetty.http.port", -1);
	}
	@Override
	public int getRpcPort() {
		return MppContext.getInt("com.leavay.nio.port",-1);
	}
	@Override
	public String getWebAccessWhiteList() {
		return getString("web.access.whitelist", "");
//		return MppContext.getString("web.access.whitelist","");
	}
	@Override
	public void refreshServerConfigCache() throws Exception {
		try (IDao dao = IDaoService.newIDao()){
			MultiPageResultDto<ServerConfig> rs = queryPage(dao, null, new CPager(0, Integer.MAX_VALUE));
			Map<String,String> map = new ConcurrentHashMap<>();
			for(ServerConfig config : rs.getTableData()) {
				if(!CmnUtil.isStringEmpty(config.getValueText())) {
					map.put(config.getCode(), config.getValueText());
				}
			}
			configCache = map;
			String errorHandlers = getString(ServerConfigConst.ErrorHandlers,null);
			Map<String,String> errorHandlerMap = new ConcurrentHashMap<>();
			if(!CmnUtil.isStringEmpty(errorHandlers)) {
				String[]  errorHandlerArr = errorHandlers.split(",");
				for(String errorHandlerPair : errorHandlerArr) {
					String[] pair = errorHandlerPair.split(":");
					if(pair.length == 2) {
						errorHandlerMap.put(pair[0], pair[1]);
					}
				}
			}
			errorHandlerCache = errorHandlerMap;
		}
	}

	Thread mainThread = null;
	@Override
	protected void doStartService() throws Exception {
		mainThread = new Thread("ServerConfig Cache Refresh Thread") {
			@Override
			public void run() {
				while(true) {
					try {
						Long centerUpdateTag = getCenterServerConfigTag();
						if(localServerConfigTag != centerUpdateTag) {
	//							System.out.println("updateServerConfigCache" + localServerConfigTag);
								refreshServerConfigCache();
								if(centerUpdateTag == null) {
	//								System.out.println("putCenterServerConfigTag" + localServerConfigTag);
									putCenterServerConfigTag(localServerConfigTag);
								}else {
									localServerConfigTag = centerUpdateTag;
								}
						}
					} catch (Throwable e) {
						ToolUtilities.warning(CServerConfig.class.getSimpleName(), "refreshServerConfigCache", e);
					}
					ToolUtilities.sleep(1000);
				}
			}
		};
		mainThread.start();
	}

	@Override
	protected void doStopService() {
		if(mainThread != null) {
			mainThread.interrupt();
			mainThread = null;
		}
	}
	

}
