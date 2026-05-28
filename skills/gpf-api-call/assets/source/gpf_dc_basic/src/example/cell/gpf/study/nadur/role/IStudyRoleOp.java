package cell.gpf.study.nadur.role;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.role.IRoleMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Role;
@ClassDeclare(label = "身份、角色操作代码样例"
,what="身份、角色操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyRoleOp extends CellIntf{

	static IStudyRoleOp get() {
		return Cells.get(IStudyRoleOp.class);
	}
	
	
	default void testCreateUpdateUser() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try(IDao dao = IDaoService.newIDao()){
			Role role = IRoleMgr.get().queryRoleByCode(dao, StudyOrgMockConst.TestRoleCode);
			if(role == null) {
				role = new Role();
				role.setCode(StudyOrgMockConst.TestRoleCode).setLabel(StudyOrgMockConst.TestRoleName);
				tracer.info(role);
				IRoleMgr.get().createRole(dao, role);
			}else {
				IRoleMgr.get().updateRole(dao, role);
			}
			dao.commit();
			
			Cnd cnd = Cnd.NEW();
			cnd.and(new SqlExpressionGroup().andIsNull(Role.Owner));//身份数据不携带Owner,只有挂在组织下的角色才带有Owner
			int pageNo = 1;
			int pageSize = 20;
			ResultSet<Role> rs = IRoleMgr.get().queryRolePage(dao, cnd, pageNo, pageSize);
			for(Role r : rs.getDataList()) {
				tracer.info(r);
			}
		}
	}
	
	
}
