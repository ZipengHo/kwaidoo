package cell.octo.cm.basic;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.basic.IBasicCacheMgr;
import cell.octo.cm.IContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.exception.VerifyException;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import com.kwaidoo.ms.tool.CmnUtil;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Org;
import gpf.adur.role.Role;
import gpf.adur.user.User;
import gpf.dc.basic.dto.privilege.IdentifyMatchParam;
import octo.cm.enums.ContextSystemVarKey;
import octo.cm.enums.GpfContextSystemVarKey;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ClassDeclare(label = "基础用户匹配规则"
        , what = ""
        , why = ""
        , how = ""
        , developer = "陈晓斌"
        , createTime = "2025-07-25"
        , updateTime = "2025-07-25"
        , version = "")
public interface IBasicUserMatchRule extends CellIntf,IdentifyRuleIntf {
	
	static IBasicUserMatchRule get() {
		return Cells.get(IBasicUserMatchRule.class);
	}
    /**
     * 缓存key：路径->组织对象Uuid
     */
    public static final String CacheBlock_OrgPath2Uuid = "OrgPath2Uuid";
    /**
     * 缓存key：路径(角色）->角色对象Uuid
     */
    public static final String CacheBlock_RolePath2Uuid = "RolePath2Uuid";

    @MethodDeclare(
            label = "所有人"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = ContextSystemVarKey.Const.$env$)
    }
    )
    default Object matchAllUser(Map<String,Object> env) throws Exception {
        if(isMatchUserMode(env)) {
            IdentifyMatchParam queryParam = new IdentifyMatchParam();
            queryParam.setMatchExpression("true");
            return queryParam;
        }else{
            IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
            String userModelId = getUserModelId(env);
            List<User> users = IUserMgr.get().queryUserPage(dao,userModelId,null,1,Integer.MAX_VALUE,false).getDataList();
            return users;
        }
    }

    @MethodDeclare(
            label = "指定用户"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = ContextSystemVarKey.Const.$env$)
            ,@InputDeclare(desc = "", label = "用户名称", name = "userName")
    }
    )
    default Object matchUser(Map<String,Object> env, String userName) throws Exception {
        if(isMatchUserMode(env)) {
            User currentUser = ContextSystemVarKey.$operator$.getContextValue(env);
            boolean match = CmnUtil.isStringEqual(currentUser.getUserName(),userName);
            IdentifyMatchParam queryParam = new IdentifyMatchParam();
            queryParam.setMatchExpression(""+match);
            return queryParam;
        }else{
            IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
            String userModelId = getUserModelId(env);
            Cnd cnd = Cnd.where(User.UserName, "=", userName);
            List<User> users = IUserMgr.get().queryUserPage(dao,userModelId,cnd,1,1,false).getDataList();
            return users;
        }

    }

    @MethodDeclare(
            label = "指定组织范围"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = ContextSystemVarKey.Const.$env$)
            ,@InputDeclare(desc = "", label = "组织路径", name = "path")
    }
    )
    default Object matchUserInOrg(Map<String,Object> env, String path) throws Exception {
        String orgModelId = getOrgModelId(env);
        String userModelId = getUserModelId(env);
        String operatorCode = GpfContextSystemVarKey.$operatorCode$.getContextValue(env);
        IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
        Tracer tracer = TraceUtil.getCurrentTracer();
        if(CmnUtil.isStringEmpty(path))
            throw new VerifyException("指定范围规则执行出错：组织路径未定义！");
        tracer.info("path="+path);
        tracer.info("orgModel="+orgModelId);
        tracer.info("userModel="+userModelId);
        tracer.info("user="+operatorCode);
        String[] pathArr = path.split("->");
        String orgPath = null;
        String roleName = null;
        if(pathArr.length == 1) {
            orgPath = pathArr[0];
        }else if(pathArr.length == 2) {
            orgPath = pathArr[0];
            roleName = pathArr[1];
        }else {
            throw new Exception("范围参数格式不正确：组织路径->角色。" + path);
        }
        if (isMatchUserMode(env)) {
            return caculateIdentifyMatchParamOfMatchUserInOrg(dao,orgModelId,userModelId,operatorCode,orgPath,roleName);
        }else{
            return queryUserListOfMatchUserInOrg(dao,orgModelId,userModelId,orgPath,roleName);
        }
    }

    default IdentifyMatchParam caculateIdentifyMatchParamOfMatchUserInOrg(IDao dao, String orgModelId,String userModelId,String operatorCode,String orgPath,String roleName) throws Exception {
        Tracer tracer = TraceUtil.getCurrentTracer();
//        if(CmnUtil.isStringEmpty(orgModelId)) {
//            tracer.warning("未指定组织模型!");
//            IdentifyMatchParam queryParam = new IdentifyMatchParam();
//            queryParam.setMatchExpression("false");
//            return queryParam;
//        }
//        if(CmnUtil.isStringEmpty(userModelId)){
//            tracer.warning("未指定用户模型!");
//            IdentifyMatchParam queryParam = new IdentifyMatchParam();
//            queryParam.setMatchExpression("false");
//            return queryParam;
//        }
        ResultSet<User> userRs = null;
        Cnd cnd = Cnd.where(User.Code, "=", operatorCode);
        if(roleName == null) {
            Org org = getOrgByPath(dao, orgModelId, orgPath);
            String orgUuid = org.getUuid();
            SqlExpressionGroup orgCondition = new SqlExpressionGroup().andEquals("uuid", orgUuid);
            String userQuerySql = IRoleMgr.get().buildQueryUserOfOrgSql(orgModelId, Cnd.where(orgCondition).toString().replaceAll("WHERE", ""), userModelId);
            userQuerySql = "with T1 as (" + userQuerySql + ") \n" +
                    " select *,"+ResultSet.TotalCount_Select + " from T1";
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            userRs = IUserMgr.get().queryUserPageBySql(dao, userModelId, userQuerySql, extFields, cnd, 1, 1);
//            userRs = IRoleMgr.get().queryUserPageOfOrg(dao, orgModelId, orgUuid, userModelId, cnd, 1, 1);
        }else {
            Role role = getRoleByPath(dao,orgModelId, orgPath, roleName);
            SqlExpressionGroup roleCondition = new SqlExpressionGroup().andEquals("uuid", role.getUuid());
            String userQuerySql = IRoleMgr.get().buildQueryUserOfRoleSql(Cnd.where(roleCondition).toString().replaceAll("WHERE", ""), userModelId);
            userQuerySql = "with T1 as (" + userQuerySql + ") \n" +
                    " select *,"+ResultSet.TotalCount_Select + " from T1";
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            userRs = IUserMgr.get().queryUserPageBySql(dao, userModelId, userQuerySql, extFields, cnd, 1, 1);
//            userRs = IRoleMgr.get().queryUserPageOfRole(dao, role.getUuid(), true, userModelId, cnd, 1, 1);
        }
        String expr = null;
        if(!userRs.isEmpty()) {
            expr = "true";
        }else {
            expr = "false";
        }
        IdentifyMatchParam queryParam = new IdentifyMatchParam();
        queryParam.setMatchExpression(expr);
        return queryParam;
    }

    /**
     * 计算指定组织范围规则匹配的用户列表
     * @param dao
     * @param orgModelId
     * @param userModelId
     * @param orgPath
     * @param roleName
     * @return
     * @throws Exception
     */
    default List<User> queryUserListOfMatchUserInOrg(IDao dao, String orgModelId,String userModelId,String orgPath,String roleName) throws Exception {
        Tracer tracer = TraceUtil.getCurrentTracer();
        ResultSet<User> userRs = null;
        Cnd cnd = Cnd.NEW();
        if(roleName == null) {
            Org org = getOrgByPath(dao, orgModelId, orgPath);
            String orgUuid = org.getUuid();
            SqlExpressionGroup orgCondition = new SqlExpressionGroup().andEquals("uuid", orgUuid);
            String userQuerySql = IRoleMgr.get().buildQueryUserOfOrgSql(orgModelId, Cnd.where(orgCondition).toString().replaceAll("WHERE", ""), userModelId);
            userQuerySql = "with T1 as (" + userQuerySql + ") \n" +
                    " select *,"+ResultSet.TotalCount_Select + " from T1";
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            userRs = IUserMgr.get().queryUserPageBySql(dao, userModelId, userQuerySql, extFields, cnd, 1, Integer.MAX_VALUE);
        }else {
            Role role = getRoleByPath(dao,orgModelId, orgPath, roleName);
            SqlExpressionGroup roleCondition = new SqlExpressionGroup().andEquals("uuid", role.getUuid());
            String userQuerySql = IRoleMgr.get().buildQueryUserOfRoleSql(Cnd.where(roleCondition).toString().replaceAll("WHERE", ""), userModelId);
            userQuerySql = "with T1 as (" + userQuerySql + ") \n" +
                    " select *,"+ResultSet.TotalCount_Select + " from T1";
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            userRs = IUserMgr.get().queryUserPageBySql(dao, userModelId, userQuerySql, extFields, cnd, 1, Integer.MAX_VALUE);
        }
        return userRs.getDataList();
    }


    /**
     * 根据路径查找组织对象
     * @param dao
     * @param orgModelID
     * @param orgPath
     * @return
     * @throws Exception
     */
    public static Org getOrgByPath(IDao dao,String orgModelID,String orgPath) throws Exception {
    	String key = orgModelID+":"+orgPath;
        String orgUuid = IBasicCacheMgr.get().getCacheData(CacheBlock_OrgPath2Uuid, key, String.class);
        Org org = null;
        if(CmnUtil.isStringEmpty(orgUuid)) {
            org = IRoleMgr.get().queryOrgByPath(dao, orgModelID, null,orgPath);
            if(org == null)
                throw new VerifyException("模型("+orgModelID+")内未找到组织路径："+orgPath);
            orgUuid = org.getUuid();
            IBasicCacheMgr.get().cacheData(CacheBlock_OrgPath2Uuid, key, orgUuid);
        }else {
            org = IRoleMgr.get().queryOrg(dao, orgModelID, orgUuid);
            if(org == null) {
            	org = IRoleMgr.get().queryOrgByPath(dao, orgModelID, null,orgPath);
                if(org == null)
                    throw new VerifyException("模型("+orgModelID+")内未找到组织路径："+orgPath);
                orgUuid = org.getUuid();
                IBasicCacheMgr.get().cacheData(CacheBlock_OrgPath2Uuid, key, orgUuid);
            }
        }
        return org;
    }
    //读取指定路径的角色
    public static Role getRoleByPath(IDao dao, String orgModelID, String orgPath, String roleName) throws Exception {
        IRoleMgr mgr = IRoleMgr.get();
        //入参校验
        if (!orgPath.startsWith("/"))           throw new Exception("组织路径使用/开头");
        String key = orgModelID + ":" + orgPath+"->"+roleName;
        String roleUuid = IBasicCacheMgr.get().getCacheData(CacheBlock_RolePath2Uuid, key, String.class);
        if(!CmnUtil.isStringEmpty(roleUuid)) {
            Role role = IRoleMgr.get().queryRole(dao, roleUuid);
            if(role == null) {
            	//读取组织
                Org org = getOrgByPath(dao, orgModelID, orgPath);
                if (org == null) throw new Exception("模型("+orgModelID+")内未找到组织路径："+orgPath);
                //读取角色
                for (Role role1 : mgr.queryRoleListOfOrg(dao, orgModelID, org.getUuid())) {
                    if (roleName.equals(role1.getLabel())) {
                        IBasicCacheMgr.get().cacheData(CacheBlock_RolePath2Uuid, key, role1.getUuid());
                        return role1;
                    }
                }
                //找不到这个角色
                throw new Exception("找不到这个角色：" + orgPath + "->" + roleName);
            }
            return role;
        }else {
            //读取组织
            Org org = getOrgByPath(dao, orgModelID, orgPath);
            if (org == null) throw new Exception("模型("+orgModelID+")内未找到组织路径："+orgPath);
            //读取角色
            for (Role role : mgr.queryRoleListOfOrg(dao, orgModelID, org.getUuid())) {
                if (roleName.equals(role.getLabel())) {
                    IBasicCacheMgr.get().cacheData(CacheBlock_RolePath2Uuid, key, role.getUuid());
                    return role;
                }
            }
            //找不到这个角色
            throw new Exception("找不到这个角色：" + orgPath + "->" + roleName);
        }
    }
}
