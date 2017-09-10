package org.jscience.mathematics.number;

import static javolution.context.LogContext.info;
import static javolution.testing.TestContext.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jscience.mathematics.number.util.NumberHelper;
import org.jscience.util.Pair;

import javolution.context.LocalContext;
import javolution.lang.MathLib;
import javolution.testing.TestCase;

/**
 * Tests for {@link ModuloInteger}. <br>
 * The tests consist of some tests that do not set a modulus - this checks for obvious bugs - and some thests that do
 * really use the modulus. We override a couple of tests of our super classes since ModuloInteger does not have the
 * corresponding functions.
 * @author hps
 * @since 01.02.2009
 */
public class TestModuloInteger extends AbstractIntegerTestSuite<ModuloInteger> {

    public TestModuloInteger() {
        super(NumberHelper.MODULOINTEGER);
    }

    @Override
    protected void doTest(TestCase t) {
        super.doTest(t);
    }

    public void testConstants() {
        info(" constants");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(ModuloInteger.valueOf(LargeInteger.valueOf(1)), ModuloInteger.ONE);
                assertEquals(ModuloInteger.valueOf(LargeInteger.valueOf(0)), ModuloInteger.ZERO);
            }
        });
    }

    private static final LargeInteger[] moduli = { LargeInteger.valueOf(17), LargeInteger.valueOf(93846) };

    protected List<LargeInteger> getTestModuli() {
        return Arrays.asList(moduli);
    }

    @Override
    public void testAbs() {
        // not implemented.
    }

    @Override
    public void testDivide() {
        // not implemented.
    }

    @Override
    public void testIsNegative() {
        // not implemented.
    }

    @Override
    public void testIsPositive() {
        // not implemented.
    }

    @Override
    public void testIsZero() {
        // not implemented.
    }

    @Override
    public void testPow() {
        info("  pow");
        for (final Pair<Double, ModuloInteger> p : getTestValues()) {
            for (final int exp : new Integer[] { 1, 3, 7, 8, 9 }) {
                double pow = MathLib.pow(p._x, exp);
                if (null != ModuloInteger.getModulus()) {
                    double mod = ModuloInteger.getModulus().doubleValue();
                    pow = 1;
                    for (int i = 0; i < exp; ++i) {
                        pow = (pow * p._x) % mod;
                    }
                    pow = (pow + mod) % mod;
                }
                if (getMaxNumber() >= MathLib.abs(pow)) {
                    doTest(new AbstractNumberTest<ModuloInteger>("Testing pow " + p + ", " + exp, pow, _helper) {
                        @Override
                        ModuloInteger operation() throws Exception {
                            return p._y.pow(exp);
                        }
                    });
                }
            }
        }
    }

    /** The modulo operation for comparison purposes */
    private double mod(double d, LargeInteger m) {
        double dl = Math.rint(d);
        double dm = m.doubleValue();
        return (dl % dm + dm) % dm; // 0..dm-1
    }

    @Override
    public void testPlus() {
        super.testPlus(); // without modulus
        for (final LargeInteger m : getTestModuli()) {
            for (final Pair<Double, ModuloInteger> p : getTestValues()) {
                for (final Pair<Double, ModuloInteger> q : getTestValues()) {
                    // In the case of Long.M*_VALUE we have a problem with the precision of double:
                    // (double)Long.MIN_VALUE == (double)Long.MAX_VALUE
                    if (p._x != Long.MIN_VALUE && p._x != Long.MAX_VALUE) {
                        doTest(new AbstractNumberTest<ModuloInteger>("Testing plus " + p._x + "," + q._x, mod(p._x
                                + q._x, m), _helper) {
                            @Override
                            ModuloInteger operation() throws Exception {
                                LocalContext.enter();
                                try {
                                    ModuloInteger.setModulus(m);
                                    return p._y.plus(q._y);
                                } finally {
                                    LocalContext.exit();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void testTimes() {
        super.testTimes(); // without modulus
        for (final LargeInteger m : getTestModuli()) {
            for (final Pair<Double, ModuloInteger> p : getTestValues()) {
                for (final Pair<Double, ModuloInteger> q : getTestValues()) {
                    doTest(new AbstractNumberTest<ModuloInteger>("Testing times " + p._x + "," + q._x, mod(p._x * q._x,
                            m), _helper) {
                        @Override
                        ModuloInteger operation() throws Exception {
                            LocalContext.enter();
                            try {
                                ModuloInteger.setModulus(m);
                                return p._y.times(q._y);
                            } finally {
                                LocalContext.exit();
                            }
                        }
                    });
                }
            }
        }
    }

}
