package gpf.dc.basic.expimp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.Utils;
import com.leavay.common.util.javac.ClassFactory;

import cell.cdao.IDao;
import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cell.function.CConsumer;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.backup.IBackupService;
import cmn.dto.PreloadTreeNode;
import cmn.dto.Progress;
import cmn.util.FlowGraph;
import cmn.util.NullUtil;
import cn.hutool.poi.excel.WorkbookUtil;
import gpf.adur.data.DataType;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Org;
import gpf.adur.role.Role;
import gpf.adur.user.User;
import gpf.dc.basic.dto.excel.org.MountedRoleUserRowDto;
import gpf.dc.basic.dto.excel.org.MountedRoleUserSheetDto;
import gpf.dc.basic.excel.org.MountedRoleAndUserRowHandler;
import gpf.dc.basic.excel.org.MountedRoleAndUserSheetHandler;
import gpf.dc.expimp.ExcelTemplateInfo;
import gpf.dc.expimp.ExcelUtil;
import gpf.dc.expimp.ExpImpContext;
import gpf.dc.expimp.FormDataExcelExpImp;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.util.excel.ExcelReader;
import gpf.dc.util.excel.SheetReader;

public class OrgDataExcelExpImp extends FormDataExcelExpImp {

	public final static String DataFolder = "OrgData";

	public final static String DataFile = "data.xlsx";
	ExcelTemplateInfo templateInfo = new ExcelTemplateInfo("OrgData", "OrgDataV1.0");
	String orgModelId;
	String userModelId;
	public Map<String, String> uuidPathMap = new LinkedHashMap<>();
	public Map<String, Org> pathOrgMap = new LinkedHashMap<>();
	public Map<String, List<MountedRoleUserRowDto>> mountedRoleAndUser = new LinkedHashMap<>();

	public String getDataFolder() {
		return DataFolder;
	}
	
	public String getOrgModelId() {
		return orgModelId;
	}
	
	public OrgDataExcelExpImp setOrgModelId(String orgModelId) {
		this.orgModelId = orgModelId;
		return this;
	}

	public String getUserModelId() {
		return userModelId;
	}

	public OrgDataExcelExpImp setUserModelId(String userModelId) {
		this.userModelId = userModelId;
		return this;
	}

	@Override
	public void exportData(ExpImpContext context, Progress prog, File exportFolder, String formModelId, Cnd cnd)
			throws Exception {
		FormModel formModel = context.getFormModel(formModelId);
		File tmpFolder = new File(exportFolder, getDataFolder());
		File dataFolder = new File(tmpFolder, formModel.getId().replaceAll("\\.", "_"));
		dataFolder.mkdirs();
		ResultSet<Org> rs = IRoleMgr.get().queryOrgPage(context.getDao(), formModelId, cnd, 1, Integer.MAX_VALUE);
		exportData(context, prog, dataFolder, formModel, (List) rs.getDataList());
	}

