package cssInterpreter.unittests;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import org.junit.Test;

import cssInterpreter.lexer.CssLexer;
import cssInterpreter.lexer.LexerException;
import cssInterpreter.node.Start;
import cssInterpreter.parser.Parser;
import cssInterpreter.parser.ParserException;

public class CssChecker {

	public static void readFile(String filename) throws ParserException, LexerException, IOException {
		
		FileReader sourceReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(sourceReader);

		StringBuilder stringBuilder = new StringBuilder();
		int c;
		while ((c = bufferedReader.read()) != -1) {
			stringBuilder.append((char) c);
		}
		
		bufferedReader.close();
		sourceReader.close();
		
		String text = stringBuilder.toString();
		
		
		CssLexer lexer = new CssLexer(new PushbackReader(new StringReader(text),
				1024));
		Parser parser = new Parser(lexer);
		
		
		Start ast = parser.parse();
		
		// Print the ast. Should be removed later
		//ast.apply(new ASTPrinter());
		
	}
	
	public Boolean validate(final String filename) {
		
		try {
			
			readFile(filename);
			
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
		return true;
		
	}

}













