package cell.gpf.adur.role;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cmn.dto.Progress;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Org;
import gpf.adur.role.Role;
import gpf.adur.user.User;
import gpf.dc.intf.FormOpObserver;
import web.dto.Pair;
/**
 * 角色和组织模型操作
 * 管理组织模型(FormModel)、组织数据(Org)、角色/身份数据(Role)的CRUD操作
 * 角色和身份的区分在于Role的owner是否有指定组织，不指定组织的Role即为身份
 * 
 */
public interface IRoleMgr extends ServiceCellIntf{

	static IRoleMgr get() {
		return Cells.get(IRoleMgr.class);
	}
	/**
	 * 是否组织模型
	 * @param formModelId	模型ID
	 * @return
	 * @throws Exception
	 */
	public boolean isOrgModel(String formModelId)throws Exception;
	/**
	 * 获取根组织模型ID
	 * @return
	 * @throws Exception
	 */
	public String getRootOrgModelId()throws Exception;
	/**
	 * 查询模型的继承路径
	 * @param formModelId	模型ID
	 * @return	模型的祖先模型ID列表，从根路径开始，当前模型ID为列表末尾值
	 * @throws Exception
	 */
	public List<String> queryInheritPaths(String formModelId) throws Exception ;
	/**
	 * 获取根组织模型
	 * @return
	 * @throws Exception
	 */
	public FormModel getOrgRootModel()throws Exception;
	/**
	 * 创建组织模型
	 * @param model	组织模型
	 * @return
	 * @throws Exception
	 */
	public FormModel createOrgModel(FormModel model) throws Exception;
	/**
	 * 更新组织模型
	 * @param prog	进度通知对象
	 * @param model	组织模型
	 * @return
	 * @throws Exception
	 */
	public FormModel updateOrgModel(Progress prog,FormModel model) throws Exception;
	
	/**
	 * 重命名组织模型
	 * @param prog	进度通知对象
	 * @param renameModels	key:模型ID, value:(新模型ID,模型中文名)
	 * @throws Exception
	 */
	public void renameOrgModel(Progress prog,Map<String,Pair<String, String>> renameModels)throws Exception;
	
	/**
	 * 重命名组织模型ID	
	 * @param prog	进度通知对象
	 * @param renameModels	key:模型ID, value:新模型ID
	 * @throws Exception
	 */
	public void renameOrgModelId(Progress prog,Map<String,String> renameModels)throws Exception;
	/**
	 * 查询组织模型
	 * @param orgModelID	组织模型ID
	 * @return
	 * @throws Exception
	 */
	public FormModel queryOrgModel(String orgModelID) throws Exception;
	/**
	 * 根据模型uuid查询组织模型
	 * @param uuid	模型uuid
	 * @return
	 * @throws Exception
	 */
	public FormModel queryOrgModelByUuid(String uuid) throws Exception;
	/**
	 * 查询子组织模型列表
	 * @param orgModelID	组织模型ID
	 * @return
	 * @throws Exception
	 */
	public List<FormModel> queryChildOrgModels(String orgModelID) throws Exception;
	/**
	 * 查询所有组织模型列表
	 * @return
	 * @throws Exception
	 */
	public List<FormModel> queryAllOrgModels()throws Exception;
	/**
	 * 根据包路径查询组织模型列表
	 * @param packagePath	模型包路径
	 * @return	模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryOrgModelByPackage(String packagePath) throws Exception;
	/**
	 * 分页查询组织模型结果集
	 * @param parentIds	父模型ID
	 * @param packagePath	模型包路径，填null时匹配所有
	 * @param keyword	搜索关键字，匹配模型名称或模型标签，填null时匹配所有
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return	表单模型结果集
	 * @throws Exception
	 */
	public ResultSet<FormModel> queryOrgModelPage(List<String> parentIds, String packagePath, String keyword,int pageNo,int pageSize)throws Exception;
	/**
	 * 删除组织模型
	 * @param orgModelID	组织模型ID
	 * @throws Exception
	 */
	public void deleteOrgModel(String orgModelID) throws Exception;
	
	public void deleteOrgModels(Set<String> orgModelIDs) throws Exception;
	
