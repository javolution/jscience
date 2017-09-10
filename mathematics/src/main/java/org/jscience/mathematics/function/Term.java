/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.function;

import java.io.Serializable;
import org.jscience.mathematics.structure.Ring;

import javolution.context.ArrayFactory;
import javolution.lang.MathLib;
import javolution.lang.Realtime;
import javolution.lang.ValueType;
import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * This class represents the term of a {@link Polynomial polynomial} 
 * such as <code>x·y²</code>. 
 * 
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.0, February 13, 2006
 */
public final class Term implements Serializable, Comparable<Term>, ValueType,
        Realtime {

    /**
     * Holds the multiplicative identity.
     */
    public static Term ONE = new Term(0);

    /**
     * Holds the term's factory.
     */
    private static final ArrayFactory<Term> FACTORY = new ArrayFactory<Term>() {

        @Override
        protected Term create(int size) {
            return new Term(size);
        }
    };

    /**
     * Holds the variables (ordered).
     */
    private final Variable<?>[] _variables;

    /**
     * Holds the corresponding powers (positive and different from zero).
     */
    private final int[] _powers;

    /**
     * Holds the number of variables.
     */
    private int _size;

    /**
     * Creates a new term of specified capacity.
     * 
     * @param capacity the maxium number of variables.
     */
    private Term(int capacity) {
        _variables = new Variable[capacity];
        _powers = new int[capacity];
    }

    /**
     * Return the term corresponding to the specified variable raised to
     * the specified power.
     * 
     * @param v the variable.
     * @param n the power. 
     * @return the term for <code>v<sup>n</sup></code>
     * @throws IllegalArgumentException if <code>n &lt; 0</code> 
     */
    public static Term valueOf(Variable<?> v, int n) {
        if (n == 0)
            return ONE;
        if (n < 0)
            throw new IllegalArgumentException("n: " + n
                    + " negative values are not allowed");
        Term term = FACTORY.array(1);
        term._variables[0] = v;
        term._powers[0] = n;
        term._size = 1;
        return term;
    }

    /**
     * Returns the number of variables for this term.
     * 
     * @return the number of variables.
     */
    public int size() {
        return _size;
    }

    /**
     * Returns the variable at the specified index (variables are 
     * lexically ordered).
     * 
     * @param index the variable index.
     * @return this term variables at specified position.
     * @throws IndexOutOfBoundsException if  
     *         <code>(index < 0) || (index >= size())</code>
     */
    public Variable<?> getVariable(int index) {
        if (index > _size)
            throw new IllegalArgumentException();
        return _variables[index];
    }

    /**
     * Returns the power of the variable at the specified position.
     * 
     * @param index the variable index.
     * @return the power of the variable at the specified index.
     * @throws IndexOutOfBoundsException if  
     *         <code>(index < 0) || (index >= size())</code>
     */
    public int getPower(int index) {
        if (index > _size)
            throw new IllegalArgumentException();
        return _powers[index];
    }

    /**
     * Returns the power of the specified variable.
     * 
     * @param v the variable for which the power is returned.
     * @return the power of the corresponding variable or <code>0</code> if 
     *         this term does not hold the specified variable.
     */
    public int getPower(Variable<?> v) {
        for (int i = 0; i < _size; i++) {
            if (_variables[i] == v)
                return _powers[i];
        }
        return 0;
    }

    /**
     * Return the product of this term with the one specified. 
     * 
     * @param that the term multiplier.
     * @return <code>this · that</code>
     * @throws IllegalArgumentException if the specified term holds a 
     *         variable having the same symbol as one of the variable of
     *         this term; but both variables are distinct.
     */
    public Term times(Term that) {
        final int thisSize = this.size();
        final int thatSize = that.size();
        Term result = FACTORY.array(thisSize + thatSize);
        result._size = 0;
        for (int i = 0, j = 0;;) {
            Variable<?> left = (i < thisSize) ? this._variables[i] : null;
            Variable<?> right = (j < thatSize) ? that._variables[j] : null;
            if (left == null) {
                if (right == null)
                    return result;
                result._powers[result._size] = that._powers[j++];
                result._variables[result._size++] = right;
                continue;
            }
            if (right == null) {
                result._powers[result._size] = this._powers[i++];
                result._variables[result._size++] = left;
                continue;
            }
            if (right == left) {
                result._powers[result._size] = this._powers[i++]
                        + that._powers[j++];
                result._variables[result._size++] = right;
                continue;
            }
            final int cmp = left.getSymbol().compareTo(right.getSymbol());
            if (cmp < 0) {
                result._powers[result._size] = this._powers[i++];
                result._variables[result._size++] = left;
            } else if (cmp > 0) {
                result._powers[result._size] = that._powers[j++];
                result._variables[result._size++] = right;
            } else {
                throw new IllegalArgumentException(
                        "Found distinct variables with same symbol: "
                                + left.getSymbol());
            }
        }
    }

    /**
     * Return the division of this term with the one specified. 
     * 
     * @param that the term divisor.
     * @return <code>this / that</code>
     * @throws UnsupportedOperationException if this division would 
     *         result in negative power.
     * @throws IllegalArgumentException if the specified term holds a 
     *         variable having the same symbol as one of the variable of
     *         this term; but both variables are distinct.
     */
    public Term divide(Term that) {
        final int thisSize = this._size;
        final int thatSize = that._size;
        Term result = FACTORY.array(MathLib.max(thisSize, thatSize));
        result._size = 0;
        for (int i = 0, j = 0;;) {
            Variable<?> left = (i < thisSize) ? this._variables[i] : null;
            Variable<?> right = (j < thatSize) ? that._variables[j] : null;
            if (left == null) {
                if (right == null)
                    return result;
                throw new UnsupportedOperationException(this + "/" + that
                        + " would result in a negative power");
            }
            if (right == null) {
                result._powers[result._size] = this._powers[i++];
                result._variables[result._size++] = left;
                continue;
            }
            if (right == left) {
                final int power = this._powers[i++] - that._powers[j++];
                if (power < 0)
                    throw new UnsupportedOperationException(this + "/" + that
                            + " would result in a negative power");
                if (power > 0) {
                    result._powers[result._size] = power;
                    result._variables[result._size++] = right;
                }
                continue;
            }
            final int cmp = left.getSymbol().compareTo(right.getSymbol());
            if (cmp < 0) {
                result._powers[result._size] = this._powers[i++];
                result._variables[result._size++] = left;
            } else if (cmp > 0) {
                throw new UnsupportedOperationException(this + "/" + that
                        + " would result in a negative power");
            } else {
                throw new IllegalArgumentException(
                        "Found distinct variables with same symbol: "
                                + left.getSymbol());
            }
        }
    }

    /**
     * Indicates if this term is equal to the object specified.
     *
     * @param  obj the object to compare for equality.
     * @return <code>true</code> if this term and the specified object are
     *         considered equal; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Term))
            return false;
        Term that = (Term) obj;
        if (this._size != that._size)
            return false;
        for (int i = 0; i < _size; i++) {
            if ((!this._variables[i].equals(that._variables[i]))
                    || (this._powers[i] != that._powers[i]))
                return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this term.
     *
     * @return a hash code value for this object.
     */
    public final int hashCode() {
        int h = 0;
        for (int i = 0; i < _size; i++) {
            h += _variables[i].hashCode() * _powers[i];
        }
        return h;
    }

    /**
     * Returns the text representation of this term as a 
     * <code>java.lang.String</code>.
     * 
     * @return <code>toText().toString()</code>
     */
    public final String toString() {
        return toText().toString();
    }

    /**
     * Returns the text representation of this term.
     */
    public Text toText() {
        TextBuilder tb = TextBuilder.newInstance();
        for (int i = 0; i < _size; i++) {
            tb.append(_variables[i].getSymbol());
            int power = _powers[i];
            switch (power) {
            case 1:
                break;
            case 2:
                tb.append('²');
                break;
            case 3:
                tb.append('³');
                break;
            default:
                tb.append(power);
            }
        }
        return tb.toText();
    }

    /**
     * Returns an entierely new copy of this term 
     * {@link javolution.context.AllocatorContext allocated} 
     * by the calling thread (possibly on the stack).
     *     
     * @return an identical and independant copy of this term.
     */
    public Term copy() {
        Term term = FACTORY.array(_size);
        term._size = _size;
        for (int i = 0; i < _size; i++) {
            term._powers[i] = _powers[i];
            term._variables[i] = _variables[i];
        }
        return term;
    }

    /**
     * Compares this term with the one specified for order.
     * 
     * @param that the term to be compared to.
     * @return a negative integer, zero, or a positive integer as this term
     *         is less than, equal to, or greater than the specified term.
     */
    public int compareTo(Term that) {
        int n = Math.min(this._size, that._size);
        for (int i = 0; i < n; i++) {
            int cmp = this._variables[i].getSymbol().compareTo(
                    that._variables[i].getSymbol());
            if (cmp != 0)
                return cmp;
            cmp = that._powers[i] - this._powers[i];
            if (cmp != 0)
                return cmp;
        }
        return that._size - this._size;
    }

    /**
     * Evaluates this term by replacing its {@link Variable
     * variables} by their current (context-local) values.
     *
     * @return the evaluation of this term or <code>null</code> if ONE.
     * @throws FunctionException if any of this term's variable is not set.
     */
    @SuppressWarnings("unchecked")
    Ring evaluate() {
        Ring result = null;
        for (int i = 0; i < _size; i++) {
            Ring pow2 = (Ring) _variables[i].get();
            if (pow2 == null)
                throw new FunctionException("Variable: " + _variables[i]
                        + " is not set");
            int n = _powers[i];
            while (n >= 1) { // Iteration.
                if ((n & 1) == 1) {
                    result = (result == null) ? pow2 : (Ring) result
                            .times(pow2);
                }
                pow2 = (Ring) pow2.times(pow2);
                n >>>= 1;
            }
        }
        return result;
    }

    private static final long serialVersionUID = 1L;
}