package gpf.study.errorhandler;

import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.exception.ErrorInfoInterface;
/**
 *	演示继承BaseException的业务异常，带有错误码和错误级别定义
 */
public class StudyBizException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5312334256208309217L;

	public StudyBizException(ErrorInfoInterface errorInfo) {
		super(errorInfo);
	}
	
	public StudyBizException(ErrorInfoInterface errorInfo,Throwable cause) {
		super(errorInfo,cause);
	}
	
	public StudyBizException(ErrorLevel errorLevel,String errorCode,String message) {
		super(errorLevel, errorCode, message);
	}
	public StudyBizException(ErrorLevel errorLevel,String errorCode,String message,Throwable cause) {
		super(errorLevel, errorCode, message);
	}
}
