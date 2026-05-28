package gpf.dc.basic.util;

import cell.cdao.IDao;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.basic.IPrivilegeMatrixMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cell.gpf.dc.runtime.ISqlMappingMgr;
import cmn.enums.TraceLevel;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import com.cdao.dto.DataRow;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;
import fe.cmn.app.Context;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import fe.util.component.param.WidgetParam;
import gpf.adur.data.*;
import gpf.dc.basic.dto.privilege.*;
import gpf.dc.basic.expression.RuleIntf;
import gpf.dc.basic.expression.matchUser.MatchIentifyRuleIntf;
import gpf.dc.basic.expression.privilege.PrivilegeRuleIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.component.param.BaseTableViewParam;
import gpf.dc.basic.fe.component.param.BaseTreeViewParam;
import gpf.dc.basic.fe.component.view.ViewDataQueryActionIntf;
import gpf.dc.basic.param.view.CustomQueryParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.basic.privilege.dto.AppPrivilegeDto;
import gpf.dc.basic.privilege.dto.MenuPrivilegeDto;
import gpf.dc.config.sqlmapping.SqlMappingConst;
import gpf.dc.config.sqlmapping.ViewSqlDto;
import gpf.dc.runtime.PDFForm;
import gpf.dc.runtime.PDFFormQueryOption;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import gpf.dto.model.data.FormPrivilegeDto;
import ms.cmn.util.MD5Util;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 权限矩阵运行工具类 包括 查询 数据集、 控制表格、列表、树、表单按钮权限，表单属性权限
 */
public class PrivilegeMatrixFeRunTool {
	
	public final static String LOG = PrivilegeMatrixFeRunTool.class.getSimpleName();

	/**
	 * 根据权限矩阵计算表单的数据权限和操作权限
	 * @param rtx
	 * @param form
	 * @param privilegeMatrixCode
	 * @return
	 * @throws Exception
	 */
	public static FormPrivilegeDto caculatePrivilege(IDCRuntimeContext rtx,Form form,Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode,String statusField) throws Exception {
		String modelId = rtx.getPdfUuid();
		Map<String,Boolean> identifyMatchResult = caculateIdentifyMatchResult(rtx, modelId, form, namespaces,env,privilegeMatrixCode,statusField);
		if(identifyMatchResult == null) {
			return null;
		}
		return caculatePrivilege(rtx, modelId, form, namespaces,env,privilegeMatrixCode, identifyMatchResult,statusField);
	}
	
