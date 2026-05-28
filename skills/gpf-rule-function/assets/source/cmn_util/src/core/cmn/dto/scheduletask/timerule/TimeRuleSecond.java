package cmn.dto.scheduletask.timerule;

import java.util.Calendar;
import java.util.List;

import org.dom4j.Element;

import com.leavay.common.util.SrvRes;
import com.leavay.common.util.ToolUtilities;

public class TimeRuleSecond extends FlowScheduleRule
{
    public List _second;
    
    public void setValue(String ruleKey, Object ... values)
    {
        if (ruleKey.equals(RULE_SECOND))
        {
            List<?> lst = ToolUtilities.array2List(values);
            _second = lst;
        }
        else
            super.setValue(ruleKey, values);
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        int res = super.isMatch(calOptime, isSearchNext);
        if (res != MATCH)
            return res;
        
        if (!normalVerify(calOptime, Calendar.SECOND, _second))
            return SECOND_UNMATCH;
        
        return MATCH;
    }
    
    public Element saveXML()
    {
        Element ele = super.saveXML();
        if (!ToolUtilities.isObjectEmpty(_second))
            ele.addAttribute(RULE_SECOND, list2String(_second));
        
        return ele;
    }
    
    public void loadFromXml(Element ele) throws Exception
    {
        if (ele == null)
            return;
        
        super.loadFromXml(ele);
        
        String sV = ele.attributeValue(RULE_SECOND);
        _second = string2list(sV);
    }

    public String getPureName()
    {
        String sRule = super.getPureName();
        if (!ToolUtilities.isObjectEmpty(_second))
            sRule += " " + "(" +list2String(_second) +")"+ SrvRes.getString("Second");
        
        return sRule;
    }

    public boolean isSecondLevel()
    {
        return true;
    }
}
