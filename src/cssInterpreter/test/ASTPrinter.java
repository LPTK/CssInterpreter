package cssInterpreter.test;

import com.sun.corba.se.spi.ior.Identifiable;

import cssInterpreter.analysis.DepthFirstAdapter;
import cssInterpreter.node.ABaseFullPkgImport;
import cssInterpreter.node.ABasePackageName;
import cssInterpreter.node.AFullPackageImported;
import cssInterpreter.node.APackageStatement;
import cssInterpreter.node.ARecFullPkgImport;
import cssInterpreter.node.ARecPackageName;
import cssInterpreter.node.ATypeName;
import cssInterpreter.node.ATypeNameImported;

public class ASTPrinter extends DepthFirstAdapter {

	@Override
	public void inAPackageStatement(APackageStatement node) {
		System.out.print("Package: ");
	}

	@Override
	public void inABasePackageName(ABasePackageName node) {
		System.out.println(node.getIdent().getText());
	}

	@Override
	public void inARecPackageName(ARecPackageName node) {
		System.out.print(node.getIdent().getText() + ".");
	}

	
	@Override
	public void inAFullPackageImported(AFullPackageImported node) {
		System.out.print("Importing all ");
	}
	
	@Override
	public void outAFullPackageImported(AFullPackageImported node) {
		System.out.println(" package.");
	}
	
	

	@Override
	public void inABaseFullPkgImport(ABaseFullPkgImport node) {
		System.out.print(node.getIdent().getText());
	}

	@Override
	public void inARecFullPkgImport(ARecFullPkgImport node) {
		System.out.print(node.getIdent().getText() + ".");
	}

	
	
	@Override
	public void inATypeNameImported(ATypeNameImported node) {
		System.out.print("Importing ");
	}

	@Override
	public void outATypeNameImported(ATypeNameImported node) {
		System.out.println(" type.");
	}

	@Override
	public void inATypeName(ATypeName node) {
		System.out.print(node.getIdent().getText());
	}
	
	
	

}
