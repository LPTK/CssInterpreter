package cssInterpreter.program;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class CandidateList {
	//private List<Pair<Type,Pair<Function, String>>> list = new ArrayList<>();
	private List<Pair<Type,ParamBinding>> candidates = new ArrayList<>();
	public int searchDepth = 0; // FIXME no public
	Type initialType;
	
	public CandidateList(Type initialType) {
		this.initialType = initialType;
	}
	
	/*public void add(Type t, Function f, String s) {
		list.add(new Pair<Type, Pair<Function,String>>(t, new Pair<Function,String>(f, s)));
	}*/
	public void add(Type t, ParamBinding pb) {
		candidates.add(new Pair<>(t, pb));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (candidates.size() == 0)
			sb.append("No candidates found.");
		else {
			sb.append("Candidates are:");
			/*for (Pair<Type,Function> p : list) {
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond());
			}*/
			//for (Pair<Type,Pair<Function, String>> p : list)
			//	sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond().getFirst()+" ("+p.getSecond().getSecond()+")");
			for (Pair<Type,ParamBinding> p : candidates)
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond().fct+"  (reason: "+p.getSecond().getMessage()+")");
		}
		return sb.toString();
	}

	public int count() {
		return candidates.size();
	}

	public Type getInitialType() {
		return initialType;
	}

	public String withoutNonSuccessful() {
		return toString(); // FIXME?
	}
}
