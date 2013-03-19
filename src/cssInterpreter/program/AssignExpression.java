package cssInterpreter.program;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.expressions.Expression;
import cssInterpreter.runtime.RuntimeObject;

public class AssignExpression extends Expression {

	private Expression assigned;
	private Expression value;
	
	public AssignExpression(Expression assigned, Expression value) {
		this.assigned = assigned;
		this.value = value;
		
		if (assigned == null || value == null)
			throw new IllegalArgumentException();
		
		/*try {
			System.out.println("________"+value.getType());
		} catch (CompilerException e) {
			e.printStackTrace();
		}*/
		//System.out.println("____"+value);
		
	}

	@Override
	public TypeReference getTypeRef() throws CompilerException {
		//System.out.println("____"+value.getType());
		return getValue().getTypeRef();
	}

	@Override
	public RuntimeObject evaluate() throws CompilerException {
		//RuntimeObject assignedRO = assigned.evaluate();
		/*if (!assignedRO.isAssignable())
			throw new ExecutionException("Expression "+assignedRO+" is not assignable");*/
		RuntimeObject valueRO = getValue().evaluate();
		//assignedRO.assign(valueRO);
		getAssigned().assign(valueRO);
		return valueRO;
	}
	
	@Override
	public String toString() { return getAssigned()+" = "+getValue(); }

	public Expression getValue() {
		return value;
	}

	public Expression getAssigned() {
		return assigned;
	}
	
	
}
