# TinyService 核心类与调用示例

## 适用场景

当任务涉及以下任一内容时，优先阅读本文：

- 如何通过 `TsHelper.openService(...)` 发起微服务调用
- 如何用 `TsOption` 控制路由、追踪、超时、重试和消费端 Hook
- `TsHook` 与 `TsProviderHook` 的执行位置和差异
- 服务注册表里的 `consumerHook`、`providerHook`、`enableTrack` 如何生效

## 已核实源码位置

- `tiny.service.cmn.TsHelper`：`../assets/source/tiny/service/cmn/TsHelper.java`
- `tiny.service.cmn.TsOption`：`../assets/source/tiny/service/cmn/TsOption.java`
- `tiny.service.cmn.TsHook`：`../assets/source/tiny/service/cmn/TsHook.java`
- `tiny.service.cmn.TsProviderHook`：`../assets/source/tiny/service/cmn/TsProviderHook.java`
- 消费端 Hook 选择逻辑：`../assets/source/tiny/service/cmn/member/TsRpcProxyCaller.java`
- 提供端 Hook 执行逻辑：`../assets/source/tiny/service/cmn/member/TsProvider.java`
- 注册表字段定义：`../assets/source/tiny/service/cmn/TsRegEntryInfo.java`
- 现成消费端 Hook 示例：`../assets/source/tiny/service/hook/AlarmHook.java`

## 微服务接口开发规范

### 1. 接口本质

微服务接口在设计方式上，本质接近普通 Cell 暴露接口：

- 先定义接口契约
- 再提供实现类
- 调用方通过代理对象远程调用

但它和普通 Cell 的核心差异在于：微服务接口是跨代理边界的契约，不是单机进程内的直接对象调用。

### 1.1 普通 Cell 的基础编写规范仍然有效

微服务不是另一套完全独立的编码风格。只要是定义微服务接口和实现，仍然应该遵循普通 Cell 的基本组织方式：

- 接口放在稳定的接口包中
- 实现类放在对应的 `impl` 包中
- 先定义接口契约，再补实现类
- 不要把服务能力直接写成一组无接口约束的静态工具方法

可以直接参考本技能内的普通 Cell 风格示例：

- `../assets/examples/basic_cell_style_reference.java`

这里要强调的是：

- 微服务接口的“写法”遵循普通 Cell 规范
- 微服务接口的“契约类型”再额外满足远程调用、可序列化、跨代理解耦要求

同时要明确排除一条错误迁移：

- 微服务接口不支持服务 Cell 风格，不要引入 `ServiceCellIntf`
- 微服务实现不要继承 `BasicServiceCell`
- 不要为微服务接口补 `doStartService()`、`doStopService()` 这一类服务 Cell 生命周期方法

### 2. 入参与返回值约束

定义在微服务接口方法上的入参与返回值，应遵循下面的约束：

- 优先使用基础类型，例如 `String`、`int`、`long`、`boolean`、`BigDecimal`
- 优先使用基础集合类型或简单嵌套结构
- 复杂参数或结果应收敛成可序列化的贫血 POJO，只保留字段和最基本的 getter/setter

这里的“贫血 POJO”强调的是：

- 只承载数据
- 不携带数据库访问、服务定位、运行时上下文、缓存句柄等行为能力
- 不依赖某个具体微服务代理工程里的私有基础设施
- 必须满足远程传输所需的序列化要求

### 3. 为什么要这样限制

微服务接口上的类型应该与任何微服务代理上的 Java 工程都没有强耦合依赖，原因是：

- 这样才能保证契约可以被统一下发到每个微服务代理
- 这样才能保证调用方和提供方只围绕稳定的数据结构协作
- 这样才能避免某个代理本地才存在的类型、工具类或运行时对象污染远程调用边界

如果接口类型依赖具体代理工程，常见问题包括：

- 某些代理节点缺少该类型或其依赖，导致调用失败
- 接口升级时牵连多个代理工程一起发布
- 返回对象混入行为逻辑，导致远程传输语义不稳定
- DTO 没有序列化能力，导致请求参数或返回结果无法跨代理传输

### 4. 应避免出现在微服务接口上的类型

