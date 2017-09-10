/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.util.Arrays;
import java.util.List;

import javolution.context.ObjectFactory;
import javolution.util.FastTable;
import javolution.util.Index;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents the sparse matrix default implementation.</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2007
 */
public class SparseMatrixImpl<F extends Field<F>> implements SparseMatrix<F> {

    private final int dimension;
    private final F zero;
    private final List<Index> rowIndices; // Ordered.
    private final List<Index> columnIndices; // Ordered.
    private final FastTable<F> data;

    public SparseVectorImpl(int dimension, F zero, List<Index> indices, F[] data) {
    	this.dimension = dimension;
    	this.zero = zero;
    	this.indices = indices;
    	this.data = new FastTable<F>(data);
    }	
	/**
     * Holds this matrix rows.
     */
    final FastTable<SparseVectorImpl<F>> _rows = new FastTable<SparseVectorImpl<F>>();

    /**
     * Holds the transposed view of this matrix
     */
    private final TransposedView _transposedView = new TransposedView();

    // See parent static method.
    public static <F extends Field<F>> SparseMatrixImpl<F> valueOf(VectorImpl<F>... rows) {
        return SparseMatrixImpl.valueOf(Arrays.asList(rows));
    }

    // See parent static method.
    public static <F extends Field<F>> SparseMatrixImpl<F> valueOf(List<? extends VectorImpl<F>> rows) {
        SparseMatrixImpl<F> M = SparseMatrixImpl.FACTORY.object();
        final int n = rows.get(0).getDimension();
        for (VectorImpl<F> row : rows) {
            if (row.getDimension() != n)
                throw new DimensionException();
            M._rows.add(SparseVectorImpl.valueOf(row));
        }
        return M;
    }

    // See parent static method.
    public static <F extends Field<F>> SparseMatrixImpl<F> valueOf(Matrix<F> that) {
        if (that instanceof SparseMatrixImpl)
            return (SparseMatrixImpl) that;
        SparseMatrixImpl<F> M = SparseMatrixImpl.FACTORY.object();
        for (int i = 0, m = that.getNumberOfRows(); i < m; i++) {
            M._rows.add(SparseVectorImpl.valueOf(that.getRow(i)));
        }
        return M;
    }

    @Override
    public int getNumberOfRows() {
        return _rows.size();
    }

    @Override
    public int numberOfColumns() {
        return _rows.get(0).dimension();
    }

    @Override
    public F get(int i, int j) {
        return _rows.get(i).get(j);
    }

    @Override
    public SparseVectorImpl<F> getRow(int i) {
        return _rows.get(i);
    }

    @Override
    public SparseVectorImpl<F> getColumn(int j) {
        SparseVectorImpl<F> V = SparseVectorImpl.FACTORY.object();
        final Index indexJ = Index.valueOf(j);
        final int m = _rows.size();
        V._dimension = m;
        V._zero = _rows.get(0)._zero;
        for (int i = 0; i < m; i++) {
            F e = _rows.get(i)._elements.get(indexJ);
            if (e != null) {
                V._elements.put(Index.valueOf(i), e);
            }
        }
        return V;
    }

    @Override
    public SparseMatrixImpl<F> getSubMatrix(List<Index> rows, List<Index> columns) {
        SparseMatrixImpl<F> M = FACTORY.object();
        for (int i = 0, m = rows.size(); i < m; i++) {
            SparseVectorImpl<F> row = this.getRow(rows.get(i).intValue());
            M._rows.add(row.getSubVector(columns));
        }
        return M;
    }

