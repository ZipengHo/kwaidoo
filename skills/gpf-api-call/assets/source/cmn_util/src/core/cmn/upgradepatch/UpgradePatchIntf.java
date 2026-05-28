package cmn.upgradepatch;

import java.io.Serializable;

import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.dto.Progress;

@ClassDeclare(label = "升级补丁接口"
,what="GPF版本升级时，对于需要手动变更的内容，通过定义升级补丁脚本"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-08"
,updateTime = "2025-01-08")
public interface UpgradePatchIntf extends Serializable{
	@MethodDeclare(
			label = "检查当前补丁是否需要执行",
			what = "检查当前补丁是否需要执行"
			, why = "",
			how = "", 
			createTime = "2025-01-08"
			,  updateTime = "2025-01-08"
			, version = "1.0",
			developer = "陈晓斌",
			inputs = {} )
	public boolean needUpgrade()throws Exception;
	@MethodDeclare(
			label = "获取升级补丁需要变更内容",
			what = "获取升级补丁需要变更内容"
			, why = "",
			how = "", 
			createTime = "2025-01-08"
			,  updateTime = "2025-01-08"
			, version = "1.0",
			developer = "陈晓斌",
			inputs = { @InputDeclare(desc = "", label = "进度条通知对象", name = "prog") 
			} )
	public String getChangedList(Progress prog)throws Exception;
	@MethodDeclare(
			label = "执行升级",
			what = "执行升级操作"
			, why = "",
			how = "", 
			createTime = "2025-01-08"
			,  updateTime = "2025-01-08"
			, version = "1.0",
			developer = "陈晓斌",
			inputs = { @InputDeclare(desc = "", label = "进度条通知对象", name = "prog") 
			} )
	public void upgrade(Progress prog)throws Exception;
}
