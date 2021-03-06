package sq.app.model;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

 
/**
 * Dynamic java class compiler and executer  <br>
 * 
 * To use: call JavaFileObject file = new Compiler().new InMemoryJavaFileObject(classNameString, codeString); <br>
 * 		   then call Iterable<? extends JavaFileObject> files = Arrays.asList(file); <br>
 * 				note: you can pass Arrays.asList multiple JavaFileObjects <br>
 * 		   to compile files, call Compiler.compile(files); <br>
 * 		   finally, to execute a main() function, call Compiler.runIt("[packageName].[className]"); <br>
 * 		   		note: this will run the main() inside specified class <br>
 *
 * http://www.beyondlinux.com
 *
 * @author david 2011/07
 * Modified by Scott Dennis 2016/03
 */
public class Compiler
{
    /** where shall the compiled class be saved to (should exist already) */
	String path;
	public static String compilerOutput, systemOutput;
	File directory;
    
	private static String classOutputFolder;
    
 
    public Compiler()
    {
    	path = System.getProperty("user.dir") + "\\classes";
    	compilerOutput = "";
    	systemOutput = "";
    	File directory = new File(path);
    	if(!directory.exists())
    		directory.mkdir();
    	classOutputFolder = System.getProperty("user.dir") + "\\classes";
    }
    
