package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class IfExpression extends Expression {
	Execution exec;
	Expression condition;
	Expression result;
	
	public IfExpression(Execution exec, Expression condition, Expression result) {
		this.exec = exec;
		this.condition = condition;
		this.result = result;
	}
	
	@Override
	public TypeReference getTypeRef() { // TODO: make the if/else expr return a true type
		return exec.VoidType;
	}

	@Override
	public RuntimeObject evaluate() throws CompilerException {
		RuntimeObject res = condition.evaluate();
		if (!res.getRuntimeType().conformsTo(exec.BoolType)) // TODO: really use "conformsTo"? (for classes, it's a mere equality)
			throw new CompilerException("Condition in expression "+this+" did not return a boolean-like");
		assert res.isValue() && res.getValue() instanceof Boolean;
		if (((Boolean)res.getValue()) == true)
			result.evaluate();
		return exec.getVoidobj();
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
	}
	
}

