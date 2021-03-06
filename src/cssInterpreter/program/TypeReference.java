package cssInterpreter.program;

import cssInterpreter.compiler.CircularTypeReferenceException;
import cssInterpreter.compiler.CompilerError;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.Interpreter;
import cssInterpreter.runtime.Reference.RefKind;

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
	public final RefKind getKind() {
		if (!isResolved())
			throw new CompilerError("Type kind has not been resolved yet: "+this);
		return getKindDelegate();
	}
	
	// should not throw when isResolved returns true
	protected abstract Type getTypeDelegate();
	protected abstract RefKind getKindDelegate();
	
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
	
	public TypeReference withKind(RefKind refk) {
		return new DirectTypeRef(this, refk);
	}
	
	public String isNotSettableToBecause(TypeReference tr) {
		if (getKind() == RefKind.VAL && tr.getKind() != RefKind.VAL)
			return "Expected a VAL for assignation to a VAL field";
		if (getKind() == RefKind.RVAL && tr.getKind() == RefKind.REF)
			return "Expected a VAL or RVAL for assignation to a RVAL field";
		return getType().isSettableTo(tr.getType())? null: "Incompatible types. Cannot set object of type "+tr.getType()+" to field of type "+getType();
	}
	
	
	
	//String getTypeName();
	//String toString();
}





