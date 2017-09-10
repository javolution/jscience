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
import java.util.Arrays;
import java.util.List;

import javolution.context.ObjectFactory;
import javolution.util.FastTable;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents a 64 bits floating point dense matrix.</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2007
 */
public class Float64Matrix extends DenseMatrix<Real> {

    /**
     * Holds the object factory.
     */
    static ObjectFactory<Float64Matrix> FACTORY = new ObjectFactory<Float64Matrix>() {

        @Override
        protected Float64Matrix create() {
            return new Float64Matrix();
        }

        @Override
        protected void cleanup(Float64Matrix matrix) {
            matrix._rows.reset();
        }
    };

    /**
     * Holds this matrix rows.
     */
    final FastTable<Float64Vector> _rows = new FastTable<Float64Vector>();

    /**
     * Holds the transposed view of this matrix
     */
    private final TransposedView _transposedView = new TransposedView();

    // See parent static method.
    public static Float64Matrix valueOfList(List<? extends VectorImpl<Real>> rows) {
        Float64Matrix M = Float64Matrix.FACTORY.object();
        final int n = rows.get(0).getDimension();
        for (VectorImpl<Real> row : rows) {
            if (row.getDimension() != n)
                throw new DimensionException();
            M._rows.add(Float64Vector.valueOfVector(row));
        }
        return M;
    }

    // See parent static method.
    public static Float64Matrix valueOfMatrix(Matrix<Real> that) {
        if (that instanceof Float64Matrix)
            return (Float64Matrix) that;
        Float64Matrix M = Float64Matrix.FACTORY.object();
        for (int i = 0, m = that.getNumberOfRows(); i < m; i++) {
            M._rows.add(Float64Vector.valueOfVector(that.getRow(i)));
        }
        return M;
    }

    // See parent static method.
    public static Float64Matrix valueOf(double[][] elements) {
        Float64Matrix M = Float64Matrix.FACTORY.object();
        for (int i = 0, m = elements.length; i < m; i++) {
            Float64Vector row = Float64Vector.valueOf(elements[i]);
            M._rows.add(row);
        }
        return M;
    }

    // See parent static method.
    public static Float64Matrix valueOfVector(VectorImpl<Real>... rows) {
        return Float64Matrix.valueOfList(Arrays.asList(rows));
    }

  /**
     * Returns the value of a floating point number from this matrix (fast).
     *
     * @param  i the floating point number first index.
     * @param  j the floating point number second index.
     * @return the value of the floating point number at <code>i,j</code>.
     * @throws IndexOutOfBoundsException <code>
     *         ((i &lt; 0) || (i &gt;= m)) || ((j &lt; 0) || (j &gt;= n))</code>
     */
    public double getValue(int i, int j) {
        return _rows.get(i).getValue(j);
    }

    @Override
    public int numberOfRows() {
        return _rows.size();
    }

    @Override
    public int numberOfColumns() {
        return _rows.get(0).getDimension();
    }

    @Override
    public Real get(int i, int j) {
        return _rows.get(i).get(j);
    }

    @Override
    public Float64Vector row(int i) {
        return _rows.get(i);
    }

    @Override
    public Float64Vector column(int j) {
        final int m = _rows.size();
        Float64Vector V = Float64Vector.FACTORY.array(m);
        V._dimension = m;
        for (int i = 0; i < m; i++) {
            V._values[i] = _rows.get(i)._values[j];
        }
        return V;
    }

    @Override
    public Float64Matrix getSubMatrix(List<Index> rows, List<Index> columns) {
        Float64Matrix M = FACTORY.object();
        for (int i = 0; i < rows.size(); i++) {
            Float64Vector row = this.getRow(rows.get(i).intValue());
            M._rows.add(row.getSubVector(columns));
        }
        return M;
    }

    @Override
    public Float64Matrix opposite() {
        Float64Matrix M = FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).opposite());
        }
        return M;
    }

    @Override
    public Float64Matrix plus(Matrix<Real> that) {
        Float64Matrix M = FACTORY.object();
        final int m = _rows.size();
        if (that.getNumberOfRows() != m)
            throw new DimensionException();
        for (int i = 0; i < m; i++) {
            M._rows.add(_rows.get(i).plus(that.getRow(i)));
        }
        return M;
    }

    @Override
    public Float64Matrix times(Real k) {
        Float64Matrix M = FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).times(k));
        }
        return M;
    }

    /**
     * Equivalent to <code>this.times(Float64.valueOf(k))</code>
     *
     * @param k the coefficient.
     * @return <code>this * k</code>
     */
    public Float64Matrix times(double k) {
        Float64Matrix M = FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).times(k));
        }
        return M;
    }

    @Override
    public Float64Matrix times(Matrix<Real> that) {
        //  This is a m-by-n matrix and that is a n-by-p matrix, the matrix result is mxp
        final int m = _rows.size();
        final int n = _rows.get(0).getDimension(); // Number of columns of this.
        final int p = that.getNumberOfColumns(); // Number of columns of that.
        if (n != that.getNumberOfRows())
            throw new DimensionException();
        Float64Matrix M = FACTORY.object();
        for (int i = 0; i < m; i++) {
            Float64Vector V = Float64Vector.FACTORY.array(p);
            V._dimension = p;
            for (int j = 0; j < p; j++) {
                Real element = _rows.get(i).times(that.getColumn(j));
                V._values[j] = element.doubleValue();
            }
            M._rows.add(V);
        }
        return M;
    }

    @Override
    public DenseMatrix<Real> transpose() {
        return _transposedView;
    }

    @Override
    public Float64Matrix copy() {
        Float64Matrix M = Float64Matrix.FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).copy());
        }
        return M;
    }

    /**
     * Represents a transposed view of the outer matrix.
     */
    private class TransposedView extends DenseMatrix<Real> {

        @Override
        public DenseVector<Real> row(int i) {
            return Float64Matrix.this.getColumn(i);
        }

        @Override
        public DenseVector<Real> column(int j) {
            return Float64Matrix.this.getRow(j);
        }

        @Override
        public DenseMatrix<Real> getSubMatrix(List<Index> rows, List<Index> columns) {
            return Float64Matrix.this.getSubMatrix(columns, rows)._transposedView;
        }

        @Override
        public DenseMatrix<Real> opposite() {
            return Float64Matrix.this.opposite()._transposedView;
        }

        @Override
        public DenseMatrix<Real> plus(Matrix<Real> that) {
            return Float64Matrix.this.plus(that.transpose())._transposedView;
        }

        @Override
        public DenseMatrix<Real> times(Real k) {
            return Float64Matrix.this.times(k)._transposedView;
        }

        @Override
        public DenseMatrix<Real> times(Matrix<Real> that) {
            return Float64Matrix.valueOf(this).times(that);
        }

        @Override
        public Float64Matrix transpose() {
            return Float64Matrix.this;
        }

        @Override
        public Float64Matrix copy() {
            return Float64Matrix.valueOfMatrix(this).copy();
        }

        @Override
        public int numberOfRows() {
            return Float64Matrix.this.getNumberOfColumns();
        }

        @Override
        public int numberOfColumns() {
            return Float64Matrix.this.getNumberOfRows();
        }

        @Override
        public Real get(int i, int j) {
            return Float64Matrix.this.get(j, i);
        }
    }

    private static final long serialVersionUID = 1L;

}
