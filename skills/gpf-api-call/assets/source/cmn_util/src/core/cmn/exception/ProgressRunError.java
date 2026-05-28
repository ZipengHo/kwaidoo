package cmn.exception;

public class ProgressRunError extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4241689133689102612L;

	public ProgressRunError()
    {
        super();
    }
	
	public ProgressRunError(Throwable cause)
    {
        super(cause);
    }
	
	public ProgressRunError(String message)
    {
        super(message);
    }
	
	public ProgressRunError(String message,Throwable cause)
    {
        super(message,cause);
    }
}
