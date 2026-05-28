package cell.gpf.study.app;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.app.Context;
import gpf.dc.basic.fe.intf.AppCacheMgrIntf;
@ClassDeclare(label = "应用缓存管理接口实现样例"
,what="应用缓存管理接口实现样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyAppCacheMgr extends CellIntf,AppCacheMgrIntf{

	
	@Override
	default void initCache(Context context) throws Exception {
		//在应用加载时初始化应用缓存
//		setCacheValue(context, key, value);
	}
	
}
