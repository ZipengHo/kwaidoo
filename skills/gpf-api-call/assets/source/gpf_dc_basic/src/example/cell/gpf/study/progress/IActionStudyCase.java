package cell.gpf.study.progress;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.study.progress.param.ActionStudyCaseParam;
import cmn.util.Tracer;
import gpf.dc.action.intf.BaseActionIntf;

public interface IActionStudyCase <T extends ActionStudyCaseParam> extends CellIntf,BaseActionIntf<T>
{

    public static IActionStudyCase get(){ return Cells.get(IActionStudyCase.class);}
    
    @Override
    default Object execute(T input) throws Exception {
    	String javaClass = input.getJavaClass();
    	Tracer tracer = newTracer();
    	System.out.println("Progress:"+input.getRtx().getProgress());
//    	tracer.info("Progress:"+input.getRtx().getProgress());
    	BaseActionIntf actionIntf = (BaseActionIntf) Cells.get(javaClass);
    	return actionIntf.execute(input);
    }
    
    @Override
    default Class<? extends T> getInputParamClass() {
    	return (Class<? extends T>) ActionStudyCaseParam.class;
    }
}