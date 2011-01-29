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
     * @param A modular GenPolynomial, mod p^k
     * @param B modular GenPolynomial, mod p^k
     * @param C modular GenPolynomial, mod p^k
     * @param V list of substitution values, mod p^k
     * @param d desired approximation exponent (x_i-v_i)^d.
     * @param k desired approximation exponent p^k.
     * @return [s, t] with s A' + t B' = C mod p^k, with A' = B, B' = A.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> 
           liftDiophant(GenPolynomial<MOD> A, GenPolynomial<MOD> B, GenPolynomial<MOD> C, List<MOD> V, long d, long k)
                        throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = C.ring;
        if (pkfac.nvar == 1) { // V, d ignored
            return HenselUtil.<MOD> liftDiophant(A,B,C,k);
        }
        if (!pkfac.equals(A.ring)) {
            throw new IllegalArgumentException("A.ring != pkfac: " + A.ring + " != " + pkfac);
        }

        // evaluate at v_n:
        List<MOD> Vp = new ArrayList<MOD>(V); 
        MOD v = Vp.remove(Vp.size()-1);
        GenPolynomial<MOD> zero = pkfac.getZERO();
        // (x_n - v)
        GenPolynomial<MOD> mon = pkfac.getONE();
        GenPolynomial<MOD> xv = pkfac.univariate(0,1);
        xv = xv.subtract( pkfac.fromInteger(v.getSymmetricInteger().getVal()) );
        //System.out.println("xv = " + xv);
        // A(v), B(v), C(v)
        ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>)pkfac.coFac;
        MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        //System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> ckfac = pkfac.contract(1);
        GenPolynomial<MOD> Ap = PolyUtil.<MOD> evaluateMain(ckfac,A,vp);
        GenPolynomial<MOD> Bp = PolyUtil.<MOD> evaluateMain(ckfac,B,vp);
        GenPolynomial<MOD> Cp = PolyUtil.<MOD> evaluateMain(ckfac,C,vp);
        //System.out.println("Ap = " + Ap);
        //System.out.println("Bp = " + Bp);
        //System.out.println("Cp = " + Cp);

        // recursion:
        List<GenPolynomial<MOD>> su = HenselMultUtil.<MOD> liftDiophant(Ap,Bp,Cp,Vp,d,k); 
        //System.out.println("su@p^" + k + " = " + su);
        //System.out.println("coFac = " + su.get(0).ring.coFac.toScript());
        if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Bp,Ap,su.get(0),su.get(1),Cp)) { 
           System.out.println("isDiophantLift: false");
        }
        if (!ckfac.equals(su.get(0).ring)) {
            throw new IllegalArgumentException("qfac != ckfac: " + su.get(0).ring + " != " + ckfac);
        }
        GenPolynomialRing<BigInteger> ifac  = new GenPolynomialRing<BigInteger>(new BigInteger(),pkfac);
        GenPolynomialRing<BigInteger> cifac = new GenPolynomialRing<BigInteger>(new BigInteger(),ckfac);
        //System.out.println("ifac = " + ifac.toScript());
        String[] mn = new String[] { pkfac.getVars()[pkfac.nvar-1] };
        GenPolynomialRing<GenPolynomial<MOD>> qrfac = new GenPolynomialRing<GenPolynomial<MOD>>(ckfac,1,mn);
        //System.out.println("qrfac = " + qrfac);

        List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<BigInteger>> supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
        for ( GenPolynomial<MOD> s : su ) {
            GenPolynomial<MOD> sp = s.extend(pkfac,0,0L);
            sup.add(sp);
            GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, sp);
            supi.add(spi);
        }
        //System.out.println("sup  = " + sup);
        //System.out.println("supi = " + supi);
        GenPolynomial<BigInteger> Ai = PolyUtil.integerFromModularCoefficients(ifac, A);
        GenPolynomial<BigInteger> Bi = PolyUtil.integerFromModularCoefficients(ifac, B);
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, C);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Bi = " + Bi);
        //System.out.println("Ci = " + Ci);
        GenPolynomial<MOD> aq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,Ai);
        GenPolynomial<MOD> bq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,Bi);
        //System.out.println("aq = " + aq);
        //System.out.println("bq = " + bq);

        // compute error:
        GenPolynomial<BigInteger> E = Ci; // - sum_i s_i b_i
        E = E.subtract( Bi.multiply(supi.get(0)) ); 
        E = E.subtract( Ai.multiply(supi.get(1)) );
        //System.out.println("E     = " + E);
        if ( E.isZERO() ) {
            logger.info("leaving on zero error");
            return sup; 
        }
        GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,E);
        System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
        if ( Ep.isZERO() ) {
            logger.info("leaving on zero error");
            return sup; 
        }
        for ( int e = 1; e <= d; e++ ) {
            //System.out.println("\ne = " + e + " -------------------------------------- " + pkfac.nvar);
            GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(qrfac,Ep);
            //System.out.println("Epr   = " + Epr);
            UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(qrfac);
            //System.out.println("psfac = " + psfac);
            TaylorFunction<GenPolynomial<MOD>> F = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
            //System.out.println("F     = " + F);
            List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
            GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
            Vs.add(vq);
            //System.out.println("Vs    = " + Vs);
            UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(F,vq);
            //System.out.println("Epst  = " + Epst);
            GenPolynomial<MOD> cm = Epst.coefficient(e);
            //System.out.println("cm   = " + cm + ", cm.ring   = " + cm.ring.toScript());

            // recursion:
            List<GenPolynomial<MOD>> S = HenselMultUtil.<MOD> liftDiophant(Ap, Bp, cm, Vp, d, k);
            //System.out.println("S    = " + S);
            if ( !ckfac.coFac.equals(S.get(0).ring.coFac) ) {
                throw new IllegalArgumentException("ckfac != pkfac: " + ckfac.coFac + " != " + S.get(0).ring.coFac);
            }
            if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap,Bp,S.get(1),S.get(0),cm)) { 
                System.out.println("isDiophantLift: false");
            }
            mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
            //System.out.println("mon  = " + mon);
            List<GenPolynomial<MOD>> Sp = new ArrayList<GenPolynomial<MOD>>(S.size());
            int i = 0;
            supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
            for (GenPolynomial<MOD> dd : S) {
                //System.out.println("dd = " + dd);
                GenPolynomial<MOD> de = dd.extend(pkfac,0,0L);
                GenPolynomial<MOD> dm = de.multiply( mon );
                Sp.add(dm);
                de = sup.get(i).sum(dm);
                //System.out.println("dd = " + dd);
                sup.set(i++, de);
                GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, dm);
                supi.add(spi);
            }
            //System.out.println("Sp   = " + Sp);
            //System.out.println("sup  = " + sup);
            //System.out.println("supi = " + supi);
            // compute new error
            //E = E; // - sum_i s_i b_i
            E = E.subtract( Bi.multiply(supi.get(0)) ); 
            E = E.subtract( Ai.multiply(supi.get(1)) );
            //System.out.println("E     = " + E);
            if ( E.isZERO() ) {
                logger.info("leaving on zero error");
                return sup; 
            }
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,E);
            System.out.println("Ep(" + e + "," + pkfac.nvar + ") = " + Ep); 
            if ( Ep.isZERO() ) {
                logger.info("leaving on zero error");
                return sup; 
            }
        }
        //System.out.println("*** done: " + pkfac.nvar);
        return sup;
    }


    /**
     * Modular diophantine equation solution and lifting algorithm. Let p =
     * A_i.ring.coFac.modul() and assume ggt(a,b) == 1 mod p, for a, b in A.
     * @param A list of modular GenPolynomials, mod p^k
     * @param C modular GenPolynomial, mod p^k
     * @param V list of substitution values, mod p^k
     * @param d desired approximation exponent (x_i-v_i)^d.
     * @param k desired approximation exponent p^k.
     * @return [s_1,..., s_n] with sum_i s_i A_i' = C mod p^k, with Ai' = prod_{j!=i} A_j.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> List<GenPolynomial<MOD>> 
           liftDiophant(List<GenPolynomial<MOD>> A, GenPolynomial<MOD> C, List<MOD> V, long d, long k)
                        throws NoLiftingException {
        GenPolynomialRing<MOD> pkfac = C.ring;
        if (pkfac.nvar == 1) { // V, d ignored
            return HenselUtil.<MOD> liftDiophant(A,C,k);
        }
        if (!pkfac.equals(A.get(0).ring)) {
            throw new IllegalArgumentException("A.ring != pkfac: " + A.get(0).ring + " != " + pkfac);
        }
        // co-products
        GenPolynomial<MOD> As = pkfac.getONE();
        for ( GenPolynomial<MOD> a : A ) {
            As = As.multiply(a);
        }
        List<GenPolynomial<MOD>> Bp = new ArrayList<GenPolynomial<MOD>>(A.size());
        for ( GenPolynomial<MOD> a : A ) {
            GenPolynomial<MOD> b = PolyUtil.<MOD> basePseudoDivide(As, a);
            Bp.add(b);
        }

        // evaluate at v_n:
        List<MOD> Vp = new ArrayList<MOD>(V); 
        MOD v = Vp.remove(Vp.size()-1);
        GenPolynomial<MOD> zero = pkfac.getZERO();
        // (x_n - v)
        GenPolynomial<MOD> mon = pkfac.getONE();
        GenPolynomial<MOD> xv = pkfac.univariate(0,1);
        xv = xv.subtract( pkfac.fromInteger(v.getSymmetricInteger().getVal()) );
        //System.out.println("xv = " + xv);
        // A(v), B(v), C(v)
        ModularRingFactory<MOD> cf = (ModularRingFactory<MOD>)pkfac.coFac;
        MOD vp = cf.fromInteger(v.getSymmetricInteger().getVal());
        //System.out.println("v = " + v + ", vp = " + vp);
        GenPolynomialRing<MOD> ckfac = pkfac.contract(1);
        List<GenPolynomial<MOD>> Ap = new ArrayList<GenPolynomial<MOD>>(A.size());
        for ( GenPolynomial<MOD> a : A ) {
             GenPolynomial<MOD> ap = PolyUtil.<MOD> evaluateMain(ckfac,a,vp);
             Ap.add(ap);
        }
        GenPolynomial<MOD> Cp = PolyUtil.<MOD> evaluateMain(ckfac,C,vp);
        //System.out.println("Ap = " + Ap);
        //System.out.println("Cp = " + Cp);

        // recursion:
        List<GenPolynomial<MOD>> su = HenselMultUtil.<MOD> liftDiophant(Ap,Cp,Vp,d,k); 
        //System.out.println("su@p^" + k + " = " + su);
        //System.out.println("coFac = " + su.get(0).ring.coFac.toScript());
        if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap,su,Cp)) { 
           System.out.println("isDiophantLift: false");
        }
        if (!ckfac.equals(su.get(0).ring)) {
            throw new IllegalArgumentException("qfac != ckfac: " + su.get(0).ring + " != " + ckfac);
        }
        GenPolynomialRing<BigInteger> ifac  = new GenPolynomialRing<BigInteger>(new BigInteger(),pkfac);
        GenPolynomialRing<BigInteger> cifac = new GenPolynomialRing<BigInteger>(new BigInteger(),ckfac);
        //System.out.println("ifac = " + ifac.toScript());
        String[] mn = new String[] { pkfac.getVars()[pkfac.nvar-1] };
        GenPolynomialRing<GenPolynomial<MOD>> qrfac = new GenPolynomialRing<GenPolynomial<MOD>>(ckfac,1,mn);
        //System.out.println("qrfac = " + qrfac);

        List<GenPolynomial<MOD>> sup = new ArrayList<GenPolynomial<MOD>>(su.size());
        List<GenPolynomial<BigInteger>> supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
        for ( GenPolynomial<MOD> s : su ) {
            GenPolynomial<MOD> sp = s.extend(pkfac,0,0L);
            sup.add(sp);
            GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, sp);
            supi.add(spi);
        }
        //System.out.println("sup  = " + sup);
        //System.out.println("supi = " + supi);
        List<GenPolynomial<BigInteger>> Ai = new ArrayList<GenPolynomial<BigInteger>>(A.size());
        for (GenPolynomial<MOD> a : A) {
             GenPolynomial<BigInteger> ai = PolyUtil.integerFromModularCoefficients(ifac, a);
             Ai.add(ai);
        }
        List<GenPolynomial<BigInteger>> Bi = new ArrayList<GenPolynomial<BigInteger>>(A.size());
        for (GenPolynomial<MOD> b : Bp) {
             GenPolynomial<BigInteger> bi = PolyUtil.integerFromModularCoefficients(ifac, b);
             Bi.add(bi);
        }
        GenPolynomial<BigInteger> Ci = PolyUtil.integerFromModularCoefficients(ifac, C);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Ci = " + Ci);

        List<GenPolynomial<MOD>> Aq = new ArrayList<GenPolynomial<MOD>>(A.size());
        for (GenPolynomial<BigInteger> ai : Ai) {
             GenPolynomial<MOD> aq = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,ai);
             Aq.add(aq);
        }
        //System.out.println("Aq = " + Aq);

        // compute error:
        GenPolynomial<BigInteger> E = Ci; // - sum_i s_i b_i
        int i = 0;
        for ( GenPolynomial<BigInteger> bi : Bi ) {
            E = E.subtract( bi.multiply(supi.get(i++)) ); 
        }
        //System.out.println("E     = " + E);
        if ( E.isZERO() ) {
            logger.info("leaving on zero error");
            return sup; 
        }
        GenPolynomial<MOD> Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,E);
        //System.out.println("Ep(0," + pkfac.nvar + ") = " + Ep);
        logger.info("Ep(0," + pkfac.nvar + ") = " + Ep);
        if ( Ep.isZERO() ) {
            logger.info("leaving on zero error");
            return sup; 
        }
        for ( int e = 1; e <= d; e++ ) {
            //System.out.println("\ne = " + e + " -------------------------------------- " + pkfac.nvar);
            GenPolynomial<GenPolynomial<MOD>> Epr = PolyUtil.<MOD> recursive(qrfac,Ep);
            //System.out.println("Epr   = " + Epr);
            UnivPowerSeriesRing<GenPolynomial<MOD>> psfac = new UnivPowerSeriesRing<GenPolynomial<MOD>>(qrfac);
            //System.out.println("psfac = " + psfac);
            TaylorFunction<GenPolynomial<MOD>> F = new PolynomialTaylorFunction<GenPolynomial<MOD>>(Epr);
            //System.out.println("F     = " + F);
            List<GenPolynomial<MOD>> Vs = new ArrayList<GenPolynomial<MOD>>(1);
            GenPolynomial<MOD> vq = ckfac.fromInteger(v.getSymmetricInteger().getVal());
            Vs.add(vq);
            //System.out.println("Vs    = " + Vs);
            UnivPowerSeries<GenPolynomial<MOD>> Epst = psfac.seriesOfTaylor(F,vq);
            //System.out.println("Epst  = " + Epst);
            GenPolynomial<MOD> cm = Epst.coefficient(e);
            //System.out.println("cm   = " + cm + ", cm.ring   = " + cm.ring.toScript());

            // recursion:
            List<GenPolynomial<MOD>> S = HenselMultUtil.<MOD> liftDiophant(Ap, cm, Vp, d, k);
            //System.out.println("S    = " + S);
            if ( !ckfac.coFac.equals(S.get(0).ring.coFac) ) {
                throw new IllegalArgumentException("ckfac != pkfac: " + ckfac.coFac + " != " + S.get(0).ring.coFac);
            }
            if (pkfac.nvar == 2 && !HenselUtil.<MOD> isDiophantLift(Ap,S,cm)) { 
                System.out.println("isDiophantLift: false");
            }
            mon = mon.multiply(xv); // Power.<GenPolynomial<MOD>> power(pkfac,xv,e);
            //System.out.println("mon  = " + mon);
            List<GenPolynomial<MOD>> Sp = new ArrayList<GenPolynomial<MOD>>(S.size());
            i = 0;
            supi = new ArrayList<GenPolynomial<BigInteger>>(su.size());
            for (GenPolynomial<MOD> dd : S) {
                //System.out.println("dd = " + dd);
                GenPolynomial<MOD> de = dd.extend(pkfac,0,0L);
                GenPolynomial<MOD> dm = de.multiply( mon );
                Sp.add(dm);
                de = sup.get(i).sum(dm);
                //System.out.println("dd = " + dd);
                sup.set(i++, de);
                GenPolynomial<BigInteger> spi = PolyUtil.integerFromModularCoefficients(ifac, dm);
                supi.add(spi);
            }
            //System.out.println("Sp   = " + Sp);
            //System.out.println("sup  = " + sup);
            //System.out.println("supi = " + supi);
            // compute new error
            //E = E; // - sum_i s_i b_i
            i = 0;
            for ( GenPolynomial<BigInteger> bi : Bi ) {
                 E = E.subtract( bi.multiply(supi.get(i++)) ); 
            }
            //System.out.println("E     = " + E);
            if ( E.isZERO() ) {
                logger.info("leaving on zero error");
                return sup; 
            }
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(pkfac,E);
            //System.out.println("Ep(" + e + "," + pkfac.nvar + ") = " + Ep); 
            logger.info("Ep(" + e + "," + pkfac.nvar + ") = " + Ep); 
            if ( Ep.isZERO() ) {
                logger.info("leaving on zero error");
                return sup; 
            }
        }
        //System.out.println("*** done: " + pkfac.nvar);
        return sup;
    }

}
