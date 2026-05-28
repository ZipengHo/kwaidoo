package cell.gpf.dc.http;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingIntf;
import gpf.adur.user.User;
@ClassDeclare(
		label = "应用登录认证样例接口", 
		what = "", 
		why = "",
		how = "", 
		developer = "陈晓斌", 
		createTime = "2025-04-27", 
		updateTime = "2025-04-27", 
		version = "1.0"
		)
@RequestMapping(path = "/gpfdc/app")
public interface ILoginRequestMapping extends CellIntf,RequestMappingIntf{

	static ILoginRequestMapping get() {
		return Cells.get(ILoginRequestMapping.class);
	}
	@MethodDeclare(
			label = "登录",
			what = "",
			why = "",
			how = "",
			inputs = {
					@InputDeclare (name = "appCode", label = "应用编号", desc = ""),
					@InputDeclare (name = "user", label = "用户名", desc = ""),
					@InputDeclare (name = "password", label = "密码", desc = "")
			}
			)
	@RequestMapping(path = "/login", method = {RequestMethod.GET,RequestMethod.POST})
	public void login(String appCode,String user,String password)throws Exception;
	
	@MethodDeclare(
			label = "登出",
			what = "",
			why = "",
			how = "",
			inputs = {
			}
			)
	@RequestMapping(path = "/logout", method = {RequestMethod.GET,RequestMethod.POST})
	public void logout()throws Exception;
	@MethodDeclare(
			label = "查询当前用户信息",
			what = "",
			why = "",
			how = "",
			inputs = {
			}
			)
	@RequestMapping(path = "/getCurrentUser", method = {RequestMethod.GET,RequestMethod.POST})
	public User getCurrentUser()throws Exception;
}
