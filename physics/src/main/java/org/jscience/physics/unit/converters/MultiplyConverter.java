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
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> This class represents a converter multiplying numeric values by a
 *     constant scaling factor (<code>double</code> based).</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public final class MultiplyConverter extends PhysicsConverter implements Immutable {

    /**
     * Holds the scale factor.
     */
    private double factor;

    /**
     * Creates a multiply converter with the specified scale factor.
     *
     * @param  factor the scaling factor.
     * @throws IllegalArgumentException if coefficient is <code>1.0</code>
     *        (would result in identity converter)
     */
    public MultiplyConverter(double factor) {
        if (factor == 1.0)
            throw new IllegalArgumentException("Would result in identity converter");
        this.factor = factor;
    }

    /**
     * Returns the scale factor of this converter.
     *
     * @return the scale factor.
     */
    public double getFactor() {
        return factor;
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (!(converter instanceof MultiplyConverter))
            return super.concatenate(converter);
        double newfactor = factor * ((MultiplyConverter) converter).factor;
        return newfactor == 1.0 ? IDENTITY : new MultiplyConverter(newfactor);
    }

    @Override
    public MultiplyConverter inverse() {
        return new MultiplyConverter(1.0 / factor);
    }

    @Override
    public double convert(double value) {
        return value * factor;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        return value.multiply(BigDecimal.valueOf(factor), ctx);
    }

    @Override
    public final String toString() {
        return "MultiplyConverter(" + factor + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiplyConverter))
            return false;
        MultiplyConverter that = (MultiplyConverter) obj;
        return this.factor == that.factor;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(factor);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public boolean isLinear() {
        return true;
    }
}
