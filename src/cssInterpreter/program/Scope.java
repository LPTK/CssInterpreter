package cssInterpreter.program;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.expressions.Expression;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class Scope {
	private Scope parent;
	TupleType type;
	List<Expression> exprs = new ArrayList<>();
	List<Type> types = new ArrayList<>();
	List<Scope> childs = new ArrayList<>();
	//String name;
	TypeIdentifier typeId;
	
	public Scope(String name, final Execution exec) { // TODO: replace with a StandardLibraryScope class
		type = new TupleType(new TypeIdentifier(name, null), null);
		
		type.addFct(new VoidFunction("print", exec, new FormalParameters(new NamedType[]{new NamedType(exec.AnyType,null,false)})) {
			
			@Override public void execute(RuntimeObject thisReference, RuntimeObject params) {
				/*if (params.isValue())
					output = params.toString();*/
				//System.out.println("Execution Message: \""+params.toString()+"\"");
				//Execution.message(params.toString());
				exec.message(params);
			}
			
		});
		
		type.addFct(new Function(new Signature("read", new FormalParameters())) {
			
			@Override
			public TypeReference getOutputType() {
				return exec.StringType;
			}
			
			@Override
			public RuntimeObject evaluateDelegate(RuntimeObject thisReference, RuntimeObject args) throws CompilerException {
				// TODO: use exec
				Scanner sc = new Scanner(System.in);
				String str = sc.nextLine();
				sc.close();
				return new PrimitiveRuntimeObject<String>(exec.StringType, str, null, true);
			}
			
		});
		
		
		/*
		type.addType(Interpreter.VoidType);
		type.addType(Interpreter.IntType);
		type.addType(Interpreter.LongType);
		type.addType(Interpreter.StringType);
		*/
		/*
		type.addType(exec.VoidType);
		type.addType(exec.IntType);
		type.addType(exec.LongType);
		type.addType(exec.StringType);
		*/
		addType(exec.VoidType);
		addType(exec.IntType);
		addType(exec.LongType);
		addType(exec.StringType);
		
	}

	public Scope(String name, Scope parent) {
		//this.parent = parent;
		//type = new TupleType(new TypeIdentifier(name, (parent==null?null:parent.type)), parent.type);
		//this.name = name;
		setParent(parent, name);
	}
	
	public Scope getParent() {
		return parent;
	}

	public void setParent(Scope par, String name) {
		parent = par;
		type = new TupleType(typeId = new TypeIdentifier(name, (parent==null?null:parent.type)), parent.type);
	}
	
	public void setParent(Scope par) {
		//setParent(par, typeId.name);
		parent = par;
		type.setParent(par.type);
		typeId.setParent(par.type);
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
	
	public void addType(Type t) {
		if (t == null)
			throw new IllegalArgumentException();
		types.add(t);
		type.addType(t);
	}
	
	public Type getType(String name) {
		Scope s = this;
		//System.out.println("..."+types);
		while (s != null) {
			//for (Type t : s.types)
			//	System.out.println(".."+t.id.name);
			for (Type t : s.types)
				if (t.id.name.equals(name))
					return t;
			s = s.parent;
		}
		return null;
	}

	public boolean hasReturnStatement() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private int getTypeInferenceId(Integer id) {
		return id == null? TypeReference.getNewTypeInferenceId(): id;
	}
	public void resolveTypes() throws CompilerException {
		//resolveTypes(TypeReference.getNewTypeInferenceId());
		resolveTypes(null);
	}
	public void resolveTypes(Integer currentTypeInferenceId) throws CompilerException {
		for (Expression e : exprs)
			e.resolveTypes(getTypeInferenceId(currentTypeInferenceId));
		//for (Type t : types)
		//	t.resolveTypes
		for (Scope s : childs)
			s.resolveTypes();
		type.resolve(getTypeInferenceId(currentTypeInferenceId));
	}

	public void addChild(Scope s) {
		childs.add(s);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("({");
		for (Expression e : exprs)
			sb.append(e.toString()+";");
		sb.append("})");
		return sb.toString();
	}
	
}





