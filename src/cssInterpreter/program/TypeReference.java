package cssInterpreter.program;

import cssInterpreter.compiler.CircularTypeReferenceException;
import cssInterpreter.compiler.CompilerError;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.Interpreter;

public abstract class TypeReference {
	//Type getType(int currentTypeInferenceId) throws CompilerException;
	
	private static int lastTypeInferenceId = 0;
	public static int getNewTypeInferenceId() {
		return ++lastTypeInferenceId;
	}
	
	//Integer currentTypeInferenceId;
	int myTypeInferenceId = 0;
	private TypeReference pointerTypeRef;// = new PointerTypeReference(this);
	

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
		if (myTypeInferenceId == currentTypeInferenceId && !isPointer())
			throw new CircularTypeReferenceException(this);
		
		//System.out.println("Resolving tref "+this);
		Interpreter.getInstance().out("Resolving tref "+this);
		Interpreter.getInstance().indent();
		
		myTypeInferenceId = currentTypeInferenceId;
		
		/*
		if (pointerTypeRef != null)
			pointerTypeRef.resolve(currentTypeInferenceId);
		*/
		
		//return resolveDelegate(currentTypeInferenceId);
		
		Type ret = resolveDelegate(currentTypeInferenceId);
		//ret.resolve(currentTypeInferenceId);
		
		
		if (pointerTypeRef != null)
			pointerTypeRef.resolve(currentTypeInferenceId);
		
		
		Interpreter.getInstance().unindent();
		return ret;
		
	}
	
	protected boolean isPointer() { return false; } // FIXME replace with instanceof tests?

	public abstract Type resolveDelegate(int currentTypeInferenceId) throws CompilerException;
	
	public TypeReference getPointerTypeRef() {
		if (pointerTypeRef == null)
			pointerTypeRef = new PointerTypeReference(this);
		return pointerTypeRef;
	}
	
	
	
	//String getTypeName();
	//String toString();
}