下面这些类型不应直接出现在微服务接口方法签名里：

- 具体代理工程里的私有业务对象
- 带复杂行为的富领域对象
- 管理器、服务定位器、DAO、连接对象、上下文对象
- 与 Cell 运行环境强绑定的对象
- 依赖本地代码执行语义才能成立的回调或闭包对象
- 没有实现序列化约定的自定义 DTO/POJO

### 5. 建议写法

建议把微服务接口拆成两层认知：

- 接口层：只表达稳定的请求 DTO、返回 DTO、错误语义
- 实现层：在服务内部把 DTO 转换成领域对象、数据库对象或内部上下文对象

这样做的直接效果是：

- 接口契约稳定
- 发布边界清晰
- 代理间依赖最小化
- 更容易做跨服务升级和灰度

同时保留普通 Cell 的结构约束：

- 接口名、实现名和包结构保持一一对应
- 接口负责暴露能力边界，实现负责承载业务逻辑
- 调用方面向接口编程，不面向实现类编程

这里说的“普通 Cell 结构约束”，仅限接口与实现分层、包结构、命名和面向接口编程这些基础规则，不包含服务 Cell 的生命周期模型。

### 6. 序列化要求

对微服务接口来说，只要方法签名中出现非基础类型，就必须把“可序列化”当作契约的一部分，而不是实现细节。

最低要求：

- 请求 DTO 和返回 DTO 明确实现 `java.io.Serializable`
- 显式声明 `serialVersionUID`
- DTO 字段继续保持数据化、简单化，避免塞入不可稳定传输的运行时对象

如果一个类型只是“长得像 DTO”，但没有序列化能力，在微服务场景里仍然是不合格的接口类型。

## 四个类各自负责什么

### 1. `TsHelper`

`TsHelper` 是 TinyService 的通用入口类，职责分成两类：

- 集群角色与节点信息：`isChief()`、`isServant()`、`isMember()`、`isConsumer()`、`getMyUri()`、`getChiefUri()`
- 微服务访问入口：`openService(Class<T> intfClass, TsOption option)`

最关键的方法是：

```java
public static <T> T openService(Class<T> intfClass, TsOption option) throws Exception
```

它会根据当前进程角色转发到：

- Consumer 进程：`TsConsumer.get().openService(...)`
- Member/Servant/Chief 进程：`TsMember.get().openService(...)`

源码注释已经确认：`openService(...)` 返回的微服务对象可以反复使用，也支持多线程并发使用，内部会自己处理重路由、重连等。

还要注意两个辅助能力：

- `prepareServantIntf()`：拿到管理层服务接口，优先可用 servant，不行再回退 chief
- `callServant(...)`：执行针对管理层接口 `TsClusterIntf` 的函数式调用

这两个方法是“调用集群管理能力”的入口，不是普通业务微服务的标准入口。业务微服务调用应优先写成 `TsHelper.openService(...)`。

### 2. `TsOption`

`TsOption` 是调用侧参数容器，控制一次“打开微服务代理对象”的行为。已核实的能力包括：

- 路由过滤：`setRouteID(...)`、`setRouteExp(...)`
- 追踪开关：`setEnableTrack(...)`
- 超时控制：`setConnectTimeout(...)`、`setInvokeTimeout(...)`
- 重路由间隔：`setRerouteInterval(...)`
- 重试次数与间隔：`setRetryTime(...)`、`setRetryInterval(...)`
- 消费端 Hook：`setHook(TsHook hook)`

常用构造方式：

```java
TsOption.getDefault()
TsOption.build()
TsOption.buildId("routeA")
TsOption.buildExp("^prd-.*$")
```

使用规则：

- 没有特殊需求时，从 `TsOption.build()` 开始链式设置最清晰
- 只想按固定路由 ID 过滤时，用 `buildId(...)`
- 只想按路由表达式过滤时，用 `buildExp(...)`
- 需要消费端拦截、熔断、打点、动态追踪时，用 `setHook(...)`

重要注意事项：

- `TsOption` 是消费端参数，不是提供端注册配置
- `TsOption.setHook(...)` 的优先级高于注册表里的 `consumerHook`
- 不建议同时设置 `routeID` 和 `routeExp`

