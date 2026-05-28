---
name: gpf-cloud-cell
description: GPF 云开发 Cell 技能。用于实现和修改基础 Cell、服务 Cell、资源 Cell、异步 Cell、云配置和云 UDF 相关代码，并处理远程联调与资源生命周期问题；适用于最终交付物是 Cell 的任务，不适用于 HTTP 接口、规则函数或权限矩阵任务。
---

# GPF 云开发 Cell

## 适用范围

适用：

- 新增或修改基础 Cell、服务 Cell、资源 Cell、异步 Cell
- 配置类绑定、云配置读取、云 UDF 整理
- 远程联调、资源释放、生命周期排障

不适用：

- 对外暴露 HTTP 接口
- 提交前校验、数据填值、环境变量驱动的规则函数
- 鉴权规则、授权规则、身份匹配

## 输入契约

至少确认：

- 目标 Cell 类型
- 运行时是否需要缓存、线程池、连接、配置或异步能力
- 现有接口、实现类、包结构或调用方

## 执行流程

1. 先读 `references/overview.md` 判断当前需求属于哪一类 Cell。
2. 再读 `references/core-concepts.md` 确认工厂获取、包结构、配置和 UDF 约束。
3. 涉及 GPF API 调用时，必须同时加载 `gpf-api-call` 技能，不得只凭本技能资料直接编写 API 调用代码。
4. 生成代码前读 `references/workflow.md` 和 `references/patterns.md`，选择最接近的实现模式。
5. 如果用户在排错，优先读 `references/errors.md`。
6. 输出前按 `references/checklist.md` 自检，并参考 `assets/examples/` 中最接近的模板。

## 输出契约

输出必须包含：

- Cell 类型与选择原因
- 关键代码或修改点
- 生命周期、配置、联调或资源管理说明
- 不确定前提与后续风险

## 自检清单

- 是否用对了 Cell 类型
- 是否说明了获取方式与生命周期
- 是否避免了不可序列化对象直接跨边界传递
- 是否给出了配置和调试前提

## 强制规范

- 严禁编造工程中已存在类的包路径、接口定义、方法签名、继承关系或返回类型
- 只要输出里引用现有 Cell、配置类、资源类、异步类或工厂接口，就必须先以当前工程源码或技能内源码快照为准完成核实
- 如果当前技能目录内还没有对应源码快照，应先根据 `references/源码类索引.md` 定位待核实类，再决定是否把最小必要源码收敛进技能目录
- 如果类名能确认但包路径、接口方法或实现细节不能确认，必须明确标注“待从源码核实”，不得补全猜测值
- 涉及 GPF API 调用时，必须同时加载 `gpf-api-call` 技能，不能由本技能单独定义 API 调用规则

## 引用导航

- 总览：`references/overview.md`
- 核心概念：`references/core-concepts.md`
- 执行流程：`references/workflow.md`
- 代码模式：`references/patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`
- 类型与案例索引：`references/资料索引.md`
- 源码类索引：`references/源码类索引.md`
- 源码快照：`assets/source/`
- 示例模板：`assets/examples/`

## 最小验证

输入：

- “实现一个带缓存和线程池的服务 Cell，并说明如何关闭资源”

预期：

- 选用服务 Cell
- 给出 `doStartService` 与 `doStopService` 的关键处理
- 明确线程池和缓存的释放方式
