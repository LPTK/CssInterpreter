package cssInterpreter.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cssInterpreter.analysis.DepthFirstAdapter;
import cssInterpreter.compiler.CompilerError;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.NotSupportedCompExc;
import cssInterpreter.compiler.UnknownFunctionCompExc;
import cssInterpreter.node.AAccessNakedType;
import cssInterpreter.node.AAssignExpr;
import cssInterpreter.node.AAttrDeclStatement;
import cssInterpreter.node.AClosure;
import cssInterpreter.node.AClosureExpr;
import cssInterpreter.node.ADefDeclStatement;
import cssInterpreter.node.AExprStatement;
import cssInterpreter.node.AFieldAccess;
import cssInterpreter.node.AIdExpr;
import cssInterpreter.node.AIdentGeneralId;
import cssInterpreter.node.AIdentNakedType;
import cssInterpreter.node.AInvocationExpr;
import cssInterpreter.node.AListExpr;
import cssInterpreter.node.ANumberExpr;
import cssInterpreter.node.AStringExpr;
import cssInterpreter.node.AType;
import cssInterpreter.node.ATypedValue;
import cssInterpreter.node.Node;
import cssInterpreter.node.PAttrType;
import cssInterpreter.node.PClosure;
import cssInterpreter.node.PExpr;
import cssInterpreter.node.PNakedType;
import cssInterpreter.node.Start;
import cssInterpreter.node.TIdent;
import cssInterpreter.runtime.ExecutionException;
//import cssInterpreter.program.Type;

/*

// TODO unit tests

a := b;
b := a; // must not loop and bug


foo a;
foo a = 1;
foo a, b= 1;


// Understandable error for:

def foo := {
  ...
} // MISSING SEMI
foo 42;





*/



class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
    	super();
    	this.first = first;
    	this.second = second;
    }

    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
    	if (other instanceof Pair) {
    		Pair otherPair = (Pair) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
    	return first;
    }

    public void setFirst(A first) {
    	this.first = first;
    }

    public B getSecond() {
    	return second;
    }

    public void setSecond(B second) {
    	this.second = second;
    }
}






class Field { // FIXME not used
	Node node;
	public Field(Node node) {
		this.node = node;
	}
}

class UndeterminedField extends Field { // FIXME not used
	Field field = null;
	
	public UndeterminedField(Node node) {
		super(node);
	}
}

class Scope {
	Scope parent;
	/////HashMap<Signature, Field> fields = new HashMap<>();
	//HashMap<String, Field> requestedFields = new HashMap<>();
	//HashMap<String, List<Function>> fcts = new HashMap<>();
	//Type type;
	//InConstructionType type = new InConstructionType(new TypeIdentifier());
	TupleType type;
	
	List<Expression> exprs = new ArrayList<>();
	
	public Scope(String name) { // TODO: replace with a StandardLibraryScope class
		type = new TupleType(new TypeIdentifier(name, null));
		
		type.addFct(new VoidFunction("print") {
			@Override public void execute(RuntimeObject thisReference, RuntimeObject params) {
				/*if (params.isValue())
					output = params.toString();*/
				//System.out.println("Execution Message: \""+params.toString()+"\"");
				//Execution.message(params.toString());
				Execution.message(params);
			}
		});
		
		type.addType(DumbInterpreter.VoidType);
		type.addType(DumbInterpreter.IntType);
		type.addType(DumbInterpreter.LongType);
		type.addType(DumbInterpreter.StringType);
		
	}
	
	public Scope(String name, Scope parent) {
		this.parent = parent;
		type = new TupleType(new TypeIdentifier(name, (parent==null?null:parent.type)));
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
	
}
/*
class Closure {
	Scope scope = new Scope();
}
*/


abstract class Expression {
	/*
	String identifier = null;
	public String getIdentifier() {
		return identifier;
	}*/
	
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

/*
class FieldAccessExpression implements Expression {

	@Override
	public RuntimeObject evaluate() {
		
		return null;
	}
	
}*/

/*****
class EmptyExpr extends Expression {
	@Override
	public Type getType() throws CompilerException {
		return DumbInterpreter.VoidType;
	}
	@Override
	public RuntimeObject evaluate() {
		return Execution.voidObj;
	}
	public String toString() { return ""; }
}
*/

/*
class ListExpression extends Expression {
	@Override
	public Type getType() throws CompilerException {
		// TODO
		throw new NotSupportedCompExc();
	}
	@Override
	public RuntimeObject evaluate() {
		throw new NotSupportedCompExc();
	}
	public String toString() {
		return "(,)"; // TODO
	}
}*/
class TupleExpression extends Expression {
	Type type;
	//Expression[] exprs;
	List<Expression> ordinalParams = new ArrayList<>();
	//Map<String,Expression> namedParams = new HashMap<>();
	Map<String,Pair<Integer,Expression>> namedParams = new HashMap<>();
	/*
	 * Invariant: p ordinals followed by k named, NOT MIXED
	 */
	public TupleExpression(Type parentType) throws CompilerException {
		////type = DumbInterpreter.VoidType;
		//exprs = new Expression[0];
		
		this(new Expression[0], parentType, null);
		
	}
	
	public TupleExpression(Expression[] exs, Type parentType, String name) throws CompilerException {
		//this.exprs = exs;

		//TupleType tt = new TupleType(new TypeIdentifier("[AnonTuple]", DumbInterpreter.standardScope.type)); //FIXME?
		TupleType tt = new TupleType(new TypeIdentifier((name==null?"[AnonTuple]":name), parentType)); //FIXME?

		System.out.println(">>>>>>>>>>>");
		
		boolean namedParamsBegan = false;
		
		for (int i = 0; i < exs.length; i++) {
			
			Expression e = exs[i];
			assert e != null;
			
			System.out.println(":t:"+e.getClass());
			
			if (e instanceof AssignExpression) {
				AssignExpression ae = ((AssignExpression) e);
				namedParamsBegan = true;
				FieldAccessExpression fa = (FieldAccessExpression) ae.assigned;
				
				//System.out.println(((TupleExpression)fa.thisExpression));
				
				assert ((TupleExpression)fa.thisExpression) == null
					|| ((TypeBase)((TupleExpression)fa.thisExpression).getType()).isEmpty(); // laid comme ta mère
				//assert ae.assigned instanceof 
				
				//System.out.println(":n:"+fa.fieldName);
				System.out.println(":n: "+fa.fieldName+" = "+ae.value);
				//System.out.println(":n:"+fa.fieldName+" - "+fa.args);
				
				//namedParams.put(fa.fieldName, fa.args);
				//namedParams.put(fa.fieldName, ae.value);
				namedParams.put(fa.fieldName, new Pair<>(i, ae.value));
				tt.addAttribute(null, fa.fieldName, new TypeOf(ae.value));
				
			} else {
				
				if (namedParamsBegan)
					throw new CompilerException("Invalid sequence of arguments: named arguments must follow ordinal arguments");
				ordinalParams.add(e);
				tt.addAttribute(null, (String) null, new TypeOf(e));

				System.out.println(":o:"+e+" "+new TypeOf(e));
				
			}
		}
		
		System.out.println("<<<<<<<<<");
		
		tt.done();
		type = tt;
		
		
		//this.type = type;
		
		
		
		
		
		
	}
	/*
	public TupleExpression(Expression args) {
		
	}*/

