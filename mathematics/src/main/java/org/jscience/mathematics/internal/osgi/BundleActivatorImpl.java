/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.osgi;

import javolution.context.LogContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * <p> The OSGi activator for the jscience-mathematics bundle.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, October 21, 2011
 */
public class BundleActivatorImpl implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        Object name = bc.getBundle().getHeaders().get(Constants.BUNDLE_NAME);
        Object version = bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        LogContext.info("Start Bundle: ", name, ", Version: ", version);
        
    }

    public void stop(BundleContext bc) throws Exception {
        Object name = bc.getBundle().getHeaders().get(Constants.BUNDLE_NAME);
        Object version = bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        LogContext.info("Stop Bundle: ", name, ", Version: ", version);
    }
        
}
