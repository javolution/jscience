/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.linear;

import static javolution.lang.Realtime.Limit.LINEAR;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javolution.lang.MathLib;
import javolution.lang.Realtime;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.util.FastTable;
import javolution.util.Index;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.linear.DenseVector;
import org.jscience.mathematics.linear.Matrix;
import org.jscience.mathematics.linear.DenseMatrix;
import org.jscience.mathematics.linear.DimensionException;
import org.jscience.mathematics.linear.Vector;

/**
 * <p> This class holds the dense matrix default implementation.</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2007
 */
public abstract MatrixImpl<F extends Field<F>> implements DenseMatrix<F> {

    public final F[][] elements;
    
    public MatrixImpl(F[][] elements) {
    	this.elements = elements;
    }

    @Override
    public int getRowDimension() {
        return elements.length;
    }

    @Override
    public int getColumnDimension() {
        return elements[0].length;
    }

    @Override
    public F get(int i, int j) {
        return rows.get(i).get(j);
    }

    @Override
    public DenseVectorImpl<F> getRow(int i) {
    	return rows.get(i);
    }

    @Override
    public DenseVectorImpl<F> getColumn(int j) {
    	DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
    	for (int i=0; i < rows.size(); i++) {
    		dv.elements.add(this.get(i, j));
    	}
    	return dv;
    }


	@Override
	public DenseVectorImpl<F> getDiagonal() {
		   final int m = this.getRowDimension();
	        final int n = this.getColumnDimension();
	        final int dimension = MathLib.min(m, n);
	        DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
	        for (int i = 0; i < dimension; i++) {
	            dv.elements.add(this.get(i, i));
	        }
	        return dv;
	}


    @Override
    public MatrixImpl<F> getSubMatrix(int[] rows, int[] columns) {
        MatrixImpl<F> dm = new MatrixImpl<F>();
        for (int i : rows) {
            DenseVectorImpl<F> row = this.getRow(i);
            dm.rows.add(row.getSubVector(columns));
        }
        return dm;
    }

    @Override
    public MatrixImpl<F> opposite() {
        MatrixImpl<F> dm = new MatrixImpl<F>();
        for (int i = 0, m = getRowDimension(); i < m; i++) {
            dm.rows.add(getRow(i).opposite());
        }
        return dm;
    }

    @Override
    public MatrixImpl<F> plus(Matrix<F> that) {
        MatrixImpl<F> dm = new MatrixImpl<F>();
        final int m = this.getRowDimension();
        if (that.getRowDimension() != m)
            throw new DimensionException();
        for (int i = 0; i < m; i++) {
            dm.rows.add(this.getRow(i).plus(that.getRow(i)));
        }
        return dm;
    }

    @Override
    public MatrixImpl<F> times(F k) {
        MatrixImpl<F> dm = new MatrixImpl<F>();
        for (int i = 0, m = rows.size(); i < m; i++) {
            dm.rows.add(this.getRow(i).times(k));
        }
        return dm;
    }

    @Override
    public MatrixImpl<F> times(Matrix<F> that) {
        //  This is a m-by-n matrix and that is a n-by-p matrix, the matrix result is mxp
        final int m = rows.size();
        final int n = rows.get(0).getDimension(); // Number of columns of this.
        final int p = that.getColumnDimension(); // Number of columns of that.
        if (n != that.getRowDimension())
            throw new DimensionException();
        MatrixImpl<F> dm = new MatrixImpl<F>();
        for (int i = 0; i < m; i++) {
            DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
            for (int j = 0; j < p; j++) {
                F element = rows.get(i).times(that.getColumn(j));
                dv.elements.add(element);
            }
            dm.rows.add(dv);
        }
        return dm;
    }

    @Override
    public DenseMatrix<F> transpose() {
        return new Transpose();
    }


    /**
     * Represents a transposed view of the outer matrix.
     */
    private class Transpose extends DenseMatrix<F> {

        @Override
        public F get(int i, int j) {
            return MatrixImpl.this.get(j, i);
        }

        @Override
        public DenseVectorImpl<F> getColumn(int j) {
            return MatrixImpl.this.getRow(j);
        }

        @Override
        public int getColumnDimension() {
            return MatrixImpl.this.getRowDimension();
        }

        @Override
        public DenseVector<F> getRow(int i) {
            return MatrixImpl.this.getColumn(i);
        }

        @Override
        public int getRowDimension() {
            return MatrixImpl.this.getColumnDimension();
        }

        @Override
        public DenseMatrix<F> getSubMatrix(int[] rows, int[] columns) {
            return MatrixImpl.this.getSubMatrix(columns, rows).transpose();
        }

        @Override
        public DenseMatrix<F> opposite() {
            return MatrixImpl.this.opposite().transpose();
        }

        @Override
        public DenseMatrix<F> plus(Matrix<F> that) {
            return MatrixImpl.this.plus(that.transpose()).transpose();
        }

        @Override
        public DenseMatrix<F> times(F k) {
            return MatrixImpl.this.times(k).transpose();
        }

        @Override
        public MatrixImpl<F> times(Matrix<F> that) {
            return MatrixImpl.valueOf(this).times(that);
        }

        @Override
        public MatrixImpl<F> transpose() {
            return MatrixImpl.this;
        }
    }


	@Override
	public F determinant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DenseMatrix<F> inverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DenseMatrix<F> solve(Matrix<F> y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	@Override
	public DenseMatrix<F> adjoint() {
		DenseMatrixImpl<F> dm = new DenseMatrixImpl<F>();
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		for (int i = 0; i < m; i++) {
			DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
			for (int j = 0; j < n; j++) {
				F cofactor = this.cofactor(i, j);
				dv.elements.add(((i + j) % 2 == 0) ? cofactor : cofactor
						.opposite());
			}
			dm.rows.add(dv);
		}
		return dm.transpose();
	};

	@Override
	public F cofactor(int i, int j) {
		int[] rows = new int[this.getRowDimension() - 1];
		int[] columns = new int[this.getColumnDimension() - 1];
		for (int ii = 0, k = 0; ii <= rows.length; ii++) {
			if (ii == i)
				continue; // Don't include row i.
			rows[k++] = ii;
		}
		for (int jj = 0, k = 0; jj <= columns.length; jj++) {
			if (jj == j)
				continue; // Don't include column j.
			columns[k++] = jj;
		}
		return this.getSubMatrix(rows, columns).determinant();
	}

	@Override
	public abstract F determinant();

	@Override
	public DenseMatrix<F> divide(Matrix<F> that) {
		return this.times(that.inverse());
	}

	@Override
	public boolean equals(Matrix<F> that, Comparator<F> cmp) {
		if (this == that)
			return true;
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		if ((that.getRowDimension() != m) || (that.getColumnDimension() != n))
			return false;
		for (int i = m; --i >= 0;) {
			for (int j = n; --j >= 0;) {
				if (cmp.compare(this.get(i, j), that.get(i, j)) != 0)
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Matrix))
			return false;
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		Matrix<?> M = (Matrix<?>) that;
		if ((M.getRowDimension() != m) || (M.getColumnDimension() != n))
			return false;
		for (int i = m; --i >= 0;) {
			for (int j = n; --j >= 0;) {
				if (!this.get(i, j).equals(M.get(i, j)))
					return false;
			}
		}
		return true;
	}

	@Override
	public abstract DenseVector<F> getColumn(int j);

	@Override
	public abstract DenseVector<F> getDiagonal();

	@Override
	public abstract DenseVector<F> getRow(int i);

	@Override
	public abstract DenseMatrix<F> getSubMatrix(int[] rows, int[] columns);

	@Override
	public int hashCode() {
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		int code = 0;
		for (int i = m; --i >= 0;) {
			for (int j = n; --j >= 0;) {
				code += get(i, j).hashCode();
			}
		}
		return code;
	}

	@Override
	public abstract DenseMatrix<F> inverse();

	@Override
	public boolean isSquare() {
		return this.getRowDimension() == this.getColumnDimension();
	}

	@Override
	public DenseMatrix<F> minus(Matrix<F> that) {
		return this.plus(that.opposite());
	}

	@Override
	public abstract DenseMatrix<F> opposite();

	@Override
	public abstract DenseMatrix<F> plus(Matrix<F> that);

	@Override
	public DenseMatrix<F> pow(int exp) {
		if (exp > 0) {
			DenseMatrix<F> pow2 = this;
			DenseMatrix<F> result = null;
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
	public DenseMatrix<F> pseudoInverse() {
		if (isSquare())
			return this.inverse();
		DenseMatrix<F> thisTranspose = this.transpose();
		return (thisTranspose.times(this)).inverse().times(thisTranspose);
	}

	@Override
	public abstract DenseMatrix<F> solve(Matrix<F> y);

	@Override
	public DenseVector<F> solve(Vector<F> y) {
		return solve(y.asColumn()).getColumn(0);
	}

	@Override
	public DenseMatrix<F> tensor(Matrix<F> that) {
		//  If this is a m-by-n matrix and that is a p-by-q matrix,
		// then the Kronecker product is the mp-by-nq block.
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		final int p = that.getRowDimension();
		final int q = that.getColumnDimension();
		DenseMatrixImpl<F> dm = new DenseMatrixImpl<F>();
		for (int i0 = 0; i0 < m; i0++) {
			for (int i1 = 0; i1 < p; i1++) {
				DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
				for (int j0 = 0; j0 < n; j0++) {
					for (int j1 = 0; j1 < q; j1++) {
						F e = this.get(i0, j0).times(that.get(i1, j1));
						dv.elements.add(e);
					}
				}
				dm.rows.add(dv);
			}
		}
		return dm;
	}

	@Override
	public abstract DenseMatrix<F> times(F k);

	@Override
	public abstract DenseMatrix<F> times(Matrix<F> that);

	@Override
	public Vector<F> times(Vector<F> v) {
		return this.times(v.asColumn()).getRow(0);
	}

	/** 
	 * Returns the string representation of this dense matrix using its 
	 * default {@link TextFormat format}.
	 * 
	 * @see TextContext
	 */
	@Override
	@Realtime(limit = LINEAR)
	public String toString() {
		return TextContext.getFormat(DenseMatrix.class).format(this);
	}

	@Override
	public F trace() {
		F sum = this.get(0, 0);
		for (int i = MathLib.min(getColumnDimension(), getRowDimension()); --i > 0;) {
			sum = sum.plus(get(i, i));
		}
		return sum;
	}

	@Override
	public abstract DenseMatrix<F> transpose();

	@Override
	public DenseMatrix<F> value() {
		return this;
	}

	@Override
	public DenseVector<F> vectorization() {
		final int m = this.getRowDimension();
		final int n = this.getColumnDimension();
		DenseVectorImpl<F> dv = new DenseVectorImpl<F>();
		for (int j = 0; j < n; j++) { // For each column.
			for (int i = 0; i < m; i++) {
				dv.elements.add(this.get(i, j));
			}
		}
		return dv;
	}	
}

