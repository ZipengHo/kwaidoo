package gpf.study.fe.component.param;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import gpf.dc.basic.fe.component.param.BaseViewParam;
@ClassDeclare(label = "自定义视图组件参数代码样例"
,what="演示如何实现一个自定义视图组件参数代码，具体按照组件参数上需要哪些入参进行定义，要求数据可序列化"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public class StudyCustomViewParam extends BaseViewParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8727035768637524643L;
	
	@FieldDeclare(label = "文本参数",desc = "")
	String text;
	
	public String getText() {
		return text;
	}
	public StudyCustomViewParam setText(String text) {
		this.text = text;
		return this;
	}

}
