/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.util.List;

import javolution.context.ObjectFactory;
import javolution.util.Index;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents a square diagonal matrix. 
 *     It is a {@link SparseMatrix sparse matrix} for which the non-zero elements are
 *     on the matrix diagonal. Diagonal matrices are typically used to create
 *     identity matrices or coefficient matrix.
 *     For example:[code]
 *        // Creates a rational identiy matrix of 4x4
 *        DiagonalMatrix<Rational> identity = DiagonalMatrix.valueOf(4, Rational.ONE);
 *     [/code]</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2007
 */
public class DiagonalMatrix<F extends Field<F>> extends SparseMatrix<F> {

    /**
     * Holds the object factory.
     */
    static final ObjectFactory<DiagonalMatrix> FACTORY = new ObjectFactory<DiagonalMatrix>() {

        @Override
        protected DiagonalMatrix create() {
            return new DiagonalMatrix();
        }

        @Override
        protected void cleanup(DiagonalMatrix matrix) {
            matrix._diagonal = null;
        }
    };

    /**
     * Holds the diagonal vector.
     */
    private DenseVectorImpl<F> _diagonal;

    /**
     * Creates a square diagonal matrix always on the heap independently from
     * the current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     *
     * @param elements the diagonal elements.
     */
    public DiagonalMatrix(F... elements) {
        _diagonal = DenseVectorImpl.valueOf(elements);
    }

    /**
     * Returns a square diagonal matrix holding the specified elements as
     * diagonal elements.
     *
     * @param elements the diagonal elements.
     * @return the matrix having the specified diagonal elements.
     */
    public static <F extends Field<F>> DiagonalMatrix<F> valueOf(
            F... elements) {
        DiagonalMatrix M = FACTORY.object();
        M._diagonal = DenseVectorImpl.valueOf(elements);
        return M;
    }

    /**
     * Returns a square diagonal matrix holding the specified elements as
     * diagonal elements.
     *
     * @param elements the diagonal elements.
     * @return the matrix having the specified diagonal elements.
     */
    public static <F extends Field<F>> DiagonalMatrix<F> valueOfList(
            List<F> elements) {
        DiagonalMatrix M = FACTORY.object();
        M._diagonal = DenseVectorImpl.valueOf(elements);
        return M;
    }

    /**
     * Returns a square diagonal matrix of specified dimension holding the
     * same specified element on the diagonal.
     *
     * @param dimension the dimension of the diagonal vector.
     * @param element the element (repeated) on the diagonal.
     * @return the matrix having the specified element on the diagonal.
     */
    public static <F extends Field<F>> DiagonalMatrix<F> valueOf(
            int dimension, F element) {
        DiagonalMatrix M = FACTORY.object();
        DenseVectorImpl V = DenseVectorImpl.FACTORY.object();
        for (int i = 0; i < dimension; i++) {
            V._elements.add(element);
        }
        M._diagonal = V;
        return M;
    }

    /**
     * Returns a square diagonal matrix from the specified vector.
     * The matrix returned is squared and has the same diagonal elements
     * as the specified vector.
     *
     * @param diagonal the vector holding the diagonal element.
     * @return the corresponding diagonal matrix.
     */
    public static <F extends Field<F>> DiagonalMatrix<F> valueOf(VectorImpl<F> that) {
        DiagonalMatrix M = FACTORY.object();
        M._diagonal = DenseVectorImpl.valueOf(that);
        return M;
    }

    /**
     * Returns a square diagonal matrix from the specified matrix.
     * The matrix returned is squared and has the same diagonal elements
     * as the specified matrix, non-diagonal elements are zero.
     *
     * @param that the matrix to convert.
     * @return <code>that</code> or a diagonal matrix holding the same
     *         diagonal elements as the specified matrix.
     */
    public static <F extends Field<F>> DiagonalMatrix<F> valueOf(Matrix<F> that) {
        if (that instanceof DiagonalMatrix)
            return (DiagonalMatrix<F>) that;
        return DiagonalMatrix.valueOf(that.getDiagonal());
    }

    @Override
    public int getNumberOfRows() {
        return _diagonal.dimension();
    }

    @Override
    public int numberOfColumns() {
        return _diagonal.dimension();
    }

    @Override
    public F get(int i, int j) {
        if ((j < 0) || (j > _diagonal.dimension()))
            throw new IndexOutOfBoundsException();
        F e = _diagonal.get(i);
        return (i == j) ? e : e.plus(e.opposite()); // Zero
    }

    @Override
    public SparseMatrix<F> getSubMatrix(List<Index> rows, List<Index> columns) {
        return SparseMatrix.valueOf(this).getSubMatrix(rows, columns);
    }

    @Override
    public SparseVector<F> getRow(int i) {
        return SparseVector.valueOf(i, _diagonal.get(i), _diagonal.dimension());
    }

    @Override
    public SparseVector<F> getColumn(int j) {
        return getRow(j); // Same row and column.
    }

    @Override
    public DiagonalMatrix<F> opposite() {
        return DiagonalMatrix.valueOf(_diagonal.opposite());
    }

    @Override
    public Matrix<F> plus(Matrix<F> that) {
        if (that instanceof DiagonalMatrix)
            return plus((DiagonalMatrix<F>) that);
        return that.plus(this);
    }

    private DiagonalMatrix<F> plus(DiagonalMatrix<F> that) {
        return DiagonalMatrix.valueOf(this._diagonal.plus(that._diagonal));
    }

    @Override
    public SparseMatrix<F> times(F k) {
        return DiagonalMatrix.valueOf(_diagonal.times(k));
    }

    @Override
    public Matrix<F> times(Matrix<F> that) {
        if (that instanceof DiagonalMatrix)
            return times((DiagonalMatrix<F>) that);
        // Else (AxB)=C, T(AxB)=T(C), T(B)xT(A)=T(C)
        return that.transpose().times(this.transpose()).transpose();
    }

    private DiagonalMatrix<F> times(DiagonalMatrix<F> that) {
        final int n = _diagonal.dimension();
        if (that._diagonal.dimension() != n)
            throw new DimensionException();
        DenseVectorImpl<F> V = DenseVectorImpl.FACTORY.object();
        for (int i = 0; i < n; i++) {
            V._elements.add(this._diagonal.get(i).times(that._diagonal.get(i)));
        }
        return DiagonalMatrix.valueOf(V);
    }

    @Override
    public SparseMatrix<F> transpose() {
        return this;
    }

    @Override
    public SparseMatrix<F> copy() {
        DiagonalMatrix M = FACTORY.object();
        M._diagonal = _diagonal.copy();
        return M;
    }
    private static final long serialVersionUID = 1L;

}
