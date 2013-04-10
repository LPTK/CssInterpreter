package cssInterpreter.unittests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import cssInterpreter.compiler.Interpreter;
import cssInterpreter.lexer.CssLexer;
import cssInterpreter.lexer.LexerException;
import cssInterpreter.node.Start;
import cssInterpreter.parser.Parser;
import cssInterpreter.parser.ParserException;

class CssExecChecker {
	
	// synchronized 
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
		
		File f = new File("Execution/UnitTests/Output/"+filename.substring(filename.lastIndexOf("/")+1));
		f.createNewFile();
		OutputStream out = new FileOutputStream(f);
		PrintStream prnt = new PrintStream(out);
		boolean finished = false;
		
		try {
			//ast.apply(new DumbInterpreter(text, prnt));
			ast.apply(new Interpreter(text, prnt));
			finished = true;
		} catch (Exception e) {
			e.printStackTrace(prnt);
			throw e;
		} finally {
			if (!finished)
				prnt.println(" [ EXECUTION INTERRUPTED ] ");
		}
	}
	
	public Boolean validate(final String filename) {
		try {
			//System.out.println(filename);
			readFile(filename);
		} catch (Exception e) {
			System.out.println(e);
			//throw new RuntimeException(e);
			return false;
		}
		return true;
	}

}


@RunWith(Parameterized.class)
public class ExecutionChecker {
   private String inputString;
   private Boolean expectedResult;
   private CssExecChecker cssExecChecker;

   @Before
   public void initialize() {
	   cssExecChecker = new CssExecChecker();
   }

   // Each parameter should be placed as an argument here
   // Every time runner triggers, it will pass the arguments
   // from parameters we defined in primeNumbers() method
   public ExecutionChecker(String inputString, 
      Boolean expectedResult) {
      this.inputString = inputString;
      this.expectedResult = expectedResult;
   }
   
   static List<String> filesInDir(String dirname) {
	   List<String> ret = new ArrayList<String>();
	   File folder = new File(dirname);
	   File[] listOfFiles = folder.listFiles();
	   //System.out.println(dirname);
	   //System.out.println(listOfFiles);
	   for (int i = 0; i < listOfFiles.length; i++)
		   if (listOfFiles[i].isFile())
			   ret.add(dirname+"/"+listOfFiles[i].getName());
	   return ret;
   }

   @Parameterized.Parameters
   public static Collection<Object[]> utFiles() {
	   List<Object[]> ret = new ArrayList<Object[]>();
	   for (String f : filesInDir("Execution/UnitTests/Valid"))
		   ret.add(new Object[] {f, true});
	   for (String f : filesInDir("Execution/UnitTests/Invalid"))
		   ret.add(new Object[] {f, false});
	   return ret;
   }
   
   @Test
   public void testCssChecker() {
      //System.out.println("Parameterized String is : " + inputString);
	  System.out.println(">> "+(expectedResult?"Valid":"Invalid")+" unit test file: " + inputString);
	  Boolean result = cssExecChecker.validate(inputString);
	  if (expectedResult == result)
		   System.out.println("---> Unit test succeeded.\n");
	  else System.out.println("---> Unit test failed.\n");
      assertEquals(expectedResult, result);
   }
}