	/**
	 * 根据权限矩阵计算表单数据的用户身份匹配结果
	 * @param rtx
	 * @param modelId
	 * @param form
	 * @param privilegeMatrixCode
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Boolean> caculateIdentifyMatchResult(IDCRuntimeContext rtx, String modelId, Form form,Set<String> namespaces,Map<String,Object> env, String privilegeMatrixCode,String statusField)
			throws Exception {
		// 根据权限矩阵计算用户匹配权限，找到用户匹配对应的权限规则
		Tracer tracer = TraceUtil.getCurrentTracer();
		if (CmnUtil.isStringEmpty(privilegeMatrixCode)) {
			tracer.warning("权限编号为空，跳过用户身份匹配计算！");
			return null;
		}
//		String node = null;
		List<FormField> meta = null;
		if (!CmnUtil.isStringEmpty(modelId)) {
			if (IPDFMgr.get().isPDF(modelId)) {
				meta = IPDFRuntimeMgr.get().queryPDFFormFields(modelId);
//				PDCForm pdcForm = (PDCForm) form;
//				node = pdcForm.getNodeName();
////				node = rtx.getRefPDCNode().getName();
			} else {
				FormModel formModel = IFormMgr.get().queryFormModel(modelId, false);
				if(formModel == null)
					throw new Exception("模型[" + modelId + "]不存在！");
				meta = formModel.getFieldList();
			}
		}
//		if (node == null) {
//			node = PrivilegeMatrix.DefaultCategory;
//		}
		String node = null;
		tracer.info("状态属性 = " + statusField );
		if(!CmnUtil.isStringEmpty(statusField) && form != null) {
			node = form.getString(statusField);
		}
		if(CmnUtil.isStringEmpty(node)) {
			node = PrivilegeMatrix.DefaultCategory;
		}
		tracer.info("状态属性值 = " + node );
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(rtx.getDao(),privilegeMatrixCode);
		if(privilegeMatrix == null) {
			throw new Exception("权限矩阵[" + privilegeMatrixCode + "]不存在！");
		}
		Map<String, Map<String, PrivilegeMatrixRow>> allMatrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap();
		if(allMatrixRowMap.isEmpty()) {
			tracer.warning("权限矩阵配置为空，跳过权限计算！"+privilegeMatrixCode);
			return null;
		}
		Map<String, PrivilegeMatrixRow> matrixRowMap = allMatrixRowMap.get(node);
		if (CmnUtil.isMapEmpty(matrixRowMap)) {
			matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(PrivilegeMatrix.DefaultCategory);
		}
		if (matrixRowMap == null) {
			tracer.warning("未找到分类[" + node + "]的权限方案配置！当前所有分类：" + allMatrixRowMap.keySet());
			return new LinkedHashMap<>();
		}
//		IExpressionMgr.get().registerFun(ConditionRuleDefine.FormModelId);
//		IExpressionMgr.get().registerFun(MatchUserRuleDefine.FormModelId);
//		Map<String, Object> env = new LinkedHashMap<>();
		boolean useFieldName = true;

		String alias = SqlMappingConst.Form;
		String aliasLabel = SqlMappingConst.FormLabel;
		long start5 = System.currentTimeMillis();
		Map<String, String> cteSqls = null;
		if (form == null) {
			cteSqls = new LinkedHashMap<>();
			if (!CmnUtil.isStringEmpty(modelId)) {
				if (IPDFMgr.get().isPDF(modelId)) {
					form = IPDFRuntimeMgr.get().newStartForm(rtx, modelId, false);
				} else {
					form = new Form(modelId);
				}
				cteSqls = ISqlMappingMgr.get().buildCteSqlOfForm(meta, form, alias, aliasLabel, useFieldName);
			}else {
				cteSqls.put("表单", "SELECT 1");
			}
		} else {
			cteSqls = ISqlMappingMgr.get().buildCteSqlOfForm(meta, form, alias, aliasLabel, useFieldName);
			tracer.debug("构建表单SQL耗时： " + (System.currentTimeMillis() - start5) + "ms");
		}
		long start3 = System.currentTimeMillis();
		ISqlMappingMgr sqlMgr = ISqlMappingMgr.get();
		IPrivilegeMatrixMgr matrixMgr = IPrivilegeMatrixMgr.get();
		Map<String, ViewSqlDto> viewSqls = new LinkedHashMap<>();
		AtomicInteger appendAliasCnt = new AtomicInteger(0);
		for (String viewName : cteSqls.keySet()) {
			ViewSqlDto viewSql = new ViewSqlDto();
			viewSql.setAlias("T" + (appendAliasCnt.incrementAndGet())).setViewName(viewName)
					.setQuerySql(cteSqls.get(viewName));
			viewSqls.put(viewName, viewSql);
		}
		String orgModelId = rtx.getOrgModelId();
		String userModelId = rtx.getUserModelId();
		MatchIentifyRuleIntf.prepareEnv(env, rtx, meta, form, viewSqls, useFieldName, orgModelId, userModelId);
//		Map<String,String> identifyExps = (Map<String, String>) rtx.getParam(MatchUserRuleIntf.KEY_IdentifyExpressions);
		Map<String, IdentifyMatchParam> identifyExps = new LinkedHashMap<>();

		for (String identify : matrixRowMap.keySet()) {
			PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
			String userMatchRule = matrixRow.getUserMatchRule();
			String ruleParams = matrixRow.getMatchParams();
			IdentifyMatchParam queryParams = matrixMgr.calculateMatchUserRule(userMatchRule, ruleParams, namespaces,env);
			identifyExps.put(identify, queryParams);
		}
		tracer.debug("计算匹配用户表达式总耗时：" + (System.currentTimeMillis() - start3) + "ms");

		String mainAlias = viewSqls.get(aliasLabel).getAlias();
		StringBuffer querySql = new StringBuffer();
		querySql.append("SELECT ");
		Map<String, String> regexMap = new LinkedHashMap<>();
		Map<String, String> modelAliasMap = new LinkedHashMap<>();
		Map<String, String> roleCode2QueryColMap = new LinkedHashMap<>();
		for (String roleCode : identifyExps.keySet()) {
			IdentifyMatchParam expr = identifyExps.get(roleCode);
			String roleCodeQueryCol = generateRoleCodeQueryCol(roleCode);
			roleCode2QueryColMap.put(roleCode, roleCodeQueryCol);
			querySql.append(expr.getMatchExpression() + " AS " + roleCodeQueryCol);
			querySql.append(",");
		}
		querySql.deleteCharAt(querySql.length() - 1);
		Map<String, JoinViewInfo> allJoinViews = new LinkedHashMap<>();
		ResultSetQueryParam resultQuery = matrixMgr._doBuildApppendViewsByIdentifyMatchQueryParam(identifyExps,
				appendAliasCnt, useFieldName, regexMap, modelAliasMap, allJoinViews);
		Map<String, ViewSqlDto> appendViewSqls = resultQuery.getAppendViewSqls();

		tracer.info("当前用户：" + rtx.getOperator());
		tracer.info("权限表达式：" + querySql.toString());
		StringBuffer withSql = new StringBuffer();
		withSql.append("WITH ");
		withSql.append(mainAlias + " AS (" + viewSqls.get(aliasLabel).getQuerySql() + ")");
		for (ViewSqlDto appendView : appendViewSqls.values()) {
			regexMap.put(quoteAsVariable(appendView.getViewName()), appendView.getAlias());
			withSql.append("\n , " + appendView.getAlias() + " AS (" + appendView.getQuerySql() + ")");
		}
		StringBuffer formAlias = new StringBuffer();
		formAlias.append(" FROM (SELECT 1 as rowNo) R1 LEFT JOIN " + quoteAsVariable("表单") + " ON 1=1 ");
		String matchSql = querySql.toString() + " " + formAlias.toString();
		for (String viewName : viewSqls.keySet()) {
			regexMap.put(quoteAsVariable(viewName), viewSqls.get(viewName).getAlias());
		}
		if (meta != null) {
			for (FormField field : meta) {
				String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
				regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
			}
		}
		String matchIdentifySql = withSql + "\n" + matchSql;
		matchIdentifySql = ToolUtilities.replaceAll(matchIdentifySql, regexMap);
		matchIdentifySql = ISqlMappingMgr.get().escapeAtSymbol(matchIdentifySql);
		tracer.info(matchIdentifySql);
		ResultSet<DataRow> rs = ISqlMappingMgr.get().queryDataRowPage(rtx.getDao(), null, matchIdentifySql, 1, 1);
		Map<String, Boolean> matchResult = new LinkedHashMap<>();
		tracer.info("匹配身份结果数：" + rs.getSize());
		if (!rs.isEmpty()) {
			DataRow dataRow = rs.getDataList().get(0);
			for (String key : dataRow.keySet()) {
				tracer.info(key +" = " + dataRow.getBoolean(key, false));
				for (String roleCode : identifyExps.keySet()) {
					String roleCodeQueryCol = roleCode2QueryColMap.get(roleCode);
					if (CmnUtil.isStringEqual(key.toLowerCase(), roleCodeQueryCol.toLowerCase())) {
						matchResult.put(roleCode, dataRow.getBoolean(key, false));
						break;
					}
				}
			}
		}
		return matchResult;
	}
	
	/**
	 * 检查动作实例编号长度是否超过63位，超过时使用MD5值作为编号
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	public static String generateRoleCodeQueryCol(String roleCode) throws Exception {
		if(roleCode.length() > 63) {
			String roleMd5 = "RoleMD5_"+MD5Util.encode(roleCode);
			LvUtil.trace(roleCode + "长度超过数据库列限制，使用MD5值：" + roleMd5);
			roleCode = roleMd5;
		}
		return roleCode;
	}
	/**
	 * 根据权限矩阵和用户身份匹配结果计算表单的数据权限和操作权限
	 * @param rtx
	 * @param modelId
	 * @param form
	 * @param privilegeMatrixCode
	 * @param identifyMatchResult
	 * @return
	 * @throws Exception
	 */
	public static FormPrivilegeDto caculatePrivilege(IDCRuntimeContext rtx,String modelId,Form form,Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode,Map<String,Boolean> identifyMatchResult,String statusField) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		if(CmnUtil.isStringEmpty(privilegeMatrixCode)) {
			tracer.warning("权限编号为空，跳过权限计算！");
			return null;
		}
		IPrivilegeMatrixMgr privilegeMgr = IPrivilegeMatrixMgr.get();
		IDao dao = rtx.getDao();
		String orgModelId = rtx.getOrgModelId();
		String userModelId = rtx.getUserModelId();
		tracer.info("没有已计算的表单权限集，开始计算：");
//		String node = null;
//		if(IPDFMgr.get().isPDF(modelId)) {
//			PDCForm pdcForm = (PDCForm) form;
////			node = rtx.getRefPDCNode().getName();
//			node = pdcForm.getNodeName();
//		}else {
//			node = PrivilegeMatrix.DefaultCategory;
//		}
		tracer.info("状态属性 = " + statusField);
		String node = null;
		if(!CmnUtil.isStringEmpty(statusField) && form != null) {
			node = form.getString(statusField);
		}
		if(node == null) {
			node = PrivilegeMatrix.DefaultCategory;
		}
		tracer.info("状态属性值 = " + node);
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(rtx.getDao(),privilegeMatrixCode);
		if(privilegeMatrix == null)
			throw new Exception("权限矩阵["+privilegeMatrixCode+"]不存在！");
		if(CmnUtil.isCollectionEmpty(privilegeMatrix.getSettings())) {
			tracer.info("权限矩阵未配置，跳过权限计算！" + privilegeMatrixCode);
			return null;
		}
//		Map<String,Object> env = new LinkedHashMap<>();
		PrivilegeRuleIntf.prepareEnv(env, rtx, new ArrayList<>(), form,new LinkedHashMap<>(), true, orgModelId,userModelId);
		Map<String,FormPrivilegeDto> identifyPrivMap = new LinkedHashMap<>();
		tracer.logStart();
		Map<String,PrivilegeMatrixRow> matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(node);
		if(CmnUtil.isMapEmpty(matrixRowMap)) {
			matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(PrivilegeMatrix.DefaultCategory);
		}
		tracer.printCost("privilegeMatrix.getPrivilegeMatrixSettingMap()", false);
		if(!CmnUtil.isMapEmpty(matrixRowMap)) {
			for(String identify : identifyMatchResult.keySet()) {
				boolean isMatch = CmnUtil.getBoolean(identifyMatchResult.get(identify),false);
				tracer.info("身份["+identify+"]的匹配结果：" + isMatch);
				PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
				if(matrixRow != null) {
					if(!isMatch) {
						continue;
					}else {
						String dataSolutionCode = matrixRow.getDataSolution();
						tracer.info("数据权限方案:" + dataSolutionCode);
						DataPrivilegeSolution dataSolution = queryDataPrivilegeSolution(dao,dataSolutionCode);
						String dataCategory = matrixRow.getDataSolutionCategory();
						String actionSolutionCode = matrixRow.getActionSolution();
						tracer.info("动作权限方案:" + actionSolutionCode);
						ActionPrivilegeSolution actionSolution = queryActionPrivilegeSolution(dao,actionSolutionCode);
						String actionCategoy = matrixRow.getActionSolutionCategory();
						tracer.logStart();
						FormPrivilegeDto formPriv = privilegeMgr.caculateFormPrivilege(namespaces,env,dataSolution, dataCategory, actionSolution, actionCategoy,true);
						tracer.printCost("privilegeMgr.caculateFormPrivilege", false);
						identifyPrivMap.put(identify, formPriv);
					}
				}else {
					tracer.warning("未找到身份["+identify+"]的权限配置！");
				}
			}
		}else {
			tracer.warning("未找到分类["+node+"]的权限配置");
		}
		
