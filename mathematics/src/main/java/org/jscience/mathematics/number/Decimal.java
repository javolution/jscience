/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javolution.context.LocalContext;
import javolution.context.ObjectFactory;
import javolution.text.CharSet;
import javolution.text.Cursor;
import javolution.text.TextBuilder;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;

/**
 * <p> This class represents a floating point decimal number of arbitrary
 *     precision. A decimal number consists of an integer {@link #getSignificand
 *     significand} and a power of ten {@link #getExponent exponent}:
 *     (<code>significand · 10<sup>exponent</sup></code>).</p>
 * 
 * <p> The number of significand digits used by decimal is adjustable
 *     and context-based (can be made local to the current thread using 
 *     Javolution context).
 *     [code]
 *         Decimal two = Decimal.valueOf(2);
 *         LocalContext.enter();
 *         try {
 *              Decimal.setDigits(30); // 30 digits calculations.
 *              System.out.println(two.sqrt());
 *         } finally {
 *              LocalContext.exit(); // Reverts to previous settings.
 *         }
 *
 *         >   0.141421356237309504880168872420E1
 *     [/code]</p>
 *
 * <p> Instances of this class can be utilized to find approximate
 *     solutions to linear equations using the
 *     {@link org.jscience.mathematics.vector.Matrix Matrix} class for which
 *     high-precision decimal is often required, the primitive type
 *     <code>double</code> being not accurate enough to resolve equations
 *     when the matrix's size exceeds 100x100.</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, November 20, 200(
 * @see <a href="http://en.wikipedia.org/wiki/Floating_point">
 *      Wikipedia: decimal</a>
 */
public final class Decimal extends NumberField<Decimal> {

    /**
     * Holds the default text format for decimal numbers (same formatting
     * as for <code>double</code> numbers, for example: "0.003", "-12.3E-5").
     *
     * @see TextFormat#getDefault
     */
    protected static final TextFormat<Decimal> TEXT_FORMAT = new TextFormat<Decimal>(Decimal.class) {

        public Appendable format(Decimal decimal, Appendable out)
                throws IOException {
            if (decimal == NaN)
                return out.append("NaN");
            LargeInteger significand = decimal.getSignificand();
            if (significand.isZero())
                return out.append("0.0");
            if (significand.isNegative()) {
                out.append('-');
                significand = significand.opposite();
            }
            int digits = significand.digitLength();
            int exponent = decimal.getExponent();
            // Try not to show the exponent.
            if (exponent < 0) {
                int dotPos = digits + exponent;
                switch (dotPos) {
                    case 0:
                        return LargeInteger.TEXT_FORMAT.format(significand, out.append("0."));
                    case -1:
                        return LargeInteger.TEXT_FORMAT.format(significand, out.append("0.0"));
                    case -2:
                        return LargeInteger.TEXT_FORMAT.format(significand, out.append("0.00"));
                    case -3:
                        return LargeInteger.TEXT_FORMAT.format(significand, out.append("0.000"));
                    default:
                        if (dotPos > 0) { // Inserts dot, e.g. xxx.xxx
                            TextBuilder tmp = TextBuilder.newInstance();
                            try {
                                LargeInteger.TEXT_FORMAT.format(significand, tmp);
                                tmp.insert(dotPos, ".");
                                return out.append(tmp);
                            } finally {
                                TextBuilder.recycle(tmp);
                            }
                        }
                }
            } else { // Positive exponent or zero.
                switch (exponent) {
                    case 0:
                        return LargeInteger.TEXT_FORMAT.format(significand, out).append(".0");
                    case 1:
                        return LargeInteger.TEXT_FORMAT.format(significand, out).append("0.0");
                    case 2:
                        return LargeInteger.TEXT_FORMAT.format(significand, out).append("00.0");
                    case 3:
                        return LargeInteger.TEXT_FORMAT.format(significand, out).append("000.0");
                }
            }
            // Scientific notation 0.xxxExx
            out.append("0.");
            LargeInteger.TEXT_FORMAT.format(significand, out);
            out.append('E');
            return TypeFormat.format(exponent + digits, out);
        }

        // Expect xxx.xxxxxExx or NaN
        public Decimal parse(CharSequence csq, Cursor cursor) {
            if (cursor.skip("NaN", csq))
                return Decimal.NaN;
            if (cursor.skip('-', csq))
                return parse(csq, cursor).opposite();
            LargeInteger significand = LargeInteger.TEXT_FORMAT.parse(csq, cursor);
            LargeInteger fraction = LargeInteger.ZERO;
            int fractionDigits = 0;
            if (cursor.skip('.', csq)) {
                while (cursor.skip('0', csq)) {
                    fractionDigits++;
                }
                fraction = LargeInteger.TEXT_FORMAT.parse(csq, cursor);
                if (!LargeInteger.ZERO.equals(fraction))
                    fractionDigits += fraction.digitLength();
            }
            int exponent = cursor.skip(CharSet.valueOf('E', 'e'), csq) ? TypeFormat.parseInt(csq, 10, cursor) : 0;
            return Decimal.valueOf(significand.E(fractionDigits).plus(fraction), exponent - fractionDigits);
        }
    };

