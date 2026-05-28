package cell.example.http.sse;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.dto.SSEMessage;
import cmn.http.servlet.mapping.RequestMappingIntf;
import reactor.core.publisher.Flux;

@ClassDeclare(
    label = "消息推送SSE接口",
    what = "提供SSE订阅和测试推送接口",
    why = "支持浏览器实时接收服务端消息",
    how = "通过/event-stream订阅接口接收实时事件，订阅方法返回Flux<String>",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/sse")
public interface IMessageSseHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "订阅消息流",
        what = "建立SSE连接并持续接收消息",
        why = "支持客户端实时订阅消息推送",
        how = "通过GET请求访问/example/sse/subscribe，Accept为text/event-stream，返回Flux<String>",
        inputs = {}
    )
    @RequestMapping(path = "/subscribe", method = RequestMethod.GET)
    Flux<String> subscribe() throws Exception;

    @MethodDeclare(
        label = "订阅结构化消息流",
        what = "建立SSE连接并返回包含事件元数据的消息",
        why = "支持客户端按event、id、retry和data解析事件",
        how = "通过GET请求访问/example/sse/structured，返回Flux<SSEMessage>",
        inputs = {}
    )
    @RequestMapping(path = "/structured", method = RequestMethod.GET)
    Flux<SSEMessage> structuredSubscribe() throws Exception;

    @MethodDeclare(
        label = "订阅原始SSE消息流",
        what = "返回由业务层完全控制格式的原始SSE文本",
        why = "支持多行data、自定义注释帧或特殊协议拼接",
        how = "通过GET请求访问/example/sse/raw，返回携带rawData的Flux<SSEMessage>",
        inputs = {}
    )
    @RequestMapping(path = "/raw", method = RequestMethod.GET)
    Flux<SSEMessage> rawSubscribe() throws Exception;

    @MethodDeclare(
        label = "发送测试消息",
        what = "向指定主题发送测试消息",
        why = "验证SSE推送链路是否正常",
        how = "通过POST请求访问/example/sse/publish",
        inputs = {
            @InputDeclare(name = "topic", label = "主题", desc = "消息主题", nullable = false),
            @InputDeclare(name = "message", label = "消息内容", desc = "推送内容", nullable = false)
        }
    )
    @RequestMapping(path = "/publish", method = RequestMethod.POST)
    void publish(String topic, String message) throws Exception;
}

package cell.example.http.sse.impl;

import cmn.http.cells.BasicCell_RequestMapping;
import cell.example.http.sse.IMessageSseHttpMapping;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class CMessageSseHttpMapping extends BasicCell_RequestMapping implements IMessageSseHttpMapping {

    @Override
    public Flux<String> subscribe() throws Exception {
        return Flux.interval(Duration.ofSeconds(1))
            .map(index -> "event-" + index);
    }

    @Override
    public Flux<SSEMessage> structuredSubscribe() throws Exception {
        return Flux.interval(Duration.ofSeconds(1))
            .map(index -> new SSEMessage()
                .setEvent("message")
                .setId(String.valueOf(index))
                .setRetry(3000L)
                .setData("payload-" + index));
    }

    @Override
    public Flux<SSEMessage> rawSubscribe() throws Exception {
        return Flux.interval(Duration.ofSeconds(1))
            .map(index -> SSEMessage.Raw(
                "event: custom\n"
                    + "id: " + index + "\n"
                    + "data: line-" + index + "\n"
                    + "data: extra-" + index + "\n\n"
            ));
    }

    @Override
    public void publish(String topic, String message) throws Exception {
        // 实际项目中可将消息写入事件总线、共享主题或任务池
    }
}
