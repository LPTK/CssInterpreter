package cssInterpreter.test;

import cssInterpreter.analysis.DepthFirstAdapter;
import cssInterpreter.node.ABasePackageName;
import cssInterpreter.node.APackageStatement;
import cssInterpreter.node.ARecPackageName;

public class ASTPrinter extends DepthFirstAdapter {

	@Override
	public void inAPackageStatement(APackageStatement node) {
		System.out.println("Package: ");
	}

	@Override
	public void inABasePackageName(ABasePackageName node) {
		System.out.println(node.getIdent().getText());
	}

	@Override
	public void inARecPackageName(ARecPackageName node) {
		System.out.println(node.getIdent().getText() + ".");
	}
	
	

}
