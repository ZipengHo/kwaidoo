# GPF 基础 API 常见实体与结果对象

## `User`

适用场景：

- 用户创建
- 用户更新
- 用户查询结果

常见设置项：

- 用户名
- 姓名
- 手机号
- 邮箱
- 密码
- 状态

注意：

- 密码通常不是普通字符串，应看项目约定是否使用专门数据对象

## `Role`

适用场景：

- 角色创建
- 独立身份创建
- 用户挂角色

注意：

- 角色是否挂在组织下，会影响创建方式

## `Org`

适用场景：

- 组织树
- 部门、机构等组织节点管理

常见查询方式：

- 按 UUID
- 按编号
- 按路径

## `FormModel`

适用场景：

- 创建或修改业务模型
- 判断字段定义和数据类型

## `Form`

适用场景：

- 表单实例新增
- 表单实例更新
- 表单查询结果

常见操作：

- `setAttrValue`
- `getString`
- `getAssociation`
- `getPropTable`
- `getPropKeyValueMap`
- `getTime`

补充规则：

- 业务属性默认优先 `setXXX` / `getXXX`
- `ByCode` 主要保留给系统属性、动态字段和查询条件相关场景
- 如果不确定 `Form` 真实提供哪些方法，先看 `form-capability-matrix.md`，不要按字段类型或英文语义猜方法名

常见系统属性：

- `Form.UUID`
- `Form.Code`
- `Form.Owner`
- `Form.ForeignClass`
- `Form.ForeignKey`

查阅系统属性规则：

- 如果你不确定当前字段是不是系统属性，先看 `system-fields.md`

查阅示例时机：

- 如果你要直接读写表单字段，继续看 `entity-examples.md` 中的 `Form` 条目和 `assets/examples/form_basic_usage.java`

## `PDCForm`

适用场景：

- 流程开始节点表单
- 当前节点提交流程数据
- 查询或回填节点级系统字段

常见操作：

- `setAttrValue(...)` 写业务字段
- `setPdfInstUuid(...)`
- `setParentFormUuid(...)`
- `setCreator(...)`
- `getNodeName()`
- `getNodeKey()`
- `getActions()`

补充规则：

- `PDCForm` 继承自 `Form`，但额外承载当前节点上下文与流程状态字段
- `PDCForm` 的 `CreateTime`、`Creator`、`NodeKey`、`Status` 等字段应优先以类常量和现有封装方法为准
- 如果不确定某个字段是走 `getString` 还是专门 getter，先看源码快照

查阅示例时机：

- 如果任务涉及流程发起、节点提交、流程 Excel 导入，继续看 `entity-examples.md` 中的 `PDCForm` 条目和 `assets/examples/pdf_runtime_create_and_submit.java`

## `PDFForm`

适用场景：

- 流程列表页
- 待办、已办、进度查询结果
- 按流程状态或节点信息做列表筛选

常见操作：

- `getPdfInstUuid()`
- `getCreator()`
- `getCreatorCnName()`
- `getAssignee()`
- `getNodeKey()`
- `getStepName()`
- `getStatus()`

关键点：

- `PDFForm` 是流程总表单视角的列表对象，不等价于当前节点 `PDCForm`
- 很多字段通过 `ByCode` 存取，不能想当然按普通业务字段写法处理
- 输出时要区分“列表展示字段”和“当前节点提交字段”

查阅示例时机：

- 如果任务涉及流程列表查询、待办列表、导出前筛选，继续看 `entity-examples.md` 中的 `PDFForm` 条目和 `assets/examples/pdf_runtime_create_and_submit.java`

## `TableData`

适用场景：

- 嵌套表
- 明细行
- 订单行、子表行、表格型业务数据

常见操作：

- 创建 `TableData`
- `add(Form)` 追加明细行
- `setRows(List<Form>)` 整体覆盖明细行
- 从表单中读取嵌套表
- `getData(int)`、`getData(String)` 获取指定明细行
- `delete(int)`、`delete(Form)`、`deleteByUuids(...)` 删除明细行
- `getRows()` 遍历全部明细行
- 读取 `TableData.MasterClass`、`TableData.MasterKey`、`TableData.MasterField`、`TableData.OrderSeq`

