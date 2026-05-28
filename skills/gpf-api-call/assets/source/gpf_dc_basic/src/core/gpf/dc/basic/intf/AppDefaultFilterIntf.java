package gpf.dc.basic.intf;

import java.io.Serializable;

import org.nutz.dao.util.cri.SqlExpression;

/**
 * 默认筛选接口，返回一段筛选查询SQL，用于应用下的默认数据过滤
 * @author chenxb
 *
 */
public interface AppDefaultFilterIntf extends Serializable{

	SqlExpression buildDefaultFilter(String formModelId)throws Exception;
}