    @Override
    public SparseMatrixImpl<F> opposite() {
        SparseMatrixImpl<F> M = FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).opposite());
        }
        return M;
    }

    @Override
    public Matrix<F> plus(Matrix<F> that) {
        if (that instanceof SparseMatrix) // Returns sparse matrix.
            return plus((SparseMatrix<F>) that);
        return that.plus(this);
    }

    private SparseMatrixImpl<F> plus(SparseMatrix<F> that) {
        SparseMatrixImpl<F> M = FACTORY.object();
        final int m = _rows.size();
        if (that.getNumberOfRows() != m)
            throw new DimensionException();
        for (int i = 0; i < m; i++) {
            M._rows.add(_rows.get(i).plus(that.getRow(i)));
        }
        return M;
    }

    @Override
    public SparseMatrixImpl<F> times(F k) {
        SparseMatrixImpl<F> M = FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).times(k));
        }
        return M;
    }

    @Override
    public Matrix<F> times(Matrix<F> that) {
        if (that instanceof SparseMatrix) // Returns a sparse matrix.
            return times((SparseMatrix<F>) that);
        // Else (AxB)=C, T(AxB)=T(C), T(B)xT(A)=T(C)
        return that.transpose().times(this.transpose()).transpose();
    }

    private SparseMatrixImpl<F> times(SparseMatrix<F> that) {
        //  This is a m-by-n matrix and that is a n-by-p matrix, the matrix result is mxp
        final int m = _rows.size();
        final int n = _rows.get(0).dimension(); // Number of columns of this.
        final int p = that.numberOfColumns(); // Number of columns of that.
        if (n != that.getNumberOfRows())
            throw new DimensionException();
        SparseMatrixImpl<F> M = FACTORY.object();
        for (int i = 0; i < m; i++) {
            SparseVectorImpl<F> V = SparseVectorImpl.FACTORY.object();
            V._dimension = p;
            V._zero = this._rows.get(i)._zero;
            for (int j = 0; j < p; j++) {
                F e = _rows.get(i).times(that.getColumn(j));
                if (!e.equals(V._zero)) {
                    V._elements.put(Index.valueOf(j), e);
                }
            }
            M._rows.add(V);
        }
        return M;
    }

    @Override
    public SparseMatrix<F> transpose() {
        return _transposedView;
    }

    @Override
    public SparseMatrixImpl<F> copy() {
        SparseMatrixImpl<F> M = SparseMatrixImpl.FACTORY.object();
        for (int i = 0, m = _rows.size(); i < m; i++) {
            M._rows.add(_rows.get(i).copy());
        }
        return M;
    }

    /**
     * Represents a transposed view of the outer matrix.
     */
    private class TransposedView extends SparseMatrix<F> {

        @Override
        public int getNumberOfRows() {
            return SparseMatrixImpl.this.numberOfColumns();
        }

        @Override
        public int numberOfColumns() {
            return SparseMatrixImpl.this.getNumberOfRows();
        }

        @Override
        public F get(int i, int j) {
            return SparseMatrixImpl.this.get(j, i);
        }

        @Override
        public SparseVectorImpl<F> getRow(int i) {
            return SparseMatrixImpl.this.getColumn(i);
        }

        @Override
        public SparseVectorImpl<F> getColumn(int j) {
            return SparseMatrixImpl.this.getRow(j);
        }

        @Override
        public SparseMatrix<F> getSubMatrix(List<Index> rows, List<Index> columns) {
            return SparseMatrixImpl.this.getSubMatrix(columns, rows)._transposedView;
        }

        @Override
        public SparseMatrix<F> opposite() {
            return SparseMatrixImpl.this.opposite()._transposedView;
        }

        @Override
        public Matrix<F> plus(Matrix<F> that) {
            return SparseMatrixImpl.this.plus(that.transpose()).transpose();
        }

        @Override
        public SparseMatrix<F> times(F k) {
            return SparseMatrixImpl.this.times(k)._transposedView;
        }

        @Override
        public Matrix<F> times(Matrix<F> that) {
            return SparseMatrixImpl.valueOf(this).times(that);
        }

        @Override
        public SparseMatrixImpl<F> transpose() {
            return SparseMatrixImpl.this;
        }

        @Override
        public SparseMatrixImpl<F> copy() {
            return SparseMatrixImpl.valueOf(this).copy();
        }
    }
    private static final long serialVersionUID = 1L;

}