	@Override
	public void exportData(ExpImpContext context, Progress prog, File exportFolder, FormModel formModel,
			List<Form> list) throws Exception {
		// 处理依赖数据
		prog.setMessage("预处理导出数据", true);
		//将组织数据先按路径排序后再导入
		Map<String,Form> orgUuidMap = new LinkedHashMap<>();
		FlowGraph treeGraph = new FlowGraph();
		for(Form form : list) {
			orgUuidMap.put(form.getUuid(), form);
			String parentUuid = form.getStringByCode(Org.ParentUuid);
			if(CmnUtil.isStringEmpty(parentUuid)) {
				treeGraph.addOneNode(form.getUuid());
			}else {
				treeGraph.addRelation(parentUuid, form.getUuid());
			}
		}
		Set<String> children = treeGraph.getFirstLayer();
		List<Form> sortedForms = new ArrayList<>();
		dfsSort(treeGraph, orgUuidMap, children, sortedForms);
		list = sortedForms;
		uuidPathMap = buildUuidPathMap(context.getDao(), formModel.getId(), (List) list);
		
		beforeExport(prog, context, exportFolder, formModel, list);
		List<MountedRoleUserRowDto> mountedRoleAndUsers = new ArrayList<>();
		for (Form data : list) {
			// 组织下的角色、角色和用户的关系也要一起导出
			String orgPath = uuidPathMap.get(data.getUuid());
			List<Role> roles = IRoleMgr.get().queryRoleListOfOrg(context.getDao(), data.getFormModelId(),
					data.getUuid());
			for (Role role : roles) {
				MountedRoleUserRowDto mountedRole = new MountedRoleUserRowDto();
				mountedRole.setOrgName(orgPath).setRoleName(role.getLabel()).setRoleDesc(role.getDescription());
				mountedRoleAndUsers.add(mountedRole);
				if (!CmnUtil.isStringEmpty(userModelId)) {
					List<User> moutedUsers = IRoleMgr.get().queryMountedUserList(context.getDao(), role.getUuid(),
							userModelId);
					for (User user : moutedUsers) {
						MountedRoleUserRowDto mountedUser = new MountedRoleUserRowDto();
						mountedUser.setOrgName(orgPath).setRoleName(role.getLabel()).setRoleDesc(role.getDescription())
								.setUserName(user.getUserName());
						mountedRoleAndUsers.add(mountedUser);
					}
				}
			}
		}

		prog.setMessage("预处理完成", true);

		IBackupService backupService = IBackupService.get();
		FormOpObserver observer = context.getFormOpObserver();
		backupService.exportForms(prog, context.getDao(), (ArrayList<Form>) list, observer,
				CConsumer.NEW(new Consumer<ArrayList<Form>>() {
					public void accept(ArrayList<Form> t) {
						try {
							ExcelUtil util = new ExcelUtil();
							List<String> hiddenFields = getHiddenFields(formModel);
							List<FormField> formFields = formModel.getFieldList().stream()
									.filter(field -> (!hiddenFields.contains(field.getCode())
											&& !field.getCode().equals(Org.Code)
											&& !field.getCode().equals(Org.ParentUuid)
											&& !field.getCode().equals(Org.ParentOrgCode)))
									.collect(Collectors.toList());
							XSSFWorkbook wb = buildTemplateWithHeader(util, formModel.getId(), formModel.getLabel(),
									formFields, templateInfo, false);
							XSSFSheet mainSheet = wb.getSheet(IndexSheetName);
							writeFormToExcel(prog, context, exportFolder, util, formFields, t, wb, mainSheet, null,
									null, null, getMatchWriteUuidModel());
							// 生成
							util.autoColumnWidth(wb);
							MountedRoleUserSheetDto mountedRoleAndUserSheet = new MountedRoleUserSheetDto();
							if(!CmnUtil.isStringEmpty(userModelId)) {
								FormModel userModel = IUserMgr.get().queryUserModel(userModelId);
								mountedRoleAndUserSheet.setUserModelId(userModelId).setUserModelName(userModel.getLabel());
							}
							mountedRoleAndUserSheet.setRows(mountedRoleAndUsers);
							MountedRoleAndUserSheetHandler rowHandler = new MountedRoleAndUserSheetHandler(wb, "角色用户");
							rowHandler.writeData(null, mountedRoleAndUserSheet);
							prog.setMessage("导出数据完成", true);

							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							wb.write(bos);
							wb.close();
							bos.flush();
							Utils.writeFile(new File(exportFolder, DATA_FILE_NAME), bos.toByteArray());

						} catch (Exception e) {
							if (e instanceof RuntimeException) {
								throw (RuntimeException) e;
							} else {
								throw new RuntimeException(e);
							}
						}
					}
				}));

	}
	
	public void dfsSort(FlowGraph treeGraph,Map<String,Form> orgUuidMap,Set<String> children,List<Form> sortedForms) {
		if(!CmnUtil.isCollectionEmpty(children)) {
			for(String child : children) {
				Form form = orgUuidMap.get(child);
				sortedForms.add(form);
				Set<String> grandsons = treeGraph.getTarget(child);
				dfsSort(treeGraph, orgUuidMap, grandsons, sortedForms);
			}
		}
	}

