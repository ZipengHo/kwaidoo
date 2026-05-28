package cell.octo.cm.basic;

import java.util.Map;

import gpf.dc.basic.dto.privilege.PrivilegeRuleEnum;
import gpf.dc.basic.exception.ExpressionException;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;

public interface PrivilegeRuleIntf {
	public final static String Key_Privilege = "$privilege";
	public final static String Key_RuleResultCachable = "$ruleResultCachable$";

	default void setPrivilege(Map<String, Object> env, FieldPrivilegeDto fieldPriv) {
		env.put(Key_Privilege, fieldPriv);
	}

	default Object getPrivilege(Map<String, Object> env) {
		return env.get(Key_Privilege);
	}

	default void setPrivilege(Map<String, Object> env, ActionPrivilegeDto actionPriv) {
		env.put(Key_Privilege, actionPriv);
	}

	default void setPrivilege(Map<String, Object> env, String rule) {
		String[] rules = rule.split(",");
		for (String r : rules) {
			PrivilegeRuleEnum ruleEnum = PrivilegeRuleEnum.formValue(r);
			if (ruleEnum == null)
				throw new ExpressionException("参数格式不正确，取值格式：R/W/X/N，如：R");
			Object priv = getPrivilege(env);
			if (priv instanceof FieldPrivilegeDto) {
				PrivilegeRuleEnum.setFieldPrivielge(ruleEnum, (FieldPrivilegeDto) priv);
			} else if (priv instanceof ActionPrivilegeDto) {
				PrivilegeRuleEnum.setActionPrivielge(ruleEnum, (ActionPrivilegeDto) priv);
			}
		}
	}
}
