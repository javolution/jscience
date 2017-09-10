/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/) All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.formats;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;
import javolution.context.LogContext;
import javolution.util.FastMap;
import org.jscience.physics.unit.SIPrefix;
import org.jscience.physics.unit.PhysicsUnit;
import org.jscience.physics.unit.PhysicsConverter;
import org.unitsofmeasurement.unit.UnitConverter;

/**
 * <p> This class provides a set of mappings between
 *     {@link PhysicsUnit physical units} and symbols (both ways), between {@link SIPrefix prefixes}
 * and symbols (both ways), and from {@link PhysicsConverter
 *     physical unit converters} to {@link SIPrefix prefixes} (one way). No attempt
 * is made to verify the uniqueness of the mappings.</p>
 *
 * <p> Mappings are read from a
 * <code>ResourceBundle</code>, the keys of which should consist of a
 * fully-qualified class name, followed by a dot ('.'), and then the name of a
 * static field belonging to that class, followed optionally by another dot and a
 * number. If the trailing dot and number are not present, the value associated
 * with the key is treated as a
 *     {@link SymbolMap#label(PhysicsUnit, String) label}, otherwise if the trailing
 * dot and number are present, the value is treated as an {@link SymbolMap#alias(PhysicsUnit,String) alias}.
 * Aliases map from String to Unit only, whereas labels map in both directions. A
 * given unit may have any number of aliases, but may have only one label.</p>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @version 5.0, October 12, 2010
 */
public class SymbolMap {

    private FastMap<String, PhysicsUnit<?>> _symbolToUnit;
    private FastMap<PhysicsUnit<?>, String> _unitToSymbol;
    private FastMap<String, Object> _symbolToPrefix;
    private FastMap<Object, String> _prefixToSymbol;
    private FastMap<UnitConverter, SIPrefix> _converterToPrefix;

    /**
     * Creates an empty mapping.
     */
    public SymbolMap() {
        _symbolToUnit = new FastMap<String, PhysicsUnit<?>>();
        _unitToSymbol = new FastMap<PhysicsUnit<?>, String>();
        _symbolToPrefix = new FastMap<String, Object>();
        _prefixToSymbol = new FastMap<Object, String>();
        _converterToPrefix = new FastMap<UnitConverter, SIPrefix>();
    }

    /** 
     * Creates a symbol map from the specified resource bundle,
     *
     * @param rb the resource bundle.
     */
    public SymbolMap(ResourceBundle rb) {
        this();
        for (Enumeration<String> i = rb.getKeys(); i.hasMoreElements();) {
            String fqn = i.nextElement();
            String symbol = rb.getString(fqn);
            boolean isAlias = false;
            int lastDot = fqn.lastIndexOf('.');
            String className = fqn.substring(0, lastDot);
            String fieldName = fqn.substring(lastDot + 1, fqn.length());
            if (Character.isDigit(fieldName.charAt(0))) {
                isAlias = true;
                fqn = className;
                lastDot = fqn.lastIndexOf('.');
                className = fqn.substring(0, lastDot);
                fieldName = fqn.substring(lastDot + 1, fqn.length());
            }
            try {
                Class<?> c = Class.forName(className);
                Field field = c.getField(fieldName);
                Object value = field.get(null);
                if (value instanceof PhysicsUnit<?>) {
                    if (isAlias) {
                        alias((PhysicsUnit<?>) value, symbol);
                    } else {
                        label((PhysicsUnit<?>) value, symbol);
                    }
                } else if (value instanceof SIPrefix) {
                    label((SIPrefix) value, symbol);
                } else {
                    throw new ClassCastException("unable to cast " + value + " to Unit or Prefix");
                }
            } catch (Exception error) {
                LogContext.error(error);
            }
        }
    }

    /**
     * Attaches a label to the specified unit. For example:[code]
     *    symbolMap.label(DAY.multiply(365), "year");
     *    symbolMap.label(NonSI.FOOT, "ft");
     * [/code]
     *
     * @param unit the unit to label.
     * @param symbol the new symbol for the unit.
     */
    public void label(PhysicsUnit<?> unit, String symbol) {
        _symbolToUnit.put(symbol, unit);
        _unitToSymbol.put(unit, symbol);
    }

    /**
     * Attaches an alias to the specified unit. Multiple aliases may be
     * attached to the same unit. Aliases are used during parsing to
     * recognize different variants of the same unit.[code]
     *     symbolMap.alias(NonSI.FOOT, "foot");
     *     symbolMap.alias(NonSI.FOOT, "feet");
     *     symbolMap.alias(SI.METER, "meter");
     *     symbolMap.alias(SI.METER, "metre");
     * [/code]
     *
     * @param unit the unit to label.
     * @param symbol the new symbol for the unit.
     */
    public void alias(PhysicsUnit<?> unit, String symbol) {
        _symbolToUnit.put(symbol, unit);
    }

    /**
     * Attaches a label to the specified prefix. For example:[code]
     *    symbolMap.label(SIPrefix.GIGA, "G");
     *    symbolMap.label(SIPrefix.MICRO, "µ");
     * [/code]
     */
    public void label(SIPrefix prefix, String symbol) {
        _symbolToPrefix.put(symbol, prefix);
        _prefixToSymbol.put(prefix, symbol);
        _converterToPrefix.put(prefix.getConverter(), prefix);
    }

    /**
     * Returns the unit for the specified symbol.
     * 
     * @param symbol the symbol.
     * @return the corresponding unit or <code>null</code> if none.
     */
    public PhysicsUnit<?> getUnit(String symbol) {
        return _symbolToUnit.get(symbol);
    }

    /**
     * Returns the symbol (label) for the specified unit.
     *
     * @param unit the corresponding symbol.
     * @return the corresponding symbol or <code>null</code> if none.
     */
    public String getSymbol(PhysicsUnit<?> unit) {
        return _unitToSymbol.get(unit);
    }

    /**
     * Returns the prefix (if any) for the specified symbol.
     *
     * @param symbol the unit symbol.
     * @return the corresponding prefix or <code>null</code> if none.
     */
    public SIPrefix getPrefix(String symbol) {
        for (Iterator<String> i = _symbolToPrefix.keySet().iterator(); i.hasNext();) {
            String pfSymbol = i.next();
            if (symbol.startsWith(pfSymbol)) {
                return (SIPrefix) _symbolToPrefix.get(pfSymbol);
            }
        }
        return null;
    }

    /**
     * Returns the prefix for the specified converter.
     *
     * @param converter the unit converter.
     * @return the corresponding prefix or <code>null</code> if none.
     */
    public SIPrefix getPrefix(UnitConverter converter) {
        return (SIPrefix) _converterToPrefix.get(converter);
    }

    /**
     * Returns the symbol for the specified prefix.
     *
     * @param prefix the prefix.
     * @return the corresponding symbol or <code>null</code> if none.
     */
    public String getSymbol(SIPrefix prefix) {
        return _prefixToSymbol.get(prefix);
    }
}
