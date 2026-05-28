package cmn.dto.scheduletask.timerule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.leavay.common.util.Pair;
import com.leavay.common.util.SrvRes;
import com.leavay.common.util.ToolUtilities;
/**
 * @What: 常用规则
 * @Why: 
 * @How: 
 * @Author 陈晓斌
 * @CreateTime : 2024年11月12日
 * @Version: 1.0
 */
public class FlowScheduleRule implements TimeRule
{
    private static final long serialVersionUID = -107626671730435408L;
    public List _year;
    public List _month;
    public List _day;
    public List _hour;
    public List _min;
    public List _dayOfWeek;
    
    public FlowScheduleRule()
    {
    }
    
    public void setValue(String ruleKey, Object ... values)
    {
        List<?> lst = ToolUtilities.array2List(values);
        if (ruleKey.equals(RULE_YEAR))
            _year = lst;
        else if  (ruleKey.equals(RULE_MONTH))
            _month = lst;
        else if  (ruleKey.equals(RULE_DAY))
            _day = lst;
        else if  (ruleKey.equals(RULE_HOUR))
            _hour = lst;
        else if  (ruleKey.equals(RULE_MINUTE))
            _min = lst;
        else if (ruleKey.equals(RULE_DAY_OF_WEEK))
            _dayOfWeek = lst;
    }
    

    public static Pair<Integer, Integer> getIntRange(String s) throws Exception
    {
        int iPos = s.indexOf("-");
        if (iPos <= 0)
            return null;
        List<String> lst = ToolUtilities.getSplitedString(s, "-", true);
        if (ToolUtilities.getObjectSize(lst) != 2)
            return null;
        
        return new Pair<Integer, Integer>(ToolUtilities.getInteger(lst.get(0)), ToolUtilities.getInteger(lst.get(1)));
    }
    
    public static boolean isAliquotExp(String s)
    {
        if (s.trim().startsWith("/"))
        {
            int ipos = s.indexOf("/");
            return ToolUtilities.isNumeric(s.substring(ipos+1));
        }
        return false;
    }
    
    public static int getAliquot(String s) throws Exception
    {
        if (!s.trim().startsWith("/"))
            throw new RuntimeException(s+" is not a aliquot expression");
        int ipos = s.indexOf("/");
        return ToolUtilities.getInteger(s.substring(ipos+1));
    }
    
    public static boolean isGreaterExp(String s)
    {
        if (s.trim().startsWith(">"))
        {
            int ipos = s.indexOf(">");
            return ToolUtilities.isNumeric(s.substring(ipos+1));
        }
        return false;
    }
    
    public static boolean verifyGreater(String s, int v) throws Exception
    {
        if (!s.trim().startsWith(">"))
            throw new RuntimeException(s+" is not a greater expression");
        int ipos = s.indexOf(">");
        int std = ToolUtilities.getInteger(s.substring(ipos+1));
        
        return v > std;
    }
    
    public static boolean isGreaterEqualExp(String s)
    {
        if (s.trim().startsWith(">="))
        {
            int ipos = s.indexOf(">=");
            return ToolUtilities.isNumeric(s.substring(ipos+2));
        }
        return false;
    }
    
    public static boolean verifyGreaterEqual(String s, int v) throws Exception
    {
        if (!s.trim().startsWith(">="))
            throw new RuntimeException(s+" is not a greater expression");
        int ipos = s.indexOf(">=");
        int std = ToolUtilities.getInteger(s.substring(ipos+2));
        
        return v >= std;
    }
    
    public static boolean isLessExp(String s)
    {
        if (s.trim().startsWith("<"))
        {
            int ipos = s.indexOf("<");
            return ToolUtilities.isNumeric(s.substring(ipos+1));
        }
        return false;
    }
    
    public static boolean verifyLess(String s, int v) throws Exception
    {
        if (!s.trim().startsWith("<"))
            throw new RuntimeException(s+" is not a less expression");
        int ipos = s.indexOf("<");
        int std = ToolUtilities.getInteger(s.substring(ipos+1));
        
        return v < std;
    }
    
    public static boolean isLessEqual(String s)
    {
        if (s.trim().startsWith("<="))
        {
            int ipos = s.indexOf("<=");
            return ToolUtilities.isNumeric(s.substring(ipos+2));
        }
        return false;
    }
    
    public static boolean verifyLessEqual(String s, int v) throws Exception
    {
        if (!s.trim().startsWith("<="))
            throw new RuntimeException(s+" is not a less expression");
        int ipos = s.indexOf("<=");
        int std = ToolUtilities.getInteger(s.substring(ipos+2));
        
        return v <= std;
    }
    
    public static boolean isNotEqual(String s)
    {
        if (s.trim().startsWith("!="))
        {
            int ipos = s.indexOf("!=");
            return ToolUtilities.isNumeric(s.substring(ipos+2));
        }
        return false;
    }
    
