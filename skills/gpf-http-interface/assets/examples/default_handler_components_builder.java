package cell.example.http.dispatcher;

import cmn.anotation.ClassDeclare;
import cmn.http.servlet.DispatcherMappingBuilder;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.servlet.HandlerMapping;
import cmn.http.servlet.HttpRequestHandler;
import cmn.http.servlet.HttpResponseHandler;
import cmn.http.servlet.impl.DefaultHandlerMapping;
import cmn.http.servlet.impl.DefaultHttpRequestHandler;
import cmn.http.servlet.impl.DefaultHttpResponseHandler;
import cmn.http.servlet.impl.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

@ClassDeclare(
    label = "默认HTTP处理链装配示例",
    what = "演示系统自带请求处理器、响应处理器和拦截器的典型装配方式",
    why = "帮助快速复用框架默认能力，而不是无必要地重写处理链",
    how = "在DispatcherMappingBuilder中配置DefaultHandlerMapping并挂载默认组件",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
public class DefaultHandlerComponentsBuilder implements DispatcherMappingBuilder {

    @Override
    public String[] getIncludePatterns() {
        return new String[]{"/example/default/**"};
    }

    @Override
    public String[] getExcludePatterns() {
        return null;
    }

    @Override
    public HandlerMapping getHandlerMapping() {
        HttpRequestHandler requestHandler = new DefaultHttpRequestHandler();
        HttpResponseHandler responseHandler = new DefaultHttpResponseHandler();
        List<HandlerInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new CustomHandlerInterceptor());

        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping(
            getIncludePatterns(),
            getExcludePatterns(),
            requestHandler
        );
        handlerMapping.setInterceptors(interceptors);
        handlerMapping.setRespHandler(responseHandler);
        return handlerMapping;
    }

    public HandlerMapping getJsonHandlerMapping() {
        HttpRequestHandler requestHandler = new DefaultHttpRequestHandler();
        HttpResponseHandler responseHandler = new JsonHttpResponseHandler();

        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping(
            new String[]{"/example/json/**"},
            null,
            requestHandler
        );
        handlerMapping.setRespHandler(responseHandler);
        return handlerMapping;
    }
}
