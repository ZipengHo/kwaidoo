# GPF 基础 API 代码模式

## 用户创建模式

```java
public User createUser(IDao dao, String userModelId, User user) throws Exception {
    IUserMgr userMgr = IUserMgr.get();
    User created = userMgr.createUser(dao, user);
    dao.commit();
    return created;
}
```

关键点：

- 先构造 `User`
- 写操作后提交事务

## 用户分页模式

```java
public ResultSet<User> queryUserPage(IDao dao, String userModelId, Cnd cnd, int pageNo, int pageSize)
        throws Exception {
    return IUserMgr.get().queryUserPage(dao, userModelId, cnd, pageNo, pageSize, false);
}
```

## 表单查询模式

```java
public ResultSet<Form> queryOrders(IDao dao, String modelId, Cnd cnd, int pageNo, int pageSize)
        throws Exception {
    return IFormMgr.get().queryFormPage(dao, modelId, cnd, pageNo, pageSize, true, false);
}
```

## 组织与角色模式

```java
public Role createRole(IDao dao, String orgModelId, String orgUuid, Role role) throws Exception {
    Role created = IRoleMgr.get().createRole(dao, orgModelId, orgUuid, role);
    dao.commit();
    return created;
}
```

## 关联字段模式

```java
AssociationData creator = new AssociationData(userModelId, userCode);
form.setAttrValue("创建人", creator);
```

## 字段编码模式

- 业务属性读写默认优先字段名称
- `Cnd`、系统属性、动态字段场景再转字段编码
- 字段不明确时不要猜