    /**
     * Sends compiler output to console <br>
     */
    public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject>
    {
        public void report(Diagnostic<? extends JavaFileObject> diagnostic)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

        	
        	
//        	MainViewController.CompilerOutput.appendText("Line Number->" + diagnostic.getLineNumber());
//        	MainViewController.CompilerOutput.appendText("code->" + diagnostic.getCode());
//        	MainViewController.CompilerOutput.appendText("Message->" + diagnostic.getMessage(Locale.ENGLISH));
//        	MainViewController.CompilerOutput.appendText("Source->" + diagnostic.getSource());
//        	MainViewController.CompilerOutput.appendText("  ");
//            compilerOutput = "Line Number-> " + diagnostic.getLineNumber() + "\n"
//            						+ "Code-> " + diagnostic.getCode() + "\n"
//            						+ "Message-> " + diagnostic.getMessage(Locale.ENGLISH) + "\n"
//            						+ "Source-> " + diagnostic.getSource() + "\n";
//            System.out.println(compilerOutput);
            System.out.println("Line Number->" + diagnostic.getLineNumber());
        	System.out.println("Code->" + diagnostic.getCode());
            System.out.println("Message->"
            		+ diagnostic.getMessage(Locale.ENGLISH));
           System.out.println("Source->" + diagnostic.getSource());
           System.out.println(" ");
           
           System.out.flush();
           System.setOut(old);
           compilerOutput += baos.toString();
        }
    }
    
    public void compileAndRunProject(ServerConnection server, String projectID, String mainFile) throws Exception
    {
    	JSONObject params1 = new JSONObject();
    	String category1 = "PROJECT";
    	String action1 = "GETFILES";
    	params1.put("projectID", projectID);
    	
    	JSONObject params2 = new JSONObject();
    	String category2 = "PROJECT";
    	String action2 = "GETLINES";
    	
    	String getFilesReturn = (String) server.sendSingleRequest(category1, action1, params1);
//    	System.out.println(getFilesReturn);
    	
    	Object returnObj;
    	returnObj = new JSONParser().parse(getFilesReturn);
    	JSONArray fileArray = (JSONArray) returnObj;
    	String code = "";
    	List<JavaFileObject> javaFileList = new ArrayList<JavaFileObject>();
    	
    	for(int i = 0; i < fileArray.size(); i++)
    	{
    		JSONObject file = (JSONObject) fileArray.get(i);
    		String fileID = (String) file.get("pfid");
    		String fileName = (String) file.get("pfname");
//    		System.out.println(fileName);
        	params2.put("fileID", fileID);
    		String getLinesReturn = (String) server.sendSingleRequest(category2, action2, params2);
    		
//    		System.out.println(getLinesReturn);
    		
    		returnObj = new JSONParser().parse(getLinesReturn);
        	JSONArray lineArray = (JSONArray) returnObj;
        	for(int j = 0; j < lineArray.size(); j++)
        	{
        		JSONObject line = (JSONObject) lineArray.get(j);
        		String codeLine = (String) line.get("text");
        		code += codeLine + "\n";
//        		System.out.println(code);
        	}
        	
        	javaFileList.add(i, this.new InMemoryJavaFileObject(fileName, code));
        	code = "";

    	}
    	Iterable<? extends JavaFileObject> files = javaFileList;
    	if(compile(files))
    		runIt(mainFile);  	
    }
    
    public String getCodeFromFile(ServerConnection server, String fileID) throws ParseException
    {
		JSONObject params = new JSONObject();
    	String category = "PROJECT";
    	String action = "GETLINES";
    	params.put("fileID",fileID);
    	
    	String returnString = (String) server.sendSingleRequest(category, action, params);
//    	System.out.println(returnString);
    	Object returnObj;
    	returnObj = new JSONParser().parse(returnString);
    	JSONArray lineArray = (JSONArray) returnObj;
    	String code = "";
    	for(int i = 0; i < lineArray.size(); i++)
    	{
    		JSONObject line = (JSONObject) lineArray.get(i);
    		String codeLine = (String) line.get("text");
    		code += codeLine + "\n";
    	}
    	System.out.println(code);
    	return code;

    }
 
    /** java File Object represents an in-memory java source file <br>
     * so there is no need to put the source file on hard disk  **/
    public class InMemoryJavaFileObject extends SimpleJavaFileObject
    {
        private String contents = null;
 
        public InMemoryJavaFileObject(String className, String contents) throws Exception
        {
            super(URI.create("string:///" + className.replace(".java","").replace('.', '/').replace(" ", "")
                             + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }
 
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException
        {
            return contents;
        }
    }
 
 
    /** Compile your files by JavaCompiler */
    public static boolean compile(Iterable<? extends JavaFileObject> files)
    {
    	//initialize new ps for compiler s/f results
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
    	
        compilerOutput = "Compiler output >>\n\n";
    	
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler==null)
        {
            compilerOutput += "Error: JDK not found\n"+
        	"Add JDK to system PATH variable and try again\n";
//            System.out.flush();
            System.setOut(old);

        	return false;
        }
        else
        {
        	// for compilation diagnostic message processing on compilation WARNING/ERROR
        	MyDiagnosticListener c = new MyDiagnosticListener();
        	StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH,null);
        	Iterable options = Arrays.asList("-d", classOutputFolder);
        	JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        	Boolean result = task.call();
        	if (result == true)
        	{
        		System.out.println("Compilation successful");
        	}
            
        	//reset System.out
            System.out.flush();
            System.setOut(old);
            compilerOutput += baos.toString();
            return result;
        }        
    }
 
    /** Run class from the compiled byte code file by URLClassloader */
    public static void runIt(String packageDotClassName)
    {
    	
    	systemOutput = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
        // IMPORTANT: Save the old System.out!
    	PrintStream old = System.out;
        // Tell Java to use your special stream
    	System.setOut(ps);
    	
    	compilerOutput = "Run output >>\n";
    	
    	System.out.println("Running main() of class " + packageDotClassName + " >>\n");
    	
        // Create a File object on the root of the directory
        // containing the class file
        File file = new File(classOutputFolder);
 
        try
        {            
            // Convert File to a URL
            URL url = file.toURL(); // file:/classes
            URL[] urls = new URL[] { url };
 
            // Create a new class loader with the directory
            ClassLoader loader = new URLClassLoader(urls);
            
            // got rid of '.java' extension
            Class thisClass = loader.loadClass(packageDotClassName.replace(".java", ""));
 
            String params[] = null;
            Method mainMethod = thisClass.getMethod("main", String[].class);
 
            // run the the main method on the instance:
            mainMethod.invoke(null, (Object) params);
        }
        catch (MalformedURLException e)
        {
        	e.printStackTrace(System.out);
        } 
        catch (ClassNotFoundException e)
        {
        	e.printStackTrace(System.out);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        System.out.flush();
        System.setOut(old);
        compilerOutput += baos.toString();
    }
 
}
