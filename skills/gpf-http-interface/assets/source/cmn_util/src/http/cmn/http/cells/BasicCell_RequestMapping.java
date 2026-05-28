package cmn.http.cells;

import bap.cells.BasicCell;
import cmn.anotation.ClassDeclare;
import cmn.http.servlet.mapping.RequestMappingContext;
import cmn.http.servlet.mapping.RequestMappingIntf;
@ClassDeclare(
		label = "接口映射实现类继承基类", 
		what = "实现get/set Context接口，携带请求接口的上下文参数", 
		why = "",
		how = "", 
		developer = "陈晓斌", 
		createTime = "2025-04-19", 
		updateTime = "2025-04-19", 
		version = "1.0"
		)
public class BasicCell_RequestMapping extends BasicCell implements RequestMappingIntf{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5308370296722948106L;
	RequestMappingContext context;
	@Override
	public RequestMappingContext getContext() {
		return context;
	}
	@Override
	public void setContext(RequestMappingContext context) {
		this.context = context;
	}
}
