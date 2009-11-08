/*
 * $Id$
 */

package edu.jas.integrate;


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


/**
 * Methods related to elementary integration. For example Hermite reduction.
 * @author Axel Kramer
 * @param <C> coefficient type
 */

public class ElementaryIntegration {


    /**
     * Engine for squarefree decomposition.
     */
    SquarefreeFieldChar0<BigRational> sqf;


    /**
     * Engine for greatest common divisors.
     */
    GreatestCommonDivisorAbstract<BigRational> ufd;


    GenPolynomialRing<BigRational> fac;


    /**
     * Constructor.
     */
    public ElementaryIntegration() {
        BigRational br = new BigRational(0);
        String[] vars = new String[1];
        vars[0] = "x";
        fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);
        ufd = GCDFactory.getProxy(br);
        sqf = new SquarefreeFieldChar0<BigRational>(br);
    }


    /**
     * Hermite reduction step.
     * @param a numerator
     * @param d denominator
     * @return [ [ gn_i, gd_i ], [ h0, hn_j, hd_j ] ] such that 
     *    integrate(a/d) = sum_i(gn_i/gd_i) + integrate(h0) + sum_j( integrate(hn_j/hd_j) )  
     */
    public List<GenPolynomial<BigRational>>[] integrateHermite(GenPolynomial<BigRational> a, GenPolynomial<BigRational> d) {
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
            DPower.add(Power.<GenPolynomial<BigRational>> positivePower(Di.get(i), i + 1));
        }
        System.out.println(DPower);
        List<GenPolynomial<BigRational>> Ai = ufd.basePartialFraction(a, DPower);
        System.out.println("A_i: " + Ai.toString());
        List<GenPolynomial<BigRational>> G = new ArrayList<GenPolynomial<BigRational>>();
        List<GenPolynomial<BigRational>> H = new ArrayList<GenPolynomial<BigRational>>();
        H.add( Ai.get(0) ); // P
        if ( ! Ai.get(1).isZERO() ) {
            H.add( Ai.get(1) ); // A_1
            H.add( Di.get(1) ); // D_1
        }
        GenPolynomial<BigRational> v;
        GenPolynomial<BigRational> vj;
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
                    GenPolynomial<BigRational>[] BC = ufd.baseGcdDiophant(DV_dx, v, Ai.get(k).negate()
                            .divide(fac.fromInteger(j)));
                    b = BC[0];
                    c = BC[1];
                    vj = Power.<GenPolynomial<BigRational>> positivePower(v, j);
                    G.add( b ); // B
                    G.add( vj ); // v^j 
                    Ai.set(k, fac.fromInteger(j).negate().multiply(c).subtract(
                            PolyUtil.<BigRational> baseDeriviative(b)));
                    System.out.println("B:" + b.toString());
                    System.out.println("C:" + c.toString());
                }
                if ( ! Ai.get(k).isZERO() ) {
                    H.add( Ai.get(k) ); // A_k
                    H.add( v ); // v 
                }
           }
        }
        List<GenPolynomial<BigRational>>[] ret = new List[2];
        ret[0] = G;
        ret[1] = H;
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
        //return fac.fromInteger(coeff).multiply(fac.univariate(0,exp));
    }


    /**
     * Test program.
     * @param args
     */
    public static void main(String[] args) {
        ElementaryIntegration hr = new ElementaryIntegration();

        // A: x^7 - 24 x^4 - 4 x^2 + 8 x - 8
        GenPolynomial<BigRational> a = hr.xPow(1, 7).sum(hr.xPow(-24, 4)).sum(hr.xPow(-4, 2)).sum(
                hr.xPow(8, 1)).sum(hr.xPow(-8, 0));
        System.out.println("A: " + a.toString());
        //a = hr.fac.parse("x^7 - 24 x^4 - 4 x^2 + 8 x - 8");
        //System.out.println("A: " + a.toString());
        // D: x^8 + 6 x^6 + 12 x^4 + 8 x^2
        GenPolynomial<BigRational> d = hr.xPow(1, 8).sum(hr.xPow(6, 6)).sum(hr.xPow(12, 4))
                .sum(hr.xPow(8, 2));
        System.out.println("D: " + d.toString());
        List<GenPolynomial<BigRational>>[] ret = hr.integrateHermite(a, d);
        System.out.println("Result: " + ret[0] + " , " + ret[1] );
        System.out.println("-----");

        System.exit(0);
    }
}
