package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class ConstantExpression extends Expression {
	final RuntimeObject value;
	private TypeReference outputType;
	
	public ConstantExpression(RuntimeObject value) {
		this.value = value;
	}
	
	@Override
	public TypeReference getTypeRef() {
		//return value.getRuntimeType();
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return RefKind.REF;
	}*/
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) {
		//System.out.println("CCCCCCCCC "+value);
		//return value.getValRef();
		//return new Reference(value, getRetKind());
		return new Reference(value, RefKind.REF);
	}
	public String toString() { return "const "+value.toString(); }
	//public String toString() { return "(const "+value.toString()+")"; }
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		//super.resolveTypes(currentTypeInferenceId);
		// Nothing to do here, type is already resolved
		outputType = value.getRuntimeType().withKind(RefKind.REF);
	}
	
}
