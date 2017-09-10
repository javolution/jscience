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
import java.util.Iterator;
import java.util.List;

import org.jscience.mathematics.structure.GroupAdditive;
import org.jscience.mathematics.structure.GroupMultiplicative;

import javolution.context.LocalContext;
import javolution.context.ObjectFactory;
import javolution.util.FastList;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * <p> This abstract class represents a mapping between two sets such that
 *     there is a unique element in the second set assigned to each element
 *     in the first set.</p>
 *     
 * <p> Functions can be discrete or continuous and multivariate functions 
 *     (functions with multiple variables) are also supported as illustrated 
 *     below:[code]
 *         // Defines local variables.
 *         Variable.Local<Rational> varX = new Variable.Local<Rational>("x");
 *         Variable.Local<Rational> varY = new Variable.Local<Rational>("y");
 *         
 *         // f(x, y) =  x² + x·y + 1;
 *         Polynomial<Rational> x = Polynomial.valueOf(Rational.ONE, varX);
 *         Polynomial<Rational> y = Polynomial.valueOf(Rational.ONE, varY);
 *         Polynomial<Rational> fx_y = x.pow(2).plus(x.times(y)).plus(Rational.ONE);
 *         System.out.println("f(x,y) = " + fx_y);
 * 
 *         // Evaluates f(1,0) 
 *         System.out.println("f(1,0) = " + fx_y.evaluate(Rational.ONE, Rational.ZERO));
 * 
 *         // Calculates df(x,y)/dx
 *         System.out.println("df(x,y)/dx = " + fx_y.differentiate(varX));
 *          
 *         > f(x,y) = [1/1]x^2 + [1/1]xy + [1/1]
 *         > f(1,0) = 2/1
 *         > df(x,y)/dx = [2/1]x + [1/1]y
 *     [/code]</p>
 *     
 * <p> Functions are often given by formula (e.g. <code>f(x) = x²-x+1,
 *     f(x,y)= x·y</code>) but the general function instance might tabulate
 *     the values, solve an equation, etc.</p>
 *     
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 1, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Function_%28mathematics%29">
 *      Wikipedia: Functions (mathematics)</a>
 */
public abstract class Function<X, Y> implements Serializable, Realtime  {

    // TODO: Implements XMLSerializable.
        
    /**
     * Default constructor. 
     */
    protected Function() {
    }

    /**
     * Returns a lexically ordered list of the variables (or arguments)
     * for this function (empty list for constant functions).
     * 
     * @return this function current unset variables (sorted).
     */
    public abstract List<Variable<X>> getVariables();

    /**
     * Evaluates this function using its {@link Variable variables} current
     * values.
     *
     * @return the evaluation of this function.
     * @throws FunctionException if any of this function's variable is not set.
     */
    public abstract Y evaluate();

    /**
     * Indicates if this function is equals to the specified object.
     *
     * @param obj the object to be compared with.
     * @return <code>true</code> if this function and the specified argument
     *         represent the same function; <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Returns the hash code for this function (consistent with 
     * {@link #equals(Object)}.
     *
     * @return this function hash code.
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Retrieves the variable from this function having the specified 
     * symbol (convenience method).
     *
     * @return the variable having the specified symbol or <code>null</code>
     *         if none.
     */
    public final Variable<X> getVariable(String symbol) {
        for (Variable<X> v : this.getVariables()) {
            if (symbol.equals(v.getSymbol()))
                return v;
        }
        return null;
    }

    /**
     * Evaluates this function for the specified argument value
     * (convenience method). The evaluation is performed 
     * in a {@link javolution.context.LocalContext LocalContext} and 
     * can safely be called upon functions with {@link Variable.Global global
     * variables}.
     *
     * @param arg the single variable value used for the evaluation.
     * @return the evaluation of this function.
     * @throws FunctionException if <code>getVariables().size() != 1</code> 
     */
    public final Y evaluate(X arg) {
        List<Variable<X>> vars = getVariables();
        if (vars.size() != 1)
            throw new FunctionException("This function is not monovariate");
        Variable<X> x = vars.get(0);
        X prev = x.get();
        LocalContext.enter();
        try {
            x.set(arg);
            return evaluate();
        } finally {
            x.set(prev);
            LocalContext.exit();
        }
    }

