# history

查询文件或项目的版本历史记录。

## 语法

```bash
bapdev [全局选项] history [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-f, --file <file>` | 单个文件路径，查询文件历史时使用 | 否，与 `--files` 二选一 |
| `--files <files>` | 多个文件路径，逗号分隔 | 否，与 `--file` 二选一 |
| `-l, --limit <n>` | 返回数量限制，默认 `10` | 否 |

## 说明

`history` 的帮助输出格式与其它子命令略有不同，但参数含义一致。

## 示例

```bash
bapdev history
bapdev history --file /path/to/file.java
bapdev history --files file1.java,file2.java
bapdev history --limit 20
```

## 输出示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "history": [
      {
        "version": "v1.0.1",
        "author": "admin",
        "date": "2024-01-15 10:30:00",
        "message": "修复 bug"
      }
    ]
  }
}
```
