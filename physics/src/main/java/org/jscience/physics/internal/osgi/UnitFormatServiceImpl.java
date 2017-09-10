/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.internal.osgi;

import java.util.Locale;
import org.jscience.physics.unit.formats.LocalUnitFormat;
import org.jscience.physics.unit.formats.UCUMFormat;
import org.unitsofmeasurement.service.UnitFormatService;
import org.unitsofmeasurement.unit.UnitFormat;

/**
 * UnitFormatService Implementation.
 */
class UnitFormatServiceImpl implements UnitFormatService {

    /**
     * Returns the UCUM instance.
     */
    public UnitFormat getUnitFormat() {
        return UCUMFormat.getCaseSensitiveInstance();
    }

    /**
     * Returns the format having the specified name.
     */
    public UnitFormat getUnitFormat(String name) {
        if (name.equals("UCUM")) return UCUMFormat.getCaseSensitiveInstance();
        return null;
    }

    /**
     * Returns the format for the specified locale.
     */
    public UnitFormat getUnitFormat(Locale locale) {
        return LocalUnitFormat.getInstance(locale);
    }
    
}
