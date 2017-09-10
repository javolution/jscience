package org.jscience.mathematics.number.util;

import static java.lang.StrictMath.nextAfter;
import static java.lang.StrictMath.nextUp;

import java.math.BigDecimal;

import org.jscience.mathematics.number.Number;

/**
 * A feeble attempt at double interval arithmetics.
 * @author hps
 * @since 20.02.2009
 */
public class DoubleInterval extends Number<DoubleInterval> {

    public static final DoubleInterval ZERO = new DoubleInterval(0, 0);
    public static final DoubleInterval ONE = new DoubleInterval(1, 1);
    public static final DoubleInterval NaN = new DoubleInterval(Double.NaN, Double.NaN);

    /** SUID */
    private static final long serialVersionUID = -1798040855774523462L;

    private final double _lower;
    private final double _upper;

    public DoubleInterval(final double lower, final double upper) {
        if (Double.isNaN(upper) || Double.isNaN(lower)) {
            _lower = Double.NaN;
            _upper = Double.NaN;
        } else {
            assert lower <= upper : lower + ">" + upper;
            _lower = lower;
            _upper = upper;
        }
    }

    public static DoubleInterval valueOf(final double lower, final double upper) {
        return new DoubleInterval(lower, upper);
    }

    /** Rounds one ulp outward for operations that are within at most 1 ulp of the exact value. */
    private static DoubleInterval outward(final double lower, final double upper) {
        return new DoubleInterval(nextAfter(lower, Double.NEGATIVE_INFINITY), nextUp(upper));
    }

    public double lower() {
        return _lower;
    }

    public double upper() {
        return _upper;
    }

    @Override
    public int compareTo(final DoubleInterval that) {
        final UnsupportedOperationException e = new UnsupportedOperationException(
                "Unimplemented method called: Number<DoubleInterval>.compareTo");
        throw e;
    }

    @Override
    public DoubleInterval copy() {
        return valueOf(_lower, _upper);
    }

    @Override
    public double doubleValue() {
        return (_lower + _upper) / 2;
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    public DoubleInterval times(final DoubleInterval that) {
        final double _l1 = _lower * that._lower;
        final double _l2 = _lower * that._upper;
        final double _l3 = _upper * that._lower;
        final double _l4 = _upper * that._upper;
        return outward(StrictMath.min(StrictMath.min(_l1, _l2), StrictMath.min(_l3, _l4)), StrictMath.max(
                StrictMath.max(_l1, _l2), StrictMath.max(_l3, _l4)));
    }

    public DoubleInterval opposite() {
        return valueOf(-_upper, -_lower);
    }

    public DoubleInterval plus(final DoubleInterval that) {
        return outward(_lower + that._lower, _upper + that._upper);
    }

    public boolean contains(final double d) {
        return _lower <= d && d <= _upper;
    }

    /** The multiplicative inverse. */
    public DoubleInterval inverse() {
        DoubleInterval res;
        if (contains(0)) res = NaN;
        else res = outward(1 / _upper, 1 / _lower);
        return res;
    }

    public DoubleInterval divide(final DoubleInterval that) {
        return times(that.inverse());
    }

    @Override
    public DoubleInterval abs() {
        DoubleInterval res;
        if (contains(0)) res = valueOf(0, StrictMath.max(-_lower, _upper));
        else res = this;
        return res;
    }

    public DoubleInterval min(final DoubleInterval that) {
        return valueOf(StrictMath.min(_lower, that._lower), StrictMath.min(_upper, that._upper));
    }

    public DoubleInterval max(final DoubleInterval that) {
        return valueOf(StrictMath.max(_lower, that._lower), StrictMath.max(_upper, that._upper));
    }

    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(doubleValue());
    }
}
