package cmn.dto.scheduletask.timerule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dom4j.Element;
import org.mvel2.OpTime;

import com.leavay.common.util.TimeoutException;
import com.leavay.common.util.ToolUtilities;
import com.leavay.common.util.javac.ClassFactory;

public class TimeRuleUtil
{
    public static TimeRule  parseXML(String ruleXml) throws Exception
    {
        if (ToolUtilities.isStringEmpty(ruleXml))
            return null;
        
        Element ele = ToolUtilities.xmlString2Element(ruleXml);
        return parseXML(ele);
    }
    
    public static String saveXML(TimeRule rule) throws Exception
    {
        if (rule == null)
            return "";

        Element ele =  rule.saveXML();
        if (ele == null)
            return "";
        
        return ToolUtilities.xmlElement2String(ele);
    }
    
    public static TimeRule  parseXML(Element ele) throws Exception
    {
        TimeRule rule = null;
        if (ele.getName().equals(TimeRule.XML_AND))
            rule = new TimeRuleAnd();
        else if (ele.getName().equals(TimeRule.XML_OR))
            rule = new TimeRuleOr();
        else if (ele.getName().equals(TimeRule.XML_RULE))
            rule = new FlowScheduleRule();
        else if (ele.getName().equals(TimeRule.XML_RULE_TIME_POINT))
            rule = new TimePointRule();
        else if (ele.getName().equals(TimeRule.XML_RULE_CYCLE))
            rule = new TimeCycleRule();
        else if (ele.getName().equals(TimeRule.XML_RULE_SECOND_CYCLE))
            rule = new SecondCycleRule();
        else if (ele.getName().equals(TimeRule.XML_RULE_USER_DEFINE)) {
//            rule = new CfgTriggerTimeRule();
        	String ruleClass = ele.attributeValue(TimeRule.RULE_CLASS);
        	try {
        		Class<? extends TimeRule> ruleClazz = (Class<? extends TimeRule>) ClassFactory.getValidClassLoader().loadClass(ruleClass);
        		rule = ruleClazz.newInstance();
        	}catch (Exception e) {
        		e.printStackTrace();
        		return null;
			}
        }
        else
            return null;

        rule.loadFromXml(ele);
        return rule;
    }
    
    @SuppressWarnings("unchecked")
	public static List<TimeRule> parseChild(Element eleParent) throws Exception
    {
        List<TimeRule> lstRet = new ArrayList<TimeRule>();
        List<Element> lst = eleParent.elements();
        if (lst != null) for (Object o : lst)
        {
            if (!(o instanceof Element))
                continue;
            
            TimeRule rule = parseXML((Element)o);
            if (rule != null)
                lstRet.add(rule);
        }
        return lstRet;
    }
    
    public static OpTime calcNextOneOpTime(TimeRule rule, long refTime) throws Exception
    {
        List<Long> opTimeList = calcNextOpTimeList(rule, refTime, 1);
        if (ToolUtilities.isObjectEmpty(opTimeList))
            return null;
        
        return new OpTime(opTimeList.get(0));
    }
    

    public static List<Long> calcNextOpTimeList(TimeRule rule, long refTime, int size) throws Exception
    {
        return calcNextOpTimeList(rule, refTime, size, 10000);
    }
    
    // 计算给定时间以后的若干opTime时间点
    public static List<Long> calcNextOpTimeList(TimeRule rule, long refTime, int size, long timeOut) throws Exception
    {
        if (rule == null)
            return null;
        
        if (rule.isSecondLevel())
            return calcNextOpTimeList_Second(rule, refTime, size, timeOut);
        
        List<Long> lst = new ArrayList<Long>();
        Calendar cal = ToolUtilities.time2Calendar(refTime);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MINUTE, 1); // 算Next就需要加这个整分钟
        
        int iPos = 0;
        long lTimeOut = System.currentTimeMillis() + timeOut;
        
        // 调用预装载，主要用于用户自定义规则
        rule.preLoad(cal, true);
        
