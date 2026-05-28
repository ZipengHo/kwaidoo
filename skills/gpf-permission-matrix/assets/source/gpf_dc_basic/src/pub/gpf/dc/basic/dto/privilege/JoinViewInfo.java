package gpf.dc.basic.dto.privilege;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import cmn.util.NullUtil;

public class JoinViewInfo implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 473109207564686523L;
    /**
     * 用于关联查询的视图名称
     */
    String viewName;
    /**
     * 关联视图的SQL语句
     */
    String viewSql;
    /**
     * 关联视图内部的视图名和模型Id的对应关系
     */
    Map<String,String> innerViewName2Model = new LinkedHashMap<>();
    public String getViewName() {
        return viewName;
    }
    public JoinViewInfo setViewName(String viewName) {
        this.viewName = viewName;
        return this;
    }
    public String getViewSql() {
        return viewSql;
    }
    public JoinViewInfo setViewSql(String viewSql) {
        this.viewSql = viewSql;
        return this;
    }
    public Map<String, String> getInnerViewName2Model() {
        return innerViewName2Model;
    }
    public JoinViewInfo setInnerViewName2Model(Map<String, String> innerViewName2Model) {
        this.innerViewName2Model = innerViewName2Model;
        return this;
    }
    public JoinViewInfo addInnerViewName2Model(String innerViewName,String modelId) {
        this.innerViewName2Model.put(innerViewName, modelId);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof JoinViewInfo))
            return false;
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int result = 17;
        if(viewName != null)
            result = 31 * result + viewName.hashCode();
        if(viewSql != null)
            result = 31 * result + viewSql.hashCode();
        for(String key : NullUtil.get(innerViewName2Model).keySet()) {
            result = 31 * result + key.hashCode();
            result = 31 * result + innerViewName2Model.get(key).hashCode();
        }
        return result;
    }
}
