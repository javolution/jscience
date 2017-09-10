package org.jscience.util;

import org.junit.Before;

import javolution.testing.JUnitContext;
import javolution.testing.TestCase;
import javolution.testing.TestContext;

import static javolution.testing.TestContext.*;

public abstract class AbstractJavolutionJUnitAdapter extends  junit.framework.TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JUnitContext.enter();
    }

    /** Feeds the test to the {@link TestContext}. This method is here to provide a hook to do other things here. */
    protected void doTest(final TestCase test) {
        try {
            TestContext.run(test);
        } catch (Throwable e) {
            // try { // for easier debugging a retry
            // test.setUp();
            // test.execute();
            // test.validate();
            // test.tearDown();
            // } catch (Exception e2) {}
            e.printStackTrace();
            if (e instanceof Error) throw (Error) e;
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        JUnitContext.exit();
    }

}
