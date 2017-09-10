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
import java.math.BigInteger;

import javolution.context.ArrayFactory;
import javolution.context.ConcurrentContext;
import javolution.context.ObjectFactory;
import javolution.context.StackContext;
import javolution.lang.Configurable;
import javolution.lang.MathLib;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;
import javolution.text.Cursor;
import static org.jscience.mathematics.number.Calculus.*;

/**
 * <p> This class represents an immutable integer number of arbitrary size.</p>
 * 
 * <p> It has the following advantages over the 
 *     <code>java.math.BigInteger</code> class:
 * <ul>
 *     <li> Optimized for 64 bits architectures. But still runs significantly 
 *          faster on 32 bits processors.</li>
 *     <li> Real-time compliant for improved performance and predictability
 *          (no garbage generated when executing in 
 *          {@link javolution.context.StackContext StackContext}).</li>
 *     <li> Improved algorithms (e.g. Concurrent Karatsuba multiplication in
 *          O(n<sup>Log3</sup>) instead of O(n<sup>2</sup>).</li>
 * </ul></p>
 * 
 * <p> <b>Note:</b> This class uses {@link ConcurrentContext ConcurrentContext}
 *     to accelerate calculations on multi-cores systems.</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, November 20, 2009
 * @see <a href="http://en.wikipedia.org/wiki/Arbitrary-precision_arithmetic">
 *      Wikipedia: Arbitrary-precision Arithmetic</a>
 */
public final class LargeInteger extends Number<LargeInteger> {

    /**
     * Holds the certainty required when testing for primality
     * (default <code>100</code>, the probability for a composite to
     * pass the primality test is less than <code>2<sup>-100</sup></code>).
     */
    public static final Configurable<Integer> PRIME_CERTAINTY = new Configurable<Integer>(
            100) {
    };

    /**
     * Holds the default text format for large integers numbers (decimal representation).
     *
     * @see TextFormat#getDefault
     * @see #format(org.jscience.mathematics.number.LargeInteger, int, java.lang.Appendable)
     * @see #parse(java.lang.CharSequence, int, javolution.text.Cursor)
     */
    protected static final TextFormat<LargeInteger> TEXT_FORMAT =
            new TextFormat<LargeInteger>(LargeInteger.class) {

                @Override
                public Appendable format(LargeInteger li, Appendable out)
                        throws IOException {
                    return LargeInteger.format(li, 10, out);
                }

                @Override
                public LargeInteger parse(CharSequence csq, Cursor cursor) {
                    return LargeInteger.parse(csq, 10, cursor);
                }
            };

    /**
     * Holds factory for LargeInteger with variable size arrays.
     */
    private static final ArrayFactory<LargeInteger> ARRAY_FACTORY = new ArrayFactory<LargeInteger>() {

        @Override
        protected LargeInteger create(int capacity) {
            return new LargeInteger(capacity);
        }
    };

    /**
     * Holds the factory for LargeInteger with no intrinsic array (wrapper instances).
     */
    private static final ObjectFactory<LargeInteger> NO_ARRAY_FACTORY = new ObjectFactory<LargeInteger>() {

        @Override
        protected LargeInteger create() {
            return new LargeInteger();
        }
    };

    /**
     * Holds small integers values.
     */
    private static final LargeInteger[] SMALL_INTEGERS = new LargeInteger[16];

    static {
        for (int i = 0; i < SMALL_INTEGERS.length; i++) {
            SMALL_INTEGERS[i] = new LargeInteger((long) i);
        }
    }
    /**
     * The large integer representing the additive identity.
     */
    public static final LargeInteger ZERO = SMALL_INTEGERS[0];

    /**
     * The large integer representing the multiplicative identity.
     */
    public static final LargeInteger ONE = SMALL_INTEGERS[1];

    /**
     * Holds Long.MIN_VALUE
     */
    private static final LargeInteger LONG_MIN_VALUE = new LargeInteger(Long.MIN_VALUE);

    /**
     * Holds the remainder after a {@link #divide} operation.
     */
    private LargeInteger _remainder;

    /**
     * Indicates if this large integer is negative.
     */
    private boolean _isNegative;

    /**
     * The size of this large integer in words. 
     * The most significand word different from 0 is at index: _size-1
     */
    private int _size;

    /**
     * This large integer positive words (63 bits). 
     * Least significant word first (index 0).
     */
    private long[] _words;

    /**
     * Default constructor (no words array).
     */
    private LargeInteger() {
    }

    /**
     * Creates a large integer number always on the heap independently from the
     * current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     *
     * <p>Note: This method differs from {@link #valueOf(java.lang.CharSequence)}
     *          in the sense that it is independant from the local
     *          {@link TextFormat#getInstance(java.lang.Class) text format}
     *          setting for large integer.</p>
     *
     * @param  csq the decimal value.
     * @return the corresponding large integer.
     * @throws NumberFormatException if error when parsing.
     */
    public LargeInteger(CharSequence csq) {
        LargeInteger value = TEXT_FORMAT.parse(csq);
        _isNegative = value._isNegative;
        _size = value._size;
        _words = (long[]) value._words.clone(); // Always on the heap.
    }

    /**
     * Convenience method equivalent to {@link #LargeInteger(java.lang.CharSequence)
     * LargeInteger(String.valueOf(significand))} (but faster).
     *
     * @param longValue the value as <code>long</code> of this large integer.
     */
    public LargeInteger(long longValue) {
        _isNegative = longValue < 0;
        if (longValue == Long.MIN_VALUE) {
            _words = new long[2];
            _words[1] = 1;
            _size = 2;
        } else if (longValue == 0) {
            _words = new long[1];
            _size = 0;
        } else {
            _words = new long[1];
            _words[0] = _isNegative ? -longValue : longValue;
            _size = 1;
        }
    }

    /**
     * Creates a large integer with the specified 63 bits word capacity.
     * 
     * @link wordLength the internal positive <code>long</code> array length.
     */
    private LargeInteger(int wordLength) {
        _words = new long[wordLength];
    }

