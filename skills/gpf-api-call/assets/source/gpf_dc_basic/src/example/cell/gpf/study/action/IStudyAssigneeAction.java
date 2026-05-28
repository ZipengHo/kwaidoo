package cell.gpf.study.action;

import java.util.ArrayList;
import java.util.List;

import cell.CellIntf;
import cell.gpf.adur.user.IUserMgr;
import cmn.anotation.ClassDeclare;
import gpf.adur.user.User;
import gpf.dc.action.intf.SetAssigneeIntf;
import gpf.dc.action.param.BaseActionParameter;
@ClassDeclare(label = "接收人计算代码样例"
,what="接收人计算代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyAssigneeAction <T extends BaseActionParameter> extends CellIntf, SetAssigneeIntf<T>{

	
	@Override
	default List<User> getAssigneeList(T input) throws Exception {
		// 返回计算后的接收人列表
		List<User> userList = new ArrayList<>();
		String userModelId = null;
		String userCode = null;
		User user = IUserMgr.get().queryUserByCode(input.getRtx().getDao(), userModelId, userCode, User.UUID,User.Code,User.UserName,User.FullName);
		userList.add(user);
		return userList;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型声明的参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
