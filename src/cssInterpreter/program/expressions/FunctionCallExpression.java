package cssInterpreter.program.expressions;

import util.Pair;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.UnknownFunctionCompExc;
import cssInterpreter.program.CallSignature;
import cssInterpreter.program.CandidateList;
import cssInterpreter.program.ParamBinding;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

public class FunctionCallExpression extends Expression {
	Expression thisExpression;
	String fieldName;
	TupleExpression args;
	//RuntimeObject thisObj;
	Type thisType = null;
	boolean searchRecursively;
	Execution exec;
	
	/*
	public FunctionCallExpression(Expression thisExpression, String fieldName, TupleExpression args) {
		this.thisExpression = thisExpression;
		this.fieldName = fieldName;
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
		thisObj = null;
	}*/
	
	private FunctionCallExpression(Execution exec, String fieldName, TupleExpression args) {
		this.fieldName = fieldName;
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
		this.exec = exec;
	}

	public FunctionCallExpression(Execution exec, Expression thisExpression, String fieldName, TupleExpression args) {
		this(exec, fieldName, args);
		this.thisExpression = thisExpression;
		searchRecursively = false;
	}
	
	public FunctionCallExpression(Execution exec, Type implicitThisType, String fieldName, TupleExpression args) {
		this(exec, fieldName, args);
		this.thisType = implicitThisType;
		searchRecursively = true;
	}
	
	public FunctionCallExpression(Execution exec, Type implicitThisType, String str) {
		this(exec, implicitThisType, str, exec.getEmptyExpr(implicitThisType));
	}

	public void setArgs(TupleExpression args) {
		if (args == null)
			throw new IllegalArgumentException();
		this.args = args;
	}
	
	ParamBinding getFunction(CandidateList candidates) throws CompilerException {
		//CandidateList candidates = new CandidateList();
		if (searchRecursively) {
			//Expression expr = 
			//RuntimeObject
			
			/*RuntimeObject execArgs = exec.getCurrentArgObject();
			if (args != null)
				try {
					return execArgs.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
				} catch(UnknownFunctionCompExc e) {}*/
			
			while (thisType != null) {
				try {
					//System.out.println("+++"+args);
					//return thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					
					///Function fct = thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					ParamBinding pb = thisType.getFunction(new CallSignature(fieldName, args), candidates);
					
					//System.out.println("\tFound "+fct.signature);
					//System.out.println(candidates);
					//return fct;
					return pb;
					
				} catch(UnknownFunctionCompExc e) {
					thisType = thisType.getParent();
					candidates.searchDepth++;
					//System.out.println(thisObj);
					if (thisType == null)
						throw e; // TODO: keep the most appropriate message (which parameters are missing?)
								 // TODO: aggregate all possible functions with this name?
				}
			}
			//System.out.println(thisObj);
			//throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this);
			throw new AssertionError("Never gets there");
			//assert false: "Never gets there";
		} else
			return evalThisType().getFunction(new CallSignature(fieldName, args), candidates);
	}

	/*private RuntimeObject evalThis() {
		return thisObj == null ? thisExpression.evaluate() : thisObj;
	}*/
	
	private Type evalThisType() throws CompilerException {
		if (thisType == null)
			thisType = thisExpression.getTypeRef().getType();
		return thisType;
	}
	private RuntimeObject getThis() {
		return thisExpression == null ? exec.getThis() : thisExpression.evaluate();
	}
	
	@Override
	public TypeReference getTypeRef() throws CompilerException {
		//System.out.println(thisExpression.getType()+" "+fieldName);
		///System.out.println(".. "+thisExpression.getType().getFunction(new CallSignature(fieldName, args)));
		
		//return thisExpression.getType().getFunction(new CallSignature(fieldName, new ArrayList<Expression>())).getOutputType();
		//return thisExpression.getType().getFunction(new CallSignature(fieldName, args)).getOutputType();
		
		//return getFunction().getOutputType();
		//return getFunction().fct.getOutputType(); // FIXME?! Type -> TypeRef
		return getFunction(new CandidateList()).fct.getOutputType(); // FIXME?! Type -> TypeRef
		
	}
	
	public Pair<ParamBinding,RuntimeObject> getFunctionAndThisOrExecArgs() throws CompilerException {
		//RuntimeObject execArgs = exec.getCurrentArgObject();
		
		/*
		RuntimeObject execArgs = getThis().getAssociatedArgs();
		CandidateList candidates = new CandidateList();
		
		if (execArgs != null) System.out.println(">>>>> trying "+execArgs);
		
		if (execArgs != null)
			try {
				return new Pair<>(execArgs.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates), execArgs);
			} catch(UnknownFunctionCompExc e) {}
		return new Pair<>(getFunction(candidates), getThis());
		*/
		
		return new Pair<>(getFunction(new CandidateList()), getThis());
		
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
			
			/**return getFunction().evaluate(getThis(), args.evaluate());*/
			Pair<ParamBinding, RuntimeObject> pb_obj = getFunctionAndThisOrExecArgs();
			return pb_obj.getFirst().evaluate(pb_obj.getSecond(), args.evaluate());
			
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}

	@Override public boolean isAssignable() { return true; }
	@Override
	public RuntimeObject assignDelegate(RuntimeObject value) {
		try {
			//System.out.println("ASSIGN "+value);
			//getFunction().set(evalThis(), args.evaluate(), value);
			
			/**getFunction().set(getThis(), args.evaluate(), value);*/
			Pair<ParamBinding, RuntimeObject> pb_obj = getFunctionAndThisOrExecArgs();
			pb_obj.getFirst().set(pb_obj.getSecond(), args.evaluate(), value);
			return value;
			
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
