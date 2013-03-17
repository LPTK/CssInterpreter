package cssInterpreter.program;


public 
class TypeIdentifier {
	String name;
	Type parent;
	Type type;
	
	public TypeIdentifier(String name, Type parent) {
		this.name = name;
		this.parent = parent;
	}
	@Override
	public String toString() {
		return (parent == null ? "" : parent.toString()+".")+name;
	}
	public String toDetailedString() {
		//return toString()+"  ("+type.attributeTypes+")";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		/*
		Iterator<RuntimeObject> ite = values();
		while(ite.hasNext())
			sb.append(ite.next());*/
		for (int i = 0; i < type.attributeTypes.size(); i++) {
			String fname = type.getAttributeNames()[i];
			sb.append((fname==null?"":fname)+":"+type.getAttributeTypes()[i]+"; ");
		}
		if (type.attributeTypes.size() > 0)
			sb.delete(sb.length()-2, sb.length());
		sb.append("}");
		return toString()+"  "+sb.toString();
	}
	
	public void setParent(TupleType par) {
		parent = par;
	}
	
}

