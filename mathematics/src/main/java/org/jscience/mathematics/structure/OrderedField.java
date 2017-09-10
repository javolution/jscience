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
 * A field together with a total ordering of its elements that is compatible 
 * with the field operations.
 * 
 * @param <F> The structure type.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Ordered_field">
 *      Wikipedia: Ordered Field</a>
 */
public interface OrderedField<F> extends Field<F>, Comparable<F> {

}