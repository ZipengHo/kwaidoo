package cell.gpf.study.nadur.user;

import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.study.nadur.role.StudyOrgMockConst;
import gpf.adur.role.Org;
import gpf.adur.role.Role;
import org.nutz.dao.Cnd;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.user.IUserMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Password;
import gpf.adur.data.ResultSet;
import gpf.adur.user.User;
import gpf.adur.user.UserGender;
import gpf.adur.user.UserStatus;
@ClassDeclare(label = "用户操作代码样例"
,what="用户操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyUserOp extends CellIntf{

	static IStudyUserOp get() {
		return Cells.get(IStudyUserOp.class);
	}
	
	
	default void testCreateUpdateUser() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try(IDao dao = IDaoService.newIDao()){
			User user = IUserMgr.get().queryUserByCode(dao, StudyUserMockConst.ModelId, StudyUserMockConst.TestUserCode);
			if(user == null) {
				user = new User(StudyUserMockConst.ModelId);
				user.setCode(StudyUserMockConst.TestUserCode);
				user.setUserName(StudyUserMockConst.TestUserName);
				user.setFullName("测试员");
				user.setEmail("test@kwaidoo.com");
				user.setPhone("135001235900");
				user.setPassword(new Password().setValue("123456"));
//			user.setTokenExpireTime(120);//Token生效时间，单位：分钟，不设置默认不失效
//			user.setProfilePhoto(profilePhoto);//头像
				user.setGender(UserGender.Male);
				user.setStatus(UserStatus.UnLocked);
				user = IUserMgr.get().createUser(dao, user);
				tracer.info(user);
			}else {
				user.setPassword(new Password().setValue("123123"));
				user = IUserMgr.get().updateUser(dao, user);
			}
			dao.commit();
			
			String userModelID = StudyUserMockConst.ModelId;
			Cnd cnd = Cnd.where(User.Code, "=", StudyUserMockConst.TestUserCode);
			String[] fields = null;//查询的属性列表，不传时默认查所有
			boolean compoundField = false;//是否查询嵌套模型数据
			int pageNo = 1;
			int pageSize = 20;
			ResultSet<User> rs = IUserMgr.get().queryUserPage(dao, userModelID, cnd, pageNo, pageSize, compoundField, fields);
			for(User u : rs.getDataList()) {
				tracer.info(u);
			}
		}
	}

	//查询用户已挂载角色和组织
	default void testQueryUserMountedRoleAndOrgs() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try(IDao dao = IDaoService.newIDao()){
			String orgModelID = StudyOrgMockConst.ModelId;
			String userModelID = StudyUserMockConst.ModelId;
			String userCode = StudyUserMockConst.TestUserCode;
			User user = IUserMgr.get().queryUserByCode(dao, userModelID, userCode);
			int pageNo = 1;
			int pageSize = 20;
			boolean onlyMounted = true;//是否只查询已挂载角色
			Cnd cnd = Cnd.NEW();
			ResultSet<Role> rs = IRoleMgr.get().queryRolePageOfUser(dao,userModelID,user.getUuid(),cnd,pageNo,pageSize,onlyMounted);
			for(Role r : rs.getDataList()) {
				Org org = IRoleMgr.get().queryOrg(dao,orgModelID,r.getOwner());
				tracer.info("角色："+r.getLabel()+"，角色ID："+r.getUuid()+"，组织："+org.getLabel()+"，组织ID："+org.getUuid());
			}
		}
	}
	
	default void testChangePassword() throws Exception {
		try(IDao dao = IDaoService.newIDao()){
			String userModelID = StudyUserMockConst.ModelId;
			String userCode = StudyUserMockConst.TestUserCode;
			String password = "123456";
			String oldPassword = "123123";
			IUserMgr.get().verifyPassword(dao, userCode, password);
			IUserMgr.get().changePassword(dao, userModelID, userCode, oldPassword, password);
			dao.commit();
		}
	}
	
}
