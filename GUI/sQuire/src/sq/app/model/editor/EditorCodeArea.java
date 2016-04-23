package sq.app.model.editor;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.*;
import javafx.scene.input.KeyCode;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.json.simple.JSONObject;

import javafx.stage.Stage;
import sq.app.model.Line;
import sq.app.model.ServerConnection;

public class EditorCodeArea extends CodeArea implements KeyListener{

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
    private static final String ISLOCKED_PATTERN =  "" ;
    
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
//            + "|(?<ISLOCKED>" + ISLOCKED_PATTERN + ")"
    );
	
//    private static final String sampleCode = String.join("\n", new String[] {
//            "package com.example;",
//            "",
//            "import java.util.*;",
//            "",
//            "public class Foo extends Bar implements Baz {",
//            "",
//            "    /*",
//            "     * multi-line comment",
//            "     */",
//            "    public static void main(String[] args) {",
//            "        // single-line comment",
//            "        for(String arg: args) {",
//            "            if(arg.length() != 0)",
//            "                System.out.println(arg);",
//            "            else",
//            "                System.err.println(\"Warning: empty string as argument\");",
//            "        }",
//            "    }",
//            "",
//            "}"
//        });
	
    public String prevLine="";
    public int prevLineNum=-1;
    
	public EditorCodeArea() {
		super();

      this.plainTextChanges().subscribe(change->{
	      this.doHighlight();
	  });

//        this.setOnKeyPressed(event->{
//        	KeyCode c = event.getCode();
//        	if (this.getCurrentParagraph()==2 && !event.getCode().isArrowKey())
//        	{
//        		event.consume();
//        		Boolean b = event.isConsumed();
//        		b=b;
//        	}
//        });
        this.currentParagraphProperty().addListener(change ->{       	
        	if (this.getCurrentParagraph() != this.prevLineNum)
        		{
        			String currentLine = "";
        			if (this.prevLineNum != -1){
        				this.getText(this.prevLineNum); 
        			}
            		if (currentLine != this.prevLine &&
                			this.prevLineNum != -1)
            		{
                    	ServerConnection server = sq.app.MainApp.GetServer();
            			JSONObject jo = new JSONObject();
                		jo.put("lineID", lineArray.get(this.getCurrentParagraph()).getID());
                		jo.put("text", this.getText(this.getCurrentParagraph()));
                    	server.sendSingleRequest("Project", "changeLine", jo);

            			if (this.getText(this.prevLineNum) == "")
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", this.prevLineNum, this.prevLine, "DELETED");
            			}
            			else
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", this.prevLineNum, this.prevLine, this.getText(this.prevLineNum));
            			}
            		}
                	try{
                    	ServerConnection server = sq.app.MainApp.GetServer();
                		if (this.getCurrentParagraph() < lineArray.size()){
	                    	JSONObject jo = new JSONObject();
	                		jo.put("lineID", lineArray.get(this.getCurrentParagraph()).getID());
	                    	server.sendSingleRequest("project", "lockline", jo);
                		}
                		if (this.prevLineNum > -1 && this.prevLineNum < lineArray.size()){
	                		JSONObject jo = new JSONObject();
	                    	jo.put("lineID", lineArray.get(this.prevLineNum).getID());
	                    	server.sendSingleRequest("project", "unlockline", jo);
                    	}
                	} catch (Exception e){
                		System.out.println("an exception happened trying to send line lock/unclock data");
                		//do nothing
                	}
            		this.prevLineNum = this.getCurrentParagraph();
            		this.prevLine = this.getText(this.prevLineNum);
    		}
        });
        
//        this.caretPositionProperty().addListener((observable, oldvalue, newvalue) -> {
//	    });


    	this.setParagraphGraphicFactory(LineNumberFactory.get(this));
		//this.replaceText(sampleCode);
		//this.LockParagraph(2);
		addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> {
        	if (this.getCurrentParagraph()==2 && !event.getCode().isArrowKey())
        	{
        		this.selectLine();
        		this.replaceSelection("import java.util.*;");
        		
        		
//        		event.consume();
  //      		Boolean b = event.isConsumed();
    //    		b=b;
        	}
		});
		//EventHandlerHelper.exclude(handler, subHandler)(handler)
