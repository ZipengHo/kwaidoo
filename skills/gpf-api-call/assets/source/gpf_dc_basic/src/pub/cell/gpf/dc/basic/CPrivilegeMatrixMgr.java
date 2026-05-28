package cell.gpf.dc.basic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.cdao.model.CDoRole;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.kwaidoo.ms.tool.Utils;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.dfc.gui.LvUtil;

import bap.cells.Cells;
import bap.cells.EmptyServiceCell;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cell.gpf.dc.runtime.ISqlMappingMgr;
import cmn.dto.Progress;
import cmn.exception.VerifyException;
import cmn.util.NullUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.WorkbookUtil;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Role;
import gpf.dc.basic.dto.GlobalVariableDto;
import gpf.dc.basic.dto.privilege.ActionItemPrivilegeSetting;
import gpf.dc.basic.dto.privilege.ActionPrivilegeSolution;
import gpf.dc.basic.dto.privilege.DataItemPrivilegeSetting;
import gpf.dc.basic.dto.privilege.DataPrivilegeSolution;
import gpf.dc.basic.dto.privilege.IdentifyMatchParam;
import gpf.dc.basic.dto.privilege.JoinViewInfo;
import gpf.dc.basic.dto.privilege.MatchUserRuleDefine;
import gpf.dc.basic.dto.privilege.PrivilegeMatrix;
import gpf.dc.basic.dto.privilege.PrivilegeMatrixPackage;
import gpf.dc.basic.dto.privilege.PrivilegeMatrixRow;
import gpf.dc.basic.dto.privilege.PrivilegeRuleDefine;
import gpf.dc.basic.dto.privilege.PrivilegeRuleEnum;
import gpf.dc.basic.dto.privilege.ResultSetQueryParam;
import gpf.dc.basic.dto.privilege.excel.GlobalVariableExcelReader;
import gpf.dc.basic.dto.privilege.excel.GlobalVariableExcelRow;
import gpf.dc.basic.dto.privilege.excel.PrivilegeMatrixExcelReaderV2;
import gpf.dc.basic.dto.privilege.excel.PrivilegeMatrixExcelRow;
import gpf.dc.basic.dto.privilege.excel.PrivilegeMatrixExcelWriter;
import gpf.dc.basic.dto.privilege.excel.PrivilegeSolutionExcelReaderV2;
import gpf.dc.basic.dto.privilege.excel.PrivilegeSolutionExcelRow;
import gpf.dc.basic.dto.privilege.excel.PrivilegeSolutionExcelWriter;
import gpf.dc.basic.exception.ExpressionException;
import gpf.dc.basic.expression.matchUser.MatchIentifyRuleIntf;
import gpf.dc.basic.expression.privilege.PrivilegeRuleIntf;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.config.sqlmapping.SqlMappingConst;
import gpf.dc.config.sqlmapping.ViewSqlDto;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.runtime.PDCForm;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import gpf.dto.model.data.FormPrivilegeDto;
import gpf.translate.assist.DataConvertAssistor;
import web.dto.Pair;

public class CPrivilegeMatrixMgr extends EmptyServiceCell implements IPrivilegeMatrixMgr,DataConvertAssistor{

	public final static String CacheBlock_PrivilegeMatrix = "PrivilegeMatrix";
	public final static String CacheBlock_DataSolution = "DataSolution";
	public final static String CacheBlock_ActionSolution = "ActionSolution";
	
	protected IDaoService getDaoService() {
		return Cells.get(IDaoService.class);
	}
	@Override
	public PrivilegeMatrix queryPrivilegeMatrixByCode(IDao dao, String code) throws Exception {
		Form form = IFormMgr.get().queryFormByCode(dao, PrivilegeMatrix.FormModelId, code);
		return convert2PrivilegeMatrix(form);
	}
	
