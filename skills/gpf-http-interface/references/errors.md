# GPF HTTP 接口常见错误

## 404

常见原因：

- 路径拼接错误
- 类或方法缺少映射
- 分发器未包含目标路径

## 405

常见原因：

- GET/POST 等方法声明不一致

## 400

常见原因：

- 参数缺失
- 类型转换失败
- `@InputDeclare.inputs` 与真实参数不一致
- `$context$`、`$RequestBody$` 或路径参数 `exampleValue` 标记错误

## 401

常见原因：

- JWT 未传
- JWT 校验失败

## 500

常见原因：

- 业务异常
- 空指针
- 类型转换错误
- 误以为 `RequestMappingContext` 中存在实际未定义的字段
- 未判空直接读取 `getSessionInfo()`、`getPathVariables()` 等上下文信息

## SSE 不工作

常见原因：

- 订阅方法返回的不是 `Flux<?>`
- 接口说明写成事件流，但方法签名仍是普通对象
- 客户端未按 `text/event-stream` 方式消费
