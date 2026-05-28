package cell.gpf.dc.basic;

import java.io.Serializable;
import java.util.function.Function;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import gpf.dc.basic.dto.CacheBlockSetting;

public interface IBasicCacheMgr extends ServiceCellIntf{

	public static IBasicCacheMgr get() {
		return Cells.get(IBasicCacheMgr.class);
	}
	
	public void initAllCacheBlock()throws Exception;
	
	public void initCacheBlock(CacheBlockSetting setting)throws Exception;
	
	public void rebuildCacheBlock(String cacheBlock,int cacheSize)throws Exception;
	
	public void removeCacheBlock(String cacheBlock)throws Exception;
	
	public <T> T computeCacheIfAbsent(String cacheBlock, String key, Class<T> clazz,Function<String, T> mappingFunction)
			throws Exception;
	
	public <T> T getCacheData(String cacheBlock,String cacheKey,Class<T> clazz)throws Exception;
	
	public void cacheData(String cacheBlock,String cacheKey,Serializable data)throws Exception;
}
