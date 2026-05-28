package cell.gpf.study.action.view;

import cell.CellIntf;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;

//界面动作模型代码需要继承CellIntf和BaseFeActionIntf接口或继承BaseFeActionIntf的子接口，并在接口上声明泛型T继承的动作模型参数类型
public interface IStudyBaseFeActionDefine <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		//这里编写界面动作模型代码
		return null;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}