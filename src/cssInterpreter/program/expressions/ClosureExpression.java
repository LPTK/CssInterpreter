package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.Scope;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.RuntimeObject;

public class ClosureExpression extends Expression {
	
	Scope scope;
	private TypeReference outputType;
	
	public ClosureExpression(Scope scope) {
		this.scope = scope;
	}
	
	@Override
	public TypeReference getTypeRef() {
		/**return scope.getType().getType();*/
		//return scope.getReturnType();
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return scope.getReturnKind();
	}*/
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) throws CompilerException {
		/**return scope.getType().getObjectRepresentation();*/
		//return Execution.getInstance().execute(null, scope);
		return Execution.getInstance().execute(parentOfThis, null, scope);
	}
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		super.resolveTypes(currentTypeInferenceId);
		//scope.resolveTypes(); // FIXME: not propagating id?
		scope.resolveTypes(currentTypeInferenceId);
		outputType = scope.getReturnType().withKind(scope.getReturnKind());
	}
	
	@Override
	public String toString() {
		return scope.toString();
	}
	
}

