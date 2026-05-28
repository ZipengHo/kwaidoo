# refresh

刷新项目，检测本地与云端文件状态差异。

## 语法

```bash
bapdev [全局选项] refresh
```

## 前提条件

需要能从 `.bapdev-cli` 或显式命令参数解析到项目标识。

## 示例

```bash
bapdev refresh
bapdev --project my-project refresh
```

## 输出示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "data": {
    "modified": ["file1.java"],
    "added": ["file2.java"],
    "deleted": ["file3.java"],
    "conflicted": []
  }
}
```
