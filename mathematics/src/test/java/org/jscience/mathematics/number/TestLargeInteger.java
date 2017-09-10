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

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import javolution.testing.TestCase;
import javolution.testing.TestContext;

import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.Pair;

/**
 * <p>
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link LargeInteger} and some further
 * tests that are specific to {@link LargeInteger}.
 * </p>
 * <p>
 * We do not test the trivial methods plus(long), minus(long).
 * </p>
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class TestLargeInteger extends AbstractIntegerTestSuite<LargeInteger> {

    private final Random rnd = new Random();

    /** Sets the {@link NumberHelper}. */
    public TestLargeInteger() {
        super(NumberHelper.LARGEINTEGER);
    }

    /**
     * Extends by some large test values out of the general integer range.
     * @see org.jscience.mathematics.number.AbstractIntegerTestSuite#initTestValues(java.util.List)
     */
    @Override
    protected void initTestValues(final List<Pair<Double, LargeInteger>> values) {
        super.initTestValues(values);
        for (final String s : new String[] { "9876543212345678985432123456789876543210",
                "-9876543212345678985432123456789876543210", "100000000000000000000" })
            values.add(Pair.make(Double.valueOf(s), _helper.valueOf(s)));
        values.add(Pair.make(Double.valueOf(Integer.MIN_VALUE), _helper.valueOf(Integer.MIN_VALUE)));
        values.add(Pair.make(Double.valueOf(Integer.MAX_VALUE), _helper.valueOf(Integer.MAX_VALUE)));
        values.add(Pair.make(Double.valueOf(Integer.MAX_VALUE + 1L), _helper.valueOf(Integer.MAX_VALUE + 1L)));
        values.add(Pair.make(Double.valueOf(Long.MIN_VALUE), _helper.valueOf(Long.MIN_VALUE)));
        values.add(Pair.make(Double.valueOf(Long.MAX_VALUE), _helper.valueOf(Long.MAX_VALUE)));
    }

    public void testConstants() {
        info(" constants");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(LargeInteger.valueOf(1), LargeInteger.ONE);
                assertEquals(LargeInteger.valueOf(0), LargeInteger.ZERO);
            }
        });
    }

    public void testDigitLength() {
        info(" digitLength");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(1, LargeInteger.ZERO.digitLength());
                assertEquals(1, LargeInteger.ONE.digitLength());
                long val = 10;
                int len = 2;
                while (val < Long.MAX_VALUE / 10) {
                    final LargeInteger l = LargeInteger.valueOf(val);
                    TestContext.assertEquals(len, l.digitLength(), l.toString());
                    TestContext.assertEquals(len, l.plus(LargeInteger.ONE).digitLength(), l.toString());
                    TestContext.assertEquals((len - 1), l.plus(LargeInteger.ONE.opposite()).digitLength(), l.toString());
                    val *= 10;
                    len++;
                }
            }
        });
    }

    public void testBitLength() {
        info("  bitLength");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(0, LargeInteger.ZERO.bitLength());
                assertEquals(1, LargeInteger.ONE.bitLength());
                long val = 2;
                int len = 2;
                while (val < Long.MAX_VALUE / 2) {
                    final LargeInteger l = LargeInteger.valueOf(val);
                    TestContext.assertEquals(len, l.bitLength(), l.toString());
                    TestContext.assertEquals(len, l.plus(_helper.getOne()).bitLength(), l.toString());
                    TestContext.assertEquals((len - 1), l.plus(_helper.getOne().opposite()).bitLength(), l.toString());
                    val *= 2;
                    len++;
                }
            }
        });
    }

    public void testHexadecimal() {
        info(" hexadecimal");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals("6a8af7ae5a6759aa49fa43b8b4cd49cf655e41795ba270e613a557", LargeInteger.valueOf(
                        "43829182938374882394282398298374848392872392839238754323223782743").toText(16).toString());
                assertEquals("43829182938374882394282398298374848392872392839238754323223782743", LargeInteger.valueOf(
                        "6a8af7ae5a6759aa49fa43b8b4cd49cf655e41795ba270e613a557", 16).toString());
            }
        });
        for (final int radix : new int[] { 2, 10, 16, 36 })
            for (final Pair<Double, LargeInteger> p : getTestValues())
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        final String val = p._y.toText(radix).toString();
                        TestContext.assertEquals(p._y, LargeInteger.valueOf(val, radix), ("hexadecimal (" + radix + ") " + p + " : "
                        + val));
                    }
                });
    }

    public void testBigInteger() {
        info(" biginteger");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    final String val = p._y.toString();
                    final BigInteger bi = new BigInteger(val);
                    TestContext.assertEquals(p._y, LargeInteger.valueOf(bi), ("" + p));
                }
            });
    }

    public void testToByteArray() {
        info(" toByteArray");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    final byte[] buf = new byte[1000]; // large enough for
                    // everything.
                    for (int i = 0; i < buf.length; ++i)
                        buf[i] = 42; // must not matter
                    final int offset = 16;
                    final int num = p._y.toByteArray(buf, offset);
                    TestContext.assertEquals(p._y, LargeInteger.valueOf(buf, offset, num), ("" + p));
                }
            });
    }

    public void testCompareToLong() {
        info(" compareToLong");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            for (final Pair<Double, LargeInteger> q : getTestValues()) {
                final long ql = q._x.longValue();
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        final Double qd = new Double(ql);
                        final int expected = p._x.compareTo(qd);
                        final int res = p._y.compareTo(ql);
                        TestContext.assertEquals(expected, res, (p + "," + q));
                    }
                });
            }
    }

    public void testEqualsLong() {
        info(" equalsLong");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            for (final Pair<Double, LargeInteger> q : getTestValues())
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        final boolean expected = p._x.equals(new Double(q._x.longValue()));
                        final boolean res = p._y.equals(q._x.longValue());
                        TestContext.assertEquals(expected, res, (p + "," + q));
                    }
                });
    }

    public void testTimesLong() {
        info(" timesLong");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            for (final Pair<Double, LargeInteger> q : getTestValues()) {
                final long ql = q._x.longValue();
                doTest(new AbstractNumberTest<LargeInteger>("Testing timesLong " + p + "," + ql, ql * p._x, _helper) {
                    @Override
                    LargeInteger operation() throws Exception {
                        return p._y.times(ql);
                    }
                });
            }
    }

    public void testDivideLong() {
        info(" divideInt");
        for (final Pair<Double, LargeInteger> p : getTestValues())
            for (final Pair<Double, LargeInteger> q : getTestValues()) {
                final int qi = q._x.intValue();
                if (0 != qi)
                    doTest(new AbstractNumberTest<LargeInteger>("Testing divideInt " + p + "," + qi, p._x, _helper) {
                        @Override
                        LargeInteger operation() throws Exception {
                            return p._y.times(qi).divide(qi);
                        }
                    });
            }
    }

    /** This is a probabilistic test - it micht fail very rarely */
    public void testGCD() {
        info(" gcd");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                for (int i = 0; i < 10; ++i) {
                    final BigInteger bi1 = makePrime(133);
                    final BigInteger bi2 = makePrime(95);
                    final BigInteger bi3 = makePrime(52);
                    final LargeInteger f = _helper.valueOf(bi3);
                    TestContext.assertEquals(f, _helper.valueOf(bi1).times(f).gcd(_helper.valueOf(bi2).times(f)), (bi1 + "\n" + bi2
                    + "\n" + bi3));
                }
            }
        });
    }

    private BigInteger makePrime(final int bits) {
        BigInteger res;
        do
            res = new BigInteger(bits, rnd).nextProbablePrime();
        while (!res.isProbablePrime(20));
        return res;
    }

    /** Test multiplication of very big numbers. */
    public void testKaratsuba() {
        info(" karatsuba");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                final long p1 = 8147;
                final long p2 = 9433;
                final LargeInteger pl1 = LargeInteger.valueOf(p1);
                final LargeInteger pl2 = LargeInteger.valueOf(p2);
                long p = p1;
                LargeInteger pl = pl1;
                for (int i = 0; i < 10; ++i) {
                    pl = pl.times(pl);
                    p = p * p % p2;
                }
                final LargeInteger pls = pl.mod(pl2);
                // we check the result is correct modulo p2
                assertEquals(pls.longValue(), p);
            }
        });
    }

    public void testSqrt() {
        info("  sqrt");
        final LargeInteger n = LargeInteger.valueOf(9);
        n.sqrt();
        for (final Pair<Double, LargeInteger> p : getTestValues())
            if (p._x >= 0) executesqrt(p._y);
        for (int i = 0; i < 64; ++i)
            executesqrt(LargeInteger.valueOf(i));
    }

    private void executesqrt(final LargeInteger s) {
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                // k^2 <= p._y < (k + 1)^2
                final LargeInteger k = s.sqrt();
                TestContext.assertTrue((!k.isNegative()), (s + " -> " + k));
                final LargeInteger k1 = k.plus(1);
                TestContext.assertTrue((!s.isLessThan(k.times(k))), (s + " -> " + k));
                TestContext.assertTrue(s.isLessThan(k1.times(k1)), (s + " -> " + k));
            }
        });
    }

    /** FIXME modInverse is broken but I haven't yet found the bug. */
    public void testModInverse() {
        info("  modInverse");
        LargeInteger.valueOf(8).gcd(LargeInteger.valueOf("9876543212345678985432123456789876543210"));
        for (final Pair<Double, LargeInteger> p : getTestValues())
            for (final Pair<Double, LargeInteger> m : getTestValues()) {
                if (!LargeInteger.ZERO.equals(p._y) && m._y.isGreaterThan(LargeInteger.ONE)
                        && p._y.gcd(m._y).abs().equals(LargeInteger.ONE)) doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        final LargeInteger res = p._y.modInverse(m._y);
                        final LargeInteger pres = p._y.times(res).mod(m._y);
                        TestContext.assertTrue(LargeInteger.ONE.equals(pres), (p + "," + m + " -> " + res + " : " + pres));
                    }
                });
            }
    }

    /** Tests for bug https://jscience.dev.java.net/issues/show_bug.cgi?id=102 */
    public void testBug102() {
        info("  bug102");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                final String P = "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF";
                final String X = "45a9d2f1bc91fe103bf997089f8d640f28e56a13fd0d24dc8912f85b20d1f2f3";
                final String Y = "fa524f482cc22eb69a395b9cce557b8b026ef82186181299f081f0938292ba94";
                final String Z = "f5c4ecdbbbde6621dc07a9c6bba7ee6222a571bb66dfbc420a6b7a1c5a4cc800";

                System.out.println("BigInteger result:");
                final BigInteger bP = new BigInteger(P, 16);
                final BigInteger bX = new BigInteger(X, 16);
                final BigInteger bY = new BigInteger(Y, 16);
                final BigInteger bZ = new BigInteger(Z, 16);

                final BigInteger bT1 = bZ.pow(2).modInverse(bP);
                final BigInteger bT2 = bZ.pow(3).modInverse(bP);

                // System.out.println("t1: " + bT1.toString(16));
                // System.out.println("t2: " + bT2.toString(16));
                // System.out.println("x:  " +
                // bX.multiply(bT1).mod(bP).toString(16));
                // System.out.println("y:  " +
                // bY.multiply(bT2).mod(bP).toString(16));
                //
                // System.out.println("LargeInteger result:");
                final LargeInteger lP = LargeInteger.valueOf(bP);
                final LargeInteger lX = LargeInteger.valueOf(bX);
                final LargeInteger lY = LargeInteger.valueOf(bY);
                final LargeInteger lZ = LargeInteger.valueOf(bZ);

                final LargeInteger lT1 = lZ.pow(2).modInverse(lP);
                final LargeInteger lT2 = lZ.pow(3).modInverse(lP);

                // System.out.println("t1: " + lT1.toText(16));
                // System.out.println("t2: " + lT2.toText(16));
                // System.out.println("x:  " +
                // lX.times(lT1).mod(lP).toText(16));
                // System.out.println("y:  " +
                // lY.times(lT2).mod(lP).toText(16));

                assertEquals(bP.toString(), lP.toString());
                assertEquals(bX.toString(), lX.toString());
                assertEquals(bY.toString(), lY.toString());
                assertEquals(bZ.toString(), lZ.toString());
                assertEquals(bZ.pow(2).toString(), lZ.pow(2).toString());
                // FIXME assertEquals(bT1.toString(), lT1.toString());
                // FIXME assertEquals(bT2.toString(), lT2.toString());
            }
        });
    }

    public void testKarazuba() {
        doTest(new TestCase() {
            LargeInteger val;

            @Override
            public void execute() throws Exception {
                val = LargeInteger.valueOf(9 * 11 * 13);
                for (int i = 0; i < 6; ++i)
                    val = val.times(val);
            }

            @Override
            public void validate() throws Exception {
                assertEquals("karatzuba failure: " + val.doubleValue(), 1.0304501857918365E199, val.doubleValue(),
                        1E190);
                assertEquals(LargeInteger.ZERO, val.mod(LargeInteger.valueOf(9)));
                assertEquals(LargeInteger.ZERO, val.mod(LargeInteger.valueOf(11)));
                assertEquals(LargeInteger.ZERO, val.mod(LargeInteger.valueOf(13)));
            }

            @Override
            public void tearDown() {
                super.tearDown();
                val = null;
            }
        });
    }

}