    /**
     * Evaluates this function for the specified arguments values
     * (convenience method). The evaluation is performed 
     * in a {@link javolution.context.LocalContext LocalContext} and 
     * can safely be called upon functions with {@link Variable.Global global
     * variables}.
     *
     * @param args the variables values used for the evaluation.
     * @return the evaluation of this function.
     * @throws IllegalArgumentException 
     *         if <code>args.length != getVariables().size())</code> 
     */
    public final Y evaluate(X... args) {
        List<Variable<X>> vars = getVariables();
        if (vars.size() != args.length)
            throw new IllegalArgumentException("Found " + args.length
                    + " arguments, but " + vars.size() + "required");
        LocalContext.enter();
        try {
            return evaluate(args, vars, 0);
        } finally {
            LocalContext.exit();
        }
    }

    private final Y evaluate(X[] args, List<Variable<X>> vars, int i) {
        if (i < args.length) {
            Variable<X> var = vars.get(i);
            X prev = var.get();
            var.set(args[i]);
            try {
                return evaluate(args, vars, i + 1);
            } finally {
                var.set(prev); // Restores previous variable value.
            }
        } else {
            return evaluate();
        }
    }

    /**
     * Returns the composition of this function with the one specified.
     *
     * @param  that the function for which the return value is passed as
     *         argument to this function.
     * @return the function <code>(this o that)</code>
     * @throws FunctionException if this function is not monovariate.
     */
    public <Z> Function<Z, Y> compose(Function<Z, X> that) {
        if (getVariables().size() != 1)
            throw new FunctionException("This function is not monovariate");
        return Compose.newInstance(this, that);
    }

    /**
     * Returns the first derivative of this function with respect to 
     * the specified variable. 
     * 
     * @param  v the variable for which the derivative is calculated.
     * @return <code>d[this]/dv</code>
     * @see <a href="http://mathworld.wolfram.com/Derivative.html">
     *      Derivative -- from MathWorld</a>
     * @throws FunctionException if the derivative is undefined.
     */
    public Function<X, Y> differentiate(Variable<X> v) {
        return Derivative.newInstance(this, v);
    }

    /**
     * Returns an integral of this function with respect to 
     * the specified variable. 
     * 
     * @param  v the variable for which the integral is calculated.
     * @return <code>S[this·dv]</code>
     * @see <a href="http://mathworld.wolfram.com/Integral.html">
     *      Integral -- from MathWorld</a>
     */
    public Function<X, Y> integrate(Variable<X> v) {
        return Integral.newInstance(this, v);
    }

    /**
     * Returns the sum of this function with the one specified.
     *
     * @param  that the function to be added.
     * @return <code>this + that</code>.
     */
    public Function<X, Y> plus(Function<X, Y> that) {
        return Plus.newInstance(this, that);
    }

    /**
     * Returns the difference of this function with the one specified.
     *
     * @param  that the function to be subtracted.
     * @return <code>this - that</code>.
     */
    @SuppressWarnings("unchecked")
    public Function<X, Y> minus(Function<X, Y> that) {
        if (that instanceof GroupAdditive) {
            Function thatOpposite = (Function) ((GroupAdditive) that)
                    .opposite();
            return this.plus(thatOpposite);
        }
        return Minus.newInstance(this, that);
    }

    /**
     * Returns the product of this function with the one specified.
     *
     * @param  that the function multiplier.
     * @return <code>this · that</code>.
     */
    public Function<X, Y> times(Function<X, Y> that) {
        return Times.newInstance(this, that);
    }

    /**
     * Returns the quotient of this function with the one specified.
     * Evaluation of this function may raise an exception if the 
     * function result is not a {
     *
     * @param  that the function divisor.
     * @return <code>this / that</code>.
     */
    @SuppressWarnings("unchecked")
    public Function<X, Y> divide(Function<X, Y> that) {
        if (that instanceof GroupMultiplicative) {
            Function thatInverse = (Function) ((GroupMultiplicative) that)
                    .reciprocal();
            return this.times(thatInverse);
        }
        return Divide.newInstance(this, that);
    }

