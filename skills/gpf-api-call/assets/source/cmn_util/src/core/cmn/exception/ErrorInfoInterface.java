package cmn.exception;

import cmn.anotation.ClassDeclare;
import cmn.enums.ErrorLevel;

@ClassDeclare(label = "错误码接口定义类"
,what="声明为业务错误码的服务接口"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-13"
,updateTime = "2025-02-13")
public interface ErrorInfoInterface {

	/**
	 * 异常级别
	 * @return
	 */
	ErrorLevel getErrorLevel();
	/**
	 * 错误码
	 * @return
	 */
	String getErrorCode();
	/**
	 * 错误描述
	 * @return
	 */
	String getErrorMsg();
}
