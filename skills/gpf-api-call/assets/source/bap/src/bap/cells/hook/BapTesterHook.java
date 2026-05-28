package bap.cells.hook;

import java.util.HashMap;
import java.util.Map;

import com.leavay.common.util.ToolUtilities;

import bap.cells.CellHookIntf;
import cell.CellIntf;

public class BapTesterHook implements CellHookIntf
{
    static HashMap<String, Long> _ratio = new HashMap();
    static synchronized void record(Object cell, String func)
    {
        String key = cell.getClass().getName()+"::"+func;
        Long l = _ratio.get(key);
        if (l == null)
            l=0L;
        _ratio.put(key, l+1);
    }
    
    public static synchronized Map<String, Long> getRatio()
    {
        return new HashMap<String, Long>(_ratio);
    }
    
    public static synchronized void reset()
    {
        _ratio.clear();
    }
    
    public void onAction(ENTRY entry, long id, CellIntf cell, String func, Object... params)
    {
        record(cell, func+"()");
    }
    
    public static String toLogString()
    {
        return ToolUtilities.logString(getRatio(), true);
    }
}
