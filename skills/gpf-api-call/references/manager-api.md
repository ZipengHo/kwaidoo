# GPF 基础 API 管理器操作面

## `IUserMgr`

### 适用对象

- `User`
- 用户模型
- 用户分页结果

### 常见操作

#### 创建用户

典型方法：

- `createUser(IDao dao, User user)`

关键点：

- 需要先构造 `User`
- 常见字段包括用户名、姓名、手机号、邮箱、密码、状态
- 写操作后通常需要提交事务

#### 查询用户

典型方法：

- `queryUser(IDao dao, String userModelId, String userUuid)`
- `queryUserByCode(IDao dao, String userModelId, String code)`
- `queryUserByName(IDao dao, String userModelId, String name)`

关键点：

- 通常依赖 `userModelId`
- 查询条件不明确时优先用编号或 UUID

#### 更新用户

典型方法：

- `updateUser(IDao dao, User user)`

关键点：

- 先查后改更稳妥
- 如果依赖编号更新，可先查 UUID

#### 分页查询用户

典型方法：

- `queryUserPage(IDao dao, String userModelId, Cnd cnd, int pageNo, int pageSize, boolean compoundField)`
- `queryUserPageBySql(...)`

关键点：

- `compoundField` 决定是否查询复合字段
- 结果通常是 `ResultSet<User>`

## `IRoleMgr`

### 适用对象

- `Role`
- `Org`
- 用户与角色、组织关系

### 常见操作

#### 创建组织

典型方法：

- `createOrg(IDao dao, Org org)`

关键点：

- 查询组织时通常仍需要 `orgModelId`

#### 查询组织

典型方法：

- `queryOrg(IDao dao, String orgModelId, String orgUuid)`
- `queryOrgByCode(IDao dao, String orgModelId, String code)`
- `queryOrgByPath(IDao dao, String orgModelId, String parentUuid, String path)`

#### 创建角色或身份

典型方法：

- `createRole(IDao dao, String orgModelId, String orgUuid, Role role)`：角色挂在组织下
- `createRole(IDao dao, Role role)`：独立身份

关键点：

- “挂在组织下的角色”和“独立身份”是两套入口

#### 查询和更新角色

典型方法：

- `queryRole(IDao dao, String roleUuid)`
- `queryRoleByCode(IDao dao, String roleCode)`
- `updateRole(IDao dao, Role role)`

#### 用户与角色关系

典型方法：

- `mountRoleToUser(IDao dao, String roleUuid, String userModelId, List<String> userUuids)`
- `unmountRoleFromUser(IDao dao, String roleUuid, String userModelId, List<String> userUuids)`

关键点：

- 要明确是“角色到用户”还是“角色到角色”
- 用户关系操作通常需要 `userModelId`

## `IFormMgr`

### 适用对象

- `FormModel`
- `Form`
- `FormField`
- `ResultSet<Form>`

### 常见操作

#### 字段编码

典型方法：

- `getFieldCode(String fieldName)`

关键点：

- 常用于中文字段名转字段编码
- 不需要 `IDao`

#### 模型操作

典型方法：

- `createFormModel(FormModel formModel)`
- `queryFormModel(String formModelId)`
- `updateFormModel(Progress prog, FormModel formModel)`

#### 表单创建与更新

典型方法：

- `createForm(IDao dao, Form form)`
- `createForm(Progress prog, IDao dao, Form form, FormOpObserver observer)`
- `updateForm(IDao dao, Form form)`
- `updateForm(Progress prog, IDao dao, Form form, FormOpObserver observer)`

#### 表单查询

典型方法：

- `queryForm(IDao dao, String formModelId, String uuid)`
- `queryFormByCode(IDao dao, String formModelId, String code)`
- `queryFormUuidByCode(IDao dao, String formModelId, String code)`

#### 表单分页与统计

典型方法：

- `countForm(IDao dao, String formModelId, Cnd cnd)`
- `queryFormPage(IDao dao, String formModelId, Cnd cnd, int pageNo, int pageSize, boolean queryRowCount, boolean compoundField, String... fields)`

关键点：

- 是否查询总数由 `queryRowCount` 控制
- 是否查询嵌套模型属性字段由 `compoundField` 控制
- 可指定只查部分字段

## `IPDFRuntimeMgr`

### 适用对象

- `PDCForm`
- `PDFForm`
- `PDFInstance`
- 流程运行上下文与流程实例状态

### 常见操作

#### 创建运行上下文并发起流程

典型方法：

