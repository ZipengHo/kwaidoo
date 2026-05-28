package cell.gpf.study.nadur.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.google.common.collect.ImmutableMap;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import gpf.adur.data.AssociationData;
import gpf.adur.data.AttachData;
import gpf.adur.data.Form;
import gpf.adur.data.Password;
import gpf.adur.data.ResultSet;
import gpf.adur.data.TableData;
import gpf.adur.data.WebAttachData;
@ClassDeclare(label = "表单操作代码样例"
,what="表单操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyFormOp extends CellIntf{

	static IStudyFormOp get() {
		return Cells.get(IStudyFormOp.class);
	}
	
	
	default void testSuite() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		createOrUpdateForm(StudyFormMockConst.AssocModelId, StudyFormMockConst.AssocFormCode, true);
		String code = "编号001";
		createOrUpdateForm(StudyFormMockConst.ModelId, code, false);
		try(IDao dao = IDaoService.newIDao()){
			Form form = IFormMgr.get().queryFormByCode(dao, StudyFormMockConst.ModelId, code);
			getAttrValue(form);
			Cnd cnd = Cnd.NEW();
			cnd.where(new SqlExpressionGroup().andEquals(Form.Code, code).andEquals(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), "文本值"));
			ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, StudyFormMockConst.ModelId, cnd, 1, 20);
			tracer.info("查询记录数："+ rs.getTotalCount());
			IFormMgr.get().deleteForm(dao, StudyFormMockConst.ModelId, cnd);
			dao.commit();
		}
	}
	
	default void createOrUpdateForm(String formModelId,String code,boolean onlyBasic) throws Exception {
		try(IDao dao = IDaoService.newIDao()){
			Form form = IFormMgr.get().queryFormByCode(dao, formModelId, code);
			if(form == null) {
				form = new Form(formModelId);
				form.setAttrValueByCode(Form.Code, code);
				setAttrValue(form, false);
				form = IFormMgr.get().createForm(dao, form);
			}else {
				form.setAttrValueByCode(Form.Code, code);
				setAttrValue(form, false);
				form = IFormMgr.get().updateForm(dao, form);
			}
			dao.commit();
		}
	}
	
	default void setAttrValue(Form form,boolean onlyBasic) throws Exception {
		form.setAttrValue(StudyFormMockConst.TextField, "文本值");
		form.setAttrValue(StudyFormMockConst.BooleanField, true);
		form.setAttrValue(StudyFormMockConst.LongField, 100.1f);
		form.setAttrValue(StudyFormMockConst.DoubleField, 100);
		form.setAttrValue(StudyFormMockConst.PasswordField, new Password().setValue("123456"));
		form.setAttrValue(StudyFormMockConst.DateField, System.currentTimeMillis());
		form.setAttrValue(StudyFormMockConst.AttachField, CollUtil.newArrayList(new AttachData("test.txt", "测试附件内容".getBytes())));
		WebAttachData webAttach = IFormMgr.get().uploadWebAttach("test.txt", "测试上传附件".getBytes());
		//大文件可使用输入流方式上传，避免占用太多内存
//		InputStream in = new FileInputStream(new File("./text.txt"));
//		WebAttachData webAttach = IFormMgr.get().uploadWebAttach("test.txt", in);
		List<WebAttachData> lstWebAttach = CollUtil.newArrayList(webAttach);
		form.setAttrValue(StudyFormMockConst.WebAttachField, lstWebAttach);
		
		List<Map<String,String>> keyValues = new ArrayList<>();
		keyValues.add(ImmutableMap.of(Form.KeyValue_Key, "AAA", Form.KeyValue_Label, "BBB",Form.KeyValue_Value , "CCC"));
		form.setAttrValue(StudyFormMockConst.KeyValueField, keyValues);
		if(!onlyBasic) {
			form.setAttrValue(StudyFormMockConst.AssociateField, new AssociationData(StudyFormMockConst.AssocModelId, StudyFormMockConst.AssocFormCode));
			TableData tableData = new TableData(StudyFormMockConst.NestingModelId);
			Form nestingForm = new Form(StudyFormMockConst.NestingModelId);
			setAttrValue(nestingForm, true);
			tableData.add(nestingForm);
			form.setAttrValue(StudyFormMockConst.NestingField, tableData);
		}
		
	}
	
	default void setAttrValue(Map<String,Object> valueMap,boolean onlyBasic) throws Exception {
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), "文本值");
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.BooleanField), true);
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.LongField), 100L);
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.DoubleField), 100D);
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.PasswordField), new Password().setValue("123456"));
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.DateField), System.currentTimeMillis());
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.AttachField), CollUtil.newArrayList(new AttachData("test.txt", "测试附件内容".getBytes())));
		WebAttachData webAttach = IFormMgr.get().uploadWebAttach("test.txt", "测试上传附件".getBytes());
		//大文件可使用输入流方式上传，避免占用太多内存
