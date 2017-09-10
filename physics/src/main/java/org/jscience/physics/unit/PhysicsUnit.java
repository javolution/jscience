/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.util.Map;
import javolution.text.TextBuilder;
import javolution.xml.XMLSerializable;
import org.jscience.physics.dimension.PhysicsDimension;
import org.jscience.physics.dimension.DimensionalModel;
import org.jscience.physics.unit.SI;
import org.jscience.physics.unit.converters.AddConverter;
import org.jscience.physics.unit.converters.MultiplyConverter;
import org.jscience.physics.unit.PhysicsConverter;
import org.jscience.physics.unit.converters.RationalConverter;
import org.jscience.physics.unit.formats.UCUMFormat;
import org.jscience.physics.unit.types.AlternateUnit;
import org.jscience.physics.unit.types.AnnotatedUnit;
import org.jscience.physics.unit.types.ProductUnit;
import org.jscience.physics.unit.types.TransformedUnit;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.IncommensurableException;
import org.unitsofmeasurement.unit.UnconvertibleException;
import org.unitsofmeasurement.unit.Unit;
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> The class represents units founded on the seven
 *     {@link org.jscience.physics.unit.system.SI SI} base units for
 *     seven base quantities assumed to be mutually independent.</p>
 *
 * <p> For all physics units, units conversions are symmetrical:
 *     <code>u1.getConverterTo(u2).equals(u2.getConverterTo(u1).inverse())</code>.
 *     Non-physical units (e.g. currency units) for which conversion is
 *     not symmetrical should have their own separate class hierarchy and
 *     are considered distinct (e.g. financial units), although
 *     they can always be combined with physics units (e.g. "€/Kg", "$/h").</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, July 1, 2011
 */
public abstract class PhysicsUnit<Q extends Quantity<Q>> implements Unit<Q>, XMLSerializable {

    /**
     * Default constructor.
     */
    protected PhysicsUnit() {
    }

   /**
     * Indicates if this unit belongs to the set of coherent SI units 
     * (unscaled SI units).
     * 
     * The base and coherent derived units of the SI form a coherent set, 
     * designated the set of coherent SI units. The word coherent is used here 
     * in the following sense: when coherent units are used, equations between 
     * the numerical values of quantities take exactly the same form as the 
     * equations between the quantities themselves. Thus if only units from 
     * a coherent set are used, conversion factors between units are never 
     * required. 
     * 
     * @return <code>equals(toSI())</code>
     */
    public boolean isSI() {
        PhysicsUnit<Q> si = this.toSI();
        return (this == si) || this.equals(si);
    }

    /**
     * Returns the unscaled {@link SI} unit  from which this unit is derived.
     * 
     * They SI unit can be be used to identify a quantity given the unit.
     * For example:[code]
     *    static boolean isAngularVelocity(PhysicsUnit<?> unit) {
     *        return unit.toSI().equals(RADIAN.divide(SECOND));
     *    }
     *    assert(REVOLUTION.divide(MINUTE).isAngularVelocity()); // Returns true.
     * [/code]
     *
     * @return the unscaled metric unit from which this unit is derived.
     */
    public abstract PhysicsUnit<Q> toSI();

    /**
     * Returns the converter from this unit to its unscaled {@link #toSI SI} 
     * unit.
     *
     * @return <code>getConverterTo(this.toSI())</code>
     * @see #toSI
     */
    public abstract UnitConverter getConverterToSI();

   /**
     * Annotates the specified unit. Annotation does not change the unit
     * semantic. Annotations are often written between curly braces behind units.
     * For example:
     * [code]
     *     PhysicsUnit<Volume> PERCENT_VOL = SI.PERCENT.annotate("vol"); // "%{vol}"
     *     PhysicsUnit<Mass> KG_TOTAL = SI.KILOGRAM.annotate("total"); // "kg{total}"
     *     PhysicsUnit<Dimensionless> RED_BLOOD_CELLS = SI.ONE.annotate("RBC"); // "{RBC}"
     * [/code]
     *
     * Note: Annotation of system units are not considered themselves as system units.
     *
     * @param annotation the unit annotation.
     * @return the annotated unit.
     */
    public AnnotatedUnit<Q> annotate(String annotation) {
        return new AnnotatedUnit<Q>(this, annotation);
    }
    
