package cssInterpreter.program;

import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

public abstract class VoidFunction extends Function {
	
	static final RefKind retKind = RefKind.REF;
	
	Execution exec;

	public VoidFunction(String name, Execution exec) {
		super(new Signature(name, new FormalParameters()), retKind);
		this.exec = exec;
	}
	
	public VoidFunction(String name, Execution exec, FormalParameters params) {
		super(new Signature(name, params), retKind);
		this.exec = exec;
	}
	
	@Override
	public Type getOutputType() {
		return exec.VoidType;
	}
	
	abstract protected void execute(RuntimeObject thisReference, RuntimeObject params);
	
	@Override
	public final Reference evaluateDelegate(RuntimeObject thisReference, RuntimeObject params) {
		execute(thisReference, params);
		//return Execution.getVoidobj();
		//return exec.getVoidobj().copy();
		return new Reference(exec.getVoidobj(), retKind);
	}

}
