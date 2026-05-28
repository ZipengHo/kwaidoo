package cell.bap.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bap.cells.BasicCell;

public class CRequestDispather extends BasicCell implements IRequestDispatcher{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5064288094155085497L;
	HttpServletRequest req;
	HttpServletResponse resp;
	String path;
	public CRequestDispather(HttpServletRequest req,HttpServletResponse resp,String url) {
		this.req = req;
		this.resp = resp;
		this.path= url;
	}
	
	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forward() throws ServletException, IOException {
		req.getRequestDispatcher(path).forward(req, resp);
	}

	@Override
	public void include() throws ServletException, IOException {
		req.getRequestDispatcher(path).include(req, resp);
	}

}
