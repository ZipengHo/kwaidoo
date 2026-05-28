package cmn.i18n;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

public interface I18nIntf extends Serializable{

	public List<Resource> getResources();
	
	public String getResourceFileName();
	
	public Setting getSetting();
	
	/**
	 * 按国际化资源格式输出文本，在文本中可用{数字}占位符替换参数，将根据传入参数逐个替换，
	 * 如 文本：{1}的值不能为{2}，接收两个输入参数
	 * @param key
	 * @param params
	 * @return
	 */
	default String format(String key, Object... params) {
		Setting res = getSetting();
		if(res != null) {
			key = res.getStr(key,key);
		}
		Map<String,Object> map = buildFormatMap(params);
		if(!CmnUtil.isMapEmpty(map)) {
			return StrUtil.format(key, map);
		}else {
			return key;
		}
	}
	
	default Map<String,Object> buildFormatMap(Object... params){
		if(params != null && params.length > 0) {
			Map<String,Object> map = new HashMap<>();
			for(int i =0;i<params.length;i++) {
				map.put(""+(i+1), params[i] == null ? "null" : params[i]);
			}
			return map;
		}
		return null;
	}
	/**
	 * 在指定资源组中获取国际化资源格式，当在资源组中找不到时找全局定义的资源格式
	 * 并在文本中可用{数字}占位符替换参数，将根据传入参数逐个替换，
	 * 如 文本：{1}的值不能为{2}，接收两个输入参数
	 * @param key
	 * @param group
	 * @param params
	 * @return
	 */
	default String formatInGroup(String key, String group,Object... params) {
		Setting res = getSetting();
		String format = key;
		if(res != null) {
			format = res.getStr(key,group,null);
			if(format == null)
				format = res.getStr(key,key);
		}
		Map<String,Object> map = buildFormatMap(params);
		if(!CmnUtil.isMapEmpty(map)) {
			return StrUtil.format(format, map);
		}else {
			return format;
		}
	}
	/**
	 * 在指定资源组中获取国际化资源格式，当在资源组中找不到时找全局定义的资源格式
	 * 并在文本中可用{数字}占位符替换参数，将根据传入参数逐个替换，
	 * 如 文本：{1}的值不能为{2}，接收两个输入参数
	 * @param key
	 * @param groups
	 * @param params
	 * @return
	 */
	default String formatInGroups(String key, String[] groups,Object... params) {
		Setting res = getSetting();
		String format = key;
		if(res != null) {
			for(String group : groups) {
				format = res.getStr(key,group,null);
				if(format != null)
					break;
			}
			if(format == null)
				format = res.getStr(key,key);
		}
		Map<String,Object> map = buildFormatMap(params);
		if(!CmnUtil.isMapEmpty(map)) {
			return StrUtil.format(format, map);
		}else {
			return format;
		}
	}
	
}
