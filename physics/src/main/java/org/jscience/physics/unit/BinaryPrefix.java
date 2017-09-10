/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit;

import org.unitsofmeasurement.unit.Unit;
import org.unitsofmeasurement.quantity.Quantity;

/**
 * <p> This class provides support for common binary prefixes to be used by
 *     units.</p>
 *
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 5.0, October 12, 2010
 */
public final class BinaryPrefix {

     /**
     * Default constructor (private).
     */
	private BinaryPrefix() {
		// Utility class no visible constructor.
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>10</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1024)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> KIBI(Unit<Q> unit) {
		return unit.multiply(1024);
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>20</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1048576)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> MEBI(Unit<Q> unit) {
		return unit.multiply(1048576);
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>30</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1073741824)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> GIBI(Unit<Q> unit) {
		return unit.multiply(1073741824);
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>40</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1099511627776L)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> TEBI(Unit<Q> unit) {
		return unit.multiply(1099511627776L);
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>50</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1125899906842624L)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> PEBI(Unit<Q> unit) {
		return unit.multiply(1125899906842624L);
	}

	/**
	 * Returns the specified unit multiplied by the factor
	 * <code>2<sup>60</sup></code> (binary prefix).
	 * 
	 * @param unit any unit.
	 * @return <code>unit.times(1152921504606846976L)</code>.
	 */
	public static <Q extends Quantity<Q>> Unit<Q> EXBI(Unit<Q> unit) {
		return unit.multiply(1152921504606846976L);
	}
}
