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
import java.io.Serializable;

import javolution.lang.ValueType;
import javolution.text.CharSet;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.internal.number.ComplexImpl;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A value that can be expressed in the form {@code a + bi}, where 
 *     {@code a} and {@code b} are {@link Real real} numbers and {@code i}
 *     is the imaginary unit. In mathematical terms, the 
 *     <a href="http://en.wikipedia.org/wiki/Complex_plane">complex plane</a> 
 *     is identified with the Euclidean plane R<sup>2</sup>.</p>
 *     
 * <p> Complex numbers can be formed using arbitrary {@link Real} parts.
* [code]
 * // Traditional complex numbers (64 bits floating-point parts).
 * Complex c = Complex.of(12.4, -3.8);  
 *      
 * // Arbitrary precision complex numbers.
 * Complex cDecimal = Complex.of(Decimal.of("12.32"), Decimal.of("-3.8"));
 * 
 * // Complex exact arithmetics.
 * 
 * Complex cRational = Complex.of(Rational.of(12, 7), Rational.of(-1, 3));
 * 
 * // Complex interval arithmetics.
 * Complex cInterval = Complex.of(Interval.of(12.3, 12.7), Interval.of(-4.0, -3.7));
 * [/code]</p>
 *          
 * <p> It should be noted that even though complex numbers are called 
 *     'numbers' they are not {@link Number} in the Java sense. 
 *     They do not implement the {@link Comparable} interface and don't 
 *     form an ordered field.</p>
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 * @see <a href="http://en.wikipedia.org/wiki/Complex_number">
 *      Wikipedia: Complex Numbers</a>
 */
