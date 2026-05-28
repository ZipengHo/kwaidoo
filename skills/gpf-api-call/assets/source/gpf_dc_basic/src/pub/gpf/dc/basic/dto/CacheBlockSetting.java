package gpf.dc.basic.dto;

import java.io.Serializable;

public class CacheBlockSetting implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1173959436893042157L;

	public final static String FormModelId = "gpf.md.basic.CacheBlockSettingDo";

	public final static String Description = "说明";
	public final static String CacheSize = "缓存大小";
	public final static String CacheGroup = "缓存分组";
	public final static String ClassName = "数据类型";
	
	String uuid;
	String code;
	String description;
	Long cacheSize;
	String cacheGroup;
	String className;
	public CacheBlockSetting() {
	}
	public String getUuid() {
		return uuid;
	}
	public CacheBlockSetting setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	public String getCode() {
		return code;
	}
	public CacheBlockSetting setCode(String code) {
		this.code = code;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public CacheBlockSetting setDescription(String description) {
		this.description = description;
		return this;
	}
	public Long getCacheSize() {
		return cacheSize;
	}
	public CacheBlockSetting setCacheSize(Long cacheSize) {
		this.cacheSize = cacheSize;
		return this;
	}
	public String getCacheGroup() {
		return cacheGroup;
	}
	public CacheBlockSetting setCacheGroup(String cacheGroup) {
		this.cacheGroup = cacheGroup;
		return this;
	}
	public String getClassName() {
		return className;
	}
	public CacheBlockSetting setClassName(String className) {
		this.className = className;
		return this;
	}
	
	
}
