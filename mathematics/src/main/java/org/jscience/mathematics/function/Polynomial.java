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
import java.util.Map;
import java.util.Set;

import org.jscience.mathematics.structure.GroupAdditive;
import org.jscience.mathematics.structure.GroupMultiplicative;
import org.jscience.mathematics.structure.Ring;

import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * <p> This class represents a mathematical expression involving a sum of powers
 *     in one or more {@link Variable variables} multiplied by 
 *     coefficients (such as <code>x² + x·y + 3y²</code>).</p>
 *     
 * <p> Polynomials are characterized by the type of variable they operate 
 *     upon. For example:[code]
 *           Variable<Amount<?>> varX = new Variable.Local<Amount<?>>("x");
 *           Polynomial<Amount<?>> x = Polynomial.valueOf(Amount.valueOf(1, SI.METRE), varX);
 *     and
 *           Variable<Complex> varX = new Variable.Local<Complex>("x");
 *           Polynomial<Complex> x = Polynomial.valueOf(Complex.ONE, varX);[/code]
 *     are two different polynomials, the first one operates on physical 
 *     {@link org.jscience.physics.amount.Amount measures},
 *     whereas the second operates on 
 *     {@link org.jscience.mathematics.number.ComplexField complex} numbers.</p>
 *     
 * <p> Terms (others than {@link Term#ONE ONE}) having zero (additive identity) 
 *     for coefficient are automatically removed.</p>    
 * 
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 1, 2006
 */
public class Polynomial<R extends Ring<R>> extends Function<R, R> implements 
         Ring<Polynomial<R>> {

    /**
     * Holds the terms to coefficients mapping 
     * (never empty, holds Term.ONE when constant)
     */
    final FastMap<Term, R> _termToCoef = new FastMap<Term, R>();

    /**
     * Default constructor.
     */
    Polynomial() {
    }

    /**
     * Returns an univariate polynomial of degree one with the specified 
     * coefficient multiplier.
     * 
     * @param coefficient the coefficient for the variable of degree 1.  
     * @param variable the variable for this polynomial.
     * @return <code>valueOf(coefficient, Term.valueOf(variable, 1))</code>
     */
    public static <R extends Ring<R>> Polynomial<R> valueOf(R coefficient,
            Variable<R> variable) {
        return valueOf(coefficient, Term.valueOf(variable, 1));
    }

    /**
     * Returns a polynomial corresponding to the specified {@link Term term}
     * with the specified coefficient multiplier.
     * 
     * @param coefficient the coefficient multiplier.  
     * @param term the term multiplicand.
     * @return <code>coefficient * term</code>
     */
    public static <R extends Ring<R>> Polynomial<R> valueOf(R coefficient,
            Term term) {
        if (term.equals(Term.ONE)) return Constant.valueOf(coefficient);
        if (isZero(coefficient)) return Constant.valueOf(coefficient);
        Polynomial<R> p = Polynomial.newInstance();
        p._termToCoef.put(term, coefficient);
        return p;
    }

    private static boolean isZero(GroupAdditive<?> coefficient) {
        return coefficient.equals(coefficient.opposite());
    }
    
    /**
     * Returns the terms of this polynomial.
     * 
     * @return this polynomial's terms.
     */
    public Set<Term> getTerms() {
        return _termToCoef.unmodifiable().keySet();
    }

    /**
     * Returns the coefficient for the specified term.
     * 
     * @param term the term for which the coefficient is returned.
     * @return the coefficient for the specified term or <code>null</code>
     *         if this polynomial does not contain the specified term.
     */
    public final R getCoefficient(Term term) {
        return _termToCoef.get(term);
    }

    /**
     * Returns the order of this polynomial for the specified variable.
     * 
     * @return the polynomial order relative to the specified variable.
     */
    public int getOrder(Variable<R> v) {
        int order = 0;
        for (Term term : _termToCoef.keySet()) {
            int power = term.getPower(v);
            if (power > order) {
                order = power;
            }
        }
        return order;
    }

    /**
     * Returns the sum of this polynomial with a constant polynomial 
     * having the specified value (convenience method).
     * 
     * @param constantValue the value of the constant polynomial to add.
     * @return <code>this + Constant.valueOf(constantValue)</code>
     */
    public Polynomial<R> plus(R constantValue) {
        return this.plus(Constant.valueOf(constantValue));
    }

    /**
     * Returns the product of this polynomial with a constant polynomial 
     * having the specified value (convenience method).
     * 
     * @param constantValue the value of the constant polynomial to multiply.
     * @return <code>this · Constant.valueOf(constantValue)</code>
     */
    public Polynomial<R> times(R constantValue) {
        return this.times(Constant.valueOf(constantValue));
    }

    /**
     * Returns the sum of two polynomials.
     * 
     * @param that the polynomial being added.
     * @return <code>this + that</code>
     */
    public Polynomial<R> plus(Polynomial<R> that) {
        Polynomial<R> result = Polynomial.newInstance();
        R zero = null;
        result._termToCoef.putAll(this._termToCoef);
        result._termToCoef.putAll(that._termToCoef);
        for (FastMap.Entry<Term, R> e = result._termToCoef.head(), 
                end = result._termToCoef.tail(); (e = e.getNext()) != end;) {
            Term term = e.getKey();
            R thisCoef = this._termToCoef.get(term);
            R thatCoef = that._termToCoef.get(term);
            if ((thisCoef != null) && (thatCoef != null)) {
                R sum = thisCoef.plus(thatCoef);
                if (isZero(sum)) { // Remove entry (be careful iterating)
                    FastMap.Entry<Term, R> prev = e.getPrevious();
                    result._termToCoef.remove(term);
                    e = prev; // Move back to previous entry.
                    zero = sum; // To be used if constant polynomial.
                } else {
                    result._termToCoef.put(term, sum);
                }
            } // Else the current coefficient is correct.
        }
        if (result._termToCoef.size() == 0) return Constant.valueOf(zero);
        return result;
    }

    /**
     * Returns the opposite of this polynomial.
     * 
     * @return <code> - this</code>
     */
    public Polynomial<R> opposite() {
        Polynomial<R> result = Polynomial.newInstance();
        for (FastMap.Entry<Term, R> e = _termToCoef.head(), 
                end = _termToCoef.tail(); (e = e.getNext()) != end;) {
            result._termToCoef.put(e.getKey(), e.getValue().opposite());
        }
        return result;
    }
    
    /**
     * Returns the difference of two polynomials.
     * 
     * @param that the polynomial being subtracted.
     * @return <code>this - that</code>
     */
    public Polynomial<R> minus(Polynomial<R> that) {
        return this.plus(that.opposite());
    }

    /**
     * Returns the product of two polynomials.
     * 
     * @param that the polynomial multiplier.
     * @return <code>this · that</code>
     */
    public Polynomial<R> times(Polynomial<R> that) {
        Polynomial<R> result = Polynomial.newInstance();
        R zero = null;
        for (Map.Entry<Term, R> entry1 : this._termToCoef.entrySet()) {
            Term t1 = entry1.getKey();
            R c1 = entry1.getValue();
            for (Map.Entry<Term, R> entry2 : that._termToCoef.entrySet()) {
                Term t2 = entry2.getKey();
                R c2 = entry2.getValue();
                Term t = t1.times(t2);
                R c = c1.times(c2);
                R prev = result.getCoefficient(t);
                R coef = (prev != null) ? prev.plus(c) : c;
                if (isZero(coef)) {
                    zero = coef;
                } else {
                    result._termToCoef.put(t, coef);
                }
            }
        }
        if (result._termToCoef.size() == 0) return Constant.valueOf(zero);
        return result;
    }

    /**
     * Returns the composition of this polynomial with the one specified.
     *
     * @param  that the polynomial for which the return value is passed as
     *         argument to this function.
     * @return the polynomial <code>(this o that)</code>
     * @throws FunctionException if this function is not univariate.
     */
    public Polynomial<R> compose(Polynomial<R> that) {
        List<Variable<R>> variables = getVariables();
        if (getVariables().size() != 1)
            throw new FunctionException("This polynomial is not monovariate");
        Variable<R> v = variables.get(0);
        Polynomial<R> result = null;
        for (Map.Entry<Term, R> entry : this._termToCoef.entrySet()) {
            Term term = entry.getKey();
            Constant<R> cst = Constant.valueOf(entry.getValue());
            int power = term.getPower(v);
            if (power > 0) {
                Polynomial<R> fn = that.pow(power);
                result = (result != null) ? result.plus(cst.times(fn)) : cst
                        .times(fn);
            } else { // power = 0
                result = (result != null) ? result.plus(cst) : cst;
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////
    // Overrides parent method potentially returning polynomials  //
    ////////////////////////////////////////////////////////////////
    
    @SuppressWarnings("unchecked")
    @Override
    public <Z> Function<Z, R> compose(Function<Z, R> that) {
      return (Function<Z, R>) ((that instanceof Polynomial) ?
      compose((Polynomial)that) : super.compose(that)); 
    }

    @Override
    public Polynomial<R> differentiate(Variable<R> v) {
        if (this.getOrder(v) > 0) {
            Polynomial<R> result = null;
            for (Map.Entry<Term, R> entry : this._termToCoef.entrySet()) {
                Term term = entry.getKey();
                R coef = entry.getValue();
                int power = term.getPower(v);
                if (power > 0) {
                    R newCoef = multiply(coef, power);
                    Term newTerm = term.divide(Term.valueOf(v, 1));
                    Polynomial<R> p = valueOf(newCoef, newTerm);
                    result = (result != null) ? result.plus(p) : p;
                }
            }
            return result;
        } else { // Returns zero.
            R coef = _termToCoef.values().iterator().next();
            return Constant.valueOf(coef.plus(coef.opposite()));
        }
    }

    private static <R extends Ring<R>> R multiply(R o, int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n: " + n
                    + " zero or negative values not allowed");
        R shift2 = o;
        R result = null;
        while (n >= 1) { // Iteration.
            if ((n & 1) == 1) {
                result = (result == null) ? shift2 : result.plus(shift2);
            }
            shift2 = shift2.plus(shift2);
            n >>>= 1;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Polynomial<R> integrate(Variable<R> v) {
        Polynomial<R> result = null;
        for (Map.Entry<Term, R> entry : this._termToCoef.entrySet()) {
            Term term = entry.getKey();
            R coef = entry.getValue();
            int power = term.getPower(v);
            R newCoef = (R)((GroupMultiplicative)multiply((R)((GroupMultiplicative)coef).reciprocal(), power + 1)).reciprocal();
            Term newTerm = term.times(Term.valueOf(v, 1));
            Polynomial<R> p = valueOf(newCoef, newTerm);
            result = (result != null) ? result.plus(p) : p;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<R, R> plus(Function<R, R> that) {
        return (that instanceof Polynomial) ?
                this.plus((Polynomial<R>)that) : super.plus(that);       
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<R, R> minus(Function<R, R> that) {
        return (that instanceof Polynomial) ?
                this.minus((Polynomial<R>)that) : super.minus(that);       
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<R, R> times(Function<R, R> that) {
        return (that instanceof Polynomial) ?
                this.times((Polynomial<R>)that) : super.times(that);       
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Polynomial<R> pow(int n) {
        return (Polynomial<R>) super.pow(n);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Variable<R>> getVariables() {
        // We multiply all terms togethers, the resulting product 
        // will hold all variabgles (powers are always positive).
        Term product = _termToCoef.head().getNext().getKey();
        for (FastMap.Entry<Term, R> e = _termToCoef.head().getNext(), 
                end = _termToCoef.tail(); (e = e.getNext()) != end;) {
            product = product.times(e.getKey());
        }
        FastTable vars = FastTable.newInstance();
        for (int i=0, n = product.size(); i < n; i++) {
            vars.add(product.getVariable(i));
        }
        return vars;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R evaluate() {
        R sum = null;
        for (Map.Entry<Term, R> entry : _termToCoef.entrySet()) {
            Term term = entry.getKey();
            R coef = entry.getValue();
            R termValue = (R) term.evaluate();
            R value = (termValue != null) ? coef.times(termValue) : coef;
            sum = (sum == null) ? value : sum.plus(value);
        }
        return sum;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Polynomial))
            return false;
        Polynomial<?> that = (Polynomial<?>) obj;
        return this._termToCoef.equals(that._termToCoef);
    }

    @Override
    public int hashCode() {
        return _termToCoef.hashCode();
    }

    @Override
    public Text toText() {
        FastTable<Term> terms = FastTable.newInstance();
        terms.addAll(_termToCoef.keySet());
        terms.sort();
        TextBuilder tb = TextBuilder.newInstance();
        for (int i=0, n = terms.size(); i < n; i++) {
            if (i != 0) {
                tb.append(" + ");
            }
            tb.append('[').append(_termToCoef.get(terms.get(i)));
            tb.append(']').append(terms.get(i));
        }
        return tb.toText();
    }
    

    /**
     * Returns a copy of this polynomial 
     * {@link javolution.context.AllocatorContext allocated} 
     * by the calling thread (possibly on the stack).
     *     
     * @return an identical and independant copy of this polynomial.
     */
    public Polynomial<R> copy() {
        Polynomial<R> p = Polynomial.newInstance();
        for (Map.Entry<Term, R> entry : _termToCoef.entrySet()) {
            p._termToCoef.put(entry.getKey().copy(), entry.getValue());
        }    
        return p;
    }

    @SuppressWarnings("unchecked")
    private static <R extends Ring<R>> Polynomial<R> newInstance() {
        Polynomial p = FACTORY.object();
        p._termToCoef.clear();
        return p;
    }
    
    @SuppressWarnings("unchecked")
    private static final ObjectFactory<Polynomial> FACTORY = new ObjectFactory<Polynomial>() {
        protected Polynomial create() {
            return new Polynomial();
        }
    };

    private static final long serialVersionUID = 1L;

}