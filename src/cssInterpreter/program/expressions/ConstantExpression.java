package cssInterpreter.program.expressions;

import cssInterpreter.program.Type;
import cssInterpreter.runtime.RuntimeObject;

public class ConstantExpression extends Expression {
	final RuntimeObject value;
	
	public ConstantExpression(RuntimeObject value) {
		this.value = value;
	}
	@Override
	public Type getTypeRefDelegate(int currentTypeInferenceId) {
		return value.getRuntimeType();
	}
	@Override
	public RuntimeObject evaluate() {
		//System.out.println("CCCCCCCCC "+value);
		return value;
	}
	public String toString() { return "const "+value.toString(); }
	//public String toString() { return "(const "+value.toString()+")"; }
	
}
