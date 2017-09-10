package org.jscience;

import javolution.text.TextBuilder;
import junit.framework.TestCase;

/**
 * Checks for some javolution bugs that can break JScience stuff. These have obviously to be fixed in javolution before
 * things are right. :-)
 * @author hps
 * @since 06.02.2010
 */
public class TestJavolutionBugs extends TestCase {

    public void testTextformat() {
        final long c = 10000000000L;
        final TextBuilder builder = javolution.text.TextBuilder.newInstance();
        assertEquals("" + c, builder.append(c).toString());
    }

}