最后一条必须强调。源码顶部注释写的是“两者同时指定是或逻辑”，但 `isRouteMatch(...)` 的实际代码分支是先判断 `routeID`，命中后直接返回，不再进入 `routeExp` 分支。为了避免误判，技能输出里不要建议同时设置两者。

### 3. `TsHook`

`TsHook` 是消费端 Hook，接口定义如下：

```java
public interface TsHook
{
    void before(CRpcClient target, TsRequest request);
    void after(CRpcClient target, TsRequest request, Object result);
    void failed(CRpcClient target, TsRequest request, Throwable error);
}
```

执行时机：

- `before(...)`：发起远程调用前
- `after(...)`：远程调用成功返回后
- `failed(...)`：远程调用抛错时

源码注释明确说明：`before(...)` 比“是否追踪”的判断还早，因此可以在这里动态干预本次请求是否打开调用链追踪。

典型用途：

- 熔断
- 消费端调用统计
- 动态决定是否开启追踪
- 失败告警

工程内现成示例是 `tiny.service.hook.AlarmHook`，它在 `failed(...)` 中构造 `TsAlarm` 并上报告警。

### 4. `TsProviderHook`

`TsProviderHook` 是提供端 Hook，接口定义如下：

```java
public interface TsProviderHook
{
    void before(TsRequest request);
    void after(TsRequest request, Object result);
    void failed(TsRequest request, Throwable error);
}
```

执行位置在服务提供者一侧，由 `TsProvider.executeRpcRequest(...)` 调用。

执行顺序已经由源码确认：

1. 根据注册表拿到 `providerHook`
2. 执行 `hook.before(tsReq)`
3. 执行真实服务方法
4. 成功则执行 `hook.after(tsReq, ret)`
5. 失败则执行 `hook.failed(tsReq, err)`

典型用途：

- 限流
- 降级
- 提供端审计
- 提供端异常打点

## Hook 的来源和优先级

### 消费端 Hook

消费端 Hook 的选择逻辑在 `TsRpcProxyCaller.getHook(...)`，优先级如下：

1. `TsOption.getHook()` 不为空时，直接使用调用方传入的 Hook
2. 否则读取注册表中的 `consumerHook`
3. 两者都没有时，不执行消费端 Hook

这意味着：

- 想对某一次调用做临时拦截，优先用 `TsOption.setHook(...)`
- 想对某个服务的所有消费调用统一拦截，才考虑注册表 `consumerHook`

### 提供端 Hook

提供端 Hook 不从 `TsOption` 读取，只从注册表 `providerHook` 读取。

这意味着：

- `TsProviderHook` 是服务提供者行为
- 不能指望调用方通过 `TsOption` 去临时改写提供端 Hook

## 追踪开关的判断顺序

`TsRegEntryInfo` 注释已经写明追踪开关的判断顺序，结合调用链可整理成下面这条规则：

1. 请求对象上是否已经带了追踪标志
2. 当前调用栈是否存在父调用，若父调用已开启追踪则继承
3. 注册表里该服务是否 `enableTrack=true`
4. 插件全局默认追踪开关

因此：

- 调用方想强制本次调用开启追踪，优先在 `TsOption` 或消费端 `TsHook.before(...)` 中处理
- 运维或平台想默认追踪某个服务，配注册表 `enableTrack`

## 路由是怎么生效的

`TsOption.prepareValidRoute(...)` 已确认路由筛选流程如下：

1. 先根据服务键读取注册表 `TsRegEntryInfo`
2. 如果注册表没有路由表：
   - 调用侧也没指定路由时，默认可匹配全部存活代理
   - 调用侧指定了 `routeID/routeExp`，会抛路由不匹配异常
3. 如果注册表有路由表：
   - 只保留和 `TsOption` 匹配的 `TsRegAgentPath`
4. 再剔除 `excludeAgents`
5. 再剔除不健康代理
6. 最后结合物理负载、虚拟负载、本地进程权重进行选择

直接含义：

