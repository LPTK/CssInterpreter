package cssInterpreter.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.UnknownFunctionException;
import cssInterpreter.node.TIdent;
import cssInterpreter.runtime.Execution;
import cssInterpreter.runtime.Reference;
import cssInterpreter.runtime.Reference.RefKind;
import cssInterpreter.runtime.RuntimeObject;

//public class Type extends RuntimeObject implements TypeReference {
public class Type extends TypeReference {
	HashMap<String, List<Function>> fcts = new HashMap<>();
	//Type[] attributes;
	TypeIdentifier id;
	//boolean tuple;
	Type parent;
	final private RuntimeObject objectRepr; // "inheritance" by composition
	
	List<String> attributeNames = new ArrayList<>();
	List<TypeReference> attributeTypes = new ArrayList<>();
	//List<RefKind> attributeKinds = new ArrayList<>(); // ie: val, rval or ref
	
	boolean isAClass = false; // TODO: use

	private PointerType pointerType;
	
	
	public Type(TypeIdentifier id, Type parent, boolean hasCopyFunction) {//, boolean tuple) {
		//super(Execution.getInstance().TypeType, parent, true);
		objectRepr = new RuntimeObject(Execution.getInstance().TypeType, parent == null? null: parent.getObjectRepresentation(), true);
		this.id = id;
		//this.tuple = tuple;
		id.type = this; // sale
		this.parent = parent;
		//pointerType = new PointerType(this);
		
		if (hasCopyFunction) { // because it makes an infinite recursion if every type had copy
			final Type that = this;
			addFct(new Function(new Signature("copy", new FormalParameters()), RefKind.VAL) {
				@Override
				public TypeReference getOutputType() {
					return that;
				}
				@Override
				public Reference evaluateDelegate(RuntimeObject thisReference, RuntimeObject args) {
					return thisReference.copy(false);
				}
			});
		}
	}
	
	public RuntimeObject getObjectRepresentation() {
		return objectRepr;
	}
	

	public void setParent(Type par) {
		parent = par;
	}
	
	
	public void addFct(Function fct) { // TODO: detect ambiguities
		//System.out.println("Adding "+fct.signature);
		
		
		////System.out.println("Adding "+fct+"  in type  "+this);
		
		
		
		List<Function> ls = fcts.get(fct.signature.getName());
		if (ls == null) {
			ls = new ArrayList<Function>();
			fcts.put(fct.signature.getName(), ls);
		}
		ls.add(fct);
	}
	
	public Map<String, List<Function>> getFcts() {
		return fcts;
	}
	
