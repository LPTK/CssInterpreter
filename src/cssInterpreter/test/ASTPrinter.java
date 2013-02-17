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

		// FIXME : maybe remove this (if package_name is used in other
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
				+ "\" (with " + node.getBlock().size() + " member(s)):");
	}
	@Override
	public void outAClassDeclaration(AClassDeclaration node) {
		System.out.println("End of " + node.getClassName().getText() 
				+ " class declaration.");
	}
	
	

	
	
	/*
	 * Method declaration
	 */
	@Override
	public void inAMethodDeclaration(AMethodDeclaration node) {
		System.out.println("Declaring method " + node.getName().getText()
				+ " with " + node.getBlock().size() + " statement(s).");
	}

	
	
	
	
	/*
	 * Statements
	 */
	@Override
	public void inAExprStatement(AExprStatement node) {
		System.out.print("  statement: expression: ");
	}

	
	
	/*
	 * Expressions	
	 */
	@Override
	public void inAMethodCall(AMethodCall node) {
		// FIXME : this will explode if the method is not referenced via its name
		String name = ((AIdentGeneralId) ((AIdExpr) node.getExpr()).getGeneralId())
				.getIdent().getText();
		System.out.print("calling method " + name + " with parameters: ");
	}
	@Override
	public void outAMethodCall(AMethodCall node) {
		System.out.println();
	}

	@Override
	public void inAStringExpr(AStringExpr node) {
		System.out.print("\"" + node.getStringContent().getText() + "\"");
	}
	
	

}
