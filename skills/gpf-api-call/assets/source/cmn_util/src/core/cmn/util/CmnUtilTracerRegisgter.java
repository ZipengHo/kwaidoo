package cmn.util;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cell.cmn.http.CHttpRequestService;
import cmn.enums.TraceLevel;
import cmn.servlet.CommonServlet;
import cmn.servlet.SSEStreamServlet;

public class CmnUtilTracerRegisgter implements TraceLevelRegister{

	@Override
	public Map<String, TraceLevel> regist() {
		return ImmutableMap.of(CHttpRequestService.class.getSimpleName(),TraceLevel.INFO
				,CommonServlet.class.getSimpleName(),TraceLevel.INFO
				,SSEStreamServlet.class.getSimpleName(),TraceLevel.INFO);
	}

}