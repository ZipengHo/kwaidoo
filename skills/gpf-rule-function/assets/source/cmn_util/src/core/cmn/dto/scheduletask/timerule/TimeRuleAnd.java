package cmn.dto.scheduletask.timerule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.ToolUtilities;

public class TimeRuleAnd extends TimeRuleGroup
{
	private static final long serialVersionUID = 6871358274043312695L;

	public TimeRuleAnd(List<TimeRule> listRule)
    {
        super(listRule);
    }
    
    public TimeRuleAnd(TimeRule ... rules)
    {
        super(rules);
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        if (ToolUtilities.isObjectEmpty(_listRules))
            return MATCH;
        
        for (TimeRule rule : _listRules)
        {
            int iRes = rule.isMatch(calOptime, isSearchNext);
            if (MATCH != iRes)
                return iRes;
        }
        
        // 如果有多个不match，那么需要找一个最高级别的code，从而避免老是碎步前进
        boolean hasFailed =false;
        int maxErrorCode = OTHER_UNMATCH;
        for (TimeRule rule : _listRules)
        {
            int errCode =  rule.isMatch(calOptime, isSearchNext);
            if (MATCH != errCode)
            {
                hasFailed = true;
                maxErrorCode = Math.min(errCode, maxErrorCode);
            }
        }
        
        if (hasFailed)
            return maxErrorCode;
        else
            return MATCH;
    }
    
    public Element saveXML() throws Exception
    {
        if (ToolUtilities.isObjectEmpty(_listRules))
            return null;
        
        DocumentFactory doc = DocumentFactory.getInstance();
        
        Element ele = doc.createElement(XML_AND);
        for (TimeRule rule : _listRules)
        {
            ele.add(rule.saveXML());
        }
        return ele;
    }
    
    public void loadFromXml(Element ele) throws Exception
    {
        _listRules = TimeRuleUtil.parseChild(ele);
    }
    
    public String toString()
    {
        try
        {
            return ToolUtilities.xmlElement2String(saveXML());
        } catch (Exception exp)
        {
            return "Unknown : " + exp.getMessage();
        }
    }
    
    public String getName()
    {
        return XML_AND;
    }
    
    // 浅copy，没有实现递归clone
    public Object clone()
    {
        TimeRuleAnd newO = new TimeRuleAnd();
        if (_listRules != null)
            newO._listRules = new ArrayList<TimeRule>(_listRules);
        
        return newO;
    }

    public void preLoad(Calendar calOptime, boolean isSearchNext)
    {
    	if (!ToolUtilities.isObjectEmpty(_listRules))
            for (TimeRule rule : _listRules)
            {
            	rule.preLoad(calOptime, isSearchNext);
            }
    }

    public boolean isSecondLevel()
    {
        if (!ToolUtilities.isObjectEmpty(_listRules))
            for (TimeRule rule : _listRules)
            {
                if (rule.isSecondLevel())
                    return true;
            }
        return false;
    }
}
