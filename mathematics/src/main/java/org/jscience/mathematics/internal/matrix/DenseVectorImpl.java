/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.matrix;

import org.jscience.mathematics.matrix.DenseVector;
import org.jscience.mathematics.structure.Field;

/**
 * Dense vector default implementation.
 */
public class DenseVectorImpl<F extends Field<F>> extends DenseVector<F> {

	private static final long serialVersionUID = 0x500L;
	private final F[] data;  
	
	public DenseVectorImpl(F[] elements) {
		this.data = elements;
	}

	@Override
	public final F get(int i) {
		return data[i];
	}

	@Override
	public final int dimension() {
		return data.length;
	}
}
