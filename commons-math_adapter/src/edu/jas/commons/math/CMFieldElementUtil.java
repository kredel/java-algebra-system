/*
 * $Id$
 */

package edu.jas.commons.math;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.BlockFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;

import edu.jas.structure.RingElem;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenVectorModul;

/**
 * Conversion methods from JAS to commons-math and vice versa.
 * 
 */
public class CMFieldElementUtil {

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
      av.add(CMFieldElementUtil.<C> toList(a[i]));
    }
    return av;
  }

  public static <C extends RingElem<C>> C[][] toArray(GenMatrix<C> a) {
    if (a == null) {
      return null;
    }
    return toArrayFromMatrix(a.matrix);
  }

  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> C[][] toArrayFromMatrix(List<ArrayList<C>> a) { // Array
    // only
    // once
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
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          array of ring elements
   * @return array of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[] toCMFieldElement(C[] v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[] va = (CMFieldElement<C>[]) new CMFieldElement[v.length];
    for (int i = 0; i < v.length; i++) {
      va[i] = new CMFieldElement<C>(v[i]);
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          array of ring elements
   * @return array of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[] toCMFieldElementRE(
      RingElem<C>[] v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[] va = (CMFieldElement<C>[]) new CMFieldElement[v.length];
    for (int i = 0; i < v.length; i++) {
      va[i] = new CMFieldElement<C>((C) v[i]);
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          JAS vector of ring elements
   * @return array of CMFieldElement objects
   */
  public static <C extends RingElem<C>> CMFieldElement<C>[] toCMFieldElement(
      GenVector<C> v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[] va = CMFieldElementUtil.<C> toCMFieldElement(v.val);
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          list of ring elements
   * @return array of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[] toCMFieldElement(List<C> v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[] va = (CMFieldElement<C>[]) new CMFieldElement[v.size()];
    for (int i = 0; i < v.size(); i++) {
      va[i] = new CMFieldElement<C>(v.get(i));
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          JAS vector of ring elements
   * @return commons-math vector of CMFieldElementr objects
   */
  public static <C extends RingElem<C>> FieldVector<CMFieldElement<C>> toCMFieldElementVector(
      GenVector<C> v) {
    if (v == null) {
      return null;
    }
    return new ArrayFieldVector<CMFieldElement<C>>(CMFieldElementUtil
        .<C> toCMFieldElement(v.val));
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          matrix of ring elements
   * @return matrix of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[][] toCMFieldElement(C[][] v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[][] va = (CMFieldElement<C>[][]) new CMFieldElement[v.length][];
    for (int i = 0; i < v.length; i++) {
      va[i] = CMFieldElementUtil.<C> toCMFieldElement(v[i]);
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          matrix of ring elements
   * @return matrix of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[][] toCMFieldElementRE(
      RingElem<C>[][] v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[][] va = (CMFieldElement<C>[][]) new CMFieldElement[v.length][];
    for (int i = 0; i < v.length; i++) {
      va[i] = CMFieldElementUtil.<C> toCMFieldElementRE(v[i]);
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          JAS matrix of ring elements
   * @return matrix of CMFieldElement objects
   */
  public static <C extends RingElem<C>> CMFieldElement<C>[][] toCMFieldElement(
      GenMatrix<C> v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[][] va = CMFieldElementUtil.<C> toCMFieldElementFromMatrix(v.matrix);
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          list of lists of ring elements
   * @return array of CMFieldElement objects
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> CMFieldElement<C>[][] toCMFieldElementFromMatrix(
      List<ArrayList<C>> v) {
    if (v == null) {
      return null;
    }
    CMFieldElement<C>[][] va = (CMFieldElement<C>[][]) new CMFieldElement[v.size()][];
    for (int i = 0; i < v.size(); i++) {
      va[i] = CMFieldElementUtil.<C> toCMFieldElement(v.get(i));
    }
    return va;
  }

  /**
   * Convert JAS <code>RingElem</code> to commons-math <code>FieldElement</code>.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          JAS vector of ring elements
   * @return commons-math FieldMatrix of CMFieldElement objects
   */
  public static <C extends RingElem<C>> FieldMatrix<CMFieldElement<C>> toCMFieldMatrix(
      GenMatrix<C> v) {
    if (v == null) {
      return null;
    }
    FieldMatrix<CMFieldElement<C>> va = new BlockFieldMatrix<CMFieldElement<C>>(CMFieldElementUtil
        .<C> toCMFieldElementFromMatrix(v.matrix));
    return va;
  }

  /**
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          array of CMFieldElement objects
   * @return array of ring elements
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> C[] fromCMFieldElement(CMFieldElement<C>[] v) {
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
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          matrix of CMFieldElement objects
   * @return matrix of ring elements
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> C[][] fromCMFieldElement(CMFieldElement<C>[][] v) {
    if (v == null) {
      return null;
    }
    C[][] va = (C[][]) new RingElem[v.length][];
    for (int i = 0; i < v.length; i++) {
      va[i] = CMFieldElementUtil.<C> fromCMFieldElement(v[i]);
    }
    return va;
  }

  /**
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          Commons-math vector of CMFieldElement objects
   * @return array of ring elements
   */
  @SuppressWarnings("unchecked")
  public static <C extends RingElem<C>> C[] fromCMFieldVector(FieldVector<CMFieldElement<C>> v) {
    if (v == null) {
      return null;
    }
    C[] va = (C[]) new RingElem[v.getDimension()];
    for (int i = 0; i < va.length; i++) {
      CMFieldElement<C> e = v.getEntry(i);
      if (e != null) {
        va[i] = e.val;
      } else {
        va[i] = null;
      }
    }
    return va;
  }

  /**
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          commons-math vector of CMFieldElement objects
   * @return Java list of ring elements
   */
  public static <C extends RingElem<C>> ArrayList<C> listFromCMFieldVector(
      FieldVector<CMFieldElement<C>> v) {
    if (v == null) {
      return null;
    }
    ArrayList<C> vv = new ArrayList<C>(v.getDimension());
    for (int i = 0; i < v.getDimension(); i++) {
      CMFieldElement<C> e = v.getEntry(i);
      if (e != null) {
        vv.add(e.val);
      } else {
        vv.add(null);
      }
    }
    return vv;
  }

  /**
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          commons-math FieldVector of CMFieldElement objects
   * @return JAS vector of ring elements
   */
  public static <C extends RingElem<C>> GenVector<C> vectorFromCMFieldVector(
      GenVectorModul<C> fac, FieldVector<CMFieldElement<C>> v) {
    if (v == null) {
      return null;
    }
    List<C> list = listFromCMFieldVector(v);
    GenVector<C> vv = new GenVector<C>(fac, list);
    return vv;
  }

  /**
   * Convert commons-math <code>FieldElement</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          commons-math FieldMatrix of CMFieldElement objects
   * @return java.util.List of ring elements
   */
  public static <C extends RingElem<C>> List<List<C>> listFromCMFieldMatrix(
      FieldMatrix<CMFieldElement<C>> v) {
    if (v == null) {
      return null;
    }
    ArrayList<List<C>> vv = new ArrayList<List<C>>(v.getRowDimension());
    for (int i = 0; i < v.getRowDimension(); i++) {
      FieldVector<CMFieldElement<C>> e = v.getRowVector(i + 1);
      List<C> l = listFromCMFieldVector(e);
      vv.add(l);
    }
    return vv;
  }

  /**
   * Convert commons-math <code>FieldMatrix</code> to JAS <code>RingElem</code> to.
   * 
   * @param <C>
   *          ring element type
   * @param v
   *          commons-math <code>FieldMatrix</code> of CMFieldElement objects
   * @return JAS matrix of ring elements
   */
  public static <C extends RingElem<C>> GenMatrix<C> matrixFromCMFieldMatrix(
      GenMatrixRing<C> fac, FieldMatrix<CMFieldElement<C>> v) {
    if (v == null) {
      return null;
    }
    List<List<C>> list = listFromCMFieldMatrix(v);
    GenMatrix<C> vv = new GenMatrix<C>(fac, list);
    return vv;
  }

}
