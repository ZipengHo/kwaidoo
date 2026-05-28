package cmn.dto.scheduletask.timerule;

import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.ToolUtilities;
/**
 * @What: 循环间隔规则
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月12日
 * @Version: 1.0
 */
public class TimeCycleRule implements TimeRule
{

    private static final long serialVersionUID = 2001765882784793248L;
    
    long stdTime = 0;
    boolean ignoreDate =true;
    int periodicMin = 1;
    

    public final static String RULE_STD_TIME = "StdTime";
    public final static String RULE_IGNORE_DATE = "IgnoreDate";
    public final static String RULE_PERIODIC_MIN = "PeriodicMin";

    public TimeCycleRule()
    {
        setStdTime(System.currentTimeMillis());
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        if (isIgnoreDate())
        {
            // 将基准时间的日期统一后再进行比对
            Calendar calStd = ToolUtilities.time2Calendar(getStdTime());
            calStd.set(Calendar.YEAR, calOptime.get(Calendar.YEAR));
            calStd.set(Calendar.MONTH, calOptime.get(Calendar.MONTH));
            calStd.set(Calendar.DAY_OF_MONTH, calOptime.get(Calendar.DAY_OF_MONTH));
            
            long time = calOptime.getTimeInMillis();
            long tempStdTime = calStd.getTimeInMillis();
            int periodicMilSecond = periodicMin*60*1000;
            if ((time-tempStdTime)%periodicMilSecond == 0)
                return MATCH;
            else
                return MINUTE_UNMATCH;
        }else
        {
            long time = calOptime.getTimeInMillis();
            int periodicMilSecond = periodicMin*60*1000;
            if ((time-stdTime)%periodicMilSecond == 0)
                return MATCH;
            else
                return MINUTE_UNMATCH;
        }
    }

    public Element saveXML() throws Exception
    {
        DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_RULE_CYCLE);
        ele.addAttribute(RULE_STD_TIME, ""+stdTime);
        ele.addAttribute(RULE_IGNORE_DATE, ""+ignoreDate);
        ele.addAttribute(RULE_PERIODIC_MIN, ""+periodicMin);
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
        
        sV = ele.attributeValue(RULE_PERIODIC_MIN);
        periodicMin = ToolUtilities.getInteger(sV);
    }

    public String getName()
    {
        String sTime = "";
        if (isIgnoreDate())
            sTime = ToolUtilities.time2String(getStdTime(), false, true);
        else
            sTime = ToolUtilities.time2String(getStdTime(), false);
        return sTime+" (/"+periodicMin+"min)";
    }

    public Object clone()
    {
        TimeCycleRule newO = new TimeCycleRule();
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

    public boolean isIgnoreDate()
    {
        return ignoreDate;
    }

    public void setIgnoreDate(boolean ignoreDate)
    {
        this.ignoreDate = ignoreDate;
    }

    public int getPeriodicMin()
    {
        return periodicMin;
    }

    public void setPeriodicMin(int periodicSecond)
    {
        this.periodicMin = periodicSecond;
    }
    
    public static void main(String[] args) throws Exception
    {
        TimeCycleRule rule = new TimeCycleRule();
        
        long curTime = System.currentTimeMillis();
        System.out.println("CURRENT-TIME : " + ToolUtilities.time2String(curTime));
        
        rule.setStdTime(curTime);
        rule.setPeriodicMin(7*60);

        System.out.println("STD-TIME : " + ToolUtilities.time2String(rule.getStdTime()));
        
        List<Long> lstTime = TimeRuleUtil.calcLastOpTimeList(rule, curTime, 20);
        for (Long l : lstTime)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        System.out.println("==============================================");

        lstTime = TimeRuleUtil.calcNextOpTimeList(rule, curTime, 20);
        for (Long l : lstTime)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        
        
    }

    @Override
    public void preLoad(Calendar calOptime, boolean isSearchNext)
    {
        // TODO Auto-generated method stub
        
    }

    public boolean isSecondLevel()
    {
        return false;
    }
}
