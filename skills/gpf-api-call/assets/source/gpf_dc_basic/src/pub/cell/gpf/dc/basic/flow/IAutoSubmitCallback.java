package cell.gpf.dc.basic.flow;

import com.leavay.nio.crpc.RpcMap;

import cell.CellIntf;
import cell.gpf.cfg.AutoSubmitCallback;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.dto.cfg.runtime.AutoSubmitResult;

public interface IAutoSubmitCallback extends CellIntf, AutoSubmitCallback {


	@Override
	default AutoSubmitResult buildAutoSubmit(IDCRuntimeContext rtx,RpcMap<Object> callbackParams) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("进入自动提交回调");
		String operator = (String) callbackParams.get("operator");
		String actionName = (String) callbackParams.get("actionName");
		boolean synchronize = (boolean) callbackParams.get("synchronize");
		AutoSubmitResult autoSubmit = new AutoSubmitResult();
		autoSubmit.setOperator(operator).setActionName(actionName).setSynchronize(synchronize);
		tracer.info("自动提交回调执行结束");
		return autoSubmit;
	}

}
