package sq.app.model.editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import jdk.net.NetworkPermission;
import sq.app.model.Line;
import sq.app.model.LineDictionary;
import sq.app.model.ServerConnection;

public class EditorCodeArea extends CodeArea{

	private KeyCode lastCharEntered;
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
	
    private String previousLineText = "";
    private int currentLineNumber = -1;
    private int previousLineNumber = -1;
	ServerConnection server = sq.app.MainApp.GetServer();
    //ArrayList<Line> listOfLineObjects = new ArrayList<Line>();
    private int currentFileID = -1;
	public LineDictionary lineDictionary = new LineDictionary();
	public int currentUserID = -1;
	public BooleanProperty checkIt = new SimpleBooleanProperty();
	
	public void setUserID(int userID){
		currentUserID = userID;
		lineDictionary.setUserID(userID);
	}

	public EditorCodeArea() {
		super();
    	this.setParagraphGraphicFactory(LineNumberFactory.get(this));
    	this.lastCharEntered = KeyCode.SHIFT;	// innocuous key that won't affect anything
       	checkIt.addListener(change->{
    		Platform.runLater(new Runnable(){
    			@Override public void run() {
    				try{
	    				if (lineDictionary.HasChanges() && isEditable()){
			    			for(Line l : lineDictionary.GetChangeList()){
			    				
			    				int c = getCaretPosition();
			    				try{
			    					String oldText = getText(l.getLineNumber());
			    					if (!oldText.equals(l.getText())){
										int start = 0;
										for (int i = 0; i < l.getLineNumber(); i++){
											start += getText(i).length()+1;
										}
										int end = start + oldText.length();
										replaceText(start, end, l.getText());
										int oldLen = end - start;
										int newLen = l.getText().length();
										c += (newLen - oldLen);
										positionCaret(c);
			    					}
			    				} catch (Exception e){		    					
			    					insertText(lengthProperty().getValue()-1, "\n"+l.getText());
			    				}
								doHighlight();
								//System.out.println("got chg: " + String.valueOf(l.getLineNumber()) +", "+ l.getText());
			    			}
			    		}
    				}
    				catch(Exception e){
    					e.printStackTrace(System.out);
    				}
		    	}
    		});
		});
    	
    	this.caretPositionProperty().addListener(event->{
    		Platform.runLater(new Runnable(){
    			@Override public void run() {
    				try{
    					doHighlight();    		
    					//updateMyLockedLine();
    				}
    				catch(Exception e){
    					e.printStackTrace(System.out);
    				}
    			}
    		});
    	});
    	
    	
    	this.currentParagraphProperty().addListener(event->{
    		try{
    			if (isEditable()){
	    			this.previousLineNumber = this.currentLineNumber;
		    		if (this.previousLineNumber!= -1 && this.previousLineNumber < lineDictionary.getSize()){
		    			this.previousLineText = this.getText(this.previousLineNumber);
		    		}
		    		else if(this.previousLineNumber >= lineDictionary.getSize()){
		    			this.previousLineNumber=-1;
		    		}
		    		this.currentLineNumber = getCurrentParagraph();
		        	if (this.currentLineNumber != this.previousLineNumber){
		    			if (lineDictionary.getLockedLines().contains(previousLineNumber) &&
		    					!getText(previousLineNumber).equals(lineDictionary.getLine(previousLineNumber).getText()))
		    			{
		    				revertLine(previousLineNumber);
		    			}
		    			sendChangesToServer();
		    		}
    			}
    		}
    		catch(Exception e){
				e.printStackTrace(System.out);
    		}
    	});
    	
//    	this.plainTextChanges().subscribe(change->{
//    	});

		this.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
//			int p = this.getCurrentParagraph();
			this.lastCharEntered = event.getCode();
////			System.out.println("Last Char entered: '" + this.lastCharEntered.getName() + "'");
//        	if (this.lineDictionary.getLockedLines().contains(p) && !event.getCode().isArrowKey())
//        	{
//        		revertLine(this.getCurrentParagraph());
//    		}
        	if (event.getCode() == KeyCode.ENTER){
        		int cp = getCaretPosition();
        		int cc = getCaretColumn();
        		createNewLine(cp, cc);
        	}
////        	else if (event.getCode() == KeyCode.DELETE){
////        		char deleteChar = this.getText().charAt(this.caretPositionProperty().getValue()+1);
////        		if (deleteChar == '\r' || deleteChar == '\n'){
////        			deleteChar = deleteChar;
////        		}
////        	}
        	else if (event.getCode() == KeyCode.BACK_SPACE){
        		char backChar = this.getText().charAt(this.caretPositionProperty().getValue()-1);
        		if (backChar == '\r' || backChar == '\n'){
//        			System.out.println("Killing line #: " + String.valueOf(this.getCurrentParagraph()));
        			backChar = backChar;
        			
        			if (this.lineDictionary.getSize() > this.getCurrentParagraph())
        				this.removeLineNum(this.getCurrentParagraph());
        		}
        	}
//        	
		});
	}
	
	private void removeLineNum(int lineNum)
	{
		this.UnlockParagraph(lineNum);
		
		
		JSONObject params = new JSONObject();
		
		int delLineID =  this.lineDictionary.getLine(lineNum).getID();
				
		params.put("lineID", String.valueOf(delLineID) );
		server.addRequest("project", "unlockline", params);
		
		// Sending the command to delete this line...
		// uses the same parameters as unlocking
		server.addRequest("project", "removeline", params);
		
		//System.out.println("Trying to unlock and then remove line: " + String.valueOf(lineNum) + "; ID: " + String.valueOf(delLineID));
		
		// send both requests at once.
		server.sendRequestBuffer();
		
		this.lineDictionary.removeLine(lineNum);
	}
	
	private void sendChangesToServer(){	
		if (isEditable()){
			Platform.runLater(new Runnable() {
			    @Override public void run(){	    
			    	try{
//			    		for (int i = 0; i < lineDictionary.getSize(); i++){
//			    			Line x = lineDictionary.getLine(i);
//			    			System.out.print(x.getLineNumber()+", ");
//			    			System.out.print(x.getID()+", ");
//			    			System.out.print(x.getText()+", ");
//			    			System.out.print(x.getLastEditorID()+", ");
//			    			System.out.print(x.getLocked()+", ");
//			    			System.out.print(x.getTimestamp()+"\n");
//			    		}
			    		//System.out.println(String.valueOf(lineDictionary));
			        	int curLineNum = getCurrentParagraph();
			        	int prevLineNum = previousLineNumber;
			    		String prevLineText = "";
			    		if (prevLineNum != -1 && prevLineNum < lineDictionary.getSize()){
				    		prevLineText = lineDictionary.getLine(prevLineNum).getText();
			    		}
			    		else
			    		{
			    			prevLineNum = -1;
			    		}
			    		String curLineText = "";
			    		if (prevLineNum != -1){
			    			curLineText = getText(prevLineNum); 
			    		}
			    		if (curLineNum != prevLineNum && prevLineNum < lineDictionary.getSize()){
			        		if (!new String(curLineText).equals("") &&
			        				!new String(curLineText).equals(prevLineText) && 
			        				prevLineNum != -1){
//			                	if (prevLineNum == lineDictionary.getSize()){
//			                		createNewLine(getCaretPosition(),getCaretColumn());
//			                	}
	        			JSONObject jo = new JSONObject();
	            		jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(prevLineNum)));
	            		jo.put("text", curLineText);
	                	server.sendSingleRequest("project", "changeLine", jo);
	                	lineDictionary.updateText(prevLineNum, curLineText);
						//System.out.println("snd chg: " + String.valueOf(prevLineNum) +", "+ prevLineText);
			    		    }
					    }
				    }
	            	catch (Exception e){
    					e.printStackTrace(System.out);
	            	}
			    }
        	});
		}
	}
	
	public int GotPositionOfLineID(int id){
		try{
			int line = lineDictionary.getID(id).getLineNumber();
			int start = 0;
			for (int i = 0; i < line; i++){
				start += getText(i).length()+1;
			}
			return start;
		}
		catch(Exception e){
			e.printStackTrace(System.out);
			return -1;
		}
	}
	
	private void updateMyLockedLine(){
		
//		int curLineNum = this.getCurrentParagraph();
//		int prevLineNum = this.previousLineNumber;
//		String curLineText = getText(curLineNum);
//		String prevLineText = lineDictionary.getLine(curLineNum).getText();
//	        	try{
//	        		if (curLineNum < lineDictionary.getSize() &&
//	        				!curLineText.equals(prevLineText)){
//	        			new Thread(new Runnable() {
//	        			    @Override
//	        			    public void run(){
//	        			    	lineDictionary.lockLine(currentLineNumber);
//	                	JSONObject jo = new JSONObject();
//	            		jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(curLineNum)));
//	                	server.sendSingleRequest("project", "lockline", jo);
//	                	System.out.println("    lck " + String.valueOf(curLineNum));
//	        			    }
//	        			    }).start();		 
//	        		}
//	        		if (prevLineNum > -1 && prevLineNum < lineDictionary.getSize() //&&
//	        				//lineDictionary.getLockedLines().contains(prevLineNum)
//	        				){
//	        			new Thread(new Runnable() {
//	        			    @Override
//	        			    public void run(){
//	            		JSONObject jo = new JSONObject();
//	                	jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(prevLineNum)));
//	                	server.sendSingleRequest("project", "unlockline", jo);
//	                	System.out.println("unn lck " + String.valueOf(prevLineNum));
//	        			    }
//        			    }).start();		 
//	        		}
//	        	}
//	        	catch (Exception e){
//	        		System.out.println("an exception happened trying to send line lock/unclock data");
//	        	}
		    }
	
	
	private void revertLine(int line){
		int p = this.getCurrentParagraph();
		int c = getCaretPosition();
		//System.out.println("rev par "+String.valueOf(line));
		try{
			String oldText = getText(line);
			String newText = lineDictionary.getLine(line).getText();
			int start = 0;
			for (int i = 0; i < line; i++){
				start += getText(i).length()+1;
			}
			int end = start + oldText.length();
			replaceText(start, end, newText);
			int oldLen = end - start;
			int newLen = newText.length();
			c += (newLen - oldLen);
		} catch (Exception e){
			e.printStackTrace(System.out);
			//insertText(lengthProperty().getValue()-1, "\n"+newText);
		}
		//this.replaceSelection(this.lineDictionary.getLine(p).getText());	        		
	}
	
	private void createNewLine(int caretPosition, int caretColumn){
		if (isEditable()){
	    	try{
		    	JSONObject jo = new JSONObject();
		    	int curLineNum = getCurrentParagraph();
		    	int prevLineNum = curLineNum-1;
				String prevLineText = "";
				if (prevLineNum != -1){
					prevLineText = getText(prevLineNum);
				}
				String curLineText = "";
				if (curLineNum != -1){
					curLineText = getText(curLineNum); 
				}
		    	String beforeCaretText = getText(getCaretPosition()-getCaretColumn(), getCaretPosition());
		    	String afterCaretText = getText(getCaretPosition(), getCaretPosition()-getCaretColumn()+getParagraph(curLineNum).length());
		    	Object response = null;
//				if (curLineNum==0){
//					try{
//						System.out.println("new line at head");
//						jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(curLineNum)));
//						jo.put("text", beforeCaretText);
//				    	server.sendSingleRequest("project", "changeLine", jo);
//						jo = new JSONObject();
//		    			jo.put("fileID", String.valueOf(currentFileID));
//		    			jo.put("text", afterCaretText);
//		    			response = server.sendSingleRequest("project", "createLineAtHead", jo);
//					}
//					catch(Exception e){
//						System.out.println("error trying to add line to begginning of file");
//					}
//				}
//				else 
		    	if (curLineNum >= lineDictionary.getSize()-1){
					try{
						System.out.println("new line at tail");
						jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(curLineNum)));
						jo.put("text", beforeCaretText);
				    	server.sendSingleRequest("project", "changeLine", jo);
						jo.put("text", afterCaretText);
						jo.put("fileID", String.valueOf(currentFileID));
						response = server.sendSingleRequest("project", "createLineAtEnd", jo);
					}
					catch(Exception e){
						System.out.println("error trying to add line to end of file");
					}
				}
				else{
					try{
						System.out.println("new line");
						jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(curLineNum)));
						jo.put("text", beforeCaretText);
				    	server.sendSingleRequest("project", "changeLine", jo);
						jo = new JSONObject();
						jo.put("nextLineID", String.valueOf(lineDictionary.getLine(curLineNum).getNextID()));
						jo.put("text", afterCaretText);
						response = server.sendSingleRequest("project", "createLine", jo);
					}
					catch(Exception e){
						System.out.println("error trying to add line in middle of file");
					}
				}
				if (response != null){
					JSONArray ja;
					try {
						ja = (JSONArray)new org.json.simple.parser.JSONParser().parse((String)response);
						jo = (JSONObject)ja.get(0);
						int newLineID = Integer.valueOf((String)jo.get("LAST_INSERT_ID()"));
						int nextLineID = 0;
						if (curLineNum+1 < lineDictionary.getSize()){
							nextLineID = lineDictionary.getIDfromLine(curLineNum+1);
						}
						Line l = new Line(newLineID, curLineNum+1, currentUserID, nextLineID,afterCaretText,new Timestamp(0));
						lineDictionary.add(l);
						lineDictionary.getLine(curLineNum).setNextID(newLineID);
						for (int i = 0; i < lineDictionary.getSize(); i++){
 			    			Line x = lineDictionary.getLine(i);
 			    			System.out.print(x.getLineNumber()+", ");
 			    			System.out.print(x.getID()+", ");
 			    			System.out.print(x.getText()+", ");
 			    			System.out.print(x.getLastEditorID()+", ");
 			    			System.out.print(x.getLocked()+", ");
 			    			System.out.print(x.getTimestamp()+"\n");
 			    		}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
	    	}catch(Exception e){
				e.printStackTrace();
	    	}
		}
	}
	
	// this function will need to update the server
	// probably create file
	// then insert on line at a time while updating the server?
	public void CreateNewFileFromImportedText(String newFileName, String newFileText, int projectID, int directoryID){
    	ServerConnection server = sq.app.MainApp.GetServer();
    	JSONObject jo = new JSONObject();
		jo.put("projectID", projectID);
		jo.put("dirID", directoryID);
		jo.put("fileName", newFileName);
    	Object o = server.sendSingleRequest("project", "createFile", jo);	
    	if (o!=null){
	    	JSONArray ja = (JSONArray)o;
	    	if (ja!= null){
		    	jo = (JSONObject)ja.get(0);
		    	if (ja!= null){
			    	int newFileID = (int)jo.get("fileID");
		    	
			    	ArrayList<String> fileLines = new ArrayList<String>(Arrays.asList(newFileText.split("\r|\n")));
			    	for (String line : fileLines){
			    		jo = new JSONObject();
						jo.put("fileID", newFileID);
						jo.put("text", line);
				    	server.sendSingleRequest("project", "createLineAtEnd", jo);
			    	}
		    	}
	    	}
    	}
	}
	
	public Timestamp GetLatestEditTime(){
		return Line.GetLatestGreatestEditTime();
	}
	
	public int GetLineIDFromIndex(int index){
		return lineDictionary.getIDfromLine(this.getCurrentParagraph());
	}
	
	public int GetLineIndexFromID(int id){
		return lineDictionary.getLinefromID(id);
	}
	
    public int GetFileID(){
    	return currentFileID;
	}

	public void ReplaceText(String text, List<Line> listOfLines, int fileID){
		try{
			lineDictionary.resetTo(listOfLines);
			this.previousLineNumber = -1;
			this.currentFileID = fileID;
			Platform.runLater(new Runnable() {
			    @Override
			    public void run(){	  
			    	setEditable(false);
			    	clear();
					replaceText(text);
					doHighlight();
					setEditable(true);
			    }
	    	});
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	public void InsertLine(int textPos, String text){
		Platform.runLater(new Runnable() {
		    @Override
		    public void run(){	  
				try{
					int c = getCaretPosition();
					insertText(getLength()-2, "\n"+text);
					positionCaret(c);//-text.length()-2);
				}
				catch(Exception e){
					e.printStackTrace(System.out);						
				}
		    }
		});
	}
	
	public void LockParagraph(int paragraphNumber){
		lineDictionary.lockLine(paragraphNumber);
	}
	public void UnlockParagraph(int paragraphNumber){
		lineDictionary.unLockLine(paragraphNumber);
	//	doHighlight();
	}
	public void SetLockedParagraphs(List<Integer> lockedPs){
//		if(!lockedPs.equals(lineDictionary.getLockedIDs()))
//		{
//			int size = lineDictionary.getSize();
//			for(int i = 0; i < size; i++){
//				lineDictionary.unLockLine(i);
//			}
//			for (int i : lockedPs){			
//				lineDictionary.lockLinebyID(i);
//			}
//    		//this.doHighlight();
//		}
	}
	 
	public void doHighlight() {
		setStyleSpans(0, computeHighlighting(this.getText()));
		
		ArrayList<String> styleStr = new ArrayList<String>();
		styleStr.add("islocked");
		for (int line : lineDictionary.getLockedLines()){
			if (lineDictionary.getLine(line).getLastEditorID()!=currentUserID){
				StyleSpansBuilder<Collection<String>> lockedSpansBuilder = new StyleSpansBuilder<Collection<String>>();
				lockedSpansBuilder.add(new StyleSpan(styleStr, this.getParagraph(line).length()));
				this.clearStyle(line);
				//this.clearParagraphStyle(line);
				this.setStyleSpans(line, 0, lockedSpansBuilder.create());
			}
		}
  	}

    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
