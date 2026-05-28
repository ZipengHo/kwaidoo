package cell.gpf.study.observer;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.intf.OperateStatusChangeObserver;
@ClassDeclare(label = "节点操作状态变更监听样例"
,what="节点操作状态变更监听样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-24"
,updateTime = "2025-01-24")
//节点状态变更监听需要实现 OperateStatusChangeObserver接口
public interface IStudyOperateStatusChangeObserver extends CellIntf,OperateStatusChangeObserver{

}
