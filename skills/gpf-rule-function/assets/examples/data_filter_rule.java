package cell.example.rule;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import org.nutz.dao.Cnd;

@ClassDeclare(
        label = "状态过滤规则",
        what = "按业务状态过滤数据",
        why = "限制用户只看到目标状态数据",
        how = "在数据过滤规则配置中使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface DataFilterRule extends CellIntf {

    @MethodDeclare(
            label = "按状态过滤",
            what = "按业务状态过滤查询结果",
            how = "在列表过滤规则中使用",
            why = "避免用户看到无关数据",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "状态字段名称", name = "statusFieldName", label = "状态字段名称"),
                    @InputDeclare(desc = "状态值", name = "statusValue", label = "状态值")
            }
    )
    default Cnd buildFilter(IDCRuntimeContext rtx, String statusFieldName, String statusValue) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode(statusFieldName);
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(statusFieldCode, statusValue);
        return cnd;
    }
}
