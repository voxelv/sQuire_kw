import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.lang.String;
import java.lang.Thread;


public class HelloWorld 
{
    public static void main( String[] args )
    {	
    	try {
    		Boolean isExit = false;
    		while(!isExit){
    			System.out.println("***File Manager***");
				System.out.println("1. Create File");
				System.out.println("2. Move File");
				System.out.println("3. Remove File");
				System.out.print("Enter your choice: ");
				
	    		Scanner reader = new Scanner(System.in);
				int menuInput = reader.nextInt();		
//				Thread.sleep(1000);

				switch(menuInput){
					case 1:
						System.out.println("Enter the filename: ");
						String fileName = reader.next();
						String desktop = System.getProperty ("user.home") + "/Desktop/";

	    				File file = new File(desktop+fileName+"");
	      
	    				if (file.createNewFile()){
	    				System.out.println("File "+fileName+" is created!");
	    				}else{
	    	    			System.out.println("File "+fileName+" already exists.");
	    				}


						break;
					default:
						break;
				}



    		}





	      
    		} catch (IOException e) {
	    		e.printStackTrace();
			}
    }
}
