package cmn.http.dto;

import java.io.Serializable;
import java.util.Map;

import com.leavay.common.util.javac.ClassFactory;

import bap.cells.Cells;
import cell.CellIntf;
import cell.bap.servlet.IHttpServlet;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingContext;
import cmn.http.servlet.mapping.RequestMappingIntf;
import cmn.http.util.HttpSessionUtil;

public class RequestMappingDto implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -8702624012610319303L;
	String name;
	String[] path;
	RequestMethod[] method;
	String[] params;
	String[] headers;
	String[] consumes;
	String[] produces;
	String handleClass;
	String handleMethod;
	Map<String, String> pathVariables;
	
	public String getName() {
		return name;
	}
	public String[] getPath() {
		return path;
	}
	public RequestMethod[] getMethod() {
		return method;
	}
	public String[] getParams() {
		return params;
	}
	public String[] getHeaders() {
		return headers;
	}
	public String[] getConsumes() {
		return consumes;
	}
	public String[] getProduces() {
		return produces;
	}
	public String getHandleClass() {
		return handleClass;
	}
	public Class<? extends RequestMappingIntf> getHandleClazz() {
		try {
			return ClassFactory.loadClass(handleClass);
		}catch (Exception e) {
			return null;
		}
	}
	
	public RequestMappingIntf getHandleInstance(IHttpServlet servlet) throws Exception {
		Class<?extends RequestMappingIntf> clazz = getHandleClazz();
		if(clazz != null) {
			RequestMappingIntf instance = null;
			if(CellIntf.class.isAssignableFrom(clazz)) {
				instance = Cells.get(clazz);
			}else {
				try {
					instance = clazz.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			RequestMappingContext context = new RequestMappingContext();
			context.setHttpServlet(servlet).setRequestMapping(this)
			.setSessionInfo(HttpSessionUtil.getSessionInfo(SessionInfo.class))
			.setAccessToken(HttpSessionUtil.getAccessToken())
			.setPathVariables(this.getPathVariables())
			;
			instance.setContext(context);
			return instance;
		}
		return null;
	}
	public String getHandleMethod() {
		return handleMethod;
	}
	public RequestMappingDto setName(String name) {
		this.name = name;
		return this;
	}
	public RequestMappingDto setPath(String[] path) {
		this.path = path;
		return this;
	}
	public RequestMappingDto setMethod(RequestMethod[] method) {
		this.method = method;
		return this;
	}
	public RequestMappingDto setParams(String[] params) {
		this.params = params;
		return this;
	}
	public RequestMappingDto setHeaders(String[] headers) {
		this.headers = headers;
		return this;
	}
	public RequestMappingDto setConsumes(String[] consumes) {
		this.consumes = consumes;
		return this;
	}
	public RequestMappingDto setProduces(String[] produces) {
		this.produces = produces;
		return this;
	}
	public RequestMappingDto setHandleClass(String handleClass) {
		this.handleClass = handleClass;
		return this;
	}
	public RequestMappingDto setHandleClass(Class<? extends RequestMappingIntf> handleClass) {
		this.handleClass = handleClass.getName();
		return this;
	}
	
	public RequestMappingDto setHandleMethod(String handleMethod) {
		this.handleMethod = handleMethod;
		return this;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public RequestMappingDto setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
		return this;
	}

	public String getPathVariable(String name) {
		return pathVariables != null ? pathVariables.get(name) : null;
	}
}
