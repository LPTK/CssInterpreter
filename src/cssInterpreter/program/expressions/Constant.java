package cssInterpreter.program.expressions;

import cssInterpreter.program.Type;
import cssInterpreter.runtime.RuntimeObject;

public class Constant extends Expression {
	final RuntimeObject value;
	
	public Constant(RuntimeObject value) {
		this.value = value;
	}
	@Override
	public Type getTypeRef() {
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
