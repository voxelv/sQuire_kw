package sq.app.model.editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

import javafx.scene.input.KeyCode;
import sq.app.model.Line;
import sq.app.model.LineDictionary;
import sq.app.model.ServerConnection;

public class EditorCodeArea extends CodeArea{

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
    private int previousLineNumber = -1;
	ServerConnection server = sq.app.MainApp.GetServer();
    //ArrayList<Line> listOfLineObjects = new ArrayList<Line>();
    private int currentFileID = -1;
	private LineDictionary lineDictionary = new LineDictionary();
	private int currentUserID = -1;
	
	public void setUserID(int userID){
		currentUserID = userID;
		lineDictionary.setUserID(userID);
	}

	public EditorCodeArea() {
		super();
    	this.setParagraphGraphicFactory(LineNumberFactory.get(this));

    	this.caretPositionProperty().addListener(event->{
        	updateMyLockedLine();
    		int currentLineNum = this.getCurrentParagraph();
        	if (currentLineNum != this.previousLineNumber){
        		this.previousLineNumber = currentLineNum;
        		this.previousLineText = this.getText(this.previousLineNumber);
    		}
    	});
    	
//    	this.currentParagraphProperty().addListener(event->{
//    		this.doHighlight();    		
//    	});
    	
    	this.plainTextChanges().subscribe(change->{
    		this.doHighlight();    		
        	updateMyLockedLine();
        	sendChangesToServer();
    	});

		this.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
			int p = this.getCurrentParagraph();
        	if (this.lineDictionary.getLockedLines().contains(p) && !event.getCode().isArrowKey())
        	{
        		revertCurrentLine();
    		}
        	if (event.getCode() == KeyCode.ENTER){
        		createNewLine();
        	}
        	else if (event.getCode() == KeyCode.DELETE){
        		char deleteChar = this.getText().charAt(this.caretPositionProperty().getValue()+1);
        		if (deleteChar == '\r' || deleteChar == '\n'){
        			deleteChar = deleteChar;
        		}
        	}
        	else if (event.getCode() == KeyCode.BACK_SPACE){
        		char backChar = this.getText().charAt(this.caretPositionProperty().getValue()-1);
        		if (backChar == '\r' || backChar == '\n'){
        			backChar = backChar;
        		}
        	}
		});
	}
	
	private void sendChangesToServer(){
    	if (this.getCurrentParagraph() != this.previousLineNumber){
    		int currentLineNum = this.getCurrentParagraph();
			String currentLine = "";
			if (this.previousLineNumber != -1){
				currentLine = this.getText(this.previousLineNumber); 
			}
    		if (!new String(currentLine ).equals(this.previousLineText) && this.previousLineNumber != -1){
            	if (this.previousLineNumber  == lineDictionary.getSize()){
            		createNewLine();
            	}
    			try{
        			JSONObject jo = new JSONObject();
            		jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(this.previousLineNumber)));
            		jo.put("text", this.getText(this.previousLineNumber));
                	server.sendSingleRequest("project", "changeLine", jo);
                	this.lineDictionary.updateText(this.previousLineNumber,(this.getText(this.previousLineNumber)));
            	} 
            	catch (Exception e){
            		System.out.println("an exception happened while trying to send line change data");
            	}
    		}
    	}
	}
	
	private void updateMyLockedLine(){
    	if (this.getCurrentParagraph() != this.previousLineNumber){
    		int currentLineNum = this.getCurrentParagraph();
        	try{
        		if (this.getCurrentParagraph() < lineDictionary.getSize()){
                	JSONObject jo = new JSONObject();
            		jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(currentLineNum)));
                	server.sendSingleRequest("project", "lockline", jo);
        		}
        		if (this.previousLineNumber > -1 && this.previousLineNumber < lineDictionary.getSize()){
            		JSONObject jo = new JSONObject();
                	jo.put("lineID", String.valueOf(lineDictionary.getIDfromLine(this.previousLineNumber)));
                	server.sendSingleRequest("project", "unlockline", jo);
            	}
        	} 
        	catch (Exception e){
        		System.out.println("an exception happened trying to send line lock/unclock data");
        	}
    	}
	}
	
	private void revertCurrentLine(){
		int p = this.getCurrentParagraph();
		this.selectLine();
		this.replaceSelection(this.lineDictionary.getLine(p).getText());	        		
	}
	
	private void createNewLine(){
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
		    	server.sendSingleRequest("project", "changeLine", jo);
				jo = new JSONObject();
				jo.put("fileID", String.valueOf(this.currentFileID));
				jo.put("text", beforeCaretText);
				response = server.sendSingleRequest("project", "createLineAtEnd", jo);
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
//				int i1 = Integer.valueOf((String)jo.get("LAST_INSERT_ID()"));
//				int i2 = listOfLineObjects.get(p+1).getID();
//				String s = "";
//				Timestamp t = new Timestamp(0);
				
				Line l = new Line(Integer.valueOf((String)jo.get("LAST_INSERT_ID()")), p+1, currentUserID, lineDictionary.getIDfromLine(p+1),"",new Timestamp(0));
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
		doHighlight();
	}
	public void SetLockedParagraphs(List<Integer> lockedPs){
		if(!lockedPs.equals(lineDictionary.getLockedIDs()))
		{
			int size = lineDictionary.getSize();
			for(int i = 0; i < size; i++){
				lineDictionary.unLockLine(i);
			}
			for (int i : lockedPs){
				lineDictionary.lockLinebyID(i);
			}
    		//this.doHighlight();
		}
	}
	 
	public void doHighlight() {
		setStyleSpans(0, computeHighlighting(this.getText()));
		
		ArrayList<String> styleStr = new ArrayList<String>();
		styleStr.add("islocked");
		for (int line : lineDictionary.getLockedLines()){
			StyleSpansBuilder<Collection<String>> lockedSpansBuilder = new StyleSpansBuilder<Collection<String>>();
			lockedSpansBuilder.add(new StyleSpan(styleStr, this.getParagraph(line).length()));
			this.clearParagraphStyle(line);
			this.setStyleSpans(line, 0, lockedSpansBuilder.create());
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
