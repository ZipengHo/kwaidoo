# GPF HTTP 接口总览

## 核心组件

- `RequestMappingIntf`：定义接口入口
- `BasicCell_RequestMapping`：实现业务逻辑
- `DispatcherMappingBuilder`：配置路径分发、处理器与拦截器
- `HttpRequestHandler`：负责把 HTTP 请求解析并调用目标映射方法
- `HttpResponseHandler`：负责把返回值写回 HTTP 响应
- `HandlerInterceptor`：负责在处理前后织入日志、鉴权、审计等通用逻辑

## 注解约束

- HTTP 接口类必须添加 `@ClassDeclare`
- HTTP 接口方法必须添加 `@MethodDeclare`
- 方法入参必须在 `@MethodDeclare.inputs` 中使用 `@InputDeclare` 声明
- 分发器、拦截器等框架扩展类如果是技能示例或项目内核心配置类，建议补齐 `@ClassDeclare`

## 特殊参数约束

- 路径参数应使用占位符路径，并在 `@InputDeclare.exampleValue` 中写 `{参数名}`
- 请求体参数应使用 `exampleValue = "$RequestBody$"`
- 请求上下文参数应使用 `exampleValue = "$context$"`，对应类型为 `RequestMappingContext`

## 常见任务

- 普通 REST 接口
- 文件上传下载
- JWT 认证
- SSE 推送
- 统一异常与统一响应

SSE 补充说明：

- 事件流接口的返回类型应为 `Flux<String>`、`Flux<业务对象>` 或其他 `Flux<?>`
- 需要精细控制 SSE 标准字段时，应返回 `Flux<SSEMessage>`
- HTTP 响应层已经对 `Flux<?>` 做了 `text/event-stream` 处理，不应再用 `Object` 作为 SSE 返回占位

## 对应示例

- 基础接口与实现：`assets/examples/basic_user_mapping.java`
- 上下文参数与当前用户信息：`assets/examples/context_mapping.java`
- 分发器：`assets/examples/user_dispatcher_mapping_builder.java`
- 请求处理器：`assets/examples/custom_http_request_handler.java`
- 响应处理器：`assets/examples/custom_http_response_handler.java`
- 拦截器：`assets/examples/custom_handler_interceptor.java`
- 系统自带实现装配：`assets/examples/default_handler_components_builder.java`
- 文件上传下载：`assets/examples/file_mapping.java`
- JWT 认证：`assets/examples/jwt_auth_mapping.java`
- SSE 推送：`assets/examples/sse_mapping.java`
