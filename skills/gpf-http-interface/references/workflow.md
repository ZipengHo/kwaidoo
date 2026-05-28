# GPF HTTP 接口执行流程

## 新增接口

1. 确定请求路径和 HTTP 方法
2. 定义 `RequestMappingIntf`
3. 补齐接口类 `@ClassDeclare`、方法 `@MethodDeclare` 和参数 `@InputDeclare`
4. 如果涉及请求体或上下文注入，明确 `exampleValue` 标记方式
5. 实现 `BasicCell_RequestMapping`
6. 配置分发映射和需要的拦截器
7. 如果需要扩展处理链，确认是否要自定义 `HttpRequestHandler`、`HttpResponseHandler` 或 `HandlerInterceptor`
8. 明确认证、异常和响应结构

## 修改已有接口

1. 先保留 URL 契约
2. 判断问题在路径、声明注解、参数绑定、上下文注入、实现还是分发器
3. 最小改动修复

## 排障顺序

1. 404：先查路径和接口注册
2. 405：再查 HTTP 方法
3. 400：查参数绑定、`@MethodDeclare.inputs` 和 `exampleValue`
4. 401：查 JWT 或会话
5. 500：查业务异常和空值
