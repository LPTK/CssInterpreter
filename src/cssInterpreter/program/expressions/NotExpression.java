package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.PrimitiveRuntimeObject;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class NotExpression extends Expression {
	
	Expression expr;
	private TypeReference outputType;
	
	public NotExpression(Expression expr) {
		this.expr = expr;
	}

	@Override
	public TypeReference getTypeRef() {
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return RefKind.VAL;
	}*/
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		//super.resolveTypes(currentTypeInferenceId);
		// Nothing to do here, type is already resolved
		expr.resolveTypes(currentTypeInferenceId);
		outputType = Execution.getInstance().BoolType.withKind(RefKind.VAL);
		outputType.resolve(currentTypeInferenceId);
	}
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) throws CompilerException {
		Reference resRef = expr.evaluate(parentOfThis);
		Execution.getInstance().stackLocal(resRef);
		RuntimeObject res = resRef.access(); //expr.evaluate(parentOfThis).access();
		assert res.isValue() && res.getValue() instanceof Boolean; // TODO: check this properly
		//return !((Boolean)res.getValue());
		return new Reference(new PrimitiveRuntimeObject<Boolean>(Execution.getInstance().BoolType, !((Boolean)res.getValue()), res.getParent(), false), RefKind.VAL);
	}
	
	@Override
	public String toString() {
		return "!"+expr;
	}
	
}