    /**
     * Returns this function raised at the specified exponent.
     *
     * @param  n the exponent.
     * @return <code>this<sup>n</sup></code>
     * @throws IllegalArgumentException if <code>n &lt;= 0</code>
     */
    public Function<X, Y> pow(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n: " + n
                    + " zero or negative values not allowed");
        Function<X, Y> pow2 = this;
        Function<X, Y> result = null;
        while (n >= 1) { // Iteration.
            if ((n & 1) == 1) {
                result = (result == null) ? pow2 : result.times(pow2);
            }
            pow2 = pow2.times(pow2);
            n >>>= 1;
        }
        return result;
    }
    /**
     * Returns the textual representation of this real-time object
     * (equivalent to <code>toString</code> except that the returned value
     * can be allocated from the local context space).
     * 
     * @return this object's textual representation.
     */
    public abstract Text toText();

    /**
     * Returns the text representation of this function as a 
     * <code>java.lang.String</code>.
     * 
     * @return <code>toText().toString()</code>
     */
    public final String toString() {
        return toText().toString();
    }
    
    // Merges the variable from the specified function into a single table.
    @SuppressWarnings("unchecked")
    static final List merge(List left, List right) {
        if (left.containsAll(right))
            return left;
        if (right.containsAll(left))
            return right;
        FastList result = FastList.newInstance();
        Iterator iLeft = left.iterator();
        Iterator iRight = right.iterator();
        Variable l = null;
        Variable r = null;
        while (true) {
            if (!iLeft.hasNext()) {
                while (iRight.hasNext()) {
                    result.add(iRight.next());
                }
                return result;
            }
            if (!iRight.hasNext()) {
                while (iLeft.hasNext()) {
                    result.add(iLeft.next());
                }
                return result;
            }
            l = (l == null) ? (Variable) iLeft.next() : l;
            r = (r == null) ? (Variable) iRight.next() : r;
            if (l == r) {
                result.add(l);
                l = null;
                r = null;
                continue;
            }
            int comp = l.getSymbol().compareTo(r.getSymbol());
            if (comp < 0) {
                result.add(l);
                l = null;
                continue;
            }
            if (comp > 0) {
                result.add(r);
                r = null;
                continue;
            }
            throw new FunctionException("Duplicate symbol " + l.getSymbol());
        }
    }

    // Function composition (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Compose extends Function {

        private static final ObjectFactory<Compose> FACTORY = new ObjectFactory<Compose>() {

            protected Compose create() {
                return new Compose();
            }

            protected void cleanup(Compose compose) {
                compose._f = null;
                compose._g = null;
            }
        };

        private Function _f;

        private Function _g;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Function g) {
            Compose compose = FACTORY.object();
            compose._f = f;
            compose._g = g;
            return compose;
        }

        @Override
        public List getVariables() {
            return _g.getVariables();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object evaluate() {
            return evaluate(_g.evaluate());
        }

        @SuppressWarnings("unchecked")
        public Function differentiate(Variable v) {
            // Chain rule: http://en.wikipedia.org/wiki/Chain_rule
            Function fd = _f.differentiate(v);
            Function gd = _g.differentiate(v);
            return fd.compose(_g).times(gd);
        }

        public Text toText() {
            return TextBuilder.newInstance().append('(').append(_f).append(')')
                    .append('o').append('(').append(_g).append(')').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Compose))
                return false;
            Compose that = (Compose) obj;
            return this._f.equals(that._f) && this._g.equals(that._g);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _g.hashCode();
        }

        private static final long serialVersionUID = 1L;

    }

    // Function derivative (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Derivative extends Function {

        private static final ObjectFactory<Derivative> FACTORY = new ObjectFactory<Derivative>() {

            protected Derivative create() {
                return new Derivative();
            }

            protected void cleanup(Derivative derivative) {
                derivative._f = null;
                derivative._v = null;
            }
        };

        private Function _f;

        private Variable _v;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Variable v) {
            Derivative derivative = FACTORY.object();
            derivative._f = f;
            derivative._v = v;
            return derivative;
        }

        @Override
        public List getVariables() {
            return _f.getVariables();
        }

        @Override
        public Object evaluate() {
            throw new FunctionException("Derivative of " + _f + " undefined");
        }

        public Text toText() {
            return TextBuilder.newInstance().append("d[").append(_f).append("]/d")
            .append(_v.getSymbol()).toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Derivative))
                return false;
            Derivative that = (Derivative) obj;
            return this._f.equals(that._f) && this._v.equals(that._v);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _v.hashCode();
        }

        private static final long serialVersionUID = 1L;
    }

    // Function integral (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Integral extends Function {

        private static final ObjectFactory<Integral> FACTORY = new ObjectFactory<Integral>() {

            protected Integral create() {
                return new Integral();
            }

            protected void cleanup(Integral integral) {
                integral._f = null;
                integral._v = null;
            }
        };

        private Function _f;

        private Variable _v;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Variable v) {
            Integral integral = FACTORY.object();
            integral._f = f;
            integral._v = v;
            return integral;
        }

        @Override
        public List getVariables() {
            return _f.getVariables();
        }

        @Override
        public Object evaluate() {
            throw new FunctionException("Integral of " + _f + " undefined");
        }

        public Text toText() {
            return TextBuilder.newInstance().append("S[").append(_f).append("·d")
            .append(_v.getSymbol()).append(']').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Integral))
                return false;
            Integral that = (Integral) obj;
            return this._f.equals(that._f) && this._v.equals(that._v);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _v.hashCode();
        }

        private static final long serialVersionUID = 1L;
    }

    // Function addition (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Plus extends Function {

        private static final ObjectFactory<Plus> FACTORY = new ObjectFactory<Plus>() {

            protected Plus create() {
                return new Plus();
            }

            protected void cleanup(Plus plus) {
                plus._f = null;
                plus._g = null;
            }
        };

        private Function _f, _g;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Function g) {
            Plus plus = FACTORY.object();
            plus._f = f;
            plus._g = g;
            return plus;
        }

        @Override
        public List getVariables() {
            return merge(_f.getVariables(), _g.getVariables());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object evaluate() {
            Object y2 = _g.evaluate();
            Object y1 = _f.evaluate();
            if (!(y1 instanceof GroupAdditive))
                throw new FunctionException(y1.getClass()
                        + " is not an additive group");

            return ((GroupAdditive) y1).plus(y2);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function differentiate(Variable v) {
            return _f.differentiate(v).plus(_g.differentiate(v));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function integrate(Variable v) {
            return _f.integrate(v).plus(_g.integrate(v));
        }

        public Text toText() {
            return TextBuilder.newInstance().append('(').append(_f).append(")")
            .append('+').append('(').append(_g).append(')').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Plus))
                return false;
            Plus that = (Plus) obj;
            return this._f.equals(that._f) && this._g.equals(that._g);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _g.hashCode();
        }
        
        private static final long serialVersionUID = 1L;
    }

    // Function addition (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Minus extends Function {

        private static final ObjectFactory<Minus> FACTORY = new ObjectFactory<Minus>() {

            protected Minus create() {
                return new Minus();
            }

            protected void cleanup(Minus minus) {
                minus._f = null;
                minus._g = null;
            }
        };

        private Function _f, _g;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Function g) {
            Minus minus = FACTORY.object();
            minus._f = f;
            minus._g = g;
            return minus;
        }

        @Override
        public List getVariables() {
            return merge(_f.getVariables(), _g.getVariables());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object evaluate() {
            Object y2 = _g.evaluate();
            if (!(y2 instanceof GroupAdditive))
                throw new FunctionException(y2.getClass()
                        + " is not an additive group");
            y2 = ((GroupAdditive) y2).opposite();

            Object y1 = _f.evaluate();
            if (!(y1 instanceof GroupAdditive))
                throw new FunctionException(y1.getClass()
                        + " is not an additive group");

            return ((GroupAdditive) y1).plus(y2);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function differentiate(Variable v) {
            return _f.differentiate(v).minus(_g.differentiate(v));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function integrate(Variable v) {
            return _f.integrate(v).minus(_g.integrate(v));
        }

        public Text toText() {
            return TextBuilder.newInstance().append('(').append(_f).append(")")
            .append('-').append('(').append(_g).append(')').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Minus))
                return false;
            Minus that = (Minus) obj;
            return this._f.equals(that._f) && this._g.equals(that._g);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _g.hashCode();
        }
        
        private static final long serialVersionUID = 1L;
    }

    // Function multiplication (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Times extends Function {

        private static final ObjectFactory<Times> FACTORY = new ObjectFactory<Times>() {

            protected Times create() {
                return new Times();
            }

            protected void cleanup(Times times) {
                times._f = null;
                times._g = null;
            }
        };

        private Function _f, _g;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Function g) {
            Times times = FACTORY.object();
            times._f = f;
            times._g = g;
            return times;
        }

        @Override
        public List getVariables() {
            return merge(_f.getVariables(), _g.getVariables());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object evaluate() {
            Object y2 = _g.evaluate();
            Object y1 = _f.evaluate();
            if (!(y1 instanceof GroupMultiplicative))
                throw new FunctionException(y1.getClass()
                        + " is not a multiplicative group");

            return ((GroupMultiplicative) y1).times(y2);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function differentiate(Variable v) {
            // Product rule: http://en.wikipedia.org/wiki/Product_rule
            // (support for non-commutative multiplications).
            // r' = d(f·g) = f'g + fg'
            Function fd = _f.differentiate(v);
            Function gd = _g.differentiate(v);
            return fd.times(_g).plus(_f.times(gd));
        }

        public Text toText() {
            return TextBuilder.newInstance().append('(').append(_f).append(")")
            .append('·').append('(').append(_g).append(')').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Times))
                return false;
            Times that = (Times) obj;
            return this._f.equals(that._f) && this._g.equals(that._g);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _g.hashCode();
        }
        
        private static final long serialVersionUID = 1L;
    }

    // Function multiplication (default implementation).
    @SuppressWarnings("unchecked")
    private static final class Divide extends Function {

        private static final ObjectFactory<Divide> FACTORY = new ObjectFactory<Divide>() {

            protected Divide create() {
                return new Divide();
            }

            protected void cleanup(Divide divide) {
                divide._f = null;
                divide._g = null;
            }
        };

        private Function _f, _g;

        @SuppressWarnings("unchecked")
        public static <X, Y> Function<X, Y> newInstance(Function f, Function g) {
            Divide divide = FACTORY.object();
            divide._f = f;
            divide._g = g;
            return divide;
        }

        @Override
        public List getVariables() {
            return merge(_f.getVariables(), _g.getVariables());
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object evaluate() {
            Object y2 = _g.evaluate();
            if (!(y2 instanceof GroupMultiplicative))
                throw new FunctionException(y2.getClass()
                        + " is not a multiplicative group");
            y2 = ((GroupMultiplicative) y2).reciprocal();
            Object y1 = _f.evaluate();
            if (!(y1 instanceof GroupMultiplicative))
                throw new FunctionException(y1.getClass()
                        + " is not a multiplicative group");

            return ((GroupMultiplicative) y1).times(y2);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function differentiate(Variable v) {
            // Quotient rule: http://en.wikipedia.org/wiki/Quotient_rule
            // with support for non-commutative multiplications.
            // r = f/g,  rg = f, r'g + rg' = f' (produt rule)
            // r' = (f' - rg')/g, r' = (f' - (f/g)g')/g
            Function fd = _f.differentiate(v);
            Function gd = _g.differentiate(v);
            return fd.minus(_f.divide(_g).times(gd)).divide(_g);
        }

        public Text toText() {
            return TextBuilder.newInstance().append('(').append(_f).append(")")
            .append('/').append('(').append(_g).append(')').toText();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Divide))
                return false;
            Divide that = (Divide) obj;
            return this._f.equals(that._f) && this._g.equals(that._g);
        }

        @Override
        public int hashCode() {
            return _f.hashCode() + _g.hashCode();
        }

        private static final long serialVersionUID = 1L;
    }
}