- `newRuntimeContext()`
- `newStartForm(IDCRuntimeContext rtx, String pdfUuid)`
- `newStartForm(IDCRuntimeContext rtx, String pdfUuid, boolean executeAction)`
- `createAndSubmitPDCForm(String pdfUuid, IDCRuntimeContext rtx, PDCForm form)`
- `createAndSubmitPDCForm(String pdfUuid, String nodeKey, IDCRuntimeContext rtx, PDCForm form)`

关键点：

- 常见模式是先创建运行上下文，再构造开始节点表单，最后提交
- `PDCForm` 是节点表单，和普通 `Form` 的语义不同
- 需要先确认运行上下文里是否已补齐操作人、动作名以及相关模型信息

#### 查询流程实例

典型方法：

- `queryPDFInstance(String pdfInstUuid)`
- `queryPDFInstanceStatus(String pdfInstUuid)`
- `queryLatestPDCForm(IDCRuntimeContext rtx, String pdfUuid, String pdfInstUuid, String nodeKey)`

关键点：

- 流程实例查询通常围绕实例 UUID 展开
- 节点表单查询通常需要 `pdfUuid`、实例 UUID 和节点标识共同确定

#### 查询流程表单列表

典型方法：

- `queryPDFFormPage(String pdfUuid, Cnd cnd, SqlExpressionGroup privilegeExpr, int pageNo, int pageSize)`
- `queryPDFFormPage(String pdfUuid, PDFFormQueryOption queryOption, Cnd cnd, SqlExpressionGroup privilegeExpr, int pageNo, int pageSize, boolean queryRowCount, boolean compoundField)`
- `queryPDFFormPageBySql(...)`

关键点：

- 结果对象是 `ResultSet<PDFForm>`
- 列表查询关注的是流程总表单视角，不是当前节点 `PDCForm`
- 如果需要扩展字段或权限表达式，必须先以源码快照核实重载签名

#### 流程实例状态操作

典型方法：

- `startPDFInstance(String pdfInstUuid)`
- `stopPDFInstance(String pdfInstUuid)`
- `resetPDFInstance(String pdfInstUuid, List<String> nodeKeys)`
- `resetPDFInstanceAllNode(String pdfInstUuid)`

关键点：

- 这些操作会直接影响流程运行状态，输出时要明确风险
- 方法是否需要指定节点列表，不能按经验混写

## `IBackupService`

### 适用对象

- 表单导出导入
- 流程表单 Excel 导出导入
- 备份数据包、模型和流程

### 常见操作

#### 导出表单或数据包

典型方法：

- `exportFormToExcel(Progress prog, ExportImportIntf expImpIntf, String formModelId, Cnd cnd)`
- `exportFormToJson(Progress prog, ExportImportIntf expImpIntf, String formModelId, Cnd cnd)`
- `exportDataPackage(Progress prog, ExportImportIntf expImpIntf, ExportSetting setting)`
- `exportPDCFormToExcel(Progress prog, PDCFormDataExcelExpImp expImpIntf, String pdfUuid, List<FormField> formFields, String user, Cnd cnd)`

关键点：

- 导出结果统一是 `Pair<String, byte[]>`
- Excel、Json、数据包和流程表单导出是不同入口，不要混用
- 如果需要额外配置，先看 `ExportSetting` 相关方法是否已在源码快照中出现

#### 导入表单数据

典型方法：

- `preImportFormFormExcel(...)`
- `importFormFormExcel(...)`
- `preImportFormFormJson(...)`
- `importFormFormJson(...)`
- `submitPDCFormFormExcel(...)`

关键点：

- 预导入和正式导入是两阶段，不要省略阶段差异
- 流程表单 Excel 导入走 `submitPDCFormFormExcel`，不是普通表单导入入口

#### 包装导入导出钩子

典型方法：

- `preImportForms(Progress prog, IDao dao, ArrayList<Form> list, FormOpObserver observer, CConsumer<ArrayList<Form>> preImportHandler)`
- `importForms(Progress prog, IDao dao, ArrayList<Form> list, FormOpObserver observer, CConsumer<ArrayList<Form>> importHandler)`
- `exportForms(Progress prog, IDao dao, ArrayList<Form> list, FormOpObserver observer, CConsumer<ArrayList<Form>> exportHandler)`

关键点：

- 这三类方法适合把真实导入导出逻辑包进统一观察者和批处理流程
- 自定义处理逻辑是通过 `CConsumer<ArrayList<Form>>` 注入的

#### Json 工具和模型导入

典型方法：

- `getIJson()`
- `importFormModels(Progress prog, List<FormModel> formModels, List<PDF> pdfs, ImportModelOpObserver observer, boolean forceUpdate)`
- `addImportModelTaskToPool(...)`

关键点：

- `getIJson()` 采用 `try` 资源方式使用
- 模型与流程导入可能触发生效任务，不是普通表单数据导入
