# Web 服务配置说明

## 适用范围

本文件只在用户明确要求修改 `conf/web/service.json` 时读取。

适合处理：

- 路由处理器配置
- 拦截器配置
- Servlet 映射
- 过滤器映射
- 跨域配置
- 响应头配置
- DoS 防护配置
- 静态资源目录映射

不适合处理：

- 默认单 GPF 最小启动配置
- 只修改数据库连接、HTTP 端口、RPC 端口的场景

## 校验依据

- 技能内权威 schema：`assets/schema/service.schema.json`
- 修改 `service.json` 后，必须以该 schema 为准检查字段结构、必填项、固定值、默认值约束和枚举值约束
- 如果用户只给出修改意图但没有完整文件，应先基于用户提供的现有 `service.json` 增量修改，再按 schema 校验
- 如果用户提供了新的类名、处理器类名、拦截器类名或过滤器类名，可以按用户确认值写入；schema 主要校验结构，不负责保证类实际存在

## 根对象

`service.json` 根对象对应 `web.config.Configuration`，根级配置项如下。

### `@c`

- 含义：根配置对象类型标识
- 类型：`string`
- 固定值：`web.config.Configuration`
- 要求：必填

### `dirAllowed`

- 含义：是否允许目录列表访问
- 类型：`boolean`
- 默认值：`false`

### `handler`

- 含义：全局 HTTP 请求处理器
- 结构：对象，至少包含 `@c`
- 常见字段：
  - `@c`
    - 含义：处理器完整类名
    - schema 中枚举值包括：
      - `web.core.GdfDefaultServletHttpRequestHandler`
      - `web.core.GdfLoginHttpRequestHandler`
      - `web.core.SSOLoginHttpRequestHandler`
      - `web.core.GdfDefaultServletHttpRequestHandlerV1`
      - `web.core.UserDefineHttpRequestHandler`
- 说明：对象允许附加属性，用户如果提供了已确认的类名或额外参数，可按用户提供值写入

### `respHandler`

- 含义：全局 HTTP 响应处理器
- 结构：对象，至少包含 `@c`
- 常见字段：
  - `@c`
    - 含义：响应处理器完整类名
    - schema 中枚举值：`web.core.DefaultHttpResponseHandler`
- 说明：对象允许附加属性

### `multipartResolver`

- 含义：文件上传解析器
- 结构：对象，至少包含 `@c`
- 常见字段：
  - `@c`
    - 含义：上传解析器完整类名
    - schema 中枚举值：`web.multipart.StandardServletMultipartResolver`
- 说明：对象允许附加属性，样例中可附带 `resolveLazily`

### `handlerAdapters`

- 含义：处理器适配器列表
- 类型：数组
- 数组项结构：对象，至少包含 `@c`
- 常见字段：
  - `@c`
    - 含义：处理器适配器完整类名
    - schema 中枚举值：`web.core.HttpRequestHandlerAdapter`
- 说明：数组项允许附加属性

### `handlerMappings`

- 含义：请求映射配置列表
- 类型：数组
- 数组项结构：见下文“`handlerMappings` 详细说明”

### `handlerExceptionResolvers`

- 含义：异常解析器列表
- 类型：数组
- 数组项结构：见下文“`handlerExceptionResolvers` 详细说明”

### `servletMapping`

- 含义：Servlet 映射列表
- 类型：数组
- 默认值：空数组
- 数组项结构：见下文“`servletMapping` 详细说明”

### `filterMapping`

- 含义：过滤器映射列表
- 类型：数组
- 默认值：空数组
- 数组项结构：见下文“`filterMapping` 详细说明”

### `crossOriginFilter`

- 含义：跨域过滤器配置
- 类型：对象
- 结构：见下文“`crossOriginFilter` 详细说明”

### `headerFilter`

- 含义：响应头过滤器配置
- 类型：对象
- 结构：见下文“`headerFilter` 详细说明”

### `dosFilter`

- 含义：DoS 攻击防护配置
- 类型：对象
- 结构：见下文“`dosFilter` 详细说明”

### `resourceDirs`

- 含义：静态资源目录映射
- 类型：对象
- 结构：`key -> value`
  - key：URL 路径前缀
  - value：文件系统路径
- 说明：例如把 `/static` 映射到某个物理目录

## 通用对象：`typeReference`

以下配置块大量复用统一对象结构：

- 至少包含 `@c`
- `@c` 表示完整 Java 类名
- schema 允许附加属性

适用位置包括：

