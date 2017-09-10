/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import java.io.Serializable;
import java.util.SortedMap;

import javolution.lang.Immutable;

import org.jscience.mathematics.structure.Field;

/**
 * <p> This interface represents an estimator of the values at a certain point 
 *     using surrounding points and values. Interpolators are typically used 
 *     with {@link DiscreteFunction discrete functions}.</p>
 * 
 * <p> As a convenience {@link Interpolator.Linear linear} interpolator class
 *     for point-values of the same {@link Field field} is provided.</p>
 *     
 * <p> Custom interpolators can be used between Java objects of different kind.
 *     For example:[code]
 *     // Creates a linear interpolator between the java.util.Date and Measures<Mass>
 *     Interpolator<Date, Amount<Mass>> linear 
 *          = new Interpolator<Date, Amount<Mass>>() { ... }
 *     DiscreteFunction<Date, Amount<Mass>> weight 
 *         = new DiscreteFunction<Date, Amount<Mass>>(samples, linear, t);
 *     [/code]</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle </a>
 * @version 3.0, February 13, 2006
 */
public interface Interpolator<P, V> extends Immutable, Serializable {

    /**
     * Estimates the value at the specified point.
     * 
     * @param point the point for which the value is estimated.
     * @param pointValues the point-value entries.
     * @return the estimated value at the specified point.
     */
    V interpolate(P point, SortedMap<P, V> pointValues);
    
    
    /**
     * <p> This class represents a linear interpolator for {@link Field field}
     *     instances (point and values from the same field).</p>
     * 
     * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle </a>
     * @version 3.0, February 13, 2006
     */
    public static class Linear<F extends Field<F>> implements Interpolator<F, F> {

        public F interpolate(F point, SortedMap<F, F> pointValues) {
            // Searches exact.
            F y = pointValues.get(point);
            if (y != null)
                return y;

            // Searches surrounding points/values.
            SortedMap<F, F> headMap = pointValues.headMap(point);
            F x1 = headMap.lastKey();
            F y1 = headMap.get(x1);
            SortedMap<F, F> tailMap = pointValues.tailMap(point);
            F x2 = tailMap.firstKey();
            F y2 = tailMap.get(x2);

            // Interpolates.
            final F x = point;
            F deltaInv = (x2.plus(x1.opposite())).reciprocal();
            F k1 = (x2.plus(x.opposite())).times(deltaInv);
            F k2 = (x.plus(x1.opposite())).times(deltaInv);
            return ((y1.times(k1))).plus(y2.times(k2));
        }        

        private static final long serialVersionUID = 1L;
    }

}