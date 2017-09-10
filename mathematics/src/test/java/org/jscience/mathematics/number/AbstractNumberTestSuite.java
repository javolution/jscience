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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javolution.lang.MathLib;
import javolution.testing.TestContext;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;

import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.AbstractJavolutionJUnitAdapter;
import org.jscience.util.AbstractTestSuite;
import org.jscience.util.Pair;

/**
 * This class contains tests that can be run for all {@link Number} subclasses. The result of the {@link Number}
 * operation is compared with the result of the same operation on double values, which should give roughly the same
 * result. Of course, this does not check wether all digits are correct, but catches many errors already. You might want
 * to add tests for the full precision.
 * @since 22.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 * @param <T> the type of number to test
 */
public abstract class AbstractNumberTestSuite<T extends Number<T>> extends AbstractJavolutionJUnitAdapter {

    protected final NumberHelper<T> _helper;

    protected AbstractNumberTestSuite(final NumberHelper<T> helper) {
        _helper = helper;
    }

    private List<Pair<Double, T>> _testValues;

    protected final List<Pair<Double, T>> getTestValues() {
        if (null == _testValues) {
            _testValues = new ArrayList<Pair<Double, T>>();
            initTestValues(_testValues);
        }
        return _testValues;

    }

    /** Generates a list of values to test with, along with their value as double. */
    protected abstract void initTestValues(List<Pair<Double, T>> values);

