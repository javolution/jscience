/**
<p> Provides mathematical sets (identified by the class parameter) associated to binary operations, 
    such as multiplication or addition, satisfying certain axioms.</p>
    
<p> For example, 
    {@link org.jscience.mathematics.number.Real Real} is a 
    {@link org.jscience.mathematics.structure.OrderedField OrderedField&lt;Real&gt;},
    but
    {@link org.jscience.mathematics.number.LargeInteger LargeInteger} is only a 
    {@link org.jscience.mathematics.structure.Ring Ring&lt;LargeInteger&gt;} as its 
    elements do not have multiplicative inverse (except for one).</p>
    
<p> To implement a structure means not only that some operations are now available
    but also that some properties (such as associativity and distributivity) must be verified.
    For example, the declaration: [code]class Quaternions implements Field<Quaternions>[/code]
    Indicates that addition (+), multiplication (·) and their respective inverses 
    are automatically defined for Quaternions objects; but also that (·) is distributive over (+),
    both operations (+) and (·) are associative and (+) is commutative.</p>
 */
package org.jscience.mathematics.structure;

