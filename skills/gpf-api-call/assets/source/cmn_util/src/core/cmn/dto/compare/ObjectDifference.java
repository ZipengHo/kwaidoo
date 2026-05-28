package cmn.dto.compare;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: 对照实体
 * 
 * @Description: 两个对象的差异
 * @author chenxb
 */
public class ObjectDifference implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589856230412055834L;

	public ObjectDifference(String field, Object oldValue, Object newValue) {
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public ObjectDifference(String field,List<ObjectDifference> added,List<ObjectDifference> modified,List<ObjectDifference> removed) {
		this.field = field;
		this.added = added;
		this.modified = modified;
		this.removed = removed;
	}
	
	// 字段
	private String field;
	// 字段旧值
	private Object oldValue;
	// 字段新值
	private Object newValue;
//	//对象的属性差异
//	private List<BeanDifference> objectDifferences;
	//集合的差异，包括新增、删除、修改
	private List<ObjectDifference> added;
	private List<ObjectDifference> modified;
	private List<ObjectDifference> removed;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public List<ObjectDifference> getAdded() {
		return added;
	}

	public void setAdded(List<ObjectDifference> added) {
		this.added = added;
	}

	public List<ObjectDifference> getModified() {
		return modified;
	}

	public void setModified(List<ObjectDifference> modified) {
		this.modified = modified;
	}

	public List<ObjectDifference> getRemoved() {
		return removed;
	}
	public void setRemoved(List<ObjectDifference> removed) {
		this.removed = removed;
	}
//	public List<BeanDifference> getObjectDifferences() {
//		return objectDifferences;
//	}
//	public void setObjectDifferences(List<BeanDifference> objectDifferences) {
//		this.objectDifferences = objectDifferences;
//	}
	
	


//	@Override
//	public String toString() {
//		if(CmnUtil.isCollectionEmpty(subDifferences)) {
//			return String.format("{'field' : '%s','oldValue':'%s','newValue':'%s'}, args)
//		}
//		return super.toString();
//	}

}