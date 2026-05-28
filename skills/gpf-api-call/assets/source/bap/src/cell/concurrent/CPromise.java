package cell.concurrent;

import java.io.Serializable;
import java.util.List;

import com.leavay.client.util.lazy.LazyPool;
import com.leavay.common.util.ToolBasic;
import com.leavay.ms.tool.CmnUtil;

import bap.cells.BasicStackCell;
import cell.function.IConsumer;
import cmn.util.Nulls;

/**
 * 运行在执行端，这里的then/error消费者是调用端设置下来的回调对象
 * 当结束时，主动调用then/error的回调对象，如果还没来得及设置回调对象则会延迟重试
 * 如果已经通知过则不会再通知，如果执行完还没来得及设置消费者，则会延迟重试，直到调用端设置消费者为止。
 * 
 * 默认有2s保护时长，2s内都没有设置消费者，而执行又结束得很快，则结果会被抛弃
 * 
 * 2025年5月26日
 * 
 *
 */
public class CPromise<T extends Serializable> extends BasicStackCell implements IPromise<T>
{

    IConsumer<T> _thenCsm;
    IConsumer<Throwable> _errorCsm;
    
    boolean _notified = false;
    boolean _done = false;
    long _doneTime = -1;
    T _result;
    Throwable _error;
    
    // 用于保护异步执行过快而调用端还没来得及设置消费者
    long _protectTimeout = 2*1000; // 默认有2s保护时长，2s内都没有设置消费者，而执行又结束得很快，则结果会被抛弃
    
    static LazyPool<CPromise> _lazyNotify = new LazyPool<CPromise>("", 100)
    {
        public void handle(List<CPromise> lstData)
        {
            for (CPromise p : Nulls.get(lstData))
            {
                try
                {
                    p.tryNotifyAction();
                }catch (Throwable err)
                {
                    logError("Failed on promise notify", err);
                }
            }
        }
    };
    
    public CPromise()
    {
        super();
    }

    /**
     * 
     * @param protectTimeout ：保护超时。
     *  当异步执行过快，调用端还没来得及设置消费者就结束了，这时需要一个保护机制，等待消费者被设置，从而正确接收结果。
     */
    public CPromise(long protectTimeout)
    {
        super();
        setProtectTimeout(protectTimeout);
    }
    
    static void lazyNotify(CPromise promise)
    {
        _lazyNotify.add(promise);
    }
    
    public static void logError(String msg, Throwable err)
    {
        CmnUtil.err(msg+"\n"+ToolBasic.getExceptionStatck(err, true));
    }
    
    @Override
    public IPromise<T> then(IConsumer<T> consumer)
    {
        this._thenCsm = consumer;

        tryNotifyAction();
        return this;
    }

    @Override
    public IPromise<T> error(IConsumer<Throwable> consumer)
    {
        this._errorCsm = consumer;

        tryNotifyAction();
        return this;
    }

    public synchronized void setResult(T result)
    {
        _result = result;
        setDone();
        
        tryNotifyAction();
    }

    public synchronized void setError(Throwable err)
    {
        _error = err;
        setDone();

        tryNotifyAction();
    }
    
    public synchronized T getResult()
    {
        return _result;
    }
    
    public synchronized Throwable getError()
    {
        return _error;
    }
    
    public synchronized boolean isDone()
    {
        return _done;
    }

    public synchronized void setDone()
    {
        this._done = true;
        _doneTime = System.currentTimeMillis();
    }

    public synchronized boolean isNotified()
    {
        return _notified;
    }

    public synchronized void setNotified(boolean _notified)
    {
        this._notified = _notified;
    }

    public void tryNotifyAction()
    {
        if (!notifyAction())
            lazyNotify(this); // 不具备发送条件的，延迟重试
    }
    
    public long getProtectTimeout()
    {
        return _protectTimeout;
    }


    // 用于保护异步执行过快而调用端还没来得及设置消费者
    public CPromise<T> setProtectTimeout(long protectTimeout)
    {
        this._protectTimeout = protectTimeout;
        return this;
    }

    protected void checkProtectTimeout(Object value)
    {
        if ((System.currentTimeMillis() - _doneTime) > _protectTimeout)
            throw new RuntimeException("Promise protect timeout.Ignore notify : "+value);
    }
    
    /**
     * 返回是否已经完成通知，可以抛弃了
     */
    public synchronized boolean notifyAction()
    {
        if (isNotified())
            return true;
        
        if (!isDone())
            return false;
        
        if (_error != null)
        {
            // 处理异常
            // 如果还没设置消费者，则延迟再试
            if (_errorCsm == null)
            {
                checkProtectTimeout(_error);
                return false;
            }
            else
            {
                try
                {
                    _errorCsm.accept(ToolBasic.convertException2Remote(_error));
                }
                catch(Throwable err)
                {
                    logError("Invalid Promise Exception : " + _result, err); // 只记录异常日志，忽略掉异常，视为已经通知成功
                } 
            }
        }else
        { 
            // 处理成功的结果
            // 如果还没设置消费者，则延迟再试
            if (_thenCsm == null)
            {
                checkProtectTimeout(_result);
                return false;
            }
            else
            {
                try
                {
                    _thenCsm.accept(_result);
                }catch(Throwable err)
                {
                    // 只记录异常日志，忽略掉异常，视为已经通知成功
                    logError("Promise Error : " +_result, err);
                } 
            }
        }
        
        // 走到这里说明已顺利发出通知，并可以抛弃了
        setNotified(true);
        return true;
    }
}