	public XSSFWorkbook buildTemplateWithHeader(ExcelUtil excelUtil, String modelId, String modelLabel,
			List<FormField> formfields, ExcelTemplateInfo templateInfo, boolean showSampleRow) throws Exception {
		// ZipSecureFile.setMinInflateRatio(-1.0d); // 此处需要设置不校验zip boom，该设置为全局，GDF中已处理
		URL url = ClassFactory.getResourceURL("resource/template/MountedRoleUserTemplate.xlsx");
		InputStream ins = url.openStream();
		try {
			File excelFile = new File("./temp/Org_" + ToolUtilities.allockUUIDWithUnderline() + ".xlsx");
			Utils.writeFile(excelFile, Utils.getBytes(ins));
			Workbook wb = WorkbookUtil.createBookForWriter(excelFile);
			XSSFWorkbook workbook = (XSSFWorkbook) wb;
			excelUtil.setBorder(true);
	
			XSSFSheet sheet = workbook.createSheet(IndexSheetName);
			workbook.setSheetOrder(IndexSheetName, 0);
			excelUtil.clearMaxWidth(IndexSheetName);
			buildSheetHeader(excelUtil, modelId, modelLabel, templateInfo, formfields, workbook, sheet, showSampleRow,
					new LinkedHashSet<>());
			return workbook;
		}finally {
			Utils.close(ins);
		}
	}

	public Map<String, String> buildUuidPathMap(IDao dao, String orgModelID, List<Org> orgList) throws Exception {
		Map<String, Org> pathMap = IRoleMgr.get().queryPathOfOrg(dao, orgList);
		Map<String, String> uuidPathMap = new LinkedHashMap<>();
		for (String key : pathMap.keySet()) {
			uuidPathMap.put(pathMap.get(key).getUuid(), key);
		}
		List<String> parentUuids = new ArrayList<>();
		for (Org org : orgList) {
			if (!CmnUtil.isStringEmpty(org.getParentUuid()) && !uuidPathMap.containsKey(org.getParentUuid())) {
				parentUuids.add(org.getParentUuid());
			}
		}
		if (!parentUuids.isEmpty()) {
			Cnd cnd = Cnd.where(new SqlExpressionGroup().andInStrList(Org.ParentUuid, parentUuids));
			ResultSet<Org> rs = IRoleMgr.get().queryOrgPage(dao, orgModelID, cnd, 1, Integer.MAX_VALUE);
			Map<String, Org> parentPathMap = IRoleMgr.get().queryPathOfOrg(dao, rs.getDataList());
			for (String key : parentPathMap.keySet()) {
				uuidPathMap.put(parentPathMap.get(key).getUuid(), key);
			}
		}
		return uuidPathMap;
	}

	@Override
	public void beforeExport(Progress prog, ExpImpContext context, File pageFolder, FormModel formModel, Form data)
			throws Exception {
		for (FormField field : formModel.getFieldList()) {
			if (CmnUtil.isStringEqual(field.getCode(), Org.Name)) {
				String path = uuidPathMap.get(data.getUuid());
				if(CmnUtil.isStringEmpty(path)) {
					prog.setMessage(data.getUuid()+",name = " + data.getStringByCode(Org.Name) + "未找到匹配的组织路径！", true);
					data.setAttrValueByCode(field.getCode(), data.getUuid()+",ParentUuid = "+data.getStringByCode(Org.ParentUuid)+",Name = " + data.getStringByCode(field.getCode()));
				}else {
					String label = data.getStringByCode(Org.Label);
					if(CmnUtil.isStringEmpty(label)) {
						data.setAttrValueByCode(Org.Label, data.getStringByCode(Org.Name));
					}
					data.setAttrValueByCode(field.getCode(), path);
				}
			} else if (CmnUtil.isStringEqual(field.getCode(), Org.ParentOrgCode)
					|| CmnUtil.isStringEqual(field.getCode(), Org.ParentUuid)
					|| CmnUtil.isStringEqual(field.getCode(), Org.Code)) {
				continue;
			} else {
				DataType dataType = field.getDataTypeEnum();
				if (dataType == DataType.Attach) {
					exportFormAttach(context, pageFolder, formModel, data, field);
				} else if (dataType == DataType.WebAttach) {
					exportFormWebAttach(context, pageFolder, formModel, data, field);
				} else if (dataType == DataType.Depend) {
					exportFormDepend(prog, context, pageFolder, formModel, data, field);
				} else if (dataType == DataType.NestingModel) {
					exportFormNestingTableData(prog, context, pageFolder, formModel, data, field);
				} else if (field.getCode().equals("refInstUuid")) {
					exportFormDepend(prog, context, pageFolder, formModel, data, field);
				}
			}
		}

	}

