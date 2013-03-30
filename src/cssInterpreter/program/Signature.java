package cssInterpreter.program;

import cssInterpreter.runtime.Execution;


public class Signature {
	
	//public static final List<Type> formalParamTypes = new ArrayList<>();
	
	private String name = null;
	//Type[] types;
	//NamedType[] namedTypes;
	Type type;
	
	public final FormalParameters params;
	
	private TypeIdentifier id; // used when the function this signature signs is a cosntructor
	
	//public Signature(String name, Type[] types) {
	//public Signature(String name, NamedType[] namedTypes) {
	public Signature(String name, FormalParameters params) {
		this.name = name;
		if (params == null)
			throw new IllegalArgumentException();
		this.params = params;
		this.type = new TupleType(new TypeIdentifier("[FormalParam?!]", null), null, false);
		//formalParamTypes.add(type);
		Execution.getInstance().allAnonTypes.add(type);
		//signatureTypes.add(type);
		
		//Scope a = Execution.getInstance().standardScope;
		//Execution.getInstance().standardScope.addType(type);
		
		for (NamedType nt : params.namedTypes)
			//type.addAttribute(new ARefAttrType(), nt.name, nt.type);
			//type.addAttribute(nt.kind, nt.name, nt.type);
			type.addAttribute(nt.name, nt.type);
		
		// FIXME: no fparams
		
		
	}
	/*
	public Signature(Type type, FormalParameters params) {
		this(null, type);
	}*/
	public Signature(TypeIdentifier id, FormalParameters params) { // TODO rm notused
		this((String)null, params);
		this.id = id;
	}

	public Signature(final String name, Type type) {
	//public Signature(TypeIdentifier id, Type type) {
		this.name = name;
		//this.id = id;
		if (name == null)
			this.id = type.id;
		this.type = type;
		//formalParamTypes.add(type);
		//signatureTypes.add(type);
		
		//assert type.getConstructors().size() == 1;
		//this.params = type.getConstructors().get(0).signature.params; // TODO: dump 'params' and only use 'type'
		
		NamedType[] nts = new NamedType[type.attributeTypes.size()];
		for (int i = 0; i < type.attributeTypes.size(); i++) {
			//nts[i] = new NamedType(type.attributeKinds.get(i), type.attributeTypes.get(i), type.attributeNames.get(i), false); // FIXME: use provided default values!
			nts[i] = new NamedType(type.attributeTypes.get(i), type.attributeNames.get(i), false); // FIXME: use provided default values!
		}
		this.params = new FormalParameters(nts);
		
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
	
	/*public void destroy() {
		type.destroy();
	}*/
	
	public Type getType() {
		return type;
	}
	
	public String getName() {
		return name==null? id.name : name;
	}
	
	@Override
	public String toString() {
		//return name+": {"+params+"}";
		//return (name==null?"":name)+": "+params;
		return (name==null?"":name)+": "+type.id.toDetailedString();
	}
	
}


