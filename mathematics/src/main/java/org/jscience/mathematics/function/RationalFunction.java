/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import java.util.List;
import org.jscience.mathematics.structure.Field;

import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * This class represents the quotient of two {@link Polynomial}, 
 * it is also a {@link Field field} (invertible). 
 * 
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 1, 2006
 */
public class RationalFunction<F extends Field<F>> extends Function<F, F>
        implements Field<RationalFunction<F>> {

    /**
     * Holds the factory for rational functions.
     */
    /**
     * Holds the dividend.
     */
    private Polynomial<F> _dividend;

    /**
     * Holds the divisor.
     */
    private Polynomial<F> _divisor;

    /**
     * Default constructor.
     */
    private RationalFunction() {
    }

    /**
     * Returns the dividend of this rational function.
     * 
     * @return this rational function dividend. 
     */
    public Polynomial<F> getDividend() {
        return _dividend;
    }

    /**
     * Returns the divisor of this rational function.
     * 
     * @return this rational function divisor.
     */
    public Polynomial<F> getDivisor() {
        return _divisor;
    }

    /**
     * Returns the rational function from the specified dividend and divisor.
     * 
     * @param dividend the dividend value.
     * @param divisor the divisor value.
     * @return <code>dividend / divisor</code>
     */
    @SuppressWarnings("unchecked")
    public static <F extends Field<F>> RationalFunction<F> valueOf(
            Polynomial<F> dividend, Polynomial<F> divisor) {
        RationalFunction<F> rf = FACTORY.object();
        rf._dividend = dividend;
        rf._divisor = divisor;
        return rf;
    }

    @SuppressWarnings("unchecked")
    private static final ObjectFactory<RationalFunction> FACTORY = new ObjectFactory<RationalFunction>() {

        protected RationalFunction create() {
            return new RationalFunction();
        }

        @SuppressWarnings("unchecked")
        protected void cleanup(RationalFunction rf) {
            rf._dividend = null;
            rf._divisor = null;
        }
    };

    /**
     * Returns the sum of two rational functions.
     * 
     * @param that the rational function being added.
     * @return <code>this + that</code>
     */
    public RationalFunction<F> plus(RationalFunction<F> that) {
        return valueOf(this._dividend.times(that._divisor).plus(
                this._divisor.times(that._dividend)), this._divisor
                .times(that._divisor));
    }

    /**
     * Returns the opposite of this rational function.
     * 
     * @return <code>- this</code>
     */
    public RationalFunction<F> opposite() {
        return valueOf(_dividend.opposite(), _divisor);
    }

    /**
     * Returns the difference of two rational functions.
     * 
     * @param that the rational function being subtracted.
     * @return <code>this - that</code>
     */
    public RationalFunction<F> minus(RationalFunction<F> that) {
        return this.plus(that.opposite());
    }

    /**
     * Returns the product of two rational functions.
     * 
     * @param that the rational function multiplier.
     * @return <code>this · that</code>
     */
    public RationalFunction<F> times(RationalFunction<F> that) {
        return valueOf(this._dividend.times(that._dividend), this._divisor
                .times(that._divisor));
    }

    /**
     * Returns the inverse of this rational function.
     * 
     * @return <code>1 / this</code>
     */
    public RationalFunction<F> reciprocal() {
        return valueOf(_divisor, _dividend);
    }

    /**
     * Returns the quotient of two rational functions.
     * 
     * @param that the rational function divisor.
     * @return <code>this / that</code>
     */
    public RationalFunction<F> divide(RationalFunction<F> that) {
        return this.times(that.reciprocal());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Variable<F>> getVariables() {
        return merge(_dividend.getVariables(), _divisor.getVariables());
    }

    @SuppressWarnings("unchecked")
    @Override
    public F evaluate() {
        return _dividend.evaluate().times(_divisor.evaluate().reciprocal());
    }

    @Override
    public Text toText() {
        TextBuilder tb = TextBuilder.newInstance();
        tb.append('(');
        tb.append(_dividend);
        tb.append(")/(");
        tb.append(_divisor);
        tb.append(')');
        return tb.toText();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RationalFunction) {
            RationalFunction<?> that = (RationalFunction<?>) obj;
            return this._dividend.equals(this._dividend)
                    && this._divisor.equals(that._divisor);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _dividend.hashCode() - _divisor.hashCode();
    }

    //////////////////////////////////////////////////////////////////////
    // Overrides parent method potentially returning rational functions //
    //////////////////////////////////////////////////////////////////////
    
    @Override
    public RationalFunction<F> differentiate(Variable<F> v) {
        return valueOf(_divisor.times(_dividend.differentiate(v)).plus(
                _dividend.times(_divisor.differentiate(v)).opposite()),
                _dividend.pow(2));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<F, F> plus(Function<F, F> that) {
        return (that instanceof RationalFunction) ?
                this.plus((RationalFunction<F>)that) : super.plus(that);       
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<F, F> minus(Function<F, F> that) {
        return (that instanceof RationalFunction) ?
                this.minus((RationalFunction<F>)that) : super.minus(that);       
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<F, F> times(Function<F, F> that) {
        return (that instanceof RationalFunction) ?
                this.times((RationalFunction<F>)that) : super.times(that);       
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Function<F, F> divide(Function<F, F> that) {
        return (that instanceof RationalFunction) ?
                this.divide((RationalFunction<F>)that) : super.divide(that);       
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RationalFunction<F> pow(int n) {
        return (RationalFunction<F>) super.pow(n);
    }


    /**
     * Returns a copy of this rational function. 
     * {@link javolution.context.AllocatorContext allocated} 
     * by the calling thread (possibly on the stack).
     *     
     * @return an identical and independant copy of this rational function.
     */
    public RationalFunction<F> copy() {
        return RationalFunction.valueOf(_dividend.copy(), _divisor.copy());
    }

    private static final long serialVersionUID = 1L;

}