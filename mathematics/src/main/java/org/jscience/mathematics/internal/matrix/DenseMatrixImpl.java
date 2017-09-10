/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.matrix;

import org.jscience.mathematics.matrix.DenseMatrix;
import org.jscience.mathematics.structure.Field;

/**
 * Dense matrix default implementation.
 */
public class DenseMatrixImpl<F extends Field<F>> extends DenseMatrix<F> {

	private static final long serialVersionUID = 0x500L;
	private final int m, n;
	private final F[] elements;  
	
	public DenseMatrixImpl(int m, int n, F[] elements) {
		this.m = m;
		this.n = n;
		this.elements = elements;
	}

	@Override
	public final F get(int i, int j) {
		if ((j < 0) || (j >= n)) throw new IndexOutOfBoundsException();
		return elements[i * n + j];
	}

	@Override
	public final int numberOfRows() {
		return n;
	}

	@Override
	public final int numberOfColumns() {
		return m;
	}

}
