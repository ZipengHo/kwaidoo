/*
 * 版权所有 2002-2023 原作者或作者团队。
 *
 * 根据 Apache License, Version 2.0（“许可证”）授权；
 * 除非符合许可证规定，否则不得使用此文件。
 * 您可以在以下网址获取许可证副本：
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则根据许可证分发的软件按“原样”分发，
 * 不附带任何明示或暗示的保证或条件。
 * 请查看许可证，了解有关权限和限制的具体语言。
 */

package cmn.http.anotation;

import org.eclipse.jetty.http.HttpMethod;

import cmn.util.AssertUtil;

/**
 * HTTP 请求方法的枚举。用于与 {@link RequestMapping#method()} 属性结合使用。
 *
 * <p>
 * 请注意，默认情况下，{@link cmn.http.servlet.web.servlet.DispatcherServlet} 只支持
 * GET、HEAD、POST、PUT、PATCH 和 DELETE。DispatcherServlet 会使用默认的 HttpServlet 行为处理
 * TRACE 和 OPTIONS，除非明确指示同时分发这些请求类型：
 * 请查看“dispatchOptionsRequest”和“dispatchTraceRequest”属性，如有需要，可将其设置为“true”。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see RequestMapping
 * @see cmn.http.servlet.web.servlet.DispatcherServlet#setDispatchOptionsRequest
 * @see cmn.http.servlet.web.servlet.DispatcherServlet#setDispatchTraceRequest
 */
public enum RequestMethod {

	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

	/**
	 * 将给定的方法值解析为 {@code RequestMethod} 枚举值。 如果 {@code method} 没有对应的值，则返回
	 * {@code null}。
	 * 
	 * @param method 方法值，作为字符串
	 * @return 对应的 {@code RequestMethod}，如果未找到则返回 {@code null}
	 * @throws Exception
	 * @since 6.0.6
	 */
	public static @Nullable RequestMethod resolve(String method) {
		AssertUtil.isNull(method, "Method must not be null");
		switch (method) {
		case "GET":
			return GET;
		case "HEAD":
			return HEAD;
		case "POST":
			return POST;
		case "PUT":
			return PUT;
		case "PATCH":
			return PATCH;
		case "DELETE":
			return DELETE;
		case "OPTIONS":
			return OPTIONS;
		case "TRACE":
			return TRACE;
		default:
			return null;
		}
	}

	/**
	 * 将给定的 {@link HttpMethod} 解析为 {@code RequestMethod} 枚举值。 如果 {@code httpMethod}
	 * 没有对应的值，则返回 {@code null}。
	 * 
	 * @param httpMethod HTTP 方法对象
	 * @return 对应的 {@code RequestMethod}，如果未找到则返回 {@code null}
	 * @since 6.0.6
	 */
	public static @Nullable RequestMethod resolve(HttpMethod httpMethod) {
		AssertUtil.isNull(httpMethod, "HttpMethod must not be null");
		return resolve(httpMethod.name());
	}

	/**
	 * 返回与此 {@code RequestMethod} 对应的 {@link HttpMethod}。
	 * 
	 * @return 此请求方法对应的 HTTP 方法
	 * @since 6.0.6
	 */
	public HttpMethod asHttpMethod() {
		switch (this) {
		case GET:
			return HttpMethod.GET;
		case HEAD:
			return HttpMethod.HEAD;
		case POST:
			return HttpMethod.POST;
		case PUT:
			return HttpMethod.PUT;
		case PATCH:
			return HttpMethod.PATCH;
		case DELETE:
			return HttpMethod.DELETE;
		case OPTIONS:
			return HttpMethod.OPTIONS;
		case TRACE:
			return HttpMethod.TRACE;
		default:
			return null;
		}
	}
}