    /**
     * Returns the large integer of specified <code>long</code> value.
     * 
     * @param  value the <code>long</code> value.
     * @return the corresponding large integer number.
     */
    public static LargeInteger valueOf(long value) {
        if (value < 0) {
            if (value == Long.MIN_VALUE)
                return LONG_MIN_VALUE;
        } else if (value < SMALL_INTEGERS.length)
            return SMALL_INTEGERS[(int) value];
        LargeInteger li = ARRAY_FACTORY.array(1);
        li._isNegative = value < 0;
        li._words[0] = MathLib.abs(value);
        li._size = 1;
        return li;
    }

    /**
     * Returns the large integer of specified two's-complement binary
     * representation. The input array is assumed to be in <i>big-endian</i>
     * byte-order: the most significant byte is at the offset position.
     * 
     * @param  bytes the binary representation (two's-complement).
     * @param  offset the offset at which to start reading the bytes.
     * @param  length the maximum number of bytes to read.
     * @return the corresponding large integer number.
     * @throws IndexOutOfBoundsException 
     *         if <code>offset + length > bytes.length</code>  
     * @see    #toByteArray
     */
    public static LargeInteger valueOf(byte[] bytes, int offset, int length) {
        // Ensures result is large enough (takes into account potential
        // extra bits during negative to positive conversion).
        LargeInteger li = ARRAY_FACTORY.array(((length * 8 + 1) / 63) + 1);
        final boolean isNegative = bytes[offset] < 0;
        int wordIndex = 0;
        int bitIndex = 0;
        li._words[0] = 0;
        for (int i = offset + length; i > offset; bitIndex += 8) {
            long bits = (isNegative ? ~bytes[--i] : bytes[--i]) & MASK_8;
            if (bitIndex < 63 - 8) {
                li._words[wordIndex] |= bits << bitIndex;
            } else { // End of word reached.
                li._words[wordIndex] |= (bits << bitIndex) & MASK_63;
                bitIndex -= 63; // In range [-8..-1]
                li._words[++wordIndex] = bits >> -bitIndex;
            }
        }
        // Calculates size.
        while (li._words[wordIndex] == 0) {
            if (--wordIndex < 0)
                break;
        }
        if (isNegative && wordIndex < 0)
            wordIndex = 0; // special case for -1
        li._size = wordIndex + 1;
        li._isNegative = isNegative;

        // Converts one's-complement to two's-complement if negative.
        if (isNegative) { // Adds ONE.
            li._size = Calculus.add(li._words, li._size, ONE._words, 1,
                    li._words);
        }
        return li;
    }

    /**
     * Returns the two's-complement binary representation of this 
     * large integer. The output array is in <i>big-endian</i>
     * byte-order: the most significant byte is at the offset position.
     * 
     * @param  bytes the bytes to hold the binary representation 
     *         (two's-complement) of this large integer.
     * @param  offset the offset at which to start writing the bytes.
     * @return the number of bytes written.
     * @throws IndexOutOfBoundsException 
     *         if <code>bytes.length < (bitLength() >> 3) + 1</code>  
     * @see    #valueOf(byte[], int, int)
     * @see    #bitLength
     */
    public int toByteArray(byte[] bytes, int offset) {
        int bytesLength = (bitLength() >> 3) + 1;
        int wordIndex = 0;
        int bitIndex = 0;
        if (_isNegative) {
            long word = _words[0] - 1;
            long borrow = word >> 63; // -1 if borrow
            word = ~word & MASK_63;
            for (int i = bytesLength + offset; i > offset; bitIndex += 8) {
                if (bitIndex < 63 - 8) {
                    bytes[--i] = (byte) word;
                    word >>= 8;
                } else { // End of word reached.
                    byte bits = (byte) word;
                    word = (++wordIndex < _size) ? _words[wordIndex] + borrow
                            : borrow;
                    borrow = word >> 63; // -1 if borrow
                    word = ~word & MASK_63;
                    bitIndex -= 63; // In range [-8..-1]
                    bytes[--i] = (byte) ((word << -bitIndex) | bits);
                    word >>= (8 + bitIndex);
                }
            }
        } else {
            if (_size != 0) {
                long word = _words[0];
                for (int i = bytesLength + offset; i > offset; bitIndex += 8) {
                    if (bitIndex < 63 - 8) {
                        bytes[--i] = (byte) word;
                        word >>= 8;
                    } else { // End of word reached.
                        byte bits = (byte) word;
                        word = (++wordIndex < _size) ? _words[wordIndex] : 0;
                        bitIndex -= 63; // In range [-8..-1]
                        bytes[--i] = (byte) ((word << -bitIndex) | bits);
                        word >>= (8 + bitIndex);
                    }
                }
            } else { // ZERO
                bytes[offset] = 0;
            }
        }
        return bytesLength;
    }

    /**
     * Returns the large integer for the specified character sequence.
     *
     * @param  csq the character sequence.
     * @return <code>TEXT_FORMAT.parse(csq)</code>.
     * @throws IllegalArgumentException if the character sequence does not
     *         contain a parsable number.
     * @see #TEXT_FORMAT
     */
    public static LargeInteger valueOf(CharSequence csq) {
        return TEXT_FORMAT.parse(csq);
    }

    /**
     * Returns the large integer for the specified character sequence in
     * the specified radix.
     * 
     * @param  csq the character sequence to parse.
     * @param radix the radix of the representation.
     * @return <code>LargeInteger.parse(csq, radix, cursor)</code>
     * @throws NumberFormatException if error when parsing.
     */
    public static LargeInteger valueOf(CharSequence csq, int radix) {
        Cursor cursor = new Cursor();
        LargeInteger num = LargeInteger.parse(csq, radix, cursor);
        if (cursor.getIndex() < csq.length())
            throw new IllegalArgumentException(
                    "Extraneous characters in \"" + csq + "\"");
        return num;
    }

    /**
     * Returns the large integer corresponding to the specified 
     * <code>java.math.BigInteger</code> instance.
     * 
     * @param  bigInteger the big integer instance.
     * @return the large integer having the same value.
     */
    public static LargeInteger valueOf(BigInteger bigInteger) {
        byte[] bytes = bigInteger.toByteArray();
        return LargeInteger.valueOf(bytes, 0, bytes.length);
    }

    /**
     * Indicates if this large integer is equal to {@link #ZERO}.
     *
     * @return <code>this == 0</code>
     */
    public boolean isZero() {
        return _size == 0;
    }

