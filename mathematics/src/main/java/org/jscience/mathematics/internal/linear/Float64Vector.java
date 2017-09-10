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
import java.util.List;

import javolution.context.ArrayFactory;
import javolution.lang.MathLib;
import javolution.text.CharSet;
import javolution.text.Cursor;
import javolution.text.TextFormat;
import javolution.util.FastTable;
import javolution.util.Index;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.structure.NormedVectorSpace;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.DimensionException;

/**
 * <p> This class represents an optimized {@link DenseVector}
 *     implementation for 64 bits floating point elements.</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 */
public class Float64Vector extends DenseVector<Real> implements
        NormedVectorSpace<VectorImpl<Real>, Real> {

    /**
     * Holds factory for vectors with variable size arrays.
     */
    static final ArrayFactory<Float64Vector> FACTORY = new ArrayFactory<Float64Vector>() {

        @Override
        protected Float64Vector create(int capacity) {
            return new Float64Vector(capacity);
        }
    };

    /**
     * Holds the dimension.
     */
    int _dimension;

    /**
     * Holds the values.
     */
    final double[] _values;

    /**
     * Creates a vector of specified capacity.
     */
    private Float64Vector(int capacity) {
        _values = new double[capacity];
    }

    /**
     * Creates a 64 bits floating point vector always on the heap independently
     * from the current {@link javolution.context.AllocatorContext allocator context}.
     * To allow for custom object allocation policies, static factory methods
     * <code>valueOf(...)</code> are recommended.
     *
     * @param values the values of the 64 bits floating points elements.
     */
    public Float64Vector(double... values) {
        this(values.length);
        int n = values.length;
        _dimension = n;
        System.arraycopy(values, 0, _values, 0, n);
    }

    /**
     * Returns a new vector holding the specified <code>double</code> values.
     *
     * @param values the vector values.
     * @return the vector having the specified values.
     */
    public static Float64Vector valueOf(double... values) {
        int n = values.length;
        Float64Vector V = FACTORY.array(n);
        V._dimension = n;
        System.arraycopy(values, 0, V._values, 0, n);
        return V;
    }

    /**
     * Returns a {@link Float64Vector} instance equivalent to the 
     * specified vector.
     *
     * @param that the vector to convert. 
     * @return <code>that</code> or new equivalent Float64Vector.
     */
    public static Float64Vector valueOfVector(VectorImpl<Real> that) {
        if (that instanceof Float64Vector)
            return (Float64Vector) that;
        int n = that.getDimension();
        Float64Vector V = FACTORY.array(n);
        V._dimension = n;
        for (int i = 0; i < n; i++) {
            V._values[i] = that.get(i).doubleValue();
        }
        return V;
    }

   /**
     * Returns a new vector holding the elements from the specified
     * collection.
     *
     * @param elements the collection of numbers.
     * @return the vector having the specified elements.
     */
    public static Float64Vector valueOfList(List<Real> elements) {
        int n = elements.size();
        Float64Vector V = FACTORY.array(n);
        V._dimension = n;
        for (int i = 0; i < n; i++) {
            V._values[i] = elements.get(i).doubleValue();
        }
        return V;
    }
    
    /**
     * Returns the 64 bits floating point vector for the specified character
     * sequence.
     *
     * @param  csq the character sequence.
     * @return <code>TextFormat.getInstance(Float64Vector.class).parse(csq)</code>
     */
    public static Float64Vector valueOf(CharSequence csq) {
        return TextFormat.getInstance(Float64Vector.class).parse(csq);
    }

    /**
     * Returns the value of a floating point number from this vector (fast).
     *
     * @param  i the floating point number index.
     * @return the value of the floating point number at <code>i</code>.
     * @throws IndexOutOfBoundsException <code>(i &lt; 0) || (i &gt;= dimension())</code>
     */
    public double getValue(int i) {
        if (i >= _dimension)
            throw new ArrayIndexOutOfBoundsException();
        return _values[i];
    }

    /**
     * Returns the Euclidian norm of this vector (square root of the 
     * dot product of this vector and itself).
     *
     * @return <code>sqrt(this · this)</code>.
     */
    public Real norm() {
        return Real.valueOf(normValue());
    }

    /**
     * Returns the {@link #norm()} value of this vector.
     *
     * @return <code>this.norm().doubleValue()</code>.
     */
    public double normValue() {
        double normSquared = 0;
        for (int i = _dimension; --i >= 0;) {
            double values = _values[i];
            normSquared += values * values;
        }
        return MathLib.sqrt(normSquared);
    }

    @Override
    public List<Real> asList() {
        FastTable<Real> list = FastTable.newInstance();
        for (int i=0; i < _dimension; i++) {
            list.add(Real.valueOf(_values[i]));
        }
        return list.unmodifiable();
    }
    
    @Override
    public int dimension() {
        return _dimension;
    }

    @Override
    public Real get(int i) {
        if (i >= _dimension)
            throw new IndexOutOfBoundsException();
        return Real.valueOf(_values[i]);
    }

    @Override
    public Float64Vector getSubVector(List<Index> indices) {
        int dimension = indices.size();
        Float64Vector V = FACTORY.array(dimension);
        V._dimension = dimension;
        for (int i = 0; i < dimension; i++) {
            V._values[i] = _values[indices.get(i).intValue()];
        }
        return V;
    }

    @Override
    public Float64Vector opposite() {
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = -_values[i];
        }
        return V;
    }

    @Override
    public Float64Vector plus(VectorImpl<Real> that) {
        Float64Vector T = Float64Vector.valueOfVector(that);
        if (T._dimension != _dimension)
            throw new DimensionException();
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = _values[i] + T._values[i];
        }
        return V;
    }

    @Override
    public Float64Vector minus(VectorImpl<Real> that) {
        Float64Vector T = Float64Vector.valueOfVector(that);
        if (T._dimension != _dimension)
            throw new DimensionException();
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = _values[i] - T._values[i];
        }
        return V;
    }

    @Override
    public Float64Vector times(Real k) {
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        double d = k.doubleValue();
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = _values[i] * d;
        }
        return V;
    }

    /**
     * Equivalent to <code>this.times(Float64.valueOf(k))</code>
     *
     * @param k the coefficient. 
     * @return <code>this * k</code>
     */
    public Float64Vector times(double k) {
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = _values[i] * k;
        }
        return V;
    }

    @Override
    public Real times(VectorImpl<Real> that) {
        Float64Vector T = Float64Vector.valueOfVector(that);
        if (T._dimension != _dimension)
            throw new DimensionException();
        double[] T_values = T._values;
        double sum = _values[0] * T_values[0];
        for (int i = 1; i < _dimension; i++) {
            sum += _values[i] * T_values[i];
        }
        return Real.valueOf(sum);
    }

    @Override
    public Float64Vector cross(VectorImpl<Real> that) {
        Float64Vector T = Float64Vector.valueOfVector(that);
        if ((this._dimension != 3) || (T._dimension != 3))
            throw new DimensionException(
                    "The cross product of two vectors requires " + "3-dimensional vectors");
        double x = _values[1] * T._values[2] - _values[2] * T._values[1];
        double y = _values[2] * T._values[0] - _values[0] * T._values[2];
        double z = _values[0] * T._values[1] - _values[1] * T._values[0];
        return Float64Vector.valueOf(x, y, z);
    }

    @Override
    public Float64Vector copy() {
        Float64Vector V = FACTORY.array(_dimension);
        V._dimension = _dimension;
        for (int i = 0; i < _dimension; i++) {
            V._values[i] = _values[i];
        }
        return V;
    }
    private static final long serialVersionUID = 1L;

}
