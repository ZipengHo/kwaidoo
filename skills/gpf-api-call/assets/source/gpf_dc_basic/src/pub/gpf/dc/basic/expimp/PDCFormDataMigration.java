package gpf.dc.basic.expimp;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nutz.dao.Cnd;

import com.cdao.model.CDoUser;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.Utils;
import com.leavay.common.util.ZipUtils;

import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.IJson;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.dto.Progress;
import cmn.enums.NestingTableUpdateMode;
import cmn.reflect.TypeToken;
import cmn.util.TraceUtil;
import gpf.adur.data.AssociationData;
import gpf.adur.data.DataType;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.user.User;
import gpf.dc.expimp.ExpImpContext;
import gpf.dc.expimp.FormDataExpImp;
import gpf.dc.i18n.GpfDCI18n;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.runtime.PDCForm;
import gpf.md.process.ProcessForm;
import web.dto.Pair;
/**
 * 流程表单数据迁移工具，可用于不同环境上流程已发生变更的表单数据归档，此部分数据不走流程
 */
public class PDCFormDataMigration extends FormDataExpImp{

	String userModelId;
	String orgModelId;
	String pdfUuid;
	String actionName;
	Map<String,String> userName2UserCodeMap = new LinkedHashMap<>();
	Map<String,String> fullName2UserCodeMap = new LinkedHashMap<>();
	Map<String,FormField> formFieldMapping = new LinkedHashMap<>();

	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getUserModelId() {
		return userModelId;
	}
	public PDCFormDataMigration setUserModelId(String userModelId) {
		this.userModelId = userModelId;
		return this;
	}
	public String getOrgModelId() {
		return orgModelId;
	}
	public PDCFormDataMigration setOrgModelId(String orgModelId) {
		this.orgModelId = orgModelId;
		return this;
	}
	
	public String getPdfUuid() {
		return pdfUuid;
	}
	
	public PDCFormDataMigration setPdfUuid(String pdfUuid) {
		this.pdfUuid = pdfUuid;
		return this;
	}
	
	public Map<String, FormField> getFormFieldMapping() {
		return formFieldMapping;
	}
	
	public PDCFormDataMigration setFormFieldMapping(Map<String, FormField> formFieldMapping) {
		this.formFieldMapping = formFieldMapping;
		return this;
	}

