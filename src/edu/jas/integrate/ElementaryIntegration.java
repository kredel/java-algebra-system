/*
 * $Id$
 */

package edu.jas.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.FactorAbsolute;
import edu.jas.ufd.PartialFraction;

/**
 * Methods related to elementary integration. For example Hermite reduction.
 * 
 * @author Axel Kramer
 * @param <C>
 *          coefficient type
 */

public class ElementaryIntegration<C extends GcdRingElem<C>> {


  /**
   * Engine for factorization.
   */
  public final FactorAbsolute<C> irr;


  /**
   * Engine for squarefree decomposition.
   */
  public final SquarefreeAbstract<C> sqf;


  /**
   * Engine for greatest common divisors.
   */
  public final GreatestCommonDivisorAbstract<C> ufd;


  /**
   * Constructor.
   */
  public ElementaryIntegration(RingFactory<C> br) {
    ufd = GCDFactory.<C> getProxy(br);
    sqf = SquarefreeFactory.<C> getImplementation(br);
    irr = (FactorAbsolute<C>) FactorFactory.<C> getImplementation(br);
  }


  /**
   * Integration of a rational function.
   * 
   * @param a
   *          numerator
   * @param d
   *          denominator
   * @return [ ... ] such that integrate(a/d) =
   *         sum_i(gn_i/gd_i) + integrate(h0) + sum_j( an_j log(hd_j) ) 
   */
  public Integral<C> integrate(GenPolynomial<C> a, GenPolynomial<C> d) {
      if ( d.isZERO() ) {
          throw new RuntimeException("zero denominator not allowed");
      }
      if ( a.isZERO() ) {
          return new Integral<C>(a,d,a);
      }
      if ( d.isONE() ) {
          GenPolynomial<C> pi = PolyUtil.<C> baseIntegral(a); 
          return new Integral<C>(a,d,pi);
      }
      GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(a, d); 
      GenPolynomial<C> p = qr[0];
      GenPolynomial<C> r = qr[1];

      GenPolynomial<C> c = ufd.gcd(r,d);
      if ( ! c.isONE() ) {
          r = PolyUtil.<C> basePseudoQuotientRemainder(r, c)[0];
          d = PolyUtil.<C> basePseudoQuotientRemainder(d, c)[0];
      }
      List<GenPolynomial<C>>[] ih = integrateHermite(r,d);
      List<GenPolynomial<C>> rat = ih[0];
      List<GenPolynomial<C>> log = ih[1];

      GenPolynomial<C> pp = log.remove(0);
      p = p.sum(pp);
      GenPolynomial<C> pi = PolyUtil.<C> baseIntegral(p); 

      System.out.println("pi  = " + pi);
      System.out.println("rat = " + rat);
      System.out.println("log = " + log);

      if ( log.size() == 0 ) {
          return new Integral<C>(a,d,pi,rat);
      }

      List<PartialFraction<C>> logi = new ArrayList<PartialFraction<C>>( log.size()/2 );
      for ( int i = 0; i < log.size(); i++ ) {
          GenPolynomial<C> ln = log.get(i++);
          GenPolynomial<C> ld = log.get(i);
          PartialFraction<C> pf = irr.baseAlgebraicPartialFraction(ln,ld);
          logi.add(pf);
      }
      System.out.println("logi = " + logi);
      return new Integral<C>(a,d,pi,rat,logi);
  }



  /**
   * Hermite reduction step.
   * 
   * @param a
   *          numerator
   * @param d
   *          denominator, gcd(a,d) == 1
   * @return [ [ gn_i, gd_i ], [ h0, hn_j, hd_j ] ] such that integrate(a/d) =
   *         sum_i(gn_i/gd_i) + integrate(h0) + sum_j( integrate(hn_j/hd_j) )
   */
  public List<GenPolynomial<C>>[] integrateHermite(GenPolynomial<C> a, GenPolynomial<C> d) {

    // get squarefree decomposition
    SortedMap<GenPolynomial<C>, Long> sfactors = sqf.squarefreeFactors(d);

    List<GenPolynomial<C>> D = new ArrayList<GenPolynomial<C>>( sfactors.keySet() );
    List<GenPolynomial<C>> DP = new ArrayList<GenPolynomial<C>>();
    for (GenPolynomial<C> f : D) {
      long e = sfactors.get(f);
      GenPolynomial<C> dp = Power.<GenPolynomial<C>> positivePower(f, e);
      DP.add(dp);
    }
    System.out.println("D:      " + D);
    System.out.println("DP:     " + DP);

    // get partial fraction decompostion 
    List<GenPolynomial<C>> Ai = ufd.basePartialFraction(a, DP);
    System.out.println("Ai:     " + Ai);

    List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
    List<GenPolynomial<C>> H = new ArrayList<GenPolynomial<C>>();
    H.add(Ai.remove(0)); // P

    GenPolynomial<C> vj;
    GenPolynomial<C> DV_dx;
    GenPolynomial<C> b;
    GenPolynomial<C> c;
    GenPolynomial<C> Aik;
    GenPolynomial<C> Ak;
    GenPolynomialRing<C> fac = d.ring;
    int i = 0;
    for (GenPolynomial<C> v : D) { 
        System.out.println("V:" + v.toString());
        int k = sfactors.get(v).intValue(); // assert low power
        Ak = Ai.get(i);
        if ( k == 1 ) {
            if (!Ak.isZERO()) {
                H.add(Ak); // A_1
                H.add(v); // D_1
            }
            continue;
        }
        System.out.println("Ak:  " + Ak.toString());
        for (int j = k - 1; j >= 1; j--) {
            System.out.println("Step(" + k + "," + j + ")");
            DV_dx = PolyUtil.<C> baseDeriviative(v);
            Aik = Ak.divide(fac.fromInteger(-j));
            GenPolynomial<C>[] BC = ufd.baseGcdDiophant(DV_dx, v, Aik);
            b = BC[0];
            c = BC[1];
            vj = Power.<GenPolynomial<C>> positivePower(v, j);
            G.add(b);  // B
            G.add(vj); // v^j
            Ak = fac.fromInteger(-j).multiply(c).subtract(PolyUtil.<C> baseDeriviative(b));
            System.out.println("B:   " + b.toString());
            System.out.println("C:   " + c.toString());
            System.out.println("Ak:  " + Ak.toString());
        }
        if (!Ak.isZERO()) {
            H.add(Ak); // A_k
            H.add(v); // v
        }
        i++;
    }
    List<GenPolynomial<C>>[] ret = new List[2];
    ret[0] = G;
    ret[1] = H;
    return ret;
  }

}
