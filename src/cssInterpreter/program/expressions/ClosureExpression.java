package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.Scope;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.RuntimeObject;

public class ClosureExpression extends Expression {
	
	Scope scope;
	
	public ClosureExpression(Scope scope) {
		this.scope = scope;
	}
	
	@Override
	public TypeReference getTypeRef() throws CompilerException {
		return scope.getType();
	}

	@Override
	public RuntimeObject evaluate() throws CompilerException {
		return scope.getType();
	}

}
