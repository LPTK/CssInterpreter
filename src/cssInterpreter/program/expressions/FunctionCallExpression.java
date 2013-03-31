package cssInterpreter.program.expressions;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.UnknownFunctionException;
import cssInterpreter.program.CallSignature;
import cssInterpreter.program.CandidateList;
import cssInterpreter.program.ParamBinding;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeReference;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.RuntimeObject;

public class FunctionCallExpression extends Expression {
	Expression thisExpression;
	String fieldName;
	TupleExpression args;
	//RuntimeObject thisObj;
	Type thisType = null;
	boolean searchRecursively;
	Execution exec;
	TypeReference outputType;
	//RefKind outputKind;
	ParamBinding fctBinding;
	
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
			
			Type ttype = thisType;
			
			while (ttype != null) {
				try {
					//System.out.println("+++"+args);
					//return thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					
					///Function fct = thisObj.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates);
					ParamBinding pb = ttype.getFunction(new CallSignature(fieldName, args), candidates);
					
					//System.out.println("\tFound "+fct.signature);
					//System.out.println(candidates);
					//return fct;
					return pb;
					
				} catch(UnknownFunctionException e) {
					ttype = ttype.getParent();
					candidates.searchDepth++;
					//System.out.println(thisObj);
					if (ttype == null)
						throw e; // TODO: keep the most appropriate message (which parameters are missing?)
								 // TODO: aggregate all possible functions with this name?
				}
			}
			//System.out.println(thisObj);
			//throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this);
			throw new AssertionError("Never gets there");
			//assert false: "Never gets there";
		} else
			return getThisType().getFunction(new CallSignature(fieldName, args), candidates); // FIXME -1 as currentTypeInferenceId?
	}

	/*private RuntimeObject evalThis() {
		return thisObj == null ? thisExpression.evaluate() : thisObj;
	}*/
	
	private Type getThisType() throws CompilerException {
		if (thisType == null)
			thisType = thisExpression.getTypeRef().getType();
		return thisType;
	}
	private Reference getThis() throws CompilerException {
		return thisExpression == null ? exec.getThis() : thisExpression.evaluate(exec.getThis().access());
	}
	
//	@Override
//	public TypeReference getTypeRefDelegate(int currentTypeInferenceId) throws CompilerException {
//		//System.out.println(thisExpression.getType()+" "+fieldName);
//		///System.out.println(".. "+thisExpression.getType().getFunction(new CallSignature(fieldName, args)));
//		
//		//return thisExpression.getType().getFunction(new CallSignature(fieldName, new ArrayList<Expression>())).getOutputType();
//		//return thisExpression.getType().getFunction(new CallSignature(fieldName, args)).getOutputType();
//		
//		//return getFunction().getOutputType();
//		//return getFunction().fct.getOutputType(); // FIXME?! Type -> TypeRef
//		
//		//return getFunction(new CandidateList()).fct.getOutputType(); // FIXME?! Type -> TypeRef
//		return getFunction(new CandidateList(evalThisType(currentTypeInferenceId))).fct.getOutputType(); // FIXME?! Type -> TypeRef
//
//		//Function f = getFunction(new CandidateList()).fct;
//		//return f.getOutputType(); // FIXME?! Type -> TypeRef
//		
//	}
	
	@Override
	public TypeReference getTypeRef() {
		//return getFunction(new CandidateList(evalThisType())).fct.getOutputType(); // FIXME?! Type -> TypeRef
		return outputType;
	}
	/*
	@Override
	public RefKind getRetKind() {
		return outputKind;
	}*/
	
	@Override
	public void resolveTypes(int currentTypeInferenceId) throws CompilerException {
		if (thisExpression != null) {
			thisExpression.resolveTypes(currentTypeInferenceId);
			thisExpression.getTypeRef().resolve(currentTypeInferenceId);
		}
		args.resolveTypes(currentTypeInferenceId);
		//Function fct = getFunction(new CandidateList(getThisType())).fct;
		fctBinding = getFunction(new CandidateList(getThisType()));
		//outputType = fct.getOutputType().resolve(currentTypeInferenceId);
		//outputKind = fct.getRetKind();
		//outputType = fct.getOutputType();
		outputType = fctBinding.fct.getOutputType().withKind(fctBinding.fct.getRetKind());
		outputType.resolve(currentTypeInferenceId);
	}
	
	
