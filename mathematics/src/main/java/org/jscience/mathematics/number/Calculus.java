/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package org.jscience.mathematics.number;

import javolution.context.ObjectFactory;

/**
 * <p> This class holds utilities upon arrays of positive <code>long</code>.</p>
 *     
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.3, January 14, 2006
 */
final class Calculus {

    /**
     * Default constructor (private for utilities).
     */
    private Calculus() {
    }

    static final long MASK_63 = 0x7FFFFFFFFFFFFFFFL;

    static final long MASK_32 = 0xFFFFFFFFL;

    static final long MASK_31 = 0x7FFFFFFFL;

    static final long MASK_8 = 0xFFL;

    /**
     * x += y
     * @return x size
     */
    static int add(long[] x, int xSize, long y) {
        long sum = x[0] + y;
        x[0] = sum & MASK_63;
        int i = 1;
        sum >>>= 63;
        while (sum != 0) {
            if (i == xSize) {
                x[xSize] = sum;
                return xSize + 1;
            }
            sum += x[i];
            x[i++] = sum & MASK_63;
            sum >>>= 63;
        }
        return xSize;
    }

    /**
     * z = x + y
     * Preconditions: xSize >= ySize
     * @return z size
     */
    static int add(long[] x, int xSize, long[] y, int ySize, long[] z) {
        long sum = 0;
        int i = 0;
        while (i < ySize) {
            sum += x[i] + y[i];
            z[i++] = sum & MASK_63;
            sum >>>= 63;
        }
        while (true) {
            if (sum == 0) {
                while (i < xSize) {
                    z[i] = x[i++];
                }
                return xSize;
            }
            if (i == xSize) {
                z[xSize] = sum;
                return xSize + 1;
            }
            sum += x[i];
            z[i++] = sum & MASK_63;
            sum >>>= 63;
        }
    }

    /**
     * z = x - y
     * Preconditions: x >= y
     * @return z size
     */
    static int subtract(long[] x, int xSize, long[] y, int ySize, long[] z) {
        long diff = 0;
        int i = 0;
        while (i < ySize) {
            diff += x[i] - y[i];
            z[i++] = diff & MASK_63;
            diff >>= 63; // Equals to -1 if borrow.
        }
        while (diff != 0) {
            diff += x[i];
            z[i++] = diff & MASK_63;
            diff >>= 63; // Equals to -1 if borrow.
        }
        // Copies rest of x to z.
        while (i < xSize) {
            z[i] = x[i++];
        }
        // Calculates size.
        for (int j = xSize; j > 0;) {
            if (z[--j] != 0)
                return j + 1;
        }
        return 0;
    }

    /**
     * x.compare(y)
     * Preconditions: xSize = ySize = size
     * @return 1, -1, 0 
     */
    static int compare(long[] x, long[] y, int size) {
        for (int i = size; --i >= 0;) {
            if (x[i] > y[i])
                return 1;
            if (x[i] < y[i])
                return -1;
        }
        return 0;
    }

    /**
     * x << n
     * Preconditions: xSize != 0
     * @return z size 
     */
    static int shiftLeft(int wordShift, int bitShift, long[] x, int xSize,
            long[] z) {
        final int shiftRight = 63 - bitShift;
        int i = xSize;
        int j = xSize + wordShift;
        long tmp = x[--i];
        long high = tmp >>> shiftRight;
        if (high != 0) {
            z[j] = high;
        }
        while (i > 0) {
            z[--j] = ((tmp << bitShift) & MASK_63)
                    | ((tmp = x[--i]) >>> shiftRight);
        }
        z[--j] = (tmp << bitShift) & MASK_63;
        while (j > 0) {
            z[--j] = 0;
        }
        return (high != 0) ? xSize + wordShift + 1 : xSize + wordShift;
    }

    /**
     * x >> n
     * Preconditions: xSize > wordShift
     * @return z size 
     */
    static int shiftRight(int wordShift, int bitShift, long[] x, int xSize,
            long[] z) {
        final int shiftLeft = 63 - bitShift;
        int i = wordShift;
        int j = 0;
        long tmp = x[i];
        while (i < xSize - 1) {
            z[j++] = (tmp >>> bitShift) | ((tmp = x[++i]) << shiftLeft)
                    & MASK_63;
        }
        tmp >>>= bitShift;
        z[j] = tmp;
        return (tmp != 0) ? j + 1 : j;
    }

    /**
     * z = x * y
     * Preconditions: y != 0, x != 0
     * @return z size 
     */
    static int multiply(long[] x, int xSize, long y, long[] z) {
        return multiply(x, xSize, y, z, 0);
    }

    /**
     * z = x * y
     * Preconditions: y != 0, xSize >= ySize
     * @return z size 
     */
    static int multiply(long[] x, int xSize, long[] y, int ySize, long[] z) {
        int zSize = 0;
        for (int i = 0; i < ySize;) {
            zSize = multiply(x, xSize, y[i], z, i++);
        }
        return zSize;
    }

    // Multiplies by k, add to z if shift != 0
    private static int multiply(long[] x, int xSize, long k, long[] z, int shift) {

        final long kl = k & MASK_32; // 32 bits.
        final long kh = k >> 32; // 31 bits

        long carry = 0; // 63 bits
        for (int i = 0, j = shift; i < xSize;) {

            // Adds carry.
            long zz = (shift == 0) ? carry : z[j] + carry; // 63 bits.
            carry = zz >>> 63;
            zz &= MASK_63; // 63 bits.

            // Splits words in [31 bits][32 bits]
            final long w = x[i++];
            final long wl = w & MASK_32; // 32 bits
            final long wh = w >> 32; // 31 bits

            // Adds low.
            long tmp = wl * kl; // 64 bits
            carry += tmp >>> 63;
            zz += tmp & MASK_63; // 64 bits.
            carry += zz >>> 63;
            zz &= MASK_63;

            // Adds middle.
            tmp = wl * kh + wh * kl; // 64 bits.
            carry += tmp >>> 31;
            zz += (tmp << 32) & MASK_63; // 64 bits.
            carry += zz >>> 63;
            z[j++] = zz & MASK_63;

            // Adds high to carry.
            carry += (wh * kh) << 1;

        }
        int size = shift + xSize;
        z[size] = carry;
        if (carry == 0)
            return size;
        return ++size;
    }

    /**
     * z = x / y
     * Preconditions: y is positive (31 bits).
     * @return remainder 
     */
    static long divide(long[] x, int xSize, int y, long[] z) {
        long r = 0;
        for (int i = xSize; i > 0;) {
            long w = x[--i];

            long wh = (r << 31) | (w >>> 32);
            long qh = wh / y;
            r = wh - qh * y;

            long wl = (r << 32) | (w & MASK_32);
            long ql = wl / y;
            r = wl - ql * y;

            z[i] = (qh << 32) | ql;
        }
        return r;
    }

    /**
     * Multiplication logic (for concurrent context)
     */
    static final class MultiplyLogic implements Runnable {
        private static final ObjectFactory<MultiplyLogic> FACTORY =
            new ObjectFactory<MultiplyLogic>() {
                @Override
                protected MultiplyLogic create() {
                    return new MultiplyLogic();
                }
        };
        private LargeInteger _left, _right, _value;
        
        public static MultiplyLogic newInstance(LargeInteger left,
                LargeInteger right) {
            MultiplyLogic logic = FACTORY.object();
            logic._left = left;
            logic._right = right;
            return logic;
        }
        public void run() {
           _value = _left.times(_right);// Recursive.
        }
        public LargeInteger value() {
            return _value;
        }
    };

}