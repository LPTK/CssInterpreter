package cssInterpreter.program;

import cssInterpreter.runtime.RuntimeObject;

public class Pointer extends RuntimeObject {
	
	RuntimeObject pointee;
	
	public Pointer(RuntimeObject pointee) { // TODO: handle possible constness of refs (ptrs)
		super(pointee.getRuntimeType().getPointerTypeRef().getType(), null, true);
		this.pointee = pointee;
	}
	
	public RuntimeObject access() {
		return pointee;
	}
	
	@Override
	public void destructDelegate() {
		// Nothing to be done, as we've just got a non-owning reference
	}
	
}

