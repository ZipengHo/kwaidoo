package cell.gpf.study.nadur.role;

import java.util.List;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.adur.role.IRoleMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import gpf.adur.data.FormModel;
@ClassDeclare(label = "组织模型操作代码样例"
,what="组织模型操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyOrgModelOp extends CellIntf{
	
	static IStudyOrgModelOp get() {
		return Cells.get(IStudyOrgModelOp.class);
	}
	
	default void createFormModel()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		String rootModelId = IRoleMgr.get().getRootOrgModelId();
		
		FormModel testOrgModel = IRoleMgr.get().queryOrgModel(StudyOrgMockConst.ModelId);
		if(testOrgModel == null) {
			testOrgModel = new FormModel();
			testOrgModel.setParentId(rootModelId);//父模型ID
			testOrgModel.setName(StudyOrgMockConst.ModelName)//模型名称
			.setLabel(StudyOrgMockConst.ModelLabel)//模型中文名
			.setPackagePath(StudyOrgMockConst.PackagePath);//包路径
			
			testOrgModel = IRoleMgr.get().createOrgModel(testOrgModel);
		}
		
		IRoleMgr.get().updateOrgModel(null, testOrgModel);
	}
	
	
	default void deleteOrgModel()throws Exception{
		String formModelID = StudyOrgMockConst.ModelId;
		IRoleMgr.get().deleteOrgModel(formModelID);
	}
	
	default void queryOrgModelPage()throws Exception{
		List<String> parentIds = CollUtil.newArrayList(IRoleMgr.get().getRootOrgModelId());
		String packagePath = null;
		String keyword = null;
		int pageNo = 1;
		int pageSize = 20;
		IRoleMgr.get().queryOrgModelPage(parentIds, packagePath, keyword, pageNo, pageSize);
	}
}
