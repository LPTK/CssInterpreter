package cssInterpreter.runtime;

public class ExecutionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ExecutionException(String msg) {
		super(msg);
	}
	
	public ExecutionException(Exception cause) {
		super(cause);
	}
	
}
