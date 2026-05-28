package cell.gpf.study.schedulttask;

import org.nutz.dao.Cnd;

import com.cdao.dto.CPager;

import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.scheduletask.IScheduleTaskService;
import cell.gpf.study.scheduletask.IActionStudyScheduleTask;
import cell.gpf.study.scheduletask.param.ActionStudyScheuldTaskParam;
import cmn.dto.scheduletask.ScheduleTaskDto;
import cmn.dto.scheduletask.timerule.SecondCycleRule;
import cmn.dto.scheduletask.timerule.TimeRuleAnd;
import web.dto.MultiPageResultDto;

public class IStudyScheduleTask {

	/**
	 * 时间规则填写规范：
★.常用规则：  FlowScheduleRule
	1、穷举，直接写出具体的值，以逗号分隔，例如:2011,2012,2013
	2、指定范围，以减号"-"连接，例如：2011-2013,1-5,0-9
	3、指定整除数，以/号开头，表示能被此数整除即满足条件，
	比如希望每隔15分钟可以写成/15,表示0,15,30,45,60均符合条件
	4、支持倒数第几，以减号"-"开头的负数，比如：-1可表示每月最后一天
	（注：倒数不支持范围,即不能写成20--1或者-2--1这样。也不支持年）
	5、 支持!=（不等于），>（大于），>=（大于等于），<（小于），<=（小于等于）
	（注：“不等于”通常不能在单一表达式中混用，否则等于无效）
	6、以上这些均可自由组合，以逗号分隔
	
	举例：
	以年为例："2011-2013,2014,2016"表示2011到2013以及2014、2016这些都符合条件
	以月为例：“1,/4,11"表示1月、所有能被4整除的月份以及11月
	以天为例："1-5,15,-1"表示1到5号，15号以及最后一天
	以分钟为例："0-10,/15,59"表示0到10每分钟以及所有能整除15的分钟以及59分这个时刻都满足条件

★.时间点规则：  TimePointRule

★.循环间隔规则：执行完成间隔n分钟后继续执行  TimeCycleRule

★.秒级规则：执行完成间隔n秒后继续执行  SecondCycleRule

	 */
	public static void main(String[] args) throws Exception {
		IScheduleTaskService taskSrv = IScheduleTaskService.get();
		
		String taskName = "测试创建定时任务";
		try(IDao dao = IDaoService.newIDao()){
			//常用规则
//			FlowScheduleRule normalRule = new FlowScheduleRule();
//			//时间点规则
//			TimePointRule timeRule2 = new TimePointRule();
//			//循环间隔规则
//			TimeCycleRule cycleRule = new TimeCycleRule();
			//秒级规则
			SecondCycleRule secondRule = new SecondCycleRule();
			secondRule.setStdTime(System.currentTimeMillis());
			secondRule.setIgnoreDate(true);
			secondRule.setPeriodicSecond(30);
			//设置定时任务的输入参数
			ActionStudyScheuldTaskParam param = new ActionStudyScheuldTaskParam().setInputText("输入参数");
			ScheduleTaskDto task = new ScheduleTaskDto();
			task.setTaskName(taskName)
				.setCategory("分类1")
				//设置定时任务的执行类
				.setExecuteClass(IActionStudyScheduleTask.class.getName())
				//设置定时任务执行类的入参，具体写法见IActionStudyScheduleTask
				.setExecuteParam(param)
				//设置定时任务规则
				.setTimeRule(new TimeRuleAnd(secondRule))
				;
			//创建定时任务
			task = taskSrv.createTask(dao, task);
			dao.commit();
			//修改定时任务
			task = taskSrv.updateTask(dao, task);
			dao.commit();
			Cnd cnd = Cnd.where(ScheduleTaskDto.TaskName,"=",taskName);
			MultiPageResultDto<ScheduleTaskDto> rs = taskSrv.queryTaskPage(dao, cnd, new CPager(0, 1));
			if(rs.hasData()) {
				ScheduleTaskDto task1 = rs.getTableData().get(0);
				//执行定时任务
				taskSrv.executeTask(task1);
				//激活定时任务
//				taskSrv.activeTask(task1);
				//挂起定时任务
//				taskSrv.suspendTask(task1);
				//删除定时任务
//				taskSrv.deleteTask(dao, task1.getUuid());
//				dao.commit();
			}
		}
	}
}
