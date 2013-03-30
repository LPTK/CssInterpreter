package cssInterpreter.program;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public 
class TupleType extends Type {
	Function constructor;
	
	/*
	 * Invariant:
	 * 0..p unnamed attributes, THEN 0..q named attributes
	 */

	public TupleType(TypeIdentifier id, Type parent) {
		super(id, parent, true);
	}
	public TupleType(TypeIdentifier id, Type parent, boolean hasCopyFunction) {
		//super(id, false);
		super(id, parent, hasCopyFunction);
	}
	
	@Override public boolean isTuple() { return true; }
	
	public void generateConstructor(final Execution exec) {
		
		/**
		NamedType[] nts = new NamedType[attributeTypes.size()];
		for (int i = 0; i < attributeTypes.size(); i++) {
			nts[i] = new NamedType(attributeTypes.get(i), attributeNames.get(i), false); // FIXME: use provided default values!
		}*/
		
		final Type that = this;
		
		//constructor = new Function(new Signature(id.name, new FormalParameters(nts))) {
		//constructor = new Function(new Signature(this, new FormalParameters(nts))) {
		constructor = new Function(new Signature(null, this), RefKind.VAL) {
			
			@Override public Type getOutputType() {
				return that;
			}
			
			@Override public Reference evaluateDelegate(RuntimeObject thisReference, RuntimeObject args) {
				/*if (!params.getRuntimeType().conformsTo(signature))
					throw new IllegalArgumentException();
				return new RuntimeObject(that, params, DumbInterpreter.standardScopeRO, false);*/
				
				
				
				
				
				/* TODO
				
				if (!args.getRuntimeType().getBinding(this).isSuccessful())
					throw new IllegalArgumentException();
				//return new RuntimeObject(that, args, exec.standardScopeRO, false);
				//return new RuntimeObject(that, args, exec.standardScopeRO, false);
				
				
				*/
				
				
				//return new Reference(args, RefKind.VAL); // TODONE: is it really THIS simple? <- NOPE
				
				args.setIsAnArg(true);
				return args.copy(true);
				
				
				
				
				
				
				
				//throw new NotSupportedException();
				
				
				
				
				
			}
		};
		
	}
	
	public List<Function> getConstructors() { // FIXME: correct?
		//return new ArrayList<Function>();
		if (constructor == null) return new ArrayList<>();
		return Arrays.asList(new Function[]{constructor});
	}
	
	/*
	public void setName(String newName) {
		// TODO Auto-generated method stub
		
	}*/
	
}
