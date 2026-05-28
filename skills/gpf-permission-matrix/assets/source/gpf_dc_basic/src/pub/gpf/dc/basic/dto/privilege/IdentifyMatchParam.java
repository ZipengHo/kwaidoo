package gpf.dc.basic.dto.privilege;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import cmn.util.NullUtil;

/**
 * 用户匹配身份的查询参数
 * 包括 条件表达式以及表达式所需的关联查询视图
 * @author chenxb
 *
 */
public class IdentifyMatchParam implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 3055885936326786825L;

    /**
     * 构建关联查询视图的视图列表
     * 配置的关联视图
     */
    Map<String,JoinViewInfo> joinViews = new LinkedHashMap<>();
    /**
     * 匹配表达式的关联查询视图，若表达式内的字段来源于表单时，不需要填写
     */
    String mainJoinViewName;
    /**
     * 匹配用户身份的表达式
     */
    String matchExpression;

    public Map<String, JoinViewInfo> getJoinViews() {
        return joinViews;
    }
    public IdentifyMatchParam setJoinViews(Map<String, JoinViewInfo> joinViews) {
        this.joinViews = joinViews;
        return this;
    }
    public void addJoinView(JoinViewInfo view) {
        joinViews.put(view.getViewName(), view);
    }
    public String getMainJoinViewName() {
        return mainJoinViewName;
    }
    public IdentifyMatchParam setMainJoinViewName(String mainJoinViewName) {
        this.mainJoinViewName = mainJoinViewName;
        return this;
    }
    public String getMatchExpression() {
        return matchExpression;
    }
    public IdentifyMatchParam setMatchExpression(String matchExpression) {
        this.matchExpression = matchExpression;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IdentifyMatchParam))
            return false;
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int result = 17;
        if(mainJoinViewName != null)
            result = 31 * result + mainJoinViewName.hashCode();
        if(matchExpression != null)
            result = 31 * result + matchExpression.hashCode();
        for(String key : NullUtil.get(joinViews).keySet()) {
            result = 31 * result + key.hashCode();
            result = 31 * result + joinViews.get(key).hashCode();
        }
        return result;
    }
}
