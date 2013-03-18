package cssInterpreter.runtime;

import cssInterpreter.program.Type;

public class RuntimeObjectRef extends RuntimeObject {
	
	RuntimeObject referenced;
	
	public RuntimeObjectRef(Type type, RuntimeObject parent, RuntimeObject referenced, boolean constant) {
		super(type, parent, constant);
		assert type == referenced.type;
		this.referenced = referenced;
	}
	
	public RuntimeObjectRef(Type type, RuntimeObject parent, boolean constant) {
		super(type, parent, constant);
		referenced = new RuntimeObject(type, parent, constant);
	}
	
	@Override
	public RuntimeObject readDelegate(int index) {
		return referenced.readDelegate(index);
	}
	
	@Override
	public void writeDelegate(int index, RuntimeObject obj) {
		referenced.writeDelegate(index, obj);
	}
	
	@Override
	public String toString() {
		return "&"+super.toString();
	}
	
}
