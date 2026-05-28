package cmn.util;

import java.util.ArrayList;

import com.kwaidoo.ms.tool.ToolUtilities;

public class NodePath<T> extends ArrayList<T>
{
    public NodePath(T ... data)
    {
        if (ToolUtilities.isArrayEmpty(data))
            return;
        
        for (T d : data)
            add(d);
    }
    
    public T getFirst()
    {
        return ToolUtilities.isObjectEmpty(this)?null:get(0);
    }
    
    public T getLast()
    {
        return ToolUtilities.isObjectEmpty(this)?null:get(size()-1);
    }
    
    public boolean endWith(NodePath<T> other)
    {
        if (ToolUtilities.isObjectEmpty(other))
            return true;
        
        for (int i=other.size()-1; i>=0; i--)
        {
            // 自身还没有别人长，返回false
            if (i >= size())
                return false;
            
            int j = other.size()-i;
            int m = size() - j;
            
            T o = other.get(i);
            T this1 = get(m);
            if (!o.equals(this1))
                return false;
        }
        
        return true;
    }
}