	/**
	 * 构建组织组织数据副本
	 * @param org	组织
	 * @return
	 * @throws Exception
	 */
	public Org copyOrg(Org org)throws Exception;
	/**
	 * 创建组织
	 * @param dao
	 * @param org	组织
	 * @return
	 * @throws Exception
	 */
	public Org createOrg(IDao dao,Org org) throws Exception;
	/**
	 * 创建组织，携带组织操作观察者，在组织操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param org	组织
	 * @param observer	组织操作观察者
	 * @return
	 * @throws Exception
	 */
	public Org createOrg(Progress prog,IDao dao,Org org,FormOpObserver observer) throws Exception;
	/**
	 * 更新组织
	 * @param dao
	 * @param org	组织
	 * @return
	 * @throws Exception
	 */
	public Org updateOrg(IDao dao,Org org) throws Exception;
	/**
	 * 更新组织，携带组织操作观察者，在组织操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param org	组织
	 * @param observer	组织操作观察者
	 * @return
	 * @throws Exception
	 */
	public Org updateOrg(Progress prog,IDao dao,Org org,FormOpObserver observer) throws Exception;
	/**
	 * 查询组织
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织uuid
	 * @return
	 * @throws Exception
	 */
	public Org queryOrg(IDao dao,String orgModelID, String orgUuid) throws Exception;
	/**
	 * 根据组织编号查询组织
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param code	编号
	 * @return
	 * @throws Exception
	 */
	public Org queryOrgByCode(IDao dao,String orgModelID, String code) throws Exception;
	/**
	 * 根据组织编号查询组织uuid
	 * @param dao	
	 * @param orgModelID	组织模型ID
	 * @param code	编号
	 * @return
	 * @throws Exception
	 */
	public String queryOrgUuidByCode(IDao dao,String orgModelID, String code) throws Exception;
	/**
	 * 根据标签获取子组织
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param parentOrgUuid	父组织uuid，传null时查找根路径
	 * @param label	子组织标签
	 * @return
	 * @throws Exception
	 */
	public Org queryChildOrgByLabel(IDao dao,String orgModelID, String parentOrgUuid,String label) throws Exception;
	/**
	 * 根据组织路径查找组织
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param parentOrgUuid	父组织uuid，开始查找的根组织，传null时从根路径开始
	 * @param path	查找路径
	 * @return
	 * @throws Exception
	 */
	public Org queryOrgByPath(IDao dao,String orgModelID, String parentOrgUuid,String path) throws Exception;
	/**
	 * 查询角色所在组织
	 * @param dao
	 * @param orgModelId	组织模型ID
	 * @param roleUuid	角色uuid
	 * @return
	 * @throws Exception
	 */
	public Org queryOrgOfRole(IDao dao,String orgModelId,String roleUuid)throws Exception;
	/**
	 * 查询组织的完整路径
	 * @param dao
	 * @param orgList
	 * @return	key：组织完整路径，value：组织
	 * @throws Exception
	 */
	public Map<String,Org> queryPathOfOrg(IDao dao,List<Org> orgList)throws Exception;
	/**
	 * 查询组织分页结果
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param cnd	查询条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Org> queryOrgPage(IDao dao,String orgModelID, Cnd cnd, int pageNo,int pageSize)
			throws Exception;
	/**
	 * 根据自定义SQL查询组织分页结果
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param querySql	自定义查询SQL
	 * @param extFields	附带查询列，需要在自定义查询SQL中有定义该列的查询语句，查询记录数时需要携带totalCount，其他附带的查询列值都将设置在Form的extFields上
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Org> queryOrgPageBySql(IDao dao, String orgModelID, String querySql, Set<String> extFields,Cnd cnd, int pageNo, int pageSize)
			throws Exception;
	
	/**
	 * 查询子组织列表
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param parentOrgUuid	父组织uuid
	 * @param cnd	查询条件
	 * @return
	 * @throws Exception
	 */
	public List<Org> queryChildOrg(IDao dao,String orgModelID,String parentOrgUuid,Cnd cnd)throws Exception;
	/**
	 * 查询用户所在的组织分页结果
	 * @param dao
	 * @param orgModelId	组织模型ID
	 * @param cnd	查询条件
	 * @param userModelId	用户模型ID
	 * @param userUuid	用户uuid
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Org> queryOrgPageOfUser(IDao dao, String orgModelId, Cnd cnd,String userModelId,String userUuid,int pageNo,int pageSize)throws Exception;
	/**
	 * 查询组织下的用户分页结果
	 * @param dao
	 * @param orgModelId	组织模型ID
	 * @param orgUuid	组织uuid
	 * @param userModelId	用户模型ID
	 * @param cnd	查询条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<User> queryUserPageOfOrg(IDao dao, String orgModelId, String orgUuid, String userModelId,Cnd cnd, int pageNo,
			int pageSize)throws Exception;
	/**
	 * 删除组织
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织模型uuid
	 * @throws Exception
	 */
	public void deleteOrg(IDao dao,String orgModelID, String orgUuid) throws Exception;
	/**
	 * 删除组织,携带组织操作观察者，在组织操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param orgModelID 组织模型ID
	 * @param orgUuid	组织模型uuid
	 * @param observer	组织操作观察者
	 * @throws Exception
	 */
	public void deleteOrg(Progress prog,IDao dao,String orgModelID, String orgUuid,FormOpObserver observer) throws Exception;
	
