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
 * A structure with a binary multiplicative operation (·), satisfying the 
 * group axioms (associativity, neutral element, inverse element and closure).
 * 
 * @param <G> The structure type.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Mathematical_Group">
 *      Wikipedia: Mathematical Group</a>
 */
public interface GroupMultiplicative<G> extends Structure<G> {

    /**
     * Returns the product of this object with the one specified.
     *
     * @param  that the object multiplier.
     * @return <code>this · that</code>.
     */
    G times(G that);

    /**
     * Returns the multiplicative inverse of this object. It it the object
     * such as  {@code this.times(this.reciprocal()) == ONE},
     * with <code>ONE</code> being the multiplicative identity.
     * 
     * <p> Note: For coherence, implementations should provide a multiplicative
     *     inverse for ZERO (e.g. INFINITY) for which 
     *    {@code ZERO.times(ZERO.reciprocal()) == ONE}.</p>     
     *
     * @return <code>ONE / this</code>.
     */
    G reciprocal();

}