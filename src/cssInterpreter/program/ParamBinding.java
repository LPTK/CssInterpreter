package cssInterpreter.program;

import java.util.ArrayList;
import java.util.List;

import util.Pair;
import cssInterpreter.runtime.RuntimeObject;

public class ParamBinding {
	//final boolean successful;
	//public final String bindingError;
	String bindingError = "Unknown";
	List<Pair<Integer,String>> names = new ArrayList<>();
	//private Function fct;
	public final Function fct;
	private int searchDepth;
	
	/*public ParamBinding(String bindingError) {
		this.bindingError = bindingError;
	}*/
	public ParamBinding(Function fct, int searchDepth) {
		this.fct = fct;
		this.searchDepth = searchDepth;
	}
	
	private void mutate(RuntimeObject args) { // FIXME: duplicate type before?? this code adds reeaaally nasty states
		for (Pair<Integer,String> p : names)
			args.renameAttribute(p.getFirst(), p.getSecond());
	}
	
	
	// reaches up the right type through parent hierarchy
	private RuntimeObject recurseBack(RuntimeObject args) {
		for (int i = 0; i < searchDepth; i++)
			args = args.getParent();
		return args;
	}
	
	
	public void set(RuntimeObject thisReference, RuntimeObject args, RuntimeObject value) {
		//args = recurseBack(args);
		thisReference = recurseBack(thisReference);
		mutate(args);
		fct.set(thisReference, args, value);
	}
	
	public RuntimeObject evaluate(RuntimeObject thisReference, RuntimeObject args) {
		//args = recurseBack(args);
		thisReference = recurseBack(thisReference);
		mutate(args);
		return fct.evaluate(thisReference, args);
	}

	/*public Function getFunction() {
		return fct;
	}*/
	public void addBinding(Integer index, String name) {
		names.add(new Pair<>(index,name));
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