	@Override
	public void beforeImport(ExpImpContext context, File pageFolder, FormModel formModel, Form data) throws Exception {
		for (FormField field : formModel.getFieldList()) {
			if (CmnUtil.isStringEqual(field.getCode(), Org.Name)) {
				String path = data.getStringByCode(Org.Name);
//				Org existOrg = pathOrgMap.get(path);
//				if(existOrg == null) {
//					String code = ToolUtilities.allockUUIDWithUnderline();
//					data.setAttrValueByCode(Form.Code, code);
//					pathOrgMap.put(path, (Org)data);
//				}
				int lastNameIdx = path.lastIndexOf("/");
				String name = path;
				String parentPath = null;
				String parentOrgCode = null;
				String parentOrgUuid = null;
				Org existOrg = pathOrgMap.get(path);
				if(existOrg != null) {
					data.setUuid(existOrg.getUuid());
				}
				if (lastNameIdx > -1) {
					parentPath = path.substring(0,lastNameIdx);
					if(!CmnUtil.isStringEmpty(parentPath)) {
						Org parentOrg = pathOrgMap.get(parentPath);
						if (parentOrg == null) {
							throw new RuntimeException("父组织[" + parentPath + "]不存在！");
						}
						parentOrgCode = parentOrg.getCode();
						parentOrgUuid = parentOrg.getUuid();
					}
					name = path.substring(lastNameIdx + 1);
				}
				if(CmnUtil.isStringEmpty(name)) {
					throw new Exception("组织名称不能为空！组织编号：" + data.getStringByCode(Form.Code));
				}
				data.setAttrValueByCode(field.getCode(), name);
				data.setAttrValueByCode(Org.ParentOrgCode, parentOrgCode);
				data.setAttrValueByCode(Org.ParentUuid, parentOrgUuid);
			} else if (CmnUtil.isStringEqual(field.getCode(), Org.ParentOrgCode)
					|| CmnUtil.isStringEqual(field.getCode(), Org.ParentUuid)
					|| CmnUtil.isStringEqual(field.getCode(), Org.Code)) {
				continue;
			}
			DataType dataType = field.getDataTypeEnum();
			if (dataType == DataType.Attach) {
				importFormAttach(context, pageFolder, formModel, data, field);
			} else if (dataType == DataType.WebAttach) {
				importFormWebAttach(context, pageFolder, formModel, data, field);
			} else if (dataType == DataType.Binary || dataType == DataType.Image) {
				Object value = data.getAttrValueByCode(field.getCode());
				if (value instanceof String) {
					String content = (String) value;
					if (!CmnUtil.isStringEmpty(content)) {
						try (IJson json = IJsonService.get().getJson()) {
							byte[] bytes = json.fromJson(content, byte[].class);
							data.setAttrValueByCode(field.getCode(), bytes);
						}
					}
				}
			} else if (dataType == DataType.Depend) {
				importFormDepend(context, pageFolder, formModel, data, field);
			} else if (dataType == DataType.NestingModel) {
				importFormNestingTableData(context, pageFolder, formModel, data, field);
			} else if (field.getCode().equals("refInstUuid")) {
				importFormDepend(context, pageFolder, formModel, data, field);
			}
		}
	}

	@Override
	public void preImportPage(ExpImpContext context, Progress prog, File pageFolder) throws Exception {

	}

