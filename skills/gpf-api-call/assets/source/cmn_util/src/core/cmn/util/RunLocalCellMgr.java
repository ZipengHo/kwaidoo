package cmn.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bap.cells.Cells;
import cell.CellIntf;
import cell.ServiceCellIntf;
/**
 * 本地调试Cell工具类，在本地调试时，将指定的cell通过实例化，跑在本地代码中，只能对非服务类cell进行实例化,
 * 在获取Cell 实例时，使用get(xxx)代替Cells.get(xxx)
 * @author chenxb
 *
 */
public class RunLocalCellMgr {
	
	public static Map<String,Object> localCellMap = new ConcurrentHashMap<>();
	
	public static void clear() {
		localCellMap.clear();
	}
	
	public static <T extends CellIntf> void add(Class<T> clazz,T instance) throws Exception {
		if(ServiceCellIntf.class.isAssignableFrom(clazz)) {
			throw new Exception("Unsupport to set Service Cell in local debug mode! "+clazz);
		}
		localCellMap.put(clazz.getName(), instance);
	}
	
	public static void remove(Class clazz) {
		localCellMap.remove(clazz.getName());
	}

	public static <T extends CellIntf> T tryGet(Class<T> clazz) {
		if(localCellMap.containsKey(clazz.getName())) {
			return (T) localCellMap.get(clazz.getName());
		}
		return Cells.get(clazz);
	}
}
