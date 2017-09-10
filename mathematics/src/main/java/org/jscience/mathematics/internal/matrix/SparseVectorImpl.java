/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.matrix;

import java.util.Map;
import javolution.lang.MathLib;
import javolution.util.Index;
import org.jscience.mathematics.matrix.SparseVector;
import org.jscience.mathematics.structure.Field;

/**
 * Sparse vector default implementation (trie-based).
 */
public class SparseVectorImpl<F extends Field<F>> extends SparseVector<F> {

    // Trie Implementation.
    private static final long serialVersionUID = 0x500L;
    private static final int SHIFT = 4;
    private static final int MASK = (1 << SHIFT) - 1;
       private final int dimension;
    private final F zero;
    private final Object[] nodes;
    private final int shift;

    /**
     * Creates a new sparse vector (empty).
     */
    public SparseVectorImpl(int dimension, F zero) {
        this.dimension = dimension;
        this.zero = zero;
        int shiftValue = 0; 
        int capacity = 1 << SHIFT;
        while (capacity < dimension) {
            capacity <<= SHIFT;
            shiftValue += SHIFT;
        }
        this.shift = shiftValue;
        this.nodes = new Object[((dimension-1) >> shift) + 1];        
    }

    @SuppressWarnings("unchecked")
    @Override
    public final F get(int index) {
        if (index >= dimension) throw new IndexOutOfBoundsException();
        int shiftValue = shift;
        Object node = nodes[index >> shiftValue];
        while (node != null) {
            if (node instanceof Entry) {
                Entry<F> entry = (Entry<F>) node;
                return (entry.getIndex() == index) ? entry.getValue() : zero;
            } 
            shiftValue -= SHIFT;
            node = ((Object[])node)[(index >> shiftValue) & MASK];
        }
        return zero;
        
    }
 
    /** Adds the specified value at the specified index. */
    @SuppressWarnings("unchecked")
    public final void add(int index, F value) {
        Object node = nodes[index >> shift];
        if (node == null) {
            nodes[index >> shift] = new Entry<F>(index, value);
        } else if (node instanceof SparseVectorImpl) { // Recurse.
            ((SparseVectorImpl<F>)node).add(index & ((1 << shift) -1), value);
        } else { 
            Entry<F> entry = (Entry<F>) node;
            if (entry.getIndex() == i) {
                entry.setValue(entry.getValue().plus(value));
            } else { // Collision.
                int newShift = shift + SHIFT;
                SparseVectorImpl<F> inner = new SparseVectorImpl<F>(dimension, zero, newShift);
                inner.nodes[(entry.getIndex() >> newShift) & MASK] = entry;
                inner.add(index, value); // Recurse.
                nodes[i] = inner;
            }
        }
    }
 
    @Override
    public final int dimension() {
        return dimension;
    }

    @Override
    public final F getZero() {
        return zero;
    }

    public void forNonZero(Consumer<? super F> consumer) {
        forNonZero(consumer, nodes);
    }
    
    // Attention.
    // Diagonale: filter(accept(Range<x, y>)
    //      row1 = x / n; x % n
    //      row2 = x / n; x % n
    //  
    
    
    @SuppressWarnings("unchecked")
    private static <F extends Field<F>> void forNonZero(Consumer<? super F> consumer, Object[] nodes) {
        for (Object node : nodes) {
            if (node != null) {
                if (node instanceof Entry) {
                    Entry<F> entry = (Entry<F>) node;
                    consumer.accept(entry.getIndex(), entry.getValue());
                } else {
                    forNonZero(consumer, (Object[])node);
                }                
            }
        }
    }

}
