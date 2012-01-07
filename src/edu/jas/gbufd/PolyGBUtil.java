/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.ExpVector;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;


/**
 * Polynomial gbufd utilities.
 * @author Heinz Kredel
 */

public class PolyGBUtil {


    private static final Logger logger = Logger.getLogger(PolyGBUtil.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Test for resultant.
     * @param A generic polynomial.
     * @param B generic polynomial.
     * @param r generic polynomial.
     * @return true if res(A,B) isContained in ideal(A,B), else false.
     */
    public static <C extends GcdRingElem<C>> boolean isResultant(GenPolynomial<C> A, GenPolynomial<C> B,
                    GenPolynomial<C> r) {
        if (r == null || r.isZERO()) {
            return true;
        }
        GroebnerBaseAbstract<C> bb = GBFactory.<C> getImplementation(r.ring.coFac);
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(2);
        F.add(A);
        F.add(B);
        List<GenPolynomial<C>> G = bb.GB(F);
        //System.out.println("G = " + G);
        GenPolynomial<C> n = bb.red.normalform(G, r);
        //System.out.println("n = " + n);
        return n.isZERO();
    }


    /**
     * Characteristic set.
     * According to Wu's algorithm with rereduction of leading coefficients.
     * @param A list of generic polynomials.
     * @return charSetWu(A).
     */
    public static <C extends GcdRingElem<C>> 
      List<GenPolynomial<C>> characteristicSet(List<GenPolynomial<C>> A) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if ( A == null || A.isEmpty() ) {
            return S;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if ( pfac.nvar <= 1 ) { // take gcd
            GreatestCommonDivisorAbstract<C> ufd = GCDFactory.<C> getImplementation(pfac.coFac);
            GenPolynomial<C> g = ufd.gcd(A).monic();
            S.add(g);
            return S;
        }
        // sort polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<GenPolynomial<C>>> positiveDeg = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>();
        for ( GenPolynomial<C> f : A ) {
            if ( f.isZERO() ) {
                continue;
            }
            f = f.monic();
            if ( f.isONE() ) {
                S.add(f);
                return S;
            }
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac,f);
            if ( fr.degree(0) == 0 ) {
                zeroDeg.add( fr.leadingBaseCoefficient() );
            } else {
                positiveDeg.add(fr);
            }
        }
        if ( positiveDeg.isEmpty() && zeroDeg.isEmpty() ) {
            return S;
        }
        // do pseudo division wrt. the main variable
        OrderedPolynomialList<GenPolynomial<C>> opl = new OrderedPolynomialList<GenPolynomial<C>>(rfac,positiveDeg);
        List<GenPolynomial<GenPolynomial<C>>> pd = new ArrayList<GenPolynomial<GenPolynomial<C>>>(opl.list);
        Collections.reverse(pd); // change OrderedPolynomialList to avoid
        if (debug) {
           logger.info("positive degrees: " + pd);
        }
        while ( pd.size() > 1 ) {
            GenPolynomial<GenPolynomial<C>> fr = pd.remove(0);
            GenPolynomial<GenPolynomial<C>> qr = pd.get(0); // = get(1)
            logger.info("pseudo remainder by deg = " + qr.degree() + " in variable " + rfac.getVars()[0]);
            GenPolynomial<GenPolynomial<C>> rr = PolyUtil.<C> recursiveSparsePseudoRemainder(fr,qr);
            if ( rr.isZERO() ) {
                logger.warn("variety is reducible"); 
                // replace qr by gcd(qr,fr) ?
                continue;
            }
            if ( rr.degree(0) == 0 ) {
                zeroDeg.add( rr.leadingBaseCoefficient().monic() );
            } else {
                pd.add(rr);
                pd = opl.sort(rfac,pd);
                Collections.reverse(pd); // avoid
            }
        }
        // recursion for degree zero polynomials
        List<GenPolynomial<C>> Sp = null;
        if ( !zeroDeg.isEmpty() ) {
            Sp = characteristicSet(zeroDeg); // recursion
            for ( GenPolynomial<C> f : Sp ) {
                GenPolynomial<C> fp = f.extend(pfac,0,0L);
                S.add(fp);
            }
        }
        // rereduction of leading coefficient wrt. characteristic set according to Wu
        if ( pd.size() > 0 ) { // == 1
            GenPolynomial<GenPolynomial<C>> rr = pd.get(0);
            // distribute
            GenPolynomial<C> sr = PolyUtil.<C> distribute(pfac,rr);
            if ( Sp != null && !Sp.isEmpty() ) {
                // reduce leading coefficient
                long d = sr.degree(pfac.nvar-1);
                long d1 = d;
                do {
                    d1 = d;
                    sr = characteristicSetRemainderCoeff(Sp,sr);
                    //System.out.println("sr = " + sr);
                    if ( sr.isZERO() ) {
                        logger.warn("zero in rededuction");
                        return S;
                    }
                    d = sr.degree(pfac.nvar-1);
                    if ( d < d1 ) {
                        logger.warn("degree reduction in rededuction");
                    }
                } while ( d < d1 && d > 0 ); 
                if ( d == 0 ) { // constant, invalid characteristic set, restart
                    S.add(0,sr);
                    logger.warn("invalid characteristic set, restarting with S = " + S);
                    return characteristicSet(S);
                }
            }
            sr = sr.monic();
            S.add(0,sr);
        }
        return S;
    }


