package cssInterpreter.program;

import cssInterpreter.runtime.ExecutionException;
import cssInterpreter.runtime.RuntimeObject;

public class PrimitiveRuntimeObject<T> extends RuntimeObject {
	T val;
	public PrimitiveRuntimeObject(PrimitiveType<T> type, T val, RuntimeObject parent, boolean constant) {
		super(type, parent, constant);
		this.val = val;
	}
	@Override
	public boolean isValue() {
		return true;
	}
	@Override
	public Object getValue() throws ExecutionException {
		return val;
	}
	@Override
	public String toStringDelegate() {
		if (val instanceof String)
			return "'"+val.toString()+"'";
		return val.toString();
	}
	@Override
	public String toOutput() {
		return val.toString();
	}
	
	@SuppressWarnings("unchecked")
	public PrimitiveRuntimeObject<T> copy() {
		return new PrimitiveRuntimeObject<T>((PrimitiveType<T>)type, val, parent, constant);
	}
}
