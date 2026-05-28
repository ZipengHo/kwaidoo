package gpf.study.extdump;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.Utils;

import bap.md.java.CJavaProject;
import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.dto.Progress;
import cn.hutool.core.util.ZipUtil;
import gpf.dc.concrete.AbsProjectExtDumpConfig;
@ClassDeclare(label = "工程扩展构建数据接口配置"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-09-18"
,updateTime = "2025-09-18")
public class StudyProjectExtDumpConfig extends AbsProjectExtDumpConfig{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3407622381937701738L;
	@FieldDeclare(label = "业务域",desc = "")
	String domain;

	@Override
	public byte[] exportDumpData(Progress prog,CJavaProject project) throws Exception {
		prog.setMessage("导出扩展Dump包", true);
		File file = new File("D:/数据-文档管理.zip");
		return Utils.getFileBytes(file);
	}

	@Override
	public void importDumpData(Progress prog,byte[] extDumpData) throws Exception {
		// TODO Auto-generated method stub
		File file = new File("./temp/ExtDump_"+ToolUtilities.allockUUIDWithUnderline());
		file.getParentFile().mkdirs();
		try (ByteArrayInputStream in = new ByteArrayInputStream(extDumpData)){
			prog.setMessage("导入扩展Dump包", true);
			ZipUtil.unzip(in, file, Charset.forName("UTF-8"));
		}finally {
			ToolUtilities.deleteFileFolder(file);
		}
	}

}
