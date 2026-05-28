package cmn.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.Constants;
import com.leavay.common.util.Pair;

import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cmn.http.HttpStatus;
import cmn.http.exception.NotFoundException;
import cmn.http.exception.RequestMethodNotSupportedException;
import cmn.http.servlet.HttpResponseHandler;
import reactor.core.publisher.Flux;
import web.action.ActionResult;
import web.action.JSONActionResult2;
import web.action.OutputActionResult;
import web.mgr.WebUtil;


public class JsonHttpResponseHandler implements HttpResponseHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4385823295965345513L;
	
	protected String charset = Constants.UTF_8;
	protected String contentType = Constants.HTML_CONTENT;

	@Override
	public Object handle(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
//		ILogService.get().logResponseData(result);
		if(result instanceof NotFoundException) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.getWriter().println(((NotFoundException) result).getMessage());
			return null;
		}else if(result instanceof RequestMethodNotSupportedException) {
			response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
			response.getWriter().println(((RequestMethodNotSupportedException) result).getMessage());
			return null;
		}else if(result instanceof Throwable) {
			//如果是事件流，使用事件流的异常格式输出
			if (DefaultHttpResponseHandler.isSseRequest(request)) {
				DefaultHttpResponseHandler.handleSseError(request, response, (Throwable) result);
		        return null;
		    }
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			writeJson(response, ToolUtilities.getFullExceptionStack((Throwable)result, true));
			return null;
		}
		if (result instanceof JSONActionResult2) {
			if(((JSONActionResult2) result).isError()) {
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				writeJson(response, ((JSONActionResult2) result).getErrMsg());
			}else {
				Object content = ((JSONActionResult2) result).getContent();
				writeJson(response, content);
			}
			return null;
		}else if(result instanceof Flux) {
			Flux<?> flux = (Flux<?>) result;
			DefaultHttpResponseHandler.handleFluxStream(request, response, flux);
			return null;
		} else {
			if (result instanceof Pair) {
				Pair p = (Pair) result;
				if (p.left != null && p.right != null && (p.left instanceof String) && (p.right instanceof byte[])) {
					String charset = request.getParameter("CHARSET");
					String outputType = request.getParameter("OUTPUT_TYPE");
					if (outputType == null) {
						outputType = WebUtil.getOutputContentType((String) p.left);
					}
					ActionResult actionResult = outputFile(p, charset, outputType);
					actionResult.response(request, response);
					return actionResult;
				}else if (p.left != null && p.right != null && (p.left instanceof String) && (p.right instanceof InputStream)) {
					String charset = request.getParameter("CHARSET");
					String outputType = request.getParameter("OUTPUT_TYPE");
					if (outputType == null) {
						outputType = WebUtil.getOutputContentType((String) p.left);
					}
					ActionResult actionResult = outputFileStream(p, charset, outputType);
					actionResult.response(request, response);
					return actionResult;
				} else {
					writeJson(response, result);
					return null;
				}
			}else if(result instanceof File) {
				// 检查 Range 请求头
				String rangeHeader = request.getHeader("Range");
				String charset = request.getParameter("CHARSET");
				String outputType = request.getParameter("OUTPUT_TYPE");
				if(rangeHeader != null) {
					ActionResult actionResult = outputFileRange((File)result,rangeHeader, charset, outputType);
					actionResult.response(request, response);
					return actionResult;
				}else {
					File file = (File) result;
					FileInputStream fin = new FileInputStream(file);
					ActionResult actionResult = outputFileStream(new Pair<>(file.getName(),fin), charset, outputType);
					actionResult.response(request, response);
					return actionResult;
				}
			} else {
				writeJson(response, result);
				return null;
			}
		}
	}
	
	protected void writeJson(HttpServletResponse response,Object content) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding(charset);
		PrintWriter out = response.getWriter();
		if (content != null) {
			if(content instanceof String) {
				out.write((String)content);
			}else {
				try(IJson json = IJsonService.get().getPrettyJson()){
					out.write(json.toJson(content));
				}
			}
		}
		out.flush();
	}
	
	protected ActionResult outputFile(Pair<String, byte[]> p, String charset, String outputType) throws UnsupportedEncodingException {
		OutputActionResult result = new OutputActionResult(URLEncoder.encode(p.left,"UTF-8"), p.right);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}
	
	protected ActionResult outputFileStream(Pair<String, InputStream> p, String charset, String outputType) throws UnsupportedEncodingException {
		OutputStreamActionResult result = new OutputStreamActionResult(URLEncoder.encode(p.left,"UTF-8"), p.right);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}
	
	protected ActionResult outputFileRange(File file, String rangeHeader,String charset, String outputType) throws UnsupportedEncodingException {
		OutputStreamRangeActionResult result = new OutputStreamRangeActionResult(URLEncoder.encode(file.getName(),"UTF-8"),file, rangeHeader);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}

}