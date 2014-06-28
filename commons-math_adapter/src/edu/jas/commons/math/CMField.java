package edu.jas.commons.math;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

import edu.jas.structure.ElemFactory;
import edu.jas.structure.Element;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

/**
 * Class that wraps a JAS <code>RingFactory</code> in a commons-math
 * <code>Field</code>.
 * 
 * @param <C>
 *          JAS ring element type
 */
public class CMField<C extends RingElem<C>> implements Field<CMFieldElement<C>> {
  
  public final RingFactory<C> fac;

  /**
   * @param f
   */
  public CMField(ElemFactory<C> f) {
    this((RingFactory<C>) f);
  }

  /**
   * @param f
   */
  public CMField(RingFactory<C> f) {
    fac = f;
  }


  /**
   * get runtime class
   */
  @Override
  public Class<CMFieldElement<C>> getRuntimeClass() {
      return (Class<CMFieldElement<C>>) (Class) CMFieldElement.class; //getClass();
  }


  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    // System.out.println("factory equals, this = " + this + ", obj = " + obj);
    if (!(obj instanceof Field)) {
      return false;
    }
    Field other = (Field) obj;
    if (!(other instanceof Field)) {
      return false;
    }
    CMField<C> fother = (CMField<C>) other;
    RingFactory<C> ofac = fother.fac;
    // System.out.println("factory equals, this = " + fac.getClass() +
    // ", obj = " + ofac.getClass());
    if (!fac.getClass().getName().equals(ofac.getClass().getName())) {
      return false;
    }
    RingFactory<C> ofac1 = null;
    try {
      ofac1 = (RingFactory<C>) ((RingElem<C>) ofac).factory();
    } catch (ClassCastException e) {
    }
    if ( /* fac */ofac.equals(ofac1)) { // case BigInteger etc
      return true;
    }
    // System.out.println("factory equals, this = " + ofac + ", obj = " +
    // ofac1);
    // if (fac.characteristic().equals(ffac.characteristic())) {
    // return true;
    // }
    return fac.equals(ofac);
  }

  public CMFieldElement<C> get(int i) {
    return new CMFieldElement<C>(fac.fromInteger(i));
  }

  public CMFieldElement<C> get(long i) {
    return new CMFieldElement<C>(fac.fromInteger(i));
  }

  public CMFieldElement<C> get(Object o) {
    if (o == null) {
      return null;
    }
    String s = o.toString();
    return new CMFieldElement<C>(fac.parse(s));
  }

  @SuppressWarnings("unchecked")
  public CMFieldElement<C>[] getArray(int size) {
    CMFieldElement<C>[] arr = new CMFieldElement[size];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = getZero();
    }
    return arr;
  }

  @SuppressWarnings("unchecked")
  public CMFieldElement<C>[][] getArray(int rows, int columns) {
    CMFieldElement<C>[][] arr = new CMFieldElement[rows][columns];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = getArray(columns);
    }
    return arr;
  }

  @Override
  public CMFieldElement<C> getOne() {
    return new CMFieldElement<C>(fac.getONE());
  }

  @Override
  public CMFieldElement<C> getZero() {
    return new CMFieldElement<C>(fac.getZERO());
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @SuppressWarnings("unchecked")
  @Override
  public int hashCode() {
    RingFactory<C> fac1 = null;
    try {
      fac1 = (RingFactory<C>) ((RingElem<C>) fac).factory();
    } catch (ClassCastException e) {
    }
    if (fac.equals(fac1)) { // case BigInteger etc
      // int h = fac.getClass().getSimpleName().hashCode();
      // h = h * 37 + fac.characteristic().hashCode();
      return fac.getClass().getName().hashCode();
    }
    return fac.hashCode();
  }

  /**
   * Get the string representation.
   * 
   * @see java.lang.Object#toString()
   */
  @SuppressWarnings("unchecked")
  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    String f = null;
    try {
      f = ((Element<C>) fac).toScriptFactory();
    } catch (Exception ignored) {
      f = fac.toScript();
    }
    if (f != null) {
      s.append(f);
    }
    return s.toString();
  }

}
