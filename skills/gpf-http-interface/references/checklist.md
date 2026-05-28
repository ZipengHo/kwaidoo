# GPF HTTP 接口检查清单

- 是否给出完整路径
- 是否区分类级路径和方法级路径
- 是否为接口类补齐了 `@ClassDeclare`
- 是否为每个接口方法补齐了 `@MethodDeclare`
- 是否为每个参数补齐了 `@InputDeclare`
- 是否正确使用了 `$context$`、`$RequestBody$` 或路径参数占位符
- 如果用了 `RequestMappingContext`，是否说明读取了哪些字段
- 如果是 SSE 接口，返回类型是否使用了 `Flux<String>`、`Flux<SSEMessage>`、`Flux<?>` 或 `Flux<业务对象>`
- 是否说明了该返回类型对应的事件输出格式
- 如果使用了 `SSEMessage.rawData`，是否明确说明事件格式由业务层自行控制
- 是否说明接口、实现类和分发器关系
- 如果扩展了处理链，是否说明 `HttpRequestHandler`、`HttpResponseHandler`、`HandlerInterceptor` 的职责边界
- 如果只是常规接口，是否优先复用了系统自带实现
- 是否说明认证或异常处理
- 是否覆盖常见故障点
