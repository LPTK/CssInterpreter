package cssInterpreter.program;


public class Signature {
	String name;
	//Type[] types;
	//NamedType[] namedTypes;
	public final FormalParameters params;
	
	//public Signature(String name, Type[] types) {
	//public Signature(String name, NamedType[] namedTypes) {
	public Signature(String name, FormalParameters params) {
		this.name = name;
		if (params == null)
			throw new IllegalArgumentException();
		this.params = params;
	}
	
	// TODO: implement hashCode/equality?
	/*
    @Override
    public int hashCode() {
		return name.hashCode();
    }
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Signature))
			return false;
		return equals((Signature) obj);
	}
	public boolean equals(Signature sign) {
		if (namedTypes.length != sign.namedTypes.length)
			return false;
		for (int i = 0; i < types.length; i++)
			if (!types[i].equals(sign.types[i]))
				return false;
		return (
			name.equals(sign.name)
		);
	}*/
	@Override
	public String toString() {
		//return name+": {"+params+"}";
		return (name==null?"":name)+": "+params;
	}
	
}
