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
 * A vector space over a field with two operations, vector addition and 
 * scalar multiplication.
 * 
 * @param <V> The structure type of the vector.
 * @param <F> The structure type of the scalar.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Vector_space">
 *      Wikipedia: Vector Space</a>
 */
public interface VectorSpace<V, F extends Field<F>> extends GroupAdditive<V> {
    
    /**
     * Returns the scalar multiplication of this vector by the specified 
     * field element.
     *
     * @param  a the field element,
     * @return <code>this · a</code>.
     */
    V times(F a);

}