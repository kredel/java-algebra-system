/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;


/**
 * Ideal. Some methods for ideal arithmetic.
 * @author Heinz Kredel
 */

public class Ideal<C extends RingElem<C>> implements Serializable {


  /** 
   * The data structure is a PolynomialList. 
   */
  protected PolynomialList<C> list;


  /** 
   * Indicator if list is a Groebner Base. 
   */
  protected boolean isGB;


  /** 
   * Indicator if test has been performed if this is a Groebner Base. 
   */
  protected boolean testGB;


  /** 
   * Groebner base algorithm. 
   */
  protected GroebnerBase<C> bb;


  /**
   * Reduction engine.
   */
  protected Reduction<C> red;


  private static Logger logger = Logger.getLogger(Ideal.class);


  /**
   * Constructor.
   * @param ring polynomial ring
   * @param F list of polynomials
   */
  public Ideal( GenPolynomialRing<C> ring, List<GenPolynomial<C>> F ) {
      this( new PolynomialList<C>( ring, F ) );
  }

  /**
   * Constructor.
   * @param ring polynomial ring
   * @param F list of polynomials
   * @param gb true if F is known to be a Groebner Base, else false
   */
  public Ideal( GenPolynomialRing<C> ring, 
                List<GenPolynomial<C>> F, boolean gb ) {
      this( new PolynomialList<C>( ring, F ), gb );
  }


  /**
   * Constructor.
   * @param list polynomial list
   */
  public Ideal( PolynomialList<C> list) {
      this( list, 
            ( list == null ? true : ( list.list == null ? true : false ) ) );
  }


  /**
   * Constructor.
   * @param list polynomial list
   * @param gb true if list is known to be a Groebner Base, else false
   */
  public Ideal( PolynomialList<C> list, boolean gb) {
      this.list = list;
      this.isGB = gb;
      this.testGB = ( gb ? true : false ); // ??
      bb = new GroebnerBaseSeq<C>();
      red = new ReductionSeq<C>();
  }


  /**
   * Get the List of GenPolynomials.
   * @return list.list
   */
  public List< GenPolynomial<C> > getList() {
      if ( list == null ) {
          return null;
      }
      return list.list;
  }


  /**
   * Get the GenPolynomialRing.
   * @return list.ring
   */
  public GenPolynomialRing<C> getRing() {
      if ( list == null ) {
          return null;
      }
      return list.ring;
  }


  /**
   * String representation of the ideal.
   * @see java.lang.Object#toString()
   */
  public String toString() {
      return list.toString();
  }


  /** Comparison with any other object.
   * Note: If both ideals are not Groebner Bases, then
   *       false may be returned even the ideals are equal.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  @SuppressWarnings("unchecked") // not jet working
  public boolean equals(Object b) {
      if ( ! (b instanceof Ideal) ) {
         logger.warn("equals no Ideal");
         return false;
      }
      Ideal<C> B = null;
      try {
          B = (Ideal<C>)b;
      } catch (ClassCastException ignored) {
      }
      if ( isGB && B.isGB ) {
         return list.equals( B.list );
      } else { // compute GBs ?
         return list.equals( B.list );
      }
  }


  /**
   * Test if ZERO ideal.
   * @return true, if this is the 0 ideal, else false
   */
  public boolean isZERO() {
      if ( getList() == null ) {
          return true;
      }
      for ( GenPolynomial<C> p : getList() ) {
          if ( p == null ) {
              continue;
          }
          if ( ! p.isZERO() ) {
             return false;
          }
      }
      return true;
  }


  /**
   * Test if ONE ideal.
   * @return true, if this is the 1 ideal, else false
   */
  public boolean isONE() {
      if ( getList() == null ) {
          return false;
      }
      for ( GenPolynomial<C> p : getList() ) {
          if ( p == null ) {
              continue;
          }
          if ( p.isONE() ) {
             return true;
          }
      }
      return false;
  }


  /**
   * Test if this is a Groebner base.
   * @return true, if this is a Groebner base, else false
   */
  public boolean isGB() {
      if ( testGB ) {
          return isGB;
      }
      logger.warn("isGB computing");
      isGB = bb.isGB( getList() );
      testGB = true;
      return isGB;
  }


  /**
   * Groebner Base. Get a Groebner Base for this ideal.
   * @return GB(this)
   */
  public Ideal<C> GB() {
      if ( isGB ) {
          return this;
      }
      logger.warn("GB computing");
      List< GenPolynomial<C> > c = getList();
      c = bb.GB( c );
      return new Ideal<C>( getRing(), c, true );
  }


