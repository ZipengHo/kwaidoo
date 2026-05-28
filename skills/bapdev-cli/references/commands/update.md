# update

从云端更新文件到本地。

## 语法

```bash
bapdev [全局选项] update [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-f, --file <file>` | 要更新的单个文件路径 | 否，与 `--files` 二选一 |
| `--files <files>` | 要更新的多个文件路径，逗号分隔 | 否，与 `--file` 二选一 |
| `--force` | 强制覆盖本地修改 | 否 |

## 工作流

```text
开始
  ↓
先执行 `refresh`
  ↓
根据 `refresh` 结果判断
  ├─ 本地代码 vs 云工程代码 无差异
  │   ↓
  │  直接结束
  │
  └─ 本地代码 vs 云工程代码 有差异
      ↓
    列出差异文件
      ↓
    询问用户下一步
      ├─ 直接更新 -> 执行 `update`
      └─ 先比较差异 -> 先查看差异内容，再决定是否执行 `update`
```

## 使用规则

- `update` 前应先执行 `refresh`，不要在未知差异状态下直接覆盖本地文件。
- 如果 `refresh` 结果显示无差异，则不需要再执行 `update`。
- 如果 `refresh` 结果显示有差异，应先把差异文件列出来，再由用户决定是直接更新，还是先比较差异内容后再决定。

## 示例

```bash
bapdev update --file /path/to/file.java
bapdev update --files file1.java,file2.java
bapdev update --file file.java --force
```

## 输出示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "更新成功",
  "data": {
    "updatedFiles": ["file1.java", "file2.java"]
  }
}
```
