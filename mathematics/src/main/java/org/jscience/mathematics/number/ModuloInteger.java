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
import javolution.context.LocalContext;
import javolution.context.ObjectFactory;
import javolution.text.Cursor;
import javolution.text.TextFormat;

/**
 * <p> This class represents a modulo integer. It can be used in conjonction 
 *     with the {@link org.jscience.mathematics.vector.Matrix Matrix}
 *     class to resolve modulo equations (ref. number theory).</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, November 20, 2009
 * @see <a href="http://en.wikipedia.org/wiki/Modular_arithmetic">
 *      Wikipedia: Modular Arithmetic</a>
 */
public final class ModuloInteger extends NumberField<ModuloInteger> {

    /**
     * The modulo integer representing the additive identity.
     */
    public static final ModuloInteger ZERO = new ModuloInteger(LargeInteger.ZERO);

    /**
     * The modulo integer representing the multiplicative identity.
     */
    public static final ModuloInteger ONE = new ModuloInteger(LargeInteger.ONE);

    /**
     * Holds the default text format for modulo integers numbers (decimal representation).
     *
     * @see TextFormat#getDefault
     * @see LargeInteger#format(org.jscience.mathematics.number.LargeInteger, int, java.lang.Appendable)
     * @see LargeInteger#parse(java.lang.CharSequence, int, javolution.text.Cursor)
     */
    protected static final TextFormat<ModuloInteger> TEXT_FORMAT =
            new TextFormat<ModuloInteger>(ModuloInteger.class) {

                @Override
                public Appendable format(ModuloInteger mi, Appendable out)
                        throws IOException {
                    return LargeInteger.format(mi._value, 10, out);
                }

                @Override
                public ModuloInteger parse(CharSequence csq, Cursor cursor) {
                    return ModuloInteger.valueOf(LargeInteger.parse(csq, 10, cursor));
                }
            };

    /**
     * Holds the factory used to produce modulor integer instances.
     */
    private static final ObjectFactory<ModuloInteger> FACTORY = new ObjectFactory<ModuloInteger>() {

        protected ModuloInteger create() {
            return new ModuloInteger();
        }
    };

    /**
     * Holds the local modulus (for modular arithmetic).
     */
    private static final LocalContext.Reference<LargeInteger> MODULUS = new LocalContext.Reference<LargeInteger>();

    /**
     * Holds the large integer value.
     */
    private LargeInteger _value;

    /**
     * Default constructor.
     */
    private ModuloInteger() {
    }

    /**
     * Creates a modulo integer number always on the heap independently from the
     * current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     *
     * @param  value the modulo integer intrinsic value.
     */
    public ModuloInteger(LargeInteger value) {
        _value = value;
    }

    /**
     * Convenience method equivalent to
     * {@link #ModuloInteger(org.jscience.mathematics.number.LargeInteger)
     * ModuloInteger(new LargeInteger(value))}.
     *
     * @param  value the modulo integer intrinsic value.
     */
    public ModuloInteger(long value) {
        this(new LargeInteger(value));
    }

    /**
     * Returns the modulo integer having the specified value (independently of
     * the current modulo).
     * 
     * @param  value the modulo integer intrinsic value.
     * @return the corresponding modulo number.
     */
    public static ModuloInteger valueOf(LargeInteger value) {
        ModuloInteger m = FACTORY.object();
        m._value = value;
        return m;
    }

    /**
     * Returns the modulo integer having the specified value (independently of
     * the current modulo).
     *
     * @param  value the modulo integer intrinsic value.
     * @return the corresponding modulo number.
     */
    public static ModuloInteger valueOf(long value) {
        return ModuloInteger.valueOf(LargeInteger.valueOf(value));
    }

    /**
     * Returns the modulo integer for the specified character sequence.
     *
     * @param  csq the character sequence.
     * @return <code>TEXT_FORMAT.parse(csq)</code>.
     * @throws IllegalArgumentException if the character sequence does not
     *         contain a parsable number.
     * @see #TEXT_FORMAT
     */
    public static ModuloInteger valueOf(CharSequence csq) {
        return TEXT_FORMAT.parse(csq);
    }

    /**
     * Returns the {@link javolution.context.LocalContext local} modulus 
     * for modular arithmetic or <code>null</code> if the arithmetic operations
     * are non-modular (default). 
     * 
     * @return the local modulus or <code>null</code> if none.
     * @see #setModulus
     */
    public static LargeInteger getModulus() {
        return MODULUS.get();
    }

    /**
     * Sets the {@link javolution.context.LocalContext local} modulus 
     * for modular arithmetic.
     * 
     * @param modulus the new modulus or <code>null</code> to unset the modulus.
     * @throws IllegalArgumentException if <code>modulus &lt;= 0</code>
     */
    public static void setModulus(LargeInteger modulus) {
        if ((modulus != null) && (!modulus.isPositive()))
            throw new IllegalArgumentException("modulus: " + modulus + " has to be greater than 0");
        MODULUS.set(modulus);
    }

    /**
     * Returns the current modulo value of this number. If the modulus 
     * is {@link #setModulus set} to <code>null</code> the intrinsic value
     * (the creation value) is returned.
     * 
     * @return the positive number equals to this number modulo modulus or
     *         this modulo creation value.
     */
    public LargeInteger moduloValue() {
        LargeInteger modulus = MODULUS.get();
        return (modulus == null) ? _value : _value.mod(modulus);
    }

    // Implements GroupAdditive.
    public ModuloInteger opposite() {
        LargeInteger value = moduloValue().opposite();
        LargeInteger modulus = MODULUS.get();
        return ModuloInteger.valueOf((modulus == null) ? value : value.mod(modulus));
    }

    // Implements GroupAdditive.
    public ModuloInteger plus(ModuloInteger that) {
        LargeInteger value = moduloValue().plus(that.moduloValue());
        LargeInteger modulus = MODULUS.get();
        return ModuloInteger.valueOf((modulus == null) ? value : value.mod(modulus));
    }

    @Override
    public ModuloInteger times(long multiplier) {
        LargeInteger value = moduloValue().times(multiplier);
        LargeInteger modulus = MODULUS.get();
        return ModuloInteger.valueOf((modulus == null) ? value : value.mod(modulus));
    }

    // Implements GroupMultiplicative.
    public ModuloInteger times(ModuloInteger that) {
        LargeInteger value = moduloValue().times(that.moduloValue());
        LargeInteger modulus = MODULUS.get();
        return ModuloInteger.valueOf((modulus == null) ? value : value.mod(modulus));
    }

    // Implements GroupMultiplicative.
    public ModuloInteger reciprocal() {
        LargeInteger modulus = MODULUS.get();
        if (modulus == null)
            throw new ArithmeticException("Modulus not set");
        return ModuloInteger.valueOf(_value.modInverse(modulus));
    }

    // Implements abstract class Number.
    public ModuloInteger abs() {
        return _value.isNegative() ? this.opposite() : this;
    }

    // Implements abstract class Number.
    public long longValue() {
        return moduloValue().longValue();
    }

    // Implements abstract class Number.
    public double doubleValue() {
        return moduloValue().doubleValue();
    }

    // Implements abstract class Number.
    public BigDecimal decimalValue() {
        return new BigDecimal(moduloValue().asBigInteger());
    }

    // Implements abstract class Number.
    public int compareTo(ModuloInteger that) {
        return _value.compareTo(that._value);
    }

    // Implements abstract class Number.
    public ModuloInteger copy() {
        return ModuloInteger.valueOf(_value.copy());
    }
    private static final long serialVersionUID = 1L;

}
