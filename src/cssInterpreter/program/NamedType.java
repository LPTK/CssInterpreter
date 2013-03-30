package cssInterpreter.program;



public class NamedType {
	//RefKind kind;
	TypeReference type;
	String name;
	boolean hasDefaultValue;
	
	/*public NamedType(RefKind kind, TypeReference type, String name, boolean hasDefaultValue) {
		this.kind = kind;
		this.type = type;
		this.name = name;
		this.hasDefaultValue = hasDefaultValue;
	}*/
	public NamedType(TypeReference type, String name, boolean hasDefaultValue) {
		this.type = type;
		this.name = name;
		this.hasDefaultValue = hasDefaultValue;
	}
}