	@Override
	public void importPage(ExpImpContext context, Progress prog, File pageFolder) throws Exception {
		File modelFile = new File(pageFolder, DATA_FILE_NAME);
		if (!modelFile.exists())
			return;
		byte[] content = Utils.getFileBytes(modelFile);
		InputStream wIn = new ByteArrayInputStream(content);
		Workbook wb = WorkbookFactory.create(wIn);
		ExcelReader excel = new ExcelReader(wb);
		SheetReader sheet = excel.getMainSheet();
		String mainSheetName = IndexSheetName;
		List<String> modelInfo = sheet.getRow(0);
		String modelId = modelInfo.get(1);
		String modelLabel = modelInfo.get(3);
		String templateVersion = modelInfo.get(5);
		if(!CmnUtil.isStringEqual(templateVersion, templateInfo.getRevision())) {
			throw new RuntimeException("模板版本不匹配！当前版本：" + templateInfo.getRevision());
		}
		//校验excel模板版本
		if(CmnUtil.isStringEmpty(orgModelId)) {
			orgModelId = modelId;
		}
		FormModel model = IFormMgr.get().queryFormModel(orgModelId);
		Map<String, FormField> formFieldMap = new LinkedHashMap<>();
		for (FormField formField : model.getFieldList()) {
			if (CmnUtil.isStringEmpty(formField.getName()))
				continue;
			formFieldMap.put(formField.getName(), formField);
		}
		String codeContextKey = getImportFormCodeContextKey(orgModelId);
		List<String> codes = (List<String>) context.getContextMap().get(codeContextKey);
		if(codes == null) {
			codes = new ArrayList<>();
			context.getContextMap().put(codeContextKey, codes);
		}
		List<String> header = sheet.getRow(1);
		IDao dao = context.getDao();
		List<Form> list = new ArrayList<>();
		Progress prog1 = prog.newChildProgress(0, 50);
		IRoleMgr roleMgr = IRoleMgr.get();
		// 查询当前组织模型下已有的组织路径和code的对应关系
		ResultSet<Org> orgRs = roleMgr.queryOrgPage(dao, orgModelId, null, 1, Integer.MAX_VALUE);
		pathOrgMap = roleMgr.queryPathOfOrg(dao, orgRs.getDataList());

		for (int rowIndex = 2; rowIndex < sheet.getMaxRowCnt(); rowIndex++) {
			Progress childProg = prog1.newChildProgress(rowIndex, rowIndex + 1, sheet.getMaxRowCnt());
			Org form = new Org(orgModelId);
			readDataFromRow(prog1, context, pageFolder, excel, mainSheetName, rowIndex, header, formFieldMap, form);
			childProg.setMessage("从工作表[" + mainSheetName + "]页读取行[" + rowIndex + "]数据[" + form.getFormModelId() + ":"
					+ form.getStringByCode(Form.Code) + "]", true);
			list.add(form);
			String code = form.getStringByCode(Form.Code);
			if(!CmnUtil.isStringEmpty(code)) {
				codes.add(code);
			}
			childProg.finish();
		}
		MountedRoleAndUserSheetHandler mountedRoleAndUserRowHandler = new MountedRoleAndUserSheetHandler(wb, "角色用户");
		MountedRoleUserSheetDto mountedRoleAndUserSheet = (MountedRoleUserSheetDto) mountedRoleAndUserRowHandler.readData();
		if(CmnUtil.isStringEmpty(userModelId)) {
			userModelId = mountedRoleAndUserSheet.getUserModelId();
		}
		List<MountedRoleUserRowDto> mountedRoleAndUserRows = mountedRoleAndUserSheet.getRows();
		Map<String, List<MountedRoleUserRowDto>> mountedRoleAndUserMap = new LinkedHashMap<>();
		for(MountedRoleUserRowDto row : mountedRoleAndUserRows) {
			if(!mountedRoleAndUserMap.containsKey(row.getOrgName())) {
				mountedRoleAndUserMap.put(row.getOrgName(), new ArrayList<>());
			}
			mountedRoleAndUserMap.get(row.getOrgName()).add(row);
		}
		for (Form form : list) {
			String path = form.getStringByCode(Org.Name);
			if (!pathOrgMap.containsKey(path)) {
				String code = ToolUtilities.allockUUIDWithUnderline();
				form.setAttrValueByCode(Form.Code, code);
				pathOrgMap.put(path, (Org) form);
			}else {
				String code = pathOrgMap.get(path).getStringByCode(Form.Code);
				form.setAttrValueByCode(Form.Code, code);
			}
		}
		Map<String, String> orgCode2PathMap = new LinkedHashMap<>();
		for (String path : pathOrgMap.keySet()) {
			orgCode2PathMap.put(pathOrgMap.get(path).getCode(), path);
		}
		for (Form form : list) {
			beforeImport(context, pageFolder, model, form);
		}
		//将组织数据先按路径排序后再导入
		Map<String,Form> orgUuidMap = new LinkedHashMap<>();
		FlowGraph treeGraph = new FlowGraph();
		for(Form form : list) {
			orgUuidMap.put(form.getUuid(), form);
			String parentUuid = form.getStringByCode(Org.ParentUuid);
			if(CmnUtil.isStringEmpty(parentUuid)) {
				treeGraph.addOneNode(form.getUuid());
			}else {
				treeGraph.addRelation(parentUuid, form.getUuid());
			}
		}
		Set<String> children = treeGraph.getFirstLayer();
		List<Form> sortedForms = new ArrayList<>();
		dfsSort(treeGraph, orgUuidMap, children, sortedForms);
		Progress prog2 = prog.newChildProgress(51, 100);
		IBackupService backupService = IBackupService.get();
		FormOpObserver observer = context.getFormOpObserver();
		backupService.importForms(prog, context.getDao(), (ArrayList<Form>) sortedForms, observer,
				CConsumer.NEW(new Consumer<ArrayList<Form>>() {
					public void accept(ArrayList<Form> t) {
						try {
							for (int i = 0; i < t.size(); i++) {
								Form form = t.get(i);
								String path = orgCode2PathMap.get(form.getStringByCode(Form.Code));
								List<MountedRoleUserRowDto> mountedRoleAndUsers = mountedRoleAndUserMap.get(path);
								Progress childProg = prog2.newChildProgress(i, i + 1, t.size());
								childProg.setMessage(
										"导入数据[" + form.getFormModelId() + ":" + form.getStringByCode(Form.Code) + "]",
										true);
								_doImportForm(dao, orgModelId, mountedRoleAndUsers, form, context);
								childProg.finish();
							}
						} catch (Exception e) {
							if (e instanceof RuntimeException) {
								throw (RuntimeException) e;
							} else {
								throw new RuntimeException(e);
							}
						}
					}
				}));
//			dao.commit();
//		}
	}

