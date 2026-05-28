package cmn.exception;

import cmn.enums.ErrorLevel;

public class VerifyException extends BaseException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4847864050501039472L;

	public VerifyException() {
		super();
	}

	public VerifyException(Throwable cause) {
		super(ErrorLevel.INFO,"",cause);
	}

	public VerifyException(String message) {
		super(ErrorLevel.INFO,"",message);
	}

	public VerifyException(String message, Throwable cause) {
		super(ErrorLevel.INFO,"",message, cause);
	}
	
	public VerifyException(ErrorInfoInterface errorInfo) {
		super(errorInfo);
	}
	
	public VerifyException(ErrorInfoInterface errorInfo, Throwable cause) {
		super(errorInfo,cause);
	}
	
	public VerifyException(ErrorInfoInterface errorInfo, String message) {
		super(errorInfo.getErrorLevel(),errorInfo.getErrorCode(),message);
	}

}