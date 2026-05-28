package cmn.util.maintenance;

import com.leavay.common.util.javac.LvClassLoader;

import cell.cdao.IDao;
/*
 * ThreadLocal级别的类加载器，用于CDao操作importModels时保持模型在一个事务内操作
 */
public class ThreadLocalClassLoader {

	private static ThreadLocal<LvClassLoader> localClassLoader = new ThreadLocal<LvClassLoader>();
	
	private static ThreadLocal<IDao> localDao = new ThreadLocal<IDao>();

	public static LvClassLoader getThreadLocalClassLoader() {
		LvClassLoader df = localClassLoader.get();
		return df;
	}
	
	public static void setThreadLocalClassLoader(LvClassLoader classloder) {
		localClassLoader.set(classloder);
	}
	
	public static void removeClassLoader() {
		localClassLoader.remove();
	}
	
	public static IDao getThreadLocalDao() {
		IDao df = localDao.get();
		return df;
	}
	
	public static void setThreadLocalDao(IDao dao) {
		localDao.set(dao);
	}
	
	public static void removeThreadLocalDao() {
		localDao.remove();
	}

}
