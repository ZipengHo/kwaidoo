package gpf.study.errorhandler;

import cell.gpf.study.errorhandler.IStudyErrorHandler;
import cmn.util.ProxyUtil;
/**
 * 服务代理类样例，代理类通过单例方式构建，避免反复构建动态代理类的开销
 */
public class StudyProxyService {

	private static IStudyErrorHandler testMgr = null;

	public synchronized static IStudyErrorHandler getTestMgr() {
		if (testMgr == null) {
			testMgr = (IStudyErrorHandler) ProxyUtil.newProxyInstance(IStudyErrorHandler.get(),
					new StudyErrorHandler());
		}
		return testMgr;
	}

	public static void testErrorHandler() throws Exception {
		IStudyErrorHandler testMgr = getTestMgr();
		testMgr.testErrorHander("测试表");
	}
}
