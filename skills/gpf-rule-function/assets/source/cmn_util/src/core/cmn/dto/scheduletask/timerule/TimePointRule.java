package cmn.dto.scheduletask.timerule;

import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.ToolUtilities;
/**
 * @What: 时间点规则
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月12日
 * @Version: 1.0
 */
public class TimePointRule implements TimeRule
{

    private static final long serialVersionUID = 2001765882784793248L;
    
    public final static int TIME_OPERATOR_GREATER = 1;
    public final static int TIME_OPERATOR_GREATER_EQUAL = 2;
    public final static int TIME_OPERATOR_EQUAL = 3;
    public final static int TIME_OPERATOR_LESS = 4;
    public final static int TIME_OPERATOR_LESS_EQUAL = 5;
    public final static int TIME_OPERATOR_NOT_EQUAL = 6;
    
    
    long stdTime = 0;
    boolean ignoreDate = false;
    int operator = TIME_OPERATOR_GREATER_EQUAL;
    
            
    public final static String RULE_STD_TIME = "StdTime";
    public final static String RULE_IGNORE_DATE = "IgnoreDate";
    public final static String RULE_OPERATOR = "Operator";

    public TimePointRule()
    {
        setStdTime(System.currentTimeMillis());
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        calOptime = (Calendar) calOptime.clone();
        calOptime.set(Calendar.MILLISECOND, 0);
        calOptime.set(Calendar.SECOND, 0);
        if (isIgnoreDate())
        {
            // 若忽略日期，则强行统一日期后再比对
            Calendar calStd = ToolUtilities.time2Calendar(getStdTime());
            calOptime.set(Calendar.YEAR, calStd.get(Calendar.YEAR));
            calOptime.set(Calendar.MONTH, calStd.get(Calendar.MONTH));
            calOptime.set(Calendar.DAY_OF_MONTH, calStd.get(Calendar.DAY_OF_MONTH));
        }
        long opTime = calOptime.getTimeInMillis();
        Calendar calStd = ToolUtilities.time2Calendar(getStdTime());

        boolean match = false;
        
        if (isIgnoreDate())
        {
            switch (getOperator())
            {
            case TIME_OPERATOR_EQUAL:
                match = opTime == getStdTime();
                break;
            case TIME_OPERATOR_GREATER:
                match = opTime > getStdTime();
                break;
            case TIME_OPERATOR_GREATER_EQUAL:
                match = opTime >= getStdTime();
                break;
            case TIME_OPERATOR_LESS:
                match = opTime < getStdTime();
                break;
            case TIME_OPERATOR_LESS_EQUAL:
                match = opTime <= getStdTime();
                break;
            case TIME_OPERATOR_NOT_EQUAL:
                match = opTime != getStdTime();
                break;
            }
        }else
        {
        switch (getOperator())
            {
            case TIME_OPERATOR_EQUAL:
                if (isSearchNext && opTime > getStdTime())
                    return OVERFLOW_UNMATCH_MAX;
                if (!isSearchNext && opTime < getStdTime())
                    return OVERFLOW_UNMATCH_MIN;

                match = opTime == getStdTime();
                break;
            case TIME_OPERATOR_GREATER:
                if (!isSearchNext && opTime < getStdTime())
                    return OVERFLOW_UNMATCH_MIN;

                match = opTime > getStdTime();
                break;
            case TIME_OPERATOR_GREATER_EQUAL:
                if (!isSearchNext && opTime < getStdTime())
                    return OVERFLOW_UNMATCH_MIN;

                match = opTime >= getStdTime();
                break;
            case TIME_OPERATOR_LESS:
                if (isSearchNext && opTime > getStdTime())
                    return OVERFLOW_UNMATCH_MAX;

                match = opTime < getStdTime();
                break;
            case TIME_OPERATOR_LESS_EQUAL:
                if (isSearchNext && opTime > getStdTime())
                    return OVERFLOW_UNMATCH_MAX;

                match = opTime <= getStdTime();
                break;
            case TIME_OPERATOR_NOT_EQUAL:
                match = opTime != getStdTime();
                break;
            }
        }

        if (match)
            return MATCH;
        else 
        {
            // 不匹配的时候要分析是年月日时哪个不匹配，争取跨大步跳格
            int opYear = calOptime.get(Calendar.YEAR);
            int stdYear = calStd.get(Calendar.YEAR);
            
            int opMoth = calOptime.get(Calendar.MONTH);
            int stdMoth = calStd.get(Calendar.MONTH);
            
            int opDay = calOptime.get(Calendar.DAY_OF_MONTH);
            int stdDay = calStd.get(Calendar.DAY_OF_MONTH);
            
            int opHour = calOptime.get(Calendar.HOUR_OF_DAY);
            int stdHour = calStd.get(Calendar.HOUR_OF_DAY);
            
            switch(getOperator())
            {
            case TIME_OPERATOR_EQUAL:
                if (opYear != stdYear)
                    return YEAR_UNMATCH;
                break;
            case TIME_OPERATOR_GREATER:
            case TIME_OPERATOR_GREATER_EQUAL:
            case TIME_OPERATOR_LESS:
            case TIME_OPERATOR_LESS_EQUAL:
                if (isSearchNext)
                {
                    if (opYear < stdYear)
                        return YEAR_UNMATCH;
                    else if (opMoth < stdMoth)
                        return MONTH_UNMATCH;
                    else if (opDay < stdDay)
                        return DAY_UNMATCH;
                    else if (opHour < stdHour)
                        return HOUR_UNMATCH;
                }else
                {
                    if (opYear > stdYear)
                        return YEAR_UNMATCH;
                    else if (opMoth > stdMoth)
                        return MONTH_UNMATCH;
                    else if (opDay > stdDay)
                        return DAY_UNMATCH;
                    else if (opHour > stdHour)
                        return HOUR_UNMATCH;
                }
                break;
            }
            return OTHER_UNMATCH;
        }
    }
    
