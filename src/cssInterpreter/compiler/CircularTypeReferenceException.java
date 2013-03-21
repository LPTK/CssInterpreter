package cssInterpreter.compiler;

import cssInterpreter.program.TypeReference;

public class CircularTypeReferenceException extends CompilerException {
	
	private static final long serialVersionUID = 1L;
	
	public CircularTypeReferenceException(TypeReference tr) {
		super("Type reference "+tr+" refers to itself.");
	}

}
