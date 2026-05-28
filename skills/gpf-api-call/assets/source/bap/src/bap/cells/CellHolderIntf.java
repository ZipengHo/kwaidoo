package bap.cells;

// 派生这个接口的，就是Empty和Simple两个，他们实际内部会缓存impl的类、singleton对象等，在发现脏、类冲突时，可能触发clear instance动作
public interface CellHolderIntf
{
    /**
     *  当发现类冲突时，需要清空内部缓存的imple类以及singleton对象等
     *  
     *  这个动作会无条件清空builder内部缓存的singleton对象，然后再执行stopService动作，不要让此动作抛出异常
     */
    public void clearAndStopInstance();
}
