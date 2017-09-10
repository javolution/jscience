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
import javolution.lang.Realtime;
import javolution.util.function.Orders;
import javolution.util.function.Equality;

import org.jscience.mathematics.matrix.decomposition.LowerUpper;
import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.structure.Ring;
import org.jscience.mathematics.structure.VectorSpace;

/**
 * <p> A rectangular table of elements of a ring-like algebraic structure.</p>
 *     
 * <p> Matrices' instances are produced through static factory methods of
 *     implementing classes.  
 * [code]
 * // Creates double precision (64 bits) floating-point matrix (OpenCL).
 * RealMatrix M0 = RealMatrix.of(
 *     { {1.1, 1.2, 1.3}, {2.1, 2.2, 2.3} });
 *
 * // Creates double precision complex numbers matrix (OpenCL).
 * ComplexMatrix M1 = ComplexMatrix.of(new Complex[][] 
 *    { {c00, c01}, {c10, c11} });
 * 
 * // Creates a dense matrix of rational numbers.
 * DenseMatrix<Rational> M2 = DenseMatrix.of(new Rational[][] 
 *     { {r00, r01}, {r10, r11} }); 
 *
 * // Creates a sparse matrix (8x8) of decimal numbers.
 * SparseMatrix<Decimal> M3 = SparseMatrix.of(8, 8, Decimal.ZERO, 
 *    new SparseMatrix.Entry(2, 4, Decimal.of("2.4")),
 *    new SparseMatrix.Entry(3, 4, Decimal.of("3.4")));  
 *     
 * // Creates diagonal matrices.
 * DiagonalMatrix<Real> M4 = DiagonalMatrix.of(2.3, 4.5);
 * DiagonalMatrix<Complex> IDENTITY = DiagonalMatrix.of(16, Complex.ONE); // 16x16 matrix.
 * 
 * // Converts a sparse matrix to a dense matrix.
 * DenseMatrix<Decimal> M5 = DenseMatrix.of(M3);
 * 
 * // Converts a dense matrix to a sparse matrix.
 * SparseMatrix<Rational> M6 = SparseMatrix.of(M2);
 * [/code]
 *      
 * <p> Non-commutative field multiplication is supported. Invertible square 
 *     matrices may form a non-commutative field (also called a division
 *     ring). In which case this class may be used to resolve system of linear
 *     equations with matrix coefficients.</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/Matrix_%28mathematics%29">
 *      Wikipedia: Matrix (mathematics)</a>
 */
