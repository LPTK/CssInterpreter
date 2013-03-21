package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

public abstract class Expression {
	
	static int nb = 0;
	int id = nb++;
	
	Integer line;
	
	
//	public final TypeReference getTypeRef() throws CompilerException {
//		/*if (Execution.getInstance().getInterpreter().getCurrentTypeInferenceId() == lastTypeInferenceId)
//			throw new CompilerException("Cyclic tye reference"); // TODO
//		return getTypeRefDelegate();*/
//		if (myTypeInferenceId == )
//		
//		return getTypeRefDelegate(getNewTypeInferenceId());
//	}
//	protected abstract TypeReference getTypeRefDelegate(int currentTypeInferenceId) throws CompilerException;
	
	public abstract TypeReference getTypeRef();
	
	public abstract void resolveTypes(int currentTypeInferenceId) throws CompilerException;
	
	
	
	
	
	
	
	public abstract RuntimeObject evaluate() throws CompilerException;
	
	public boolean isAssignable() { return false; }
	public final RuntimeObject assign(RuntimeObject value) {
		if (!isAssignable())
			throw new ExecutionException("Expression "+this+" is not assignable");
		return assignDelegate(value);
	}
	public RuntimeObject assignDelegate(RuntimeObject value) { assert false; throw new AssertionError(); }
	public String toString() { return "(Unnamed Expr)"; }

	public void setLine(Integer line) { this.line = line; }
	public Integer getLine() { return line; }
	

}
