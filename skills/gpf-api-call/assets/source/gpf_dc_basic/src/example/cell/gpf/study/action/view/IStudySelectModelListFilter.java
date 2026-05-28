package cell.gpf.study.action.view;

import org.nutz.dao.Cnd;

import cell.CellIntf;
import cell.cdao.IDao;
import cmn.anotation.ClassDeclare;
import gpf.adur.user.User;
import gpf.dc.basic.action.intf.CustomQueryIntf;
import gpf.dc.basic.param.view.CustomQueryParameter;
@ClassDeclare(label = "关联列表数据过滤样例"
,what="关联列表数据过滤，演示关联属性作为关联列表过滤式的代码样例，"
		+ "可返回SQL表达式、Cnd、ResultSet三种,"
		+ "返回Cnd时将替换当前搜索条件，返回ResultSet则直接使用结果集"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-18"
,updateTime = "2025-02-18")
public interface IStudySelectModelListFilter <T extends CustomQueryParameter> extends CellIntf, CustomQueryIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		IDao dao = input.getRtx().getDao();
		//关联模型ID
		String modelId = input.getRtx().getPdfUuid();
		//界面构建完成的查询条件
		Cnd cnd = input.getCnd();
		int pageNo = input.getPageNo();
		int pageSize = input.getPageSize();
		//1.返回SQL表达式，将在cnd中拼接当前表达式
//		SqlExpressionGroup sqlExpr = new SqlExpressionGroup().andEquals("属性1", "匹配值");
//		return sqlExpr;
		//2.返回Cnd，将替换cnd作为查询条件
		if(cnd == null)
			cnd = Cnd.NEW();
		cnd.and(User.FullName, "=", "人事");
		cnd.asc(User.FullName);
		return cnd;
		//3.返回结果集，将使用当前查询结果集
//		ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, modelId, cnd, pageNo, pageSize);
//		return rs;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) CustomQueryParameter.class;
	}
	
}