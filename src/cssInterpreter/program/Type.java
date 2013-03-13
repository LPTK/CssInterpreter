package cssInterpreter.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.UnknownFunctionCompExc;
import cssInterpreter.node.PAttrType;
import cssInterpreter.node.TIdent;

public class Type {
	HashMap<String, List<Function>> fcts = new HashMap<>();
	//Type[] attributes;
	TypeIdentifier id;
	//boolean tuple;

	List<String> attributeNames = new ArrayList<>();
	List<Type> attributeTypes = new ArrayList<>();

	
	
	public TypeBase(TypeIdentifier id) {//, boolean tuple) {
		this.id = id;
		//this.tuple = tuple;
		id.type = this; // sale
	}
	
	public void addFct(Function fct) { // TODO: detect ambiguities
		//System.out.println("Adding "+fct.signature);
		System.out.println("Adding "+fct+"  in type  "+this);
		
		List<Function> ls = fcts.get(fct.signature.name);
		if (ls == null) {
			ls = new ArrayList<Function>();
			fcts.put(fct.signature.name, ls);
		}
		ls.add(fct);
	}

	public void addType(Type t) {
		for (Function f : t.getConstructors())
			addFct(f);
	}
	
	
	@Override
	public boolean isDetermined() {
		return true;
	}
	@Override
	public ParamBinding getFunction(CallSignature callSign, CandidateList candidates) throws CompilerException {
		
		// HEAVY //System.out.println("Searching for function "+callSign+" in "+id.detailedString());
		// LIGHT // System.out.println("Searching for "+callSign.name+" in "+id);
		
		ParamBinding ret = null;
		List<Function> ls = fcts.get(callSign.name);
		if (ls == null)
			//throw new CompilerException("Name '"+callSign.name+"' is unknown in type "+this);
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+"\n\t"+candidates.toString());
		for (Function f : ls) {
			
			ParamBinding pb = callSign.getBinding(f);
			
			candidates.add(this,pb);
			
			if (!pb.isSuccessful()) {
				candidates.add(this,pb);
				if (ret != null)
					throw new CompilerException("Ambiguous call signature\n\t"+candidates);
				ret = pb;
			}
			
		}
		if (ret == null)
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+" with parameters "+callSign.args+"\n\t"+candidates.toString());
		return ret;
	}
	
	public void addAttribute(PAttrType attrType, TIdent name, Type type) {
		addAttribute(attrType, name.getText(), type);
	}
	public void addAttribute(PAttrType attrType, String name, Type type) {
		// TODO: use attrType to generate a Function.FieldType
		//addFct(new RuntimeField(name, type, attributeTypes.size()));
		attributeNames.add(name);
		//setAttributeName(attributeNames.size()-1, name);
		attributeTypes.add(type);
		setAttributeFunction(attributeNames.size()-1);
	}

	@Override
	public String[] getAttributeNames() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.toArray(new String[attributeNames.size()]);
	}
	
	@Override
	public Type[] getAttributeTypes() {
		assert attributeNames.size() == attributeTypes.size();
		//return (Type[]) attributeTypes.toArray();
		//return attributeTypes.toArray<Type>();
		//new ArrayList<Type>().t
		return attributeTypes.toArray(new Type[attributeTypes.size()]);
	}

	@Override
	public boolean isTuple() {
		//return tuple;
		return false;
	}
	
	
	
	@Override
	public ParamBinding getBinding(Function f) {
		//String error = null;
		assert isTuple(); // even single and void params are converted to a tuple with one or zero unnamed value, when passed to a function
		ParamBinding pb = new ParamBinding(f);
		
		System.out.println("Getting binding for "+this+" with "+f);
		
		int i = 0;
		for (String n : attributeNames) {
			if (n != null)
				break;
			if (i >= f.signature.params.namedTypes.length) {
				pb.setUnsuccessful("Not enough parameters. Parameter "+i+" expected, not found.");
				break;
			}
			pb.addBinding(i, f.signature.params.namedTypes[i].name);
			i++;
		}
		
		
		
		
		
		return pb;
	}
	
	
	@Override
	// returns null if it conforms
	public String notConformingToBecause(Signature sign) { // TODO rm
		
		//System.out.println(this);
		
		assert isTuple(); // even single and void params are converted to a tuple with one or zero unnamed value, when passed to a function
//		if (isTuple()) {
//			throw new NotSupportedCompExc();
//		} else {
//			if (sign.params.withoutValueNb() > 1)
//				return "More than one argument is needed";
//			return null;
//		}
		
		
		
		
		
		
		
		// handle default values
		
		
		// bind ordinals to names...?
		
		
		
		
		
		
		
		
		return null;
		/*
		if (sign.params.namedTypes.length == 1) {
			
		}*/
	}
	
	
	public void setAttributeFunction(int index) {
		addFct(new RuntimeField(attributeNames.get(index), attributeTypes.get(index), index));
	}
	
	public void setAttributeName(int index, String name) {
		assert attributeNames.get(index) == null;
		attributeNames.set(index, name);
		setAttributeFunction(index);
	}
	
	
	
	public boolean conformsTo(Signature sign) {
		return notConformingToBecause(sign) == null;
	}
	
	public boolean hasAttributes() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.size() != 0;
	}
	public boolean hasFunctions() {
		return fcts.size() != 0;
	}
	public boolean isEmpty() {
		return !hasAttributes() && !hasFunctions();
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
