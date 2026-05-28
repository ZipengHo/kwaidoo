package cell.octo.cm.basic;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.dc.basic.IPositionPrivilegeMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import com.kwaidoo.ms.tool.CmnUtil;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Role;
import gpf.adur.user.User;
import gpf.dc.basic.dto.privilege.nesting.MenuSetDto;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.runtime.OperateLog;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import octo.cm.enums.ContextSystemVarKey;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface IBasicPrivilegeRule extends CellIntf, PrivilegeRuleIntf {

    @MethodDeclare(
            label = "R"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
    }
    )
    default void R(Map<String, Object> env) {
        Object priv = getPrivilege(env);
        if (priv instanceof FieldPrivilegeDto) {
            ((FieldPrivilegeDto) priv).setVisible(true);
        } else if (priv instanceof ActionPrivilegeDto) {
            ((ActionPrivilegeDto) priv).setVisible(true);
        }
    }

    @MethodDeclare(
            label = "RT"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
            , @InputDeclare(desc = "", label = "GPF运行上下文", name = "rtx", exampleValue = "$IDCRuntimeContext$")
            , @InputDeclare(desc = "", label = "来源节点", name = "router")
    }
    )
    default void RT(Map<String, Object> env, IDCRuntimeContext rtx, String router) throws Exception {
        Tracer tracer = TraceUtil.getCurrentTracer();
        Object priv = getPrivilege(env);
//			String[] routerArr = router.split("-");
//			if(routerArr.length != 2)
//				throw new Exception("参数格式不正确，格式：来源节点-当前节点");
        OperateLog opLog = rtx.getCurrOpLog();
        if (opLog != null)
            tracer.info("来源节点：[" + opLog.getLastNodeName() + "]");
        if (opLog != null && CmnUtil.isStringEqual(opLog.getLastNodeName(), router)) {
            if (priv instanceof FieldPrivilegeDto) {
                ((FieldPrivilegeDto) priv).setVisible(true);
            } else if (priv instanceof ActionPrivilegeDto) {
                ((ActionPrivilegeDto) priv).setVisible(true);
            }
        }
    }

    @MethodDeclare(
            label = "W"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
    }
    )
    default void W(Map<String, Object> env) {
        Object priv = getPrivilege(env);
        if (priv instanceof FieldPrivilegeDto) {
            ((FieldPrivilegeDto) priv).setVisible(true).setWritable(true);
        }
    }

    @MethodDeclare(
            label = "WT"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
            , @InputDeclare(desc = "", label = "GPF运行上下文", name = "rtx", exampleValue = "$IDCRuntimeContext$")
            , @InputDeclare(desc = "", label = "来源节点", name = "router")
    }
    )
    default void WT(Map<String, Object> env, IDCRuntimeContext rtx, String router) throws Exception {
        Object priv = getPrivilege(env);
//			String[] routerArr = router.split("-");
//			if(routerArr.length != 2)
//				throw new Exception("参数格式不正确，格式：来源节点-当前节点");
        OperateLog opLog = rtx.getCurrOpLog();
        if (opLog != null && CmnUtil.isStringEqual(opLog.getLastNodeName(), router)) {
            if (priv instanceof FieldPrivilegeDto) {
                ((FieldPrivilegeDto) priv).setVisible(true).setWritable(true);
            } else if (priv instanceof ActionPrivilegeDto) {
                ((ActionPrivilegeDto) priv).setVisible(true).setOperatable(true);
            }
        }
    }

    @MethodDeclare(
            label = "X"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
    }
    )
    default void X(Map<String, Object> env) {
        Object priv = getPrivilege(env);
        if (priv instanceof ActionPrivilegeDto) {
            ((ActionPrivilegeDto) priv).setVisible(true).setOperatable(true);
        }
    }

    @MethodDeclare(
            label = "N"
            , how = ""
            , what = ""
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
    }
    )
    default void N(Map<String, Object> env) {
        Object priv = getPrivilege(env);
        if (priv instanceof ActionPrivilegeDto) {
            ((ActionPrivilegeDto) priv).setVisible(false).setOperatable(false);
        } else if (priv instanceof FieldPrivilegeDto) {
            ((FieldPrivilegeDto) priv).setVisible(false).setWritable(false);
        }
    }

    static String EnvParamKey_PositionMenus = "PositionMenus";
    @MethodDeclare(
            label = "匹配岗位菜单"
            , how = ""
            , what = "由应用自身控制组织内的岗位菜单权限"
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
    }
    )
    default void matchMenuPrivilegeByPosition(Map<String, Object> env) throws Exception {
        Set<String> cacheMatchMenus = (Set<String>) env.get(EnvParamKey_PositionMenus);
        if(cacheMatchMenus == null){
            cacheMatchMenus = new LinkedHashSet<>();
            IPositionPrivilegeMgr positionMenuMgr = IPositionPrivilegeMgr.get();
            IApplicationService applicationService = IApplicationService.get();
            User user = ContextSystemVarKey.$operator$.getContextValue(env);
            ApplicationSetting appSetting = ContextSystemVarKey.$applicationSetting$.getContextValue(env);
            Map<String,String> menuUuidPath = applicationService.queryAppMenuUuidPathMap(appSetting);
            //查找当前用户所在的组织和角色路径
            IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
            String orgModelId = ContextSystemVarKey.$orgModelId$.getContextValue(env);
            String userModelId = ContextSystemVarKey.$userModelId$.getContextValue(env);
            IRoleMgr roleMgr = IRoleMgr.get();
            ResultSet<Role> roleRs = roleMgr.queryRolePageOfUser(dao,userModelId,user.getUuid(),null,1,Integer.MAX_VALUE,true);
            if(!roleRs.isEmpty()){
                List<String> roleUuids = roleRs.getDataList().stream().map(Role::getUuid).collect(Collectors.toList());
                for(Role role : roleRs.getDataList()){
                    List<MenuSetDto> menuSetDtos = positionMenuMgr.queryPositionMenuRelationList(dao,role.getUuid(),appSetting.getUuid());
                    for(MenuSetDto menuSetDto : menuSetDtos){
                        if(menuUuidPath.containsKey(menuSetDto.getMenuUuid())){
                            cacheMatchMenus.add(menuUuidPath.get(menuSetDto.getMenuUuid()));
                        }
                    }
                }
            }
            env.put(EnvParamKey_PositionMenus, cacheMatchMenus);
        }

		Object priv = getPrivilege(env);
        if (priv instanceof FieldPrivilegeDto) {
            String fieldName = ((FieldPrivilegeDto) priv).getFieldName();
            if(cacheMatchMenus.contains(fieldName)){
                ((FieldPrivilegeDto) priv).setVisible(true);
            }else{
                ((FieldPrivilegeDto) priv).setVisible(false);
            }
        }
        //由于权限计算结果是根据用户所在岗位与菜单关系计算的，所以不能直接缓存权限计算结果
        env.put(Key_RuleResultCachable, false);
    }

    static String EnvParamKey_PositionResources = "PositionResources";
    @MethodDeclare(
            label = "匹配岗位资源"
            , how = ""
            , what = "由应用自身控制组织内的岗位资源权限"
            , why = ""
            , inputs = {
            @InputDeclare(desc = "", label = "规则运行环境", name = "env", exampleValue = "$env$")
            ,@InputDeclare(desc = "", label = "规则运行环境", name = "ruleNamespaces", exampleValue = "$ruleNamespaces$")
            ,@InputDeclare(desc = "", label = "资源名称", name = "resourceName")
    }
    )
    default void matchResourcePrivilegeByPosition(Map<String, Object> env,Set<String> ruleNamespaces,String resourceName) throws Exception {
        Set<String> cacheMatchResources = (Set<String>) env.get(EnvParamKey_PositionResources);
        if(cacheMatchResources == null){
            cacheMatchResources = new LinkedHashSet<>();
            IPositionPrivilegeMgr positionMenuMgr = IPositionPrivilegeMgr.get();
            IApplicationService applicationService = IApplicationService.get();
            User user = ContextSystemVarKey.$operator$.getContextValue(env);
            ApplicationSetting appSetting = ContextSystemVarKey.$applicationSetting$.getContextValue(env);

            //查找当前用户所在的组织和角色路径
            IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
            String orgModelId = ContextSystemVarKey.$orgModelId$.getContextValue(env);
            String userModelId = ContextSystemVarKey.$userModelId$.getContextValue(env);
            IRoleMgr roleMgr = IRoleMgr.get();
            ResultSet<Role> roleRs = roleMgr.queryRolePageOfUser(dao,userModelId,user.getUuid(),null,1,Integer.MAX_VALUE,true);
            if(!roleRs.isEmpty()){
                for(Role role : roleRs.getDataList()){
                    //TODO 补充资源操作权限查询

                }
            }
            env.put(EnvParamKey_PositionResources, cacheMatchResources);
        }

        Object priv = getPrivilege(env);
        if (priv instanceof FieldPrivilegeDto) {
            String fieldName = ((FieldPrivilegeDto) priv).getFieldName();
            if(cacheMatchResources.contains(fieldName)){
                ((FieldPrivilegeDto) priv).setVisible(true);
            }else{
                ((FieldPrivilegeDto) priv).setVisible(false);
            }
        }
        //由于权限计算结果是根据用户所在岗位与菜单关系计算的，所以不能直接缓存权限计算结果
        env.put(Key_RuleResultCachable, false);
    }


}
