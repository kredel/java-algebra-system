/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import edu.jas.kern.StringUtil;
import edu.jas.structure.MonoidElem;
import edu.jas.structure.MonoidFactory;


/**
 * IndexList factory implements a factory for index lists for exterior polynomials.
 * Objects of this class are intended to
 * be immutable. If in doubt use <code>valueOf</code> to
 * get a conformant index list.
 * @see "masnc.DIPE.mi#ILEXPR from SAC2/MAS"
 * @author Heinz Kredel
 */

public class IndexFactory implements MonoidFactory<IndexList> {


    /**
     * The maximal length index list for this factory.
     */
    public final int imaxlength;


    /**
     * The maximal index list for this factory.
     */
    public final IndexList imax;


    /**
     * Random number generator.
     */
    private final static Random random = new Random();


    /**
     * Log4j logger object.
     */
    private static final Logger logger = LogManager.getLogger(WordFactory.class);


    /**
     * The one element index list.
     */
    public final IndexList ONE;


    /**
     * The default imaxlength for this index lists.
     */
    public static final int DEFAULT_SIZE = 4;


    /**
     * Constructor for IndexFactory. No argument constructor .
     */
    public IndexFactory() {
        this(DEFAULT_SIZE);
    }


    /**
     * Constructor for IndexFactory.
     * @param r length of index lists.
     */
    public IndexFactory(int r) {
        if (r < 0) {
            throw new IllegalArgumentException("negative length index not allowed");
        }
        imaxlength = r;
        imax = new IndexList(this, 1, sequenceArray(0, imaxlength));
        ONE = new IndexList(this, 1, sequenceArray(0, 0));
    }


