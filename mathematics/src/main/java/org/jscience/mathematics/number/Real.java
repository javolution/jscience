/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import java.io.IOException;

import javolution.lang.MathLib;
import javolution.lang.ValueType;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.internal.number.RealImpl;
import org.jscience.mathematics.structure.OrderedField;

/**
 * <p> A {@link Number number} (immutable) that represents a quantity 
 *     along a continuous line.</p>
 *     
 * <p> A real number is not always a 64 bits floating-point number; 
 *     {@link Decimal}, {@link Rational} and {@link Estimate} are also valid 
 *     {@link Real} numbers (subclasses).</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/Real_number">
 *      Wikipedia: Real Number</a>
 */
@DefaultTextFormat(Real.Text.class)
@DefaultXMLFormat(Real.XML.class)
public abstract class Real extends Number implements OrderedField<Real>,
		ValueType<Real> {

	/**
	 * Defines the default text format for real numbers (double precision).
	 */
	public static class Text extends TextFormat<Real> {

		@Override
		public Appendable format(Real that, final Appendable dest)
				throws IOException {
			return TypeFormat.format(that.doubleValue(), dest);
		}

		@Override
		public Real parse(CharSequence csq, Cursor cursor)
				throws IllegalArgumentException {
			return Real.of(TypeFormat.parseDouble(csq, cursor));
		}

	}

	/**
	 * Defines the default XML representation for real numbers 
	 * (double precision).
	 * [code]<Real value="1.234" />[/code]
	 */
	public static class XML extends XMLFormat<Real> {

		@Override
		public Real newInstance(Class<? extends Real> cls, InputElement xml)
				throws XMLStreamException {
			return Real.of(xml.getAttribute("value", Double.NaN));
		}

		@Override
		public void read(InputElement xml, Real that) throws XMLStreamException {
			// Do nothing, already read.
		}

		@Override
		public void write(Real that, OutputElement xml)
				throws XMLStreamException {
			xml.setAttribute("value", that.doubleValue());
		}
	}

	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * A real number holding zero.
	 */
	public static final Real ZERO = new RealImpl(0.0);

	/**
	 * A real number holding one.
	 */
	public static final Real ONE = new RealImpl(1.0);

	/**
	 * A real number holding Not-a-Number.
	 */
	public static final Real NaN = new RealImpl(Double.NaN);

	/**
	 * A real number holding the mathematical constant
	 * <a href="http://en.wikipedia.org/wiki/Pi">π</a>.
	 */
	public static final Real PI = new RealImpl(0.0);

	/**
	 * A real number holding the mathematical constant
	 * <a href="http://en.wikipedia.org/wiki/E_(mathematical_constant)">e</a>
	 * (Euler constant).
	 */
	public static final Real E = new RealImpl(0.0);

	/**
	 * Returns a real number (double precision) having the specified 
	 * textual representation.
	 *
	 * @param  csq the character sequence.
	 * @return <code>TextContext.getFormat(Real.class).parse(csq)</code>
	 * @throws IllegalArgumentException if the character sequence does not
	 *         contain a parsable number.
	 * @see    TextContext
	 */
	public static Real of(CharSequence csq) {
		return TextContext.getFormat(Real.class).parse(csq);
	}

	/**
	 * Returns the real number corresponding the specified 
	 * {@link #doubleValue double value}.
	 */
	public static Real of(double doubleValue) {
		return new RealImpl(doubleValue);
	}

	/**
	 * Returns the absolute value of this real number.
	 */
	public Real abs() {
		return isLessThan(ZERO) ? opposite() : this;
	}

	/**
	 * Compares this real number with the specified value for order.
	 *
	 * @param value the value to be compared with.
	 * @return a negative integer, zero, or a positive integer as this number
	 *        is less than, equal to, or greater than the specified value.
	 */
	public int compareTo(double value) {
		return compareTo(Real.of(value));
	}

	/**
	 * Returns the cosine of this real number.
	 */
	public abstract Real cosine();

	/**
	 * Returns this real number divided by the specified value.
	 */
	public Real divides(double value) {
		return divides(Real.of(value));
	}

	/**
	 * Returns this real number divided by the one specified.
	 */
	public Real divides(Real that) {
		return this.times(that.reciprocal());
	}

	/**
	 * Returns the representation of this real number as a {@code double}.	 
	 */
	public abstract double doubleValue();

	/**
	 * Indicates if this real number is approximatively equal to the 
	 * specified value given the specified tolerance.
	 *
	 * @param value the value to compare with.
	 * @param tolerance the tolerance value.
	 * @return <code>MathLib.abs(this.value - value) <= tolerance</code>.
	 */
	public boolean equals(double value, double tolerance) {
		return MathLib.abs(doubleValue() - value) <= tolerance;
	}

	/**
	 * Indicates if this object and the one specified represent the 
	 * same real numbers.
	 */
	@Override
	public abstract boolean equals(Object obj);

	/**
	 * Indicates if this real number is approximatively equal to the 
	 * one specified value given the specified tolerance.
	 *
	 * @param that the real number to compare with.
	 * @param tolerance the tolerance value.
	 * @return <code>MathLib.abs(this - that) <= tolerance</code>.
	 */
	public boolean equals(Real that, double tolerance) {
		return equals(that.doubleValue(), tolerance);
	}

	/**
	 * Returns the exponential number <i>e</i> raised to the power of this
	 * number.
	 */
	public abstract Real exp();

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public final int hashCode() {
		long bits = Double.doubleToLongBits(doubleValue());
		return (int) (bits ^ (bits >>> 32));
	}

	@Override
	public int intValue() {
		return (int) doubleValue();
	}

	/**
	 * Indicates if this real number is ordered after the one specified
	 * (convenience method).
	 *
	 * @param that the number to compare with.
	 * @return <code>this.compareTo(that) > 0</code>.
	 */
	public boolean isGreaterThan(Real that) {
		return this.compareTo(that) > 0;
	}

	/**
	 * Indicates if this number is infinite.
	 *
	 * @return <code>true</code> if this number is infinite;
	 *         <code>false</code> otherwise.
	 */
	public final boolean isInfinite() {
		return Double.isInfinite(doubleValue());
	}

	/**
	 * Indicates if the absolute value of this real number is greater than 
	 * the one specified.
	 *
	 * @param that the number to be compared with.
	 * @return <code>|this| > |that|</code>
	 * @see #abs()
	 */
	public boolean isLargerThan(Real that) {
		return this.abs().compareTo(that.abs()) > 0;
	}

	/**
	 * Indicates if this real number is ordered before that number
	 * (convenience method).
	 *
	 * @param that the number to compare with.
	 * @return <code>this.compareTo(that) < 0</code>.
	 */
	public boolean isLessThan(Real that) {
		return this.compareTo(that) < 0;
	}

	/**
	 * Indicates if this 64 bits floating point is not a number.
	 *
	 * @return <code>true</code> if this number is NaN;
	 *         <code>false</code> otherwise.
	 */
	public final boolean isNaN() {
		return Double.isNaN(doubleValue());
	}

	/**
	 * Returns the natural logarithm (base e) of this number.
	 *
	 * @return <code>log(this)</code>.
	 */
	public abstract Real log();

	@Override
	public long longValue() {
		return (long) doubleValue();
	}

	/**
	 * Returns this real number minus the specified value.
	 */
	public Real minus(double value) {
		return minus(Real.of(value));
	}

	/**
	 * Returns this real number minus the one specified.
	 */
	public Real minus(Real that) {
		return this.plus(that.opposite());
	}

	/**
	 * Returns this real number plus the specified value.
	 */
	public Real plus(double value) {
		return plus(Real.of(value));
	}

	/**
	 * Returns this real number raised to the specified power.
	 */
	public Real pow(double e) {
		return pow(Real.of(e));
	}

	/**
	 * Returns this real number raised to the power of the specified exponent.
	 */
	public abstract Real pow(Real that);

	/**
	 * Returns the closest integer value to this real number.
	 */
	public abstract LargeInteger round();

	/**
	 * Returns the sine of this real number.
	 */
	public abstract Real sine();

	/**
	 * Returns the positive square root of this real number.
	 */
	public abstract Real sqrt();

	/**
	 * Returns this real number multiplied by itself.
	 */
	public Real square() {
		return this.times(this);
	}

	/**
	 * Returns the tangent of this real number.
	 */
	public abstract Real tangent();

	/**
	 * Returns this real number multiplied by the specified value.
	 */
	public Real times(double value) {
		return times(Real.of(value));
	}

	/**
	 * Returns the textual representation of this number.
	 *
	 * @return <code>TextContext.getFormat(Real.class).format(this)</code>
	 * @see    TextContext
	 */
	public String toString() {
		return TextContext.getFormat(Real.class).format(this);
	}

	/**
	 * Returns the largest integer smaller or equal to this real number.
	 */
	public abstract LargeInteger trunc();

}