	@Override
	public Type getType() throws CompilerException {
		// TODO
		//throw new NotSupportedCompExc();
		//if (type.isEmpty())
		//return DumbInterpreter.VoidType;
		return type;
	}
	@Override
	public RuntimeObject evaluate() {
		//throw new NotSupportedCompExc();
		//if (type.isEmpty())
		//return Execution.voidObj;
		/*if (exprs.length == 0)
			 return Execution.voidObj;*/
		
		
//		if (type == DumbInterpreter.VoidType) // WARNING: object address equality here
//			return Execution.voidObj;
//		else {
			
			
			RuntimeObjectBase ret = new RuntimeObjectBase(type, Execution.getThis()/*FIXME?*/, false);
			//evals = new RUnt;
			/*for (int i = 0; i < exprs.length; i++) {
				ret.write(i, exprs[i].evaluate());
			}*/
			int i;
			for (i = 0; i < ordinalParams.size(); i++)
				ret.write(i, ordinalParams.get(i).evaluate());
			/*
			Iterator<Expression> ite = namedParams.values().iterator(); // FIXME: in right order?
			while(ite.hasNext())
				ret.write(i++, ite.next().evaluate());
			*/
			Iterator<Pair<Integer,Expression>> ite = namedParams.values().iterator(); // FIXEDME: in right order?
			while(ite.hasNext()) {
				Pair<Integer,Expression> p = ite.next();
				ret.write(p.getFirst(), p.getSecond().evaluate());
				i++;
			}
			
			return ret;
			
			
//		}
	}
	public String toString() {
		//if (exprs.length == 0)
		try {
		if (type == DumbInterpreter.VoidType) // WARNING: object address equality here
			 //return "[Tpl]()";
			return "()";
		else {
			StringBuffer sb = new StringBuffer();
			//sb.append("[Tpl](");
			sb.append("(");
			for (Expression e : ordinalParams)
				//sb.append(e+", ");
				sb.append(e+":"+e.getType()+", ");
			//for (Expression e : namedParams.values())
			
			//for (Map.Entry<String,Expression> e : namedParams.entrySet()) System.out.println(e+" "+e.getValue().getClass());
			
//			for (Map.Entry<String,Expression> e : namedParams.entrySet())
//				sb.append(e.getKey()+":"+e.getValue().getType()+"="+e.getValue()+", ");
			for (Map.Entry<String,Pair<Integer,Expression>> e : namedParams.entrySet())
				sb.append(e.getKey()+":"+e.getValue().getSecond().getType()+"="+e.getValue().getSecond()+", ");
			
			if (ordinalParams.size() != 0 || namedParams.size() != 0)
				sb.delete(sb.length()-2, sb.length());
			sb.append(")");
			return sb.toString(); // TODONE
		}
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}
}


class AssignExpression extends Expression {

	Expression assigned;
	Expression value;
	
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
	public Type getType() throws CompilerException {
		//System.out.println("____"+value.getType());
		return value.getType();
	}

	@Override
	public RuntimeObject evaluate() {
		//RuntimeObject assignedRO = assigned.evaluate();
		/*if (!assignedRO.isAssignable())
			throw new ExecutionException("Expression "+assignedRO+" is not assignable");*/
		RuntimeObject valueRO = value.evaluate();
		//assignedRO.assign(valueRO);
		assigned.assign(valueRO);
		return valueRO;
	}

	public String toString() { return assigned+" = "+value; }
}

class FieldAccessExpression extends Expression {
	Expression thisExpression;
	String fieldName;
	TupleExpression args;
	RuntimeObject thisObj;
	
	public FieldAccessExpression(Expression thisExpression, String fieldName, TupleExpression args) {
		this.thisExpression = thisExpression;
		this.fieldName = fieldName;
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
		thisObj = null;
	}
	
	public void setArgs(TupleExpression args) {
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
	}
	
	Function getFunction() throws CompilerException {
		CandidateList candidates = new CandidateList();
		if (thisExpression == null) {
			//Expression expr = 
			//RuntimeObject
			thisObj = Execution.getThis();
			while (thisObj != null) {
				try {
					//System.out.println("+++"+args);
					//return thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					
					Function fct = thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					//System.out.println("\tFound "+fct.signature);
					//System.out.println(candidates);
					return fct;
					
				} catch(UnknownFunctionCompExc e) {
					thisObj = thisObj.getParent();
					//System.out.println(thisObj);
					if (thisObj == null)
						throw e; // TODO: keep the most appropriate message (which parameters are missing?)
								 // TODO: aggregate all possible functions with this name?
				}
			}
			//System.out.println(thisObj);
			//throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this);
			throw new AssertionError("Never gets there");
		} else
			return thisExpression.getType().getFunction(new CallSignature(fieldName, args), candidates);
	}
	
	private RuntimeObject evalThis() {
		return thisObj == null ? thisExpression.evaluate() : thisObj;
	}
	
