insert into PFLines(nextid, text) values (NULL, "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    }");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "        System.out.println(\"Hello, World\");");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "        // Prints \"Hello, World\" to the terminal window.");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    public static void main(String[] args) {");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class HelloWorld {");
insert into PFiles(pfname, pflhead) values ("Hello World.java", (select LAST_INSERT_ID()));

/*Add Scott's Tots*/

/*1. Project that doesn't exist*/

/* 2. Project with no files */

/* 3. Project with one file that is blank */
insert into PFLines(nextid, text) values (NULL, "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFiles(pfname, pflhead) values ("Blank.java", (select LAST_INSERT_ID()));

/* 4. Project with one file that's gibberish */

insert into PFLines(nextid, text) values (NULL, "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "xxxxxxxxxxkljkh.k.kjhkjhkjhkxx");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class Gibberish");
insert into PFiles(pfname, pflhead) values ("Gibberish.java", (select LAST_INSERT_ID()));

/* 5. Project with one file that outputs text to console SUCCEEDS
 */
/* 6. Project with a file that imports a second file
 */
 ///////////////////////////////
///////////////////////////////


insert into PFLines(nextid, text) values (NULL, "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    }");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    System.out.println("This is the TestFile printLine() function");");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "    {");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public printLine()");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "System.out.println("This is the TestFile constructor");");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "TestFile()");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class TestFile");
insert into PFiles(pfname, pflhead) values ("TestFile.java", (select LAST_INSERT_ID()));


///////////////////////////////

insert into PFLines(nextid, text) values (NULL, "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "testFile.printLine(); ");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "TestFile testFile = new TestFile();");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public static void main(String[] args)");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class TestFileImport");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "import TestFile;");
insert into PFiles(pfname, pflhead) values ("TestFileImport.java", (select LAST_INSERT_ID()));





/* 7. Project that with a file that imports a java library and uses it
 */


insert into PFLines(nextid, text) values (NULL, "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "}");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "System.out.println("The answer is:" + output);");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "String output = String.valueOf(result);");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "double result = sqrt(input);");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "double input = 25;");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{
");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public static void main(String[] args)");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "{");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "public class SqrtTest");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "import static java.lang.Math.sqrt;");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "import java.lang.Math;");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "");
insert into PFLines(nextid, text) values ((select LAST_INSERT_ID()), "//Hi Scott");
insert into PFiles(pfname, pflhead) values ("SqrtTest.java", (select LAST_INSERT_ID()));












