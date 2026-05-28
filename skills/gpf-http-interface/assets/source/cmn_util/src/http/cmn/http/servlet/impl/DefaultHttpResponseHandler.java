package cmn.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.Utils;
import com.leavay.common.util.Constants;
import com.leavay.common.util.Pair;
import com.leavay.common.util.ToolUtilities;

import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.http.HttpStatus;
import cmn.http.dto.SSEMessage;
import cmn.http.exception.NotFoundException;
import cmn.http.servlet.HttpResponseHandler;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import web.action.ActionResult;
import web.action.JSONActionResult2;
import web.action.OutputActionResult;
import web.mgr.WebUtil;

public class DefaultHttpResponseHandler implements HttpResponseHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4385823295965345513L;
	
	public final static String LOG = DefaultHttpResponseHandler.class.getSimpleName();

	@Override
	public Object handle(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
//		ILogService.get().logResponseData(result);
		if (result instanceof NotFoundException) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		} else if (result instanceof Throwable) {
			//如果是事件流，使用事件流的异常格式输出
			if (isSseRequest(request)) {
				handleSseError(request, response, (Throwable) result);
		        return null;
		    }

		    ActionResult actionResult = handle(request, response, (Throwable) result);
		    actionResult.response(request, response);
		    return actionResult;
		}
		if (result instanceof ActionResult) {
			((ActionResult) result).response(request, response);
			return result;
		} else if (result instanceof Flux) {
			Flux<?> flux = (Flux<?>) result;
			handleFluxStream(request, response, flux);
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
//					ActionResult actionResult = outputFile(p, charset, outputType);
//					actionResult.response(request, response);
//					return actionResult;
					prepareDownload(request, response, (String) p.left, charset, outputType);
					downloadBytes(request, response, (byte[]) p.right);
					return null;
				} else if (p.left != null && p.right != null && (p.left instanceof String)
						&& (p.right instanceof InputStream)) {
					String charset = request.getParameter("CHARSET");
					String outputType = request.getParameter("OUTPUT_TYPE");
					if (outputType == null) {
						outputType = WebUtil.getOutputContentType((String) p.left);
					}
//					ActionResult actionResult = outputFileStream(p, charset, outputType);
//					actionResult.response(request, response);
//					return actionResult;
					prepareDownload(request, response, (String) p.left, charset, outputType);
					downloadStream(request, response, (InputStream) p.right);
					return null;
				} else {
					JSONActionResult2 actionResult = new JSONActionResult2(result);
					actionResult.response(request, response);
					return actionResult;
				}
			} else if (result instanceof File) {
				// 检查 Range 请求头
				String rangeHeader = request.getHeader("Range");
				String charset = request.getParameter("CHARSET");
				String outputType = request.getParameter("OUTPUT_TYPE");
				if (rangeHeader != null) {
//					ActionResult actionResult = outputFileRange((File)result,rangeHeader, charset, outputType);
//					actionResult.response(request, response);
//					return actionResult;
					File file = (File) result;
					prepareDownload(request, response, file.getName(), charset, outputType);
					downloadFileRange(request, response, rangeHeader, file);
					return null;
				} else {
					File file = (File) result;
					FileInputStream fin = new FileInputStream(file);
//					ActionResult actionResult = outputFileStream(new Pair<>(file.getName(),fin), charset, outputType);
//					actionResult.response(request, response);
//					return actionResult;
					prepareDownload(request, response, file.getName(), charset, outputType);
					downloadStream(request, response, fin);
					return null;
				}
			} else {
				JSONActionResult2 actionResult = new JSONActionResult2(result);
				actionResult.response(request, response);
				return actionResult;
			}
		}
	}
	
	public static boolean isSseRequest(HttpServletRequest request) {
	    String accept = request.getHeader("Accept");
	    return accept != null && accept.contains("text/event-stream");
	}
	
	public static void handleSseError(HttpServletRequest request,
			HttpServletResponse response,
			Throwable err) throws IOException {
		
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		
		ServletOutputStream out = response.getOutputStream();
		
		String sseError = formatSSE(err);
		
		out.write(sseError.getBytes(StandardCharsets.UTF_8));
		out.flush();
	}

	/**
	 * 处理Flux流
	 *
	 * @param request
	 * @param response
	 * @param flux
	 * @throws IOException
	 */
	public static void handleFluxStream(HttpServletRequest request, HttpServletResponse response, Flux<?> flux)
			throws IOException {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 设置 SSE 响应头
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");

		AsyncContext async = request.startAsync();
		async.setTimeout(0);
		ServletOutputStream out = response.getOutputStream();
		String clientId = request.getRemoteAddr() + "-" + request.getSession().getId() + "-" + UUID.randomUUID();
		SseConnection client = new SseConnection(clientId, async, out);
		SseConnectionManager.register(client);
		// 将心跳流与实际流合并
		// 1. 创建共享数据流
		Flux<String> dataFlux = flux.map(DefaultHttpResponseHandler::formatSSE)
		        .doOnSubscribe(s -> tracer.info("[SSE] dataFlux subscribed, clientId: {1}", clientId))
		        .doOnNext(d -> tracer.debug("[SSE] dataFlux emit: {1}", d))
		        .doOnComplete(() -> tracer.info("[SSE] dataFlux completed, clientId: {1}", clientId))
		        .doOnCancel(() -> tracer.warning("[SSE] dataFlux canceled, clientId: {1}", clientId))
		        .publish()
		        .refCount(1); // 确保共享订阅，避免被重复订阅干扰

		// 2. 创建一个当 dataFlux 完成时发出信号的 Mono
		Mono<Void> dataCompletion = dataFlux.then();

		// 3. 心跳流 - 每 15 秒发一次 “心跳”，直到 dataFlux 完成
		Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
		        .map(i -> ":\n\n")
		        .takeUntilOther(dataCompletion).map(i -> {
		            tracer.debug("[SSE] heartbeat {}", i);
		            return ":\n\n";
		        })
		        .takeUntilOther(dataCompletion)
		        .doOnCancel(() -> tracer.warning("[SSE] heartbeat canceled"))
		        .doOnComplete(() -> tracer.info("[SSE] heartbeat completed"))
		        .doOnSubscribe(s -> tracer.info("[SSE] heartbeat started"));

		// 4. 合并流，确保心跳与数据流并行发出，直到 dataFlux 完成
		Flux<String> sseFlux = Flux.merge(dataFlux, heartbeat)
				.doOnCancel(() -> tracer.warning("[SSE] sseFlux canceled"))
		        .doOnComplete(() -> tracer.info("[SSE] sseFlux completed"))
		        .doFinally(signal -> tracer.info("[SSE] sseFlux terminated with signal: " + signal));
		Disposable subscription = sseFlux.publishOn(Schedulers.boundedElastic()).subscribe(
                client::sendEvent, err -> {
					tracer.error(LOG, err);
					client.sendEvent(formatSSE(err));
					SseConnectionManager.close(client.getClientId());
				},
				() -> SseConnectionManager.close(client.getClientId()));
		client.setSubscription(subscription);

		// AsyncListener 监控断开/超时/异常
		async.addListener(new AsyncListener() {
			@Override
			public void onComplete(AsyncEvent event) {
				tracer.info("[AsyncListener] onComplete Event,close clientId:"+clientId);
				SseConnectionManager.close(clientId);
			}
			@Override
			public void onTimeout(AsyncEvent event) {
				tracer.info("[AsyncListener] onTimeout Event,close clientId:"+clientId);
				SseConnectionManager.close(clientId);
			}

			@Override
			public void onError(AsyncEvent event) {
				if(event.getThrowable() != null) {
					event.getThrowable().printStackTrace();
				}
				tracer.info("[AsyncListener] onError Event,close clientId:"+clientId);
				SseConnectionManager.close(clientId);
			}

			@Override
			public void onStartAsync(AsyncEvent event) {
				tracer.info("[AsyncListener] onStartAsync Event,clientId:"+clientId);
			}
		});

//		String sseSessionId = "SseSession_" + System.currentTimeMillis();
//		final AsyncContext async = request.startAsync();
//		async.setTimeout(0);
//
//		final PrintWriter writer = response.getWriter();
//		final AtomicReference<Disposable> subRef = new AtomicReference<>();
//
//		// 安全写入工具方法
//		Runnable closeResources = () -> {
//			Disposable s = subRef.getAndSet(null);
//			if (s != null && !s.isDisposed()) {
//				s.dispose();
//			}
//			try {
//				writer.close();
//			} catch (Exception ignore) {}
//			try {
//				async.complete();
//			} catch (Exception ignore) {}
//		};
//
//		// 将心跳流与实际流合并
//		Flux<String> heartbeat = Flux.interval(Duration.ofSeconds(15))
//				.map(i -> ":\n\n"); // SSE 心跳
//
//		Flux<String> dataFlux = flux
//				.map(data -> {
//					if (data instanceof String) {
//						return "data: " + data + "\n\n";
//					}else if(data instanceof SSEMessage) {
//						SSEMessage message = (SSEMessage) data;
//						StringBuffer strBuf = new StringBuffer();
//						if(!message.isEventEmpty()) {
//							strBuf.append("event: "+ message.getEvent()+"\n");
//						}
//						if(!message.isIdEmpty()) {
//							strBuf.append("id: "+ message.getId()+"\n");
//						}
//						if(!message.isRetryNull()) {
//							strBuf.append("retry: "+ message.getId()+"\n");
//						}
//						if(!message.isDataEmpty()) {
//							strBuf.append("data: " + JsonUtil.toJson(data) + "\n");
//						}
//						strBuf.append("\n");
//						return strBuf.toString();
//					} else {
//						return "data: " + JsonUtil.toJson(data) + "\n\n";
//					}
//				});
//
//		Flux<String> sseFlux = Flux.merge(dataFlux, heartbeat)
//				.onErrorResume(err -> Flux.just(
//						"event: error\ndata: " + err.getMessage() + "\n\n",
//						"event: complete\n\n"
//				));
//
//		Disposable sub = sseFlux.subscribe(
//				msg -> {
//					try {
//						writer.write(msg);
//						writer.flush();
//
//						// 检查客户端是否断开
//						if (writer.checkError()) {
//							tracer.info("⚠️ Client disconnected.");
//							closeResources.run();
//						}
//					} catch (Exception e) {
//						tracer.error("❌ SSE write failed: " + e.getMessage());
//						closeResources.run();
//					}
//				},
//				err -> {
//					System.err.println("⚠️ Flux error: " + err.getMessage());
//					try {
//						writer.write("event: error\ndata: " + err.getMessage() + "\n\n");
//						writer.flush();
//					} catch (Exception ignore) {}
//					closeResources.run();
//				},
//				() -> {
//					try {
//						writer.write("event: complete\n\n");
//						writer.flush();
//					} catch (Exception ignore) {}
//					closeResources.run();
//				}
//		);
//
//		subRef.set(sub);
	}

	public static String formatSSE(Object data) {
		if (data instanceof String) {
			return "data: " + data + "\n\n";
		} else if (data instanceof SSEMessage) {
			SSEMessage message = (SSEMessage) data;
			StringBuilder strBuf = new StringBuilder();
			boolean isAllEmpty = true;
			if (!message.isEventEmpty()) {
				isAllEmpty = false;
				strBuf.append("event: ").append(message.getEvent()).append("\n");
			}
			if (!message.isIdEmpty()) {
				isAllEmpty = false;
				strBuf.append("id: ").append(message.getId()).append("\n");
			}
			if (!message.isRetryNull()) {
				isAllEmpty = false;
				strBuf.append("retry: ").append(message.getRetry()).append("\n");
			}
			if (!message.isDataEmpty()) {
				isAllEmpty = false;
				strBuf.append("data: ").append(JsonUtil.toJson(message.getData())).append("\n");
			}
			if(!isAllEmpty) {
				strBuf.append("\n");
			}else if(!CmnUtil.isStringEmpty(message.getRawData())){
				strBuf.append(message.getRawData());
			}
			return strBuf.toString();
		} else if(data instanceof Throwable){
			Throwable rootCause = ToolUtilities.getExceptionRootCause((Throwable) data);
			String errorMsg = rootCause != null ? rootCause.getMessage() : ToolUtilities.getExceptionMessage((Throwable) data);
			return "event: error\ndata: " + errorMsg + "\n\n";
		} else {
			return "data: " + JsonUtil.toJson(data) + "\n\n";
		}
	}

	protected void prepareDownload(HttpServletRequest request, HttpServletResponse response, String fileName,
			String charset, String outputType) throws Exception {
		// ----------------------------------------------------
		// 步骤 1: 对文件名进行 URL 编码（使用 UTF-8）
		// ----------------------------------------------------

		// 使用 URLEncoder 对文件名进行编码，将空格转为 %20 (更安全，而不是 +)
		// 注意：URLEncoder.encode 在 Java 13+ 会自动使用 UTF-8，但在 JDK 8 必须指定编码。
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20"); // 关键：将
																														// +
																														// 号替换回
																														// %20

		// ----------------------------------------------------
		// 步骤 2: 设置 Content-Disposition 头部
		// ----------------------------------------------------

		// 方案 A: 兼容现代浏览器 (使用 filename* 避免编码问题)
		// RFC 6266 推荐使用 filename* 参数来处理非 ASCII 字符和空格
		String contentDisposition = String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", fileName, // fallback
																												// (用于旧版浏览器)
				encodedFileName);

		// 方案 B: (可选) 针对特定浏览器（如果上面的方案不生效）
		/*
		 * if (userAgent.contains("MSIE") || userAgent.contains("Trident")) { // 针对 IE 和
		 * Edge 的特殊处理 contentDisposition = "attachment; filename=" + encodedFileName; }
		 * else { contentDisposition = "attachment; filename*=UTF-8''" +
		 * encodedFileName; }
		 */

		response.setHeader("Content-Disposition", contentDisposition);

		// ----------------------------------------------------
		// 步骤 3: 设置 Content-Type 和内容长度
		// ----------------------------------------------------
		if (!CmnUtil.isStringEmpty(outputType)) {
			response.setContentType(outputType);
		} else {
			response.setContentType("application/octet-stream"); // 通用文件类型
		}
		// response.setContentLengthLong(fileSize); // 设置文件大小
		if (!CmnUtil.isStringEmpty(charset)) {
			response.setCharacterEncoding(charset);
		} else {
			response.setCharacterEncoding(Constants.UTF_8);
		}

	}

	public void downloadBytes(HttpServletRequest request, HttpServletResponse response, byte[] content)
			throws IOException {
		ServletOutputStream out = response.getOutputStream();
		if (content != null) {
			out.write(content);
		}
		out.flush();
	}

	public void downloadStream(HttpServletRequest request, HttpServletResponse response, InputStream ins)
			throws IOException {
		ServletOutputStream out = response.getOutputStream();
		try {
			// 缓冲区大小
			byte[] buffer = new byte[4096];
			int bytesRead;
			// 读取数据并写入文件
			while ((bytesRead = ins.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				out.flush();
			}
			out.flush();
		} finally {
			Utils.close(ins);
		}
	}

	public void downloadFileRange(HttpServletRequest request, HttpServletResponse response, String rangeHeader,
			File file) throws IOException {
		if (file != null) {
			// 解析 Range（如 "bytes=0-1023"）
			long fileLength = file.length();
			long start, end;
			if (rangeHeader.startsWith("bytes=")) {
				String[] ranges = rangeHeader.substring(6).split("-");
				start = Long.parseLong(ranges[0]);
				end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileLength - 1;
			} else {
				start = 0;
				end = fileLength - 1;
			}
			// 设置响应头（206 Partial Content）
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
			response.setContentLengthLong(end - start + 1);

			// 跳过已下载的部分
			try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
				raf.seek(start);
				ServletOutputStream out = response.getOutputStream();
				try {
					byte[] buffer = new byte[4096];
					long remaining = end - start + 1;
					int bytesRead;
					while (remaining > 0
							&& (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
						out.write(buffer, 0, bytesRead);
						remaining -= bytesRead;
					}
					out.flush();
				} finally {
					Utils.close(raf);
				}
			}
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
	}

	protected ActionResult handle(HttpServletRequest request, HttpServletResponse response, Throwable err) {

		int state = JSONActionResult2.ERROR;
		String errCode = "";
		String errBrief = "";
		String errMsg = "";
		BaseException baseException = ToolUtilities.getCauseExcpetion(err, BaseException.class);
		if (baseException instanceof NotFoundException) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		if (baseException != null) {
			ErrorLevel errorLevel = baseException.getErrorLevel();
			if (errorLevel == ErrorLevel.INFO) {
				state = JSONActionResult2.ALERT;
			} else if (errorLevel == ErrorLevel.WARN || errorLevel == ErrorLevel.FATAL) {
				state = JSONActionResult2.ERROR;
			}
			errCode = baseException.getErrorCode();
			errBrief = baseException.getMessage();
			errMsg = ToolUtilities.getExceptionStatck(err);
		} else {
			Throwable rootCause = ToolUtilities.getExceptionRootCause(err);
			if (!CmnUtil.isStringEmpty(rootCause.getMessage())) {
				errBrief = rootCause.getMessage();
			} else {
				errBrief = ToolUtilities.getExceptionMessage(rootCause);
			}
			errMsg = ToolUtilities.getExceptionStatck(err);
		}

		JSONActionResult2 result = new JSONActionResult2();
		result.setState(state);
		result.setErrCode(errCode);
		result.setErrBrief(errBrief);
		result.setErrMsg(errMsg);
		return result;
	}

	protected ActionResult outputFile(Pair<String, byte[]> p, String charset, String outputType)
			throws UnsupportedEncodingException {
		OutputActionResult result = new OutputActionResult(URLEncoder.encode(p.left, "UTF-8"), p.right);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}

	protected ActionResult outputFileStream(Pair<String, InputStream> p, String charset, String outputType)
			throws UnsupportedEncodingException {
		OutputStreamActionResult result = new OutputStreamActionResult(URLEncoder.encode(p.left, "UTF-8"), p.right);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}

	protected ActionResult outputFileRange(File file, String rangeHeader, String charset, String outputType)
			throws UnsupportedEncodingException {
		OutputStreamRangeActionResult result = new OutputStreamRangeActionResult(
				URLEncoder.encode(file.getName(), "UTF-8"), file, rangeHeader);
		if (charset != null) {
			result.setCharset(charset);
		}
		result.setContentType(outputType);
		return result;
	}

}