    public static boolean verifyNotEqual(String s, int v) throws Exception
    {
        if (!s.trim().startsWith("!="))
            throw new RuntimeException(s+" is not equal expression");
        int ipos = s.indexOf("!=");
        int std = ToolUtilities.getInteger(s.substring(ipos+2));
        
        return v != std;
    }
    
    public Integer getMin(List<?> lst) throws Exception
    {
        int iMin = Integer.MAX_VALUE;
        for (Object i : lst)
        {
            String s = ""+i;
            if (ToolUtilities.isNumeric(s))
            {
                int vv = ToolUtilities.getInteger(s);
                if (vv >= 0)
                    iMin = Math.min(iMin, vv);
            }
            else if (s.indexOf("-") > 0)
            {
                Pair<Integer, Integer> range = getIntRange(s);
                iMin = Math.min(iMin, range.left);
                iMin = Math.min(iMin, range.right);
            }
        }
        
        if (iMin == Integer.MAX_VALUE)
            return null;
        
        return iMin;
    }
    
    public Integer getMax(List<?> lst) throws Exception
    {
        int iMax = Integer.MIN_VALUE;
        for (Object i : lst)
        {
            String s = ""+i;
            if (ToolUtilities.isNumeric(s))
            {
                int vv = ToolUtilities.getInteger(s);
                if (vv >= 0)
                    iMax = Math.max(iMax, vv);
            }
            else if (s.indexOf("-") > 0)
            {
                Pair<Integer, Integer> range = getIntRange(s);
                iMax = Math.max(iMax, range.left);
                iMax = Math.max(iMax, range.right);
            }
        }
        
        if (iMax == Integer.MIN_VALUE)
            return null;
        
        return iMax;
    }
    
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception
    {
        // 首先检查年越界，只要越界不管是上边界还是下边界，那么年月日等等都不可能匹配了
        if (!ToolUtilities.isObjectEmpty(_year))
        {
            // 往后找的时候如果超出最大年限则直接返回年越界
            int iYear = calOptime.get(Calendar.YEAR);
            Integer IMax = getMax(_year);
            if (isSearchNext && IMax != null &&  iYear > IMax.intValue())
                return OVERFLOW_UNMATCH_MAX;

            // 往前找的时候如果小于最小年限则直接返回年越界
            Integer IMin = getMin(_year);
            if (!isSearchNext && IMin != null && iYear < IMin.intValue())
                return OVERFLOW_UNMATCH_MIN;
        }
        
        if (!normalVerify(calOptime, Calendar.YEAR, _year))
            return YEAR_UNMATCH;
        if (!normalVerify(calOptime, Calendar.MONTH, _month))
            return MONTH_UNMATCH;
        if (!normalVerify(calOptime, Calendar.DAY_OF_MONTH, _day))
            return DAY_UNMATCH;
        if (!normalVerify(calOptime, Calendar.DAY_OF_WEEK, _dayOfWeek))
            return DAY_UNMATCH;
        if (!normalVerify(calOptime, Calendar.HOUR_OF_DAY, _hour))
            return HOUR_UNMATCH;
        if (!normalVerify(calOptime, Calendar.MINUTE, _min))
            return MINUTE_UNMATCH;

        return MATCH;
    }
    
    // 没有配置的列表认为是没限制即任何值均可
    public boolean normalVerify(Calendar cal, int calendarField, List<?> rules) throws Exception
    {
        if (ToolUtilities.isObjectEmpty(rules))
            return true;
        
        int v = cal.get(calendarField);
        if (calendarField == Calendar.MONTH)
            v = v+1;
        else if (calendarField == Calendar.DAY_OF_WEEK)
            v = ToolUtilities.getDayOfWeek(v);
        
        for (Object o : rules)
        {
            if (ToolUtilities.isNumeric(o))
            {
                int iv = ToolUtilities.getInteger(o);

                // 负数要取倒数值，并且针对不同的单位计算方法大不同
                if (iv < 0)
                {
                    if (calendarField == Calendar.YEAR)
                        return false; // 年不支持倒数
                    else if (calendarField == Calendar.MONTH)
                        iv = 13+iv; // 13+(-1)=12, 13+(-2)=11
                    else if (calendarField == Calendar.DAY_OF_MONTH)
                    {
                        int iMaxDayOfMonth = ToolUtilities.getMaxDayOfMonth(cal.getTimeInMillis());
                        iv = iMaxDayOfMonth + 1+ iv; // 30+1+(-2) = 29
                    }else if (calendarField == Calendar.DAY_OF_WEEK)
                    {
                        iv = 8+iv;
                    }else if (calendarField == Calendar.HOUR_OF_DAY)
                        iv = 24 + iv;  // 24+(-1) = 23
                    else if (calendarField == Calendar.MINUTE)
                        iv = 60+iv;  // 60+(-1)=59
                }

                if (iv == v)
                    return true;
            }else
            {
                String s = ""+o;
                if (s.indexOf("-") > 0)
                {
                    Pair<Integer, Integer> range = getIntRange(s);
                    if (v >= range.left && v <= range.right)
                        return true;
                }else if (isAliquotExp(s))
                {
                    int divisor = getAliquot(s);
                    if (v % divisor == 0)
                        return true;
                }else if (isNotEqual(s))
                {
                    if (verifyNotEqual(s, v))
                        return true;
                }
                else if (isGreaterEqualExp(s))
                {
                    if (verifyGreaterEqual(s, v))
                        return true;
                }
                else if (isGreaterExp(s))
                {
                    if (verifyGreater(s, v))
                        return true;
                }else if (isLessEqual(s))
                {
                    if (verifyLessEqual(s, v))
                        return true;
                }else if (isLessExp(s))
                {
                    if (verifyLess(s, v))
                        return true;
                }
            }
        }
        
        return false;
    }
    

