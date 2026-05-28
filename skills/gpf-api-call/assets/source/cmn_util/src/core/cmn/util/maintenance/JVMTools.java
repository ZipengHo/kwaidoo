package cmn.util.maintenance;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.CpuAndMem;
import com.leavay.common.util.ToolBasic;

import cmn.dto.maintenance.JVMInfo;

public class JVMTools {
	
	public static JVMInfo getJVMInfo() {
		CpuAndMem cpuMem = ToolBasic.getCpuAndMem();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage us = memoryMXBean.getHeapMemoryUsage();
		JVMInfo info = new JVMInfo();
		info.setProcessID(ToolUtilities.getProcessID())
			.setProcessStartTime(ToolUtilities.getProcessStartTime())
			.setProcessCpu(cpuMem.procCpu)
			.setSystemCpu(cpuMem.sysCpu)
			.setHeapCommittedMem(us.getCommitted())
			.setHeapInitMem(us.getInit())
			.setHeapUsedMem(us.getUsed())
			.setHeapMaxMem(us.getMax())
			.setSystemFreeMem(cpuMem.freeSysMem)
			.setSystemTotalMem(cpuMem.getTotalSysMem())
			.setThreadCount(ToolUtilities.getThreadCount())
		;
		return info;
	}
//	/**
//	 * 查询所有线程堆栈信息
//	 */
//	public static String getAllThreadInfo() {
//		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
//		ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds(), 100);
//		StringBuffer sb = new StringBuffer();
//		for(ThreadInfo threadInfo:threadInfos) {
//			StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
//			for(StackTraceElement stack: stackTraceElements) {
//				sb.append(stack.toString());
//				sb.append("\n");
//			}
//		}
//		return sb.toString();
//	}
	/**
	 * 获取所有线程堆栈信息
	 * @param onlyBlocked
	 * @return
	 */
	public static String getAllThreadStacks(boolean onlyBlocked) {
		return ToolUtilities.printThreadStacks(onlyBlocked);
	}
	
}
