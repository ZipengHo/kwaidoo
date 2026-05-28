package cell.gpf.study.action;

import java.util.Arrays;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import gpf.adur.data.AssociationData;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;
import gpf.exception.VerifyException;

//动作模型代码需要继承CellIntf和BaseActionIntf接口或继承BaseActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型
public interface IStudyBaseActionDefine3 <T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		PDCForm pdcForm = rtx.getPdcForm();
		//申请类型模型
		String applyModelId = "gpf.md.jgwl.ApplicationType";
		AssociationData applyType = new AssociationData(applyModelId,"001");
		pdcForm.setAttrValue("申请类型", applyType);
		//用户状态模型
		String statusModelId = "gpf.md.jgwl.CustomerStatus";
		AssociationData statuData = new AssociationData(statusModelId,"002");
		pdcForm.setAttrValue("状态", statuData);
		rtx.setPdcForm(pdcForm);
		//车辆管理模型
		String carModelId = "gpf.md.jgwl.CLXG";
		IDao dao = rtx.getDao();
		Cnd cnd = Cnd.NEW();
		String statuFieldCode = IFormMgr.get().getFieldCode("状态");
		String drvierFieldCode = IFormMgr.get().getFieldCode("行驶证用户");
		AssociationData driverAssoc = pdcForm.getAssociation("行驶证用户");
		String driverValue = driverAssoc.getValue();
		cnd.and(new SqlExpressionGroup().andInStrList(statuFieldCode, Arrays.asList("001","002")).andEquals(drvierFieldCode, driverValue));
		boolean exist = IFormMgr.get().isFormExistByCnd(dao, carModelId, cnd);
		if(exist)
			throw new VerifyException("该行驶证用户已有注册车辆，不得重复注册！");
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
