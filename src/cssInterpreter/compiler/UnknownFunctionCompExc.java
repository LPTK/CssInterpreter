package cssInterpreter.compiler;

public class UnknownFunctionCompExc extends CompilerException {
	
	private static final long serialVersionUID = 1L;
	
	public UnknownFunctionCompExc(Integer line, String msg) { // TODO: embed missing fct message
		//String lineMessage = line == null ? "" : "" ;
		//super(lineMessage+" "+msg);
		super((line == null ? "" : "["+line+"] ") + msg);
	}

}
