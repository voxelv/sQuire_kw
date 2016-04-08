package sQuire_editor;

import java.io.Console;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;

public class JavaKeywords extends Application {

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


    public static void main(String[] args) {
        launch(args);
    }

    private String prevLine="";
    private int prevLineNum=0;
    
    @Override
    public void start(Stage primaryStage) {
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.replaceText(0, 0, sampleCode);
        
        codeArea.richChanges().subscribe(change -> {
            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
        });
        
        codeArea.selectedTextProperty().addListener((observable, oldvalue, newvalue) -> {
        	System.out.println("Selected text is now: \"" + newvalue + "\"");
        	System.out.println("Interval: " + codeArea.getSelection().getStart() + " to " + codeArea.getSelection().getEnd());
        	
        });
        
        codeArea.caretPositionProperty().addListener((observable, oldvalue, newvalue) -> {
        	//System.out.println("Caret Line: " + codeArea.getCurrentParagraph() + " Caret Index: "+ newvalue);
        });
        
        codeArea.setOnKeyReleased(event->{
        	System.out.print(event.getCode());
        });
        
        codeArea.selectionProperty().addListener((observable, oldvalue, newvalue) -> {
        	if (newvalue.getLength()>0)
        	{
        		System.out.println("Selection: " + newvalue);
        	}
        });
               
//        int parCounter = 0;
//        for(Iterator<Paragraph<Collection<String>, Collection<String>>> par = codeArea.getParagraphs().iterator(); par.hasNext();)
//        {
//        	parCounter++;
//        	Paragraph<Collection<String>, Collection<String>> item = par.next();
//        	System.out.println(Integer.toString(parCounter) + item.getText());
//        }
        
        codeArea.currentParagraphProperty().addListener(change ->{
        	if (codeArea.getCurrentParagraph() != prevLineNum)
        		{
            		if (codeArea.getText(prevLineNum) != prevLine)
            		{
            			if (codeArea.getText(prevLineNum) == "")
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", prevLineNum, prevLine, "DELETED");
            			}
            			else
            			{
            				System.out.printf("%d : '%s' -> '%s'\n", prevLineNum, prevLine, codeArea.getText(prevLineNum));
            			}
            		}
        		prevLineNum = codeArea.getCurrentParagraph();
    			prevLine = codeArea.getText(prevLineNum);
    		}
        });
        
        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(codeArea)), 600, 400);
        scene.getStylesheets().add(JavaKeywords.class.getResource("resources/java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Demo");
        primaryStage.show();
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
      
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
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