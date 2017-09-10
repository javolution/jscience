/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import static javolution.testing.TestContext.assertEquals;
import static javolution.testing.TestContext.assertTrue;

import java.lang.reflect.InvocationTargetException;

import javolution.lang.MathLib;
import javolution.testing.TestCase;
import javolution.testing.TestContext;

import org.jscience.mathematics.number.util.NumberHelper;

/**
 * A testcase that executes an operation and compares the {@link Number#doubleValue()} of the result with a given
 * expected result. If the expected result is 0 we check that the result should be less than {@link #EPSILON}, otherwise
 * the quotient should differ by at most {@link #EPSILON} from 1.
 * @since 22.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 * @param <T> the type of number to test
 */
public abstract class AbstractNumberTest<T extends Number<T>> extends TestCase {

    /** The maximum allowable relative difference of the result from the expected result. */
    protected double EPSILON = 1e-9;

    final double _expected;
    final NumberHelper<T> _helper;
    final String _description;
    T _value;
    Exception _exception;

    /**
     * Sets the expected values.
     * @param description a description that can be printed in failures etc.
     * @param helper helper to be used in the test
     */
    public AbstractNumberTest(final String description, final double expected, final NumberHelper<T> helper) {
        _expected = expected;
        _helper = helper;
        _description = description;
    }

    /**
     * Calls {@link #operation()} and catches Exceptions for later validation.
     * @see javolution.testing.TestCase#execute()
     */
    @Override
    public final void execute() {
        try {
            _value = operation();
        } catch (final InvocationTargetException e) {
            _exception = (Exception) e.getTargetException();
        } catch (final Exception e) {
            _exception = e;
        }
    }

    /**
     * Checks that there was no exception and that the result is approximately equal to the expected result.
     * @see javolution.testing.TestCase#validate()
     */
    @Override
    public final void validate() {
        if (null != _exception) _exception.printStackTrace();
        assertEquals(null, _exception, getName().toString());
        assertTrue(null != _value, getName() + ": no value received");
        compareresult();
    }

    /**
     * Compares {@link #_value} and {@link #_expected} after normalizing them with {@link #_suite}'s
     * {@link AbstractNumberTestSuite#normalize(Number)} or {@link AbstractNumberTestSuite#normalizeExpected(double)}.
     * The result of the comparison is {@link TestContext#assertTrue(boolean, String)}ed.
     */
    void compareresult() {
        assertTrue(equal(_value.doubleValue(), _expected), getName().toString() + " but got " + _value.doubleValue());
        
    }

    /** Verifies whether two numbers are approximately equal. */
    private boolean equal(final double result, final double expected) {
        boolean equal;
        if (0 == _expected) equal = EPSILON > MathLib.abs(result);
        else equal = EPSILON > MathLib.abs(result / expected - 1);
        return equal;
    }

    @Override
    public String getName() {
        return _description + " expecting " + _expected;
    }
    
    /**
     * We free the calculated resources to avoid memory problems.
     * @see javolution.testing.TestCase#tearDown()
     */
    @Override
    public void tearDown() {
        _value = null;
        _exception = null;
        super.tearDown();
    }

    /** Should return the value of the operation to test and set _expected to the expected value. */
    abstract T operation() throws Exception;
}
