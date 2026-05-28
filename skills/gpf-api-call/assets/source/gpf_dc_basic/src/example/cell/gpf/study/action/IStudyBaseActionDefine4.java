package cell.gpf.study.action;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import gpf.adur.data.AssociationData;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;

//动作模型代码需要继承CellIntf和BaseActionIntf接口或继承BaseActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型
public interface IStudyBaseActionDefine4 <T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		PDCForm pdcForm = rtx.getPdcForm();
		//申请类型模型
		AssociationData approveType = pdcForm.getAssociation("申请类型");
		String approveTypeCode = approveType.getValue();
		//用户状态模型
		String statusModelId = "gpf.md.jgwl.CustomerStatus";
		if(approveTypeCode.equals("002")) {
			pdcForm.setAttrValue("状态", new AssociationData(statusModelId, "001"));
		}else if(approveTypeCode.equals("003")) {
			pdcForm.setAttrValue("状态", new AssociationData(statusModelId, "003"));
		}
		rtx.setPdcForm(pdcForm);
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
