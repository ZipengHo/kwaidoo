package gpf.dc.basic.dto.privilege;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gpf.dc.config.sqlmapping.ViewSqlDto;
/**
 * 结果集查询权限参数
 * @author chenxb
 *
 */
public class ResultSetQueryParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7408645665120771055L;
	/**
	 * 补充查询视图名称，在结果集查询时追加到form之后，用于权限表达式的视图字段引用
	 */
	Set<String> appendViewAliases = new LinkedHashSet<>();
	/**
	 * 补充查询的视图SQL
	 */
	Map<String,ViewSqlDto> appendViewSqls = new LinkedHashMap<>();
	/**
	 * 结果集的查询权限表达式
	 */
	String privilegeExpression;
	/**
	 * 数据对应的角色集合
	 */
	List<String> roleCaseWhenExpressions;
	public Set<String> getAppendViewAliases() {
		return appendViewAliases;
	}
	public ResultSetQueryParam setAppendViewAliases(Set<String> appendViewAliases) {
		this.appendViewAliases = appendViewAliases;
		return this;
	}
	public void addAppendViewAlias(String appendViewAlias) {
		this.appendViewAliases.add(appendViewAlias);
	}
	public Map<String, ViewSqlDto> getAppendViewSqls() {
		return appendViewSqls;
	}
	public ResultSetQueryParam setAppendViewSqls(Map<String, ViewSqlDto> appendViewSqls) {
		this.appendViewSqls = appendViewSqls;
		return this;
	}
	public String getPrivilegeExpression() {
		return privilegeExpression;
	}
	public ResultSetQueryParam setPrivilegeExpression(String privilegeExpression) {
		this.privilegeExpression = privilegeExpression;
		return this;
	}
	public List<String> getRoleCaseWhenExpressions() {
		return roleCaseWhenExpressions;
	}
	public ResultSetQueryParam setRoleCaseWhenExpressions(List<String> roleCaseWhenExpressions) {
		this.roleCaseWhenExpressions = roleCaseWhenExpressions;
		return this;
	}
	
}
