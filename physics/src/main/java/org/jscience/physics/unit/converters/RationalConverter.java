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
import java.math.BigInteger;
import java.math.MathContext;
import javolution.lang.Immutable;
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> This class represents a converter multiplying numeric values by an
 *     exact scaling factor (represented as the quotient of two
 *     <code>BigInteger</code> numbers).</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public final class RationalConverter extends PhysicsConverter implements Immutable {

    /**
     * Holds the converter dividend.
     */
    private BigInteger dividend;

    /**
     * Holds the converter divisor (always positive).
     */
    private BigInteger divisor;

    /**
     * Creates a rational converter with the specified dividend and
     * divisor.
     *
     * @param dividend the dividend.
     * @param divisor the positive divisor.
     * @throws IllegalArgumentException if <code>divisor &lt;= 0</code>
     * @throws IllegalArgumentException if <code>dividend == divisor</code>
     */
    public RationalConverter(BigInteger dividend, BigInteger divisor) {
        if (divisor.compareTo(BigInteger.ZERO) <= 0)
            throw new IllegalArgumentException("Negative or zero divisor");
        if (dividend.equals(divisor))
            throw new IllegalArgumentException("Would result in identity converter");
        this.dividend = dividend; // Exact conversion.
        this.divisor = divisor; // Exact conversion.
    }

    /**
     * Convenience method equivalent to
     * <code>RationalConverter.valueOf(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor))</code>
     *
     * @param dividend the dividend.
     * @param divisor the positive divisor.
     * @throws IllegalArgumentException if <code>divisor &lt;= 0</code>
     * @throws IllegalArgumentException if <code>dividend == divisor</code>
     */
    public RationalConverter (long dividend, long divisor) {
        this(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
    }

    /**
     * Returns the integer dividend for this rational converter.
     *
     * @return this converter dividend.
     */
    public BigInteger getDividend() {
        return dividend;
    }

    /**
     * Returns the integer (positive) divisor for this rational converter.
     *
     * @return this converter divisor.
     */
    public BigInteger getDivisor() {
        return divisor;
    }

    @Override
    public double convert(double value) {
        return value * toDouble(dividend) / toDouble(divisor);
    }

    // Optimization of BigInteger.doubleValue() (implementation too inneficient).
    private static double toDouble(BigInteger integer) {
        return (integer.bitLength() < 64) ? integer.longValue() : integer.doubleValue();
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        BigDecimal decimalDividend = new BigDecimal(dividend, 0);
        BigDecimal decimalDivisor = new BigDecimal(divisor, 0);
        return value.multiply(decimalDividend, ctx).divide(decimalDivisor, ctx);
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (!(converter instanceof RationalConverter))
            return super.concatenate(converter);
        RationalConverter that = (RationalConverter) converter;
        BigInteger newDividend = this.getDividend().multiply(that.getDividend());
        BigInteger newDivisor = this.getDivisor().multiply(that.getDivisor());
        BigInteger gcd = newDividend.gcd(newDivisor);
        newDividend = newDividend.divide(gcd);
        newDivisor = newDivisor.divide(gcd);
        return (newDividend.equals(BigInteger.ONE) && newDivisor.equals(BigInteger.ONE))
                ? IDENTITY : new RationalConverter(newDividend, newDivisor);
    }

    @Override
    public RationalConverter inverse() {
        return dividend.signum() == -1 ? new RationalConverter(getDivisor().negate(), getDividend().negate())
                : new RationalConverter(getDivisor(), getDividend());
    }

    @Override
    public final String toString() {
        return "RationalConverter(" + dividend + "," + divisor + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RationalConverter))
            return false;
        RationalConverter that = (RationalConverter) obj;
        return this.dividend.equals(that.dividend)
                && this.divisor.equals(that.divisor);
    }

    @Override
    public int hashCode() {
        return dividend.hashCode() + divisor.hashCode();
    }

    @Override
    public boolean isLinear() {
        return true;
    }
}
