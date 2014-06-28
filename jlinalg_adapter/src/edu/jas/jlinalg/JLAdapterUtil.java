/*
 * $Id$
 */

package edu.jas.jlinalg;


// import static edu.jas.jlinalg.JLAdapterUtil.toArray;

import java.util.ArrayList;
import java.util.List;

import org.jlinalg.Matrix;
import org.jlinalg.Vector;

import edu.jas.structure.RingElem;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenVectorModul;


/**
 * @author Heinz Kredel
 * 
 */

public class JLAdapterUtil {


    public static <C extends RingElem<C>> C[] toArray(GenVector<C> a) {
        if (a == null) {
            return null;
        }
        return toArray(a.val);
    }


    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C[] toArray(List<C> a) {
        if (a == null) {
            return null;
        }
        C[] av = (C[]) new RingElem[a.size()];
        int i = 0;
        for (C e : a) {
            av[i++] = e;
        }
        return av;
    }


    public static <C extends RingElem<C>> ArrayList<C> toList(C[] a) {
        if (a == null) {
            return null;
        }
        ArrayList<C> av = new ArrayList<C>(a.length);
        for (int i = 0; i < a.length; i++) {
            av.add(a[i]);
        }
        return av;
    }


    public static <C extends RingElem<C>> ArrayList<ArrayList<C>> toList(C[][] a) {
        if (a == null) {
            return null;
        }
        ArrayList<ArrayList<C>> av = new ArrayList<ArrayList<C>>(a.length);
        for (int i = 0; i < a.length; i++) {
            av.add(JLAdapterUtil.<C> toList(a[i]));
        }
        return av;
    }


