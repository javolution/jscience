/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.number;

import java.io.IOException;
import java.math.BigDecimal;

import org.jscience.mathematics.number.Complex;
import org.jscience.mathematics.number.Complex64;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.structure.Field;

import javolution.lang.MathLib;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;
import javolution.context.ObjectFactory;
import javolution.text.CharSet;
import javolution.text.Cursor;

/**
 * Default 64 bits complex number implementation.
 */
public final class ComplexImpl extends Complex {

    private double real, imag;

    public ComplexImpl(double real, double imaginary) {
        this.real = real;
        this.imag = imaginary;
    }
    private static Complex64Impl of(Complex<Float64> that) {
    	if (that instanceof Complex64Impl)
    }

    @Override
    public boolean isInfinite() {
        return Double.isInfinite(real) | Double.isInfinite(imag);
    }

    @Override
    public boolean isNaN() {
        return Double.isNaN(real) | Double.isNaN(imag);
    }

    @Override
    public double real() {
        return this.real;
    }

    @Override
    public double imaginary() {
        return this.imag;
    }

    @Override
    public Complex64Impl times(double k) {
        return new Complex64Impl(real * k, imag * k);
    }

    @Override
    public Complex64Impl divide(double k) {
        return new Complex64Impl(real / k, imag / k);
    }

    @Override
    public Complex64Impl conjugate() {
        return new Complex64Impl(real, -imag);
    }
    
    @Override
    public double magnitude() {
        return MathLib.sqrt(real * real + imag * imag);
    }

    @Override
    public double argument() {
        return MathLib.atan2(imag, real);
    }

    @Override
    public Complex64Impl sqrt() {
        double m = MathLib.sqrt(this.magnitude());
        double a = this.argument() / 2.0;
        return new Complex64Impl(m * MathLib.cos(a), m * MathLib.sin(a));
    }

    @Override
    public Complex64Impl exp() {
        double m = MathLib.exp(real);
        return new Complex64Impl(m * MathLib.cos(imag), m * MathLib.sin(imag));
    }

    @Override
    public Complex64Impl log() {
        return new Complex64Impl(MathLib.log(magnitude()), argument());
    }

    @Override
    public Complex64Impl pow(double e) {
        double m = MathLib.pow(this.magnitude(), e);
        double a = this.argument() * e;
        return new Complex64Impl(m * MathLib.cos(a), m * MathLib.sin(a));
    }

    @Override
    public Complex64Impl pow(Complex64 that) {
        double r1 = MathLib.log(this.magnitude());
        double i1 = this.argument();
        double r2 = (r1 * that.real()) - (i1 * that.imaginary());
        double i2 = (r1 * that.imaginary()) + (i1 * that.real());
        double m = MathLib.exp(r2);
        return new Complex64Impl(m * MathLib.cos(i2), m * MathLib.sin(i2));
    }

    @Override
    public boolean equals(Complex64 that, double tolerance) {
        return MathLib.abs(this.minus(that).magnitude()) <= tolerance;
    }

    @Override
    public Complex64Impl opposite() {
        return Complex64Impl(-real, -imag);
    }

    @Override
    public Complex64Impl plus(Complex<Float64> that) {
        return Complex64Impl.valueOf(this.real + that._real, this._imaginary + that._imaginary);
    }

    @Override
    public Complex64Impl minus(Complex64Impl that) {
        return Complex64Impl.valueOf(this._real - that._real, this._imaginary - that._imaginary);
    }

    @Override
    public Complex64Impl times(long n) {
        return this.times((double) n);
    }

    // Implements GroupMultiplicative.
    public Complex64Impl times(Complex64Impl that) {
        return Complex64Impl.valueOf(this._real * that._real - this._imaginary * that._imaginary,
                this._real * that._imaginary + this._imaginary * that._real);
    }

    // Implements GroupMultiplicative.
    public Complex64Impl inverse() {
        double tmp = (this._real * this._real) + (this._imaginary * this._imaginary);
        return Complex64Impl.valueOf(this._real / tmp, -this._imaginary / tmp);
    }

    @Override
    public Complex64Impl divide(long n) {
        return this.divide((double) n);
    }

    @Override
    public Complex64Impl divide(Complex64Impl that) {
        double tmp = (that._real * that._real) + (that._imaginary * that._imaginary);
        double thatInvReal = that._real / tmp;
        double thatInvImaginary = -that._imaginary / tmp;
        return Complex64Impl.valueOf(this._real * thatInvReal - this._imaginary * thatInvImaginary,
                this._real * thatInvImaginary + this._imaginary * thatInvReal);
    }

    @Override
    public Complex64Impl pow(int exp) {
        return this.pow((double) exp);
    }

    /**
     * Returns the {@link #getReal real} component of this {@link Complex64Impl}
     * number as a <code>long</code>.
     *
     * @return <code>(long) this.getReal()</code>
     */
    public long longValue() {
        return (long) _real;
    }

    /**
     * Returns the {@link #getReal real} component of this {@link Complex64Impl}
     * number as a <code>double</code>.
     *
     * @return <code>(double) this.getReal()</code>
     */
    public double doubleValue() {
        return _real;
    }

    /**
     * Returns the {@link #getReal real} component of this {@link Complex64Impl}
     * number as a <code>BigDecimal</code>.
     *
     * @return <code>(double) this.getReal()</code>
     */
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(_real);
    }

    /**
     * Compares two complex numbers, the real components are compared first,
     * then if equal, the imaginary components.
     *
     * @param that the complex number to be compared with.
     * @return -1, 0, 1 based upon the ordering. 
     */
    public int compareTo(Complex64Impl that) {
        if (this._real < that._real)
            return -1;
        if (this._real > that._real)
            return 1;
        long l1 = Double.doubleToLongBits(this._real);
        long l2 = Double.doubleToLongBits(that._real);
        if (l1 < l2)
            return -1;
        if (l2 > l1)
            return 1;
        if (this._imaginary < that._imaginary)
            return -1;
        if (this._imaginary > that._imaginary)
            return 1;
        l1 = Double.doubleToLongBits(this._imaginary);
        l2 = Double.doubleToLongBits(that._imaginary);
        if (l1 < l2)
            return -1;
        if (l2 > l1)
            return 1;
        return 0;
    }

    // Implements abstract class Number.
    public Complex64Impl copy() {
        return Complex64Impl.valueOf(_real, _imaginary);
    }
    private static final long serialVersionUID = 1L;

	@Override
	public Complex<Float64> times(Complex<Float64> that) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Complex<Float64> plus(Complex<Float64> that) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Complex<Float64> value() {
		// TODO Auto-generated method stub
		return null;
	}

}
