package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.expressions.TupleExpression;

public class CallSignature {
	String name;
	TupleExpression params;
	
	public CallSignature(String name, TupleExpression params) {
		this.name = name;
		if (params == null)
			throw new IllegalArgumentException();
		this.params = params;
	}
	
	ParamBinding getBinding(Function f, int searchDepth) throws CompilerException {
		return params.getTypeRef().getBinding(f, searchDepth);
	}
	
	@Override
	public String toString() {
		//return "CallSignature: " + name + args;
		return name + params;
	}
}