	public void exportData(ExpImpContext context, Progress prog, File exportFolder, String pdfUuid, Cnd cnd)
			throws Exception {
		FormModel formModel = IPDFMgr.get().queryFormModelOfPDF(pdfUuid);
		String formModelId = formModel.getId();
		
		long count = IFormMgr.get().countForm(context.getDao(), formModelId, cnd);
		if(count == 0)
			return;
		int pageSize = 10000;
		int pageNo = 1;
		File tmpFolder = new File(exportFolder,getDataFolder());
		File dataFolder = new File(tmpFolder,formModel.getId().replaceAll("\\.", "_"));
		dataFolder.mkdirs();
		List<String> fields = formModel.getFieldList().stream().filter(v->{
			return !CmnUtil.isStringEqual(v.getCode(), ProcessForm.OperateLogs)
					&& !CmnUtil.isStringEqual(v.getCode(), ProcessForm.HisOperateLogs)
					&& !CmnUtil.isStringEqual(v.getCode(), ProcessForm.CurrentOpStatus);
		}).map(v->v.getCode()).collect(Collectors.toList()); 
		int pageCnt = (int) Math.ceil(((double)count/(double)pageSize));
		IDao dao = context.getDao();
		do {
			ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, formModelId, cnd, pageNo, pageSize, true, true, fields.toArray(new String[0]));
			File pageFolder = new File(dataFolder,"Page_"+pageNo);
			pageFolder.mkdirs();
			prog.sendProcess(pageNo/pageCnt*100, GpfDCI18n.TIPS_EXPORT_FORM_DATA+":["+formModel.getNameText()+"],"+ + pageNo +"/"+pageCnt+"", true);
//			prog.sendProcess(pageNo/pageCnt*100, "导出分页数据:" + pageNo +"/"+pageCnt+"", true);
			exportData(context,prog, pageFolder, formModel, rs.getDataList());
			pageNo++;
		}while(pageNo <= pageCnt);
	}
	
	@Override
	public void preImportPage(ExpImpContext context, Progress prog, File pageFolder) throws Exception {
		
	}

	@Override
	public void importPage(ExpImpContext context, Progress prog, File pageFolder) throws Exception {
		if(CmnUtil.isStringEmpty(userModelId)) {
			throw new Exception("userModelId can not be empty!");
		}
		if(CmnUtil.isStringEmpty(orgModelId)) {
			throw new Exception("orgModelId can not be empty!");
		}
		if(CmnUtil.isStringEmpty(pdfUuid)) {
			throw new Exception("pdfUuid can not be empty!");
		}
		IFormMgr formMgr = IFormMgr.get();
		IPDFMgr pdfMgr = IPDFMgr.get();
		if(!pdfMgr.isPDF(pdfUuid)) {
			throw new Exception(pdfUuid + "不是流程!");
		}
		IDao dao = context.getDao();
		if(userName2UserCodeMap.isEmpty()) {
			ResultSet<User> userRs = IUserMgr.get().queryUserPage(dao, userModelId, null, 1, Integer.MAX_VALUE, false, User.Code,User.UserName,User.FullName);
			for(User user : userRs.getDataList()) {
				userName2UserCodeMap.put(user.getUserName(), user.getCode());
				fullName2UserCodeMap.put(user.getFullName(), user.getCode());
			}
		}
		FormModel formModel = pdfMgr.queryFormModelOfPDF(pdfUuid);
		
		File modelFile = new File(pageFolder,DataFile);
		byte[] content = Utils.getFileBytes(modelFile);
		Type type = new TypeToken<List<Form>>() {}.getType(); 
		try(IJson json = getIJson()){
			String data = new String(content);
			List<Form> list = json.fromJsonByType(data, type);
			if(CmnUtil.isCollectionEmpty(list))
				return;
			//将导入数据转换成当前总表单模型数据
			beforeImport(context,pageFolder,formModel,list);
			String codeContextKey = getImportFormCodeContextKey(list.get(0).getFormModelId());
			List<String> codes = (List<String>) context.getContextMap().get(codeContextKey);
			if(codes == null) {
				codes = new ArrayList<>();
				context.getContextMap().put(codeContextKey, codes);
			}
			for(Form form : list) {
				String code = form.getStringByCode(Form.Code);
				if(!CmnUtil.isStringEmpty(code)) {
					codes.add(code);
				}
			}
			
			FormOpObserver observer = context.getFormOpObserver();
			try {
				int cnt = 0;
				for(Form form : list) {
					prog.sendProcess(cnt++/list.size()*100, GpfDCI18n.TIPS_IMPORT_FORM_DATA + "：" + form, true);
					//如果数据是已经生成的，直接更新总表单数据
					String existUuid = IFormMgr.get().queryFormUuidByCode(context.getDao(), form.getFormModelId(), form.getStringByCode(Form.Code));
					if(existUuid != null) {
						form.setUuid(existUuid);
						IFormMgr.get().updateForm(context.getDao(), form,NestingTableUpdateMode.IncrementUpdate,null,new String[] {ProcessForm.ProcessUuid,ProcessForm.OperateLogs,ProcessForm.CurrentOpStatus,ProcessForm.HisOperateLogs,ProcessForm.Creator},observer);
					}else {
						try(IDao dao2 = IDaoService.newIDao()){
							IPDFRuntimeMgr runtimeMgr = IPDFRuntimeMgr.get();
							IDCRuntimeContext rtx = runtimeMgr.newRuntimeContext();
							rtx.setDao(dao2);
							rtx.setPdfUuid(pdfUuid);
							rtx.setUserModelId(userModelId);
							rtx.setOrgModelId(orgModelId);
							AssociationData creator = form.getAssociation(ProcessForm.Creator);
							if(creator == null) {
								throw new Exception("发起人不能为空");
							}
							rtx.setOperator(creator.getValue());
							rtx.setActionName(actionName);
							PDCForm pdcForm = runtimeMgr.newStartForm(rtx, pdfUuid, false);
							pdcForm.getData().putAll(form.getData());
							runtimeMgr.createPDFInstance(rtx, pdfUuid, pdcForm, observer);
							Form totalForm = rtx.getTotalForm();
							totalForm.setAttrValueByCode(Form.Code, form.getStringByCode(Form.Code));
							IFormMgr.get().updateForm(dao2, totalForm, NestingTableUpdateMode.Nothing, new String[]{Form.Code}, null);
							dao2.commit();
						}
					}
				}
			}catch (Exception e) {
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}else {
					throw new RuntimeException(e);
				}
			}
			//导入完成后再更新form的code
		}
	}
	
	@Override
	public void beforeImport(ExpImpContext context, File pageFolder, FormModel formModel, Form data) throws Exception {
		//设置为新的模型ID
		data.setFormModelId(formModel.getId());
		//清理依赖的流程实例关系
		data.setAttrValueByCode(ProcessForm.ProcessUuid, null);
		//处理用户属性
		for(FormField field : formModel.getFieldList()) {
			DataType dataType = field.getDataTypeEnum();
			if(formFieldMapping.containsKey(field.getName())) {
				FormField orgField = formFieldMapping.get(field.getName());
				if(CmnUtil.isStringEmpty(orgField.getCode())) {
					Object value = data.getAttrValueByCode(field.getCode());
					String defaultValue = field.getDefaultValue();
					if(value == null && !CmnUtil.isStringEmpty(defaultValue)) {
						data.setAttrValueByCode(field.getCode(), defaultValue);
						continue;
					}
				}else {
					DataType orgDataType = orgField.getDataTypeEnum();
					Object value = data.getAttrValueByCode(orgField.getCode());
					if(value != null) {
						if(dataType == orgDataType) {
							data.setAttrValueByCode(field.getCode(), value);
						}else {
							if(orgDataType == DataType.Text && dataType == DataType.Relate
									&& (CmnUtil.isStringEqual(field.getAssocFormModel(), userModelId)
									|| CmnUtil.isStringEqual(field.getAssocFormModel(), CDoUser.class.getName()))
									) {
								//这里要处理
								String userCode = fullName2UserCodeMap.get((String)value);
								if(userCode == null) {
									throw new Exception("未找到属性["+field.getName()+"]的映射用户:[" + value+"],fullName2UserCodeMap = " + fullName2UserCodeMap);
								}
								data.setAttrValueByCode(field.getCode(), new AssociationData(null, userCode));
							}else if(orgDataType == DataType.Text && dataType == DataType.Long){
								value = CmnUtil.getLong(value);
								data.setAttrValueByCode(field.getCode(), value);
							}else if(orgDataType == DataType.Text && dataType == DataType.Decimal){
								value = CmnUtil.getDouble(value);
								data.setAttrValueByCode(field.getCode(), value);
							}else if(orgDataType == DataType.Relate && dataType == DataType.Text){
								AssociationData assocValue = (AssociationData) value;
								data.setAttrValueByCode(field.getCode(), assocValue.getValue());
							}
						}
					}
				}
			}
			if(field.getDataTypeEnum() == DataType.Relate 
					&& (CmnUtil.isStringEqual(field.getAssocFormModel(), userModelId)
					|| CmnUtil.isStringEqual(field.getAssocFormModel(), CDoUser.class.getName()))) {
				
				if(field.isAssocMultiSelect()) {
					List<AssociationData> values = data.getAssociationsByCode(field.getCode());
					if(!CmnUtil.isCollectionEmpty(values)) {
						for(AssociationData assocValue : values) {
							int lastIdx = assocValue.getValue().lastIndexOf("_");
							String userName = assocValue.getValue().substring(lastIdx+1);
							String userCode = userName2UserCodeMap.get(userName);
							if(userCode == null) {
								throw new Exception("未找到用户：" + userName);
							}
							assocValue.setValue(userCode);
						}
						data.setAttrValueByCode(field.getCode(), values);
					}
				}else {
					AssociationData assocValue = data.getAssociationByCode(field.getCode());
					if(assocValue != null) {
						int lastIdx = assocValue.getValue().lastIndexOf("_");
						String userName = assocValue.getValue().substring(lastIdx+1);
						String userCode = userName2UserCodeMap.get(userName);
						if(userCode == null) {
							throw new Exception("未找到用户：" + userName);
						}
						assocValue.setValue(userCode);
						data.setAttrValueByCode(field.getCode(), assocValue);
					}
				}
			}
		}
		super.beforeImport(context, pageFolder, formModel, data);
	}
	
	public static void exportData(String pdfUuid) throws Exception {
		Progress prog = Progress.newTracer(TraceUtil.getCurrentTracer());
		try(IDao dao = IDaoService.newIDao()){
			ExpImpContext context = new ExpImpContext();
			context.setDao(dao);
			PDCFormDataMigration expImpIntf = new PDCFormDataMigration();
			FormModel formModel = IPDFMgr.get().queryFormModelOfPDF(pdfUuid);
			String formModelId = formModel.getId();
			Cnd cnd = null;
			String fileName = "Export_"+ToolUtilities.allockUUIDWithUnderline();
			File exportFolder = new File("./temp",fileName);
			expImpIntf.exportData(context, prog, exportFolder, pdfUuid, cnd);
			File targetFolder = new File(exportFolder,"/FormData/"+formModelId.replaceAll("\\.", "_"));
			byte[] bytes = ZipUtils.zipByte(targetFolder.getAbsolutePath(), "UTF-8");
			prog.finish();
			ToolUtilities.deleteFileFolder(exportFolder);
			Utils.writeFile(new File("./temp","PDFFormData_"+formModel.getName()+".zip"), bytes);
		}
	}
	
	public static List<String> importData(String orgModelId,String userModelId,String pdfUuid,String actionName,Map<String,FormField> formFieldMapping,Pair<String,byte[]> zipFile) throws Exception {
		Progress prog = Progress.newTracer(TraceUtil.getCurrentTracer());
		if(!zipFile.getKey().endsWith(".zip")) {
			throw new Exception(GpfDCI18n.TIPS_PLEASE_UPLOAD_ZIP_FILE);
		}
		FormModel formModel = IPDFMgr.get().queryFormModelOfPDF(pdfUuid);
		String formModelId = formModel.getId();
		File destFolder = null;
		try(IDao dao = IDaoService.newIDao()){
			ExpImpContext context = new ExpImpContext();
			context.setDao(dao);
			context.setFormOpObserver(null);
			PDCFormDataMigration expImpIntf = new PDCFormDataMigration();
			expImpIntf.setOrgModelId(orgModelId).setUserModelId(userModelId).setPdfUuid(pdfUuid)
			.setActionName(actionName);
			expImpIntf.setFormFieldMapping(formFieldMapping);
			File file = new File("./temp",zipFile.getKey());
			Utils.writeFile(file, zipFile.getValue());
			destFolder = new File("./temp",file.getName().replaceAll(".zip", "")+"_"+ToolUtilities.allockUUIDWithUnderline());
			ZipUtils.unzip(file.getAbsolutePath(), destFolder.getAbsolutePath());
			File[] listFiles = destFolder.listFiles();
			File innerFolder = new File(destFolder,"/FormData/"+formModelId.replaceAll("\\.", "_"));
			innerFolder.mkdirs();
			for(File lstFile : listFiles) {
				lstFile.renameTo(new File(innerFolder,lstFile.getName()));
			}
			Progress childProg2 = prog.newChildProgress(0, 99);
			expImpIntf.importData(context, childProg2, destFolder);
			dao.commit();
			prog.finish();
			List<String> codes = (List<String>) context.getContextMap().get(FormDataExpImp.getImportFormCodeContextKey(formModelId));
			if(CmnUtil.isCollectionEmpty(codes)) {
				return Collections.emptyList();
			}else {
				return codes;
			}
		}finally {
			if(destFolder != null)
				ToolUtilities.deleteFileFolder(destFolder);
		}
	}

}
