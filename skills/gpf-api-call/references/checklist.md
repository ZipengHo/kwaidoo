# GPF 基础 API 检查清单

- 是否选用了真实管理器接口
- 是否确认了管理器的真实方法入口
- 是否先用 `references/form-capability-matrix.md` 或源码快照核实了对象真实方法名
- 是否说明了实体对象如何构造
- 是否说明了模型 ID 前提
- 是否说明了业务属性默认使用字段名称
- 是否只在必要场景下使用字段编码
- 如果用了 `getTime`、`getPropKeyValueMap`、`getData`、`delete` 等对象方法，是否能在源码快照中定位到
- 输出中是否混入了能力面之外的 `Form` / `TableData` / `ResultSet` 方法名
- 是否说明了关联字段是否需要 `AssociationData`
- 是否说明了事务边界
- 是否补齐了查询条件和分页
