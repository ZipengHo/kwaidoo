package cmn.http.servlet.mapping;

import java.io.Serializable;
import java.util.Map;

import cell.bap.servlet.IHttpServlet;
import cmn.http.dto.RequestMappingDto;
import cmn.http.dto.SessionInfo;
import crpc.CRpcContainerIntf;

public class RequestMappingContext implements Serializable,CRpcContainerIntf{

	private static final long serialVersionUID = -4666680297403310520L;

	IHttpServlet httpServlet;
	String accessToken;
	SessionInfo sessionInfo;
	RequestMappingDto requestMapping;
	Map<String, String> pathVariables;
	public IHttpServlet getHttpServlet() {
		return httpServlet;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public SessionInfo getSessionInfo() {
		return sessionInfo;
	}
	public RequestMappingDto getRequestMapping() {
		return requestMapping;
	}
	public RequestMappingContext setHttpServlet(IHttpServlet httpServlet) {
		this.httpServlet = httpServlet;
		return this;
	}
	public RequestMappingContext setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
	public RequestMappingContext setSessionInfo(SessionInfo sessionInfo) {
		this.sessionInfo = sessionInfo;
		return this;
	}
	public RequestMappingContext setRequestMapping(RequestMappingDto requestMapping) {
		this.requestMapping = requestMapping;
		return this;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public RequestMappingContext setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
		return this;
	}

	public String getPathVariable(String name) {
		return pathVariables != null ? pathVariables.get(name) : null;
	}

}
	
