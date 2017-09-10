/**
 * This package provides supports for physics units, in conformity with the
 * <a href="http://www.unitsofmeasurement.org/">Units of Measurement API</a>.
 *
 * <h3> Standard / Non Standard Units</h3>
 * <ul>
 *    <li> The class {@link SI} contains standard units as defined by the 
 *       <a href="http://physics.nist.gov/Pubs/SP330/sp330.pdf">
 *      "The International System of Units"</a>.</li>
 *    <li> The class {@link UCUM} contains standard and non-standard units 
 *      as defined by the <a href="http://unitsofmeasure.org/">
 *      "Uniform Code for Units of Measure"</a>.</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * [code]
 *
 * import org.unitsofmeasurement.quantity.*; // Holds quantity types.
 * 
 * import org.jscience.physics.unit.PhysicsUnit;
 * import org.jscience.physics.unit.PhysicsConverter;
 * 
 * import static org.jscience.physics.unit.SI.*; // Standard Units.
 * import static org.jscience.physics.unit.SIPrefix.*;
 * import static org.jscience.physics.unit.UCUM.*; // Standard & Non-Standard Units.
 *
 * public class Main {
 *     public void main(String[] args) {
 *
 *         // Conversion between units (explicit way).
 *         PhysicsUnit<Length> sourceUnit = KILO(METRE);
 *         PhysicsUnit<Length> targetUnit = MILE;
 *         PhysicsConverter uc = sourceUnit.getConverterTo(targetUnit);
 *         System.out.println(uc.convert(10)); // Converts 10 km to miles.
 *
 *         // Same conversion than above, packed in one line.
 *         System.out.println(KILO(METRE).getConverterTo(MILE).convert(10));
 *
 *         // Retrieval of the SI unit (identifies the measurement type).
 *         System.out.println(REVOLUTION.divide(MINUTE).toSI());
 *
 *         // Dimension checking (allows/disallows conversions)
 *         System.out.println(ELECTRON_VOLT.isCompatible(WATT.times(HOUR)));
 *
 *         // Retrieval of the unit dimension (depends upon the current model).
 *         System.out.println(ELECTRON_VOLT.getDimension());
 *     }
 * }
 *
 * > 6.2137119223733395
 * > 6.2137119223733395
 * > rad/s
 * > true
 * > [L]²·[M]/[T]²
 * [/code]
 *
 * <h3>Unit Parameterization</h3>
 *
 *     Units are parameterized enforce compile-time checks of units/measures consistency, for example:[code]
 *
 *     PhysicsUnit<Time> MINUTE = SECOND.times(60); // Ok.
 *     PhysicsUnit<Time> MINUTE = METRE.times(60); // Compile error.
 *
 *     PhysicsUnit<Pressure> HECTOPASCAL = HECTO(PASCAL); // Ok.
 *     PhysicsUnit<Pressure> HECTOPASCAL = HECTO(NEWTON); // Compile error.
 *
 *     Measure<Time> duration = Measure.valueOf(2, MINUTE); // Ok.
 *     Measure<Time> duration = Measure.valueOf(2, CELSIUS); // Compile error.
 *
 *     long milliseconds = duration.longValue(MILLI(SECOND)); // Ok.
 *     long milliseconds = duration.longValue(POUND); // Compile error.
 *     [/code]
 *
 *     Runtime checks of dimension consistency can be done for more complex cases.
 *
 *     [code]
 *     PhysicsUnit<Area> SQUARE_FOOT = FOOT.times(FOOT).asType(Area.class); // Ok.
 *     PhysicsUnit<Area> SQUARE_FOOT = FOOT.times(KELVIN).asType(Area.class); // Runtime error.
 *
 *     PhysicsUnit<Temperature> KELVIN = PhysicsUnit.valueOf("K").asType(Temperature.class); // Ok.
 *     PhysicsUnit<Temperature> KELVIN = PhysicsUnit.valueOf("kg").asType(Temperature.class); // Runtime error.
 *     [/code]
 *     </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0.0
 */
package org.jscience.physics.unit;