- 调用侧指定路由前，先确认服务注册表里确实配置了对应路由
- 路由错配时常见异常是“没有匹配代理”或“没有可用提供者”

## 微服务调用示例

下面的接口名和业务类名是示例占位名，用来说明写法，不表示仓库里已存在这些业务接口。

其中接口命名默认按普通 Cell 风格使用 `I` 前缀，例如 `IOrderQueryService`、`IInventoryService`、`IPaymentService`。

### 示例 0：接口签名应该怎么收敛

推荐写法：

```java
import java.io.Serializable;

import bap.cells.BasicCell;
import cell.CellIntf;

public interface IOrderQueryService extends CellIntf
{
    OrderQueryResult queryDetail(OrderQueryParam param) throws Exception;
}

public class OrderQueryParam implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String tenantCode;

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getTenantCode()
    {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode)
    {
        this.tenantCode = tenantCode;
    }
}

public class OrderQueryResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String status;
    private long amount;

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public long getAmount()
    {
        return amount;
    }

    public void setAmount(long amount)
    {
        this.amount = amount;
    }
}

public class COrderQueryService extends BasicCell implements IOrderQueryService
{
    @Override
    public OrderQueryResult queryDetail(OrderQueryParam param) throws Exception
    {
        OrderQueryResult result = new OrderQueryResult();
        result.setOrderNo(param.getOrderNo());
        return result;
    }
}
```

如果还原成普通 Cell 的组织方式，它至少应保持下面这种结构意识：

```java
package cell.order;

import cell.CellIntf;

public interface IOrderQueryService extends CellIntf
{
    OrderQueryResult queryDetail(OrderQueryParam param) throws Exception;
}

package cell.order.impl;

import bap.cells.BasicCell;
import cell.order.IOrderQueryService;

public class COrderQueryService extends BasicCell implements IOrderQueryService
{
    @Override
    public OrderQueryResult queryDetail(OrderQueryParam param) throws Exception
    {
        return new OrderQueryResult();
    }
}
```

这个例子不是要求你必须使用某个具体类名前缀，而是强调：

- 接口和实现要分离
- 包结构要稳定
- 微服务只是比普通 Cell 多了一层远程契约限制，不是少了接口抽象这一层

不推荐写法：

```java
import java.io.Serializable;

public interface IOrderQueryService
{
    OrderDomainModel queryDetail(OrderManager mgr, OrderQueryContext context) throws Exception;
}

public class OrderQueryParam implements Serializable
{
    private String orderNo;
}
```

原因不是“代码风格不好”，而是这种签名把接口绑死到了某个代理本地工程对象上；另外就算自定义了 `OrderQueryParam`，如果它没有完整满足序列化约束，在微服务调用里也同样不合格。

### 示例 1：最基础的调用

```java
import tiny.service.cmn.TsHelper;
import tiny.service.cmn.TsOption;

public class OrderQueryFacade
{
    public OrderQueryResult query(String orderNo, String tenantCode) throws Exception
    {
        OrderQueryParam param = new OrderQueryParam();
        param.setOrderNo(orderNo);
        param.setTenantCode(tenantCode);

        IOrderQueryService service = TsHelper.openService(
            IOrderQueryService.class,
            TsOption.build()
        );
        return service.queryDetail(param);
    }
}
```

适合场景：

- 没有特定路由要求
- 使用默认追踪、超时、重试策略

### 示例 2：指定路由、超时和重试

```java
import tiny.service.cmn.TsHelper;
import tiny.service.cmn.TsOption;

public class InventoryGateway
{
    private final IInventoryService service;

    public InventoryGateway() throws Exception
    {
        TsOption option = TsOption.build()
            .setRouteID("prd-sh")
            .setConnectTimeout(2000)
            .setInvokeTimeout(5000)
            .setRetryTime(2)
            .setRetryInterval(300);

        this.service = TsHelper.openService(IInventoryService.class, option);
    }

    public StockDto queryStock(String sku) throws Exception
    {
        return service.queryStock(sku);
    }
}
```

适合场景：

- 明确只允许打到某个路由分组
- 需要收紧连接超时和调用超时
- 允许失败后切换提供者再试

