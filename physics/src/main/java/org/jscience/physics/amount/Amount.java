/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.amount;

import org.jscience.mathematics.number.NumberField;
import org.jscience.mathematics.structure.Field;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.Unit;

/**
 * <p> A determinate or estimated amount for which operations such as addition, 
 *     subtraction, multiplication and division can be performed (it implements
 *     the {@link Field} interface).</p>
 * <p> The nature of an amount can be deduced from its parameterization type
 *     (compile time) or its unit (run time).
 * [code]
 * // Amount equivalent to the primitive type {@code double} but of mass nature.
 * Amount<Float64, Mass> weight = Amount.of(3.4, KILOGRAM);
 * 
 * // Exact amount. 
 * Amount<Rational, Length> length = Amount.of(Rational.of(33, 1), FOOT);
 * 
 * // Arbitrary precision amount (here dimensionless).
 * Amount<Real, Dimensionless> π = Amount.of(Real.of("3.14 ± 0.01"));
 * 
 * // Complex amount.
 * Amount<Complex<Float64>, Current>  Ψ = Amounts.of(Complex.of(2.3, 5.6), AMPERE);
 * 
 * // Amount vectors (all vector's elements share the same unit).
 * AmountVector<Float64, Velocity> velocity3D = AmountVector.of(METER_PER_SECOND, 1.2, 3.4 -5.7);
 * 
 * // Vector of heterogeneous amounts (each vector's elements has its own unit).
 * Vector<Amount<Float64, ?>> V0 = Vector.of(weight, duration); // See Vectors class.
 * 
 * // etc...
 * [/code]</p> 
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/Measuring">
 *      Wikipedia: Measuring</a>
 */
public abstract class Amount<N extends NumberField<N>, Q extends Quantity<Q>> 
    implements Field<Amount<N,?>> {

	/**
	 * Returns the value of this amount stated in this amount {@link #getUnit unit}.
	 */
	public abstract N value();
	
	/**
	 * Returns the unit identifying the nature of this amount.
	 */
	public abstract Unit<Q> unit();
	
}