关键点：

- `TableData` 用于 `NestingModel`
- 它承载的是一组嵌套表单行，不等价于普通集合
- 它有自己的操作面，不要想当然写成 `isEmpty()`、`get(index)`、`remove(...)`
- 系统列要用 `TableData` 常量
- 明细业务字段默认优先按业务属性名读写，系统列才按常量或编码读取
- 如果不确定 `TableData` 的真实方法，先看 `form-capability-matrix.md`

常见系统属性：

- `TableData.MasterClass`
- `TableData.MasterKey`
- `TableData.MasterField`
- `TableData.OrderSeq`

查阅示例时机：

- 如果任务涉及订单明细、子表、嵌套模型，继续看 `entity-examples.md` 中的 `TableData` 条目和 `assets/examples/table_data_usage.java`

## `Password`

适用场景：

- 用户密码
- 表单中的密码字段

常见操作：

- `new Password().setValue(...)`
- 从表单或用户对象读取密码对象

关键点：

- 它不是普通字符串
- 读写密码时要先确认当前对象 API 返回的是 `Password` 还是普通文本

查阅示例时机：

- 如果任务涉及用户创建、密码重置、密码字段读写，继续看 `entity-examples.md` 中的 `Password` 条目和 `assets/examples/password_usage.java`

## `AssociationData`

适用场景：

- 单选关联
- 多选关联
- 组织、用户、角色等关联值

关键点：

- 通常由“关联模型 ID + 关联对象标识”组成
- 不能把关联字段当作普通字符串字段处理

## `ResultSet<T>`

适用场景：

- 分页查询返回
- 用户列表、角色列表、表单列表

关键点：

- 关注数据列表和总数
- 只查当前页时也要交代页码和页大小

查阅示例时机：

- 如果任务涉及分页查询或列表返回，继续看 `entity-examples.md` 中的 `ResultSet` 条目和 `assets/examples/result_set_usage.java`

## `AttachData`

适用场景：

- 小文件附件
- 直接把文件内容随表单一起保存
- 附件字段的新增、替换、读取

常见操作：

- `new AttachData(fileName, bytes)`
- `form.setAttrValue(...)`
- `form.getAttachments(...)`

关键点：

- 适合直接以内存字节数组处理的文件
- 文件内容不能为空
- 多附件通常以 `List<AttachData>` 形式写入

查阅示例时机：

- 如果任务涉及小文件上传、附件字段读写，继续看 `entity-examples.md` 中的 `AttachData` 条目和 `assets/examples/attach_data_usage.java`

## `WebAttachData`

适用场景：

- 大文件
- 云端或外部存储附件
- 先上传再回填到表单字段

常见操作：

- `IFormMgr.get().uploadWebAttach(fileName, bytes)`
- `IFormMgr.get().uploadWebAttach(fileName, inputStream)`
- `form.setAttrValue(...)`
- `form.getWebAttachs(...)`

关键点：

- 它不是普通字节数组附件
- 一般先上传，再把返回对象写回字段
- 更适合大文件或不希望直接把完整内容放进表单对象的场景

查阅示例时机：

- 如果任务涉及大文件、流式上传、云存储附件，继续看 `entity-examples.md` 中的 `WebAttachData` 条目和 `assets/examples/web_attach_data_usage.java`

## `Cnd`

适用场景：

- 条件查询
- 列表筛选
- 分页前置过滤

关键点：

- 输出时要写清条件基于哪个字段
- 在 `Cnd` 中，业务属性通常需要先转换成字段编码；系统属性通常可直接使用固定字段名
- `Cnd` 中哪些字段属于可直接使用的系统属性，以 `system-fields.md` 为准，这里不重复维护清单

查阅示例时机：

- 如果任务涉及动态筛选、多条件拼接、排序，继续看 `entity-examples.md` 中的 `Cnd` 条目和 `assets/examples/cnd_basic_usage.java`
- 如果任务涉及系统属性和业务属性的边界判断，先看 `system-fields.md`