//	public Pair<ParamBinding,RuntimeObject> getFunctionAndThisOrExecArgs() throws CompilerException {
//		//RuntimeObject execArgs = exec.getCurrentArgObject();
//		
//		/*
//		RuntimeObject execArgs = getThis().getAssociatedArgs();
//		CandidateList candidates = new CandidateList();
//		
//		if (execArgs != null) System.out.println(">>>>> trying "+execArgs);
//		
//		if (execArgs != null)
//			try {
//				return new Pair<>(execArgs.getRuntimeType().getFunction(new CallSignature(fieldName, args), candidates), execArgs);
//			} catch(UnknownFunctionCompExc e) {}
//		return new Pair<>(getFunction(candidates), getThis());
//		*/
//		
//		//System.out.println(getThis());
//		//RuntimeObject th = getThis();
//		
//		//return new Pair<>(getFunction(new CandidateList()), getThis());
//		return new Pair<>(getFunction(new CandidateList(evalThisType())), getThis());
//		
//	}
	
	@Override
	public Reference evaluate(RuntimeObject parentOfThis) {
		try {
			//System.out.println(">>> "+thisExpression.getType().getFunction(new CallSignature(fieldName, args)));
			//System.out.println(">>> "+thisExpression+" "+(thisExpression == null ? "" : thisExpression.evaluate()));
			//System.out.println(">>> "+args+" "+args.evaluate());
			
			//Expression expr = thisExpression;
			
			//return thisExpression.getType().getFunction(new CallSignature(fieldName, args)).evaluate(thisExpression.evaluate(), args.evaluate());
			//return getFunction().evaluate(thisObj == null ? thisExpression.evaluate() : thisObj, args.evaluate());
			
			/**return getFunction().evaluate(getThis(), args.evaluate());*/
			/*
			Pair<ParamBinding, RuntimeObject> pb_obj = getFunctionAndThisOrExecArgs();
			return pb_obj.getFirst().evaluate(pb_obj.getSecond(), args.evaluate());*/
			Reference argsRef = args.evaluate(exec.getThis().access());
			argsRef.access().setIsAnArg(true);
			Execution.getInstance().stackLocal(argsRef);
			//return getFunction(new CandidateList(getThisType())).evaluate(getThis().access(), argsRef.access()); // TODONE: cache fct resolution
			return fctBinding.evaluate(getThis().access(), argsRef.access());
			
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}

	@Override public boolean isAssignable() { return true; }
	@Override
	public RuntimeObject assignDelegate(Reference valueRef) {
		try {
			//System.out.println("ASSIGN "+value);
			//getFunction().set(evalThis(), args.evaluate(), value);
			
			/**getFunction().set(getThis(), args.evaluate(), value);*/
			/*
			Pair<ParamBinding, RuntimeObject> pb_obj = getFunctionAndThisOrExecArgs();
			pb_obj.getFirst().set(pb_obj.getSecond(), args.evaluate(), value);
			return value;*/
			Reference argsRef = args.evaluate(exec.getThis().access());
			argsRef.access().setIsAnArg(true);
			Execution.getInstance().stackLocal(argsRef);
			//getFunction(new CandidateList(getThisType())).set(getThis().access(), argsRef.access(), valueRef);
			fctBinding.set(getThis().access(), argsRef.access(), valueRef);
			return valueRef.access();
			
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}
	public TypeReference getSetTypeRef() {
		return fctBinding.fct.getOutputType().withKind(fctBinding.fct.getSetKind());
	}
	
	
	public String toString() {
		/*if (thisExpression == null)
			return "("+thisObj+")";
		else
			return "("+thisExpression+")";*/
		//String ret = "("+(thisExpression == null ? (thisObj == null ? : ) : thisExpression)+").";
		//String ret = (thisExpression == null ? "[CurrentScope]." : "("+ thisExpression) + ").";
		//String ret = (thisExpression == null ? "." : "("+ thisExpression + ").");
		/**String ret = (thisExpression == null ? "" : "("+ thisExpression + ").");
		ret += fieldName;
		//ret += "("+args+")";
		ret += args;
		return ret;*/

		return
				(thisExpression == null ? "" : "("+ thisExpression + ").")
				+ fieldName
				+ args;
	}
}














