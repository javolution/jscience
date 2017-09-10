/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit;

import java.math.BigInteger;
import org.jscience.physics.unit.converters.RationalConverter;
import org.unitsofmeasurement.quantity.Quantity;

/**
 * <p> This class provides support for the 20 SI prefixes used in the metric
 *     system (decimal multiples and submultiples of SI units).
 *     For example:<pre><code>
 *     import static org.jscience.physics.unit.system.SI.*;  // Static import.
 *     import static org.jscience.physics.unit.system.SIPrefix.*; // Static import.
 *     import org.unitsofmeasurement.quantity.*;
 *     ...
 *     PhysicsUnit<Pressure> HECTOPASCAL = HECTO(PASCAL);
 *     PhysicsUnit<Length> KILOMETRE = KILO(METRE);
 *     </code></pre>
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/SI_prefix">Wikipedia: SI Prefix</a>
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public enum SIPrefix {

    YOTTA(new RationalConverter(BigInteger.TEN.pow(24), BigInteger.ONE)),
    ZETTA(new RationalConverter(BigInteger.TEN.pow(21), BigInteger.ONE)),
    EXA(new RationalConverter(BigInteger.TEN.pow(18), BigInteger.ONE)),
    PETA(new RationalConverter(BigInteger.TEN.pow(15), BigInteger.ONE)),
    TERA(new RationalConverter(BigInteger.TEN.pow(12), BigInteger.ONE)),
    GIGA(new RationalConverter(BigInteger.TEN.pow(9), BigInteger.ONE)),
    MEGA(new RationalConverter(BigInteger.TEN.pow(6), BigInteger.ONE)),
    KILO(new RationalConverter(BigInteger.TEN.pow(3), BigInteger.ONE)),
    HECTO(new RationalConverter(BigInteger.TEN.pow(2), BigInteger.ONE)),
    DEKA(new RationalConverter(BigInteger.TEN.pow(1), BigInteger.ONE)),
    DECI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(1))),
    CENTI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(2))),
    MILLI(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(3))),
    MICRO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(6))),
    NANO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(9))),
    PICO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(12))),
    FEMTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(15))),
    ATTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(18))),
    ZEPTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(21))),
    YOCTO(new RationalConverter( BigInteger.ONE, BigInteger.TEN.pow(24)));

    private final RationalConverter _converter;

    /**
     * Creates a new prefix.
     *
     * @param converter the associated unit converter.
     */
    private SIPrefix (RationalConverter converter) {
        _converter = converter;
    }

    /**
     * Returns the corresponding unit converter.
     *
     * @return the unit converter.
     */
    public RationalConverter getConverter() {
        return _converter;
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>24</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e24)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> YOTTA(PhysicsUnit<Q> unit) {
        return unit.transform(YOTTA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>21</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e21)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> ZETTA(PhysicsUnit<Q> unit) {
        return unit.transform(ZETTA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>18</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e18)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> EXA(PhysicsUnit<Q> unit) {
        return unit.transform(EXA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>15</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e15)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> PETA(PhysicsUnit<Q> unit) {
        return unit.transform(PETA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>12</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e12)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> TERA(PhysicsUnit<Q> unit) {
        return unit.transform(TERA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>9</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e9)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> GIGA(PhysicsUnit<Q> unit) {
        return unit.transform(GIGA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>6</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e6)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> MEGA(PhysicsUnit<Q> unit) {
        return unit.transform(MEGA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>3</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e3)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> KILO(PhysicsUnit<Q> unit) {
        return unit.transform(KILO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>2</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e2)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> HECTO(PhysicsUnit<Q> unit) {
        return unit.transform(HECTO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>1</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e1)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> DEKA(PhysicsUnit<Q> unit) {
        return unit.transform(DEKA.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-1</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-1)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> DECI(PhysicsUnit<Q> unit) {
        return unit.transform(DECI.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-2</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-2)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> CENTI(PhysicsUnit<Q> unit) {
        return unit.transform(CENTI.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-3</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-3)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> MILLI(PhysicsUnit<Q> unit) {
        return unit.transform(MILLI.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-6</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-6)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> MICRO(PhysicsUnit<Q> unit) {
        return unit.transform(MICRO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-9</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-9)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> NANO(PhysicsUnit<Q> unit) {
        return unit.transform(NANO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-12</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-12)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> PICO(PhysicsUnit<Q> unit) {
        return unit.transform(PICO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-15</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-15)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> FEMTO(PhysicsUnit<Q> unit) {
        return unit.transform(FEMTO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-18</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-18)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> ATTO(PhysicsUnit<Q> unit) {
        return unit.transform(ATTO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-21</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-21)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> ZEPTO(PhysicsUnit<Q> unit) {
        return unit.transform(ZEPTO.getConverter());
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-24</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-24)</code>.
     */
    public static <Q extends Quantity<Q>> PhysicsUnit<Q> YOCTO(PhysicsUnit<Q> unit) {
        return unit.transform(YOCTO.getConverter());
    }

}
