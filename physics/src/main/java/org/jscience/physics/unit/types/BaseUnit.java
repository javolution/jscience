/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.types;

import java.util.Map;
import org.jscience.physics.dimension.PhysicsDimension;
import org.jscience.physics.unit.PhysicsConverter;
import org.jscience.physics.unit.PhysicsUnit;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.UnitConverter;


/**
 * <p> This class represents the building blocks on top of which all others
 *     physical units are created. Base units are always unscaled SI units.</p>
 * 
 * <p> When using the {@link org.jscience.physics.model.StandardModel standard model},
 *     all seven {@link org.jscience.physics.unit.system.SI SI} base units
 *     are dimensionally independent.</p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/SI_base_unit">
 *       Wikipedia: SI base unit</a>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public class BaseUnit<Q extends Quantity<Q>> extends PhysicsUnit<Q> {

    /**
     * Holds the symbol.
     */
    private final String symbol;

    /**
     * Holds the base unit dimension.
     */
    private final PhysicsDimension dimension;

    /**
     * Creates a base unit having the specified symbol and dimension.
     *
     * @param symbol the symbol of this base unit.
     */
    public BaseUnit(String symbol, PhysicsDimension dimension) {
        this.symbol = symbol;
        this.dimension = dimension;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public PhysicsUnit<Q> toSI() {
        return this;
    }

    @Override
    public UnitConverter getConverterToSI() throws UnsupportedOperationException {
        return PhysicsConverter.IDENTITY;
    }

    @Override
    public Map<? extends PhysicsUnit, Integer> getProductUnits() {
        return null;
    }

    @Override
    public PhysicsDimension getDimension() {
        return dimension;
    }

    @Override
    public final boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof BaseUnit)) return false;
        BaseUnit thatUnit = (BaseUnit) that;
        return this.symbol.equals(thatUnit.symbol) 
                && this.dimension.equals(thatUnit.dimension);
    }

    @Override
    public final int hashCode() {
        return symbol.hashCode();
    }
}