    public int compaireField(int field, Calendar calOptime, Calendar stdTime)
    {
        int optimeF = calOptime.get(field);
        int optimeSTD = stdTime.get(field);

        return Integer.compare(optimeF, optimeSTD);
    }
    
    public Element saveXML() throws Exception
    {
        DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_RULE_TIME_POINT);
        ele.addAttribute(RULE_STD_TIME, ""+stdTime);
        ele.addAttribute(RULE_IGNORE_DATE, ""+ignoreDate);
        ele.addAttribute(RULE_OPERATOR, ""+getOperator());
        return ele;
    }

    public void loadFromXml(Element ele) throws Exception
    {
        if (ele == null)
            return;
        
        String sV = ele.attributeValue(RULE_STD_TIME);
        stdTime = ToolUtilities.getLong(sV);
        
        sV = ele.attributeValue(RULE_IGNORE_DATE);
        ignoreDate = ToolUtilities.getBoolean(sV, true);
        
        sV = ele.attributeValue(RULE_OPERATOR);
        operator = ToolUtilities.getInteger(sV);
    }

    public static String getOperatorText(int op)
    {
        switch (op)
        {
        case TIME_OPERATOR_EQUAL:
            return "=";
        case TIME_OPERATOR_GREATER:
            return ">";
        case TIME_OPERATOR_GREATER_EQUAL:
            return ">=";
        case TIME_OPERATOR_LESS:
            return "<";
        case TIME_OPERATOR_LESS_EQUAL:
            return "<=";
        case TIME_OPERATOR_NOT_EQUAL:
            return "!=";
        }
        return "";
    }
    
    public String getName()
    {
        String sTime = "";
        if (isIgnoreDate())
            sTime = ToolUtilities.time2String(getStdTime(), false, true);
        else
            sTime = ToolUtilities.time2String(getStdTime(), false);

        String sOp = "";
     
        return getOperatorText(getOperator())+" " + sTime;
    }

    public Object clone()
    {
        TimePointRule newO = new TimePointRule();
        try
        {
            newO.loadFromXml(saveXML());
            return newO;
        } catch (Exception exp)
        {
            throw new RuntimeException(exp);
        }
    }

    public long getStdTime()
    {
        return stdTime;
    }

    public void setStdTime(long stdTime)
    {
        Calendar cal = ToolUtilities.time2Calendar(stdTime);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        this.stdTime = cal.getTimeInMillis();
    }

    public int getOperator()
    {
        return operator;
    }

    public void setOperator(int operator)
    {
        this.operator = operator;
    }
    
    public boolean isIgnoreDate()
    {
        return ignoreDate;
    }

    public void setIgnoreDate(boolean ignoreDate)
    {
        this.ignoreDate = ignoreDate;
    }

    public static void main(String[] args) throws Exception
    {
        TimePointRule rule = new TimePointRule();
//        rule.setIgnoreDate(true);
        long curTime = System.currentTimeMillis();
        System.out.println("CURRENT-TIME : " + ToolUtilities.time2String(curTime));
        
        
        rule.setStdTime(ToolUtilities.time2Calendar(1988, 2, 8, 17, 30, 0).getTimeInMillis());
        rule.setOperator(TIME_OPERATOR_LESS_EQUAL);

//        rule.setStdTime(ToolUtilities.time2Calendar(3988, 2, 8, 17, 30, 0).getTimeInMillis());
//        rule.setOperator(TIME_OPERATOR_GREATER_EQUAL);

        System.out.println("STD-TIME : " + ToolUtilities.time2String(rule.getStdTime()));
        System.out.println("==============================================");
        
        List<Long> lstTime = null;
        
        
        lstTime = TimeRuleUtil.calcLastOpTimeList(rule, curTime, 40);
        for (int i = lstTime.size()-1; i>=0; i--)
        {
            Long l = lstTime.get(i);
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        System.out.println("------------------------------------------------------------------------------");

        lstTime = TimeRuleUtil.calcNextOpTimeList(rule, curTime, 20);
        for (Long l : lstTime)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
    }

    public void preLoad(Calendar calOptime, boolean isSearchNext)
    {
        
    }

    public boolean isSecondLevel()
    {
        return false;
    }
}
