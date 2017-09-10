/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/) All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.formats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.*;
import java.util.Map;
import java.util.ResourceBundle;
import org.jscience.physics.internal.unit.format.ParseException;
import org.jscience.physics.unit.SIPrefix;
import org.jscience.physics.internal.unit.format.TokenMgrError;
import org.jscience.physics.internal.unit.format.UCUMParser;
import org.jscience.physics.unit.types.AnnotatedUnit;
import org.jscience.physics.unit.types.BaseUnit;
import org.jscience.physics.unit.PhysicsUnit;
import org.jscience.physics.unit.PhysicsConverter;
import org.jscience.physics.unit.SI;
import org.jscience.physics.unit.converters.MultiplyConverter;
import org.jscience.physics.unit.converters.RationalConverter;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.unit.Unit;
import org.unitsofmeasurement.unit.UnitConverter;
import org.unitsofmeasurement.unit.UnitFormat;

/**
 * <p> This class provides the interface for formatting and parsing
 * {@link PhysicsUnit units} according to the <a
 * href="http://unitsofmeasure.org/">Uniform Code for Units of Measure</a>
 * (UCUM). </p>
 *
 * <p> For a technical/historical overview of this format please read <a
 * href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?artid=61354"> Units
 * of Measure in Clinical Information Systems</a>. </p>
 *
 * <p> As of revision 1.16, the BNF in the UCUM standard contains an <a
 * href="http://unitsofmeasure.org/ticket/4">error</a>. I've attempted to work
 * around the problem by modifying the BNF productions for &lt;Term&gt;. Once the
 * error in the standard is corrected, it may be necessary to modify the
 * productions in the UCUMParser.jj file to conform to the standard. </p>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 5.0, October 12, 2010
 */
public abstract class UCUMFormat implements UnitFormat {

    // A helper to declare bundle names for all instances
    private static final String BUNDLE_BASE = UCUMFormat.class.getName();

    // /////////////////
    // Class methods //
    // /////////////////
    /** Returns the instance for formatting using "print" symbols */
    public static UCUMFormat getPrintInstance() {
        return Print.DEFAULT;
    }

    /** Returns the instance for formatting using user defined symbols */
    public static UCUMFormat getPrintInstance(SymbolMap symbolMap) {
        return new Print(symbolMap);
    }

    /**
	 * Returns the instance for formatting and parsing using case sensitive
	 * symbols
	 */
    public static UCUMFormat getCaseSensitiveInstance() {
        return Parsing.DEFAULT_CS;
    }

    /**
	 * Returns a case sensitive instance for formatting and parsing using user
	 * defined symbols
	 */
    public static UCUMFormat getCaseSensitiveInstance(SymbolMap symbolMap) {
        return new Parsing(symbolMap, true);
    }

    /**
	 * Returns the instance for formatting and parsing using case insensitive
	 * symbols
	 */
    public static UCUMFormat getCaseInsensitiveInstance() {
        return Parsing.DEFAULT_CI;
    }

    /**
	 * Returns a case insensitive instance for formatting and parsing using user
	 * defined symbols
	 */
    public static UCUMFormat getCaseInsensitiveInstance(SymbolMap symbolMap) {
        return new Parsing(symbolMap, false);
    }
    /**
     * The symbol map used by this instance to map between
     * {@link PhysicsUnit Unit}s and
     * <code>String</code>s.
     */
    final SymbolMap _symbolMap;

    // ////////////////
    // Constructors //
    // ////////////////
    /**
	 * Base constructor.
	 */
    UCUMFormat(SymbolMap symbolMap) {
        _symbolMap = symbolMap;
    }

    /////////////
    // Parsing //
    /////////////
    @Override
    public abstract PhysicsUnit<? extends Quantity> parse(CharSequence csq,
            ParsePosition cursor) throws IllegalArgumentException;

