package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.runtime.AccessViolationException;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class FieldAccessFunction extends Function { // TODO: add checks for the return value is of the right type
	//final RuntimeObject thisReference;
	TypeReference type;
	int index;
	/*
	public RuntimeField(RuntimeObject thisReference, int index) {
		this.thisReference = thisReference;
		this.index = index;
	}*/
	public FieldAccessFunction(String name, TypeReference type, int index) {
		super(new Signature(name, new FormalParameters()), RefKind.REF);
		this.type = type;
		this.index = index;
	}
	
	@Override
	public TypeReference getOutputType() {
		return type;
	}
	@Override
	public Reference evaluateDelegate(RuntimeObject thisReference, RuntimeObject args) {
		//assert params.length == 0;
		//assert params == null;
		//assert params.getRuntimeType() == DumbInterpreter.VoidType;
		assert args.getRuntimeType().isTuple() && ((TupleType)args.getRuntimeType()).isEmpty();
		
		///System.out.println("--->"+index+" "+thisReference.read(index));
		
		//return thisReference.data[index];
		return Reference.REF(thisReference.read(index));
	}
	
	@Override
	public boolean isSettable() {
		return true;
	}
	@Override
	public void setDelegate(RuntimeObject thisReference, RuntimeObject params, Reference valueRef) throws CompilerException {
		try {
			thisReference.write(index, valueRef.access());
		} catch (AccessViolationException ave) { // TODO: useful?
			System.err.println("Error in call to "+this);
			throw ave;
		}
	}

}
