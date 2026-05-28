package gpf.adur.data;

import java.io.Serializable;

import com.kwaidoo.ms.tool.CmnUtil;

import cmn.anotation.ClassDeclare;
@ClassDeclare(
	    label = "密码数据",
	    what = "表单中属性类型为密码(Password)的值类型\r\n",
	    why = "",
	    how = "构建密码数据示例：\r\n" + 
	    		"Password password = new Password().setValue(\"123456\");\r\n" + 
	    		"获取密码数据示例：\r\n" + 
	    		"Password password = form.getPassword(\"密码属性\");",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class Password implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5832040862009049807L;

	public static final String PASSWORD_NOCHANGE = "PasswordNoChange(!@#)";

	/**
	 * 设值时传明文
	 */
	String value = PASSWORD_NOCHANGE;
	/**
	 * 密文
	 */
	String secPwd;
	
	public String getValue() {
		return value;
	}
	public Password setValue(String value) {
		this.value = value;
		return this;
	}
	public String getSecPwd() {
		return secPwd;
	}
	public Password setSecPwd(String secPwd) {
		this.secPwd = secPwd;
		return this;
	}
	
	public boolean isPasswordNoChange() {
		return CmnUtil.isStringEqual(value, PASSWORD_NOCHANGE);
	}
	
	@Override
	public String toString() {
		if(isPasswordNoChange()) {
			if(secPwd == null)
				return value+"";
			else
				return secPwd+"";
		}else {
			return "original :" + secPwd + ",modified:"+value;
		}
	}
}