        while (iPos < size)
        {
            if (System.currentTimeMillis() > lTimeOut)
            {
                if (ToolUtilities.isObjectEmpty(lst))
                    throw new TimeoutException();
                else
                    return lst;
            }
            
//            System.out.println("Check : " + ToolUtilities.time2String(cal.getTimeInMillis()));
            int result = rule.isMatch(cal, true);
            if (TimeRule.MATCH == result)
            {
                // 匹配的留下，记录，并跳到下一分钟继续查找
                lst.add(cal.getTimeInMillis());
                iPos ++;
                cal.add(Calendar.MINUTE, 1);
            }else if (TimeRule.OVERFLOW_UNMATCH_MAX == result)
            {
                // 年越界以后，就不可能再有匹配的了，防止死循环
                break;
            }
            else if (TimeRule.YEAR_UNMATCH == result)
            {
                // 跳到来年一月一号0点继续找
                cal.add(Calendar.YEAR, 1);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            else if (TimeRule.MONTH_UNMATCH == result)
            {
                //跳到下月的1号0点
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            else if (TimeRule.DAY_UNMATCH == result)
            {
                // 跳到第二天的0点
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            else if (TimeRule.HOUR_UNMATCH == result)
            {
                // 跳到下一个小时的0分
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
            }
            else
                cal.add(Calendar.MINUTE, 1);
        }
        return lst;
    }
    
    public static List<Long> calcNextOpTimeList_Second(TimeRule rule, long refTime, int size, long timeOut) throws Exception
    {
        if (rule == null)
            return null;
        
        List<Long> lst = new ArrayList<Long>();
        Calendar cal = ToolUtilities.time2Calendar(refTime);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.SECOND, 1); // 算Next就需要后跳1秒
        
        int iPos = 0;
        long lTimeOut = System.currentTimeMillis() + timeOut;
        
        // 调用预装载，主要用于用户自定义规则
        rule.preLoad(cal, true);
        
        while (iPos < size)
        {
            if (System.currentTimeMillis() > lTimeOut)
            {
                if (ToolUtilities.isObjectEmpty(lst))
                    throw new TimeoutException();
                else
                    return lst;
            }
            
//            System.out.println("Check : " + ToolUtilities.time2String(cal.getTimeInMillis()));
            int result = rule.isMatch(cal, true);
            if (TimeRule.MATCH == result)
            {
                // 匹配的留下，记录，并跳到下一秒继续查找
                lst.add(cal.getTimeInMillis());
                iPos ++;
                cal.add(Calendar.SECOND, 1);
            }else if (TimeRule.OVERFLOW_UNMATCH_MAX == result)
            {
                // 年越界以后，就不可能再有匹配的了，防止死循环
                break;
            }
            else if (TimeRule.YEAR_UNMATCH == result)
            {
                // 跳到来年一月一号0点继续找
                cal.add(Calendar.YEAR, 1);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }
            else if (TimeRule.MONTH_UNMATCH == result)
            {
                //跳到下月的1号0点
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }
            else if (TimeRule.DAY_UNMATCH == result)
            {
                // 跳到第二天的0点
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }
            else if (TimeRule.HOUR_UNMATCH == result)
            {
                // 跳到下一个小时的0分
                cal.add(Calendar.HOUR_OF_DAY, 1);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
            }else if (TimeRule.MINUTE_UNMATCH == result)
            {
                // 跳到下一个分钟的0秒
                cal.add(Calendar.MINUTE, 1);
                cal.set(Calendar.SECOND, 0);
            }
            else
            {
                // 跳到下一秒
                cal.add(Calendar.SECOND, 1);
            }
        }
        return lst;
    }
    

    public static OpTime calcLastOneOpTime(TimeRule rule, long refTime) throws Exception
    {
        List<Long> opTimeList = calcLastOpTimeList(rule, refTime, 1);
        if (ToolUtilities.isObjectEmpty(opTimeList))
            return null;
        
        return new OpTime(opTimeList.get(0));
    }

    public static List<Long> calcLastOpTimeList(TimeRule rule, long refTime, int size) throws Exception
    {
        return calcLastOpTimeList(rule, refTime, size, 10000);
    }
    
 // 计算给定时间以前的若干opTime时间点
    public static List<Long> calcLastOpTimeList(TimeRule rule, long refTime, int size, long timeout) throws Exception
    {
        if (rule == null)
            return null;
        
        if (rule.isSecondLevel())
            return calcLastOpTimeList_Second(rule, refTime, size, timeout);
        
        List<Long> lst = new ArrayList<Long>();
        Calendar cal = ToolUtilities.time2Calendar(refTime);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        
        int iPos = 0;
        long lTimeOut = System.currentTimeMillis() + timeout;
        
        // 调用预装载
        rule.preLoad(cal, false);
        
        while (iPos < size)
        {
            if (System.currentTimeMillis() > lTimeOut)
            {
                if (ToolUtilities.isObjectEmpty(lst))
                    throw new TimeoutException();
                else
                    return lst;
            }
            
//            System.out.println("Check : " + ToolUtilities.time2String(cal.getTimeInMillis()));
            int result = rule.isMatch(cal, false);
            if (TimeRule.MATCH == result)
            {
                // 匹配的留下，记录，并跳到下一分钟继续查找
                long lV = cal.getTimeInMillis();
                if (lV < 0 )
                    break;
                
                lst.add(lV);
                iPos ++;
                cal.add(Calendar.MINUTE, -1);
            }else if (TimeRule.OVERFLOW_UNMATCH_MIN == result)
            {
                // 年越界以后，就不可能再有匹配的了，防止死循环
                break;
            }
            else if (TimeRule.YEAR_UNMATCH == result)
            {
                // 跳到去年年尾最后一天最后一小时最后一分钟
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                
                // 从今年年初第一天0点再往前跳一分钟就是去年最后一分钟了
                cal.add(Calendar.MINUTE, -1);
            }
            else if (TimeRule.MONTH_UNMATCH == result)
            {
                //跳到下月的1号0点
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                
                //跳到本月初，再往前一分钟
                cal.add(Calendar.MINUTE, -1);
            }
            else if (TimeRule.DAY_UNMATCH == result)
            {
                // 跳到第二天的0点
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);

                //跳到今天0点，再往前一分钟就是昨天
                cal.add(Calendar.MINUTE, -1);
            }
            else if (TimeRule.HOUR_UNMATCH == result)
            {
                // 跳到下一个小时的0分
                cal.set(Calendar.MINUTE, 0);

                //跳到本小时0分，再往前一分钟就是上一小时59分
                cal.add(Calendar.MINUTE, -1);
            }
            else
                cal.add(Calendar.MINUTE, -1);
        }
        return lst;
    }
    
