/* TestEditorCodeArea.java
 * 
 * Created by: Tim
 * On: Apr 21, 2016 at 10:30:35 AM
*/

package test;

import static org.junit.Assert.*;

import java.util.Collection;

import org.fxmisc.richtext.StyleSpans;
import org.junit.Test;

import sq.app.model.editor.EditorCodeArea;

public class TestEditorCodeArea {
	
	@Test
	public void test_computeHighlighting() {
		System.out.println("Testing EditorCodeArea.computeHighlighting");
		
		EditorCodeArea eca = new EditorCodeArea();

		Object sscs = eca.computeHighlighting("");
		boolean isSSCS = sscs instanceof StyleSpans;
		assertEquals(isSSCS, true);
		
		sscs = eca.computeHighlighting("public class Empty {}");
		isSSCS = sscs instanceof StyleSpans;
		assertEquals(isSSCS, true);
		
		sscs = eca.computeHighlighting(
				"package this.package;\n\n"
				+ "import java.util.*;\n\n"
				+ "public class Foo extends Bar {\n\n"
				+ "public static void main(String[] args) {\n\n"
				+ "System.out.println(\"Hello World\");\n\n"
				+ "}\n\n"
				+ "}");
		isSSCS = sscs instanceof StyleSpans;
		assertEquals(isSSCS, true);
	}
	
	@Test
	public void test_doHighlight() {
		System.out.println("Testing EditorCodeArea.doHighlight");
		EditorCodeArea eca = new EditorCodeArea();
		
		Object sscs1 = eca.getStyleSpans(0, eca.getParagraphs().size() - 1);
		eca.replaceText("");
		try {
			eca.doHighlight(); // This is what's being tested
		}
		catch (IndexOutOfBoundsException e) {
			assertFalse("EditorCodeArea.doHighlight() causes IndexOutOfBoundsException", true);
		}
		
		Object sscs2 = eca.getStyleSpans(0, eca.getParagraphs().size() - 1);
		assertFalse("Ensure that the stylespans changed.", sscs2.equals(sscs1));
	}
}
