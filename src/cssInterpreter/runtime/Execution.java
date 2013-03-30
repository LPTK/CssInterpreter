package cssInterpreter.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.Interpreter;
import cssInterpreter.program.PrimitiveRuntimeObject;
import cssInterpreter.program.PrimitiveType;
import cssInterpreter.program.Scope;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeIdentifier;
import cssInterpreter.program.Void;
import cssInterpreter.program.expressions.Expression;
import cssInterpreter.program.expressions.TupleExpression;
import cssInterpreter.runtime.Reference.RefKind;

public class Execution {
	
	private static Execution instance;
	{instance = this;}

	//private RuntimeObject thisObject = null; //DumbInterpreter.standardScopeRO;
	private Reference currentThisRef = null;
	//Stack<RuntimeObject> thisStack = new Stack<>();
	
	//private RuntimeObject currentArgObject = null;
	//Stack<RuntimeObject> argStack = new Stack<>();
	
	int indentation = -1;
	List<String> output = new ArrayList<>();
	PrintStream out;// = System.out;
	boolean started;
	Interpreter interp;
	
	Stack<Reference> currentLocalStack;
	
	public final List<Type> allAnonTypes = new ArrayList<>();
	
	// These must also be set in the standard scope in class Scope (to change because not convenient)
	public final PrimitiveType<Type> TypeType = new PrimitiveType<>(new TypeIdentifier("Type", null), this);
	{TypeType.setType(TypeType);}
	public final PrimitiveType<Void> AnyType = new PrimitiveType<>(new TypeIdentifier("Any", null), this);
	public final PrimitiveType<Void> VoidType = new PrimitiveType<>(new TypeIdentifier("Void", null), this);
	public final PrimitiveType<Boolean> BoolType = new PrimitiveType<>(new TypeIdentifier("Bool", null), this);
	public final PrimitiveType<Integer> IntType = new PrimitiveType<>(new TypeIdentifier("Int", null), this);
	public final PrimitiveType<Long> LongType = new PrimitiveType<>(new TypeIdentifier("Long", null), this);
	public final PrimitiveType<String> StringType = new PrimitiveType<>(new TypeIdentifier("Str", null), this);
	
	public final Scope standardScope = new Scope("[Std]", this);
	public final RuntimeObject standardScopeRO = new RuntimeObject(standardScope.getType(), null, false) {};
	
	final PrimitiveRuntimeObject<Void> voidObj = new PrimitiveRuntimeObject<>(VoidType, new Void(), standardScopeRO, true);
	
	public Execution(Interpreter interp, PrintStream out) {
		this.interp = interp;
		this.out = out;
		//instance = this;
	}
	
//	public RuntimeObject execute(RuntimeObject args, Scope scope) throws CompilerException
//	{
//		////System.out.println(">> Executing:  "+scope.getType()+"  with params  "+args);
//		
//		assert scope.getParent() == null || args == null || args.type.conformsTo(scope.getParent().getType());
//		
//		started = true;
//		indentation++;
//		//System.out.println(scope);
//		
//		//out("Executing scope with "+scope.getExprs().size()+" expression statements");
//		out("Executing "+scope.getType()+" with args "+args+", containing "+scope.getExprs().size()+" expression statements");
//		//out("containing "+scope.getExprs().size()+" expression statements");
//
//		thisStack.push(thisObject);
//		//thisObject = new RuntimeObjectBase(scope.type, thisObject, false) {};
//		
//		//System.out.println(">> Adding:  "+params+": "+params.getRuntimeType()+" as parent to "+scope.getType());
//		////System.out.println(">> Creating 'this' for "+scope.getType()+" using parent "+params);
//		
//		//argStack.push(currentArgObject);
//		//currentArgObject = args; // TODO: remove arg's parents
//		
//		if (args == null)
//			args = thisObject;
//		
//		thisObject = new RuntimeObject(scope.getType(), args, false);
//		
//		//indentation++;
//		for (Expression expr : scope.getExprs()) {
//			//out("Expression "+expr+" produced: "+expr.evaluate());
//			RuntimeObject res = expr.evaluate();
//			/*out("Expression "+expr);
//			out("\tproduced: "+res);*/
//			out("Expression  \""+expr+"\"  produced value: "+res);
//		}
//		//indentation--;
//		
//		RuntimeObject retObj;
//		
//		if (scope.hasReturnStatement()) {
//			thisObject.destruct();
//			retObj = null; // TODO
//		} else {
//			retObj = thisObject;
//		}
//		
//		thisObject = thisStack.pop();
//		indentation--;
//		
//		//return getVoidobj(); // FIXME
//		return retObj;
//	}
	