    public static List<Long> calcLastOpTimeList_Second(TimeRule rule, long refTime, int size, long timeout) throws Exception
    {
        if (rule == null)
            return null;
        
        List<Long> lst = new ArrayList<Long>();
        Calendar cal = ToolUtilities.time2Calendar(refTime);
        cal.set(Calendar.MILLISECOND, 0);
        
        int iPos = 0;
        long lTimeOut = System.currentTimeMillis() + timeout;
        
        // 调用预装载
        rule.preLoad(cal, false);
        
        while (iPos < size)
        {
            if (System.currentTimeMillis() > lTimeOut)
            {
                if (ToolUtilities.isObjectEmpty(lst))
                    throw new TimeoutException();
                else
                    return lst;
            }
            
//            System.out.println("Check : " + ToolUtilities.time2String(cal.getTimeInMillis()));
            int result = rule.isMatch(cal, false);
            if (TimeRule.MATCH == result)
            {
                // 匹配的留下，记录，并跳到上一秒继续查找
                long lV = cal.getTimeInMillis();
                if (lV < 0 )
                    break;
                
                lst.add(lV);
                iPos ++;
                cal.add(Calendar.SECOND, -1);
            }else if (TimeRule.OVERFLOW_UNMATCH_MIN == result)
            {
                // 年越界以后，就不可能再有匹配的了，防止死循环
                break;
            }
            else if (TimeRule.YEAR_UNMATCH == result)
            {
                // 跳到去年年尾最后一天最后一小时最后一分钟
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                
                // 从今年年初第一天0点再往前跳一分钟就是去年最后一分钟的最后一秒
                cal.add(Calendar.SECOND, -1);
            }
            else if (TimeRule.MONTH_UNMATCH == result)
            {
                //跳到下月的1号0点
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                
                //跳到本月初，再往前一秒
                cal.add(Calendar.SECOND, -1);
            }
            else if (TimeRule.DAY_UNMATCH == result)
            {
                // 跳到第二天的0点
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                //跳到今天0点，再往前一秒就是昨天23点59分59秒
                cal.add(Calendar.SECOND, -1);
            }
            else if (TimeRule.HOUR_UNMATCH == result)
            {
                // 跳到下一个小时的0分
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                //跳到本小时0分，再往前一秒就是上一小时59分59秒
                cal.add(Calendar.SECOND, -1);
            }
            else if (TimeRule.MINUTE_UNMATCH == result)
            {
                cal.set(Calendar.SECOND, 0);
                
                //跳到本小时0分，再往前一分钟就是上一小时59分
                cal.add(Calendar.MINUTE, -1);
            }
            else
                cal.add(Calendar.SECOND, -1);
        }
        
        return lst;
    }
    