    public static <C extends RingElem<C>> C[][] toArray(GenMatrix<C> a) {
        if (a == null) {
            return null;
        }
        return toArrayFromList(a.matrix);
    }


    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C[][] toArrayFromList(List<ArrayList<C>> a) { // Array only once
        if (a == null) {
            return null;
        }
        C[][] av = (C[][]) new RingElem[a.size()][];
        int i = 0;
        for (List<C> e : a) {
            av[i++] = toArray(e);
        }
        return av;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v array of ring elements
     * @return array of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[] toJLAdapter(C[] v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[] va = (JLAdapter<C>[]) new JLAdapter[v.length];
        for (int i = 0; i < v.length; i++) {
            va[i] = new JLAdapter<C>(v[i]);
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v array of ring elements
     * @return array of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[] toJLAdapterRE(RingElem<C>[] v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[] va = (JLAdapter<C>[]) new JLAdapter[v.length];
        for (int i = 0; i < v.length; i++) {
            va[i] = new JLAdapter<C>((C) v[i]);
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v JAS vector of ring elements
     * @return array of JLAdapter objects
     */
    public static <C extends RingElem<C>> JLAdapter<C>[] toJLAdapter(GenVector<C> v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[] va = JLAdapterUtil.<C> toJLAdapter(v.val);
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v list of ring elements
     * @return array of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[] toJLAdapter(List<C> v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[] va = (JLAdapter<C>[]) new JLAdapter[v.size()];
        for (int i = 0; i < v.size(); i++) {
            va[i] = new JLAdapter<C>(v.get(i));
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v JAS vector of ring elements
     * @return JLinAlg vector of JLAdapter objects
     */
    public static <C extends RingElem<C>> Vector<JLAdapter<C>> toJLAdapterVector(GenVector<C> v) {
        if (v == null) {
            return null;
        }
        Vector<JLAdapter<C>> va = new Vector<JLAdapter<C>>(JLAdapterUtil.<C> toJLAdapter(v.val));
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v matrix of ring elements
     * @return matrix of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[][] toJLAdapter(C[][] v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[][] va = (JLAdapter<C>[][]) new JLAdapter[v.length][];
        for (int i = 0; i < v.length; i++) {
            va[i] = JLAdapterUtil.<C> toJLAdapter(v[i]);
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v matrix of ring elements
     * @return matrix of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[][] toJLAdapterRE(RingElem<C>[][] v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[][] va = (JLAdapter<C>[][]) new JLAdapter[v.length][];
        for (int i = 0; i < v.length; i++) {
            va[i] = JLAdapterUtil.<C> toJLAdapterRE(v[i]);
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v JAS matrix of ring elements
     * @return array of JLAdapter objects
     */
    public static <C extends RingElem<C>> JLAdapter<C>[][] toJLAdapter(GenMatrix<C> v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[][] va = JLAdapterUtil.<C> toJLAdapterFromList(v.matrix);
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v list of lists of ring elements
     * @return array of JLAdapter objects
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> JLAdapter<C>[][] toJLAdapterFromList(List<ArrayList<C>> v) {
        if (v == null) {
            return null;
        }
        JLAdapter<C>[][] va = (JLAdapter<C>[][]) new JLAdapter[v.size()][];
        for (int i = 0; i < v.size(); i++) {
            va[i] = JLAdapterUtil.<C> toJLAdapter(v.get(i));
        }
        return va;
    }


    /**
     * Convert JAS <code>RingElem</code> to JLinAlg <code>IRingElement</code>.
     * @param <C> ring element type
     * @param v JAS vector of ring elements
     * @return JLinAlg vector of JLAdapter objects
     */
    public static <C extends RingElem<C>> Matrix<JLAdapter<C>> toJLAdapterMatrix(GenMatrix<C> v) {
        if (v == null) {
            return null;
        }
        Matrix<JLAdapter<C>> va = new Matrix<JLAdapter<C>>(JLAdapterUtil.<C> toJLAdapterFromList(v.matrix));
        return va;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v array of JLAdapter objects
     * @return array of ring elements
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C[] fromJLAdapter(JLAdapter<C>[] v) {
        if (v == null) {
            return null;
        }
        C[] va = (C[]) new RingElem[v.length];
        for (int i = 0; i < v.length; i++) {
            if (v[i] != null) {
                va[i] = v[i].val;
            } else {
                va[i] = null;
            }
        }
        return va;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v matrix of JLAdapter objects
     * @return matrix of ring elements
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C[][] fromJLAdapter(JLAdapter<C>[][] v) {
        if (v == null) {
            return null;
        }
        C[][] va = (C[][]) new RingElem[v.length][];
        for (int i = 0; i < v.length; i++) {
            va[i] = JLAdapterUtil.<C> fromJLAdapter(v[i]);
        }
        return va;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v JLinAlg vector of JLAdapter objects
     * @return array of ring elements
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> C[] fromJLAdapter(Vector<JLAdapter<C>> v) {
        if (v == null) {
            return null;
        }
        C[] va = (C[]) new RingElem[v.length()];
        for (int i = 0; i < va.length; i++) {
            JLAdapter<C> e = v.getEntry(i + 1);
            if (e != null) {
                va[i] = e.val;
            } else {
                va[i] = null;
            }
        }
        return va;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v JLinAlg vector of JLAdapter objects
     * @return Java list of ring elements
     */
    public static <C extends RingElem<C>> ArrayList<C> listFromJLAdapter(Vector<JLAdapter<C>> v) {
        if (v == null) {
            return null;
        }
        ArrayList<C> vv = new ArrayList<C>(v.length());
        for (int i = 0; i < v.length(); i++) {
            JLAdapter<C> e = v.getEntry(i + 1);
            if (e != null) {
                vv.add(e.val);
            } else {
                vv.add(null);
            }
        }
        return vv;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v JLinAlg vector of JLAdapter objects
     * @return JAS vector of ring elements
     */
    public static <C extends RingElem<C>> GenVector<C> vectorFromJLAdapter(GenVectorModul<C> fac,
                    Vector<JLAdapter<C>> v) {
        if (v == null) {
            return null;
        }
        List<C> list = listFromJLAdapter(v);
        GenVector<C> vv = new GenVector<C>(fac, list);
        return vv;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v JLinAlg vector of JLAdapter objects
     * @return Java list of ring elements
     */
    public static <C extends RingElem<C>> List<List<C>> listFromJLAdapter(Matrix<JLAdapter<C>> v) {
        if (v == null) {
            return null;
        }
        ArrayList<List<C>> vv = new ArrayList<List<C>>(v.getRows());
        for (int i = 0; i < v.getRows(); i++) {
            Vector<JLAdapter<C>> e = v.getRow(i + 1);
            List<C> l = listFromJLAdapter(e);
            vv.add(l);
        }
        return vv;
    }


    /**
     * Convert JLinAlg <code>IRingElement</code> to JAS <code>RingElem</code>
     * to.
     * @param <C> ring element type
     * @param v JLinAlg vector of JLAdapter objects
     * @return JAS matrix of ring elements
     */
    public static <C extends RingElem<C>> GenMatrix<C> matrixFromJLAdapter(GenMatrixRing<C> fac,
                    Matrix<JLAdapter<C>> v) {
        if (v == null) {
            return null;
        }
        List<List<C>> list = listFromJLAdapter(v);
        GenMatrix<C> vv = new GenMatrix<C>(fac, list);
        return vv;
    }

}