	@Override
	public Type getType() throws CompilerException {
		//System.out.println(thisExpression.getType()+" "+fieldName);
		///System.out.println(".. "+thisExpression.getType().getFunction(new CallSignature(fieldName, args)));
		
		//return thisExpression.getType().getFunction(new CallSignature(fieldName, new ArrayList<Expression>())).getOutputType();
		//return thisExpression.getType().getFunction(new CallSignature(fieldName, args)).getOutputType();
		return getFunction().getOutputType();
	}
	@Override
	public RuntimeObject evaluate() {
		try {
			//System.out.println(">>> "+thisExpression.getType().getFunction(new CallSignature(fieldName, args)));
			//System.out.println(">>> "+thisExpression+" "+(thisExpression == null ? "" : thisExpression.evaluate()));
			//System.out.println(">>> "+args+" "+args.evaluate());
			
			//Expression expr = thisExpression;
			
			//return thisExpression.getType().getFunction(new CallSignature(fieldName, args)).evaluate(thisExpression.evaluate(), args.evaluate());
			//return getFunction().evaluate(thisObj == null ? thisExpression.evaluate() : thisObj, args.evaluate());
			
			return getFunction().evaluate(evalThis(), args.evaluate());
			
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}

	@Override public boolean isAssignable() { return true; }
	@Override
	public void assignDelegate(RuntimeObject value) {
		try {
			//System.out.println("ASSIGN "+value);
			getFunction().set(evalThis(), args.evaluate(), value);
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}
	
	public String toString() {
		/*if (thisExpression == null)
			return "("+thisObj+")";
		else
			return "("+thisExpression+")";*/
		//String ret = "("+(thisExpression == null ? (thisObj == null ? : ) : thisExpression)+").";
		//String ret = (thisExpression == null ? "[CurrentScope]." : "("+ thisExpression) + ").";
		String ret = (thisExpression == null ? "." : "("+ thisExpression + ").");
		ret += fieldName;
		//ret += "("+args+")";
		ret += args;
		return ret;
	}
}
/***
class InvokeExpression extends Expression { // FIXME not used
	//RuntimeObject thisReference;
	Expression thisExpression;
	Function fct = null;
	Expression[] args;
	
	public InvokeExpression(Expression thisExpression, Function fct, Expression[] args) {
		this.thisExpression = thisExpression;
		this.fct = fct;
		this.args = args;
	}
	
	public InvokeExpression(Expression thisExpression, Expression[] args) {
		
	}

	@Override
	public Type getType() {
		if (fct == null)
			;
		return fct.getOutputType();
	}

	@Override
	public RuntimeObject evaluate() {
		RuntimeObject[] params = new RuntimeObject[args.length];
		for (int i = 0; i < args.length; i++) {
			params[i] = args[i].evaluate();
		}
		//return fct.evaluate(thisReference, params);
		return fct.evaluate(thisExpression.evaluate(), params);
	}
}
*/
class ThisExpression extends Expression {
	@Override
	public Type getType() throws CompilerException {
		return Execution.getThis().getRuntimeType();
	}
	@Override
	public RuntimeObject evaluate() {
		return Execution.getThis();
	}
	public String toString() { return "this"; }
}
/*
class UnspecifiedScopeExpression extends Expression {
	String name;
	Signature sign;
	
	public UnspecifiedScopeExpression(String name, Signature sign) {
		this.name = name;
		this.sign = sign;
	}
	
	@Override
	public Type getType() throws CompilerException {
		return Execution.getThis().getRuntimeType();
	}
	@Override
	public RuntimeObject evaluate() {
		return Execution.getThis();
	}
}
*/


class Constant extends Expression {
	final RuntimeObject value;
	
	public Constant(RuntimeObject value) {
		this.value = value;
	}
	@Override
	public Type getType() {
		return value.getRuntimeType();
	}
	@Override
	public RuntimeObject evaluate() {
		//System.out.println("CCCCCCCCC "+value);
		return value;
	}
	public String toString() { return "const "+value.toString(); }
	//public String toString() { return "(const "+value.toString()+")"; }
}

class NamedType {
	Type type;
	String name;
	boolean hasDefaultValue;
	
	public NamedType(Type type, String name, boolean hasDefaultValue) {
		this.type = type;
		this.name = name;
		this.hasDefaultValue = hasDefaultValue;
	}
}

class CallSignature {
	String name;
	//List<Expression> exprs;
	//Expression args;
	TupleExpression args;
	
	/*
	private boolean conformsTo(Expression e, NamedType nt) {
		return e.getType().conformsTo(nt.type);
	}
	*/
	boolean conformsTo(Signature sign) throws CompilerException {
		// FIXME: more general algo? (using a ListType or TupleType)
		/*
		if (args instanceof ListExpression) {
			// TODO
			args.getType();
		} else {
			Hash
			return conformsTo(args, sign.namedTypes);
		}
		return false;*/
		//System.out.println(args);
		return args.getType().conformsTo(sign);
	}
	String notConformingToBecause(Signature sign) throws CompilerException {
		return args.getType().notConformingToBecause(sign);
	}
	
	//public CallSignature(String name, List<Expression> exprs) {
	public CallSignature(String name, TupleExpression args) {
		this.name = name;
		//this.exprs = exprs;
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
	}
	
	@Override
	public String toString() {
		//return "CallSignature: " + name + args;
		return name + args;
	}
}

class FormalParameters {
	NamedType[] namedTypes;
	
	public FormalParameters(NamedType[] namedTypes) {
		this.namedTypes = namedTypes;
	}
	public FormalParameters() {
		namedTypes = new NamedType[0];
	}
	public int withoutValueNb() {
		int ret = 0;
		for (NamedType nt : namedTypes)
			if (!nt.hasDefaultValue)
				ret++;
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (NamedType nt : namedTypes)
			sb.append((nt.name==null?"":nt.name)+":"+nt.type+"; ");
		if (namedTypes.length != 0)
			//sb.deleteCharAt(sb.length()-1);
			sb.delete(sb.length()-2,sb.length());
		sb.append("}");
		return sb.toString();
	}
}

class Signature {
	String name;
	//Type[] types;
	//NamedType[] namedTypes;
	FormalParameters params;
	
	//public Signature(String name, Type[] types) {
	//public Signature(String name, NamedType[] namedTypes) {
	public Signature(String name, FormalParameters params) {
		this.name = name;
		if (params == null)
			throw new IllegalArgumentException();
		this.params = params;
	}
	
	// TODO: implement hashCode/equality?
	/*
    @Override
    public int hashCode() {
		return name.hashCode();
    }
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Signature))
			return false;
		return equals((Signature) obj);
	}
	public boolean equals(Signature sign) {
		if (namedTypes.length != sign.namedTypes.length)
			return false;
		for (int i = 0; i < types.length; i++)
			if (!types[i].equals(sign.types[i]))
				return false;
		return (
			name.equals(sign.name)
		);
	}*/
	@Override
	public String toString() {
		//return name+": {"+params+"}";
		return (name==null?"":name)+": "+params;
	}
}
/*
class RuntimeField {
	Type type;
	int index;
}
*/

abstract class Function {
	enum FieldType {
		VAL_ATTR,
		RVAL_ATTR,
		REF_ATTR,
		DEF,
		NONE,
	}
	FieldType fieldType = FieldType.NONE;
	boolean meta = false;
	Signature signature;
	public Function(Signature signature) {
		this.signature = signature;
	}
	public boolean isSettable() {
		return false;
	}
	public final void set(RuntimeObject thisReference, RuntimeObject params, RuntimeObject value) {
		if (!isSettable())
			throw new ExecutionException("Function "+this+" has no setter defined");
		setDelegate(thisReference, params, value);
	}
	public void setDelegate(RuntimeObject thisReference, RuntimeObject params, RuntimeObject value) { assert false; }
	
	public abstract Type getOutputType();
	//public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject[] params);
	public abstract RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params);
	
	@Override
	public String toString() {
		return "Function  "+signature+" -> "+getOutputType(); //+" on type "+type; //+" with "+args;
	}
}

abstract class VoidFunction extends Function {
	
	public VoidFunction(String name) {
		super(new Signature(name, new FormalParameters()));
	}

	@Override
	public Type getOutputType() {
		return DumbInterpreter.VoidType;
	}
	
	abstract protected void execute(RuntimeObject thisReference, RuntimeObject params);
	
	@Override
	public final RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
		execute(thisReference, params);
		return Execution.voidObj;
	}

}

class RuntimeField extends Function { // TODO: add checks for the return value is of the right type
	//final RuntimeObject thisReference;
	Type type;
	int index;
	/*
	public RuntimeField(RuntimeObject thisReference, int index) {
		this.thisReference = thisReference;
		this.index = index;
	}*/
	public RuntimeField(String name, Type type, int index) {
		super(new Signature(name, new FormalParameters()));
		this.type = type;
		this.index = index;
	}
	
	@Override
	public Type getOutputType() {
		return type;
	}
	@Override
	public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
		//assert params.length == 0;
		//assert params == null;
		//assert params.getRuntimeType() == DumbInterpreter.VoidType;
		assert params.getRuntimeType().isTuple() && ((TupleType)params.getRuntimeType()).isEmpty();
		
