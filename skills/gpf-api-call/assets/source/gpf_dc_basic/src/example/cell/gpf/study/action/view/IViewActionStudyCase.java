package cell.gpf.study.action.view;

import com.leavay.dfc.gui.LvUtil;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.study.action.view.param.ViewActionStudyCaseParam;
import cmn.util.Tracer;
import gpf.dc.basic.fe.component.BaseFeActionIntf;

public interface IViewActionStudyCase  <T extends ViewActionStudyCaseParam> extends CellIntf,BaseFeActionIntf<T>
{

    public static IViewActionStudyCase get(){ return Cells.get(IViewActionStudyCase.class);}
    
    @Override
    default Object execute(T input) throws Exception {
    	String javaClass = input.getJavaClass();
    	Tracer tracer = newTracer();
    	System.out.println("Progress:"+input.getRtx().getProgress());
//    	tracer.info("Progress:"+input.getRtx().getProgress());
    	tracer.info("javaClass : " + javaClass);
    	BaseFeActionIntf actionIntf = (BaseFeActionIntf) Cells.get(javaClass);
    	return actionIntf.execute(input);
    }
    
    @Override
    default Class<? extends T> getInputParamClass() {
    	return (Class<? extends T>) ViewActionStudyCaseParam.class;
    }
}