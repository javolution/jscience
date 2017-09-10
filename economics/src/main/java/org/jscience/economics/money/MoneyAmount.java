/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */

package org.jscience.economics.money;

import java.text.FieldPosition;
import java.text.NumberFormat;
import org.jscience.physics.unit.PhysicalUnit;
import javolution.context.ObjectFactory;
import org.jscience.mathematics.number.Decimal;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.physics.measure.Measure;
import org.jscience.physics.measure.DecimalMeasure;

/**
 * This class represents an amount of money specified in a given
 * {@link Currency} (convenience method).
 * 
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 2, 2010
 */
public class MoneyAmount extends Measure<Decimal, Money> {

   /**
     * Holds the factory constructing new instances (possibly on the stack).
     */
    private static final ObjectFactory<MoneyAmount> FACTORY = new ObjectFactory<MoneyAmount>() {

        protected MoneyAmount create() {
            return new MoneyAmount();
        }
    };

  /**
     * Holds the decimal value stated in the specified currency.
     */
    private Decimal _value;

    /**
     * Holds the unit.
     */
    private Currency _currency;

    /**
     * Default constructor.
     */
    MoneyAmount() {
    }

    /**
     * Creates a money amount always on the heap
     * independently from the
     * current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     *
     * @param value the value stated in the specified currency.
     * @param currency the currency in which the value is stated.
     */
    public MoneyAmount(Decimal value, Currency unit) {
        _value = value;
        _currency = unit;
    }

    /**
     * Returns the money amount corresponding to the specified decimal
     * value and currency.
     *
     * @param value the value stated in the specified currency.
     * @param currency the currency in which the value is stated.
     * @return the corresponding amount.
     */
    public static MoneyAmount valueOf(Decimal value, Currency currency) {
        MoneyAmount amount = FACTORY.object();
        amount._value = value;
        amount._currency = currency;
        return amount;
    }

    /**
     * Returns the money amount corresponding to the specified value and cents.
     *
     * @param value the integer value in the specified currency.
     * @param cents the cents value in the specified currency.
     * @param currency the currency in which the value and cents are stated.
     * @return the corresponding amount.
     */
    public static MoneyAmount valueOf(long value, int cents, Currency currency) {
        MoneyAmount amount = FACTORY.object();
        amount._value = Decimal.valueOf(value * 100 + cents, -2);
        amount._currency = currency;
        return amount;
    }

    /**
     * Returns the money amount corresponding to the specified generic amount.
     *
     * @param amount the raw amount.
     * @return the corresponding money amount stated in an existing {@link Currency}.
     * @throws ClassCastException if the SI unit of the specified amount
     *         is not a {@link Currency}.
     */
    public static MoneyAmount valueOf(Measure<?, Money> amount) {
        Measure<?, Money> amountSI = amount.toSI();
        return MoneyAmount.valueOf(Decimal.valueOf(amountSI.getValue().decimalValue()),
                (Currency) amountSI.getUnit());
    }

    /**
     * Overrides the default {@link javax.measure.Measure#toStringLocale()}
     * to show only the currency {@link Currency#getFractionDigits() fraction
     * digits} of the associated currency (e.g. rounding to closest cents).
     *
     * @return the string representation of this money amount.
     */
    public String toStringLocale() {
        Decimal value = this.getValue();
        int digits = this.getUnit().getFractionDigits();
        int exponent = value.getExponent();
        LargeInteger significand = value.getSignificand();
        int scale = exponent + digits;
        significand = significand.E(scale);
        exponent -= scale;
        value = Decimal.valueOf(significand, exponent);
        NumberFormat numberFormat = NumberFormat.getInstance();
        StringBuffer tmp = new StringBuffer();
        numberFormat.format(value, tmp, new FieldPosition(0));
        tmp.append(' ');
        tmp.append(this.getUnit().toString());
        return tmp.toString();
    }
    
    @Override
    public Decimal getValue() {
        return _value;
    }

    @Override
    public Currency getUnit() {
        return _currency;
    }

    @Override
    public MoneyAmount opposite() {
        return MoneyAmount.valueOf(_value.opposite(), _currency);
    }

    @Override
    public MoneyAmount plus(Measure<Decimal, ?> that) {
        Measure<Decimal, ?> amount = that.to((PhysicalUnit)_currency);
        return MoneyAmount.valueOf(this._value.plus(amount.getValue()), _currency);
    }

    @Override
    public MoneyAmount minus(Measure<Decimal, ?> that) {
        Measure<Decimal, ?> amount = that.to((PhysicalUnit)_currency);
        return MoneyAmount.valueOf(this._value.minus(amount.getValue()), _currency);
    }

    @Override
    public MoneyAmount times(long n) {
        return MoneyAmount.valueOf(_value.times(n), _currency);
    }

    @Override
    public Measure<Decimal, ?> times(Measure<Decimal, ?> that) {
        return DecimalMeasure.valueOf(_value.times(that.getValue()),
                this._currency.multiply(that.getUnit()));
    }

    @Override
    public Measure<Decimal, ?> pow(int exp) {
        return DecimalMeasure.valueOf(_value.pow(exp), this._currency.pow(exp));
    }

    @Override
    public Measure<Decimal, ?> inverse() {
        return DecimalMeasure.valueOf(_value.inverse(), this._currency.inverse());
    }

    @Override
    public MoneyAmount divide(long n) {
        return MoneyAmount.valueOf(_value.divide(n), _currency);
    }

    @Override
    public Measure<Decimal, ?> divide(Measure<Decimal, ?> that) {
        return DecimalMeasure.valueOf(_value.divide(that.getValue()),
                this._currency.divide(that.getUnit()));
    }
 
    @Override
    public MoneyAmount copy() {
        return MoneyAmount.valueOf(_value.copy(), _currency);
    }
}