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
 * Instantiation of the generic tests of the {@link AbstractFloatTestSuite} for {@link Decimal} and some further
 * tests that are specific to {@link Decimal}. <br>
 * We omit getExponent, getSignificand, times(long) since these are trivial.
 * @since 23.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public class DecimalTestSuite extends AbstractFloatTestSuite<Decimal> {

    /** Sets the needed helper class. */
    public DecimalTestSuite() {
        super(NumberHelper.DECIMAL);
    }

    /**
     * We add a couple of values with different precision.
     * @see org.jscience.mathematics.number.AbstractFloatTestSuite#initTestValues(java.util.List)
     */
    @Override
    protected void initTestValues(List<Pair<Double, Decimal>> values) {
        super.initTestValues(values);
        values.add(Pair.make(0.7234938, Decimal.valueOf("0.7234938")));
        values.add(Pair.make(0.7234938, Decimal.valueOf("0.72349380000000000000000000000000000000")));
    }

    public void testConstants() {
        info(" constants");
        doTest(new SimpleTestCase() {
            @Override
            public void execute() {
                assertEquals(Decimal.valueOf(1), Decimal.ONE);
                assertEquals(Decimal.valueOf(0), Decimal.ZERO);
                assertTrue(Decimal.NaN.isNaN());
            }
        });        
    }
    
    public void testRound() {
        info("  round");
        for (final Pair<Double, Decimal> p : getTestValues()) {
            doTest(new AbstractNumberTest<Decimal>("Testing round " + p, MathLib.round(p._x), _helper) {
                @Override
                Decimal operation() throws Exception {
                    final LargeInteger rounded = p._y.round();
                    return Decimal.valueOf(rounded, 0);
                }
            });
        }
    }
    
    public void testSetDigits() {
        info("  setDigits");
        for (final Pair<Double, Decimal> p : getTestValues()) {
            doTest(new SimpleTestCase() {
                @Override
                public void execute() {
                    Decimal v1 = _helper.valueOf(0.123);
                    try {
                        LocalContext.enter();
                        Decimal.setDigits(50);
                        Decimal v2 = v1.reciprocal();
                        final int dl = v2.getSignificand().digitLength();
                        TestContext.assertTrue(50 == dl, "" + dl);
                    } finally {
                        LocalContext.exit();
                    }
                    // now we should have a different digitlength
                    Decimal v2 = v1.reciprocal();
                    final int dl = v2.getSignificand().digitLength();
                    TestContext.assertTrue(50 != dl, "" + dl);
                }
            });
        }
    }
}
