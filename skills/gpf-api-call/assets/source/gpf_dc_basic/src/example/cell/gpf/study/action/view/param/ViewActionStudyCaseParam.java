package cell.gpf.study.action.view.param;

import org.nutz.dao.entity.annotation.Comment;

import gpf.dc.basic.param.view.BaseFeActionParameter;

public class ViewActionStudyCaseParam extends BaseFeActionParameter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5747757084293627729L;
	@Comment("代码")
	String javaClass;
	
	public String getJavaClass() {
		return javaClass;
	}
	public ViewActionStudyCaseParam setJavaClass(String javaClass) {
		this.javaClass = javaClass;
		return this;
	}
}
