# GPF 云开发 Cell 总览

## 适用任务

- 封装可复用业务逻辑
- 管理缓存、线程、连接等长期资源
- 在云端运行并支持联调
- 用配置驱动行为，而不是把常量写死在代码里

## 三类核心 Cell

### 普通 Cell

特点：

- 无状态
- 多例
- 适合计算型、工具型逻辑
- 可通过 `Cells.get()` 获取，也可直接 `new`
- 需要远程联调时优先通过工厂获取

典型场景：

- 金额计算
- 字符串处理
- 一次性业务编排

对应示例：

- `assets/examples/basic_cell.java`

### 服务 Cell

特点：

- 常驻
- 单例
- 适合维护缓存、线程池、锁、长期连接
- 必须通过 `Cells.get()` 获取
- 必须实现并关注 `doStartService()` 与 `doStopService()`

典型场景：

- 缓存服务
- 健康检查
- 消息异步分发
- 配置驱动服务

对应示例：

- `assets/examples/basic_service_cell.java`
- `assets/examples/service_cell_with_preload.java`
- `assets/examples/async_service_cell.java`
- `assets/examples/config_service_cell.java`

### 资源 Cell

特点：

- 用来包裹无法远程传递的对象
- 常见对象包括文件句柄、Socket、数据库连接
- 不能按普通服务那样通过 `Cells.get()` 获取
- 必须由调用方显式关闭，推荐 `try-with-resources`

典型场景：

- 文件读取
- 数据库连接句柄
- 远端资源实例句柄

对应示例：

- `assets/examples/resource_cell.java`

## 伴随能力

- 云配置
- 云 UDF
- 远程联调
- 生命周期管理
- Cell 预加载
- 异步 Promise

## 快速选型

- 只做一次性无状态计算：普通 Cell
- 需要缓存、线程、监听或守护任务：服务 Cell
- 需要包裹文件流、连接、Socket 等对象：资源 Cell
- 需要启动时自动初始化：服务 Cell + `CellPreloadIntf`
- 需要异步返回：服务 Cell + `IPromise`
