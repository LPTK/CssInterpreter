package cssInterpreter.program;

import java.util.Arrays;
import java.util.List;

import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class PrimitiveType<T> extends Type {
	Function constructor;
	
	public PrimitiveType(TypeIdentifier id, final Execution exec) {
		//super(id, false);
		super(id, null); // TODO: null or standardScope?
		final PrimitiveType<T> that = this;
		constructor = new Function(new Signature(id.name, new FormalParameters(new NamedType[]{new NamedType(this,null,true)}))) {
			@Override public Type getOutputType() {
				return that;
			}
			@Override public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
				//return new RuntimeObjectBase(that, DumbInterpreter.standardScopeRO, false);
				return new PrimitiveRuntimeObject<T>(that, (T) params.getValue(), exec.standardScopeRO, false); // FIXME: perform checks..?
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
