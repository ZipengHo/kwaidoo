package gpf.dc.basic.util.shell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.leavay.client.util.CNClientUtil;
import com.leavay.common.util.Pair;
import com.leavay.common.util.ToolUtilities;
import com.leavay.common.util.Utils;

import cmn.util.TraceUtil;
import cmn.util.Tracer;

/**
 * Shell脚本执行工具类
 * 
 * @author chenxb 2018年3月29日
 */
public class ShellUtil {

	/**
	 * 执行Shell脚本
	 * 
	 * @param scriptDir
	 *            创建脚本的目录
	 * @param cmd
	 *            脚本代码
	 * @return left:脚本返回码 right:错误流中的日志
	 * @throws Exception
	 */
	public static Pair<Integer, String> execShell(String scriptDir, String cmd) throws Exception {
		return execShell(scriptDir, cmd, CNClientUtil.getEncode());
	}

	/**
	 * 执行Shell脚本
	 * 
	 * @param scriptDir
	 *            创建脚本的目录
	 * @param cmd
	 *            脚本代码
	 * @param encoding
	 *            指定输出日志编码
	 * @return left:脚本返回码 right:错误流中的日志
	 * @throws Exception
	 */
	public static Pair<Integer, String> execShell(String scriptDir, String cmd, String encoding) throws Exception {
		return execShell(scriptDir, cmd, encoding, false);
	}

	/**
	 * 执行Shell脚本
	 * 
	 * @param scriptDir
	 *            创建脚本的目录
	 * @param cmd
	 *            脚本代码
	 * @param encoding
	 *            指定输出日志编码
	 * @param ignoreError
	 *            脚本执行报错时是否忽略
	 * @return left:脚本返回码 right:错误流中的日志
	 * @throws Exception
	 */
	public static Pair<Integer, String> execShell(String scriptDir, String cmd, String encoding, boolean ignoreError)
			throws Exception {
		// to create a tmp file for sh cmd
		File dir = new File(scriptDir);
		dir.mkdirs();
		File shFile = new File(dir, "tmp" + Utils.getId() + ".sh");
		String path = shFile.getAbsolutePath();
		ToolUtilities.createFile(path, cmd,"utf-8");

		// to chmod the sh file
		ShellTraceLogger logger1 = new ShellTraceLogger();
		ShellErrorLogger logger2 = new ShellErrorLogger();
		Process proc = ShellCaller.exec("chmod +x " + path, logger1, logger2, encoding);
		int v = ShellCaller.waitFor(proc);
		if (v != 0) {
			shFile.delete();
			throw new Exception("Chmod sh fail: " + path);
		}

		// to execute the sh file
		// trace("Shell path: " + path);
		v = -1;
		proc = ShellCaller.exec("sh " + path, logger1, logger2, encoding);
		try {
			v = ShellCaller.waitFor(proc);
			// to delete the sh file
			shFile.delete();
			if (v != 0 && !ignoreError) {
				throw new Exception("Execute sh error,return code : " + v + ",error:" + logger2.getErrorMsg());
			}
		} catch (Exception e) {
			boolean isTerminate = ToolUtilities.isCausedBy(e, InterruptedException.class);
			if (!isTerminate && logger2.isError()) {
				// 非中断异常时，检查
				throw new Exception("Execute sh error: " + logger2.getErrorMsg());
			}
		} finally {
			if (v != 0) {
				// 程序非正常退出，有可能还未中断，得先杀死所有未中断的进程
				if (proc != null)
					pkillProcess(proc);
				killProcess(scriptDir, path);
			}
		}
		return new Pair<Integer, String>(v, logger2.getErrorMsg());
	}

	/**
	 * 根据shell脚本路径杀死shell脚本
	 * 
	 * @param scriptDir
	 *            创建脚本的目录
	 * @param processPath
	 *            要杀死的目标shell进程的脚本文件路径
	 * @throws Exception
	 * @throws IOException
	 */
	public static void killProcess(String scriptDir, String processPath) throws Exception, IOException {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("Kill process: " + processPath);
		List<String> pids = new ArrayList<>();
		pids.add(processPath);

		String dir = scriptDir;
		String killPath = new File(dir, "k" + Utils.getId() + ".sh").getAbsolutePath();
		String filePath = new File(dir, "c" + Utils.getId() + ".txt").getAbsolutePath();
		String shPath = new File(dir, "d" + Utils.getId() + ".sh").getAbsolutePath();
		try {
			String encoding = CNClientUtil.getEncode();
			Set<String> killPIDs = new TreeSet<>();
			while (!pids.isEmpty()) {
				String pid = pids.remove(0);
				String cmd = "ps -ef|grep " + pid + " > " + filePath;
				ToolUtilities.createFile(shPath, cmd,"utf-8");
				Process proc = ShellCaller.exec("chmod 777 " + shPath);
				ShellCaller.waitFor(proc);
				proc = ShellCaller.exec("sh " + shPath);
				ShellCaller.waitFor(proc);
				String content = Utils.getFileContent(filePath, encoding);
				content = content.replaceAll("\r", "\n");
				content = content.replaceAll("\n\n", "\n");
				String[] rows = content.split("\n");
				for (String row : rows) {
					if (row.indexOf("grep") == -1) {
						row = row.trim();
						if (row.length() > 0) {
							StringTokenizer st = new StringTokenizer(row, " ");
							if (st.countTokens() > 1) {
								st.nextToken();
								pid = st.nextToken();
								if (!killPIDs.contains(pid)) {
									pids.add(pid);
									killPIDs.add(pid);
								}
							} else {
								tracer.warning("Invalid row: " + row);
							}
						}
					}
				}
			}
			StringBuffer sb = new StringBuffer();
			for (String pid : killPIDs) {
				sb.append("kill -9 ");
				sb.append(pid);
				sb.append(";\n");
			}
			tracer.info(sb.toString());
			ToolUtilities.createFile(killPath, sb.toString(),"utf-8");
			Process proc = ShellCaller.exec("chmod 777 " + killPath);
			ShellCaller.waitFor(proc);
			proc = ShellCaller.exec("sh " + killPath);
			ShellCaller.waitFor(proc);
		} finally {
			new File(killPath).delete();
			new File(filePath).delete();
			new File(shPath).delete();
		}
	}

	// kill sub processes
	public static void pkillProcess(Process p) {
		Tracer tracer = TraceUtil.getCurrentTracer();
		int pid = getPid(p);
		if (pid != -1) {
			try {
				Process cleanUpProcess = new ProcessBuilder("/usr/bin/pkill", "-9", "-P", String.valueOf(pid)).start();
				int exitcode = cleanUpProcess.waitFor();
				tracer.info(String.format("pid: %d exitcode: %d", pid, exitcode));
			} catch (Exception e) {
				tracer.warning("CLEANUP pid: " + pid + " ERROR:" + ToolUtilities.getFullExceptionStack(e));
			}
		} else {
			tracer.warning("get process pid error");
		}
	}

	public static synchronized int getPid(Process p) {
		int pid = -1;
		try {
			if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				pid = f.getInt(p);
				f.setAccessible(false);
			}
		} catch (Exception e) {
			pid = -1;
		}
		return pid;
	}
}
