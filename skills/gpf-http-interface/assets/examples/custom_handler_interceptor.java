package cell.example.http.handler;

import cmn.http.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"缺少Authorization头\"}");
            return false;
        }
        request.setAttribute("authorized", true);
        return true;
    }

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex
    ) throws Exception {
        request.removeAttribute("authorized");
    }
}
