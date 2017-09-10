/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

/**
 * Signals that an operation is performed upon vectors or matrices whose
 * dimensions disagree.
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public class DimensionException extends RuntimeException {

    private static final long serialVersionUID = 0x300L; // Version.

	/**
     * Constructs a dimension exception with no detail message.
     */
    public DimensionException() {
        super();
    }

    /**
     * Constructs a dimension exception with the specified message.
     * 
     * @param message the error message.
     */
    public DimensionException(String message) {
        super(message);
    }

}