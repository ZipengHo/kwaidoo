package gpf.study.errorhandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;

import cmn.anotation.ClassDeclare;
import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.exception.ErrorInfoInterface;
import cmn.exception.handler.ErrorHandler;
@ClassDeclare(label = "异常处理类代码样例"
,what="异常处理类代码样例，演示如何对服务抛出的异常进行干预包装成业务可以读懂的异常，以下定义了错误码枚举类示例，具体可根据实际项目需要，调整为模型管理配置的错误码和匹配规则"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-14"
,updateTime = "2025-02-14")
public class StudyErrorHandler implements ErrorHandler{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7752892622107640444L;

	/**
	 * 错误码枚举类定义示例
	 * 带有错误级别、错误码、错误描述
	 */
	public static enum StudyErrorInfo implements ErrorInfoInterface{
		
		ConnectionFail(ErrorLevel.ERROR,"ERROR_0001","数据库连接异常"),
		TableNotFound(ErrorLevel.INFO,"ERROR_0002","表不存在"),
		Unkown(ErrorLevel.WARN,"ERROR_9999","未知异常")
		;
		
		String errorCode;
		ErrorLevel errorLevel;
		String errorMsg;
		private StudyErrorInfo(ErrorLevel level,String errorCode,String errorMsg) {
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

	@Override
	public Throwable handle(Throwable exception) {
		LvUtil.trace("处理异常：" + exception);
		String message = exception.getMessage();
		LvUtil.trace("message：" + message);
		String exceptionStack = ToolUtilities.getFullExceptionStack(exception);
		LvUtil.trace("exceptionStack：" + exceptionStack);
		//如果是异常基类，可以选择是原封不动抛出，或者是重新转译后抛出
		if(exception instanceof BaseException) {
			return new StudyBizException(((BaseException) exception).getErrorLevel(), ((BaseException) exception).getErrorCode(), exception.getMessage(), exception.getCause());
		}
		if(find(exceptionStack,"PSQLException:(.+)timed out")) {
			return new StudyBizException(StudyErrorInfo.ConnectionFail,exception);
		}else if(find(exceptionStack,"PSQLException: 错误: 关系 (.+) 不存在")) {
			return new StudyBizException(StudyErrorInfo.TableNotFound,exception);
		}else {
			return new StudyBizException(StudyErrorInfo.Unkown,exception);
		}
	}
	/**
	 * 检测堆栈日志是否匹配正则
	 * @param errorStack
	 * @param regexStr
	 * @return
	 */
	public boolean find(String errorStack,String regexStr) {
		Pattern regex = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL | Pattern.MULTILINE);
		Matcher regexMatcher = regex.matcher(errorStack);
		return regexMatcher.find();
	}
	
	public static void main(String[] args) {
		String errorStack = "org.postgresql.util.PSQLException: 错误: 关系 测试表 不存在\r\n" + 
				"	at cell.gpf.study.errorhandler.IStudyErrorHandler.testErrorHande";
		 String regexStr = "PSQLException: 错误: 关系 (.+) 不存在";
		System.out.println(new StudyErrorHandler().find(errorStack, regexStr));
	}

}
