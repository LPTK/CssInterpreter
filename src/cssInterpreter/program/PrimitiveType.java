package cssInterpreter.program;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public class PrimitiveType<T> extends Type {
	
	@SuppressWarnings("rawtypes")
	public static List<PrimitiveType> allPrimitiveTypes = new ArrayList<PrimitiveType>();
	
	Function constructor;
	
	public PrimitiveType(TypeIdentifier id, final Execution exec) {
		//super(id, false);
		super(id, null); // TODO: null or standardScope?
		isAClass = true;
		final PrimitiveType<T> that = this;
		constructor = new Function(new Signature(id.name, new FormalParameters(new NamedType[]{new NamedType(this,null,true)}))) {
			@Override public Type getOutputType() {
				return that;
			}
			@Override public RuntimeObject evaluateDelegate(RuntimeObject thisReference, RuntimeObject params) {
				//return new RuntimeObjectBase(that, DumbInterpreter.standardScopeRO, false);
				return new PrimitiveRuntimeObject<T>(that, (T) params.getValue(), exec.standardScopeRO, false); // FIXME: perform checks..?
			}
		};
		allPrimitiveTypes.add(this);
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
	
	// should only be used to set the type of TypeType
	public void setType(PrimitiveType<Type> typeType) {
		//type = typeType;
		getObjectRepresentation().setType(typeType);
	}
	
}


