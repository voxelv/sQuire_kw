package sq.app.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import sq.app.model.editor.EditorCodeArea;

public class MainViewController implements Initializable{
	
	URL fxmlFileLocation;
	ResourceBundle resources;
	Scene scene;

	@FXML
    private StackPane editorStackPane;
    
    @FXML
    private EditorCodeArea editorCodeArea;
    
    @FXML
    public void initialize(){
    	System.out.println("test");
    	initialize(fxmlFileLocation, resources);// This method is called by the FXMLLoader when initialization is complete
    }
    
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert editorCodeArea != null : "fx:id=\"editorCodeArea\" was not injected: check your FXML file 'MainView.fxml'.";
        
        // initialize your logic here: all @FXML variables will have been injected
        
        editorCodeArea.doHighlight();
        
        editorCodeArea.richChanges().subscribe(change -> {
            editorCodeArea.doHighlight();
        });
        
        editorCodeArea.selectedTextProperty().addListener((observable, oldvalue, newvalue) -> {
        	System.out.println("Selected text is now: \"" + newvalue + "\"");
        	System.out.println("Interval: " + editorCodeArea.getSelection().getStart() + " to " + editorCodeArea.getSelection().getEnd());
        	
        });
        
        editorCodeArea.caretPositionProperty().addListener((observable, oldvalue, newvalue) -> {
        	//System.out.println("Caret Line: " + this.getCurrentParagraph() + " Caret Index: "+ newvalue);
        	//System.out.println("Scene: " + editorCodeArea.getScene() != null);
        });
        
        editorCodeArea.setOnKeyReleased(event->{
        	System.out.print(event.getCode());
        });
        
        editorCodeArea.selectionProperty().addListener((observable, oldvalue, newvalue) -> {
        	if (newvalue.getLength()>0)
        	{
        		System.out.println("Selection: " + newvalue);
        	}
        });
               
//        int parCounter = 0;
//        for(Iterator<Paragraph<Collection<String>, Collection<String>>> par = this.getParagraphs().iterator(); par.hasNext();)
//        {
//        	parCounter++;
//        	Paragraph<Collection<String>, Collection<String>> item = par.next();
//        	System.out.println(Integer.toString(parCounter) + item.getText());
//        }
        
        editorCodeArea.currentParagraphProperty().addListener(change ->{
        	
        	if (editorCodeArea.getCurrentParagraph() != editorCodeArea.prevLineNum)
        		{
            		if (editorCodeArea.getText(editorCodeArea.prevLineNum) != editorCodeArea.prevLine)
            		{
            			if (editorCodeArea.getText(editorCodeArea.prevLineNum) == "")
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", editorCodeArea.prevLineNum, editorCodeArea.prevLine, "DELETED");
            			}
            			else
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", editorCodeArea.prevLineNum, editorCodeArea.prevLine, editorCodeArea.getText(editorCodeArea.prevLineNum));
            			}
            		}
            		editorCodeArea.prevLineNum = editorCodeArea.getCurrentParagraph();
            		editorCodeArea.prevLine = editorCodeArea.getText(editorCodeArea.prevLineNum);
    		}
        });
    }

}
