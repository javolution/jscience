/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.dimension;

import org.unitsofmeasurement.quantity.Quantity;

/**
 * <p> This interface represents the service to retrieve the dimension
 *     given a quantity type.</p>
 * 
 * <p> Bundles providing new quantity types and/or dimensions should publish 
 *     instances of this class in order for the framework to be able
 *     to determinate the dimension associated to the new quantities.</p>
 * 
 * <p> When activated the jscience-physics bundle publishes the dimensional 
 *     mapping of all quantities defined in the 
 *     <code>org.unitsofmeasurement.quantity</code> package.</p>

 * <p> Published instances are typically used to check the dimensional 
 *     consistency of physical quantities.</p>
 *     
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 27, 2011
 */
public interface PhysicsDimensionService {

    /**
     * Returns the dimension for the specified quantity or <code>null</code> if
     * unknown.
     *
     * @return the corresponding dimension or <code>null</code>
     */
    <Q extends Quantity<Q>> PhysicsDimension getDimension(Class<Q> quantityType);
    
}
