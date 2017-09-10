/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import javolution.context.ObjectFactory;

import org.jscience.mathematics.structure.Ring;

/**
 * <p> This class represents a constant function (polynomial of degree 0).<p>
 * 
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 1, 2006
 */
public final class Constant<R extends Ring<R>> extends Polynomial<R> {

    /**
     * Default constructor.
     */
    private Constant() {
    }

    /**
     * Returns a constant function of specified value.
     * 
     * @param value the value returned by this function.
     * @return the corresponding constant function.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Ring<R>> Constant<R> valueOf(R value) {
        Constant<R> cst = FACTORY.object();
        cst._termToCoef.put(Term.ONE, value);
        return cst;
    }

    @SuppressWarnings("unchecked")
    private static final ObjectFactory<Constant> FACTORY = new ObjectFactory<Constant>() {
        protected Constant create() {
            return new Constant();
        }

        protected void cleanup(Constant cst) {
            cst._termToCoef.reset();
        }
    };

    /**
     * Returns the constant value for this function.
     *
     * @return <code>getCoefficient(Term.CONSTANT)</code>
     */
    public R getValue() {
        return getCoefficient(Term.ONE);
    }

    private static final long serialVersionUID = 1L;
}