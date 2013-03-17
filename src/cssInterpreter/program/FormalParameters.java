package cssInterpreter.program;


public class FormalParameters {
	NamedType[] namedTypes;
	
	public FormalParameters(NamedType[] namedTypes) {
		this.namedTypes = namedTypes;
	}
	public FormalParameters() {
		namedTypes = new NamedType[0];
	}
	public int withoutValueNb() {
		int ret = 0;
		for (NamedType nt : namedTypes)
			if (!nt.hasDefaultValue)
				ret++;
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (NamedType nt : namedTypes)
			sb.append((nt.name==null?"":nt.name)+":"+nt.type+"; ");
		if (namedTypes.length != 0)
			//sb.deleteCharAt(sb.length()-1);
			sb.delete(sb.length()-2,sb.length());
		sb.append("}");
		return sb.toString();
	}
}
