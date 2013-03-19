package cssInterpreter.compiler;

public class NotSupportedException extends CompilerError {
	
	private static final long serialVersionUID = 1L;

	public NotSupportedException() {
	}
	
	public NotSupportedException(String msg) {
		super(msg);
	}

}
