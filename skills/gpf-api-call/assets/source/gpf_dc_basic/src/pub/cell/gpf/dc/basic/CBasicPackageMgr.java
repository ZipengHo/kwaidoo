package cell.gpf.dc.basic;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.cdao.dto.CPager;
import com.cdao.dto.DataRow;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.Utils;
import com.leavay.common.util.ZipUtils;
import com.leavay.common.util.ProgressCtrl.crpc.COutputProgress;
import com.leavay.common.util.javac.ClassFactory;

import bap.cells.BasicCell;
import bap.cells.Cells;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.model.IDataService;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.model.CModelCacheService;
import cell.gpf.model.ICacheService;
import cell.gpf.model.IModelExtService;
import cmn.dto.Progress;
import cn.hutool.poi.excel.WorkbookUtil;
import gpf.adur.action.ActionModel;
import gpf.adur.data.BaseFormFieldExtend;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.dto.excel.pdf.ActionDefineExcelSheetDto;
import gpf.dc.basic.dto.excel.pdf.DataModelExcelSheetDto;
import gpf.dc.basic.dto.excel.pdf.PDFExcelSheetDto;
import gpf.dc.basic.excel.ActionDefineSheetDtoConvertor;
import gpf.dc.basic.excel.ConvertContext;
import gpf.dc.basic.excel.DataModelSheetDtoConvertor;
import gpf.dc.basic.excel.ModelConvertorIntf;
import gpf.dc.basic.excel.PDFExcelSheetDtoConvertor;
import gpf.dc.basic.excel.pdf.ActionDefineExcelHandler;
import gpf.dc.basic.excel.pdf.DataModelExcelHandler;
import gpf.dc.basic.excel.pdf.PDFExcelHandler;
import gpf.dc.basic.expimp.OrgDataExcelExpImp;
import gpf.dc.basic.field.extend.SelectViewActionCodeExtend;
import gpf.dc.basic.field.extend.SelectViewActionModelExtend;
import gpf.dc.basic.util.GpfDCBasicConst;
import gpf.dc.config.PDF;
import gpf.dc.expimp.ExpImpContext;
import gpf.dc.expimp.FormDataExcelExpImp;
import gpf.dc.intf.FormOpObserver;
import gpf.dto.model.FieldRelation;
import gpf.dto.model.ModelExt;
import gpf.dto.model.relation.ModelDataRelation;
import gpf.dto.model.relation.ModelDataViewInfo;
import web.dto.MultiPageResultDto;
import web.dto.Pair;

public class CBasicPackageMgr extends BasicCell implements IBasicPackageMgr{

	protected IDaoService getDaoService() {
		return Cells.get(IDaoService.class);
	}
	
	@Override
	public ActionModel queryRootViewActionModel() throws Exception {
		ActionModel model = IActionMgr.get().queryActionModel(GpfDCBasicConst.ViewActionModelRootId);
		return model;
	}
	@Override
	public List<ActionModel> queryAllViewActionModel()throws Exception{
		ActionModel rootViewActionModel = queryRootViewActionModel();
		ResultSet<ActionModel> rs = IActionMgr.get().queryActionModelPage(Arrays.asList(rootViewActionModel.getId()), "", null, 1, Integer.MAX_VALUE);
		return rs.getDataList();
	}
	
