package cssInterpreter.runtime;

import cssInterpreter.program.Type;


public class RuntimeObjectRVal extends RuntimeObjectRef {

	public RuntimeObjectRVal(Type type, RuntimeObject parent, boolean constant) {
		super(type, parent, constant);
	}
	
	@Override
	public void destruct() {
		referenced.destruct();
		super.destruct();
	}
	
}
