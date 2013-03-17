package cssInterpreter.program;

import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.RuntimeObject;

public abstract class VoidFunction extends Function {
	
	Execution exec;
	
	public VoidFunction(String name, Execution exec) {
		super(new Signature(name, new FormalParameters()));
		this.exec = exec;
	}

	@Override
	public Type getOutputType() {
		return exec.VoidType;
	}
	
	abstract protected void execute(RuntimeObject thisReference, RuntimeObject params);
	
	@Override
	public final RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject params) {
		execute(thisReference, params);
		//return Execution.getVoidobj();
		return exec.getVoidobj();
	}

}