    /**
     * Holds the factory constructing decimal instances.
     */
    private static final ObjectFactory<Decimal> FACTORY = new ObjectFactory<Decimal>() {

        protected Decimal create() {
            return new Decimal();
        }
    };

    /**
     * The decimal instance representing the additive identity.
     */
    public static final Decimal ZERO = new Decimal(
            LargeInteger.ZERO, 0);

    /**
     * The decimal instance representing the multiplicative identity.
     */
    public static final Decimal ONE = new Decimal(LargeInteger.ONE,
            0);

    /** 
     * The Not-a-Number instance (unique). 
     */
    public static final Decimal NaN = new Decimal(
            LargeInteger.ZERO, Integer.MAX_VALUE);

    /**
     * Holds the number of digits to be used (default 20 digits).
     */
    private static final LocalContext.Reference<Integer> DIGITS_PRECISION = new LocalContext.Reference<Integer>(
            20);

    /**
     * Holds the significand value.
     */
    private LargeInteger _significand;

    /**
     * Holds the power of 10 exponent.
     */
    private int _exponent;

    /**
     * Default constructor. 
     */
    private Decimal() {
    }

    /**
     * Creates a decimal number always on the heap independently from the
     * current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     * 
     * @param significand the significand.
     * @param exponent the power of ten exponent.
     */
    public Decimal(LargeInteger significand, int exponent) {
        _significand = significand;
        _exponent = exponent;
    }

    /**
     * Convenience method equivalent to
     * {@link #Decimal(org.jscience.mathematics.number.LargeInteger, int)
     * Decimal(new LargeInteger(significand), error)}.
     *
     * @param significand the significand.
     * @param exponent the power of ten exponent.
     */
    public Decimal(long significand, int exponent) {
        this(new LargeInteger(significand), exponent);
    }

    /**
     * Returns the decimal number for the specified {@link
     * LargeInteger} significand and power of ten exponent.
     * 
     * @param significand the significand value.
     * @param exponent the power of ten exponent.
     * @return <code>(significand · 10<sup>exponent</sup></code>
     */
    public static Decimal valueOf(LargeInteger significand, int exponent) {
        Decimal fp = FACTORY.object();
        fp._significand = significand;
        fp._exponent = exponent;
        return fp;
    }

    /**
     * Convenience method equivalent to
     * {@link #valueOf(org.jscience.mathematics.number.LargeInteger, int)
     * Decimal.valueOf(LargeInteger.valueOf(significand), exponent)
     *
     * @param significand the scaled value.
     * @param exponent the power of ten exponent.
     * @return the decimal number <code>(significand · 10<sup>pow10</sup></code>
     */
    public static Decimal valueOf(long significand, int exponent) {
        return Decimal.valueOf(LargeInteger.valueOf(significand), exponent);
    }

    /**
     * Convenience method equivalent to
     * {@link #valueOf(org.jscience.mathematics.number.LargeInteger, int)
     * Decimal.valueOf(value, 0) }
     *
     * @param value the integral value.
     * @return the decimal number <code>(value)</code>
     */
    public static Decimal valueOf(LargeInteger value) {
        return Decimal.valueOf(value, 0);
    }

    /**
     * Convenience method equivalent to
     * {@link #valueOf(org.jscience.mathematics.number.LargeInteger, int)
     * Decimal.valueOf(LargeInteger.valueOf(value), 0) }
     *
     * @param value the integral value.
     * @return the fixed point number <code>(value)</code>
     */
    public static Decimal valueOf(long value) {
        return Decimal.valueOf(LargeInteger.valueOf(value), 0);
    }

