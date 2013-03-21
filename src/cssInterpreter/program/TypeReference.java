package cssInterpreter.program;

import cssInterpreter.compiler.CircularTypeReferenceException;
import cssInterpreter.compiler.CompilerError;
import cssInterpreter.compiler.CompilerException;

public abstract class TypeReference {
	//Type getType(int currentTypeInferenceId) throws CompilerException;
	
	private static int lastTypeInferenceId = 0;
	public static int getNewTypeInferenceId() {
		return ++lastTypeInferenceId;
	}
	
	//Integer currentTypeInferenceId;
	int myTypeInferenceId = 0;
	

	public final Type getType() {
		if (!isResolved())
			throw new CompilerError("Type reference has not been resolved yet: "+this);
		return getTypeDelegate();
	}
	
	// should not throw when isResolved returns true
	protected abstract Type getTypeDelegate();
	
	public abstract boolean isResolved();
	
	public final Type resolve(int currentTypeInferenceId) throws CompilerException {
		if (isResolved())
			return getType();
		if (myTypeInferenceId == currentTypeInferenceId)
			throw new CircularTypeReferenceException(this);
		System.out.println("Resolving tref "+this);
		myTypeInferenceId = currentTypeInferenceId;
		return resolveDelegate(currentTypeInferenceId);
	}
	
	public abstract Type resolveDelegate(int currentTypeInferenceId) throws CompilerException;
	
	
	
	//String getTypeName();
	//String toString();
}

