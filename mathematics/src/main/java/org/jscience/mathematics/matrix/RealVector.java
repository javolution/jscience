/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import java.util.List;

import javolution.context.ComputeContext;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.NormedVectorSpace;

/**
 * <p> A {@link DenseVector dense vector} of {@link Real} numbers.</p>
 * 
 * <p> Real vectors can be directly created from 64 floating-points values.
 * [code]
 * RealVector V = RealVector.of(0.1, 0.2, 0.3); // OpenCL Implementation.
 * double x1 = V.getValue(0); 
 * double norm = V.normValue();
 * [/code]</p>      
 *      
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 * @see SparseVector
 */
public abstract class RealVector extends DenseVector<Real> implements
		NormedVectorSpace<Vector<Real>, Real>, ComputeContext.Local {

	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * Returns a real vector having the specified {@code double} values.
	 */
	public static RealVector of(double... values) {
		return null;
	}

	/**
	 * Returns a real vector having the specified elements.
	 */
	public static RealVector of(Real... elements) {
		return null;
	}

	/**
	 * Returns a real vector equivalent to the generic vector specified.
	 */
	public static RealVector of(Vector<Real> that) {
		return (that instanceof RealVector) ? (RealVector) that : null;
	}

	/**
	 * Returns the {@code double} value of a single element of this vector.
	 *
	 * @param  i the element index (range [0..dimension[).
	 * @return <code>get(i).doubleValue()</code>.
	 * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i &gt;= getDimension())</code>
	 */
	public abstract double getValue(int i);

	/**
	 * Returns the {@code double} value of the {@link NormedVectorSpace#norm()
	 *  norm} of this vector.
	 *
	 * @return <code>norm().doubleValue()</code>.
	 */
	public abstract double normValue();

	@Override
	public abstract RealVector cross(Vector<Real> that);

	@Override
	public abstract RealVector getSubVector(List<Index> indices);

	@Override
	public abstract RealVector minus(Vector<Real> that);

	@Override
	public abstract RealVector opposite();

	@Override
	public abstract RealVector plus(Vector<Real> that);

	@Override
	public abstract RealMatrix tensor(Vector<Real> that);

	@Override
	public abstract RealVector times(Real k);

}
