package cssInterpreter.program;

public class PointerType extends Type {
	
	Type pointedType;

//	public PointerType(Type t, Type parent) { // FIXME: meaning of "parent" type here?
//		super(new TypeIdentifier(t.id.name+"*", parent), parent);
//	}
	
	public PointerType(Type t) { // FIXME: meaning of "parent" type here?
		super(new TypeIdentifier(t.id.name+"*", null), null, true);
		pointedType = t;
	}

	public Type getPointedType() {
		return pointedType;
	}
	
}
