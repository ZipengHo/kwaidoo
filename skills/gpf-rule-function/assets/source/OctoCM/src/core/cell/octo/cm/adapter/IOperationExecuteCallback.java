package cell.octo.cm.adapter;

import cell.octo.cm.IContext;
import cmn.anotation.ClassDeclare;

import java.io.Serializable;

/**
 * 操作执行回调接口。
 * 操作函数可在运行时将回调句柄注册到上下文的  $afterOperationCallbacks$ 中，
 * 由 IRuleExpressionExecutor 在规则/流程执行的finally 阶段统一回调。
 */
@ClassDeclare(label = "", what = "", why = "", how = "", developer = "", version = "", createTime = "", updateTime = "")
@FunctionalInterface
public interface IOperationExecuteCallback extends Serializable {

    /**
     * 执行回调。
     *
     * @param context 执行上下文，包含全部运行时参数
     * @param result  执行结果（前置回调时为 null，后置回调时为规则/流程的最终返回值，可能为 null）
     * @param error   执行过程中捕获的异常，正常结束时为 null
     */
    void execute(IContext context, Object result, Throwable error) throws Exception;
}
