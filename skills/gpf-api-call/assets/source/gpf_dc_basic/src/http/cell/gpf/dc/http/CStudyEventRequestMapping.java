package cell.gpf.dc.http;

import cmn.dto.Progress;
import cmn.http.cells.BasicCell_RequestMapping;
import com.kwaidoo.ms.tool.ToolUtilities;
import gpf.dc.http.ProgressMessage;
import gpf.dc.http.SSETaskRunnable;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CStudyEventRequestMapping extends BasicCell_RequestMapping implements IStudyEventRequestMapping {

	/**
	 *
	 */
	private static final long serialVersionUID = -6897639601944264584L;

	@Override
	public Flux<String> LLMChat(String userInput) throws Exception {
		String apiUrl = "https://api-inference.modelscope.cn";
		String accessToken = "ms-fdeb1ec6-8df1-46e4-9df8-c4d1793e0977";
		LLMFluxService service = new LLMFluxService(apiUrl,accessToken);
		Map<String, Object> requestPayload = new LinkedHashMap<>() ;
		requestPayload.put("model", "Qwen/Qwen2.5-Coder-32B-Instruct");
		// --- ⬇️ 修改这一部分 ⬇️ ---

		// 构造第一个对话元素：System 角色
		Map<String, String> systemMessage = new HashMap<>();
		systemMessage.put("role", "system");
		systemMessage.put("content", "You are an expert programmer.");
		// 最好给 system role 一个非空内容

		// 构造第二个对话元素：User 角色
		Map<String, String> userMessage = new HashMap<>();
		userMessage.put("role", "user");
		userMessage.put("content", userInput);

		requestPayload.put("messages", Arrays.asList(
						systemMessage,
						userMessage
				)
		);
		// --- ⬆️ 修改结束 ⬆️ ---

		// 强制开启流式模式（如果 API 没有默认开启）
		requestPayload.put("stream", true);

		return service.streamApi(requestPayload);
	}
	@Override
	public String startTask() throws Exception {
		SSEProgress progress = new SSEProgress();
		SSETaskRunnable task = new SSETaskRunnable(progress,this,"testProgress",new Object[]{Progress.wrap(progress)});
		ISSETaskPool.get().run(task);
		return task.getKey();
	}

	public void testProgress(Progress prog) throws Exception {
		long start = System.currentTimeMillis();
		int index = 0;
		int size = 100;
		while (index < size) {
			prog.assertCancel();
			prog.sendProcess(((index + 1) * 100 / size), "任务进行中...", true);
			ToolUtilities.sleep(500);
			index++;
			if (index > 50) {
				throw new Exception("测试进度通知异常！");
			}
		}
	}

	@Override
	public void stopTask(String taskUuid) throws Exception {
		ISSETaskPool.get().terminate(taskUuid);
	}

	@Override
	public Flux<ProgressMessage> getTaskProgressStream(String taskUuid) throws Exception {
		return ISSETaskPool.get().getProgress(taskUuid);
	}

}

