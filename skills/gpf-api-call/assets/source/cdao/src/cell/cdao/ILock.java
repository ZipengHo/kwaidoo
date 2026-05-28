package cell.cdao;

import com.cdao.mgr.lock.LockFailException;
import com.cdao.mgr.lock.NoLockException;
import com.leavay.common.util.TimeoutException;

import bap.cells.Cells;
import cell.ServiceCellIntf;

/**
 * 分布式锁Cell（基于CDao的cell封装，提供本地调试核）
 * 
 * 注：此Cell有两个核，当处于远程调试状态时，启用的是本地锁替代，失去分布式锁的功能，以保护服务环境安全
 */
public interface ILock extends ServiceCellIntf
{
    public static ILock get()
    {
        return Cells.get(ILock.class);
    }

    public default boolean tryLockKey(String key)
    {
        return tryLockKey(key, null);
    }
    
    public boolean tryLockKey(String key, String info);

    // timeout (ms)
    public default void lockKey(String key, long timeout) throws LockFailException, TimeoutException
    {
        lockKey(key, null, timeout);
    }

    // timeout (ms)
    public void lockKey(String key, String info, long timeout) throws LockFailException, TimeoutException;

    public void unlock(String key);
    
    /** 
    * force : 强行解锁（不管是不是自己线程锁住的）
    */
    public void unlock(String key, boolean force);
    
    public String getLockInfo(String key) throws NoLockException, Exception;
}
