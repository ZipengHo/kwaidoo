package cell.gpf.study.action;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import gpf.adur.data.AssociationData;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;
import gpf.exception.VerifyException;

public interface IStudyBaseActionDefine2 <T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		PDCForm pdcForm = rtx.getPdcForm();
		AssociationData jsType = pdcForm.getAssociation("结算方式");
		if(jsType.getValue().equals("002")) {
			Double value = pdcForm.getDouble("体积");
			if(value == null || value == 0.0D)
				throw new VerifyException("按方结算，体积不得为空！");
		}else if(jsType.getValue().equals("003")) {
			Double value = pdcForm.getDouble("体积");
			if(value == null || value == 0.0D)
				throw new VerifyException("按吨结算，重量不得为空!");
		}
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
