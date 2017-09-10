package org.jscience.mathematics.number;

import static javolution.context.LogContext.info;
import static javolution.testing.TestContext.assertEquals;
import static javolution.testing.TestContext.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jscience.mathematics.number.util.NumberHelper;

import javolution.testing.TestCase;
import javolution.testing.TestContext;

/**
 * Testsuite for {@link ComplexField}. It inherits many tests from {@link AbstractFloatTestSuite} that test the operations
 * for {@link ComplexField} numbers with zero real value - these are basically simple smoketests. <br>
 * TODO: more tests are needed.
 * @author hps
 * @since 02.02.2009
 */
public class TestComplex extends AbstractFloatTestSuite<ComplexField> {

    protected static final double EPSILON = 1e-13;
    private static final ComplexField[] TESTVALUES = new ComplexField[] { ComplexField.valueOf(3.2, 1.7), ComplexField.valueOf(-3.72, 2.8),
            ComplexField.valueOf(-95.3, -4.2), ComplexField.valueOf(0.3, -4.2), ComplexField.valueOf(0, 1.7), ComplexField.valueOf(0, -5),
            ComplexField.valueOf(3, 0), ComplexField.valueOf(-42, 0) };

    public TestComplex() {
        super(NumberHelper.COMPLEX);
    }

    @Override
    public void testAbs() {
        // Complex has no abs, which is a bug in itself. 8-)
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

    protected List<ComplexField> getComplexTestvalues() {
        return Arrays.asList(TESTVALUES);
    }

    public void testEquals2() {
        info("  equals2");
        for (final ComplexField c : getComplexTestvalues()) {
            for (final ComplexField d : getComplexTestvalues()) {
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        if (c == d) {
                            TestContext.assertTrue(c.equals(d, EPSILON), (c.toString() + ", " + d.toString()));
                        } else {
                            TestContext.assertTrue((!c.equals(d, EPSILON)), (c.toString() + ", " + d.toString()));
                        }
                    }
                });
            }
        }
    }

    /** Compares c and d and asserts that the difference or the relative difference is less than EPSILON */
    protected void assertNear(ComplexField a, ComplexField b) {
        double d = a.minus(b).magnitude();
        TestContext.assertTrue((d < EPSILON || d / a.magnitude() < EPSILON), (a + " versus " + b + " differ by " + d));
    }

    public void testToString2() {
        info("  toString2 ");
        for (final ComplexField c : getComplexTestvalues()) {
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    ComplexField cn = ComplexField.valueOf(c.toString());
                    assertNear(cn, c);
                }
            });
        }
    }

    /**
     * We execute quite a lot of operations in one go and compare with the known result. Not the best test, but quick to
     * write.
     */
    public void testOperationChain() {
        info("  operationChain");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                ComplexField m = ComplexField.valueOf(0.8, 0.8);
                ComplexField s = ComplexField.ZERO;
                ComplexField f = ComplexField.ONE;
                for (int i = 0; i < 10; ++i) {
                    s = s.plus(f);
                    f = f.times(m);
                }
                final ComplexField scmp = m.pow(10).minus(ComplexField.ONE).divide(m.minus(ComplexField.ONE));
                TestContext.assertTrue(s.equals(scmp, EPSILON), s.toString());
                m = m.conjugate();
                ComplexField mi = ComplexField.ONE;
                for (int i = 0; i < 10; ++i) {
                    mi = mi.times(m);
                    s = s.minus(f.divide(mi));
                }
                TestContext.assertTrue(s.equals(ComplexField.valueOf(scmp.getReal(), 0).times(2), EPSILON), s.toString());
            }
        });
    }

    public void testPow2() {
        info("  pow2");
        for (final ComplexField c : getComplexTestvalues()) {
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    ComplexField cx = c.times(c).times(c);
                    assertNear(cx, c.pow(3));
                    assertNear(cx, c.pow(3.0));
                    assertNear(cx, c.pow(ComplexField.valueOf(3, 0)));
                }
            });
        }
    }

    public void testExponentials() {
        info("  exponentials");
        for (final ComplexField c : getComplexTestvalues()) {
            for (final ComplexField d : getComplexTestvalues()) {
                doTest(new SimpleTestCase() {
                    @Override
                    public void execute() {
                        ComplexField cx = c.pow(d);
                        ComplexField cy = c.log().times(d).exp();
                        assertNear(cx, cy);
                    }
                });
            }
        }
    }

    public void testSqrt2() {
        info("  sqrt2");
        for (final ComplexField c : getComplexTestvalues()) {
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    assertNear(c, c.sqrt().times(c.sqrt()));
                }
            });
        }
    }
}
