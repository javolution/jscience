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

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.structure.NormedVectorSpace;

/**
 * <p> A {@link DenseVector dense vector} of {@link Complex} numbers.</p>
 * 
 * <p> Complex vectors can be directly created from 64 floating-points values.
 * [code]
 * ComplexVector V = ComplexVector.of(
 *     new double[]{0.1, 0.2, 0.3}, // real values.
 *     new double[]{0.7, 0.7, 0.7}); // imaginary values.
 * double r0 = V.realValue(0);
 * double i0 = V.imaginaryValue(0);
 * double norm = V.normValue();
 * [/code]</p>      
 *      
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 * @see SparseVector
 */
public abstract class ComplexVector extends DenseVector<Complex> implements
		NormedVectorSpace<Vector<Complex>, Complex>,
		ComputeContext.Local {
	
	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * Returns a complex vector having the specified real and imaginary 
	 * values.
	 */
	public static ComplexVector of(double[] realValues,
			double[] imaginaryValues) {
		return null;
	}

	/**
	 * Returns a complex vector having the specified complex elements.
	 */
	public static ComplexVector complexVector(Complex... elements) {
		return null;
	}

	/**
	 * Returns a complex vector equivalent to the generic vector specified.
	 */
	public static ComplexVector complexVector(Vector<Complex> that) {
		return (that instanceof ComplexVector) ? (ComplexVector) that : null;
	}

	/**
	 * Returns the {@code double} real value of a single complex number
	 * element of this vector.
	 *
	 * @param  i the element index (range [0..dimension[).
	 * @return <code>get(i).getReal().doubleValue()</code>.
	 * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i &gt;= getDimension())</code>
	 */
	public abstract double realValue(int i);
	
	/**
	 * Returns the {@code double} imaginary value of a single complex number
	 * element of this vector.
	 *
	 * @param  i the element index (range [0..dimension[).
	 * @return <code>get(i).getImaginary().doubleValue()</code>.
	 * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i &gt;= getDimension())</code>
	 */
	public abstract double imaginaryValue(int i);
	
	/**
	 * Returns the {@code double} value of the {@link NormedVectorSpace#norm()
	 *  norm} of this vector.
	 * @return {@code norm().realValue()} 
	 */
	public abstract double normValue();

	@Override
	public abstract ComplexVector cross(Vector<Complex> that);

    @Override
    public abstract ComplexVector getSubVector(List<Index> indices);

	@Override
	public abstract ComplexVector minus(Vector<Complex> that);

	@Override
	public abstract ComplexVector opposite();

	@Override
	public abstract ComplexVector plus(Vector<Complex> that);

	@Override
	public abstract ComplexMatrix tensor(Vector<Complex> that);

	@Override
	public abstract ComplexVector times(Complex k);

}
