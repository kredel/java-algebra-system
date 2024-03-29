/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInt;
import edu.jas.arith.ModIntRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.Power;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.LinAlg;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * PolyUfdUtil tests with JUnit.
 * @author Heinz Kredel
 */

public class PolyUfdUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public PolyUfdUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyUfdUtilTest.class);
        return suite;
    }


    TermOrder to = TermOrderByName.INVLEX;


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai, bi, ci, di, ei;


    GenPolynomial<BigInteger> a, b, c, d, e;


    GenPolynomial<GenPolynomial<BigInteger>> ar, br, cr, dr, er;


    GenPolynomialRing<BigRational> crfac;


    GenPolynomialRing<GenPolynomial<BigRational>> rrfac;


    GenPolynomial<GenPolynomial<BigRational>> arr, brr, crr, drr, err, frr;


    int rl = 5;


    int kl = 5;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        cfac = null;
        rfac = null;
        ComputerThreads.terminate();
    }


    protected static java.math.BigInteger getPrime1() {
        long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 60; i++) {
            prime *= 2;
        }
        prime -= 93;
        //prime = 37;
        //System.out.println("p1 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    protected static java.math.BigInteger getPrime2() {
        long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 30; i++) {
            prime *= 2;
        }
        prime -= 35;
        //prime = 19;
        //System.out.println("p1 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    /**
     * Test Kronecker substitution.
     */
    public void testKroneckerSubstitution() {
        for (int i = 0; i < 10; i++) {
            a = dfac.random(kl, ll * 2, el * 5, q);
            long d = a.degree() + 1L;
            //System.out.println("\na        = " + a);
            //System.out.println("deg(a)+1 = " + d);

            b = PolyUfdUtil.<BigInteger> substituteKronecker(a, d);
            //System.out.println("b        = " + b);

            c = PolyUfdUtil.<BigInteger> backSubstituteKronecker(dfac, b, d);
            //System.out.println("c        = " + c);
            e = a.subtract(c);
            //System.out.println("e        = " + e);
            assertTrue("back(subst(a)) = a", e.isZERO());
        }
    }


    /**
     * Test algebraic number field.
     */
    public void testAlgebraicNumberField() {
        int deg = 11;
        // characteristic non zero, small
        ModLongRing gfp = new ModLongRing(32003);
        //System.out.println("gfp = " + gfp.toScript());
        AlgebraicNumberRing<ModLong> gfpq = PolyUfdUtil.<ModLong> algebraicNumberField(gfp, deg);
        //System.out.println("gfpq = " + gfpq.toScript());
        assertTrue("gfpq.isField: ", gfpq.isField());

        // characteristic non zero, large
        ModIntegerRing gfP = new ModIntegerRing(getPrime1());
        //System.out.println("gfP = " + gfP.toScript());
        AlgebraicNumberRing<ModInteger> gfPq = PolyUfdUtil.<ModInteger> algebraicNumberField(gfP, deg);
        //System.out.println("gfPq = " + gfPq.toScript());
        assertTrue("gfPq.isField: ", gfPq.isField());

        // characteristic zero
        BigRational q = BigRational.ONE;
        //System.out.println("q = " + q.toScriptFactory());
        AlgebraicNumberRing<BigRational> gfqq = PolyUfdUtil.<BigRational> algebraicNumberField(q, deg);
        //System.out.println("gfqq = " + gfqq.toScript());
        assertTrue("gfqq.isField: ", gfqq.isField());

        //PolyUfdUtil.<BigRational> ensureFieldProperty(gfqq);
        //System.out.println("gfqq = " + gfqq);
        //assertTrue("gfqq.isField: ", gfqq.isField());
    }


    /**
     * Test recursive dense pseudo division.
     */
    public void testRecursivePseudoDivisionDense() {
        String[] cnames = new String[] { "x" };
        String[] mnames = new String[] { "t" };
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), to, cnames);
        //GenPolynomialRing<BigRational> rdfac = new GenPolynomialRing<BigRational>(new BigRational(1),dfac);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(dfac, to, mnames);
        QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(dfac);
        GenPolynomialRing<Quotient<BigInteger>> rqfac = new GenPolynomialRing<Quotient<BigInteger>>(qfac,
                        rfac);
        //System.out.println("\ndfac  = " + dfac);
        //System.out.println("rdfac = " + rdfac);
        //System.out.println("rfac  = " + rfac);
        //System.out.println("qfac  = " + qfac);
        //System.out.println("rqfac = " + rqfac);

        ar = rfac.random(kl, 2 * ll, el + 4, q);
        //ar = rfac.parse(" ( -2 x^4 + 8 x^3 - 5 x^2 - x + 6  ) t^3 + ( 2 x - 8  ) t^2 - ( 13 x^4 - 13 x^3 + x^2 + 2 x - 13  ) ");
        br = rfac.random(kl, 2 * ll, el + 2, q);
        //ar = ar.multiply(br);
        //br = rfac.parse(" ( 13 ) t^3 + ( 3 x^2 - 6  ) t - ( 13 x^4 - 8 x^3 + 10 x^2 + 22 x + 21  ) ");
        //System.out.println("ar   = " + ar);
        //System.out.println("br   = " + br);

        dr = PolyUtil.<BigInteger> recursivePseudoDivide(ar, br);
        //System.out.println("qr   = " + dr);
        cr = PolyUtil.<BigInteger> recursiveDensePseudoRemainder(ar, br);
        //System.out.println("rr   = " + cr);

        //boolean t = PolyUtil.<BigInteger> isRecursivePseudoQuotientRemainder(ar, br, dr, cr);
        //System.out.println("assertTrue lc^n a = q b + r: " + t);
        //assertTrue("lc^n a = q b + r: " + cr, t); // ?? not always true

        GenPolynomial<Quotient<BigInteger>> ap = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, ar);
        GenPolynomial<Quotient<BigInteger>> bp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, br);
        GenPolynomial<Quotient<BigInteger>> cp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, cr);
        GenPolynomial<Quotient<BigInteger>> dp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, dr);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        ////System.out.println("dp  = " + dp);
        //System.out.println("dp  = " + dp.monic());

        GenPolynomial<Quotient<BigInteger>> qp = ap.divide(bp);
        GenPolynomial<Quotient<BigInteger>> rp = ap.remainder(bp);
        ////System.out.println("qp  = " + qp);
        //System.out.println("qp  = " + qp.monic());
        //System.out.println("rp  = " + rp);
        GenPolynomial<Quotient<BigInteger>> rhs = qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);

        assertEquals("ap = qp bp + rp: ", ap, rhs);

        assertEquals("cp = rp: ", rp.monic(), cp.monic());
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        assertEquals("dp = qp: ", qp.monic(), dp.monic()); // ??
    }


    /**
     * Test recursive sparse pseudo division.
     */
    public void testRecursivePseudoDivisionSparse() {
        String[] cnames = new String[] { "x" };
        String[] mnames = new String[] { "t" };
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), to, cnames);
        //GenPolynomialRing<BigRational> rdfac = new GenPolynomialRing<BigRational>(new BigRational(1),dfac);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(dfac, to, mnames);
        QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(dfac);
        GenPolynomialRing<Quotient<BigInteger>> rqfac = new GenPolynomialRing<Quotient<BigInteger>>(qfac,
                        rfac);
        //System.out.println("\ndfac  = " + dfac);
        //System.out.println("rdfac = " + rdfac);
        //System.out.println("rfac  = " + rfac);
        //System.out.println("qfac  = " + qfac);
        //System.out.println("rqfac = " + rqfac);

        ar = rfac.random(kl, 2 * ll, el + 4, q);
        //ar = rfac.parse(" ( -2 x^4 + 8 x^3 - 5 x^2 - x + 6  ) t^3 + ( 2 x - 8  ) t^2 - ( 13 x^4 - 13 x^3 + x^2 + 2 x - 13  ) ");
        br = rfac.random(kl, 2 * ll, el + 2, q);
        //ar = ar.multiply(br);
        //br = rfac.parse(" ( 13 ) t^3 + ( 3 x^2 - 6  ) t - ( 13 x^4 - 8 x^3 + 10 x^2 + 22 x + 21  ) ");
        //System.out.println("ar   = " + ar);
        //System.out.println("br   = " + br);

        dr = PolyUtil.<BigInteger> recursivePseudoDivide(ar, br);
        //System.out.println("qr   = " + dr);
        cr = PolyUtil.<BigInteger> recursiveSparsePseudoRemainder(ar, br);
        //System.out.println("rr   = " + cr);

        boolean t = PolyUtil.<BigInteger> isRecursivePseudoQuotientRemainder(ar, br, dr, cr);
        //System.out.println("assertTrue lc^n a = q b + r: " + t);
        assertTrue("lc^n a = q b + r: " + dr + "*b + " + cr, t); // not always true, some what fixed

        GenPolynomial<Quotient<BigInteger>> ap = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, ar);
        GenPolynomial<Quotient<BigInteger>> bp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, br);
        GenPolynomial<Quotient<BigInteger>> cp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, cr);
        GenPolynomial<Quotient<BigInteger>> dp = PolyUfdUtil
                        .<BigInteger> quotientFromIntegralCoefficients(rqfac, dr);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        ////System.out.println("dp  = " + dp);
        //System.out.println("dp  = " + dp.monic());

        GenPolynomial<Quotient<BigInteger>> qp = ap.divide(bp);
        GenPolynomial<Quotient<BigInteger>> rp = ap.remainder(bp);
        ////System.out.println("qp  = " + qp);
        //System.out.println("qp  = " + qp.monic());
        //System.out.println("rp  = " + rp);
        GenPolynomial<Quotient<BigInteger>> rhs = qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);

        assertEquals("ap = qp bp + rp: ", ap, rhs);

        //System.out.println("cp = rp: rp_m = " + rp.monic() + ", cp_m = " + cp.monic());
        assertEquals("cp = rp: ", rp.monic(), cp.monic());
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        //System.out.println("qp = dp: qp_m = " + qp.monic() + ", dp_m = " + dp.monic());
        assertEquals("dp = qp: ", qp.monic(), dp.monic()); // ??
    }


    /**
     * Test integer from rational coefficients, recursive.
     */
    public void testRecursiveIntegerFromRationalCoefficients() {
        crfac = new GenPolynomialRing<BigRational>(new BigRational(1), cfac);
        rrfac = new GenPolynomialRing<GenPolynomial<BigRational>>(crfac, rfac);
        //System.out.println("\ncfac  = " + cfac);
        //System.out.println("crfac = " + crfac);
        //System.out.println("rfac  = " + rfac);
        //System.out.println("rrfac  = " + rrfac);

        // BigRational
        arr = rrfac.random(kl * kl, 2 * ll, el + 4, q);
        arr = arr.sum(arr).multiply(arr); //rrfac.fromInteger(11));
        //System.out.println("arr   = " + arr);

        // BigInteger
        ar = PolyUfdUtil.integerFromRationalCoefficients(rfac, arr);
        //System.out.println("ar   = " + ar);

        crr = PolyUtil.<BigRational> monic(arr);
        //System.out.println("crr   = " + crr);

        // BigRational
        err = PolyUfdUtil.<BigRational> fromIntegerCoefficients(rrfac, ar);
        //System.out.println("err   = " + err);
        err = PolyUtil.<BigRational> monic(err);
        //System.out.println("err   = " + err);

        assertEquals("crr != err: ", crr, err);
    }


    /**
     * Test norm over algebraic number field.
     */
    public void testNormAlgebraicNumberField() {
        int deg = 5;
        // characteristic zero
        BigRational q = BigRational.ONE;
        //System.out.println("q = " + q.toScriptFactory());
        AlgebraicNumberRing<BigRational> gfqq = PolyUfdUtil.<BigRational> algebraicNumberField(q, deg);
        //System.out.println("gfqq = " + gfqq.toScript());
        assertTrue("gfqq.isField: ", gfqq.isField());

        GenPolynomialRing<AlgebraicNumber<BigRational>> pafac;
        pafac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(gfqq, new String[] { "x" },
                        TermOrderByName.INVLEX);
        //System.out.println("pafac = " + pafac.toScript());

        GenPolynomial<AlgebraicNumber<BigRational>> P, Q, R;
        P = pafac.random(2, 4, 3, 0.4f).monic();
        Q = pafac.random(2, 4, 3, 0.4f).monic();
        R = P.multiply(Q);
        //System.out.println("P = " + P);
        //System.out.println("Q = " + Q);
        //System.out.println("R = " + R);

        GenPolynomial<BigRational> nP, nQ, nR, nPQ, rem, gcd;
        nP = PolyUfdUtil.<BigRational> norm(P);
        nQ = PolyUfdUtil.<BigRational> norm(Q);
        nR = PolyUfdUtil.<BigRational> norm(R);
        nPQ = nP.multiply(nQ);
        //System.out.println("normP  = " + nP);
        //System.out.println("normQ  = " + nQ);
        //System.out.println("normR  = " + nR);
        //System.out.println("normPQ = " + nPQ);

        //System.out.println("normP*normQ = norm(P*Q): " + nPQ.equals(nR) + "\n");
        if (nPQ.equals(nR)) {
            assertEquals("normP*normQ == norm(P*Q)", nPQ, nR);
            return;
        }
        rem = nR.remainder(nPQ);
        //System.out.println("norm(P*Q) mod normP*normQ == 0: " + rem.isZERO());
        if (rem.isZERO()) {
            assertTrue("norm(P*Q) mod normP*normQ == 0", rem.isZERO());
            return;
        }

        GreatestCommonDivisorAbstract<BigRational> gcdr = GCDFactory.getImplementation(q);
        gcd = gcdr.gcd(nPQ, nR);
        //System.out.println("gcd(norm(P*Q), normP*normQ) != 1: " + (!gcd.isONE()));
        if (!gcd.isONE()) {
            assertFalse("gcd(norm(P*Q), normP*normQ) != 1", gcd.isONE());
            return;
        }
        // unreachable:        
        FactorAbstract<BigRational> facr = FactorFactory.getImplementation(q);
        SortedMap<GenPolynomial<BigRational>, Long> fnPQ = facr.factors(nPQ);
        System.out.println("fnPQ = " + fnPQ);
        SortedMap<GenPolynomial<BigRational>, Long> fnR = facr.factors(nR);
        System.out.println("fnR = " + fnR);
    }


    /**
     * Test multivariate norm over algebraic number field.
     */
    public void testMultiNormAlgebraicNumberField() {
        int deg = 5;
        // characteristic zero
        BigRational q = BigRational.ONE;
        //System.out.println("q = " + q.toScriptFactory());
        AlgebraicNumberRing<BigRational> gfqq = PolyUfdUtil.<BigRational> algebraicNumberField(q, deg);
        //System.out.println("gfqq = " + gfqq.toScript());
        assertTrue("gfqq.isField: ", gfqq.isField());

        GenPolynomialRing<AlgebraicNumber<BigRational>> pafac;
        pafac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(gfqq, new String[] { "x", "y" },
                        TermOrderByName.INVLEX);
        //System.out.println("pafac = " + pafac.toScript());

        GenPolynomial<AlgebraicNumber<BigRational>> P, Q, R;
        P = pafac.random(2, 4, 2, 0.2f).monic();
        Q = pafac.random(2, 4, 2, 0.2f).monic();
        R = P.multiply(Q);
        //System.out.println("P = " + P);
        //System.out.println("Q = " + Q);
        //System.out.println("R = " + R);

        GenPolynomial<BigRational> nP, nQ, nR, nPQ, rem, gcd;
        nP = PolyUfdUtil.<BigRational> norm(P);
        nQ = PolyUfdUtil.<BigRational> norm(Q);
        nR = PolyUfdUtil.<BigRational> norm(R);
        nPQ = nP.multiply(nQ);
        //System.out.println("normP  = " + nP);
        //System.out.println("normQ  = " + nQ);
        //System.out.println("normR  = " + nR);
        //System.out.println("normPQ = " + nPQ);

        //System.out.println("normP*normQ == norm(P*Q): " + nPQ.equals(nR) + "\n");
        if (nPQ.equals(nR)) {
            assertEquals("normP*normQ == norm(P*Q)", nPQ, nR);
            return;
        }

        rem = nR.remainder(nPQ);
        //System.out.println("norm(P*Q) mod normP*normQ == 0: " + rem.isZERO());
        if (rem.isZERO()) {
            assertTrue("norm(P*Q) mod normP*normQ == 0", rem.isZERO());
            return;
        }

        GreatestCommonDivisorAbstract<BigRational> gcdr = GCDFactory.getImplementation(q);
        gcd = gcdr.gcd(nPQ, nR);
        //System.out.println("gcd(norm(P*Q), normP*normQ) != 1: " + (!gcd.isONE()));
        if (!gcd.isONE()) {
            assertFalse("gcd(norm(P*Q), normP*normQ) != 1", gcd.isONE());
            return;
        }
        // unreachable:        
        FactorAbstract<BigRational> facr = FactorFactory.getImplementation(q);
        SortedMap<GenPolynomial<BigRational>, Long> fnPQ = facr.factors(nPQ);
        System.out.println("fnPQ = " + fnPQ);
        SortedMap<GenPolynomial<BigRational>, Long> fnR = facr.factors(nR);
        System.out.println("fnR = " + fnR);
    }


    /**
     * Q matrix construction for Berlekamp.
     */
    public void testQmatix() {
        int q = 11; //32003; //11;
        ModIntRing mi = new ModIntRing(q);
        // for (ModInt s : mi) {
        //      System.out.print(" " + s + " ");
        // }
        // System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //System.out.println("A = " + A.toScript());
        int d = (int) A.degree(0);
        ArrayList<ArrayList<ModInt>> Q = PolyUfdUtil.<ModInt> constructQmatrix(A);
        //System.out.println("Q = " + Q);
        int n = Q.size();
        int m = Q.get(0).size();
        assertTrue("size(Q) == deg(a): " + Q, n == d);
        assertTrue("size(Q(0)) == deg(a): " + Q, m == d);

        GenMatrixRing<ModInt> mfac = new GenMatrixRing<ModInt>(mi, n, m);
        //System.out.println("mfac = " + mfac.toScript());
        GenMatrix<ModInt> Qm = new GenMatrix<ModInt>(mfac, Q);
        //System.out.println("Qm = " + Qm);
        GenMatrix<ModInt> Qm1 = Qm.subtract(mfac.getONE());
        //System.out.println("Qm1 = " + Qm1);

        LinAlg<ModInt> lu = new LinAlg<ModInt>();
        List<GenVector<ModInt>> Nsb = lu.nullSpaceBasis(Qm1);
        //System.out.println("Nsb = " + Nsb);
        //GenMatrixRing<ModInt> nfac = new GenMatrixRing<ModInt>(mi,k,d);
        GenMatrix<ModInt> Ns = mfac.fromVectors(Nsb);
        //System.out.println("Ns = " + Ns);
        GenMatrix<ModInt> L1 = Ns.negate(); //mfac.getONE().subtract(Ns);
        //System.out.println("L1 = " + L1);
        int k = L1.ring.rows;
        //System.out.println("k = " + k);
        assertTrue("0 <= k && k < n: " + L1, 0 <= k && k < n);

        // test with random polynomial
        do {
            A = pfac.random(10);
            //System.out.println("A = " + A.toScript());
        } while (A.isZERO() || A.degree(0) <= 1);
        d = (int) A.degree(0);
        Q = PolyUfdUtil.<ModInt> constructQmatrix(A);
        //System.out.println("Q = " + Q);
        n = Q.size();
        m = Q.get(0).size();
        assertTrue("size(Q) == deg(a): " + Q, n == d);
        assertTrue("size(Q(0)) == deg(a): " + Q, m == d);
        ArrayList<ArrayList<ModInt>> Qa = Q;

        mfac = new GenMatrixRing<ModInt>(mi, n, m);
        //System.out.println("mfac = " + mfac.toScript());
        Qm = new GenMatrix<ModInt>(mfac, Q);
        //System.out.println("Qm = " + Qm);
        Qm1 = Qm.subtract(mfac.getONE());
        //System.out.println("Qm1 = " + Qm1);

        Nsb = lu.nullSpaceBasis(Qm1);
        //System.out.println("Nsb = " + Nsb);
        //GenMatrixRing<ModInt> nfac = new GenMatrixRing<ModInt>(mi,k,d);
        Ns = mfac.fromVectors(Nsb);
        //System.out.println("Ns = " + Ns);
        L1 = Ns.negate(); //mfac.getONE().subtract(Ns);
        //System.out.println("L1 = " + L1);
        k = L1.ring.rows;
        //System.out.println("k = " + k);
        assertTrue("0 <= k && k < n: " + L1, 0 <= k && k <= n);

        // test with modPower
        GenPolynomial<ModInt> x = pfac.univariate(0);
        //System.out.println("x = " + x.toScript());
        GenPolynomial<ModInt> r = pfac.getONE();
        //System.out.println("r = " + r.toScript());
        ArrayList<GenPolynomial<ModInt>> Qp = new ArrayList<GenPolynomial<ModInt>>();
        Qp.add(r);
        GenPolynomial<ModInt> pow = Power.<GenPolynomial<ModInt>> modPositivePower(x, q, A);
        //System.out.println("pow = " + pow.toScript());
        Qp.add(pow);
        r = pow;
        for (int i = 2; i < d; i++) {
            r = r.multiply(pow).remainder(A);
            Qp.add(r);
        }
        //System.out.println("Qp = " + Qp);
        assertTrue("deg(r) < deg(A): " + Qp, r.degree(0) <= A.degree(0));

        UnivPowerSeriesRing<ModInt> psfac = new UnivPowerSeriesRing<ModInt>(pfac);
        //System.out.println("psfac = " + psfac.toScript());
        ArrayList<ArrayList<ModInt>> Qb = new ArrayList<ArrayList<ModInt>>();
        for (GenPolynomial<ModInt> p : Qp) {
            UnivPowerSeries<ModInt> ps = psfac.fromPolynomial(p);
            //System.out.println("ps = " + ps.toScript());
            ArrayList<ModInt> pr = new ArrayList<ModInt>();
            for (int i = 0; i < d; i++) {
                ModInt c = ps.coefficient(i);
                pr.add(c);
            }
            Qb.add(pr);
        }
        //System.out.println("Qb = " + Qb);
        assertEquals("Qa == Qb: ", Qa, Qb);
    }


    /**
     * Test search evaluation points.
     */
    public void testSearchEvaluationPoints() {
        //System.out.println("dfac = " + dfac.toScript());
        crfac = new GenPolynomialRing<BigRational>(new BigRational(1), dfac);
        //System.out.println("crfac = " + crfac.toScript());
        SquarefreeAbstract<BigInteger> isqf = SquarefreeFactory.getImplementation(dfac.coFac);
        SquarefreeAbstract<BigRational> rsqf = SquarefreeFactory.getImplementation(crfac.coFac);
        for (int i = 0; i < 5; i++) {
            a = dfac.random(kl, ll * 2, el * 5, q);
            //System.out.println("a = " + a + ", isSquarefree: " + isqf.isSquarefree(a));
            a = isqf.squarefreePart(a);
            EvalPoints<BigInteger> L = PolyUfdUtil.<BigInteger> evaluationPoints(a);
            //System.out.println("L = " + L);
            assertFalse("L != (): ", L.evalPoints.isEmpty());

            GenPolynomial<BigRational> ar = crfac.random(kl, ll, el * 2, q * 1.5f);
            //System.out.println("ar = " + ar + ", isSquarefree: " + rsqf.isSquarefree(ar));
            ar = rsqf.squarefreePart(ar);
            EvalPoints<BigRational> Lr = PolyUfdUtil.<BigRational> evaluationPoints(ar);
            //System.out.println("Lr = " + Lr);
            assertFalse("Lr != (): ", Lr.evalPoints.isEmpty());
        }
    }

}
