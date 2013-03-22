package cssInterpreter.program;


public class NamedType {
	TypeReference type;
	String name;
	boolean hasDefaultValue;
	
	public NamedType(TypeReference type, String name, boolean hasDefaultValue) {
		this.type = type;
		this.name = name;
		this.hasDefaultValue = hasDefaultValue;
	}
}
