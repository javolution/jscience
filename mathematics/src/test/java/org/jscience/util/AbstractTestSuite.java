/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.util;

import static javolution.context.LogContext.info;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javolution.testing.JUnitContext;
import javolution.testing.TestCase;
import javolution.testing.TestContext;
import javolution.testing.TestSuite;

/**
 * Base class for test suites that run all methods starting with "test" to create the actual tests. This is makes sense
 * if there are many tests - so one cannot forget to call any of them. A free JUnit-Adapter is also included. :-)
 * @since 22.12.2008
 * @author <a href="http://www.stoerr.net/">Hans-Peter Störr</a>
 */
public abstract class AbstractTestSuite extends TestSuite {

    private boolean initialized = false;

    @Override
    public void setUp() {
        super.setUp();
        if (!initialized) {
            runTestCreatorMethods();
            initialized = true;
        }        
    }

    /**
     * Runs all methods of this class that start with "test" by reflection. All methods starting with "test" must have
     * noarguments.
     */
    private void runTestCreatorMethods() {
        final Map<String, Method> testMethods = new TreeMap<String, Method>();
        Class<?> clazz = getClass();
        while (null != clazz) {
            for (final Method m : clazz.getDeclaredMethods()) {
                final String name = m.getName();
                if (name.startsWith("test") && !name.equals("tests") && !testMethods.containsKey(name))
                    testMethods.put(name, m);
            }
            clazz = clazz.getSuperclass();
        }
        for (final Map.Entry<String, Method> e : testMethods.entrySet())
            try {
                info(getClass() + " invoking " + e.getValue());
                e.getValue().invoke(this);
            } catch (final IllegalAccessException e1) {
                throw new RuntimeException(e1.toString(), e1);
            } catch (final InvocationTargetException e1) {
                throw new RuntimeException(e1.toString(), e1.getTargetException());
            }
    }

    /** Gives the test to the {@link TestContext}. This method is here to provide a hook to do other things here. */
    protected void doTest(final TestCase t) {
        addTest(t);
    }

    @Test
    public void runTests() throws Exception {
        JUnitContext.enter();
        try {
            TestContext.run(this);
        } finally {
            JUnitContext.exit();
        }
    }

}
