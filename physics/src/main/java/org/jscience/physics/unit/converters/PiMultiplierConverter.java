/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.converters;

import org.jscience.physics.unit.PhysicsConverter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import javolution.lang.Immutable;
import javolution.lang.MathLib;

/**
 * <p> This class represents a converter multiplying numeric values by π (Pi).</p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Pi"> Wikipedia: Pi</a>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public final class PiMultiplierConverter extends PhysicsConverter implements Immutable {

    /**
     * Creates a Pi multiplier converter.
     */
    public PiMultiplierConverter() {
    }

    @Override
    public double convert(double value) {
        return value * MathLib.PI;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        int nbrDigits = ctx.getPrecision();
        if (nbrDigits == 0) throw new ArithmeticException("Pi multiplication with unlimited precision");
        BigDecimal pi = Pi.pi(nbrDigits);
        return value.multiply(pi, ctx).scaleByPowerOfTen(1-nbrDigits);
    }

    @Override
    public PhysicsConverter inverse() {
        return new PiDivisorConverter();
    }

    @Override
    public final String toString() {
        return "(π)";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PiMultiplierConverter) ? true : false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isLinear() {
        return true;
    }

    /**
     * Pi calculation with Machin's formula.
     *
     * @see <a href="http://en.literateprograms.org/Pi_with_Machin's_formula_(Java)">Pi with Machin's formula</a>
     *
     */
    static final class Pi {

        private Pi() {
        }

        public static BigDecimal pi(int numDigits) {
            int calcDigits = numDigits + 10;
            return FOUR.multiply((FOUR.multiply(arccot(FIVE, calcDigits))).subtract(arccot(TWO_THIRTY_NINE, calcDigits))).setScale(numDigits, RoundingMode.DOWN);
        }

        private static BigDecimal arccot(BigDecimal x, int numDigits) {
            BigDecimal unity = BigDecimal.ONE.setScale(numDigits, RoundingMode.DOWN);
            BigDecimal sum = unity.divide(x, RoundingMode.DOWN);
            BigDecimal xpower = new BigDecimal(sum.toString());
            BigDecimal term = null;
            boolean add = false;
            for (BigDecimal n = new BigDecimal("3"); term == null
                    || !term.equals(BigDecimal.ZERO); n = n.add(TWO)) {
                xpower = xpower.divide(x.pow(2), RoundingMode.DOWN);
                term = xpower.divide(n, RoundingMode.DOWN);
                sum = add ? sum.add(term) : sum.subtract(term);
                add = !add;
            }
            return sum;
        }
    }
    private static final BigDecimal TWO = new BigDecimal("2");

    private static final BigDecimal FOUR = new BigDecimal("4");

    private static final BigDecimal FIVE = new BigDecimal("5");

    private static final BigDecimal TWO_THIRTY_NINE = new BigDecimal("239");

}