	public void addType(Type t) { // FIXME: still useful???
		assert t.getConstructors().size() > 0;
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
			///throw new UnknownFunctionCompExc(callSign.getLine(), "Name '"+callSign.name+"' is unknown in type "+this+"\n\t  "+candidates.toString());
			throw new UnknownFunctionException(callSign.getLine(),
					"Name '"+callSign.name+"' is unknown in scope of type "+candidates.getInitialType()
					+(candidates.count() > 0 ? " with parameters "+callSign.params : "")
					+"\n\t  "+candidates.toString()
				);
		for (Function f : ls) {
			
			ParamBinding pb = callSign.getBinding(f, candidates.searchDepth);
			
			candidates.add(this,pb);
			/*
			if (!pb.isSuccessful()) {
				candidates.add(this,pb);
				if (ret != null)
					throw new CompilerException("Ambiguous call signature\n\t"+candidates);
				ret = pb;
			}*/
			
			if (pb.isSuccessful()) {
				if (ret != null)
					throw new CompilerException("Ambiguous call signature "+callSign+"\n\t"+candidates.withoutNonSuccessful());
				ret = pb;
			}
			
		}
		if (ret == null) // TODO: factorize (le meme en haut)
			throw new UnknownFunctionException(callSign.getLine(), "Name '"+callSign.name+"' is unknown in type "+candidates.getInitialType()+" with parameters "+callSign.params+"\n\t  "+candidates.toString());
		return ret;
	}

//	public void addAttribute(RefKind attrKind, TIdent name, TypeReference type) {
//		addAttribute(attrKind, name.getText(), type);
//	}
	public void addAttribute(TIdent name, TypeReference type) {
		addAttribute(name.getText(), type);
	}
	//public void addAttribute(PAttrType attrType, String name, TypeReference type) {
	//public void addAttribute(RefKind attrKind, String name, TypeReference type) {
	public void addAttribute(String name, TypeReference type) {
		// TODO: use attrType to generate a Function.FieldType
		
		//addFct(new RuntimeField(name, type, attributeTypes.size()));
		attributeNames.add(name);
		//setAttributeName(attributeNames.size()-1, name);
		
		
		/*
		if (attrType == null || attrType instanceof ARefAttrType) // TODO: refine? // TODO handle rvals
			type = type.getPointerTypeRef();
		*/
		/*
		RefKind rtype = null;
		
		if (attrType == null || attrType instanceof ARefAttrType)
			rtype = RefKind.REF;
		else if (attrType instanceof AValAttrType)
			rtype = RefKind.VAL;
		else if (attrType instanceof ARvalAttrType)
			rtype = RefKind.VAL;
		else assert false;
		*/
		
		
		
		// FIXME
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		attributeTypes.add(type);
		
		//attributeKinds.add(Reference.kindFromNode(attrType));
		
		////attributeKinds.add(attrKind);
		
		
		
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
	/*
	public RefKind[] getAttributeKinds() {
		return attributeKinds.toArray(new RefKind[attributeKinds.size()]);
	}*/
	
	public boolean isTuple() {
		//return tuple;
		return false;
	}
	
	
	private boolean isSettable(int index, NamedType nt, ParamBinding pb) {
		
		String n = attributeNames.get(index);
		
		String pname = nt.name;
		Type ptype = nt.type.getType();
		RefKind pkind = nt.type.getKind();
		
		if (!ptype.isSettableTo(attributeTypes.get(index).getType())) {
			pb.setUnsuccessful(
					"Cannot use argument "
					+(n==null? "": n+" ")
					+"of type "+attributeTypes.get(index).getType()
					+" to initialize parameter "
					+(pname==null? "": pname+" ")
					+"of type "+ptype
				);
			return false;
		}
		if (pkind == RefKind.VAL && attributeTypes.get(index).getKind() != RefKind.VAL) {
			pb.setUnsuccessful(
					"Was expecting a VAL for parameter "
					+(pname==null? "": pname+" ")
					+"of type "+attributeTypes.get(index).getType()
					+", was given a "
					+attributeTypes.get(index).getKind()
				);
			return false;
		}
		if (pkind == RefKind.RVAL && attributeTypes.get(index).getKind() == RefKind.REF) {
			pb.setUnsuccessful(
					"Was expecting a RVAL for parameter "
					+(pname==null? "": pname)
					+"of type "+attributeTypes.get(index).getType()
					+", was given a "
					+attributeTypes.get(index).getKind()
				);
			return false;
		}
		
		return true;
	}
	
	
	public ParamBinding getBinding(Function f, int searchDepth) throws CompilerException { // TODO: also check for functions existence
		//String error = null;
		assert isTuple(); // even single and void params are converted to a tuple with one or zero unnamed value, when passed to a function
		ParamBinding pb = new ParamBinding(f,searchDepth);
		
		
		////System.out.println("Getting binding for "+this+" with "+f);
		
		
		/*
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
		*/
		
		/*
		 * Currently, actual bindings are created only for unnamed args
		 * If the ParamBinding is successful, then all named args correspond to their named param counterpart
		 */
		
		int k = 0;
		for (String n : attributeNames) { // Looping through all attributes not having a name
			if (n != null) {
				//k++;
				break;
			}
			if (k >= f.signature.params.namedTypes.length) {
				pb.setUnsuccessful("Too many arguments given: "+attributeNames.size()+" given, "+f.signature.params.namedTypes.length+" expected.");
				break;
			}
			/**
			if (f.signature.params.namedTypes[k].name == null) {
				pb.setUnsuccessful("Parameter "+k+" of "+f+"  has no name and cannot be bound.");
				break;
			}
			*/
			/*
			String pname = f.signature.params.namedTypes[k].name;
			Type ptype = f.signature.params.namedTypes[k].type.getType();
			RefKind pkind = f.signature.params.namedTypes[k].kind;
			*/
			//if (attributeTypes.get(k).getType().isPointer() && ptype.isSettableTo(attributeTypes.get(k).getType())) {
//			if (attributeTypes.get(k).getType() instanceof PointerType
//				&& ptype.isSettableTo(((PointerType)attributeTypes.get(k).getType()).getPointedType())) { // TODO rm useless (no more used)
//				pb.addBinding(k, k, true);
//			} else {
				/*
				if (!ptype.isSettableTo(attributeTypes.get(k).getType())) {
					pb.setUnsuccessful(
							"Cannot use argument "
							+(n==null? "": n+" ")
							+"of type "+attributeTypes.get(k).getType()
							+" to initialize parameter "
							+(pname==null? "": pname+" ")
							+"of type "+ptype
						);
					break;
				}
				
				if (pkind == RefKind.VAL && attributeKinds.get(k) != RefKind.VAL) {
					pb.setUnsuccessful(
							"Was expecting a VAL for parameter "
							+(pname==null? "": pname)
							+", was given a "
							+attributeKinds.get(k)
						);
					break;
				} else if (pkind == RefKind.RVAL && attributeKinds.get(k) == RefKind.REF) {
					pb.setUnsuccessful(
							"Was expecting a RVAL for parameter "
							+(pname==null? "": pname)
							+", was given a "
							+attributeKinds.get(k)
						);
					break;
				}
				*/
				
				
				
				
				
				
				//pb.addBinding(k, f.signature.params.namedTypes[k].name);
//				pb.addBinding(k, k);
//			}
			
			if (isSettable(k, f.signature.params.namedTypes[k], pb))
				 pb.addBinding(k, k);
			else break;
			
			k++;
		}
		
		if (!pb.isSuccessful()) return pb;
		
		/*
		 *  At this point we've covered all unnamed arguments in the arg type; we've yet to check the rest
		 *  THe rest starts at index k
		 *  
		 */
		
		//for (int i = 0; i < f.signature.params.namedTypes.length; i++) {
		int i;
		for (i = k; i < f.signature.params.namedTypes.length; i++) {
			NamedType param = f.signature.params.namedTypes[i];
			boolean found = false;
			/*for (String n : attributeNames) {
				if (n != null && n.equals(param.name)) {
					found = true;
					break;
				}
			}*/
			//System.out.println("searching for "+param.name);
			int j;
			//for (j = k; j < attributeNames.size(); j++) {
			for (j = k; j < attributeNames.size() && j < f.signature.params.namedTypes.length; j++) {
				String n = attributeNames.get(j);
				assert n != null;
				if (n != null && n.equals(param.name)) {
					found = true;
					break;
				}
			}
			if (found) {
				
				
				
				//pb.addBinding(i, f.signature.params.namedTypes[j].name);
				//pb.addBinding(i, j);
				
				if (isSettable(i, f.signature.params.namedTypes[j], pb))
					 pb.addBinding(i, j);
				else break;
				
				
			} else {
				if (!pb.hasName(param.name)) {
					pb.setUnsuccessful("Not enough arguments: parameter '"+param.name+"' expected, not found.");
					break;
				}
			}
			
		}
		if (pb.isSuccessful() && i < attributeNames.size()) {
			//pb.setUnsuccessful("Too many arguments given: "+attributeNames.size()+" given, "+f.signature.params.namedTypes.length+" expected.");
			pb.setUnsuccessful(
					"Too many arguments given: "
					+attributeNames.size()+" given, "
					+f.signature.params.namedTypes.length
					+" expected. Argument '"+attributeNames.get(i)
					+"' was not expected"
				);
		}
		
		/*
		if (i >= f.signature.params.namedTypes.length) {
				pb.setUnsuccessful("Not enough parameters. Parameter "+i+" expected, not found.");
				break;
			}
		*/
		
		//pb.setSuccessful();
		
		return pb;
	}
	
	
	
	
	
	public boolean conformsTo(Type type) throws CompilerException {
		if (isAClass)
			return this == type;
		for (int i = 0; i < getAttributeCount(); i++) {
			if (!attributeTypes.get(i).getType().conformsTo(type.attributeTypes.get(i).getType()))
				return false;
		}
		// TODO: check that functions are also present
		return true;
	}
	
	
	public int getAttributeCount() {
		assert attributeNames.size() == attributeTypes.size();
		return attributeNames.size();
	}
	
	
	public boolean isSettableTo(Type t) { // TODO: implement inheritance and traits
		if (id.name.equals("Any")) // FIXME!
			return true;
		return this == t;
	}
	
	public void setAttributeFunction(int index) {
		assert attributeNames.get(index) == null || !attributeNames.get(index).equals("_"+index); // TODO: better check for this shit
		String attrName = attributeNames.get(index) == null? "_"+index : attributeNames.get(index);
		addFct(new FieldAccessFunction(attrName, attributeTypes.get(index), index, attributeTypes.get(index)));
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
	
	/*
	@Override
	public Type getType(int currentTypeInferenceId) {
		return this;
	}
	*/
	
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

	@Override
	protected Type getTypeDelegate() {
		return this;
	}
	
	private boolean resolved = false;
	@Override
	public boolean isResolved() {
		return resolved;
	}

	@Override
	public Type resolveDelegate(int currentTypeInferenceId) throws CompilerException {
		
		//System.out.println("Resolving type "+this);
		///Interpreter.getInstance().out("Resolving type "+this);
		///Interpreter.getInstance().indent();
		
		for (TypeReference t : attributeTypes)
			t.resolve(currentTypeInferenceId);
		for (List<Function> fls : fcts.values())
			for (Function f: fls)
				f.resolve(currentTypeInferenceId);
		/*for (Function f: getConstructors()) // really necessary? should have been resolved by resolving the type that contains it...
			f.resolve(currentTypeInferenceId);*/
		resolved = true;
		
		///Interpreter.getInstance().deindent();
		return this;
	}
	
	private boolean destroyed = false;
	public boolean isDestroyed() {
		return destroyed;
	}
	
	//boolean deb=false;
	public void destroy() { //deb = true;
//		if (id.name.equals("B"))
//			System.out.println();
		objectRepr.destruct();
		destroyed = true;
	}

	public PointerType getPointerType() { //assert false;
		if (pointerType == null)
			pointerType = new PointerType(this);
		return pointerType;
	}
	
	@Override
	protected RefKind getKindDelegate() {
		//throw new NotSupportedException();
		return null;
	}
	
}




