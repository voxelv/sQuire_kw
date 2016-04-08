import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
 
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

 
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
	File directory;
    
	private static String classOutputFolder;
    
 
    public Compiler()
    {
    	path = System.getProperty("user.dir") + "\\classes";
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
 
            System.out.println("Line Number->" + diagnostic.getLineNumber());
            System.out.println("code->" + diagnostic.getCode());
            System.out.println("Message->"
                               + diagnostic.getMessage(Locale.ENGLISH));
            System.out.println("Source->" + diagnostic.getSource());
            System.out.println(" ");
        }
    }
 
    /** java File Object represents an in-memory java source file <br>
     * so there is no need to put the source file on hard disk  **/
    public class InMemoryJavaFileObject extends SimpleJavaFileObject
    {
        private String contents = null;
 
        public InMemoryJavaFileObject(String className, String contents) throws Exception
        {
            super(URI.create("string:///" + className.replace('.', '/')
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
    public static void compile(Iterable<? extends JavaFileObject> files)
    {
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", classOutputFolder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        Boolean result = task.call();
        if (result == true)
        {
            System.out.println("Succeeded");
        }
    }
 
    /** Run class from the compiled byte code file by URLClassloader */
    public static void runIt(String packageDotClassName)
    {
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
 
            Class thisClass = loader.loadClass(packageDotClassName);
 
            String params[] = null;
            Method mainMethod = thisClass.getMethod("main", String[].class);
 
            // run the the main method on the instance:
            mainMethod.invoke(null, (Object) params);
        }
        catch (MalformedURLException e)
        {
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
 
}
