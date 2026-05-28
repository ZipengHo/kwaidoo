# GPF 基础 API 实体示例与查阅时机

本文件只回答两件事：

- 什么时候应该查看某个实体的示例
- 该实体最小可改造代码长什么样

## `Form`

### 何时查阅

- 需要创建或更新表单实例
- 需要按字段名称或字段编码读写属性
- 需要和 `IFormMgr.createForm`、`IFormMgr.updateForm` 配合

### 重点示例

读取：

- `assets/examples/form_basic_usage.java`
- `assets/examples/form_update_with_association.java`
- `assets/examples/system_field_usage.java`

### 关键提示

- 业务属性默认优先 `setAttrValue` / `getXXX`
- 只有系统属性、动态字段或明确的编码驱动场景才优先用 `ByCode`
- 如果你拿不准 `Form` 真实有哪些 `getXXX` 方法，先看 `form-capability-matrix.md`

## `TableData`

### 何时查阅

- 字段类型是嵌套表
- 需要向明细行写数据
- 需要批量替换、删除或遍历明细行
- 需要读取 `TableData.MasterClass`、`TableData.MasterKey` 等系统列

### 重点示例

读取：

- `assets/examples/table_data_usage.java`
- `assets/examples/system_field_usage.java`

### 关键提示

- `TableData` 不是普通 `List<Form>`
- 嵌套表行通常仍以 `Form` 形式承载
- 常用真实方法是 `add`、`setRows`、`getData`、`delete`、`getRows`
- 判空方法是 `isEmtpy()`，不是 `isEmpty()`
- 嵌套表相关系统字段要用 `TableData` 常量，不要误用 `Form` 常量
- 明细业务字段默认仍优先用字段名称读写
- 如果你拿不准 `TableData` 真实有哪些方法，先看 `form-capability-matrix.md`

## `Password`

### 何时查阅

- 创建用户
- 设置密码字段
- 从表单或用户对象读取密码对象

### 重点示例

读取：

- `assets/examples/password_usage.java`
- `assets/examples/user_create_example.java`

### 关键提示

- 密码字段使用 `Password` 对象，不建议把明文字符串直接当普通文本字段处理
- 读取时既要知道如何取明文值，也要知道对象本身不是普通字符串

## `AssociationData`

### 何时查阅

- 单选关联、多选关联
- 给表单字段赋用户、组织、业务对象等关联值

### 重点示例

读取：

- `assets/examples/association_data_usage.java`
- `assets/examples/form_update_with_association.java`

### 关键提示

- 关联字段通常不是普通字符串字段
- 示例里要明确第二个参数到底是业务编号还是 UUID，不能模糊

## `AttachData`

### 何时查阅

- 需要给表单写入小文件附件
- 需要读取附件列表并遍历文件名、内容
- 需要替换或追加附件内容

### 重点示例

读取：

- `assets/examples/attach_data_usage.java`

### 关键提示

- 适合小文件
- 构造时需要文件名和字节数组
- 常见字段值类型是 `List<AttachData>`

## `WebAttachData`

### 何时查阅

- 需要上传大文件
- 需要通过 `IFormMgr` 先上传附件再写入表单
- 需要处理输入流上传

### 重点示例

读取：

- `assets/examples/web_attach_data_usage.java`

### 关键提示

- 它通常不是手工 `new` 出来的主流程对象
- 常见模式是先调用上传接口，再把返回值写回表单字段

## `ResultSet`

### 何时查阅

- 分页查询
- 列表页接口
- 导出或迁移前先统计再分批处理

### 重点示例

读取：

- `assets/examples/result_set_usage.java`
- `assets/examples/query_form_page.java`
- `assets/examples/form_query_example.java`

### 关键提示

- `ResultSet` 不是普通 `List`
- 使用时要同时说明数据列表和总记录数

## `PDCForm`

### 何时查阅

- 需要发起流程
- 需要提交开始节点或指定节点表单
- 需要从流程运行接口拿到节点表单再回填业务字段

### 重点示例

读取：

- `assets/examples/pdf_runtime_create_and_submit.java`

### 关键提示

- 先构造运行上下文，再拿 `newStartForm(...)`
- 业务字段仍按 `Form` 真实能力写值
- 节点级字段优先使用 `PDCForm` 常量或封装 getter/setter

## `PDFForm`

### 何时查阅

- 需要查询流程列表
- 需要读取流程发起人、待办人、节点名、状态等列表字段
- 需要导出流程表单前先分页筛选

### 重点示例

读取：

- `assets/examples/pdf_runtime_create_and_submit.java`

### 关键提示

- `PDFForm` 通常来自 `queryPDFFormPage(...)`
- 它偏列表展示和检索，不是提交入口
- 如果你拿不准某个字段是否真实存在，先看源码快照

## `Cnd`

### 何时查阅

- 构建查询条件
- 做多条件过滤、排序、分页前筛选
- 需要判断业务属性和系统属性的写法差异

### 重点示例

读取：

- `assets/examples/cnd_basic_usage.java`
- `assets/examples/cnd_advanced_usage.java`
- `assets/examples/query_form_page.java`

### 关键提示

- 系统属性通常可直接使用字段名
- 业务属性进入 `Cnd` 前通常先转字段编码
- `and` / `or` 组合时要注意返回的新条件对象
- 如果不确定哪些字段属于系统属性，先看 `system-fields.md`