    /**
     * Indicates if this large integer is greater than {@link #ZERO}
     * ({@link #ZERO}is not included).
     * 
     * @return <code>this &gt; 0</code>
     */
    public boolean isPositive() {
        return !_isNegative && (_size != 0);
    }

    /**
     * Indicates if this large integer is less than {@link #ZERO}.
     * 
     * @return <code>this &lt; 0</code>
     */
    public boolean isNegative() {
        return _isNegative;
    }

    /**
     * Indicates if this large integer is an even number.
     * 
     * @return <code>(this & 1) == ZERO</code>
     */
    public boolean isEven() {
        return (_size == 0) || ((_words[0] & 1) == 0);
    }

    /**
     * Indicates if this large integer is an odd number.
     * 
     * @return <code>(this & 1) != ZERO</code>
     */
    public boolean isOdd() {
        return !isEven();
    }

    /**
     * Indicates if this large integer is probably prime.
     * 
     * @return <code>true</code> if this large integer is probable prime;
     *         <code>false</code> otherwise.
     */
    public boolean isProbablyPrime() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Returns the minimal number of bits to represent this large integer
     * in the minimal two's-complement (sign excluded).
     * 
     * @return the length of this integer in bits (sign excluded).
     */
    public int bitLength() {
        if (_size == 0)
            return 0;
        final int n = _size - 1;
        final int bitLength = MathLib.bitLength(_words[n]) + (n << 6) - n;
        return (this.isNegative() && this.isPowerOfTwo()) ? bitLength - 1
                : bitLength;
    }

    /**
     * Returns the minimal number of decimal digits necessary to represent 
     * this large integer (sign excluded).
     * 
     * @return the maximum number of digits.
     */
    public int digitLength() {
        if (_size <= 1)
            return MathLib.digitLength(_words[0]);
        return this.divide(1000000000).digitLength() + 9;
    }

    /**
     * Indicates if this number is a power of two (equals to 2<sup>
     * ({@link #bitLength bitLength()} - 1)</sup>).
     * 
     * @return <code>true</code> if this number is a power of two; 
     *         <code>false</code> otherwise.
     */
    public boolean isPowerOfTwo() {
        if (_size == 0)
            return false;
        final int n = _size - 1;
        for (int j = 0; j < n; j++) {
            if (_words[j] != 0)
                return false;
        }
        final int bitLength = MathLib.bitLength(_words[n]);
        return _words[n] == (1L << (bitLength - 1));
    }

    /**
     * Returns the index of the lowest-order one bit in this large integer
     * or <code>-1</code> if <code>this.equals(ZERO)</code>.
     *
     * @return the index of the rightmost bit set or <code>-1</code>
     */
    public int getLowestSetBit() {
        if (_size == 0)
            return -1;
        for (int i = 0;; i++) {
            long w = _words[i];
            if (w == 0)
                continue;
            for (int j = 0;; j++) {
                if (((1L << j) & w) != 0)
                    return i * 63 + j;
            }
        }
    }

    /**
     * Returns the final undivided part after division that is less or of 
     * lower degree than the divisor. This value is only set by the 
     * {@link #divide} operation and is not considered as part of 
     * this large integer (ignored by all methods).
     * 
     * @return the remainder of the division for which this large integer
     *         is the quotient.
     */
    public LargeInteger getRemainder() {
        return _remainder;
    }

    /**
     * Returns the sum of this large integer with the specified
     * <code>long</code> integer (convenience method)
     *
     * @param value the <code>long</code> integer being added.
     * @return <code>this + value</code>.
     */
    public LargeInteger plus(long value) {
        return this.plus(LargeInteger.valueOf(value));
    }

    /**
     * Returns the difference between this large integer and the specified
     * value
     * 
     * @param value the value to be subtracted.
     * @return <code>this - value</code>.
     */
    public LargeInteger minus(long value) {
        return this.minus(LargeInteger.valueOf(value));
    }

    /**
     * Returns this large integer divided by the one specified (integer
     * division). The remainder of this division is accessible using 
     * {@link #getRemainder}. 
     * 
     * @param that the integer divisor.
     * @return <code>this / that</code> and <code>this % that</code> 
     *        ({@link #getRemainder})
     * @throws ArithmeticException if <code>that.equals(ZERO)</code>
     */
    public LargeInteger divide(LargeInteger that) {
        if ((that._size <= 1) && ((that._words[0] >> 31) == 0))
            return divide(that.intValue());
        LargeInteger result;
        LargeInteger remainder;
        LargeInteger thisAbs = this.abs();
        LargeInteger thatAbs = that.abs();
        int precision = thisAbs.bitLength() - thatAbs.bitLength() + 1;
        if (precision <= 0) {
            result = LargeInteger.ZERO;
            remainder = this;
        } else {
            LargeInteger thatReciprocal = thatAbs.inverseScaled(precision);
            result = thisAbs.times(thatReciprocal);
            result = result.shiftRight(thisAbs.bitLength() + 1);

            // Calculates remainder, corrects for result +/- 1 error. 
            remainder = thisAbs.minus(thatAbs.times(result));
            if (remainder.compareTo(thatAbs) >= 0) {
                remainder = remainder.minus(thatAbs);
                result = result.plus(LargeInteger.ONE);
                if (remainder.compareTo(thatAbs) >= 0)
                    throw new Error("Verification error for " + this + "/" + that + ", please submit a bug report.");
            } else if (remainder.isNegative()) {
                remainder = remainder.plus(thatAbs);
                result = result.minus(ONE);
                if (remainder.isNegative())
                    throw new Error("Verification error for " + this + "/" + that + ", please submit a bug report.");
            }
        }
        // Setups result and remainder.
        LargeInteger li = NO_ARRAY_FACTORY.object();
        li._words = result._words;
        li._size = result._size;
        li._isNegative = (this._isNegative != that._isNegative) && (result._size != 0);
        li._remainder = _isNegative ? remainder.opposite() : remainder;
        return li;
    }

