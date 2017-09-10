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
import java.util.Collection;
import java.util.List;

import javolution.lang.Constant;
import javolution.lang.MathLib;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.util.ConstantTable;
import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.util.Index;
import javolution.util.function.Orders;
import javolution.util.function.Equality;
import javolution.util.function.Composites;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.internal.matrix.DenseMatrixImpl;
import org.jscience.mathematics.internal.matrix.SparseMatrixImpl;
import org.jscience.mathematics.internal.matrix.SparseVectorImpl;
import org.jscience.mathematics.matrix.decomposition.LowerUpper;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A {@link Matrix matrix} with most elements equal to zero.</p>
 *     
  * <p> This abstract class minimizes the effort required to implement the
 *     {@link Matrix matrix} interface for dense matrices.<p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 */
@Constant
@DefaultTextFormat(SparseMatrix.Text.class)
@DefaultXMLFormat(SparseMatrix.XML.class)
public abstract class SparseMatrix<F extends Field<F>> implements Matrix<F>,
		Serializable {

	/**
	 * Defines the default text format for sparse matrices (list of rows). 
	 */
	public static class Text extends TextFormat<SparseMatrix<?>> {

		@Override
		public Appendable format(SparseMatrix<?> that, final Appendable dest)
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
		public SparseMatrix<?> parse(CharSequence csq, Cursor cursor)
				throws IllegalArgumentException {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Defines the default XML representation for sparse matrices. For example:
	 * [code]
	 * <SparseMatrix>
	 *     <SparseVector dimension="3">...</SparseVector>
	 *     <SparseVector dimension="3">...</SparseVector>
	 * </SparseMatrix>[/code]
	 */
	public static class XML extends XMLFormat<SparseMatrix<?>> {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public SparseMatrix<?> newInstance(
				Class<? extends SparseMatrix<?>> cls, InputElement xml)
				throws XMLStreamException {
			List<SparseVector> rows = new FastTable<SparseVector>();
			while (xml.hasNext()) {
				rows.add(xml.get("SparseVector", SparseVector.class));
			}
			return SparseMatrix.of((List) rows);
		}

		@Override
		public void read(InputElement xml, SparseMatrix<?> v)
				throws XMLStreamException {
			// Do nothing, vector already read.
		}

		@Override
		public void write(SparseMatrix<?> that, OutputElement xml)
				throws XMLStreamException {
			for (int i = 0, m = that.numberOfRows(); i < m; i++) {
				xml.add(that.row(i), "SparseVector", SparseVector.class);
			}
		}
	}

	/** Vector view over the matrix columns. */
	private class Column extends SparseVector<F> {
		private static final long serialVersionUID = 0x500L; // Version.
		private final int j;

		Column(int j) {
			this.j = j;
		}

		@Override
		public F get(int i) {
			return SparseMatrix.this.get(i, j);
		}

		@Override
		public int dimension() {
			return SparseMatrix.this.numberOfRows();
		}

		@Override
		public Collection<Index> getIndices() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public F getZero() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	/** Transpose view over this matrix. */
	private class Transpose extends SparseMatrix<F> {
		private static final long serialVersionUID = 0x500L; // Version.

		@Override
		public F get(int i, int j) {
			return SparseMatrix.this.get(j, i);
		}

		@Override
		public int numberOfColumns() {
			return SparseMatrix.this.numberOfRows();
		}

		@Override
		public int numberOfRows() {
			return SparseMatrix.this.numberOfColumns();
		}

		@Override
		public SparseVector<F> column(int j) {
			return SparseMatrix.this.row(j);
		}

		@Override
		public SparseVector<F> diagonal() {
			return SparseMatrix.this.diagonal();
		}

		@Override
		public SparseVector<F> row(int i) {
			return SparseMatrix.this.column(i);
		}

	}

	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * Returns a sparse matrix having the specified sparse vectors as rows.
	 *
	 * @throws DimensionException if the matrix's rows don't have all the 
	 *         same dimensions.
	 */
	public static <F extends Field<F>> SparseMatrix<F> of(
			SparseVector<F>... rows) {
		int m = rows.length;
		int n = rows[0].dimension();
		for (int i = 1; i < m; i++)
			if (rows[i].dimension() != n)
				throw new DimensionException(
						"All rows must have the same length.");
		return new SparseMatrixImpl<F>(rows);
	}

	/**
	 * Returns a sparse matrix holding the rows from the specified collection
	 * (convenience method).
	 * 
	 * @throws DimensionException if the matrix's rows don't have all the 
	 *         same dimensions.
	 */
	@SuppressWarnings("unchecked")
	public static <F extends Field<F>> SparseMatrix<F> of(
			List<? extends SparseVector<F>> rows) {
		return SparseMatrix.of(rows.toArray(new SparseVector[rows.size()]));
	}

	/**
	 * Converts (if required) the specified matrix to a sparse matrix 
	 * (convenience method).
	 */
	public static <F extends Field<F>> SparseMatrix<F> of(Matrix<F> that) {
		if (that instanceof SparseMatrix)
			return (SparseMatrix<F>) that;
		int m = that.numberOfRows();
		@SuppressWarnings("unchecked")
		SparseVector<F>[] rows = (SparseVector<F>[]) new SparseVector[m];
		for (int i = 0; i < m; i++)
			rows[i] = SparseVector.of(that.row(i));
		return new SparseMatrixImpl<F>(rows);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < numberOfRows(); i++)
			hash += 31 * row(i).hashCode();
		return hash;
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
	public boolean equals(Matrix<F> that, Equality<? super F> equal) {
		final int m = numberOfRows();
		if (that.numberOfRows() != m)
			return false;
		for (int i = 0; i < m; i++)
			if (this.row(i).equals(that.row(i), equal))
				return false;
		return true;
	}

	@Override
	public SparseMatrix<F> adjoint() {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		final F zero = this.row(0).getZero();
		@SuppressWarnings("unchecked")
		SparseVector<F>[] rows = (SparseVector<F>[]) new SparseVector[m];
		for (int i = 0; i < m; i++) {
			FastMap<Index, F> mapping = new FastMap<Index, F>();
			for (int j = 0; j < n; j++) {
				F cofactor = this.cofactor(i, j);
				if (!zero.equals(cofactor)) // Different from zero.
					mapping.put(Index.of(j), ((i + j) % 2 == 0) ? cofactor
							: cofactor.opposite());
			}
			rows[i] = SparseVector.of(n, mapping, zero);
		}
		return new SparseMatrixImpl<F>(rows).transpose();
	}

	@Override
	public F cofactor(int i, int j) {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		if ((m == 1) || (n == 1))
			throw new DimensionException(
					"Both row and column dimension should "
							+ "be greater than one");
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m - 1][n - 1];
		for (int ii = 0, iii = 0; ii < m; ii++) {
			if (ii == i)
				continue;
			F[] row = elements[iii++];
			for (int jj = 0, jjj = 0; jj < n; jj++) {
				if (jj == j)
					continue;
				row[jjj++] = get(ii, jj);
			}
		}
		return new DenseMatrixImpl<F>(elements).determinant();
	}

	@Override
	public F determinant() {
		return lowerUpper().determinant();
	}

	@Override
	public SparseMatrix<F> divides(Matrix<F> that) {
		return this.times(that.inverse());
	}

	@Override
	public abstract SparseVector<F> column(int j);

	@Override
	public abstract SparseVector<F> diagonal();

	@Override
	public abstract SparseVector<F> row(int i);

	@Override
	public SparseMatrix<F> getSubMatrix(List<Index> rows, List<Index> columns) {
		final int m = rows.size();
		final int n = columns.size();
		if ((m == 0) || (n == 0))
			throw new DimensionException("Zero dimension not permitted.");
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m][n];
		for (int i = 0; i < m; i++) {
			F[] row = elements[i];
			int rowi = rows.get(i).intValue();
			for (int j = 0; j < n; j++)
				row[j] = get(rowi, columns.get(j).intValue());
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public SparseMatrix<F> inverse() {
		return lowerUpper().inverse();
	}

	@Override
	public boolean isSquare() {
		return this.numberOfRows() == this.numberOfColumns();
	}

	@Override
	public SparseMatrix<F> minus(Matrix<F> that) {
		return this.plus(that.opposite());
	}

	@Override
	public SparseMatrix<F> opposite() {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m][n];
		for (int i = 0; i < m; i++) {
			F[] row = elements[i];
			for (int j = 0; j < n; j++)
				row[j] = get(i, j).opposite();
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public SparseMatrix<F> plus(Matrix<F> that) {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m][n];
		for (int i = 0; i < m; i++) {
			F[] row = elements[i];
			for (int j = 0; j < n; j++)
				row[j] = get(i, j).plus(that.get(i, j));
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public SparseMatrix<F> pow(int exp) {
		if (exp > 0) {
			SparseMatrix<F> pow2 = this;
			SparseMatrix<F> result = null;
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
	public SparseMatrix<F> pseudoInverse() {
		if (isSquare())
			return this.inverse();
		SparseMatrix<F> thisTranspose = this.transpose();
		return (thisTranspose.times(this)).inverse().times(thisTranspose);
	}

	@Override
	public SparseMatrix<F> solve(Matrix<F> Y) {
		return lowerUpper().solve(Y);
	}

	@Override
	public DenseVector<F> solve(Vector<F> y) {
		return lowerUpper().solve(y.column()).row(0);
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
		F[][] elements = (F[][]) new Field[m * p][n * q];
		for (int i0 = 0; i0 < m; i0++) {
			for (int i1 = 0; i1 < p; i1++) {
				F[] row = elements[i0 * m + i1];
				for (int j0 = 0; j0 < n; j0++) {
					for (int j1 = 0; j1 < q; j1++) {
						row[j0 * n + j1] = this.get(i0, j0).times(
								that.get(i1, j1));
					}
				}
			}
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public SparseMatrix<F> times(Matrix<F> that) {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		final int q = that.numberOfColumns();
		if (n != that.numberOfRows())
			throw new DimensionException();
		//  This is a m-by-n matrix and that is a n-by-q matrix, the matrix result is mxq
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m][q];
		for (int i = 0; i < m; i++) {
			F[] row = elements[i];
			for (int j = 0; j < q; j++)
				row[j] = this.row(i).times(that.column(j));
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public DenseVector<F> times(Vector<F> v) {
		return this.times(v.column()).column(0);
	}

	@Override
	public SparseMatrix<F> times(F k) {
		final int m = this.numberOfRows();
		final int n = this.numberOfColumns();
		@SuppressWarnings("unchecked")
		F[][] elements = (F[][]) new Field[m][n];
		for (int i = 0; i < m; i++) {
			F[] row = elements[i];
			for (int j = 0; j < n; j++)
				row[j] = get(i, j).times(k);
		}
		return new DenseMatrixImpl<F>(elements);
	}

	@Override
	public LowerUpper<F> lowerUpper() {
		return null; // TODO.
	}

	@Override
	public String toString() {
		return TextContext.getFormat(SparseMatrix.class).format(this);
	}

	@Override
	public F trace() {
		F sum = this.get(0, 0);
		for (int i = MathLib.min(numberOfColumns(), numberOfRows()); --i > 0;)
			sum = sum.plus(get(i, i));
		return sum;
	}

	@Override
	public SparseMatrix<F> transpose() {
		return new Transpose();
	}

}
