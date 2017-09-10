/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.types;

import org.jscience.physics.unit.PhysicsUnit;
import java.util.Map;
import org.jscience.physics.dimension.PhysicsDimension;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> This class represents an annotated unit.</p>
 * 
 * <p> Instances of this class are created through the
 *     {@link PhysicsUnit#annotate(String)} method.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public final class AnnotatedUnit<Q extends Quantity<Q>> extends PhysicsUnit<Q> {

    /**
     * Holds the actual unit.
     */
    private final PhysicsUnit<Q> actualUnit;

    /**
     * Holds the annotation.
     */
    private final String annotation;

    /**
     * Creates an annotated unit equivalent to the specified unit.
     *
     * @param actualUnit the unit to be annotated.
     * @param annotation the annotation.
     * @return the annotated unit.
     */
    public AnnotatedUnit(PhysicsUnit<Q> actualUnit, String annotation) {
        this.actualUnit = (actualUnit instanceof AnnotatedUnit) ?
            ((AnnotatedUnit<Q>)actualUnit).actualUnit : actualUnit;
        this.annotation = annotation;
    }

    /**
     * Returns the actual unit of this annotated unit (never an annotated unit
     * itself).
     *
     * @return the actual unit.
     */
    public PhysicsUnit<Q> getActualUnit() {
        return actualUnit;
    }

    /**
     * Returns the annotqtion of this annotated unit.
     *
     * @return the annotation.
     */
     public String getAnnotation() {
        return annotation;
    }

    @Override
    public String getSymbol() {
        return actualUnit.getSymbol();
    }

    @Override
    public Map<? extends PhysicsUnit, Integer> getProductUnits() {
        return actualUnit.getProductUnits();
    }

    @Override
    public PhysicsUnit<Q> toSI() {
        return actualUnit.getSystemUnit();
    }

    @Override
    public PhysicsDimension getDimension() {
        return actualUnit.getDimension();
    }

    @Override
    public UnitConverter getConverterToSI() {
        return actualUnit.getConverterToSI();
    }

    @Override
    public int hashCode() {
        return actualUnit.hashCode() + annotation.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof AnnotatedUnit<?>))
            return false;
        AnnotatedUnit<?> thatUnit = (AnnotatedUnit<?>) that;
        return this.actualUnit.equals(thatUnit.actualUnit) &&
                this.annotation.equals(thatUnit.annotation);
    }
}
