package cell.cmn.util;

import org.nutz.dao.Cnd;

import com.cdao.dto.CPager;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cmn.md.ServerConfig;
import web.dto.MultiPageResultDto;

public interface IServerConfig extends ServiceCellIntf{

	public static IServerConfig get() {
		return Cells.get(IServerConfig.class);
	}
	
	public void refreshCenterServerConfigTag();
	public Long getCenterServerConfigTag();
	public void putCenterServerConfigTag(Long tag) ;
	public void refreshServerConfigCache() throws Exception ;
	//-------------------------服务配置操作-------------------------------
	public ServerConfig create(IDao dao,ServerConfig serverConfig)throws Exception;
	
	public ServerConfig update(IDao dao,ServerConfig serverConfig)throws Exception;
	
	public ServerConfig save(IDao dao,ServerConfig serverConfig)throws Exception;
	
	public ServerConfig query(IDao dao,String uuid)throws Exception;
	
	public ServerConfig queryByCode(IDao dao,String code)throws Exception;
	
	public MultiPageResultDto<ServerConfig> queryPage(IDao dao,Cnd cnd,CPager pager)throws Exception;
	
	public void delete(IDao dao,String uuid)throws Exception;
	
	public void deleteByCode(IDao dao,String code)throws Exception;
	
	//------------------------异常处理接口-----------------------------------
	/**
	 * 获取全局设置的异常处理类
	 * @return
	 */
	public String getGlobalErrorHandlerClass();
	/**
	 * 获取指定服务的异常处理类
	 * @param serviceClass
	 * @return
	 */
	public String getErrorHandlerClass(String serviceClass);
	
	//-----------------------服务配置参数获取操作------------------------
	public String getString(String key,String publicValue);
	
	public long getLong(String key,long publicValue) ;
	
	public int getInteger(String key,int publicValue) ;
	
	public boolean getBoolean(String key,boolean publicValue) ;
	
	public int getHttpPort() ;
	
	public int getRpcPort() ;
	
	public String getWebAccessWhiteList() ;
	
}
