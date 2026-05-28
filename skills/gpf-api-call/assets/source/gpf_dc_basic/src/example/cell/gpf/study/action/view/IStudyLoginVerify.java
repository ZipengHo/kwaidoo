package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cmn.anotation.ClassDeclare;
import fe.util.exception.VerifyException;
import gpf.adur.data.AssociationData;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Org;
import gpf.adur.user.User;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.loginverify.LoginVerifyUserUnderOrgParameter;
@ClassDeclare(label = "应用自定义登录认证代码样例"
,what="应用自定义登录认证代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyLoginVerify <T extends LoginVerifyUserUnderOrgParameter> extends CellIntf,BaseFeActionIntf<T>{ 

	@Override
	default Object execute(T input) throws Exception {
		List<AssociationData> allowOrgs = input.getAllowOrgs();
		if(allowOrgs.isEmpty())
			throw new VerifyException("未获得访问许可！");
		String orgModelId = input.getOrgModelId();
		String userModelId = input.getUserModelId();
		String loginUser = input.getRtx().getOperator();
		User user = IUserMgr.get().queryUserByCode(input.getRtx().getDao(), userModelId, loginUser);
		List<Org> allowOrgList = new ArrayList<>();
		for(AssociationData assocData : allowOrgs) {
			Org allowOrg = IRoleMgr.get().queryOrgByCode(input.getRtx().getDao(), orgModelId, assocData.getValue());
			if(allowOrg != null)
				allowOrgList.add(allowOrg);
		}
		if(allowOrgList.isEmpty())
			throw new VerifyException("未获得访问许可！");
		Map<String,Org> allowOrgMap = IRoleMgr.get().queryPathOfOrg(input.getRtx().getDao(), allowOrgList);
		ResultSet<Org> rs = IRoleMgr.get().queryOrgPageOfUser(input.getRtx().getDao(), orgModelId, null, userModelId, user.getUuid(), 1, Integer.MAX_VALUE);
		Map<String,Org> userOrgMap = IRoleMgr.get().queryPathOfOrg(input.getRtx().getDao(), rs.getDataList());
		boolean isAllow = false;
		for(String userOrgPath : userOrgMap.keySet()) {
			for(String allowOrgPath : allowOrgMap.keySet()) {
				if(userOrgPath.startsWith(allowOrgPath)) {
					isAllow = true;
					break;
				}
			}
		}
		if(!isAllow) {
			throw new VerifyException("未获得访问许可！");
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) LoginVerifyUserUnderOrgParameter.class;
	}
}