# unit-test

在本地直接执行工程单元测试，优先使用工程自身的 `bin`、`lib` 和 `.classpath` 组装运行时 classpath。

从 2026-03-20 起，`unit-test` 默认执行模式为 `fork`：

- 父进程先解析工程运行时 classpath
- 再启动新的 Java 子进程执行 JUnit
- 这样更接近 IDE 里“启动前就把 classpath 准备好”的运行方式

仅在需要对比旧行为或排查类加载问题时，才建议显式切回 `in-process`。

## 语法

```bash
bapdev unit-test [选项]
```

也可以通过技能包装脚本执行：

```bash
"${SKILL_DIR}/scripts/unit-test.sh" [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-C, --class <class>` | 指定单个测试类名（全限定名） | 否，与 `--package` 二选一 |
| `-P, --package <package>` | 指定包名扫描测试类 | 否，与 `--class` 二选一 |
| `-m, --method <method>` | 指定方法名过滤，必须与 `--class` 一起使用 | 否 |
| `--parallel` | 启用 JUnit 并行执行 | 否 |
| `--project-root <dir>` | 项目根目录，默认当前目录 | 否 |
| `--execution-mode <mode>` | 测试执行模式，支持 `fork` 和 `in-process`，默认 `fork` | 否 |
| `--show-classpath-details` | 输出 `classpathMode`、`classpathFile`、`runtimeClasspath` 明细 | 否 |

## 规则

- 默认 `fork` 模式会先在父进程中解析工程 classpath，再启动子进程执行测试。
- `fork` 模式更适合 JUnit4 测试、`SilentBapTester` 这类依赖完整启动 classpath 的场景。
- `in-process` 模式会在当前 JVM 里创建 `URLClassLoader` 再执行测试，仅用于兼容或问题对比。
- 如果工程根目录存在 `.classpath`，会优先按其中的 `src`、`lib`、`output` 条目组装运行时 classpath。
- 如果没有 `.classpath`，会回退为约定模式：优先使用 `bin` 作为输出目录，并扫描 `lib` 下全部 `jar` / `zip`。
- 为避免测试框架冲突，运行项目测试时会自动排除工程 `lib` 中的 `junit-*`、`junit-platform-*`、`junit-vintage-*`、`hamcrest-*`、`opentest4j-*`、`apiguardian-*` 等测试框架 jar。
- 默认只返回测试摘要和每个用例的结果，不输出超长 classpath 列表。
- 需要排查 classpath、类加载顺序或 fork / in-process 差异时，再显式加 `--show-classpath-details`。
- 如果命令返回 `NO_CONFIG`、`NOT_CONNECTED` 或提示未连接 BAP，应先确认当前工程已提供可用的 `uri`、项目标识和登录信息。

## 单元测试类要求

- 如果测试里会调用 `Cells.get(...)`、接管本地 Cell、或依赖 BAP 调试运行环境初始化，测试类应继承 `SilentBapTester`。
- 对这类测试，仅写普通 JUnit4 / JUnit5 测试类而不继承 `SilentBapTester`，通常会导致 Cell Factory 未初始化。
- 一般建议同时在测试类中声明当前测试会接管的 Cell 接口与实现类，避免本地接管不完整。

标准格式示例：

```java
package test;

import static org.junit.Assert.assertEquals;

import cell.demo.CGreetingCell;
import org.junit.Test;

import bap.cells.Cells;
import bap.tester.SilentBapTester;
import cell.demo.IGreetingCell;

/**
 * 普通 Cell 单元测试样例。
 *
 * <p>业务测试通常只需要继承 SilentBapTester，
 * 并声明当前测试需要本地接管的 Cell 类型。
 *
 * <p>注意：为了避免接管不完整，通常需要同时声明 Cell 接口和实现类。
 */
public class GreetingCellTest extends SilentBapTester
{
    @Test
    public void testGreet()
    {
        IGreetingCell greetingCell = Cells.get(IGreetingCell.class);
        String result = greetingCell.greet("张三");
        System.out.println(result);
        assertEquals("Hello, 张三!", result);
    }
}
```

