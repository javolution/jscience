/**
 *  The UCUM codes, UCUM table (regardless of format) and UCUM Specification
 *  are copyright © 1999-2009, Regenstrief Institute, Inc.
 */
package org.jscience.physics.unit;

import org.unitsofmeasurement.quantity.InformationRate;
import org.unitsofmeasurement.quantity.Information;
import org.jscience.physics.unit.converters.PiMultiplierConverter;
import java.util.Set;
import javolution.util.FastSet;
import org.unitsofmeasurement.quantity.Acceleration;
import org.unitsofmeasurement.quantity.Action;
import org.unitsofmeasurement.quantity.AmountOfSubstance;
import org.unitsofmeasurement.quantity.Angle;
import org.unitsofmeasurement.quantity.Area;
import org.unitsofmeasurement.quantity.Dimensionless;
import org.unitsofmeasurement.quantity.DynamicViscosity;
import org.unitsofmeasurement.quantity.ElectricCapacitance;
import org.unitsofmeasurement.quantity.ElectricCharge;
import org.unitsofmeasurement.quantity.ElectricConductance;
import org.unitsofmeasurement.quantity.ElectricCurrent;
import org.unitsofmeasurement.quantity.ElectricInductance;
import org.unitsofmeasurement.quantity.ElectricPermittivity;
import org.unitsofmeasurement.quantity.ElectricPotential;
import org.unitsofmeasurement.quantity.ElectricResistance;
import org.unitsofmeasurement.quantity.Energy;
import org.unitsofmeasurement.quantity.Force;
import org.unitsofmeasurement.quantity.Frequency;
import org.unitsofmeasurement.quantity.Illuminance;
import org.unitsofmeasurement.quantity.IonizingRadiation;
import org.unitsofmeasurement.quantity.KinematicViscosity;
import org.unitsofmeasurement.quantity.Length;
import org.unitsofmeasurement.quantity.Luminance;
import org.unitsofmeasurement.quantity.LuminousFlux;
import org.unitsofmeasurement.quantity.LuminousIntensity;
import org.unitsofmeasurement.quantity.MagneticFieldStrength;
import org.unitsofmeasurement.quantity.MagneticFlux;
import org.unitsofmeasurement.quantity.MagneticFluxDensity;
import org.unitsofmeasurement.quantity.MagneticPermeability;
import org.unitsofmeasurement.quantity.MagnetomotiveForce;
import org.unitsofmeasurement.quantity.Mass;
import org.unitsofmeasurement.quantity.Power;
import org.unitsofmeasurement.quantity.Pressure;
import org.unitsofmeasurement.quantity.Quantity;
import org.unitsofmeasurement.quantity.RadiationDoseAbsorbed;
import org.unitsofmeasurement.quantity.RadiationDoseEffective;
import org.unitsofmeasurement.quantity.RadioactiveActivity;
import org.unitsofmeasurement.quantity.SolidAngle;
import org.unitsofmeasurement.quantity.Temperature;
import org.unitsofmeasurement.quantity.Time;
import org.unitsofmeasurement.quantity.Velocity;
import org.unitsofmeasurement.quantity.Volume;
import org.unitsofmeasurement.quantity.WaveNumber;
import org.unitsofmeasurement.unit.Dimension;
import org.unitsofmeasurement.unit.SystemOfUnits;

import static org.jscience.physics.unit.SIPrefix.*;

/**
 * <p> This class contains {@link SI} and Non-SI units as defined
 *     in the <a href="http://unitsofmeasure.org/"> Uniform Code for Units
 *     of Measure</a>.</p>
 *
 * <p> Compatibility with {@link SI} units has been given
 *     priority over strict adherence to the standard. We have attempted to note
 *     every place where the definitions in this class deviate from the
 *     UCUM standard, but such notes are likely to be incomplete.</p>
 *
 * @author  <a href="mailto:eric-r@northwestern.edu">Eric Russell</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @see <a href="http://aurora.regenstrief.org/UCUM/ucum.html">UCUM</a>
 */
public final class UCUM implements SystemOfUnits {

    /**
     * The singleton instance.
     */
    private static final UCUM INSTANCE = new UCUM();

    /**
     * Holds the units.
     */
    private final FastSet<PhysicsUnit> units = new FastSet<PhysicsUnit>();


