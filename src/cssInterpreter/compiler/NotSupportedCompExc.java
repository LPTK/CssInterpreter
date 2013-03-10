package cssInterpreter.compiler;

public class NotSupportedCompExc extends CompilerError {
	
	private static final long serialVersionUID = 1L;

	public NotSupportedCompExc() {
	}
	
	public NotSupportedCompExc(String msg) {
		super(msg);
	}

}