    ////////////////
    // Formatting //
    // //////////////
    public Appendable format(Unit<?> unknownUnit, Appendable appendable) throws IOException {
        if (!(unknownUnit instanceof PhysicsUnit)) {
            throw new UnsupportedOperationException("The UCUM format supports only physical units (PhysicsUnit instances)");
        }
        PhysicsUnit unit = (PhysicsUnit) unknownUnit;
        CharSequence symbol;
        CharSequence annotation = null;
        if (unit instanceof AnnotatedUnit) {
            AnnotatedUnit annotatedUnit = (AnnotatedUnit) unit;
            unit = annotatedUnit.getActualUnit();
            annotation = annotatedUnit.getAnnotation();
        }
        String mapSymbol = _symbolMap.getSymbol(unit);
        if (mapSymbol != null) {
            symbol = mapSymbol;
        } else if (unit.getProductUnits() != null) {
            Map<? extends PhysicsUnit, Integer> productUnits = unit.getProductUnits();
            StringBuffer app = new StringBuffer();
            for (PhysicsUnit u : productUnits.keySet()) {
                StringBuffer temp = new StringBuffer();
                temp = (StringBuffer) format(u, temp);
                if ((temp.indexOf(".") >= 0) || (temp.indexOf("/") >= 0)) {
                    temp.insert(0, '(');
                    temp.append(')');
                }
                int pow = productUnits.get(u);
                if (app.length() > 0) { // Not the first unit.
                    if (pow >= 0) {
                        app.append('.');
                    } else {
                        app.append('/');
                        pow = -pow;
                    }
                } else { // First unit.
                    if (pow < 0) {
                        app.append("1/");
                        pow = -pow;
                    }
                }
                app.append(temp);
                if (pow != 1) {
                    app.append(Integer.toString(pow));
                }
            }
            symbol = app;
        } else if (!unit.isSI() || unit.equals(SI.KILOGRAM)) {
            StringBuffer temp = new StringBuffer();
            UnitConverter converter;
            boolean printSeparator;
            if (unit.equals(SI.KILOGRAM)) {
                // A special case because KILOGRAM is a BaseUnit instead of
                // a transformed unit, for compatability with existing SI
                // unit system.
                format(SI.GRAM, temp);
                converter = SIPrefix.KILO.getConverter();
                printSeparator = true;
            } else {
                PhysicsUnit<?> parentUnit = unit.getSystemUnit();
                converter = unit.getConverterTo(parentUnit);
                if (parentUnit.equals(SI.KILOGRAM)) {
                    // More special-case hackery to work around gram/kilogram
                    // incosistency
                    parentUnit = SI.GRAM;
                    converter = converter.concatenate(SIPrefix.KILO.getConverter());
                }
                format(parentUnit, temp);
                printSeparator = !parentUnit.equals(SI.ONE);
            }
            formatConverter(converter, printSeparator, temp);
            symbol = temp;
        } else if (unit.getSymbol() != null) {
            symbol = unit.getSymbol();
        } else {
            throw new IllegalArgumentException(
                    "Cannot format the given Object as UCUM units (unsupported unit "
                    + unit.getClass().getName()
                    + "). "
                    + "Custom units types should override the toString() method as the default implementation uses the UCUM format.");
        }

        appendable.append(symbol);
        if (annotation != null && annotation.length() > 0) {
            appendAnnotation(unit, symbol, annotation, appendable);
        }

        return appendable;
    }

    void appendAnnotation(PhysicsUnit<?> unit, CharSequence symbol,
            CharSequence annotation, Appendable appendable) throws IOException {
        appendable.append('{');
        appendable.append(annotation);
        appendable.append('}');
    }

    /**
	 * Formats the given converter to the given StringBuffer. This is similar to
	 * what {@link ConverterFormat} does, but there's no need to worry about
	 * operator precedence here, since UCUM only supports multiplication,
	 * division, and exponentiation and expressions are always evaluated left-
	 * to-right.
	 * 
	 * @param converter
	 *            the converter to be formatted
	 * @param continued
	 *            <code>true</code> if the converter expression should begin
	 *            with an operator, otherwise <code>false</code>. This will
	 *            always be true unless the unit being modified is equal to
	 *            Unit.ONE.
	 * @param buffer
	 *            the <code>StringBuffer</code> to append to. Contains the
	 *            already-formatted unit being modified by the given converter.
	 */
    void formatConverter(UnitConverter converter, boolean continued,
            StringBuffer buffer) {
        boolean unitIsExpression = ((buffer.indexOf(".") >= 0) || (buffer.indexOf("/") >= 0));
        SIPrefix prefix = _symbolMap.getPrefix(converter);
        if ((prefix != null) && (!unitIsExpression)) {
            buffer.insert(0, _symbolMap.getSymbol(prefix));
        } else if (converter == PhysicsConverter.IDENTITY) {
            // do nothing
        } else if (converter instanceof MultiplyConverter) {
            if (unitIsExpression) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            MultiplyConverter multiplyConverter = (MultiplyConverter) converter;
            double factor = multiplyConverter.getFactor();
            long lFactor = (long) factor;
            if ((lFactor != factor) || (lFactor < -9007199254740992L)
                    || (lFactor > 9007199254740992L)) {
                throw new IllegalArgumentException(
                        "Only integer factors are supported in UCUM");
            }
            if (continued) {
                buffer.append('.');
            }
            buffer.append(lFactor);
        } else if (converter instanceof RationalConverter) {
            if (unitIsExpression) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            RationalConverter rationalConverter = (RationalConverter) converter;
            if (!rationalConverter.getDividend().equals(BigInteger.ONE)) {
                if (continued) {
                    buffer.append('.');
                }
                buffer.append(rationalConverter.getDividend());
            }
            if (!rationalConverter.getDivisor().equals(BigInteger.ONE)) {
                buffer.append('/');
                buffer.append(rationalConverter.getDivisor());
            }
        } else { // All other converter type (e.g. exponential) we use the string representation.
            buffer.insert(0, converter.toString() + "(");
            buffer.append(")");
        }
    }

