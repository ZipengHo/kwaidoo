# GPF HTTP 接口核心概念

## 三件套关系

### 请求映射接口

- 使用 `@RequestMapping` 声明类级和方法级路径
- 必须补齐 `@ClassDeclare`
- 每个接口方法都必须补齐 `@MethodDeclare`
- 每个业务参数都应在 `@MethodDeclare.inputs` 里声明 `@InputDeclare`
- 特殊参数要用 `exampleValue` 明确标记来源

### 请求映射实现类

- 继承 `BasicCell_RequestMapping`
- 实现对应接口

### DispatcherMappingBuilder

- 决定哪些路径进入哪套处理链
- 可以挂拦截器和响应处理器
- 项目内核心分发器类建议补齐 `@ClassDeclare`

### HttpRequestHandler

- 接口定义位于 [HttpRequestHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/HttpRequestHandler.java)
- 负责接收 `HttpServletRequest`、`HttpServletResponse`、`RequestMethod` 和 `ErrorHandler`
- 默认实现是 [DefaultHttpRequestHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/DefaultHttpRequestHandler.java)
- 默认行为包括：解析查询参数、JSON 请求体、多文件上传、`@MethodDeclare.inputs`、`$context$`、`$RequestBody$` 和路径变量

适用建议：

- 大多数业务接口直接复用 `DefaultHttpRequestHandler`
- 只有在需要自定义参数装配、特殊请求解析、或完全替换映射调用流程时，才实现自己的 `HttpRequestHandler`

### HttpResponseHandler

- 接口定义位于 [HttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/HttpResponseHandler.java)
- 负责将处理结果写入 HTTP 响应
- 系统自带实现包括：
  - [DefaultHttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/DefaultHttpResponseHandler.java)
  - [JsonHttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/JsonHttpResponseHandler.java)

适用建议：

- 默认场景优先复用 `DefaultHttpResponseHandler`
- 需要统一 JSON 包装、文件下载、或更偏 API 风格的返回时，可参考 `JsonHttpResponseHandler`
- 只有在返回写出规则明显不同于现有实现时，才自定义 `HttpResponseHandler`

### HandlerInterceptor

- 接口定义位于 [HandlerInterceptor.java](../assets/source/cmn_util/src/http/cmn/http/servlet/HandlerInterceptor.java)
- 核心方法是 `preHandle(...)` 和 `afterCompletion(...)`
- 典型系统实现可参考：
  - [HttpRequestLogInterceptor.java](../assets/source/OctoCM/src/modelExecute/octo/cm/httpservice/HttpRequestLogInterceptor.java)

适用建议：

- 日志、鉴权、链路追踪、审计、请求头校验优先放在拦截器
- 业务主逻辑不要写进拦截器
- 如果只对部分路径生效，可通过 `MappedInterceptor` 或分发器中的 include/exclude 规则控制

## 系统自带实现的常见用法

分发器中最常见的装配方式是：

- `HttpRequestHandler handler = new DefaultHttpRequestHandler();`
- `HttpResponseHandler respHandler = new DefaultHttpResponseHandler();`
- 或 `HttpResponseHandler respHandler = new JsonHttpResponseHandler();`
- `List<HandlerInterceptor> interceptors = new ArrayList<>();`
- 将这些组件挂到 `DefaultHandlerMapping`

对应源码示例：

- [StudyDispatcherMappingBuilder.java](../assets/source/cmn_util/src/http/cell/cmn/http/StudyDispatcherMappingBuilder.java)
- [ShortLinkDispatcherMappingBuilder.java](../assets/source/cmn_util/src/http/cell/cmn/http/ShortLinkDispatcherMappingBuilder.java)

## 路径规则

- 类级路径定义模块前缀
- 方法级路径建议显式以 `/` 开头
- 生成代码时要给出完整路径
- 路径参数名必须与方法签名和 `@InputDeclare.name` 保持一致

## 特殊参数与环境变量

HTTP 接口里常见的特殊参数不是普通查询参数，而是通过 `@InputDeclare.exampleValue` 告诉框架如何注入。

常见写法：

- 路径参数：`exampleValue = "{id}"`
- 请求体参数：`exampleValue = "$RequestBody$"`
- 上下文参数：`exampleValue = "$context$"`

约束：

- 普通查询参数通常不需要设置 `exampleValue`
- 路径参数、请求体参数、上下文参数应明确标记，否则调用方和生成逻辑容易误判
- `$context$` 对应类型应为 `RequestMappingContext`

## 声明规范

