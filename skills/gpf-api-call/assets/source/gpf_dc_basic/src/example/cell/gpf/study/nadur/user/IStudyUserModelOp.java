package cell.gpf.study.nadur.user;

import java.util.List;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.adur.user.IUserMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import gpf.adur.data.FormModel;
@ClassDeclare(label = "用户模型操作代码样例"
,what="用户模型操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyUserModelOp extends CellIntf{
	
	static IStudyUserModelOp get() {
		return Cells.get(IStudyUserModelOp.class);
	}
	
	default void createFormModel()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		String rootModelId = IUserMgr.get().getRootBasicUserModelId();
		
		FormModel testUserModel = IUserMgr.get().queryUserModel(StudyUserMockConst.ModelId);
		if(testUserModel == null) {
			testUserModel = new FormModel();
			testUserModel.setParentId(rootModelId);//父模型ID
			testUserModel.setName(StudyUserMockConst.ModelName)//模型名称
			.setLabel(StudyUserMockConst.ModelLabel)//模型中文名
			.setPackagePath(StudyUserMockConst.PackagePath);//包路径
			
			testUserModel = IUserMgr.get().createUserModel(testUserModel);
		}
		
		IUserMgr.get().updateUserModel(null, testUserModel);
	}
	
	
	default void deleteFormModel()throws Exception{
		String formModelID = StudyUserMockConst.ModelId;
		IUserMgr.get().deleteUserModel(formModelID);
	}
	
	default void queryUserModelPage()throws Exception{
		List<String> parentIds = CollUtil.newArrayList(IUserMgr.get().getRootBasicUserModelId());
		String packagePath = null;
		String keyword = null;
		int pageNo = 1;
		int pageSize = 20;
		IUserMgr.get().queryUserModelPage(parentIds, packagePath, keyword, pageNo, pageSize);
	}
}
