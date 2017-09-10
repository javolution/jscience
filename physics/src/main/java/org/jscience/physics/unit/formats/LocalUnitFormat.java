/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2010 - JScience (http://jscience.org/) All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted, provided that this notice is preserved.
 */
package org.jscience.physics.unit.formats;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.jscience.physics.internal.unit.format.ParseException;
import org.jscience.physics.unit.SIPrefix;
import org.jscience.physics.internal.unit.format.TokenMgrError;
import org.jscience.physics.internal.unit.format.UnitParser;
import org.jscience.physics.unit.types.AnnotatedUnit;
import org.jscience.physics.unit.PhysicsUnit;
import org.jscience.physics.unit.SI;
import org.jscience.physics.unit.converters.AddConverter;
import org.jscience.physics.unit.converters.LogConverter;
import org.jscience.physics.unit.converters.MultiplyConverter;
import org.jscience.physics.unit.converters.RationalConverter;
import org.unitsofmeasurement.unit.Unit;
import org.unitsofmeasurement.unit.UnitConverter;
import org.unitsofmeasurement.unit.UnitFormat;

/**
 * <p> This class represents the local sensitive format.</p>
 *
 * <h3>Here is the grammar for Units in Extended Backus-Naur Form (EBNF)</h3>
 * <p> Note that the grammar has been left-factored to be suitable for use by a
 * top-down parser generator such as <a
 * href="https://javacc.dev.java.net/">JavaCC</a> </p> <table width="90%"
 * align="center"> <tr> <th colspan="3" align="left">Lexical Entities:</th> </tr>
 * <tr valign="top"> <td>&lt;sign&gt;</td> <td>:=</td> <td>"+" | "-"</td> </tr>
 * <tr valign="top"> <td>&lt;digit&gt;</td> <td>:=</td> <td>"0" | "1" | "2" | "3"
 * | "4" | "5" | "6" | "7" | "8" | "9"</td> </tr> <tr valign="top">
 * <td>&lt;superscript_digit&gt;</td> <td>:=</td> <td>"⁰" | "¹" | "²" | "³" | "⁴"
 * | "⁵" | "⁶" | "⁷" | "⁸" | "⁹"</td> </tr> <tr valign="top">
 * <td>&lt;integer&gt;</td> <td>:=</td> <td>(&lt;digit&gt;)+</td> </tr> <tr
 * valign="top"> <td>&lt;number&gt;</td> <td>:=</td> <td>(&lt;sign&gt;)?
 * (&lt;digit&gt;)* (".")? (&lt;digit&gt;)+ (("e" | "E") (&lt;sign&gt;)?
 * (&lt;digit&gt;)+)? </td> </tr> <tr valign="top"> <td>&lt;exponent&gt;</td>
 * <td>:=</td> <td>( "^" ( &lt;sign&gt; )? &lt;integer&gt; ) <br>| ( "^("
 * (&lt;sign&gt;)? &lt;integer&gt; ( "/" (&lt;sign&gt;)? &lt;integer&gt; )? ")" )
 * <br>| ( &lt;superscript_digit&gt; )+</td> </tr> <tr valign="top">
 * <td>&lt;initial_char&gt;</td> <td>:=</td> <td>? Any Unicode character
 * excluding the following: ASCII control & whitespace (&#92;u0000 - &#92;u0020),
 * decimal digits '0'-'9', '(' (&#92;u0028), ')' (&#92;u0029), '*' (&#92;u002A),
 * '+' (&#92;u002B), '-' (&#92;u002D), '.' (&#92;u002E), '/' (&#92;u005C), ':'
 * (&#92;u003A), '^' (&#92;u005E), '²' (&#92;u00B2), '³' (&#92;u00B3), '·'
 * (&#92;u00B7), '¹' (&#92;u00B9), '⁰' (&#92;u2070), '⁴' (&#92;u2074), '⁵'
 * (&#92;u2075), '⁶' (&#92;u2076), '⁷' (&#92;u2077), '⁸' (&#92;u2078), '⁹'
 * (&#92;u2079) ?</td> </tr> <tr valign="top"> <td>&lt;unit_identifier&gt;</td>
 * <td>:=</td> <td>&lt;initial_char&gt; ( &lt;initial_char&gt; | &lt;digit&gt;
 * )*</td> </tr> <tr> <th colspan="3" align="left">Non-Terminals:</th> </tr> <tr
 * valign="top"> <td>&lt;unit_expr&gt;</td> <td>:=</td>
 * <td>&lt;compound_expr&gt;</td> </tr> <tr valign="top">
 * <td>&lt;compound_expr&gt;</td> <td>:=</td> <td>&lt;add_expr&gt; ( ":"
 * &lt;add_expr&gt; )*</td> </tr> <tr valign="top"> <td>&lt;add_expr&gt;</td>
 * <td>:=</td> <td>( &lt;number&gt; &lt;sign&gt; )? &lt;mul_expr&gt; (
 * &lt;sign&gt; &lt;number&gt; )?</td> </tr> <tr valign="top">
 * <td>&lt;mul_expr&gt;</td> <td>:=</td> <td>&lt;exponent_expr&gt; ( ( ( "*" |
 * "·" ) &lt;exponent_expr&gt; ) | ( "/" &lt;exponent_expr&gt; ) )*</td> </tr>
 * <tr valign="top"> <td>&lt;exponent_expr&gt;</td> <td>:=</td> <td>(
 * &lt;atomic_expr&gt; ( &lt;exponent&gt; )? ) <br>| (&lt;integer&gt; "^"
 * &lt;atomic_expr&gt;) <br>| ( ( "log" ( &lt;integer&gt; )? ) | "ln" ) "("
 * &lt;add_expr&gt; ")" )</td> </tr> <tr valign="top">
 * <td>&lt;atomic_expr&gt;</td> <td>:=</td> <td>&lt;number&gt; <br>|
 * &lt;unit_identifier&gt; <br>| ( "(" &lt;add_expr&gt; ")" )</td> </tr> </table>
 *
 * @author <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 5.0, October 12, 2010
 */
public class LocalUnitFormat implements UnitFormat {

    //////////////////////////////////////////////////////
    // Class variables                                  //
    //////////////////////////////////////////////////////
    /**
     * Default locale instance. If the default locale is changed after the class
     * is initialized, this instance will no longer be used.
     */
    private static LocalUnitFormat DEFAULT_INSTANCE = new LocalUnitFormat(new SymbolMap(ResourceBundle.getBundle(LocalUnitFormat.class.getName())));
    /**
     * Multiplicand character
     */
    private static final char MIDDLE_DOT = '\u00b7';
    /**
     * Operator precedence for the addition and subtraction operations
     */
    private static final int ADDITION_PRECEDENCE = 0;
    /**
     * Operator precedence for the multiplication and division operations
     */
    private static final int PRODUCT_PRECEDENCE = ADDITION_PRECEDENCE + 2;
    /**
     * Operator precedence for the exponentiation and logarithm operations
     */
    private static final int EXPONENT_PRECEDENCE = PRODUCT_PRECEDENCE + 2;
    /**
     * Operator precedence for a unit identifier containing no mathematical
     * operations (i.e., consisting exclusively of an identifier and possibly a
     * prefix). Defined to be
     * <code>Integer.MAX_VALUE</code> so that no operator can have a higher
     * precedence.
     */
    private static final int NOOP_PRECEDENCE = Integer.MAX_VALUE;

    ///////////////////
    // Class methods //
    ///////////////////
    /** Returns the instance for the current default locale (non-ascii characters are allowed) */
    public static LocalUnitFormat getInstance() {
        return DEFAULT_INSTANCE;
    }

    /** 
     * Returns an instance for the given locale.
     * @param locale
     */
    public static LocalUnitFormat getInstance(Locale locale) {
        return new LocalUnitFormat(new SymbolMap(ResourceBundle.getBundle(LocalUnitFormat.class.getName(), locale)));
    }

    /** Returns an instance for the given symbol map. */
    public static LocalUnitFormat getInstance(SymbolMap symbols) {
        return new LocalUnitFormat(symbols);
    }
    ////////////////////////
    // Instance variables //
    ////////////////////////
    /**
     * The symbol map used by this instance to map between
     * {@link PhysicsUnit Unit}s and
     * <code>String</code>s, etc...
     */
    private transient SymbolMap symbolMap;

    //////////////////
    // Constructors //
    //////////////////
    /**
     * Base constructor.
     *
     * @param symbols the symbol mapping.
     */
    private LocalUnitFormat(SymbolMap symbols) {
        symbolMap = symbols;
    }

    ////////////////////////
    // Instance methods //
    ////////////////////////
    /** 
     * Get the symbol map used by this instance to map between 
     * {@link PhysicsUnit Unit}s and <code>String</code>s, etc...
     * @return SymbolMap the current symbol map
     */
    public SymbolMap getSymbols() {
        return symbolMap;
    }

    ////////////////
    // Formatting //
    ////////////////
    @Override
    public Appendable format(Unit<?> unit, Appendable appendable) throws IOException {
        if (!(unit instanceof PhysicsUnit)) {
            return appendable.append(unit.toString()); // Unknown unit (use intrinsic toString() method)
        }
        formatInternal((PhysicsUnit) unit, appendable);
        return appendable;
    }

    @Override
    public PhysicsUnit<?> parse(CharSequence csq, ParsePosition cursor) throws IllegalArgumentException {
        // Parsing reads the whole character sequence from the parse position.
        int start = cursor.getIndex();
        int end = csq.length();
        if (end <= start) {
            return SI.ONE;
        }
        String source = csq.subSequence(start, end).toString().trim();
        if (source.length() == 0) {
            return SI.ONE;
        }
        try {
            UnitParser parser = new UnitParser(symbolMap, new StringReader(source));
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

    /**
     * Format the given unit to the given StringBuffer, then return the operator
     * precedence of the outermost operator in the unit expression that was 
     * formatted. See {@link ConverterFormat} for the constants that define the
     * various precedence values.
     * @param unit the unit to be formatted
     * @param buffer the <code>StringBuffer</code> to be written to
     * @return the operator precedence of the outermost operator in the unit 
     *   expression that was output
     */
    private int formatInternal(PhysicsUnit<?> unit, Appendable buffer) throws IOException {
        if (unit instanceof AnnotatedUnit) {
            unit = ((AnnotatedUnit) unit).getActualUnit();
        }
        String symbol = symbolMap.getSymbol(unit);
        if (symbol != null) {
            buffer.append(symbol);
            return NOOP_PRECEDENCE;
        } else if (unit.getProductUnits() != null) {
            Map<? extends PhysicsUnit, Integer> productUnits = unit.getProductUnits();
            int negativeExponentCount = 0;
            // Write positive exponents first...
            boolean start = true;
            for (PhysicsUnit u : productUnits.keySet()) {
                int pow = productUnits.get(u);
                if (pow >= 0) {
                    formatExponent(u, pow, 1, !start, buffer);
                    start = false;
                } else {
                    negativeExponentCount += 1;
                }
            }
            // ..then write negative exponents.
            if (negativeExponentCount > 0) {
                if (start) {
                    buffer.append('1');
                }
                buffer.append('/');
                if (negativeExponentCount > 1) {
                    buffer.append('(');
                }
                start = true;
                for (PhysicsUnit u : productUnits.keySet()) {
                    int pow = productUnits.get(u);
                    if (pow < 0) {
                        formatExponent(u, -pow, 1, !start, buffer);
                        start = false;
                    }
                }
                if (negativeExponentCount > 1) {
                    buffer.append(')');
                }
            }
            return PRODUCT_PRECEDENCE;
        } else if ((!unit.isSI()) || unit.equals(SI.KILOGRAM)) {
            UnitConverter converter = null;
            boolean printSeparator = false;
            StringBuffer temp = new StringBuffer();
            int unitPrecedence = NOOP_PRECEDENCE;
            if (unit.equals(SI.KILOGRAM)) {
                // A special case because KILOGRAM is a BaseUnit instead of 
                // a transformed unit, even though it has a prefix.
                converter = SIPrefix.KILO.getConverter();
                unitPrecedence = formatInternal(SI.GRAM, temp);
                printSeparator = true;
            } else {
                PhysicsUnit parentUnit = unit.getSystemUnit();
                converter = unit.getConverterTo(parentUnit);
                if (parentUnit.equals(SI.KILOGRAM)) {
                    // More special-case hackery to work around gram/kilogram 
                    // incosistency
                    parentUnit = SI.GRAM;
                    converter = converter.concatenate(SIPrefix.KILO.getConverter());
                }
                unitPrecedence = formatInternal(parentUnit, temp);
                printSeparator = !parentUnit.equals(SI.ONE);
            }
            int result = formatConverter(converter, printSeparator, unitPrecedence, temp);
            buffer.append(temp);
            return result;
        } else if (unit.getSymbol() != null) {
            buffer.append(unit.getSymbol());
            return NOOP_PRECEDENCE;
        } else {
            throw new IllegalArgumentException("Cannot format the given Object as a Unit (unsupported unit type " + unit.getClass().getName() + ")");
        }
    }

    /**
     * Format the given unit raised to the given fractional power to the
     * given <code>StringBuffer</code>.
     * @param unit Unit the unit to be formatted
     * @param pow int the numerator of the fractional power
     * @param root int the denominator of the fractional power
     * @param continued boolean <code>true</code> if the converter expression 
     *    should begin with an operator, otherwise <code>false</code>. This will 
     *    always be true unless the unit being modified is equal to Unit.ONE.
     * @param buffer StringBuffer the buffer to append to. No assumptions should
     *    be made about its content.
     */
    private void formatExponent(PhysicsUnit<?> unit, int pow, int root, boolean continued, Appendable buffer) throws IOException {
        if (continued) {
            buffer.append(MIDDLE_DOT);
        }
        StringBuffer temp = new StringBuffer();
        int unitPrecedence = formatInternal(unit, temp);
        if (unitPrecedence < PRODUCT_PRECEDENCE) {
            temp.insert(0, '(');
            temp.append(')');
        }
        buffer.append(temp);
        if ((root == 1) && (pow == 1)) {
            // do nothing
        } else if ((root == 1) && (pow > 1)) {
            String powStr = Integer.toString(pow);
            for (int i = 0; i < powStr.length(); i += 1) {
                char c = powStr.charAt(i);
                switch (c) {
                    case '0':
                        buffer.append('\u2070');
                        break;
                    case '1':
                        buffer.append('\u00b9');
                        break;
                    case '2':
                        buffer.append('\u00b2');
                        break;
                    case '3':
                        buffer.append('\u00b3');
                        break;
                    case '4':
                        buffer.append('\u2074');
                        break;
                    case '5':
                        buffer.append('\u2075');
                        break;
                    case '6':
                        buffer.append('\u2076');
                        break;
                    case '7':
                        buffer.append('\u2077');
                        break;
                    case '8':
                        buffer.append('\u2078');
                        break;
                    case '9':
                        buffer.append('\u2079');
                        break;
                }
            }
        } else if (root == 1) {
            buffer.append("^");
            buffer.append(String.valueOf(pow));
        } else {
            buffer.append("^(");
            buffer.append(String.valueOf(pow));
            buffer.append('/');
            buffer.append(String.valueOf(root));
            buffer.append(')');
        }
    }

    /**
     * Formats the given converter to the given StringBuffer and returns the
     * operator precedence of the converter's mathematical operation. This is
     * the default implementation, which supports all built-in UnitConverter
     * implementations. Note that it recursively calls itself in the case of 
     * a {@link javax.measure.converter.UnitConverter.Compound Compound} 
     * converter.
     * @param converter the converter to be formatted
     * @param continued <code>true</code> if the converter expression should 
     *    begin with an operator, otherwise <code>false</code>.
     * @param unitPrecedence the operator precedence of the operation expressed
     *    by the unit being modified by the given converter.
     * @param buffer the <code>StringBuffer</code> to append to.
     * @return the operator precedence of the given UnitConverter
     */
    private int formatConverter(UnitConverter converter,
            boolean continued,
            int unitPrecedence,
            StringBuffer buffer) {
        SIPrefix prefix = symbolMap.getPrefix(converter);
        if ((prefix != null) && (unitPrecedence == NOOP_PRECEDENCE)) {
            buffer.insert(0, symbolMap.getSymbol(prefix));
            return NOOP_PRECEDENCE;
        } else if (converter instanceof AddConverter) {
            if (unitPrecedence < ADDITION_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            double offset = ((AddConverter) converter).getOffset();
            if (offset < 0) {
                buffer.append("-");
                offset = -offset;
            } else if (continued) {
                buffer.append("+");
            }
            long lOffset = (long) offset;
            if (lOffset == offset) {
                buffer.append(lOffset);
            } else {
                buffer.append(offset);
            }
            return ADDITION_PRECEDENCE;
        } else if (converter instanceof MultiplyConverter) {
            if (unitPrecedence < PRODUCT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            if (continued) {
                buffer.append(MIDDLE_DOT);
            }
            double factor = ((MultiplyConverter) converter).getFactor();
            long lFactor = (long) factor;
            if (lFactor == factor) {
                buffer.append(lFactor);
            } else {
                buffer.append(factor);
            }
            return PRODUCT_PRECEDENCE;
        } else if (converter instanceof RationalConverter) {
            if (unitPrecedence < PRODUCT_PRECEDENCE) {
                buffer.insert(0, '(');
                buffer.append(')');
            }
            RationalConverter rationalConverter = (RationalConverter) converter;
            if (!rationalConverter.getDividend().equals(BigInteger.ONE)) {
                if (continued) {
                    buffer.append(MIDDLE_DOT);
                }
                buffer.append(rationalConverter.getDividend());
            }
            if (!rationalConverter.getDivisor().equals(BigInteger.ONE)) {
                buffer.append('/');
                buffer.append(rationalConverter.getDivisor());
            }
            return PRODUCT_PRECEDENCE;
        } else { // All other converter type (e.g. exponential) we use the string representation.
            buffer.insert(0, converter.toString() + "(");
            buffer.append(")");
            return EXPONENT_PRECEDENCE;
        }
    }
}
