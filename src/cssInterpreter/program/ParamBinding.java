package cssInterpreter.program;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.Pair;
import cssInterpreter.compiler.CompilerException;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public class ParamBinding {
	//final boolean successful;
	//public final String bindingError;
	
	//String bindingError = "Unknown";
	String bindingError = null;

	//List<Pair<Integer,String>> names = new ArrayList<>();
	List<Pair<Integer,Integer>> bindings = new ArrayList<>();
	//private Function fct;
	public final Function fct;
	private int searchDepth;

	private Set<Integer> pointerDereferences = new HashSet<>();
	
	/*public ParamBinding(String bindingError) {
		this.bindingError = bindingError;
	}*/
	public ParamBinding(Function fct, int searchDepth) {
		this.fct = fct;
		this.searchDepth = searchDepth;
	}
	/*
	private void mutate(RuntimeObject args) { // FIXME: duplicate type before?? this code adds reeaaally nasty states

		//System.out.println(args);
		
		for (Pair<Integer,Integer> p : names)
			args.renameAttribute(p.getFirst(), p.getSecond());
		
		//System.out.println(args);
	}
	*/
	
	// reaches up the right type through parent hierarchy
	private RuntimeObject recurseBack(RuntimeObject args) {
		for (int i = 0; i < searchDepth; i++)
			args = args.getParent();
		return args;
	}
	
	private RuntimeObject getConformingArgs(RuntimeObject args) {
		Type argsType = fct.getInputType();
		RuntimeObject conformingArgs = new RuntimeObject(argsType, args.getParent(), false);
		for (Pair<Integer,Integer> p : bindings) {
			
			RuntimeObject arg = args.read(p.getFirst());
			
			if (pointerDereferences.contains(p.getFirst()))
				arg = ((Pointer)arg).access();
			
			if (argsType.getAttributeTypes()[p.getSecond()].getKind() == RefKind.REF
					&& args.getRuntimeType().getAttributeTypes()[p.getFirst()].getKind() != RefKind.REF)
				Execution.getInstance().stackLocal(arg.getValRef());
			
			conformingArgs.write(p.getSecond(), arg);
		}
		Execution.getInstance().stackLocal(conformingArgs.getValRef());
		return conformingArgs;
	}
	
	public void set(RuntimeObject thisReference, RuntimeObject args, Reference valueRef) throws CompilerException {
		assert isSuccessful();
		//args = recurseBack(args);
		thisReference = recurseBack(thisReference);
		
		args.setParent(recurseBack(args.getParent()));
		
		/**
		assert false: "not done yet";
		//mutate(args);
		fct.set(thisReference, args, value);*/
		
		//Execution.getInstance().stackLocal(args.getValRef());
		
		fct.set(thisReference, getConformingArgs(args), valueRef);
	}
	
	public Reference evaluate(RuntimeObject thisReference, RuntimeObject args) throws CompilerException {
		assert isSuccessful();
		//args = recurseBack(args);
		thisReference = recurseBack(thisReference);
		
		args.setParent(recurseBack(args.getParent()));
		
		/***
		/////mutate(args);
		return fct.evaluate(thisReference, args);
		*/
		/*
		RuntimeObject conformingArgs = new RuntimeObject(fct.getInputType(), args.getParent(), false);
		for (Pair<Integer,Integer> p : bindings) {
//			int index = -1; // TODONE Sale!! Mieux stocker le premier indice !!
//			for (int i = 0; i < conformingArgs.getRuntimeType().getAttributeCount(); i++)
//				if (conformingArgs.getRuntimeType().getAttributeNames()[i].equals(p.getSecond())) {
//					assert index==-1;
//					index = i;
//				}
//			System.out.println("Setting "+p.getSecond());
			conformingArgs.write(p.getSecond(), args.read(p.getFirst()));
		}
		return fct.evaluate(thisReference, conformingArgs);*/
		
		return fct.evaluate(thisReference, getConformingArgs(args));
		
	}
	
	/*
	public boolean hasName(String name) {
		for (Pair<Integer,String> p : names)
			if (p.getSecond().equals(name))
				return true;
		return false;
	}
	*/
	public boolean hasName(String name) {
		for (Pair<Integer,Integer> p : bindings)
			if (fct.getInputType().getAttributeNames()[p.getSecond()].equals(name))
				return true;
		return false;
	}

	/*public Function getFunction() {
		return fct;
	}*/
	/*public void addBinding(Integer index, String name) {
		names.add(new Pair<>(index,name));
	}*/
	
	public void addBinding(int argIndex, int paramIndex) {
		bindings.add(new Pair<>(argIndex,paramIndex));
	}

	public void addBinding(int argIndex, int paramIndex, boolean pointerDereference) {
		addBinding(argIndex, paramIndex);
		pointerDereferences.add(argIndex);
	}
	
	public void setSuccessful() {
		this.bindingError = null;
	}
	public void setUnsuccessful(String bindingError) {
		this.bindingError = bindingError;
	}
	public boolean isSuccessful() {
		//return successful;
		return bindingError == null;
	}
	public String getError() {
		return bindingError;
	}
	public String getMessage() {
		if (bindingError == null)
			 return "Conforming";
		else return bindingError;
	}
	
}



