package gpf.dc.basic.util.shell;

import java.io.File;

import com.leavay.common.util.ToolUtilities;
import com.leavay.common.util.Utils;
import com.leavay.dfc.mgr.etl.Exception.TTerminatedException;

public class ShellExecutor {
	
	private ShellTraceLogger traceLogger = new ShellTraceLogger();
	private ShellErrorLogger errLogger = new ShellErrorLogger();
	private String encoding = "utf-8";
	private int returnCode;
//	private TRuntimeContext rc;
	private String scriptDirectory;
	/**
	 * Shell脚本执行器
	 * @param scriptDirectory	脚本生成目录
	 */
	public ShellExecutor(String scriptDirectory) {
		this(scriptDirectory,-1,-1,-1,-1);
	}
	
	public ShellExecutor(String scriptDirectory,int outMaxKeepLine,int outLimitLineLens,int errMaxKeepLine,int errLimitLineLens) {
		this.scriptDirectory = scriptDirectory;
		if(outMaxKeepLine == -1){
			traceLogger = new ShellTraceLogger();
		}else{
			traceLogger = new ShellTraceLogger(outMaxKeepLine);
		}
		traceLogger.setLimitLineLens(outLimitLineLens);
		if(errLimitLineLens == -1){
			errLogger = new ShellErrorLogger();
		}else{
			errLogger = new ShellErrorLogger(errMaxKeepLine);
		}
		errLogger.setLimitLineLens(errLimitLineLens);
	}
	
	public boolean isKeepTraceLog(){
		return traceLogger.isKeepTraceLog();
	}
	
	public void setKeepTraceLog(boolean keepTraceLog){
		traceLogger.setKeepTraceLog(keepTraceLog);
	}
	
//	public TRuntimeContext getRC(){
//		return rc;
//	}
//	
//	public void setRC(TRuntimeContext rc){
//		this.rc = rc;
//	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	/**
	 * 获取打印日志
	 * @return	打印日志
	 */
	public String getTraceMsg(){
		return traceLogger.getTraceMsg();
	}
	/**
	 * 获取错误日志
	 * @return	错误日志
	 */
	public String getErrorMsg(){
		return errLogger.getErrorMsg();
	}
	
	public String getScriptDirectory() {
		return scriptDirectory;
	}

	public void setScriptDirectory(String scriptDirectory) {
		this.scriptDirectory = scriptDirectory;
	}

	public void execShell(String cmd) throws Exception {
		execShell(cmd, false);
	}

	public void execShell(String cmd,boolean ignoreError) throws Exception {
		traceLogger.clear();
		errLogger.clear();
		// to create a tmp file for sh cmd
		File dir = new File(scriptDirectory);
		dir.mkdirs();
		File shFile = new File(dir, "tmp" + Utils.getId() + ".sh");
		String path = shFile.getAbsolutePath();
		ToolUtilities.createFile(path, cmd, "utf-8");

		// to chmod the sh file
		Process proc = ShellCaller.exec("chmod +x " + path, traceLogger, errLogger, encoding);
		returnCode = ShellCaller.waitFor(proc);
		if (returnCode != 0) {
			shFile.delete();
			throw new Exception("Chmod sh fail: " + path);
		}

		// to execute the sh file
		// trace("Shell path: " + path);
		returnCode = -1;
		proc = ShellCaller.exec("sh " + path, traceLogger, errLogger, encoding);
		try {
			returnCode = ShellCaller.waitFor(proc);
			// to delete the sh file
			shFile.delete();
			if(returnCode != 0 && !ignoreError){
				throw new Exception("Execute sh error,return code : " + returnCode+",error:" + errLogger.getErrorMsg());
			}
		} catch (Exception e) {
			boolean isTerminate = ToolUtilities.isCausedBy(e, InterruptedException.class)
					|| ToolUtilities.isCausedBy(e, TTerminatedException.class);
			if (!isTerminate && errLogger.isError()) {
				// 非中断异常时，检查
				throw new Exception("Execute sh error: " + errLogger.getErrorMsg());
			}
		} finally {
			if (returnCode != 0) {
				if(proc != null)
					ShellUtil.pkillProcess(proc);
				ShellUtil.killProcess(scriptDirectory, path);
			}
		}

	}
	
}
