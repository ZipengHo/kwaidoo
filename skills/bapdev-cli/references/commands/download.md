# download

从 BAP 服务器下载项目文件到本地目录。

## 语法

```bash
bapdev [全局选项] download [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-o, --output <dir>` | 输出目录路径 | 是 |
| `--init-git-baseline` | 下载完成后提交 Git 基线，并写入 `.bapdev-cli` 基线状态 | 否 |
| `--git-message <message>` | 初始化 Git 基线时使用的提交说明 | 否 |

## 前提条件

需要能从 `.bapdev-cli` 或显式命令参数解析到项目标识。

## 行为说明

- 下载完成后，会补齐 `.develop`、`.launch`、`.classpath` 与 `.bapdev-cli`。
- 自动生成的 `.develop` 不包含密码，`Password` 字段固定为空字符串。
- `.bapdev-cli` 中会写入云工程元数据与基础配置，供后续命令复用当前工程连接信息。
  其中 `project` 记录项目ID，`projectName` 记录工程名称。
- 下载工作流应先等待下载与解压完全结束，再由上层流程询问用户是否提交 Git 仓库。
- 只有用户明确要求提交 Git 基线时，才应追加 `--init-git-baseline`。

## 工作流

```text
开始
  ↓
执行 `download`
  ↓
等待下载、解压、`.classpath`、`.develop`、`.bapdev-cli` 写入全部完成
  ↓
输出下载结果
  ↓
询问用户下一步
  ├─ 不提交 Git -> 直接结束
  └─ 提交 Git 仓库
      ↓
    检查当前目录是否在 Git 仓库内
      ├─ 否 -> 提示用户先初始化或指定 Git 仓库，再决定是否继续
      └─ 是 -> 输出基线提交摘要
               ↓
             用户确认
               ↓
             追加 `--init-git-baseline`
               ↓
             提交 Git 基线，并写入 `.bapdev-cli` 基线状态
```

## 示例

```bash
bapdev download --output /path/to/dir
bapdev download --output ./local-copy
bapdev --project my-project download --output ./local-copy --init-git-baseline --git-message "初始化云工程基线"
```

## 输出示例

下载过程中可能输出 JSON 进度流：

```json
{"success":true,"data":{"percent":50,"status":"downloading","file":"file.java"}}
```

最终结果：

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "下载完成",
  "data": {
    "downloadedFiles": 42,
    "outputDir": "/path/to/dir",
    "gitBaselineInitialized": true,
    "gitCommitted": true,
    "gitCommit": "abc123",
    "gitBranch": "main",
    "cloudVersion": "12"
  }
}
```