@DefaultTextFormat(Complex.Text.class)
@DefaultXMLFormat(Complex.XML.class)
public abstract class Complex implements Field<Complex>, ValueType<Complex>,
		Serializable {

	/**
	 * Defines the default text format for complex numbers.
	 */
	public static class Text extends TextFormat<Complex> {

		@Override
		public Appendable format(Complex that, final Appendable dest)
				throws IOException {
			double real = that.realValue();
			double imaginary = that.imaginaryValue();
			dest.append('(');
			TypeFormat.format(real, dest);
			if (imaginary < 0.0) {
				dest.append(" - ");
				TypeFormat.format(-imaginary, dest);
			} else {
				dest.append(" + ");
				TypeFormat.format(imaginary, dest);
			}
			return dest.append("i)");
		}

		@Override
		public Complex parse(CharSequence csq, Cursor cursor)
				throws IllegalArgumentException {
			// Skip parenthesis if any.
			boolean parenthesis = cursor.skip('(', csq);

			// Reads real part.
			double real = TypeFormat.parseDouble(csq, cursor);

			// Reads separator (possibly surrounded by whitespaces).
			cursor.skipAny(CharSet.WHITESPACES, csq);
			char op = cursor.nextChar(csq);
			if ((op != '+') && (op != '-'))
				throw new NumberFormatException("'+' or '-' expected");
			cursor.skipAny(CharSet.WHITESPACES, csq);

			// Reads imaginary part.
			double imaginary = TypeFormat.parseDouble(csq, cursor);
			if (!cursor.skip('i', csq))
				throw new NumberFormatException("'i' expected");

			// Skip closing parenthesis if required..
			if (parenthesis && !cursor.skip(')', csq))
				throw new NumberFormatException("Closing ')' expected");

			return Complex.of(real, op == '-' ? -imaginary : imaginary);
		}
	}

	/**
	 * Defines the default XML representation for 64 complex numbers.
	 * [code]
	 * <Complex real="1.234" imaginary="-3.4"/>[/code]
	 */
	public static class XML extends XMLFormat<Complex> {

		@Override
		public Complex newInstance(Class<? extends Complex> cls,
				InputElement xml) throws XMLStreamException {
			return Complex.of(xml.getAttribute("real", Double.NaN),
					xml.getAttribute("imaginary", Double.NaN));
		}

		@Override
		public void read(InputElement xml, Complex that)
				throws XMLStreamException {
			// Do nothing, already read.
		}

		@Override
		public void write(Complex that, OutputElement xml)
				throws XMLStreamException {
			xml.setAttribute("real", that.realValue());
			xml.setAttribute("imaginary", that.imaginaryValue());
		}
	}

	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * A constant holding zero.
	 */
	public static final Complex ZERO = new ComplexImpl(0.0, 0.0);

	/**
	 * A constant holding one.
	 */
	public static final Complex ONE = new ComplexImpl(1.0, 0.0);

	/**
	 * A constant holding the imaginary unit {@code i}.
	 */
	public static final Complex I = new ComplexImpl(0.0, 1.0);

	/**
	 * A constant holding Not-a-Number value.
	 */
	public static final Complex NaN = new ComplexImpl(Double.NaN, Double.NaN);

	/**
	 * Returns a complex number having the specified textual
	 * representation (Cartesian).
	 *
	 * @param  csq the character sequence.
	 * @return <code>TextContext.getFormat(Complex.class).parse(csq)</code>
	 * @throws IllegalArgumentException if the character sequence does not
	 *         contain a parsable number.
	 * @see    TextContext
	 */
	public static Complex of(CharSequence csq) {
		return TextContext.getFormat(Complex.class).parse(csq);
	}

	/**
	 * Returns a complex number having the specified real and 
	 * imaginary values.
	 *
	 * @param  real the real value of this complex number.
	 * @param  imaginary the imaginary value of this complex number.
	 * @return the corresponding complex number.
	 * @see    #real
	 * @see    #imag
	 */
	public static Complex of(double real, double imaginary) {
		return new ComplexImpl(real, imaginary);
	}

	/**
	 * Returns a complex number having the specified real and 
	 * imaginary parts.
	 *
	 * @param  real the real value of this complex number.
	 * @param  imag the imaginary value of this complex number.
	 * @return the corresponding complex number.
	 * @see    #real
	 * @see    #imag
	 */
	public static <R extends Real> Complex of(R real, R imaginary) {
		return null; // TODO
	}

	/**
	 * Returns the conjugate of this complex number.
	 */
	public abstract Complex conjugate();

	/**
	 * Returns the complex cos of this.
	 */
	public abstract Complex cosine();

	/**
	 * Returns this complex number divided by the one specified.
	 */
	public Complex divides(Complex that) {
		return this.times(that.reciprocal());
	}

	/**
	 * Returns this complex number divided by the specified divisor.
	 */
	public Complex divides(double divisor) {
		return this.divides(Complex.of(divisor, 0.0));
	}

	/**
	 * Indicates if two complexes are "sufficiently" alike to be considered
	 * equal.
	 *
	 * @param  that the complex to compare with.
	 * @param  tolerance the maximum magnitude of the difference between
	 *         them before they are considered <i>not</i> equal.
	 * @return <code>true</code> if they are considered equal;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean equals(Complex that, double tolerance);

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Complex))
			return false;
		Complex that = (Complex) obj;
		return this.real().equals(that.real())
				&& this.imaginary().equals(that.imaginary());
	}

	/**
	 * Returns the exponential number {@link Real#e e} raised to the power of
	 * this complex.
	 * Note: <code><i><b>e</b></i><sup><font size=+0><b>PI</b>*<i><b>i
	 * </b></i></font></sup> = -1</code>
	 *
	 * @return  <code>exp(this)</code>.
	 */
	public abstract Complex exp();

	@Override
	public int hashCode() {
		long r = Double.doubleToLongBits(realValue());
		long i = Double.doubleToLongBits(imaginaryValue());		
		return (int) ((r ^ (r >>> 32)) + (i ^ (i >>> 32)));
	}

	/**
	 * Returns the imaginary part of this complex number.
	 */
	public abstract Real imaginary();

	/**
	 * Returns the value  as {@code double} of the imaginary part of this complex number.
	 */
	public abstract double imaginaryValue();

	/**
	 * Indicates if either the real or imaginary component of this complex
	 * is infinite.
	 *
	 * @return  <code>true</code> if this complex is infinite;
	 *          <code>false</code> otherwise.
	 */
	public abstract boolean isInfinite();

	/**
	 * Indicates if either the real or imaginary component of this complex
	 * is not a number.
	 *
	 * @return  <code>true</code> if this complex is NaN;
	 *          <code>false</code> otherwise.
	 */
	public abstract boolean isNaN();

	/**
	 * Returns the principal natural logarithm (base e) of this complex.
	 * Note: There are an infinity of solutions.
	 *
	 * @return  <code>log(this)</code>.
	 */
	public abstract Complex log();

	/**
	 * Returns this complex number minus the one specified.
	 */
	public Complex minus(Complex that) {
		return this.plus(that.opposite());
	}

	/**
	 * Returns the norm or 
	 * <a href="http://en.wikipedia.org/wiki/Absolute_value#Complex_numbers">
	 * absolute value</a> of this complex number.
	 */
	public abstract Real norm();

	/**
	 * Returns the phase or 
	 * <a href="http://en.wikipedia.org/wiki/Argument_(complex_analysis)">
	 * argument</a> of this complex number (range -π to π).
	 */
	public abstract Real phase();

	/**
	 * Returns this complex raised to the power of the specified complex
	 * exponent.
	 */
	public abstract Complex pow(Complex that);

	/**
	 * Returns this complex raised to the specified power.
	 */
	public Complex pow(double e) {
		return pow(Complex.of(e, 0));
	}

	/**
	 * Returns the real part of this complex number.
	 */
	public abstract Real real();

	/**
	 * Returns the value as {@code double} of the real part of this complex number.
	 */
	public abstract double realValue();

	/**
	 * Returns the complex sine of this.
	 */
	public abstract Complex sine();

	/**
	 * Returns one of the two square root of this complex number.
	 */
	public abstract Complex sqrt();

	/**
	 * Returns this complex number multiplied by itself.
	 */
	public Complex square() {
		return this.times(this);
	}

	/**
	 * Returns the complex tangent of this.
	 */
	public abstract Complex tangent();

	/**
	 * Returns this complex number multiplied by the specified factor.
	 */
	public Complex times(double factor) {
		return this.times(Complex.of(factor, 0.0));
	}

	/**
	 * Returns the textual representation of this complex number.
	 *
	 * @return <code>TextContext.getFormat(Complex.class).format(this)</code>
	 * @see    TextContext
	 */
	public String toString() {
		return TextContext.getFormat(Complex.class).format(this);
	}

}