    /**
     * Returns this large integer divided by the specified <code>int</code>
     * divisor. The remainder of this division is accessible using 
     * {@link #getRemainder}. 
     * 
     * @param divisor the <code>int</code> divisor.
     * @return <code>this / divisor</code> and <code>this % divisor</code>
     *        ({@link #getRemainder})
     * @throws ArithmeticException if <code>divisor == 0</code>
     */
    public LargeInteger divide(int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by zero");
        if (divisor == Integer.MIN_VALUE) { // abs(divisor) would overflow.
            LargeInteger li = this.times2pow(-31).copy();
            li._isNegative = !_isNegative && (li._size != 0);
            li._remainder = _isNegative ? LargeInteger.valueOf(-(_words[0] & MASK_31)) : LargeInteger.valueOf(_words[0] & MASK_31);
            return li;
        }
        LargeInteger li = ARRAY_FACTORY.array(_size);
        long rem = Calculus.divide(_words, _size, MathLib.abs(divisor),
                li._words);
        li._size = (_size > 0) && (li._words[_size - 1] == 0L) ? _size - 1
                : _size;
        li._isNegative = (_isNegative != (divisor < 0)) && (li._size != 0);
        li._remainder = LargeInteger.valueOf(_isNegative ? -rem : rem);
        return li;
    }

    /**
     * Returns the remainder of the division of this large integer with 
     * the one specified (convenience method equivalent to 
     * <code>this.divide(that).getRemainder()</code>).
     *
     * @param that the value by which this integer is to be divided, and the
     *        remainder returned.
     * @return <code>this % that</code>
     * @throws ArithmeticException if <code>that.equals(ZERO)</code>
     * @see #divide(LargeInteger)
     */
    public LargeInteger remainder(LargeInteger that) {
        return this.divide(that).getRemainder();
    }

    /**
     * Returns a scaled approximation of <code>1 / this</code>.
     * 
     * @param precision the requested precision (reciprocal error being ± 1).
     * @return <code>2<sup>(precision + this.bitLength())</sup> / this</code>
     * @throws ArithmeticException if <code>this.isZero()</code>
     */
    public LargeInteger inverseScaled(int precision) {
        if (precision <= 30) { // Straight calculation.
            long divisor = this.shiftRight(this.bitLength() - precision - 1)._words[0];
            long dividend = 1L << (precision * 2 + 1);
            return (this.isNegative()) ? LargeInteger.valueOf(-dividend / divisor) : LargeInteger.valueOf(dividend / divisor);
        } else { // Newton iteration (x = 2 * x - x^2 * this).
            LargeInteger x = inverseScaled(precision / 2 + 1); // Estimate.
            LargeInteger thisTrunc = shiftRight(bitLength() - (precision + 2));
            LargeInteger prod = thisTrunc.times(x).times(x);
            int diff = 2 * (precision / 2 + 2);
            LargeInteger prodTrunc = prod.shiftRight(diff);
            LargeInteger xPad = x.shiftLeft(precision - precision / 2 - 1);
            LargeInteger tmp = xPad.minus(prodTrunc);
            return xPad.plus(tmp);
        }
    }

    /**
     * Returns the integer square root of this integer.
     * 
     * @return <code>k<code> such as <code>k^2 <= this < (k + 1)^2</code>
     * @throws ArithmeticException if this integer is negative.
     */
    public LargeInteger sqrt() {
        if (this.isNegative())
            throw new ArithmeticException("Square root of negative integer");
        int bitLength = this.bitLength();
        if (bitLength <= 1)
            return this; // ZERO or ONE.
        StackContext.enter();
        try {
            // First approximation.
            LargeInteger k = this.times2pow(-((bitLength >> 1) + (bitLength & 1)));
            while (true) {
                LargeInteger newK = (k.plus(this.divide(k))).times2pow(-1);
                if (!newK.minus(k).isLargerThan(ONE)) {
                    if (this.divide(newK).isLessThan(newK)) {
                        newK = newK.minus(ONE);
                    }
                    return StackContext.outerCopy(newK);
                }
                k = newK;
            }
        } finally {
            StackContext.exit();
        }
    }

    /**
     * Returns this large integer modulo the specified large integer. 
     * 
     * <p> Note: The result as the same sign as the divisor unlike the Java 
     *     remainder (%) operator (which as the same sign as the dividend).</p> 
     * 
     * @param m the modulus.
     * @return <code>this mod m</code>
     * @see #getRemainder()
     */
    public LargeInteger mod(LargeInteger m) {
        final LargeInteger li = m.isLargerThan(this) ? this : this.divide(m).getRemainder();
        return (this._isNegative == m._isNegative) ? li : li.plus(m);
    }

    /**
     * Returns the large integer whose value is <code>(this<sup>-1</sup> mod m)
     * </code>.
     *
     * @param  m the modulus.
     * @return <code>this<sup>-1</sup> mod m</code>.
     * @throws ArithmeticException <code> m &lt;= 0</code>, or this integer
     *         has no multiplicative inverse mod m (that is, this integer
     *         is not <i>relatively prime</i> to m).
     */
    public LargeInteger modInverse(LargeInteger m) {
        if (!m.isPositive())
            throw new ArithmeticException("Modulus is not a positive number");
        StackContext.enter();
        try {
            // Extended Euclidian Algorithm
            LargeInteger a = this.mod(m);
            LargeInteger b = m;
            LargeInteger p = ONE;
            LargeInteger q = ZERO;
            LargeInteger r = ZERO;
            LargeInteger s = ONE;
            while (!b.isZero()) {
                LargeInteger quot = a.divide(b);
                LargeInteger c = quot.getRemainder();
                a = b;
                b = c;

                LargeInteger new_r = p.minus(quot.times(r));
                LargeInteger new_s = q.minus(quot.times(s));
                p = r;
                q = s;
                r = new_r;
                s = new_s;
            }
            if (!a.abs().equals(ONE)) // (a != 1) || (a != -1)
                throw new ArithmeticException("GCD(" + this + ", " + m + ") = " + a);
            return StackContext.outerCopy(a.isNegative() ? p.opposite().mod(m) : p.mod(m));
        } finally {
            StackContext.exit();
        }
    }