	public Form _doImportForm(IDao dao, String modelId, List<MountedRoleUserRowDto> mountedRoleAndUsers, Form form,
			ExpImpContext context) throws Exception {
		String code = form.getStringByCode("code");
		if (CmnUtil.isStringEmpty(code))
			throw new Exception("编号不能为空！");
		FormOpObserver observer = context.getFormOpObserver();
		String orgCode = form.getStringByCode(Form.Code);
		String existUuid = IRoleMgr.get().queryOrgUuidByCode(context.getDao(), form.getFormModelId(), orgCode);
		if(!CmnUtil.isStringEmpty(existUuid)) {
			form.setUuid(existUuid);
			form = IRoleMgr.get().updateOrg(null, dao, (Org) form, observer);
		}else {
			form = IRoleMgr.get().createOrg(null, dao, (Org) form, observer);
		}
		
		// 更新组织下的角色和用户
		List<Role> existRoles = IRoleMgr.get().queryRoleListOfOrg(dao, form.getFormModelId(), form.getUuid());
		Map<String, Role> existRoleMap = existRoles.stream()
				.collect(Collectors.toMap(Role::getLabel, v -> v, (e, r) -> e));
		Map<String, Role> roleMap = new LinkedHashMap<>();
		Map<String, List<String>> role2UserNameMap = new LinkedHashMap<>();
		for (MountedRoleUserRowDto mountedRoleAndUser : NullUtil.get(mountedRoleAndUsers)) {
			String rolePath = mountedRoleAndUser.getOrgName() + "->" + mountedRoleAndUser.getRoleName();
			Role role = new Role();
			role.setLabel(mountedRoleAndUser.getRoleName()).setDescription(mountedRoleAndUser.getRoleDesc());
			if (CmnUtil.isStringEmpty(mountedRoleAndUser.getUserName())) {
				Role existRole = existRoleMap.get(role.getLabel());
				if (existRole != null) {
					role.setUuid(existRole.getUuid());
					IRoleMgr.get().updateRole(context.getDao(), role);
				} else {
					role = IRoleMgr.get().createRole(dao, form.getFormModelId(), form.getUuid(), role);
				}
				roleMap.put(rolePath, role);
			} else {
				if (!role2UserNameMap.containsKey(rolePath)) {
					role2UserNameMap.put(rolePath, new ArrayList<>());
				}
				role2UserNameMap.get(rolePath).add(mountedRoleAndUser.getUserName());
			}
		}
		for (String rolePath : role2UserNameMap.keySet()) {
			List<String> userNames = role2UserNameMap.get(rolePath);
			Role role = roleMap.get(rolePath);
			Cnd cnd = Cnd.NEW().and(new SqlExpressionGroup().andInStrList(User.UserName, userNames));
			ResultSet<User> userRs = IUserMgr.get().queryUserPage(context.getDao(), userModelId, cnd, 1,
					Integer.MAX_VALUE, false, User.UUID);
			if (!userRs.isEmpty()) {
				List<String> userUuids = userRs.getDataList().stream().map(v -> v.getUuid())
						.collect(Collectors.toList());
				IRoleMgr.get().mountRoleToUser(context.getDao(), role.getUuid(), userModelId, userUuids);
			}
		}
		return form;
	}
}