    /**
     * Constructor for IndexFactory.
     * @param o some index list.
     */
    public IndexFactory(IndexList o) {
        if (o == null) {
            throw new IllegalArgumentException("null index list not allowed");
        }
        int b = o.minDeg();
        int e = o.maxDeg();
        imaxlength = e - b;
        imax = new IndexList(this, 1, sequenceArray(0, imaxlength));
        if (imaxlength != o.length()) {
            logger.warn("length do not match: {} != {}", imaxlength, o.length());
        }
        //ONE = imax.getONE(); // ??
        ONE = new IndexList(this, 1, new int[0]);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite() <b>Note: </b> returns true
     *      because of finite set of values in each index.
     */
    public boolean isFinite() {
        return true;
    }


    /**
     * Query if this monoid is commutative.
     * @return true if this monoid is commutative, else false.
     */
    public boolean isCommutative() {
        if (imaxlength == 0) {
            return true;
        }
        return false;
    }


    /**
     * Query if this monoid is associative.
     * @return true if this monoid is associative, else false.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Get the Element for a.
     * @param a long
     * @return element corresponding to a.
     */
    @Override
    public IndexList fromInteger(long a) {
        throw new UnsupportedOperationException("not implemented for IndexFactory");
    }


    /**
     * Get the Element for a.
     * @param a java.math.BigInteger.
     * @return element corresponding to a.
     */
    @Override
    public IndexList fromInteger(java.math.BigInteger a) {
        throw new UnsupportedOperationException("not implemented for IndexFactory");
    }


    /**
     * Value of other.
     * @param e other ExpVector.
     * @return value as IndexList.
     */
    public IndexList valueOf(ExpVector e) {
        if (e == null) {
            return getZERO();
        }
        int r = e.length();
        int[] w = new int[r];
        int ii = 0;
        for (int i = 0; i < r; i++) {
            long x = e.getVal(i);
            if (x <= 0l) {
                continue;
            }
            if (x > 1l) {
                return getZERO();
            }
            w[ii++] = i;
        }
        int[] v = Arrays.copyOf(w, ii);
        return new IndexList(this, v);
    }


    /**
     * Value of other.
     * @param e String of index names.
     * @return value as IndexList.
     */
    public IndexList valueOf(String var) {
        return sequence(0, var.length());
    }


    /**
     * Value of other.
     * @param e other Collection of Integer indexes.
     * @return value as IndexList.
     */
    public IndexList valueOf(Collection<Integer> e) {
        if (e == null) {
            return getZERO();
        }
        int r = e.size();
        int[] w = new int[r];
        int ii = 0;
        for (Integer x : e) {
            int xi = (int) x;
            if (xi < 0) {
                continue;
            }
            w[ii++] = xi;
        }
        int[] v = Arrays.copyOf(w, ii);
        return new IndexList(this, v);
    }


    /**
     * Value of other.
     * @param e other int[] of indexes, may not be conform to IndexList
     *            specification.
     * @return value as IndexList.
     */
    public IndexList valueOf(int[] e) {
        if (e == null) {
            return getZERO();
        }
        int r = e.length;
        IndexList w = new IndexList(this, new int[] {}); // = 1
        int[] v = new int[1];
        for (int i = 0; i < r; i++) {
            v[0] = e[i];
            IndexList vs = new IndexList(this, v);
            w = w.exteriorProduct(vs);
            if (w.isZERO()) {
                return w;
            }
        }
        //System.out.println("valueOf: " + w);
        return w;
    }


    /**
     * Value of other.
     * @param e other IndexList, may not be conform to IndexList specification.
     * @return value as IndexList.
     */
    public IndexList valueOf(IndexList e) {
        if (e == null) {
            return getZERO();
        }
        return valueOf(e.val);
    }


    /**
     * Generators.
     * @return list of generators for this index list.
     */
    public List<IndexList> generators() {
        List<IndexList> gens = new ArrayList<IndexList>();
        //IndexList one = getONE();
        //gens.add(one);
        for (int i = 0; i < imax.val.length; i++) {
            int[] v = new int[1];
            v[0] = imax.val[i];
            IndexList vs = new IndexList(this, v);
            gens.add(vs);
        }
        //System.out.println("gens: " + gens + ", val = " + Arrays.toString(val));
        return gens;
    }


    /**
     * Clone IndexList.
     * @see java.lang.Object#clone()
     */
    @Override
    public IndexList copy(IndexList o) {
        if (o == null) {
            return o;
        }
        return o.copy();
    }


    /**
     * Get the maximal length of index lists of this factory.
     * @return imaxlength.
     */
    public int length() {
        return imaxlength;
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(imax.toString());
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        return imax.toScript();
    }


    /**
     * Constructor for IndexList. Converts a String representation to an
     * IndexList. Accepted format = E(1,2,3,4,5,6,7).
     * @param s String representation.
     */
    // public IndexFactory(String s) throws NumberFormatException {
    //     this(parse(s).val);
    // }


    /**
     * Parser for IndexList. Converts a String representation to an IndexList.
     * Accepted format = E(1,2,3,4,5,6,7).
     * @param s String representation.
     * @return parsed IndexList
     */
    public IndexList parse(String s) throws NumberFormatException {
        int[] v = null;
        // first format = (1,2,3,4,5,6,7)
        List<Integer> idxs = new ArrayList<Integer>();
        s = s.trim();
        int b = s.indexOf('(');
        int e = s.indexOf(')', b + 1);
        String teil;
        int k;
        int a;
        if (b >= 0 && e >= 0) {
            b++;
            while ((k = s.indexOf(',', b)) >= 0) {
                teil = s.substring(b, k);
                a = Integer.parseInt(teil);
                idxs.add(Integer.valueOf(a));
                b = k + 1;
            }
            if (b <= e) {
                teil = s.substring(b, e);
                a = Integer.parseInt(teil);
                idxs.add(Integer.valueOf(a));
            }
            int length = idxs.size();
            v = new int[length];
            for (int j = 0; j < length; j++) {
                v[j] = idxs.get(j).intValue();
            }
        }
        return valueOf(v); // sort and compute sign
    }


    /**
     * Parse from Reader. White space is delimiter for index list.
     * @param r Reader.
     * @return the next Element found on r.
     */
    public IndexList parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof IndexFactory)) {
            return false;
        }
        IndexFactory b = (IndexFactory) B;
        int t = imax.compareTo(b.imax);
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /**
     * hashCode. Optimized for small indexs, i.e. &le; 2<sup>4</sup> and small
     * number of variables, i.e. &le; 8.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = imax.hashCode();
        return hash;
    }


    /**
     * Get IndexList zero.
     * @return 0 IndexList.
     */
    public IndexList getZERO() {
        return new IndexList(this);
    }


    /**
     * Get IndexList one.
     * @return 1 IndexList.
     */
    public IndexList getONE() {
        return ONE; //?? .copy()
    }


    /**
     * Generate a random IndexList.
     * @param r length of new IndexList.
     * @return random IndexList.
     */
    public final IndexList random(int r) {
        return random(r, 0.5f, random);
    }


    /**
     * Generate a random IndexList.
     * @param r length of new IndexList.
     * @param rnd is a source for random bits.
     * @return random IndexList.
     */
    public final IndexList random(int r, Random rnd) {
        return random(r, 0.5f, rnd);
    }


    /**
     * Generate a random IndexList.
     * @param r length of new IndexList.
     * @param q density of nozero indexes.
     * @return random IndexList.
     */
    public final IndexList random(int r, float q) {
        return random(r, q, random);
    }


    /**
     * Generate a random IndexList.
     * @param r length of new IndexList.
     * @param q density of nozero indexes.
     * @param rnd is a source for random bits.
     * @return random IndexList.
     */
    public final IndexList random(int r, float q, Random rnd) {
        if (r > imaxlength || r < 0) {
            throw new IllegalArgumentException("r > imaxlength not allowed: " + r + " > " + imaxlength);
        }
        int[] w = new int[r];
        int s = 1;
        float f;
        f = rnd.nextFloat();
        if (f < q * 0.001f) {
            return getZERO(); // = 0
        }
        if (f < q * q) {
            s = -1;
        }
        int ii = 0;
        for (int i = 0; i < w.length; i++) {
            f = rnd.nextFloat();
            if (f < q) {
                w[ii++] = i;
            }
        }
        int[] v = Arrays.copyOf(w, ii);
        //System.out.println("v = " + Arrays.toString(v));
        //System.out.println("this = " + this);
        return new IndexList(this, s, v);
    }


    /**
     * Generate a sequence IndexList.
     * @param s starting index.
     * @param r length of new IndexList.
     * @return sequence (s, s+1, ..., s+r-1) IndexList.
     */
    public final IndexList sequence(int s, int r) {
        return new IndexList(this, 1, sequenceArray(s, r));
    }


    /**
     * Generate a sequence array.
     * @param s starting index.
     * @param r length of array.
     * @return sequence (s, s+1, ..., s+r-1) array.
     */
    public static int[] sequenceArray(int s, int r) {
        if (s < 0) {
            throw new IllegalArgumentException("s < 0 not allowed: " + s + " < 0");
        }
        int[] w = new int[r];
        for (int i = 0; i < w.length; i++) {
            w[i] = s + i;
        }
        //System.out.println("v = " + Arrays.toString(w));
        return w;
    }


    /**
     * Comparator for IndexLists.
     */
    public static abstract class IndexListComparator implements Comparator<IndexList>, Serializable {


        public abstract int compare(IndexList e1, IndexList e2);
    }


    /**
     * Defined descending order comparator. Sorts the highest terms first.
     */
    private final IndexListComparator horder = new IndexListComparator() {


        @Override
        public int compare(IndexList e1, IndexList e2) {
            return -e1.strongCompareTo(e2);
        }
    };


    /**
     * Defined ascending order comparator. Sorts the lowest terms first.
     */
    private final IndexListComparator lorder = new IndexListComparator() {


        @Override
        public int compare(IndexList e1, IndexList e2) {
            return e1.strongCompareTo(e2);
        }
    };


    /**
     * Get the descending order comparator. Sorts the highest terms first.
     * @return horder.
     */
    public IndexListComparator getDescendComparator() {
        return horder;
    }


    /**
     * Get the ascending order comparator. Sorts the lowest terms first.
     * @return lorder.
     */
    public IndexListComparator getAscendComparator() {
        return lorder;
    }

}
