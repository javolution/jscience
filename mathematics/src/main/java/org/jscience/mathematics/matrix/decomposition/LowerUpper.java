/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix.decomposition;

import java.util.Comparator;

import javolution.context.LocalContext.Parameter;

import org.jscience.mathematics.matrix.DenseMatrix;
import org.jscience.mathematics.matrix.DimensionException;
import org.jscience.mathematics.matrix.Matrix;
import org.jscience.mathematics.matrix.SparseMatrix;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.Field;

/**
 * <p> This interface represents the decomposition of a {@link DenseMatrix 
 *     dense matrix} <code>A</code> into a product of a {@link #getLower lower} 
 *     and {@link #getUpper upper} triangular matrices, <code>L</code>
 *     and <code>U</code> respectively, such as <code>A = P·L·U<code> with 
 *     <code>P<code> a {@link #getPermutation permutation} matrix.</p>
 *     
 * <p> This decomposition</a> is typically used to resolve linear systems
 *     of equations (Gaussian elimination) or to calculate the determinant
 *     of a square matrix (<code>O(m³)</code>).</p>
 *     
 * <p> Numerical stability is guaranteed through pivoting if the
 *     {@link Field} elements are {@link Real real} numbers.
 *     For others elements types, numerical stability can be ensured by setting
 *     the {@link javolution.context.LocalContext context-local} 
 *     {@link #PIVOT_COMPARATOR pivot comparator}.</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">
 *      Wikipedia: LU decomposition</a>
 */
public interface LowerUpper<F extends Field<F>>  {

    /**
     * Holds the element comparator for pivoting. By default, pivoting is
     * performed for {@link Real} instances.  Pivoting can be disabled 
     * by setting the comparator to {@code null} in which case the 
     * {@link #getPermutation permutation} matrix is the matrix identity.</p>
     */
    public static final Parameter<Comparator<?>> 
         PIVOT_COMPARATOR = new Parameter<Comparator<?>>() {
    	           protected Comparator<?> getDefault() { 
    	        	   return null;
    	        }
    };

    /**
     * Returns the solution X of the equation: A * X = B  with
     * <code>this = A.lowerUpper()</code> using back and forward substitutions.
     *
     * @param  B the input matrix.
     * @return the solution X = (1 / A) * B.
     * @throws DimensionException if the dimensions do not match.
     */
    DenseMatrix<F> solve(Matrix<F> B);
    
    /**
     * Returns the solution X of the equation: A * X = Identity  with
     * <code>this = A.lowerUpper()</code> using back and forward substitutions.
     *
     * @return <code>this.solve(Identity)</code>
     */
    DenseMatrix<F> inverse();

    /**
     * Returns the determinant of the {@link Matrix} having this
     * decomposition.
     */
    F determinant();
    
    /**
     * Returns the lower matrix decomposition (<code>L</code>) with diagonal
     * elements equal to the multiplicative identity (one) for F. 
     */
    DenseMatrix<F> getLower();

    /**
     * Returns the upper matrix decomposition (<code>U</code>). 
     */
    DenseMatrix<F> getUpper(); 

    /**
     * Returns the permutation matrix (<code>P</code>). 
     */
    SparseMatrix<F> getPermutation();
    
}