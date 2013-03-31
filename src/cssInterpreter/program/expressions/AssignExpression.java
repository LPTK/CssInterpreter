package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.NotSupportedException;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class AssignExpression extends Expression {

	private Expression assigned;
	private Expression value;
	private boolean resolveAssigned = true;
	private TypeReference outputType;
	
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
	public TypeReference getTypeRef() {
		//System.out.println("____"+value.getType());
		//return getValue().getTypeRef();
		//return getValue().getTypeRef().getPointerTypeRef();
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return RefKind.REF;
	}*/
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) throws CompilerException {
		//RuntimeObject assignedRO = assigned.evaluate();
		/*if (!assignedRO.isAssignable())
			throw new ExecutionException("Expression "+assignedRO+" is not assignable");*/
		//RuntimeObject valueRO = getValue().evaluate(parentOfThis).access();
		Reference valueRef = getValue().evaluate(parentOfThis);
		if (valueRef.getRefType() != RefKind.REF && assigned.getSetTypeRef().getKind() == RefKind.REF)
			Execution.getInstance().stackLocal(valueRef);
		
		//assignedRO.assign(valueRO);
		//getAssigned().assign(valueRO);
		getAssigned().assign(valueRef);
		//return valueRO;
		//return new Reference(valueRO);
		//return new Pointer(valueRO);
		return new Reference(valueRef.access(), RefKind.REF);
	}
	
	@Override
	public String toString() { return getAssigned()+" = "+getValue(); }

	public Expression getValue() {
		return value;
	}

	public Expression getAssigned() {
		return assigned;
	}

	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		super.resolveTypes(currentTypeInferenceId);
		//System.out.println("Resolving "+this);
		
//		if (resolveAssigned)
//			assigned.resolveTypes(currentTypeInferenceId);
		
		//System.out.println("RESASS!!!");
		value.resolveTypes(currentTypeInferenceId);
		//outputType = getValue().getTypeRef().getPointerTypeRef();
		//outputType.resolve(currentTypeInferenceId);
		
		if (resolveAssigned) {
			assigned.resolveTypes(currentTypeInferenceId);
			
			//if (!assigned.getTypeRef().isNotSettableToBecause(value.getTypeRef()))
			if (assigned.getSetTypeRef() == null)
				throw new CompilerException("Expression "+assigned+" of type "+assigned.getSetTypeRef()+" is not settable");
			String reason = assigned.getSetTypeRef().isNotSettableToBecause(value.getTypeRef());
			if (reason != null)
				throw new CompilerException(reason+" in assignation of "+assigned+" to "+value);
		}
		
		outputType = getValue().getTypeRef().withKind(RefKind.REF);
	}

	public void setResolveAssignedExpression(boolean resolve) {
		if (!(assigned instanceof FunctionCallExpression && ((FunctionCallExpression)assigned).args.rawEprs.length == 0))
			throw new NotSupportedException("Assigning non-idents??");
		resolveAssigned = resolve;
	}
	
}










