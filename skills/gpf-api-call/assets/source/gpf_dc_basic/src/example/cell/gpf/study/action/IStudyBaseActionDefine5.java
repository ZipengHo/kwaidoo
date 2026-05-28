package cell.gpf.study.action;

import cell.CellIntf;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.TableData;
import gpf.adur.user.User;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;

//动作模型代码需要继承CellIntf和BaseActionIntf接口或继承BaseActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型
public interface IStudyBaseActionDefine5 <T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		PDCForm pdcForm = rtx.getPdcForm();
		//用户状态模型
		String statusModelId = "gpf.md.jgwl.CustomerStatus";
		pdcForm.setAttrValue("状态", new AssociationData(statusModelId, "001"));
		//审批意见
		String operator = rtx.getOperator();
		User user = IUserMgr.get().queryUserByCode(rtx.getDao(), rtx.getUserModelId(), operator, User.FullName);
		String opinion = pdcForm.getString("审批意见");
		String approveTableModelId = "gpf.md.slave.jgwl.VehicleManagementApprovalRecord";
		TableData approveTable = pdcForm.getTable("审批记录");
		if(approveTable == null) {
			approveTable = new TableData();
		}
		Form approveLog = new Form(approveTableModelId);
		approveLog.setAttrValue("审批人", user.getFullName());
		approveLog.setAttrValue("审批意见", opinion);
		approveLog.setAttrValue("审批时间", System.currentTimeMillis());
		approveTable.add(approveLog);
		pdcForm.setAttrValue("审批记录", approveTable);
		rtx.setPdcForm(pdcForm);
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
