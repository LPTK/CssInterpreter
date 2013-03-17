package cssInterpreter.program;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

public class CandidateList {
	//private List<Pair<Type,Pair<Function, String>>> list = new ArrayList<>();
	private List<Pair<Type,ParamBinding>> list = new ArrayList<>();
	public int searchDepth = 0; // FIXME no public
	
	/*public void add(Type t, Function f, String s) {
		list.add(new Pair<Type, Pair<Function,String>>(t, new Pair<Function,String>(f, s)));
	}*/
	public void add(Type t, ParamBinding pb) {
		list.add(new Pair<>(t, pb));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (list.size() == 0)
			sb.append("No candidates found.");
		else {
			sb.append("Candidates are:");
			/*for (Pair<Type,Function> p : list) {
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond());
			}*/
			//for (Pair<Type,Pair<Function, String>> p : list)
			//	sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond().getFirst()+" ("+p.getSecond().getSecond()+")");
			for (Pair<Type,ParamBinding> p : list)
				sb.append("\n\t\t"+p.getFirst()+"::"+p.getSecond().fct+" ("+p.getSecond().getMessage()+")");
		}
		return sb.toString();
	}
}
