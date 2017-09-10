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
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.internal.number.ComplexFieldImpl;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A number with lower and upper bound.</p>
 * 
 * <p> It should be noted that even though complex numbers are called 
 *     'numbers' they are neither {@link Number} nor {@link NumberField} 
 *     in the Java sense. They do not implement the {@link Comparable} 
 *     interface and don't form an ordered field.</p> 
 * 
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, January 26, 2014
 */
@DefaultTextFormat(Estimate.Text.class)
@DefaultXMLFormat(Estimate.XML.class)
public abstract class Estimate<N extends NumberField<N>> implements
		Field<Estimate<N>>, ValueType<Estimate<N>>, Serializable {

	/**
	 * Defines the default text format for all complex numbers 
	 * (parsing not supported). 
	 */
	public static class Text extends TextFormat<Estimate<?>> {

		@SuppressWarnings("rawtypes")
		@Override
		public Appendable format(Estimate<?> that, final Appendable dest)
				throws IOException {
			NumberField real = that.getReal();
			NumberField imag = that.getImaginary();
			dest.append('(');
			TextContext.getFormat(real.getClass()).format(real, dest);
			if (imag.doubleValue() < 0.0) {
				dest.append(" - ");
				TextContext.getFormat(imag.getClass()).format(imag, dest);
			} else {
				dest.append(" + ");
				TextContext.getFormat(imag.getClass()).format(imag, dest);
			}
			return dest.append("i)");
		}

		@Override
		public Estimate<?> parse(CharSequence csq, Cursor cursor)
				throws IllegalArgumentException {
			throw new UnsupportedOperationException(
					"Parsing of generic Complex instance");
		}
	}

	/**
	 * Defines the default XML representation for complex numbers.
	 * [code]
	 * <Complex>
	 *    <Real><Decimal value="23.34"/></Real>
	 *    <Imaginary><Decimal value="23.34"/></Imaginary>
	 * </Complex>[/code]
	 */
	public static class XML extends XMLFormat<Estimate<?>> {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Estimate<?> newInstance(Class<? extends Estimate<?>> cls,
				InputElement xml) throws XMLStreamException {
			NumberField real = xml.get("Real");
			NumberField imag = xml.get("Imaginary");
			return Estimate.of(real, imag);
		}

		@Override
		public void read(InputElement xml, Estimate<?> that)
				throws XMLStreamException {
			// Do nothing, already read.
		}

		@Override
		public void write(Estimate<?> that, OutputElement xml)
				throws XMLStreamException {
			xml.add(that.getReal(), "Real");
			xml.add(that.getImaginary(), "Imaginary");
		}
	}

	private static final long serialVersionUID = 0x500L; // Version.

	/**
	 * Returns the complex number having the specified real and imaginary
	 * components.
	 *
	 * @param  real the real component of this complex number.
	 * @param  imaginary the imaginary component of this complex number.
	 * @return the corresponding complex number.
	 * @see    #getReal
	 * @see    #getImaginary
	 */
	public static <N extends NumberField<N>> Estimate<N> of(N real, N imaginary) {
		return new ComplexFieldImpl<N>(real, imaginary);
	}

	/**
	 * Returns the conjugate of this complex number.
	 *
	 * @return <code>(this.real(), - this.imaginary())</code>.
	 */
	public abstract Estimate<N> conjugate();

	/**
	 * Returns this complex number divided by the one specified.
	 *
	 * @param  that the divisor
	 * @return <code>this / that</code>
	 */
	public Estimate<N> divide(Estimate<N> that) {
		return this.times(that.reciprocal());
	}

	/**
	 * Returns this complex number divided by the specified divisor.
	 *
	 * @param  n the divisor.
	 * @return <code>this / n</code>
	 */
	public abstract Estimate<N> divide(long n);

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Estimate))
			return false;
		Estimate<?> that = (Estimate<?>) obj;
		return this.getReal().equals(that.getReal())
				&& this.getImaginary().equals(that.getImaginary());
	}

	/**
	 * Returns the imaginary component of this complex number.
	 *
	 * @return the imaginary component.
	 */
	public abstract N getImaginary();

	/**
	 * Returns the real component of this complex number.
	 *
	 * @return the real component.
	 */
	public abstract N getReal();

	@Override
	public int hashCode() {
		return getReal().hashCode() + getImaginary().hashCode();
	}

	/**
	 * Returns this complex number minus the one specified.
	 *
	 * @param that the complex number to subtract.
	 * @return <code>this - that</code>.
	 */
	public Estimate<N> minus(Estimate<N> that) {
		return this.plus(that.opposite());
	}

	/**
	 * Returns this complex number raised at the specified exponent 
	 * (which can be negative).
	 *
	 * @param  n the exponent.
	 * @return <code>this<sup>n</sup></code>
	 */
	public Estimate<N> pow(long n) {
		final Estimate<N> that = this.value();
		if (n <= 0) {
			if (n == 0)
				return that.divide(n);
			if (n == Long.MIN_VALUE) // Negative would overflow
				return that.pow(n + 1).divide(that);
			return that.pow(-n).reciprocal();
		}
		if (n == 1)
			return that;
		if (n == 2)
			return that.square();
		if (n == 3)
			return that.square().times(that);
		long half = n >> 1;
		return this.pow(half).times(this.pow(n - half));
	}

	/**
	 * Returns this number multiplied by itself.
	 *
	 * @param  that the divisor
	 * @return <code>this * that</code>
	 */
	public Estimate<N> square() {
		return this.times(this.value());
	}

	/**
	 * Returns this complex number multiplied by the specified factor.
	 *
	 * @param  n the multiplier.
	 * @return <code>this * n</code>
	 */
	public abstract Estimate<N> times(long n);

	/**
	 * Returns the textual representation of this number.
	 *
	 * @return <code>TextContext.getFormat(Float64.class).format(this)</code>
	 * @see    TextContext
	 */
	public String toString() {
		return TextContext.getFormat(Estimate.class).format(this);
	}

}
