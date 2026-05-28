package cmn.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cmn.enums.ErrorLevel;
import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.ToolUtilities;

public class MultiException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6375961257572158713L;
	private static final String DEFAULT_MESSAGE = "Multiple exceptions";
	private List<Throwable> nested;
	
	public MultiException() {
		super(ErrorLevel.ERROR, "", DEFAULT_MESSAGE);
		this.nested = new ArrayList<>();
	}

	public MultiException(ErrorLevel errorLevel, String errorCode, String message) {
		super(errorLevel, errorCode, message);
		this.nested = new ArrayList<>();
	}

	private MultiException(ErrorLevel errorLevel, String errorCode, String message,List<Throwable> nested) {
		super(errorLevel, errorCode, message);
		this.nested = new ArrayList<>(nested);

		if (nested.size() > 0) {
			initCause((Throwable) nested.get(0));
		}
		for (Throwable t : nested) {

			if (t != this) {
				addSuppressed(t);
			}
		}
	}

	public void add(Throwable e) {
		if (e instanceof MultiException) {

			MultiException me = (MultiException) e;
			this.nested.addAll(me.nested);
		} else {

			this.nested.add(e);
		}
	}

	public int size() {
		return (this.nested == null) ? 0 : this.nested.size();
	}

	public List<Throwable> getThrowables() {
		if (this.nested == null)
			return Collections.emptyList();
		return this.nested;
	}

	public Throwable getThrowable(int i) {
		return (Throwable) this.nested.get(i);
	}

	/**
	 * 如果有异常，抛出异常
	 * 如果只有一个异常，抛出该异常
	 * 如果有多个异常，抛出 MultiException
	 * @throws Exception
	 */
	public void ifExceptionThrow() throws Exception {
		Throwable th;
		if (this.nested == null) {
			return;
		}
		switch (this.nested.size()) {
		case 0:
			return;

		case 1:
			th = (Throwable) this.nested.get(0);
			if (th instanceof Error)
				throw (Error) th;
			if (th instanceof Exception)
				throw (Exception) th;
			throw new MultiException(getErrorLevel(),getErrorCode(),getMessage(),this.nested);
		}
		//处理多个异常的摘要消息
		StringBuffer errorMsg =  new StringBuffer();
		int index = 1;
		for (Throwable t : this.nested) {
			Throwable rootCause = ToolUtilities.getExceptionRootCause(t);
			String rootCauseMessage = rootCause.getMessage();
			if(CmnUtil.isStringEmpty(rootCauseMessage)){
				errorMsg.append("Error["+index+"]:"+ToolUtilities.getExceptionMessage(rootCause)).append("\n");
			}else{
				errorMsg.append("Error["+index+"]:"+rootCauseMessage).append("\n");
			}
			index++;
		}
		throw new MultiException(getErrorLevel(),getErrorCode(),errorMsg.toString(),this.nested);
	}

	/**
	 * 如果有异常，抛出RuntimeException异常 
	 * 如果只有一个异常，抛出该异常包裹后的RuntimeException
	 * 如果有多个异常，抛出 MultiException包裹后的RuntimeException
	 */
	public void ifExceptionThrowRuntime() {
		Throwable th;
		if (this.nested == null) {
			return;
		}
		switch (this.nested.size()) {
		case 0:
			return;

		case 1:
			th = (Throwable) this.nested.get(0);
			if (th instanceof Error)
				throw (Error) th;
			if (th instanceof RuntimeException) {
				throw (RuntimeException) th;
			}
			throw new RuntimeException(th);
		}
		//处理多个异常的摘要消息
		StringBuffer errorMsg =  new StringBuffer();
		int index = 1;
		for (Throwable t : this.nested) {
			Throwable rootCause = ToolUtilities.getExceptionRootCause(t);
			String rootCauseMessage = rootCause.getMessage();
			if(CmnUtil.isStringEmpty(rootCauseMessage)){
				errorMsg.append("Error["+index+"]:"+ToolUtilities.getExceptionMessage(rootCause)).append("\n");
			}else{
				errorMsg.append("Error["+index+"]:"+rootCauseMessage).append("\n");
			}
			index++;
		}
		throw new RuntimeException(new MultiException(getErrorLevel(),getErrorCode(),errorMsg.toString(),this.nested));
	}
	/**
	 * 如果有异常，抛出 MultiException
	 */
	public void ifExceptionThrowMulti() {
		if (this.nested == null) {
			return;
		}
		if (this.nested.size() > 0) {
			//处理多个异常的摘要消息
			StringBuffer errorMsg =  new StringBuffer();
			int index = 1;
			for (Throwable t : this.nested) {
				Throwable rootCause = ToolUtilities.getExceptionRootCause(t);
				String rootCauseMessage = rootCause.getMessage();
				if(CmnUtil.isStringEmpty(rootCauseMessage)){
					errorMsg.append("Error["+index+"]:"+ToolUtilities.getExceptionMessage(rootCause)).append("\n");
				}else{
					errorMsg.append("Error["+index+"]:"+rootCauseMessage).append("\n");
				}
				index++;
			}
			MultiException exception = new MultiException(getErrorLevel(),getErrorCode(),errorMsg.toString(),this.nested);
			throw exception;
		}
	}
	/**
	 * 如果有异常，抛出异常
	 * 如果只有一个异常，抛出该异常
	 * 如果有多个异常，抛出 MultiException
	 * @throws Exception
	 */
	public void ifExceptionThrowSuppressed() throws Exception {
		if (this.nested == null || this.nested.size() == 0) {
			return;
		}
		Throwable th = (Throwable) this.nested.get(0);
		if (!(th instanceof Error) && !(th instanceof Exception)) {
			th = new MultiException(getErrorLevel(),getErrorCode(),getMessage(),Collections.emptyList());
		}
		for (Throwable s : this.nested) {

			if (s != th)
				th.addSuppressed(s);
		}
		if (th instanceof Error)
			throw (Error) th;
		throw (Exception) th;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(MultiException.class.getSimpleName());
		str.append(":"+getMessage());
		if (this.nested == null || this.nested.size() <= 0) {
			str.append("[]");
		} else {
			str.append(this.nested);
		}
		return str.toString();
	}

	public static void main(String[] args) throws Exception {
		MultiException exception = new  MultiException();
		exception.add(new Exception("错误1"));
		exception.add(new Exception("错误2"));
		exception.ifExceptionThrow();
	}
}