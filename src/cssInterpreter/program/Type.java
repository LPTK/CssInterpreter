package cssInterpreter.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.UnknownFunctionCompExc;
import cssInterpreter.node.PAttrType;
import cssInterpreter.node.TIdent;

public class Type implements TypeReference {
	HashMap<String, List<Function>> fcts = new HashMap<>();
	//Type[] attributes;
	TypeIdentifier id;
	//boolean tuple;
	Type parent;
	
	List<String> attributeNames = new ArrayList<>();
	List<TypeReference> attributeTypes = new ArrayList<>();

	
	
	public Type(TypeIdentifier id, Type parent) {//, boolean tuple) {
		this.id = id;
		//this.tuple = tuple;
		id.type = this; // sale
		this.parent = parent;
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
	
	
	public List<Function> getConstructors() {
		return new ArrayList<Function>(); // FIXME: correct?
	}
	
	public ParamBinding getFunction(CallSignature callSign, CandidateList candidates) throws CompilerException {
		
		// HEAVY //System.out.println("Searching for function "+callSign+" in "+id.detailedString());
		// LIGHT //System.out.println("Searching for "+callSign.name+" in "+id);
		
		ParamBinding ret = null;
		List<Function> ls = fcts.get(callSign.name);
		if (ls == null)
			//throw new CompilerException("Name '"+callSign.name+"' is unknown in type "+this);
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+"\n\t\t"+candidates.toString());
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
			throw new UnknownFunctionCompExc("Name '"+callSign.name+"' is unknown in type "+this+" with parameters "+callSign.args+"\n\t\t"+candidates.toString());
		return ret;
	}
	
	public void addAttribute(PAttrType attrType, TIdent name, TypeReference type) {
		addAttribute(attrType, name.getText(), type);
	}
	public void addAttribute(PAttrType attrType, String name, TypeReference type) {
		// TODO: use attrType to generate a Function.FieldType
		//addFct(new RuntimeField(name, type, attributeTypes.size()));
		attributeNames.add(name);
		//setAttributeName(attributeNames.size()-1, name);
		attributeTypes.add(type);
		setAttributeFunction(attributeNames.size()-1);
	}
	
	public String[] getAttributeNames() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.toArray(new String[attributeNames.size()]);
	}
	
	public TypeReference[] getAttributeTypes() {
		assert attributeNames.size() == attributeTypes.size();
		//return (Type[]) attributeTypes.toArray();
		//return attributeTypes.toArray<Type>();
		//new ArrayList<Type>().t
		return attributeTypes.toArray(new TypeReference[attributeTypes.size()]);
	}
	
	public boolean isTuple() {
		//return tuple;
		return false;
	}
	
	
	
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
	
	
	
	public void setAttributeFunction(int index) {
		assert attributeNames.get(index) == null || !attributeNames.get(index).equals("_"+index); // TODO: better check for this shit
		String attrName = attributeNames.get(index) == null? "_"+index : attributeNames.get(index);
		addFct(new FieldAccessFunction(attrName, attributeTypes.get(index), index));
	}
	
	public void setAttributeName(int index, String name) {
		assert attributeNames.get(index) == null;
		attributeNames.set(index, name);
		setAttributeFunction(index);
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
	public Type getType() {
		return this;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}

	public Type getParent() {
		return parent;
	}

	public void setName(String newName) {
		id.name = newName;
	}

}
