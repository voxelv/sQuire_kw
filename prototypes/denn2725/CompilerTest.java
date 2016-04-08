import java.util.Arrays;
import java.lang.StringBuilder;
import javax.tools.JavaFileObject;

public class CompilerTest {

	public static void main(String[] args) throws Exception
    {
    	//1.Construct an in-memory java source file from your dynamic code
    	String className = "Calculator";
    	String packageClass = "math.Calculator";
    	//Iterable<? extends JavaFileObject> files = Arrays.asList(file);
    	
    	StringBuilder contents = new StringBuilder("package math;\n"+"import math.Test;\n"+"public class Calculator { \n"+"public void testAdd() { \n"+"Test test = new Test();"+"System.out.println(test.thing+300); \n"+" } \n"+" public static void main(String[] args) { \n"+" Calculator cal = new Calculator(); \n"+" cal.testAdd(); \n"+" } \n" + "} \n");
    	StringBuilder contentsTest = new StringBuilder("package math;\n"+"public class Test {\n"+"int thing;\n"+"public Test() {\n"+"thing = 200;\n"+"}\n"+"}\n");
    	StringBuilder contents1 = new StringBuilder("package testing;\n" +
    												"public class PrintTest {\n"
    												+ "public static void main(String[] args) {\n"
    												+ "System.out.println(\"This program is coded as strings within the CompilerTest Class\");\n"
    												+ "System.out.println(\"And hey look, it works!\");\n"
    												+ "}\n"
    												+ "\n");
    	String f3PackName = "testing.";
    	String f3ClassName = "PrintTest";
    	JavaFileObject file1 = new Compiler().new InMemoryJavaFileObject(className, contents.toString());
    	JavaFileObject file2 = new Compiler().new InMemoryJavaFileObject("math.Test", contentsTest.toString());
    	JavaFileObject file3 = new Compiler().new InMemoryJavaFileObject(f3PackName+f3ClassName, contents1.toString());
    	Iterable<? extends JavaFileObject> files = Arrays.asList(file3);
    	
    	//2.Compile your files by JavaCompiler
    	Compiler.compile(files);
     
        //3.Load your class by URLClassLoader, then instantiate the instance, and call method by reflection
    	Compiler.runIt(f3PackName+f3ClassName);
    }
}