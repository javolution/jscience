/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.number;

import javolution.lang.MathLib;

import org.jscience.mathematics.number.Real;

/**
 * Float64 default implementation.
 */
public final class RealImpl extends Real {

	private static final long serialVersionUID = 0x500L; // Version.
	private double doubleValue;
	
	public RealImpl(double doubleValue) {
		this.doubleValue = doubleValue;
	}
	
	@Override
	public RealImpl divide(double value) {
		return new RealImpl(this.doubleValue / value);
	}

	@Override
	public double doubleValue() {
		return doubleValue;
	}

	@Override
	public Real exp() {
		return new RealImpl(MathLib.exp(doubleValue));
	}

	@Override
	public Real reciprocal() {
		return new RealImpl(1.0 / doubleValue);
	}

	@Override
	public Real log() {
		return new RealImpl(MathLib.log(doubleValue));
	}

	@Override
	public Real minus(double value) {
		return new RealImpl(this.doubleValue - value);
	}

	@Override
	public Real opposite() {
		return new RealImpl(-doubleValue);
	}

	@Override
	public Real plus(double value) {
		return new RealImpl(this.doubleValue + value);
	}

	@Override
	public Real pow(double e) {
		return new RealImpl(MathLib.pow(this.doubleValue, e));
	}

	@Override
	public Real sqrt() {
		return new RealImpl(MathLib.sqrt(doubleValue));
	}

	@Override
	public Real times(double value) {
		return new RealImpl(this.doubleValue * value);
	}

}
