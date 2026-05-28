# 规则函数测试代码编写规范

本文档用于约束 `gpf-rule-function` 技能范围内的规则函数单元测试写法。适用于：

- 新增规则函数测试类
- 修复规则函数测试失败
- 排查 `Cells.get(...)`、`SilentBapTester`、测试类路径、依赖同步等问题

编写规则函数测试代码时，必须同时加载 `gpf-api-call` 技能，用于：

- 查看相关参数、上下文对象和数据对象的真实源码
- 核对可用 API 和真实构建方式
- 参考已有参数构建示例，避免在测试里编造对象结构或方法用法

## 1. 基本要求

### 1.1 必须继承 `SilentBapTester`

所有需要调用规则函数 Cell、通过 `Cells.get(...)` 获取实例、或依赖 BAP 本地测试环境初始化的测试类，都必须继承：

```java
bap.tester.SilentBapTester
```

标准示例：

```java
package cell.testdev.rule;

import bap.cells.Cells;
import bap.tester.SilentBapTester;
import org.junit.Before;
import org.junit.Test;

public class IFormDataExportRuleTest extends SilentBapTester {

    private IFormDataExportRule rule;

    @Before
    public void setUp() {
        rule = Cells.get(IFormDataExportRule.class);
    }

    @Test
    public void testSomething() throws Exception {
        // 测试逻辑
    }
}
```

### 1.2 为什么必须继承 `SilentBapTester`

`SilentBapTester` 会负责初始化 Cell Factory，这是 `Cells.get(...)` 能正常工作的前提。

如果未继承，常见报错如下：

```text
bap.cells.Cells$CellFactoryUninitException: Please initialize cell factory first.
For Example : Start cell server, start BAP Debugger or extend BAP Tester
```

## 2. 获取规则函数实例的方式

### 2.1 必须使用 `Cells.get(...)`

规则函数测试中，应通过 `Cells.get(...)` 获取被测规则函数实例：

```java
import bap.cells.Cells;

IFormDataExportRule rule = Cells.get(IFormDataExportRule.class);
```

### 2.2 禁止使用匿名类替代

下面这种写法禁止用于规则函数测试：

```java
IFormDataExportRule rule = new IFormDataExportRule() {};
```

原因：

- `CellIntf` 体系接口往往存在抽象方法约束
- 匿名类方式不能替代真实 Cell 运行环境
- 这类写法既不符合云工程测试规范，也容易掩盖真实初始化问题

## 3. 测试类命名与位置

### 3.1 命名规范

- 测试类命名统一为：`{被测类名}Test`
- 示例：`IFormDataExportRule` -> `IFormDataExportRuleTest`

### 3.2 文件位置

测试类应放在 `src/test` 目录下，包路径与被测类保持一致：

```text
src/core/cell/testdev/rule/
└── IFormDataExportRule.java

src/test/cell/testdev/rule/
└── IFormDataExportRuleTest.java
```

## 4. 规则函数测试的执行方式

### 4.1 首次测试前先同步依赖

如果是首次在本地跑规则函数测试，或云工程依赖已变更，应先同步依赖库：

```bash
使用 `bapdev-cli` 技能执行 `sync-libs`，并指定工程根目录
```

### 4.2 先编译工程

```bash
使用 `bapdev-cli` 技能执行 `compile`，并指定工程根目录
```

### 4.3 再执行单元测试

```bash
使用 `bapdev-cli` 技能执行 `unit-test`，测试类使用完整类名，并指定工程根目录
```

如果只跑单个方法：

```bash
使用 `bapdev-cli` 技能执行 `unit-test`，并通过 `--class` + `--method` 指定单个测试方法
```

## 5. 执行测试时的类名要求

执行测试类时，必须使用包含包名的完整类名：

```bash
# 错误
--class IFormDataExportRuleTest

# 正确
--class cell.testdev.rule.IFormDataExportRuleTest
```

## 6. 规则函数测试完整示例