		///System.out.println("--->"+index+" "+thisReference.read(index));
		
		//return thisReference.data[index];
		return thisReference.read(index);
	}
	
	@Override
	public boolean isSettable() {
		return true;
	}
	@Override
	public void setDelegate(RuntimeObject thisReference, RuntimeObject params, RuntimeObject value) {
		thisReference.write(index, value);
	}

}

class TypeIdentifier {
	String name;
	Type parent;
	TypeBase type;
	
	public TypeIdentifier(String name, Type parent) {
		this.name = name;
		this.parent = parent;
	}
	@Override
	public String toString() {
		return (parent == null ? "" : parent.toString()+".")+name;
	}
	public String detailedString() {
		//return toString()+"  ("+type.attributeTypes+")";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		/*
		Iterator<RuntimeObject> ite = values();
		while(ite.hasNext())
			sb.append(ite.next());*/
		for (int i = 0; i < type.attributeTypes.size(); i++) {
			String fname = type.getAttributeNames()[i];
			sb.append((fname==null?"":fname)+":"+type.getAttributeTypes()[i]+"; ");
		}
		if (type.attributeTypes.size() > 0)
			sb.delete(sb.length()-2, sb.length());
		sb.append("}");
		return toString()+"  "+sb.toString();
	}
}

class CandidateList {
	private List<Pair<Type,Pair<Function, String>>> list = new ArrayList<>();
	
	public void add(Type t, Function f, String s) {
		list.add(new Pair<Type, Pair<Function,String>>(t, new Pair<Function,String>(f, s)));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (list.size() == 0)
			sb.append("No candidates found.");
		else {
			sb.append("Candidates are:");
			/*for (Pair<Type,Function> p : list) {
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond());
			}*/
			for (Pair<Type,Pair<Function, String>> p : list)
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond().getFirst()+" ("+p.getSecond().getSecond()+")");
		}
		return sb.toString();
	}
}

/*
class Type {
	Map<Signature, Function> fields;
}*/
interface Type {
	boolean isDetermined();
	boolean isTuple();
	String notConformingToBecause(Signature sign);
	boolean conformsTo(Signature sign);
	//Function getFunction(CallSignature sign) throws CompilerException;
	Function getFunction(CallSignature sign, CandidateList candidates) throws CompilerException;
	Type[] getAttributeTypes();
	String[] getAttributeNames();
	List<Function> getConstructors(); // TODO: cannot overload type name with def
}

class TypeOf implements Type {
	Expression expr;
	public TypeOf(Expression expr) {
		this.expr = expr;
	}
	Type getType() {
		try {
			return expr.getType();
		} catch (CompilerException e) {
			//throw new RuntimeException(e);
			throw new ExecutionException(e);
		}
	}
	@Override public boolean isDetermined() {
		return getType().isDetermined();
	}
	@Override public boolean isTuple() {
		return getType().isTuple();
	}
	@Override public String notConformingToBecause(Signature sign) {
		return getType().notConformingToBecause(sign);
	}
	@Override public boolean conformsTo(Signature sign) {
		return getType().conformsTo(sign);
	}
	@Override public Function getFunction(CallSignature sign, CandidateList candidates) throws CompilerException {
		return getType().getFunction(sign, candidates);
	}
	@Override public Type[] getAttributeTypes() {
		return getType().getAttributeTypes();
	}
	@Override public String[] getAttributeNames() {
		return getType().getAttributeNames();
	}
	@Override public List<Function> getConstructors() {
		return getType().getConstructors();
	}
	@Override public String toString() {
		if (Execution.hasStarted())
			 return getType().toString();
		else return "TypeOf("+expr+")";
		//else return "TypeOf"+expr+"";
	}
}

abstract class TypeBase implements Type {
	HashMap<String, List<Function>> fcts = new HashMap<>();
	//Type[] attributes;
	TypeIdentifier id;
	//boolean tuple;
	
	public TypeBase(TypeIdentifier id) {//, boolean tuple) {
		this.id = id;
		//this.tuple = tuple;
		id.type = this; // sale
	}
	
	public void addFct(Function fct) { // TODO: detect ambiguities
		//System.out.println("Adding "+fct.signature);
		System.out.println("Adding "+fct+"  in type  "+this);
		
		List<Function> ls = fcts.get(fct.signature.name);
		if (ls == null) {
			ls = new ArrayList<Function>();
			fcts.put(fct.signature.name, ls);
		}
		ls.add(fct);
	}

	public void addType(Type t) {
		for (Function f : t.getConstructors())
			addFct(f);
	}
	
	
	@Override
	public boolean isDetermined() {
		return true;
	}
	@Override
	public Function getFunction(CallSignature callSign, CandidateList candidates) throws CompilerException {
		///
		//System.out.println("Searching for function "+callSign.name+" in "+id.detailedString());
		//System.out.println("\t"+callSign);

		// HEAVY //System.out.println("Searching for function "+callSign+" in "+id.detailedString());
		// LIGHT //
			System.out.println("Searching "+callSign.name+" in "+id);
		
		Function ret = null;
		List<Function> ls = fcts.get(callSign.name);
		if (ls == null)
			//throw new CompilerException("Name '"+callSign.name+"' is unknown in type "+this);
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+"\n\t"+candidates.toString());
		for (Function f : ls) {
			/*
			System.out.println(f);
			System.out.println(callSign.conformsTo(f.signature));
			*/
			//candidates.list.add(new Pair<Type,Function>(this,f));
			
			/*
			if (callSign.conformsTo(f.signature)) {
				if (ret != null)
					throw new CompilerException("Ambiguous call signature");
				ret = f;
			}*/
			
			String reason = callSign.notConformingToBecause(f.signature);
			if (reason == null) {
				candidates.add(this,f,"Conforming");
				if (ret != null)
					throw new CompilerException("Ambiguous call signature\n\t"+candidates);
				ret = f;
			} else
				candidates.add(this,f,reason);
			
		}
		if (ret == null)
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+" with parameters "+callSign.args+"\n\t"+candidates.toString());
		return ret;
	}
	/*
	@Override
	public Type[] getAttributes() {
		return attributes;
	}*/
	
	
	List<String> attributeNames = new ArrayList<>();
	List<Type> attributeTypes = new ArrayList<>();

	public void addAttribute(PAttrType attrType, TIdent name, Type type) {
		addAttribute(attrType, name.getText(), type);
	}
	public void addAttribute(PAttrType attrType, String name, Type type) {
		// TODO: use attrType to generate a Function.FieldType
		addFct(new RuntimeField(name, type, attributeTypes.size()));
		attributeNames.add(name);
		attributeTypes.add(type);
	}

	@Override
	public String[] getAttributeNames() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.toArray(new String[attributeNames.size()]);
	}
	
	@Override
	public Type[] getAttributeTypes() {
		assert attributeNames.size() == attributeTypes.size();
		//return (Type[]) attributeTypes.toArray();
		//return attributeTypes.toArray<Type>();
		//new ArrayList<Type>().t
		return attributeTypes.toArray(new Type[attributeTypes.size()]);
	}

	@Override
	public boolean isTuple() {
		//return tuple;
		return false;
	}
	
	@Override
	// returns null if it conforms
	public String notConformingToBecause(Signature sign) {
		
		//System.out.println(this);
		
		assert isTuple(); // even single and void params are converted to a tuple with one or zero unnamed value, when passed to a function
//		if (isTuple()) {
//			throw new NotSupportedCompExc();
//		} else {
//			if (sign.params.withoutValueNb() > 1)
//				return "More than one argument is needed";
//			return null;
//		}
		
		
		
		
		
		
		
		// handle default values
		
		
		// bind ordinals to names...?
		
		
		
		
		
		
		
		
		return null;
		/*
		if (sign.params.namedTypes.length == 1) {
			
		}*/
	}
	
	@Override
	public boolean conformsTo(Signature sign) {
		//System.out.println("! "+notConformingToBecause(sign));
		//if (sign.namedTypes.length == 1)
		return notConformingToBecause(sign) == null;
	}
	
	public boolean hasAttributes() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.size() != 0;
	}
	public boolean hasFunctions() {
		return fcts.size() != 0;
	}
	public boolean isEmpty() {
		return !hasAttributes() && !hasFunctions();
	}
	
	
	@Override
	public String toString() {
		return id.toString();
	}
}

