package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.runtime.AccessViolationException;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
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
	private RefKind retKind;
	
	public Function(Signature signature, RefKind retKind) { // TODO handle unresolved retKinds when types are not yet resolved (cf: "return expr")
		this.signature = signature;
		this.retKind = retKind;
	}
	public boolean isSettable() {
		return false;
	}
	public final void set(RuntimeObject thisReference, RuntimeObject params, Reference valueRef) throws CompilerException {
		if (!isSettable())
			throw new ExecutionException("Function "+this+" has no setter defined");
		setDelegate(thisReference, params, valueRef);
	}
	public void setDelegate(RuntimeObject thisReference, RuntimeObject args, Reference valueRef) throws CompilerException { assert false; }
	
	public abstract TypeReference getOutputType();
	//public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject[] params);
	
	//public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject args);
	
	public final Reference evaluate(RuntimeObject thisReference, RuntimeObject args) throws CompilerException {
		if (!args.getRuntimeType().getBinding(this, 0).isSuccessful())
		//if (!args.getRuntimeType().conformsTo(  type  )) // FIXME: functions MUST have an input type/trait rather than (just) "FormalParams"
			throw new AccessViolationException("Type "+args.getRuntimeType()+" does not conform with "+this);
		
		//args.setIsAnArg(true);
		
		return evaluateDelegate(thisReference, args);
	}
	public abstract Reference evaluateDelegate(RuntimeObject thisReference, RuntimeObject args);
	
	
	@Override
	public String toString() {
		return "Function  "+signature+" -> "+getOutputType(); //+" on type "+type; //+" with "+args;
	}
	
	public Signature getSignature() {
		return signature;
	}
	public Type getInputType() {
		return signature.getType();
	}
	
	
	
	public void resolve(int currentTypeInferenceId) throws CompilerException {
		// TODO useful?
		signature.type.resolve(currentTypeInferenceId);
		for (NamedType nt: signature.params.namedTypes) {
			nt.type.resolve(currentTypeInferenceId);
		}
	}
	public RefKind getRetKind() {
		return retKind;
	}
	
	
}









