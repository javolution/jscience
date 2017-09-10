/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2014 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.matrix;

import static javolution.lang.Realtime.Limit.LINEAR;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javolution.lang.Constant;
import javolution.lang.Realtime;
import javolution.lang.ExceptionWrapper;
import javolution.text.Cursor;
import javolution.text.DefaultTextFormat;
import javolution.text.TextContext;
import javolution.text.TextFormat;
import javolution.text.TypeFormat;
import javolution.util.ConstantTable;
import javolution.util.FastMap;
import javolution.util.FastSortedTable;
import javolution.util.Index;
import javolution.util.function.Orders;
import javolution.util.function.Equality;
import javolution.xml.DefaultXMLFormat;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import org.jscience.mathematics.internal.matrix.SparseVectorImpl;
import org.jscience.mathematics.matrix.Vector.Consumer;
import org.jscience.mathematics.structure.Field;

/**
 * <p> A {@link Vector vector} with a larger number of elements equals to 
 *     zero.</p>
 *     
  * <p> This abstract class minimizes the effort required to implement the
 *     {@link Vector vector} interface for sparse vectors.<p>
    
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 5.0, February 16, 2014
 * @see DenseVector
 */
@Constant
@DefaultTextFormat(SparseVector.Text.class)
@DefaultXMLFormat(SparseVector.XML.class)
public abstract class SparseVector<F extends Field<F>> implements Vector<F>,
        Serializable {

    /** 
     * A sparse vector entry.
     */
    @Constant
    public static class Entry<F extends Field<F>> implements Map.Entry<Index, F>, Serializable {
        
        private static final long serialVersionUID = 0x500L; // Version.
        private int index;
        private F value;

        /** Creates an entry at the specified index having the specified value. */
        public Entry(int index, F value) {
            this.index = index;
            this.value = value;
        }

        /** Returns this entry's index. */
        public int getIndex() {
            return index;
        }

        /** Returns this entry's value. */
        public F getValue() {
            return value;
        }

        /** Throws UnsupportedOperationException. */
        public F setValue(F value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Index getKey() {
            return Index.of(index);
        }
    }
    

    /** Column Matrix View. */
    private class Column extends SparseMatrix<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public F get(int i, int j) {
            if (j != 0)
                throw new IllegalArgumentException();
            return SparseVector.this.get(i);
        }

        @Override
        public int numberOfColumns() {
            return 1;
        }

        @Override
        public int numberOfRows() {
            return SparseVector.this.dimension();
        }
    }

    /** Row Matrix View. */
    private class Row extends SparseMatrix<F> {
        private static final long serialVersionUID = 0x500L; // Version.

        @Override
        public F get(int i, int j) {
            if (i != 0)
                throw new IllegalArgumentException();
            return SparseVector.this.get(j);
        }

        @Override
        public int numberOfColumns() {
            return SparseVector.this.dimension();
        }

        @Override
        public int numberOfRows() {
            return 1;
        }
    }

    /** Sub-Vector View. */
    private class SubVector extends SparseVector<F> {
        private static final long serialVersionUID = SparseVector.serialVersionUID;
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

    /**
     * Defines the default text format for sparse vectors (mapping index-elements). 
     */
    public static class Text extends TextFormat<SparseVector<?>> {

        @SuppressWarnings("rawtypes")
        @Override
        public Appendable format(SparseVector<?> that, final Appendable dest)
                throws IOException {
            dest.append('{');
            try {
                that.forEachNonZero(new Consumer<Object>() {
                    int count = 0;

                    @Override
                    public void accept(int index, Object element) {
                        try {
                            if (count++ != 0)
                                dest.append(", ");
                            TypeFormat.format(index, dest);
                            dest.append("=>");
                            TextContext.format(element, dest);
                        } catch (IOException e) {
                            new ExceptionWrapper(e);
                        }
                    }
                });
            } catch (ExceptionWrapper e) {
                e.rethrow(IOException.class);
            }
            return dest.append('}');
        }

        @Override
        public SparseVector<?> parse(CharSequence csq, Cursor cursor)
                throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Defines the default XML representation for sparse vectors. For example:
     * [code]
     * <SparseVector dimension="256">
     *     <Zero class="org.jscience.mathematics.number.Rational" value="0" />
     *     <Index value="58"/><org.jscience.mathematics.number.Rational" value="5/3"/>
     *     <Index value="203"/><org.jscience.mathematics.number.Rational" value="1/3"/>
     * </SparseVector>[/code]
     */
    public static class XML extends XMLFormat<SparseVector<?>> {

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public SparseVector<?> newInstance(
                Class<? extends SparseVector<?>> cls, InputElement xml)
                throws XMLStreamException {
            int n = xml.getAttribute("dimension", 0);
            Field zero = xml.get("Zero");
            SparseVectorImpl vector = new SparseVectorImpl(n, zero);
            while (xml.hasNext()) {
                Index i = xml.get("Index", Index.class);
                vector.setIfAbsent(i.intValue(), (Field) xml.getNext());
            }
            return vector;
        }

        @Override
        public void read(InputElement xml, SparseVector<?> v)
                throws XMLStreamException {
            // Do nothing (already created).
        }

        @Override
        public void write(SparseVector<?> that, final OutputElement xml)
                throws XMLStreamException {
            xml.setAttribute("dimension", that.dimension());
            xml.add(that.getZero(), "Zero");
            try {
                that.forEachNonZero(new Consumer<Object>() {
                    @Override
                    public void accept(int index, Object element) {
                        try {
                            xml.add(Index.of(index), "Index", Index.class);
                            xml.add(element);
                        } catch (XMLStreamException e) {
                            throw new ExceptionWrapper(e);
                        }
                    }
                });
            } catch (ExceptionWrapper e) {
                e.rethrow(XMLStreamException.class);
            }
        }
    }

    private static final long serialVersionUID = 0x500L; // Version.

    /**
     * Returns a sparse vector of the specified dimension holding the 
     * specified elements at the specified indices.
     *
     * @param dimension the sparse vector dimension.
     * @param zero the value of the element not set.
     * @param indices the indices of the non-zero elements.
     * @param elements the elements corresponding to the specified indices.
     * @return the corresponding sparse vector.
     * @throws IllegalArgumentException if {@code indices.size() != elements.length}
     */
    public static <F extends Field<F>> SparseVector<F> of(int dimension,
            F zero, Entry<F>... elements) {
        if (indices.size() != elements.length)
            throw new IllegalArgumentException();
        SparseVectorImpl<F> vector = new SparseVectorImpl<F>(dimension, zero);
        int j = 0;
        for (Index i : indices)
            vector.setIfAbsent(i.intValue(), elements[j++]);
        return vector;
    }

    /**
     * Converts (if required) the specified vector to a dense vector 
     * (convenience method).
     * 
     * @param that the vector to convert.
     */
    public static <F extends Field<F>> SparseVector<F> of(Vector<F> that) {
        if (that instanceof SparseVector)
            return (SparseVector<F>) that;
        int n = that.dimension();
        F zero = (n > 0) ? that.get(0).plus(that.get(0).opposite()) : null;
        final SparseVectorImpl<F> vector = new SparseVectorImpl<F>(n, zero);
        that.forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int index, F element) {
                vector.setIfAbsent(index, element);
            }
        });
        return vector;
    }

    @Override
    public SparseMatrix<F> row() {
        return new Row();
    }

    @Override
    public SparseMatrix<F> column() {
        return new Column();
    }

    @Override
    public int hashCode() {
        final AtomicInteger hash = new AtomicInteger();
        forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                hash.set(31 * hash.get() + element.hashCode() + i);
            }
        });
        return hash.get();
    }

    @Override
    public Vector<F> cross(Vector<F> that) {
        return DenseVector.of(this).cross(that);
    }

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
    public boolean equals(final Vector<F> that, final Equality<? super F> cmp) {
        int n = this.dimension();
        if (that.dimension() != n)
            return false;
        final AtomicBoolean areEqual = new AtomicBoolean();
        this.forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                if (!cmp.equal(element, that.get(i)))
                    areEqual.set(false);
            }
        });
        if (!areEqual.get()) return false;
        that.forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                if (!cmp.equal(SparseVector.this.get(i), element))
                    areEqual.set(false);
            }
        });
        return areEqual.get();
    }

    @Override
    public abstract F get(int i);

    @Override
    public abstract int dimension();

    /**
     * Returns the zero element of this sparse vector. 
     */
    public abstract F getZero();

    @Override
    public SparseVector<F> subVector(int fromIndex, int toIndex) {
        if ((fromIndex < 0) || (toIndex > dimension()) || (fromIndex > toIndex))
            throw new IndexOutOfBoundsException();
        return new SubVector(fromIndex, toIndex);
    }


    @Override
    public Vector<F> minus(Vector<F> that) {
        return this.plus(that.opposite());
    }

    @Override
    public SparseVector<F> opposite() {
        @SuppressWarnings("unchecked")
        F[] elements = (F[]) new Field[getIndices().size()];
        
 
        final SparseVectorImpl<F> result = new SparseVectorImpl<F>(dimension(), getZero());
        forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                result.setIfAbsent(i, element.opposite());
            }});
        return result;
    }

    @Override
    public Vector<F> plus(final Vector<F> that) {
        final SparseVectorImpl<F> result = this.clone(); 
        that.forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                result.add(i, element); 
            }});
        return result;
    }

    @Override
    public SparseMatrix<F> tensor(Vector<F> that) {
        return this.column().times(that.row());
    }

    @Override
    public SparseVector<F> times(final F k) {
        final SparseVectorImpl<F> result = new SparseVectorImpl<F>(dimension(), getZero());
        forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                result.set(i, element.times(k));
            }});
        return result;
    }

    @Override
    public F times(final Vector<F> that) {
        int n = this.dimension();
        if (that.dimension() != n)
            throw new DimensionException();
        final Reference sum = new Reference();
        sum.value = getZero();
        forEachNonZero(new Consumer<F>() {
            @Override
            public void accept(int i, F element) {
                F product = element.times(that.get(i));
                sum.value = sum.value.plus(product);
            }});
        return sum.value;
    }
    private class Reference { F value; }
    
    @Override
    public void forEach(Consumer<? super F> consumer) {
        for (int i = 0, n = dimension(); i < n; i++) {
            consumer.accept(i, get(i));
        }
    }

    @Override
    public String toString() {
        return TextContext.getFormat(SparseVector.class).format(this);
    }
    
}