- `@ClassDeclare` 至少应包含 `label`、`what`、`why`、`how`、`developer`、`createTime`、`updateTime`、`version`
- `@MethodDeclare` 至少应包含 `label`、`what`、`why`、`how`、`inputs`
- `@InputDeclare.exampleValue` 可用于标记路径参数占位符，如 `{id}`
- `@InputDeclare.exampleValue = "$context$"` 用于标记上下文自动注入参数
- `@InputDeclare.exampleValue = "$RequestBody$"` 用于标记请求体反序列化参数
- 示例中的 `developer` 只是占位值，落地时必须替换为真实开发者

## RequestMappingContext

`RequestMappingContext` 的实际定义位于 [RequestMappingContext.java](../assets/source/cmn_util/src/http/cmn/http/servlet/mapping/RequestMappingContext.java)，当前可直接使用的核心信息包括：

- `getHttpServlet()`：返回 `IHttpServlet`
- `getAccessToken()`：返回访问令牌字符串
- `getSessionInfo()`：返回 `SessionInfo`
- `getRequestMapping()`：返回 `RequestMappingDto`
- `getPathVariables()`：返回路径变量 `Map<String, String>`
- `getPathVariable(String name)`：按名称读取路径变量

适用场景：

- 需要读取访问令牌或会话信息
- 需要根据当前请求路径、方法、映射元数据做分支处理
- 需要直接读取路径变量，而不想把所有路径变量都拆成方法参数

使用约束：

- `RequestMappingContext` 是框架自动注入参数，不应由调用方显式传值
- 如果已经把路径变量拆成显式方法参数，通常不必再从 `context.getPathVariable()` 重复读取
- 需要读取原始请求对象时，先通过 `context.getHttpServlet()` 再取底层 request/response 能力

实现细节补充：

- `RequestMappingIntf` 实例上的上下文会带上 `pathVariables`
- 但当前 `$context$` 方法参数注入逻辑主要设置了 `httpServlet`、`requestMapping`、`sessionInfo`、`accessToken`
- 因此方法参数里的 `RequestMappingContext` 若要依赖路径变量，建议优先直接声明路径参数；如确需走上下文读取，需要结合当前框架版本核实 `pathVariables` 是否已注入

## 常见扩展点

- JWT 拦截器
- 文件上传下载处理
- SSE 事件流
- 统一响应处理器

## SSE 返回类型

从当前工程实现看，HTTP 响应处理器已经直接支持 `Flux<?>` 作为事件流输出，相关实现可参考：

- [DefaultHttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/DefaultHttpResponseHandler.java)
- [JsonHttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/JsonHttpResponseHandler.java)

因此 SSE 接口建议：

- 简单文本流使用 `Flux<String>`
- 需要自定义 `event`、`id`、`retry`、`data` 字段时使用 `Flux<SSEMessage>`
- 结构化事件流使用 `Flux<业务对象>`
- 不确定具体泛型时，至少应声明为 `Flux<?>`

当前格式化规则可参考 [DefaultHttpResponseHandler.java](../assets/source/cmn_util/src/http/cmn/http/servlet/impl/DefaultHttpResponseHandler.java) 中的 `formatSSE`：

- `Flux<String>`：
  输出格式为 `data: 文本内容\n\n`
- `Flux<SSEMessage>`：
  按 `event:`、`id:`、`retry:`、`data:` 逐项拼接；其中 `data` 会转成 JSON
- `Flux<SSEMessage>` 且使用 `rawData`：
  当 `event`、`id`、`retry`、`data` 都为空且 `rawData` 有值时，直接原样输出 `rawData`，事件格式完全由业务层控制
- `Flux<其他对象>`：
  输出格式为 `data: JSON序列化结果\n\n`
- `Flux<Throwable>`：
  输出格式为 `event: error\ndata: 错误消息\n\n`

`SSEMessage.rawData` 适用场景：

- 业务侧需要自己拼接完整 SSE 协议文本
- 需要输出非默认的多行 `data:`、注释帧或特殊事件组合
- 需要完全控制单条事件的最终落盘格式

使用约束：

- 一旦走 `rawData`，该条消息的格式正确性由业务层自行保证
- 推荐确保 `rawData` 自带合法的 SSE 分隔，如以 `\n\n` 结束
- 如果同时设置了标准字段和 `rawData`，当前实现优先使用标准字段格式化

不推荐：

- 把 SSE 返回值写成 `Object`
- 只在说明文字里说“事件流”，但方法签名不是 `Flux<?>`
