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
import static javolution.testing.TestContext.*;
import static javolution.testing.TestContext.*;

import java.util.ArrayList;
import java.util.List;

import javolution.lang.MathLib;
import javolution.testing.TestCase;
import javolution.testing.TestContext;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.number.util.DoubleInterval;
import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.Pair;

/**
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link Real} and some further tests that
 * are specific to {@link Real}.
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class TestReal extends AbstractFloatTestSuite<Real> {

    /** Sets the {@link NumberHelper}. */
    public TestReal() {
        super(NumberHelper.REAL);
    }

    @Override
    protected void initTestValues(List<Pair<Double, Real>> values) {
        super.initTestValues(values);
        List<Pair<Double, Real>> copy = new ArrayList<Pair<Double, Real>>(values);
        for (Pair<Double, Real> pair : copy) {
            if (pair._y.isExact()) {
                Real approxy = new Real(pair._y.getSignificand(), pair._y.getExponent(), 1);
                if (approxy.getPrecision() > 10) values.add(Pair.make(pair._x, approxy));
            }
        }
    }

    /**
     * Overridden to do nothing since it has no isZero().
     */
    @Override
    public void testIsZero() {
        // not there 8-{
    }

    public void testRound() {
        info("  round");
        for (final Pair<Double, Real> p : getTestValues()) {
            doTest(new AbstractNumberTest<Real>("Testing round " + p, MathLib.round(p._x), _helper) {
                @Override
                Real operation() throws Exception {
                    return Real.valueOf(p._y.round(), 0, 0);
                }
            });
        }
    }

    /**
     * With {@link Real} we cannot take the square root of an inexact zero.
     * @see org.jscience.mathematics.number.AbstractFloatTestSuite#testSqrt()
     */
    @Override
    public void testSqrt() {
        info("  sqrt");
        for (final Pair<Double, Real> p : getTestValues()) {
            if (0 < p._x || p._y == _helper.getZero()) {
                doTest(new AbstractNumberTest<Real>("Testing sqrt " + p, MathLib.sqrt(p._x), _helper) {
                    @Override
                    Real operation() throws Exception {
                        return _helper.invokeMethod("sqrt", p._y);
                    }
                });
            }
        }
    }

    @Override
    public void testDivide() {
        info("  divide");
        for (final Pair<Double, Real> p : getTestValues())
            for (final Pair<Double, Real> q : getTestValues())
                if (0 != q._x)
                    doTest(new AbstractNumberTest<Real>("Testing divide " + p._x + "," + q._x, p._x / q._x, _helper) {
                        @Override
                        Real operation() throws Exception {
                            return p._y.divide(q._y);
                        }
                    });
    }

    public void testDivideLong() {
        info("  divide");
        for (final Pair<Double, Real> p : getTestValues()) {
            for (final Pair<Double, Real> r : getTestValues()) {
                final long l = r._x.longValue();
                if (0 != l) doTest(new AbstractNumberTest<Real>("Testing divide " + p._x + "," + l, p._x / l, _helper) {
                    @Override
                    Real operation() throws Exception {
                        return p._y.divide(l);
                    }
                });
            }
        }
    }

    public void testErrorFromString() {
        info("  errorfromstring");
        final Real rerr = Real.valueOf(1000, -3, 1);
        for (final Pair<Double, Real> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    Real r = p._y.times(rerr);
                    final String rstr = r.toString();
                    Real r1 = Real.valueOf(rstr);
                    assertEquals(r1, r);
                }
            });
    }

    @Override
    public void testCompareTo() {
        info("  compareTo");
        for (final Pair<Double, Real> p : getTestValues())
            for (final Pair<Double, Real> q : getTestValues())
                if (p._y.isExact() == q._y.isExact()) doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        TestContext.assertEquals(p._x.compareTo(q._x), p._y.compareTo(q._y), (p + "," + q));
                    }
                });
    }

    @Override
    public void testEquals() {
        info("  equals");
        for (final Pair<Double, Real> p : getTestValues())
            for (final Pair<Double, Real> q : getTestValues())
                if (p._y.isExact() == q._y.isExact()) doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        TestContext.assertEquals(p._x.equals(q._x), p._y.equals(q._y), (p + "," + q));
                    }
                });
    }

    @Override
    public void testIsLargerThan() {
        info("  isLargerThan");
        for (final Pair<Double, Real> p : getTestValues())
            for (final Pair<Double, Real> q : getTestValues())
                // In the case of Long.M*_VALUE we have a problem with the precision of double:
                // (double)Long.MIN_VALUE == (double)Long.MAX_VALUE
                if (p._x != Long.MIN_VALUE && q._x != Long.MAX_VALUE && p._y.isExact() == q._y.isExact())
                    doTest(new SimpleTestCase() {
                        @Override
                        public void execute() {
                            TestContext.assertEquals((MathLib.abs(p._x) > MathLib.abs(q._x)), p._y.isLargerThan(q._y),
                                    (p + "," + q));
                        }
                    });
    }

    public void testCrossCheckWithDoubleInterval() {
        doTest(new SimpleTestCase() {
            @Override
            public void execute() throws Exception {
                Real r1 = Real.valueOf("(3.141500000±0.0001)");
                DoubleInterval i1 = DoubleInterval.valueOf(3.1414, 3.1416);
                Real r2 = Real.valueOf("(0.618000000±0.001)");
                DoubleInterval i2 = DoubleInterval.valueOf(0.617, 0.619);
                assertEquivalent(r1, i1);
                assertEquivalent(r2, i2);
                assertEquivalent(r1.plus(r2), i1.plus(i2));
                assertEquivalent(r1.times(r2), i1.times(i2));
                assertEquivalent(r1.divide(r2), i1.divide(i2));
                assertEquivalent(r1.minus(r2), i1.minus(i2));
            }
        });
    }

    private static final double EPSILON = 1e-3;

    protected void assertEquivalent(Real r, DoubleInterval i) {
        TestContext.assertTrue(Math.abs(r.minimum().doubleValue() - i.lower())< EPSILON, r + " minumum differs from " + i + " : deviation ");
        TestContext.assertTrue(Math.abs(r.maximum().doubleValue() - i.upper())< EPSILON, r + " maximum differs from " + i + " : deviation ");
   }
}
