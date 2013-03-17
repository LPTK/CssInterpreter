package cssInterpreter.program;

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
		super(new Signature(name, new FormalParameters()));
		this.type = type;
		this.index = index;
	}
	
	@Override
	public TypeReference getOutputType() {
		return type;
	}
	@Override
	public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
		//assert params.length == 0;
		//assert params == null;
		//assert params.getRuntimeType() == DumbInterpreter.VoidType;
		assert params.getRuntimeType().isTuple() && ((TupleType)params.getRuntimeType()).isEmpty();
		
		///System.out.println("--->"+index+" "+thisReference.read(index));
		
		//return thisReference.data[index];
		return thisReference.read(index);
	}
	
	@Override
	public boolean isSettable() {
		return true;
	}
	@Override
	public void setDelegate(RuntimeObject thisReference, RuntimeObject params, RuntimeObject value) {
		thisReference.write(index, value);
	}

}
