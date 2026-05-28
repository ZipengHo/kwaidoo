package cell.example.http.dispatcher;

import cmn.anotation.ClassDeclare;
import cmn.http.servlet.DispatcherMappingBuilder;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.servlet.HandlerMapping;
import cmn.http.servlet.HttpRequestHandler;
import cmn.http.servlet.impl.DefaultHandlerMapping;
import cmn.http.servlet.impl.DefaultHttpRequestHandler;

import java.util.ArrayList;
import java.util.List;

@ClassDeclare(
    label = "用户HTTP分发器",
    what = "配置用户HTTP接口的路由和拦截器",
    why = "让/example/user/**请求进入正确的处理链",
    how = "实现DispatcherMappingBuilder并返回处理器和拦截器配置",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
public class UserDispatcherMappingBuilder implements DispatcherMappingBuilder {

    @Override
    public String[] getIncludePatterns() {
        return new String[]{"/example/user/**"};
    }

    @Override
    public String[] getExcludePatterns() {
        return null;
    }

    @Override
    public HandlerMapping getHandlerMapping() {
        HttpRequestHandler handler = new DefaultHttpRequestHandler();
        List<HandlerInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingInterceptor());
        return new DefaultHandlerMapping(getIncludePatterns(), getExcludePatterns(), handler, interceptors);
    }
}
