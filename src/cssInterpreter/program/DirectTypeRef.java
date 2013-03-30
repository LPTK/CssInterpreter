package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.runtime.Reference.RefKind;

public class DirectTypeRef extends TypeReference {
	
	TypeReference tref;
	RefKind refk;
	
	public DirectTypeRef(TypeReference tref, RefKind refk) {
		this.tref = tref;
		this.refk = refk;
	}

	@Override
	protected Type getTypeDelegate() {
		return tref.getType();
	}

	@Override
	protected RefKind getKindDelegate() {
		return refk;
	}

	@Override
	public boolean isResolved() {
		return tref.isResolved();
	}

	@Override
	public Type resolveDelegate(int currentTypeInferenceId) throws CompilerException {
		return tref.resolve(currentTypeInferenceId);
	}
	
	@Override
	public String toString() {
		return ">"+tref;
	}

}

