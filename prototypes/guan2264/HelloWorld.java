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
				System.out.println("2. Print Directory");
				System.out.println("3. Remove File");
				System.out.print("Enter your choice: ");
				
	    		Scanner reader = new Scanner(System.in);
				int menuInput = reader.nextInt();		
//				Thread.sleep(100);
				String desktop = System.getProperty ("user.home") + "/Desktop/";

				switch(menuInput){
					case 1:
						System.out.print("Enter the filename: ");
						String fileName = reader.next();

	    				File file = new File(desktop+fileName+"");
	    				if (file.createNewFile()){
	    					System.out.println("File "+fileName+" is created!");
	    				}else{
	    	    			System.out.println("File "+fileName+" already exists.");
	    				}


						break;
					case 2:
						File folder = new File(desktop);
						File[] listOfFiles = folder.listFiles();
	    	    		System.out.println("-----Printing-----");
	    	    		
	    	    		

    					for (int i = 0; i < listOfFiles.length; i++) {
      						if (listOfFiles[i].isFile()) {
        						System.out.println("-File " + listOfFiles[i].getName());
      						} else if (listOfFiles[i].isDirectory()) {
        						System.out.println("-Directory " + listOfFiles[i].getName());
      						}
    					}
	    	    		System.out.println("---End of print---");

						break;
					case 3:
						System.out.print("Enter the filename: ");
						String deleteName = reader.next();

	    				File deletefile = new File(desktop+deleteName+"");
    					if(deletefile.delete()){
    						System.out.println("File " + deletefile.getName() + " is deleted!");
    					}else{
    						System.out.println("File delete failed.");
    					}

						break;
					default:
						System.out.println("Please enter the valid number.");
						break;
				}



    		}





	      
    		} catch (IOException e) {
	    		e.printStackTrace();
			}
    }
}
