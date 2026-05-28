package cell.gpf.study.scheduletask.param;

import java.io.Serializable;

import cmn.anotation.ClassDeclare;
@ClassDeclare(label = "定时任务实现样例参数"
,what="定时任务实现样例参数"
, why = ""
, how = "在定时任务上配置参数 { ParamClass : \"cell.study.scheduletask.param.ActionStudyScheuldTaskParam\" , Param : { inputText : \"这是输入参数\"}} "
,developer="陈晓斌"
,version = "1.0"
,createTime = "205-01-21"
,updateTime = "205-01-21")
public class ActionStudyScheuldTaskParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4822586714249685839L;
	String inputText;
	
	public String getInputText() {
		return inputText;
	}
	public ActionStudyScheuldTaskParam setInputText(String inputText) {
		this.inputText = inputText;
		return this;
	}
}
