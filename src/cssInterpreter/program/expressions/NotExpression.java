package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.PrimitiveRuntimeObject;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class NotExpression extends Expression {
	
	Expression expr;
	
	public NotExpression(Expression expr) {
		this.expr = expr;
	}

	@Override
	public TypeReference getTypeRef() {
		return Execution.getInstance().BoolType;
	}

	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		//super.resolveTypes(currentTypeInferenceId);
		// Nothing to do here, type is already resolved
	}
	
	@Override
	public RuntimeObject evaluate() throws CompilerException {
		RuntimeObject res = expr.evaluate();
		assert res.isValue() && res.getValue() instanceof Boolean; // TODO: check this properly
		//return !((Boolean)res.getValue());
		return new PrimitiveRuntimeObject<Boolean>(Execution.getInstance().BoolType, !((Boolean)res.getValue()), res.getParent(), false);
	}
	
	@Override
	public String toString() {
		return "!"+expr;
	}
	
}