    /**
     * Returns the decimal number for the specified character sequence.
     *
     * @param  csq the character sequence.
     * @return <code>TEXT_FORMAT.parse(csq)</code>.
     * @throws IllegalArgumentException if the character sequence does not
     *         contain a parsable number.
     * @see #TEXT_FORMAT
     */
    public static Decimal valueOf(CharSequence csq) {
        return TEXT_FORMAT.parse(csq);
    }

    /**
     * Returns the decimal number corresponding to the specified
     * {@link BigDecimal} value.
     *
     * @param  bigDecimal the big decimal value.
     * @return the corresponding decimal number.
     * @see #asBigDecimal()
     */
    public static Decimal valueOf(BigDecimal bigDecimal) {
        BigInteger significand = bigDecimal.unscaledValue();
        int scale = bigDecimal.scale();
        return Decimal.valueOf(LargeInteger.valueOf(significand), -scale);
    }

    /**
     * Returns the {@link javolution.context.LocalContext local} number of 
     * significand digits used during calculations (default 20 digits).
     * 
     * @return the number of digits.
     */
    public static int getDigits() {
        return DIGITS_PRECISION.get();
    }

    /**
     * Sets the {@link javolution.context.LocalContext local} number of 
     * significand digits to be used during calculations.
     * 
     * @param digits the number of digits.
     * @throws IllegalArgumentException if <code>digits &lt;= 0</code>
     */
    public static void setDigits(int digits) {
        if (digits <= 0)
            throw new IllegalArgumentException("digits: " + digits + " has to be greater than 0");
        DIGITS_PRECISION.set(digits);
    }

    /**
     * Returns the <a href="http://en.wikipedia.org/wiki/Significand">
     * significand</a> value.
     *
     * @return this decimal significand.
     */
    public LargeInteger getSignificand() {
        return _significand;
    }

    /**
     * Returns the power of ten exponent.
     * 
     * @return the exponent.
     */
    public int getExponent() {
        return _exponent;
    }

    /**
     * Indicates if this decimal number is equal to zero.
     *
     * @return <code>this == 0</code>
     */
    public boolean isZero() {
        return _significand.isZero() && (this != NaN);
    }

    /**
     * Indicates if this decimal number is greater than zero.
     *
     * @return <code>this &gt; 0</code>
     */
    public boolean isPositive() {
        return _significand.isPositive();
    }

    /**
     * Indicates if this rational number is less than zero.
     *
     * @return <code>this &lt; 0</code>
     */
    public boolean isNegative() {
        return _significand.isNegative();
    }

    /**
     * Indicates if this decimal is Not-a-Number.
     *
     * @return <code>true</code> if this number has unbounded value;
     *         <code>false</code> otherwise.
     */
    public boolean isNaN() {
        return this == NaN;
    }

    /**
     * Returns the closest decimal that is less than or equal to this
     * decimal and is equal to a mathematical integer.
     *
     * @return  a decimal that less than or equal to this decimal
     *          and is equal to a mathematical integer.
     */
    public Decimal floor() {
        if (this == NaN)
            return NaN;
        LargeInteger integralPart = _significand.E(_exponent);
        return Decimal.valueOf(isNegative() ? integralPart.minus(LargeInteger.ONE) : integralPart);
    }

    /**
     * Returns the closest decimal that is greater than or equal to this
     * decimal and is equal to a mathematical integer.
     *
     * @return  a decimal that greater than or equal to this decimal
     *          and is equal to a mathematical integer.
     */
    public Decimal ceil() {
        if (this == NaN)
            return NaN;
        LargeInteger integralPart = _significand.E(_exponent);
        return Decimal.valueOf(isNegative() ? integralPart : integralPart.plus(LargeInteger.ONE));
    }

    /**
     * Returns the closest integer value to this decimal number.
     *
     * @return <code>(LargeInteger) (this + 0.5).floor() </code>
     * @throws ArithmeticException if this decimal {@link #isNaN()}.
     */
    public LargeInteger round() {
        if (this == NaN)
            throw new ArithmeticException("Cannot convert NaN to integer value");
        Decimal fp = this.plus(Decimal.valueOf(5, -1)).floor();
        return fp._significand.E(fp._exponent);
    }

