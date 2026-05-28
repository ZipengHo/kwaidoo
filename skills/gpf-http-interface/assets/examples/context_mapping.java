package cell.example.http.context;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingContext;
import cmn.http.servlet.mapping.RequestMappingIntf;

@ClassDeclare(
    label = "上下文HTTP接口",
    what = "演示如何在HTTP接口中读取请求上下文",
    why = "用于获取访问令牌、会话信息和路径变量",
    how = "通过GET请求访问/example/context/user/{id}",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/context")
public interface IContextHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "获取当前请求上下文信息",
        what = "读取访问令牌、请求映射和路径变量",
        why = "在需要上下文驱动逻辑时获取框架注入信息",
        how = "通过GET请求访问/example/context/user/{id}",
        inputs = {
            @InputDeclare(name = "context", label = "请求上下文", desc = "框架自动注入的上下文对象", exampleValue = "$context$"),
            @InputDeclare(name = "id", label = "用户ID", desc = "路径中的用户标识", nullable = false, exampleValue = "{id}")
        }
    )
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    String currentUser(RequestMappingContext context, String id) throws Exception;
}

package cell.example.http.context.impl;

import cmn.http.cells.BasicCell_RequestMapping;
import cmn.http.dto.RequestMappingDto;
import cmn.http.dto.SessionInfo;
import cmn.http.servlet.mapping.RequestMappingContext;
import cell.example.http.context.IContextHttpMapping;

public class CContextHttpMapping extends BasicCell_RequestMapping implements IContextHttpMapping {

    @Override
    public String currentUser(RequestMappingContext context, String id) throws Exception {
        String accessToken = context.getAccessToken();
        SessionInfo sessionInfo = context.getSessionInfo();
        RequestMappingDto requestMapping = context.getRequestMapping();

        String sessionId = sessionInfo != null ? sessionInfo.getSessionId() : "anonymous";
        String requestPath = requestMapping != null && requestMapping.getPath() != null
            ? String.join(",", requestMapping.getPath())
            : "unknown";

        return "id=" + id
            + ", sessionId=" + sessionId
            + ", accessToken=" + accessToken
            + ", requestPath=" + requestPath;
    }
}