## 常见报错

### 1. `bap.cells.Cells$CellFactoryUninitException: Please initialize cell factory first. For Example : Start cell server, start BAP Debugger or extend BAP Tester`

含义：

- 当前测试在调用 `Cells.get(...)` 时，Cell Factory 还没有初始化完成。

常见原因：

- 测试类没有继承 `SilentBapTester`
- 用 `org.junit.runner.JUnitCore` 或不完整 classpath 直接跑了依赖 BAP 运行环境的测试
- 本地接管的 Cell 类型声明不完整，导致启动链路没有按预期初始化

处理建议：

- 优先把测试类改为继承 `SilentBapTester`
- 优先使用 `unit-test` 默认 `fork` 模式，或使用已经验证通过的外部 classpath 方案
- 确认测试工程的 `bin`、`lib`、`.classpath` 完整，且相关 Cell 接口、实现类都已编译到位
- 如果是在 IDE 里可以通过、在 CLI 里不通过，优先怀疑执行链路不是“启动前准备好 classpath”的方式

### 2. 连接 BAP 服务失败

含义：

- CLI 在初始化 `SilentBapTester` 或测试运行环境时，无法建立到目标 BAP 服务的连接。

常见原因：

- BAP 服务未启动，或当前机器到目标 `ws://...` 地址网络不通
- 当前工程配置中的连接信息已失效、不完整或与目标环境不匹配
- 当前 shell 或工程配置未提供可用的连接信息，导致配置不完整

处理建议：

- 先检查服务本身是否连接正常、地址是否为预期服务器
- 再检查当前工程连接配置是否仍有效
- 如果怀疑当前工程连接信息已失效，优先重新补全配置：

```bash
请补全当前工程可用的连接配置后重试。
```

- 如果命令行里看到 `NO_CONFIG`、`NOT_CONNECTED`、`CONNECTION_FAILED`，也应优先回到连接配置与服务连通性检查，而不是先改测试代码

## 示例

```bash
bapdev unit-test --class test.HelloServiceSilentTest
bapdev unit-test --class test.HelloServiceSilentTest --project-root /path/to/project
bapdev unit-test --class test.HelloServiceSilentTest --execution-mode fork
bapdev unit-test --class test.HelloServiceSilentTest --execution-mode in-process
bapdev unit-test --package test --parallel
bapdev unit-test --class test.HelloServiceSilentTest --method testSayHello
bapdev unit-test --class test.HelloServiceSilentTest --show-classpath-details
"${SKILL_DIR}/scripts/unit-test.sh" --class test.HelloServiceSilentTest --execution-mode fork
```

## 输出说明

- 成功时：
  - `data.executionMode` 表示本次实际执行模式
  - `data.preparedClasspath` 表示当前执行链路是否已在启动前准备好 classpath
  - `data.result` 包含测试总数、通过数、失败数、跳过数、退出码和每个测试方法的详细结果
- 失败时：
  - `error` / `message` 给出失败类型和原因
  - 如果显式加了 `--show-classpath-details`，会额外返回 classpath 明细，便于排查

## 输出示例

```json
{
  "success": true,
  "data": {
    "testClass": "test.HelloServiceSilentTest",
    "projectRoot": "/path/to/project",
    "parallel": false,
    "executionMode": "fork",
    "preparedClasspath": true,
    "result": {
      "totalTests": 1,
      "passedTests": 1,
      "failedTests": 0,
      "skippedTests": 0,
      "exitCode": 0,
      "testResults": [
        {
          "testClass": "test.HelloServiceSilentTest",
          "methodName": "testSayHello",
          "status": "PASSED",
          "durationMs": 128
        }
      ]
    }
  }
}
```