    public void testToString() {
        info("  toString, valueOf(String)");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new AbstractNumberTest<T>("Testing toString / valueOf(String) " + p, p._x, _helper) {
                @Override
                T operation() throws Exception {
                    return _helper.valueOf(p._y.toString());
                }
            });
    }

    public void testDoubleValue() {
        info("  doubleValue");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new AbstractNumberTest<T>("Testing doubleValue " + p, p._x, _helper) {
                @Override
                T operation() throws Exception {
                    // doubleValue is called in AbstractNumberTest#compareresult()
                    return p._y;
                }
            });
    }

    public void testFloatValue() {
        info("  floatValue");
        for (final Pair<Double, T> p : getTestValues())
            if (MathLib.abs(p._x) < Float.MAX_VALUE)
                doTest(new AbstractNumberTest<T>("Testing floatValue " + p, p._x, _helper) {
                    @Override
                    T operation() throws Exception {
                        EPSILON = 1e-7;
                        return _helper.valueOf(p._y.floatValue());
                    }
                });
    }

    public void testByteValue() {
        // we do not check values > 4e15 since we could no longer check the double Value to be integral.
        info("  byteValue");
        for (final Pair<Double, T> p : getTestValues())
            if (Math.abs(p._x) < 4e15 && MathLib.round(p._x) == p._x.doubleValue())
                doTest(new AbstractNumberTest<T>("Testing byteValue " + p, (byte) (p._x.doubleValue() % 256), _helper) {
                    @Override
                    T operation() throws Exception {
                        return _helper.valueOf(p._y.byteValue());
                    }
                });
    }

    public void testShortValue() {
        // we do not check values > 4e15 since we could no longer check the double Value to be integral.
        info("  shortValue");
        for (final Pair<Double, T> p : getTestValues())
            if (Math.abs(p._x) < 4e15 && MathLib.round(p._x) == p._x.doubleValue())
                doTest(new AbstractNumberTest<T>("Testing shortValue " + p, (short) (p._x.doubleValue() % 65536),
                        _helper) {
                    @Override
                    T operation() throws Exception {
                        return _helper.valueOf(p._y.shortValue());
                    }
                });
    }

    public void testLongValue() {
        info("  longValue");
        for (final Pair<Double, T> p : getTestValues())
            if (Math.abs(p._x) < Long.MAX_VALUE)
                doTest(new AbstractNumberTest<T>("Testing longValue " + p, (long) p._x.doubleValue(), _helper) {
                    @Override
                    T operation() throws Exception {
                        return _helper.valueOf(p._y.longValue());
                    }
                });
    }

    public void testPlus() {
        info("  plus");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                // In the case of Long.M*_VALUE we have a problem with the precision of double:
                // (double)Long.MIN_VALUE == (double)Long.MAX_VALUE
                if (p._x != Long.MIN_VALUE && p._x != Long.MAX_VALUE)
                    doTest(new AbstractNumberTest<T>("Testing plus " + p._x + "," + q._x, p._x + q._x, _helper) {
                        @Override
                        T operation() throws Exception {
                            return p._y.plus(q._y);
                        }
                    });
    }

    public void testMinus() {
        info("  minus");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                doTest(new AbstractNumberTest<T>("Testing minus " + p._x + "," + q._x, p._x - q._x, _helper) {
                    @Override
                    T operation() throws Exception {
                        return p._y.minus(q._y);
                    }
                });
    }

    public void testTimes() {
        info("  times");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                doTest(new AbstractNumberTest<T>("Testing times " + p._x + "," + q._x, p._x * q._x, _helper) {
                    @Override
                    T operation() throws Exception {
                        return p._y.times(q._y);
                    }
                });
    }

    public void testCompareTo() {
        info("  compareTo");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        TestContext.assertEquals(p._x.compareTo(q._x), p._y.compareTo(q._y), (p + "," + q));
                    }
                });
    }

    public void testOpposite() {
        info("  opposite");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new AbstractNumberTest<T>("Testing opposite " + p, -p._x, _helper) {
                @Override
                T operation() throws Exception {
                    return p._y.opposite();
                }
            });
    }

    /** The maximum admissible number; at most {@link Double#MAX_VALUE} - we cannot test for more here. */
    protected double getMaxNumber() {
        return Double.MAX_VALUE;
    }

    public void testPow() {
        info("  pow");
        for (final Pair<Double, T> p : getTestValues())
            for (final int exp : new Integer[] { 1, 3, 7, 8, 9 }) {
                final double pow = MathLib.pow(p._x, exp);
                if (getMaxNumber() >= MathLib.abs(pow))
                    doTest(new AbstractNumberTest<T>("Testing pow " + p + ", " + exp, pow, _helper) {
                        @Override
                        T operation() throws Exception {
                            return p._y.pow(exp);
                        }
                    });
            }
    }

    public void testEquals() {
        info("  equals");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        TestContext.assertEquals(p._x.equals(q._x), p._y.equals(q._y), (p + "," + q));
                    }
                });
    }

    public void testIsLargerThan() {
        info("  isLargerThan");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                // In the case of Long.M*_VALUE we have a problem with the precision of double:
                // (double)Long.MIN_VALUE == (double)Long.MAX_VALUE
                if (p._x != Long.MIN_VALUE && q._x != Long.MAX_VALUE) doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        TestContext.assertEquals((MathLib.abs(p._x) > MathLib.abs(q._x)), p._y.isLargerThan(q._y), (p + "," + q));
                    }
                });
    }

    public void testDivide() {
        info("  divide");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                if (0 != q._x)
                    doTest(new AbstractNumberTest<T>("Testing divide " + p._x + "," + q._x, p._x / q._x, _helper) {
                        @Override
                        T operation() throws Exception {
                            return _helper.invokeMethod("divide", p._y, q._y);
                        }
                    });
    }

    public void testAbs() {
        info("  abs");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new AbstractNumberTest<T>("Testing abs " + p, MathLib.abs(p._x), _helper) {
                @Override
                T operation() throws Exception {
                    return _helper.invokeMethod("abs", p._y);
                }
            });
    }

    public void testIsPositive() {
        info("  isPositive");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    TestContext.assertTrue((p._x > 0 == _helper.invokeBooleanMethod("isPositive", p._y)), p.toString());
                }
            });
    }

    public void testIsNegative() {
        info("  isNegative");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    TestContext.assertTrue((p._x < 0 == _helper.invokeBooleanMethod("isNegative", p._y)), p.toString());
                }
            });
    }

    public void testIsZero() {
        info("  isZero");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    TestContext.assertTrue((p._x == 0 == _helper.invokeBooleanMethod("isZero", p._y)), p.toString());
                }
            });
    }

    public void testHashcode() {
        info("  hashcode");
        for (final Pair<Double, T> p : getTestValues())
            for (final Pair<Double, T> q : getTestValues())
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        final int phash = p._y.hashCode();
                        final int qhash = q._y.hashCode();
                        if (p._y.equals(q._y)) {
                            assertEquals(p + "," + q, phash, qhash);
                        }
                    }
                });
    }

    public void testXMLEncoding() {
        info("  XML");
        for (final Pair<Double, T> p : getTestValues())
            doTest(new AbstractNumberTest<T>("Testing XML " + p, p._x, _helper) {
                @Override
                T operation() throws Exception {
                    final StringWriter wr = new StringWriter();
                    final XMLObjectWriter w = XMLObjectWriter.newInstance(wr);
                    w.write(p._y);
                    w.close();
                    final String xml = wr.toString();
                    final StringReader rd = new StringReader(xml);
                    final XMLObjectReader r = XMLObjectReader.newInstance(rd);
                    final Object res = r.read();
                    return (T) res;
                }
            });
    }
}
