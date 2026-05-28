package gpf.dc.http.exception;

import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.exception.ErrorInfoInterface;

public class SessionException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5918711400059528664L;
	public SessionException(ErrorInfoInterface errorInfo) {
		super(errorInfo);
	}
	
	public SessionException(ErrorInfoInterface errorInfo,Throwable cause) {
		super(errorInfo,cause);
	}
	
	public SessionException(ErrorLevel errorLevel,String errorCode,String message) {
		super(errorLevel, errorCode, message);
	}
	public SessionException(ErrorLevel errorLevel,String errorCode,String message,Throwable cause) {
		super(errorLevel, errorCode, message);
	}
}
