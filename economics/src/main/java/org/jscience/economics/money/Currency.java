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
import java.util.Locale;
import org.jscience.physics.unit.Dimension;
import org.jscience.physics.unit.PhysicalUnit;
import org.jscience.physics.unit.converter.UnitConverterImpl;
import org.jscience.physics.unit.format.UnitFormatImpl;
import javolution.context.LocalContext;
import javolution.util.LocalMap;

/**
 * <p> This class represents a currency {@link javax.measure.unit.PhysicalUnit PhysicalUnit}.
 *     Currencies are a special form of {@link DerivedUnit}, conversions
 *     between currencies is possible if their respective exchange rates 
 *     have been set. The conversion factor can be changed dynamically.</p>
 *     
 * <p> Measurements stated in a {@link Currency} are usually of {@link Money}
 *     type. For example:[code]
 *         Measure<Money> tenDollars = Measure.valueOf(10, Currency.USD);
 *     [/code]</p>
 * 
 * <p> By default, the label associated to a currency is its ISO-4217 code
 *     (see the <a href="http://www.bsi-global.com/iso4217currency"> ISO 4217
 *     maintenance agency</a> for a table of currency codes). Although, 
 *     local mapping of currency symbol is possible.
 *     For example:[code]
 *         EUR.setLabel("€");
 *         GBP.setLabel("£");
 *         JPY.setLabel("¥");
 *         USD.setLabel("$");
 *     [/code]</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 2, 2010
 */
public class Currency extends PhysicalUnit<Money> {

    /**
     * Holds the context-local mapping (source to target exchange rate).
     */
    private static LocalMap<Currency, LocalMap<Currency, Number>> EXCHANGE_RATES = new LocalMap();

    /**
     * The Australian Dollar currency unit.
     */
    public static final Currency AUD = new Currency("AUD");

    /**
     * The Canadian Dollar currency unit.
     */
    public static final Currency CAD = new Currency("CAD");

    /**
     * The China Yan currency.
     */
    public static final Currency CNY = new Currency("CNY");

    /**
     * The Euro currency.
     */
    public static final Currency EUR = new Currency("EUR");

    /**
     * The British Pound currency.
     */
    public static final Currency GBP = new Currency("GBP");

    /**
     * The Japanese Yen currency.
     */
    public static final Currency JPY = new Currency("JPY");

    /**
     * The Korean Republic Won currency.
     */
    public static final Currency KRW = new Currency("KRW");

    /**
     * The Taiwanese dollar currency.
     */
    public static final Currency TWD = new Currency("TWD");

    /**
     * The United State dollar currency.
     */
    public static final Currency USD = new Currency("USD");

    /**
     * Holds the local currency to label mapping.
     */
    private static final LocalMap<Currency, String> CURRENCY_TO_LABEL = new LocalMap();

    /**
     * Holds the currency code.
     */
    private final String _code;

    /**
     * Creates the currency unit for the given currency code.
     * See the <a href="http://www.bsi-global.com/iso4217currency"> ISO 4217
     * maintenance agency</a> for more information, including a table of
     * currency codes.
     * The currency code is added to the standard (UCUM) symbol map.
     *
     * @param  code the ISO-4217 code of the currency (e.g.
     *         <code>"EUR", "USD", "JPY"</code>).
     * @throws IllegalArgumentException if the specified code is not an ISO-4217
     *         code.
     * @see UnitFormatImpl#getSymbolMap()
     */
    public Currency(String code) {
        _code = code;
        UnitFormatImpl.getInstance().getSymbolMap().label(this, code);
    }

   /**
     * Returns the currency for the country of the given locale.
     *
     * @param locale the locale for whose country a <code>Currency</code>
     *        instance is returned.
     * @return the currency instance for the country of the given locale.
     */
    public static Currency getInstance(Locale locale) {
        String code = java.util.Currency.getInstance(locale).getCurrencyCode();
        return new Currency(code);
    }

    /**
     * Returns the currency code for this currency.
     *
     * @return the ISO-4217 code of the currency 
     *         (e.g. <code>"EUR", "USD", "JPY"</code>).
     */
    public String getCode() {
        return _code;
    }

    /**
     * Returns the {@link javolution.context.LocalContext local} label
     * for this currency (default {@link #getCode()).
     *
     * @return the label used by {@link #toString}
     */
    public String getLabel() {
        String label = CURRENCY_TO_LABEL.get(this);
        return label != null ? label : _code;
    }

    /**
     * Sets the  {@link javolution.context.LocalContext local} label
     * for this currency.
     *
     * @param label the new label.
     */
    public void setLabel(String label) {
        CURRENCY_TO_LABEL.put(this, label);
    }

