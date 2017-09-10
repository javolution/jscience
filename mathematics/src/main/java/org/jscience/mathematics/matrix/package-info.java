/**
<p> Provides support for <a href="http://en.wikipedia.org/wiki/Linear_algebra">linear algebra</a>
    in the form of  {@link org.jscience.mathematics.matrix.Vector vectors}
    and {@link org.jscience.mathematics.matrix.Matrix matrices}. 
   .</p>
    
<p> With the {@link org.jscience.mathematics.matrix.Matrix Matrix} class,
    you should be able to resolve linear systems of equations
    involving any kind of elements such as 
    {@link org.jscience.mathematics.number.Rational Rational},
    {@link org.jscience.mathematics.number.ModuloInteger ModuloInteger} (modulo operations),
    {@link org.jscience.mathematics.number.ComplexField Complex},
    {@link org.jscience.mathematics.function.RationalFunction RationalFunction}, etc.
    The main requirement being that your element class implements the mathematical
    {@link org.jscience.mathematics.structure.Field Field} interface.</p>
    
<p> Most {@link org.jscience.mathematics.number numbers} and even invertible matrices
    themselves may implement this  interface. Non-commutative multiplication is supported which
    allows for the resolution of systems of equations with invertible matrix coefficients (matrices of matrices).</p>

<p> For classes embedding automatic error calculation (e.g.
    {@link org.jscience.mathematics.number.Real Real} 
    the error on the solution obtained tells you if can trust that solution or not 
    (e.g. system close to singularity). The following example illustrates this point.</p>
    
<p> Let's say you have a simple electric circuit composed of 2 resistors in series
    with a battery. You want to know the voltage (U1, U2) at the nodes of the
    resistors and the current (I) traversing the circuit.
[code]
Amount<Real, ElectricResistance> R1 = Amount.of(Real.of("100 ± 1"), OHM); // (100 ± 1) Ω
Amount<Real, ElectricResistance> R2 = Amount.of(Real.of("300 ± 3"), OHM); // (300 ± 3) Ω
Amount<Real, ElectricPotential>  U0 = Amount.of(Real.of("28 ± 0.01"), VOLT); // (28 ± 0.01) V

// Equations:  U0 = U1 + U2       |1  1  0 |   |U1|   |U0|
//             U1 = R1 * I    =>  |-1 0  R1| * |U2| = |0 |
//             U2 = R2 * I        |0 -1  R2|   |I |   |0 |
//
//                                    A      *  X   =  B
//
Matrix<Amount<Real,?>> A = Matrices.denseMatrix(
    Vectors.denseVector(Amount.ONE,            Amount.ONE,            Amount.of(Real.ZERO, OHM)),
    Vectors.denseVector(Amount.ONE.opposite(), Amount.ZERO,           R1),
    Vectors.denseVector(Amount.ZERO,           Amount.ONE.opposite(), R2));
AmountVector<Real,ElectricPotential>> B = AmountVector.of(
    U0, Amount.of(Real.ZERO, VOLT), Amount.of(Real.ZERO, VOLT));
Vector<Amount<Real,?>> X = A.solve(B);
System.out.println(X);
System.out.println(X.get(2).to(MILLI(AMPERE)));

> {(7.0 ± 1.6E-1) V, (21.0 ± 1.5E-1) V, (7.0E-2 ± 7.3E-4) V/Ω}
> (70.0 ± 7.3E-1) mA
[/code]
        
Because the {@link org.jscience.mathematics.number.Real Real} class guarantees
the accuracy/precision of its calculations. As long as the input resistances, voltage
stay within their specification range then the current <b>is guaranteed</b>
to be <code>(70.0 ± 7.3E-1) mA</code>. When the inputs have no error specified, 
the error on the result corresponds to calculations numeric errors only
(which might increase significantly if the matrix is close to singularity).</p>

<p> A few vectors/matrices such as {@link RealVector}/{@link RealMatrix} or 
    {@link ComplexVector}/{@link ComplexMatrix} are accelerated through Javolution 
    {@link javolution.context.ComputeContext ComputeContext} and there operations
    can be efficiently chained for best performance on GPUs devices and multi-cores CPUs.
[code]
FloatMatrix A, B;
FloatMatrix C;
ComputeContext ctx = ComputeContext.enter();
try {
    // Equivalent to the Matlab code: C = inv((A' * B) * 12.0)
    C = A.transpose().times(B).times(12).invert();  
    C.export(); // Moves to global memory.
} finally {
    ctx.exit(); // Releases local device memory buffers.
}[/code]</p>

 */
package org.jscience.mathematics.matrix;