    /**
     * Returns the physics unit represented by the specified characters
     * as per standard <a href="http://www.unitsofmeasure.org/">UCUM</a> format.
     *
     * Locale-sensitive unit parsing should be handled using the OSGi
     * {@link org.unitsofmeasurement.service.UnitFormatService} or
     * for non-OSGi applications the
     * {@link org.jscience.physics.unit.format.LocalUnitFormat} utility class.
     *
     * <p>Note: The standard UCUM format supports dimensionless units.[code]
     *       PhysicsUnit<Dimensionless> PERCENT = PhysicsUnit.valueOf("100").inverse().asType(Dimensionless.class);
     * [/code]</p>
     *
     * @param charSequence the character sequence to parse.
     * @return <code>UCUMFormat.getCaseSensitiveInstance().parse(csq, new ParsePosition(0))</code>
     * @throws IllegalArgumentException if the specified character sequence
     *         cannot be correctly parsed (e.g. not UCUM compliant).
     */
    public static PhysicsUnit<?> valueOf(CharSequence charSequence) {
        return UCUMFormat.getCaseSensitiveInstance().parse(charSequence, new ParsePosition(0));
    }

    /**
     * Returns the standard <a href="http://unitsofmeasure.org/">UCUM</a>
     * representation of this physics unit. The string produced for a given unit is
     * always the same; it is not affected by the locale. It can be used as a
     * canonical string representation for exchanging units, or as a key for a
     * Hashtable, etc.
     *
     * Locale-sensitive unit parsing should be handled using the OSGi
     * {@link org.unitsofmeasurement.service.UnitFormat} service (or
     * the {@link org.jscience.physics.unit.format.LocalUnitFormat} class
     * for non-OSGi applications).
     *
     * @return <code>UCUMFormat.getCaseSensitiveInstance().format(this)</code>
     */
    @Override
    public String toString() {
        TextBuilder tmp = TextBuilder.newInstance();
        try {
            return UCUMFormat.getCaseSensitiveInstance().format(this, tmp).toString();
        } catch (IOException ioException) {
             throw new Error(ioException); // Should never happen.
        } finally {
            TextBuilder.recycle(tmp);
        }
    }


   /////////////////////////////////////////////////////////
    // Implements org.unitsofmeasurement.Unit<Q> interface //
    /////////////////////////////////////////////////////////

    /**
     * Returns the system unit (unscaled SI unit) from which this unit is derived.
     * They can be be used to identify a quantity given the unit. For example:[code]
     *    static boolean isAngularVelocity(PhysicsUnit<?> unit) {
     *        return unit.getSystemUnit().equals(RADIAN.divide(SECOND));
     *    }
     *    assert(REVOLUTION.divide(MINUTE).isAngularVelocity()); // Returns true.
     * [/code]
     *
     * @return the unscaled metric unit from which this unit is derived.
     */
    @Override
    public final PhysicsUnit<Q> getSystemUnit() {
        return toSI();
    }

    /**
     * Indicates if this unit is compatible with the unit specified.
     * To be compatible both units must be physics units having
     * the same fundamental dimension.
     *
     * @param that the other unit.
     * @return <code>true</code> if this unit and that unit have equals
     *         fundamental dimension according to the current physics model;
     *         <code>false</code> otherwise.
     */
    @Override
    public final boolean isCompatible(Unit<?> that) {
        if ((this == that) || this.equals(that)) return true;
        if (!(that instanceof PhysicsUnit)) return false;
        PhysicsDimension thisDimension = this.getDimension();
        PhysicsDimension thatDimension = ((PhysicsUnit)that).getDimension();
        if (thisDimension.equals(thatDimension)) return true;
        DimensionalModel model = DimensionalModel.getCurrent(); // Use dimensional analysis model.
        return model.getFundamentalDimension(thisDimension).equals(model.getFundamentalDimension(thatDimension));
    }

