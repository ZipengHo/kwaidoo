package cmn.dto.scheduletask.timerule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.ToolUtilities;

public class TimeRuleOr extends TimeRuleGroup
{
	private static final long serialVersionUID = 7671470082966936961L;

	public TimeRuleOr(List<TimeRule> listRule)
    {
        super(listRule);
    }
    
    public TimeRuleOr(TimeRule ... rules)
    {
        super(rules);
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        if (ToolUtilities.isObjectEmpty(_listRules))
            return MATCH;
        
        // 如果全都不match，那么需要一个最低级别的code，从而避免大步进把某些可能满足的给跳过了
        int minErrorCode = -100;
        for (TimeRule rule : _listRules)
        {
            int errCode =  rule.isMatch(calOptime, isSearchNext);
            if (MATCH == errCode)
                return MATCH;
            else
                minErrorCode = Math.max(errCode, minErrorCode);
        }
        
        return minErrorCode;
    }

    public Element saveXML() throws Exception
    {
        if (ToolUtilities.isObjectEmpty(_listRules))
            return null;
        DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_OR);
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
        return XML_OR;
    }
    
 // 浅copy，没有实现递归clone
    public Object clone()
    {
        TimeRuleOr newO = new TimeRuleOr();
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