class TupleType extends TypeBase {
	Function constructor;
	
	/*
	 * Invariant:
	 * 0..p unnamed attributes, THEN 0..q named attributes
	 */
	
	public TupleType(TypeIdentifier id) {
		//super(id, false);
		super(id);
	}
	
	@Override public boolean isTuple() { return true; }
	
	public void done() {
		
		NamedType[] nts = new NamedType[attributeTypes.size()];
		for (int i = 0; i < attributeTypes.size(); i++) {
			nts[i] = new NamedType(attributeTypes.get(i), attributeNames.get(i), false); // FIXME: use provided default values!
		}
		final Type that = this;
		constructor = new Function(new Signature(id.name, new FormalParameters(nts))) {
			@Override public Type getOutputType() {
				return that;
			}
			@Override public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
				if (!params.getRuntimeType().conformsTo(signature))
					throw new IllegalArgumentException();
				return new RuntimeObjectBase(that, params, DumbInterpreter.standardScopeRO, false);
			}
		};
		
	}
	
	public List<Function> getConstructors() { // FIXME: correct?
		//return new ArrayList<Function>();
		return Arrays.asList(new Function[]{constructor});
	}
}

class PrimitiveType<T> extends TypeBase {
	Function constructor;
	
	public PrimitiveType(TypeIdentifier id) {
		//super(id, false);
		super(id);
		final PrimitiveType<T> that = this;
		constructor = new Function(new Signature(id.name, new FormalParameters(new NamedType[]{new NamedType(this,null,true)}))) {
			@Override public Type getOutputType() {
				return that;
			}
			@Override public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
				//return new RuntimeObjectBase(that, DumbInterpreter.standardScopeRO, false);
				return new PrimitiveRuntimeObject<T>(that, (T) params.getValue(), DumbInterpreter.standardScopeRO, false); // FIXME: perform checks..?
			}
		};
	}
	/*@Override
	public Type[] getAttributeTypes() {
		return new Type[] {};
	}*/
	
	@Override
	public List<Function> getConstructors() {
		//ArrayList<Function> ret = new ArrayList<Function>();
		//Arrays.asList()
		//return new ArrayList<Function>(new Function[]{constructor});
		return Arrays.asList(new Function[]{constructor});
	}
}


/*
class KnownType implements Type {

	@Override
	public boolean isDetermined() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Function getFunction(Signature sign) throws CompilerException {
		// TODO Auto-generated method stub
		return null;
	}
}

class TypeOfExpr implements Type {

	@Override
	public boolean isDetermined() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Function getFunction(Signature sign) throws CompilerException {
		// TODO Auto-generated method stub
		return null;
	}
	//Map<Signature, Function> fields;
	
}*/

interface RuntimeObject { // TODO: accessors for reading/modifying fields; check initialized etc.
	Type getRuntimeType();
	boolean isValue();
	Object getValue() throws ExecutionException;
	Iterator<RuntimeObject> values();
	RuntimeObject read(int index) throws ExecutionException;
	void write(int index, RuntimeObject obj) throws ExecutionException;
	RuntimeObject getParent();
	/*void assign(RuntimeObject value);
	boolean isAssignable();*/
	String toOutput();
}

class RuntimeObjectBase implements RuntimeObject {
	
	Type type;
	boolean constant;
	protected RuntimeObject[] attributes;
	RuntimeObject parent;
	
	public RuntimeObjectBase(Type type, RuntimeObject parent, boolean constant) {
		this.type = type;
		this.parent = parent;
		this.constant = constant;
		attributes = new RuntimeObject[type.getAttributeTypes().length];
	}
	
	public RuntimeObjectBase(Type type, RuntimeObject params, RuntimeObject parent, boolean constant) {
		this(type, parent, constant);
		//if (!params.getRuntimeType().conformsTo(type.g)) throw new IllegalArgumentException();
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

	@Override
	public boolean isValue() {
		return false;
	}
	@Override
	public Object getValue() throws ExecutionException {
		throw new ExecutionException("Cannot get the value of a non-value object");
	}
	@Override
	public final RuntimeObject read(int index) { // TODO: check types?
		return readDelegate(index);
	}
	
	@Override
	public Iterator<RuntimeObject> values() {
		//return new ArrayList<RuntimeObject>(attributes).iterator();
		return Arrays.asList(attributes).iterator();
	}
	
	@Override
	public final void write(int index, RuntimeObject obj) { // TODO: check types?
		if (constant)
			throw new ExecutionException("Cannot write to a constant object");
		writeDelegate(index, obj);
	}
	public RuntimeObject readDelegate(int index) {
		if (attributes[index] == null)
			throw new ExecutionException("Value "+type.getAttributeNames()[index]+" (at index "+index+") in "+this+" was not initialized when read");
		return attributes[index];
	}
	
	@Override
	public Type getRuntimeType() {
		return type;
	}
	public void writeDelegate(int index, RuntimeObject obj) {
		attributes[index] = obj;
	}
	
	@Override
	public RuntimeObject getParent() {
		return parent;
	}
	/*
	@Override
	public boolean isAssignable() {
		return false;
	}
	@Override
	public final void assign(RuntimeObject value) {
		if (!isAssignable())
			throw new ExecutionException("Runtime object "+this+" is not assignable");
		assignDelegate(value);
	}
	private void assignDelegate(RuntimeObject value) { }
	 */
	
	@Override
	public String toString() {
		//return "{"++"}";
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		/*
		Iterator<RuntimeObject> ite = values();
		while(ite.hasNext())
			sb.append(ite.next());*/
		for (int i = 0; i < attributes.length; i++) {
			String fname = type.getAttributeNames()[i];
			sb.append((fname==null?"":fname)+":"+type.getAttributeTypes()[i]+"="+attributes[i]+"; ");
		}
		if (attributes.length > 0)
			sb.delete(sb.length()-2, sb.length());
		sb.append("}");
		return sb.toString();
	}
	@Override
	public String toOutput() {
		return toString();
	}
}

//class PrimitiveRuntimeObject<T, PT extends PrimitiveType<T>> extends RuntimeObjectBase {
class PrimitiveRuntimeObject<T> extends RuntimeObjectBase {
	T val;
	public PrimitiveRuntimeObject(PrimitiveType<T> type, T val, RuntimeObject parent, boolean constant) {
		super(type, parent, constant);
		this.val = val;
	}
	@Override
	public boolean isValue() {
		return true;
	}
	@Override
	public Object getValue() throws ExecutionException {
		return val;
	}
	@Override
	public String toString() {
		if (val instanceof String)
			return "'"+val.toString()+"'";
		return val.toString();
	}
	@Override
	public String toOutput() {
		return val.toString();
	}
}
/*
class ConstantRuntimeObject extends RuntimeObjectBase {
	
	public ConstantRuntimeObject(Type type) {
		super(type);
	}

	public void writeDelegate(RuntimeObject obj, int index) {
		attributes[index] = obj;
	}
	
}*/

/*
class RuntimeObject { // TODO: accessors for reading/modifying fields; check initialized etc.
	Type type;
	Object[] data;
	
//	Object get(Signature sign) {
//		return data[type.fields.get(sign).index];
//	}
//	Object get(String name) {
//		//return data[type.fields.get(new Signature(name, new Type[]{})).index];
//		return get(new Signature(name, new Type[]{}));
//	}
	
}*/

/*
class Value {
	final Type type;
	
	public Value(Type type) {
		this.type = type;
	}
}*/

/*
class Pointer {
	
}
class Struct {
	long[] integers;
	String[] strings;
	HashMap<String, Pointer> members;
}*/


class Execution {
	private static RuntimeObject thisObject = null; //DumbInterpreter.standardScopeRO;
	static Stack<RuntimeObject> thisStack = new Stack<>();
	static final RuntimeObject voidObj = new PrimitiveRuntimeObject<Void>(DumbInterpreter.VoidType, new Void(), DumbInterpreter.standardScopeRO, true);
	static int indentation = -1;
	static List<String> output = new ArrayList<>();
	public static PrintStream out = System.out;
	static boolean started;
	
