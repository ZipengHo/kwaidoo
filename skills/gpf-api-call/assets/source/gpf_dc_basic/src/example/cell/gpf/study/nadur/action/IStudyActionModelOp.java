package cell.gpf.study.nadur.action;

import java.util.ArrayList;
import java.util.List;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.study.action.IStudyBaseAction;
import cmn.anotation.ClassDeclare;
import cmn.dto.Progress;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import gpf.adur.action.ActionModel;
import gpf.adur.action.ParamMapping;
import gpf.enums.CfgRunSysVariable;
@ClassDeclare(label = "动作模型操作代码样例"
,what="动作模型操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyActionModelOp extends CellIntf{
	
	static IStudyActionModelOp get() {
		return Cells.get(IStudyActionModelOp.class);
	}
	
	default void testActionModelOp()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		String rootModelId = IActionMgr.get().getRootActionModelId();
		ActionModel testModel = IActionMgr.get().queryActionModel(StudyActionMockConst.ModelId);
		if(testModel == null) {
			String javaClassPath = IStudyBaseAction.class.getName();
			List<ParamMapping> paramMappings = new ArrayList<>();
			ParamMapping paramMapping = new ParamMapping().setJavaArgumentName("rtx").setInputValue(CfgRunSysVariable.$sysvar_context$.name());
			paramMappings.add(paramMapping);
			//自定义属性使用"$var_"+field.getCode()+"$"作为输入值
//			ParamMapping paramMapping2 = new ParamMapping().setJavaArgumentName("param").setInputValue("$var_"+field.getCode()+"$");
//			paramMappings.add(paramMapping2);
			testModel = new ActionModel();
			testModel.setParentId(rootModelId);//父模型ID
			testModel.setName(StudyActionMockConst.ModelName)//模型名称
			.setLabel(StudyActionMockConst.ModelLabel)//模型中文名
			.setPackagePath(StudyActionMockConst.PackagePath);//包路径
			testModel.setJavaClassPath(javaClassPath);
			testModel.setParamMappings(paramMappings);
			//构建属性与表单操作一样，具体见IStudyFormModelOp;
			testModel = IActionMgr.get().createActionModel(testModel);
		}
		Progress prog = null;
		IActionMgr.get().updateActionModel(prog, testModel);
	}
	
	
	default void deleteActionModel()throws Exception{
		String formModelID = StudyActionMockConst.ModelId;
		IActionMgr.get().deleteActionModel(formModelID);
	}
	
	default void queryActionModelPage()throws Exception{
		List<String> parentIds = CollUtil.newArrayList(IActionMgr.get().getActionModelRoot().getId());
		String packagePath = null;
		String keyword = null;
		int pageNo = 1;
		int pageSize = 20;
		IActionMgr.get().queryActionModelPage(parentIds, packagePath, keyword, pageNo, pageSize);
	}
}
