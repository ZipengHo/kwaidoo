package cell.gpf.dc.http;

import cmn.util.JsonUtil;
import okhttp3.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ModelScopeFluxService {

    private final OkHttpClient httpClient;

    // !!! 替换为您的实际 ModelScope API URL
    // 示例（请以官方文档为准）：
    private static final String API_URL = "https://api-inference.modelscope.cn/v1/chat/completions";
    private static final String ACCESS_TOKEN = "ms-a8f76de4-6861-4b01-a505-e822d08fcb50";

    public ModelScopeFluxService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS) // 流式 API 需要较长的读取时间
                .build();
    }

    /**
     * 调用 ModelScope API 并以 Flux<String> 形式返回流式响应
     * @param requestPayload 模型的输入参数 Map
     * @return 包含 AI 实时响应内容的 Flux<String>
     */
    public Flux<String> streamApi(Map<String, Object> requestPayload) {

        return Flux.create(sink -> {
            // 确保在请求被订阅时，阻塞的 API 调用在一个独立的弹性线程中执行
            sink.onRequest(n -> {
                Schedulers.boundedElastic().schedule(() -> {
                    try {
                        executeCall(requestPayload, sink);
                    } catch (IOException e) {
                        sink.error(e);
                    }
                });
            });
        });
    }

    private void executeCall(Map<String, Object> requestPayload, FluxSink<String> sink)
            throws IOException {

        // 1. 构建请求体 (确保 payload 中设置了 stream: true)
        String requestBody = JsonUtil.toJson(requestPayload);

        RequestBody body = RequestBody.create(requestBody,
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .post(body)
                .build();

        // 2. 执行阻塞请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                sink.error(new IOException("ModelScope API failed: " + response.code() + ", " + response.body().string()));
                return;
            }

            // 3. 逐行读取流式响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8))) {
                String line;
                while (!sink.isCancelled() && (line = reader.readLine()) != null) {
                    // 假设 ModelScope 流式 API 返回的是 SSE 格式
                    if (line.startsWith("data: ")) {
                        String jsonChunk = line.substring(5).trim();
                        if ("[DONE]".equals(jsonChunk)) {
                            break;
                        }

                        // 【解析】提取内容并推送到 Flux
                        String content = parseContentFromChunk(jsonChunk);
                        if (!content.isEmpty()) {
                            sink.next(content);
                        }
                    }
                }
                sink.complete();
            }
        } catch (IOException e) {
            sink.error(e);
        }
    }

    // 占位符：实现具体的 ModelScope 流式 JSON 解析逻辑
    private String parseContentFromChunk(String jsonChunk) {
        // ... (使用 Jackson 从 jsonChunk 中提取真正的文本内容)
        return jsonChunk;
//        if (jsonChunk.contains("content")) {
//            Map<String, Object> map = JsonUtil.fromJson(jsonChunk, Map.class);
//            List<Map<String,Object>> choices = (List<Map<String, Object>>) map.get("choices");
//            if (choices != null &&  !choices.isEmpty()) {
//                for (Map<String, Object> choice : choices) {
//                    Map<String,Object> message =  (Map<String, Object>) choice.get("delta");
//                    if (message != null) {
//                        String content = (String) message.get("content");
//                        if (content != null) {
//                            return content;
//                        }
//                    }
//                }
//            }
//        }
//        return "";
    }

    public static void main(String[] args) {
        ModelScopeFluxService service = new ModelScopeFluxService();
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
        userMessage.put("content", "write a python quicksort");

        requestPayload.put("messages", Arrays.asList(
                        systemMessage,
                        userMessage
                )
        );
        // --- ⬆️ 修改结束 ⬆️ ---

        // 强制开启流式模式（如果 API 没有默认开启）
        requestPayload.put("stream", true);

        try {
            // 使用 blockLast() 或 block() 强制 main 线程等待 Flux 完成
            service.streamApi(requestPayload)
                    .doOnNext(System.out::print) // 在流上打印
                    .blockLast(); // 阻塞直到流完成 (或直到最后一条数据到达)

            // 另一个阻塞等待的方法：使用 CountDownLatch
            // 推荐使用 blockLast() 或 Mono.block()

        } catch (Exception e) {
            System.err.println("Flux execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}