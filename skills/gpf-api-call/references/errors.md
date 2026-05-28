# GPF 基础 API 常见错误

## 接口名错误

- 使用了虚构接口
- 把管理器职责混用

## 模型 ID 缺失

- 用户、角色、表单相关操作常依赖模型 ID

## 实体对象构造不完整

- 创建 `User`、`Role`、`Form` 时缺少关键字段
- 把关联字段当作普通字符串处理

## 字段名称与字段编码混用

- 把业务属性读写一律写成 `ByCode`
- 把 `Cnd` 场景和普通表单读写场景混在一起
- 表单 API 和查询语句对字段的要求不完全一致

## `TableData` 接口误用

- 把 `TableData` 写成 `List<Form>` 一样的 `isEmpty()`、`get(index)`、`remove(...)`
- 忘记 `TableData` 的真实方法是 `isEmtpy()`、`getData(...)`、`delete(...)`

## 事务未提交

- 修改成功但结果未持久化，常见于遗漏 `dao.commit()`

## 分页结果误用

- 把 `ResultSet` 当普通列表
- 忽略总数、页码、页大小
