package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class IfExpression extends Expression {
	Execution exec;
	Expression condition;
	Expression result;
	private TypeReference outputType;
	
	public IfExpression(Execution exec, Expression condition, Expression result) {
		this.exec = exec;
		this.condition = condition;
		this.result = result;
	}
	
	@Override
	public TypeReference getTypeRef() { // TODO: make the if/else expr return a true type
		//return exec.VoidType;
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return RefKind.REF;
	}*/
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) throws CompilerException {
		//RuntimeObject res = condition.evaluate(parentOfThis).access();
		Reference resRef = condition.evaluate(parentOfThis);
		Execution.getInstance().stackLocal(resRef);
		RuntimeObject res = resRef.access();
		
		if (!res.getRuntimeType().conformsTo(exec.BoolType)) // TODO: really use "conformsTo"? (for classes, it's a mere equality)
			throw new CompilerException("Condition in expression "+this+" did not return a boolean-like");
		assert res.isValue() && res.getValue() instanceof Boolean;
		
		if (((Boolean)res.getValue()) == true)
			Execution.getInstance().stackLocal(result.evaluate(parentOfThis));
		
		return new Reference(exec.getVoidobj(), RefKind.REF);
	}
	
	@Override
	public String toString() {
		return "if "+condition+" then "+result;
	}
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		super.resolveTypes(currentTypeInferenceId);
		condition.resolveTypes(currentTypeInferenceId);
		result.resolveTypes(currentTypeInferenceId);
		outputType = exec.VoidType.withKind(RefKind.REF);
		outputType.resolve(currentTypeInferenceId);
	}
	
}

