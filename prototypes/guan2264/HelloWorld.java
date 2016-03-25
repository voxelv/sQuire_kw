/* Feng Guan
 * File Management prototype
 * March 24, 2016
 * CS 383
 */
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
  			String desktop = System.getProperty ("user.home") /*+ "/Desktop/"*/;
  			String currPos = desktop;
    		Boolean isExit = false;
			Boolean exitPositionMenu = false;

    		while(!isExit){
    			System.out.println("***Welcome to the File Management Menu***");
    			System.out.println("1. Set Current Position");
    			System.out.println("2. Print Directory");
    			System.out.println("3. File Manager");
    			System.out.println("4. Exit");
    			System.out.println("Current position: "+currPos);
				System.out.print("Enter your choice: ");
				
	    		Scanner reader = new Scanner(System.in);
				int mainInput = reader.nextInt();		
//				Thread.sleep(100);
				switch(mainInput){
					case 1:
						exitPositionMenu = false;
						while(!exitPositionMenu){

    					System.out.println("***Set Position Menu***");
    					System.out.println("1. Move to Sub Directory");
    					System.out.println("2. Back to Previous Directory");
    					System.out.println("3. Back to Main Menu");
    					System.out.println("Current position: "+currPos);

						System.out.print("Enter your choice: ");
						int subMenu = reader.nextInt();	
						switch(subMenu){
							case 1:
								File folder = new File(currPos);
								File[] listOfFiles = folder.listFiles();
								String[] tempDirectory = new String[listOfFiles.length];
								int directoryNum = 0;
								for (int i = 0; i < listOfFiles.length; i++) {
	      							if (listOfFiles[i].isDirectory()) {
	        							tempDirectory[directoryNum] = listOfFiles[i].getName();
	      								directoryNum++;
	        							System.out.println( + directoryNum + " - " + listOfFiles[i].getName());
	      							}
	    						}
    							System.out.println("Do you want to move to sub directory?");
								System.out.print("Y/N? ");
								String moveSubDir = reader.next();	
	    						switch(moveSubDir){
	    							case "Y": case "y":
	    								System.out.print("Type in the number before the sub directory you want to move: ");
	    								int desireNum = reader.nextInt();
	    								currPos = currPos + "\\"+ tempDirectory[desireNum -1];
	    								break;
	    							case "N": case "n":
	    								System.out.println("Back to Set Position Menu");
	    								break;
	    							default:
	    								System.out.println("Please enter the valid character.");
										break;
	    						}






	    						break;
							case 2:
								String temp = currPos.substring(0, currPos.lastIndexOf("\\"));
								currPos = temp;
								break;
							case 3:
								exitPositionMenu = true;
								break;
							default:
								System.out.println("Please enter the valid number.");
								break;
						}
						}


						break;
					case 2:
						File folder = new File(currPos);
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

    					System.out.println("***File Manager Menu***");
    					System.out.println("Current position: "+currPos);
						System.out.println("1. Create");
						System.out.println("2. Move");
						System.out.println("3. Remove");
						System.out.println("4. Copy");
						System.out.println("5. Paste");
						System.out.println("6. Rename");

    					System.out.println("7. Back to Main Menu");

						System.out.print("Enter your choice: ");
						int menuInput = reader.nextInt();		
		
						switch(menuInput){
							case 1:
							System.out.print("Enter the filename: ");
							String fileName = reader.next();
	
		    					File file = new File(desktop+fileName+"");
		    					if (file.createNewFile()){
		    						System.out.println("---File "+fileName+" Is Created---");
		    					}else{
		    	    				System.out.println("---File "+fileName+" Already Exists---");
		    					}
	
	
								break;
							case 2:

	
								break;
							case 3:
								System.out.print("Enter the filename: ");
								String deleteName = reader.next();
	
		    					File deletefile = new File(desktop+deleteName+"");
	    						if(deletefile.delete()){
	    							System.out.println("---File " + deletefile.getName() + " Is Deleted---");
	    						}else{
	    							System.out.println("---File Delete Failed---");
	    						}
	
								break;
							case 7:
								System.out.println("Back to Main Menu");
								break;


							default:
								System.out.println("Please enter the valid number.");
								break;
						}
						break;
					case 4:
						isExit = true;
						System.out.println("Exit the file management.");
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