    /**
     * Returns this large integer raised at the specified exponent modulo 
     * the specified modulus.
     *
     * @param  exp the exponent.
     * @param  m the modulus.
     * @return <code>this<sup>exp</sup> mod m</code>
     * @throws ArithmeticException <code>m &lt;= 0</code>
     * @see    #modInverse
     */
    public LargeInteger modPow(LargeInteger exp, LargeInteger m) {
        if (!m.isPositive())
            throw new ArithmeticException("Modulus is not a positive number");
        if (exp.isPositive()) {
            StackContext.enter();
            try {
                LargeInteger result = null;
                LargeInteger pow2 = this.mod(m);
                while (exp.compareTo(ONE) >= 0) { // Iteration.
                    if (exp.isOdd()) {
                        result = (result == null) ? pow2 : result.times(pow2).mod(m);
                    }
                    pow2 = pow2.times(pow2).mod(m);
                    exp = exp.shiftRight(1);
                }
                return StackContext.outerCopy(result);
            } finally {
                StackContext.exit();
            }
        } else if (exp.isNegative()) {
            return this.modPow(exp.opposite(), m).modInverse(m);
        } else { // exp == 0
            return LargeInteger.ONE;
        }
    }

    /**
     * Returns the greatest common divisor of this large integer and 
     * the one specified.
     * 
     * @param  that the other number to compute the GCD with.
     * @return a positive number or {@link #ZERO} if
     *         <code>(this.isZero() && that.isZero())</code>.
     */
    public LargeInteger gcd(LargeInteger that) {
        if (this.isZero())
            return that;
        if (that.isZero())
            return this;
        StackContext.enter();
        try {
            LargeInteger u = this;
            LargeInteger v = that;

            // Euclidian algorithm until u, v about the same size.
            while (MathLib.abs(u._size - v._size) > 1) {
                LargeInteger tmp = u.divide(v);
                LargeInteger rem = tmp.getRemainder();
                u = v;
                v = rem;
                if (v.isZero())
                    return u;
            }

            // Works with local (modifiable) copies of the inputs.
            u = u.copy();
            u._isNegative = false; // abs()
            v = v.copy();
            v._isNegative = false; // abs()

            // Binary GCD Implementation adapted from
            // http://en.wikipedia.org/wiki/Binary_GCD_algorithm
            final int uShift = u.getLowestSetBit();
            u.shiftRightSelf(uShift);
            final int vShift = v.getLowestSetBit();
            v.shiftRightSelf(vShift);

            // From here on, u is always odd.
            while (true) {
                // Now u and v are both odd, so diff(u, v) is even.
                // Let u = min(u, v), v = diff(u, v)/2.
                if (u.compareTo(v) < 0) {
                    v.subtract(u);
                } else {
                    u.subtract(v);
                    LargeInteger tmp = u;
                    u = v;
                    v = tmp; // Swaps.
                }
                v.shiftRightSelf();
                if (v.isZero())
                    break;
                v.shiftRightSelf(v.getLowestSetBit());
            }
            return StackContext.outerCopy(u.shiftLeft(MathLib.min(uShift, vShift)));
        } finally {
            StackContext.exit();
        }
    }

    private void shiftRightSelf() {
        if (_size == 0)
            return;
        _size = Calculus.shiftRight(0, 1, _words, _size, _words);
    }

    private void shiftRightSelf(int n) {
        if ((n == 0) || (_size == 0))
            return;
        int wordShift = n < 63 ? 0 : n / 63;
        int bitShift = n - ((wordShift << 6) - wordShift); // n - wordShift * 63
        _size = Calculus.shiftRight(wordShift, bitShift, _words, _size, _words);
    }

    private void subtract(LargeInteger that) { // this >= that
        _size = Calculus.subtract(_words, _size, that._words, that._size,
                _words);
    }

    /**
     * Returns the value of this large integer after performing a binary
     * shift to left. The shift distance, <code>n</code>, may be negative,
     * in which case this method performs a right shift.
     * 
     * @param n the shift distance, in bits.
     * @return <code>this &lt;&lt; n</code>.
     * @see #shiftRight
     */
    public LargeInteger shiftLeft(int n) {
        if (n == 0)
            return this;
        if (n < 0)
            return shiftRight(-n);
        if (_size == 0)
            return LargeInteger.ZERO;
        final int wordShift = n < 63 ? 0 : n / 63;
        final int bitShift = n - wordShift * 63;
        LargeInteger li = ARRAY_FACTORY.array(_size + wordShift + 1);
        li._isNegative = _isNegative;
        li._size = Calculus.shiftLeft(wordShift, bitShift, _words, _size,
                li._words);
        return li;
    }

    /**
     * Returns the value of this large integer after performing a binary
     * shift to right with sign extension <code>(-1 >> 1 == -1)</code>.
     * The shift distance, <code>n</code>, may be negative, in which case 
     * this method performs a {@link #shiftLeft(int)}.
     * 
     * @param n the shift distance, in bits.
     * @return <code>this &gt;&gt; n</code>.
     */
    public LargeInteger shiftRight(int n) {
        LargeInteger li = this.times2pow(-n);
        return (_isNegative) && (n > 0) && (isShiftRightCorrection(n)) ? li.minus(LargeInteger.ONE) : li;
    }

    // Indicates if bits lost when shifting right the two's-complement
    // representation (affects only negative numbers).
    private boolean isShiftRightCorrection(int n) {
        int wordShift = n < 63 ? 0 : n / 63;
        int bitShift = n - ((wordShift << 6) - wordShift); // n - wordShift * 63
        int i = wordShift;
        boolean bitsLost = (bitShift != 0) && (_words[i] << (64 - bitShift)) != 0;
        while ((!bitsLost) && --i >= 0) {
            bitsLost = _words[i--] != 0;
        }
        return bitsLost;
    }

    /**
     * Returns the value of this large integer after multiplication by 
     * a power of two. This method is equivalent to {@link #shiftLeft(int)}
     * for positive <code>n</code>; but is different from 
     * {@link #shiftRight(int)} for negative <code>n</code> as no sign 
     * extension is performed (<code>-1 >>> 1 == 0</code>).
     * 
     * @param n the power of 2 exponent.
     * @return <code>this · 2<sup>n</sup></code>.
     */
    public LargeInteger times2pow(int n) {
        if (n == 0)
            return this;
        if (n > 0)
            return shiftLeft(n);
        n = -n; // Works with positive n.
        int wordShift = n < 63 ? 0 : n / 63;
        int bitShift = n - ((wordShift << 6) - wordShift); // n - wordShift * 63
        if (_size <= wordShift) // All bits have been shifted.
            return LargeInteger.ZERO;
        LargeInteger li = ARRAY_FACTORY.array(_size - wordShift);
        li._size = Calculus.shiftRight(wordShift, bitShift, _words, _size,
                li._words);
        li._isNegative = _isNegative && (li._size != 0);
        return li;
    }

