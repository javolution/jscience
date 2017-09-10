/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.economics.money;

import java.math.BigDecimal;
import java.math.MathContext;
import org.jscience.physics.unit.converter.UnitConverterImpl;

/**
 * <p> This class represents a converter between two currencies.</p>
 *
 * <p> Currency converters convert values based upon the current
 *     exchange rate {@link Currency#getExchangeRate() exchange rate}.
 *     If the {@link Currency#getExchangeRate() exchange rate} from the
 *     target currency to the source currency is not set, conversion
 *     fails. In others words, the converter from a currency <code>A</code>
 *     to a currency <code>B</code> is independant from the converter from
 *     <code>B</code> to <code>A</code>.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 2, 2010
 * @see     Currency#setExchangeRate
 */
public class CurrencyConverter extends UnitConverterImpl {

    /**
     * Holds the source currency.
     */
    private Currency _source;

    /**
     * Holds the target currency.
     */
    private Currency _target;

    /**
     * Creates the currency converter from the source currency to the target
     * currency.
     *
     * @param source the source currency.
     * @param target the target currency.
     * @param factor the multiplier factor from source to target.
     * @return the corresponding converter.
     */
    public CurrencyConverter(Currency source, Currency target) {
        _source = source;
        _target = target;
    }

    /**
     * Returns the source currency.
     *
     * @return the source currency.
     */
    public Currency getSource() {
        return _source;
    }

    /**
     * Returns the target currency.
     * 
     * @return the target currency.
     */
    public Currency getTarget() {
        return _target;
    }

    @Override
    public CurrencyConverter inverse() {
        return new CurrencyConverter(_target, _source);
    }

    @Override
    public double convert(double x) {
        Number factor = _source.getExchangeRate(_target);
        if (factor == null)
            throw new UnsupportedOperationException("Exchange rate from " +
                    _source + " to " + _target + " not set.");
        return factor.doubleValue() * x;
    }

    @Override
    public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
        Number factor = _source.getExchangeRate(_target);
        if (factor == null)
            throw new UnsupportedOperationException("Exchange rate from " +
                    _source + " to " + _target + " not set.");
        if (factor instanceof BigDecimal)
            return value.multiply((BigDecimal) factor, ctx);
        if (factor instanceof org.jscience.mathematics.number.Number) {
            return value.multiply(((org.jscience.mathematics.number.Number)factor).decimalValue(), ctx);
        } else { // Reverts to double convert.
            return value.multiply(BigDecimal.valueOf(factor.doubleValue()), ctx);
        }
    }

    @Override
    public boolean equals(Object cvtr) {
        if (!(cvtr instanceof CurrencyConverter))
            return false;
        CurrencyConverter that = (CurrencyConverter) cvtr;
        return this._source.equals(that._source) &&
                this._target.equals(that._target);
    }

    @Override
    public int hashCode() {
        return _source.hashCode() + _target.hashCode();
    }

    @Override
    public final String toString() {
        return "Currency.Converter(From: " + _source + " to " + _target + ")";
    }

    @Override
    public boolean isLinear() {
        return true;
    }

    private static final long serialVersionUID = 1L;

}
