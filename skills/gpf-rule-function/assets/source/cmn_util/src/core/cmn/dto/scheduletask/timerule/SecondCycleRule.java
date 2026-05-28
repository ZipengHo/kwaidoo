package cmn.dto.scheduletask.timerule;

import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.ToolUtilities;
/**
 * @What: 秒级规则
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月12日
 * @Version: 1.0
 */
public class SecondCycleRule implements TimeRule
{

    private static final long serialVersionUID = 2001765882784793248L;
    
    long stdTime = 0;
    boolean ignoreDate =true;
    int periodicSecond = 30;
    

    public final static String RULE_STD_TIME = "StdTime";
    public final static String RULE_IGNORE_DATE = "IgnoreDate";
    public final static String RULE_PERIODIC_SECOND= "PeriodicSecond";

    public SecondCycleRule()
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
            int periodicMilSecond = periodicSecond*1000;
            if ((time-tempStdTime)%periodicMilSecond == 0)
                return MATCH;
            else
                return SECOND_UNMATCH;
        }else
        {
            long time = calOptime.getTimeInMillis();
            int periodicMilSecond = periodicSecond*1000;
            if ((time-stdTime)%periodicMilSecond == 0)
                return MATCH;
            else
                return SECOND_UNMATCH;
        }
    }

    public Element saveXML() throws Exception
    {
        DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_RULE_SECOND_CYCLE);
        ele.addAttribute(RULE_STD_TIME, ""+stdTime);
        ele.addAttribute(RULE_IGNORE_DATE, ""+ignoreDate);
        ele.addAttribute(RULE_PERIODIC_SECOND, ""+periodicSecond);
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
        
        sV = ele.attributeValue(RULE_PERIODIC_SECOND);
        periodicSecond = ToolUtilities.getInteger(sV);
    }

    public String getName()
    {
        String sTime = "";
        if (isIgnoreDate())
            sTime = ToolUtilities.time2String(getStdTime(), false, true);
        else
            sTime = ToolUtilities.time2String(getStdTime(), false);
        return sTime+" (/"+periodicSecond+"s)";
    }

    public Object clone()
    {
        SecondCycleRule newO = new SecondCycleRule();
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

    public int getPeriodicSecond()
    {
        return periodicSecond;
    }

    public void setPeriodicSecond(int periodicSecond)
    {
        this.periodicSecond = periodicSecond;
    }
    
    public static void main(String[] args) throws Exception
    {
        SecondCycleRule rule = new SecondCycleRule();
        
        long curTime = System.currentTimeMillis();
        System.out.println("CURRENT-TIME : " + ToolUtilities.time2String(curTime));
        
        rule.setStdTime(curTime);
        rule.setPeriodicSecond(15);

        System.out.println("STD-TIME : " + ToolUtilities.time2String(rule.getStdTime()));
        
        List<Long> lstTime = TimeRuleUtil.calcLastOpTimeList(rule, curTime, 20);
        for (int i=lstTime.size()-1; i>=0; i--)
        {
            Long L = lstTime.get(i);
            Calendar cal = ToolUtilities.time2Calendar(L);
            String s = ToolUtilities.time2String(L, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        System.out.println("==============================================");

        List<Long> lstTime2 = TimeRuleUtil.calcNextOpTimeList(rule, curTime, 20);
        for (Long l : lstTime2)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        
        
    }

    public void preLoad(Calendar calOptime, boolean isSearchNext)
    {
        // TODO Auto-generated method stub
        
    }

    public boolean isSecondLevel()
    {
        return true;
    }
}
