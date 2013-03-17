package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.expressions.TupleExpression;

public class CallSignature {
	String name;
	TupleExpression args;
	
	ParamBinding getBinding(Function f) throws CompilerException {
		return args.getTypeRef().getBinding(f);
	}
	
	public CallSignature(String name, TupleExpression args) {
		this.name = name;
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
	}
	
	@Override
	public String toString() {
		//return "CallSignature: " + name + args;
		return name + args;
	}
}
