package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.Interpreter;
import cssInterpreter.program.expressions.Expression;
import cssInterpreter.runtime.Reference.RefKind;


public class TypeOf extends TypeReference { // FIXME: useless?
	
	Interpreter interp;
	Expression expr;
	Type inferredType;
	RefKind inferredKind;
	
	public TypeOf(Expression expr, Interpreter interp) {
		this.interp = interp;
		this.expr = expr;
	}
	/**
	@Override
	public Type getType(int currentTypeInferenceId) throws CompilerException {
		//return expr.getTypeRef().getType();
		if (inferredType == null)
			inferredType = expr.getTypeRef().getType(currentTypeInferenceId);
		return inferredType; // FIXME: doesn't address the actual problem of referring to formal params
	}
	*/
	
	/*
	Type getType() {
		try {
			return expr.getType();
		} catch (CompilerException e) {
			//throw new RuntimeException(e);
			throw new ExecutionException(e);
		}
	}
	
	@Override public String toString() {
		if (Execution.hasStarted())
			 return getType().toString();
		else return "TypeOf("+expr+")";
		//else return "TypeOf"+expr+"";
	}*/
	
	
//	@Override
//	public String toString() {
//		//try {
//			if (interp.hasFinishedReading())
//				 return getType().toString();
//			else return "TypeOf("+expr+")";
//		/*} catch (CompilerException e) {
//			throw new ExecutionException(e);
//		}*/
//	}
	
	@Override
	public String toString() {
		if (isResolved())
			 return getType().toString();
		else return "TypeOf("+expr+")";
	}

	@Override
	protected Type getTypeDelegate() {
		return inferredType;
	}
	
	@Override
	protected RefKind getKindDelegate() {
		return inferredKind;
	}
	

	@Override
	public boolean isResolved() {
		return inferredType != null;
	}

	@Override
	public Type resolveDelegate(int currentTypeInferenceId) throws CompilerException {
		expr.resolveTypes(currentTypeInferenceId);
		//return inferredType = expr.getTypeRef().resolve(currentTypeInferenceId);
		inferredType = expr.getTypeRef().resolve(currentTypeInferenceId);
		inferredKind = expr.getTypeRef().getKind();
		return inferredType;
	}
}


