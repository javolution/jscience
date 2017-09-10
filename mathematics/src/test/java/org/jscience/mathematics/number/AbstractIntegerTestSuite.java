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
 * Common tests for all integer {@link Number} classes.
 * @author hps
 * @since 22.12.2008
 * @param <T>
 */
public abstract class AbstractIntegerTestSuite<T extends Number<T>> extends AbstractNumberTestSuite<T> {

    /** Sets the {@link NumberHelper} to use. */
    AbstractIntegerTestSuite(NumberHelper<T> helper) {
        super(helper);
    }

    /**
     * Generates some integer point testvalues suitable in range for all integer classes.
     */
    @Override
    protected void initTestValues(List<Pair<Double, T>> values) {
        values.add(Pair.make(0.0, _helper.getZero()));
        values.add(Pair.make(1.0, _helper.getOne()));
        values.add(Pair.make(0.0, _helper.valueOf(12345).minus(_helper.valueOf(12345))));
        for (double d : new double[] { 0, 1, -1, 8, 33, 12345678, -12345678, 87654321 }) {
            values.add(Pair.make(d, _helper.valueOf(d)));
        }
        /* for (double d = 2; d < 100; d++) {
            values.add(Pair.make(d, _helper.valueOf(d)));
            values.add(Pair.make(-d, _helper.valueOf(-d)));
        } */
    }

    /**
     * This is different for integers than for Floating points.
     */
    @Override
    public void testDivide() {
        info("  divide");
        for (final Pair<Double, T> p : getTestValues()) {
            for (final Pair<Double, T> q : getTestValues()) {
                if (0 != q._x) {
                    double expected = p._x / q._x;
                    expected = 0 > expected ? -MathLib.floor(-expected) : MathLib.floor(expected);
                    // insufficient precision of double :
                    if (Long.MAX_VALUE == p._x && Long.MIN_VALUE == q._x) expected = 0;
                    doTest(new AbstractNumberTest<T>("Testing divide " + p._x + "," + q._x, expected, _helper) {
                        @SuppressWarnings("unchecked")
                        @Override
                        T operation() throws Exception {
                            Class<T> cl = (Class<T>) p._y.getClass();
                            return (T) cl.getMethod("divide", cl).invoke(p._y, q._y);
                        }
                    });
                }
            }
        }
    }

}
