package cell.gpf.study.observer;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.intf.ActionModelOpObserver;
import gpf.dc.intf.CDCOpObserver;
import gpf.dc.intf.FormModelOpObserver;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.intf.OrgModelOpObserver;
import gpf.dc.intf.PDFFormOpObserver;
import gpf.dc.intf.PDFOpObserver;
import gpf.dc.intf.UserModelOpObserver;
@ClassDeclare(label = "GPF数据操作监听样例"
,what="GPF数据操作监听样例"
, why = ""
, how = "实现相应数据操作监听接口添加自定义的处理逻辑"
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-24"
,updateTime = "2025-01-24")
public interface IStudyGpfDataOpObserver extends CellIntf,FormModelOpObserver,FormOpObserver,ActionModelOpObserver,UserModelOpObserver,OrgModelOpObserver,CDCOpObserver,PDFOpObserver,PDFFormOpObserver{

	static IStudyGpfDataOpObserver get() {
		return Cells.get(IStudyGpfDataOpObserver.class);
	}
}
