package cssInterpreter.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.StringReader;

import cssInterpreter.compiler.CompilerException;
import cssInterpreter.compiler.Interpreter;
import cssInterpreter.lexer.CssLexer;
import cssInterpreter.node.Start;
import cssInterpreter.parser.Parser;

public class CssInterpreter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CssLexer lexer;
		Parser parser;
		Start ast = null;
		
		String text = new String();
		//String filename = "/tmp/test.css"; // TODO
		String filename = "/tmp/FunctionCalls.txt"; // TODO
		
		try {
			FileReader sourceReader = new FileReader(filename);
			//InputStreamReader sourceReader = new InputStreamReader(System.in);
			BufferedReader bufferedReader = new BufferedReader(sourceReader);
			
			StringBuilder stringBuilder = new StringBuilder();
			int c;
			while ((c = bufferedReader.read()) != -1) {
				stringBuilder.append((char) c);
			}
			
			bufferedReader.close();
			sourceReader.close();
			
			text = stringBuilder.toString();

			
			lexer = new CssLexer(new PushbackReader(new StringReader(text),
					1024));
			parser = new Parser(lexer);
			

			ast = parser.parse();
			
			//ast.getPCompilationUnit().apply(new Switch(){});
			//ast.getPCompilationUnit().apply(new Switch(){});

			// Print the ast. Should be removed later
			//ast.apply(new ASTPrinter());
			

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	
		//ast.apply(new DumbInterpreter());
		
		final boolean debug = true;
		
		if (debug)
			ast.apply(new Interpreter());			
		else try {
			ast.apply(new Interpreter());
		} catch (Exception e) {
			if (e.getCause() instanceof CompilerException)
				System.err.println(e);
			else
				throw e;
		}

	}

}








