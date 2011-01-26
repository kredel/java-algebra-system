/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.Power;
import edu.jas.ps.MultiVarPowerSeriesRing;
import edu.jas.ps.MultiVarPowerSeries;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.ps.UnivPowerSeries;


/**
 * Hensel multivariate utilities for ufd.
 * @author Heinz Kredel
 */

public class HenselMultUtil {


    private static final Logger logger = Logger.getLogger(HenselMultUtil.class);


    private static final boolean debug = logger.isInfoEnabled();


    /**
     * Modular diophantine equation solution and lifting algorithm. Let p =
     * A_i.ring.coFac.modul() and assume ggt(A,B) == 1 mod p.
     * @param A modular GenPolynomial, mod p
     * @param B modular GenPolynomial, mod p
     * @param C modular GenPolynomial, mod p^k
     * @param V list of substitution values, mod p^k
     * @param d desired approximation exponent (x-v)^d.
     * @param k desired approximation exponent p^k.
     * @return [s, t] with s A' + t B' = C mod p^k, with A' = B, B' = A.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> 
           liftDiophant(GenPolynomial<MOD> A, GenPolynomial<MOD> B, GenPolynomial<MOD> C, List<MOD> V, long d, long k)
                        throws NoLiftingException {
        GenPolynomialRing<MOD> fac = A.ring;
        GenPolynomialRing<MOD> pkfac = C.ring;
        System.out.println("fac = " + fac);
        System.out.println("pkfac = " + pkfac);
        if (fac.nvar == 1) { // v, d??
            return HenselUtil.<MOD> liftDiophant(A,B,C,k);
        }
        List<MOD> Vp = new ArrayList<MOD>(V); 
        MOD v = Vp.remove(Vp.size()-1);
        //if (fac.nvar > 2) { 
        //    throw new UnsupportedOperationException("polynomial ring in more than 2 variables");
        //}
        //System.out.println("C = " + C);
        GenPolynomial<MOD> zero = pkfac.getZERO();
        // (x_2 - v)
        GenPolynomial<MOD> mon = pkfac.getONE();
        GenPolynomial<MOD> xv = pkfac.univariate(0,1);
        xv = xv.subtract( pkfac.fromInteger(v.getSymmetricInteger().getVal()) );
        //System.out.println("xv = " + xv);
        // A(v), B(v), C(v)
        ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>)fac.coFac;
        MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> cfac = fac.contract(1);
        GenPolynomialRing<MOD> ckfac = pkfac.contract(1);
        GenPolynomial<MOD> Ap = PolyUtil.<MOD> evaluateMain(cfac,A,vp);
        GenPolynomial<MOD> Bp = PolyUtil.<MOD> evaluateMain(cfac,B,vp);
        GenPolynomial<MOD> Cp = PolyUtil.<MOD> evaluateMain(ckfac,C,v);
        System.out.println("Ap = " + Ap);
        System.out.println("Bp = " + Bp);
        System.out.println("Cp = " + Cp);

        // recursion:
        List<GenPolynomial<MOD>> su = HenselMultUtil.<MOD> liftDiophant(Ap,Bp,Cp,Vp,d,k); 
        System.out.println("su@p^" + k + " = " + su);
        System.out.println("coFac = " + su.get(0).ring.coFac.toScript());
        if (fac.nvar == 2) { 
           System.out.println("isDiophantLift: " +  HenselUtil.<MOD> isDiophantLift(Bp,Ap,su.get(0),su.get(1),Cp) );
        }

        String[] mn = new String[] { fac.getVars()[fac.nvar-1] };
        GenPolynomialRing<MOD> qfac = su.get(0).ring;
        System.out.println("qfac = " + qfac.toScript() + ", pkfac = " + pkfac.toScript() );
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac);
        GenPolynomialRing<BigInteger> cifac = new GenPolynomialRing<BigInteger>(new BigInteger(),cfac);
        System.out.println("ifac = " + ifac.toScript());
        // adjust coefficient ring:
        if ( !qfac.coFac.equals(pkfac.coFac) ) {
            qfac = new GenPolynomialRing<MOD>(pkfac.coFac,qfac);
            List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
            for ( GenPolynomial<MOD> s : su ) {
                s = PolyUtil.<MOD> fromIntegerCoefficients(qfac,PolyUtil.integerFromModularCoefficients(cifac,s));
                sup.add(s);
            }
            su = sup;
            System.out.println("su   = " + su);
        }
        GenPolynomialRing<MOD> q2fac = new GenPolynomialRing<MOD>(qfac.coFac,ifac);
        System.out.println("q2fac = " + q2fac.toScript());
        List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<MOD>> suq = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<BigInteger>> supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
        for ( GenPolynomial<MOD> s : su ) {
            GenPolynomial<MOD> sp = s.extend(q2fac,0,0L);
            sup.add(sp);
            GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, sp);
            supi.add(spi);
            GenPolynomial<MOD> sq = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,spi);
            suq.add(sq);
        }
        System.out.println("sup  = " + sup);
        System.out.println("supi = " + supi);
        System.out.println("suq  = " + suq);
        GenPolynomial<BigInteger> Ai = PolyUtil.integerFromModularCoefficients(ifac, A);
        GenPolynomial<BigInteger> Bi = PolyUtil.integerFromModularCoefficients(ifac, B);
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, C);
        System.out.println("Ai = " + Ai);
        System.out.println("Bi = " + Bi);
        System.out.println("Ci = " + Ci);
        GenPolynomial<MOD> aq = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,Ai);
        GenPolynomial<MOD> bq = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,Bi);
        System.out.println("aq = " + aq);
        System.out.println("bq = " + bq);

        // compute error
        GenPolynomial<BigInteger> E = Ci; // - sum_i s_i b_i
        E = E.subtract( Bi.multiply(supi.get(0)) ); 
        E = E.subtract( Ai.multiply(supi.get(1)) );
        System.out.println("E     = " + E);
        System.out.println("s1 b1 = " + Bi.multiply(supi.get(0)));
        System.out.println("s2 b2 = " + Ai.multiply(supi.get(1)));
        if ( E.isZERO() ) {
            return sup; //??
        }
        GenPolynomial<MOD> E1 = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,Ci); // - sum_i s_i b_i
        E1 = E1.subtract( bq.multiply(suq.get(0)) ); 
        E1 = E1.subtract( aq.multiply(suq.get(1)) );
        System.out.println("E1    = " + E1);
        if ( E1.isZERO() ) {
            return sup; //??
        }

        GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,E);
        System.out.println("Ep    = " + Ep + ", Ep == E1: " + Ep.equals(E1)  + "");
        if ( Ep.isZERO() ) {
            return sup; //??
        }
        for ( int e = 1; e <= d; e++ ) {
            System.out.println("\ne = " + e + " -------------------------------------- " + fac.nvar);
            GenPolynomialRing<GenPolynomial<MOD>> qrfac = new GenPolynomialRing<GenPolynomial<MOD>>(qfac,1,mn);
            System.out.println("qrfac = " + qrfac);
            GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(qrfac,Ep);
            System.out.println("Epr   = " + Epr);
            UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(qrfac);
            System.out.println("psfac = " + psfac);
            TaylorFunction<GenPolynomial<MOD>> F = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
            System.out.println("F     = " + F);
            List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
            GenPolynomial<MOD> vq = qfac.fromInteger(v.getSymmetricInteger().getVal());
            Vs.add(vq);
            //Vs.add(qfac.getZERO());
            System.out.println("Vs    = " + Vs);
            UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(F,vq);
            System.out.println("Epst  = " + Epst);
            GenPolynomial<MOD> cm = Epst.coefficient(e);

            xv = q2fac.univariate(0,1);
            xv = xv.subtract( q2fac.fromInteger(v.getSymmetricInteger().getVal()) );
            System.out.println("xv = " + xv);

            System.out.println("Ap   = " + Ap + ", Ap.ring   = " + Ap.ring.toScript());
            System.out.println("Bp   = " + Bp + ", Bp.ring   = " + Bp.ring.toScript());
            System.out.println("cm   = " + cm + ", cm.ring   = " + cm.ring.toScript());
            //cm = PolyUtil.<MOD> fromIntegerCoefficients(cfac,PolyUtil.integerFromModularCoefficients(cifac, cm));
            List<GenPolynomial<MOD>> S = HenselMultUtil.<MOD> liftDiophant(Ap, Bp, cm, Vp, d, k);
            System.out.println("S    = " + S);
            System.out.println("coFac = " + S.get(0).ring.coFac.toScript());
            if ( !qfac.coFac.equals(S.get(0).ring) ) {
                List<GenPolynomial<MOD>> Sup = new ArrayList<GenPolynomial<MOD>>(S.size());
                for ( GenPolynomial<MOD> s : S ) {
                    s = PolyUtil.<MOD> fromIntegerCoefficients(qfac,PolyUtil.integerFromModularCoefficients(cifac,s));
                    Sup.add(s);
                }
                S = Sup;
                System.out.println("S    = " + S);
            }
            if (fac.nvar == 2) { 
                System.out.println("isDiophantLift: " +  HenselUtil.<MOD> isDiophantLift(Ap,Bp,S.get(1),S.get(0),cm) );
            }
            mon = Power.<GenPolynomial<MOD>> power(q2fac,xv,e);
            System.out.println("mon  = " + mon);
            List<GenPolynomial<MOD>> Sp = new ArrayList<GenPolynomial<MOD>>(S.size());
            int i = 0;
            supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
            for (GenPolynomial<MOD> dd : S) {
                //System.out.println("dd = " + dd);
                GenPolynomial<MOD> de = dd.extend(q2fac,0,0L);
                GenPolynomial<MOD> dm = de.multiply( mon );
                Sp.add(dm);
                de = sup.get(i).sum(dm);
                //System.out.println("dd = " + dd);
                sup.set(i++, de);
                GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, dm);
                supi.add(spi);
            }
            System.out.println("Sp   = " + Sp);
            System.out.println("sup  = " + sup);
            System.out.println("supi = " + supi);
            // compute error
            //E = E; // - sum_i s_i b_i
            E = E.subtract( Bi.multiply(supi.get(0)) ); 
            E = E.subtract( Ai.multiply(supi.get(1)) );
            System.out.println("E     = " + E);
            System.out.println("s1 b1 = " + Bi.multiply(supi.get(0)));
            System.out.println("s2 b2 = " + Ai.multiply(supi.get(1)));
            if ( E.isZERO() ) {
                return sup; //??
            }
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(q2fac,E);
            System.out.println("Ep    = " + Ep);
            if ( Ep.isZERO() ) {
                return sup; //??
            }
        }
        return sup;
    }

}
