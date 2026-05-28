package cell.gpf.study.progress.param;

import org.nutz.dao.entity.annotation.Comment;

import gpf.dc.action.param.BaseActionParameter;

public class ActionStudyCaseParam extends BaseActionParameter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5747757084293627729L;
	@Comment("代码")
	String javaClass;
	
	public String getJavaClass() {
		return javaClass;
	}
	public ActionStudyCaseParam setJavaClass(String javaClass) {
		this.javaClass = javaClass;
		return this;
	}
}
