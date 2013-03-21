package cssInterpreter.compiler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cssInterpreter.analysis.DepthFirstAdapter;
import cssInterpreter.node.AAccessNakedType;
import cssInterpreter.node.AAssignExpr;
import cssInterpreter.node.AAttrDeclStatement;
import cssInterpreter.node.AClassDeclStatement;
import cssInterpreter.node.AClosure;
import cssInterpreter.node.AClosureExpr;
import cssInterpreter.node.AComparisonExpr;
import cssInterpreter.node.AConditionalStatement;
import cssInterpreter.node.ADefDeclStatement;
import cssInterpreter.node.AExprStatement;
import cssInterpreter.node.AFalseExpr;
import cssInterpreter.node.AFieldAccess;
import cssInterpreter.node.AFieldAccessExpr;
import cssInterpreter.node.AIdExpr;
import cssInterpreter.node.AIdentGeneralId;
import cssInterpreter.node.AIdentNakedType;
import cssInterpreter.node.AIfCondType;
import cssInterpreter.node.AInvocationExpr;
import cssInterpreter.node.AListExpr;
import cssInterpreter.node.ANotExpr;
import cssInterpreter.node.ANumberExpr;
import cssInterpreter.node.AStringExpr;
import cssInterpreter.node.ATrueExpr;
import cssInterpreter.node.AType;
import cssInterpreter.node.ATypedValue;
import cssInterpreter.node.Node;
import cssInterpreter.node.PClosure;
import cssInterpreter.node.PExpr;
import cssInterpreter.node.PNakedType;
import cssInterpreter.node.Start;
import cssInterpreter.program.AssignExpression;
import cssInterpreter.program.Function;
import cssInterpreter.program.PrimitiveRuntimeObject;
import cssInterpreter.program.Scope;
import cssInterpreter.program.Signature;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeByName;
import cssInterpreter.program.TypeOf;
import cssInterpreter.program.TypeReference;
import cssInterpreter.program.expressions.ClosureExpression;
import cssInterpreter.program.expressions.ConstantExpression;
import cssInterpreter.program.expressions.Expression;
import cssInterpreter.program.expressions.FunctionCallExpression;
import cssInterpreter.program.expressions.IfExpression;
import cssInterpreter.program.expressions.NotExpression;
import cssInterpreter.program.expressions.TupleExpression;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

/*

TODO list:

handle refs and rvals
use typerefs
name scopes

*/

public class Interpreter extends DepthFirstAdapter {
	/*
	public final static PrimitiveType<Void> VoidType = new PrimitiveType<Void>(new TypeIdentifier("[Builtin Void]", null));
	public final static PrimitiveType<Integer> IntType = new PrimitiveType<Integer>(new TypeIdentifier("[Builtin Int]", null));
	public final static PrimitiveType<Long> LongType = new PrimitiveType<Long>(new TypeIdentifier("[Builtin Long]", null));
	public final static PrimitiveType<String> StringType = new PrimitiveType<String>(new TypeIdentifier("[Builtin String]", null));
	*/
	/**
	public final static PrimitiveType<Void> VoidType = new PrimitiveType<Void>(new TypeIdentifier("Void", null));
	public final static PrimitiveType<Integer> IntType = new PrimitiveType<Integer>(new TypeIdentifier("Int", null));
	public final static PrimitiveType<Long> LongType = new PrimitiveType<Long>(new TypeIdentifier("Long", null));
	public final static PrimitiveType<String> StringType = new PrimitiveType<String>(new TypeIdentifier("Str", null));
	*/
	
	//public final static Scope standardScope = new Scope("[StdScope]");
	////public Scope standardScope;// = new Scope("[StdScope]");
	
	
	//public final static RuntimeObject standardScopeRO = new RuntimeObjectBase(new TypeBase(new TypeIdentifier("StdType", null), false) {}, null, true) {};
	////public final RuntimeObject standardScopeRO = new RuntimeObject(standardScope.getType(), null, false) {};
	//public final static TupleExpression emptyExpr = new TupleExpression();
	
	//public static TupleExpression getEmptyExpr() {
	/*public TupleExpression getEmptyExpr(Type parentType) {
		try {
			return new TupleExpression(exec, parentType);
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}*/
	
