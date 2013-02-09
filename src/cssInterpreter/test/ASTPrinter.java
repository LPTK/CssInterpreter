package cssInterpreter.test;

import java.util.Iterator;

import cssInterpreter.analysis.DepthFirstAdapter;
import cssInterpreter.node.*;

public class ASTPrinter extends DepthFirstAdapter {

	/*
	 * Package specification
	 */
	@Override
	public void inAPackageStatement(APackageStatement node) {
		System.out.print("Package: ");
	}

	@Override
	public void caseAPackageName(APackageName node) {
		Iterator<TIdent> it = node.getComponents().iterator();

		while (it.hasNext()) {
			System.out.print(it.next().getText());

			if (it.hasNext())
				System.out.print(".");
		}

		// FIXME : maybe remove that (if package_name is used in other
		// productions than package_statement alone)
		System.out.println();
	}

	
	
	
	
	
	/*
	 * Package import
	 */
	@Override
	public void inAImportStatement(AImportStatement node) {
		System.out.print("Importing ");
	}

	@Override
	public void inAPackageImportPath(APackageImportPath node) {
		if (node.getImportType() instanceof AFullPackageImportType)
			System.out.print("everything");
		else if (node.getImportType() instanceof ASingleTypeImportType)
			System.out.print(((ASingleTypeImportType) node.getImportType())
					.getIdent().getText());

		System.out.print(" from ");

		Iterator<PPackageImportPrefix> it = node.getPrefix().iterator();

		if (!it.hasNext())
			System.out.print("<root>");

		while (it.hasNext()) {
			System.out.print(((APackageImportPrefix) it.next()).getIdent()
					.getText());

			if (it.hasNext())
				System.out.print(".");
		}

		System.out.println();
	}

	
	
	
	
	/*
	 * Class declaration
	 */
	@Override
	public void inAClassDeclaration(AClassDeclaration node) {
		System.out.println("Declaring class \"" + node.getClassName().getText()
				+ "\"");
	}

}