    /**
     * Convenience method equivalent to {@link #times10pow(int)}.
     * [code]
     *     LargeInteger billion = LargeInteger.ONE.E(9); // 1E9
     *     LargeInteger million = billion.E(-3);
     * [/code]
     *
     * @param n the decimal exponent.
     * @return <code>this · 10<sup>n</sup></code>
     */
    public LargeInteger E(int n) {
        return times10pow(n);
    }

    /**
     * Returns the value of this large integer after multiplication by 
     * a power of ten. For example:[code]
     *     LargeInteger billion = LargeInteger.ONE.times10pow(9); // 1E9
     *     LargeInteger million = billion.times10pow(-3);[/code]
     *
     * @param n the decimal exponent.
     * @return <code>this · 10<sup>n</sup></code>
     */
    public LargeInteger times10pow(int n) {
        if (n == 0)
            return this;
        if (this._size == 0)
            return LargeInteger.ZERO;
        if (n > 0) {
            int bitLength = (int) (n * DIGITS_TO_BITS);
            LargeInteger li = ARRAY_FACTORY.array(_size + (bitLength / 63) + 1); // Approx.
            li._isNegative = _isNegative;
            int i = (n >= LONG_POW_5.length) ? LONG_POW_5.length - 1 : n;
            li._size = Calculus.multiply(_words, _size, LONG_POW_5[i],
                    li._words);
            for (int j = n - i; j != 0; j -= i) {
                i = (j >= LONG_POW_5.length) ? LONG_POW_5.length - 1 : j;
                li._size = Calculus.multiply(li._words, li._size,
                        LONG_POW_5[i], li._words);
            }
            // Multiplies by 2^n
            final int wordShift = n < 63 ? 0 : n / 63;
            final int bitShift = n - ((wordShift << 6) - wordShift); // n - 63 * wordShift
            li._size = Calculus.shiftLeft(wordShift, bitShift, li._words,
                    li._size, li._words);
            return li;
        } else {// n < 0
            n = -n;
            // Divides by 2^n
            final int wordShift = n < 63 ? 0 : n / 63;
            final int bitShift = n - ((wordShift << 6) - wordShift); // n - 63 * wordShift
            if (_size <= wordShift) // All bits would be shifted. 
                return LargeInteger.ZERO;
            LargeInteger li = ARRAY_FACTORY.array(_size - wordShift);
            li._size = Calculus.shiftRight(wordShift, bitShift, _words, _size,
                    li._words);
            for (int j = n; j != 0;) { // Divides by 5^n
                int i = (j >= INT_POW_5.length) ? INT_POW_5.length - 1 : j;
                Calculus.divide(li._words, li._size, INT_POW_5[i], li._words);
                if ((li._size > 0) && (li._words[li._size - 1] == 0L)) {
                    li._size--;
                }
                j -= i;
            }
            li._isNegative = _isNegative && (li._size != 0);
            return li;
        }
    }
    private static final double DIGITS_TO_BITS = MathLib.LOG10 / MathLib.LOG2;

    private static final int[] INT_POW_5 = new int[]{1, 5, 25, 125, 625,
        3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625,
        1220703125};

    private static final long[] LONG_POW_5 = new long[]{1L, 5L, 25L, 125L,
        625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L,
        48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L,
        152587890625L, 762939453125L, 3814697265625L, 19073486328125L,
        95367431640625L, 476837158203125L, 2384185791015625L,
        11920928955078125L, 59604644775390625L, 298023223876953125L,
        1490116119384765625L, 7450580596923828125L};

    /**
     * Compares this large integer against the specified <code>long</code>
     * value.
     *
     * @param value <code>long</code> value to compare with.
     * @return <code>true</code> if this large integer has the specified value;
     *          <code>false</code> otherwise.
     */
    public boolean equals(long value) {
        if (_size == 0)
            return value == 0;
        return ((_size <= 1) && (_isNegative ? -_words[0] == value
                : _words[0] == value)) || ((value == Long.MIN_VALUE) && (_isNegative) && (_size == 2) && (_words[1] == 1) && (_words[0] == 0));
    }

    /**
     * Compares this large integer to the specified <code>long</code> value.
     *
     * @param  value the <code>long</code> value to compare with.
     * @return -1, 0 or 1 as this integer is numerically less than, equal to,
     *         or greater than the specified value.
     */
    public int compareTo(long value) {
        if (_size > 1)
            return (value == Long.MIN_VALUE) && (this.equals(Long.MIN_VALUE)) ? 0
                    : (_isNegative ? -1 : 1);
        // size <= 1
        long thisValue = _isNegative ? -_words[0] : _words[0];
        return thisValue < value ? -1 : ((thisValue == value) ? 0 : 1);
    }

    /**
     * Returns the {@link BigInteger} equivalent to this large integer number.
     *
     * @return the corresponding <code>java.math.BigInteger</code>
     */
    public BigInteger asBigInteger() {
        byte[] bytes = new byte[(bitLength() >> 3) + 1];
        this.toByteArray(bytes, 0);
        return new BigInteger(bytes);
    }

    // Implements abstract class Number.
    public LargeInteger abs() {
        return _isNegative ? this.opposite() : this;
    }

    // Implements GroupAdditive.
    public LargeInteger opposite() {
        LargeInteger li = NO_ARRAY_FACTORY.object();
        li._words = _words;
        li._size = _size;
        li._isNegative = !_isNegative && (_size != 0);
        return li;
    }

    // Implements GroupAdditive.
    public LargeInteger plus(LargeInteger that) {
        if (that.isZero())
            return this;
        if (this._size < that._size) // Adds smallest in size to largest.
            return that.plus(this);
        if (this._isNegative != that._isNegative)
            return this.minus(that.opposite()); // Switches that sign.
        LargeInteger li = ARRAY_FACTORY.array(_size + 1);
        li._size = Calculus.add(_words, _size, that._words, that._size,
                li._words);
        li._isNegative = _isNegative;
        return li;
    }

