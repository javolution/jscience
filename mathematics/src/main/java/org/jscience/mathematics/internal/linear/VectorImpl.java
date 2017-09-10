/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javolution.lang.Realtime;
import javolution.lang.ValueType;
import javolution.text.Cursor;
import javolution.text.Text;
import javolution.text.TextFormat;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.structure.VectorSpace;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents an immutable element of a vector space.</p>
 *
 * <p> Instances of this class are usually created from static factory methods.
 *     [code]
 *        // Creates a vector of 64 bits floating points numbers.
 *        Vector<Float64> V0 = Vector.valueOf(1.1, 1.2, 1.3);
 *
 *        // Creates a dense vector of rational numbers.
 *        DenseVector<Rational> V1 = DenseVector.valueOf(Rational.valueOf(23, 45), Rational.valueOf(33, 75));
 *
 *        // Creates the sparse vector { 0, 0, 0, 3.3, 0, 0, 0, -3.7 } of decimal numbers.
 *        SparseVector<Decimal> V2 =
 *            SparseVector.valueOf(3, Decimal.valueOf("3.3"), 8).plus(
 *            SparseVector.valueOf(7, Decimal.valueOf("-3.7"), 8));
 *     [/code]

 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 * @see <a href="http://en.wikipedia.org/wiki/Vector_space">
 *      Wikipedia: Vector Space</a>
 */
