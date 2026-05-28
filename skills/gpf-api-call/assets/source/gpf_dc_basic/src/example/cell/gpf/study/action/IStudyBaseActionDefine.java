package cell.gpf.study.action;

import cell.CellIntf;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;

//动作模型代码需要继承CellIntf和BaseActionIntf接口或继承BaseActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型
public interface IStudyBaseActionDefine <T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		//这里编写动作模型代码
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