//		InputStream in = new FileInputStream(new File("./text.txt"));
//		WebAttachData webAttach = IFormMgr.get().uploadWebAttach("test.txt", in);
		List<WebAttachData> lstWebAttach = CollUtil.newArrayList(webAttach);
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.WebAttachField), lstWebAttach);
		
		List<Map<String,String>> keyValues = new ArrayList<>();
		keyValues.add(ImmutableMap.of(Form.KeyValue_Key, "AAA", Form.KeyValue_Label, "BBB",Form.KeyValue_Value , "CCC"));
		valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.KeyValueField), keyValues);
		if(!onlyBasic) {
			valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.AssociateField), new AssociationData(StudyFormMockConst.AssocModelId, StudyFormMockConst.AssocFormCode));
			TableData tableData = new TableData(StudyFormMockConst.NestingModelId);
			Form nestingForm = new Form(StudyFormMockConst.NestingModelId);
			setAttrValue(nestingForm, true);
			tableData.add(nestingForm);
			valueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.NestingField), tableData);
		}
		
	}
	
	default void getAttrValue(Form form) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String code = form.getStringByCode(Form.Code);
		tracer.info(Form.Code+"="+code);
		String text = form.getString(StudyFormMockConst.TextField);
		tracer.info(StudyFormMockConst.TextField+"="+text);
		Boolean bool = form.getBoolean(StudyFormMockConst.BooleanField);
		tracer.info(StudyFormMockConst.BooleanField+"="+bool);
		Long longValue = form.getLong(StudyFormMockConst.LongField);
		tracer.info(StudyFormMockConst.LongField+"="+longValue);
		Double doubleValue = form.getDouble(StudyFormMockConst.DoubleField);
		tracer.info(StudyFormMockConst.DoubleField+"="+doubleValue);
		Password password = form.getPassword(StudyFormMockConst.PasswordField);
		tracer.info(StudyFormMockConst.PasswordField+"="+password);
		Long dateValue = form.getLong(StudyFormMockConst.DateField);
		tracer.info(StudyFormMockConst.DateField+"="+dateValue);
		List<AttachData> lstAttach = form.getAttachments(StudyFormMockConst.AttachField);
		tracer.info(StudyFormMockConst.AttachField+"="+lstAttach);
		//读取网络附件
		List<WebAttachData> lstWebAttach = form.getWebAttachs(StudyFormMockConst.WebAttachField);
		if(lstWebAttach != null) {
			for(WebAttachData webAttach : lstWebAttach) {
				byte[] content = IFormMgr.get().downloadWebAttach(webAttach.getFileUuid());
				//大文件可通过输入流方式下载，避免占用太多内存
	//			InputStream in = IFormMgr.get().openWebAttachStream(webAttach.getFileUuid());
				tracer.info(webAttach.getName()+"="+new String(content));
			}
		}
		List<Map<String,String>> keyValues = form.getPropTable(StudyFormMockConst.KeyValueField);
		if(keyValues != null) {
			for(Map<String,String> keyValue : keyValues) {
				tracer.info(Form.KeyValue_Key+"="+keyValue.get(Form.KeyValue_Key));
				tracer.info(Form.KeyValue_Label+"="+keyValue.get(Form.KeyValue_Label));
				tracer.info(Form.KeyValue_Value+"="+keyValue.get(Form.KeyValue_Value));
			}
		}
		tracer.info(StudyFormMockConst.KeyValueField+"="+keyValues);
		AssociationData assocData = form.getAssociation(StudyFormMockConst.AssociateField);
		tracer.info(StudyFormMockConst.AssociateField+"="+assocData);
		TableData tableData = form.getTable(StudyFormMockConst.NestingField);
		tracer.info(StudyFormMockConst.NestingField+"="+tableData);
	}
}