### 示例 3：用消费端 Hook 做失败告警或动态追踪

```java
import com.leavay.nio.crpc.CRpcClient;
import com.leavay.nio.crpc.TsRequest;

import tiny.service.cmn.TsHelper;
import tiny.service.cmn.TsHook;
import tiny.service.cmn.TsOption;

public class RemoteAuditHook implements TsHook
{
    @Override
    public void before(CRpcClient target, TsRequest request)
    {
        request.setTrackEnable(true);
    }

    @Override
    public void after(CRpcClient target, TsRequest request, Object result)
    {
    }

    @Override
    public void failed(CRpcClient target, TsRequest request, Throwable error)
    {
        // 这里补充告警、日志或熔断状态更新
    }
}

public class PaymentFacade
{
    private final IPaymentService service;

    public PaymentFacade() throws Exception
    {
        TsOption option = TsOption.build()
            .setHook(new RemoteAuditHook())
            .setInvokeTimeout(8000);

        this.service = TsHelper.openService(IPaymentService.class, option);
    }

    public PayResult pay(PayCommand cmd) throws Exception
    {
        return service.pay(cmd);
    }
}
```

这个模式说明了两件事：

- `TsHook.before(...)` 可以在真正调用前强制打开追踪
- 只要 `TsOption.setHook(...)` 传了本地 Hook，就会覆盖注册表 `consumerHook`

### 示例 4：提供端 Hook 做限流或审计

```java
import com.leavay.nio.crpc.TsRequest;

import tiny.service.cmn.TsProviderHook;

public class ProviderAuditHook implements TsProviderHook
{
    @Override
    public void before(TsRequest request)
    {
        // 这里做限流、鉴权补充、审计记录等
    }

    @Override
    public void after(TsRequest request, Object result)
    {
    }

    @Override
    public void failed(TsRequest request, Throwable error)
    {
        // 这里记录失败审计或上报告警
    }
}
```

该 Hook 要放到服务注册表的 `providerHook` 字段里，调用链才会在提供端执行。它不是通过 `TsOption` 注入的。

## 写技能输出时建议直接复用的结论

- 写“如何定义微服务接口”时，先强调它本质上是普通 Cell 风格接口，但契约类型必须跨代理解耦
- 写“如何组织微服务代码”时，明确要求接口/实现分层、稳定包结构、面向接口编程，这些都沿用普通 Cell 的基础规范
- 写组织规范时，要主动排除服务 Cell 写法，不要引入 `ServiceCellIntf`、`BasicServiceCell` 或生命周期方法
- 写接口签名时，默认优先使用基础类型、集合类型和可序列化的贫血 DTO/POJO
- 写“如何调用某个微服务”时，默认给出 `TsHelper.openService(接口类, TsOption.build()...)`
- 写“如何加熔断/告警/动态追踪”时，优先从 `TsHook` 切入，并说明这是消费端能力
- 写“如何做限流/降级/执行前审计”时，优先从 `TsProviderHook` 切入，并说明这是提供端能力
- 写“如何指定机房、环境、租户路由”时，优先说明 `TsOption.setRouteID(...)` 或 `setRouteExp(...)`
- 写“为什么路由不生效”时，同时检查注册表 `routeTable`、`excludeAgents`、节点健康状态

## 常见误区

- 觉得“微服务”就不需要遵循普通 Cell 的接口/实现分层规范
- 把普通 Cell 规范错误扩大成服务 Cell 规范，进而把微服务接口写成服务 Cell
- 直接把远程服务能力写成实现类或工具类，跳过接口定义
- 把微服务接口当成进程内接口，直接暴露代理私有工程类型
- 在接口参数或返回值里混入富领域对象、管理器对象或上下文对象
- 使用了自定义 DTO/POJO，却没有实现序列化约定
- 把 `TsProviderHook` 当成调用侧能力
- 认为 `TsOption` 可以影响提供端 Hook
- 在服务注册表没有路由表时，仍然在调用端强行指定 `routeID/routeExp`
- 同时设置 `routeID` 和 `routeExp`，期待两者叠加
- 把 `prepareServantIntf()` 误用成普通业务微服务的调用入口