	@Override
	public PrivilegeMatrix queryPrivilegeMatrixCache(IDao dao, String code) throws Exception {
		PrivilegeMatrix privilegeMatrix = IBasicCacheMgr.get().computeCacheIfAbsent(CacheBlock_PrivilegeMatrix, code, PrivilegeMatrix.class,k -> {
			try {
				return queryPrivilegeMatrixByCode(dao, code);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return privilegeMatrix;
	}
	
	@Override
	public Form convertPrivilegeMatrix2Form(PrivilegeMatrix privilegeMatrix) throws Exception {
		Form form = new Form(PrivilegeMatrix.FormModelId);
		form.setUuid(privilegeMatrix.getUuid())
		.setAttrValueByCode(Form.Code, privilegeMatrix.getCode())
		.setAttrValue(PrivilegeMatrix.sName, privilegeMatrix.getName())
		.setAttrValue(PrivilegeMatrix.sType, privilegeMatrix.getType())
		.setAttrValue(PrivilegeMatrix.sDecription, privilegeMatrix.getDescription())
		.setAttrValue(PrivilegeMatrix.PrivilegeMatrixRow, convertToTableData(privilegeMatrix.getSettings(), v->{
			try {
				return convertPrivilegeMatrixRow2Form(v,privilegeMatrix.getCode());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}))
		.setAttrValue(Form.Owner, privilegeMatrix.getOwner());
//		try(IJson json = IJsonService.get().getPrettyJson()){
//			LvUtil.trace(json.toJson(form));
//		}
		return form;
	}
	
	public Form convertPrivilegeMatrixRow2Form(PrivilegeMatrixRow row,String privilegeMatrixCode) throws Exception {
		Form form = new Form(PrivilegeMatrixRow.FormModelId);
		AssociationData identify = null;
		if(!CmnUtil.isStringEmpty(row.getIdentify())){
			identify = new AssociationData(CDoRole.class.getName(), row.getIdentify());
		}
		AssociationData dataSolution = null;
		if(!CmnUtil.isStringEmpty(row.getDataSolution())){
			dataSolution = new AssociationData(DataPrivilegeSolution.FormModelId, row.getDataSolution());
		}
		AssociationData actionSolution = null;
		if(!CmnUtil.isStringEmpty(row.getActionSolution())){
//			System.out.println("动作权限方案：" + privilegeMatrixCode + row.getActionSolution());
			actionSolution = new AssociationData(ActionPrivilegeSolution.FormModelId, row.getActionSolution());
		}
//		AssociationData tagSolution = null;
//		if(!CmnUtil.isStringEmpty(row.getTagSolution())){
////			System.out.println("动作权限方案：" + privilegeMatrixCode + row.getActionSolution());
//			tagSolution = new AssociationData(ActionPrivilegeSolution.FormModelId, row.getTagSolution());
//		}
		AssociationData userMatchRule = null;
		if(!CmnUtil.isStringEmpty(row.getUserMatchRule())){
			userMatchRule = new AssociationData(MatchUserRuleDefine.FormModelId, row.getUserMatchRule());
		}
		form.setUuid(row.getUuid())
		
		.setAttrValue(PrivilegeMatrixRow.NodeName, row.getNodeName())
		.setAttrValue(PrivilegeMatrixRow.Identify, identify)
		.setAttrValue(PrivilegeMatrixRow.DataSolution, dataSolution)
		.setAttrValue(PrivilegeMatrixRow.DataSolutionCategory, row.getDataSolutionCategory())
		.setAttrValue(PrivilegeMatrixRow.ActionSolution, actionSolution)
		.setAttrValue(PrivilegeMatrixRow.ActionSolutionCategory, row.getActionSolutionCategory())
//		.setAttrValue(PrivilegeMatrixRow.TagSolution, tagSolution)
//		.setAttrValue(PrivilegeMatrixRow.TagSolutionCategory, row.getTagSolutionCategory())
		.setAttrValue(PrivilegeMatrixRow.UserMatchRule, userMatchRule)
		.setAttrValue(PrivilegeMatrixRow.MatchParams, row.getMatchParams());
		return form;
	}
	
	public PrivilegeMatrix convert2PrivilegeMatrix(Form form)throws Exception{
		if(form == null)
			return null;
		PrivilegeMatrix dto = new PrivilegeMatrix();
		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code))
		.setName(form.getString(PrivilegeMatrix.sName))
		.setType(form.getString(PrivilegeMatrix.sType))
		.setDescription(form.getString(PrivilegeMatrix.sDecription))
		.setSettings(convertToTypeList(PrivilegeMatrixRow.class, form.getTable(PrivilegeMatrix.PrivilegeMatrixRow), v->{
			try {
				return convert2PrivilegeMatrixRow(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		return dto;
	}
	
	public PrivilegeMatrixRow convert2PrivilegeMatrixRow(Form form)throws Exception{
		PrivilegeMatrixRow dto = new PrivilegeMatrixRow();
		dto.setUuid(form.getUuid()).setNodeName(form.getString(PrivilegeMatrixRow.NodeName))
		.setIdentify(getAssociationCode(form, PrivilegeMatrixRow.Identify))
		.setDataSolution(getAssociationCode(form, PrivilegeMatrixRow.DataSolution))
		.setDataSolutionCategory(form.getString(PrivilegeMatrixRow.DataSolutionCategory))
		.setActionSolution(getAssociationCode(form, PrivilegeMatrixRow.ActionSolution))
		.setActionSolutionCategory(form.getString(PrivilegeMatrixRow.ActionSolutionCategory))
		.setTagSolution(getAssociationCode(form, PrivilegeMatrixRow.TagSolution))
		.setTagSolutionCategory(form.getString(PrivilegeMatrixRow.TagSolutionCategory))
		.setUserMatchRule(getAssociationCode(form, PrivilegeMatrixRow.UserMatchRule))
		.setMatchParams(form.getString(PrivilegeMatrixRow.MatchParams));
		return dto;
	}
	
	public String getAssociationCode(Form form,String fieldName) throws Exception {
		AssociationData value = form.getAssociation(fieldName);
		if(value == null)
			return null;
		return value.getValue();
	}
	
	public List<String> getAssociationCodes(Form form,String fieldName) throws Exception {
		List<AssociationData> value = form.getAssociations(fieldName);
		if(value == null)
			return new ArrayList<>();
		List<String> codes = new ArrayList<>();
		for(AssociationData data : (List<AssociationData>)value) {
			codes.add(data.getValue());
		}
		return codes;
	}
	
	@Override
	public DataPrivilegeSolution queryDataPrivilegeSolution(IDao dao, String code) throws Exception {
		Form form = IFormMgr.get().queryFormByCode(dao, DataPrivilegeSolution.FormModelId, code);
		return convert2DataPrivilegeSolution(form);
	}
	
	public List<DataPrivilegeSolution> queryDataPrivilegeSolutions(IDao dao, List<String> dataSolutionCodes) throws Exception {
		if(CmnUtil.isCollectionEmpty(dataSolutionCodes))
			return new ArrayList<>();
		Cnd condition = Cnd.NEW();
		condition.and(new SqlExpressionGroup().andInStrList("code", dataSolutionCodes));
		ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, DataPrivilegeSolution.FormModelId, condition, 1, Integer.MAX_VALUE, true, true);
		List<DataPrivilegeSolution> list = convert2List(rs.getDataList(), v->{
			try {
				return convert2DataPrivilegeSolution(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return list;
	}
	
	public List<ActionPrivilegeSolution> queryActionPrivilegeSolutions(IDao dao, List<String> actionSolutionCodes) throws Exception {
		if(CmnUtil.isCollectionEmpty(actionSolutionCodes))
			return new ArrayList<>();
		Cnd condition = Cnd.NEW();
		condition.and(new SqlExpressionGroup().andInStrList("code", actionSolutionCodes));
		ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, ActionPrivilegeSolution.FormModelId, condition, 1, Integer.MAX_VALUE, true, true);
		List<ActionPrivilegeSolution> list = convert2List(rs.getDataList(), v->{
			try {
				return convert2ActionPrivilegeSolution(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return list;
	}
	
	@Override
	public gpf.dc.basic.dto.privilege.DataPrivilegeSolution queryDataPrivilegeSolutionCache(IDao dao, String code)
			throws Exception {
		DataPrivilegeSolution dataSolution = IBasicCacheMgr.get().computeCacheIfAbsent(CacheBlock_DataSolution, code, DataPrivilegeSolution.class,k -> {
			try {
				return queryDataPrivilegeSolution(dao, code);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return dataSolution;
	}
	
	DataPrivilegeSolution convert2DataPrivilegeSolution(Form form)throws Exception{
		if(form == null)
			return null;
		DataPrivilegeSolution dto = new DataPrivilegeSolution();
		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code))
		.setItemSettings(convertToTypeList(DataItemPrivilegeSetting.class, form.getTable(DataPrivilegeSolution.ItemSettings), v->{
			try {
				return convert2DataPrivilegeSetting(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		return dto;
	}
	
	DataItemPrivilegeSetting convert2DataPrivilegeSetting(Form form)throws Exception{
		DataItemPrivilegeSetting setting = new DataItemPrivilegeSetting();
		setting.setUuid(form.getUuid()).setCategory(form.getString(DataItemPrivilegeSetting.Category))
		.setItem(form.getString(DataItemPrivilegeSetting.Item)).setRules(getAssociationCodes(form, DataItemPrivilegeSetting.Rule))
		.setRuleParams(form.getString(DataItemPrivilegeSetting.RuleParams));
		return setting;
	}
	
	@Override
	public Form convertDataPrivilegeSolution2Form(DataPrivilegeSolution dataSolution) throws Exception {
		Form form = new Form(DataPrivilegeSolution.FormModelId);
		form.setUuid(dataSolution.getUuid())
		.setAttrValueByCode(Form.Code, dataSolution.getCode())
		.setAttrValue(DataPrivilegeSolution.ItemSettings, convertToTableData(dataSolution.getItemSettings(), v->{
			try {
				return convertDataItemPrivilegeSeting2Form(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}))
		.setAttrValue(Form.Owner, dataSolution.getOwner());
		return form;
	}
	
	public Form convertDataItemPrivilegeSeting2Form(DataItemPrivilegeSetting setting)throws Exception{
		Form form = new Form(DataItemPrivilegeSetting.FormModelId);
		form.setUuid(setting.getUuid())
		.setAttrValue(DataItemPrivilegeSetting.Item, setting.getItem())
		.setAttrValue(DataItemPrivilegeSetting.Category, setting.getCategory())
		.setAttrValue(DataItemPrivilegeSetting.RuleParams, setting.getRuleParams())
		;
		List<AssociationData> rules = new ArrayList<>();
		setting.getRules().forEach(v->
			rules.add(new AssociationData(PrivilegeRuleDefine.FormModelId,v))
			);
		form.setAttrValue(DataItemPrivilegeSetting.Rule, rules);
		return form;
	}
	
	@Override
	public ActionPrivilegeSolution queryActionPrivilegeSolution(IDao dao, String code) throws Exception {
		Form form = IFormMgr.get().queryFormByCode(dao, ActionPrivilegeSolution.FormModelId, code);
		return convert2ActionPrivilegeSolution(form);
	}
	
	@Override
	public gpf.dc.basic.dto.privilege.ActionPrivilegeSolution queryActionPrivilegeSolutionCache(IDao dao, String code)
			throws Exception {
		ActionPrivilegeSolution actionSolution =IBasicCacheMgr.get().computeCacheIfAbsent(CacheBlock_ActionSolution, code, ActionPrivilegeSolution.class,k -> {
			try {
				return queryActionPrivilegeSolution(dao, code);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		});
		return actionSolution;
	}
	
	ActionPrivilegeSolution convert2ActionPrivilegeSolution(Form form)throws Exception{
		if(form == null)
			return null;
		ActionPrivilegeSolution dto = new ActionPrivilegeSolution();
		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code))
		.setItemSettings(convertToTypeList(ActionItemPrivilegeSetting.class, form.getTable(ActionPrivilegeSolution.ItemSettings), v->{
			try {
				return convert2ActionItemPrivilegeSetting(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		return dto;
	}
	
	ActionItemPrivilegeSetting convert2ActionItemPrivilegeSetting(Form form)throws Exception{
		ActionItemPrivilegeSetting setting = new ActionItemPrivilegeSetting();
		setting.setUuid(form.getUuid()).setCategory(form.getString(ActionItemPrivilegeSetting.Category))
		.setItem(form.getString(ActionItemPrivilegeSetting.Item)).setRules(getAssociationCodes(form, ActionItemPrivilegeSetting.Rule))
		.setRuleParams(form.getString(ActionItemPrivilegeSetting.RuleParams));
		return setting;
	}
	
	@Override
	public Form convertActionPrivilegeSolution2Form(ActionPrivilegeSolution solution) throws Exception {
		Form form = new Form(ActionPrivilegeSolution.FormModelId);
		form.setUuid(solution.getUuid())
		.setAttrValueByCode(Form.Code, solution.getCode())
		.setAttrValue(ActionPrivilegeSolution.ItemSettings, convertToTableData(solution.getItemSettings(), v->{
			try {
				return convertActionItemPrivilegeSeting2Form(v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}))
		.setAttrValue(Form.Owner, solution.getOwner());
		return form;
	}
	
	public Form convertActionItemPrivilegeSeting2Form(ActionItemPrivilegeSetting setting)throws Exception{
		Form form = new Form(ActionItemPrivilegeSetting.FormModelId);
		form.setUuid(setting.getUuid())
		.setAttrValue(ActionItemPrivilegeSetting.Item, setting.getItem())
		.setAttrValue(ActionItemPrivilegeSetting.Category, setting.getCategory())
		.setAttrValue(ActionItemPrivilegeSetting.RuleParams, setting.getRuleParams())
		;
		List<AssociationData> rules = new ArrayList<>();
		NullUtil.get(setting.getRules()).forEach(v->
			rules.add(new AssociationData(PrivilegeRuleDefine.FormModelId,v))
			);
		form.setAttrValue(ActionItemPrivilegeSetting.Rule, rules);
		return form;
	}

	@Override
	public IdentifyMatchParam calculateMatchUserRule(String rule, String ruleParams,Set<String> namespaces, Map<String, Object> env)
			throws Exception {
		long start = System.currentTimeMillis();
		String expression = null;
		//这里处理了规则，即使不配置关联的匹配规则，也可以通过规则参数运行，用来兼容新规则不在默认的用户匹配规则中定义的
		if(CmnUtil.isStringEmpty(rule)) {
			expression = ruleParams;
		}else {
			expression = rule + "(" + CmnUtil.getString(ruleParams,"") + ")";
		}
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.debug("[表达式]：" + expression);
		if(CmnUtil.isStringEmpty(expression)) {
			//表达式为空时默认没权限
			return new IdentifyMatchParam().setMatchExpression("false");
		}
		List<String> variableNames = IExpressionMgr.get().parseVariableNames(expression);
		for(String variable : variableNames) {
			if(env.containsKey(variable))
				continue;
			if(variable.startsWith(GlobalVariableDto.VarPrefix)) {
				String variableCode = variable.substring(1, variable.length());
				Object value = IGlobalVariableMgr.get().getVariableValue(variableCode,env);
				env.put(variable, value);
			}else {
				env.put(variable, variable);
			}
		}
		long end = System.currentTimeMillis();
		tracer.debug("[表达式]：运行参数筹备：" + (end - start)+"ms");
		start= System.currentTimeMillis();
		Object result = IExpressionMgr.get().execute(namespaces,env, expression);
		end = System.currentTimeMillis();
		tracer.debug("[表达式]：运行耗时" + (end - start)+"ms");
		if(result instanceof IdentifyMatchParam) {
			return (IdentifyMatchParam) result;
		}else {
			throw new ExpressionException("表达式："+expression+"执行结果不是MatchQueryParam类型！");
		}
	}
	
	@Override
	public ResultSetQueryParam _doBuildApppendViewsByIdentifyMatchQueryParam(Map<String, IdentifyMatchParam> identifyMap,AtomicInteger appendAliasCnt,boolean useFieldName,Map<String,String> regexMap,Map<String,String> modelAliasMap,Map<String,JoinViewInfo> allJoinViews)
			throws Exception {
		//还要处理关联视图存在同名冲突的处理
		ISqlMappingMgr sqlMgr = ISqlMappingMgr.get();
		//追加的关联查询视图
		Map<String,ViewSqlDto> appendViewSqls = new LinkedHashMap<>();
		//追加的关联视图匹配
		Set<String> appendViewAliases = new LinkedHashSet<>();
		//身份匹配表达式
		StringBuffer orExpr = new StringBuffer();
		List<String> roleCaseWhenExpressions = new ArrayList<>();
		for(String roleCode : identifyMap.keySet()) {
			IdentifyMatchParam expr = identifyMap.get(roleCode);
			String roleExpression = expr.getMatchExpression();
			//根据身份匹配参数构建所需的关联查询视图，有可能视图在其他身份匹配参数中已经构建过
			Map<String,JoinViewInfo> joinViews = expr.getJoinViews();
			for(String viewName : joinViews.keySet()) {
				JoinViewInfo joinView = joinViews.get(viewName);
				//关联视图已添加，这里跳过添加
				if(allJoinViews.containsKey(joinView.getViewName())) {
					String joinViewName = joinView.getViewName();
					int viewNameCnt = 0;
					boolean isJoinViewExist = false;
					JoinViewInfo replaceJoinView = ToolUtilities.clone(joinView);
					do{
						if(allJoinViews.get(replaceJoinView.getViewName()).equals(replaceJoinView)) {
							isJoinViewExist = true;
							break;
						}
						joinViewName = joinView.getViewName() + (++viewNameCnt);
						replaceJoinView.setViewName(joinViewName);
					}while(allJoinViews.containsKey(joinViewName)); 
					if(isJoinViewExist) {
						Map<String,String> innerViewName2ModelIds = replaceJoinView.getInnerViewName2Model();
						Map<String,String> innerRegexMap = new LinkedHashMap<>();
						for(String innerViewName : innerViewName2ModelIds.keySet()) {
							String modelId = innerViewName2ModelIds.get(innerViewName);
							innerRegexMap.put(quoteAsVariable(innerViewName), modelAliasMap.get(modelId));
						}
						innerRegexMap.put(quoteAsVariable(joinView.getViewName()), modelAliasMap.get(joinView.getViewName()));
						roleExpression = ToolUtilities.replaceAll(roleExpression, innerRegexMap);
						
						continue;
					}else {
						//关联视图名称出现冲突了，将关联视图名称以及SQL中的变量进行替换，在做后面的处理
						Map<String,String> replaceViewRegexMap = new LinkedHashMap<>();
						replaceViewRegexMap.put(quoteAsVariable(joinView.getViewName()), quoteAsVariable(replaceJoinView.getViewName()));
						roleExpression = ToolUtilities.replaceAll(roleExpression, replaceViewRegexMap);
						joinView = replaceJoinView;
					}
				}
				allJoinViews.put(joinView.getViewName(), joinView);
				Map<String,String> innerViewName2ModelIds = joinView.getInnerViewName2Model();
				Map<String,String> innerRegexMap = new LinkedHashMap<>();
				for(String innerViewName : innerViewName2ModelIds.keySet()) {
					String modelId = innerViewName2ModelIds.get(innerViewName);
					//关联视图依赖的其他关联模型视图
					if(modelAliasMap.containsKey(modelId)) {
						//视图变量名与关联模型视图别名的参数映射
						innerRegexMap.put(quoteAsVariable(innerViewName), modelAliasMap.get(modelId));
					}else {
						String modelAlias = "T"+(appendAliasCnt.incrementAndGet());
						modelAliasMap.put(modelId, modelAlias);
						List<FormField> jionViewFields = new ArrayList<>();
						if(IPDFMgr.get().isPDF(modelId)) {
							//TODO 关联流程视图
						}else {
							FormModel model = IFormMgr.get().queryFormModel(modelId,false);
							if(model == null)
								throw new VerifyException("关联模型不存在：" + modelId);
							String modelSql = sqlMgr.buildSqlOfFormModel(model, useFieldName);
							appendViewSqls.put(modelAlias, new ViewSqlDto().setAlias(modelAlias).setQuerySql(modelSql).setViewName(innerViewName));
							jionViewFields = model.getNotHiddenFieldList();
							//视图变量名与关联模型视图别名的参数映射
							innerRegexMap.put(quoteAsVariable(innerViewName), modelAlias);
						}
						for(FormField field : jionViewFields) {
							String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
							regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
							//							innerRegexMap.put("#"+field.getName() +"#", fieldColumn);
						}
					}
					
				}
				String viewSql = ToolUtilities.replaceAll(joinView.getViewSql(), innerRegexMap);
				viewSql = ToolUtilities.replaceAll(viewSql, regexMap);
				String joinAlias = "T"+(appendAliasCnt.incrementAndGet());
				modelAliasMap.put(joinView.getViewName(), joinAlias);
				regexMap.put(quoteAsVariable(joinView.getViewName()), joinAlias);
				innerRegexMap.put(quoteAsVariable(joinView.getViewName()), joinAlias);
				appendViewSqls.put(joinView.getViewName(), new ViewSqlDto().setAlias(joinAlias).setQuerySql(viewSql).setViewName(joinView.getViewName()));
				roleExpression = ToolUtilities.replaceAll(roleExpression, innerRegexMap);
			}
			if(!CmnUtil.isStringEmpty(expr.getMainJoinViewName())) {
				if(appendViewSqls.get(expr.getMainJoinViewName()) != null) {
					String joinAlias = appendViewSqls.get(expr.getMainJoinViewName()).getAlias();
					appendViewAliases.add(joinAlias);
				}
			}
			if(orExpr.length() > 0)
				orExpr.append( " OR ");
			orExpr.append(roleExpression);
			//这里的角色计算表达式用来计算当前行数据对应的角色有哪些，其中#分类条件# 表示 通过权限矩阵计算得到的分类匹配条件，如 #表单#.#节点名称# = '#分类#',#分类#表示 当前权限方案所对应的分类
			roleCaseWhenExpressions.add("CASE WHEN #分类条件# AND (" + roleExpression + ") THEN '#分类#:"+roleCode+"' END");
		}
		ResultSetQueryParam resultQuery = new ResultSetQueryParam();
		resultQuery.setAppendViewAliases(appendViewAliases);
		resultQuery.setAppendViewSqls(appendViewSqls);
		resultQuery.setPrivilegeExpression(orExpr.toString());
		resultQuery.setRoleCaseWhenExpressions(roleCaseWhenExpressions);
		return resultQuery;
	}
	
	@Override
	public String quoteAsVariable(String name) {
		return "#" + name + "#";
	}
		
	@Override
	public FormPrivilegeDto caculateFormPrivilege(Set<String> namespaces,Map<String,Object> env,DataPrivilegeSolution dataSolution, String dataCategory,
			ActionPrivilegeSolution actionSolution, String actionCategoy,boolean useDefaultCategory) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		Map<String,FieldPrivilegeDto> fieldPrivMap = new LinkedHashMap<>();
		if(dataSolution != null) {
			tracer.logStart();
			Map<String, DataItemPrivilegeSetting> dataSettings = dataSolution.getItemSettingsByCategory(dataCategory,useDefaultCategory);
			tracer.printCost("dataSolution.getItemSettingsByCategory", false);
			if(dataSettings == null) {
				throw new VerifyException(GpfDCBasicI18n.getString("未找到分类[{1}]的数据权限方案配置！",dataCategory)) ;
			}
			Map<String,FieldPrivilegeDto> expressionResults = new LinkedHashMap<>();
			for(DataItemPrivilegeSetting setting : dataSettings.values()) {
				String field = allocateFieldCode(setting.getItem());
				List<String> rules = setting.getRules();
				String ruleParams = setting.getRuleParams();
//				String[] rules = rule.split(",");
				FieldPrivilegeDto totalFieldPriv = new FieldPrivilegeDto().setField(field)
						.setFieldName(setting.getItem());
				for(String r : rules) {
					if(CmnUtil.isStringEqual(r, "*")) {
						continue;
					}
					String ruleExpr = r+"()";
					if(expressionResults.containsKey(ruleExpr)) {
						tracer.debug(LOG,setting.getItem()+":"+ruleExpr +",cached");
						FieldPrivilegeDto cacheFieldPriv = expressionResults.get(ruleExpr);
						megerFieldPrivilege(totalFieldPriv, cacheFieldPriv);
						continue;
					}
					if(ruleParams != null && ruleParams.contains(r+"(")) {
						continue;
					}
					//当前权限表达式结果是否可缓存
					env.put(PrivilegeRuleIntf.Key_RuleResultCachable, true);
					PrivilegeRuleEnum ruleEnum = PrivilegeRuleEnum.formValue(r);
					FieldPrivilegeDto fieldPriv = new FieldPrivilegeDto().setField(field)
							.setFieldName(setting.getItem());
					if(ruleEnum != null) {
						PrivilegeRuleEnum.setFieldPrivielge(ruleEnum, fieldPriv);
					}else {
//						LvUtil.trace(setting.getItem()+":"+ruleExpr);
						env.put(PrivilegeRuleIntf.Key_Privilege, fieldPriv);
						setPrivilege(namespaces,env, ruleExpr);
					}
					megerFieldPrivilege(totalFieldPriv, fieldPriv);
					boolean ruleResultCachable = CmnUtil.getBoolean(env.get(PrivilegeRuleIntf.Key_RuleResultCachable),false);
					if(ruleResultCachable) {
						expressionResults.put(ruleExpr, fieldPriv);
					}
				}
				if(!CmnUtil.isStringEmpty(ruleParams)) {
					if(expressionResults.containsKey(ruleParams)) {
						tracer.debug(LOG,setting.getItem()+":"+ruleParams +",cached");
						FieldPrivilegeDto cacheFieldPriv = expressionResults.get(ruleParams);
						megerFieldPrivilege(totalFieldPriv, cacheFieldPriv);
					}else {
						tracer.debug(LOG,setting.getItem()+":"+ruleParams);
						FieldPrivilegeDto fieldPriv = new FieldPrivilegeDto().setField(field)
								.setFieldName(setting.getItem());
						env.put(PrivilegeRuleIntf.Key_Privilege, fieldPriv);
						setPrivilege(namespaces,env, ruleParams);
						megerFieldPrivilege(totalFieldPriv, fieldPriv);
						boolean ruleResultCachable = CmnUtil.getBoolean(env.get(PrivilegeRuleIntf.Key_RuleResultCachable),false);
						if(ruleResultCachable) {
							expressionResults.put(ruleParams, fieldPriv);
						}
					}
				}
				fieldPrivMap.put(setting.getItem(), totalFieldPriv);
			}
		}
		Map<String,ActionPrivilegeDto> actionPrivMap = new LinkedHashMap<>();
		if(actionSolution != null) {
			Map<String, ActionItemPrivilegeSetting> actionSetting = actionSolution.getItemSettingsByCategory(actionCategoy,useDefaultCategory);
			if(actionSetting == null) {
				throw new VerifyException(GpfDCBasicI18n.getString("未找到分类[{1}]的动作权限方案配置！",dataCategory)) ;
			}
			Map<String,ActionPrivilegeDto> expressionResults = new LinkedHashMap<>();
			for(ActionItemPrivilegeSetting setting : actionSetting.values()) {
				List<String> rules = setting.getRules();
				String ruleParams = setting.getRuleParams();
				
				ActionPrivilegeDto totalActionPriv = new ActionPrivilegeDto().setName(setting.getItem());
				for(String r : rules) {
					String ruleExpr = r+"()";
					if(expressionResults.containsKey(ruleExpr)) {
						tracer.debug(LOG,setting.getItem()+":"+ruleExpr +",cached");
						ActionPrivilegeDto cachePriv = expressionResults.get(ruleExpr);
						megerActionPrivilege(totalActionPriv, cachePriv);
						continue;
					}
					if(ruleParams != null && ruleParams.contains(r+"(")) {
						continue;
					}
					env.put(PrivilegeRuleIntf.Key_RuleResultCachable, true);
					PrivilegeRuleEnum ruleEnum = PrivilegeRuleEnum.formValue(r);
					ActionPrivilegeDto actionPriv = new ActionPrivilegeDto().setName(setting.getItem());
					if(ruleEnum != null) {
						PrivilegeRuleEnum.setActionPrivielge(ruleEnum, actionPriv);
					}else {
//						LvUtil.trace(setting.getItem()+":"+ruleExpr);
						env.put(PrivilegeRuleIntf.Key_Privilege, actionPriv);
						setPrivilege(namespaces,env, ruleExpr);
					}
					megerActionPrivilege(totalActionPriv, actionPriv);

					boolean ruleResultCachable = CmnUtil.getBoolean(env.get(PrivilegeRuleIntf.Key_RuleResultCachable),false);
					if(ruleResultCachable) {
						expressionResults.put(ruleExpr, actionPriv);
					}
				}
				if(!CmnUtil.isStringEmpty(ruleParams)) {
					if(expressionResults.containsKey(ruleParams)) {
						tracer.debug(LOG,setting.getItem()+":"+ruleParams +",cached");
						ActionPrivilegeDto cachePriv = expressionResults.get(ruleParams);
						megerActionPrivilege(totalActionPriv, cachePriv);
					}else {
						tracer.debug(LOG,setting.getItem()+":"+ruleParams);
						ActionPrivilegeDto actionPriv = new ActionPrivilegeDto().setName(setting.getItem());
						env.put(PrivilegeRuleIntf.Key_Privilege, actionPriv);
						setPrivilege(namespaces,env, ruleParams);
						megerActionPrivilege(totalActionPriv, actionPriv);

						boolean ruleResultCachable = CmnUtil.getBoolean(env.get(PrivilegeRuleIntf.Key_RuleResultCachable),false);
						if(ruleResultCachable) {
							expressionResults.put(ruleParams, actionPriv);
						}
					}
				}
				actionPrivMap.put(setting.getItem(), totalActionPriv);
			}
		}
		FormPrivilegeDto formPriv = new FormPrivilegeDto();
		formPriv.setFieldPrivileges(new ArrayList<>(fieldPrivMap.values()));
		formPriv.setActionPrivileges(new ArrayList<>(actionPrivMap.values()));
		return formPriv;
	}
	
	public void megerFieldPrivilege(FieldPrivilegeDto totalFieldPriv,FieldPrivilegeDto newFieldPriv) {
		if(newFieldPriv.isVisible())
			totalFieldPriv.setVisible(newFieldPriv.isVisible());
		if(newFieldPriv.isWritable())
			totalFieldPriv.setWritable(newFieldPriv.isWritable());
	}
	
	public void megerActionPrivilege(ActionPrivilegeDto totalFieldPriv,ActionPrivilegeDto newFieldPriv) {
		if(newFieldPriv.isVisible())
			totalFieldPriv.setVisible(newFieldPriv.isVisible());
		if(newFieldPriv.isOperatable())
			totalFieldPriv.setOperatable(newFieldPriv.isOperatable());
	}
	
	public void setPrivilege(Set<String> namespaces,Map<String,Object> env,String expression) throws Exception {
//		long start = System.currentTimeMillis();
		List<String> variableNames = IExpressionMgr.get().parseVariableNames(expression);
		for(String variable : variableNames) {
			if(env.containsKey(variable))
				continue;
			if(variable.startsWith(GlobalVariableDto.VarPrefix)) {
				String variableCode = variable.substring(1, variable.length());
				Object value = IGlobalVariableMgr.get().getVariableValue(variableCode,env);
				env.put(variable, value);
			}else {
				env.put(variable, variable);
			}
		}
		IExpressionMgr.get().execute(namespaces,env, expression);
//		long end = System.currentTimeMillis();
//		System.out.println("setPrivilege:"+(end -start) + "ms");
	}

	@Override
	public Map<String,Map<String,IdentifyMatchParam>> buildAllMatchUserExpressions(PrivilegeMatrix matrix, Set<String> nodeNames,
			String user,Set<String> namespaces, Map<String, Object> env) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		Map<String,Map<String,PrivilegeMatrixRow>> rowMap = matrix.getPrivilegeMatrixSettingMap();
//		StringBuffer totalOrExpr = new StringBuffer();
		Map<String,Map<String,IdentifyMatchParam>> exprMap = new LinkedHashMap<>();
		for(String nodeName : nodeNames) {
			Map<String,PrivilegeMatrixRow> identifyRow = rowMap.get(nodeName);
			if(identifyRow == null) {
				tracer.warning("未找到分类["+nodeName+"]的权限配置");
				continue;
			}
//			StringBuffer stepCond = new StringBuffer();
//			stepCond.append(" #表单#.#节点名称# = '" + nodeName + "'");
//			StringBuffer orExpr = new StringBuffer();
			//节点中所有身份的匹配表达式，构成表单数据的匹配规则
			for(String identify : identifyRow.keySet()) {
				PrivilegeMatrixRow row = identifyRow.get(identify);
				if(CmnUtil.isStringEmpty(row.getUserMatchRule())
						&& CmnUtil.isStringEmpty(row.getMatchParams()))
					continue;
				IdentifyMatchParam queryParams = calculateMatchUserRule(row.getUserMatchRule(), row.getMatchParams(), namespaces,env);
//				if(orExpr.length() > 0)
//					orExpr.append( " OR ");
//				orExpr.append(queryParams.getMatchExpression());
				exprMap.computeIfAbsent(nodeName, k->new LinkedHashMap<>())
					.put(identify, queryParams)
					;
			}
//			stepCond.append(" AND ( " + orExpr.toString() + " ) ");
//			exprMap.put(nodeName, new Static(str))
		}
		return exprMap;
	}
	
	@Override
	public String buildMatchUserQuerySql(IDCRuntimeContext rtx, String orgModelId,String userModelId,Set<String> namespaces, String privilegeMatrixCode,
			String nodeName,Form form) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String node = rtx.getRefPDCNode().getName();
		PrivilegeMatrix privilegeMatrix = queryPrivilegeMatrixCache(rtx.getDao(), privilegeMatrixCode);
		Map<String,Map<String,PrivilegeMatrixRow>> allMatrixRowMap = privilegeMatrix.getPrivilegeMatrixSettingMap();
		Map<String, PrivilegeMatrixRow> matrixRowMap = allMatrixRowMap.get(node);
		
		boolean useFieldName = true;
		
		List<FormField> meta = IPDFRuntimeMgr.get().queryPDFFormFields(rtx.getPdfUuid());
		PDCForm pdcForm = rtx.getPdcForm();
		String alias = SqlMappingConst.Form;
		String aliasLabel = SqlMappingConst.FormLabel;
		Map<String,String> cteSqls = ISqlMappingMgr.get().buildCteSqlOfForm(meta, pdcForm, alias, aliasLabel, useFieldName);
		
		Map<String,ViewSqlDto> viewSqls = new LinkedHashMap<>();
		AtomicInteger appendAliasCnt= new AtomicInteger(0);
		for(String viewName : cteSqls.keySet()) {
			ViewSqlDto viewSql = new ViewSqlDto();
			viewSql.setAlias("T"+(appendAliasCnt.incrementAndGet())).setViewName(viewName).setQuerySql(cteSqls.get(viewName));
			viewSqls.put(viewName, viewSql);
		}
		Map<String,Object> env = new LinkedHashMap<>();
		MatchIentifyRuleIntf.prepareEnv(env, rtx, meta, form,viewSqls, useFieldName,orgModelId,userModelId);
		Map<String,IdentifyMatchParam> identifyExps = new LinkedHashMap<>();
		
		for(String identify : matrixRowMap.keySet()) {
			PrivilegeMatrixRow matrixRow = matrixRowMap.get(identify);
			String userMatchRule = matrixRow.getUserMatchRule();
			if(CmnUtil.isStringEmpty(userMatchRule))
				continue;
			String ruleParams = matrixRow.getMatchParams();
			IdentifyMatchParam queryParams = calculateMatchUserRule(userMatchRule, ruleParams, namespaces,env);
			identifyExps.put(identify, queryParams);
		}
		
		Map<String,String> modelAliasMap = new LinkedHashMap<>();
		Map<String,JoinViewInfo> allJoinViews = new LinkedHashMap<>();
		
		FormModel userModel = IUserMgr.get().queryUserModel(userModelId);
		ISqlMappingMgr sqlMgr = ISqlMappingMgr.get();
		Map<String,String> regexMap = new LinkedHashMap<>();
		regexMap.put(quoteAsVariable("表单"), "allData");
		for(FormField field : meta) {
			String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
			regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
		}
		
		ResultSetQueryParam nodeRsQuery = _doBuildApppendViewsByIdentifyMatchQueryParam(identifyExps, appendAliasCnt, useFieldName, regexMap, modelAliasMap,allJoinViews);
		
		tracer.info(LOG,"当前用户：" + rtx.getOperator());
		StringBuffer withSql = new StringBuffer();
		withSql.append("WITH " );
		String mainAlias = viewSqls.get(aliasLabel).getAlias();
		withSql.append(mainAlias + " AS (" + viewSqls.get(aliasLabel).getQuerySql() +")");
		for(ViewSqlDto appendView : nodeRsQuery.getAppendViewSqls().values()) {
			regexMap.put(quoteAsVariable(appendView.getViewName()), appendView.getAlias());
			withSql.append("\n , "+appendView.getAlias() + " AS (" + appendView.getQuerySql() + ")");
		}
		StringBuffer formAlias = new StringBuffer();
		formAlias.append("SELECT * FROM " + userModel.getTableName() + " ");
		String matchSql = formAlias.toString();
		StringBuffer querySql = new StringBuffer();
		
		tracer.info(LOG,"权限表达式："+querySql.toString());
		for(String viewName : viewSqls.keySet()) {
			regexMap.put(quoteAsVariable(viewName), viewSqls.get(viewName).getAlias());
		}
		for(FormField field : meta) {
			String fieldColumn = sqlMgr.quoteColumn(field.getCode(), field.getName(), useFieldName);
			regexMap.put(quoteAsVariable(field.getName()), fieldColumn);
		}
		String matchIdentifySql = withSql + "\n" + matchSql;
		matchIdentifySql = ToolUtilities.replaceAll(matchIdentifySql, regexMap);
		tracer.info(LOG,matchIdentifySql);
		return matchIdentifySql;
	}

	@Override
	public Pair<String, byte[]> exportPrivilegeMatrixToExcel(String privilegeMatrixCode) throws Exception {
		try(IDao dao = IDaoService.newIDao()){
			Map<String,String> identifyMap = getIdentifyMap();
			PrivilegeMatrix matrix = queryPrivilegeMatrixByCode(dao, privilegeMatrixCode);
			List<PrivilegeMatrixRow> rows = matrix.getSettings();
			List<PrivilegeMatrixExcelRow> excelRows = new ArrayList<>();
			Set<String> dataSolutionCodes = new LinkedHashSet<>();
			Set<String> actionSolutionCodes = new LinkedHashSet<>();
			for(PrivilegeMatrixRow row : rows) {
				if(!CmnUtil.isStringEmpty(row.getDataSolution())) {
					dataSolutionCodes.add(row.getDataSolution());
				}
				if(!CmnUtil.isStringEmpty(row.getActionSolution())) {
					actionSolutionCodes.add(row.getActionSolution());
				}
				PrivilegeMatrixExcelRow excelRow = PrivilegeMatrixExcelRow.toPrivilegeMatrixExcelRow(privilegeMatrixCode,row, identifyMap);
				excelRows.add(excelRow);
			}
			List<PrivilegeSolutionExcelRow> solutionExcelRows = new ArrayList<>();
			List<DataPrivilegeSolution> dataSolutions = queryDataPrivilegeSolutions(dao,new ArrayList<>(dataSolutionCodes));
			for(DataPrivilegeSolution solution : dataSolutions) {
				PrivilegeSolutionExcelRow dataSolutionExcelRow = PrivilegeSolutionExcelRow.toPrivilegeSolutionExcelRow(privilegeMatrixCode, solution);
				solutionExcelRows.add(dataSolutionExcelRow);
			}
			
			List<ActionPrivilegeSolution> actionSolutions = queryActionPrivilegeSolutions(dao,new ArrayList<>(actionSolutionCodes));
			for(ActionPrivilegeSolution solution : actionSolutions) {
				PrivilegeSolutionExcelRow actionSolutionExcelRow = PrivilegeSolutionExcelRow.toPrivilegeSolutionExcelRow(privilegeMatrixCode, solution);
				solutionExcelRows.add(actionSolutionExcelRow);
			}
			
			URL url = ClassFactory.getResourceURL("resource/template/PDFTemplate.xlsx");
			InputStream ins = url.openStream();
			ExcelReader sampleReader = ExcelUtil.getReader(ins);
			File excelFile = new File("./temp/PrivilegeMatrix"+ToolUtilities.allockUUIDWithUnderline()+".xlsx");
			Workbook wb = WorkbookUtil.createBookForWriter(excelFile);
			try {
				PrivilegeMatrixExcelWriter writer = new PrivilegeMatrixExcelWriter(wb,sampleReader);
				writer.write(excelRows);
				//关闭writer，释放内存
				int startRow = writer.getCurrentRow();
				writer.flush(excelFile);
				PrivilegeSolutionExcelWriter writer2 = new PrivilegeSolutionExcelWriter(startRow, wb, sampleReader);
				writer2.write(solutionExcelRows);
				writer2.flush(excelFile);
				byte[] content = Utils.getFileBytes(excelFile);
				return new Pair<String, byte[]>(privilegeMatrixCode+".xlsx", content);
			}finally {
				Utils.close(ins);
//				ToolUtilities.deleteFile(excelFile);
				IOUtils.closeQuietly(sampleReader);
				IOUtils.closeQuietly(wb);
			}
		}

	}
	@Override
	public void writePrivilegeMatrixPackage(Progress prog,String destFilePath,Workbook wb,PrivilegeMatrixPackage privilegeMatrixPack)throws Exception{
		try(IDao dao = IDaoService.newIDao()){
			Map<String,String> identifyMap = getIdentifyMap();
			PrivilegeMatrix matrix = privilegeMatrixPack.getMatrix();
			String privilegeMatrixCode = matrix.getCode();
			List<PrivilegeMatrixRow> rows = matrix.getSettings();
			List<PrivilegeMatrixExcelRow> excelRows = new ArrayList<>();
			for(PrivilegeMatrixRow row : rows) {
				PrivilegeMatrixExcelRow excelRow = PrivilegeMatrixExcelRow.toPrivilegeMatrixExcelRow(privilegeMatrixCode,row, identifyMap);
				excelRows.add(excelRow);
			}
			List<PrivilegeSolutionExcelRow> solutionExcelRows = new ArrayList<>();
			List<DataPrivilegeSolution> dataSolutions = privilegeMatrixPack.getDataSolutions();
			for(DataPrivilegeSolution solution : dataSolutions) {
				PrivilegeSolutionExcelRow dataSolutionExcelRow = PrivilegeSolutionExcelRow.toPrivilegeSolutionExcelRow(privilegeMatrixCode, solution);
				solutionExcelRows.add(dataSolutionExcelRow);
			}
			
			List<ActionPrivilegeSolution> actionSolutions = privilegeMatrixPack.getActionSolution();
			for(ActionPrivilegeSolution solution : actionSolutions) {
				PrivilegeSolutionExcelRow actionSolutionExcelRow = PrivilegeSolutionExcelRow.toPrivilegeSolutionExcelRow(privilegeMatrixCode, solution);
				solutionExcelRows.add(actionSolutionExcelRow);
			}
			
			URL url = ClassFactory.getResourceURL("resource/template/PDFTemplate.xlsx");
			InputStream ins = url.openStream();
			ExcelReader sampleReader = ExcelUtil.getReader(ins);
			try {
				PrivilegeMatrixExcelWriter writer = new PrivilegeMatrixExcelWriter(wb,sampleReader);
				writer.write(excelRows);
				writer.flush(new File(destFilePath));
				//关闭writer，释放内存
				int startRow = writer.getCurrentRow();
				PrivilegeSolutionExcelWriter writer2 = new PrivilegeSolutionExcelWriter(startRow, wb, sampleReader);
				writer2.write(solutionExcelRows);
				writer2.flush(new File(destFilePath));
			}finally {
//				ToolUtilities.deleteFile(excelFile);
				Utils.close(ins);
				IOUtils.closeQuietly(sampleReader);
				IOUtils.closeQuietly(wb);
			}
		}
	}
	
	public Map<String,String> getIdentifyMap() throws Exception{
		try(IDao dao = IDaoService.newIDao()){
			Cnd cnd = Cnd.where(Exps.isNull(Role.Owner));
			ResultSet<Role> rs = IRoleMgr.get().queryRolePage(dao, cnd, 1, Integer.MAX_VALUE);
			return rs.getDataList().stream().collect(Collectors.toMap(Role::getCode, v->v.getLabel(),(e,r)->r));
		}
	}
	
	public Map<String,String> getIdentifyLabelMap() throws Exception{
		try(IDao dao = IDaoService.newIDao()){
			Cnd cnd = Cnd.where(Exps.isNull(Role.Owner));
			ResultSet<Role> rs = IRoleMgr.get().queryRolePage(dao, cnd, 1, Integer.MAX_VALUE);
			return rs.getDataList().stream().collect(Collectors.toMap(Role::getLabel, v->v.getCode(),(e,r)->r));
		}
	}
	
	public Set<String> getRuleDefs(IDao dao) throws Exception{
		Set<String> ruleDefs = new LinkedHashSet<>();
		ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, MatchUserRuleDefine.FormModelId, null, 1, Integer.MAX_VALUE);
		rs.getDataList().forEach(v->{
			try {
				ruleDefs.add(v.getStringByCode(Form.Code));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		rs = IFormMgr.get().queryFormPageWithoutNesting(dao, PrivilegeRuleDefine.FormModelId, null, 1, Integer.MAX_VALUE);
		rs.getDataList().forEach(v->{
			try {
				ruleDefs.add(v.getStringByCode(Form.Code));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return ruleDefs;
	}
	
	@Override
	public PrivilegeMatrixPackage queryPrivilegeMatrixPackage(IDao dao, String uuid) throws Exception {
		Form form = IFormMgr.get().queryForm(dao, PrivilegeMatrix.FormModelId, uuid);
		PrivilegeMatrix matrix = convert2PrivilegeMatrix(form);
		return queryPrivilegeMatrixPackage(dao,matrix);
	}
	@Override
	public PrivilegeMatrixPackage queryPrivilegeMatrixPackageByCode(IDao dao, String code) throws Exception {
		Form form = IFormMgr.get().queryFormByCode(dao, PrivilegeMatrix.FormModelId, code);
		PrivilegeMatrix matrix = convert2PrivilegeMatrix(form);
		return queryPrivilegeMatrixPackage(dao,matrix);
	}
	
	private PrivilegeMatrixPackage queryPrivilegeMatrixPackage(IDao dao,PrivilegeMatrix matrix)throws Exception{
		if(matrix == null)
			return null;
		PrivilegeMatrixPackage pack = new PrivilegeMatrixPackage();
		pack.setMatrix(matrix);
		Set<String> dataSolutionCodes = new LinkedHashSet<>();
		Set<String> actionSolutionCodes = new LinkedHashSet<>();
		for(PrivilegeMatrixRow row : NullUtil.get(matrix.getSettings())) {
			if(!CmnUtil.isStringEmpty(row.getDataSolution())) {
				dataSolutionCodes.add(row.getDataSolution());
			}
			if(!CmnUtil.isStringEmpty(row.getActionSolution())) {
				actionSolutionCodes.add(row.getActionSolution());
			}
		}
		List<DataPrivilegeSolution> dataSolutions = queryDataPrivilegeSolutions(dao, new ArrayList<>(dataSolutionCodes));
		pack.setDataSolutions(dataSolutions);
		List<ActionPrivilegeSolution> actionSolutions = queryActionPrivilegeSolutions(dao, new ArrayList<>(actionSolutionCodes));
		pack.setActionSolution(actionSolutions);
		return pack;
	}
	
	
	public PrivilegeMatrixPackage readPrivilegeMatrixPackageFormExcel(IDao dao,String privilegeMatrixCode,Workbook workbook)throws Exception{
		Map<String,String> identifyMap = getIdentifyLabelMap();
		Set<String> ruleDefs = getRuleDefs(dao);
		ExcelReader reader = new ExcelReader(workbook, PrivilegeMatrixExcelReaderV2.SheetName);
		List<GlobalVariableDto> varList = readGlobalVariableDefineSheet(dao, reader);
		Pair<List<DataPrivilegeSolution>, List<ActionPrivilegeSolution>> pair = readPrivilegeSolutionSheet(dao, reader, privilegeMatrixCode, ruleDefs);
		PrivilegeMatrix matrix = readPrivilegeMatrixSheet(reader, privilegeMatrixCode, identifyMap);
		PrivilegeMatrixPackage pack = new PrivilegeMatrixPackage();
		pack.setMatrix(matrix).setVarDtos(varList).setDataSolutions(pair.left).setActionSolution(pair.right);
		return pack;
	}
	
	public PrivilegeMatrixPackage savePrivilegeMatrixPackage(IDao dao,PrivilegeMatrixPackage pack,FormOpObserver observer)throws Exception{
		if(!CmnUtil.isCollectionEmpty(pack.getVarDtos())) {
			for(GlobalVariableDto varDto : pack.getVarDtos()) {
				Form form = IGlobalVariableMgr.get().convert2Form(varDto);
				saveForm(dao, form,observer);
			}
		}
		if(!CmnUtil.isCollectionEmpty(pack.getDataSolutions())) {
			for(DataPrivilegeSolution solution : pack.getDataSolutions()) {
				Form form = convertDataPrivilegeSolution2Form(solution);
				saveForm(dao, form,observer);
				solution.setUuid(form.getUuid());
			}
		}
		if(!CmnUtil.isCollectionEmpty(pack.getActionSolution())) {
			for(ActionPrivilegeSolution solution : pack.getActionSolution()) {
				Form form = convertActionPrivilegeSolution2Form(solution);
				saveForm(dao, form,observer);
				solution.setUuid(form.getUuid());
			}
		}
		PrivilegeMatrix matrix = pack.getMatrix();
		Form form = convertPrivilegeMatrix2Form(matrix);
		saveForm(dao, form,observer);
		matrix.setUuid(form.getUuid());
		return pack;
	}
	
	@Override
	public PrivilegeMatrixPackage importPrivilegeMatrixFormExcel(IDao dao,String privilegeMatrixCode,byte[] excel,FormOpObserver observer)
			throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(excel);
		Set<String> ruleDefs = getRuleDefs(dao);
		Map<String,String> identifyMap = getIdentifyLabelMap();
		ExcelReader reader = ExcelUtil.getReader(bin);
		List<GlobalVariableDto> varList = readGlobalVariableDefineSheet(dao, reader);
		for(GlobalVariableDto varDto : varList) {
			Form form = IGlobalVariableMgr.get().convert2Form(varDto);
			saveForm(dao, form,observer);
			varDto.setUuid(form.getUuid());
		}
		Pair<List<DataPrivilegeSolution>, List<ActionPrivilegeSolution>> pair = readPrivilegeSolutionSheet(dao, reader, privilegeMatrixCode, ruleDefs);
		for(DataPrivilegeSolution solution : pair.left) {
			Form form = convertDataPrivilegeSolution2Form(solution);
			saveForm(dao, form,observer);
			solution.setUuid(form.getUuid());
		}
		for(ActionPrivilegeSolution solution : pair.right) {
			Form form = convertActionPrivilegeSolution2Form(solution);
			saveForm(dao, form,observer);
			solution.setUuid(form.getUuid());
		}
		PrivilegeMatrix matrix = readPrivilegeMatrixSheet(reader, privilegeMatrixCode, identifyMap);
		Form form = convertPrivilegeMatrix2Form(matrix);
		saveForm(dao, form,observer);
		matrix.setUuid(form.getUuid());
		PrivilegeMatrixPackage pack = new PrivilegeMatrixPackage();
		pack.setMatrix(matrix).setVarDtos(varList).setDataSolutions(pair.left).setActionSolution(pair.right);
		return pack;
	}
	
	void saveForm(IDao dao,Form form,FormOpObserver observer) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String uuid = IFormMgr.get().queryFormUuidByCode(dao, form.getFormModelId(), form.getStringByCode(Form.Code));
		if(uuid != null) {
			form.setUuid(uuid);
			tracer.debug(LOG,"更新数据：" + form.getStringByCode(Form.Code));
			IFormMgr.get().updateForm(null,dao, form,observer);
		}else {
			tracer.debug(LOG,"新增数据：" + form.getStringByCode(Form.Code));
			IFormMgr.get().createForm(null,dao, form,observer);
		}
	}
	
	public Map<String,String> getVariableTypeMap(){
		Map<String,String> typeMap = new LinkedHashMap<>();
		typeMap.put("文本", "String");
		typeMap.put("数值", "Number");
		typeMap.put("数据", "Data");
		typeMap.put("表达式", "Expression");
		return typeMap;
	}
	
	public List<GlobalVariableDto> readGlobalVariableDefineSheet(IDao dao,ExcelReader reader){
		Map<String,String> typeMap = getVariableTypeMap(); 
		GlobalVariableExcelReader variableReader = new GlobalVariableExcelReader(reader);
		List<GlobalVariableExcelRow> excelRows = variableReader.read();
		List<GlobalVariableDto> rows = new ArrayList<>();
		excelRows.stream().forEach(v->
				{
					try {
						rows.add(v.toGlobalVariableDto(dao,typeMap));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				);
		return rows;
	}
	
	public Pair<List<DataPrivilegeSolution>,List<ActionPrivilegeSolution>>
		readPrivilegeSolutionSheet(IDao dao,ExcelReader reader,String privilegeMatrixCode,Set<String> ruleDefs){
		PrivilegeSolutionExcelReaderV2 variableReader = new PrivilegeSolutionExcelReaderV2(reader);
		List<PrivilegeSolutionExcelRow> excelRows = variableReader.read();
		List<DataPrivilegeSolution> dataItemRows = new ArrayList<>();
		List<ActionPrivilegeSolution> actionItemRows = new ArrayList<>();
		excelRows.stream().forEach(v->
				{
					try {
						if(v.isActionSolution()) {
							actionItemRows.add(v.toActionPrivilegeSolution(privilegeMatrixCode, ruleDefs));
						}else if(v.isDataSolution()){
							dataItemRows.add(v.toDataPrivilegeSolution(privilegeMatrixCode, ruleDefs));
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				);
		return new Pair<List<DataPrivilegeSolution>, List<ActionPrivilegeSolution>>(dataItemRows, actionItemRows);
	}
	
	public PrivilegeMatrix readPrivilegeMatrixSheet(ExcelReader reader,String privilegeMatrixCode,Map<String,String> identifyMap) {
		PrivilegeMatrixExcelReaderV2 privilegeMatrixReader = new PrivilegeMatrixExcelReaderV2(reader);
		List<PrivilegeMatrixExcelRow> excelRows = privilegeMatrixReader.read();
		List<PrivilegeMatrixRow> rows = new ArrayList<>();
		excelRows.stream().forEach(v->
				{
					try {
						rows.add(v.toPrivilegeMatrixRow(identifyMap));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				);
		PrivilegeMatrix privilegeMatrix = new PrivilegeMatrix();
		privilegeMatrix.setCode(privilegeMatrixCode).setSettings(rows);
		return privilegeMatrix; 
	}
	
	/**
	 * 构建未授权的表单权限，整合所有数据权限方案的数据项和动作权限方案的操作项
	 * @return
	 * @throws Exception 
	 */
	@Override
	public FormPrivilegeDto buildUnauthorizedFormPrivilege(PrivilegeMatrix matrix) throws Exception {
		IPrivilegeMatrixMgr matrixMgr = IPrivilegeMatrixMgr.get();
		try(IDao dao = IDaoService.newIDao()){
			Map<String,ActionPrivilegeDto> actionPrivMap = new LinkedHashMap<>();
			Map<String,FieldPrivilegeDto> fieldPrivMap = new LinkedHashMap<>();
			for(PrivilegeMatrixRow matrixRow : NullUtil.get(matrix.getSettings())) {
				String dataSolutionCode = matrixRow.getDataSolution();
				DataPrivilegeSolution dataSolution = queryDataPrivilegeSolutionCache(dao,dataSolutionCode);
				for(DataItemPrivilegeSetting setting : dataSolution.getItemSettings()) {
					String field = allocateFieldCode(setting.getItem());
					FieldPrivilegeDto fieldPriv = new FieldPrivilegeDto().setField(field)
							.setFieldName(setting.getItem()).setVisible(false).setWritable(false);
					fieldPrivMap.put(setting.getItem(), fieldPriv);
				}
				String actionSolutionCode = matrixRow.getActionSolution();
				ActionPrivilegeSolution actionSolution = queryActionPrivilegeSolutionCache(dao,actionSolutionCode);
				String actionCategoy = matrixRow.getActionSolutionCategory();
				for(ActionItemPrivilegeSetting setting : actionSolution.getItemSettings()) {
					ActionPrivilegeDto actionPriv = new ActionPrivilegeDto().setName(setting.getItem()).setVisible(false);
					actionPrivMap.put(setting.getItem(), actionPriv);
				}
				
			}
			FormPrivilegeDto formPriv = new FormPrivilegeDto();
			formPriv.setFieldPrivileges(new ArrayList<>(fieldPrivMap.values()));
			formPriv.setActionPrivileges(new ArrayList<>(actionPrivMap.values()));
			return formPriv;
		}
	}
	
	public static void main(String[] args) throws Exception {
		try(IDao dao = IDaoService.get().newDao()){
			byte[] excel = com.leavay.common.util.Utils.getFileBytes("D:\\文档管理权限矩阵表-.xlsx");
			String privilegeMatrixCode = "gpf.md.process.document.DocumentMgr(文档管理)";
			PrivilegeMatrixPackage matrixPack = IPrivilegeMatrixMgr.get().importPrivilegeMatrixFormExcel(dao, privilegeMatrixCode, excel,null);
			dao.commit();
			Tracer tracer = TraceUtil.getCurrentTracer();
			for(DataPrivilegeSolution solution : matrixPack.getDataSolutions()) {
				Set<String> items = solution.getItems("文档上传", "*");
				tracer.info(items);
			}
		}
	}
}
