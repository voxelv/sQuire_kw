import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.lang.String;


public class HelloWorld 
{
    public static void main( String[] args )
    {	
    	try {

    		Scanner reader = new Scanner(System.in);
			System.out.println("Enter the filename: ");
			String userInput = reader.next();

	    	File file = new File("c:\\"+userInput+"");
	      
	    	if (file.createNewFile()){
	    	System.out.println("File "+userInput+" is created!");
	    	}else{
	    	    System.out.println("File "+userInput+" already exists.");
	    	}
	      
    		} catch (IOException e) {
	    		e.printStackTrace();
			}
    }
}
