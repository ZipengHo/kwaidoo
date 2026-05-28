package cmn.exception;

import com.kwaidoo.ms.tool.CmnUtil;

import cmn.anotation.ClassDeclare;
import cmn.enums.ErrorLevel;

@ClassDeclare(label = "异常类基类"
,what="异常类基类，带有错误码信息，所有需要抛出错误码的自定义异常都需要继承此类"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-13"
,updateTime = "2025-02-13")
public class BaseException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4847864050501039472L;
	private ErrorLevel errorLevel = ErrorLevel.ERROR;
	/**
	 * 错误码
	 */
	private String errorCode = "";
	
	public BaseException() {
	}

	public BaseException(ErrorLevel errorLevel,String errorCode,String message) {
		super(message);
		this.errorLevel = errorLevel;
		this.errorCode = errorCode;
	}
	
	public BaseException(ErrorLevel errorLevel,String errorCode,Throwable cause) {
		super(cause);
		this.errorLevel = errorLevel;
		this.errorCode = errorCode;
	}

	public BaseException(ErrorLevel errorLevel,String errorCode,String message, Throwable cause) {
		super(message, cause);
		this.errorLevel = errorLevel;
		this.errorCode = errorCode;
	}
	
	public BaseException(ErrorInfoInterface errorInfo) {
		super(errorInfo.getErrorMsg());
		this.errorLevel = errorInfo.getErrorLevel();
		this.errorCode = errorInfo.getErrorCode();
	}

	public BaseException(ErrorInfoInterface errorInfo,Throwable cause) {
		super(errorInfo.getErrorMsg(),cause);
		this.errorLevel = errorInfo.getErrorLevel();
		this.errorCode = errorInfo.getErrorCode();
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	public ErrorLevel getErrorLevel() {
		return errorLevel;
	}
	
	public String getErrorBrief() {
		if(!CmnUtil.isStringEmpty(errorCode)) {
			if(CmnUtil.isStringEmpty(getMessage())) {
				return errorCode;
			}else {
				return errorCode+"-"+getMessage();
			}
		}else {
			return getMessage();
		}
	}
	
	@Override
	public String toString() {
		String s = getClass().getName();
        String errorBrief = getErrorBrief();
        return (errorBrief != null) ? (s + ": " + errorBrief) : s;
	}
}