package cssInterpreter.runtime;

import java.util.Arrays;
import java.util.Iterator;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.PrimitiveType;
import cssInterpreter.program.Type;

public 
class RuntimeObject  {
	
	protected Type type;
	boolean constant;
	protected RuntimeObject[] attributes;
	RuntimeObject parent;
	boolean destructed;
	
	//RuntimeObject associatedArgs;
	
	
	//public RuntimeObject(Type type, RuntimeObject parent, boolean constant) {
	/*
	 * associatedArgs represent the arguments passed to a method if this object is the instanciated scope of this method
	 * should be null in any other cases
	 * 
	 */
	//public RuntimeObject(Type type, RuntimeObject associatedArgs, boolean constant) {
	public RuntimeObject(Type type, RuntimeObject parent, boolean constant) {
		this.type = type;
		this.parent = parent;
		//this.associatedArgs = associatedArgs;
		this.constant = constant;
		if (type != null) { // normally only happens if this is the RuntimeObject associated with type TypeType
			//assert ?
			attributes = new RuntimeObject[type.getAttributeTypes().length];
		}
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
	
	public Iterator<RuntimeObject> values() {
		//return new ArrayList<RuntimeObject>(attributes).iterator();
		return Arrays.asList(attributes).iterator();
	}
	
	public final void write(int index, RuntimeObject obj) throws CompilerException { // TODONE: check types?
		if (constant)
			throw new AccessViolationException("Cannot write to a constant object");
		if (destructed)
			throw new AccessViolationException("Cannot write to a destructed object");
		Type t = type.getAttributeTypes()[index].getType(); // FIXME -1?
		//System.out.println(type.getAttributeTypes()[index]);
		if (!t.isSettableTo(obj.type))
			//throw new AccessViolationException("Field of type '"+t+"' cannot be set to object of type '"+obj.type+"'");
			throw new AccessViolationException("Object of type '"+obj.type+"' cannot be assigned to field of type '"+t+"' in "+this+":"+type);
		writeDelegate(index, obj);
	}

	public final RuntimeObject read(int index) { // TODO: check types?
		if (attributes[index] == null)
			throw new AccessViolationException("Value "+type.getAttributeNames()[index]+" (at index "+index+") in "+this+" was not initialized when read");
		if (destructed)
			throw new AccessViolationException("Cannot write to a destructed object");
		return readDelegate(index);
	}
	
	protected RuntimeObject readDelegate(int index) {
		return attributes[index];
	}
	
	public Type getRuntimeType() {
		return type;
	}
	protected void writeDelegate(int index, RuntimeObject obj) {
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
		
		for (String fname : type.getFcts().keySet()) {
			sb.append(fname+"(); "); // type.getFcts()
		}
		
		if (attributes.length > 0 || type.getFcts().size() > 0)
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
		for (RuntimeObject obj : attributes)
			obj.destruct();
		destructed = true;
	}

	public RuntimeObject getParent() {
		return parent;
	}
	
	// should only be used to set the type of TypeType
	public void setType(PrimitiveType<Type> typeType) {
		type = typeType;
	}

	/*
	public RuntimeObject getAssociatedArgs() {
		return associatedArgs;
	}*/
	
}

