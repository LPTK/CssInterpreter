package cssInterpreter.unittests;

import static org.junit.Assert.*;

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
import cssInterpreter.test.ASTPrinter;

public class Basics {
	
	public void readFile(String filename) throws ParserException, LexerException, IOException {
	
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
	
	@Test
	public void test() {
		/*
		Object[][] o = new Object[][] {
		         { 2, true },
		         { 6, false },
		         { 19, true },
		         { 22, false },
		         { 23, true }
		      };
		o[0][0].equals(new Object(){2,true});
		*/
		try {
			//fail("Not yet implemented");
			File folder = new File("Grammar/UnitTests/Valid");
			File[] listOfFiles = folder.listFiles(); 
			 
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					String filename = listOfFiles[i].getName();
					System.out.println(">>"+filename);
					readFile(filename);
				}
			}

			//readFile("Grammar/UnitTests/Valid/hellow.txt");
			
		} catch (Exception e) {
			//System.out.println(e);
			e.printStackTrace();
			fail(e.toString());
		}
	}

}