```java
package cell.testdev.rule;

import bap.cells.Cells;
import bap.tester.SilentBapTester;
import gpf.adur.data.Form;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class IFormDataExportRuleTest extends SilentBapTester {

    private IFormDataExportRule rule;

    @Before
    public void setUp() {
        rule = Cells.get(IFormDataExportRule.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToExcel_NullFormModelId_ThrowsException() throws Exception {
        List<String> codes = Arrays.asList("CODE001");
        rule.exportToExcel(null, null, null, codes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToExcel_EmptyCodes_ThrowsException() throws Exception {
        rule.exportToExcel(null, null, "modelId", Collections.emptyList());
    }

    @Test
    public void testExportToExcel_SingleCode_BuildsCorrectCnd() throws Exception {
        String code = "SINGLE_CODE";
        Cnd cnd = Cnd.where(Form.Code, "IN", Collections.singletonList(code));
        assertNotNull(cnd);
    }
}
```

## 7. 环境变量 mock 数据构建方式

规则函数测试里如果需要手工构建环境变量或上下文对象，默认按下面的优先级处理。

### 7.1 `CellIntf` 和 `ServiceIntf`

- 如果类型属于 `CellIntf` 或 `ServiceIntf` 体系，优先使用：

```java
Cells.get(Xxx.class)
```

示例：

```java
IFormDataExportRule rule = Cells.get(IFormDataExportRule.class);
```

### 7.2 `ResourceCellIntf`

- 如果类型属于 `ResourceCellIntf`，优先直接创建实现类实例：

```java
new CXXXX()
```

示例：

```java
IContext context = new CContext();
```

### 7.3 其他普通类型

- 如果没有特殊说明，普通对象默认使用 `new` 方式构建。

示例：

```java
SomeDto dto = new SomeDto();
RpcMap<Object> env = new RpcMap<Object>();
```

### 7.4 `Progress`

- `Progress` 不要随意 `new`，统一使用：

```java
Progress progress = Progress.newTracer();
```

### 7.5 一句话规则

- `CellIntf` / `ServiceIntf`：`Cells.get(...)`
- `ResourceCellIntf`：`new CXXXX()`
- 其他类型：默认 `new`
- `Progress`：固定使用 `Progress.newTracer()`

### 7.6 环境变量 mock 示例速查表

下面的示例用于规则函数单元测试中快速准备环境变量。

默认规则：

- `CellIntf` / `ServiceIntf`：优先 `Cells.get(Xxx.class)`
- `ResourceCellIntf`：优先 `new CXXXX()`
- 其他普通类型：若无特殊说明，默认 `new`
- `Progress`：固定使用 `Progress.newTracer()`
- 如果某个变量通常由上游环境变量派生，测试中也优先按派生方式准备，而不是强行猜实现类

#### 通用上下文变量

| 变量 | mock 示例 |
|------|-----------|
| `$context$` | `IContext context = new CContext();` |
| `$form$` | `Form form = new Form();` |
| `$rowCodes$` | `List<String> rowCodes = Arrays.asList("CODE001", "CODE002");` |
| `$dao$` | `try (IDao dao = IDaoService.newIDao()) { ... }` |
| `$progress$` | `Progress progress = Progress.newTracer();` |
| `$operator$` | `User operator = 通过 IUserMgr 基于真实 userModelId 和用户名查询获取；不要伪造 User 对象。` |
| `$ruleNamespace$` | `Set<String> ruleNamespace = new LinkedHashSet<String>();` |
| `$env$` | `RpcMap<Object> env = new RpcMap<Object>();` |
| `$sessionInfo$` | `AppUserInfo sessionInfo = new AppUserInfo();` |
| `$formSaved$` | `Boolean formSaved = Boolean.TRUE;` |
| `$applicationSetting$` | `ApplicationSetting applicationSetting = new ApplicationSetting();` |
| `$userModelId$` | `String userModelId = 从真实应用配置、运行时上下文或测试环境中获取；不要使用假值占位。` |
| `$orgModelId$` | `String orgModelId = 从真实应用配置、运行时上下文或测试环境中获取；不要使用假值占位。` |

