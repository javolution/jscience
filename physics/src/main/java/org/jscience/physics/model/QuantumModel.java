/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.model;

/**
 * This class represents the quantum model.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 12, 2010
 */
public class QuantumModel extends HighEnergyModel {

    /**
     * Default constructor.
     */
    public QuantumModel() {
    }
    
// TODO: Allow more conversion.
//        // ENERGY = m²·kg/s² = kg·c²
//        SI.KILOGRAM.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * c * c / ePlus));
//
//        // H_BAR (SECOND * JOULE = SECOND * (KILOGRAM / C^2 )) = 1
//        SI.SECOND.setDimension(Unit.ONE.divide(SI.GIGA(NonSI.ELECTRON_VOLT)),
//                new MultiplyConverter(1E9 * ePlus / hBar));
//
//        // SPEED_OF_LIGHT (METRE / SECOND) = 1
//        SI.METRE.setDimension(Unit.ONE.divide(SI.GIGA(NonSI.ELECTRON_VOLT)),
//                new MultiplyConverter(1E9 * ePlus / (c * hBar)));
//
//        // BOLTZMANN (JOULE / KELVIN = (KILOGRAM / C^2 ) / KELVIN) = 1
//        SI.KELVIN.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * k / ePlus));
//
//        // MAGNETIC CONSTANT (NEWTON / AMPERE^2) = 1
//        SI.AMPERE.setDimension(SI.GIGA(NonSI.ELECTRON_VOLT),
//                new MultiplyConverter(1E-9 * MathLib.sqrt(µ0 * c * hBar) / ePlus));
//
//        SI.MOLE.setDimension(SI.MOLE, Converter.IDENTITY);
//        SI.CANDELA.setDimension(SI.CANDELA, Converter.IDENTITY);
    
}