	//public RuntimeObject execute(RuntimeObject thisReference, RuntimeObject args, Scope scope) throws CompilerException
	public Reference execute(RuntimeObject thisReference, RuntimeObject args, Scope scope) throws CompilerException {
		
		assert scope.getParent() == null || args == null || args.type.conformsTo(scope.getParent().getType());
		
		started = true;
		
		out("Executing "+scope.getType()+" with args "+args+", containing "+scope.getExprs().size()+" expression statements");
		
		indentation++;

		//Stack<Pointer> localStack = new Stack<>();
		Stack<Reference> localStack = new Stack<>();
		currentLocalStack = localStack;
		
		//thisStack.push(thisObject);
		Reference oldThisRef = currentThisRef;
		
		if (args == null) // FIXME is it really ever null?
			args = currentThisRef.access();
		
		currentThisRef = new Reference(new RuntimeObject(scope.getType(), args, false), RefKind.REF);
		
		
		for (Expression expr : scope.getExprs()) {
			//RuntimeObject res = expr.evaluate(thisObject);
			Reference res = expr.evaluate(currentThisRef.access());
			currentLocalStack = localStack; // because it may have changed when expr evaluation calls a new function
			out("Expression  \""+expr+"\"  produced value: "+res);
			//res.destruct(); // this only destructs the pointer!!!
			//((Pointer)res).access().destruct();
			//localStack.push((Pointer)res);
			localStack.push(res);
		}
		Reference retObj;
		
		if (scope.hasReturnStatement()) {
			//thisObject.destruct();
			currentThisRef.destroy();
			retObj = null; // TODO
		} else {
			//retObj = new Reference(thisObject, RefType.VAL);
			retObj = new Reference(currentThisRef.access(), RefKind.VAL);
		}
		
//		for (Pointer p: localStack)
//			p.access().destruct();
		for (Reference ref: localStack)
			ref.destroy();
		
		//args.destruct(); // TODO: really here the place to do it?
		//args.destructAsAnArg(); // cf: a "val" in an arg must not be destroyed! // TODO: really here the place to do it?
		
		//thisObject = thisStack.pop();
		currentThisRef = oldThisRef;
		indentation--;
		
		return retObj;
	}
	
	
	public void destroyStaticConstants() {
		
		/*
		for (@SuppressWarnings("rawtypes") PrimitiveType pt: PrimitiveType.allPrimitiveTypes)
			pt.destroy();
		*/
		//for (Type t: Signature.formalParamTypes)
		for (Type t: allAnonTypes)
			t.destroy();
		
		//standardScopeRO.destruct(); // already destructed in call to execute as it is used as a this AND as an arg // <- NO MORE TRUE
		standardScopeRO.destruct();
		standardScope.destroy();
		voidObj.destruct();
	}
	
	
	
	public boolean hasStarted() {
		return started;
	}

	public Reference getThis() {
		//return thisObject;
		return currentThisRef;
	}
	
	//static void message(String msg) {
	public void message(RuntimeObject msg) {
		//out("<<<< Execution Message: \""+msg+"\" >>>>");
		
		
//		out("<<<< Execution Message: "+msg+" >>>>");
//		output.add(msg.toOutput());
		
		
		assert msg.getRuntimeType().getAttributeTypes().length == 1; // only one arg
		out("<<<< Execution Message: "+msg.read(0)+", from "+msg+" >>>>");
		output.add(msg.read(0).toOutput());
		
		
		//System.out.println("!!! Adding "+msg);
	}
	
	void out(String string) {
		if (indentation >= 0)
			out.print(indentation+":");
		if (indentation == 0)
			out.print(" ");
		for (int i = 0; i < indentation; i++)
			out.print("\t");
		out.println(string);
	}

	/*public static RuntimeObject getVoidobj() {
		return voidObj;
	}*/

	public PrintStream getOut() {
		return out;
	}
	
	public List<String> getOutput() {
		return output;
	}

	public PrimitiveRuntimeObject<Void> getVoidobj() {
		return voidObj;
	}
	
	public TupleExpression getEmptyExpr(Type parentType) {
		try {
			return new TupleExpression(this, parentType);
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}

	public Interpreter getInterpreter() {
		return interp;
	}
	/*
	public RuntimeObject getCurrentArgObject() {
		return currentArgObject;
	}
	*/
	
	public static boolean hasTypeType() {
		return getInstance().TypeType != null;
	}
	
	public static Execution getInstance() {
		return instance;
	}

	public void stackLocal(Reference ref) {
		currentLocalStack.push(ref);
	}
	
}











