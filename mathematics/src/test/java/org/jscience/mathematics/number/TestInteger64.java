/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import javolution.lang.MathLib;

import org.jscience.mathematics.number.Integer64;
import org.jscience.mathematics.number.util.NumberHelper;

/**
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link Integer64} and some further tests
 * that are specific to {@link Integer64}.
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class TestInteger64 extends AbstractIntegerTestSuite<Integer64> {

    /** Sets the needed helper class. */
    public TestInteger64() {
        super(NumberHelper.INTEGER64);
    }

    @Override
    protected double getMaxNumber() {
        return MathLib.pow(2, 63);
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
