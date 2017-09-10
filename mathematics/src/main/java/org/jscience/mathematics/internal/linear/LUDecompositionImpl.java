/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.linear;

import java.util.Comparator;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.DenseMatrix;
import org.jscience.mathematics.vector.DimensionException;
import org.jscience.mathematics.number.Number;

import javolution.context.LocalContext;
import javolution.context.ObjectFactory;
import javolution.util.FastTable;
import javolution.util.Index;

/**
 * <p> This class represents the decomposition of a {@link Matrix matrix} 
 *     <code>A</code> into a product of a {@link #getLower lower} 
 *     and {@link #getUpper upper} triangular matrices, <code>L</code>
 *     and <code>U</code> respectively, such as <code>A = P·L·U<code> with 
 *     <code>P<code> a {@link #getPermutation permutation} matrix.</p>
 *     
 * <p> This decomposition</a> is typically used to resolve linear systems
 *     of equations (Gaussian elimination) or to calculate the determinant
 *     of a square {@link Matrix} (<code>O(m³)</code>).</p>
 *     
 * <p> Numerical stability is guaranteed through pivoting if the
 *     {@link Field} elements are {@link Number numbers}
 *     For others elements types, numerical stability can be ensured by setting
 *     the {@link javolution.context.LocalContext context-local} pivot 
 *     comparator (see {@link #setPivotComparator}).</p>
 *     
 * <p> Pivoting can be disabled by setting the {@link #setPivotComparator 
 *     pivot comparator} to <code>null</code> ({@link #getPermutation P} 
 *     is then the matrix identity).</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.3, January 2, 2007
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">
 *      Wikipedia: LU decomposition</a>
 */
public final class LUDecompositionImpl<F extends Field<F>>  {

    /**
     * Holds the default comparator for pivoting.
     */
    public static final Comparator<Field<?>> NUMERIC_COMPARATOR = new Comparator<Field<?>>() {

        @SuppressWarnings("unchecked")
        public int compare(Field left, Field right) {
            if ((left instanceof Number) && (right instanceof Number))
                return ((Number) left).isLargerThan((Number) right) ? 1 : -1;
            if (left.equals(left.plus(left))) // Zero
                return -1;
            if (right.equals(right.plus(right))) // Zero
                return 1;
            return 0;
        }
    };

    /**
     * Holds the local comparator.
     */
    private static final LocalContext.Reference<Comparator<Field<?>>> 
       PIVOT_COMPARATOR = new LocalContext.Reference<Comparator<Field<?>>>(
            NUMERIC_COMPARATOR);

   /**
     * Holds the object factory.
     */
    static final ObjectFactory<LUDecompositionImpl> FACTORY = new ObjectFactory<LUDecompositionImpl>() {
        protected LUDecompositionImpl create() {
            return new LUDecompositionImpl();
        }

        @Override
        protected void cleanup(LUDecompositionImpl lu) {
            lu._LU = null;
        }
    };

    /**
     * Holds the dimension of the square matrix source.
     */
    private int _n;

    /**
     * Holds the pivots indexes.
     */
    private final FastTable<Index> _pivots = new FastTable<Index>();

    /**
     * Holds the LU elements.
     */
    private DenseMatrixImpl<F> _LU;

    /**
     * Holds the number of permutation performed.
     */
    private int _permutationCount;

    /**
     * Default constructor.
     */
    private LUDecompositionImpl() {
    }

    /**
     * Returns the lower/upper decomposition of the specified matrix.
     *
     * @param  source the matrix for which the decomposition is calculated.
     * @return the lower/upper decomposition of the specified matrix.
     * @throws DimensionException if the specified matrix is not square.
     */
    public static <F extends Field<F>> LUDecompositionImpl<F> valueOf(
            Matrix<F> source) {
        if (!source.isSquare())
            throw new DimensionException("Matrix is not square");
        int dimension = source.getNumberOfRows();
        LUDecompositionImpl lu = FACTORY.object();
        lu._n = dimension;
        lu._permutationCount = 0;
        lu.construct(source);
        return lu;
    }

