package cell.gpf.study.action;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.action.intf.MatchIntf;
import gpf.dc.action.param.BaseActionParameter;
@ClassDeclare(label = "流程匹配策略代码样例"
,what="流程匹配策略代码样例，适用于路由匹配和身份匹配"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyMatchAction <T extends BaseActionParameter> extends CellIntf, MatchIntf<T>{

	@Override
	default boolean isMatch(T input) throws Exception {
		return true;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