public abstract class VectorImpl<F extends Field<F>>
        implements VectorSpace<VectorImpl<F>, F>, ValueType, Realtime {

    /**
     * Defines the default text format for vectors (formatting only). This format
     * consists of the vector elements represented as a list
     * (e.g. rational vector "{30/23, 12/7}"). This representation uses
     * the current format associated to the vector's elements.
     * @see TextFormat#getInstance
     */
    protected static final TextFormat<VectorImpl> DEFAULT_VECTOR_FORMAT = new TextFormat<VectorImpl>(VectorImpl.class) {

        @Override
        public Appendable format(VectorImpl V, Appendable out) throws IOException {
            out.append('{');
            for (int i = 0, n = V.getDimension(); i < n;) {
                Field element = V.get(i);
                TextFormat elementFormat = TextFormat.getInstance(element.getClass());
                elementFormat.format(element, out);
                if (++i < n) { // More to append.
                    out.append(", ");
                }
            }
            return out.append('}');
        }

        @Override
        public boolean isParsingSupported() {
            return false;
        }
        
        @Override
        public VectorImpl parse(CharSequence csq, Cursor cursor) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Parsing not supported for generic vector.");
        }
    };

    /**
     * Returns a vector holding the specified <code>double</code> values
     * (convenience method).
     *
     * @param values the vector values.
     * @return the vector having the specified values.
     */
    public static VectorImpl<Real> valueOf(double... values) {
        return Float64Vector.valueOf(values);
    }

    /**
     * Default constructor (for sub-classes).
     */
    protected VectorImpl() {
    }

    /**
     * Returns the number of elements  held by this vector.
     *
     * @return this vector dimension.
     */
    public abstract int getDimension();

    /**
     * Returns a single element from this vector.
     *
     * @param  i the element index (range [0..n[).
     * @return the element at <code>i</code>.
     * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i &gt;= getDimension())</code>
     */
    public abstract F get(int i);

    /**
     * Returns the sub-vector formed by the elements having the specified
     * indices. The indices do not have to be ordered, for example
     * <code>getSubVector(Index.valuesOf(1, 0))</code> returns the subvector
     * holding the first two elements of this vector exchanged.
     *
     * @returnthe corresponding sub-vector.
     * @throws IndexOutOfBoundsException if any of the indices is greater
     *         than this vector dimension.
     */
    public abstract VectorImpl<F> getSubVector(List<Index> indices);

    /**
     * Returns the negation of this vector.
     *
     * @return <code>-this</code>.
     */
    public abstract VectorImpl<F> opposite();

    /**
     * Returns the sum of this vector with the one specified.
     *
     * @param   that the vector to be added.
     * @return  <code>this + that</code>.
     * @throws  DimensionException is vectors dimensions are different.
     */
    public abstract VectorImpl<F> plus(VectorImpl<F> that);

    /**
     * Returns the difference between this vector and the one specified.
     *
     * @param  that the vector to be subtracted.
     * @return <code>this - that</code>.
     */
    public VectorImpl<F> minus(VectorImpl<F> that) {
        return this.plus(that.opposite());
    }

    /**
     * Returns the product of this vector with the specified coefficient.
     *
     * @param  k the coefficient multiplier.
     * @return <code>this · k</code>
     */
    public abstract VectorImpl<F> times(F k);

    /**
     * Returns the dot product of this vector with the one specified.
     *
     * @param  that the vector multiplier.
     * @return <code>this · that</code>
     * @throws DimensionException if <code>this.dimension() != that.dimension()</code>
     * @see <a href="http://en.wikipedia.org/wiki/Dot_product">
     *      Wikipedia: Dot Product</a>
     */
    public abstract F times(VectorImpl<F> that);

    /**
     * Returns the cross product of two 3-dimensional vectors.
     *
     * @param  that the vector multiplier.
     * @return <code>this x that</code>
     * @throws DimensionException if 
     *         <code>(this.getDimension() != 3) && (that.getDimension() != 3)</code> 
     */
    public VectorImpl<F> cross(VectorImpl<F> that) {
        if ((this.getDimension() != 3) || (that.getDimension() != 3))
            throw new DimensionException(
                    "The cross product of two vectors requires " + "3-dimensional vectors");
        DenseVectorImpl V = DenseVectorImpl.FACTORY.object();
        V._elements.add((this.get(1).times(that.get(2))).plus((this.get(2).times(that.get(1))).opposite()));
        V._elements.add((this.get(2).times(that.get(0))).plus((this.get(0).times(that.get(2))).opposite()));
        V._elements.add((this.get(0).times(that.get(1))).plus((this.get(1).times(that.get(0))).opposite()));
        return V;
    }

    /**
     * Returns the textual representation of this vector.
     * This method cannot be overriden, sub-classes should define their own
     * textual format which will automatically be used here.
     *
     * @return <code>TextFormat.getInstance(this.getClass()).format(this)</code>
     * @see #DEFAULT_VECTOR_FORMAT
     */
    public final Text toText() {
        TextFormat<VectorImpl> textFormat = TextFormat.getInstance(this.getClass());
        return textFormat.format(this);
    }

    /**
     * Returns the text representation of this vector as a 
     * <code>java.lang.String</code>.
     * This method cannot be overriden, sub-classes should define their own
     * textual format which will automatically be used here.
     *
     * @return <code>TextFormat.getInstance(this.getClass()).formatToString(this)</code>
     * @see #DEFAULT_VECTOR_FORMAT
     */
    @Override
    public final String toString() {
        TextFormat<VectorImpl> textFormat = TextFormat.getInstance(this.getClass());
        return textFormat.formatToString(this);
    }

    /**
     * Indicates if this vector can be considered equals to the one 
     * specified using the specified comparator when testing for 
     * element equality. The specified comparator may allow for some 
     * tolerance in the difference between the vector elements.
     *
     * @param  that the vector to compare for equality.
     * @param  cmp the comparator to use when testing for element equality.
     * @return <code>true</code> if this vector and the specified matrix are
     *         both vector with equal elements according to the specified
     *         comparator; <code>false</code> otherwise.
     */
    public boolean equals(VectorImpl<F> that, Comparator<F> cmp) {
        if (this == that)
            return true;
        final int dimension = this.getDimension();
        if (that.getDimension() != dimension)
            return false;
        for (int i = dimension; --i >= 0;) {
            if (cmp.compare(this.get(i), that.get(i)) != 0)
                return false;
        }
        return true;
    }

    /**
     * Indicates if this vector is equal to the object specified.
     *
     * @param  that the object to compare for equality.
     * @return <code>true</code> if this vector and the specified object are
     *         both vectors with equal elements; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof VectorImpl))
            return false;
        final int dimension = this.getDimension();
        VectorImpl<?> v = (VectorImpl<?>) that;
        if (v.getDimension() != dimension)
            return false;
        for (int i = dimension; --i >= 0;) {
            if (!this.get(i).equals(v.get(i)))
                return false;
        }
        return true;
    }

    /**
     * Returns a hash code value for this vector.
     * Equals objects have equal hash codes.
     *
     * @return this vector hash code value.
     * @see    #equals
     */
    @Override
    public int hashCode() {
        final int dimension = this.getDimension();
        int code = 0;
        for (int i = dimension; --i >= 0;) {
            code += get(i).hashCode();
        }
        return code;
    }

    /**
     * Returns a copy of this vector 
     * {@link javolution.context.AllocatorContext allocated} 
     * by the calling thread (possibly on the stack).
     *     
     * @return an identical and independant copy of this matrix.
     */
    public abstract VectorImpl<F> copy();
}