	int indentation = 0;
	Scope currentScope;
	Execution exec;
	Map<PExpr,Expression> exprs = new HashMap<>();
	Map<Node,Expression> otherExprs = new HashMap<>(); // because "fieldAccess" is not an expression...
	//Map<PExpr,Scope> scopes = new HashMap<>();
	Map<PClosure,Scope> scopes = new HashMap<>();
	boolean finishedReading = false;
	private int currentTypeInferenceId;

	/*private void init() {
		standardScope = new Scope("[StdScope]", exec);
	}*/
	
	public Interpreter() {
		exec = new Execution(this, System.out);
		//init();
	}
	
	public Interpreter(String source, PrintStream out) {
		exec = new Execution(this, out);
		//init();
		out("############# SOURCE CODE #############");
		out(source);
	}
	
	private void out(String string) {
		for (int i = 0; i < indentation; i++)
			exec.getOut().print("\t");
		exec.getOut().println(string);
	}
	

	/*** DECLARATION ***/

	@Override
    public void inStart(Start node)
    {
		out("\n############# PROGRAM CONSTRUCTION #############");
    	//AClosure module = (AClosure) node.getPClosure();
    	//currentScope = new Scope();
		//currentScope = null;
		currentScope = exec.standardScope;
		
    	/// TODO: define standard lib in a closure

		out("\n############# STATIC ANALYSIS #############");
		
		currentScope.resolveTypes();
		
    }
	
