package cell.basic.cmn.event;

import java.util.Map;

import com.leavay.nio.crpc.RpcMap;

import basic.cmn.dto.event.EventDto;
import basic.cmn.event.EventHandlerInitParameter;
import cell.CellIntf;
import cmn.util.TraceUtil;
import cmn.util.Tracer;

public interface StudyEventHandler extends CellIntf,EventHandler<EventHandlerInitParameter> {
    @Override
    default Object onEvent(EventHandlerInitParameter initParameter, RpcMap<Object> context, EventDto event) throws Exception {
        Map<String,Object> payload = event.getPayload();
        String userId = (String) payload.get("userId");
        Tracer tracer = TraceUtil.getCurrentTracer();
        tracer.info("用户登录成功事件，用户ID：{" + userId + "}");
        return null;
    }
}
