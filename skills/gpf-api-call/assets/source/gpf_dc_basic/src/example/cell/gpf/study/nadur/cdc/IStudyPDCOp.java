package cell.gpf.study.nadur.cdc;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.dc.config.IPDCMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.ResultSet;
import gpf.dc.config.PDC;
@ClassDeclare(label = "PDC操作代码样例"
,what="PDC操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyPDCOp extends CellIntf{

	static IStudyPDCOp get() {
		return Cells.get(IStudyPDCOp.class);
	}
	
	
	default void testPDCOp() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String code = "测试PDC001";
		try(IDao dao = IDaoService.newIDao()){
			PDC pdc = IPDCMgr.get().queryPDCByCode(dao, StudyCDCMockConst.ModelId, code);
			if(pdc == null) {
				pdc = IPDCMgr.get().newPDC(StudyCDCMockConst.ModelId);
				pdc.setCode(code);
				//动作、引用属性、权限配置与CDC一样，具体见IStudyCDCOp
//				pdc.setActionList(actionList);
//				pdc.setRefFieldList(refFieldList);
//				pdc.setPrivilegeSettings(privilegeFunctions);
				
				//设置属性值与数据模型一样，具体见IStudyFormOp
//				pdc.setAttrValue(fieldName, attrValue);
				
				pdc = IPDCMgr.get().createPDC(dao, pdc);
			}else{
				pdc = IPDCMgr.get().updatePDC(dao, pdc);
			}
			Cnd cnd = Cnd.NEW();
			cnd.where(new SqlExpressionGroup().andEquals(PDC.Code, code));
			ResultSet<PDC> rs = IPDCMgr.get().queryPDCPage(dao, StudyCDCMockConst.ModelId, cnd, 1, 20);
			tracer.info("查询记录数："+ rs.getTotalCount());
			dao.commit();
		}
	}
	
	
	
}