	/**
	 * 创建角色
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织uuid
	 * @param role	角色
	 * @return
	 * @throws Exception
	 */
	Role createRole(IDao dao, String orgModelID,String orgUuid, Role role) throws Exception;
	/**
	 * 创建角色,携带角色操作观察者，在角色操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织uuid
	 * @param role	角色
	 * @param observer	角色操作观察者
	 * @return
	 * @throws Exception
	 */
	Role createRole(Progress prog,IDao dao, String orgModelID,String orgUuid, Role role,FormOpObserver observer) throws Exception;
	/**
	 * 创建身份
	 * @param dao
	 * @param role	身份，不设置owner时为身份
	 * @return
	 * @throws Exception
	 */
	Role createRole(IDao dao,Role role)throws Exception;
	/**
	 * 创建身份,携带角色操作观察者，在角色操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param role	身份，不设置owner时为身份
	 * @param observer	角色操作观察者
	 * @return
	 * @throws Exception
	 */
	Role createRole(Progress prog,IDao dao,Role role,FormOpObserver observer)throws Exception;

	/**
	 * 通过uuid查询角色
	 *
	 * @param dao
	 * @param uuid 角色 uuid
	 * @return
	 * @throws Exception
	 */
	Role queryRole(IDao dao, String roleUuid) throws Exception;
	/**
	 * 通过编号查询角色
	 * @param dao
	 * @param roleCode	角色编号
	 * @return
	 * @throws Exception
	 */
	Role queryRoleByCode(IDao dao,String roleCode)throws Exception;

	/**
	 * 查询挂载角色列表
	 *
	 * @param dao
	 * @param uuid 角色 uuid
	 * @return
	 * @throws Exception
	 */
	List<Role> queryMountedRoleList(IDao dao, String roleUuid) throws Exception;

	/**
	 * 查询角色下挂载的用户列表
	 *
	 * @param dao
	 * @param uuid 角色 uuid
	 * @param userModelID 用户模型ID
	 * @return
	 * @throws Exception
	 */
	List<User> queryMountedUserList(IDao dao, String roleUuid,String userModelID) throws Exception;

	/**
	 * 查询角色下的用户分页结果
	 * @param dao
	 * @param roleUuid            角色 uuid
	 * @param onlyMountedRole 仅计算挂载角色,为true时不计算当前角色挂载的用户
	 * @param userModelID	用户模型ID
	 * @param cnd	条件表达式
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<User> queryUserPageOfRole(IDao dao, String roleUuid, boolean onlyMountedRole,String userModelID,Cnd cnd, int pageNo,int pageSize) throws Exception;
	/**
	 * 查询用户关联的角色分页结果
	 * @param dao
	 * @param userModelId	用户模型
	 * @param userUuid	用户uuid	
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @param onlyMounted	只查询直接关联的角色
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Role> queryRolePageOfUser(IDao dao, String userModelId, String userUuid,Cnd cnd,int pageNo,int pageSize,boolean onlyMounted)throws Exception;


	/**
	 * 更新角色
	 * @param dao
	 * @param role	角色
	 * @throws Exception
	 */
	void updateRole(IDao dao, Role role) throws Exception;
	/**
	 * 更新角色,携带角色操作观察者，在角色操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param role	角色
	 * @param observer	角色操作观察者
	 * @throws Exception
	 */
	void updateRole(Progress prog,IDao dao, Role role,FormOpObserver observer) throws Exception;

	/**
	 * 删除角色
	 * @param dao
	 * @param uuids	角色uuid列表
	 * @throws Exception
	 */
	void deleteRole(IDao dao, List<String> uuids) throws Exception;
	/**
	 * 删除角色,携带角色操作观察者，在角色操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao	
	 * @param uuids	角色uuid列表
	 * @param observer	角色操作观察者
	 * @throws Exception
	 */
	void deleteRole(Progress prog,IDao dao, List<String> uuids,FormOpObserver observer) throws Exception;
	