	@Override
    public void outStart(Start node)
    {
		//Execution.output.clear();
    	// Execute the program
		finishedReading = true;
		out("\n############# PROGRAM EXECUTION #############");
		/*for (Expression expr : currentScope.exprs) {
			System.out.println("Expression "+expr+" produced: "+expr.evaluate());
		}*/
		try {
			exec.execute(exec.standardScopeRO, currentScope);
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
		out("\n############# PROGRAM OUTPUT #############");
		for (String str : exec.getOutput())
			exec.getOut().println(str);
    }
	
	
	public boolean hasFinishedReading() {
		return finishedReading;
	}
	
	
	
	private int nbClos = 0;
	@Override
    public void inAClosure(AClosure node)
    {
		/*if (currentScope == null)
			 currentScope = standardScope;
		else currentScope = new Scope("[AnonClosure "+nbClos+"]", currentScope);*/
		
    	//currentScope = new Scope("[AnonClosure "+nbClos+"]", currentScope);
		String scopeName = nbClos == 0? "[Main]": "[AnonClosure "+nbClos+"]";
		currentScope = new Scope(scopeName, currentScope);
		
    	scopes.put(node, currentScope);
		nbClos++;
    }

	@Override
    public void outAClosure(AClosure node)
    {
    	//currentScope = currentScope.parent;
		//if (currentScope.parent != null)
		currentScope.getType().generateConstructor(exec);
		//exprs.put(node, new ClosureExpression(currentScope));
		///otherExprs.put(node, new ClosureExpression(currentScope));
		
		if (currentScope.getParent() != exec.standardScope)
			currentScope = currentScope.getParent();
		
    }
	
	@Override
    public void outAClosureExpr(AClosureExpr node)
    {
		exprs.put(node, new ClosureExpression(currentScope));
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
		otherExprs.put(fa, new FunctionCallExpression(exec, expr, fa.getIdent().getText(), exec.getEmptyExpr(currentScope.getType())));
    }

	@Override
    public void outAFieldAccessExpr(AFieldAccessExpr node)
    {
		exprs.put(node, otherExprs.get(node.getFieldAccess()));
    }
	

	@Override
    public void outAConditionalStatement(AConditionalStatement node)
    {
        if (node.getConditionType() instanceof AIfCondType) {
        	currentScope.addExpr(new IfExpression(exec, exprs.get(node.getConditionExpr()), exprs.get(node.getStatementExpr())));
        } else {
        	throw new NotSupportedException();
        }
    }
	
	
	
	TypeReference determineType(ATypedValue tval) throws CompilerException {
		//AType t;
		if (tval.getType() == null) { // Determine type using expression
			
			if (tval.getValue() == null)
				throw new CompilerException("Cannot infer type from a null value");
			

			//out("Expr: "+tval.getValue().hashCode());
			//out("Expr: "+tval.getValue());
			
			Expression expr = exprs.get(tval.getValue());
			//return expr.getType();
			return new TypeOf(expr, this);
			
		} else { // Type is stated explicitly
			
			//t = (AType) tval.getType();
			return determineType((AType) tval.getType());
			
		}
	}
	
	private TypeReference determineType(AType type) throws CompilerException {
		PNakedType t = type.getBase();
		
		//System.out.println("det type");
		
		// TODO: handle template specialization
		
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
			//return new TypeOf(new FieldAccessExpression(null, ((AIdentNakedType) t).getName().getText(), getEmptyExpr(currentScope.type)));
			
			
			
			/**return new TypeOf(new FunctionCallExpression(exec, currentScope.getType(), ((AIdentNakedType) t).getName().getText()), this);*/
			//return new TypeByName(currentScope, ((AIdentNakedType) t).getName().getText());
			//return new TypeByName2(currentScope, ((AIdentNakedType) t).getName().getText());
			return new TypeByName(currentScope, ((AIdentNakedType) t).getName().getText());
			
		} else if (t instanceof AAccessNakedType) {
			/*
			AFieldAccess fa = ((AFieldAccess)((AAccessNakedType) t).getFieldAccess());
			Expression expr = exprs.get(fa.getPrefixExpr());
			//new InvokeExpression(expr,fa.getIdent().getText());
			return new FieldAccessExpression(expr, fa.getIdent().getText()).getType();
			*/
			//return otherExprs.get(((AAccessNakedType) t).getFieldAccess()).getType();
			
			/*
			 * Shall we consider that "f: Mc" works and "f: A.Mc" doesn't? And that we whould write "f: {A.Mc}" ?
			 * It might be useful that the rule be the same than this of "f := ..."
			 * As "def f := A.print" is eqto "def f := {A.print}", we might be able to write "f: A.Mc"
			 * TODO: make a closure?
			 */
			return new TypeOf(otherExprs.get(((AAccessNakedType) t).getFieldAccess()), this);
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
			currentScope.getType().addAttribute(node.getAttrType(), node.getName(), determineType(tv));
			
			// TODONE: add expr
			
			//Expression val = tv.getValue() == null? getEmptyExpr(): exprs.get(tv.getValue());
			
			if (tv.getValue() != null) {
				Expression val = exprs.get(tv.getValue());
				//if (val == null)
				//	val = otherExprs.get(tv.getValue());
				assert val != null;
				currentScope.getExprs().add(new AssignExpression(new FunctionCallExpression(
							exec,
							currentScope.getType(),
							node.getName().getText()), val));
				
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
		
		//System.out.println("LEX>> "+node);
		
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
		
		Expression firstExpr = exprs.get(ls.get(0)); // firstExpr can be null in cases like "; a,b;" where we have [[a,b]]
		//if (firstExpr == null)
		//	firstExpr = otherExprs.get(ls.get(0));
		
		
		/***
		if (firstExpr == null) { // firstExpr can be null in cases like "; a,b;" where we have [[a,b]]
			assert ls.size() == 1;
			ls = ((AListExpr)ls.get(0)).getExpr(); // wrong; "a = 42, print 'ok'" is a call expr with "a = 42, print" and "ok"
			// ls.get(0) can be a closure
			
			
			
			
			
			
			
			
			// FIXME TODO
			
			
			
			
			
			
			
			
		}
		
		//if (ls.size() == 1 && firstExpr != null && !(firstExpr instanceof AssignExpression)) {		// wrong
		
		*/
		
		
		
		/*
		if (firstExpr instanceof AssignExpression) {
			curr
			return;
		}
		*/
		
		if (ls.size() == 1 && !(firstExpr instanceof AssignExpression)) {
			//System.out.println("OK");
			exprs.put(node, firstExpr);
		} else {
			Expression[] exs = new Expression[ls.size()];
			for (int i = 0; i < ls.size(); i++)
				exs[i] = exprs.get(ls.get(i));
			try {
				//System.out.println("-------->"+exs[0]);
				exprs.put(node, new TupleExpression(exec, exs, currentScope.getType(), null/*TODO*/));
			} catch (CompilerException e) {
				throw new ExecutionException(e);
			}
		}
		
    }
    
	
	/*
	@Override
    public void outABooleanExpr(ABooleanExpr node)
    {
		PBoolean bool = node.getBoolean();
        if (bool instanceof ATrueBoolean)
        	exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<Boolean>(exec.BoolType, true, exec.standardScopeRO, true)));
        else if (bool instanceof AFalseBoolean)
        	exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<Boolean>(exec.BoolType, false, exec.standardScopeRO, true)));
        else if (bool instanceof ANotBoolean)
        	exprs.put(node, new NotExpression(exprs.get(((ANotBoolean) bool).getExpr())));
        else {
        	throw new NotSupportedException();
        }
    }
	*/

	@Override public void outATrueExpr(ATrueExpr node)
    { exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<Boolean>(exec.BoolType, true, exec.standardScopeRO, true))); }

