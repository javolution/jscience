/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import javolution.util.FastTable;
import javolution.xml.XMLSerializable;
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> The base class for our {@link UnitConverter} physics implementations.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public abstract class PhysicsConverter implements UnitConverter, XMLSerializable {

    /**
     * Holds identity converter.
     */
    public static final PhysicsConverter IDENTITY = new Identity();

    /**
     * Default constructor.
     */
    protected PhysicsConverter() {
    }

    /**
     * Concatenates this physics converter with another physics converter.
     * The resulting converter is equivalent to first converting by the
     * specified converter (right converter), and then converting by
     * this converter (left converter).
     *
     * @param that the other converter.
     * @return the concatenation of this converter with that converter.
     */
    public PhysicsConverter concatenate(PhysicsConverter that) {
        return (that == IDENTITY) ? this : new Compound(this, that);
    }

    @Override
    public boolean isIdentity() {
        return false;
    }

    @Override
    public abstract boolean equals(Object cvtr);

    @Override
    public abstract int hashCode();

    @Override
    public abstract PhysicsConverter inverse();

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        return (converter == IDENTITY) ? this : new Compound(this, converter);
    }

    @Override
    public List<? extends UnitConverter> getCompoundConverters() {
        FastTable<PhysicsConverter> converters = FastTable.newInstance();
        converters.add(this);
        return converters;
    }

    @Override
    public Number convert(Number value) { // This method should not be in the org.unitsofmeasurement.interface.
        throw new UnsupportedOperationException();
    }

    /**
     * This class represents the identity converter (singleton).
     */
    private static final class Identity extends PhysicsConverter {

        @Override
        public boolean isIdentity() {
            return true;
        }

        @Override
        public Identity inverse() {
            return this;
        }

        @Override
        public double convert(double value) {
            return value;
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext ctx) {
            return value;
        }

        @Override
        public UnitConverter concatenate(UnitConverter converter) {
            return converter;
        }

        @Override
        public boolean equals(Object cvtr) {
            return (cvtr instanceof Identity) ? true : false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isLinear() {
            return true;
        }

        public Identity copy() {
            return this; // Unique instance.
        }
    }

    /**
     * This class represents converters made up of two or more separate
     * converters (in matrix notation <code>[compound] = [left] x [right]</code>).
     */
    private static final class Compound extends PhysicsConverter {

        /**
         * Holds the first converter.
         */
        private UnitConverter left;

        /**
         * Holds the second converter.
         */
        private UnitConverter right;

        /**
         * Creates a compound converter resulting from the combined
         * transformation of the specified converters.
         *
         * @param  left the left converter.
         * @param  right the right converter.
         */
        public Compound(UnitConverter left, UnitConverter right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean isLinear() {
            return left.isLinear() && right.isLinear();
        }

        @Override
        public boolean isIdentity() {
            return false;
        }

        @Override
        public FastTable<UnitConverter> getCompoundConverters() {
            FastTable<UnitConverter> converters = FastTable.newInstance();
            List<? extends UnitConverter> leftCompound = left.getCompoundConverters();
            List<? extends UnitConverter> rightCompound = right.getCompoundConverters();
            converters.addAll(leftCompound);
            converters.addAll(rightCompound);
            return converters;
        }

        @Override
        public Compound inverse() {
            return new Compound(right.inverse(), left.inverse());
        }

        @Override
        public double convert(double value) {
            return left.convert(right.convert(value));
        }

        @Override
        public BigDecimal convert(BigDecimal value, MathContext ctx) {
            return left.convert(right.convert(value, ctx), ctx);
        }

        @Override
        public boolean equals(Object cvtr) {
            if (this == cvtr) return true;
            if (!(cvtr instanceof Compound)) return false;
            Compound that = (Compound) cvtr;
            return (this.left.equals(that.left)) && (this.right.equals(that.right));
        }

        @Override
        public int hashCode() {
            return left.hashCode() + right.hashCode();
        }

    }

}
