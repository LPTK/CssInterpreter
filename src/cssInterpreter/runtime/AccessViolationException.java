package cssInterpreter.runtime;

public class AccessViolationException extends ExecutionException {
	
	private static final long serialVersionUID = 1L;

	public AccessViolationException(String msg) {
		super(msg);
	}

}