    /**
     * Constructs the LU decomposition of the specified matrix.
     * We make the choise of Lii = ONE (diagonal elements of the
     * lower triangular matrix are multiplicative identities).
     *
     * @param  source the matrix to decompose.
     * @throws MatrixException if the matrix source is not square.
     */
    private void construct(Matrix<F> source) {
        _LU = source instanceof DenseMatrixImpl ? ((DenseMatrixImpl<F>) source).copy()
                : DenseMatrixImpl.valueOf(source);
        _pivots.clear();
        for (int i = 0; i < _n; i++) {
            _pivots.add(Index.valueOf(i));
        }

        // Main loop.
        Comparator<Field<?>> cmp = LUDecompositionImpl.getPivotComparator();
        final int n = _n;
        for (int k = 0; k < _n; k++) {

            if (cmp != null) { // Pivoting enabled.
                // Rearranges the rows so that the absolutely largest
                // elements of the matrix source in each column lies
                // in the diagonal.
                int pivot = k;
                for (int i = k + 1; i < n; i++) {
                    if (cmp.compare(_LU.get(i, k), _LU.get(pivot, k)) > 0) {
                        pivot = i;
                    }
                }
                if (pivot != k) { // Exchanges.
                    for (int j = 0; j < n; j++) {
                        F tmp = _LU.get(pivot, j);
                        _LU.set(pivot, j, _LU.get(k, j));
                        _LU.set(k, j, tmp);
                    }
                    int j = _pivots.get(pivot).intValue();
                    _pivots.set(pivot, _pivots.get(k));
                    _pivots.set(k, Index.valueOf(j));
                    _permutationCount++;
                }
            }

            // Computes multipliers and eliminate k-th column.
            F lukkInv = _LU.get(k, k).reciprocal();
            for (int i = k + 1; i < n; i++) {
                // Multiplicative order is important
                // for non-commutative elements.
                _LU.set(i, k, _LU.get(i, k).times(lukkInv));
                for (int j = k + 1; j < n; j++) {
                    _LU.set(i, j, _LU.get(i, j).plus(
                            _LU.get(i, k).times(_LU.get(k, j).opposite())));
                }
            }
        }
    }

    /**
     * Sets the {@link javolution.context.LocalContext local} comparator used 
     * for pivoting or <code>null</code> to disable pivoting.
     *
     * @param  cmp the comparator for pivoting or <code>null</code>.
     */
    public static void setPivotComparator(Comparator<Field<?>> cmp) {
        PIVOT_COMPARATOR.set(cmp);
    }

    /**
     * Returns the {@link javolution.context.LocalContext local} 
     * comparator used for pivoting or <code>null</code> if pivoting 
     * is not performed (default {@link #NUMERIC_COMPARATOR}).
     *
     * @return the comparator for pivoting or <code>null</code>.
     */
    public static Comparator<Field<?>> getPivotComparator() {
        return PIVOT_COMPARATOR.get();
    }

    /**
     * Returns the solution X of the equation: A * X = B  with
     * <code>this = A.lu()</code> using back and forward substitutions.
     *
     * @param  B the input matrix.
     * @return the solution X = (1 / A) * B.
     * @throws DimensionException if the dimensions do not match.
     */
    public DenseMatrix<F> solve(Matrix<F> B) {
        if (_n != B.getNumberOfRows())
            throw new DimensionException("Input vector has "
                    + B.getNumberOfRows() + " rows instead of " + _n);

        // Copies B with pivoting.
        final int n = B.getNumberOfColumns();
        DenseMatrixImpl<F> X = createNullDenseMatrix(_n, n);
        for (int i = 0; i < _n; i++) {
            for (int j = 0; j < n; j++) {
                X.set(i, j, B.get(_pivots.get(i).intValue(), j));
            }
        }

        // Solves L * Y = pivot(B)
        for (int k = 0; k < _n; k++) {
            for (int i = k + 1; i < _n; i++) {
                F luik = _LU.get(i, k);
                for (int j = 0; j < n; j++) {
                    X.set(i, j, X.get(i, j).plus(
                            luik.times(X.get(k, j).opposite())));
                }
            }
        }

        // Solves U * X = Y;
        for (int k = _n - 1; k >= 0; k--) {
            for (int j = 0; j < n; j++) {
                X.set(k, j, (_LU.get(k, k).reciprocal()).times(X.get(k, j)));
            }
            for (int i = 0; i < k; i++) {
                F luik = _LU.get(i, k);
                for (int j = 0; j < n; j++) {
                    X.set(i, j, X.get(i, j).plus(
                            luik.times(X.get(k, j).opposite())));
                }
            }
        }
        return X;
    }

    private DenseMatrixImpl<F> createNullDenseMatrix(int m, int n) {
        DenseMatrixImpl<F> M = DenseMatrixImpl.FACTORY.object();
        for (int i = 0; i < m; i++) {
            DenseVectorImpl<F> V = DenseVectorImpl.FACTORY.object();
            for (int j = 0; j < n; j++) {
                V._elements.add(null);
            }
            M._rows.add(V);
        }
        return M;
    }

