package cssInterpreter.program.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.Pair;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.program.AssignExpression;
import cssInterpreter.program.TupleType;
import cssInterpreter.program.Type;
import cssInterpreter.program.TypeIdentifier;
import cssInterpreter.program.TypeOf;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

public class TupleExpression extends Expression {
	Type type;
	//Expression[] exprs;
	Expression[] rawEprs;
	
	List<Expression> ordinalArgs = new ArrayList<>();
	//Map<String,Expression> namedParams = new HashMap<>();
	Map<String,Pair<Integer,Expression>> namedArgs = new HashMap<>();
	Execution exec;
	
	/*
	 * Invariant: p ordinals followed by k named, NOT MIXED
	 */
	
	public TupleExpression(Execution exec, Type parentType) throws CompilerException {
		////type = DumbInterpreter.VoidType;
		//exprs = new Expression[0];
		
		this(exec, new Expression[0], parentType, null);
		
	}
	
	public TupleExpression(Execution exec, Expression[] exs, Type parentType, String name) throws CompilerException {
		
		// TODO: add line detection
		
		//this.exprs = exs;
		this.exec = exec;
		
		this.rawEprs = exs;
		
		//TupleType tt = new TupleType(new TypeIdentifier("[AnonTuple]", DumbInterpreter.standardScope.type)); //FIXME?
		TupleType tt = new TupleType(new TypeIdentifier((name==null?"[AnonTuple]":name), parentType), parentType); //FIXME?
		
		///System.out.println(">>>>>>>>>>>");
		
		boolean namedParamsBegan = false;
		
		for (int i = 0; i < exs.length; i++) {
			
			Expression e = exs[i];
			assert e != null;
			
			///System.out.println(":t:"+e.getClass());
			
			if (e instanceof AssignExpression) {
				AssignExpression ae = ((AssignExpression) e);
				namedParamsBegan = true;
				FunctionCallExpression fa = (FunctionCallExpression) ae.getAssigned();
				
				//System.out.println(((TupleExpression)fa.thisExpression));
				
				assert ((TupleExpression)fa.thisExpression) == null
				//		|| (((TupleExpression)fa.thisExpression).getTypeRef()).isEmpty(); // laid comme ta mère
						|| (((TupleExpression)fa.thisExpression).getTypeRef()).getType(-1).isEmpty(); // laid comme ta mère
				//assert ae.assigned instanceof 
				
				//System.out.println(":n:"+fa.fieldName);
				///System.out.println(":n: "+fa.fieldName+" = "+ae.value);
				//System.out.println(":n:"+fa.fieldName+" - "+fa.args);
				
				//namedParams.put(fa.fieldName, fa.args);
				//namedParams.put(fa.fieldName, ae.value);
				namedArgs.put(fa.fieldName, new Pair<>(i, ae.getValue()));
				tt.addAttribute(null, fa.fieldName, new TypeOf(ae.getValue(), exec.getInterpreter()));
				
			} else {
				
				if (namedParamsBegan)
					throw new CompilerException("Invalid sequence of arguments: named arguments must follow ordinal arguments");
				ordinalArgs.add(e);
				tt.addAttribute(null, (String) null, new TypeOf(e, exec.getInterpreter()));

				///System.out.println(":o:"+e+" "+new TypeOf(e));
				
			}
		}
		
		///System.out.println("<<<<<<<<<");
		
		tt.generateConstructor(exec);
		type = tt;
		
		
		//this.type = type;
		
		
		
		
		
		
	}
	/*
	public TupleExpression(Expression args) {
		
	}*/
	
	public boolean isSingleExpr() {
		return type.getAttributeTypes().length == 1;
	}
	public Expression getSingleExpr() {
		assert isSingleExpr();
		return ordinalArgs.get(0);
	}
	
	public Expression[] getRawExprs() {
		return rawEprs;
	}
	
	
	@Override
	public Type getTypeRefDelegate(int currentTypeInferenceId) throws CompilerException {
		// TODO
		//throw new NotSupportedCompExc();
		//if (type.isEmpty())
		//return DumbInterpreter.VoidType;
		return type;
	}
	@Override
	public RuntimeObject evaluate() throws CompilerException {
		//throw new NotSupportedCompExc();
		//if (type.isEmpty())
		//return Execution.voidObj;
		/*if (exprs.length == 0)
			 return Execution.voidObj;*/
		
		
//		if (type == DumbInterpreter.VoidType) // WARNING: object address equality here
//			return Execution.voidObj;
//		else {
			
			
			RuntimeObject ret = new RuntimeObject(type, exec.getThis()/*FIXME?*/, false);
			//evals = new RUnt;
			/*for (int i = 0; i < exprs.length; i++) {
				ret.write(i, exprs[i].evaluate());
			}*/
			int i;
			for (i = 0; i < ordinalArgs.size(); i++)
				ret.write(i, ordinalArgs.get(i).evaluate());
			/*
			Iterator<Expression> ite = namedParams.values().iterator(); // FIXME: in right order?
			while(ite.hasNext())
				ret.write(i++, ite.next().evaluate());
			*/
			Iterator<Pair<Integer,Expression>> ite = namedArgs.values().iterator(); // FIXEDME: in right order?
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
		if (type == exec.VoidType) // WARNING: object address equality here
			 //return "[Tpl]()";
			return "()";
		else {
			StringBuffer sb = new StringBuffer();
			//sb.append("[Tpl](");
			sb.append("(");
			
			for (Expression e : ordinalArgs)
				//sb.append(e+", ");
				//sb.append(e+":"+e.getTypeRef()+", ");
				sb.append(":"+e.getTypeRef()+"="+e+", ");
			
			//for (Expression e : namedParams.values())
			
			//for (Map.Entry<String,Expression> e : namedParams.entrySet()) System.out.println(e+" "+e.getValue().getClass());
			
//			for (Map.Entry<String,Expression> e : namedParams.entrySet())
//				sb.append(e.getKey()+":"+e.getValue().getType()+"="+e.getValue()+", ");
			for (Map.Entry<String,Pair<Integer,Expression>> e : namedArgs.entrySet())
				sb.append(e.getKey()+":"+e.getValue().getSecond().getTypeRef()+"="+e.getValue().getSecond()+", ");
			
			if (ordinalArgs.size() != 0 || namedArgs.size() != 0)
				sb.delete(sb.length()-2, sb.length());
			sb.append(")");
			return sb.toString(); // TODONE
		}
		} catch (CompilerException e) {
			throw new ExecutionException(e);
		}
	}
}








