/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.physics.internal.osgi;

import javolution.context.LogContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.unitsofmeasurement.service.SystemOfUnitsService;
import org.unitsofmeasurement.service.UnitFormatService;

/**
 * <p> The OSGi activator for the jscience-physics bundle.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 21, 2011
 */
public class BundleActivatorImpl implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        Object name = bc.getBundle().getHeaders().get(Constants.BUNDLE_NAME);
        Object version = bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        LogContext.info("Start Bundle: ", name, ", Version: ", version);
        
        // Publish SystemOfUnitsServices Implementation.
         bc.registerService(SystemOfUnitsService.class.getName(), new SystemOfUnitsServiceImpl(), null);

        // Publish UnitFormatService Implementation.
         bc.registerService(UnitFormatService.class.getName(), new UnitFormatServiceImpl(), null);
        
    }

    public void stop(BundleContext bc) throws Exception {
        Object name = bc.getBundle().getHeaders().get(Constants.BUNDLE_NAME);
        Object version = bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        LogContext.info("Stop Bundle: ", name, ", Version: ", version);
    }
        
}
