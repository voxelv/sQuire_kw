package sQuire_editor;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

public class DiffCodeArea extends CodeArea{
	
	public DiffCodeArea() {
		super();
		System.out.println("DiffCodeArea creation");
	}

}
