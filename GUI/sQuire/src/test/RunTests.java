/* RunTests.java
 * 
 * Created by: Tim
 * On: Apr 21, 2016 at 5:05:04 PM
*/

package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class RunTests {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(TestEditorCodeArea.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
} 