- `handler`
- `respHandler`
- `multipartResolver`
- `handlerAdapters[]`
- `handlerMappings[].defaultHandler`
- `handlerMappings[].interceptors[].interceptor`

## `handlerMappings` 详细说明

数组项类型：对象

### 必填字段

- `@c`
  - 固定值：`web.servlet.handler.DefaultHandlerMapping`

### 可选字段

- `defaultHandler`
  - 含义：该路由组的默认处理器
  - 结构：对象，至少包含 `@c`
  - schema 中枚举值包括：
    - `web.core.GdfDefaultServletHttpRequestHandler`
    - `web.core.GdfLoginHttpRequestHandler`
    - `web.core.SSOLoginHttpRequestHandler`
    - `web.core.GdfDefaultServletHttpRequestHandlerV1`
    - `web.core.UserDefineHttpRequestHandler`

- `includePatterns`
  - 含义：匹配的 URL 模式列表
  - 类型：`string[]`
  - 默认值：空数组
  - 支持形式：
    - `/path` 精确匹配
    - `/path/*` 一级匹配
    - `/path/**` 多级匹配

- `excludePatterns`
  - 含义：排除的 URL 模式列表
  - 类型：`string[]`
  - 默认值：空数组

- `interceptors`
  - 含义：拦截器列表
  - 类型：数组
  - 默认值：空数组
  - 数组项结构：见下文“`mappedInterceptor` 详细说明”

### 说明

- `handlerMappings` 常用于给不同 URL 范围配置不同处理器和拦截链
- 如果只是现场定制某个 URL 的登录控制，通常改这里

## `mappedInterceptor` 详细说明

数组项类型：对象

### 必填字段

- `@c`
  - 固定值：`web.servlet.handler.MappedInterceptor`

- `interceptor`
  - 含义：拦截器实例
  - 结构：对象，至少包含 `@c`
  - schema 中枚举值包括：
    - `web.servlet.handler.RequestInfoInterceptor`
    - `web.servlet.handler.GdfValidateLoginInterceptor`

### 可选字段

- `includePatterns`
  - 含义：拦截器生效的 URL 模式
  - 类型：`string[]`
  - 默认值：空数组

- `excludePatterns`
  - 含义：拦截器排除的 URL 模式
  - 类型：`string[]`
  - 默认值：空数组

### 说明

- 如果用户给出新的拦截器类名，可按用户确认值写入
- 如果未给出新类名，优先复用现有配置里的拦截器模式

## `handlerExceptionResolvers` 详细说明

数组项类型：对象

### 必填字段

- `@c`
  - 固定值：`web.core.DefaultHandlerExceptionResolver`

### 可选字段

- `loginErrorClass`
  - 含义：登录错误类名列表
  - 类型：`string[]`
  - 默认值：空数组

- `alterErrorClass`
  - 含义：需要特殊处理的异常类名列表
  - 类型：`string[]`
  - 默认值：空数组

## `servletMapping` 详细说明

数组项类型：对象

### 必填字段

- `@c`
  - 固定值：`web.config.ServletMapping`

- `servlet`
  - 含义：Servlet 完整类名
  - 类型：`string`
  - 示例：`web.servlet.DispatcherServlet`

- `urlPatterns`
  - 含义：URL 模式，多个模式用逗号分隔
  - 类型：`string`
  - 示例：
    - `/api/*`
    - `/v1/*,/v2/*`
    - `/app,/service/*`

### 说明

- `servletMapping` 适合补充额外的 Servlet 暴露路径
- 该对象不允许 schema 外字段

## `filterMapping` 详细说明

数组项类型：对象

### 必填字段

- `@c`
  - 固定值：`web.config.FilterMapping`

- `filterClass`
  - 含义：过滤器完整类名
  - 类型：`string`

- `urlPatterns`
  - 含义：URL 模式，多个模式用逗号分隔
  - 类型：`string`

### 可选字段

