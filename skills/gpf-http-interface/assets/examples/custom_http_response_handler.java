package cell.example.http.handler;

import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cmn.http.servlet.HttpResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomHttpResponseHandler implements HttpResponseHandler {

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        try (IJson json = IJsonService.get().getPrettyJson()) {
            response.getWriter().write(json.toJson(new ApiWrapper(0, "ok", result)));
        }
        return null;
    }
}
