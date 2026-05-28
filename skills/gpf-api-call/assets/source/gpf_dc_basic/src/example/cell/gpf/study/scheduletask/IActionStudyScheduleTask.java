package cell.gpf.study.scheduletask;

import cell.CellIntf;
import cell.gpf.study.scheduletask.param.ActionStudyScheuldTaskParam;
import cmn.anotation.ClassDeclare;
import cmn.dto.intf.ScheduleTaskIntf;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
@ClassDeclare(label = "定时任务实现样例"
,what="定时任务实现样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-21"
,updateTime = "2025-01-21")
public interface IActionStudyScheduleTask extends CellIntf,ScheduleTaskIntf{

	@Override
	default Object execute(Object input) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		ActionStudyScheuldTaskParam param = (ActionStudyScheuldTaskParam)input;
		//输出定时任务日志
		tracer.info(param.getInputText());
		return null;
	}
}
