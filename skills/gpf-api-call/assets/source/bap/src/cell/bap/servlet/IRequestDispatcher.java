package cell.bap.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import cell.ResourceCellIntf;

public interface IRequestDispatcher extends ResourceCellIntf{

	public void forward()throws ServletException, IOException;
	public void include()throws ServletException, IOException;
}