    /**
     * Returns the solution X of the equation: A * X = Identity  with
     * <code>this = A.lu()</code> using back and forward substitutions.
     *
     * @return <code>this.solve(Identity)</code>
     */
    public DenseMatrix<F> inverse() {
        // Calculates inv(U).
        final int n = _n;
        DenseMatrixImpl<F> R = createNullDenseMatrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                R.set(i, j, _LU.get(i, j));
            }
        }
        for (int j = n - 1; j >= 0; j--) {
            R.set(j, j, R.get(j, j).reciprocal());
            for (int i = j - 1; i >= 0; i--) {
                F sum = R.get(i, j).times(R.get(j, j).opposite());
                for (int k = j - 1; k > i; k--) {
                    sum = sum.plus(R.get(i, k).times(R.get(k, j).opposite()));
                }
                R.set(i, j, (R.get(i, i).reciprocal()).times(sum));
            }
        }
        // Solves inv(A) * L = inv(U)
        for (int i = 0; i < n; i++) {
            for (int j = n - 2; j >= 0; j--) {
                for (int k = j + 1; k < n; k++) {
                    F lukj = _LU.get(k, j);
                    if (R.get(i, j) != null) {
                        R.set(i, j, R.get(i, j).plus(
                                R.get(i, k).times(lukj.opposite())));
                    } else {
                        R.set(i, j, R.get(i, k).times(lukj.opposite()));
                    }
                }
            }
        }
        // Swaps columns (reverses pivots permutations).
        FastTable<F> tmp = FastTable.newInstance();
        for (int i = 0; i < n; i++) {
            tmp.reset();
            for (int j = 0; j < n; j++) {
                tmp.add(R.get(i, j));
            }
            for (int j = 0; j < n; j++) {
                R.set(i, _pivots.get(j).intValue(), tmp.get(j));
            }
        }
        FastTable.recycle(tmp);
        return R;
    }

    /**
     * Returns the determinant of the {@link Matrix} having this
     * decomposition.
     *
     * @return the determinant of the matrix source.
     */
    public F determinant() {
        F product = _LU.get(0, 0);
        for (int i = 1; i < _n; i++) {
            product = product.times(_LU.get(i, i));
        }
        return ((_permutationCount & 1) == 0) ? product : product.opposite();
    }

    /**
     * Returns the lower matrix decomposition (<code>L</code>) with diagonal
     * elements equal to the multiplicative identity for F. 
     *
     * @param zero the additive identity for F.
     * @param one the multiplicative identity for F.
     * @return the lower matrix.
     */
    public DenseMatrix<F> getLower(F zero, F one) {
        DenseMatrixImpl<F> L = _LU.copy();
        for (int j = 0; j < _n; j++) {
            for (int i = 0; i < j; i++) {
                L.set(i, j, zero);
            }
            L.set(j, j, one);
        }
        return L;
    }

    /**
     * Returns the upper matrix decomposition (<code>U</code>). 
     *
     * @param zero the additive identity for F.
     * @return the upper matrix.
     */
    public DenseMatrix<F> getUpper(F zero) {
        DenseMatrixImpl<F> U = _LU.copy();
        for (int j = 0; j < _n; j++) {
            for (int i = j + 1; i < _n; i++) {
                U.set(i, j, zero);
            }
        }
        return U;
    }

    /**
     * Returns the permutation matrix (<code>P</code>). 
     *
     * @param zero the additive identity for F.
     * @param one the multiplicative identity for F.
     * @return the permutation matrix.
     */
    public SparseMatrix<F> getPermutation(F zero, F one) {
        SparseMatrixImpl<F> P = SparseMatrixImpl.FACTORY.object();
        for (int i=0; i < _n; i++) {
            SparseVectorImpl<F> V = SparseVectorImpl.FACTORY.object();
            V._dimension = _n;
            V._zero = zero;
            P._rows.add(V);
        }
        // Sets elements.
        for (int i = 0; i < _n; i++) {
            P.getRow(_pivots.get(i).intValue())._elements.put(Index.valueOf(i),
                    one);
        }
        return P;
    }

    /**
     * Returns the lower/upper decomposition in one single matrix. 
     *
     * @return the lower/upper matrix merged in a single matrix.
     */
    public DenseMatrix<F> getLU() {
        return _LU;
    }

    /**
     * Returns the pivots elements of this decomposition. 
     *
     * @return the row indices after permutation.
     */
    public FastTable<Index> getPivots() {
        return _pivots;
    }

}