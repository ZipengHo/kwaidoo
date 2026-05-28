package cell.bap.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bap.cells.BasicCell;

public class CHttpServlet extends BasicCell implements IHttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 580575041590999232L;
	HttpServletRequest req;
	HttpServletResponse resp;
	public CHttpServlet(HttpServletRequest req,HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}
	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public IHttpServletRequest getRequest() {
		return new CHttpServletRequest(req);
	}
	
	@Override
	public IHttpServletResponse getResponse() {
		return new CHttpServletResponse(resp);
	}

	@Override
	public IRequestDispatcher getRequestDispatcher(String url) {
		return new CRequestDispather(req, resp, url);
	}

}