	static RuntimeObject execute(RuntimeObject params, Scope scope)
	{
		started = true;
		indentation++;
		//System.out.println(scope);
		out("Executing scope with "+scope.exprs.size()+" expression statements");
		thisStack.push(thisObject);
		//thisObject = new RuntimeObjectBase(scope.type, thisObject, false) {};
		thisObject = new RuntimeObjectBase(scope.type, params, false);
		for (Expression expr : scope.exprs) {
			//out("Expression "+expr+" produced: "+expr.evaluate());
			RuntimeObject res = expr.evaluate();
			/*out("Expression "+expr);
			out("\tproduced: "+res);*/
			out("Expression  \""+expr+"\"  produced value: "+res);
		}
		thisObject = thisStack.pop();
		indentation--;
		return voidObj; // FIXME
	}
	
	public static boolean hasStarted() {
		return started;
	}

	static RuntimeObject getThis() {
		return thisObject;
	}
	
	//static void message(String msg) {
	static void message(RuntimeObject msg) {
		//out("<<<< Execution Message: \""+msg+"\" >>>>");
		out("<<<< Execution Message: "+msg+" >>>>");
		
		//System.out.println("!!! Adding "+msg);
		
		output.add(msg.toOutput());
	}
	
	static void out(String string) {
		out.print(indentation+":");
		if (indentation == 0)
			out.print(" ");
		for (int i = 0; i < indentation; i++)
			out.print("\t");
		out.println(string);
	}
}

class Void {
	@Override public String toString() { return "void"; }
}

public class DumbInterpreter extends DepthFirstAdapter {
	/*
	public final static PrimitiveType<Void> VoidType = new PrimitiveType<Void>(new TypeIdentifier("[Builtin Void]", null));
	public final static PrimitiveType<Integer> IntType = new PrimitiveType<Integer>(new TypeIdentifier("[Builtin Int]", null));
	public final static PrimitiveType<Long> LongType = new PrimitiveType<Long>(new TypeIdentifier("[Builtin Long]", null));
	public final static PrimitiveType<String> StringType = new PrimitiveType<String>(new TypeIdentifier("[Builtin String]", null));
	*/
	public final static PrimitiveType<Void> VoidType = new PrimitiveType<Void>(new TypeIdentifier("Void", null));
	public final static PrimitiveType<Integer> IntType = new PrimitiveType<Integer>(new TypeIdentifier("Int", null));
	public final static PrimitiveType<Long> LongType = new PrimitiveType<Long>(new TypeIdentifier("Long", null));
	public final static PrimitiveType<String> StringType = new PrimitiveType<String>(new TypeIdentifier("Str", null));
	
	public final static Scope standardScope = new Scope("[StdScope]");
	//public final static RuntimeObject standardScopeRO = new RuntimeObjectBase(new TypeBase(new TypeIdentifier("StdType", null), false) {}, null, true) {};
	public final static RuntimeObject standardScopeRO = new RuntimeObjectBase(standardScope.type, null, false) {};
	//public final static TupleExpression emptyExpr = new TupleExpression();
	