	public final static String Cache_RelateViewModelReation = "$relateViewModelRelation";
	public static Long relateViewModelRelationTimeTag;
	protected Map<String,List<Pair<FormField, FormField>>> queryRelateViewModelRelationCache()throws Exception{
		Long timeTag = ICacheService.get().getCacheTimeTag(CModelCacheService.CacheBlock_ModelExt);
		if(!Objects.equals(relateViewModelRelationTimeTag, timeTag)) {
			ICacheService.get().notifyCacheDirtyTimeTag(Cache_RelateViewModelReation);
			relateViewModelRelationTimeTag = timeTag;
			return null;
		}
		return ICacheService.get().get(Cache_RelateViewModelReation, Cache_RelateViewModelReation, Map.class);
	}
	protected void cacheAllRelateViewModelRelation(Map<String,List<Pair<FormField, FormField>>> relation)throws Exception{
		ICacheService.get().initCache(Cache_RelateViewModelReation, 1);
		ICacheService.get().put(Cache_RelateViewModelReation, Cache_RelateViewModelReation, (Serializable) relation);
	}
	@Override
	public Map<String,List<Pair<FormField, FormField>>> queryAllRelateViewModelRelation(Progress prog)throws Exception{
		Map<String,List<Pair<FormField, FormField>>> cacheMap = queryRelateViewModelRelationCache();
		if(cacheMap != null)
			return cacheMap;
		Map<String,List<Pair<FormField, FormField>>> map = new LinkedHashMap<>();
		if(prog == null)
			prog = Progress.newOutput();
		prog.setMessage("分析所有模型的关联视图模型和视图编号属性集合。", true);
		List<FormModel> formModels = IFormMgr.get().queryAllFormModel();
		formModels.stream().forEach(v->{
			try {
				analyzeRelateViewModelRelation(map, v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		List<ActionModel> actionModels = IActionMgr.get().queryAllActionModels();
		actionModels.stream().forEach(v->{
			try {
				analyzeRelateViewModelRelation(map, v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		cacheAllRelateViewModelRelation(map);
		return map;
	}
	@Override
	public void analyzeRelateViewModelRelation(Map<String,List<Pair<FormField, FormField>>> map,FormModel formModel)throws Exception{
		for(FormField formField : formModel.getFieldList()) {
			BaseFormFieldExtend fieldExtend = formField.getExtendInfo();
			if(fieldExtend instanceof SelectViewActionModelExtend) {
				for(FormField formField2 : formModel.getFieldList()) {
					BaseFormFieldExtend fieldExtend2 = formField2.getExtendInfo();
					if(fieldExtend2 instanceof SelectViewActionCodeExtend) {
						if(CmnUtil.isStringEqual(
								((SelectViewActionCodeExtend)fieldExtend2).getDependViewModelField(),
								formField.getName())
								){
							if(!map.containsKey(formModel.getId())) {
								map.put(formModel.getId(), new ArrayList<>());
							}
							map.get(formModel.getId()).add(new Pair<FormField, FormField>(formField,formField2));
						}
					}
				}
			}
		}
	}
	@Override
	public boolean isApplicationModel(String modelId)throws Exception{
		return CmnUtil.isStringEqual(modelId, GpfDCBasicConst.ApplicationModelId);
	}
	@Override
	public Map<String,List<String>> queryRelateViewCodes(Progress prog,List<String> viewModelClasses,List<String> relateModelIds) throws Exception{
		if(prog == null)
			prog = Progress.newOutput();
		prog.setMessage("查询模型" + relateModelIds + "的关联视图配置", true);
		Set<String> fields = new LinkedHashSet<>();
		fields.add("code");
		fields.add(GpfDCBasicConst.FieldCode_ModelId);
		Cnd cnd = Cnd.where(new SqlExpressionGroup().andInStrList(GpfDCBasicConst.FieldCode_ModelId, relateModelIds));
		MultiPageResultDto<DataRow> rs = IDataService.get().queryDataRowPageByUnion(viewModelClasses, fields, cnd, new CPager(0, Integer.MAX_VALUE, true));
		Map<String,List<String>> relateViewCodes = new LinkedHashMap<>();
		for(DataRow row : rs.getTableData()) {
			String viewModelClass = row.getString("gid", "");
			String code = row.getString("code", "");
			String modelId = row.getString(GpfDCBasicConst.FieldCode_ModelId.toLowerCase(), "");
			if(!relateViewCodes.containsKey(viewModelClass)) {
				relateViewCodes.put(viewModelClass, new ArrayList<>());
			}
			relateViewCodes.get(viewModelClass).add(code);
		}
		return relateViewCodes;
	}
	@Override
	public Map<String,ModelDataViewInfo> queryEffectModelDataInfoOfView(Progress prog,ModelDataRelation dataRelation,Map<String,List<String>> relateViewCodes,Map<String,List<Pair<FormField,FormField>>> relateViewModels)throws Exception{
		if(prog == null)
			prog = Progress.newOutput();
		if(CmnUtil.isMapEmpty(relateViewCodes))
			return new LinkedHashMap<>();
//		ModelRelation modelRelation = new ModelRelation();
//		ModelDataRelation dataRelation = new ModelDataRelation(modelRelation);
		StringBuffer sb = new StringBuffer();
		for(String viewModelClass : relateViewCodes.keySet()) {
			if(sb.length() > 0)
				sb.append(" UNION ");
			List<String> relateCodes = relateViewCodes.get(viewModelClass);
			ModelExt model = IModelExtService.get().queryByModelClass(viewModelClass, true);
			String dataQuerySql = "select '#viewModelClass#' as viewModelId,#viewTable#.uuid,#viewTable#.#modelId#,#viewTable#.code from #viewTable#";
			Cnd cnd = Cnd.where(new SqlExpressionGroup().andInStrList("code", relateCodes));
			dataQuerySql = dataQuerySql.replaceAll("#viewModelClass#", viewModelClass).replaceAll("#viewTable#", model.getTableName())
					.replaceAll("#modelId#", GpfDCBasicConst.FieldCode_ModelId);
			dataQuerySql += cnd.toString();
			sb.append(dataQuerySql);
		}
		ModelDataViewInfo viewData = dataRelation.addSourceDataView("viewTotal","视图汇总", sb.toString());
		Map<String,String> cteSqls = dataRelation.buildWithSql(viewData.getAlias());
		Map<String,ModelDataViewInfo> totalMap = new LinkedHashMap<>();
		for(String relateModelClass : relateViewModels.keySet()) {
			List<Pair<FormField,FormField>> viewRelatePairs = relateViewModels.get(relateModelClass);
			FormModel formModel = IFormMgr.get().queryFormModel(relateModelClass);
			String relateQuerySql = "select #effectTable#.* from #effectTable#,viewTotal where ";
			StringBuffer relateCnd = new StringBuffer();
			for(Pair<FormField, FormField> pair : viewRelatePairs) {
				if(relateCnd.length() > 0)
					relateCnd.append(" OR " );
				FormField viewModelField = pair.left;
				FormField viewCodeField = pair.right;
				relateCnd.append("( #effectTable#." + viewModelField.getCode() + " = viewTotal.viewModelId and #effectTable#." + viewCodeField.getCode() + " = viewTotal.code)");
			}
			relateQuerySql += relateCnd.toString();
			relateQuerySql = relateQuerySql.replaceAll("#effectTable#", formModel.getTableName());
			if(dataRelation.hasRelateData(cteSqls, relateQuerySql)) {
				prog.setMessage("添加[" + relateModelClass + "]的影响数据查询视图", true);
				ModelDataViewInfo targetView = dataRelation.addTargetDataView("viewTotal", null, relateModelClass, relateQuerySql);
				FieldRelation fieldRelation = new FieldRelation().setSrcModelId("viewTotal").setDstModelId(targetView.getAlias()).setDstField("viewModel").setDstModelId("视图模型");
				targetView.addDependFieldRelation(fieldRelation);
				totalMap.put(targetView.getAlias(), targetView);
			}
		}
		Map<String,ModelDataViewInfo> map = new LinkedHashMap<>(totalMap);
		while(!map.isEmpty()) {
			Map<String,ModelDataViewInfo> tmpMap = new LinkedHashMap<>();
			for(ModelDataViewInfo relateData : map.values()) {
				if(IFormMgr.get().isNestingEntityModel(relateData.getModelClass())) {
					prog.setMessage("查询模型[" + relateData.getModelClass() + "]的影响数据", true);
					Map<String,ModelDataViewInfo> childMap = dataRelation.queryDirectEffectModelDataInfos(relateData.getAlias());
					tmpMap.putAll(childMap);
					totalMap.putAll(childMap);
				}
			}
			map = tmpMap;
		}
		for(Iterator<Entry<String, ModelDataViewInfo>> it = totalMap.entrySet().iterator();it.hasNext();) {
			Entry<String, ModelDataViewInfo> entry = it.next();
			if(IFormMgr.get().isNestingEntityModel(entry.getValue().getModelClass())) {
				it.remove();
			}
		}
		return totalMap;
	}
	
	
	@Override
	public Pair<String, byte[]> exportPDFExcel(Progress prog, String pdfUuid,ModelConvertorIntf convertor) throws Exception {
		PDF pdf = IPDFMgr.get().queryPDF(pdfUuid);
		ConvertContext context = new ConvertContext();
		URL url = ClassFactory.getResourceURL("resource/template/PDFTemplate.xlsx");
		InputStream ins = null;
		Workbook wb = null;
		File destFile = new File("./temp/PDF"+ToolUtilities.allockUUIDWithUnderline()+".xlsx");
		try(IDao dao = getDaoService().newDao()){
			ins = url.openStream();
			byte[] content = getFileBytes(ins);
			Utils.writeFile(destFile, content);
			wb = WorkbookUtil.createBookForWriter(destFile);
			context.setDao(dao);
			if(convertor != null)
				context.setModelConvertor(convertor);
			PDFExcelSheetDtoConvertor pdfConvertor = new PDFExcelSheetDtoConvertor(context);
			PDFExcelSheetDto sheetDto = pdfConvertor.convert2SheetDto(pdf);
			PDFExcelHandler flowHeandler = new PDFExcelHandler(wb);
			flowHeandler.writeSheet(null, sheetDto);
			flowHeandler.flush(destFile);
			DataModelExcelHandler dataModelHandler = new DataModelExcelHandler(wb);
			DataModelSheetDtoConvertor dataModelConvertor = new DataModelSheetDtoConvertor(context);
			DataModelExcelSheetDto dataModelSheet = dataModelConvertor.convertToDataModelExcelSheetDto(pdf);
			dataModelHandler.writeSheet(null, dataModelSheet);
			dataModelHandler.flush(destFile);
			ActionDefineExcelHandler actionHandler = new ActionDefineExcelHandler(wb);
			ActionDefineSheetDtoConvertor actionConvertor = new ActionDefineSheetDtoConvertor(context);
			ActionDefineExcelSheetDto actionSheet = actionConvertor.convertToActionDefineExcelSheetDto(pdf);
			actionHandler.writeSheet(null, actionSheet);
			actionHandler.flush(destFile);
			return new Pair<String, byte[]>(pdf.getLabel(), Utils.getFileBytes(destFile));
		}finally {
			Utils.close(ins);
			IOUtils.closeQuietly(wb);
			ToolUtilities.deleteFile(destFile);
		}
	}
	
	public static byte[] getFileBytes(InputStream is) throws Exception {
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(is);
			out = new ByteArrayOutputStream();
			byte[] bs = new byte[10240];
			int offset = 0;
			while ((offset = in.read(bs, 0, bs.length)) > -1) {
				out.write(bs, 0, offset);
			}
			return out.toByteArray();
		} finally {
			Utils.close(in);
			Utils.close(out);
		}
	}
	
	@Override
	public void importPDFExcel(Progress prog, Pair<String, byte[]> file,ModelConvertorIntf convertor) throws Exception {
		ConvertContext context = new ConvertContext();
		File destFile = new File("./temp/PDF"+ToolUtilities.allockUUIDWithUnderline()+".xlsx");
		Utils.writeFile(destFile, file.right);
		
		Workbook wb = WorkbookUtil.createBookForWriter(destFile);
		try(IDao dao = getDaoService().newDao()){
			context.setDao(dao);
			if(convertor != null)
				context.setModelConvertor(convertor);
			PDFExcelHandler flowHandler = new PDFExcelHandler(wb);
			PDFExcelSheetDtoConvertor pdfConvertor = new PDFExcelSheetDtoConvertor(context);
			PDFExcelSheetDto sheetDto = flowHandler.readSheet();
			PDF pdf = pdfConvertor.convertToPDF(sheetDto);
			DataModelExcelHandler dataModelHandler = new DataModelExcelHandler(wb);
			DataModelSheetDtoConvertor dataModelConvertor = new DataModelSheetDtoConvertor(context);
			DataModelExcelSheetDto dataModelSheet = dataModelHandler.readSheet();
			dataModelConvertor.convertToPDF(pdf, dataModelSheet);
			dataModelHandler.writeSheet(null, dataModelSheet);
			dataModelHandler.flush(destFile);
			ActionDefineExcelHandler actionHandler = new ActionDefineExcelHandler(wb);
			ActionDefineExcelSheetDto actionSheet = actionHandler.readSheet();
			ActionDefineSheetDtoConvertor actionConvertor = new ActionDefineSheetDtoConvertor(context);
			actionConvertor.convert2PDF(actionSheet, pdf);
			actionHandler.writeSheet(null, actionSheet);
			actionHandler.flush(destFile);
			dao.commit();
			IPDFMgr.get().updatePDF(prog, pdf);
		}finally {
			IOUtils.closeQuietly(wb);
			ToolUtilities.deleteFile(destFile);
		}
	}
	
	
	@Override
	public Pair<String, byte[]> exportOrgExcelDatas(Progress prog, String orgModelId, String userModelId,Cnd cnd)
			throws Exception {
		if(prog == null) {
			prog = Progress.newOutput();
		}
		try (IDao dao = IDaoService.newIDao()) {
			ExpImpContext context = new ExpImpContext();
			context.setDao(dao);
			OrgDataExcelExpImp expImpIntf = new OrgDataExcelExpImp();
			expImpIntf.setUserModelId(userModelId).setOrgModelId(orgModelId);
			FormModel orgModel = IRoleMgr.get().queryOrgModel(orgModelId);
			FormModel userModel = IUserMgr.get().queryUserModel(userModelId);
			String fileName = "Export_" + ToolUtilities.allockUUIDWithUnderline();
			File exportFolder = new File("./temp", fileName);
			expImpIntf.exportData(context, prog, exportFolder, orgModelId, cnd);
			File targetFolder = new File(exportFolder, "/OrgData/" + orgModelId.replaceAll("\\.", "_"));
			File[] childFiles = targetFolder.listFiles();
			if (childFiles.length == 1) {
				byte[] bytes = Utils.getFileBytes(childFiles[0]);
				prog.finish();
//		ToolUtilities.deleteFileFolder(exportFolder);
				return new Pair<String, byte[]>("" + orgModel.getLabel() + ".xlsx", bytes);
			} else {
				byte[] bytes = ZipUtils.zipByte(targetFolder.getAbsolutePath(), "UTF-8");
				prog.finish();
//			ToolUtilities.deleteFileFolder(exportFolder);
				return new Pair<String, byte[]>("" + orgModel.getLabel() + ".zip", bytes);
			}
		}
	}
	
	@Override
	public List<String> importOrgExcelDatas(Progress prog, String orgModelId, String userModelId, FormOpObserver observer,Pair<String, byte[]> zipFile) throws Exception {
//		if(CmnUtil.isStringEmpty(orgModelId)) {
//			throw new RuntimeException("组织模型不能为空！");
//		}
//		if(CmnUtil.isStringEmpty(userModelId)) {
//			throw new RuntimeException("用户模型不能为空！");
//		}
		if(prog == null) {
			prog = Progress.newOutput();
		}
		File destFolder = null;
		File file = null;
		try(IDao dao = IDaoService.newIDao()){
			ExpImpContext context = new ExpImpContext();
			context.setDao(dao);
			context.setFormOpObserver(observer);
			OrgDataExcelExpImp expImpIntf = new OrgDataExcelExpImp();
			expImpIntf.setUserModelId(userModelId).setOrgModelId(orgModelId);
			File innerFolder = null;
			if(zipFile.getKey().endsWith(".zip")) {
//				File file = new File("./temp",zipFile.getKey());
				file = new File("./temp","OrgExcelData_"+ToolUtilities.allockUUIDWithUnderline()+".zip");
				Utils.writeFile(file, zipFile.getValue());
				destFolder = new File("./temp",file.getName().replaceAll(".zip", "")+"_"+ToolUtilities.allockUUIDWithUnderline());
				ZipUtils.unzip(file.getAbsolutePath(), destFolder.getAbsolutePath());
				File[] listFiles = destFolder.listFiles();
				innerFolder = new File(destFolder,"/OrgData/"+orgModelId.replaceAll("\\.", "_"));
				innerFolder.mkdirs();
				for(File lstFile : listFiles) {
					lstFile.renameTo(new File(innerFolder,lstFile.getName()));
				}
			}else {
				innerFolder = new File("./temp","Org_"+ToolUtilities.allockUUIDWithUnderline()+"/OrgData/"+orgModelId.replaceAll("\\.", "_"));
				innerFolder.mkdirs();
				Utils.writeFile(new File(innerFolder,OrgDataExcelExpImp.DataFile), zipFile.getValue());
			}
			Progress childProg2 = prog.newChildProgress(0, 99);
			expImpIntf.importData(context, childProg2, innerFolder);
			dao.commit();
			prog.finish();
			List<String> codes = (List<String>) context.getContextMap().get(FormDataExcelExpImp.getImportFormCodeContextKey(orgModelId));
			if(CmnUtil.isCollectionEmpty(codes)) {
				return Collections.emptyList();
			}else {
				return codes;
			}
		}finally {
			if(destFolder != null)
				ToolUtilities.deleteFileFolder(destFolder);
			if(file != null)
				ToolUtilities.deleteFile(file);
		}
	}
}
