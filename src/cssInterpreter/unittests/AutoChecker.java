package cssInterpreter.unittests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
 
import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AutoChecker {
   private String inputString;
   private Boolean expectedResult;
   private CssChecker cssChecker;

   @Before
   public void initialize() {
	   cssChecker = new CssChecker();
   }

   // Each parameter should be placed as an argument here
   // Every time runner triggers, it will pass the arguments
   // from parameters we defined in primeNumbers() method
   public AutoChecker(String inputString, 
      Boolean expectedResult) {
      this.inputString = inputString;
      this.expectedResult = expectedResult;
   }
   
   static List<String> filesInDir(String dirname) {
	   List<String> ret = new ArrayList<String>();
	   File folder = new File(dirname);
	   File[] listOfFiles = folder.listFiles();
	   for (int i = 0; i < listOfFiles.length; i++)
		   if (listOfFiles[i].isFile())
			   ret.add(dirname+"/"+listOfFiles[i].getName());
	   return ret;
   }

   @Parameterized.Parameters
   public static Collection primeNumbers() {
	   /*return Arrays.asList(new Object[][] {
		         { "lol", true },
		         { "test", false }
		      });*/
	   /*
	  List<Object[]> ret = new ArrayList<Object[]>();
	  String foldername = "Grammar/UnitTests/Valid";
	  File folder = new File(foldername);
	  File[] listOfFiles = folder.listFiles();
	  //System.out.println(folder.list());
	  for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  String filename = listOfFiles[i].getName();
			  //System.out.println(">>"+filename);
			  ret.add(new Object[] {foldername+"/"+filename, true});
		  }
	  }
	  return ret;*/
	   
	   List<Object[]> ret = new ArrayList<Object[]>();
	   for (String f : filesInDir("Grammar/UnitTests/Valid"))
		   ret.add(new Object[] {f, true});
	   for (String f : filesInDir("Grammar/UnitTests/Invalid"))
		   ret.add(new Object[] {f, false});
	   return ret;
   }

   // This test will run 4 times since we have 5 parameters defined
   @Test
   public void testCssChecker() {
      //System.out.println("Parameterized String is : " + inputString);
	  System.out.println((expectedResult?"Valid":"Invalid")+" unit test file: " + inputString);
	  Boolean result = cssChecker.validate(inputString);
	  if (expectedResult == result)
		   System.out.println("Unit test succeeded.\n");
	  else System.out.println("Unit test failed.\n");
      assertEquals(expectedResult, result);
   }
}














