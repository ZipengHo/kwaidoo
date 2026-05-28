package gpf.dc.http;

import cmn.dto.jwt.JwtUserInfo;

import java.util.Map;

public class AppUserInfo extends JwtUserInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5420865622299858975L;
	String appCode;

	Map<String,Object> extFields;
	
	public String getAppCode() {
		return appCode;
	}
	public AppUserInfo setAppCode(String appCode) {
		this.appCode = appCode;
		return this;
	}

	public Map<String, Object> getExtFields() {
		return extFields;
	}

	public AppUserInfo setExtFields(Map<String, Object> extFields) {
		this.extFields = extFields;
		return this;
	}
}
