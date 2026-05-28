package cell.gpf.study.nadur.role;

import java.util.List;

import org.nutz.dao.Cnd;

import com.kwaidoo.ms.tool.ToolUtilities;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.role.IRoleMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.role.Org;
@ClassDeclare(label = "组织操作代码样例"
,what="组织操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyOrgOp extends CellIntf{

	static IStudyOrgOp get() {
		return Cells.get(IStudyOrgOp.class);
	}
	
	
	default void testCreateUpdateOrg() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try(IDao dao = IDaoService.newIDao()){
			String orgModelID = StudyOrgMockConst.ModelId;
			String orgUuid = null;
			String path = StudyOrgMockConst.TestOrgPath;
			Org org = IRoleMgr.get().queryOrgByPath(dao, orgModelID, orgUuid, path);
			if(org == null) {
				org = new Org(StudyOrgMockConst.ModelId);
				org.setCode(ToolUtilities.allockUUIDWithUnderline());
				org.setParentUuid(null);
				org.setName(StudyOrgMockConst.TestOrgName);
				org.setLabel(StudyOrgMockConst.TestOrgName);
//				org.setDescription(description);
				org = IRoleMgr.get().createOrg(dao, org);
				tracer.info(org);
			}else {
				org = IRoleMgr.get().updateOrg(dao, org);
			}
			dao.commit();
			
			Cnd cnd = Cnd.where(Org.Name, "=", StudyOrgMockConst.TestOrgName);
			String parentOrgUuid = null;
			List<Org> list = IRoleMgr.get().queryChildOrg(dao, orgModelID, parentOrgUuid, cnd);
			for(Org u : list) {
				tracer.info(u);
			}
		}
	}
	
	
}
