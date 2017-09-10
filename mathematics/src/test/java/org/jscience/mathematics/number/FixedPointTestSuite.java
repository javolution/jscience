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
import static javolution.testing.TestContext.assertEquals;
import static javolution.testing.TestContext.assertTrue;

import java.util.List;

import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.Pair;

import javolution.context.LocalContext;
import javolution.lang.MathLib;
import javolution.testing.TestCase;
import javolution.testing.TestContext;

/**
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link FixedPoint} and some further
 * tests that are specific to {@link FixedPoint}. <br>
 * We omit getExponent, getSignificand, times(long) since these are trivial.
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class FixedPointTestSuite extends AbstractFloatTestSuite<FixedPoint> {

    /** Sets the needed helper class. */
    public FixedPointTestSuite() {
        super(NumberHelper.FIXEDPOINT);
    }

    /**
     * We add a couple of values with different precision.
     * @see org.jscience.mathematics.number.AbstractFloatTestSuite#initTestValues(java.util.List)
     */
    @Override
    protected void initTestValues(List<Pair<Double, FixedPoint>> values) {
        super.initTestValues(values);
        values.add(Pair.make(0.7234938, FixedPoint.valueOf("0.7234938")));
        values.add(Pair.make(0.7234938, FixedPoint.valueOf("0.72349380000000000000000000000000000000")));
    }

    public void testConstants() {
        info(" constants");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(FixedPoint.valueOf(1), FixedPoint.ONE);
                assertEquals(FixedPoint.valueOf(0), FixedPoint.ZERO);
                assertTrue(FixedPoint.NaN.isNaN());
            }
        });
    }
    
    @Override
    public void testDivide() {
        info("  divide");
        for (final Pair<Double, FixedPoint> p : getTestValues())
            for (final Pair<Double, FixedPoint> q : getTestValues())
                // for very small quotients the result is too unexact to verify
                if (0 != q._x && MathLib.abs(p._x / q._x) > 1e-10)
                    doTest(new AbstractNumberTest<FixedPoint>("Testing divide " + p._x + "," + q._x, p._x / q._x, _helper) {
                        @Override
                        FixedPoint operation() throws Exception {
                            return _helper.invokeMethod("divide", p._y, q._y);
                        }
                    });
    }
    
    @Override
    public void testInverse() {
        info("  inverse");
        for (final Pair<Double, FixedPoint> p : getTestValues()) {
            if (0 != p._x && MathLib.abs(p._x) < 1e8) {
                doTest(new AbstractNumberTest<FixedPoint>("Testing inverse " + p, 1.0 / p._x, _helper) {
                    @Override
                    FixedPoint operation() throws Exception {
                        return _helper.invokeMethod("inverse", p._y);
                    }
                });
            }
        }
    }

    public void testInverse2() {
        info("  inverse");
        for (final Pair<Double, FixedPoint> p : getTestValues()) {
            if (0 != p._x && MathLib.abs(p._x) < 1e8) {
                doTest(new AbstractNumberTest<FixedPoint>("Testing inverse " + p, 1.0 / p._x, _helper) {
                    @Override
                    FixedPoint operation() throws Exception {
                        try {
                            LocalContext.enter();
                            FixedPoint.setFractionalDigits(23);
                            return _helper.invokeMethod("inverse", p._y);
                        } finally {
                            LocalContext.exit();
                        }
                    }
                });
            }
        }
    }

    public void testRound() {
        info("  round");
        for (final Pair<Double, FixedPoint> p : getTestValues()) {
            doTest(new AbstractNumberTest<FixedPoint>("Testing round " + p, MathLib.round(p._x), _helper) {
                @Override
                FixedPoint operation() throws Exception {
                    final LargeInteger rounded = p._y.round();
                    return FixedPoint.valueOf(rounded, 0);
                }
            });
        }
    }

    public void testSetDigits() {
        info("  setDigits");
        for (final Pair<Double, FixedPoint> p : getTestValues()) {
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    FixedPoint v1 = _helper.valueOf(1.123);
                    try {
                        LocalContext.enter();
                        FixedPoint.setFractionalDigits(50);
                        FixedPoint v2 = v1.reciprocal();
                        final int dl = v2.getSignificand().digitLength();
                        TestContext.assertTrue(50 == dl, "" + dl);
                    } finally {
                        LocalContext.exit();
                    }
                    // now we should have a different digitlength
                    FixedPoint v2 = v1.reciprocal();
                    final int dl = v2.getSignificand().digitLength();
                    TestContext.assertTrue(50 != dl, "" + dl);
                }
            });
        }
    }
}
