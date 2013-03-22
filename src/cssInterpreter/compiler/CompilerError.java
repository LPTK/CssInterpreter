package cssInterpreter.compiler;

public class CompilerError extends Error {
	
	private static final long serialVersionUID = 1L;

	public CompilerError() {
	}

	public CompilerError(String msg) {
		super(msg);
	}

}
