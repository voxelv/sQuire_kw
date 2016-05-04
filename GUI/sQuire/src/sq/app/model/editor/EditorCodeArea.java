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
    				if (lineDictionary.HasChanges()){
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
    		});
		});
    	
    	this.caretPositionProperty().addListener(event->{
    		Platform.runLater(new Runnable(){
    			@Override public void run() {
    				doHighlight();    		
                	updateMyLockedLine();
    			}
    		});
    	});
    	
    	
    	this.currentParagraphProperty().addListener(event->{
    		//System.out.println("cur par: " + String.valueOf(this.getCurrentParagraph()));
    		
    		this.previousLineNumber = this.currentLineNumber;
    		if (this.previousLineNumber!= -1){
    			this.previousLineText = this.getText(this.previousLineNumber);
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
    	});
    	
//    	this.plainTextChanges().subscribe(change->{
//    	});

//		this.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
//			int p = this.getCurrentParagraph();
//			this.lastCharEntered = event.getCode();
////			System.out.println("Last Char entered: '" + this.lastCharEntered.getName() + "'");
//        	if (this.lineDictionary.getLockedLines().contains(p) && !event.getCode().isArrowKey())
//        	{
//        		revertLine(this.getCurrentParagraph());
//    		}
////        	if (event.getCode() == KeyCode.ENTER){
////        		createNewLine();
////        	}
////        	else if (event.getCode() == KeyCode.DELETE){
////        		char deleteChar = this.getText().charAt(this.caretPositionProperty().getValue()+1);
////        		if (deleteChar == '\r' || deleteChar == '\n'){
////        			deleteChar = deleteChar;
////        		}
////        	}
////        	else if (event.getCode() == KeyCode.BACK_SPACE){
////        		char backChar = this.getText().charAt(this.caretPositionProperty().getValue()-1);
////        		if (backChar == '\r' || backChar == '\n'){
////        			System.out.println("Killing line #: " + String.valueOf(this.getCurrentParagraph()));
////        			backChar = backChar;
////        			
////        			if (this.lineDictionary.getSize() > this.getCurrentParagraph())
////        				this.removeLineNum(this.getCurrentParagraph());
////        		}
////        	}
//        	
//		});
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
        	new Thread(new Runnable() {
			    @Override
			    public void run(){	    
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
			    		String prevLineText = lineDictionary.getLine(prevLineNum).getText();
			    		String curLineText = "";
			    		if (curLineNum != -1){
			    			curLineText = getText(prevLineNum); 
			    		}
			    		if (curLineNum != prevLineNum){
			        		if (!new String(curLineText).equals(prevLineText) && prevLineNum != -1){
			                	if (prevLineNum == lineDictionary.getSize()){
			                		createNewLine();
			                	}
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
	            		System.out.println("an exception happened while trying to send line change data");
	            	}
			    }
        	}).start();
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
			//insertText(lengthProperty().getValue()-1, "\n"+newText);
		}
		//this.replaceSelection(this.lineDictionary.getLine(p).getText());	        		
	}
	
	private void createNewLine(){
		System.out.println("Creating new line...");
    	JSONObject jo = new JSONObject();
		int p = this.getCurrentParagraph();
    	String beforeCaretText = this.getText(this.getCaretPosition()-this.getCaretColumn(), this.getCaretPosition());
    	String afterCaretText = this.getText(this.getCaretPosition(), this.getCaretPosition()-this.getCaretColumn()+this.getParagraph(p).length());
    	Object response = null;
		if (p==0){
			try{
				jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(p)));
				jo.put("text", afterCaretText);
		    	server.sendSingleRequest("project", "changeLine", jo);
				jo = new JSONObject();
    			jo.put("fileID", String.valueOf(this.currentFileID));
    			jo.put("text", beforeCaretText);
    			response = server.sendSingleRequest("project", "createLineAtHead", jo);
			}
			catch(Exception e){
				System.out.println("error trying to add line to begginning of file");
			}
		}
		else if (p == lineDictionary.getSize()){
			try{
				jo.put("text", beforeCaretText);
				jo.put("fileID", String.valueOf(this.currentFileID));
				response = server.sendSingleRequest("project", "createLineAtEnd", jo);
			}
			catch(Exception e){
				System.out.println("error trying to add line to end of file");
			}
		}
		else if (p == lineDictionary.getSize()-1){
			try{
				jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(p)));
				jo.put("text", afterCaretText);
				server.addRequest("project", "changeLine", jo);

				jo = new JSONObject();
				jo.put("fileID", String.valueOf(this.currentFileID));
				jo.put("text", beforeCaretText);
				server.addRequest("project", "createLineAtEnd", jo);
				
				
				JSONArray multipleResponses = (JSONArray) server.sendRequestBuffer();
				JSONObject t = (JSONObject) multipleResponses.get(1);
				
				response = (Object) ( t.get("result") );
				
			}
			catch(Exception e){
				System.out.println("error trying to add line to end of file");
			}
		}
		else{
			try{
				jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(p)));
				jo.put("text", afterCaretText);
		    	server.sendSingleRequest("project", "changeLine", jo);
				jo = new JSONObject();
				jo.put("nextLineID", String.valueOf(lineDictionary.getIDfromLine(p)));
				jo.put("text", beforeCaretText);
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
				System.out.println("JSONObject: " + jo);
//				int i1 = Integer.valueOf((String)jo.get("LAST_INSERT_ID()"));
//				System.out.println("")
//				int i2 = listOfLineObjects.get(p+1).getID();
//				String s = "";
//				Timestamp t = new Timestamp(0);
				int nextLineID = 0;
				if (p+1 < this.lineDictionary.getSize())
					nextLineID = lineDictionary.getIDfromLine(p+1);
				
				Line l = new Line(Integer.valueOf((String)jo.get("LAST_INSERT_ID()")), p+1, currentUserID, nextLineID,"",new Timestamp(0));
				this.lineDictionary.add(l);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
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
		lineDictionary.resetTo(listOfLines);
		this.previousLineNumber = -1;
		this.currentFileID = fileID;
		this.replaceText(text);
		this.doHighlight();
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
