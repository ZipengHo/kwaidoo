package cmn.exception;

public class SuspendTaskExeption extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4847864050501039472L;

	public SuspendTaskExeption() {
	}

	public SuspendTaskExeption(Throwable cause) {
		super(cause);
	}

	public SuspendTaskExeption(String message) {
		super(message);
	}

	public SuspendTaskExeption(String message, Throwable cause) {
		super(message, cause);
	}

}