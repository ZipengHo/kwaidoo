package cell.cmn.object.comparator;

import java.util.List;
import java.util.Map;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.dto.compare.ObjectDifference;

public interface ICompareObject extends CellIntf{
	
	public static ICompareObject get() {
		return Cells.get(ICompareObject.class);
	}
	/**
	 * 比较两个对象的差异
	 * @param obj1	对象1
	 * @param obj2	对象2
	 * @param idFieldMap	对象中类指定的id属性
	 * @param ignoreCompareFieldMap 对象中类忽略比对的属性
	 * @return
	 * @throws Exception
	 */
	public ObjectDifference compare(Object obj1, Object obj2,
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception ;
	
	/**
	 * 比较两个集合的差异
	 * @param fieldName	属性
	 * @param list1	集合1
	 * @param list2	集合2
	 * @param idFieldMap	对象中类指定的id属性
	 * @param ignoreCompareFieldMap 对象中类忽略比对的属性
	 * @return
	 * @throws Exception
	 */
	public ObjectDifference compareList(String fieldName,List list1, List list2,
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception;

	/**
	 * 比较两个集合的差异
	 * @param fieldName 属性
	 * @param map1	集合1
	 * @param map2	集合2
	 * @param idFieldMap	对象中类指定的id属性
	 * @param ignoreCompareFieldMap	对象中类忽略比对的属性
	 * @return
	 * @throws Exception
	 */
	public ObjectDifference compareMap(String fieldName,Map map1, Map map2,
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception ;
}
