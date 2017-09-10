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

/**
 * <p> This class represents a logarithmic converter of limited precision.
 *     Such converter  is typically used to create logarithmic unit.
 *     For example:[code]
 *     Unit<Dimensionless> BEL = Unit.ONE.transform(new LogConverter(10).inverse());
 *     [/code]</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public final class LogConverter extends PhysicsConverter implements Immutable {

    /**
     * Holds the logarithmic base.
     */
    private double base;
    /**
     * Holds the natural logarithm of the base.
     */
    private double logOfBase;

    /**
     * Returns a logarithmic converter having the specified base.
     *
     * @param  base the logarithmic base (e.g. <code>Math.E</code> for
     *         the Natural Logarithm).
     */
    public LogConverter(double base) {
        this.base = base;
        this.logOfBase = Math.log(base);
    }

    /**
     * Returns the logarithmic base of this converter.
     *
     * @return the logarithmic base (e.g. <code>Math.E</code> for
     *         the Natural Logarithm).
     */
    public double getBase() {
        return base;
    }

    @Override
    public PhysicsConverter inverse() {
        return new ExpConverter(base);
    }

    @Override
    public final String toString() {
        if (base == Math.E) {
            return "ln";
        } else {
            return "Log(" + base + ")";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LogConverter))
            return false;
        LogConverter that = (LogConverter) obj;
        return this.base == that.base;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(base);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public double convert(double amount) {
        return Math.log(amount) / logOfBase;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        return BigDecimal.valueOf(convert(value.doubleValue())); // Reverts to double conversion.
    }

    @Override
    public boolean isLinear() {
        return false;
    }
}
