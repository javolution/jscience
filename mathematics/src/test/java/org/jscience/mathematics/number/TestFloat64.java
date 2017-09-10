/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.number.util.NumberHelper;

/**
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link Real} and some further tests
 * that are specific to {@link Real}.
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class TestFloat64 extends AbstractFloatTestSuite<Real> {

    /** Sets the needed helper class. */
    public TestFloat64() {
        super(NumberHelper.FLOAT64);
    }

    /**
     * Overridden to do nothing since it has no isZero().
     */
    @Override
    public void testIsPositive() {
        // not there 8-{
    }

    /**
     * Overridden to do nothing since it has no isZero().
     */
    @Override
    public void testIsNegative() {
        // not there 8-{
    }

    /**
     * Overridden to do nothing since it has no isZero().
     */
    @Override
    public void testIsZero() {
        // not there 8-{
    }
}
