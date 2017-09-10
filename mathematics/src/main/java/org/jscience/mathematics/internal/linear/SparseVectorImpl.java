/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.linear;

import static javolution.lang.Realtime.Limit.LINEAR;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javolution.lang.Realtime;
import javolution.text.TextContext;
import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastTable;
import javolution.util.Index;
import javolution.util.function.Orders;

import org.jscience.mathematics.matrix.DenseVector;
import org.jscience.mathematics.matrix.DimensionException;
import org.jscience.mathematics.matrix.SparseMatrix;
import org.jscience.mathematics.matrix.SparseVector;
import org.jscience.mathematics.matrix.Vector;
import org.jscience.mathematics.structure.Field;

/**
 * <p> This class holds the sparse vector default implementation.</p>
 *         
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 */
public final class SparseVectorImpl<F extends Field<F>> implements SparseVector<F>, Serializable {

    private final int dimension;
    private final F zero;
    private final List<Index> indices; // Ordered.
    private final FastTable<F> data;

    public SparseVectorImpl(int dimension, F zero, List<Index> indices, F[] data) {
    	this.dimension = dimension;
    	this.zero = zero;
    	this.indices = indices;
    	this.data = new FastTable<F>(data);
    }

    public SparseVectorImpl(Vector<F> that, F zero) {
		FastTable<Index> indices = new FastTable<Index>();
		FastTable<F> data = new FastTable<F>();
		int n = that.dimension();
		for (int i=0; i < n; i++) {
			F e = that.get(i);
			if (!zero.equals(e)) {
				indices.add(Index.valueOf(i));
				data.add(e);
			}
		}
    	this.dimension = n;
    	this.zero = zero;
    	this.indices = indices;
    	this.data = data;
    }

    @Override
    public F getZero() {
        return zero;
    }
    
    @Override
	public List<Index> getIndices() {
    	return indices;
    }

    @Override
	public FastTable<F> getData() {
    	return data;
    }

	@Override
	public abstract SparseMatrix<F> column();

	@Override
	public abstract SparseMatrix<F> row();

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Vector))
			return false;
		return equals((Vector<F>)obj, Orders.STANDARD);
	}

    @Override
	public boolean equals(Vector<F> that, Comparator<? super F> cmp) {
		if (this == that)
			return true;
		final int n = this.dimension();
		if (that.dimension() != n)
			return false;
		if (!(that instanceof SparseVector))
			return DenseVector.valueOf(that).equals(this, cmp);
		SparseVector<F> thatSparse = (SparseVector<F>) that;
		if (cmp.compare(this.getZero(), thatSparse.getZero()) != 0)
			return false;
		// Beware: Indices might not be the same (e.g. some data can be zero).
		FastSet<Index> indices = new FastSet<Index>();
		indices.addAll(this.getIndices()).addAll(thatSparse.getIndices());
		for (Index i : indices) {
			if (cmp.compare(this.get(i.intValue()), that.get(i.intValue())) != 0)
				return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		return getIndices().hashCode() + getData().hashCode() + getZero().hashCode();
	}

	@Override
	public SparseVector<F> minus(Vector<F> that) {
		return this.plus(that.opposite());
	}

	@Override
	public abstract SparseVector<F> opposite();

	@Override
	public abstract SparseVector<F> plus(Vector<F> that);

	@Override
	public abstract SparseVector<F> times(F k);

	@Override
	@Realtime(limit = LINEAR)
	public String toString() {
		return TextContext.getFormat(SparseVector.class).format(this);
	}


    @Override
    public Map<Index, F> asMap() {
        return _elements.unmodifiable();
    }

    @Override
    public int dimension() {
        return _dimension;
    }

    @Override
    public Map<Index, F> getElements() {
        return _elements;
    }

    @Override
    public F get(int i) {
        if ((i < 0) || (i >= _dimension))
            throw new IndexOutOfBoundsException();
        F element = _elements.get(Index.valueOf(i));
        return (element == null) ? _zero : element;
    }

    @Override
    public SparseVectorImpl<F> getSubVector(List<Index> indices) {
        SparseVectorImpl<F> V = FACTORY.object();
        V._dimension = indices.size();
        V._zero = _zero;
        int i = 0;
        for (Index index : indices) {
            F element = this._elements.get(index);
            if (element != null) {
                V._elements.put(Index.valueOf(i++), element);
            }
        }
        return V;
    }

    @Override
    public SparseVectorImpl<F> opposite() {
        SparseVectorImpl<F> V = FACTORY.object();
        V._dimension = _dimension;
        V._zero = _zero;
        for (FastMap.Entry<Index, F> e = _elements.head(), n = _elements.tail(); (e = e.getNext()) != n;) {
            V._elements.put(e.getKey(), e.getValue().opposite());
        }
        return V;
    }

    @Override
    public SparseVectorImpl<F> plus(VectorImpl<F> that) {
        if (that instanceof SparseVectorImpl)
            return plus((SparseVectorImpl<F>) that);
        return plus((SparseVectorImpl) SparseVector.valueOf(that));
    }

    private SparseVectorImpl<F> plus(SparseVectorImpl<F> that) {
        if (this._dimension != that._dimension)
            throw new DimensionException();
        SparseVectorImpl<F> V = FACTORY.object();
        V._dimension = _dimension;
        V._zero = _zero;
        V._elements.putAll(this._elements);
        for (FastMap.Entry<Index, F> e = that._elements.head(), n = that._elements.tail();
                (e = e.getNext()) != n;) {
            Index index = e.getKey();
            FastMap.Entry<Index, F> entry = V._elements.getEntry(index);
            if (entry == null) {
                V._elements.put(index, e.getValue());
            } else {
                F newElement = entry.getValue().plus(e.getValue());
                if (!_zero.equals(newElement)) {
                    V._elements.put(e.getKey(), newElement);
                } else {
                    V._elements.remove(e.getKey());
                }
            }
        }
        return V;
    }

    @Override
    public SparseVectorImpl<F> times(F k) {
        SparseVectorImpl<F> V = FACTORY.object();
        V._dimension = _dimension;
        V._zero = _zero;
        for (FastMap.Entry<Index, F> e = _elements.head(), n = _elements.tail(); (e = e.getNext()) != n;) {
            F newElement = e.getValue().times(k);
            if (!_zero.equals(newElement)) {
                V._elements.put(e.getKey(), newElement);
            }
        }
        return V;
    }

    @Override
    public F times(VectorImpl<F> that) {
        if (that.getDimension() != _dimension)
            throw new DimensionException();
        F sum = null;
        for (FastMap.Entry<Index, F> e = _elements.head(), n = _elements.tail(); (e = e.getNext()) != n;) {
            F f = e.getValue().times(that.get(e.getKey().intValue()));
            sum = (sum == null) ? f : sum.plus(f);
        }
        return (sum != null) ? sum : _zero;
    }

    @Override
    public SparseVectorImpl<F> copy() {
        SparseVectorImpl<F> V = FACTORY.object();
        V._dimension = _dimension;
        V._zero = (F) _zero.copy();
        for (Map.Entry<Index, F> e : _elements.entrySet()) {
            V._elements.put(e.getKey(), (F) e.getValue().copy());
        }
        return V;
    }
    private static final long serialVersionUID = 1L;

}
