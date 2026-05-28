package cell.example.rule;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.octo.cm.IContext;
import cell.octo.cm.basic.NCMDataService;
import cell.octo.cm.basic.query.NCMOperationParameter;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.exception.VerifyException;
import com.leavay.nio.crpc.RpcMap;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.param.view.CustomQueryParameter;
import octo.cm.basic.enums.NCMDataServiceSystemOp;
import octo.cm.enums.ContextSystemVarKey;
import octocm.design.consts.Octomica2DesignConst;
import org.nutz.dao.Cnd;

import java.util.Set;

@ClassDeclare(
        label = "CM服务调用规则",
        what = "演示一个CM服务中通过NCMDataService调用另一个CM服务的表单操作",
        why = "复用目标CM服务的查询、权限、状态流转和操作编排语义",
        how = "在CM服务跨服务调用时使用，业务域从当前规则运行上下文获取，不直接绕过CM服务通过IFormMgr查询或操作表单",
        developer = "开发者",
        createTime = "2026-05-07",
        updateTime = "2026-05-07",
        version = "1.0"
)
public interface NcmDataServiceRule extends CellIntf {
    String CM_NAME = "基础信息面板名称";
    String TARGET_CM_NAME = "IML_00004";
    String FIELD_PROJECT = "关联工程";
    String OPERATION_NEW = "新增";
    String OPERATION_SAVE = "保存";

    @MethodDeclare(
            label = "实例化CM表单",
            what = "调用另一个CM服务的内部新增操作构建一份表单实例",
            how = "在当前CM服务需要发起目标CM服务新增流程时使用",
            why = "让表单初始化逻辑沿用目标CM服务配置",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$")
            }
    )
    default Form newCmForm(IContext context) throws Exception {
        NCMOperationParameter operationParameter = NcmDataServiceRule.buildOperationParameter(context, CM_NAME);
        operationParameter.setInternalOperation(OPERATION_NEW);
        return (Form) NCMDataService.get().internalOpeationCall(operationParameter);
    }

    @MethodDeclare(
            label = "按编号查询CM表单",
            what = "通过目标CM服务的详情查询操作按表单编号查询表单",
            how = "在当前CM服务需要读取另一个CM服务的表单详情时使用",
            why = "让详情查询沿用目标CM服务的数据服务语义",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单编号", name = "code", label = "表单编号")
            }
    )
    default Form queryCmFormByCode(IContext context, String code) throws Exception {
        NCMOperationParameter operationParameter = NcmDataServiceRule.buildOperationParameter(context, CM_NAME);
        operationParameter.setInternalOperation(NCMDataServiceSystemOp.系统操作_详情查询);

        RpcMap<Object> params = new RpcMap<>();
        params.put(CustomQueryParameter.FeActionParameter_Cnd, Cnd.where(Form.Code, "=", code));
        operationParameter.setParams(params);
        return (Form) NCMDataService.get().internalOpeationCall(operationParameter);
    }

    @MethodDeclare(
            label = "分页查询CM表单",
            what = "通过目标CM服务的列表查询操作分页查询表单",
            how = "在当前CM服务需要按条件查询另一个CM服务的表单列表时使用",
            why = "让列表查询沿用目标CM服务的数据服务语义",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "查询条件", name = "cnd", label = "查询条件", exampleValue = "$sysvar_cnd$"),
                    @InputDeclare(desc = "页码", name = "pageNo", label = "页码", exampleValue = "$sysvar_pageNo$"),
                    @InputDeclare(desc = "每页数量", name = "pageSize", label = "每页数量", exampleValue = "$sysvar_pageSize$")
            }
    )
    default ResultSet<Form> queryCmFormPage(IContext context, Cnd cnd, int pageNo, int pageSize) throws Exception {
        NCMOperationParameter operationParameter = NcmDataServiceRule.buildOperationParameter(context, CM_NAME);
        operationParameter.setInternalOperation(NCMDataServiceSystemOp.系统操作_列表查询);

        RpcMap<Object> params = new RpcMap<>();
        params.put(CustomQueryParameter.FeActionParameter_Cnd, cnd);
        params.put(CustomQueryParameter.FeActionParameter_PageNo, pageNo);
        params.put(CustomQueryParameter.FeActionParameter_PageSize, pageSize);
        operationParameter.setParams(params);
        return (ResultSet<Form>) NCMDataService.get().internalOpeationCall(operationParameter);
    }

    @MethodDeclare(
            label = "执行CM表单操作",
            what = "把表单放入$form$上下文变量后调用目标CM服务的指定内部操作",
            how = "在当前CM服务需要触发另一个CM服务的提交、审核、保存等操作时使用",
            why = "复用目标CM服务中已编排的表单操作逻辑",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
                    @InputDeclare(desc = "操作名称", name = "operation", label = "操作名称")
            }
    )
    default Object invokeCmOperation(IContext context, Form form, String operation) throws Exception {
        NCMOperationParameter operationParameter = NcmDataServiceRule.buildOperationParameter(context, CM_NAME);
        operationParameter.setInternalOperation(operation);

        RpcMap<Object> params = new RpcMap<>();
        ContextSystemVarKey.$form$.setContextValue(params, form);
        operationParameter.setParams(params);
        return NCMDataService.get().internalOpeationCall(operationParameter);
    }

    @MethodDeclare(
            label = "保存关联工程",
            what = "通过当前表单的关联工程属性调用编号为IML_00004的CM服务保存操作",
            how = "在当前CM服务需要把关联工程交给目标CM服务执行保存时使用",
            why = "复用目标CM服务的保存操作编排，不直接通过IFormMgr保存关联工程表单",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "当前表单", name = "form", label = "当前表单", exampleValue = "$form$")
            }
    )
    default Object saveRelatedProjectByTargetCm(IContext context, Form form) throws Exception {
        AssociationData project = form.getAssociation(FIELD_PROJECT);
        if (project == null) {
            throw new VerifyException("请选择关联工程");
        }
        Form projectForm = project.getForm();
        if (projectForm == null) {
            throw new VerifyException("关联工程不存在：" + project.getValue());
        }

        NCMOperationParameter operationParameter =
                NcmDataServiceRule.buildOperationParameter(context, TARGET_CM_NAME);
        operationParameter.setInternalOperation(OPERATION_SAVE);

        RpcMap<Object> params = new RpcMap<>();
        ContextSystemVarKey.$form$.setContextValue(params, projectForm);
        operationParameter.setParams(params);
        return NCMDataService.get().internalOpeationCall(operationParameter);
    }

    static NCMOperationParameter buildOperationParameter(IContext context, String cmName) throws Exception {
        return NcmDataServiceRule.buildOperationParameter(context.getDao(), currentDomainCode(context), cmName);
    }

    static NCMOperationParameter buildOperationParameter(IDao dao, String domain, String cmName) {
        NCMOperationParameter operationParameter = new NCMOperationParameter();
        operationParameter.setDao(dao);
        operationParameter.setCheckPrivilege(false);
        operationParameter.setDomain(domain);
        operationParameter.setCmName(cmName);
        return operationParameter;
    }

    static String currentDomainCode(IContext context) throws Exception {
        Set<String> ruleNamespaces = ContextSystemVarKey.$ruleNamespace$.getContextValue(context);
        for (String namespace : ruleNamespaces) {
            if (!Octomica2DesignConst.DOMAIN_SYSTEM.equals(namespace)) {
                return namespace;
            }
        }
        throw new VerifyException("当前规则运行上下文中未找到业务域");
    }
}
