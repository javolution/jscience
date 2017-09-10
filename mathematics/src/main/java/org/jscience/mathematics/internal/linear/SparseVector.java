/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.internal.vector;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javolution.util.Index;
import javolution.xml.XMLFormat;
import javolution.xml.XMLFormat.InputElement;
import javolution.xml.XMLFormat.OutputElement;
import javolution.xml.stream.XMLStreamException;
import org.jscience.mathematics.structure.Field;

/**
 * <p> This class represents a sparse vector.</p>
 *
 * <p> Sparse vectors are created using {@link Index index} to
 *     non-zero elements mapping.</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, December 12, 2009
 */
public abstract class SparseVector<F extends Field<F>> extends VectorImpl<F> {

    /**
     * Holds the default XML representation for sparse vectors.
     * For example:[code]
     *    <SparseVector dimension="16">
     *        <Zero>
     *            <Complex value="0.0 + 0.0i" />
     *        </Zero>
     *        <Elements>
     *            <Index value="4" />
     *            <Complex value="1.0 + 0.0i" />
     *            <Index value="6" />
     *            <Complex value="0.0 + 1.0i" />
     *        </Elements>
     *    </SparseVector>[/code]
     */
    protected static final XMLFormat<SparseVector> XML_FORMAT = new XMLFormat<SparseVector>(
            SparseVector.class) {

        @Override
        public SparseVector newInstance(Class<SparseVector> cls, InputElement xml)
                throws XMLStreamException {
            int dimension = xml.getAttribute("dimension", 0);
            Field zero = xml.get("Zero");
            Map<Index, Field> elements = xml.get("Elements", Map.class);
            return SparseVector.valueOf(elements, zero, dimension);
        }

        @Override
        public void read(InputElement xml, SparseVector V)
                throws XMLStreamException {
            // Nothing to do (already parsed by newInstance)
        }

        @Override
        public void write(SparseVector V, OutputElement xml)
                throws XMLStreamException {
            xml.setAttribute("dimension", V.getDimension());
            xml.add(V.getZero(), "Zero");
            xml.add(V.getElements(), "Elements", Map.class);
        }
    };

    /**
     * Returns a sparse vector from the specified arguments.
     *
     * @param elements the index to element mapping.
     * @param zero the element representing zero.
     * @param dimension this vector dimension.
     * @return the corresponding vector.
     */
    public static <F extends Field<F>> SparseVector<F> valueOf(Map<Index, F> elements,
            F zero, int dimension) {
        return SparseVectorImpl.valueOf(elements, zero, dimension);
    }

    /**
     * Returns a sparse vector holding the specified single element at the
     * specified position (convenience method).
     *
     * @param index the position of the element.
     * @param element the element
     * @param dimension the vector dimension.
     * @return the corresponding vector.
     */
    public static <F extends Field<F>> SparseVector<F> valueOf(int index, F element,
            int dimension) {
        return SparseVectorImpl.valueOf(index, element, dimension);
    }

    /**
     * Returns a sparse vector equivalent to the specified vector but with
     * the zero elements removed using the specified object equality comparator.
     *
     * @param that the vector to convert to a sparse vector.
     * @param zero the element representing zero.
     * @param comparator the comparator used to determine zero equality.
     * @return a sparse vector with zero elements removed.
     */
    public static <F extends Field<F>> SparseVector<F> valueOf(
            VectorImpl<F> that, F zero, Comparator<? super F> comparator) {
        return SparseVectorImpl.valueOf(that, zero, comparator);
    }

    /**
     * Returns a sparce vector equivalent to that vector.
     * If the specified vector is not a sparse vector, any element equals
     * to its opposite is considered to be a zero element.
     *
     * @param that the vector to convert to a sparse vector.
     * @return a sparse vector equivalent to that vector or <code>that</code>
     */
    public static <F extends Field<F>> SparseVector<F> valueOf(VectorImpl<F> that) {
        if (that instanceof SparseVector)
            return (SparseVector<F>) that;
        return SparseVectorImpl.valueOf(that);
    }

    /**
     * Default constructor.
     */
    protected SparseVector() {
    }

    /**
     * Returns a map view (read only) over the elements of this sparse vector.
     *
     * @return this vector index to element mapping.
     */
    public abstract Map<Index, F> asMap();

    /**
     * Returns the index to value mapping (zero elements are not mapped).
     *
     * @return the non zero elements.
     */
    public abstract Map<Index, F> getElements();

    /**
     * Returns the zero elememnt value for this sparse vector.
     *
     * @return the zero element.
     */
    public abstract F getZero();

    @Override
    public abstract SparseVector<F> getSubVector(List<Index> indices);

    @Override
    public abstract SparseVector<F> opposite();

    @Override
    public abstract SparseVector<F> plus(VectorImpl<F> that);

    @Override
    public SparseVector<F> minus(VectorImpl<F> that) {
        return this.plus(that.opposite());
    }

    @Override
    public abstract SparseVector<F> times(F k);

    @Override
    public abstract SparseVector<F> copy();

}