    /**
     * Characteristic set test.
     * @param A list of generic polynomials.
     * @return true, if A = charSet(A), else false.
     */
    public static <C extends RingElem<C>> 
      boolean isCharacteristicSet(List<GenPolynomial<C>> A) {
        if ( A == null || A.isEmpty() ) {
            return true; // ?
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if ( pfac.nvar <= 1 ) { 
            return A.size() <= 1;
        }
        if ( pfac.nvar < A.size() ) { 
            return false;
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>();
        int positiveDeg = 0;
        for ( GenPolynomial<C> f : A ) {
            if ( f.isZERO() ) {
                return false;
            }
            //f = f.monic();
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac,f);
            if ( fr.degree(0) == 0 ) {
                zeroDeg.add( fr.leadingBaseCoefficient() );
            } else {
                positiveDeg++;
                if ( positiveDeg > 1 ) {
                    return false;
                }
            }
        }
        return isCharacteristicSet(zeroDeg);
    }


    /**
     * Characteristic set reduction wrt. main variable.
     * @param P generic polynomial.
     * @param A list of generic polynomials as characteristic set.
     * @return pseudo remainder of P wrt. A for the main variable.
     */
    public static <C extends RingElem<C>> 
     GenPolynomial<C> characteristicSetRemainder(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if ( A == null || A.isEmpty() ) {
            return P.monic();
        }
        if ( P.isZERO() || P.isONE() ) {
            return P.monic();
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if ( pfac.nvar <= 1 ) { // recursion base 
            GenPolynomial<C> R = PolyUtil.<C> baseSparsePseudoRemainder(P,A.get(0));
            return R.monic();
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<C> Q = A.get(0);
        GenPolynomial<GenPolynomial<C>> qr = PolyUtil.<C> recursive(rfac,Q);
        GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> rr = PolyUtil.<C> recursiveSparsePseudoRemainder(pr,qr);
        if (debug) {
           logger.info("remainder = " + rr);
        }
        if ( rr.degree(0) > 0 ) {
            GenPolynomial<C> R = PolyUtil.<C> distribute(pfac,rr);
            return R.monic();
            // not further reduced wrt. other variables
        }
        List<GenPolynomial<C>> zeroDeg = zeroDegrees(A);
        GenPolynomial<C> R = characteristicSetRemainder(zeroDeg,rr.leadingBaseCoefficient());
        R = R.extend(pfac,0,0L);
        return R.monic();
    }


    /**
     * Characteristic set coefficient reduction wrt. all variables.
     * @param P generic polynomial in n+1 variables.
     * @param A list of generic polynomials as characteristic set of polynomials in n variables.
     * @return pseudo remainder P wrt. to the leading coefficient of P wrt. A.
     */
    public static <C extends RingElem<C>> 
      GenPolynomial<C> characteristicSetRemainderCoeff(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if ( A == null || A.isEmpty() ) {
            return P;
        }
        if ( P.isZERO() || P.isONE() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        //System.out.println("pfac  = " + pfac);
        GenPolynomialRing<C> pfac1 = A.get(0).ring;
        //System.out.println("pfac1 = " + pfac1);
        if ( pfac1.nvar <= 1 ) { // recursion base 
            GenPolynomial<C> a = A.get(0);
            //System.out.println("recursion base, a = " + a);
            if ( a.isONE() ) {
                return pfac.getZERO(); // pfac.getONE();
            }
            if ( pfac.nvar <= 1 ) {
                GenPolynomial<C> R = PolyUtil.<C> baseSparsePseudoRemainder(P,a);
                return R.monic();
            }
            GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(pfac.nvar-1);
            GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac,P); 
            GenPolynomial<GenPolynomial<C>> ar = new GenPolynomial<GenPolynomial<C>>(rfac,a,pr.leadingExpVector());
            pr = pr.subtract(ar);
            GenPolynomial<C> R = PolyUtil.<C> distribute(pfac,pr);
            return R.monic();
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac1 = pfac1.recursive(1);
        int nv = pfac.nvar - pfac1.nvar;
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1+nv);
        GenPolynomialRing<GenPolynomial<GenPolynomial<C>>> rfac2 = rfac.recursive(nv);
        if (debug) {
           logger.info("rfac =" + rfac);
        }
        GenPolynomial<GenPolynomial<C>> pr = PolyUtil.<C> recursive(rfac,P);
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> pr2 = PolyUtil.<GenPolynomial<C>> recursive(rfac2,pr);
        GenPolynomial<GenPolynomial<C>> plr = pr2.leadingBaseCoefficient();
        GenPolynomial<C> pld = plr.leadingBaseCoefficient();
        //System.out.println("plr  = " + plr);
        if ( plr.isConstant() ) { // test irreducible
            if ( pld.isConstant() ) { // irreducible
                //System.out.println("pld  = " + pld);
                return P.monic();
            }
        }
        GenPolynomial<C> Q = A.get(0);
        GenPolynomial<GenPolynomial<C>> qr = PolyUtil.<C> recursive(rfac1,Q);
        GenPolynomial<C> qld = qr.leadingBaseCoefficient();

        // pseudo remainder:
        GenPolynomial<GenPolynomial<C>> ql = new GenPolynomial<GenPolynomial<C>>(rfac1,qld);
        GenPolynomial<GenPolynomial<C>> pl = new GenPolynomial<GenPolynomial<C>>(rfac1,pld);
        ExpVector e = qr.leadingExpVector();
        ExpVector f = plr.leadingExpVector();
        //System.out.println("e = " + e + ", f = " +f);
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> rr = null;
        GenPolynomial<GenPolynomial<GenPolynomial<C>>> qrp = null;
        GenPolynomial<C> R = null;
        if ( e.divides(f) ) {
            ExpVector g = e.subtract(f);
            rr = pr2.multiply(ql);
            GenPolynomial<GenPolynomial<C>> qrr = qr.multiply(pl);
            qrr = qrr.multiply(g);
            qrp = new GenPolynomial<GenPolynomial<GenPolynomial<C>>>(rfac2,qrr,pr2.leadingExpVector());
            rr = rr.subtract(qrp);
            if ( debug) {
                logger.info("remainder = " + rr);
            }
            if ( rr.isZERO() ) {
                return pfac.getZERO();
            }
            GenPolynomial<GenPolynomial<C>> Rp = PolyUtil.<GenPolynomial<C>> distribute(rfac,rr);
            R = PolyUtil.<C> distribute(pfac,Rp);
            //System.out.println("R    = " + R);
        } else {
            //System.out.println("e not divides f");
            R = P;
        }
        // reduction wrt. the other variables
        List<GenPolynomial<C>> zeroDeg = zeroDegrees(A);
        R = characteristicSetRemainderCoeff(zeroDeg,R);
        return R;
    }


    /**
     * Characteristic set reduction.
     * Pseudo remainder wrt. the main variabe with further pseudo reduction of the leading coefficient.
     * @param P generic polynomial.
     * @param A list of generic polynomials as characteristic set.
     * @return characteristicSetReductionCoeff(characteristicSetRemainder(A,P),Ap).
     */
    public static <C extends RingElem<C>> 
     GenPolynomial<C> characteristicSetReduction(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if ( A == null || A.isEmpty() ) {
            return P.monic();
        }
        if ( P.isZERO() || P.isONE() ) {
            return P.monic();
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        GenPolynomial<C> R = characteristicSetRemainder(A,P);
        //System.out.println("R = " + R);
        if ( R.isZERO() || R.isONE() ) {
            return R;
        }
        List<GenPolynomial<C>> Ap = zeroDegrees(A);
        //System.out.println("Ap = " + Ap);
        R = characteristicSetRemainderCoeff(Ap,R);
        //System.out.println("R = " + R);
        return R;
    } 


    /**
     * Characteristic set polynomials with degree zero in the main variable.
     * @param A list of generic polynomials in n variables.
     * @return Z = [a_i] with deg(a_i,x_n) = 0 and in n-1 variables.
     */
    public static <C extends RingElem<C>> 
      List<GenPolynomial<C>> zeroDegrees(List<GenPolynomial<C>> A) {
        if ( A == null || A.isEmpty() ) {
            return A;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>(A.size());
        for ( int i = 0; i < A.size(); i++ ) {
            GenPolynomial<C> q = A.get(i);
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac,q);
            if ( fr.degree(0) == 0 ) {
                zeroDeg.add( fr.leadingBaseCoefficient() );
            }
        }
        return zeroDeg;
    }

}
