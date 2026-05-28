package cell.octo.cm.basic;

import cell.octo.cm.IContext;
import com.kwaidoo.ms.tool.CmnUtil;
import gpf.dc.basic.dto.privilege.PrivilegeRuleEnum;
import gpf.dc.basic.exception.ExpressionException;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import octo.cm.enums.ContextSystemVarKey;

import java.text.Normalizer;
import java.util.Map;

/**
 * 匹配身份规则接口，定义运行匹配身份规则的相关运行环境参数和获取方法
 * 匹配身份规则运行有两种运行模式：
 * matchUser ： 用于计算当前用户是否匹配指定的身份规则，返回IdentifyMatchParam
 * queryUser ： 用于计算当前身份规则匹配的用户列表，返回List<User>
 * 默认是 matchUser 模式
 */
public interface IdentifyRuleIntf {
	public final static String Key_IdentifyRuleMode = "$identifyRuleMode$";
	public final static String IdentifyRuleMode_Match = "matchUser";
	public final static String IdentifyRuleMode_Query = "queryUser";

	/**
	 * 当前运行用于匹配用户身份模式
	 * @param env
	 * @return
	 */
	default boolean isMatchUserMode(Map<String,Object> env){
		String mode = CmnUtil.getString(env.get(Key_IdentifyRuleMode),IdentifyRuleMode_Match);
		return CmnUtil.isStringEqual(mode, IdentifyRuleMode_Match);
	}

	/**
	 * 当前运行用于查询用户列表模式
	 * @param env
	 * @return
	 */
	default boolean isQueryUserMode(Map<String,Object> env){
		String mode = (String)env.get(Key_IdentifyRuleMode);
		return CmnUtil.isStringEqual(mode, IdentifyRuleMode_Query);
	}

	/**
	 * 返回当前环境下的组织模型ID
	 * @param env
	 * @return
	 */
	default String getOrgModelId(Map<String,Object> env) throws Exception {
		String orgModelId = ContextSystemVarKey.$orgModelId$.getContextValue(env);
		if(CmnUtil.isStringEmpty(orgModelId)){
			throw new Exception("规则运行环境中缺少$orgModelId$变量！");
		}
		return orgModelId;
	}

	/**
	 * 返回当前环境下的用户模型ID
	 * @param env
	 * @return
	 */
	default String getUserModelId(Map<String,Object> env) throws Exception {
		String userModelId = ContextSystemVarKey.$userModelId$.getContextValue(env);
		if(CmnUtil.isStringEmpty(userModelId)){
			throw new Exception("规则运行环境中缺少$userModelId$变量！");
		}
		return userModelId;
	}

}

