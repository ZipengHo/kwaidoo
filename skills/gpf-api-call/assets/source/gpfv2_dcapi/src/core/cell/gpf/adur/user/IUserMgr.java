package cell.gpf.adur.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cmn.dto.Progress;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.user.User;
import gpf.dc.intf.FormOpObserver;
import web.dto.Pair;

/**
 * 用户模型操作接口 管理用户模型（FormModel）、用户数据（User）的CRUD操作。
 */
public interface IUserMgr extends ServiceCellIntf {

	static IUserMgr get() {
		return Cells.get(IUserMgr.class);
	}

	/**
	 * 判断是否为用户模型
	 * 
	 * @param formModelId 模型ID
	 * @return 是否为用户模型
	 * @throws Exception
	 */
	public boolean isUserModel(String formModelId) throws Exception;

	/**
	 * 查询模型的继承路径
	 * 
	 * @param formModelId 模型ID
	 * @return 模型的祖先模型ID列表，从根路径开始，当前模型ID为列表末尾值
	 * @throws Exception
	 */
	public List<String> queryInheritPaths(String formModelId) throws Exception;

	/**
	 * 获取根基础用户模型ID
	 * 
	 * @return 根基础用户模型ID
	 * @throws Exception
	 */
	public String getRootBasicUserModelId() throws Exception;

	/**
	 * 获取基础用户模型
	 * 
	 * @return 基础用户模型
	 * @throws Exception
	 */
	public FormModel getBasicUserModel() throws Exception;

	/**
	 * 创建用户模型
	 * 
	 * @param userModel 用户模型
	 * @return 创建后的用户模型
	 * @throws Exception
	 */
	public FormModel createUserModel(FormModel userModel) throws Exception;

	/**
	 * 更新用户模型
	 * 
	 * @param prog      进度通知对象
	 * @param userModel 用户模型
	 * @return 更新后的用户模型
	 * @throws Exception
	 */
	public FormModel updateUserModel(Progress prog, FormModel userModel) throws Exception;

	/**
	 * 重命名用户模型
	 * 
	 * @param prog         进度通知对象
	 * @param renameModels key:模型ID, value:(新模型ID,模型中文名)
	 * @throws Exception
	 */
	public void renameUserModel(Progress prog, Map<String, Pair<String, String>> renameModels) throws Exception;

	/**
	 * 重命名用户模型ID
	 * 
	 * @param prog         进度通知对象
	 * @param renameModels key:模型ID, value:新模型ID
	 * @throws Exception
	 */
	public void renameUserModelId(Progress prog, Map<String, String> renameModels) throws Exception;

	/**
	 * 删除用户模型
	 * 
	 * @param userModelID 用户模型ID
	 * @throws Exception
	 */
	public void deleteUserModel(String userModelID) throws Exception;
	
	public void deleteUserModels(Set<String> userModelIDs) throws Exception;

	/**
	 * 查询用户模型
	 * 
	 * @param userModelID 用户模型ID
	 * @return 用户模型
	 * @throws Exception
	 */
	public FormModel queryUserModel(String userModelID) throws Exception;

	/**
	 * 根据模型UUID查询用户模型
	 * 
	 * @param uuid 模型UUID
	 * @return 用户模型
	 * @throws Exception
	 */
	public FormModel queryUserModelByUuid(String uuid) throws Exception;

	/**
	 * 查询子用户模型列表
	 * 
	 * @param userModelID 用户模型ID
	 * @return 子用户模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryChildUserModels(String userModelID) throws Exception;

	/**
	 * 查询所有用户模型列表
	 * 
	 * @return 所有用户模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryAllUserModels() throws Exception;

	/**
	 * 根据包路径查询用户模型列表
	 * 
	 * @param packagePath 模型包路径
	 * @return 用户模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryUserModelByPackage(String packagePath) throws Exception;

	/**
	 * 分页查询用户模型结果集
	 * 
	 * @param parentIds   父模型ID
	 * @param packagePath 模型包路径，填null时匹配所有
	 * @param keyword     搜索关键字，匹配模型名称或模型标签，填null时匹配所有
	 * @param pageNo      查询页码，页码从1开始
	 * @param pageSize    分页大小
	 * @return 用户模型结果集
	 * @throws Exception
	 */
	public ResultSet<FormModel> queryUserModelPage(List<String> parentIds, String packagePath, String keyword,
			int pageNo, int pageSize) throws Exception;

	/**
	 * 构建用户数据副本
	 * 
	 * @param user 用户
	 * @return 用户数据副本
	 * @throws Exception
	 */
	public User copyUser(User user) throws Exception;

	/**
	 * 创建用户
	 * 
	 * @param dao  数据访问对象
	 * @param user 用户
	 * @return 创建后的用户
	 * @throws Exception
	 */
	public User createUser(IDao dao, User user) throws Exception;

	/**
	 * 创建用户，携带用户操作观察者，在用户操作时执行相应回调处理
	 * 
	 * @param prog     进度通知对象
	 * @param dao      数据访问对象
	 * @param user     用户
	 * @param observer 用户操作观察者
	 * @return 创建后的用户
	 * @throws Exception
	 */
	public User createUser(Progress prog, IDao dao, User user, FormOpObserver observer) throws Exception;

	/**
	 * 更新用户
	 * 
	 * @param dao  数据访问对象
	 * @param user 用户
	 * @return 更新后的用户
	 * @throws Exception
	 */
	public User updateUser(IDao dao, User user) throws Exception;

