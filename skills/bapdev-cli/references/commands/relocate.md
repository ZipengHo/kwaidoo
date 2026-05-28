# relocate

更新当前工程记录的目标服务器与项目定位信息。

## 语法

```bash
bapdev [全局选项] relocate [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-d, --dir <dir>` | 工作目录，默认当前目录 | 否 |
| `--history` | 列出当前工程可复用的 relocate 历史 | 否 |
| `--history-index <n>` | 按序号复用一条 relocate 历史记录 | 否 |

## 前提条件

必须提供 `ws://` 或 `wss://` 的目标地址。
如果使用 `--history-index` 复用历史记录，则可直接复用其中保存的地址和项目。

## 示例

```bash
bapdev relocate --history
bapdev --uri ws://new-server:2020 --project new-project relocate
bapdev --uri ws://new-server:2020 --project new-project relocate --dir /path/to/project
bapdev relocate --history-index 1 --dir /path/to/project
```

## 输出示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "oldUri": "ws://old-server:2020",
    "newUri": "ws://new-server:2020",
    "oldProject": "old-project",
    "newProject": "new-project",
    "historyIndex": 1
  }
}
```
