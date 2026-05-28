package octo.cm.enums;

import cmn.anotation.ClassDeclare;
import octo.cm.intf.SystemEnumIntf;
@ClassDeclare(
		label = "节点操作触发事件"
		, what = ""
		, why = ""
		, how = ""
		, developer = "陈晓斌"
		,createTime = "2025-05-14"
		, updateTime = "2025-05-14"
		, version = "" 
		)
public enum NodeTriggerEvent implements SystemEnumIntf{
	节点重置前,节点重置后,节点重置通知,节点重置异常,
	节点启动前,节点启动后,节点启动通知,节点启动异常,
	节点提交前,节点提交,节点提交后,节点提交异常,
	节点结束前,节点结束后,节点结束通知,节点结束异常
	,节点异常通知;

	@Override
	public String getValue() {
		return name();
	}

	@Override
	public String getLabel() {
		return name();
	}

	@Override
	public String getDescription() {
		return null;
	}
}
