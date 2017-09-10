/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.util.Comparator;
import java.util.List;

import javolution.util.FastTable;
import javolution.util.Index;
import javolution.xml.XMLFormat;
import javolution.xml.XMLFormat.InputElement;
import javolution.xml.XMLFormat.OutputElement;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents a matrix made of {@link SparseVector sparse
 *     vectors} (as rows). To create a sparse matrix made of column vectors the
 *     {@link #transpose} method can be used. 
 *     For example:[code]
 *        SparseVector<Rational> column0 = SparseVector.valueOf(...);
 *        SparseVector<Rational> column1 = SparseVector.valueOf(...);
 *        SparseMatrix<Rational> M = SparseMatrix.valueOf(column0, column1).transpose();
 *     [/code]</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2007
 */
public abstract class SparseMatrix<F extends Field<F>> extends Matrix<F> {

    /**
     * Holds the default XML representation for sparse matrices. For example:[code]
     *    <SparseMatrix >
     *        <SparseVector dimension="4">
     *             <Index value="1" />
     *             <Complex real="0.0" imaginary="1.0" />
     *        </SparseVector>
     *        <SparseVector dimension="4">
     *             <Index value="2" />
     *             <Complex real="1.0" imaginary="0.0" />
     *        </SparseVector>
     *    </SparseMatrix>[/code]
     */
    protected static final XMLFormat<SparseMatrix> XML_FORMAT = new XMLFormat<SparseMatrix>(
            SparseMatrix.class) {
        
        @Override
        public SparseMatrix newInstance(Class<SparseMatrix> cls, InputElement xml)
                throws XMLStreamException {
            FastTable rows = FastTable.newInstance();
            try {
                while(xml.hasNext()) {
                     rows.add(xml.getNext());
                }
                return SparseMatrix.valueOf(rows);
            } finally {
                FastTable.recycle(rows);
            }
        }

        @Override
        public void read(InputElement xml, SparseMatrix M)
                throws XMLStreamException {
            // Nothing to do (already parsed by newInstance)
        }

        @Override
        public void write(SparseMatrix M, OutputElement xml)
                throws XMLStreamException {
            int nbrRows = M.getNumberOfRows();
            for (int i = 0; i < nbrRows;) {
                xml.add(M.getRow(i++));
            }
        }
    };

    /**
     * Returns a sparse matrix holding the row vectors from the specified
     * collection (column vectors if {@link #transpose transposed}).
     *
     * @param rows the list of row vectors.
     * @return the matrix having the specified rows.
     * @throws DimensionException if the rows do not have the same dimension.
     */
    public static <F extends Field<F>> SparseMatrix<F> valueOf(
            List<? extends VectorImpl<F>> rows) {
        return SparseMatrixImpl.valueOf(rows);
    }

    /**
     * Returns a sparse matrix holding the specified row vectors
     * (column vectors if {@link #transpose transposed}).
     *
     * @param rows the row vectors.
     * @return the matrix having the specified rows.
     * @throws DimensionException if the rows do not have the same dimension.
     */
    public static <F extends Field<F>> SparseMatrix<F> valueOf(VectorImpl<F>... rows) {
        return SparseMatrixImpl.valueOf(rows);
    }

    /**
    * Returns a sparse matrix equivalent to the specified matrix.
     *
     * @param that the matrix to convert to a sparse matrix.
     * @return a sparse matrix with zero elements removed.
     */
    public static <F extends Field<F>> SparseMatrix<F> valueOf(Matrix<F> that) {
        return SparseMatrixImpl.valueOf(that);
    }
 
    /**
     * Returns a sparse matrix equivalent to the specified matrix but with
     * the zero elements removed using the specified object equality comparator
     * (convenience method).
     *
     * @param that the matrix to convert to a sparse matrix.
     * @param zero the zero element for the sparse matrix to return.
     * @param comparator the comparator used to determine zero equality.
     * @return a sparse matrix with zero elements removed.
     */
    public final static <F extends Field<F>> SparseMatrix<F> valueOf(
            Matrix<F> that, F zero, Comparator<? super F> comparator) {
        FastTable<SparseVector<F>> rows = FastTable.newInstance();
        try {
            for (int i=0, n=that.getNumberOfRows(); i < n; i++) {
                rows.add(SparseVector.valueOf(that.getRow(i), zero, comparator));
            }
            return SparseMatrix.valueOf(rows);
        } finally {
            FastTable.recycle(rows);
        }
    }

    /**
     * Default constructor.
     */
    protected SparseMatrix() {
    }

     @Override
    public abstract SparseVector<F> getRow(int i);

    @Override
    public abstract SparseVector<F> getColumn(int j);

    @Override
    public abstract SparseMatrix<F> getSubMatrix(List<Index> rows, List<Index> columns);

    @Override
    public abstract SparseMatrix<F> opposite();

    @Override
    public abstract SparseMatrix<F> times(F k);

    @Override
    public abstract SparseMatrix<F> transpose();

    @Override
    public SparseVector<F> vectorization() {
        return SparseVector.valueOf(super.vectorization());
    }

    @Override
    public abstract SparseMatrix<F> copy();

    private static final long serialVersionUID = 1L;

}