    /**
     * Returns the square root of this decimal number.
     * If this fixed point is negative {@link #NaN} is returned.
     *
     * @return the positive square root of this decimal number.
     */
    public Decimal sqrt() {
        if ((this == NaN) | this.isNegative())
            return NaN;
        int digitsShift = DIGITS_PRECISION.get() * 2 - _significand.digitLength();
        int exp = _exponent - digitsShift;
        if ((exp & 1) == 1) { // Ensures that exp is even.
            digitsShift++;
            exp--;
        }
        LargeInteger scaledValue = _significand.E(digitsShift);
        return Decimal.valueOf(scaledValue.sqrt(), exp >> 1).normalize();
    }

    // Implements GroupAdditive.
    public Decimal opposite() {
        if (this == NaN)
            return NaN;
        return Decimal.valueOf(_significand.opposite(), _exponent);
    }

    // Implements GroupAdditive.
    public Decimal plus(Decimal that) {
        if ((this == NaN) | (that == NaN))
            return NaN;
        if (this._exponent > that._exponent)
            return that.plus(this);
        int pow10Scaling = that._exponent - this._exponent;
        LargeInteger thatScaled = that._significand.times10pow(pow10Scaling);
        return Decimal.valueOf(_significand.plus(thatScaled), _exponent).normalize();
    }

    @Override
    public Decimal times(long multiplier) {
        return this.times(Decimal.valueOf(multiplier));
    }

    // Implements GroupMultiplicative.
    public Decimal times(Decimal that) {
        if ((this == NaN) | (that == NaN))
            return NaN;
        return Decimal.valueOf(
                this._significand.times(that._significand),
                this._exponent + that._exponent).normalize();
    }

    // Implements GroupMultiplicative
    public Decimal reciprocal() {
        if (_significand.isZero())
            return NaN;
        int pow10 = DIGITS_PRECISION.get() + _significand.digitLength();
        LargeInteger dividend = LargeInteger.ONE.times10pow(pow10);
        return Decimal.valueOf(dividend.divide(_significand),
                -pow10 - _exponent).normalize();
    }

    @Override
    public Decimal divide(long n) {
        return this.divide(Decimal.valueOf(n));
    }

    @Override
    public Decimal divide(Decimal that) {
        if ((this.isNaN()) | (that._significand.isZero()))
            return NaN;
        int pow10 = DIGITS_PRECISION.get() + that._significand.digitLength();
        LargeInteger dividend = _significand.E(pow10);
        return Decimal.valueOf(dividend.divide(that._significand),
                this._exponent - pow10 - that._exponent).normalize();
    }

    // Implements abstract class Number.
    public Decimal abs() {
        return this._significand.isNegative() ? this.opposite() : this;
    }

    // Implements abstract class Number.
    public long longValue() {
        if (this == NaN)
            return Long.MAX_VALUE;
        return _significand.E(_exponent).longValue();
    }

    // Implements abstract class Number.
    public double doubleValue() {
        if (this == NaN)
            return Double.NaN;
        return FixedPoint.valueOf(_significand, _exponent).doubleValue();
    }

    // Implements abstract class Number.
    public BigDecimal decimalValue() {
        return new BigDecimal(_significand.asBigInteger(), -_exponent);
    }

    // Implements abstract class Number.
    public int compareTo(Decimal that) {
        if (this.isNaN())
            return that.isNaN() ? 0 : 1;
        if (that.isNaN())
            return -1; // NaN is considered greater than !NaN

        // Delegate to fixed point.
        FixedPoint thisFP = FixedPoint.valueOf(this._significand, this._exponent);
        FixedPoint thatFP = FixedPoint.valueOf(that._significand, that._exponent);
        return thisFP.compareTo(thatFP);
    }

    // Implements abstract class Number.
    public Decimal copy() {
        if (this == NaN)
            return NaN; // Maintains unicity.
        return Decimal.valueOf(_significand, _exponent);
    }

    // Returns this decimal number after normalization based upon
    // the number of digits to be used.
    private Decimal normalize() {
        int digits = Decimal.getDigits();
        int thisDigits = this._significand.digitLength();
        if (thisDigits > digits) { // Scale down.
            int pow10 = digits - thisDigits; // Negative.
            _significand = _significand.E(pow10);
            long exponent = ((long) _exponent) - pow10;
            if (exponent > Integer.MAX_VALUE)
                return NaN;
            if (exponent < Integer.MIN_VALUE)
                return ZERO;
            _exponent = (int) exponent;
        }
        return this;
    }
    private static final long serialVersionUID = 1L;

}
