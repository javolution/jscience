/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import java.io.IOException;
import java.io.Serializable;
import javolution.lang.Constant;
import javolution.lang.MathLib;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.util.function.Orders;
import javolution.util.function.Equality;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import org.jscience.mathematics.internal.matrix.DenseMatrixImpl;
import org.jscience.mathematics.matrix.decomposition.LowerUpper;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A {@link Matrix matrix} with most elements different from zero.</p>
 *     
  * <p> This abstract class minimizes the effort required to implement the
 *     {@link Matrix matrix} interface for dense matrices.<p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 */
@Constant
@DefaultTextFormat(DenseMatrix.Text.class)
@DefaultXMLFormat(DenseMatrix.XML.class)
public abstract class DenseMatrix<F extends Field<F>> implements Matrix<F>,
        Serializable {

    /**
     * Defines the default text format for dense matrices (list of rows). 
     */
    public static class Text extends TextFormat<DenseMatrix<?>> {

        @Override
        public Appendable format(DenseMatrix<?> that, final Appendable dest)
                throws IOException {
            dest.append('{');
            for (int i = 0, m = that.numberOfRows();;) {
                TextContext.format(that.row(i), dest);
                if (++i >= m)
                    break;
                dest.append(',').append('\n').append(' ');
            }
            return dest.append('}');
        }

        @Override
        public DenseMatrix<?> parse(CharSequence csq, Cursor cursor)
                throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Defines the default XML representation for dense matrices. For example:
     * [code]
     * <DenseMatrix rows="2' columns="2">
     *     <Rational value="1/3" /> <Rational value="3/5" />
     *     <Rational value="1/3" /> <Rational value="3/5" />
     * </DenseMatrix>[/code]
     */
    public static class XML extends XMLFormat<DenseMatrix<?>> {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public DenseMatrix<?> newInstance(Class<? extends DenseMatrix<?>> cls,
                InputElement xml) throws XMLStreamException {
            int m = xml.getAttribute("rows", 0);
            int n = xml.getAttribute("columns", 0);
            Field[] elements = new Field[m * n];
            int i = 0;
            while (xml.hasNext()) {
                elements[i++] = xml.getNext();
            }
            return DenseMatrix.of(m, n, elements);
        }

        @Override
        public void read(InputElement xml, DenseMatrix<?> v)
                throws XMLStreamException {
            // Do nothing, vector already read.
        }

        @Override
        public void write(DenseMatrix<?> that, OutputElement xml)
                throws XMLStreamException {
            int m = that.numberOfRows();
            int n = that.numberOfColumns();
            xml.setAttribute("rows", m);
            xml.setAttribute("columns", n);
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++)
                    xml.add(that.get(i, j));
        }
    }

    /** Vector view over the matrix columns. */
    private class Column extends DenseVector<F> {
        private static final long serialVersionUID = 0x500L; // Version.
        private final int j;

        Column(int j) {
            this.j = j;
        }

        @Override
        public int dimension() {
            return DenseMatrix.this.numberOfRows();
        }

        @Override
        public F get(int i) {
            return DenseMatrix.this.get(i, j);
        }

    }

    /** Vector view over the matrix diagonal. */
    private class Diagonal extends DenseVector<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public int dimension() {
            return MathLib.min(DenseMatrix.this.numberOfRows(),
                    DenseMatrix.this.numberOfColumns());
        }

        @Override
        public F get(int i) {
            return DenseMatrix.this.get(i, i);
        }

    }

    /** Vector view over the matrix rows. */
    private class Row extends DenseVector<F> {
        private static final long serialVersionUID = 0x500L; // Version.
        private final int i;

        Row(int i) {
            this.i = i;
        }

        @Override
        public int dimension() {
            return DenseMatrix.this.numberOfColumns();
        }

        @Override
        public F get(int j) {
            return DenseMatrix.this.get(i, j);
        }

    }

    /** Sub-Matrix View. */
    private class SubMatrix extends DenseMatrix<F> {
        private static final long serialVersionUID = DenseMatrix.serialVersionUID;
        private int fromColumnIndex, numberOfColumns;
        private int fromRowIndex, numberOfRows;

        private SubMatrix(int fromRowIndex, int toRowIndex,
                int fromColumnIndex, int toColumnIndex) {
            this.fromRowIndex = fromRowIndex;
            this.numberOfRows = toRowIndex - fromRowIndex;
            this.fromColumnIndex = fromColumnIndex;
            this.numberOfColumns = toColumnIndex - fromColumnIndex;
        }

        @Override
        public F get(int i, int j) {
            if ((i < 0) || (i >= numberOfRows) || (j < 0)
                    || (j >= numberOfColumns))
                throw new IllegalArgumentException();
            return get(i + fromRowIndex, j + fromColumnIndex);
        }

        @Override
        public int numberOfColumns() {
            return numberOfColumns;
        }

        @Override
        public int numberOfRows() {
            return numberOfRows;
        }
    }

    /** Transpose view over this matrix. */
    private class Transpose extends DenseMatrix<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public F get(int i, int j) {
            return DenseMatrix.this.get(j, i);
        }

        @Override
        public int numberOfColumns() {
            return DenseMatrix.this.numberOfRows();
        }

        @Override
        public int numberOfRows() {
            return DenseMatrix.this.numberOfColumns();
        }

    }

    private static final long serialVersionUID = 0x500L; // Version.

    /**
     * Returns a dense matrix from a 2-dimensional array (convenience method).
     * 
     * @param data the 2-dimensional array holding the matrix element.
     * @throws DimensionException if the sub-arrays don't have all the same
     *         length.
     */
    public static <F extends Field<F>> DenseMatrix<F> of(F[][] data) {
        final int m = data.length;
        final int n = data[0].length;
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++) {
            F[] row = data[i];
            if (row.length != n)
                throw new DimensionException();
            int ixn = i * n;
            for (int j = 0; j < n; j++)
                elements[ixn + j] = row[j];
        }
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    /**
     * Returns a dense matrix of specified dimension holding the specified
     * elements.
     * 
     * @param m the number of rows.
     * @param n the number of columns.
     * @param elements the matrix's elements.
     * @throws DimensionException if {@code m * n |= elements.length}
     */
    public static <F extends Field<F>> DenseMatrix<F> of(int m, int n,
            @Constant F... elements) {
        if (m * n != elements.length)
            throw new DimensionException();
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    /**
     * Converts (if required) the specified matrix to a dense matrix 
     * (convenience method).
     */
    public static <F extends Field<F>> DenseMatrix<F> of(Matrix<F> that) {
        if (that instanceof DenseMatrix)
            return (DenseMatrix<F>) that;
        int m = that.numberOfRows();
        int n = that.numberOfColumns();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                elements[i * n + j] = that.get(i, j);
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    /**
     * Returns a dense matrix holding the specified rows (convenience method).
     * 
     * @param rows this vector rows.
     * @throws DimensionException if the rows don't have all the same dimension.
     */
    public static <F extends Field<F>> DenseMatrix<F> of(Vector<F>... rows) {
        final int m = rows.length;
        final int n = rows[0].dimension();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++) {
            Vector<F> row = rows[i];
            if (row.dimension() != n)
                throw new DimensionException();
            int ixn = i * n;
            for (int j = 0; j < n; j++)
                elements[ixn + j] = row.get(j);
        }
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    @Override
    public DenseMatrix<F> adjoint() {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                F cofactor = this.cofactor(i, j);
                elements[i * n + j] = ((i + j) % 2 == 0) ? cofactor : cofactor
                        .opposite();
            }
        return new DenseMatrixImpl<F>(m, n, elements).transpose();
    }

    @Override
    public F cofactor(int i, int j) {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        if ((m == 1) || (n == 1))
            throw new DimensionException();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[(m - 1) * (n - 1)];
        for (int ii = 0, iii = 0; ii < m; ii++) {
            if (ii == i)
                continue;
            for (int jj = 0, jjj = 0; jj < n; jj++) {
                if (jj == j)
                    continue;
                elements[iii * n + jjj++] = get(ii, jj);
            }
            iii++;
        }
        return new DenseMatrixImpl<F>(m - 1, n - 1, elements).determinant();
    }

    @Override
    public DenseVector<F> column(int j) {
        return new Column(j);
    }

    @Override
    public F determinant() {
        return lowerUpper().determinant();
    }

    @Override
    public DenseVector<F> diagonal() {
        return new Diagonal();
    }

    @Override
    public DenseMatrix<F> divides(Matrix<F> that) {
        return this.times(that.inverse());
    }

    @Override
    public boolean equals(Matrix<F> that, Equality<? super F> cmp) {
        final int m = numberOfRows();
        if (that.numberOfRows() != m)
            return false;
        for (int i = 0; i < m; i++)
            if (this.row(i).equals(that.row(i), cmp))
                return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        return (obj instanceof Matrix) ? equals((Matrix<F>) obj,
                Orders.STANDARD) : false;
    }

    @Override
    public void forEach(
            org.jscience.mathematics.matrix.Matrix.Consumer<? super F> consumer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void forEachNonZero(
            org.jscience.mathematics.matrix.Matrix.Consumer<? super F> consumer) {
        // TODO Auto-generated method stub

    }

    @Override
    public int hashCode() {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        if ((m == 0) || (n == 0))
            return 0;
        F zero = get(0, 0).plus(get(0, 0).opposite());
        int hash = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                F element = get(i, j);
                if (zero.equals(element))
                    continue;
                hash = 31 * hash + element.hashCode();
            }
        return hash;
    }

    @Override
    public DenseMatrix<F> inverse() {
        return lowerUpper().inverse();
    }

    @Override
    public boolean isSquare() {
        return this.numberOfRows() == this.numberOfColumns();
    }

    @Override
    public LowerUpper<F> lowerUpper() {
        return null; // TODO.
    }

    @Override
    public DenseMatrix<F> minus(Matrix<F> that) {
        return this.plus(that.opposite());
    }

    @Override
    public DenseMatrix<F> opposite() {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                elements[i * n + j] = get(i, j).opposite();
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    @Override
    public DenseMatrix<F> plus(Matrix<F> that) {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                elements[i * n + j] = get(i, j).plus(that.get(i, j));
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    @Override
    public DenseMatrix<F> pow(int exp) {
        if (exp > 0) {
            DenseMatrix<F> pow2 = this;
            DenseMatrix<F> result = null;
            while (exp >= 1) { // Iteration.
                if ((exp & 1) == 1) {
                    result = (result == null) ? pow2 : result.times(pow2);
                }
                pow2 = pow2.times(pow2);
                exp >>>= 1;
            }
            return result;
        } else if (exp == 0) {
            return this.times(this.inverse()); // Identity.
        } else {
            return this.pow(-exp).inverse();
        }
    }

    @Override
    public DenseMatrix<F> pseudoInverse() {
        if (isSquare())
            return this.inverse();
        DenseMatrix<F> thisTranspose = this.transpose();
        return (thisTranspose.times(this)).inverse().times(thisTranspose);
    }

    @Override
    public DenseVector<F> row(int i) {
        return new Row(i);
    }

    @Override
    public DenseMatrix<F> solve(Matrix<F> Y) {
        return lowerUpper().solve(Y);
    }

    @Override
    public DenseVector<F> solve(Vector<F> y) {
        return lowerUpper().solve(y.column()).row(0);
    }

    @Override
    public DenseMatrix<F> subMatrix(int fromRowIndex, int toRowIndex,
            int fromColumnIndex, int toColumnIndex) {
        if ((fromRowIndex < 0) || (toRowIndex > numberOfRows())
                || (fromRowIndex > toRowIndex))
            throw new IndexOutOfBoundsException();
        if ((fromColumnIndex < 0) || (toColumnIndex > numberOfRows())
                || (fromColumnIndex > toColumnIndex))
            throw new IndexOutOfBoundsException();
        return new SubMatrix(fromRowIndex, toRowIndex, fromColumnIndex,
                toColumnIndex);
    }

    @Override
    public Matrix<F> tensor(Matrix<F> that) {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        final int p = that.numberOfRows();
        final int q = that.numberOfColumns();
        //  If this is a m-by-n matrix and that is a p-by-q matrix,
        // then the Kronecker product is the mp-by-nq block.
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * p * n * q];
        for (int i0 = 0; i0 < m; i0++)
            for (int i1 = 0; i1 < p; i1++)
                for (int j0 = 0; j0 < n; j0++)
                    for (int j1 = 0; j1 < q; j1++)
                        elements[(i0 * m + i1) * n * q + (j0 * n + j1)] = this
                                .get(i0, j0).times(that.get(i1, j1));
        return new DenseMatrixImpl<F>(m * p, n * q, elements);
    }

    @Override
    public DenseMatrix<F> times(F k) {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                elements[i * n + j] = get(i, j).times(k);
        return new DenseMatrixImpl<F>(m, n, elements);
    }

    @Override
    public DenseMatrix<F> times(Matrix<F> that) {
        final int m = this.numberOfRows();
        final int n = this.numberOfColumns();
        final int q = that.numberOfColumns();
        if (n != that.numberOfRows())
            throw new DimensionException();
        //  This is a m-by-n matrix and that is a n-by-q matrix, the matrix result is mxq
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[m * q];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < q; j++)
                elements[i * q + j] = this.row(i).times(that.column(j));
        return new DenseMatrixImpl<F>(m, q, elements);
    }

    @Override
    public DenseVector<F> times(Vector<F> v) {
        return this.times(v.column()).column(0);
    }

    @Override
    public String toString() {
        return TextContext.getFormat(DenseMatrix.class).format(this);
    }

    @Override
    public F trace() {
        F sum = this.get(0, 0);
        for (int i = MathLib.min(numberOfColumns(), numberOfRows()); --i > 0;)
            sum = sum.plus(get(i, i));
        return sum;
    }

    @Override
    public DenseMatrix<F> transpose() {
        return new Transpose();
    }

}