    @Override
    public LargeInteger minus(LargeInteger that) {
        if (that.isZero())
            return this;
        if (this._isNegative != that._isNegative)
            return this.plus(that.opposite()); // Switches that sign.
        if (that.isLargerThan(this)) // Always subtract the smallest to the largest. 
            return that.minus(this).opposite();
        LargeInteger li = ARRAY_FACTORY.array(this._size);
        li._size = Calculus.subtract(_words, _size, that._words, that._size,
                li._words);
        li._isNegative = this._isNegative && (li._size != 0);
        return li;
    }

    // Implements Ring.
    public LargeInteger times(LargeInteger that) {
        if (that._size > this._size) // Always multiply the smallest to the largest.
            return that.times(this);
        if (that._size <= 1) // Direct times(long) multiplication.
            return this.times(that.longValue());
        if (that._size < 10) { // Conventional multiplication.
            LargeInteger li = ARRAY_FACTORY.array(this._size + that._size);
            li._size = Calculus.multiply(this._words, this._size, that._words,
                    that._size, li._words);
            li._isNegative = (this._isNegative != that._isNegative);
            return li;
        } else if (that._size < 20) { // Karatsuba (sequential).
            int n = (that._size >> 1) + (that._size & 1);
            // this = a + 2^(n*63) b, that = c + 2^(n*63) d
            LargeInteger b = this.high(n);
            LargeInteger a = this.low(n);
            // Optimization for square (a == c, b == d).
            LargeInteger d = (this == that) ? b : that.high(n);
            LargeInteger c = (this == that) ? a : that.low(n);
            LargeInteger ab = a.plus(b);
            LargeInteger cd = (this == that) ? ab : c.plus(d);
            LargeInteger abcd = ab.times(cd);
            LargeInteger ac = a.times(c);
            LargeInteger bd = b.times(d);
            // li = a*c + ((a+b)*(c+d)-(a*c+b*d)) 2^n + b*d 2^2n 
            return ac.plus(abcd.minus(ac.plus(bd)).shiftWordLeft(n)).plus(
                    bd.shiftWordLeft(n << 1));
        } else { // Karatsuba (concurrent).
            int n = (that._size >> 1) + (that._size & 1);
            // this = a + 2^(63*n) b, that = c + 2^(63*n) d
            LargeInteger b = this.high(n);
            LargeInteger a = this.low(n);
            // Optimization for square (a == c, b == d).
            LargeInteger d = (this == that) ? b : that.high(n);
            LargeInteger c = (this == that) ? a : that.low(n);
            LargeInteger ab = a.plus(b);
            LargeInteger cd = (this == that) ? ab : c.plus(d);
            MultiplyLogic abcd = MultiplyLogic.newInstance(ab, cd);
            MultiplyLogic ac = MultiplyLogic.newInstance(a, c);
            MultiplyLogic bd = MultiplyLogic.newInstance(b, d);
            ConcurrentContext.enter();
            try { // this = a + 2^n b,   that = c + 2^n d
                ConcurrentContext.execute(abcd);
                ConcurrentContext.execute(ac);
                ConcurrentContext.execute(bd);
            } finally {
                ConcurrentContext.exit();
            }
            // result = a*c + ((a+b)*(c+d)-(a*c+b*d)) 2^n + b*d 2^2n 
            LargeInteger result = ac.value().plus(
                    abcd.value().minus(ac.value().plus(bd.value())).shiftWordLeft(n)).plus(
                    bd.value().shiftWordLeft(n << 1));
            return result;
        }
    }

    private LargeInteger high(int w) { // this.shiftRight(w * 63)
        LargeInteger li = ARRAY_FACTORY.array(_size - w);
        li._isNegative = _isNegative;
        li._size = _size - w;
        System.arraycopy(_words, w, li._words, 0, _size - w);
        return li;
    }

    private LargeInteger low(int w) { // this.minus(high(w).shiftLeft(w * 63));
        LargeInteger li = NO_ARRAY_FACTORY.object();
        li._words = _words;
        li._isNegative = _isNegative;
        for (int i = w; i > 0; i--) {
            if (_words[i - 1] != 0) {
                li._size = i;
                return li;
            }
        } // Else zero.
        return LargeInteger.ZERO;
    }

    private LargeInteger shiftWordLeft(int w) { // this.minus(high(w).shiftLeft(w * 63));
        if (_size == 0)
            return LargeInteger.ZERO;
        LargeInteger li = ARRAY_FACTORY.array(w + _size);
        li._isNegative = _isNegative;
        li._size = w + _size;
        for (int i = 0; i < w;) {
            li._words[i++] = 0;
        }
        System.arraycopy(_words, 0, li._words, w, _size);
        return li;
    }

    @Override
    public LargeInteger times(long multiplier) {
        if (this.isZero() || (multiplier == 0))
            return LargeInteger.ZERO;
        if (multiplier == 1)
            return this;
        if (multiplier == -1)
            return this.opposite();
        if (multiplier == Long.MIN_VALUE)
            return times(LONG_MIN_VALUE); // Size 2.
        boolean isNegative = _isNegative ^ (multiplier < 0);
        multiplier = MathLib.abs(multiplier);
        LargeInteger li = ARRAY_FACTORY.array(_size + 1);
        li._size = Calculus.multiply(_words, _size, multiplier, li._words);
        li._isNegative = isNegative;
        return li;
    }

    // Implements abstract class Number.
    public long longValue() {
        if (_size == 0)
            return 0;
        return (_size <= 1) ? (_isNegative ? -_words[0] : _words[0])
                : (_isNegative ? -((_words[1] << 63) | _words[0])
                : (_words[1] << 63) | _words[0]); // bitLength > 63 bits.
    }

    // Implements abstract class Number.
    public double doubleValue() {
        if (_size == 0)
            return 0;
        if (_size <= 1)
            return _isNegative ? -_words[0] : _words[0];

        // Calculates bits length (ignores sign).    
        final int n = _size - 1;
        final int bitLength = MathLib.bitLength(_words[n]) + (n << 6) - n;

        // Keep 63 most significant bits.
        int shift = 63 - bitLength;
        LargeInteger int63 = this.times2pow(shift);
        double d = MathLib.toDoublePow2(int63._words[0], -shift);
        return _isNegative ? -d : d;
    }

