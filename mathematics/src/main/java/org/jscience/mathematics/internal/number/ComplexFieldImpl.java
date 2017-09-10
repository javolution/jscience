/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.number;

import org.jscience.mathematics.number.ComplexField;
import org.jscience.mathematics.number.NumberField;

/**
 * Default generic complex implementation.
 */
public final class ComplexFieldImpl<N extends NumberField<N>> extends ComplexField<N> {

	private static final long serialVersionUID = 0x500L; // Version.
	N real, imag;

	public ComplexFieldImpl(N real, N imag) {
		this.real = real;
		this.imag = imag;
	}

	@Override
	public ComplexFieldImpl<N> conjugate() {
		return new ComplexFieldImpl<N>(real, imag.opposite());
	}

	@Override
	public ComplexFieldImpl<N> divide(long n) {
		return new ComplexFieldImpl<N>(real.divide(n), imag.divide(n));
	}

	@Override
	public N getImaginary() {
		return imag;
	}

	@Override
	public N getReal() {
		return real;
	}

	@Override
	public ComplexFieldImpl<N> reciprocal() {
		N tmp = (this.real.square()).plus(this.imag.square());
		return new ComplexFieldImpl<N>(this.real.divide(tmp), this.imag.divide(tmp)
				.opposite());
	}

	@Override
	public ComplexFieldImpl<N> opposite() {
		return new ComplexFieldImpl<N>(real.opposite(), imag.opposite());
	}

	@Override
	public ComplexFieldImpl<N> plus(ComplexField<N> that) {
		return new ComplexFieldImpl<N>(this.real.plus(that.getReal()),
				this.imag.plus(that.getImaginary()));
	}

	@Override
	public ComplexFieldImpl<N> times(ComplexField<N> that) {
		return new ComplexFieldImpl<N>(this.real.times(that.getReal()).minus(
				this.imag.times(that.getImaginary())), this.real.times(
				that.getImaginary()).plus(
				this.getImaginary().times(that.getReal())));
	}

	@Override
	public ComplexField<N> times(long n) {
		return new ComplexFieldImpl<N>(real.times(n), imag.times(n));
	}

	@Override
	public ComplexFieldImpl<N> value() {
		return this;
	}

}
