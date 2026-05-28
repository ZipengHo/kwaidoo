package cell.example.rule;

import cell.CellIntf;
import cell.gpf.dc.intf.IBasicAutoSubmitCallback;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import com.leavay.nio.crpc.RpcMap;
import web.dto.Pair;

@ClassDeclare(
        label = "自动提交配置",
        what = "配置保存后自动提交回调",
        why = "让表单保存后自动触发流程",
        how = "在流程进入规则或保存规则中配置",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface AutoSubmitRule extends CellIntf {

    @MethodDeclare(
            label = "配置自动提交",
            what = "保存后自动触发指定流程",
            how = "在流程进入规则中配置使用",
            why = "减少人工触发流程动作",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "操作编排编号", name = "operationOMCode", label = "操作编排编号"),
                    @InputDeclare(desc = "流程名称", name = "processName", label = "流程名称"),
                    @InputDeclare(desc = "操作人", name = "operator", label = "操作人"),
                    @InputDeclare(desc = "提交模式", name = "submitMode", label = "提交模式")
            }
    )
    default Pair<Boolean, String> autoSubmit(IDCRuntimeContext rtx, String operationOMCode, String processName,
                                             String operator, String submitMode) throws Exception {
        RpcMap<Object> callbackParam = new RpcMap<>();
        callbackParam.put("operationOMCode", operationOMCode);
        callbackParam.put("processName", processName);
        callbackParam.put("operator", operator);
        callbackParam.put("synchronize", "同步".equals(submitMode));
        rtx.setAutoSubmitCallback(IBasicAutoSubmitCallback.class);
        rtx.setAutoSubmitCallbackParam(callbackParam);
        return new Pair<>(true, "");
    }
}
