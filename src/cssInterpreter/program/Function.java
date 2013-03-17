package cssInterpreter.program;

import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

public abstract class Function {
	enum FieldType {
		VAL_ATTR,
		RVAL_ATTR,
		REF_ATTR,
		DEF,
		NONE,
	}
	FieldType fieldType = FieldType.NONE;
	boolean meta = false;
	Signature signature;
	public Function(Signature signature) {
		this.signature = signature;
	}
	public boolean isSettable() {
		return false;
	}
	public final void set(RuntimeObject thisReference, RuntimeObject params, RuntimeObject value) {
		if (!isSettable())
			throw new ExecutionException("Function "+this+" has no setter defined");
		setDelegate(thisReference, params, value);
	}
	public void setDelegate(RuntimeObject thisReference, RuntimeObject args, RuntimeObject value) { assert false; }
	
	public abstract TypeReference getOutputType();
	//public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject[] params);
	public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject args);
	
	@Override
	public String toString() {
		return "Function  "+signature+" -> "+getOutputType(); //+" on type "+type; //+" with "+args;
	}
	
	public Signature getSignature() {
		return signature;
	}
	
}
