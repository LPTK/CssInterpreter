package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TupleType;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeIdentifier;
import cssInterpreter.program.TypeOf;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class ListExpression extends Expression {
	Type type;
	Expression[] exprs;
	
	public ListExpression(Execution exec, Expression[] exs, Type parentType, String name) throws CompilerException {
		
		this.exprs = exs;
		
		// FIXME: add this type to a scope!!!
		type = new TupleType(new TypeIdentifier((name==null?"[AnonList]":name), parentType), parentType); //FIXME?
		
		for (int i = 0; i < exs.length; i++) {
			
			Expression e = exs[i];
			assert e != null;
			
			type.addAttribute(null, (String) null, new TypeOf(e, exec.getInterpreter())); // FIXME: notnull attrKind
			
		}
		
	}
	
	@Override
	public Type getTypeRef() {
		return type;
	}

	@Override
	public RefKind getRetKind() {
		return RefKind.VAL;
	}
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) throws CompilerException {
	
		RuntimeObject ret = new RuntimeObject(type, Execution.getInstance().getThis().access(), false);
		
		for (int i = 0; i < exprs.length; i++)
			ret.write(i, exprs[i].evaluate(parentOfThis).access());
		
		return new Reference(ret, getRetKind());
		
	}
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		super.resolveTypes(currentTypeInferenceId);
		
		for (TypeReference tr : type.getAttributeTypes())
			tr.resolve(currentTypeInferenceId); // FIXME: really ok?!
		
		for (Expression e : exprs)
			e.resolveTypes(currentTypeInferenceId);
		
	}
	
}








