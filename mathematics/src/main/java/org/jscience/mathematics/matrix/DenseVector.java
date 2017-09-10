/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import java.io.IOException;
import java.io.Serializable;
import javolution.lang.Constant;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.util.function.Orders;
import javolution.util.function.Equality;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import org.jscience.mathematics.internal.matrix.DenseVectorImpl;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A {@link Vector vector} with a larger number of elements different
 *     from zero.</p>
 *     
 * <p> This abstract class minimizes the effort required to implement the
 *     {@link Vector vector} interface (only two methods need to be implemented
 *     {@link #get(int)} and {@link #dimension()}).<p>
 *         
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, Februar 16, 2014
 * @see SparseVector
 */
@Constant
@DefaultTextFormat(DenseVector.Text.class)
@DefaultXMLFormat(DenseVector.XML.class)
public abstract class DenseVector<F extends Field<F>> implements Vector<F>,
        Serializable {

    /**
     * Defines the default text format for dense vectors (list of elements). 
     */
    public static class Text extends TextFormat<DenseVector<?>> {

        @Override
        public Appendable format(DenseVector<?> that, final Appendable dest)
                throws IOException {
            dest.append('{');
            for (int i = 0, n = that.dimension();;) {
                TextContext.format(that.get(i), dest);
                if (++i >= n)
                    break;
                dest.append(',').append(' ');
            }
            return dest.append('}');
        }

        @Override
        public DenseVector<?> parse(CharSequence csq, Cursor cursor)
                throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Defines the default XML representation for dense vectors. For example:
     * [code]
     * <DenseVector dimension="2">
     *     <Rational value="1/3" />
     *     <Rational value="3/5" />
     * </DenseVector>[/code]
     */
    public static class XML extends XMLFormat<DenseVector<?>> {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public DenseVector<?> newInstance(Class<? extends DenseVector<?>> cls,
                InputElement xml) throws XMLStreamException {
            int n = xml.getAttribute("dimension", 0);
            Field[] elements = new Field[n];
            for (int i = 0; i < n; i++)
                elements[i] = xml.getNext();
            return DenseVector.of(elements);
        }

        @Override
        public void read(InputElement xml, DenseVector<?> v)
                throws XMLStreamException {
            // Do nothing, vector already read.
        }

        @Override
        public void write(DenseVector<?> that, OutputElement xml)
                throws XMLStreamException {
            int n = that.dimension();
            xml.setAttribute("dimension", n);
            for (int i = 0; i < n; i++) {
                xml.add(that.get(i));
            }
        }
    }

    /** Column Matrix View. */
    private class Column extends DenseMatrix<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public F get(int i, int j) {
            if (j != 0)
                throw new IllegalArgumentException();
            return DenseVector.this.get(i);
        }

        @Override
        public int numberOfColumns() {
            return 1;
        }

        @Override
        public int numberOfRows() {
            return DenseVector.this.dimension();
        }
    }

    /** Row Matrix View. */
    private class Row extends DenseMatrix<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public F get(int i, int j) {
            if (i != 0)
                throw new IllegalArgumentException();
            return DenseVector.this.get(j);
        }

        @Override
        public int numberOfColumns() {
            return DenseVector.this.dimension();
        }

        @Override
        public int numberOfRows() {
            return 1;
        }
    }

    /** Sub-Vector View. */
    private class SubVector extends DenseVector<F> {
        private static final long serialVersionUID = DenseVector.serialVersionUID;
        private int fromIndex, dimension;

        private SubVector(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.dimension = toIndex - fromIndex;
        }

        @Override
        public int dimension() {
            return dimension;
        }

        @Override
        public F get(int i) {
            if ((i < 0) || (i >= dimension))
                throw new IllegalArgumentException();
            return get(i + fromIndex);
        }
    }

    private static final long serialVersionUID = 0x500L; // Version.

    /**
     * Returns a dense vector holding the specified elements.
     * 
     * @throws DimensionException if the vector is of zero dimension.
     */
    public static <F extends Field<F>> DenseVector<F> of(
            @Constant F... elements) {
        if (elements.length == 0)
            throw new DimensionException(
                    "Zero dimension vector are not permitted.");
        return new DenseVectorImpl<F>(elements);
    }

    /**
     * Converts (if required) the specified vector to a dense vector 
     * (convenience method).
     */
    public static <F extends Field<F>> DenseVector<F> of(Vector<F> that) {
        if (that instanceof DenseVector)
            return (DenseVector<F>) that;
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[that.dimension()];
        for (int i = 0; i < elements.length; i++)
            elements[i] = that.get(i);
        return new DenseVectorImpl<F>(elements);
    }

    @Override
    public DenseMatrix<F> column() {
        return new Column();
    }

    @Override
    public DenseVector<F> cross(Vector<F> that) {
        if ((this.dimension() != 3) || (that.dimension() != 3))
            throw new DimensionException(
                    "The cross product of two vectors requires "
                            + "3-dimensional vectors");
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[3];
        elements[0] = this.get(1).times(that.get(2))
                .plus((this.get(2).times(that.get(1))).opposite());
        elements[1] = this.get(2).times(that.get(0))
                .plus((this.get(0).times(that.get(2))).opposite());
        elements[2] = this.get(0).times(that.get(1))
                .plus((this.get(1).times(that.get(0))).opposite());
        return new DenseVectorImpl<F>(elements);
    }

    @Override
    public abstract int dimension();

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Vector)
            return equals((Vector<F>) obj, Orders.STANDARD);
        return false;
    }

    @Override
    public boolean equals(Vector<F> that, Equality<? super F> cmp) {
        int n = this.dimension();
        if (that.dimension() != n)
            return false;
        for (int i = 0; i < n; i++)
            if (!cmp.equal(this.get(i), that.get(i)))
                return false;
        return true;
    }

    @Override
    public void forEach(Consumer<? super F> consumer) {
        for (int i = 0, n = dimension(); i < n; i++) {
            consumer.accept(i, get(i));
        }
    }

    @Override
    public void forEachNonZero(Consumer<? super F> consumer) {
        int n = dimension();
        if (n == 0)
            return;
        final F zero = get(0).plus(get(0).opposite());
        for (int i = 0; i < n; i++) {
            F element = get(i);
            if (!zero.equals(element))
                consumer.accept(i, element);
        }
    }

    @Override
    public abstract F get(int i);

    @Override
    public int hashCode() {
        int n = dimension();
        if (n == 0)
            return 0;
        F zero = get(0).plus(get(0).opposite());
        int hash = 0;
        for (int i = 0; i < n; i++) {
            F element = get(i);
            if (zero.equals(element))
                continue;
            hash = 31 * hash + element.hashCode() + i;
        }
        return hash;
    }

    @Override
    public DenseVector<F> minus(Vector<F> that) {
        return this.plus(that.opposite());
    }

    @Override
    public DenseVector<F> opposite() {
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[dimension()];
        for (int i = 0; i < elements.length; i++)
            elements[i] = get(i).opposite();
        return new DenseVectorImpl<F>(elements);
    }

    @Override
    public DenseVector<F> plus(Vector<F> that) {
        final int n = this.dimension();
        if (that.dimension() != n)
            throw new DimensionException();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[n];
        for (int i = 0; i < elements.length; i++)
            elements[i] = this.get(i).plus(that.get(i));
        return new DenseVectorImpl<F>(elements);
    }

    @Override
    public DenseMatrix<F> row() {
        return new Row();
    }

    @Override
    public DenseVector<F> subVector(int fromIndex, int toIndex) {
        if ((fromIndex < 0) || (toIndex > dimension()) || (fromIndex > toIndex))
            throw new IndexOutOfBoundsException();
        return new SubVector(fromIndex, toIndex);
    }

    @Override
    public DenseMatrix<F> tensor(Vector<F> that) {
        return this.column().times(that.row());
    }

    @Override
    public DenseVector<F> times(F k) {
        final int n = this.dimension();
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[n];
        for (int i = 0; i < elements.length; i++)
            elements[i] = get(i).times(k);
        return new DenseVectorImpl<F>(elements);
    }

    @Override
    public F times(Vector<F> that) {
        int n = this.dimension();
        if ((n == 0) || (that.dimension() != n))
            throw new DimensionException();
        F sum = this.get(0).times(that.get(0));
        for (int i = 1; i < n; i++)
            sum = sum.plus(this.get(i).times(that.get(i)));
        return sum;
    }

    @Override
    public String toString() {
        return TextContext.getFormat(DenseVector.class).format(this);
    }

}
