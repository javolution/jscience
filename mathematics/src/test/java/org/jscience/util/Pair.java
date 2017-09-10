/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.util;

/**
 * A simple helper class that holds an immutable pair of objects.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 * @since 11.12.2008
 * @param <T1> the type of the first object
 * @param <T2> the type of the second object
 */
public final class Pair<T1, T2> {

    /** The first object. */
    public final T1 _x;

    /** The second object. */
    public final T2 _y;

    /** Please use {@link #make(Object, Object)}. */
    private Pair(T1 x, T2 y) {
        _x = x;
        _y = y;
    }

    /** Constructs a {@link Pair} of objects: use for instance as <code>Pair.make(14,"foo")</code>. */
    public static <T1, T2> Pair<T1, T2> make(T1 x, T2 y) {
        return new Pair<T1, T2>(x, y);
    }

    /**
     * A string representation for debugging purposes. For instance {14,foo}.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + _x + "," + _y + "}";
    }
}
