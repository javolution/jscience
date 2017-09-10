/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import javolution.lang.Reference;
import javolution.context.LocalContext;

/**
 * <p> This interface represents a symbol on whose value a {@link Function}
 *     depends. If the functions is not shared between multiple-threads the 
 *     simple {@link Variable.Local} implementation can be used. 
 *     For global functions (functions used concurrently by multiple threads)
 *     the {@link Variable.Global} implementation with 
 *     {@link javolution.context.LocalContext context-local} settings is 
 *     recommended.</p>
 *   
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 * @see  Function#evaluate 
 */
public interface Variable<X> extends Reference<X> {

    /**
     * Returns the symbol for this variable.
     * 
     * @return this variable's symbol.
     */
    String getSymbol();

    /**
     * This class represents a simple {@link Variable} implementation for 
     * functions not shared between threads (non static).
     * Functions shared between multiple-threads should use a different 
     * type of variable such as {@link Variable.Global}. 
     */
    public static class Local<X> implements Variable<X> {

        /**
         * Holds the reference value.
         */
        private X _value;

        /**
         * Holds the variable symbol.
         */
        private final String _symbol;

        /**
         * Creates a new local variable with a unique symbol.
         * 
         * @param symbol the variable symbol.
         */
        public Local(String symbol) {
            _symbol = symbol;
        }

        public String getSymbol() {
            return _symbol;
        }

        public X get() {
            return _value;
        }

        public void set(X arg0) {
            _value = arg0;
        }
    }

    /**
     * This class represents a simple {@link Variable} implementation with 
     * {@link javolution.context.LocalContext context-local} values.
     * Instances of this class can be set independently by multiple-threads 
     * as long as each concurrent thread executes within a 
     * {@link javolution.context.LocalContext LocalContext}. For example:[code]
     * public abstract class Engine  {
     *     public static final Variable.Global<Amount<AngularVelocity>> RPM
     *         = new Variable.Global<Amount<AngularVelocity>>("rpm");
     *     public abstract Function<Amount<AngularVelocity>, Amount<Torque>> getTorque();    
     * }
     * ...
     * LocalContext.enter(); 
     * try {
     *     RPM.set(rpm);
     *     Amount<Torque> torque = myEngine.getTorque().evaluate();
     * } finally {
     *     LocalContext.exit();
     * }[/code]
     * It should be noted that parameterized evaluations are performed within
     *  a local context. Therefore, the example
     * above could also be rewritten:[code]
     *     Amount<Torque> torque = myEngine.getTorque().evaluate(rpm);
     * [/code]
     */
    public static class Global<X> implements Variable<X> {

        /**
         * Holds the reference value.
         */
        private LocalContext.Reference<X> _value = new LocalContext.Reference<X>();

        /**
         * Holds the variable symbol.
         */
        private final String _symbol;

        /**
         * Creates a new global variable with a unique symbol.
         * 
         * @param symbol the variable symbol.
         */
        public Global(String symbol) {
            _symbol = symbol;
        }

        public String getSymbol() {
            return _symbol;
        }

        public X get() {
            return _value.get();
        }

        public void set(X arg0) {
            _value.set(arg0);
        }
    }

}