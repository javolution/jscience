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
import javolution.lang.Immutable;
import javolution.lang.MathLib;

/**
 * <p> This class represents a converter dividing numeric values by π (Pi).</p>
 *
 * <p> This class is package private, instances are created
 *     using the {@link PiMultiplierConverter#inverse()} method.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
final class PiDivisorConverter extends PhysicsConverter implements Immutable {

    /**
     * Creates a Pi multiplier converter.
     */
    public PiDivisorConverter() {
    }

    @Override
    public double convert(double value) {
        return value / MathLib.PI;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        int nbrDigits = ctx.getPrecision();
        if (nbrDigits == 0) throw new ArithmeticException("Pi multiplication with unlimited precision");
        BigDecimal pi = PiMultiplierConverter.Pi.pi(nbrDigits);
        return value.divide(pi, ctx).scaleByPowerOfTen(nbrDigits-1);
    }

    @Override
    public PhysicsConverter inverse() {
        return new PiMultiplierConverter();
    }

    @Override
    public final String toString() {
        return "(1/π)";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PiDivisorConverter) ? true : false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean isLinear() {
        return true;
    }

}
