package cssInterpreter.runtime;

import java.util.Arrays;
import java.util.Iterator;

import cssInterpreter.program.Type;

public 
class RuntimeObject  {
	
	Type type;
	boolean constant;
	protected RuntimeObject[] attributes;
	//RuntimeObject parent;
	boolean destructed;
	
	RuntimeObject associatedArgs;
	
	
	//public RuntimeObject(Type type, RuntimeObject parent, boolean constant) {
	/*
	 * associatedArgs represent the arguments passed to a method if this object is the instanciated scope of this method
	 * should be null in any other cases
	 * 
	 */
	public RuntimeObject(Type type, RuntimeObject associatedArgs, boolean constant) {
		this.type = type;
		//this.parent = parent;
		this.associatedArgs = associatedArgs;
		this.constant = constant;
		attributes = new RuntimeObject[type.getAttributeTypes().length];
	}
	/*
	
	public RuntimeObject(Type type, RuntimeObject params, RuntimeObject parent, boolean constant) {
		this(type, parent, constant);
		//if (!params.getRuntimeType().conformsTo(type.g)) throw new IllegalArgumentException();
		
		
		
		// TODO use params !?
		
		assert false;
		
		
		
		
		
		
		
	}*/
	
	public boolean isValue() {
		return false;
	}
	
	public Object getValue() throws ExecutionException {
		throw new ExecutionException("Cannot get the value of a non-value object");
	}
	
	public final RuntimeObject read(int index) { // TODO: check types?
		return readDelegate(index);
	}
	
	public Iterator<RuntimeObject> values() {
		//return new ArrayList<RuntimeObject>(attributes).iterator();
		return Arrays.asList(attributes).iterator();
	}
	
	public final void write(int index, RuntimeObject obj) { // TODO: check types?
		if (constant)
			throw new AccessViolationException("Cannot write to a constant object");
		if (destructed)
			throw new AccessViolationException("Cannot write to a destructed object");
		writeDelegate(index, obj);
	}
	public RuntimeObject readDelegate(int index) {
		if (attributes[index] == null)
			throw new AccessViolationException("Value "+type.getAttributeNames()[index]+" (at index "+index+") in "+this+" was not initialized when read");
		if (destructed)
			throw new AccessViolationException("Cannot write to a destructed object");
		return attributes[index];
	}
	
	public Type getRuntimeType() {
		return type;
	}
	public void writeDelegate(int index, RuntimeObject obj) {
		attributes[index] = obj;
	}
	/*
	public RuntimeObject getParent() {
		return parent;
	}*/
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < attributes.length; i++) {
			String fname = type.getAttributeNames()[i];
			sb.append((fname==null?"":fname)+":"+type.getAttributeTypes()[i]+"="+attributes[i]+"; ");
		}
		if (attributes.length > 0)
			sb.delete(sb.length()-2, sb.length());
		sb.append("}");
		return sb.toString();
	}
	
	public String toOutput() {
		return toString();
	}

	public void renameAttribute(int index, String name) {
		// TODO: check access
		type.setAttributeName(index, name);
	}
	
	public void destruct() {
		destructed = true;
	}

	public RuntimeObject getAssociatedArgs() {
		return associatedArgs;
	}
	
}


