package cell.basic.cmn.event;

import basic.cmn.dto.event.EventSubscriptionDto;
import basic.cmn.event.EventInvokeMode;
import basic.cmn.event.JsonSchemaDto;
import basic.cmn.event.SchemaType;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cmn.dto.Progress;
import cn.hutool.core.collection.CollUtil;
import com.leavay.nio.crpc.RpcMap;

import java.util.LinkedHashMap;

public class EventBusTest {

    public static void testRegistEventDefinition() throws Exception {
        //注册事件定义
        basic.cmn.dto.event.EventDefinitionDto eventDefinition = new basic.cmn.dto.event.EventDefinitionDto();
        JsonSchemaDto  schema = new JsonSchemaDto(true);
        LinkedHashMap<String,JsonSchemaDto> schemaMap = new LinkedHashMap<>();
        schemaMap.put("userId", new JsonSchemaDto().setTypeEnum(SchemaType.String));
        schema.setTypeEnum(SchemaType.Object);
        schema.setRequired(CollUtil.newArrayList("userId"));
        schema.setTitle("用户登录成功事件")
                .setProperties(schemaMap);
        eventDefinition.setEventSource("用户鉴权")
                .setEventName("用户登录成功")
                .setEventType("UserLoginSuccess")
                .setEventSchema(schema)
//                .setStatus("ACTIVE")
                .setEffectTime(System.currentTimeMillis())
                ;
        eventDefinition.setCode("用户鉴权_用户登录成功");
        IEventRegistry.get().createEventDefinition(eventDefinition);
    }

    public static void testTemporarySubscribeEvent() throws Exception {
        //注册临时事件订阅者
        basic.cmn.dto.event.EventSubscriptionDto subscription = new EventSubscriptionDto();
        subscription.setEventCode("用户鉴权_用户登录成功").setEventHandler(StudyEventHandler.class.getName())
                .setSubscriber("临时订阅者")
                .setInvokeMode(EventInvokeMode.同步.name())
                .setTemporary(true)
                ;
        subscription.setCode("临时订阅者_用户登录成功");
        IEventBus.get().subscribe(subscription);
    }

    public static void testPublishEvent() throws Exception {
        //在业务逻辑中发布事件
        basic.cmn.dto.event.EventDto event = new basic.cmn.dto.event.EventDto();
        event.setEventCode("用户鉴权_用户登录成功");
        event.setEventSource("用户鉴权");
        event.addPayload("userId", "jitUser_admin");
        RpcMap<Object> context = new RpcMap();
        try(IDao dao = IDaoService.newIDao()){
            context.put("$dao$",dao);
            context.put("$progress$",Progress.newTracer());
            //同步事件可传递context,在同个事务内操作，异步事件不支持传递context，可设置为null
            IEventBus.get().publish(context, event);
        }
    }


}