- `dispatchTypes`
  - 含义：分发类型，多个类型用逗号分隔
  - 类型：`string`
  - 可选值仅限以下枚举：
    - `REQUEST`
    - `FORWARD`
    - `INCLUDE`
    - `ERROR`
    - `ASYNC`
    - `REQUEST,FORWARD`
    - `REQUEST,INCLUDE`
    - `REQUEST,ERROR`
    - `REQUEST,ASYNC`
    - `FORWARD,INCLUDE`
    - `FORWARD,ERROR`
    - `FORWARD,ASYNC`
    - `INCLUDE,ERROR`
    - `INCLUDE,ASYNC`
    - `ERROR,ASYNC`
    - `REQUEST,FORWARD,INCLUDE`
    - `REQUEST,FORWARD,ERROR`
    - `REQUEST,FORWARD,ASYNC`
    - `REQUEST,INCLUDE,ERROR`
    - `REQUEST,INCLUDE,ASYNC`
    - `REQUEST,ERROR,ASYNC`
    - `FORWARD,INCLUDE,ERROR`
    - `FORWARD,INCLUDE,ASYNC`
    - `FORWARD,ERROR,ASYNC`
    - `INCLUDE,ERROR,ASYNC`
    - `REQUEST,FORWARD,INCLUDE,ERROR`
    - `REQUEST,FORWARD,INCLUDE,ASYNC`
    - `REQUEST,FORWARD,ERROR,ASYNC`
    - `REQUEST,INCLUDE,ERROR,ASYNC`
    - `FORWARD,INCLUDE,ERROR,ASYNC`
    - `REQUEST,FORWARD,INCLUDE,ERROR,ASYNC`

### 说明

- `filterMapping` 常用于现场定制过滤器链
- 该对象不允许 schema 外字段

## `crossOriginFilter` 详细说明

对象类型：跨域资源共享配置

### 字段

- `allowedOrigins`
  - 类型：`string`
  - 默认值：`*`
  - 含义：允许的来源域名，`*` 表示允许所有来源

- `allowedMethods`
  - 类型：`string`
  - 默认值：`GET,POST,PUT,DELETE,OPTIONS,HEAD`
  - 含义：允许的 HTTP 方法，多个方法用逗号分隔

- `allowedHeaders`
  - 类型：`string`
  - 默认值：`*`
  - 含义：允许的请求头，`*` 表示允许所有请求头

- `chainPreflight`
  - 类型：`string`
  - 默认值：`false`
  - 可选值：`true`、`false`
  - 含义：是否链式处理预检请求

### 说明

- 对象允许附加属性
- 只有明确涉及跨域场景时才改这里

## `headerFilter` 详细说明

对象类型：HTTP 响应头配置

### 字段

- `headerConfig`
  - 类型：`string`
  - 格式要求：`set Header-Name: Header-Value`
  - 正则约束：`^set\\s+.+:\\s*.+$`
  - 示例：
    - `set X-Frame-Options: SAMEORIGIN`
    - `set X-Content-Type-Options: nosniff`
    - `set Strict-Transport-Security: max-age=31536000`

### 说明

- 对象允许附加属性
- 只有明确涉及响应头安全配置时才改这里

## `dosFilter` 详细说明

对象类型：DoS 攻击防护配置

### 字段

- `maxRequestMs`
  - 类型：`integer`
  - 最小值：`0`
  - 默认值：`60000`
  - 含义：请求最大处理时间，单位毫秒，超时将被中断

### 说明

- 对象允许附加属性
- 只有明确涉及超时控制或 DoS 防护时才改这里

## `resourceDirs` 详细说明

- 类型：对象
- 结构：`URL 前缀 -> 文件系统路径`
- 说明：
  - key 是 URL 路径前缀
  - value 是文件系统路径
  - 可用于补充静态资源目录映射

示例：

```json
{
  "/static": "/var/www/static",
  "/public": "/var/www/public"
}
```

## 修改与校验原则

- 只有用户明确涉及 Web 服务定制场景时才进入本文件
- 默认基于用户提供的现有 `service.json` 增量修改
- 未涉及的路由、拦截器、过滤器、资源目录保持不变
- 如果用户提供新的类名、拦截器类名、过滤器类名或处理器类名，可按用户确认值写入
- 如果用户未提供类名，应优先复用现有 `service.json` 中已存在的模式
- `filterMapping`、`servletMapping` 等结构固定对象必须遵守字段约束，不要擅自增加无关字段
- 修改完成后，必须对照 `assets/schema/service.schema.json` 校验结果内容是否仍符合结构约束
- schema 校验重点包括：
  - 根对象 `@c` 是否正确
  - 必填字段是否齐全
  - 固定值字段是否被错误修改
  - `dispatchTypes` 等枚举值是否合法
  - 不允许额外字段的对象是否被写入了 schema 外字段

## 常见任务

- 为某个 URL 范围增加登录拦截器
- 为某个 URL 范围开放匿名访问
- 新增一个 `servletMapping`
- 新增或修改 `filterMapping`
- 调整默认处理器
- 增加或修改静态资源目录
- 修改跨域、响应头或 DoS 防护配置