    /**
     * Casts this unit to a parameterized unit of specified nature or throw a
     * ClassCastException if the dimension of the specified quantity and
     * this unit's dimension do not match (regardless whether or not
     * the dimensions are independent or not).
     *
     * @param type the quantity class identifying the nature of the unit.
     * @throws ClassCastException if the dimension of this unit is different
     *         from the {@link SI} dimension of the specified type.
     * @see    SI#getUnit(Class)
     */
    @Override
    public final <T extends Quantity<T>> PhysicsUnit<T> asType(Class<T> type) {
        PhysicsDimension typeDimension = PhysicsDimension.getDimension(type);
        if ((typeDimension != null) && (!this.getDimension().equals(typeDimension)))
           throw new ClassCastException("The unit: " + this + " is not compatible with quantities of type " + type);
        return (PhysicsUnit<T>) this;
    }

    @Override
    public String getSymbol() {
        return null;
    }

    @Override
    public abstract Map<? extends PhysicsUnit, Integer> getProductUnits();

    @Override
    public abstract PhysicsDimension getDimension();


    @Override
    public final UnitConverter getConverterTo(Unit<Q> that) throws UnconvertibleException {
        if ((this == that) || this.equals(that)) return PhysicsConverter.IDENTITY; // Shortcut.
        Unit<Q> thisSystemUnit = this.getSystemUnit();
        Unit<Q> thatSystemUnit = that.getSystemUnit();
        if (!thisSystemUnit.equals(thatSystemUnit)) return getConverterToAny(that); 
        UnitConverter thisToSI= this.getConverterToSI();
        UnitConverter thatToSI= that.getConverterTo(thatSystemUnit);
        return thatToSI.inverse().concatenate(thisToSI);    
    }

    @Override
    public final UnitConverter getConverterToAny(Unit<?> that) throws IncommensurableException,
            UnconvertibleException {
        if (!isCompatible(that))
            throw new IncommensurableException(this + " is not compatible with " + that);
        PhysicsUnit thatPhysics = (PhysicsUnit)that; // Since both units are compatible they must be both physics units.
        DimensionalModel model = DimensionalModel.getCurrent();
        PhysicsUnit thisSystemUnit = this.getSystemUnit();
        UnitConverter thisToDimension = model.getDimensionalTransform(thisSystemUnit.getDimension()).concatenate(this.getConverterToSI());
        PhysicsUnit thatSystemUnit = thatPhysics.getSystemUnit();
        UnitConverter thatToDimension = model.getDimensionalTransform(thatSystemUnit.getDimension()).concatenate(thatPhysics.getConverterToSI());
        return thatToDimension.inverse().concatenate(thisToDimension);
    }


    @Override
    public final PhysicsUnit<?> alternate(String symbol) {
        return new AlternateUnit(this, symbol);
    }

    @Override
    public final PhysicsUnit<Q> transform(UnitConverter operation) {
        PhysicsUnit<Q> systemUnit = this.getSystemUnit();
        UnitConverter cvtr = this.getConverterToSI().concatenate(operation);
        if (cvtr.equals(PhysicsConverter.IDENTITY))
            return systemUnit;
        return new TransformedUnit<Q>(systemUnit, cvtr);
    }

    @Override
    public final PhysicsUnit<Q> add(double offset) {
        if (offset == 0)
            return this;
        return transform(new AddConverter(offset));
    }

