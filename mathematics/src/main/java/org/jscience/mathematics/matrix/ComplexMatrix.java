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

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.ComplexField;
import org.jscience.mathematics.number.Real;

/**
 * <p> A {@link DenseMatrix dense matrix} of {@link Complex complex} numbers.</p>
 *           
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see SparseMatrix
 */
public abstract class ComplexMatrix extends DenseMatrix<Complex> implements
		ComputeContext.Local {

	@Override
	ComplexVector row(int i);

	@Override
	ComplexVector column(int j);

	@Override
	ComplexVector diagonal();

	@Override
	ComplexMatrix getSubMatrix(List<Index> rows, List<Index> columns);

	@Override
	ComplexMatrix opposite();

	@Override
	ComplexMatrix plus(Matrix<ComplexField<Real>> that);

	@Override
	ComplexMatrix minus(Matrix<ComplexField<Real>> that);

	@Override
	ComplexMatrix times(ComplexField<Real> k);

	@Override
	ComplexVector times(Vector<ComplexField<Real>> v);

	@Override
	ComplexMatrix times(Matrix<ComplexField<Real>> that);

	@Override
	ComplexMatrix inverse();

	@Override
	ComplexMatrix divides(Matrix<ComplexField<Real>> that);

	@Override
	ComplexMatrix pseudoInverse();

	@Override
	ComplexMatrix transpose();

	@Override
	ComplexMatrix adjoint();

	@Override
	ComplexVector solve(Vector<ComplexField<Real>> y);

	@Override
	ComplexMatrix solve(Matrix<ComplexField<Real>> y);

	@Override
	ComplexMatrix pow(int exp);

	@Override
	ComplexMatrix tensor(Matrix<ComplexField<Real>> that);

	@Override
	ComplexVector vectorization();

}
