package cssInterpreter.program;

import java.util.Arrays;
import java.util.List;

import cssInterpreter.test.DumbInterpreter;

public 
class TupleType extends Type {
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
	
	public void generateConstructor() {
		
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
