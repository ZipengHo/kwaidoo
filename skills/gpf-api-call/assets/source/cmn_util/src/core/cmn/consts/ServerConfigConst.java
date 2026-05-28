package cmn.consts;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
@ClassDeclare(label = "服务配置参数名常量"
,what="服务配置参数名常量"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-13"
,updateTime = "2025-02-13")
public class ServerConfigConst {
	
	@FieldDeclare(label = "全局常处理接口类", desc = "全局的异常处理接口类")
	public final static String GlobalErrorHandler = "cmn.exception.globalhandler";
	@FieldDeclare(label = "异常处理接口类列表", desc = "特定的异常处理接口类，多个接口类用,分隔，接口类和异常处理类用:分隔，如：cell.cmn.util.IServerConfig:cmn.exception.handler.ErrorHandler,cell.cmn.util.IServerConfig:cmn.exception.handler.ErrorHandler")
	public final static String ErrorHandlers = "cmn.exception.handlers";
}
