package cssInterpreter.compiler;

public class UnknownFunctionException extends CompilerException {
	
	private static final long serialVersionUID = 1L;
	
	public UnknownFunctionException(Integer line, String msg) { // TODO: embed missing fct message
		//String lineMessage = line == null ? "" : "" ;
		//super(lineMessage+" "+msg);
		super((line == null ? "" : "["+line+"] ") + msg);
	}

}
