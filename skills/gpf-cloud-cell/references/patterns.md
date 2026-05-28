# GPF 云开发 Cell 代码模式

## 普通 Cell 模式

适用场景：

- 无状态计算
- 工具型逻辑
- 一次性业务处理

关键要点：

- 接口继承 `CellIntf`
- 简单逻辑可直接写在接口默认方法里
- 需要远程联调时优先通过 `Cells.get()` 获取

参考示例：

- `assets/examples/basic_cell.java`

## 服务 Cell 模式

适用场景：

- 缓存
- 线程池
- 长连接

关键要点：

- 继承 `BasicServiceCell`
- 在 `doStartService()` 初始化资源
- 在 `doStopService()` 彻底释放资源
- 通过 `Cells.get()` 获取
- 启动即初始化时，再考虑 `CellPreloadIntf`

```java
public class COrderCacheService extends BasicServiceCell implements IOrderCacheService {

    private ExecutorService executor;
    private Map<String, Form> cache;

    @Override
    protected void doStartService() throws Exception {
        executor = Executors.newFixedThreadPool(4);
        cache = new ConcurrentHashMap<>();
    }

    @Override
    protected void doStopService() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (cache != null) {
            cache.clear();
            cache = null;
        }
    }
}
```

参考示例：

- `assets/examples/basic_service_cell.java`
- `assets/examples/service_cell_with_preload.java`

## 异步服务 Cell 模式

适用场景：

- 调用方不希望同步阻塞
- 任务结果通过回调或 Promise 返回

关键要点：

- 返回 `IPromise<T>`
- 使用 `CPromise<T>`
- 禁止 `CompletableFuture`
- 任务完成过快时可设置保护超时

参考示例：

- `assets/examples/async_service_cell.java`

## 资源 Cell 模式

适用场景：

- 文件
- Socket
- 数据库连接

关键要点：

- 不把不可序列化对象跨边界传递
- 在使用点及时关闭资源
- 不通过 `Cells.get()` 获取资源实例
- 构造函数中显式接收资源对象或定位参数

参考示例：

- `assets/examples/resource_cell.java`

## 配置绑定模式

关键要点：

- 使用 `@Config` 绑定配置类
- 配置类应提供默认值
- 输出时说明配置来源和覆盖关系

参考示例：

- `assets/examples/config_service_cell.java`