    /**
     * Returns the number of fraction digits used with this currency 
     * unit. For example, the number of fraction digits for
     * the {@link Currency#EUR} is 2, while for the {@link Currency#JPY} (Yen)
     * it's 0. This method can be overriden for custom currencies returning 
     * values different from <code>2</code>.  
     *
     * @return the default number of fraction digits for this currency.
     */
    public int getFractionDigits() {
        return (this.equals(JPY) || (this.equals(KRW))) ? 0 : 2;
    }

    /**
     * Sets the exchange rate of this {@link Currency} to
     * the specified target currency. Setting the exchange rate allows
     * for conversion between money quantities stated in this currency 
     * to money quantities setting in the specified currency.
     * For example:[code]
     *     // Sets the exchange rate from EUR to USD and from USD to EUR.
     *     EUR.setExchangeRate(1.45, USD);
     *     USD.setExchangeRate(0.62, EUR);
     * [/code]
     *
     * @param  factor the amount of the target currency equals to this currency.
     * @param  target the target currency.
     * @see    CurrencyConverter#setInstance(org.jscience.economics.money.CurrencyConverter)
     */
    public void setExchangeRate(Number factor, Currency target) {
        LocalMap<Currency, Number> targetToRate = EXCHANGE_RATES.get(this);
        if (targetToRate == null) {
            targetToRate = new LocalMap();
            EXCHANGE_RATES.put(this, targetToRate);
        }
        targetToRate.put(target, factor);
    }
 
    /**
     * Returns the exchange rate from this {@link Currency} to the specified
     * currency.
     *
     * @param  target the target currency.
     * @return the amount of the target currency equals to this currency or
     *         <code>BigDecimal.ONE</code> if the specified target is equals
     *         to this or <code>null</code> if the exchange rate has not be set.
     */
    public Number getExchangeRate(Currency target) {
        if (target.equals(this)) return BigDecimal.ONE;
        LocalMap<Currency, Number> targetToRate = EXCHANGE_RATES.get(this);
        if (targetToRate == null) return null;
        return targetToRate.get(target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Currency))
            return false;
        Currency that = (Currency) obj;
        return this._code.equals(that._code);
    }

    @Override
    public int hashCode() {
        return _code.hashCode();
    }

    @Override
    public Currency toMetric() {
        return this;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public UnitConverterImpl getConverterToMetric() {
        return UnitConverterImpl.IDENTITY;
    }

    @Override
    public Dimension getDimension() {
        return Money.DIMENSION;
    }

    @Override
    public UnitConverterImpl getDimensionalTransform() {
        return new DimensionalTransform(this, true, UnitConverterImpl.IDENTITY);
    }

    /**
     * This class represents the converter from a currency to its 
     * dimensional unit or from the dimensional unit to a currency.
     * 
     * @see Money#UNIT
     */
    private static class DimensionalTransform extends UnitConverterImpl {

        // Holds the reference currency.
        private final Currency _currency;

        // Indicates if this is the tranformation towards or from the currency.
        private final boolean _isFrom;

        // The actual transform.
        private final UnitConverterImpl _transform;

        DimensionalTransform(Currency currency, boolean isFrom, UnitConverterImpl transform) {
            _currency = currency;
            _isFrom = isFrom;
            _transform = transform;
        }

        @Override
        public UnitConverterImpl concatenate(UnitConverterImpl cvtr) {
            if (cvtr instanceof DimensionalTransform) {
                DimensionalTransform that = (DimensionalTransform) cvtr;
                // We need to have a dimensional transform opposite.
                if (this._isFrom == that._isFrom)
                    throw new UnsupportedOperationException("Monetary transforms cannot be merged");
                UnitConverterImpl newTransform = this._transform.concatenate(that._transform);
                if (this._currency.equals(that._currency)) // Simplification.
                    return newTransform;
                CurrencyConverter currencyConverter = (this._isFrom) ?
                    new CurrencyConverter(this._currency, that._currency) :
                    new CurrencyConverter(that._currency, this._currency);
                return currencyConverter.concatenate(newTransform);
            } else {
                return new DimensionalTransform(_currency, _isFrom, _transform.concatenate(cvtr));
            }
        }

        @Override
        public boolean isLinear() {
            return _transform.isLinear();
        }

        @Override
        public UnitConverterImpl inverse() {
            return new DimensionalTransform(_currency, !_isFrom, _transform.inverse());
        }

        @Override
        public double convert(double value) {
            throw new UnsupportedOperationException(_isFrom
                    ? "No currency to convert to" : "No currency to convert from");
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
            throw new UnsupportedOperationException(_isFrom
                    ? "No currency to convert to" : "No currency to convert from");
        }

        @Override
        public boolean equals(Object cvtr) {
            if (!(cvtr instanceof DimensionalTransform))
                return false;
            DimensionalTransform that = (DimensionalTransform) cvtr;
            return this._currency.equals(that._currency) && (this._isFrom == that._isFrom)
                    && this._transform.equals(that._transform);
        }

        @Override
        public int hashCode() {
            return _currency.hashCode();
        }
    }
    private static final long serialVersionUID = 1L;

}
