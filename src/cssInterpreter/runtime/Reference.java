package cssInterpreter.runtime;

import cssInterpreter.node.ARefAttrType;
import cssInterpreter.node.ARvalAttrType;
import cssInterpreter.node.AValAttrType;
import cssInterpreter.node.PAttrType;

public class Reference {
	
	public static enum RefKind {
		VAL,
		RVAL,
		REF
	}

	public static Reference VAL(RuntimeObject obj) { return new Reference(obj, RefKind.VAL); }
	public static Reference RVAL(RuntimeObject obj) { return new Reference(obj, RefKind.RVAL); }
	public static Reference REF(RuntimeObject obj) { return new Reference(obj, RefKind.REF); }
	
	public static RefKind kindFromNode(PAttrType attrType) {
		RefKind rtype = null;
		if (attrType == null || attrType instanceof ARefAttrType)
			rtype = RefKind.REF;
		else if (attrType instanceof AValAttrType)
			rtype = RefKind.VAL;
		else if (attrType instanceof ARvalAttrType)
			rtype = RefKind.VAL;
		else assert false;
		return rtype;
	}
	
	private RuntimeObject obj;
	private RefKind refType;
	
	public Reference(RuntimeObject obj, RefKind refType) {
		this.obj = obj;
		this.refType = refType;
	}
	
	public RuntimeObject access() {
		return obj;
	}
	
	public void destroy() {
		//if (refType == RefType.RVALUE)
		if (refType != RefKind.REF)
			obj.destruct();
	}

	public void reassign(RuntimeObject obj) {
		this.obj = obj;
	}
	
	public RefKind getRefType() {
		return refType;
	}
	
	@Override
	public String toString() {
		return (getRefType()==RefKind.REF?"&":(getRefType()==RefKind.RVAL?"'&":""))+obj;
	}
	
}










