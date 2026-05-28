package cell.cmn.object.comparator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;

import bap.cells.BasicCell;
import cmn.dto.PreloadTreeNode;
import cmn.dto.compare.ObjectDifference;
import cmn.util.ClassUtil;

/**
 * 比较器工具类
 * 
 * @author chenxb
 *
 */
public class CCompareObject extends BasicCell implements ICompareObject{

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
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception {
		if(obj1 == null || obj2 == null) {
			ObjectDifference difference = new ObjectDifference("", obj1, obj2);
			return difference;
		}
//		if (obj1 == null) 
//			throw new Exception("Object1 can not be null!");
//		if (obj2 == null)
//			throw new Exception("Object2 Object can not be null!");
//		if (!obj1.getClass().isAssignableFrom(obj2.getClass()))
//			throw new Exception("Object Class can not be different!");
		if(ClassUtil.isBasicType(obj1)) {
			if (!Objects.equals(obj1, obj2)) {
				ObjectDifference difference = new ObjectDifference("", obj1, obj2);
				return difference;
			}else {
				return null;
			}
		}
		List<ObjectDifference> differences = new ArrayList<>();

		Class<?> clazz = obj1.getClass();
		if(ClassUtil.isAssignableFrom(Map.class, obj1)) {
			return compareMap("", (Map)obj1, (Map)obj2, idFieldMap, ignoreCompareFieldMap);
		}else if(ClassUtil.isAssignableFrom(List.class, obj1)) {
			return compareList("", (List)obj1, (List)obj2, idFieldMap, ignoreCompareFieldMap);
		}
		Field[] fields = clazz.getDeclaredFields();
		List<String> ignoreFields = getIgnoreFields(obj1, obj2, ignoreCompareFieldMap);
		for (Field field : fields) {
			if(ignoreFields.contains(field.getName()))
				continue;
			field.setAccessible(true);
			String fieldName = field.getName();//parentField.isEmpty() ? field.getName() : parentField + "." + field.getName();
			try {
				Object value1 = field.get(obj1);
				Object value2 = field.get(obj2);
				if(value1 == null && value2 == null)
					continue;
				else if(value1 == null) {
					ObjectDifference difference = new ObjectDifference(fieldName, null, value2);
					differences.add(difference);
					continue;
				}else if(value2 == null) {
					ObjectDifference difference = new ObjectDifference(fieldName, value1, null);
					differences.add(difference);
					continue;
				}
				if (isAssignableFrom(List.class,value1) && isAssignableFrom(List.class,value2)) {
					List<Object> list1 = (List<Object>) value1;
					List<Object> list2 = (List<Object>) value2;
					ObjectDifference difference = compareList(fieldName,list1, list2, idFieldMap,
							ignoreCompareFieldMap);
					if (difference != null) {
						differences.add(difference);
					}
				} else if (isAssignableFrom(Map.class,value1) && isAssignableFrom(Map.class,value2)) {
					Map<String, Object> map1 = (Map<String, Object>) value1;
					Map<String, Object> map2 = (Map<String, Object>) value2;
					ObjectDifference difference = compareMap(fieldName,map1, map2, idFieldMap,
							ignoreCompareFieldMap);
					if (difference != null) {
						differences.add(difference);
					}
				} else {
					// 值是基础类型时，比较值的差异，否则需要比较对象的差异
					ObjectDifference difference = compareObjectField(field, value1, value2, idFieldMap,
							ignoreCompareFieldMap);
					if (difference != null)
						differences.add(difference);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(differences.isEmpty())
			return null;
		ObjectDifference difference = new ObjectDifference("", new ArrayList<>(), differences, new ArrayList<>());
		return difference;
	}
	
	public boolean isAssignableFrom(Class clazz,Object value) {
		return ClassUtil.isAssignableFrom(clazz, value);
	}

	private ObjectDifference compareObjectField(Field field, Object value1, Object value2,
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception {
		String fieldName = field.getName();
		// 值是基础类型时，比较值的差异，否则需要比较对象的差异
		if (ClassUtil.isBasicType(field)) {
			if (!Objects.equals(value1, value2)) {
				ObjectDifference difference = new ObjectDifference(fieldName, value1, value2);
				return difference;
			}
		} else {
			ObjectDifference difference = compare(value1, value2, idFieldMap,
					ignoreCompareFieldMap);
			if(difference == null)
				return null;
			difference.setField(fieldName);
			return difference;
		}
		return null;
	}
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
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception {
		if (list1.isEmpty() && list2.isEmpty()) {
			return null;
		}
		// 检查idField是否存在，如果存在，走compareMap的形式，如果不存在，走列表遍历的形式
		String idField = getIdField(list1, list2, idFieldMap);
		if (!CmnUtil.isStringEmpty(idField)) {
			String[] idFields = idField.split("-");
			Map<String, Object> map1 = new LinkedHashMap<>();
			Map<String,Integer> indexMap1 = new LinkedHashMap<>();
			for (int i =0;i<list1.size();i++) {
				Object value1 = list1.get(i);
				String idValue = getIdValue(value1, idFields);//ToolUtilities.getFieldValue(value1, idField);
				map1.put(idValue, value1);
				indexMap1.put(idValue, i);
			}
			Map<String, Object> map2 = new LinkedHashMap<>();
			Map<String,Integer> indexMap2 = new LinkedHashMap<>();
			for (int i =0;i<list2.size();i++) {
				Object value2 = list2.get(i);
				String idValue = getIdValue(value2, idFields);//ToolUtilities.getFieldValue(value2, idField);
				map2.put(idValue, value2);
				indexMap2.put(idValue, i);
			}
			Set<String> keySet = new LinkedHashSet<>(map1.keySet());
			keySet.addAll(map2.keySet());
			Map<String,Object> addedMap = new LinkedHashMap<>();
			Map<String,Object> removedMap = new LinkedHashMap<>();
			List<ObjectDifference> modified = new ArrayList<>();
			for (String key : keySet) {
				Object value1 = map1.get(key);
				Object value2 = map2.get(key);
				if(value1 == null) {
					addedMap.put(key,value2);
					continue;
				}
				if(value2 == null) {
					removedMap.put(key,value1);
					continue;
				}
				ObjectDifference objDiffs = compare(value1, value2, idFieldMap,
						ignoreCompareFieldMap);
				if(objDiffs != null) {
					int index = indexMap1.get(key);
					objDiffs.setField("oldValue["+index+"]");
					modified.add(objDiffs);
				}
			}
			if(addedMap.isEmpty() && modified.isEmpty() && removedMap.isEmpty())
				return null;
			List<ObjectDifference> added = new ArrayList<>();
			for(String key : addedMap.keySet()) {
				int index = indexMap2.get(key);
				ObjectDifference addDiff = new ObjectDifference("newValue["+index+"]", null, addedMap.get(key));
				added.add(addDiff);
			}
			List<ObjectDifference> removed = new ArrayList<>();
			for(String key : removedMap.keySet()) {
				int index = indexMap1.get(key);
				ObjectDifference removeDiff = new ObjectDifference("oldValue["+index+"]", null, removedMap.get(key));
				removed.add(removeDiff);
			}
			ObjectDifference difference = new ObjectDifference(fieldName, added, modified, removed);
			return difference;
//			return compareMap(fieldName,map1, map2, idFieldMap, ignoreCompareFieldMap);
		} else {
			List<ObjectDifference> modified = new ArrayList<>();
			for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
				Object value1 = list1.get(i);
				Object value2 = list2.get(i);
				ObjectDifference objDiffs = compare(value1, value2, idFieldMap,
						ignoreCompareFieldMap);
				if(objDiffs != null) {
					objDiffs.setField("oldValue["+i+"]");
					modified.add(objDiffs);
				}
			}
			List<ObjectDifference> added = new ArrayList<>();
			if (list1.size() < list2.size()) {
				for(int i = list1.size();i<list2.size();i++) {
					Object newValue = list2.get(i);
					ObjectDifference addDiff = new ObjectDifference("newValue["+i+"]", null,newValue);
					added.add(addDiff);
				}
			} 
			List<ObjectDifference> removed = new ArrayList<>();
			if (list1.size() > list2.size()) {
				for(int i = list2.size();i<list1.size();i++) {
					Object oldValue = list1.get(i);
					ObjectDifference removeDiff = new ObjectDifference("oldValue["+i+"]", oldValue,null);
					removed.add(removeDiff);
				}
			}
			if(added.isEmpty() && modified.isEmpty() && removed.isEmpty())
				return null;
			ObjectDifference difference = new ObjectDifference(fieldName, added, modified, removed);
			return difference;
		}
	}

	private String getIdField(List list1, List list2, Map<String, String> idFieldMap)
			throws Exception {
		int i = 0;
		Object value1 = i < list1.size() ? list1.get(i) : null;
		Object value2 = i < list2.size() ? list2.get(i) : null;
		return getIdField(value1, value2, idFieldMap);
	}

	private String getIdField(Object value1, Object value2, Map<String, String> idFieldMap)
			throws Exception {
		if (value1 != null) {
			return idFieldMap.get(value1.getClass().getName());
		}
		if (value2 != null) {
			return idFieldMap.get(value2.getClass().getName());
		}
		return null;
	}
	
	private String getIdValue(Object value,String[] idFields) {
		StringBuffer sb = new StringBuffer();
		for(String idField : idFields) {
			if(sb.length()>0)
				sb.append("-");
			Object idValue = ToolUtilities.getFieldValue(value, idField);
			sb.append(""+idValue);
		}
		return sb.toString();
	}

	private List<String> getIgnoreFields(List list1, List list2,
			Map<String, List<String>> ignoreFieldMap) throws Exception {
		int i = 0;
		Object value1 = i < list1.size() ? list1.get(i) : null;
		Object value2 = i < list2.size() ? list2.get(i) : null;
		return getIgnoreFields(value1, value2, ignoreFieldMap);
	}

	private List<String> getIgnoreFields(Object value1, Object value2,
			Map<String, List<String>> ignoreFieldMap) throws Exception {
		int i = 0;
		if (value1 != null) {
			List<String> list = ignoreFieldMap.get(value1.getClass().getName());
			if(list == null)
				return Collections.emptyList();
			return list;
		}
		if (value2 != null) {
			List<String> list =  ignoreFieldMap.get(value2.getClass().getName());
			if(list == null)
				return Collections.emptyList();
			return list;
		}
		return Collections.emptyList();
	}
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
			Map<String, String> idFieldMap, Map<String, List<String>> ignoreCompareFieldMap) throws Exception {
		Set<String> keySet = new LinkedHashSet<>(map1.keySet());
		keySet.addAll(map2.keySet());
		Map<String,Object> addedMap = new LinkedHashMap<>();
		Map<String,Object> removedMap = new LinkedHashMap<>();
		List<ObjectDifference> modified = new ArrayList<>();
		for (String key : keySet) {
			Object value1 = map1.get(key);
			Object value2 = map2.get(key);
			if(value1 == null && value2 == null)
				continue;
			if(value1 == null) {
				addedMap.put(key,value2);
				continue;
			}
			if(value2 == null) {
				removedMap.put(key,value1);
				continue;
			}
			ObjectDifference objDiffs = compare(value1, value2, idFieldMap,
					ignoreCompareFieldMap);
			if(objDiffs != null) {
				objDiffs.setField("["+key+"]");
				modified.add(objDiffs);
			}
		}
		List<ObjectDifference> added = new ArrayList<>();
		for(String key : addedMap.keySet()) {
			ObjectDifference addDiff = new ObjectDifference("["+key+"]", null, addedMap.get(key));
			added.add(addDiff);
		}
		List<ObjectDifference> removed = new ArrayList<>();
		for(String key : removedMap.keySet()) {
			ObjectDifference removeDiff = new ObjectDifference("["+key+"]", null, removedMap.get(key));
			removed.add(removeDiff);
		}
		if(added.isEmpty() && modified.isEmpty() && removed.isEmpty())
			return null;
		ObjectDifference difference = new ObjectDifference(fieldName, added, modified, removed);
		return difference;
	}

	public static void main(String[] args) {
		PreloadTreeNode<ObjectDifference> p1 = new PreloadTreeNode<>();
		PreloadTreeNode<ObjectDifference> p2 = new PreloadTreeNode<>();
		try {
			Map<String,String> idFieldMap = new LinkedHashMap<>();
			idFieldMap.put(PreloadTreeNode.class.getName(), "id");
			Map<String,List<String>> ignoreFieldMap = new LinkedHashMap<>();
			ignoreFieldMap.put(PreloadTreeNode.class.getName(), Arrays.asList("children"));
			ICompareObject.get().compare(p1, p2, idFieldMap, ignoreFieldMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
