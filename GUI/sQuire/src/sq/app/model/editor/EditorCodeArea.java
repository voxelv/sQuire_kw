package sq.app.model.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javafx.stage.Stage;

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
	
    private static final String sampleCode = String.join("\n", new String[] {
            "package com.example;",
            "",
            "import java.util.*;",
            "",
            "public class Foo extends Bar implements Baz {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
        });
	
    public String prevLine="";
    public int prevLineNum=0;
    
	public EditorCodeArea() {
		super();
    	this.setParagraphGraphicFactory(LineNumberFactory.get(this));
		this.replaceText(sampleCode);
		this.LockParagraph(2);
	}
	
	private ArrayList<Integer> lockedParagraphs = new ArrayList<Integer>();
	
	public void LockParagraph(int paragraphNumber){
		lockedParagraphs.add(paragraphNumber);
	}
	
	public void initialize(Stage stage) {
	}
	
	public void doHighlight() {
		setStyleSpans(0, computeHighlighting(this.getText(), lockedParagraphs));
        
		ArrayList<String> newStyle = new ArrayList<String>();
		newStyle.add("islocked");
		//StyleSpans
		
		for (int index = 0; index < this.getParagraphs().size(); index++) {
    			//end += this.getParagraph(index).length();
    			if (lockedParagraphs.indexOf(index)>-1)
    			{
    				Collection<String> o = this.getStyleAtPosition(index, 0);
    				StyleSpans<Collection<String>> s = this.getStyleSpans(index);
    				//this.clearStyle(index);
    		        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    		        spansBuilder.add(new StyleSpan(newStyle,this.getParagraph(index).length()-1));
    		        this.setStyleSpans(index, 1, spansBuilder.create());
//    				s.getStyleSpan(0).getStyle().
  //  				this.setStyleSpans(index, 0, newStyle);
//					this.clearParagraphStyle(index);
  //  				this.getParagraph(index).restyle(newStyle);
//    	            spansBuilder.add(Collections.emptyList(), end - begin);
  //  	            spansBuilder.add(Collections.singleton("is-locked"), end - begin);
    			}
    			//begin += this.getParagraph(index).length();
		}
	}

    private static StyleSpans<Collection<String>> computeHighlighting(String text, ArrayList<Integer> lockedLines) {
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