	/**
	 * 列出指定组织架构下的角色列表
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织uuid
	 * @return
	 * @throws Exception
	 */
	List<Role> queryRoleListOfOrg(IDao dao, String orgModelID,String orgUuid) throws Exception;
	/**
	 * 查询角色分页
	 * @param dao
	 * @param cnd	查询条件,设置cnd.and(new SqlExpressionGroup().andIsNull("owner"))时查询身份结果分页
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	ResultSet<Role> queryRolePage(IDao dao,Cnd cnd,int pageNo,int pageSize)throws Exception;
	/**
	 * 根据自定义SQL查询角色分页结果
	 * @param dao
	 * @param querySql	自定义查询SQL
	 * @param extFields	附带查询列，需要在自定义查询SQL中有定义该列的查询语句，查询记录数时需要携带totalCount，其他附带的查询列值都将设置在Role的extFields上
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<Role> queryRolePageBySql(IDao dao, String querySql, Set<String> extFields,Cnd cnd, int pageNo, int pageSize)
			throws Exception;

	/**
	 * 列出指定组织下的角色分页
	 *
	 * @param dao
	 * @param orgModelID	组织模型ID
	 * @param orgUuid	组织uuid
	 * @param cnd	查询条
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	ResultSet<Role> queryRolePageOfOrg(IDao dao, String orgModelID,String orgUuid,Cnd cnd, int pageNo,int pageSize) throws Exception;
	
	/**
	 * 将角色分配给用户
	 * @param dao
	 * @param roleUuid	角色uuid
	 * @param userModelId	用户模型ID
	 * @param userUuids	用户uuid列表
	 * @throws Exception
	 */
	public void mountRoleToUser(IDao dao, String roleUuid, String userModelId, List<String> userUuids)throws Exception;
	/**
	 * 解绑用户和角色关系
	 * @param dao	
	 * @param roleUuid	角色uuid
	 * @param userModelId	用户模型ID
	 * @param userUuids	用户uuid列表
	 * @throws Exception
	 */
	public void unmountRoleFromUser(IDao dao, String roleUuid, String userModelId, List<String> userUuids)
			throws Exception ;
	/**
	 * 将子角色分配给角色
	 * @param dao
	 * @param childRoleUuids	子角色uuid
	 * @param roleUuid	角色uuid
	 * @throws Exception
	 */
	public void mountRoleToRole(IDao dao, List<String> childRoleUuids, String roleUuid) throws Exception ;
	/**
	 * 解绑角色和角色关系
	 * @param dao
	 * @param childRoleUuids	子角色uuid
	 * @param roleUuid	角色uuid
	 * @throws Exception
	 */
	public void unmountRoleFromRole(IDao dao, List<String> childRoleUuids, String roleUuid) throws Exception ;
	/**
	 * 构建组织下的用户查询sQL
	 * @param orgModelID	组织模型ID
	 * @param orgCondition	组织查询条件
	 * @param userModelID	用户模型ID
	 * @return
	 * @throws Exception
	 */
	public String buildQueryUserOfOrgSql(String orgModelID,String orgCondition,String userModelID)throws Exception;
	/**
	 * 构建角色下的用户查询SQL
	 * @param roleCondition	角色查询条件
	 * @param userModelID	用户模型ID
	 * @return
	 * @throws Exception
	 */
	public String buildQueryUserOfRoleSql(String roleCondition,String userModelID)throws Exception;
	/**
	 * 构建用户关联的角色查询SQL
	 * @param userModelID	用户模型ID
	 * @param userCondition	用户查询条件
	 * @return
	 * @throws Exception
	 */
	public String buildQueryRoleOfUserSql(String userModelID,String userCondition)throws Exception;
	/**
	 * 构建用户关联的组织查询SQL
	 * @param userModelID	用户模型ID
	 * @param userCondition	用户查询条件
	 * @param orgModelID	组织模型ID
	 * @return
	 * @throws Exception
	 */
	public String buildQueryOrgOfUserSql(String userModelID,String userCondition,String orgModelID)throws Exception;
	/**
	 * 构建角色下的组织查询SQL
	 * @param roleCondition	角色查询条件
	 * @param orgModelID	组织模型ID
	 * @return
	 * @throws Exception
	 */
	public String buildQueryOrgOfRoleSql(String roleCondition,String orgModelID)throws Exception;
	/**
	 * 构建组织下的角色查询SQL
	 * @param orgModelID	组织模型ID
	 * @param orgCondition	组织查询条件
	 * @param extOrgFields	角色查询SQL附带的组织查询属性
	 * @return
	 * @throws Exception
	 */
	public String buildQueryRoleOfOrgSql(String orgModelID, String orgCondition,Map<String,String> extOrgFields)throws Exception;
	/**
	 * 构建查询子组织的SQL
	 * @param orgModelID	组织模型ID
	 * @param orgCondition	组织查询条件
	 * @param allChildren	是否递归查询所有子组织
	 * @return
	 * @throws Exception
	 */
	public String buildQueryChildOrgSql(String orgModelID,String orgCondition,boolean allChildren)throws Exception;
	/**
	 * 构建查询父组织的SQL
	 * @param orgModelID	组织模型ID
	 * @param orgCondition	组织查询条件
	 * @param allAncestor	是否递归查询所有父组织
	 * @return	
	 * @throws Exception
	 */
	public String buildQueryAncestorOrgSql(String orgModelID,String orgCondition,boolean allAncestor)throws Exception;
}
