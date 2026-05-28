package cell.gpf.study.action.view;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.study.action.view.param.ViewDataQueryActionStudyCaseParam;
import cmn.util.Tracer;
import gpf.dc.basic.action.intf.CustomQueryIntf;
import gpf.dc.basic.fe.component.BaseFeActionIntf;

public interface IViewDataQueryActionStudyCase  <T extends ViewDataQueryActionStudyCaseParam> extends CellIntf,CustomQueryIntf<T>
{

    public static IViewDataQueryActionStudyCase get(){ return Cells.get(IViewDataQueryActionStudyCase.class);}
    
    @Override
    default Object execute(T input) throws Exception {
    	String javaClass = input.getJavaClass();
    	Tracer tracer = newTracer();
    	System.out.println("Progress:"+input.getRtx().getProgress());
//    	tracer.info("Progress:"+input.getRtx().getProgress());
    	BaseFeActionIntf actionIntf = (BaseFeActionIntf) Cells.get(javaClass);
    	return actionIntf.execute(input);
    }
    
    @Override
    default Class<? extends T> getInputParamClass() {
    	return (Class<? extends T>) ViewDataQueryActionStudyCaseParam.class;
    }
}