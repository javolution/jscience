/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javolution.context.StackContext;
import javolution.lang.MathLib;
import javolution.lang.Realtime;
import javolution.lang.ValueType;
import javolution.text.Cursor;
import javolution.text.Text;
import javolution.text.TextFormat;
import javolution.util.FastTable;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.structure.Ring;
import org.jscience.mathematics.structure.VectorSpace;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents a rectangular table of elements of a ring-like 
 *     algebraic structure.</p>
 *     
 * <p> Instances of this class are usually created from static factory methods.
 *     [code]
 *        // Creates a matrix (2x3) of 64 bits floating points numbers.
 *        Matrix<Float64> M0 = Matrix.valueOf(new double[][]
 *            {{ 1.1, 1.2, 1.3 },
 *             { 2.1, 2.2, 2.3 }};
 *
 *        // Creates a dense matrix (2x2) of rational numbers.
 *        DenseMatrix<Rational> M1 = DenseMatrix.valueOf(new Rational[][]
 *            { Rational.valueOf(23, 45), Rational.valueOf(33, 75) },
 *            { Rational.valueOf(15, 31), Rational.valueOf(-20, 45)});
 *
 *        // Creates a sparse matrix (16x2) of decimal numbers.
 *        SparseMatrix<Decimal> M2 = SparseMatrix.valueOf(
 *            SparseVector.valueOf(3, Decimal.valueOf("3.3"), 16),
 *            SparseVector.valueOf(7, Decimal.valueOf("-3.7"), 16));
 *
 *        // Creates an identity matrix (4x4) of complex numbers.
 *        DiagonalMatrix<Complex> IDENTITY = DiagonalMatrix.valueOf(4, Complex.ONE);
 *     [/code]
 *     Users may creates additional matrix specialization. For example:
 *     [code]
 *     public class TriangularMatrix<F extends Field<F>> extends Matrix<F> {
 *          ...
 *     }
 *     ...
 *     public class BandMatrix<F extends Field<F>> extends SparseMatrix<F> {
 *          ...
 *     }
 *     [/code]
 *     </p>
 *     
 * <p> Non-commutative field multiplication is supported. Invertible square 
 *     matrices may form a non-commutative field (also called a division
 *     ring). In which case this class may be used to resolve system of linear
 *     equations with matrix coefficients.</p>
 *     
 * <p> Implementation Note: Matrices may use {@link 
 *     javolution.context.StackContext StackContext} and {@link 
 *     javolution.context.ConcurrentContext ConcurrentContext} in order to 
 *     minimize heap allocation and accelerate calculations on multi-core 
 *     systems.</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.3, December 24, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Matrix_%28mathematics%29">
 *      Wikipedia: Matrix (mathematics)</a>
 */
public abstract class Matrix<F extends Field<F>>
         implements VectorSpace<Matrix<F>, F>, Ring<Matrix<F>>, ValueType, Realtime {

    /**
     * Defines the default text format for matrices (formatting only).
     * This format list this matrix's rows (vectors),
     * e.g. rational matrix "{{30/23, 12/7}, {33/13, -2/7}}.
     * The representation uses the current format associated to the matrix's vectors.
     * @see TextFormat#getInstance
     */
     protected static final TextFormat<Matrix> DEFAULT_MATRIX_FORMAT = new TextFormat<Matrix>(Matrix.class) {

        @Override
        public Appendable format(Matrix M, Appendable out) throws IOException {
            out.append('{');
            for (int i = 0, n = M.getNumberOfRows(); i < n;) {
                VectorImpl vector = M.getRow(i);
                TextFormat vectorFormat = TextFormat.getInstance(vector.getClass());
                vectorFormat.format(vector, out);
                if (++i < n) { // More to append.
                    out.append(", ");
                }
            }
            return out.append('}');
        }

        @Override
        public boolean isParsingSupported() {
            return false;
        }

        @Override
        public Matrix parse(CharSequence csq, Cursor cursor) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Parsing not supported for generic vector.");
        }
    };

    /**
     * Returns a matrix holding the specified <code>double</code> values
     * (convenience method).
     *
     * @param values the matrix values.
     * @return the matrix having the specified values.
     */
    public static Matrix<Real> valueOf(double[][] values) {
        return Float64Matrix.valueOf(values);
    }

    /**
     * Default constructor (for sub-classes).
     */
    protected Matrix() {
    }

    /**
     * Returns the number of rows <code>m</code> for this matrix.
     *
     * @return m, the number of rows.
     */
    public abstract int getNumberOfRows();

    /**
     * Returns the number of columns <code>n</code> for this matrix.
     *
     * @return n, the number of columns.
     */
    public abstract int getNumberOfColumns();

    /**
     * Returns a single element from this matrix.
     *
     * @param  i the row index (range [0..m[).
     * @param  j the column index (range [0..n[).
     * @return the element read at [i,j].
     * @throws IndexOutOfBoundsException <code>
     *         ((i &lt; 0) || (i &gt;= m)) || ((j &lt; 0) || (j &gt;= n))</code>
     */
    public abstract F get(int i, int j);

    /**
     * Returns the row identified by the specified index in this matrix.
     *
     * @param  i the row index (range [0..m[).
     * @return the vector holding the specified row.
     * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i gt;= m)</code>
     */
    public abstract VectorImpl<F> getRow(int i);

    /**
     * Returns the column identified by the specified index in this matrix.
     *
     * @param  j the column index (range [0..n[).
     * @return the vector holding the specified column.
     * @throws IndexOutOfBoundsException <code>(j &lt; 0) || (j &gt;= n)</code>
     */
    public abstract VectorImpl<F> getColumn(int j);

    /**
     * Returns the diagonal vector.
     *
     * @return the vector holding the diagonal elements.
     */
    public VectorImpl<F> getDiagonal() {
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        final int dimension = MathLib.min(m, n);
        DenseVectorImpl<F> V = DenseVectorImpl.FACTORY.object();
        for (int i = 0; i < dimension; i++) {
            V._elements.add(this.get(i, i));
        }
        return V;
    }

    /**
     * Returns the sub-matrix formed by the elements from the specified
     * rows and columns. The indices don't have to be ordered, for example
     * <code>getSubMatrix(Index.valuesOf(1, 0), Index.rangeOf(0, n))</code>
     * applied on a mxn matrix would result in a two rows matrix holding
     * the first and second rows exchanged.
     *
     * @return the corresponding sub-matrix.
     * @throws IndexOutOfBoundsException if any of the indices is greater
     *         than the associated dimension.
     */
    public abstract Matrix<F> getSubMatrix(List<Index> rows, List<Index> columns);

    /**
     * Returns the negation of this matrix.
     *
     * @return <code>-this</code>.
     */
    public abstract Matrix<F> opposite();

    /**
     * Returns the sum of this matrix with the one specified.
     *
     * @param   that the matrix to be added.
     * @return  <code>this + that</code>.
     * @throws  DimensionException matrices's dimensions are different.
     */
    public abstract Matrix<F> plus(Matrix<F> that);

    /**
     * Returns the difference between this matrix and the one specified.
     *
     * @param  that the matrix to be subtracted.
     * @return <code>this - that</code>.
     * @throws  DimensionException matrices's dimensions are different.
     */
    public Matrix<F> minus(Matrix<F> that) {
        return this.plus(that.opposite());
    }

    /**
     * Returns the product of this matrix by the specified factor.
     *
     * @param  k the coefficient multiplier.
     * @return <code>this · k</code>
     */
    public abstract Matrix<F> times(F k);

    /**
     * Returns the product of this matrix by the specified column vector
     * (convenience method).
     *
     * @param  v the column vector.
     * @return <code>this · v</code>
     * @throws DimensionException if <code>
     *         v.getDimension() != this.getNumberOfColumns()<code>
     * @see #times(org.jscience.mathematics.vector.Matrix)
     */
    public VectorImpl<F> times(VectorImpl<F> v) {
        DenseMatrix M = DenseMatrix.valueOf(v).transpose();
        return this.times(M).column(0);
    }

    /**
     * Returns the product of this matrix with the one specified.
     *
     * @param  that the matrix multiplier.
     * @return <code>this · that</code>.
     * @throws DimensionException if <code>
     *         this.getNumberOfColumns() != that.getNumberOfRows()</code>.
     */
    public abstract Matrix<F> times(Matrix<F> that);

    /**
     * Returns the inverse of this matrix (must be square).
     * The default implementation returns
     * <code>determinant.inverse().times(this.adjoint())</code>
     *
     * @return <code>1 / this</code>
     * @throws DimensionException if this matrix is not square.
     */
    public Matrix<F> inverse() {
        if (!isSquare())
            throw new DimensionException("Matrix not square");
        // F product might not be commutative.
        DiagonalMatrix<F> detInv = DiagonalMatrix.valueOf(this.getNumberOfRows(),
                this.determinant().reciprocal());
        return detInv.times(this.adjoint());
    }

    /**
     * Returns this matrix divided by the one specified.
     *
     * @param  that the matrix divisor.
     * @return <code>this / that</code>.
     * @throws DimensionException if that matrix is not square or dimensions 
     *         do not match.
     */
    public Matrix<F> divide(Matrix<F> that) {
        return this.times(that.inverse());
    }

    /**
     * Returns the inverse or pseudo-inverse if this matrix if not square.
     *
     * @return the inverse or pseudo-inverse of this matrix.
     */
    public Matrix<F> pseudoInverse() {
        if (isSquare())
            return this.inverse();
        Matrix<F> thisTranspose = this.transpose();
        return (thisTranspose.times(this)).inverse().times(thisTranspose);
    }

    /**
     * Returns the determinant of this matrix. The default implementation
     * uses an expansion by minors (also known as Laplacian).
     * This algorithm is division free but too slow for {@link DenseMatrix}
     * which uses the LU decomposition.
     *
     * @return this matrix determinant.
     * @throws DimensionException if this matrix is not square.
     */
    public F determinant() {
        if (!isSquare())
            throw new DimensionException("Matrix not square");
        if (this.getNumberOfRows() == 1)
            return this.get(0, 0);
        // This algorithm is division free but too slow for dense matrix.
        VectorImpl<F> row0 = this.getRow(0);
        F det = null;
        for (int i = 0; i < row0.getDimension(); i++) {
            F e = row0.get(i);
            if (e.equals(e.opposite()))
                continue; // Optimization.
            F d = e.times(cofactor(0, i));
            if (i % 2 != 0) {
                d = d.opposite();
            }
            det = (det == null) ? d : det.plus(d);
        }
        return det == null ? row0.get(0) : det; // det null only if zero everywhere.
    }

    /**
     * Returns the transpose of this matrix.
     *
     * @return <code>A'</code>.
     */
    public abstract Matrix<F> transpose();

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
    public F cofactor(int i, int j) {
        FastTable<Index> rows = FastTable.newInstance();
        FastTable<Index> columns = FastTable.newInstance();
        try {
            for (int ii = 0; ii < this.getNumberOfRows(); ii++) {
                if (ii == i)
                    continue; // Don't include row i.
                rows.add(Index.valueOf(ii));
            }
            for (int jj = 0; jj < this.getNumberOfColumns(); jj++) {
                if (jj == j)
                    continue; // Don't include column j.
                columns.add(Index.valueOf(jj));
            }
            return this.getSubMatrix(rows, columns).determinant();
        } finally {
            FastTable.recycle(rows);
            FastTable.recycle(columns);
        }
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
    public Matrix<F> adjoint() {
        DenseMatrixImpl<F> M = DenseMatrixImpl.FACTORY.object();
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        for (int i = 0; i < m; i++) {
            DenseVectorImpl<F> V = DenseVectorImpl.FACTORY.object();
            for (int j = 0; j < n; j++) {
                F cofactor = this.cofactor(i, j);
                V._elements.add(((i + j) % 2 == 0) ? cofactor : cofactor.opposite());
            }
            M._rows.add(V);
        }
        return M.transpose();
    }

    /**
     * Indicates if this matrix is square.
     *
     * @return <code>getNumberOfRows() == getNumberOfColumns()</code>
     */
    public boolean isSquare() {
        return getNumberOfRows() == getNumberOfColumns();
    }

    /**
     * Solves this matrix for the specified vector (convenience method)
     * 
     * @param  y the vector for which the solution is calculated.
     * @return <code>solve(y.transpose())</code>
     * @throws DimensionException if that matrix is not square or dimensions 
     *         do not match.
     * @see #solve(org.jscience.mathematics.vector.Matrix)
     */
    public VectorImpl<F> solve(VectorImpl<F> y) {
        DenseMatrix M = DenseMatrix.valueOf(y).transpose();
        return solve(M).column(0);
    }

    /**
     * Solves this matrix for the specified matrix (returns <code>x</code>
     * such as <code>this · x = y</code>). The default implementation
     * returns <code>this.inverse().times(y)</code>
     * 
     * @param  y the matrix for which the solution is calculated.
     * @return <code>x</code> such as <code>this · x = y</code>
     * @throws DimensionException if that matrix is not square or dimensions 
     *         do not match.
     */
    public Matrix<F> solve(Matrix<F> y) {
        return this.inverse().times(y); // Default implementation.
    }

    /**
     * Returns this matrix raised at the specified exponent.
     *
     * @param  exp the exponent.
     * @return <code>this<sup>exp</sup></code>
     * @throws DimensionException if this matrix is not square.
     */
    public Matrix<F> pow(int exp) {
        if (exp > 0) {
            StackContext.enter();
            try {
                Matrix<F> pow2 = this;
                Matrix<F> result = null;
                while (exp >= 1) { // Iteration.
                    if ((exp & 1) == 1) {
                        result = (result == null) ? pow2 : result.times(pow2);
                    }
                    pow2 = pow2.times(pow2);
                    exp >>>= 1;
                }
                return StackContext.outerCopy(result);
            } finally {
                StackContext.exit();
            }
        } else if (exp == 0) {
            return this.times(this.inverse()); // Identity.
        } else {
            return this.pow(-exp).inverse();
        }
    }

    /**
     * Returns the trace of this matrix.
     *
     * @return the sum of the diagonal elements.
     */
    public F trace() {
        F sum = this.get(0, 0);
        for (int i = MathLib.min(getNumberOfColumns(), getNumberOfRows()); --i > 0;) {
            sum = sum.plus(get(i, i));
        }
        return sum;
    }

    /**
     * Returns the linear algebraic matrix tensor product of this matrix
     * and another (Kronecker product).
     *
     * @param  that the second matrix.
     * @return <code>this &otimes; that</code>
     * @see    <a href="http://en.wikipedia.org/wiki/Kronecker_product">
     *         Wikipedia: Kronecker Product</a>
     */
    public Matrix<F> tensor(Matrix<F> that) {
        //  If this is a m-by-n matrix and that is a p-by-q matrix,
        // then the Kronecker product is the mp-by-nq block.
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        final int p = that.getNumberOfRows();
        final int q = that.getNumberOfColumns();
        DenseMatrixImpl M = DenseMatrixImpl.FACTORY.object();
        for (int i0 = 0; i0 < m; i0++) {
            for (int i1 = 0; i1 < p; i1++) {
                DenseVectorImpl V = DenseVectorImpl.FACTORY.object();
                for (int j0 = 0; j0 < n; j0++) {
                    for (int j1 = 0; j1 < q; j1++) {
                        F e = this.get(i0, j0).times(that.get(i1, j1));
                        V._elements.add(e);
                    }
                }
                M._rows.add(V);
            }
        }
        return M;
    }

    /**
     * Returns the vectorization of this matrix. The vectorization of 
     * a matrix is the column vector obtain by stacking the columns of the
     * matrix on top of one another.
     *
     * @return the vectorization of this matrix.
     * @see    <a href="http://en.wikipedia.org/wiki/Vectorization_%28mathematics%29">
     *         Wikipedia: Vectorization.</a>
     */
    public VectorImpl<F> vectorization() {
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        DenseVectorImpl V = DenseVectorImpl.FACTORY.object();
        for (int j = 0; j < n; j++) { // For each column.
            for (int i = 0; i < m; i++) {
                V._elements.add(this.get(i, j));
            }
        }
        return V;
    }

    /**
     * Returns the textual representation of this matrix.
     * This method cannot be overriden, sub-classes should define their own
     * textual format which will automatically be used here.
     *
     * @return <code>TextFormat.getInstance(this.getClass()).format(this)</code>
     * @see #DEFAULT_MATRIX_FORMAT
     */
    public final Text toText() {
        TextFormat<Matrix> textFormat = TextFormat.getInstance(this.getClass());
        return textFormat.format(this);
    }

    /**
     * Returns the text representation of this matrix as a 
     * <code>java.lang.String</code>.
     * This method cannot be overriden, sub-classes should define their own
     * textual format which will automatically be used here.
     *
     * @return <code>TextFormat.getInstance(this.getClass()).formatToString(this)</code>
     * @see #DEFAULT_MATRIX_FORMAT
     */
    @Override
    public final String toString() {
        TextFormat<Matrix> textFormat = TextFormat.getInstance(this.getClass());
        return textFormat.formatToString(this);
    }

    /**
     * Indicates if this matrix can be considered equals to the one 
     * specified using the specified comparator when testing for 
     * element equality. The specified comparator may allow for some 
     * tolerance in the difference between the matrix elements.
     *
     * @param  that the matrix to compare for equality.
     * @param  cmp the comparator to use when testing for element equality.
     * @return <code>true</code> if this matrix and the specified matrix are
     *         both matrices with equal elements according to the specified
     *         comparator; <code>false</code> otherwise.
     */
    public boolean equals(Matrix<F> that, Comparator<F> cmp) {
        if (this == that)
            return true;
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        if ((that.getNumberOfRows() != m) || (that.getNumberOfColumns() != n))
            return false;
        for (int i = m; --i >= 0;) {
            for (int j = n; --j >= 0;) {
                if (cmp.compare(this.get(i, j), that.get(i, j)) != 0)
                    return false;
            }
        }
        return true;
    }

    /**
     * Indicates if this matrix is strictly equal to the object specified.
     *
     * @param  that the object to compare for equality.
     * @return <code>true</code> if this matrix and the specified object are
     *         both matrices with equal elements; <code>false</code> otherwise.
     * @see    #equals(Matrix, Comparator)
     */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof Matrix))
            return false;
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        Matrix<?> M = (Matrix<?>) that;
        if ((M.getNumberOfRows() != m) || (M.getNumberOfColumns() != n))
            return false;
        for (int i = m; --i >= 0;) {
            for (int j = n; --j >= 0;) {
                if (!this.get(i, j).equals(M.get(i, j)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code value for this matrix.
     * Equals objects have equal hash codes.
     *
     * @return this matrix hash code value.
     * @see    #equals
     */
    @Override
    public int hashCode() {
        final int m = this.getNumberOfRows();
        final int n = this.getNumberOfColumns();
        int code = 0;
        for (int i = m; --i >= 0;) {
            for (int j = n; --j >= 0;) {
                code += get(i, j).hashCode();
            }
        }
        return code;
    }

    /**
     * Returns a copy of this matrix 
     * {@link javolution.context.AllocatorContext allocated} 
     * by the calling thread (possibly on the stack).
     *     
     * @return an identical and independant copy of this matrix.
     */
    public abstract Matrix<F> copy();
}
