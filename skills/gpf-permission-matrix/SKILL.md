---
name: gpf-permission-matrix
description: GPF 权限领域扩展技能。用于补充身份识别、授权语义、权限对象和矩阵边界等权限领域专有约束；当最终产物仍是规则函数接口或实现时，应以 `gpf-rule-function` 为主技能，本技能只负责补充权限领域差异，不适用于 HTTP 接口或云开发 Cell 任务。
---

# GPF 权限领域扩展

## 适用范围

适用：

- 身份识别语义
- 权限授予语义
- 字段或按钮权限对象约束
- 关联视图权限控制语义
- 矩阵配置与规则代码的边界判断

不适用：

- 脱离规则函数主技能单独生成通用规则函数规范
- HTTP 接口
- 服务 Cell 生命周期问题

## 输入契约

至少确认：

- 是身份识别还是权限授予
- 控制对象是什么
- 规则依赖哪些业务字段或关联数据
- 现有矩阵配置或规则类
- 最终产物是否仍为规则函数接口或实现

## 执行流程

1. 先读 `references/overview.md` 判断属于身份识别、权限授予还是矩阵边界问题。
2. 如果最终产物仍是规则函数接口或实现，必须先加载 `gpf-rule-function`，再把本技能作为权限领域补充。
3. 再读 `references/core-concepts.md`，确认 `IdentifyMatchParam`、表达式、权限对象和矩阵边界约束。
4. 生成代码前读 `references/workflow.md` 和 `references/patterns.md`。
5. 如果在排错，优先读 `references/errors.md`。
6. 输出前按 `references/checklist.md` 自检，并参考 `assets/examples/`。

## 输出契约

输出必须包含：

- 权限领域规则类型
- 身份对象或控制对象
- 授权范围和生效边界
- 关键表达式、对象约束或矩阵边界说明
- 业务前提和风险

## 强制规范

- 本技能不是独立代码形态定义，不能替代 `gpf-rule-function` 提供通用规则函数开发规范
- 只要最终产物是规则函数接口或实现，必须先以 `gpf-rule-function` 为主，再按需补充本技能
- 严禁编造工程中已存在类的包路径、接口定义、方法签名、继承关系或返回类型
- 只要输出里引用现有权限矩阵规则类、身份匹配参数类、授权对象类或相关接口，就必须先以当前工程源码或技能内源码快照为准完成核实
- 如果当前技能目录内还没有对应源码快照，应先根据 `references/源码类索引.md` 定位待核实类，再决定是否把最小必要源码收敛进技能目录
- 如果类名能确认但包路径、接口方法或实现细节不能确认，必须明确标注“待从源码核实”，不得补全猜测值

## 引用导航

- 总览：`references/overview.md`
- 核心概念：`references/core-concepts.md`
- 执行流程：`references/workflow.md`
- 代码模式：`references/patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`
- 资料索引：`references/资料索引.md`
- 源码类索引：`references/源码类索引.md`
- 示例模板：`assets/examples/`
