package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.runtime.ExecutionException;

abstract public class Expression {

	static int nb = 0;
	int id = nb++;
	
	public abstract Type getType() throws CompilerException;
	public abstract RuntimeObject evaluate();
	
	public boolean isAssignable() { return false; }
	public final void assign(RuntimeObject value) {
		if (!isAssignable())
			throw new ExecutionException("Expression "+this+" is not assignable");
		assignDelegate(value);
	}
	public void assignDelegate(RuntimeObject value) { assert false; }
	public String toString() { return "(Unnamed Expr)"; }

}
