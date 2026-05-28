package bap.cells;

import cell.CellIntf;

/**
 * Cell的HOOK，与注解After、Before配合使用
 * 
 * onRetrun/onException都只对After Hook有效
 * 
 *  实现类必须能无参构造
 *
 */
public interface CellHookIntf
{
    public static enum ENTRY {BEFORE, AFTER};
    
    /**
     * 在Cell的实现类中可以用Before、After等标注在方法上，指向该接口，从而触发相关Hook动作
     *
     * @param entry : Before or After
     * @param id : 进程内唯一分配的id，每次调用，所有hook将接收到同一个id，以标识同一次调用
     * @param cell ：细胞对象
     * @param func ：所调用的细胞方法
     * @param params ：传入的参数
     */
    public void onAction(ENTRY entry, long id, CellIntf cell, String func, Object ... params);
    
    /**
     * 当有返回对象是触发，且仅在After Hook中触发，传入返回值及函数特征
     * 如果函数是Void，则不会触发此方法
     */
    public default void onRetrun(Object returnObject, long id, CellIntf cell, String func, Object ... params)
    {
        
    }

    /**
     * 仅在After Hook中触发，传入异常对象及函数特征
     */
    public default void onException(Throwable exception, long id, CellIntf cell, String func, Object ... params)
    {
        
    }
}
