# commit

提交文件到云工程，并在成功后同步提交到 Git 仓库，同时更新项目根目录下 `.bapdev-cli` 的版本记录。

## 语法

```bash
bapdev [全局选项] commit [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-f, --file <file>` | 要提交的单个文件路径 | 否，与 `--files` 二选一 |
| `--files <files>` | 要提交的多个文件路径，逗号分隔 | 否，与 `--file` 二选一 |
| `-m, --message <message>` | 云工程与 Git 共用的提交消息 | 否 |
| `--project-root <dir>` | 项目根目录路径 | 否 |
| `--init-baseline` | 初始化 `.bapdev-cli` 基线，只记录当前云版本和 Git 版本，不执行真实提交 | 否 |
| `--confirm` | 确认执行真实云提交与 Git 提交；不传时只输出摘要 | 否 |

## 工作流

```text
开始
  ↓
先执行 `refresh`
  ↓
根据 `refresh` 结果判断
  ├─ 本地代码 vs 云工程代码 无差异
  │   ├─ 无基线 -> 自动记为基线，结束
  │   └─ 有基线
  │       ↓
  │     检查本地源码文件是否还需要提交到 Git
  │       ├─ 需要 -> 输出 Git 提交摘要，等用户确认
  │       └─ 不需要 -> 直接结束
  │
  └─ 本地代码 vs 云工程代码 有差异
      ↓
    只针对这些差异文件
      ↓
    获取云工程当前代码
      ├─ Java -> 获取当前云端源码
      └─ 资源 -> 获取当前云端资源内容
      ↓
    比较 云工程当前代码 vs Git基线版本代码
      ├─ 有差异 -> 由上层工作流提示用户处理冲突
      └─ 无差异 -> 输出提交摘要
                     ↓
                   用户追加 `--confirm`
                     ↓
                   调用 `commit` 真实提交到云工程
                     ↓
                   如有需要再提交到 Git
                     ↓
                   更新 `.bapdev-cli`
```

## 首次使用

首次在一个工程里启用该工作流时，先执行：

```bash
bapdev commit --project-root . --init-baseline
```

如果首次提交时目标文件与云工程完全一致，也可以直接执行普通 `commit`，CLI 会自动把当前状态视为基线并写入 `.bapdev-cli`。

## 提交确认

普通提交默认只输出摘要，例如：

```bash
bapdev commit --file src/cell/Test.java --message "修复问题"
```

确认摘要无误后，再执行：

```bash
bapdev commit --file src/cell/Test.java --message "修复问题" --confirm
```

## 比较限制

- 代码比较时，不能把 `history` 当作“当前云工程代码”来源。
- 代码比较时，也不能把 `download` 当作“当前云工程代码”来源。
- 当前云工程代码应通过 `fetch-current` 直接获取。
- 云工程当前代码与 Git 基线版本的冲突判断，应在上层工作流中完成；真实 `commit --confirm` 不再重复校验。

## 示例

```bash
bapdev commit --project-root . --init-baseline
bapdev commit --file /path/to/file.java --message "修复 bug"
bapdev commit --file /path/to/file.java --message "修复 bug" --confirm
bapdev commit --files file1.java,file2.java --message "添加新功能"
```

## 输出示例

```json
{
  "success": true,
  "data": {
    "projectRoot": "/path/to/project",
    "gitRepo": "/path/to/repo",
    "gitCommit": "abc123",
    "gitBranch": "main",
    "cloudVersionBefore": 12,
    "cloudVersionAfter": 13,
    "committedFiles": ["src/cell/Test.java"],
    "stateDir": "/path/to/project/.bapdev-cli"
  }
}
```

## 基线初始化输出示例

```json
{
  "success": true,
  "data": {
    "projectRoot": "/path/to/project",
    "gitRepo": "/path/to/repo",
    "cloudVersion": 12,
    "gitCommit": "abc123",
    "initialized": true
  }
}
```

## 命令特有失败

| 错误码 | 含义 |
|------|------|
| `BASELINE_REQUIRED` | 当前工程还没有 `.bapdev-cli` 基线，且本地存在待提交的云端差异文件，需先执行 `--init-baseline` |
| `PROJECT_UUID_MISSING` | 无法从 `.bapdev-cli` 或显式命令参数解析项目标识 |
