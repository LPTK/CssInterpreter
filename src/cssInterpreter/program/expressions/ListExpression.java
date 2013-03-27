package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.TupleType;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeIdentifier;
import cssInterpreter.program.TypeOf;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class ListExpression extends Expression {
	Type type;
	Expression[] exprs;
	
	public ListExpression(Execution exec, Expression[] exs, Type parentType, String name) throws CompilerException {
		
		this.exprs = exs;
		
		type = new TupleType(new TypeIdentifier((name==null?"[AnonList]":name), parentType), parentType); //FIXME?
		
		for (int i = 0; i < exs.length; i++) {
			
			Expression e = exs[i];
			assert e != null;
			
			type.addAttribute(null, (String) null, new TypeOf(e, exec.getInterpreter()));
			
		}
		
	}
	
	@Override
	public Type getTypeRef() {
		return type;
	}
	
	@Override
	public RuntimeObject evaluate(RuntimeObject parentOfThis) throws CompilerException {
	
		RuntimeObject ret = new RuntimeObject(type, Execution.getInstance().getThis(), false);
		
		for (int i = 0; i < exprs.length; i++)
			ret.write(i, exprs[i].evaluate(parentOfThis));
		
		return ret;
		
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








