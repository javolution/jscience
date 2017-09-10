/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import java.util.List;
import java.util.SortedMap;

import javolution.text.Text;
import javolution.util.FastList;

/**
 * <p> This class represents a function defined from a mapping betweem 
 *     two sets (points and values).</p>
 *     
 * <p> Instance of this class can be used to approximate continuous
 *     functions or to numerically solve differential systems.</p>
 *     
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public final class DiscreteFunction<X, Y> extends Function<X, Y> {

    /**
     * Holds the point-value entries.
     */
    private SortedMap<X, Y> _pointValues;

    /**
     * Holds the variable.
     */
    private FastList<Variable<X>> _variables = new FastList<Variable<X>>();

    /**
     * Holds the interpolator.
     */
    private Interpolator<X, Y> _interpolator;

    /**
     * Creates the discrete function for the specified point-value entries.
     * 
     * @param  pointValues the point-value entries of this function.
     * @param  interpolator the interpolator.
     * @param  variable this function variable.
     */
    public DiscreteFunction(SortedMap<X, Y> pointValues,
            Interpolator<X, Y> interpolator, Variable<X> variable) {
        _pointValues = pointValues;
        _variables.add(variable);
        _interpolator = interpolator;
    }

    /**
     * Returns the point-value entries of this discrete function.
     *
     * @return the point-value mapping.
     */
    public SortedMap<X, Y> getPointValues() {
        return _pointValues;
    }

    /**
     * Returns the interpolator used by this discrete function.
     *
     * @return the interpolator used to estimate the value between two points.
     */
    public Interpolator<X, Y> getInterpolator() {
        return _interpolator;
    }

    @Override
    public Y evaluate() {
        X point = _variables.get(0).get();
        if (point == null) {
            throw new FunctionException("Variable " + _variables.get(0)
                    + " not set");
        }
        return _interpolator.interpolate(point, _pointValues);
    }

    @Override
    public List<Variable<X>> getVariables() {
        return _variables;
    }

    private static final long serialVersionUID = 1L;

    @Override
    public Text toText() {
        return Text.valueOf(getClass().getName()).plus("@").plus(
                Text.valueOf(hashCode(), 16));
    }

}