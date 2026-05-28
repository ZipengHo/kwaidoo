package cmn.dto.scheduletask.timerule;

import java.io.Serializable;
import java.util.Calendar;

import org.dom4j.Element;

public interface TimeRule extends Serializable
{
    public final static String XML_AND = "And";
    public final static String XML_OR = "Or";
    public final static String XML_RULE = "Rule";
    public final static String XML_RULE_CYCLE = "Rule_Cycle";
    public final static String XML_RULE_SECOND_CYCLE = "Rule_Second_Cycle";
    public final static String XML_RULE_TIME_POINT = "Rule_Time_Point";
//    public final static String XML_RULE_CFG_TRIGGER = "Rule_Cfg_Trigger";
    public final static String XML_RULE_USER_DEFINE = "Rule_User_Define";

    public final static String RULE_CLASS = "RuleClass";
    
    public final static String RULE_YEAR = "Year";
    public final static String RULE_MONTH = "Month";
    public final static String RULE_DAY = "Day";
    public final static String RULE_HOUR = "Hour";
    public final static String RULE_MINUTE = "Min";
    public final static String RULE_DAY_OF_WEEK = "DayOfWeek";
    public final static String RULE_SECOND = "Second";

    public final static int MATCH = 0;
    public final static int OVERFLOW_UNMATCH_MIN = 1;  //小于最小年边界 ，或者和时间点比对时已经小于最小值，再小已无可能
    public final static int OVERFLOW_UNMATCH_MAX = 2;  //超过最大年边界 ，或者和时间点比较时已经大于最大值，再大已无可能
    public final static int YEAR_UNMATCH = 3;
    public final static int MONTH_UNMATCH = 4;
    public final static int DAY_UNMATCH = 5;
    public final static int HOUR_UNMATCH = 6;
    public final static int MINUTE_UNMATCH = 7;
    public final static int SECOND_UNMATCH = 8;

    public final static int OTHER_UNMATCH = 100;
    
    // MATH or OTHER_UNMATCH ....
    public int isMatch(Calendar calOptime, boolean isSearchNext)  throws Exception;
    
    public Element saveXML() throws Exception;
    
    public void loadFromXml(Element ele) throws Exception;
    
    public String getName();
    
    public Object clone();
    
    // 预装载，让自定义规则可以预先准备好需要的数据，以避免每迭代一分钟都要准备一次数据
    public void preLoad(Calendar calOptime, boolean isSearchNext);
    
    // 是否秒级规则，或者内部含有秒级规则
    public boolean isSecondLevel();
}
