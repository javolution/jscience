/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.structure;

/**
 * A vector space on which a positive vector length or size is defined. 
 * 
 * @param <V> The structure type of the vector.
 * @param <F> The structure type of the scalar.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Normed_vector_space">
 *      Wikipedia: Normed Vector Space</a>
 */
public interface NormedVectorSpace<V, F extends Field<F>> extends VectorSpace<V, F> {
    
    /**
     * Returns the positive length or size of this vector (Euclidean Norm).
     *
     * @return <code>|this|</code>.
     * @see <a href="http://en.wikipedia.org/wiki/Vector_norm">
     *      Wikipedia: Vector Norm</a>
     */
    F norm();
    
}