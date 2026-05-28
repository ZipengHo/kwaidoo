package cell.gpf.study.errorhandler;

import bap.cells.Cells;
import cell.CellIntf;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;

public interface IStudyErrorHandler <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	public static IStudyErrorHandler get() {
		return Cells.get(IStudyErrorHandler.class);
	}
	
	@Override
	default Object execute(T input) throws Exception {
		testErrorHander("测试表");
		return null;
	}
	
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	
	default void testErrorHander(String table) throws Exception {
		throw new Exception("org.postgresql.util.PSQLException: 错误: 关系 "+table+" 不存在");
	}
}
