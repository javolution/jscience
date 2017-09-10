/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import static javolution.lang.Realtime.Limit.LINEAR;
import static javolution.lang.Realtime.Limit.N_SQUARE;
import javolution.lang.Realtime;
import javolution.util.function.Equality;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.structure.VectorSpace;

/**
 * <p> An element of a <a href="http://en.wikipedia.org/wiki/Vector_space">
 *     vector space</a>.</p>
 *      
 * <p> Vectors' instances are produced through static factory methods of
 *     implementing classes.  
 * [code]
 * // Creates double precision (64 bits) floating-point vector (OpenCL accelerated).
 * RealVector V0 = RealVector.of(1.1, 1.2, 1.3);
 *
 * // Creates double precision (64 bits) complex numbers vector (OpenCL accelerated).
 * ComplexVector V1 = ComplexVector.of(Complex.of(1.1, 2.4), Complex.of(3.1, -1.4));
 * 
 * // Creates a dense vector of rational numbers ({23/45, 33/75}).
 * DenseVector<Rational> V2 = DenseVector.of(Rational.of(23, 45), Rational.of(33, 75));
 *
 * // Creates a sparse vector ({0, 0, 0, 2.1, 7.7, 0, 0, 0}) of decimal numbers.
 * SparseVector<Decimal> V3 = SparseVector.of(8, Decimal.ZERO, 
 *     SparseVector.Entry.of(3, Decimal.valueOf("2.1")),
 *     SparseVector.Entry.of(4, Decimal.valueOf("7.7")),
 *     
 * // Converts any vector to a dense vector.
 * DenseVector<Decimal> V4 = DenseVector.of(V3);
 * 
 * // Converts any vector to a sparse vector.
 * SparseVector<Rational> V5 = SparseVector.of(V2);
 * [/code]</p>
 * 
 * <p> JScience modules may provide additional implementing classes.
 * [code]
 * // Physical quantity.
 * class Amount<Q extends Quantity> implements Quantity<Q>, Field<Amount<?>> { 
 *     Unit<Q> unit();
 *     Real    value();
 * }
 * class AmountVector<Q extends Quantity> implements Vector<Amount<?>> { ... }
 * 
 * AmountVector<Velocity> velocity3D = AmountVector.of(RealVector.of(1.2, 3.4 -5.7), METER_PER_SECOND);
 * 
 * Amount<Energy> eV = Amount.of(Estimate.of("1.602176565(35)E-19"), JOULE); // Approximation.
 * Amount<Velocity> C = Amount.of(Rational.of(299792458), METER_PER_SECOND); // Exact.
 * Amount<Mass> weight = Amount.of(Interval.of(73.5, 74.1), KILOGRAM); // Interval Arithmetic.
 * AmountVector<?> x = AmountVector.of(eV, C, weight); // Heterogeneous amounts. 
 * [/code]</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/Vector_space">Wikipedia: Vector Space</a>
 */
public interface Vector<F extends Field<F>> extends VectorSpace<Vector<F>, F> {

	/**
	 * A consumer of vector's elements.
	 * 
	 * @see Vector#forEach(Consumer)
	 * @see Vector#forEachNonZero(Consumer)
	 */
	public interface Consumer<F> {

	    /**
	     * Accepts an element at the specified index.
	     */
	    void accept(int index, F element);

	}

	/**
	 * Returns a column matrix view of this vector.
	 */
	Matrix<F> column();

	/**
	 * Returns the cross product of two 3-dimensional vectors.
	 *
	 * @param  that the vector multiplier.
	 * @return <code>this x that</code>
	 */
	Vector<F> cross(Vector<F> that);

	/**
	 * Returns this vector dimension.
	 */
	int dimension();

    /**
	 * Compares the specified object with this vector for equality. 
	 * Returns {@code true} if the specified object is a vector having for 
	 * the same elements as this vector.
	 * 
	 * @see #equals(Vector, Equality)
	 */
	boolean equals(Object o);

    /**
	 * Indicates if this vector can be considered equal to the one 
	 * specified using the specified function when testing for 
	 * element equality. 
	 *
	 * @param  that the vector to compare for equality.
	 * @param  comparer the function to use when testing for element equality.
	 * @return <code>true</code> if this vector and the specified vector are
	 *         considered equal; <code>false</code> otherwise.
	 */
	@Realtime(limit = LINEAR)
	boolean equals(Vector<F> that, Equality<? super F> comparer);

