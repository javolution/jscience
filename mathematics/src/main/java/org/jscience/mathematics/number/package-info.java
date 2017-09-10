/**
<p> Provides common types of numbers most of them implementing the 
    {@link org.jscience.mathematics.structure.Field field} interface.</p>
    
<p> Although numbers defined in this package are not as fast as primitives types
    (e.g. <code>int</code> or <code>double</code>). They have many
    advantages (such as arbitrary size for {@link org.jscience.mathematics.number.LargeInteger LargeInteger}
    or precision for {@link org.jscience.mathematics.number.Real Real}) which
    make them irreplaceable in some calculations. This can be illustrated with the following example:<pre>
        double x = 10864;
        double y = 18817;
        double z = 9 * Math.pow(x, 4.0)- Math.pow(y, 4.0) + 2 * Math.pow(y, 2.0);
        System.out.println("Result : " + z);

        > Result : 2.0</pre>
    The mathematically correct value is z=1. However, Java compilers
    using ANSI/IEEE double precision numbers evaluate z=2. Not even the first
    digit is correct! This is due to a rounding error occurring when subtracting
    two nearly equal floating point numbers. Now, lets write the same formula
    using {@link org.jscience.mathematics.number.Real Real} numbers:<pre>
        int accuracy = 20; // 20 decimal zeros for integer values.
        Real x = Real.of(10864, accuracy);
        Real y = Real.of(18817, accuracy);
        Real z = x.pow(4).times(9).plus(y.pow(4).opposite()).plus(y.pow(2).times(2));
        System.out.println("Result : " + z);

        > Result : 1.00000</pre>

    Not only the correct result is returned, but this result is also <b>guaranteed</b> to be <code>1 ± 0.00001</code>.
    Only exact digits are written out, for example the following displays the first exact 
    digits of <code>sqrt(2)</code>: <pre>
    Real two = Real.of(2, 100); // 2.0000..00 (100 zeros after decimal point).
    Real sqrt2 = two.sqrt();
    System.out.println("sqrt(2)   = " + sqrt2);
    System.out.println("Precision = " + sqrt2.getPrecision() + " digits.");
    
    > sqrt(2)   = 1.414213562373095048801688724209698078569671875376948
    > Precision = 53 digits.</pre>

 */
package org.jscience.mathematics.number;