    @Override
    public final PhysicsUnit<Q> multiply(double factor) {
        if (factor == 1)
            return this;
        if (isLongValue(factor))
            return transform(new RationalConverter(BigInteger.valueOf((long)factor), BigInteger.ONE));
        return transform(new MultiplyConverter(factor));
    }
    private static boolean isLongValue(double value) {
        if ((value < Long.MIN_VALUE) || (value > Long.MAX_VALUE)) return false;
        return Math.floor(value) == value;
    }

    /**
     * Returns the product of this unit with the one specified.
     *
     * <p> Note: If the specified unit (that) is not a physical unit, then
     * <code>that.multiply(this)</code> is returned.</p>
     *
     * @param that the unit multiplicand.
     * @return <code>this * that</code>
     */
    @Override
    public final Unit<?> multiply(Unit<?> that) {
        if (that instanceof PhysicsUnit)
            return multiply((PhysicsUnit<?>) that);
        return that.multiply(this); // Commutatif.
    }

    /**
     * Returns the product of this physical unit with the one specified.
     *
     * @param that the physical unit multiplicand.
     * @return <code>this * that</code>
     */
    public final PhysicsUnit<?> multiply(PhysicsUnit<?> that) {
        if (this.equals(SI.ONE))
            return that;
        if (that.equals(SI.ONE))
            return this;
        return ProductUnit.getProductInstance(this, that);
    }

    /**
     * Returns the inverse of this physical unit.
     *
     * @return <code>1 / this</code>
     */
    @Override
    public final PhysicsUnit<?> inverse() {
        if (this.equals(SI.ONE))
            return this;
        return ProductUnit.getQuotientInstance(SI.ONE, this);
    }

    /**
     * Returns the result of dividing this unit by the specifified divisor.
     * If the factor is an integer value, the division is exact.
     * For example:<pre><code>
     *    QUART = GALLON_LIQUID_US.divide(4); // Exact definition.
     * </code></pre>
     * @param divisor the divisor value.
     * @return this unit divided by the specified divisor.
     */
    @Override
    public final PhysicsUnit<Q> divide(double divisor) {
        if (divisor == 1)
            return this;
        if (isLongValue(divisor))
            return transform(new RationalConverter(BigInteger.ONE, BigInteger.valueOf((long)divisor)));
        return transform(new MultiplyConverter(1.0/divisor));
    }

    /**
     * Returns the quotient of this unit with the one specified.
     *
     * @param that the unit divisor.
     * @return <code>this.multiply(that.inverse())</code>
     */
    @Override
    public final Unit<?> divide(Unit<?> that) {
        return this.multiply(that.inverse());
    }

    /**
     * Returns the quotient of this physical unit with the one specified.
     *
     * @param that the physical unit divisor.
     * @return <code>this.multiply(that.inverse())</code>
     */
    public final PhysicsUnit<?> divide(PhysicsUnit<?> that) {
        return this.multiply(that.inverse());
    }

    /**
     * Returns a unit equals to the given root of this unit.
     *
     * @param n the root's order.
     * @return the result of taking the given root of this unit.
     * @throws ArithmeticException if <code>n == 0</code> or if this operation
     *         would result in an unit with a fractional exponent.
     */
    @Override
    public final PhysicsUnit<?> root(int n) {
        if (n > 0)
            return ProductUnit.getRootInstance(this, n);
        else if (n == 0)
            throw new ArithmeticException("Root's order of zero");
        else // n < 0
            return SI.ONE.divide(this.root(-n));
    }

    /**
     * Returns a unit equals to this unit raised to an exponent.
     *
     * @param n the exponent.
     * @return the result of raising this unit to the exponent.
     */
    @Override
    public final PhysicsUnit<?> pow(int n) {
        if (n > 0)
            return this.multiply(this.pow(n - 1));
        else if (n == 0)
            return SI.ONE;
        else // n < 0
            return SI.ONE.divide(this.pow(-n));
    }


    ////////////////////////////////////////////////////////////////
    // Ensures that sub-classes implements hashCode/equals method.
    ////////////////////////////////////////////////////////////////

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object that);

}