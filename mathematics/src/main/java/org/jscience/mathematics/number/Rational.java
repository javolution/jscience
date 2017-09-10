/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import java.io.IOException;
import java.math.BigDecimal;
import javolution.context.ObjectFactory;
import javolution.text.Cursor;
import javolution.text.TextFormat;

/**
 * <p> This class represents the ratio of two {@link LargeInteger} numbers.</p>
 * 
 * <p> Instances of this class are immutable and can be used to find exact 
 *     solutions to linear equations with the {@link 
 *     org.jscience.mathematics.vector.Matrix Matrix} class.</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, November 20, 2009
 * @see <a href="http://en.wikipedia.org/wiki/Rational_numbers">
 *      Wikipedia: Rational Numbers</a>
 */
public final class Rational extends NumberField<Rational> {

    /**
     * Holds the default text format for rational numbers (decimal representation,
     * e.g. "30/23", "12"). The divisor is not written if "1".
     *
     * @see TextFormat#getDefault
     * @see LargeInteger#format(org.jscience.mathematics.number.LargeInteger, int, java.lang.Appendable)
     * @see LargeInteger#parse(java.lang.CharSequence, int, javolution.text.Cursor)
     */
    protected static final TextFormat<Rational> TEXT_FORMAT = new TextFormat<Rational>(Rational.class) {

        @Override
        public Appendable format(Rational r, Appendable out)
                throws IOException {
            LargeInteger.format(r._dividend, 10, out);
            if (r.isInteger()) // No need to write the divisor.
                return out;
            out.append('/');
            return LargeInteger.format(r._divisor, 10, out);
        }

        @Override
        public Rational parse(CharSequence csq, Cursor cursor) {
            LargeInteger dividend = LargeInteger.parse(csq, 10, cursor);
            LargeInteger divisor = LargeInteger.ONE;
            if (cursor.skip('/', csq)) {
                divisor = LargeInteger.parse(csq, 10, cursor);
            }
            return Rational.valueOf(dividend, divisor);
        }
    };

    /**
     * Holds the factory constructing rational instances.
     */
    private static final ObjectFactory<Rational> FACTORY = new ObjectFactory<Rational>() {

        protected Rational create() {
            return new Rational();
        }
    };

    /**
     * The {@link Rational} representing the additive identity.
     */
    public static final Rational ZERO = new Rational(LargeInteger.ZERO,
            LargeInteger.ONE);

    /**
     * The {@link Rational} representing the multiplicative identity.
     */
    public static final Rational ONE = new Rational(LargeInteger.ONE,
            LargeInteger.ONE);

    /**
     * Holds the dividend.
     */
    private LargeInteger _dividend;

    /**
     * Holds the divisor.
     */
    private LargeInteger _divisor;

    /**
     * Default constructor. 
     */
    private Rational() {
    }

    /**
     * Creates a rational number always on the heap independently from the
     * current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     * 
     * @param dividend the dividend value.
     * @param divisor the divisor value.
     * @throws ArithmeticException if <code>divisor == 0</code>
     */
    public Rational(LargeInteger dividend, LargeInteger divisor) {
        if (divisor.isZero())
            throw new ArithmeticException();
        _dividend = dividend;
        _divisor = divisor;
    }

    /**
     * Convenience method equivalent to
     * {@link #Rational(org.jscience.mathematics.number.LargeInteger, org.jscience.mathematics.number.LargeInteger)
     *  Rational(new LargeInteger(significand), new LargeInteger(significand)error)}.
     *
     * @param dividend the dividend value.
     * @param divisor the divisor value.
     * @throws ArithmeticException if <code>divisor == 0</code>
     */
    public Rational(long dividend, long divisor) {
        this(new LargeInteger(dividend), new LargeInteger(divisor));
    }

    /**
     * Returns the rational number for the specified large integer
     * dividend and divisor.
     *
     * @param dividend the dividend value.
     * @param divisor the divisor value.
     * @return <code>dividend / divisor</code>
     * @throws ArithmeticException if <code>divisor.isZero()</code>
     */
    public static Rational valueOf(LargeInteger dividend, LargeInteger divisor) {
        return Rational.valueOfNoNormalization(dividend, divisor).normalize();
    }

    private static Rational valueOfNoNormalization(LargeInteger dividend, LargeInteger divisor) {
        Rational r = FACTORY.object();
        r._dividend = dividend;
        r._divisor = divisor;
        return r;
    }

    /**
     * Returns the rational number for the specified integer dividend and 
     * divisor. 
     * 
     * @param dividend the dividend value.
     * @param divisor the divisor value.
     * @return <code>dividend / divisor</code>
     * @throws ArithmeticException if <code>divisor == 0</code>
     */
    public static Rational valueOf(long dividend, long divisor) {
        return Rational.valueOf(LargeInteger.valueOf(dividend), LargeInteger.valueOf(divisor));
    }

