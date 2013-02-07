package cssInterpreter.main;

import java.io.InputStreamReader;
import java.io.PushbackReader;

import cssInterpreter.lexer.CssLexer;
import cssInterpreter.node.Start;
import cssInterpreter.parser.Parser;
import cssInterpreter.test.ASTPrinter;

public class CssInterpreter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CssLexer lexer;
		Parser parser;
		Start ast;
		
		
		try {
			lexer = new CssLexer(new PushbackReader(new InputStreamReader(
					System.in), 1024));
			parser = new Parser(lexer);

			ast = parser.parse();

			// Print the ast. Should be removed later
			ast.apply(new ASTPrinter());

		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
