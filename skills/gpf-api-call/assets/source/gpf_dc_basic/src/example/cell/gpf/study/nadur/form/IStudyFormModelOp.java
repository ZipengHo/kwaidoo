package cell.gpf.study.nadur.form;

import java.util.List;

import com.kwaidoo.ms.tool.ToolUtilities;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.dto.Progress;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import gpf.adur.data.DataType;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.Password;
import gpf.adur.data.TableData;
import gpf.dc.basic.dto.view.PDCFormViewDto;
import gpf.dc.basic.field.extend.NestingFormExtend;
@ClassDeclare(label = "表单模型操作代码样例"
,what="表单模型操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyFormModelOp extends CellIntf{
	
	static IStudyFormModelOp get() {
		return Cells.get(IStudyFormModelOp.class);
	}
	
	default void createFormModel()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		FormModel rootModel = IFormMgr.get().queryFormModel(StudyFormMockConst.RootModelId);
		if(rootModel == null) {
			rootModel = new FormModel();
			rootModel.setParentId(IFormMgr.get().getRootBusinessEntityModelId());//父模型ID
			rootModel.setName(StudyFormMockConst.RootModelName)//模型名称
			.setLabel(StudyFormMockConst.RootModelLabel)//模型中文名
			.setPackagePath(StudyFormMockConst.PackagePath);//包路径
			rootModel = IFormMgr.get().createFormModel(rootModel);
		}
		
		FormModel testAssocDataModel = IFormMgr.get().queryFormModel(StudyFormMockConst.AssocModelId);
		if(testAssocDataModel == null) {
			testAssocDataModel = new FormModel();
			testAssocDataModel.setParentId(rootModel.getId());//父模型ID
			testAssocDataModel.setName(StudyFormMockConst.TestAssocDataModelName)//模型名称
			.setLabel(StudyFormMockConst.TestAssocDataModelLabel)//模型中文名
			.setPackagePath(StudyFormMockConst.PackagePath);//包路径
			buildFormField(testAssocDataModel,true);
			testAssocDataModel = IFormMgr.get().createFormModel(testAssocDataModel);
		}
		FormModel rootNestingModel = IFormMgr.get().queryFormModel(StudyFormMockConst.RootNetingModelId);
		if(rootNestingModel == null) {
			rootNestingModel = new FormModel();
			rootNestingModel.setParentId(IFormMgr.get().getRootNestingEntityModelId());//父模型ID
			rootNestingModel.setName(StudyFormMockConst.RootModelName)//模型名称
			.setLabel(StudyFormMockConst.RootModelLabel)//模型中文名
			.setPackagePath(StudyFormMockConst.NestingPackagePath);//包路径
			rootNestingModel = IFormMgr.get().createFormModel(rootNestingModel);
		}
		
		FormModel testNestingDataModel = IFormMgr.get().queryFormModel(StudyFormMockConst.NestingModelId);
		if(testNestingDataModel == null) {
			testNestingDataModel = new FormModel();
			testNestingDataModel.setParentId(rootNestingModel.getId());//父模型ID
			testNestingDataModel.setName(StudyFormMockConst.TestNestingDataModelName)//模型名称
			.setLabel(StudyFormMockConst.TestNestingDataModelLabel)//模型中文名
			.setPackagePath(StudyFormMockConst.NestingPackagePath);//包路径
			buildFormField(testNestingDataModel,true);
			testNestingDataModel = IFormMgr.get().createFormModel(testNestingDataModel);
		}
		
		FormModel testModel = IFormMgr.get().queryFormModel(StudyFormMockConst.ModelId);
		if(testModel == null) {
			testModel = new FormModel();
			testModel.setParentId(rootModel.getId());//父模型ID
			testModel.setName(StudyFormMockConst.ModelName)//模型名称
			.setLabel(StudyFormMockConst.ModelLabel)//模型中文名
			.setPackagePath(StudyFormMockConst.PackagePath);//包路径
			buildFormField(testModel,false);
			testModel = IFormMgr.get().createFormModel(testModel);
		}
	}
	
	default void buildFormField(FormModel formModel,boolean buildBasic) {
		List<FormField> fields = formModel.getFieldList();
		fields.add(new FormField().setName(StudyFormMockConst.TextField).setDataType(DataType.Text).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.BooleanField).setDataType(DataType.Boolean).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.LongField).setDataType(DataType.Long).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.DoubleField).setDataType(DataType.Decimal).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.PasswordField).setDataType(DataType.Password).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.DateField).setDataType(DataType.Date).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.AttachField).setDataType(DataType.Attach).setDescription("").setNotNull(false));
		fields.add(new FormField().setName(StudyFormMockConst.KeyValueField).setDataType(DataType.KeyValue).setDescription("").setNotNull(false));
		if(!buildBasic) {
			fields.add(new FormField().setName(StudyFormMockConst.AssociateField).setDataType(DataType.Relate).setDescription("").setNotNull(false).setAssocFormModel(StudyFormMockConst.AssocModelId).setAssocMultiSelect(false));
			fields.add(new FormField().setName(StudyFormMockConst.NestingField).setDataType(DataType.NestingModel).setDescription("").setNotNull(false).setTableFormModel(StudyFormMockConst.NestingModelId));
		}
	}
	
	default void udpateFormModel()throws Exception{
		FormModel formModel = IFormMgr.get().queryFormModel(StudyFormMockConst.ModelId);
		Progress prog = null;
		buildFormField(formModel, false);
		IFormMgr.get().updateFormModel(prog, formModel);
	}
	
	default void udpateFormModel2()throws Exception{
		FormModel formModel = IFormMgr.get().queryFormModel("gpf.md.DataTest2");
		List<FormField> fields = formModel.getFieldList();
		for(int i =1;i<=10;i++) {
			FormField field = new FormField();
			field.setName("子表单"+i).setDataType(DataType.NestingModel).setTableFormModel("gpf.md.slave.TestNotNull");
			field.setExtendInfo(new NestingFormExtend().setViewModelId(PDCFormViewDto.FormModelId).setViewCode("测试必填子表"));
			fields.add(field);
		}
		formModel.setFieldList(fields);
		IFormMgr.get().updateFormModel(null, formModel);
	}
	
	default void udpateFormModel3()throws Exception{
		try(IDao dao = IDaoService.newIDao()){
			Form form = IFormMgr.get().queryFormByCode(dao, "gpf.md.DataTest2", "001");
			for(int i =1;i<=10;i++) {
				TableData tableData = new TableData();
					Form form1 = new Form("gpf.md.slave.TestNotNull");
					form1.setAttrValue("AAA", ToolUtilities.allockUUID());
					form1.setAttrValue("BBB", ToolUtilities.allockUUID());
					form1.setAttrValue("整数", 100);
					form1.setAttrValue("小数", 100.0);
					form1.setAttrValue("密码", new Password().setValue("123456"));
					form1.setAttrValue("时间", System.currentTimeMillis());
					tableData.add(form1);
				form.setAttrValue("子表单"+i, tableData);
			}
			IFormMgr.get().updateForm(dao, form);
			dao.commit();
		}
	}
	
	default void deleteFormModel()throws Exception{
		String formModelID = StudyFormMockConst.ModelId;
		IFormMgr.get().deleteFormModel(formModelID);
	}
	
	default void queryFormModel()throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer();
		FormModel formModel = IFormMgr.get().queryFormModel(StudyFormMockConst.ModelId);
		tracer.info(formModel);
	}
	
	default void queryFormModelPage()throws Exception{
		List<String> parentIds = CollUtil.newArrayList(IFormMgr.get().getRootBusinessEntityModelId());
		String packagePath = null;
		String keyword = null;
		int pageNo = 1;
		int pageSize = 20;
		IFormMgr.get().queryFormModelPage(parentIds, packagePath, keyword, pageNo, pageSize);
	}
}
