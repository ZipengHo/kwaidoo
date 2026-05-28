# GPF HTTP 接口代码模式

## 基础接口模式

```java
@ClassDeclare(
    label = "用户HTTP接口",
    what = "提供用户查询接口",
    why = "为前端提供用户查询能力",
    how = "通过HTTP GET请求访问用户接口",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/user")
public interface IUserHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "获取用户信息",
        what = "根据用户ID获取用户信息",
        why = "支持用户详情查询",
        how = "通过GET请求访问/example/user/info?id=xxx",
        inputs = {
            @InputDeclare(name = "id", label = "用户ID", desc = "用户唯一标识", nullable = false)
        }
    )
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    UserInfo getUserInfo(String id) throws Exception;
}
```

## 实现类模式

```java
public class CUserHttpMapping extends BasicCell_RequestMapping implements IUserHttpMapping {

    @Override
    public UserInfo getUserInfo() throws Exception {
        return new UserInfo();
    }
}
```

关键要点：

- 接口类必须有 `@ClassDeclare`
- 接口方法必须有 `@MethodDeclare`
- 参数声明要与路径、查询参数、请求体语义一致
- 示例中的 `developer` 必须替换为真实值

## 分发器模式

- 明确 `includePatterns`
- 避免错误排除业务路径
- 需要认证时在分发器上挂拦截器
- 分发器类推荐补齐 `@ClassDeclare`

## 请求处理器模式

- 默认优先使用 `DefaultHttpRequestHandler`
- 自定义 `HttpRequestHandler` 时，应明确是复用默认参数解析，还是完全自行处理请求
- 自定义处理器更适合特殊协议、特殊参数绑定或统一前置解析

参考示例：

- `assets/examples/custom_http_request_handler.java`
- `assets/examples/default_handler_components_builder.java`

## 响应处理器模式

- 默认优先使用 `DefaultHttpResponseHandler`
- API 风格接口可参考 `JsonHttpResponseHandler`
- 自定义 `HttpResponseHandler` 时，要明确异常、文件下载、SSE、JSON 序列化怎么处理

参考示例：

- `assets/examples/custom_http_response_handler.java`
- `assets/examples/default_handler_components_builder.java`

## 拦截器模式

- 通用日志、鉴权、审计等逻辑写在 `HandlerInterceptor`
- `preHandle` 返回 `false` 时应自行完成响应写出
- `afterCompletion` 适合清理线程变量、记录耗时、补尾日志

参考示例：

- `assets/examples/custom_handler_interceptor.java`
- `assets/examples/default_handler_components_builder.java`

## 文件接口模式

- 上传接口要声明 `MultipartFile` 或文件列表参数
- 下载接口要说明返回字节数组、流或下载响应结构
- 上传下载接口的方法说明中要写清 `multipart/form-data` 或下载路径

参考示例：

- `assets/examples/file_mapping.java`

## JWT 接口模式

- 登录、鉴权、当前用户接口要分清匿名访问和鉴权访问
- 认证相关接口要写明令牌来源、验证方式和失败语义
- 需要鉴权时在分发器上挂拦截器

参考示例：

- `assets/examples/jwt_auth_mapping.java`

## SSE 接口模式

- SSE 接口要明确事件流用途和响应方式
- 方法说明中应写明事件流访问路径和实时推送场景
- 如果有辅助推送接口，应分别声明订阅与推送方法
- 订阅方法返回类型应为 `Flux<String>`、`Flux<SSEMessage>`、`Flux<业务对象>` 或 `Flux<?>`

返回类型与输出格式：

- `Flux<String>`：每个元素输出为 `data: xxx`
- `Flux<SSEMessage>`：可控制 `event`、`id`、`retry`、`data`
- `Flux<SSEMessage>` + `rawData`：由业务层直接控制原始 SSE 文本
- `Flux<业务对象>`：每个元素会先转成 JSON，再输出为 `data: {...}`

参考示例：

- `assets/examples/sse_mapping.java`

## 上下文参数模式

- 使用 `RequestMappingContext` 作为方法参数类型
- 在 `@InputDeclare` 中声明 `exampleValue = "$context$"`
- 优先通过上下文读取访问令牌、会话信息、请求映射和路径变量

参考示例：

- `assets/examples/context_mapping.java`
