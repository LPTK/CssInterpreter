package cssInterpreter.compiler;

public class UnknownFunctionCompExc extends CompilerException {
	
	private static final long serialVersionUID = 1L;
	
	public UnknownFunctionCompExc(String msg) { // TODO: embed missing fct message
		super(msg);
	}

}
