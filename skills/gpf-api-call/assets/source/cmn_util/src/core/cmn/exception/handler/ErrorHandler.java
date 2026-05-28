package cmn.exception.handler;

import java.io.Serializable;

import cmn.anotation.ClassDeclare;

@ClassDeclare(label = "异常处理类"
,what="异常处理类，用于对所有服务抛出的异常接入处理，重新指定错误码等"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-13"
,updateTime = "2025-02-13")
public interface ErrorHandler extends Serializable{
	
	Throwable handle(Throwable exception);
}