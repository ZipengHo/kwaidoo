package cmn.dto.scheduletask.timerule;

import java.util.ArrayList;
import java.util.List;

import com.leavay.common.util.ToolUtilities;


public abstract class TimeRuleGroup implements TimeRule
{
    /**
     * 
     */
    private static final long serialVersionUID = 464170404562369364L;
    public List<TimeRule> _listRules;
    
    public TimeRuleGroup(List<TimeRule> listRule)
    {
        _listRules = listRule;
    }
    
    public TimeRuleGroup(TimeRule ... rules)
    {
        _listRules = ToolUtilities.array2List(rules);
    }
    
    public void setChildRules(List<TimeRule> listRule)
    {
        _listRules = listRule;
    }
    
    public List<TimeRule> getChildRules()
    {
        return _listRules;
    }
    
    public boolean hasChildRule()
    {
        return !ToolUtilities.isObjectEmpty(_listRules);
    }
    
    public void addChildRule(TimeRule childRule)
    {
        if (_listRules == null)
            _listRules = new ArrayList<TimeRule>();
        
        _listRules.add(childRule);
    }
    
    public abstract Object clone();
}
