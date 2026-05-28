package cell.gpf.dc.http;

import bap.cells.Cells;
import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingIntf;
import gpf.dc.http.ProgressMessage;
import reactor.core.publisher.Flux;

@ClassDeclare(
		label = "事件流样例接口",
		what = "",
		why = "",
		how = "",
		developer = "陈晓斌",
		createTime = "2025-04-27",
		updateTime = "2025-04-27",
		version = "1.0"
)
@RequestMapping(path = "/gpfdc/app/events")
public interface IStudyEventRequestMapping extends CellIntf,RequestMappingIntf{

	static IStudyEventRequestMapping get() {
		return Cells.get(IStudyEventRequestMapping.class);
	}
	@MethodDeclare(
			label = "大模型聊天示例",
			what = "",
			why = "",
			how = "",
			inputs = {
					@InputDeclare(label = "用户输入", name = "userInput", desc = "")
			}
	)
	@RequestMapping(path = "/LLMChat", method = {RequestMethod.GET,RequestMethod.POST})
	public Flux<String> LLMChat(String userInput)throws Exception;

	@MethodDeclare(
			label = "启动任务",
			what = "",
			why = "",
			how = "",
			inputs = {
			}
	)
	@RequestMapping(path = "/startTask", method = {RequestMethod.GET,RequestMethod.POST})
	public String startTask()throws Exception;
	@MethodDeclare(
			label = "结束任务",
			what = "",
			why = "",
			how = "",
			inputs = {
					@InputDeclare(label = "任务ID", name = "taskUuid", desc = "")
			}
	)
	@RequestMapping(path = "/stopTask", method = {RequestMethod.GET,RequestMethod.POST})
	public void stopTask(String taskUuid)throws Exception;
	@MethodDeclare(
			label = "获取任务进度",
			what = "",
			why = "",
			how = "",
			inputs = {
					@InputDeclare(label = "任务ID", name = "taskUuid", desc = "")
			}
	)
	@RequestMapping(path = "/getTaskProgress", method = {RequestMethod.GET,RequestMethod.POST})
	public Flux<ProgressMessage> getTaskProgressStream(String taskUuid)throws Exception;
}
