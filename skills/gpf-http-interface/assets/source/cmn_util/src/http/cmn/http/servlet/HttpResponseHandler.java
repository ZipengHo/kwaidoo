package cmn.http.servlet;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpResponseHandler extends Serializable{

	public Object handle(HttpServletRequest request, HttpServletResponse response,Object result) throws Exception;
	
//	Object handleError(HttpServletRequest request, HttpServletResponse response,Exception exp)throws Exception;
//	
//	Object handleAlter(HttpServletRequest request, HttpServletResponse response,Exception exp)throws Exception;
//	
//	Object handleLogin(HttpServletRequest request, HttpServletResponse response,LoginException exp)throws Exception;
//	
//	Object handleRedirect(HttpServletRequest request, HttpServletResponse response,String redirectUrl)throws Exception;
}
