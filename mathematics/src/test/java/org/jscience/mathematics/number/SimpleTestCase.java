package org.jscience.mathematics.number;

import javolution.testing.TestCase;

/**
 * A very simple testcase where there is no point of separating the validation from the execution - the validation is
 * done right in the {@link #execute()} method. Probably a deviation from the {@link TestCase} philosophy, but I don't
 * know any better.
 * @author hps
 * @since 25.01.2010
 */
public abstract class SimpleTestCase extends TestCase {

    @Override
    public void validate() throws Exception {
        // empty
    }

}
