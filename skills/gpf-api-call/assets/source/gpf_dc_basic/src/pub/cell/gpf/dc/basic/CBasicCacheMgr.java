package cell.gpf.dc.basic;

import java.io.Serializable;
import java.util.function.Function;

import com.kwaidoo.ms.tool.CmnUtil;

import bap.cells.BasicServiceCell;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.cache.ICacheMgr;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.dto.CacheBlockSetting;

public class CBasicCacheMgr extends BasicServiceCell implements IBasicCacheMgr{
	
	@Override
	public void initCacheBlock(CacheBlockSetting setting) throws Exception {
		ICacheMgr.get().initCache(setting.getCode(), CmnUtil.getInteger(setting.getCacheSize()), setting.getCacheGroup());
	}
	
	@Override
	public void initAllCacheBlock() throws Exception {
		try (IDao dao = IDaoService.newIDao()){
			ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, CacheBlockSetting.FormModelId, null, 1, Integer.MAX_VALUE);
			for(Form form : rs.getDataList()) {
				CacheBlockSetting setting = convert2CacheBlockSetting(form);
				initCacheBlock(setting);
			}
		}
	}

	@Override
	public void rebuildCacheBlock(String cacheBlock, int cacheSize) throws Exception {
		if(cacheSize < 0)
			ICacheMgr.get().rebuildCache(cacheBlock);
		else
			ICacheMgr.get().rebuildCache(cacheBlock, cacheSize);
	}

	@Override
	public void removeCacheBlock(String cacheBlock) throws Exception {
		ICacheMgr.get().removeCache(cacheBlock);
	}

	@Override
	public <T> T computeCacheIfAbsent(String cacheBlock, String key, Class<T> clazz,Function<String, T> mappingFunction)
			throws Exception {
		T data = getCacheData(cacheBlock, key, clazz);
		if(data == null) {
			data = mappingFunction.apply(key);
			if(data != null)
				cacheData(cacheBlock, key, (Serializable)data);
		}
		return (T) data;
	}

	@Override
	public <T> T getCacheData(String cacheBlock, String cacheKey, Class<T> clazz) throws Exception {
		T data = ICacheMgr.get().getCacheData(cacheBlock, cacheKey, clazz);
		if(data == null)
			return null;
		if(!clazz.isAssignableFrom(data.getClass())) {
			return null;
		}
		return data;
	}

	@Override
	public void cacheData(String cacheBlock, String cacheKey, Serializable data) throws Exception {
		if(data != null)
			ICacheMgr.get().cacheData(cacheBlock, cacheKey, data);
	}
	
	CacheBlockSetting convert2CacheBlockSetting(Form form) throws Exception {
		CacheBlockSetting dto = new CacheBlockSetting();
		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code))
		.setCacheSize(form.getLong(CacheBlockSetting.CacheSize)).setCacheGroup(form.getString(CacheBlockSetting.CacheGroup))
		.setClassName(form.getString(CacheBlockSetting.ClassName)).setDescription(form.getString(CacheBlockSetting.Description));
		return dto;
	}

	@Override
	protected void doStartService() throws Exception {
		//TODO 要搞成开启线程轮询，定期检查缓存表是否需要重建
		try (IDao dao = IDaoService.newIDao()){
			ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, CacheBlockSetting.FormModelId, null, 1, Integer.MAX_VALUE);
			for(Form form : rs.getDataList()) {
				try {
					CacheBlockSetting setting = convert2CacheBlockSetting(form);
					initCacheBlock(setting);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doStopService() {
		try (IDao dao = IDaoService.newIDao()){
			ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, CacheBlockSetting.FormModelId, null, 1, Integer.MAX_VALUE);
			for(Form form : rs.getDataList()) {
				CacheBlockSetting setting = convert2CacheBlockSetting(form);
				removeCacheBlock(setting.getCode());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
