package bap.cells.hook;

import com.leavay.common.util.ToolBasic;

import bap.cells.CellHookIntf;
import cell.CellIntf;

public class CellHookPrinter implements CellHookIntf
{
    public void onAction(ENTRY entry, long id, CellIntf cell, String func, Object... params)
    {
        System.err.println("["+entry+"]("+id+") : " + cell.getClass().getSimpleName()+"."+func+ToolBasic.logString(params, false)+" - Cell="+cell);
//        System.out.println(ToolUtilities.getCurrentStack());        
    }
    

    /**
     * 当有返回对象是触发，且仅在After Hook中触发，传入返回值及函数特征
     * 如果函数是Void，则不会触发此方法
     */
    public void onRetrun(Object returnObject, long id, CellIntf cell, String func, Object ... params)
    {
        System.err.println("[RETURN]("+id+") : " + returnObject+"("+cell.getClass().getSimpleName()+"::"+func+")");
    }

    /**
     * 在After Hook中触发，传入异常对象及函数特征
     */
    public void onException(Throwable exception, long id, CellIntf cell, String func, Object ... params)
    {
//        exception.printStackTrace();
        System.err.println("[Exception]("+id+") : " + exception+"("+cell.getClass().getSimpleName()+"::"+func+")");
    }

}
