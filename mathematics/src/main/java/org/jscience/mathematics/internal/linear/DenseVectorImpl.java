/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.linear;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javolution.text.TextContext;
import javolution.util.FastTable;
import javolution.util.Index;
import javolution.util.function.Orders;

import org.jscience.mathematics.matrix.DenseMatrix;
import org.jscience.mathematics.matrix.DenseVector;
import org.jscience.mathematics.matrix.DimensionException;
import org.jscience.mathematics.matrix.Vector;
import org.jscience.mathematics.structure.Field;

/**
 * <p> This class holds the dense vector default implementation.</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 */
public class DenseVectorImpl<F extends Field<F>> extends DenseVector<F> {

	private static final long serialVersionUID = 0x500L;
	private final F[] elements;
	
	public DenseVectorImpl(F[] elements) {
		this.elements = elements.clone();
	}
	
	public DenseVectorImpl(F[] elements) {
		this.elements = elements.clone();
	}
	
	@SuppressWarnings("unchecked")
	public DenseVectorImpl(Vector<F> that) {
		final int n = that.dimension();
		elements = (F[]) new Object[n];
		for (int i = 0; i < n; i++) {
			elements[i] = that.get(i);
		}
	}

	@Override
	public F get(int i) {
		return elements[i];
	}

	@Override
	public FastTable<F> getData() {
		return new FastTable<F>(elements);
	}

	@Override
	public int dimension() {
		return elements.length;
	}

	@Override
	public DenseVectorImpl<F> getSubVector(List<Index> indices) {
		@SuppressWarnings("unchecked")
		F[] subVector = (F[]) new Object[indices.size()];
		int i = 0;
		for (Index index : indices) {
			subVector[i++] = elements[index.intValue()];
		}
		return new DenseVectorImpl<F>(subVector);
	}

	@Override
	public int hashCode() {
		return getData().hashCode();
	}

	@Override
	public DenseVector<F> minus(Vector<F> that) {
		return this.plus(that.opposite());
	}

	@Override
	public DenseVectorImpl<F> opposite() {
		final int n = elements.length;
		@SuppressWarnings("unchecked")
		F[] result = (F[]) new Object[n];
		for (int i = 0; i < n; i++) {
			result[i] = elements[i].opposite();
		}
		return new DenseVectorImpl<F>(result);
	}

	@Override
	public DenseVectorImpl<F> plus(Vector<F> that) {
		final int n = that.dimension();
		if (n != elements.length) throw new DimensionException();
		@SuppressWarnings("unchecked")
		F[] result = (F[]) new Object[n];
		for (int i = 0; i < n; i++) {
			result[i] = elements[i].plus(that.get(i));
		}
		return new DenseVectorImpl<F>(result);
	}

	@Override
	public DenseMatrixImpl<F> tensor(Vector<F> that) {
		int m = this.dimension();
		int n = that.dimension();
		@SuppressWarnings("unchecked")
		F[][] result = (F[][])new Object[m][n];
		for (int j=0; j < n; j++) {
			F ej = that.get(j);
		    for (int i=0; i < m; i++) {
				result[i][j] = elements[i].times(ej);
			}
		}
		return new DenseMatrixImpl<F>(result);
	}

	@Override
	public DenseVectorImpl<F> times(F k) {
		final int n = elements.length;
		@SuppressWarnings("unchecked")
		F[] result = (F[]) new Object[n];
		for (int i = 0; i < n; i++) {
			result[i] = elements[i].times(k);
		}
		return new DenseVectorImpl<F>(result);
	}

	@Override
	public F times(Vector<F> that) {
		final int n = that.dimension();
		if (n != elements.length) throw new DimensionException();
		F sum = elements[0].times(that.get(0));
		for (int i = 1; i < n; i++) {
			sum = sum.plus(elements[i].times(that.get(i)));
		}
		return sum;
	}

	@Override
	public String toString() {
		return TextContext.getFormat(DenseVector.class).format(this);
	}

}
