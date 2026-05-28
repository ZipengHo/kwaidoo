package cell.gpf.study.nadur.relation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.relation.ElementInfo;
import gpf.dc.relation.ElementRelation;
import web.dto.Pair;
@ClassDeclare(label = "血缘分析代码样例"
,what="血缘分析代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-04"
,updateTime = "2025-03-04")
public interface IStudyElementRelation extends CellIntf{

	static IStudyElementRelation get() {
		return Cells.get(IStudyElementRelation.class);
	}
	
	default void test() throws Exception{
		ElementRelation relation = new ElementRelation();
		String modelId = "";
		relation.addElement(modelId);
		//查询直接依赖的视图
		Map<String,ElementInfo> elementMap = relation.queryDirectDependElements(modelId);
		//查询直接影响的视图
		Map<String,ElementInfo> elementMap2 = relation.queryDirectEffectElements(modelId);
		
		Set<String> modelIds = new LinkedHashSet<>();
		modelIds.add(modelId);
		Map<String,ElementInfo> elementMap3 = relation.queryAllDependElements(modelIds);
		Map<String,ElementInfo> elementMap4 = relation.queryAllEffectElements(modelIds);
		
		Pair<String, Map<String,String>> pair = relation.buildRelateQuerySqlPair(modelId);
	}
}