	/**
	 * 更新用户，指定更新字段和忽略字段
	 * 
	 * @param dao                数据访问对象
	 * @param user               用户
	 * @param updateFields       需要更新的字段数组
	 * @param ignoreUpdateFields 需要忽略更新的字段数组
	 * @return 更新后的用户
	 * @throws Exception
	 */
	public User updateUser(IDao dao, User user, String[] updateFields, String[] ignoreUpdateFields) throws Exception;

	/**
	 * 更新用户，携带用户操作观察者，在用户操作时执行相应回调处理
	 * 
	 * @param prog               进度通知对象
	 * @param dao                数据访问对象
	 * @param user               用户
	 * @param updateFields       需要更新的字段数组
	 * @param ignoreUpdateFields 需要忽略更新的字段数组
	 * @param observer           用户操作观察者
	 * @return 更新后的用户
	 * @throws Exception
	 */
	public User updateUser(Progress prog, IDao dao, User user, String[] updateFields, String[] ignoreUpdateFields,
			FormOpObserver observer) throws Exception;

	/**
	 * 查询用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param userID      用户ID
	 * @param fields      需要返回的字段数组
	 * @return 用户
	 * @throws Exception
	 */
	public User queryUser(IDao dao, String userModelID, String userID, String... fields) throws Exception;

	/**
	 * 根据用户编号查询用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param code        用户编号
	 * @param fields      需要返回的字段数组
	 * @return 用户
	 * @throws Exception
	 */
	public User queryUserByCode(IDao dao, String userModelID, String code, String... fields) throws Exception;

	/**
	 * 根据用户编号查询用户UUID
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param code        用户编号
	 * @return 用户UUID
	 * @throws Exception
	 */
	public String queryUserUuidByCode(IDao dao, String userModelID, String code) throws Exception;

	/**
	 * 根据用户名查询用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param name        用户名
	 * @param fields      需要返回的字段数组
	 * @return 用户
	 * @throws Exception
	 */
	public User queryUserByName(IDao dao, String userModelID, String name, String... fields) throws Exception;

	/**
	 * 根据条件查询用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param cnd         查询条件
	 * @param fields      需要返回的字段数组（可选）
	 * @return 查询到的用户对象
	 * @throws Exception
	 */
	public User queryUserByCnd(IDao dao, String userModelID, Cnd cnd, String... fields) throws Exception;

	/**
	 * 分页查询用户
	 * 
	 * @param dao           数据访问对象
	 * @param userModelID   用户模型ID
	 * @param condition     查询条件
	 * @param pageNo        查询页码，从1开始
	 * @param pageSize      每页大小
	 * @param compoundField 是否复合字段查询
	 * @param fields        需要返回的字段数组（可选）
	 * @return 用户分页结果集
	 * @throws Exception
	 */
	public ResultSet<User> queryUserPage(IDao dao, String userModelID, Cnd condition, int pageNo, int pageSize,
			boolean compoundField, String... fields) throws Exception;

	/**
	 * 使用自定义SQL分页查询用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param querySql    自定义查询SQL
	 * @param extFields   需要额外返回的字段集合
	 * @param condition   查询条件
	 * @param pageNo      查询页码，从1开始
	 * @param pageSize    每页大小
	 * @return 用户分页结果集
	 * @throws Exception
	 */
	public ResultSet<User> queryUserPageBySql(IDao dao, String userModelID, String querySql, Set<String> extFields,
			Cnd condition, int pageNo, int pageSize) throws Exception;

	/**
	 * 统计用户数量
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param cnd         查询条件
	 * @return 用户数量
	 * @throws Exception
	 */
	public long countUser(IDao dao, String userModelID, Cnd cnd) throws Exception;

	/**
	 * 删除用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param userUuid    用户UUID
	 * @throws Exception
	 */
	public void deleteUser(IDao dao, String userModelID, String userUuid) throws Exception;

	/**
	 * 删除用户，携带用户操作观察者
	 * 
	 * @param prog        进度通知对象
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param userUuid    用户UUID
	 * @param observer    用户操作观察者
	 * @throws Exception
	 */
	public void deleteUser(Progress prog, IDao dao, String userModelID, String userUuid, FormOpObserver observer)
			throws Exception;

	/**
	 * 根据条件删除用户
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param cnd         查询条件
	 * @throws Exception
	 */
	public void deleteUser(IDao dao, String userModelID, Cnd cnd) throws Exception;

	/**
	 * 根据条件删除用户，携带用户操作观察者
	 * 
	 * @param prog        进度通知对象
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param cnd         查询条件
	 * @param observer    用户操作观察者
	 * @throws Exception
	 */
	public void deleteUser(Progress prog, IDao dao, String userModelID, Cnd cnd, FormOpObserver observer)
			throws Exception;

	/**
	 * 批量导入用户数据前的数据预处理操作，在数据导入导出时调用
	 * @param prog	进度通知对象
	 * @param dao	
	 * @param list	表单数据
	 * @throws Exception
	 */
	public void beforeBatchImportUsers(Progress prog,IDao dao,List<User> list) throws Exception;
	/**
	 * 验证用户密码
	 * 
	 * @param dao      数据访问对象
	 * @param userCode 用户编号
	 * @param password 需要验证的密码
	 * @return 验证结果，true表示密码正确，false表示密码错误
	 * @throws Exception
	 */
	public boolean verifyPassword(IDao dao, String userCode, String password) throws Exception;

	/**
	 * 修改用户密码
	 * 
	 * @param dao         数据访问对象
	 * @param userModelID 用户模型ID
	 * @param userCode    用户编号
	 * @param oldPassword 原密码
	 * @param newPassword 新密码
	 * @throws Exception
	 */
	public void changePassword(IDao dao, String userModelID, String userCode, String oldPassword, String newPassword)
			throws Exception;

}