	@Override public void outAFalseExpr(AFalseExpr node)
    { exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<Boolean>(exec.BoolType, true, exec.standardScopeRO, true))); }

	@Override public void outANotExpr(ANotExpr node)
    { exprs.put(node, new NotExpression(exprs.get(node.getExpr()))); }

	@Override public void outAComparisonExpr(AComparisonExpr node)
	{ throw new NotSupportedException(); }
	
	
	@Override
	public void inANumberExpr(ANumberExpr node)
	{
		out("Number: "+node.getIntegerNumber().getText());
		//exprs.put(node, new Constant(new ConstantRuntimeObject<Long>(Long.parseLong(node.getIntegerNumber().getText()))));
		exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<Long>(exec.LongType, (Long)Long.parseLong(node.getIntegerNumber().getText()), exec.standardScopeRO, true)));
		//out("Number: "+node.hashCode());
	}

	@Override
	public void inAStringExpr(AStringExpr node)
	{
		out("String: "+node.getStringContent().getText());
		exprs.put(node, new ConstantExpression(new PrimitiveRuntimeObject<String>(exec.StringType, node.getStringContent().getText(), exec.standardScopeRO, true)));
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
		
		exprs.put(node, new FunctionCallExpression(exec, currentScope.getType(), str));
		
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
    	
    	/**
    	if (value instanceof TupleExpression) {
    		TupleExpression teVal = (TupleExpression) value;
    		if (teVal.isSingleExpr())
    			value = teVal.getSingleExpr();
    	}
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
		
		if (callee instanceof FunctionCallExpression) {
			
			
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
			//assert node.getArgs() == null || (node.getArgs() != null && exprs.get(node.getArgs()) != null);
			assert node.getArgs() == null || exprs.get(node.getArgs()) != null;
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
					
					if ((args instanceof TupleExpression)) {
						 tupleArgs = (TupleExpression) args;
						 args.getTypeRef().getType(Expression.getNewTypeInferenceId()).setName("[CallArgs "+args+"]");
					} else tupleArgs = new TupleExpression(exec, new Expression[]{args},currentScope.getType(),"[SingleCallArg "+args+"]");
					
					//System.out.println("*********"+tupleArgs.namedParams);
					
					//TupleExpression tupleArgs = (TupleExpression) exprs.get(node.getArgs());
					
					//if (tupleArgs == null) tupleArgs = getEmptyExpr();
					((FunctionCallExpression) callee).setArgs(tupleArgs);
					
					
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
			throw new NotSupportedException();
		}
		
		
    }

	@Override
    public void inAClassDeclStatement(AClassDeclStatement node)
    {
		out("Reading class: "+node.getName());
		indentation++;
    }
	@Override
    public void outAClassDeclStatement(AClassDeclStatement node)
    {
		PExpr initVal = ((ATypedValue)node.getTypedValue()).getValue();

		if (initVal instanceof AListExpr) {
			LinkedList<PExpr> ls = ((AListExpr) initVal).getExpr();
			if (ls.size() != 1)
				throw new ExecutionException(new CompilerException("Class was initialized with "+ls.size()+" expression(s) instead of 1"));
			initVal = ls.get(0);
		}
		
		if (!(initVal instanceof AClosureExpr))
			throw new ExecutionException(new CompilerException("Class was not initialized with a closure"));
		else {
			
			String className = node.getName().getText();
			
			final PClosure myClosure = ((AClosureExpr)initVal).getClosure();
			
			//scopes.get(myClosure).setName("["+className+"]");
			
			Scope myScope = scopes.get(myClosure); // The main scope defined by the class
			
			// TODO: here we only treat the case where there is no explicit constructor defined; (this should actually be treated in tupleType's ctor generation)
			
			myScope.setName(className);
			
			currentScope.addType(myScope.getType());
			
			
		}
		
		indentation--;
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
			
			String fctName = node.getName().getText();
			
			//final Scope subScope = scopes.get(((AClosureExpr)v).getClosure());
			final PClosure myClosure = ((AClosureExpr)initVal).getClosure();
			//System.out.println(subScope);
			
			scopes.get(myClosure).setName("["+fctName+"]");
			//System.out.println("----------> setting name of "+fctName+": "+scopes.get(myClosure).getType());
			
			
			
			
			
			/*
			FormalParameters fparams;
			//fparams = new FormalParameters(new NamedType[] { });
			
			if (paramClosure == null)
				 fparams = new FormalParameters();
			else {
				assert scopes.get(paramClosure).getType().getConstructors().size() == 1;
				fparams = scopes.get(paramClosure).getType().getConstructors().get(0).getSignature().params;
				scopes.get(paramClosure).setName("[ParamsOf "+fctName+"]");
			}
			*/
			/*
			Type paramType;
			if (paramClosure == null)
				paramType = new TupleType(new TypeIdentifier("[EmptyParam]", currentScope.getType()), currentScope.getType());
			else {
				paramType = scopes.get(paramClosure).getType();
				scopes.get(paramClosure).setName("[ParamsOf "+fctName+"]");
			}*/
			
			Scope paramScope;
			if (paramClosure == null) {
				paramScope = new Scope("[EmptyParamOf "+fctName+"]", currentScope);
				paramScope.getType().generateConstructor(exec);
			} else {
				paramScope = scopes.get(paramClosure);
				paramScope.setName("[ParamOf "+fctName+"]");
			}
			
			scopes.get(myClosure).setParent(paramScope);
			
			
			assert paramScope.getType().getConstructors().size() == 1;
			//FormalParameters fparams = scopes.get(paramClosure).getType().getConstructors().get(0).getSignature().params;
			

			//currentScope.getType().addFct(new Function(new Signature(fctName, fparams)) {
			//currentScope.getType().addFct(new Function(new Signature(fctName, scopes.get(paramClosure).getType())) {
			currentScope.getType().addFct(new Function(new Signature(fctName, paramScope.getType())) {
				
				//final Scope scope = currentScope;
				//final PClosure myClosure = ((AClosureExpr)v).getClosure());
				
				@Override
				public Type getOutputType() {
					//return exec.VoidType; // FIXME
					return scopes.get(myClosure).getType();
				}
				
				@Override
				public RuntimeObject evaluateDelegate(RuntimeObject thisReference, RuntimeObject args) throws CompilerException {
					//System.out.println(scopes.get(myClosure));
					
					/**
					//Execution.execute(subScope);
					exec.execute(args, scopes.get(myClosure));
					//return Execution.getVoidobj();//new PrimitiveRuntimeObject<Void>(VoidType, new Void(), true); // TODO
					return exec.getVoidobj();//new PrimitiveRuntimeObject<Void>(VoidType, new Void(), true); // TODO
					*/
					
					
					return exec.execute(args, scopes.get(myClosure));
					
					
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
		
		//currentScope.addExpr(exprs.get(node.getExpr()));
		
		Expression expr = exprs.get(node.getExpr());
		if (expr instanceof TupleExpression) {
			for (Expression ex : ((TupleExpression) expr).getRawExprs()) {
				currentScope.addExpr(ex);
			}
		}
		else currentScope.addExpr(expr);
		indentation--;
	}
	
	public int getCurrentTypeInferenceId() {
		return currentTypeInferenceId;
	}
	public void newTypeInferenceId() {
		currentTypeInferenceId++;
	}
	
	
	//@Override
	//public void inA
	
	
	
	
	
}