    public static String list2String(List<?> lst)
    {
        String s = "";
        for (Object o : lst)
        {
            if (!ToolUtilities.isStringEmpty(s))
                s+=",";
            s+=o;
        }
        
        return s;
    }
    

    public static List<Object> string2list(String ss) throws Exception
    {
        if (ToolUtilities.isStringEmpty(ss) || ToolUtilities.isStringEmpty(ss.trim()))
            return null;
        
        List<Object> lst = new ArrayList<Object>();
        List<String> lstS = ToolUtilities.getSplitedString(ss.trim(), ",", true);
        for (String s : lstS)
        {
            if (ToolUtilities.isNumeric(s))
                lst.add(ToolUtilities.getInteger(s));
            else
                lst.add(s);
        }
        return lst;
    }
    
    public Element saveXML()
    {
    	DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_RULE);
        if (!ToolUtilities.isObjectEmpty(_year))
            ele.addAttribute(RULE_YEAR, list2String(_year));
        if (!ToolUtilities.isObjectEmpty(_month))
            ele.addAttribute(RULE_MONTH, list2String(_month));
        if (!ToolUtilities.isObjectEmpty(_day))
            ele.addAttribute(RULE_DAY, list2String(_day));
        if (!ToolUtilities.isObjectEmpty(_dayOfWeek))
            ele.addAttribute(RULE_DAY_OF_WEEK, list2String(_dayOfWeek));
        if (!ToolUtilities.isObjectEmpty(_hour))
            ele.addAttribute(RULE_HOUR, list2String(_hour));
        if (!ToolUtilities.isObjectEmpty(_min))
            ele.addAttribute(RULE_MINUTE, list2String(_min));
        return ele;
    }
    

    public void loadFromXml(Element ele) throws Exception
    {
        if (ele == null)
            return;
        
        String sV = ele.attributeValue(RULE_YEAR);
        _year = string2list(sV);
        sV = ele.attributeValue(RULE_MONTH);
        _month = string2list(sV);
        sV = ele.attributeValue(RULE_DAY);
        _day = string2list(sV);
        sV = ele.attributeValue(RULE_DAY_OF_WEEK);
        _dayOfWeek = string2list(sV);
        sV = ele.attributeValue(RULE_HOUR);
        _hour = string2list(sV);
        sV = ele.attributeValue(RULE_MINUTE);
        _min = string2list(sV);
    }
    
    
    public String toString()
    {
        try
        {
            return ToolUtilities.xmlElement2String(saveXML());
        } catch (IOException exp)
        {
            return "Unknown : " + exp.getMessage();
        }
    }
    
    public String getPureName()
    {
        String sRule = "";
        if (!ToolUtilities.isObjectEmpty(_year))
            sRule += " " + "(" +list2String(_year) +")"+ SrvRes.getString("Year");
        if (!ToolUtilities.isObjectEmpty(_month))
            sRule += " "+"(" +list2String(_month) +")" + SrvRes.getString("Month");
        if (!ToolUtilities.isObjectEmpty(_day))
            sRule += " "+"(" +list2String(_day) +")" + SrvRes.getString("__Day");
        if (!ToolUtilities.isObjectEmpty(_dayOfWeek))
            sRule += " " + SrvRes.getString("__DayOfWeek")+"(" +list2String(_dayOfWeek) +")";
        if (!ToolUtilities.isObjectEmpty(_hour))
            sRule += " "+"(" +list2String(_hour) +")" + SrvRes.getString("__Oclock");
        if (!ToolUtilities.isObjectEmpty(_min))
            sRule += " "+"(" +list2String(_min) +")" + SrvRes.getString("__Minute");
        
        return sRule;
    }
    
    public String getName()
    {
        String sRule = getPureName();
    
        if (ToolUtilities.isStringEmpty(sRule))
            sRule = SrvRes.getString("Unlimited");
        
        return sRule;
    }
    
    public Object clone()
    {
        FlowScheduleRule newO = new FlowScheduleRule();
        try
        {
            newO.loadFromXml(saveXML());
            return newO;
        } catch (Exception exp)
        {
            throw new RuntimeException(exp);
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