    // /////////////////
    // Inner classes //
    // /////////////////
    /**
	 * The Print format is used to output units according to the "print" column
	 * in the UCUM standard. Because "print" symbols in UCUM are not unique,
	 * this class of UCUMFormat may not be used for parsing, only for
	 * formatting.
	 */
    private static class Print extends UCUMFormat {

        /**
         *
         */
        private static final long serialVersionUID = 2990875526976721414L;
        private static final SymbolMap PRINT_SYMBOLS = new SymbolMap(
                ResourceBundle.getBundle(BUNDLE_BASE + "_Print"));
        private static final Print DEFAULT = new Print(PRINT_SYMBOLS);

        public Print(SymbolMap symbols) {
            super(symbols);
        }

        @Override
        public PhysicsUnit<? extends Quantity> parse(CharSequence csq,
                ParsePosition pos) throws IllegalArgumentException {
            throw new UnsupportedOperationException(
                    "The print format is for pretty-printing of units only. Parsing is not supported.");
        }

        @Override
        void appendAnnotation(PhysicsUnit<?> unit, CharSequence symbol,
                CharSequence annotation, Appendable appendable)
                throws IOException {
            if (symbol != null && symbol.length() > 0) {
                appendable.append('(');
                appendable.append(annotation);
                appendable.append(')');
            } else {
                appendable.append(annotation);
            }
        }
    }

    /**
	 * The Parsing format outputs formats and parses units according to the
	 * "c/s" or "c/i" column in the UCUM standard, depending on which SymbolMap
	 * is passed to its constructor.
	 */
    private static class Parsing extends UCUMFormat {

        private static final long serialVersionUID = -922531801940132715L;
        private static final SymbolMap CASE_SENSITIVE_SYMBOLS = new SymbolMap(
                ResourceBundle.getBundle(BUNDLE_BASE + "_CS"));
        private static final SymbolMap CASE_INSENSITIVE_SYMBOLS = new SymbolMap(
                ResourceBundle.getBundle(BUNDLE_BASE + "_CI"));
        private static final Parsing DEFAULT_CS = new Parsing(
                CASE_SENSITIVE_SYMBOLS, true);
        private static final Parsing DEFAULT_CI = new Parsing(
                CASE_INSENSITIVE_SYMBOLS, false);
        private final boolean _caseSensitive;

        public Parsing(SymbolMap symbols, boolean caseSensitive) {
            super(symbols);
            _caseSensitive = caseSensitive;
        }

        @Override
        public PhysicsUnit<? extends Quantity> parse(CharSequence csq,
                ParsePosition cursor) throws IllegalArgumentException {
            // Parsing reads the whole character sequence from the parse
            // position.
            int start = cursor.getIndex();
            int end = csq.length();
            if (end <= start) {
                return SI.ONE;
            }
            String source = csq.subSequence(start, end).toString().trim();
            if (source.length() == 0) {
                return SI.ONE;
            }
            if (!_caseSensitive) {
                source = source.toUpperCase();
            }
            UCUMParser parser = new UCUMParser(_symbolMap,
                    new ByteArrayInputStream(source.getBytes()));
            try {
                PhysicsUnit<?> result = parser.parseUnit();
                cursor.setIndex(end);
                return result;
            } catch (ParseException e) {
                if (e.currentToken != null) {
                    cursor.setErrorIndex(start + e.currentToken.endColumn);
                } else {
                    cursor.setErrorIndex(start);
                }
                throw new IllegalArgumentException(e.getMessage());
            } catch (TokenMgrError e) {
                cursor.setErrorIndex(start);
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }
}