    /** 
     * Iterates over all this vector's elements (smallest indices first)
     * applying the specified consumer.
     * 
     * @param consumer the functional consumer applied to the vector's elements.
     */
    @Realtime(limit = LINEAR)
    void forEach(Consumer<? super F> consumer);

	/** 
     * Iterates over all this vector's non-zero elements (smallest indices 
     * first) applying the specified consumer.
     * 
     * @param consumer the functional consumer applied to the vector's elements.
     */
    @Realtime(limit = LINEAR)
    void forEachNonZero(Consumer<? super F> consumer);

	/**
	 * Returns a single element of this vector.
	 *
	 * @param  i the element index.
	 * @return the element at <code>i</code>.
	 * @throws IndexOutOfBoundsException if the index is negative or greater
	 *         or equal to this vector's dimension.
	 */
	F get(int i);

	/**
	 * Returns the hash code value for this vector.  The hash code of a vector
	 * is defined to be: 
     * [code]
     * int hash = 0;
     * forEachNonZero((i, element) -> hash = 31 * hash + element.hashCode() + i);
     * [/code]
     * This ensures that {@code vector1.equals(vector2)} implies that
	 * {@code vector1.hashCode() == vector2.hashCode()} for any two vectors
	 * sparse or dense.
	 * @see #equals(Object)
	 */
	int hashCode();

	/**
	 * Returns the difference between this vector and the one specified.
	 *
	 * @param  that the vector to be subtracted.
	 * @return <code>this - that</code>.
	 * @throws DimensionException if {@code this.dimension() != 
	 *         that.dimension()}.
	 */
	@Realtime(limit = LINEAR)
	Vector<F> minus(Vector<F> that);
    
	/**
	 * Returns the negation of this vector.
	 *
	 * @return <code>-this</code>.
	 */
	@Realtime(limit = LINEAR)
	Vector<F> opposite();

    /**
	 * Returns the sum of this vector with the one specified.
	 *
	 * @param   that the vector to be added.
	 * @return  <code>this + that</code>.
	 * @throws DimensionException if {@code this.dimension() != 
	 *         that.dimension()}.
	 */
	@Realtime(limit = LINEAR)
	Vector<F> plus(Vector<F> that);

	/**
	 * Returns a row matrix view of this vector.
	 */
	Matrix<F> row();

	/**
     * Returns a view over a portion of this vector.
     * 
     * @param fromIndex low endpoint (inclusive) of this vector.
     * @param toIndex high endpoint (exclusive) of this vector
     * @return a vector view over the specified index range.
     * @throws IndexOutOfBoundsException if {@code (fromIndex < 0) || 
     *         (toIndex > dimension()) || (fromIndex > toIndex)}
     */
    Vector<F> subVector(int fromIndex, int toIndex);

	/**
	 * Returns the tensor product (outer product) of this vector with the one 
	 * specified.
	 *
	 * @param  that the vector multiplier.
	 * @return <code>this &otimes; that</code>
	 * @see <a href="http://en.wikipedia.org/wiki/Outer_product">
	 *      Wikipedia: Outer Product</a>
	 */
	@Realtime(limit = N_SQUARE)
	Matrix<F> tensor(Vector<F> that);

	/**
	 * Returns the product of this vector with the specified coefficient.
	 *
	 * @param  k the coefficient multiplier.
	 * @return <code>this · k</code>
	 */
	@Realtime(limit = LINEAR)
	@Override
	Vector<F> times(F k);

	/**
	 * Returns the dot product (inner product) of this vector with the one 
	 * specified.
	 *
	 * @param  that the vector multiplier.
	 * @return <code>this · that</code>
	 * @throws DimensionException if {@code this.dimension() != 
	 *         that.dimension()} or {@code this.dimension() == 0}.
	 * @see <a href="http://en.wikipedia.org/wiki/Dot_product">
	 *      Wikipedia: Dot Product</a>
	 */
	@Realtime(limit = LINEAR)
	F times(Vector<F> that);

}
