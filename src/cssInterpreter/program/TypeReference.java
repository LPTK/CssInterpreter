package cssInterpreter.program;

import cssInterpreter.compiler.CircularTypeReferenceException;
import cssInterpreter.compiler.CompilerError;
import cssInterpreter.compiler.CompilerException;

public abstract class TypeReference {
	//Type getType(int currentTypeInferenceId) throws CompilerException;
	
	private static int lastTypeInferenceId = 0;
	public static int getNewTypeInferenceId() {
		return lastTypeInferenceId++;
	}
	
	//Integer currentTypeInferenceId;
	Integer myTypeInferenceId;
	

	public final Type getType() {
		if (!isResolved())
			throw new CompilerError("Type reference has not been resolved yet: "+this);
		return getTypeDelegate();
	}
	
	// should not throw when isResolved returns true
	protected abstract Type getTypeDelegate();
	
	public abstract boolean isResolved();
	
	public final void resolve(int currentTypeInferenceId) throws CompilerException {
		if (myTypeInferenceId == currentTypeInferenceId)
			throw new CircularTypeReferenceException(this);
		myTypeInferenceId = currentTypeInferenceId;
		resolveDelegate(currentTypeInferenceId);
	}
	
	public abstract void resolveDelegate(int currentTypeInferenceId) throws CompilerException;
	
	
	
	//String getTypeName();
	//String toString();
}