#### GPF 运行时变量

| 变量 | mock 示例 |
|------|-----------|
| `$IDCRuntimeContext$` | `IDCRuntimeContext rtx = new CDCRuntimeContext();` |
| `$operatorCode$` | `String operatorCode = "admin";` |

说明：

- 如果只是做参数校验测试，拿不到真实运行时对象时，可以先传 `null`，但不要把“模型 ID”误写成独立环境变量。
- `$dao$` 在测试中优先使用 `IDaoService.newIDao()` 创建，并通过 `try-with-resources` 自动释放，不建议把它简化成普通 `new` 或长期持有对象。
- `$userModelId$` 和 `$orgModelId$` 必须来自真实环境数据，否则依赖真实模型查询的代码通常无法跑通。
- `$operator$` 不应随意手工 `new User()` 或伪造字段；应基于真实 `userModelId` 和用户名通过 `IUserMgr` 查询获取，具体 API 以工程源码或 `gpf-api-call` 技能资料为准。

#### 前端界面变量（JDF）

| 变量 | mock 示例 |
|------|-----------|
| `$ActionParameter$` | `BaseFeActionParameter input = new BaseFeActionParameter();` |
| `$feContext$` | `PanelContext feContext = input.getPanelContext();` |
| `$currentComponent$` | `AbsComponent currentComponent = input.getCurrentComponent();` |
| `$feAppContext$` | `Context feAppContext = new Context();` |
| `$listener$` | `ListenerDto listener = input.getListener();` |
| `$event$` | `EventDto event = input.getEvent();` |
| `$SelectEditorQuerier$` | `SelectEditorQuerier querier = new SelectEditorQuerier();` |

说明：

- 前端变量通常通过 `$ActionParameter$` 派生，测试时优先构造 `input` 再拆取子变量。
- 如果当前测试不是前端规则，不要为了“凑全参数”硬加这些变量。
- 即使补了 mock 数据，前端界面变量相关规则通常也无法在没有真实前端界面环境的情况下保证运行成功。
- 因此这类测试除“仅验证参数非空、空值保护、分支保护”外，通常不要求也不建议做“必须运行成功”的断言。
- 如果用户没有明确要求补前端联调或真实界面环境测试，就不要把前端界面变量相关规则硬写成可本地完整跑通的单元测试。

#### 查询参数变量

| 变量 | mock 示例 |
|------|-----------|
| `$sysvar_cnd$` | `Cnd cnd = Cnd.NEW();` |
| `$sysvar_pageNo$` | `Integer pageNo = 1;` |
| `$sysvar_pageSize$` | `Integer pageSize = 20;` |
| `$queryNesting$` | `Boolean queryNesting = Boolean.FALSE;` |
| `$includeFields$` | `List<String> includeFields = Arrays.asList("编码", "名称");` |
| `$excludeFields$` | `List<String> excludeFields = Arrays.asList("创建人", "创建时间");` |

#### 流程引擎变量（WFE）

| 变量 | mock 示例 |
|------|-----------|
| `$FlowDto$` | `FlowDto flowDto = new FlowDto();` |
| `$FlowBehavior$` | `FlowBehavior flowBehavior = new FlowBehavior();` |
| `$RouterOption$` | `Map<String, RouterOption> routerOption = new HashMap<String, RouterOption>();` |
| `$WfeStepOperator$` | `WfeStepOperator stepOperator = new WfeStepOperator();` |
| `$NodeTriggerEvent$` | `String nodeTriggerEvent = "afterSubmit";` |
| `$DefaultFlowBehavior$` | `FlowBehavior defaultFlowBehavior = new FlowBehavior();` |
| `$DefaultNodeBehavior$` | `NodeBehavior defaultNodeBehavior = new NodeBehavior();` |

说明：

