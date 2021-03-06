package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.NotSupportedException;
import cssInterpreter.runtime.Reference.RefKind;

public class TypeByName extends TypeReference {
	
	Scope fromScope;
	String name;
	Type type;
	
	public TypeByName(Scope fromScope, String name) {
		this.fromScope = fromScope;
		this.name = name;
	}
	
//	@Override
//	public Type getType(int currentTypeInferenceId) throws CompilerException {
//		/*if (type != null)
//			return type;
//		
//		//List<Function> fcts = fromScope.type.getFcts().get(name);
//		Type currentType = fromScope.type;
//		assert currentType != null;
//		List<Function> fcts = new ArrayList<>();
//		
//		do {
//			List<Function> myFcts = currentType.getFcts().get(name);
//			if (myFcts != null)
//				fcts.addAll(myFcts);
//			currentType = currentType.parent;
//		}
//		while (currentType != null);
//		
//		if (fcts.size() < 1)
//			throw new CompilerException("Could not find type "+this);
//		if (fcts.size() > 1)
//			throw new CompilerException("Several possible overloaded types for "+this+": "+fcts);
//		
//		type = fcts.get(0).getOutputType().getType();
//		return type;*/
//		
//		if (type == null)
//			type = fromScope.getType(name);
//		//assert type != null;
//		if (type == null)
//			throw new CompilerException("Could not find type named '"+name+"' in scope of type "+fromScope.type);
//		return type;
//	}
	
	
	@Override
	protected Type getTypeDelegate() {
		return type;
	}
	
	@Override
	public boolean isResolved() {
		return type != null;
	}
	
	@Override
	public Type resolveDelegate(int currentTypeInferenceId) throws CompilerException {
		type = fromScope.getType(name);
		if (type == null)
			throw new CompilerException("Could not find type named '"+name+"' in scope of type "+fromScope.type);
		return type;
	}

	@Override
	protected RefKind getKindDelegate() {
		throw new NotSupportedException();
	}
	
	
	
	@Override
	public String toString() {
		if (type == null)
			//return "[Type "+name+" in "+fromScope.type+"]";
			return name+"(?)";
		else return type.toString();
	}
	
}
