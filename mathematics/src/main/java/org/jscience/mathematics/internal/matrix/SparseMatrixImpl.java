/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.matrix;

import org.jscience.mathematics.matrix.SparseMatrix;
import org.jscience.mathematics.matrix.SparseVector;
import org.jscience.mathematics.structure.Field;

/**
 * Sparse matrix default implementation.
 */
public class SparseMatrixImpl<F extends Field<F>> extends SparseMatrix<F> {

	private static final long serialVersionUID = 0x500L;
	private final SparseVector<F>[] rows;  
	
	public SparseMatrixImpl(SparseVector<F>[] rows) {
		this.rows = rows;
	}

	@Override
	public final F get(int i, int j) {
		return rows[i].get(j);
	}

	@Override
	public final int numberOfRows() {
		return rows.length;
	}

	@Override
	public final int numberOfColumns() {
		return rows[0].dimension();
	}

}
