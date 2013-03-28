package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;

public class PointerTypeReference extends TypeReference {
	
	TypeReference ref;
	PointerType type;
	
	public PointerTypeReference(TypeReference ref) {
		this.ref = ref;
	}

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
		return type = new PointerType(ref.resolve(currentTypeInferenceId));
	}
	
	@Override
	public String toString() {
		return ref+"*(?)";
	}

}
