package gpf.dc.basic.util.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.leavay.common.util.ShellLogger;
import com.leavay.common.util.Utils;
import com.leavay.dfc.gui.LvUtil;

public class ShellCaller {
	public static void main(String args[]) throws Exception {
		if (args.length < 1) {
			System.out.println("Command not found!");
			System.exit(1);
		}
		// exec(args[0], new ShellInfoLogger(), new ShellInfoLogger());
	}

	public static Process exec(String cmd) throws Exception {
		return Runtime.getRuntime().exec(cmd);
	}

	public static int waitFor(Process proc) throws InterruptedException {
		return proc.waitFor();
	}

	public static Process exec(String cmd, ShellLogger msgLog,
			ShellLogger errLog) throws Exception {
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(cmd);
		Thread errThread = LvUtil.newThread(new StreamGobbler(proc.getErrorStream(), "ERR", errLog));
		errThread.start();
		Thread outThread = LvUtil.newThread(new StreamGobbler(proc.getInputStream(), "OUT", msgLog));
		outThread.start();
		return proc;
	}
	
	public static Process exec(String cmd, ShellLogger msgLog, ShellLogger errLog, String encoding) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmd);
        Thread errThread = LvUtil.newThread(new StreamGobbler(proc.getErrorStream(), "ERR", errLog,encoding));
		errThread.start();
		Thread outThread = LvUtil.newThread(new StreamGobbler(proc.getInputStream(), "OUT", msgLog,encoding));
		outThread.start();
        return proc;
    }
	
//	public static int waitFor(Process proc) {
//		ProcessWaitForThread t = new ProcessWaitForThread(proc);
//		t.start();
//		return t.getReturnValue();
//	}

	static class StreamGobbler implements Runnable {
		private InputStream is;
		private String type;
		private ShellLogger logger;
		private String encoding = "gbk";

        StreamGobbler(InputStream is, String type, ShellLogger logger) {
            this.is = is;
            this.type = type;
            this.logger = logger;
        }
        
		StreamGobbler(InputStream is, String type, ShellLogger logger, String encoding) {
			this.is = is;
			this.type = type;
			this.logger = logger;
			this.encoding = encoding;
		}

		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(this.is, encoding));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (this.logger != null) {
						this.logger.print(this.type + ">" + line + "\n");
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				Utils.close(br);
			}
		}
	}
}
