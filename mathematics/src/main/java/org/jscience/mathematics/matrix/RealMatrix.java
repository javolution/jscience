/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import java.util.List;

import javolution.context.ComputeContext;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;

/**
 * <p> A {@link DenseMatrix dense matrix} of {@link Real} numbers.</p>
 *           
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see SparseMatrix
 */
public abstract class RealMatrix extends DenseMatrix<Real> implements ComputeContext.Local {


	@Override
	public abstract RealMatrix getSubMatrix(List<Index> rows, List<Index> columns);

	@Override
	RealMatrix opposite();

	@Override
	RealMatrix plus(Matrix<Real> that);

	@Override
	RealMatrix minus(Matrix<Real> that);

	@Override
	RealMatrix times(Real k);

	@Override
	RealVector times(Vector<Real> v);

	@Override
	RealMatrix times(Matrix<Real> that);

	@Override
	RealMatrix inverse();

	@Override
	RealMatrix divides(Matrix<Real> that);

	@Override
	RealMatrix pseudoInverse();

	@Override
	RealMatrix transpose();

	@Override
	RealMatrix adjoint();

	@Override
	RealVector solve(Vector<Real> y);

	@Override
	RealMatrix solve(Matrix<Real> y);

	@Override
	RealMatrix pow(int exp);

	@Override
	RealMatrix tensor(Matrix<Real> that);

	@Override
	RealVector vectorization();

}
