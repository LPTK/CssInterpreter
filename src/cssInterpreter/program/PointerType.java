package cssInterpreter.program;

public class PointerType extends Type {
	
	public PointerType(Type t, Type parent) { // FIXME: meaning of "parent" type here?
		super(new TypeIdentifier(t.id.name+"*", parent), parent);
	}
	
}
