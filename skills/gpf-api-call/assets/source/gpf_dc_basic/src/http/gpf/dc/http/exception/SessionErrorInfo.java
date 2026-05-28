package gpf.dc.http.exception;

import cmn.enums.ErrorLevel;
import cmn.exception.ErrorInfoInterface;
/**
 * 错误码枚举类定义示例
 * 带有错误级别、错误码、错误描述
 */
public enum SessionErrorInfo implements ErrorInfoInterface{
	
	SessionNotFound(ErrorLevel.WARN,"SE_00001","会话不存在！"),
	SessionExpired(ErrorLevel.WARN,"SE_00002","会话已过期！"),
	SessionInvalid(ErrorLevel.WARN,"SE_00003","无效会话！"),
	AppNotExist(ErrorLevel.WARN,"SE_00004","应用不存在！"),
	UserNotExist(ErrorLevel.WARN,"SE_00005","用户不存在"),
	LoginFailed(ErrorLevel.WARN,"SE_00006","账号或密码不正确"),
	Uncategorized(ErrorLevel.ERROR,"SE_99999","未归类")
	;
	
	String errorCode;
	ErrorLevel errorLevel;
	String errorMsg;
	private SessionErrorInfo(ErrorLevel level,String errorCode,String errorMsg) {
		this.errorLevel = level;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}

	@Override
	public String getErrorMsg() {
		return errorMsg;
	}
	
}
