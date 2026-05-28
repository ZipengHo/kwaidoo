package cmn.dto.scheduletask.timerule;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.Calendar;

/**
 * 自定义定时规则
 */
public class UserDefineTimeRuleExample implements TimeRule{

    String name;
    String paramText = null;

    public final static String RULE_NAME = "Name";
    public final static String RULE_PARAM = "Param";

    /**
     * 判断是否匹配
     * @param calOptime 时间
     * @param isSearchNext 是否搜索下一个匹配时间
     * @return 匹配结果，如果返回不匹配，将根据匹配类型查找下一个一个匹配时间段，例如 年不匹配，将查找下一个匹配的年
     *  @Link TimeRule.YEAR_UNMATCH 年不匹配
     *  @Link TimeRule.MONTH_UNMATCH 月不匹配
     *  @Link TimeRule.DAY_UNMATCH 日不匹配
     *  @Link TimeRule.HOUR_UNMATCH 小时不匹配
     *  @Link TimeRule.MINUTE_UNMATCH 分钟不匹配
     *  @Link TimeRule.SECOND_UNMATCH 秒不匹配
     *  @Link TimeRule.OTHER_UNMATCH 其他不匹配
     *  @Link TimeRule.MATCH 匹配匹配
     */
    @Override
    public int isMatch(Calendar calOptime, boolean isSearchNext) throws Exception {
        //"工作日特定时间段"的规则，比如"周一到周五的9:00-18:00"。
        int year = calOptime.get(Calendar.YEAR);
        int month = calOptime.get(Calendar.MONTH);
        int day = calOptime.get(Calendar.DAY_OF_MONTH);
        int hour = calOptime.get(Calendar.HOUR_OF_DAY);
        int minute = calOptime.get(Calendar.MINUTE);
        int dayOfWeek = calOptime.get(Calendar.DAY_OF_WEEK);
        if (isSearchNext) {
            if (year < 2024) {
                return YEAR_UNMATCH;
            }
            if (year == 2024 && month < Calendar.JANUARY) {
                return MONTH_UNMATCH;
            }
            if (year == 2024 && month == Calendar.JANUARY && day < 1) {
                return DAY_UNMATCH;
            }
            if (hour < 9) {
                return HOUR_UNMATCH;
            }
            if (hour == 9 && minute < 0) {
                return MINUTE_UNMATCH;
            }
        } else {
            if (year > 2024) {
                return YEAR_UNMATCH;
            }
            if (year == 2024 && month > Calendar.DECEMBER) {
                return MONTH_UNMATCH;
            }
            if (year == 2024 && month == Calendar.DECEMBER && day > 31) {
                return DAY_UNMATCH;
            }
            if (hour > 18) {
                return HOUR_UNMATCH;
            }
            if (hour == 18 && minute > 0) {
                return MINUTE_UNMATCH;
            }
        }
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return DAY_UNMATCH;
        }
        if (hour < 9 || hour >= 18) {
            return HOUR_UNMATCH;
        }
        if (hour == 9 && minute < 0) {
            return MINUTE_UNMATCH;
        }
        if (hour == 18 && minute > 0) {
            return MINUTE_UNMATCH;
        }
        return MATCH;
    }

    @Override
    public Element saveXML() throws Exception {
        DocumentFactory doc = DocumentFactory.getInstance();
        Element ele = doc.createElement(XML_RULE_USER_DEFINE);
        ele.addAttribute(RULE_CLASS, this.getClass().getName());
        ele.addAttribute(RULE_NAME, name);
        ele.addCDATA(paramText);
        return ele;
    }

    @Override
    public void loadFromXml(Element ele) throws Exception {
        if (ele == null)
            return;
        name = ele.attributeValue(RULE_NAME);
        paramText = ele.getText();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object clone()
    {
        UserDefineTimeRuleExample newO = new UserDefineTimeRuleExample();
        try
        {
            newO.loadFromXml(saveXML());
            return newO;
        } catch (Exception exp)
        {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void preLoad(Calendar calOptime, boolean isSearchNext) {
        //预装载，让自定义规则可以预先准备好需要的数据，以避免每迭代一分钟都要准备一次数据
    }

    @Override
    public boolean isSecondLevel() {
        //是否秒级规则，或者内部含有秒级规则
        return false;
    }
}