    @SuppressWarnings("unused")
	public static void main(String[] args) throws Exception
    {
        FlowScheduleRule rule1 = new FlowScheduleRule();
        rule1.setValue(TimeRule.RULE_YEAR, "2008-2013");
        rule1.setValue(TimeRule.RULE_MONTH, 9,11);
        rule1.setValue(TimeRule.RULE_DAY, 5,6);
        rule1.setValue(TimeRule.RULE_HOUR, 2,"7-9", 12, "18-20");
        rule1.setValue(TimeRule.RULE_MINUTE, 0, "/3");
        

        FlowScheduleRule rule2 = new FlowScheduleRule();
        rule2.setValue(TimeRule.RULE_YEAR, 2012, 2013,2014);
        rule2.setValue(TimeRule.RULE_MONTH, 1);
        rule2.setValue(TimeRule.RULE_DAY, 1);
        rule2.setValue(TimeRule.RULE_HOUR, 7, "/11");
        rule2.setValue(TimeRule.RULE_MINUTE, "/15");
        
        FlowScheduleRule rule3 = new FlowScheduleRule();
//        rule3.setValue(RULE_MINUTE, "/3","/5");
        rule3.setValue(TimeRule.RULE_DAY_OF_WEEK, "3-4");
        
        TimeRule rule4 = new TimeRuleOr();
        
        TimeRule rule = new TimeRuleAnd(rule3, new TimeRuleOr(rule1, rule2));
        int iCnt = 20000;
        
        rule.isMatch(ToolUtilities.timeString2Calendar("2013-11-06 18:03:00"), true);
        
        long startTime = System.currentTimeMillis();
        List<Long> lstTime = calcNextOpTimeList(rule, System.currentTimeMillis(), iCnt);
        System.out.println("Use time : " + (System.currentTimeMillis() - startTime)+"ms");
        for (Long l : lstTime)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        
        System.out.println("====================================");
        startTime = System.currentTimeMillis();
        lstTime = calcLastOpTimeList(rule, System.currentTimeMillis(), iCnt);
        System.out.println("Use time : " + (System.currentTimeMillis() - startTime)+"ms");
        for (Long l : lstTime)
        {
            Calendar cal = ToolUtilities.time2Calendar(l);
            String s = ToolUtilities.time2String(l, false) + ", Day Of Week : " + ToolUtilities.getDayOfWeek(cal);
            System.out.println( s );
        }
        
        Element ele = rule.saveXML();
        System.out.println(ToolUtilities.xmlElement2String(ele));
        
        TimeRule ruleXml = TimeRuleUtil.parseXML(ele);
        System.out.println(ToolUtilities.logString(ruleXml, true));
    }
}
