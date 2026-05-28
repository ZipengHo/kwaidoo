package cell.gpf.study.action.view;

import org.nutz.dao.util.cri.SqlExpressionGroup;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import gpf.dc.basic.action.intf.QueryConditionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "数据权限代码样例"
,what="用于表格、列表、树等视图的结果集  数据权限代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyDataPrivilege <T extends BaseFeActionParameter> extends CellIntf,QueryConditionIntf<T>{

	@Override
	default SqlExpressionGroup getSqlExpression(T input) throws Exception {
		// 返回数据权限过滤的条件表达式
		SqlExpressionGroup expr = new SqlExpressionGroup();
		expr.andEquals(IFormMgr.get().getFieldCode("属性名"), true);
		return expr;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