    /**
     * Returns the rational number for the specified character sequence.
     *
     * @param  csq the character sequence.
     * @return <code>TEXT_FORMAT.parse(csq)</code>.
     * @throws IllegalArgumentException if the character sequence does not
     *         contain a parsable number.
     * @see #TEXT_FORMAT
     */
    public static Rational valueOf(CharSequence csq) {
        return TEXT_FORMAT.parse(csq);
    }

    /**
     * Returns the smallest dividend of the fraction representing this
     * rational number.
     * 
     * @return this rational dividend.
     */
    public LargeInteger getDividend() {
        return _dividend;
    }

    /**
     * Returns the smallest divisor of the fraction representing this 
     * rational (always positive).
     * 
     * @return this rational divisor.
     */
    public LargeInteger getDivisor() {
        return _divisor;
    }

    /**
     * Indicates if this rational number is an integer.
     *
     * @return <code>this.getDivisor().equals(1)</code>
     */
    public boolean isInteger() {
        return _divisor.equals(1);
    }

    /**
     * Indicates if this rational number is equal to zero.
     *
     * @return <code>this == 0</code>
     */
    public boolean isZero() {
        return _dividend.isZero();
    }

    /**
     * Indicates if this rational number is greater than zero.
     *
     * @return <code>this &gt; 0</code>
     */
    public boolean isPositive() {
        return _dividend.isPositive();
    }

    /**
     * Indicates if this rational number is less than zero.
     *
     * @return <code>this &lt; 0</code>
     */
    public boolean isNegative() {
        return _dividend.isNegative();
    }

    /**
     * Returns the closest integer value to this rational number.
     * 
     * @return this rational rounded to the nearest integer.
     */
    public LargeInteger round() {
        // round = (2 * dividend ± divisor) / (2 * divisor)
        LargeInteger dividend = isNegative() ? _dividend.times2pow(1).minus(_divisor) : _dividend.times2pow(1).plus(_divisor);
        LargeInteger divisor = _divisor.times2pow(1);
        return dividend.divide(divisor);
    }

    // Implements GroupAdditive.
    public Rational opposite() {
        return Rational.valueOfNoNormalization(_dividend.opposite(), _divisor);
    }

    // Implements GroupAdditive.
    public Rational plus(Rational that) {
        return Rational.valueOf(
                this._dividend.times(that._divisor).plus(
                this._divisor.times(that._dividend)),
                this._divisor.times(that._divisor));
    }

    @Override
    public Rational times(long multiplier) {
        return this.times(Rational.valueOf(multiplier, 1));
    }

    // Implements GroupMultiplicative.
    public Rational times(Rational that) {
        return Rational.valueOf(this._dividend.times(that._dividend),
                this._divisor.times(that._divisor));
    }

    // Implements GroupMultiplicative.
    public Rational reciprocal() {
        if (_dividend.isZero())
            throw new ArithmeticException("Dividend is zero");
        return _dividend.isNegative() ? Rational.valueOfNoNormalization(_divisor.opposite(),
                _dividend.opposite()) : Rational.valueOfNoNormalization(_divisor, _dividend);
    }

    @Override
    public Rational divide(long n) {
        return this.times(Rational.valueOf(1, n));
    }

    @Override
    public Rational divide(Rational that) {
        return Rational.valueOf(this._dividend.times(that._divisor),
                this._divisor.times(that._dividend));
    }

    @Override
    public Rational pow(int exp) {
        return Rational.valueOfNoNormalization(_dividend.pow(exp), _divisor.pow(exp));
    }

    // Implements abstract class Number.
    public Rational abs() {
        return (_dividend.isNegative()) ? Rational.valueOfNoNormalization(_dividend.opposite(), _divisor) : this;
    }

    // Implements abstract class Number.
    public long longValue() {
        return _dividend.divide(_divisor).longValue();
    }

    // Implements abstract class Number.
    public double doubleValue() {
        // Closest double value at least for less than 52 bits dividend, divisors.
        return _dividend.doubleValue() / _divisor.doubleValue();
    }

    // Implements abstract class Number.
    public BigDecimal decimalValue() {
        return new BigDecimal(_dividend.asBigInteger()).divide(new BigDecimal(_divisor.asBigInteger()));
    }

    // Implements abstract class Number.
    public int compareTo(Rational that) {
        return this._dividend.times(that._divisor).compareTo(
                that._dividend.times(this._divisor));
    }

    @Override
    public Rational copy() {
        return Rational.valueOfNoNormalization(_dividend.copy(), _divisor.copy());
    }

    // Returns the normalized/canonical form of this rational.
    private Rational normalize() {
        if (_divisor.isZero())
            throw new ArithmeticException("Zero divisor");
        if (_divisor.isPositive()) {
            LargeInteger gcd = _dividend.gcd(_divisor);
            if (!gcd.equals(LargeInteger.ONE)) {
                _dividend = _dividend.divide(gcd);
                _divisor = _divisor.divide(gcd);
            }
            return this;
        } else {
            _dividend = _dividend.opposite();
            _divisor = _divisor.opposite();
            return normalize();
        }
    }
    private static final long serialVersionUID = 1L;

}