	//public static TupleExpression getEmptyExpr() {
	public static TupleExpression getEmptyExpr(Type parentType) {
		try {
			return new TupleExpression(parentType);
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}
	
	int indentation = 0;
	Scope currentScope;
	Map<PExpr,Expression> exprs = new HashMap<>();
	Map<Node,Expression> otherExprs = new HashMap<>();
	//Map<PExpr,Scope> scopes = new HashMap<>();
	Map<PClosure,Scope> scopes = new HashMap<>();

	public DumbInterpreter() {
		
	}
	
	public DumbInterpreter(String source, PrintStream out) {
		Execution.out = out;
		Execution.started = false;
		out("############# SOURCE CODE #############");
		out(source);
	}
	
	private void out(String string) {
		for (int i = 0; i < indentation; i++)
			Execution.out.print("\t");
		Execution.out.println(string);
	}
	

	/*** DECLARATION ***/

	@Override
    public void inStart(Start node)
    {
		out("\n############# PROGRAM ANALYSIS #############");
    	//AClosure module = (AClosure) node.getPClosure();
    	//currentScope = new Scope();
		//currentScope = null;
		currentScope = standardScope;
		
    	/// TODO: define standard lib in a closure
    	
    }
	
	@Override
    public void outStart(Start node)
    {
		Execution.output.clear();
    	// Execute the program
		out("\n############# PROGRAM EXECUTION #############");
		/*for (Expression expr : currentScope.exprs) {
			System.out.println("Expression "+expr+" produced: "+expr.evaluate());
		}*/
		Execution.execute(standardScopeRO, currentScope);
		out("\n############# PROGRAM OUTPUT #############");
		for (String str : Execution.output)
			Execution.out.println(str);
    }
	
	private int nbClos = 0;
	@Override
    public void inAClosure(AClosure node)
    {
    	currentScope = new Scope("[AnonClosure "+nbClos+"]", currentScope);
		/*if (currentScope == null)
			 currentScope = standardScope;
		else currentScope = new Scope("[AnonClosure "+nbClos+"]", currentScope);*/
		
    	scopes.put(node, currentScope);
		nbClos++;
    }

	@Override
    public void outAClosure(AClosure node)
    {
    	//currentScope = currentScope.parent;
		//if (currentScope.parent != null)
		currentScope.type.done();
		if (currentScope.parent != standardScope)
			currentScope = currentScope.parent;
    }
	
	/*
	@Override
    public void inAClosureExpr(AClosureExpr node)
    {
    	scopes.put(node, currentScope);
    }*/
	
	@Override
    public void outAFieldAccess(AFieldAccess fa)
    {
		System.out.println("Field Access "+fa);
		//AFieldAccess fa = ((AFieldAccess)((AAccessNakedType) t).getFieldAccess());
		Expression expr = exprs.get(fa.getPrefixExpr());
		//otherExprs.put(fa, new FieldAccessExpression(expr, fa.getIdent().getText(), new EmptyExpr()));
		otherExprs.put(fa, new FieldAccessExpression(expr, fa.getIdent().getText(), getEmptyExpr(currentScope.type)));
    }
	
	Type determineType(ATypedValue tval) throws CompilerException {
		//AType t;
		if (tval.getType() == null) { // Determine type using expression
			
			if (tval.getValue() == null)
				throw new CompilerException("Cannot infer type from a null value");
			

			//out("Expr: "+tval.getValue().hashCode());
			//out("Expr: "+tval.getValue());
			
			Expression expr = exprs.get(tval.getValue());
			//return expr.getType();
			return new TypeOf(expr);
			
		} else { // Type is stated explicitly
			
			//t = (AType) tval.getType();
			return determineType((AType) tval.getType());
			
		}
	}
	
	private Type determineType(AType type) throws CompilerException {
		PNakedType t = type.getBase();
		
		//System.out.println("det type");
		
		if (t instanceof AIdentNakedType) {
			/*
			return currentScope.type.getFunction(
					new CallSignature(
							((AIdentNakedType) t).getName().getText(),
							//new ArrayList<Expression>()
							new EmptyExpr()
						)
				).getOutputType();*/
			
			//System.out.println("det type "+((AIdentNakedType) t).getName().getText());
			
			//return new TypeOf(new FieldAccessExpression(null, ((AIdentNakedType) t).getName().getText(), new EmptyExpr()));
			return new TypeOf(new FieldAccessExpression(null, ((AIdentNakedType) t).getName().getText(), getEmptyExpr(currentScope.type)));
			
		} else if (t instanceof AAccessNakedType) {
			/*
			AFieldAccess fa = ((AFieldAccess)((AAccessNakedType) t).getFieldAccess());
			Expression expr = exprs.get(fa.getPrefixExpr());
			//new InvokeExpression(expr,fa.getIdent().getText());
			return new FieldAccessExpression(expr, fa.getIdent().getText()).getType();
			*/
			//return otherExprs.get(((AAccessNakedType) t).getFieldAccess()).getType();
			return new TypeOf(otherExprs.get(((AAccessNakedType) t).getFieldAccess()));
		} else
			throw new CompilerError("PNakedType is neither a AIdentNakedType or a AAccessNakedType");
	}
	

	@Override
    public void inAAttrDeclStatement(AAttrDeclStatement node)
    {
		out("Reading attr decl: "+node.getName());
		indentation++;
    }
	@Override
    public void outAAttrDeclStatement(AAttrDeclStatement node)
    {
        //node.getAttrDecl()
		/*
		currentScope.addFct(new Function() {
			@Override public Type getOutputType() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override public RuntimeObject evaluate(RuntimeObject thisReference,
					RuntimeObject[] params) {
				// TODO Auto-generated method stub
				return null;
			}
		});*/
		
		//currentScope.addFct(new RuntimeField(node.getName(), currentScope.type, index)
		
		//PAttrType attrType = node.getAttrType();
		/*ATypedValue t = (ATypedValue)node.getTypedValue();
		t.getType()*/
		
		try {
			ATypedValue tv = (ATypedValue)node.getTypedValue();
			currentScope.type.addAttribute(node.getAttrType(), node.getName(), determineType(tv));
			
			// TODONE: add expr
			
			//Expression val = tv.getValue() == null? getEmptyExpr(): exprs.get(tv.getValue());
			
			if (tv.getValue() != null) {
				Expression val = exprs.get(tv.getValue());
				
				currentScope.exprs.add(new AssignExpression(new FieldAccessExpression(null, node.getName().getText(), getEmptyExpr(currentScope.type)), val));
				
			}
					
		} catch (CompilerException e) {
			throw new RuntimeException(e);
		}
		
		indentation--;
		
    }
    
	
	
	
	
	
	

	/*** EVALUATION ***/
	

	@Override
    public void inAListExpr(AListExpr node) {
		//out("List of expressions... "+node);
		//indentation++;
    }
	
	@Override
    public void outAListExpr(AListExpr node) {
		//indentation--;
		/*
		List<PExpr> ls = node.getExpr();
		assert ls.size() > 0;
		if (ls.size() == 1) {
			exprs.put(node, exprs.get(ls.get(0)));
		} else {
			// TODO
		}*/
		List<PExpr> ls = node.getExpr();
		assert ls.size() > 0;
		//exprs.put(node, exprs.get(ls.get(0)));
		
		Expression firstExpr = exprs.get(ls.get(0));
		if (ls.size() == 1 && !(firstExpr instanceof AssignExpression)) {
			//System.out.println("OK");
			exprs.put(node, firstExpr);
		} else {
			Expression[] exs = new Expression[ls.size()];
			for (int i = 0; i < ls.size(); i++)
				exs[i] = exprs.get(ls.get(i));
			try {
				//System.out.println("-------->"+exs[0]);
				exprs.put(node, new TupleExpression(exs, currentScope.type, null/*TODO*/));
			} catch (CompilerException e) {
				throw new ExecutionException(e);
			}
		}
		
    }
    


	
	@Override
	public void inANumberExpr(ANumberExpr node)
	{
		out("Number: "+node.getIntegerNumber().getText());
		//exprs.put(node, new Constant(new ConstantRuntimeObject<Long>(Long.parseLong(node.getIntegerNumber().getText()))));
		exprs.put(node, new Constant(new PrimitiveRuntimeObject<Long>(LongType, (Long)Long.parseLong(node.getIntegerNumber().getText()), standardScopeRO, true)));
		//out("Number: "+node.hashCode());
	}

	@Override
	public void inAStringExpr(AStringExpr node)
	{
		out("String: "+node.getStringContent().getText());
		exprs.put(node, new Constant(new PrimitiveRuntimeObject<String>(StringType, node.getStringContent().getText(), standardScopeRO, true)));
	}
	
	@Override
	//public void inAIdExpr(AIdExpr node)
	public void outAIdExpr(AIdExpr node)
	{
		out("Ref to ident: "+node.getGeneralId());
		//currentScope.get(node.getGeneralId().)
		assert node.getGeneralId() instanceof AIdentGeneralId;
		String str = ((AIdentGeneralId) node.getGeneralId()).getIdent().getText();
		//Name n = currentScope.getOrCreate(str, node);
		//Name n = 
		///currentScope.getOrCreate(str, null);
		
		exprs.put(node, new FieldAccessExpression(null, str, getEmptyExpr(currentScope.type)));
		
	}
	
	
    public void outAAssignExpr(AAssignExpr node)
    {
    	out("Assignation of "+node.getAssigned()+" to  "+node.getValue());
    	
    	/*AExpr assigned = (AExpr) node.getAssigned();
    	exprs.get(assigned)*/
    	Expression assigned = exprs.get(node.getAssigned());
    	Expression value = exprs.get(node.getValue());
    	//System.out.println(node.getValue().getClass());
    	assert assigned != null && value != null;
    	//System.out.println(value.getClass());
    	/*
    	exprs.put(node, new Expression() {
			@Override public Type getType() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override public RuntimeObject evaluate() {
				// TODO Auto-generated method stub
				return null;
			}
    	});
    	*/
    	
    	exprs.put(node, new AssignExpression(assigned, value));
    	
    	
    	
    	// TODO
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    }
	
	
    
	@Override
    public void outAInvocationExpr(AInvocationExpr node)
    {
		//out("O Invocation "+node);
		
		if (node.getTargs() != null)
			throw new CompilerError("Templates not yet supported");
		
		
		//out(exprs.get(node.getCallee()).toString());
		
		/*
        exprs.put(node, new InvokeExpression(
        		exprs.get(node.getCallee()),
        		exprs.get(node.getArgs()))
        	);*/
		
		LinkedList<PExpr> callees = node.getCallee();

		
//		System.out.println(callees);
//		System.out.println(callees.size());
//		System.out.println(callees.get(callees.size()-1).getClass());
		
		Expression callee = exprs.get(callees.get(callees.size()-1)); // The last expression 'c' is actually the callee in  a syntax like "a,b,c params"
		
		assert callee != null;
		//System.out.println(callee.getClass());
		
		if (callee instanceof FieldAccessExpression) {
			
			
			/*
			//System.out.println("!!! "+node.getArgs()+" "+exprs.get(node.getArgs()));
			assert node.getArgs() == null || (node.getArgs() != null && exprs.get(node.getArgs()) != null);
			assert exprs.get(node.getArgs()) instanceof TupleExpression;
			
			TupleExpression args = (TupleExpression) exprs.get(node.getArgs());
			if (args == null) args = getEmptyExpr();
			((FieldAccessExpression) callee).setArgs(args);
			
			if (callees.size() == 1) {
				
				exprs.put(node, callee);
				
			} else {
			*/
			
			
			//System.out.println("!!! "+node.getArgs()+" "+exprs.get(node.getArgs()));
			assert node.getArgs() == null || (node.getArgs() != null && exprs.get(node.getArgs()) != null);
			//assert exprs.get(node.getArgs()) instanceof TupleExpression;
			
			//if (args == null) {
			if (node.getArgs() == null)
				//((FieldAccessExpression) callee).setArgs(getEmptyExpr());
				;
			else {
				
				Expression args = exprs.get(node.getArgs());
				assert args != null;

				//System.out.println("*********"+((AssignExpression)args).value);
				//System.out.println("*********"+((FieldAccessExpression)args).args);
				//if (args instanceof TupleExpression) System.out.println("*********"+((TupleExpression)args));
				
				TupleExpression tupleArgs;
				
				try {
					
					if ((args instanceof TupleExpression))
						 tupleArgs = (TupleExpression) args;
					else tupleArgs = new TupleExpression(new Expression[]{args},currentScope.type,"[SingleArg]");
					
					//System.out.println("*********"+tupleArgs.namedParams);
					
					//TupleExpression tupleArgs = (TupleExpression) exprs.get(node.getArgs());
					
					//if (tupleArgs == null) tupleArgs = getEmptyExpr();
					((FieldAccessExpression) callee).setArgs(tupleArgs);
					
					
					if (callees.size() == 1) {
						
						exprs.put(node, callee);
						
					} else {
						
						// TODO
						//exprs.put(node, new ListExpression(callees));
						
						
					}
						
						
						
						
						
						
					
				} catch (CompilerException e) {
					throw new ExecutionException(e);
				}
				
			} 
			
		} else {
			throw new NotSupportedCompExc();
		}
		
		
    }
	
    

	@Override
	public void inADefDeclStatement(ADefDeclStatement node)
	{
		out("Reading def: "+node.getName());
		indentation++;
	}
	@Override
	public void outADefDeclStatement(ADefDeclStatement node)
	{
		PExpr initVal = ((ATypedValue)node.getTypedValue()).getValue();
		AClosure paramClosure = (AClosure)node.getParams();
		
		//scopes.get(paramClosure);
		
		//System.out.println(v.getClass());
		if (initVal instanceof AListExpr) {
			LinkedList<PExpr> ls = ((AListExpr) initVal).getExpr();
			if (ls.size() != 1)
				throw new ExecutionException(new CompilerException("Function was initialized with "+ls.size()+" expression(s) instead of 1"));
			initVal = ls.get(0);
		}
		
		if (initVal instanceof AClosureExpr) {
			
			//final Scope subScope = scopes.get(((AClosureExpr)v).getClosure());
			final PClosure myClosure = ((AClosureExpr)initVal).getClosure();
			//System.out.println(subScope);
			
			FormalParameters fparams;
			//fparams = new FormalParameters(new NamedType[] { });
			
			if (paramClosure == null)
				 fparams = new FormalParameters();
			else {
				assert scopes.get(paramClosure).type.getConstructors().size() == 1;
				fparams = scopes.get(paramClosure).type.getConstructors().get(0).signature.params;
			}
			
			currentScope.type.addFct(new Function(new Signature(node.getName().getText(), fparams)) {
				
				//final Scope scope = currentScope;
				//final PClosure myClosure = ((AClosureExpr)v).getClosure());
				
				@Override
				public Type getOutputType() {
					return VoidType; // FIXME
				}
				
				@Override
				public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
					//System.out.println(scopes.get(myClosure));
					
					//Execution.execute(subScope);
					Execution.execute(params, scopes.get(myClosure));
					return Execution.voidObj;//new PrimitiveRuntimeObject<Void>(VoidType, new Void(), true);
				}
				
			});
			
		} else throw new ExecutionException(new CompilerException("Function was not initialized with a closure"));
		
		indentation--;
	}
	
	
	@Override
	public void inAExprStatement(AExprStatement node)
	{
		//out("Analysing expression: "+node.getExpr());
		out("Reading statement: "+node.getExpr());
		indentation++;
	}
	@Override
	public void outAExprStatement(AExprStatement node)
	{
		//System.out.println("Expr statement: "+node.getExpr().toString());
		/*exprs.put(node, new Expression() {
			@Override
			public Type getType() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public RuntimeObject evaluate() {
				// TODO Auto-generated method stub
				return null;
			}
		});*/
		currentScope.addExpr(exprs.get(node.getExpr()));
		indentation--;
	}
	
	//@Override
	//public void inA
	
	
	
	
	
}