  /**
   * Ideal containment. Test if B is contained in this ideal.
   * Note: this is eventually modified to become a Groebner Base.
   * @param B ideal
   * @return true, if B is contained in this, else false
   */
  public boolean contains( Ideal<C> B ) {
      if ( B == null ) {
          return true;
      }
      if ( B.isZERO() ) {
          return true;
      }
      if ( this.isONE() ) {
          return true;
      }
      if ( !isGB ) {
         logger.warn("contains computing GB");
         List< GenPolynomial<C> > c = getList();
         c = bb.GB( c );
         list = new PolynomialList<C>( getRing(), c );
         isGB = true;
         testGB = true;
      }
      List< GenPolynomial<C> > z;
      z = red.normalform( getList(), B.getList() );
      if ( z == null ) {
          return true;
      }
      for ( GenPolynomial<C> p : z ) {
          if ( p == null ) {
              continue;
          }
          if ( ! p.isZERO() ) {
             return false;
          }
      }
      return true;
  }


  /**
   * Summation. Generators for the sum of ideals.
   * Note: if both ideals are Groebner bases, a Groebner base is returned.
   * @param B ideal
   * @return ideal(this+B)
   */
  public Ideal<C> sum( Ideal<C> B ) {
      if ( B == null ) {
          return this;
      }
      if ( B.isZERO() ) {
          return this;
      }
      if ( this.isZERO() ) {
          return B;
      }
      int s = getList().size() + B.getList().size();
      List< GenPolynomial<C> > c;
      c = new ArrayList<GenPolynomial<C>>( s );
      c.addAll( getList() );
      c.addAll( B.getList() );
      if ( isGB && B.isGB ) {
         logger.warn("sum computing GB");
         c = bb.GB( c );
         return new Ideal<C>( getRing(), c, true );
      } else {
         return new Ideal<C>( getRing(), c, false );
      }
  }


  /**
   * Product. Generators for the product of ideals.
   * Note: if both ideals are Groebner bases, a Groebner base is returned.
   * @param B ideal
   * @return ideal(this*B)
   */
  public Ideal<C> product( Ideal<C> B ) {
      if ( B == null ) {
          return B;
      }
      if ( B.isZERO() ) {
          return B;
      }
      if ( this.isZERO() ) {
          return this;
      }
      int s = getList().size() * B.getList().size();
      List< GenPolynomial<C> > c;
      c = new ArrayList<GenPolynomial<C>>( s );
      for ( GenPolynomial<C> p : getList() ) {
          for ( GenPolynomial<C> q : B.getList() ) {
              q = p.multiply(q);
              c.add(q);
          }
      }
      if ( isGB && B.isGB ) {
         logger.warn("product computing GB");
         c = bb.GB( c );
         return new Ideal<C>( getRing(), c, true );
      } else {
         return new Ideal<C>( getRing(), c, false );
      }
  }


  /**
   * Intersection. Generators for the intersection of ideals.
   * @param B ideal
   * @return ideal(this \cap B), a Groebner base
   */
  public Ideal<C> intersect( Ideal<C> B ) {
      if ( B == null ) { // == (0)
          return B;
      }
      if ( B.isZERO() ) { 
          return B;
      }
      if ( this.isZERO() ) { 
          return this;
      }
      int s = getList().size() + B.getList().size();
      List< GenPolynomial<C> > c;
      c = new ArrayList<GenPolynomial<C>>( s );
      List< GenPolynomial<C> > a = getList();
      List< GenPolynomial<C> > b = B.getList();

      GenPolynomialRing<C> tfac = getRing().extend(1);
      // term order is also adjusted
      for ( GenPolynomial<C> p : a ) {
          p = p.extend( tfac, 0, 1L ); // t*p
          c.add( p );
      }
      for ( GenPolynomial<C> p : b ) {
          GenPolynomial<C> q = p.extend( tfac, 0, 1L );
          GenPolynomial<C> r = p.extend( tfac, 0, 0L );
          p = r.subtract( q ); // (1-t)*p
          c.add( p );
      }
      logger.warn("intersect computing GB");
      List< GenPolynomial<C> > g = bb.GB( c );
      if ( logger.isDebugEnabled() ) {
         logger.debug("intersect GB = " + g);
      }
      Ideal<C> E = new Ideal<C>( tfac, g, true );
      return E.intersect( getRing() );
      /*
      List< GenPolynomial<C> > h;
      h = new ArrayList<GenPolynomial<C>>( g.size() );
      for ( GenPolynomial<C> p : g ) {
          Map<ExpVector,GenPolynomial<C>> m = null;
          m = p.contract( getRing() );
          if ( logger.isDebugEnabled() ) {
             logger.debug("intersect contract m = " + m);
          }
          if ( m.size() == 1 ) { // contains one power of t
             for ( ExpVector e : m.keySet() ) {
                 if ( e.isZERO() ) {
                    h.add( m.get( e ) );
                 }
             }
          }
      }
      return new Ideal<C>( getRing(), h, true );
      */
  }