- 流程变量通常只在 WFE 规则中准备，不要在普通数据校验 / 数据填值规则里默认带入。
- 如果某个流程类型在当前工程里有更明确的构造方式，应以工程源码中的真实写法为准。

## 8. 常见问题

### 8.1 `ClassNotFoundException`

现象：

- 执行测试时报 `ClassNotFoundException: IFormDataExportRuleTest`

常见原因：

- 执行时没有使用包含包名的完整类名

处理建议：

- 改成完整类名，例如：

```bash
--class cell.testdev.rule.IFormDataExportRuleTest
```

### 8.2 `CellFactoryUninitException`

现象：

- 执行测试时报 `CellFactoryUninitException: Please initialize cell factory first`

常见原因：

- 测试类未继承 `SilentBapTester`

处理建议：

- 让测试类继承 `bap.tester.SilentBapTester`
- 确认测试中通过 `Cells.get(...)` 获取规则函数实例

### 8.3 `cannot find symbol: class SilentBapTester`

现象：

- 编译阶段提示找不到 `SilentBapTester`

常见原因：

- 本地依赖库未同步

处理建议：

- 执行 `sync-libs.sh` 同步依赖库
- 再执行 `compile.sh`

### 8.4 匿名类未实现抽象方法

现象：

- 使用 `new IXxxRule() {}` 时编译报错

常见原因：

- `CellIntf` 接口存在抽象方法约束

处理建议：

- 改为 `Cells.get(IXxxRule.class)`

### 8.5 使用 `@Ignore` 或空测试方法

现象：

- 测试类里通过 `@Ignore` 跳过测试
- 或者编写一个只有注释、没有真实断言和执行逻辑的空测试方法

这类写法一律视为不合规测试代码。

禁止示例：

```java
@Test
@Ignore
public void testExportFormToExcelByCodes_ValidCodes() throws Exception {
}
```

```java
@Test
public void testExportFormToExcelByCodes_ValidCodes() throws Exception {
    // 此测试需要真实环境支持
    // 先留空，后续再补
}
```

原因：

- `@Ignore` 会把真实未完成测试伪装成“已存在测试”
- 空方法或整段注释占位不会真正验证任何业务行为
- 这类写法会误导后续开发者，以为关键路径已经被覆盖

处理建议：

- 如果当前场景没有真实环境，改写成“参数非空 / 空值保护 / 分支保护”类测试
- 如果当前场景确实依赖真实前端或真实运行环境，明确说明“不做运行成功测试”，不要留下空测试方法
- 测试方法一旦存在，就必须包含真实执行逻辑和可验证断言

### 8.6 `column "bian1hao4" does not exist`

现象：

- 测试运行时报类似错误：

```text
column "bian1hao4" does not exist
```

常见原因：

- 把系统属性“编号”错误地按普通业务字段处理
- 例如写成：

```java
IFormMgr.get().getFieldCode("编号")
```

这可能会把“编号”转换成普通字段编码，例如 `bian1hao4`，而不是使用系统属性列。

处理建议：

- 如果当前字段是系统属性“编号”，优先直接使用：

```java
Form.Code
```

- 不要把系统属性字段一律交给 `getFieldCode(...)` 做普通字段编码转换
- 一旦出现这类报错，优先检查被测代码里是否错误地把“编号”走了普通字段编码路径

## 9. 最佳实践

1. 测试前先同步依赖，保证本地 `lib` 与云工程一致
2. 规则函数测试统一使用完整类名执行
3. 需要 `Cells.get(...)` 的测试统一继承 `SilentBapTester`
4. 优先覆盖参数校验、边界条件和异常路径
5. 前端界面变量相关规则在缺少真实前端环境时，优先只测非空入参与保护分支，不强求运行成功
6. 每个测试方法保持独立，不依赖其他测试执行顺序
7. 所有测试严禁使用 `@Ignore`
8. 测试代码严禁编写空方法、注释占位方法或“仅说明需要真实环境”的伪测试
