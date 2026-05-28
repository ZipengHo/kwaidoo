# GPF 基础 API 源码调用模式

本文件不是接口文档，而是从仓库源码和旧技能样例中提炼的稳定调用模式。

## 模式 1：先按编号查，再决定创建还是更新

适用场景：

- 用户导入
- 表单同步
- 需要避免重复创建

典型思路：

1. 先用 `queryUserByCode` 或 `queryFormUuidByCode`
2. 如果不存在，调用 `createUser` 或 `createForm`
3. 如果已存在，回填 UUID 后调用 `updateUser` 或 `updateForm`

## 模式 2：列表导出或迁移前先 `count`，再分页查询

适用场景：

- 批量导出
- 数据迁移
- 分页同步

典型思路：

1. 用 `countForm` 或类似接口查总数
2. 按页调用 `queryFormPage` 或 `queryUserPage`
3. 遍历 `ResultSet` 的数据列表

## 模式 3：角色和组织关系用 `IRoleMgr`，不要混到 `IUserMgr`

适用场景：

- 用户挂角色
- 查询组织中的用户
- 查询组织节点

关键点：

- 用户实体查询归 `IUserMgr`
- 组织、角色及关系操作归 `IRoleMgr`

## 模式 4：关联值用 `AssociationData`

适用场景：

- 关联用户
- 关联组织
- 关联业务对象

关键点：

- 先明确关联模型 ID
- 再构造 `AssociationData`
- 不要直接把关联字段塞成普通字符串

## 模式 5：表单字段读写与查询条件分开处理

适用场景：

- 表单赋值
- 表单读取
- 条件查询

关键点：

- 业务属性读写优先按业务字段名
- 查询条件构造时再按需要转换成字段编码
- 系统属性和框架常量字段除外

## 模式 6：流程发起先建运行上下文，再取开始节点表单

适用场景：

- 通过 `IPDFRuntimeMgr` 发起流程
- 流程 Excel 导入时逐行提交
- 从现有用户上下文补齐流程动作参数

关键点：

1. 先 `newRuntimeContext()`
2. 再设置操作人、动作名、用户模型、组织模型、事务 `dao`
3. 调用 `newStartForm(...)` 获取开始节点 `PDCForm`
4. 回填业务字段后再 `createAndSubmitPDCForm(...)`

## 模式 7：流程列表查询用 `PDFForm`，节点提交用 `PDCForm`

适用场景：

- 同时存在“流程列表页”和“节点提交”两类需求
- 需要解释流程查询返回对象与提交流程对象差异

关键点：

- `queryPDFFormPage(...)` 返回 `ResultSet<PDFForm>`
- `createAndSubmitPDCForm(...)`、`submitPDCForm(...)` 操作的是 `PDCForm`
- 不要把 `PDFForm` 当成提交流程的入参对象

## 模式 8：备份服务把业务处理包装进统一导入导出钩子

适用场景：

- 表单 Excel 导出
- 表单导入前预处理
- 需要观察者和批处理逻辑统一编排

关键点：

- `exportForms(...)`、`preImportForms(...)`、`importForms(...)` 负责包装批处理框架
- 真正的业务处理通过 `CConsumer<ArrayList<Form>>` 传入
- 自定义逻辑里仍要显式调用真实管理器，例如 `IFormMgr`