public interface Matrix<F extends Field<F>> extends VectorSpace<Matrix<F>, F>,
		Ring<Matrix<F>> {

	/**
	 * A consumer of matrix's elements.
	 * 
	 * @see Matrix#forEach(Consumer)
	 * @see Matrix#forEachNonZero(Consumer)
	 */
	public interface Consumer<F> {

		/**
		 * Accepts an element value at the specified row/column.
		 */
		void accept(int rowIndex, int columnIndex, F param);

	}

	/**
	 * Returns the adjoint of this matrix. It is obtained by replacing each
	 * element in this matrix with its cofactor and applying a + or - sign
	 * according (-1)**(i+j), and then finding the transpose of the resulting
	 * matrix.
	 *
	 * @return the adjoint of this matrix.
	 * @throws DimensionException if this matrix is not square or if
	 *         its dimension is less than 2.
	 */
	Matrix<F> adjoint();

	/**
	 * Returns the cofactor of an element in this matrix. It is the value
	 * obtained by evaluating the determinant formed by the elements not in
	 * that particular row or column.
	 *
	 * @param  i the row index.
	 * @param  j the column index.
	 * @return the cofactor of <code>THIS[i,j]</code>.
	 * @throws DimensionException matrix is not square or its dimension
	 *         is less than 2.
	 */
	F cofactor(int i, int j);

	/**
	 * Returns the column identified by the specified index of this matrix.
	 *
	 * @param  j the column index (range [0..n[).
	 * @return the vector holding the specified column.
	 * @throws IndexOutOfBoundsException <code>(j &lt; 0) || (j &gt;= n)</code>
	 */
	Vector<F> column(int j);

	/**
	 * Returns the determinant of this matrix. The implementations
	 * may uses an expansion by minors (also known as Laplacian) or 
	 * LU decomposition ({@link DenseMatrix}).
	 *
	 * @return this matrix determinant.
	 * @throws DimensionException if this matrix is not square.
	 */
	F determinant();

	/**
	 * Returns the diagonal vector.
	 *
	 * @return the vector holding the diagonal elements.
	 */
	Vector<F> diagonal();

	/**
	 * Returns this matrix divided by the one specified.
	 *
	 * @param  that the matrix divisor.
	 * @return <code>this / that</code>.
	 * @throws DimensionException if that matrix is not square or dimensions 
	 *         do not match.
	 */
	Matrix<F> divides(Matrix<F> that);

	/**
	 * Indicates if this matrix can be considered equal to the one 
	 * specified using the specified function when testing for 
	 * element equality.
	 *
	 * @param  that the matrix to compare for equality.
	 * @param  equal the function to use when testing for element equality.
	 * @return <code>true</code> if this matrix and the specified matrix are
	 *         considered equal; <code>false</code> otherwise.
	 */
	@Realtime(limit = LINEAR)
	boolean equals(Matrix<F> that, Equality<? super F> equal);

	/**
	 * Compares the specified object with this matrix for equality. 
	 * Returns {@code true} if the specified object is a matrix equals
	 * to this matrix using the {@link Orders.STANDARD standard} equality.
	 * 
	 * @see #equals(Matrix, Equality)
	 */
	boolean equals(Object o);

	/** 
	 * Iterates over all this matrix's elements (smallest rows first then 
	 * smallest column) applying the specified consumer.
	 * 
	 * @param consumer the functional consumer applied to the matrix's elements.
	 */
	@Realtime(limit = LINEAR)
	void forEach(Consumer<? super F> consumer);

	/** 
	 * Iterates over all this matrix's non-zero elements (smallest rows first
	 * then smallest column) applying the specified consumer.
	 * 
	 * @param consumer the functional consumer applied to the matrix's elements.
	 */
	@Realtime(limit = LINEAR)
	void forEachNonZero(Consumer<? super F> consumer);

	/**
	 * Returns a single element from this matrix.
	 *
	 * @param  i the row index (range [0..m[).
	 * @param  j the column index (range [0..n[).
	 * @return the element read at [i,j].
	 * @throws IndexOutOfBoundsException <code>
	 *         ((i &lt; 0) || (i &gt;= m)) || ((j &lt; 0) || (j &gt;= n))</code>
	 */
	F get(int i, int j);

	/**
	 * Returns the hash code value for this matrix.  The hash code of a vector
	 * is defined to be: 
	 * [code]
	 * int hash = 0;
	 * forEachNonZero((i, j, element) -> hash = 31 * hash + element.hashCode());
	 * [/code]
	 * This ensures that {@code matrix1.equals(matrix2)} implies that
	 * {@code matrix1.hashCode() == matrix2.hashCode()} for any two vectors
	 * sparse or dense.
	 * @see #equals(Object)
	 */
	int hashCode();

	/**
	 * Returns the inverse of this matrix (must be square).
	 * The default implementation returns
	 * <code>determinant.inverse().times(this.adjoint())</code>
	 *
	 * @return <code>1 / this</code>
	 * @throws DimensionException if this matrix is not square.
	 */
	Matrix<F> inverse();

	/**
	 * Indicates if this matrix is square.
	 *
	 * @return <code>getNumberOfRows() == getNumberOfColumns()</code>
	 */
	boolean isSquare();

	/**
	 * Returns the lower/upper decomposition of this matrix.
	 * 
	 * @throws DimensionException if this matrix is not square.
	 */
	LowerUpper<F> lowerUpper();

	/**
	 * Returns the difference between this matrix and the one specified.
	 *
	 * @param  that the matrix to be subtracted.
	 * @return <code>this - that</code>.
	 * @throws  DimensionException matrices's dimensions are different.
	 */
	Matrix<F> minus(Matrix<F> that);

	/**
	 * Returns the number of columns <code>n</code> for this matrix.
	 *
	 * @return n, the number of columns.
	 */
	int numberOfColumns();

	/**
	 * Returns the number of rows <code>m</code> for this matrix.
	 *
	 * @return m, the number of rows.
	 */
	int numberOfRows();

	/**
	 * Returns the negation of this matrix.
	 *
	 * @return <code>-this</code>.
	 */
	Matrix<F> opposite();

	/**
	 * Returns the sum of this matrix with the one specified.
	 *
	 * @param   that the matrix to be added.
	 * @return  <code>this + that</code>.
	 * @throws  DimensionException matrices's dimensions are different.
	 */
	Matrix<F> plus(Matrix<F> that);

	/**
	 * Returns this matrix raised at the specified exponent.
	 *
	 * @param  exp the exponent.
	 * @return <code>this<sup>exp</sup></code>
	 * @throws DimensionException if this matrix is not square.
	 */
	Matrix<F> pow(int exp);

	/**
	 * Returns the inverse or pseudo-inverse if this matrix if not square.
	 *
	 * @return the inverse or pseudo-inverse of this matrix.
	 */
	Matrix<F> pseudoInverse();

	/**
	 * Returns the row identified by the specified index of this matrix.
	 *
	 * @param  i the row index (range [0..m[).
	 * @return the vector holding the specified row.
	 * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i gt;= m)</code>
	 */
	Vector<F> row(int i);

	/**
	 * Solves this matrix for the specified matrix (returns <code>x</code>
	 * such as <code>this · x = y</code>). 
	 * 
	 * @param  y the matrix for which the solution is calculated.
	 * @return <code>x</code> such as <code>this · x = y</code>
	 * @throws DimensionException if that matrix is not square or dimensions 
	 *         do not match.
	 */
	Matrix<F> solve(Matrix<F> y);

	/**
	 * Solves this matrix for the specified vector (convenience method)
	 * 
	 * @param  y the vector for which the solution is calculated.
	 * @return {@code solve(y.asColumn()).getColumn(0)}
	 * @throws DimensionException if that matrix is not square or dimensions 
	 *         do not match.
	 * @see #solve(org.jscience.mathematics.vector.Matrix)
	 */
	Vector<F> solve(Vector<F> y);

	/**
	 * Returns a view over a portion of this matrix.
	 * 
	 * @param fromRowIndex low endpoint (inclusive) of this matrix row.
	 * @param toRowIndex high endpoint (exclusive) of this matrix row.
	 * @param fromColumnIndex low endpoint (inclusive) of this matrix column.
	 * @param toColumnIndex high endpoint (exclusive) of this matrix column.
	 * @return a matrix view over the specified index ranges.
	 * @throws IndexOutOfBoundsException if {@code (fromRowIndex < 0) || 
	 *         (toRowIndex > numberOfRows()) || (fromRowIndex > toRowIndex)}
	 *          or {@code (fromColumnIndex < 0) || (toColumnIndex > 
	 *          numberOfColumns()) || (fromColumnIndex > toColumnIndex)}
	 */
	Matrix<F> subMatrix(int fromRowIndex, int toRowIndex, int fromColumnIndex,
			int toColumnIndex);

	/**
	 * Returns the linear algebraic matrix tensor product of this matrix
	 * and another (Kronecker product).
	 *
	 * @param  that the second matrix.
	 * @return <code>this &otimes; that</code>
	 * @see    <a href="http://en.wikipedia.org/wiki/Kronecker_product">
	 *         Wikipedia: Kronecker Product</a>
	 */
	Matrix<F> tensor(Matrix<F> that);

	/**
	 * Returns the product of this matrix by the specified factor.
	 *
	 * @param  k the coefficient multiplier.
	 * @return <code>this · k</code>
	 */
	Matrix<F> times(F k);

	/**
	 * Returns the product of this matrix with the one specified.
	 *
	 * @param  that the matrix multiplier.
	 * @return <code>this · that</code>.
	 * @throws DimensionException if <code>
	 *         this.getNumberOfColumns() != that.getNumberOfRows()</code>.
	 */
	Matrix<F> times(Matrix<F> that);

	/**
	 * Returns the product of this matrix by the specified column vector
	 * (convenience method).
	 *
	 * @param  v the column vector.
	 * @return <code>this · v.asColumn()</code>
	 * @throws DimensionException if <code>
	 *         v.getDimension() != this.getNumberOfColumns()<code>
	 * @see #times(org.jscience.mathematics.vector.Matrix)
	 */
	Vector<F> times(Vector<F> v);

	/**
	 * Returns the trace of this matrix.
	 *
	 * @return the sum of the diagonal elements.
	 */
	F trace();

	/**
	 * Returns the transpose of this matrix.
	 *
	 * @return <code>A'</code>.
	 */
	Matrix<F> transpose();

}
