/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cmn.http.servlet.impl;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.reflect.$Gson$Types;

import cell.bap.servlet.CHttpServlet;
import cell.bap.servlet.IHttpServlet;
import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cell.cmn.http.IHttpRequestService;
import cell.cmn.http.IStudyHttpRequestMapping;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.exception.handler.ErrorHandler;
import cmn.http.anotation.RequestMethod;
import cmn.http.dto.RequestMappingDto;
import cmn.http.dto.SessionInfo;
import cmn.http.exception.NestedServletException;
import cmn.http.multipart.MultipartFile;
import cmn.http.multipart.MultipartRequest;
import cmn.http.servlet.HttpRequestHandler;
import cmn.http.servlet.mapping.RequestMappingContext;
import cmn.http.servlet.mapping.RequestMappingIntf;
import cmn.http.util.HttpConst;
import cmn.http.util.HttpSessionUtil;
import cmn.http.util.MultiValueMap;
import cmn.http.util.UrlPathHelper;
import cmn.reflect.TypeToken;
import cmn.servlet.RepeatableReadRequestWrapper;
import cmn.util.CmnTools;
import cmn.util.JsonUtil;
import cmn.util.ProxyUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.exception.VerifyException;
import web.dto.Pair;
import web.util.TypeUtils;

/**
 * An {@link HttpRequestHandler} for serving static files using the Servlet container's "default" Servlet.
 *
 * <p>This handler is intended to be used with a "/*" mapping when the
 * {@link web.servlet.web.servlet.DispatcherServlet DispatcherServlet}
 * is mapped to "/", thus  overriding the Servlet container's default handling of static resources.
 * The mapping to this handler should generally be ordered as the last in the chain so that it will
 * only execute when no other more specific mappings (i.e., to controllers) can be matched.
 *
 * <p>Requests are handled by forwarding through the {@link RequestDispatcher} obtained via the
 * name specified through the {@link #setDefaultServletName "defaultServletName" property}.
 * In most cases, the {@code defaultServletName} does not need to be set explicitly, as the
 * handler checks at initialization time for the presence of the default Servlet of well-known
 * containers such as Tomcat, Jetty, Resin, WebLogic and WebSphere. However, when running in a
 * container where the default Servlet's name is not known, or where it has been customized
 * via server configuration, the  {@code defaultServletName} will need to be set explicitly.
 *
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @since 3.0.4
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3578580789564714302L;
	private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;
	
	@Override
	public Object handleRequest(HttpServletRequest request, HttpServletResponse response,RequestMethod requestMethod,ErrorHandler errorHandler)
			throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		IHttpServlet servlet = new CHttpServlet(request, response);
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		RequestMappingDto mapping = IHttpRequestService.get().getRequestMapping(servlet,requestMethod);
		if(mapping == null) {
			throw new NestedServletException("未找到请求处理配置!" + lookupPath);
		}
		Class<? extends RequestMappingIntf> mappingHandlerClass = mapping.getHandleClazz();
		RequestMappingIntf mappingHandler = mapping.getHandleInstance(servlet);
		if(mappingHandler == null)
			throw new NestedServletException("未找到请求处理配置!" + lookupPath);
		
		Map<String,Object> paramMap = new LinkedHashMap<>();
		boolean isMultipartRequest = false;
		List<String> fileNames = new ArrayList<>();
		tracer.debug("request：" + request);
//		List<Pair<String,byte[]>> files = new ArrayList<>();
		if(request instanceof MultipartRequest) {
			MultiValueMap<String, MultipartFile> fileMap = ((MultipartRequest) request).getMultiFileMap();
			tracer.debug("多媒体文件：" + fileMap.keySet());
			for(String paramName : fileMap.keySet()) {
				List<MultipartFile> partFiles = fileMap.get(paramName);
				for(MultipartFile partFile : partFiles) {
					if(!paramMap.containsKey(paramName)) {
						paramMap.put(paramName, new ArrayList<>());
					}
					List<MultipartFile> files = (List<MultipartFile>) paramMap.get(paramName);
					files.add(partFile);
//					files.add(new Pair<>(partFile.getOriginalFilename(),partFile.getBytes()));
					//FIXME 这里还要考虑接受文件列表的情况，目前只处理了单个上传文件
//					paramMap.put(paramName, new Pair<>(partFile.getOriginalFilename(),partFile.getBytes()));
					fileNames.add(partFile.getOriginalFilename());
				}
			}
			isMultipartRequest = true;
			for(String paramName : request.getParameterMap().keySet()) {
				String[] values = request.getParameterValues(paramName);
				if(CmnUtil.isArrayEmpty(values)) {
					paramMap.put(paramName, null);
				}else {
					paramMap.put(paramName, values[0]);
				}
			}
		}else {
			if(CmnUtil.isStringEqual(request.getContentType(), "application/json")) {
				String requestBody = ((RepeatableReadRequestWrapper)request).getRequestBodyAsString();
				if(!CmnUtil.isStringEmpty(requestBody)) {
					try {
						Map<String,Object> requestMap = JsonUtil.fromJson(requestBody, Map.class);
						paramMap.putAll(requestMap);
					}catch (Exception e) {
						throw new VerifyException("请求的body不是JSON格式！");
					}
				}
	        }else {
				Set<String> paramNames = new LinkedHashSet<>(request.getParameterMap().keySet());
		        for(String paramKey : paramNames){
		        	String paramValue = request.getParameter(paramKey);
		        	paramMap.put(paramKey, paramValue);
		        }
	        }
		}
		tracer.debug("", "多媒体上传请求："+isMultipartRequest+",文件："+fileNames);
		String methodName = mapping.getHandleMethod();
//		LvUtil.trace("actionClass = " + actionClass);
		Method method = getMethod(mappingHandlerClass, methodName);
		
		if(method == null) {
			throw new VerifyException("Java类("+mappingHandler.getClass().getName()+")中未找到方法：" + methodName);
		}
		MethodDeclare methodDeclare = method.getAnnotation(MethodDeclare.class);
		Parameter[] parameters = method.getParameters();
//		LvUtil.trace("method.getParameters() = " + parameters);
//		try {
			Object[] params = getParameterValues(servlet,request,mapping,methodDeclare,parameters, paramMap);
			if(errorHandler != null) {
				mappingHandler = (RequestMappingIntf) ProxyUtil.newProxyInstance(mappingHandler, errorHandler);
				Object result = CmnTools.callFunction(mappingHandler, methodName, params);
				return result;
			}else {
				Object result = CmnTools.callFunction(mappingHandler, methodName, params);
				return result;
			}
//		} catch (Exception e) {
//			throw new BaseException(ErrorLevel.ERROR,"000001", e);
//		}
	}
	
	public static Method getMethod(Class clazz,String methodName) {
		for(Method method : clazz.getMethods()) {
			if(method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

	private static Type MultipartFileType = new TypeToken<List<MultipartFile>>() {}.getType();
	public static Object[] getParameterValues(IHttpServlet servlet,HttpServletRequest request,RequestMappingDto requestMapping,MethodDeclare methodDeclare,Parameter[] parameters,Map<String,Object> paramMapping) throws Exception {
		Map<String, InputDeclare> inputDeclareMap = new LinkedHashMap<>();
		if(methodDeclare != null) {
			for (InputDeclare inputDeclare : methodDeclare.inputs()) {
				inputDeclareMap.put(inputDeclare.name(), inputDeclare);
			}
		}
		Tracer tracer = TraceUtil.getCurrentTracer();
		List<Object> paramValues = new ArrayList<>();
		for (Parameter parameter : parameters) {
			Object varValue = paramMapping.get(parameter.getName());
			if(varValue == null) {
				if(inputDeclareMap.containsKey(parameter.getName())) {
					InputDeclare inputDeclare = inputDeclareMap.get(parameter.getName());
					Object realValue = null;
					String exampleVavlue = inputDeclare.exampleValue();
					if(!CmnUtil.isStringEmpty(exampleVavlue)) {
						if(exampleVavlue.equals(HttpConst.$context$)){
							RequestMappingContext context = new RequestMappingContext();
							context.setHttpServlet(servlet).setRequestMapping(requestMapping)
									.setSessionInfo(HttpSessionUtil.getSessionInfo(SessionInfo.class))
									.setAccessToken(HttpSessionUtil.getAccessToken());
							realValue = context;
						}else if(exampleVavlue.equals(HttpConst.$RequestBody$)){
							String requestBody = null;
							if(request instanceof RepeatableReadRequestWrapper) {
								requestBody = ((RepeatableReadRequestWrapper)request).getRequestBodyAsString();
							}else {
								try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
									byte[] buffer = new byte[1024];
									int len;
									while ((len = request.getInputStream().read(buffer)) != -1) {
										outputStream.write(buffer, 0, len);
									}
									requestBody = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
								}
							}
							if(!CmnUtil.isStringEmpty(requestBody)){
								Type type = parameter.getParameterizedType();
								if(type == null) {
									type = parameter.getType();
								}
								try(IJson json = IJsonService.get().getJson()){
									realValue = json.fromJsonByType((String)requestBody, type);
								}catch (Exception e) {
									throw new Exception("参数解析错误,类型："+ type+",值" + requestBody,e);
								}
							}
						}else if(exampleVavlue.startsWith("{") && exampleVavlue.endsWith("}")){
							String pathVarName = exampleVavlue.substring(1, exampleVavlue.length() - 1);
							String pathVarValue = requestMapping.getPathVariable(pathVarName);
							if(pathVarValue != null) {
								varValue = pathVarValue;
								Type type = parameter.getParameterizedType();
								if(type == null) {
									type = parameter.getType();
								}
								if(type == Long.class || type == long.class) {
									realValue = CmnUtil.getLong(varValue);
								}else if(type == Integer.class || type == int.class) {
									realValue = CmnUtil.getInteger(varValue);
								}else if(type == Boolean.class || type == boolean.class) {
									realValue = CmnUtil.getBoolean(varValue);
								}else if(type == Double.class || type == double.class) {
									realValue = CmnUtil.getDouble(varValue);
								}else if(type == Float.class || type == float.class) {
									realValue = CmnUtil.getFloat(varValue);
								}else if(type == String.class) {
									realValue = varValue;
								}else{
									try(IJson json = IJsonService.get().getJson()){
										realValue = json.fromJsonByType((String)varValue, type);
									}catch (Exception e) {
										throw new Exception("参数解析错误,类型："+ type+",值" + varValue,e);
									}
								}
							}
						}else {
							varValue = exampleVavlue;
							Type type = parameter.getParameterizedType();
							if(type == null) {
								type = parameter.getType();
							}
							if(type == Long.class || type == long.class) {
								realValue = CmnUtil.getLong(varValue);
							}else if(type == Integer.class || type == int.class) {
								realValue = CmnUtil.getInteger(varValue);
							}else if(type == Boolean.class || type == boolean.class) {
								realValue = CmnUtil.getBoolean(varValue);
							}else if(type == Double.class || type == double.class) {
								realValue = CmnUtil.getDouble(varValue);
							}else if(type == Float.class || type == float.class) {
								realValue = CmnUtil.getFloat(varValue);
							}else if(type == String.class) {
								realValue = varValue;
							}else{
								if(varValue.getClass() == type) {
									realValue = varValue;
								}else {
									tracer.debug("自定义参数类型：" + type);
									try(IJson json = IJsonService.get().getJson()){
										if(varValue instanceof String) {
											realValue = json.fromJsonByType((String)varValue, type);
										}else{
											realValue = json.forceCastByType(type,varValue);
										}
									}catch (Exception e) {
										throw new Exception("参数解析错误,类型："+ type+",值" + varValue,e);
									}
								}
							}
						}
					}
					if(realValue == null && !inputDeclare.nullable()) {
						throw new VerifyException("接口参数[" + parameter.getName() + "]不能为空！");
					}
					paramValues.add(realValue);
					continue;
				}else {
					paramValues.add(null);
					continue;
				}
			}else {
				Type type = parameter.getParameterizedType();
				if(type == null) {
					type = parameter.getType();
				}
				Object realValue = null;
				if(type == Long.class || type == long.class) {
					realValue = CmnUtil.getLong(varValue);
				}else if(type == Integer.class || type == int.class) {
					realValue = CmnUtil.getInteger(varValue);
				}else if(type == Boolean.class || type == boolean.class) {
					realValue = CmnUtil.getBoolean(varValue);
				}else if(type == Double.class || type == double.class) {
					realValue = CmnUtil.getDouble(varValue);
				}else if(type == Float.class || type == float.class) {
					realValue = CmnUtil.getFloat(varValue);
				}else if(type == MultipartFile.class) {
					List<MultipartFile> files = (List<MultipartFile>) varValue;
					if(!files.isEmpty()) {
						realValue = files.get(0);
					}
				}else if(type.getTypeName().equals(MultipartFileType.getTypeName())) {
					List files = (List) varValue;
					if(!CmnUtil.isCollectionEmpty(files)) {
						Object file = files.get(0);
						if(!(file instanceof MultipartFile)) {
							throw new VerifyException("接口参数[" + parameter.getName() + "]值不为文件列表！");
						}
					}
					realValue = files;
				}else if(type == String.class) {
					realValue = varValue;
				}else{
					if(varValue.getClass() == type) {
						realValue = varValue;
					}else {
						tracer.debug("自定义参数类型：" + type);
						if(TypeUtils.isUploadFileType(type) || type.getTypeName().equals(TypeUtils.nameBytePairType.getTypeName())) {
							tracer.debug("找到上传文件参数类型");
							List files = (List) varValue;
							if (CmnUtil.isCollectionEmpty(files))
								throw new Exception("解析调用参数出错，未找到上传文件！");
							if (TypeUtils.getUploadFileTypes().contains(type.getTypeName())) {
								List<Pair<String, byte[]>> filePairs = new ArrayList<>();
								for(Object file : files) {
									if(file instanceof MultipartFile) {
										filePairs.add(new Pair<String, byte[]>(((MultipartFile) file).getOriginalFilename(), ((MultipartFile) file).getBytes()));
									}else {
										throw new VerifyException("接口参数[" + parameter.getName() + "]值不为文件！");
									}
								}
								realValue = filePairs;
							} else if(type.getTypeName().equals(TypeUtils.nameBytePairType.getTypeName())) {
								Object file = files.get(0);
								if(file instanceof MultipartFile) {
									realValue = new Pair<String, byte[]>(((MultipartFile) file).getOriginalFilename(), ((MultipartFile) file).getBytes());
								}else {
									throw new VerifyException("接口参数[" + parameter.getName() + "]值不为文件！");
								}
							}
						}else {
							try(IJson json = IJsonService.get().getJson()){
								if(varValue instanceof String) {
									realValue = json.fromJsonByType((String)varValue, type);
								}else {
									realValue = json.forceCastByType(type, varValue);
								}
							}catch (Exception e) {
								throw new Exception("参数解析错误,类型："+ type+",值" + varValue,e);
							}
						}
					}
				}
				paramValues.add(realValue);
			}
		}
		return paramValues.toArray();
	}
	
	public static void main(String[] args) {
		Class clazz = IStudyHttpRequestMapping.class;
		Method method = getMethod(clazz, "uploadMultiPartFiles");
		Parameter[] parameters = method.getParameters();
		for (Parameter parameter : parameters) {
			Type type = parameter.getParameterizedType();
			if(type == null) {
				type = parameter.getType();
			}
			Type type1 = $Gson$Types.canonicalize(Objects.requireNonNull(type));
			System.out.println(type1.getTypeName());
			System.out.println(TypeUtils.nameBytePairType.getTypeName());
			System.out.println(TypeUtils.isUploadFileType(type1));
			System.out.println(type.getTypeName().equals(TypeUtils.nameBytePairType.getTypeName()));
			Type multipartFileType = new TypeToken<List<MultipartFile>>() {}.getType();
			System.out.println(multipartFileType.getTypeName());
			System.out.println(type.getTypeName().equals(multipartFileType.getTypeName()));
			System.out.println(type == multipartFileType);
		}
	}

}
