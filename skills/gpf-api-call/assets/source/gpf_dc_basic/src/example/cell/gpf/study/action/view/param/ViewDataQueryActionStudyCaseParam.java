package cell.gpf.study.action.view.param;

import org.nutz.dao.entity.annotation.Comment;

import gpf.dc.basic.param.view.CustomQueryParameter;

public class ViewDataQueryActionStudyCaseParam extends CustomQueryParameter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5747757084293627729L;
	@Comment("代码")
	String javaClass;
	
	public String getJavaClass() {
		return javaClass;
	}
	public ViewDataQueryActionStudyCaseParam setJavaClass(String javaClass) {
		this.javaClass = javaClass;
		return this;
	}
}