//		EventHandlerHelper.installAfter(this.onKeyPressedProperty(), event->{
//        	if (this.getCurrentParagraph()==2 && !event.getCode().isArrowKey())
//        	{
//        		event.consume();
//        		Boolean b = event.isConsumed();
//        		b=b;
//        	}
//		});
	//	this.onKeyTypedProperty().addListener(event->keyTyped((KeyEvent) event));

	}
	
	public int GetLineIDFromIndex(int index){
		return lineArray.get(this.getCurrentParagraph()).getID();
	}
	
	
    ArrayList<Line> lineArray = new ArrayList<Line>();
    private int currentFileID = -1;
    public int GetFileID(){
    	return currentFileID;
	}

	public void ReplaceText(String text, List<Line> listOfLines, int fileID){
		lineArray = (ArrayList<Line>)listOfLines;
		this.replaceText(text);
		this.prevLineNum = -1;
		this.currentFileID = fileID;
	}
	
	private ArrayList<Integer> lockedParagraphs = new ArrayList<Integer>();
	//StyleSpansBuilder<Collection<String>> lockedSpansBuilder = new StyleSpansBuilder<>();
	
	public void LockParagraph(int paragraphNumber){
		lockedParagraphs.add(paragraphNumber);
		ArrayList<String> styleStr = new ArrayList<String>();
		styleStr.add("islocked");
		//this.clearStyle(2);
//		this.clearParagraphStyle(2);
		StyleSpansBuilder<Collection<String>> lockedSpansBuilder = new StyleSpansBuilder<>();
		lockedSpansBuilder.add(new StyleSpan(styleStr, this.getParagraph(2).length()));
		//StyleSpan newStyle = new StyleSpan(newStyle,this.getParagraph(2).length());
		//this.getParagraph(2).restyle(0, this.getParagraph(2).length(), styleStr);
		//this.getParagraph(2).restyle(styleStr);
		this.setStyleSpans(2, 0, lockedSpansBuilder.create());
		//StyleSpans
	}
	public void UnlockParagraph(int paragraphNumber){
		lockedParagraphs.remove(paragraphNumber);
		doHighlight();
	}
	
	 @Override
	 public void keyPressed(KeyEvent e) {
	        if (this.getCurrentParagraph()==2){
	//        	e.h
	        }
    }
	 
	 @Override
	public void keyReleased(KeyEvent e) {
		 
	 }
	
	 //@Override
	 public void keyTyped(KeyEvent event){
			if (this.getCurrentParagraph()==2 && !(event.isActionKey())) {
				event.consume();
			}
	 }

	// KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
//		  @Override
//		  public boolean dispatchKeyEvent(final KeyEvent e) {
//		    if (e.getID() == KeyEvent.KEY_TYPED) {
//		      System.out.println(e);
//		    }
//		    // Pass the KeyEvent to the next KeyEventDispatcher in the chain
//		    return false;
//		  }
	//	};
	 
//	public boolean dispatchKeyEvent (KeyEvent event) {
//	
//		
//		if (this.getCurrentParagraph()==2 && !(event.isActionKey())) {
//		//MY_GLOBAL_ACTION.actionPerformed(null);
//		return true;
//	}
//	return false;
//	}
	 
	public void doHighlight() {
		setStyleSpans(0, computeHighlighting(this.getText()));
		
		ArrayList<String> styleStr = new ArrayList<String>();
		styleStr.add("islocked");
//		StyleSpans<Collection<String>> ss = lockedSpansBuilder.create();
		for (int i =0; i < lockedParagraphs.size(); i++){
			StyleSpansBuilder<Collection<String>> lockedSpansBuilder = new StyleSpansBuilder<Collection<String>>();
			lockedSpansBuilder.add(new StyleSpan(styleStr, this.getParagraph(lockedParagraphs.get(i)).length()));
			this.clearParagraphStyle(lockedParagraphs.get(i));
			this.setStyleSpans(lockedParagraphs.get(i), 0, lockedSpansBuilder.create());
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
