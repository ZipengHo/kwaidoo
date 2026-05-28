package gpf.adur.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cmn.anotation.ClassDeclare;
/**
 * 结果集
 * @author chenxb
 *
 * @param <T>
 */
@ClassDeclare(
	    label = "数据分页结果集",
	    what = "查询数据分页结果时使用的分页结果集对象\r\n",
	    why = "",
	    how = "",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class ResultSet<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5743347515603602301L;
	/**
	 * SQL查询时的总记录数字段名
	 */
	public final static String TotalCount = "totalcount";
	/**
	 * SQL查询时的总记录数查询表达式
	 */
	public final static String TotalCount_Select = "count(1) over() as totalcount";
	int totalCount;
	List<T> dataList = new ArrayList<>();

	public static <T>  ResultSet<T> NEW(List<T> dataList,int totalCount){
		return new ResultSet<T>().setTotalCount(totalCount).setDataList(dataList);
	}
	/**
	 * 获取总记录数
	 * @return
	 */
	public int getTotalCount() {
		return totalCount;
	}
	public ResultSet<T> setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}
	
	public boolean isEmpty() {
		return dataList == null ? false : dataList.isEmpty(); 
	}
	/**
	 * 当前结果集的记录数
	 * @return
	 */
	public int getSize() {
		return dataList == null ? 0 : dataList.size();
	}
	/**
	 * 数据列表
	 * @return
	 */
	public List<T> getDataList() {
		return dataList;
	}
	public ResultSet<T> setDataList(List<T> dataList) {
		this.dataList = dataList;
		return this;
	}
	
	

}
