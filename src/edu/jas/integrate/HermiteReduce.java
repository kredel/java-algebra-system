package edu.jas.hermite;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeFieldChar0;

public class HermiteReduce {
  SquarefreeFieldChar0<BigRational> sqf;
  GreatestCommonDivisorAbstract<BigRational> ufd;
  GenPolynomialRing<BigRational> fac;

  public HermiteReduce() {
    BigRational br = new BigRational(0);
    String[] vars = new String[1];
    vars[0] = "x";
    fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(
        TermOrder.INVLEX), vars);
    ufd = GCDFactory.getProxy(br);
    sqf = new SquarefreeFieldChar0<BigRational>(br);
  }

  public GenPolynomial<BigRational>[] evaluate(GenPolynomial<BigRational> a,
      GenPolynomial<BigRational> d) {
    SortedMap<GenPolynomial<BigRational>, Long> sfactors;

    sfactors = sqf.squarefreeFactors(d);
    List<GenPolynomial<BigRational>> Di = new ArrayList<GenPolynomial<BigRational>>();
    Di.add(fac.getONE());// D1
    for (GenPolynomial<BigRational> f : sfactors.keySet()) {
      // Long E = sfactors.get(f);
      // long e = E.longValue();
      // System.out.println(e);
      // GenPolynomial<BigRational> temp = Power.<GenPolynomial<BigRational>>
      // positivePower(f, 1L);
      Di.add(f);
    }
    System.out.println("D_i: " + Di.toString());
    List<GenPolynomial<BigRational>> DPower = new ArrayList<GenPolynomial<BigRational>>();
    for (int i = 0; i < Di.size(); i++) {
      DPower.add(Power.<GenPolynomial<BigRational>> positivePower(Di.get(i),
          i + 1));
    }
    System.out.println(DPower);
    List<GenPolynomial<BigRational>> Ai = ufd.basePartialFraction(a, DPower);
    System.out.println("A_i: " + Ai.toString());
    GenPolynomial<BigRational> g = new GenPolynomial<BigRational>(fac);
    GenPolynomial<BigRational> h = Ai.get(0).sum(Ai.get(1).divide(Di.get(1)));
    GenPolynomial<BigRational> v;
    GenPolynomial<BigRational> DV_dx;
    GenPolynomial<BigRational> b;
    GenPolynomial<BigRational> c;
    int n = Di.size();
    for (int k = 2; k <= n; k++) {
      if (Di.get(k - 1).degree(0) > 0) {
        v = Di.get(k - 1);
        for (int j = k - 1; j >= 1; j--) {
          System.out.println("Step(" + k + "," + j + ")");
          System.out.println("V:" + v.toString());
          DV_dx = PolyUtil.<BigRational> baseDeriviative(v);
          GenPolynomial<BigRational>[] BC = ufd.baseGcdDiophant(DV_dx, v, Ai
              .get(k).negate().divide(fac.fromInteger(j)));
          b = BC[0];
          c = BC[1];
          g = g.sum(b.divide(Power.<GenPolynomial<BigRational>> positivePower(
              v, j)));
          Ai.set(k, fac.fromInteger(j).negate().multiply(c).subtract(
              PolyUtil.<BigRational> baseDeriviative(b)));
          System.out.println("B:" + b.toString());
          System.out.println("C:" + c.toString());
        }
        h = h.sum(Ai.get(k).divide(v));
      }
    }
    GenPolynomial<BigRational>[] ret = new GenPolynomial[2];
    ret[0] = g;
    ret[1] = h;
    return ret;
  }

  /**
   * Create coeff * x ^ exp
   * 
   * @param coeff
   * @param exp
   * @return
   */
  public GenPolynomial<BigRational> xPow(long coeff, long exp) {
    if (exp == 0L) {
      return fac.fromInteger(coeff);
    }
    ExpVector e = ExpVector.create(1, 0, exp);
    return fac.fromInteger(coeff).multiply(e);
  }

  public static void main(String[] args) {
    HermiteReduce hr = new HermiteReduce();

    // A: x^7 - 24 x^4 - 4 x^2 + 8 x - 8
    GenPolynomial<BigRational> a = hr.xPow(1, 7).sum(hr.xPow(-24, 4)).sum(
        hr.xPow(-4, 2)).sum(hr.xPow(8, 1)).sum(hr.xPow(-8, 0));
    System.out.println("A: " + a.toString());
    // D: x^8 + 6 x^6 + 12 x^4 + 8 x^2
    GenPolynomial<BigRational> d = hr.xPow(1, 8).sum(hr.xPow(6, 6)).sum(
        hr.xPow(12, 4)).sum(hr.xPow(8, 2));
    System.out.println("D: " + d.toString());
    GenPolynomial<BigRational>[] ret = hr.evaluate(a, d);
    System.out
        .println("Result: " + ret[0].toString() + "," + ret[1].toString());
    System.out.println("-----");

    System.exit(0);
  }
}
