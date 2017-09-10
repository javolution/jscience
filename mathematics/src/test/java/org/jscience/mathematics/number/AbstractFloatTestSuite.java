/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import static javolution.context.LogContext.info;

import java.util.List;

import javolution.lang.MathLib;

import org.jscience.mathematics.number.Number;
import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.Pair;

/**
 * Additional tests for all floating point {@link Number} classes that are not covered in
 * {@link AbstractNumberTestSuite}.
 * @since 22.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 * @param <T> the type of number to test
 */
public abstract class AbstractFloatTestSuite<T extends Number<T>> extends AbstractNumberTestSuite<T> {

    AbstractFloatTestSuite(NumberHelper<T> helper) {
        super(helper);
    }

    /**
     * Generates some floating point testvalues suitable in range for all floating point classes.
     */
    @Override
    protected void initTestValues(List<Pair<Double, T>> values) {
        values.add(Pair.make(0.0, _helper.getZero()));
        values.add(Pair.make(1.0, _helper.getOne()));
        values.add(Pair.make(0.0, _helper.valueOf(1234.9384).minus(_helper.valueOf(1234.9384))));
        for (double d : new double[] { 0.0, 1.0, 0.01, -0.02, 0.1, 0.9, -0.1, -0.9, 1.1, 10, 100, 1234.5678,
                -1234.5678, -9876.5432, 1000000000.0, 2.0003 , 394239234954323349.0 }) {
            values.add(Pair.make(d, _helper.valueOf(d)));
        }
    }

    public void testInverse() {
        info("  inverse");
        for (final Pair<Double, T> p : getTestValues()) {
            if (0 != p._x) {
                doTest(new AbstractNumberTest<T>("Testing inverse " + p, 1.0 / p._x, _helper) {
                    @Override
                    T operation() throws Exception {
                        return _helper.invokeMethod("inverse", p._y);
                    }
                });
            }
        }
    }

    public void testSqrt() {
        info("  sqrt");
        for (final Pair<Double, T> p : getTestValues()) {
            if (0 <= p._x) {
                doTest(new AbstractNumberTest<T>("Testing sqrt " + p, MathLib.sqrt(p._x), _helper) {
                    @Override
                    T operation() throws Exception {
                        return _helper.invokeMethod("sqrt", p._y);
                    }
                });
            }
        }
    }

}