    /**
     * Default constructor (prevents this class from being instantiated).
     */
    private UCUM() {
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the UCUM system instance.
     */
    public static UCUM getInstance() {
        return INSTANCE;
    }

    //////////////////////////////
    // BASE UNITS: UCUM 4.2 §25 //
    //////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> METER = addUnit(SI.METRE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> SECOND = addUnit(SI.SECOND);
    /**
     * We deviate slightly from the standard here, to maintain compatibility
     * with the existing SI units. In UCUM, the gram is the base unit of mass,
     * rather than the kilogram. This doesn't have much effect on the units
     * themselves, but it does make formatting the units a challenge.
     */
    public static final PhysicsUnit<Mass> GRAM = addUnit(SI.GRAM);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> RADIAN = addUnit(SI.RADIAN);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Temperature> KELVIN = addUnit(SI.KELVIN);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricCharge> COULOMB = addUnit(SI.COULOMB);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<LuminousIntensity> CANDELA = addUnit(SI.CANDELA);
    ///////////////////////////////////////////////
    // DIMENSIONLESS DERIVED UNITS: UCUM 4.3 §26 //
    ///////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> TRIILLIONS = addUnit(SI.ONE.multiply(1000000000000L));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> BILLIONS = addUnit(SI.ONE.multiply(1000000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> MILLIONS = addUnit(SI.ONE.multiply(1000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> THOUSANDS = addUnit(SI.ONE.multiply(1000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> HUNDREDS = addUnit(SI.ONE.multiply(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PI = addUnit(SI.ONE.transform(new PiMultiplierConverter()));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PERCENT = addUnit(SI.ONE.divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PER_THOUSAND = addUnit(SI.ONE.divide(1000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PER_MILLION = addUnit(SI.ONE.divide(1000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PER_BILLION = addUnit(SI.ONE.divide(1000000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> PER_TRILLION = addUnit(SI.ONE.divide(1000000000000L));
    ////////////////////////////
    // SI UNITS: UCUM 4.3 §27 //
    ////////////////////////////
    /**
     * We deviate slightly from the standard here, to maintain compatibility
     * with the existing SI units. In UCUM, the mole is no longer a base unit,
     * but is defined as <code>Unit.ONE.multiply(6.0221367E23)</code>.
     */
    public static final PhysicsUnit<AmountOfSubstance> MOLE = addUnit(SI.MOLE);
    /**
     * We deviate slightly from the standard here, to maintain compatibility
     * with the existing SI units. In UCUM, the steradian is defined as
     * <code>RADIAN.pow(2)</code>.
     */
    public static final PhysicsUnit<SolidAngle> STERADIAN = addUnit(SI.STERADIAN);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Frequency> HERTZ = addUnit(SI.HERTZ);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Force> NEWTON = addUnit(SI.NEWTON);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Pressure> PASCAL = addUnit(SI.PASCAL);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> JOULE = addUnit(SI.JOULE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Power> WATT = addUnit(SI.WATT);
    /**
     * We deviate slightly from the standard here, to maintain compatability
     * with the existing SI units. In UCUM, the ampere is defined as
     * <code>COULOMB.divide(SECOND)</code>.
     */
    public static final PhysicsUnit<ElectricCurrent> AMPERE = addUnit(SI.AMPERE);
    public static final PhysicsUnit<MagnetomotiveForce> AMPERE_TURN = addUnit(SI.AMPERE_TURN);
    /**
     * We deviate slightly from the standard here, to maintain compatibility
     * with the existing SI units. In UCUM, the volt is defined as
     * <code>JOULE.divide(COULOMB)</code>.
     */
    public static final PhysicsUnit<ElectricPotential> VOLT = addUnit(SI.VOLT);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricCapacitance> FARAD = addUnit(SI.FARAD);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricResistance> OHM = addUnit(SI.OHM);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricConductance> SIEMENS = addUnit(SI.SIEMENS);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticFlux> WEBER = addUnit(SI.WEBER);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Temperature> CELSIUS = addUnit(SI.CELSIUS);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticFluxDensity> TESLA = addUnit(SI.TESLA);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricInductance> HENRY = addUnit(SI.HENRY);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<LuminousFlux> LUMEN = addUnit(SI.LUMEN);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Illuminance> LUX = addUnit(SI.LUX);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadioactiveActivity> BECQUEREL = addUnit(SI.BECQUEREL);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadiationDoseAbsorbed> GRAY = addUnit(SI.GRAY);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadiationDoseEffective> SIEVERT = addUnit(SI.SIEVERT);
    ///////////////////////////////////////////////////////////////////////
    // OTHER UNITS FROM ISO 1000, ISO 2955, AND ANSI X3.50: UCUM 4.3 §28 //
    ///////////////////////////////////////////////////////////////////////
    // The order of GON and DEGREE has been inverted because GON is defined in terms of DEGREE
    /**
     * We deviate slightly from the standard here, to maintain compatibility
     * with the existing NonSI units. In UCUM, the degree is defined as
     * <code>PI.multiply(RADIAN.divide(180))</code>.
     */
    public static final PhysicsUnit<Angle> DEGREE = addUnit(SI.DEGREE_ANGLE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> GRADE = addUnit(SI.DEGREE_ANGLE.multiply(0.9));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> GON = GRADE;
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> MINUTE_ANGLE = addUnit(SI.MINUTE_ANGLE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> SECOND_ANGLE = addUnit(SI.SECOND_ANGLE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> LITER = addUnit(SI.LITRE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> ARE = addUnit(SI.SQUARE_METRE.multiply(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> MINUTE = addUnit(SI.MINUTE);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> HOUR = addUnit(SI.HOUR);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> DAY = addUnit(SI.DAY);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> YEAR_TROPICAL = addUnit(SI.DAY.multiply(365.24219));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> YEAR_JULIAN = addUnit(SI.DAY.multiply(365.25));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> YEAR_GREGORIAN = addUnit(SI.DAY.multiply(365.2425));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> YEAR = addUnit(SI.DAY.multiply(365.25));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> MONTH_SYNODAL = addUnit(SI.DAY.multiply(29.53059));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> MONTH_JULIAN = addUnit(YEAR_JULIAN.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> MONTH_GREGORIAN = addUnit(YEAR_GREGORIAN.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Time> MONTH = addUnit(YEAR_JULIAN.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> TONNE = addUnit(SI.KILOGRAM.multiply(1000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Pressure> BAR = addUnit(SI.PASCAL.multiply(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> ATOMIC_MASS_UNIT = addUnit(SI.UNIFIED_ATOMIC_MASS);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> ELECTRON_VOLT = addUnit(SI.ELECTRON_VOLT);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> ASTRONOMIC_UNIT = addUnit(SI.ASTRONOMICAL_UNIT);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> PARSEC = addUnit(SI.METRE.multiply(3.085678E16));
    /////////////////////////////////
    // NATURAL UNITS: UCUM 4.3 §29 //
    /////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Velocity> C = addUnit(SI.METRES_PER_SECOND.multiply(299792458));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Action> PLANCK = addUnit(SI.JOULE_SECOND.multiply(6.6260755E-24));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<?> BOLTZMAN = addUnit(JOULE.divide(KELVIN).multiply(1.380658E-23));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricPermittivity> PERMITTIVITY_OF_VACUUM = addUnit(SI.FARADS_PER_METRE.multiply(8.854187817E-12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticPermeability> PERMEABILITY_OF_VACUUM 
            = addUnit(SI.NEWTONS_PER_SQUARE_AMPERE.multiply(PI).multiply(4).divide(1E7).asType(MagneticPermeability.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricCharge> ELEMENTARY_CHARGE = addUnit(SI.COULOMB.transform(SI.ELECTRON_VOLT.getConverterToSI()));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> ELECTRON_MASS = addUnit(GRAM.multiply(9.1093897E-28));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> PROTON_MASS = addUnit(GRAM.multiply(1.6726231E-24));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<?> NEWTON_CONSTANT_OF_GRAVITY = addUnit(METER.pow(3).multiply(SI.KILOGRAM.pow(-1)).multiply(SECOND.pow(-2)).multiply(6.67259E-11));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Acceleration> ACCELLERATION_OF_FREEFALL = addUnit(SI.METRES_PER_SQUARE_SECOND.multiply(9.80665));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Pressure> ATMOSPHERE = addUnit(SI.PASCAL.multiply(101325));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> LIGHT_YEAR = addUnit(C.multiply(YEAR_JULIAN).asType(Length.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Force> GRAM_FORCE = addUnit(GRAM.multiply(ACCELLERATION_OF_FREEFALL).asType(Force.class));
    // POUND_FORCE contains a forward reference to avoirdupois pound weight, so it has been moved after section §36 below
    /////////////////////////////
    // CGS UNITS: UCUM 4.3 §30 //
    /////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<WaveNumber> KAYSER = addUnit(SI.RECIPROCAL_METRE.divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Acceleration> GAL = addUnit(CENTI(METER).divide(SECOND.pow(2)).asType(Acceleration.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Force> DYNE = addUnit(SI.GRAM.multiply(CENTI(SI.METRE)).divide(SI.SECOND.pow(2)).asType(Force.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> ERG = addUnit(DYNE.multiply(CENTI(SI.METRE)).asType(Energy.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<DynamicViscosity> POISE = addUnit(DYNE.multiply(SECOND).divide(CENTI(SI.METRE).pow(2)).asType(DynamicViscosity.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricCurrent> BIOT = addUnit(AMPERE.multiply(10));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<KinematicViscosity> STOKES = addUnit(CENTI(SI.METRE).pow(2).divide(SI.SECOND).asType(KinematicViscosity.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticFlux> MAXWELL = addUnit(SI.WEBER.divide(1E8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticFluxDensity> GAUSS = addUnit(SI.TESLA.divide(1E4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagneticFieldStrength> OERSTED = addUnit(SI.AMPERES_PER_METRE.multiply(250).divide(PI).asType(MagneticFieldStrength.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<MagnetomotiveForce> GILBERT = addUnit(OERSTED.multiply(CENTI(SI.METRE)).asType(MagnetomotiveForce.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Luminance> STILB = addUnit(CANDELA.divide(CENTI(METER).pow(2)).asType(Luminance.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Luminance> LAMBERT = addUnit(STILB.divide(PI).asType(Luminance.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Illuminance> PHOT = addUnit(LUX.divide(1E4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadioactiveActivity> CURIE = addUnit(SI.BECQUEREL.multiply(3.7E10));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<IonizingRadiation> ROENTGEN = addUnit(SI.COULOMBS_PER_KILOGRAM.multiply(2.58E-4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadiationDoseAbsorbed> RAD = addUnit(ERG.divide(SI.GRAM).multiply(100).asType(RadiationDoseAbsorbed.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<RadiationDoseEffective> REM = addUnit(RAD.asType(RadiationDoseEffective.class));
    /////////////////////////////////////////////////
    // INTERNATIONAL CUSTOMARY UNITS: UCUM 4.4 §31 //
    /////////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> INCH_INTERNATIONAL = addUnit(CENTI(METER).multiply(254).divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FOOT_INTERNATIONAL = addUnit(INCH_INTERNATIONAL.multiply(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> YARD_INTERNATIONAL = addUnit(FOOT_INTERNATIONAL.multiply(3));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> MILE_INTERNATIONAL = addUnit(FOOT_INTERNATIONAL.multiply(5280));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FATHOM_INTERNATIONAL = addUnit(FOOT_INTERNATIONAL.multiply(6));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> NAUTICAL_MILE_INTERNATIONAL = addUnit(METER.multiply(1852));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Velocity> KNOT_INTERNATIONAL = addUnit(NAUTICAL_MILE_INTERNATIONAL.divide(HOUR).asType(Velocity.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SQUARE_INCH_INTERNATIONAL = addUnit(INCH_INTERNATIONAL.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SQUARE_FOOT_INTERNATIONAL = addUnit(FOOT_INTERNATIONAL.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SQUARE_YARD_INTERNATIONAL = addUnit(YARD_INTERNATIONAL.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CUBIC_INCH_INTERNATIONAL = addUnit(INCH_INTERNATIONAL.pow(3).asType(Volume.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CUBIC_FOOT_INTERNATIONAL = addUnit(FOOT_INTERNATIONAL.pow(3).asType(Volume.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CUBIC_YARD_INTERNATIONAL = addUnit(YARD_INTERNATIONAL.pow(3).asType(Volume.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> BOARD_FOOT_INTERNATIONAL = addUnit(CUBIC_INCH_INTERNATIONAL.multiply(144));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CORD_INTERNATIONAL = addUnit(CUBIC_FOOT_INTERNATIONAL.multiply(128));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> MIL_INTERNATIONAL = addUnit(INCH_INTERNATIONAL.divide(1000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> CIRCULAR_MIL_INTERNATIONAL = addUnit(MIL_INTERNATIONAL.pow(2).multiply(PI).divide(4).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> HAND_INTERNATIONAL = addUnit(INCH_INTERNATIONAL.multiply(4));
    //////////////////////////////////////////
    // US SURVEY LENGTH UNITS: UCUM 4.4 §32 //
    //////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FOOT_US_SURVEY = addUnit(METER.multiply(1200).divide(3937));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> YARD_US_SURVEY = addUnit(FOOT_US_SURVEY.multiply(3));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> INCH_US_SURVEY = addUnit(FOOT_US_SURVEY.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> ROD_US_SURVEY = addUnit(FOOT_US_SURVEY.multiply(33).divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> CHAIN_US_SURVEY = addUnit(ROD_US_SURVEY.multiply(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> LINK_US_SURVEY = addUnit(CHAIN_US_SURVEY.divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> RAMDEN_CHAIN_US_SURVEY = addUnit(FOOT_US_SURVEY.multiply(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> RAMDEN_LINK_US_SURVEY = addUnit(CHAIN_US_SURVEY.divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FATHOM_US_SURVEY = addUnit(FOOT_US_SURVEY.multiply(6));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FURLONG_US_SURVEY = addUnit(ROD_US_SURVEY.multiply(40));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> MILE_US_SURVEY = addUnit(FURLONG_US_SURVEY.multiply(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> ACRE_US_SURVEY = addUnit(ROD_US_SURVEY.pow(2).multiply(160).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SQUARE_ROD_US_SURVEY = addUnit(ROD_US_SURVEY.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SQUARE_MILE_US_SURVEY = addUnit(MILE_US_SURVEY.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> SECTION_US_SURVEY = addUnit(MILE_US_SURVEY.pow(2).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> TOWNSHP_US_SURVEY = addUnit(SECTION_US_SURVEY.multiply(36));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> MIL_US_SURVEY = addUnit(INCH_US_SURVEY.divide(1000));
    /////////////////////////////////////////////////
    // BRITISH IMPERIAL LENGTH UNITS: UCUM 4.4 §33 //
    /////////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> INCH_BRITISH = addUnit(CENTI(METER).multiply(2539998).divide(1000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FOOT_BRITISH = addUnit(INCH_BRITISH.multiply(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> ROD_BRITISH = addUnit(FOOT_BRITISH.multiply(33).divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> CHAIN_BRITISH = addUnit(ROD_BRITISH.multiply(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> LINK_BRITISH = addUnit(CHAIN_BRITISH.divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> FATHOM_BRITISH = addUnit(FOOT_BRITISH.multiply(6));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> PACE_BRITISH = addUnit(FOOT_BRITISH.multiply(5).divide(20));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> YARD_BRITISH = addUnit(FOOT_BRITISH.multiply(3));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> MILE_BRITISH = addUnit(FOOT_BRITISH.multiply(5280));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> NAUTICAL_MILE_BRITISH = addUnit(FOOT_BRITISH.multiply(6080));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Velocity> KNOT_BRITISH = addUnit(NAUTICAL_MILE_BRITISH.divide(HOUR).asType(Velocity.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> ACRE_BRITISH = addUnit(YARD_BRITISH.pow(2).multiply(4840).asType(Area.class));
    ///////////////////////////////////
    // US VOLUME UNITS: UCUM 4.4 §34 //
    ///////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> GALLON_US = addUnit(CUBIC_INCH_INTERNATIONAL.multiply(231));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> BARREL_US = addUnit(GALLON_US.multiply(42));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> QUART_US = addUnit(GALLON_US.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> PINT_US = addUnit(QUART_US.divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> GILL_US = addUnit(PINT_US.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> FLUID_OUNCE_US = addUnit(GILL_US.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> FLUID_DRAM_US = addUnit(FLUID_OUNCE_US.divide(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> MINIM_US = addUnit(FLUID_DRAM_US.divide(60));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CORD_US = addUnit(CUBIC_FOOT_INTERNATIONAL.multiply(128));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> BUSHEL_US = addUnit(CUBIC_INCH_INTERNATIONAL.multiply(215042).divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> GALLON_WINCHESTER = addUnit(BUSHEL_US.divide(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> PECK_US = addUnit(BUSHEL_US.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> DRY_QUART_US = addUnit(PECK_US.divide(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> DRY_PINT_US = addUnit(DRY_QUART_US.divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> TABLESPOON_US = addUnit(FLUID_OUNCE_US.divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> TEASPOON_US = addUnit(TABLESPOON_US.divide(3));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> CUP_US = addUnit(TABLESPOON_US.multiply(16));
    /////////////////////////////////////////////////
    // BRITISH IMPERIAL VOLUME UNITS: UCUM 4.4 §35 //
    /////////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> GALLON_BRITISH = addUnit(LITER.multiply(454609).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> PECK_BRITISH = addUnit(GALLON_BRITISH.multiply(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> BUSHEL_BRITISH = addUnit(PECK_BRITISH.multiply(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> QUART_BRITISH = addUnit(GALLON_BRITISH.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> PINT_BRITISH = addUnit(QUART_BRITISH.divide(2));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> GILL_BRITISH = addUnit(PINT_BRITISH.divide(4));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> FLUID_OUNCE_BRITISH = addUnit(GILL_BRITISH.divide(5));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> FLUID_DRAM_BRITISH = addUnit(FLUID_OUNCE_BRITISH.divide(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> MINIM_BRITISH = addUnit(FLUID_DRAM_BRITISH.divide(60));
    ////////////////////////////////////////////
    // AVOIRDUPOIS WIEGHT UNITS: UCUM 4.4 §36 //
    ////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> GRAIN = addUnit(MILLI(GRAM).multiply(6479891).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> POUND = addUnit(GRAIN.multiply(7000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> OUNCE = addUnit(POUND.divide(16));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> DRAM = addUnit(OUNCE.divide(16));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> SHORT_HUNDREDWEIGHT = addUnit(POUND.multiply(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> LONG_HUNDREDWEIGHT = addUnit(POUND.multiply(112));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> SHORT_TON = addUnit(SHORT_HUNDREDWEIGHT.multiply(20));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> LONG_TON = addUnit(LONG_HUNDREDWEIGHT.multiply(20));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> STONE = addUnit(POUND.multiply(14));
    // CONTINUED FROM SECTION §29
    // contains a forward reference to POUND, so we had to move it here, below section §36
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Force> POUND_FORCE = addUnit(POUND.multiply(ACCELLERATION_OF_FREEFALL).asType(Force.class));
    /////////////////////////////////////
    // TROY WIEGHT UNITS: UCUM 4.4 §37 //
    /////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> PENNYWEIGHT_TROY = addUnit(GRAIN.multiply(24));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> OUNCE_TROY = addUnit(PENNYWEIGHT_TROY.multiply(24));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> POUND_TROY = addUnit(OUNCE_TROY.multiply(12));
    /////////////////////////////////////////////
    // APOTECARIES' WEIGHT UNITS: UCUM 4.4 §38 //
    /////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> SCRUPLE_APOTHECARY = addUnit(GRAIN.multiply(20));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> DRAM_APOTHECARY = addUnit(SCRUPLE_APOTHECARY.multiply(3));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> OUNCE_APOTHECARY = addUnit(DRAM_APOTHECARY.multiply(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> POUND_APOTHECARY = addUnit(OUNCE_APOTHECARY.multiply(12));
    /////////////////////////////////////////////
    // TYPESETTER'S LENGTH UNITS: UCUM 4.4 §39 //
    /////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> LINE = addUnit(INCH_INTERNATIONAL.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> POINT = addUnit(LINE.divide(6));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> PICA = addUnit(POINT.multiply(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> POINT_PRINTER = addUnit(INCH_INTERNATIONAL.multiply(13837).divide(1000000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> PICA_PRINTER = addUnit(POINT_PRINTER.multiply(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> PIED = addUnit(CENTI(METER).multiply(3248).divide(100));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> POUCE = addUnit(PIED.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> LINGE = addUnit(POUCE.divide(12));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> DIDOT = addUnit(LINGE.divide(6));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> CICERO = addUnit(DIDOT.multiply(12));
    //////////////////////////////////////
    // OTHER LEGACY UNITS: UCUM 4.5 §40 //
    //////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Temperature> FAHRENHEIT = addUnit(KELVIN.multiply(5).divide(9).add(459.67));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_AT_15C = addUnit(JOULE.multiply(41858).divide(10000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_AT_20C = addUnit(JOULE.multiply(41819).divide(10000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_MEAN = addUnit(JOULE.multiply(419002).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_INTERNATIONAL_TABLE = addUnit(JOULE.multiply(41868).divide(10000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_THERMOCHEMICAL = addUnit(JOULE.multiply(4184).divide(1000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE = addUnit(CALORIE_THERMOCHEMICAL);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> CALORIE_FOOD = addUnit(KILO(CALORIE_THERMOCHEMICAL));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_AT_39F = addUnit(KILO(JOULE).multiply(105967).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_AT_59F = addUnit(KILO(JOULE).multiply(105480).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_AT_60F = addUnit(KILO(JOULE).multiply(105468).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_MEAN = addUnit(KILO(JOULE).multiply(105587).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_INTERNATIONAL_TABLE = addUnit(KILO(JOULE).multiply(105505585262L).divide(100000000000L));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU_THERMOCHEMICAL = addUnit(KILO(JOULE).multiply(105735).divide(100000));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Energy> BTU = addUnit(BTU_THERMOCHEMICAL);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Power> HORSEPOWER = addUnit(FOOT_INTERNATIONAL.multiply(POUND_FORCE).divide(SECOND).asType(Power.class));
    /////////////////////////////////////////////////////////
    // SECTIONS §41-§43 skipped; implement later if needed //
    /////////////////////////////////////////////////////////
    ///////////////////////////////////////
    // MISCELLANEOUS UNITS: UCUM 4.5 §44 //
    ///////////////////////////////////////
    /** temporary helper for MHO */
    private static final PhysicsUnit<? extends Quantity> TMP_MHO = SIEMENS.alternate("mho");

    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Volume> STERE = addUnit(METER.pow(3).asType(Volume.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Length> ANGSTROM = addUnit(NANO(METER).divide(10));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Area> BARN = addUnit(FEMTO(METER).pow(2).multiply(100).asType(Area.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Pressure> ATMOSPHERE_TECHNICAL = addUnit(KILO(GRAM_FORCE).divide(CENTI(METER).pow(2)).asType(Pressure.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<ElectricConductance> MHO = addUnit(TMP_MHO.asType(ElectricConductance.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Pressure> POUND_PER_SQUARE_INCH =  addUnit(POUND_FORCE.divide(INCH_INTERNATIONAL.pow(2)).asType(Pressure.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Angle> CIRCLE = addUnit(PI.multiply(RADIAN).multiply(2).asType(Angle.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<SolidAngle> SPHERE = addUnit(PI.multiply(STERADIAN).multiply(4).asType(SolidAngle.class));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Mass> CARAT_METRIC = addUnit(GRAM.divide(5));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Dimensionless> CARAT_GOLD = addUnit(SI.ONE.divide(24));
    ////////////////////////////////////////////////
    // INFORMATION TECHNOLOGY UNITS: UCUM 4.6 §45 //
    ////////////////////////////////////////////////
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Information> BIT = addUnit(SI.BIT);
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Information> BYTE = addUnit(SI.BIT.multiply(8));
    /** As per <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<InformationRate> BAUD = addUnit(SI.BITS_PER_SECOND);

    ///////////////////////
    // MISSING FROM UCUM //
    ///////////////////////

    /** To be added to the <a href="http://unitsofmeasure.org/">UCUM</a> standard. */
    public static final PhysicsUnit<Frequency> FRAMES_PER_SECOND = addUnit(SI.ONE.divide(SECOND)).asType(Frequency.class);


    /////////////////////
    // Collection View //
    /////////////////////

    @Override
    public String getName() {
        return "UCUM";
    }

    @Override
    public Set<? extends PhysicsUnit> getUnits() {
        return units.unmodifiable();
    }

    /**
     * Returns the unit corresponding to the specified quantity type.
     * The UCUM system uses the same mapping as the {@link SI} system.
     *
     * @param quantityType
     * @return <code>SI.getInstance().getUnit(quantityType)</code>
     */
    @Override
    public <Q extends Quantity<Q>> PhysicsUnit<Q> getUnit(Class<Q> quantityType) {
        return SI.getInstance().getUnit(quantityType);
    }

    @Override
    public Set<? extends PhysicsUnit> getUnits(Dimension dimension) {
        FastSet<PhysicsUnit> set = FastSet.newInstance();
        for (PhysicsUnit unit : this.getUnits()) {
            if (dimension.equals(unit.getDimension())) {
                set.add(unit);
            }
        }
        return set;
    }

    /**
     * Adds a new unit not mapped to any specified quantity type.
     *
     * @param  unit the unit being added.
     * @return <code>unit</code>.
     */
    private static <U extends PhysicsUnit<?>>  U addUnit(U unit) {
        INSTANCE.units.add(unit);
        return unit;
    }
}