		FormPrivilegeDto totalFormPrivilege = new FormPrivilegeDto();
		if(identifyPrivMap.isEmpty()) {
			//不匹配，如果是发起人，考虑是否要显示，目前先不给任何权限
			totalFormPrivilege = privilegeMgr.buildUnauthorizedFormPrivilege(privilegeMatrix);
		}else {
			for(FormPrivilegeDto formPriv : identifyPrivMap.values()) {
				totalFormPrivilege = totalFormPrivilege.megerFormPrivilegeDto(totalFormPrivilege, formPriv);
			}
		}
		if(tracer.getLevel() == TraceLevel.DEBUG) {
			tracer.debug(JsonUtil.toJson(totalFormPrivilege));
		}
		return totalFormPrivilege;
	}

	/**
	 * 通过权限矩阵构建表格、列表、树的查询SQL
	 * 
	 * @param rtx
	 * @param currComponent
	 * @param listener
	 * @param feContext
	 * @param privilegeMatrixCode
	 * @return
	 * @throws Exception
	 */
	public static ResultSet queryDataPageWithPrivilege(IDCRuntimeContext rtx, AbsComponent currComponent,
			ListenerDto listener, PanelContext feContext, Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode,String statusField,Set<String> queryFields) throws Exception {
		if (!(currComponent instanceof ViewDataQueryActionIntf)) {
			throw new Exception("数据查询出错，当前组件未实现ViewDataQueryActionIntf接口！");
		}
		ViewDataQueryActionIntf dataQueryIntf = ((ViewDataQueryActionIntf) currComponent);
		String querySql = (String) rtx.getParam(CustomQueryParameter.FeActionParameter_QuerySql);
		Cnd cnd = (Cnd) rtx.getParam(CustomQueryParameter.FeActionParameter_Cnd);
		Integer pageNo = (Integer) rtx.getParam(CustomQueryParameter.FeActionParameter_PageNo);
		Integer pageSize = (Integer) rtx.getParam(CustomQueryParameter.FeActionParameter_PageSize);
		SqlExpressionGroup defaultPrivExpr = (SqlExpressionGroup) rtx
				.getParam(CustomQueryParameter.FeActionParameter_DefaultExpr);
		String modelId = null;
		WidgetParam widgetParam = currComponent.getWidgetParam();
		if (widgetParam instanceof BaseTableViewParam) {
			modelId = ((BaseTableViewParam) widgetParam).getModelId();
		} else if (widgetParam instanceof BaseTreeViewParam) {
			modelId = ((BaseTreeViewParam) widgetParam).getModelId();
		} else {
			throw new Exception("未支持删除操作的组件类型:" + currComponent.getClass().getName());
		}
		return queryDataPageWithPrivilege(rtx, modelId, querySql, cnd, pageNo, pageSize, namespaces,env,privilegeMatrixCode,
				defaultPrivExpr,statusField,queryFields);
	}
	/**
	 * 构建模型默认查询SQL
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	public static String buildDefaultQuerySql(String modelId) throws Exception {
		if(IPDFMgr.get().isPDF(modelId)) {
			PDFFormQueryOption queryOption = null;
			String dataSql = IPDFRuntimeMgr.get().buildPDFFormQuerySql(modelId,queryOption, null, null);
			return dataSql;
		}else {
			FormModel formModel = IFormMgr.get().queryFormModel(modelId);
			//检查是否有字节数据，如果有不查
			String dataSql = "select * from " + formModel.getTableName();
			boolean hasByteField = false;
			StringBuffer dataSqlWithBytes = new StringBuffer();
			dataSqlWithBytes.append("select ");
			for(FormField field : formModel.getFieldList()) {
				DataType dataType = field.getDataTypeEnum();
				if(dataType == DataType.Image || dataType == DataType.Binary) {
					hasByteField = true;
					continue;
				}
				dataSqlWithBytes.append(" " + field.getCode() + " ");
				dataSqlWithBytes.append(",");
			}
			dataSqlWithBytes.deleteCharAt(dataSqlWithBytes.length()-1);
			dataSqlWithBytes.append(" from " + formModel.getTableName());
			if(hasByteField) {
				dataSql = dataSqlWithBytes.toString();
			}
			return dataSql;
		}
	}

	/**
	 * 通过权限矩阵构建表格、列表、树的查询SQL
	 * 
	 * @param rtx
	 * @param modelId
	 * @param querySql
	 * @param cnd
	 * @param pageNo
	 * @param pageSize
	 * @param privilegeMatrixCode
	 * @param defaultPrivExpr
	 * @return
	 * @throws Exception
	 */
	public static ResultSet queryDataPageWithPrivilege(IDCRuntimeContext rtx, String modelId, String querySql, Cnd cnd,
			int pageNo, int pageSize, Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode, SqlExpressionGroup defaultPrivExpr,String statusField,Set<String> queryFields) throws Exception {
		ResultSetQueryParam privilegeParam = caculateResultSetQueryParam(rtx, modelId, namespaces,env,privilegeMatrixCode,statusField);
		ResultSet rs = null;
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.debug(LOG, "modelId = "+modelId);
		tracer.debug(LOG,"querySql = " + querySql);
		tracer.debug(LOG,"cnd = " + cnd);
		tracer.debug(LOG,"pageNo = " + pageNo);
		tracer.debug(LOG,"pageSize = " + pageSize);
		tracer.debug(LOG,"namespaces = " + namespaces);
		tracer.debug(LOG,"privilegeMatrixCode = " + privilegeMatrixCode);
		tracer.debug(LOG,"defaultPrivExpr = " + defaultPrivExpr);
		tracer.debug(LOG,"queryFields = " + queryFields);

		if (IPDFMgr.get().isPDF(modelId)) {
			Set<String> extFields = new LinkedHashSet<>();
			extFields.add(ResultSet.TotalCount);
			extFields.add(ALL_ROLES);
			String finalSql = buildResultSetQuerySqlWithPrivilege(querySql, privilegeParam, defaultPrivExpr,queryFields);
			tracer.info(LOG,"finalSql = " + finalSql);
			rs = IPDFRuntimeMgr.get().queryPDFFormPageBySql(modelId, finalSql, extFields, cnd, pageNo, pageSize);
		} else {
			String finalSql = buildResultSetQuerySqlWithPrivilege(querySql, privilegeParam, defaultPrivExpr,queryFields);
			tracer.info(LOG,"finalSql = " + finalSql);
			Set<String> extFields = new LinkedHashSet<>();
			extFields.add(ResultSet.TotalCount);
			extFields.add(ALL_ROLES);
			rs = IFormMgr.get().queryFormPageBySql(rtx.getDao(), modelId, finalSql, extFields, cnd, pageNo, pageSize);
		}
		tracer.info(LOG,"rs totalCount = " + rs.getTotalCount());
		//补充行操作权限计算
		if(privilegeParam != null) {
			IPrivilegeMatrixMgr privilegeMgr = IPrivilegeMatrixMgr.get();
			IDao dao = rtx.getDao();
			tracer.info("状态属性 = " + statusField);
			PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(rtx.getDao(),privilegeMatrixCode);
			if(privilegeMatrix == null)
				throw new Exception("权限矩阵["+privilegeMatrixCode+"]不存在！");
			tracer.info("计算行操作权限");
			for(Object row : rs.getDataList()) {
				String all_roles = null;
				if(row instanceof Form) {
					all_roles = (String) ((Form) row).getExtField(ALL_ROLES);
					String uuid = ((Form) row).getUuid();
					if(!CmnUtil.isStringEmpty(all_roles)) {
						tracer.info("表单(uuid = "+uuid+") all_roles = " + all_roles);
						Map<String,Map<String,Boolean>> nodeIdentifyResult = new LinkedHashMap<>();
						for(String nodeRole : all_roles.split(",")) {
							String[] nodeRoleArr = nodeRole.split(":");
							if(!nodeIdentifyResult.containsKey(nodeRoleArr[0])) {
								nodeIdentifyResult.put(nodeRoleArr[0], new LinkedHashMap<>());
							}
							nodeIdentifyResult.get(nodeRoleArr[0]).put(nodeRoleArr[1], true);
						}
						Map<String,FormPrivilegeDto> identifyPrivMap = new LinkedHashMap<>();
						for(String node : nodeIdentifyResult.keySet()) {
							Map<String,Boolean> identifyMatchResult = nodeIdentifyResult.get(node);
							Map<String,PrivilegeMatrixRow> matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(node);
							if(!CmnUtil.isMapEmpty(matrixRowMap)) {
								for(String identify : identifyMatchResult.keySet()) {
									boolean isMatch = CmnUtil.getBoolean(identifyMatchResult.get(identify),false);
									tracer.info("身份["+identify+"]的匹配结果：" + isMatch);
									PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
									if(matrixRow != null) {
										String dataSolutionCode = matrixRow.getDataSolution();
										tracer.info("数据权限方案:" + dataSolutionCode);
										DataPrivilegeSolution dataSolution = queryDataPrivilegeSolution(dao,dataSolutionCode);
										String dataCategory = matrixRow.getDataSolutionCategory();
										String actionSolutionCode = matrixRow.getActionSolution();
										tracer.info("动作权限方案:" + actionSolutionCode);
										ActionPrivilegeSolution actionSolution = queryActionPrivilegeSolution(dao,actionSolutionCode);
										String actionCategoy = matrixRow.getActionSolutionCategory();
										tracer.logStart();
										FormPrivilegeDto formPriv = privilegeMgr.caculateFormPrivilege(namespaces,env,dataSolution, dataCategory, actionSolution, actionCategoy,true);
										tracer.printCost("privilegeMgr.caculateFormPrivilege", false);
										if(!identifyPrivMap.containsKey(identify)) {
											identifyPrivMap.put(identify, formPriv);
										}else {
											FormPrivilegeDto orgFormPriv = identifyPrivMap.get(identify);
											orgFormPriv = FormPrivilegeDto.megerFormPrivilegeDto(orgFormPriv, formPriv);
											identifyPrivMap.put(identify, orgFormPriv);
										}
									}else {
										tracer.warning("未找到身份["+identify+"]的权限配置！");
									}
								}
							}
						}
						FormPrivilegeDto totalPrivilege = new FormPrivilegeDto();
						for(String identify : identifyPrivMap.keySet()) {
							totalPrivilege = FormPrivilegeDto.megerFormPrivilegeDto(totalPrivilege, identifyPrivMap.get(identify));
						}
						((Form) row).setFormPrivilege(totalPrivilege);
					}
				}
			}
		}
		return rs;
	}

	/**
	 * 根据权限矩阵计算数据集查询参数
	 * 
	 * @param rtx
	 * @param modelId
	 * @param privilegeMatrixCode
	 * @param statusField 状态属性
	 * @return
	 * @throws Exception
	 */
	public static ResultSetQueryParam caculateResultSetQueryParam(IDCRuntimeContext rtx, String modelId,Set<String> namespaces,Map<String,Object> env, 
			String privilegeMatrixCode,String statusField) throws Exception {
		if (CmnUtil.isStringEmpty(privilegeMatrixCode))
			return null;
		Tracer tracer = TraceUtil.getCurrentTracer();
		ISqlMappingMgr sqlMgr = ISqlMappingMgr.get();
		IPrivilegeMatrixMgr matrixMgr = IPrivilegeMatrixMgr.get();
		tracer.info("权限矩阵:" + privilegeMatrixCode);
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(rtx.getDao(), privilegeMatrixCode);
		if(privilegeMatrix == null) {
			throw new Exception("权限矩阵[" + privilegeMatrixCode+"]不存在！");
		}
		String user = rtx.getOperator();
//		Map<String, Object> env = new LinkedHashMap<>();
		String orgModelId = rtx.getOrgModelId();
		String userModelId = rtx.getUserModelId();
		RuleIntf.prepareEnv(env, rtx, null, orgModelId, userModelId);
		boolean useFieldName = false;
		Map<String, ViewSqlDto> appendViewSqls = new LinkedHashMap<>();
		Set<String> appendViewAlias = new LinkedHashSet<>();
		String privilegeExpression = null;
		List<String> roleCaseWhenExpressions = new ArrayList<>();
		Map<String,Map<String,PrivilegeMatrixRow>> matrixRowMaps =  privilegeMatrix.getPrivilegeMatrixSettingMap();
		if(matrixRowMaps.isEmpty()) {
			tracer.info("权限矩阵配置为空，忽略权限计算逻辑！");	
			return null;
		}
		Set<String> nodeNames = null;
		if(matrixRowMaps.containsKey(PrivilegeMatrix.DefaultCategory)) {
			if(matrixRowMaps.size() > 1) {
				matrixRowMaps.remove(PrivilegeMatrix.DefaultCategory);
			}
			nodeNames = matrixRowMaps.keySet();
		}else {
			nodeNames = matrixRowMaps.keySet();
		}
		tracer.info("规则工作空间:" + namespaces);
		tracer.info("状态属性:" + statusField);
		tracer.info("状态集合:" + nodeNames);
		if (IPDFMgr.get().isPDF(modelId)) {
//			PDF pdf = IPDFMgr.get().queryPDF(modelId);
			List<FormField> meta = IPDFRuntimeMgr.get().queryPDFFormFields(modelId);
			Map<String, String> regexMap = new LinkedHashMap<>();
			regexMap.put(quoteAsVariable("表单"), "allData");
			Map<String, Map<String, IdentifyMatchParam>> exprMap = matrixMgr
					.buildAllMatchUserExpressions(privilegeMatrix, nodeNames, user, namespaces,env);
			tracer.info("匹配用户计算结果:" + exprMap);
			
			Map<String, String> modelAliasMap = new LinkedHashMap<>();
			AtomicInteger appendAliasCnt = new AtomicInteger(0);
			Map<String, JoinViewInfo> allJoinViews = new LinkedHashMap<>();
			if(CmnUtil.isStringEmpty(statusField)) {
				Map<String, IdentifyMatchParam> identifyMap = exprMap.get(PrivilegeMatrix.DefaultCategory);
				if(identifyMap == null) {
					tracer.warning("未找到分类为["+PrivilegeMatrix.DefaultCategory+"]的身份匹配配置！");
					return null;
				}
				ResultSetQueryParam nodeRsQuery = matrixMgr._doBuildApppendViewsByIdentifyMatchQueryParam(identifyMap,
						appendAliasCnt, useFieldName, regexMap, modelAliasMap, allJoinViews);
				appendViewAlias.addAll(nodeRsQuery.getAppendViewAliases());
				appendViewSqls.putAll(nodeRsQuery.getAppendViewSqls());
				privilegeExpression = ToolUtilities.replaceAll(nodeRsQuery.getPrivilegeExpression(), regexMap);
				for(String roleCaseWhenExpr : nodeRsQuery.getRoleCaseWhenExpressions()) {
					roleCaseWhenExpr = roleCaseWhenExpr.replace("#分类条件#", "true");
					Map<String,String> roleRegexMap = new LinkedHashMap<>(regexMap);
					roleRegexMap.put("#分类#", PrivilegeMatrix.DefaultCategory);
					roleCaseWhenExpr = ToolUtilities.replaceAll(roleCaseWhenExpr, roleRegexMap);
					roleCaseWhenExpressions.add(roleCaseWhenExpr);
				}
			}else {
				StringBuffer totalOrExpr = new StringBuffer();
				for (FormField field : meta) {
					String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
					regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
				}
				for (String nodeName : exprMap.keySet()) {
					Map<String, IdentifyMatchParam> identifyMap = exprMap.get(nodeName);
					if (totalOrExpr.length() > 0)
						totalOrExpr.append(" OR ");
					ResultSetQueryParam nodeRsQuery = matrixMgr._doBuildApppendViewsByIdentifyMatchQueryParam(identifyMap,
							appendAliasCnt, useFieldName, regexMap, modelAliasMap, allJoinViews);
					appendViewAlias.addAll(nodeRsQuery.getAppendViewAliases());
					appendViewSqls.putAll(nodeRsQuery.getAppendViewSqls());
					StringBuffer stepCond = new StringBuffer();
					stepCond.append(" " + quoteAsVariable("表单") + "." + quoteAsVariable(statusField) + " = '" + nodeName + "'");
					stepCond.append(" AND (" + nodeRsQuery.getPrivilegeExpression() + ")");
					totalOrExpr.append(" ( " + stepCond.toString() + " ) ");
					for(String roleCaseWhenExpr : nodeRsQuery.getRoleCaseWhenExpressions()) {
						roleCaseWhenExpr = roleCaseWhenExpr.replace("#分类条件#", " " + quoteAsVariable("表单") + "." + quoteAsVariable(statusField) + " = '" + nodeName + "'");
						Map<String,String> roleRegexMap = new LinkedHashMap<>(regexMap);
						roleRegexMap.put("#分类#", nodeName);
						roleCaseWhenExpr = ToolUtilities.replaceAll(roleCaseWhenExpr, roleRegexMap);
						roleCaseWhenExpressions.add(roleCaseWhenExpr);
					}
				}
				privilegeExpression = ToolUtilities.replaceAll(totalOrExpr.toString(), regexMap);
			}
		} else {
			FormModel formModel = IFormMgr.get().queryFormModel(modelId);
			List<FormField> meta = formModel.getFieldList();
			Map<String, String> regexMap = new LinkedHashMap<>();
			regexMap.put(quoteAsVariable("表单"), "allData");
			for (FormField field : meta) {
				String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
				regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
			}
			Map<String, Map<String, IdentifyMatchParam>> exprMap = IPrivilegeMatrixMgr.get()
					.buildAllMatchUserExpressions(privilegeMatrix, nodeNames, user, namespaces,env);
			tracer.info("匹配用户计算结果:" + exprMap);
			
			Map<String, String> modelAliasMap = new LinkedHashMap<>();
			AtomicInteger appendAliasCnt = new AtomicInteger(0);
			Map<String, JoinViewInfo> allJoinViews = new LinkedHashMap<>();
			//没有状态属性的，直接拿默认分类的权限
			if(CmnUtil.isStringEmpty(statusField)) {
				Map<String, IdentifyMatchParam> identifyMap = exprMap.get(PrivilegeMatrix.DefaultCategory);
				if(identifyMap == null) {
					tracer.warning("未找到分类为["+PrivilegeMatrix.DefaultCategory+"]的身份匹配配置！");
					return null;
				}
				ResultSetQueryParam nodeRsQuery = matrixMgr._doBuildApppendViewsByIdentifyMatchQueryParam(identifyMap,
						appendAliasCnt, useFieldName, regexMap, modelAliasMap, allJoinViews);
				appendViewAlias.addAll(nodeRsQuery.getAppendViewAliases());
				appendViewSqls.putAll(nodeRsQuery.getAppendViewSqls());
				privilegeExpression = ToolUtilities.replaceAll(nodeRsQuery.getPrivilegeExpression(), regexMap);
				for(String roleCaseWhenExpr : nodeRsQuery.getRoleCaseWhenExpressions()) {
					roleCaseWhenExpr = roleCaseWhenExpr.replace("#分类条件#", "true");
					Map<String,String> roleRegexMap = new LinkedHashMap<>(regexMap);
					roleRegexMap.put("#分类#", PrivilegeMatrix.DefaultCategory);
					roleCaseWhenExpr = ToolUtilities.replaceAll(roleCaseWhenExpr, roleRegexMap);
					roleCaseWhenExpressions.add(roleCaseWhenExpr);
				}
			}else {
				StringBuffer totalOrExpr = new StringBuffer();
				regexMap.put(quoteAsVariable("表单"), "allData");
				for (FormField field : meta) {
					String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
					regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
				}
				
				for (String nodeName : exprMap.keySet()) {
					Map<String, IdentifyMatchParam> identifyMap = exprMap.get(nodeName);
					if (totalOrExpr.length() > 0)
						totalOrExpr.append(" OR ");
					ResultSetQueryParam nodeRsQuery = matrixMgr._doBuildApppendViewsByIdentifyMatchQueryParam(identifyMap,
							appendAliasCnt, useFieldName, regexMap, modelAliasMap, allJoinViews);
					appendViewAlias.addAll(nodeRsQuery.getAppendViewAliases());
					appendViewSqls.putAll(nodeRsQuery.getAppendViewSqls());
					StringBuffer stepCond = new StringBuffer();
					stepCond.append(" " + quoteAsVariable("表单") + "." + quoteAsVariable(statusField) + " = '" + nodeName + "'");
					stepCond.append(" AND (" + nodeRsQuery.getPrivilegeExpression() + ")");
					totalOrExpr.append(" ( " + stepCond.toString() + " ) ");
					for(String roleCaseWhenExpr : nodeRsQuery.getRoleCaseWhenExpressions()) {
						roleCaseWhenExpr = roleCaseWhenExpr.replace("#分类条件#", " " + quoteAsVariable("表单") + "." + quoteAsVariable(statusField) + " = '" + nodeName + "'");
						Map<String,String> roleRegexMap = new LinkedHashMap<>(regexMap);
						roleRegexMap.put("#分类#", nodeName);
						roleCaseWhenExpr = ToolUtilities.replaceAll(roleCaseWhenExpr, roleRegexMap);
						roleCaseWhenExpressions.add(roleCaseWhenExpr);
					}
				}
				privilegeExpression = ToolUtilities.replaceAll(totalOrExpr.toString(), regexMap);
			}
		}

		tracer.info("权限表达式：" + privilegeExpression);
		ResultSetQueryParam privilegeParam = new ResultSetQueryParam();
		privilegeParam.setAppendViewAliases(appendViewAlias);
		privilegeParam.setAppendViewSqls(appendViewSqls);
		privilegeParam.setPrivilegeExpression(privilegeExpression);
		privilegeParam.setRoleCaseWhenExpressions(roleCaseWhenExpressions);
		return privilegeParam;
	}

	public static PrivilegeMatrix queryPrivilegeMatrix(IDao dao, String privilegeMatrixCode) throws Exception {
		return IPrivilegeMatrixMgr.get().queryPrivilegeMatrixCache(dao, privilegeMatrixCode);
	}

	public static String quoteAsVariable(String name) {
		return IPrivilegeMatrixMgr.get().quoteAsVariable(name);
	}
	
	public static DataPrivilegeSolution queryDataPrivilegeSolution(IDao dao,String dataSolutionCode) throws Exception {
		return IPrivilegeMatrixMgr.get().queryDataPrivilegeSolutionCache(dao,dataSolutionCode);
	}
 	public static ActionPrivilegeSolution queryActionPrivilegeSolution(IDao dao,String actionSolutionCode) throws Exception {
		return IPrivilegeMatrixMgr.get().queryActionPrivilegeSolutionCache(dao,actionSolutionCode);
	}

 	public final static String ALL_ROLES = "all_roles";
	/**
	 * 根据数据查询SQL（全量数据查询）组合权限查询参数，得到权限过滤后的查询SQL
	 * 
	 * @param dataSql
	 * @param privilegeParam
	 * @return
	 * @throws Exception
	 */
	public static String buildResultSetQuerySqlWithPrivilege(String dataSql, ResultSetQueryParam privilegeParam,
			SqlExpressionGroup defaultPrivExpr,Set<String> queryFields) throws Exception {
		Map<String, String> withSqls = new LinkedHashMap<>();
		withSqls.put("allData", dataSql);
		String querySql = null;
		if (privilegeParam != null) {
			StringBuffer privilegeSql = new StringBuffer();
			privilegeSql.append("select DISTINCT allData.* ");
			if(!privilegeParam.getRoleCaseWhenExpressions().isEmpty()) {
				StringBuffer roleExprSql = new StringBuffer();
				roleExprSql.append(",");
				roleExprSql.append("CONCAT_WS(',',");
				roleExprSql.append(String.join(",", privilegeParam.getRoleCaseWhenExpressions()));
				roleExprSql.append(") AS "+ ALL_ROLES +" ");
				privilegeSql.append(roleExprSql.toString());
			}else {
				privilegeSql.append(",'' AS " + ALL_ROLES + " " );
			}
			privilegeSql.append(" from allData");
			for (ViewSqlDto viewSql : privilegeParam.getAppendViewSqls().values()) {
				withSqls.put(viewSql.getAlias(), viewSql.getQuerySql());
			}
			if(CmnUtil.isStringEmpty(privilegeParam.getPrivilegeExpression())) {
				privilegeSql.append(" WHERE false ");
				if (defaultPrivExpr != null) {
					privilegeSql.append(" OR ( "+ ISqlMappingMgr.get().cndToSql(Cnd.where(defaultPrivExpr), false, false) + " ) ");
				}
			}else {
				privilegeSql.append(" WHERE " + privilegeParam.getPrivilegeExpression());
				if (defaultPrivExpr != null) {
					privilegeSql.append(" OR ");
					privilegeSql.append(" ( "+ ISqlMappingMgr.get().cndToSql(Cnd.where(defaultPrivExpr), false, false) + " ) ");
				}
			}
			withSqls.put("filterData", privilegeSql.toString());
			if(CmnUtil.isCollectionEmpty(queryFields)){
				querySql = "select *," + ResultSet.TotalCount_Select + " from filterData";
			}else{
				querySql = "select " + String.join(",", queryFields)+ ", "+ALL_ROLES+", "  + ResultSet.TotalCount_Select + " from filterData";;
			}
		} else {
			if (defaultPrivExpr != null) {
				dataSql += Cnd.where(defaultPrivExpr).toSql(null);
				withSqls.put("allData", dataSql);
			}
			if(CmnUtil.isCollectionEmpty(queryFields)) {
				querySql = "select *,'' AS "+ALL_ROLES+"," + ResultSet.TotalCount_Select + " from allData";
			}else{
				querySql = "select " + String.join(",", queryFields)+", '' AS "+ALL_ROLES+"," + ResultSet.TotalCount_Select + " from allData";
			}
		}
		String withSql = ISqlMappingMgr.get().viewSqlsToWithSql(withSqls);
		String sql = withSql + "\n" + querySql;
		return sql;
	}
	/**
	 * 计算应用菜单权限
	 * @param appSetting
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public static Map<String,MenuPrivilegeDto> caculateMenuPrivilegeByMatrix(ApplicationSetting appSetting,String userCode) throws Exception{
		Context context = null;
		AppPrivilegeDto appPrivilege = IApplicationService.get().queryMenuPrivileges(appSetting, userCode, context);
		if(appPrivilege == null || appPrivilege.isPrivilegeSettingEmpty()) {
			//未设置权限的，默认给所有权限
			Tracer tracer = TraceUtil.getCurrentTracer(LOG);
			tracer.info("未配置应用权限，默认给所有权限！");
			Map<String,String> menuPathMap = IApplicationService.get().queryAppMenuUuidPathMap(appSetting);
			Map<String,MenuPrivilegeDto> menuMap = new LinkedHashMap<>();
			for(String uuid : menuPathMap.keySet()) {
				MenuPrivilegeDto menuPriv = new MenuPrivilegeDto().setMenuUuid(uuid).setMenuPath(menuPathMap.get(uuid)).setVisible(true);
				menuMap.put(uuid, menuPriv);
			}
			return menuMap;
		}
		return appPrivilege.getAllMenuPrivileges();
	}
	
	public static Map<String,List<MenuPrivilegeDto>> caculatePrivilegeByMatrix(IDCRuntimeContext rtx,ApplicationSetting appSetting,Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode,Map<String,Boolean> identifyMatchResult) throws Exception{
		if(CmnUtil.isStringEmpty(privilegeMatrixCode))
			return null;
		IDao dao = rtx.getDao();
		String orgModelId = rtx.getOrgModelId();
		String userModelId = rtx.getUserModelId();
		String node = PrivilegeMatrix.DefaultCategory;
		Form form = null;
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(dao,privilegeMatrixCode);
		if(privilegeMatrix == null)
			throw new Exception("权限矩阵["+privilegeMatrixCode+"]不存在！");
//		ApplicationSetting appSetting = AppCacheUtil.getSetting(panelContext);
//		Map<String,Object> env = new LinkedHashMap<>();
		PrivilegeRuleIntf.prepareEnv(env, rtx, new ArrayList<>(), form,new LinkedHashMap<>(), true, orgModelId,userModelId);
		Map<String,PrivilegeMatrixRow> matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(node);
		Map<String,List<MenuPrivilegeDto>> identifyPrivMap = new LinkedHashMap<>();
		if(!CmnUtil.isMapEmpty(matrixRowMap)) {
			Map<String,String> uuid2PathMap = IApplicationService.get().queryAppMenuUuidPathMap(appSetting);
			Map<String,String> path2UuidMap = new LinkedHashMap<>();
			Map<String,String> path2PinyinMap = new LinkedHashMap<>();
			for(String key : uuid2PathMap.keySet()) {
				path2UuidMap.put(uuid2PathMap.get(key), key);
				path2PinyinMap.put(IFormMgr.get().getFieldCode(uuid2PathMap.get(key)), uuid2PathMap.get(key));
			}
			for(String identify : identifyMatchResult.keySet()) {
				identifyPrivMap.put(identify, new ArrayList<>());
				if(!CmnUtil.getBoolean(identifyMatchResult.get(identify),false)) {
					continue;
				}
				PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
				if(matrixRow != null) {
					String dataSolutionCode = matrixRow.getDataSolution();
					DataPrivilegeSolution dataSolution = queryDataPrivilegeSolution(dao,dataSolutionCode);
					String dataCategory = matrixRow.getDataSolutionCategory();
					String actionSolutionCode = matrixRow.getActionSolution();
					ActionPrivilegeSolution actionSolution = queryActionPrivilegeSolution(dao,actionSolutionCode);
					String actionCategoy = matrixRow.getActionSolutionCategory();
					FormPrivilegeDto formPriv = IPrivilegeMatrixMgr.get().caculateFormPrivilege(namespaces,env,dataSolution, dataCategory, actionSolution, actionCategoy,true);
					List<MenuPrivilegeDto> menuPrivs = new ArrayList<>();
					for(FieldPrivilegeDto fieldPriv : formPriv.getFieldPrivileges()) {
						String path = path2PinyinMap.get(fieldPriv.getField());
						MenuPrivilegeDto menuPriv = new MenuPrivilegeDto();
						if(path2UuidMap.containsKey(path)) {
							String menuUuid = path2UuidMap.get(path);
							LvUtil.trace(path +"("+menuUuid+")"+ " = " + fieldPriv.isVisible());
							menuPriv.setMenuUuid(path2UuidMap.get(path));
							menuPriv.setMenuPath(path);
							menuPriv.setVisible(fieldPriv.isVisible());
							menuPrivs.add(menuPriv);
						}
					}
					identifyPrivMap.put(identify, menuPrivs);
				}
			}
		}
		return identifyPrivMap;
	}
	/**
	 * 计算权限矩阵
	 * @param rtx
	 * @param rows
	 * @param namespaces
	 * @param env
	 * @param privilegeMatrixCode
	 * @param statusField
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Map<String,ActionPrivilegeDto>> caculateRowActionPrivilege(IDCRuntimeContext rtx,List<Form> rows,Set<String> namespaces,Map<String,Object> env,String privilegeMatrixCode,String statusField) throws Exception{
		if(CmnUtil.isCollectionEmpty(rows))
			return null;
		Map<String,Map<String,ActionPrivilegeDto>> rowPrivMap = new LinkedHashMap<>();
		Tracer tracer = TraceUtil.getCurrentTracer();
		if(CmnUtil.isStringEmpty(privilegeMatrixCode)) {
			tracer.warning("权限编号为空，跳过权限计算！");
			return null;
		}
		IPrivilegeMatrixMgr privilegeMgr = IPrivilegeMatrixMgr.get();
		IDao dao = rtx.getDao();
		String orgModelId = rtx.getOrgModelId();
		String userModelId = rtx.getUserModelId();
		tracer.info("状态属性 = " + statusField);
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrix(rtx.getDao(),privilegeMatrixCode);
		if(privilegeMatrix == null)
			throw new Exception("权限矩阵["+privilegeMatrixCode+"]不存在！");
		for(Form form : rows) {
			PrivilegeRuleIntf.prepareEnv(env, rtx, new ArrayList<>(), form,new LinkedHashMap<>(), true, orgModelId,userModelId);
			String key = form.getUuid();
			if(form instanceof PDFForm) {
				key = ((PDFForm) form).getOpLogUuid();
			}
			String allRoles = (String) form.getExtField(ALL_ROLES);
			Map<String,ActionPrivilegeDto> actionPrivMap = new LinkedHashMap<>();
			if(CmnUtil.isStringEmpty(allRoles)) {
//				FormPrivilegeDto formPrivilege = caculatePrivilege(rtx, form, namespaces, env, privilegeMatrixCode, statusField);
//				if(formPrivilege != null) {
//					rowPrivMap.put(key, formPrivilege.getActionPrivilegeMap());
//				}else {
					rowPrivMap.put(key, actionPrivMap);
//				}
				continue;
			}else {
				tracer.info("表单 all_roles = " + allRoles);
				Map<String,Map<String,Boolean>> nodeIdentifyResult = new LinkedHashMap<>();
				for(String nodeRole : allRoles.split(",")) {
					String[] nodeRoleArr = nodeRole.split(":");
					if(!nodeIdentifyResult.containsKey(nodeRoleArr[0])) {
						nodeIdentifyResult.put(nodeRoleArr[0], new LinkedHashMap<>());
					}
					nodeIdentifyResult.get(nodeRoleArr[0]).put(nodeRoleArr[1], true);
				}
				Map<String,FormPrivilegeDto> identifyPrivMap = new LinkedHashMap<>();
				for(String node : nodeIdentifyResult.keySet()) {
					Map<String,Boolean> identifyMatchResult = nodeIdentifyResult.get(node);
					Map<String,PrivilegeMatrixRow> matrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap().get(node);
					if(!CmnUtil.isMapEmpty(matrixRowMap)) {
						for(String identify : identifyMatchResult.keySet()) {
							boolean isMatch = CmnUtil.getBoolean(identifyMatchResult.get(identify),false);
							tracer.info("身份["+identify+"]的匹配结果：" + isMatch);
							PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
							if(matrixRow != null) {
								if(!isMatch) {
									continue;
								}else {
									String actionSolutionCode = matrixRow.getActionSolution();
									tracer.info("动作权限方案:" + actionSolutionCode);
									ActionPrivilegeSolution actionSolution = queryActionPrivilegeSolution(dao,actionSolutionCode);
									String actionCategoy = matrixRow.getActionSolutionCategory();
									tracer.logStart();
									FormPrivilegeDto formPriv = privilegeMgr.caculateFormPrivilege(namespaces,env,null, null, actionSolution, actionCategoy,true);
									tracer.printCost("privilegeMgr.caculateFormPrivilege", false);
									if(!identifyPrivMap.containsKey(identify)) {
										identifyPrivMap.put(identify, formPriv);
									}else {
										FormPrivilegeDto orgFormPriv = identifyPrivMap.get(identify);
										orgFormPriv = FormPrivilegeDto.megerFormPrivilegeDto(orgFormPriv, formPriv);
										identifyPrivMap.put(identify, orgFormPriv);
									}
								}
							}else {
								tracer.warning("未找到身份["+identify+"]的权限配置！");
							}
						}
					}
				}
				FormPrivilegeDto totalPrivilege = new FormPrivilegeDto();
				for(String identify : identifyPrivMap.keySet()) {
					totalPrivilege = FormPrivilegeDto.megerFormPrivilegeDto(totalPrivilege, identifyPrivMap.get(identify));
				}
				rowPrivMap.put(key, totalPrivilege.getActionPrivilegeMap());
			}
		}
		return rowPrivMap;
	}

}
