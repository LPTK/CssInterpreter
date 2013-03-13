package cssInterpreter.compiler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.PrimitiveType;

import cssInterpreter.analysis.DepthFirstAdapter;
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
import cssInterpreter.node.PClosure;
import cssInterpreter.node.PExpr;
import cssInterpreter.node.PNakedType;
import cssInterpreter.node.Start;
import cssInterpreter.program.Execution;
import cssInterpreter.program.TypeIdentifier;
import cssInterpreter.runtime.ExecutionException;

public class Interpreter extends DepthFirstAdapter {
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
					return Execution.voidObj;//new PrimitiveRuntimeObject<Void>(VoidType, new Void(), true); // TODO
					
					
					
					
					
					
					
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

