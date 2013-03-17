package cssInterpreter.program;

import java.util.ArrayList;
import java.util.List;

import cssInterpreter.program.expressions.Expression;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class Scope {
	private Scope parent;
	TupleType type;
	List<Expression> exprs = new ArrayList<>();
	
	public Scope(String name, final Execution exec) { // TODO: replace with a StandardLibraryScope class
		type = new TupleType(new TypeIdentifier(name, null), null);
		
		type.addFct(new VoidFunction("print", exec) {
			@Override public void execute(RuntimeObject thisReference, RuntimeObject params) {
				/*if (params.isValue())
					output = params.toString();*/
				//System.out.println("Execution Message: \""+params.toString()+"\"");
				//Execution.message(params.toString());
				exec.message(params);
			}
		});
		/*
		type.addType(Interpreter.VoidType);
		type.addType(Interpreter.IntType);
		type.addType(Interpreter.LongType);
		type.addType(Interpreter.StringType);
		*/
		
		type.addType(exec.VoidType);
		type.addType(exec.IntType);
		type.addType(exec.LongType);
		type.addType(exec.StringType);
		
	}

	public Scope(String name, Scope parent) {
		this.parent = parent;
		type = new TupleType(new TypeIdentifier(name, (parent==null?null:parent.type)), parent.type);
	}
	
	public void setName(String newName) {
		//type.id.name = newName;
		type.setName(newName);
	}
	
	public TupleType getType() {
		return type;
	}
	
	public List<Expression> getExprs() {
		return exprs;
	}
	
	/*
	Field get(String name) {
		Field ret = fields.get(name);
		if (ret == null && parent != null)
			ret = parent.get(name);
		return ret;
	}
	Field getOrCreate(String name, Node n) {
		Field ret = get(name);
		if (ret == null) {
			ret = new Field(n);
			fields.put(name, ret);
		}
		return ret;
	}*/
	/*
	public void addFct(Function fct) {
		List<Function> ls = fcts.get(fct.signature.name);
		if (ls == null) {
			ls = new ArrayList<Function>();
			fcts.put(fct.signature.name, ls);
		}
		ls.add(fct);
	}
	*/
	
	public void addExpr(Expression expression) {
		if (expression == null)
			throw new IllegalArgumentException();
		exprs.add(expression);
	}

	public Scope getParent() {
		return parent;
	}

}