    @Override
    public BigDecimal decimalValue() {
        return new BigDecimal(asBigInteger());
    }

    // Implements abstract class Number.
    public int compareTo(LargeInteger that) {
        // Compares sign.
        if (_isNegative && !that._isNegative)
            return -1;
        if (!_isNegative && that._isNegative)
            return 1;
        // Same sign, compares size.
        if (_size > that._size)
            return _isNegative ? -1 : 1;
        if (that._size > _size)
            return _isNegative ? 1 : -1;
        // Same size.
        return _isNegative ? compare(that._words, _words, _size) : compare(
                _words, that._words, _size);
    }

    // Implements abstract class Number.
    public LargeInteger copy() {
        LargeInteger li = ARRAY_FACTORY.array(_size);
        li._isNegative = _isNegative;
        li._size = _size;
        if (_size <= 1) {
            li._words[0] = _words[0];
            return li;
        }
        System.arraycopy(_words, 0, li._words, 0, _size);
        return li;
    }

    ////////////////////////
    // Parsing/Formatting //
    ////////////////////////
    /**
     * Returns the text representation of this number in the specified radix.
     *
     * @param radix the radix of the representation.
     * @return the text representation of this number in the specified radix.
     */
    public Text toText(int radix) {
        TextBuilder tmp = TextBuilder.newInstance();
        try {
            format(this, radix, tmp);
            return tmp.toText();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            TextBuilder.recycle(tmp);
        }
    }

    /**
     * Parses the specified character sequence from the specified position 
     * as a large integer in the specified radix.
     *
     * @param  csq the character sequence to parse.
     * @param  radix the radix to be used while parsing.
     * @param  cursor the current cursor position (being maintained).
     * @return the corresponding large integer.
     * @throws NumberFormatException if error when parsing.
     */
    public static LargeInteger parse(CharSequence csq, int radix, Cursor cursor) {
        final int end = csq.length();
        boolean isNegative = cursor.at('-', csq);
        cursor.increment(isNegative || cursor.at('+', csq) ? 1 : 0);
        LargeInteger li = null;
        final int maxDigits = (radix <= 10) ? 18 : (radix <= 16) ? 15 : 12;
        while (true) { // Reads up to digitsCount at a time.
            int start = cursor.getIndex();
            int split = MathLib.min(start + maxDigits, end);
            long l = TypeFormat.parseLong(csq.subSequence(0, split), radix, cursor);
            int readCount = cursor.getIndex() - start;
            if (li == null) {
                li = LargeInteger.valueOf(l);
            } else {
                if (li._words.length < li._size + 2) { // Resizes.
                    LargeInteger tmp = ARRAY_FACTORY.array(li._size + 2);
                    System.arraycopy(li._words, 0, tmp._words, 0, li._size);
                    tmp._isNegative = li._isNegative;
                    tmp._size = li._size;
                    li = tmp;
                }
                long factor = pow(radix, readCount);
                li._size = Calculus.multiply(li._words, li._size, factor,
                        li._words);
                li._size = Calculus.add(li._words, li._size, l);
            }
            if (cursor.getIndex() == end)
                break; // Reached end.
            char c = csq.charAt(cursor.getIndex());
            int digit = (c <= '9') ? c - '0'
                    : ((c <= 'Z') && (c >= 'A')) ? c - 'A' + 10
                    : ((c <= 'z') && (c >= 'a')) ? c - 'a' + 10 : -1;
            if ((digit < 0) || (digit >= radix))
                break; // No more digit.
        }
        return isNegative ? li.opposite() : li;
    }

    private static long pow(int radix, int n) {
        if (radix == 10)
            return LONG_POW_10[n];
        if (radix == 16)
            return LONG_POW_16[n];
        long l = 1;
        for (int i = 0; i < n; i++) {
            l *= radix;
        }
        return l;
    }
    private static final long[] LONG_POW_10 = new long[]{1, 10, 100, 1000,
        10000, 100000, 1000000, 10000000, 100000000, 1000000000,
        10000000000L, 100000000000L, 1000000000000L, 10000000000000L,
        100000000000000L, 1000000000000000L, 10000000000000000L,
        100000000000000000L, 1000000000000000000L};

    private static final long[] LONG_POW_16 = new long[]{0x1, 0x10, 0x100,
        0x1000, 0x10000, 0x100000, 0x1000000, 0x10000000, 0x100000000L,
        0x1000000000L, 0x10000000000L, 0x100000000000L, 0x1000000000000L,
        0x10000000000000L, 0x100000000000000L, 0x1000000000000000L};

    /**
     * Formats the specified large integer in the specified radix and into
     * the specified <code>Appendable</code> argument.
     *
     * @param  li the large integer to format.
     * @param  radix the radix.
     * @param  out the <code>Appendable</code> to append.
     * @return the specified <code>Appendable</code> object.
     * @throws  IllegalArgumentException if radix is not in [2 .. 36] range.
     * @throws IOException if an I/O exception occurs.
     */
    public static Appendable format(LargeInteger li, int radix, Appendable out)
            throws IOException {
        if (li._isNegative) {
            out.append('-');
        }
        final int maxDigits = (radix <= 10) ? 9 : (radix <= 16) ? 7 : 5;
        return write(li.copy(), radix, (int) pow(radix, maxDigits), out);
    }

    private static Appendable write(LargeInteger li, int radix, int divisor,
            Appendable out) throws IOException {
        if (li._size <= 1) // Direct long formatting.
            return TypeFormat.format(li._size == 0 ? 0 : li._words[0], radix, out);
        int rem = (int) Calculus.divide(li._words, li._size, divisor, li._words);
        if (li._words[li._size - 1] == 0L) {
            li._size--;
        }
        write(li, radix, divisor, out); // Writes high.
        divisor /= radix;
        while (rem < divisor) {
            out.append('0');
            divisor /= radix;
        }
        if (0 != rem) {
            return TypeFormat.format(rem, radix, out); // Writes low.
        } else {
            return out;
        }
    }
    private static final long serialVersionUID = 1L;

}
