package cell.gpf.study.action.view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.CellIntf;
import cell.gpf.dc.basic.IExpressionMgr;
import cell.gpf.study.action.view.param.StudyAssocFieldExtendQueryParam;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.editor.SelectEditorQuerier;
import fe.cmn.editor.SelectEditorQuerierContext;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.fe.component.editor.AssocFieldExtendQueryInterface;
import gpf.dc.fe.component.editor.SelectModelListEditor;
import gpf.dc.fe.component.param.AssocDataQueryParam;
import gpf.dc.fe.dto.fieldextend.AssocFieldExtend;
@ClassDeclare(label = "关联列表自定义数据查询"
,what="关联列表自定义数据查询"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-08-26"
,updateTime = "2025-08-26")
public interface IStudyAssocFieldExtendQuery extends CellIntf,AssocFieldExtendQueryInterface{
	
	@Override
	default Class<? extends StudyAssocFieldExtendQueryParam> getParamClass() {
		return StudyAssocFieldExtendQueryParam.class;
	}
	
	@Override
    default ResultSet<Form> querySelectItems(AbsComponent component,SelectEditorQuerier querier, SelectEditorQuerierContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		SelectModelListEditor editor = (SelectModelListEditor) component;
        AssocDataQueryParam widgetParam = (AssocDataQueryParam) editor.getWidgetParam();
        AssocFieldExtend fieldExtend = (AssocFieldExtend) widgetParam.getField().getExtendInfo();
        StudyAssocFieldExtendQueryParam queryParams = (StudyAssocFieldExtendQueryParam) fieldExtend.getCustomOptionParamDto(getParamClass());
        Set<String> namespaces = queryParams.getNamespaceSet();
        if(namespaces.isEmpty()) {
        	tracer.warning("未指定规则命名空间！" );
        }
        String rule = queryParams.getRule();
        if(CmnUtil.isStringEmpty(rule)) {
        	tracer.warning("规则未配置！" );
        	return null;
        }
        IExpressionMgr exprMgr = IExpressionMgr.get();
        Map<String,Object> envMap = new LinkedHashMap<>();
        envMap.put("$feContext$", context);
        envMap.put("$currentComponent$", component);
        envMap.put("$querier$", querier);
        ResultSet<Form> rs = (ResultSet<Form>) exprMgr.execute(namespaces, envMap, rule);
        return rs;
    }
	
}