  /**
   * Intersection. Generators for the intersection of a ideal 
   * with a polynomial ring. The polynomial ring of this ideal
   * must be a contraction of R and the TermOrder must be 
   * an elimination order.
   * @param R polynomial ring
   * @return ideal(this \cap R)
   */
  public Ideal<C> intersect( GenPolynomialRing<C> R ) {
      if ( R == null ) { 
         throw new RuntimeException("R may not be null");
      }
      int d = getRing().nvar - R.nvar;
      if ( d <= 0 ) {
          return this;
      }
      //GenPolynomialRing<C> tfac = getRing().contract(d);
      //if ( ! tfac.equals( R ) ) { // check ?
      //   throw new RuntimeException("contract(this) != R");
      //}
      List< GenPolynomial<C> > h;
      h = new ArrayList<GenPolynomial<C>>( getList().size() );
      for ( GenPolynomial<C> p : getList() ) {
          Map<ExpVector,GenPolynomial<C>> m = null;
          m = p.contract( R );
          if ( logger.isDebugEnabled() ) {
             logger.debug("intersect contract m = " + m);
          }
          if ( m.size() == 1 ) { // contains one power of variables
             for ( ExpVector e : m.keySet() ) {
                 if ( e.isZERO() ) {
                    h.add( m.get( e ) );
                 }
             }
          }
      }
      return new Ideal<C>( R, h, isGB );
  }


  /**
   * Quotient. Generators for the ideal quotient.
   * @param h polynomial
   * @return ideal(this : h), a Groebner base
   */
  public Ideal<C> quotient( GenPolynomial<C> h ) {
      if ( h == null ) { // == (0)
          return this;
      }
      if ( h.isZERO() ) { 
          return this;
      }
      if ( this.isZERO() ) { 
          return this;
      }
      List< GenPolynomial<C> > H;
      H = new ArrayList<GenPolynomial<C>>( 1 );
      H.add( h );
      Ideal<C> Hi = new Ideal( getRing(), H, true );

      Ideal<C> I = this.intersect( Hi );

      List< GenPolynomial<C> > Q;
      Q = new ArrayList<GenPolynomial<C>>( I.getList().size() );
      for ( GenPolynomial<C> q : I.getList() ) {
          q = q.divide(h); // remainder == 0
          Q.add( q );
      }
      return new Ideal<C>( getRing(), Q, true /*false?*/ );
  }


  /**
   * Quotient. Generators for the ideal quotient.
   * @param H ideal
   * @return ideal(this : H), a Groebner base
   */
  public Ideal<C> quotient( Ideal<C> H ) {
      if ( H == null ) { // == (0)
          return this;
      }
      if ( H.isZERO() ) { 
          return this;
      }
      if ( this.isZERO() ) { 
          return this;
      }
      Ideal<C> Q = null;
      for ( GenPolynomial<C> h : H.getList() ) {
          Ideal<C> Hi = this.quotient(h);
          if ( Q == null ) {
             Q = Hi;
          } else {
             Q = Q.intersect( Hi );
          }
      }
      return Q;
  }


  /**
   * Infinite quotient. Generators for the infinite ideal quotient.
   * @param h polynomial
   * @return ideal(this : h<sup>s</sup>), a Groebner base
   */
  public Ideal<C> infiniteQuotient( GenPolynomial<C> h ) {
      if ( h == null ) { // == (0)
          return this;
      }
      if ( h.isZERO() ) { 
          return this;
      }
      if ( this.isZERO() ) {
          return this;
      }
      int s = 0;
      Ideal<C> I = this.GB(); // should be already
      GenPolynomial<C> hs = h;

      boolean eq = false;
      while ( !eq ) {
        Ideal<C> Is = I.quotient( hs );
        Is = Is.GB(); // should be already
        logger.debug("infiniteQuotient s = " + s);
        eq = Is.contains(I);  // I.contains(Is) always
        if ( !eq ) {
           I = Is;
           s++;
           hs = hs.multiply( h );
        }
      }
      return I;
  }

}