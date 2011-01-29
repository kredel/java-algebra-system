/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModularRingFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * HenselUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class HenselUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>HenselUtilTest</CODE> object.
     * @param name String.
     */
    public HenselUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HenselUtilTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


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
     * Test Hensel lifting.
     * 
     */
    public void testHenselLifting() {
        java.math.BigInteger p;
        p = getPrime1();
        //p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        BigInteger mi = m;

        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 1, to);

        dfac = new GenPolynomialRing<BigInteger>(mi, 1, to);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> sp;
        GenPolynomial<ModInteger> tp;
        GenPolynomial<ModInteger>[] egcd;
        GenPolynomial<ModInteger> ap1;
        GenPolynomial<ModInteger> bp1;

        HenselApprox<ModInteger> lift;
        GenPolynomial<BigInteger> a1;
        GenPolynomial<BigInteger> b1;
        GenPolynomial<BigInteger> c1;

        for (int i = 1; i < 3; i++) {
            a = dfac.random(kl + 70 * i, ll, el + 5, q).abs();
            b = dfac.random(kl + 70 * i, ll, el + 5, q).abs();
            //a = dfac.univariate(0).sum( dfac.fromInteger(30) );
            //b = dfac.univariate(0).subtract( dfac.fromInteger(20) );
            //b = b.multiply( dfac.univariate(0) ).sum( dfac.fromInteger(168));
            c = a.multiply(b);
            if (a.degree(0) < 1 || b.degree(0) < 2) {
                continue;
            }

            ap = PolyUtil.fromIntegerCoefficients(pfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(pfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            cp = PolyUtil.fromIntegerCoefficients(pfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            ap1 = ap; //.monic();
            bp1 = bp; //.monic();
            egcd = ap1.egcd(bp1);
            if (!egcd[0].isONE()) {
                continue;
            }
            sp = egcd[1];
            tp = egcd[2];

            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }

            //System.out.println("a     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //--System.out.println("mi    = " + mi);
            //System.out.println("ap    = " + ap);
            //System.out.println("bp    = " + bp);
            //System.out.println("cp    = " + cp);
            // System.out.println("ap*bp = " + ap.multiply(bp));
            //System.out.println("gcd   = " + egcd[0]);
            //System.out.println("gcd   = " + ap1.multiply(sp).sum(bp1.multiply(tp)));
            //System.out.println("sp    = " + sp);
            //System.out.println("tp    = " + tp);

            try {
                lift = HenselUtil.<ModInteger> liftHensel(c, mi, ap, bp, sp, tp);
                a1 = lift.A;
                b1 = lift.B;
                c1 = a1.multiply(b1);
                //System.out.println("\na     = " + a);
                //System.out.println("b     = " + b);
                //System.out.println("c     = " + c);
                //System.out.println("a1    = " + a1);
                //System.out.println("b1    = " + b1);
                //System.out.println("a1*b1 = " + c1);
                //assertEquals("lift(a mod p) = a",a,a1);
                //assertEquals("lift(b mod p) = b",b,b1);

                assertEquals("lift(a b mod p) = a b", c, c1);
            } catch (NoLiftingException e) {
                fail("" + e);
            }
        }
    }


    /**
     * Test Hensel lifting with gcd.
     * 
     */
    public void testHenselLiftingGcd() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        BigInteger mi = m;

        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 1, to);

        dfac = new GenPolynomialRing<BigInteger>(mi, 1, to);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;

        HenselApprox<ModInteger> lift;
        GenPolynomial<BigInteger> a1;
        GenPolynomial<BigInteger> b1;
        GenPolynomial<BigInteger> c1;

        for (int i = 1; i < 3; i++) { // 70 better for quadratic
            a = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            b = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            //a = dfac.univariate(0).sum( dfac.fromInteger(30) );
            //b = dfac.univariate(0).subtract( dfac.fromInteger(20) );
            //b = b.multiply( dfac.univariate(0) ).sum( dfac.fromInteger(168));
            c = a.multiply(b);
            if (a.degree(0) < 1 || b.degree(0) < 2) {
                continue;
            }

            ap = PolyUtil.fromIntegerCoefficients(pfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(pfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            cp = PolyUtil.fromIntegerCoefficients(pfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }

            //System.out.println("a     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //--System.out.println("mi    = " + mi);
            //System.out.println("ap    = " + ap);
            //System.out.println("bp    = " + bp);
            //System.out.println("cp    = " + cp);
            // System.out.println("ap*bp = " + ap.multiply(bp));
            //System.out.println("gcd   = " + egcd[0]);
            //System.out.println("gcd   = " + ap1.multiply(sp).sum(bp1.multiply(tp)));
            //System.out.println("sp    = " + sp);
            //System.out.println("tp    = " + tp);

            long tq = System.currentTimeMillis();
            try {
                lift = HenselUtil.<ModInteger> liftHensel(c, mi, ap, bp);
                tq = System.currentTimeMillis() - tq;
                a1 = lift.A;
                b1 = lift.B;
                c1 = a1.multiply(b1);
                assertEquals("lift(a b mod p) = a b", c, c1);
            } catch (NoLiftingException e) {
                // ok no fail(""+e);
            }

            //System.out.println("\na     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //System.out.println("a1    = " + a1);
            //System.out.println("b1    = " + b1);
            //System.out.println("a1*b1 = " + c1);

            //assertEquals("lift(a mod p) = a",a,a1);
            //assertEquals("lift(b mod p) = b",b,b1);
        }
    }


    /**
     * Test Hensel quadratic lifting.
     * 
     */
    public void testHenselQuadraticLifting() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        BigInteger mi = m;

        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 1, to);

        dfac = new GenPolynomialRing<BigInteger>(mi, pfac);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> sp;
        GenPolynomial<ModInteger> tp;
        GenPolynomial<ModInteger>[] egcd;
        GenPolynomial<ModInteger> ap1;
        GenPolynomial<ModInteger> bp1;

        HenselApprox<ModInteger> lift;
        GenPolynomial<BigInteger> a1;
        GenPolynomial<BigInteger> b1;
        GenPolynomial<BigInteger> c1;

        for (int i = 1; i < 3; i++) { // 70 better for quadratic
            a = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            b = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            //a = dfac.univariate(0).sum( dfac.fromInteger(30) );
            //b = dfac.univariate(0).subtract( dfac.fromInteger(20) );
            //b = b.multiply( dfac.univariate(0) ).sum( dfac.fromInteger(168));
            c = a.multiply(b);
            if (a.degree(0) < 1 || b.degree(0) < 2) {
                continue;
            }

            ap = PolyUtil.fromIntegerCoefficients(pfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(pfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            cp = PolyUtil.fromIntegerCoefficients(pfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            ap1 = ap; //.monic();
            bp1 = bp; //.monic();
            egcd = ap1.egcd(bp1);
            if (!egcd[0].isONE()) {
                continue;
            }
            sp = egcd[1];
            tp = egcd[2];

            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }

            //System.out.println("a     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //--System.out.println("mi    = " + mi);
            //System.out.println("ap    = " + ap);
            //System.out.println("bp    = " + bp);
            //System.out.println("cp    = " + cp);
            // System.out.println("ap*bp = " + ap.multiply(bp));
            //System.out.println("gcd   = " + egcd[0]);
            //System.out.println("gcd   = " + ap1.multiply(sp).sum(bp1.multiply(tp)));
            //System.out.println("sp    = " + sp);
            //System.out.println("tp    = " + tp);

            long tq = System.currentTimeMillis();
            try {
                lift = HenselUtil.<ModInteger> liftHenselQuadratic(c, mi, ap, bp, sp, tp);
                tq = System.currentTimeMillis() - tq;
                a1 = lift.A;
                b1 = lift.B;
                c1 = a1.multiply(b1);
                //System.out.println("\na     = " + a);
                //System.out.println("b     = " + b);
                //System.out.println("c     = " + c);
                //System.out.println("a1    = " + a1);
                //System.out.println("b1    = " + b1);
                //System.out.println("a1*b1 = " + c1);
                //assertEquals("lift(a mod p) = a",a,a1);
                //assertEquals("lift(b mod p) = b",b,b1);

                assertEquals("lift(a b mod p) = a b", c, c1);
            } catch (NoLiftingException e) {
                fail("" + e);
            }

            if (false) {
                long t = System.currentTimeMillis();
                try {
                    lift = HenselUtil.<ModInteger> liftHensel(c, mi, ap, bp, sp, tp);
                    t = System.currentTimeMillis() - t;
                    a1 = lift.A;
                    b1 = lift.B;
                    c1 = a1.multiply(b1);

                    //System.out.println("\na     = " + a);
                    //System.out.println("b     = " + b);
                    //System.out.println("c     = " + c);
                    //System.out.println("a1    = " + a1);
                    //System.out.println("b1    = " + b1);
                    //System.out.println("a1*b1 = " + c1);

                    //assertEquals("lift(a mod p) = a",a,a1);
                    //assertEquals("lift(b mod p) = b",b,b1);
                    assertEquals("lift(a b mod p) = a b", c, c1);
                } catch (NoLiftingException e) {
                    fail("" + e);
                }
                System.out.println("\nquadratic Hensel time = " + tq);
                System.out.println("linear    Hensel time = " + t);
            }
            //break;
        }
    }


    /**
     * Test Hensel quadratic lifting with gcd.
     * 
     */
    public void testHenselQuadraticLiftingGcd() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        BigInteger mi = m;

        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 1, to);

        dfac = new GenPolynomialRing<BigInteger>(mi, pfac);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;

        HenselApprox<ModInteger> lift;
        GenPolynomial<BigInteger> a1;
        GenPolynomial<BigInteger> b1;
        GenPolynomial<BigInteger> c1;

        for (int i = 1; i < 3; i++) { // 70 better for quadratic
            a = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            b = dfac.random(kl + 70 * i, ll + 10, el + 5, q).abs();
            //a = dfac.univariate(0).sum( dfac.fromInteger(30) );
            //b = dfac.univariate(0).subtract( dfac.fromInteger(20) );
            //b = b.multiply( dfac.univariate(0) ).sum( dfac.fromInteger(168));
            c = a.multiply(b);
            if (a.degree(0) < 1 || b.degree(0) < 2) {
                continue;
            }

            ap = PolyUtil.fromIntegerCoefficients(pfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(pfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            cp = PolyUtil.fromIntegerCoefficients(pfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }

            //System.out.println("a     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //--System.out.println("mi    = " + mi);
            //System.out.println("ap    = " + ap);
            //System.out.println("bp    = " + bp);
            //System.out.println("cp    = " + cp);
            // System.out.println("ap*bp = " + ap.multiply(bp));
            //System.out.println("gcd   = " + egcd[0]);
            //System.out.println("gcd   = " + ap1.multiply(sp).sum(bp1.multiply(tp)));
            //System.out.println("sp    = " + sp);
            //System.out.println("tp    = " + tp);

            long tq = System.currentTimeMillis();
            try {
                lift = HenselUtil.<ModInteger> liftHenselQuadratic(c, mi, ap, bp);
                tq = System.currentTimeMillis() - tq;
                a1 = lift.A;
                b1 = lift.B;
                c1 = a1.multiply(b1);
                assertEquals("lift(a b mod p) = a b", c, c1);
            } catch (NoLiftingException e) {
                //ok no fail(""+e);
            }

            //System.out.println("\na     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);
            //System.out.println("a1    = " + a1);
            //System.out.println("b1    = " + b1);
            //System.out.println("a1*b1 = " + c1);

            //assertEquals("lift(a mod p) = a",a,a1);
            //assertEquals("lift(b mod p) = b",b,b1);
        }
    }


    /**
     * Test lifting of extended Euclidean relation.
     * 
     */
    public void testLiftingEgcd() {
        java.math.BigInteger p;
        //p = getPrime1();
        //p = new java.math.BigInteger("19");
        p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);
        //BigInteger mi = m;

        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, 1, to,
                new String[] { "x" });

        dfac = new GenPolynomialRing<BigInteger>(m, mfac);
        GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getProxy(m);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> dp;
        GenPolynomial<ModInteger>[] lift;
        GenPolynomial<ModInteger> s;
        GenPolynomial<ModInteger> t;

        for (int i = 1; i < 2; i++) { // 70 better for quadratic
            a = dfac.random(kl + 3 * i, ll + 1, el + 1, q).abs();
            b = dfac.random(kl + 3 * i, ll + 1, el + 5, q).abs();
            d = ufd.baseGcd(a, b);
            //System.out.println("d   = " + d);
            if (!d.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, d);
                b = PolyUtil.<BigInteger> basePseudoDivide(b, d);
            }
            if (a.degree(0) < 1 || b.degree(0) < 2) {
                continue;
            }
            ap = PolyUtil.fromIntegerCoefficients(mfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(mfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            dp = ap.gcd(bp);
            //System.out.println("dp  = " + dp);
            if (!dp.isONE()) {
                continue;
            }
            c = a.multiply(b);
            cp = PolyUtil.fromIntegerCoefficients(mfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            BigInteger mi;
            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }
            long k = 1;
            BigInteger pi = m;
            while (pi.compareTo(mi) < 0) {
                k++;
                pi = pi.multiply(m);
            }
            //System.out.println("mi  = " + mi);
            //System.out.println("pi  = " + pi);
            //System.out.println("k   = " + k);

            //System.out.println("a   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("c   = " + c);
            //System.out.println("ap  = " + ap);
            //System.out.println("bp  = " + bp);
            //System.out.println("cp  = " + cp);

            long tq = System.currentTimeMillis();
            try {
                lift = HenselUtil.<ModInteger> liftExtendedEuclidean(ap, bp, k);
                tq = System.currentTimeMillis() - tq;
                s = lift[0];
                t = lift[1];
                ModularRingFactory<ModInteger> mcfac = (ModularRingFactory<ModInteger>) s.ring.coFac;
                GenPolynomialRing<ModInteger> mfac1 = new GenPolynomialRing<ModInteger>(mcfac, mfac);
                //System.out.println("\nmcfac  = " + mcfac);
                ap = PolyUtil.fromIntegerCoefficients(mfac1, PolyUtil
                        .integerFromModularCoefficients(dfac, ap));
                bp = PolyUtil.fromIntegerCoefficients(mfac1, PolyUtil
                        .integerFromModularCoefficients(dfac, bp));
                cp = s.multiply(ap).sum(t.multiply(bp));
                //System.out.println("s   = " + s);
                //System.out.println("t   = " + t);
                //System.out.println("ap  = " + ap);
                //System.out.println("bp  = " + bp);
                //System.out.println("cp  = " + cp);

                assertTrue("lift(s a + t b mod p^k) = 1: " + cp, cp.isONE());
            } catch (NoLiftingException e) {
                fail("" + e);
            }
            //System.out.println("time = " + tq);
        }
    }


    /**
     * Test lifting of list of extended Euclidean relation.
     * 
     */
    public void testLiftingEgcdList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        // BigInteger mi = m;
        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, 1, to,
                new String[] { "x" });

        dfac = new GenPolynomialRing<BigInteger>(m, mfac);
        GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getProxy(m);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> dp;
        GenPolynomial<ModInteger> ep;
        List<GenPolynomial<ModInteger>> lift;
        GenPolynomial<ModInteger> s;
        GenPolynomial<ModInteger> t;

        for (int i = 1; i < 2; i++) { // 70 better for quadratic
            a = dfac.random(kl + 3 * i, ll + 5, el + 1, q).abs();
            //a = dfac.parse("(x - 1)");
            b = dfac.random(kl + 3 * i, ll + 5, el + 5, q).abs();
            //b = dfac.parse("(x - 2)");
            e = ufd.baseGcd(a, b);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
            }
            if (a.degree(0) < 1 || b.degree(0) < 1) {
                continue;
            }
            ap = PolyUtil.fromIntegerCoefficients(mfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(mfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            ep = ap.gcd(bp);
            //System.out.println("ep  = " + ep);
            if (!ep.isONE()) {
                continue;
            }
            d = dfac.random(kl + 3 * i, ll + 5, el + 4, q).abs();
            //d = dfac.parse("(x - 3)");
            e = ufd.baseGcd(a, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            e = ufd.baseGcd(b, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            if (d.degree(0) < 1) {
                continue;
            }
            dp = PolyUtil.fromIntegerCoefficients(mfac, d);
            if (!d.degreeVector().equals(dp.degreeVector())) {
                continue;
            }

            c = a.multiply(b).multiply(d);
            cp = PolyUtil.fromIntegerCoefficients(mfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            BigInteger mi;
            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }
            BigInteger dn = d.maxNorm();
            if (dn.compareTo(mi) > 0) {
                mi = dn;
            }
            long k = 1;
            BigInteger pi = m;
            while (pi.compareTo(mi) < 0) {
                k++;
                pi = pi.multiply(m);
            }
            //System.out.println("mi  = " + mi);
            //System.out.println("pi  = " + pi);
            //System.out.println("k   = " + k);

            //System.out.println("a   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("d   = " + d);
            //System.out.println("c   = " + c);
            //System.out.println("ap  = " + ap);
            //System.out.println("bp  = " + bp);
            //System.out.println("dp  = " + dp);
            //System.out.println("cp  = " + cp);

            List<GenPolynomial<ModInteger>> A = new ArrayList<GenPolynomial<ModInteger>>();
            List<GenPolynomial<ModInteger>> As = new ArrayList<GenPolynomial<ModInteger>>();
            A.add(ap);
            A.add(bp);
            A.add(dp);
            //A.add(mfac.parse("(x - 4)"));
            //A.add(mfac.parse("(x - 5)"));
            //System.out.println("A  = " + A);
            List<GenPolynomial<ModInteger>> A2 = new ArrayList<GenPolynomial<ModInteger>>();
            List<GenPolynomial<ModInteger>> As2 = new ArrayList<GenPolynomial<ModInteger>>();
            //System.out.println("A2 = " + A2);

            long tq = System.currentTimeMillis();
            try {
                A2.add(ap);
                A2.add(bp);
                GenPolynomial<ModInteger>[] L = HenselUtil.<ModInteger> liftExtendedEuclidean(ap, bp, k);
                //System.out.println("lift(a,b) = " + L[0] + ", " + L[1] + "\n");

                lift = HenselUtil.<ModInteger> liftExtendedEuclidean(A, k);
                tq = System.currentTimeMillis() - tq;

                //System.out.println("");
                //System.out.println("lift(a,b) = " + L[0] + ", " + L[1] );
                //System.out.println("lift = " + lift);

                As = PolyUtil.fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(dfac,
                        lift));
                //System.out.println("As   = " + As);

                boolean il = HenselUtil.<ModInteger> isExtendedEuclideanLift(A, As);
                //System.out.println("islift = " + il);
                assertTrue("lift(s0,s1,s2) mod p^k) = 1: ", il);

                As2.add(L[1]);
                As2.add(L[0]);
                As2 = PolyUtil.fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(dfac,
                        As2));
                //System.out.println("As2   = " + As2);

                il = HenselUtil.<ModInteger> isExtendedEuclideanLift(A2, As2);
                //System.out.println("islift = " + il);
                assertTrue("lift(s a + t b mod p^k) = 1: ", il);
            } catch (NoLiftingException e) {
                // ok fail(""+e);
            }
            //System.out.println("time = " + tq);
        }
    }


    /**
     * Test lifting of list of Diophant relation.
     * 
     */
    public void testLiftingDiophantList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        // BigInteger mi = m;
        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, 1, to,
                new String[] { "x" });

        dfac = new GenPolynomialRing<BigInteger>(m, mfac);
        GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getProxy(m);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> dp;
        GenPolynomial<ModInteger> ep;
        GenPolynomial<ModInteger> fp;
        List<GenPolynomial<ModInteger>> lift;
        GenPolynomial<ModInteger> s;
        GenPolynomial<ModInteger> t;

        for (int i = 1; i < 2; i++) { // 70 better for quadratic
            a = dfac.random(kl + 3 * i, ll + 5, el + 1, q).abs();
            //a = dfac.parse("(x - 1)");
            b = dfac.random(kl + 3 * i, ll + 5, el + 5, q).abs();
            //b = dfac.parse("(x - 2)");
            e = ufd.baseGcd(a, b);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
            }
            if (a.degree(0) < 1 || b.degree(0) < 1) {
                continue;
            }
            ap = PolyUtil.fromIntegerCoefficients(mfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(mfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            ep = ap.gcd(bp);
            //System.out.println("ep  = " + ep);
            if (!ep.isONE()) {
                continue;
            }
            d = dfac.random(kl + 3 * i, ll + 5, el + 4, q).abs();
            //d = dfac.parse("(x - 3)");
            e = ufd.baseGcd(a, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            e = ufd.baseGcd(b, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            if (d.degree(0) < 1) {
                continue;
            }
            dp = PolyUtil.fromIntegerCoefficients(mfac, d);
            if (!d.degreeVector().equals(dp.degreeVector())) {
                continue;
            }

            c = a.multiply(b).multiply(d);
            cp = PolyUtil.fromIntegerCoefficients(mfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            BigInteger mi;
            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }
            BigInteger dn = d.maxNorm();
            if (dn.compareTo(mi) > 0) {
                mi = dn;
            }
            long k = 1;
            BigInteger pi = m;
            while (pi.compareTo(mi) < 0) {
                k++;
                pi = pi.multiply(m);
            }
            //System.out.println("mi  = " + mi);
            //System.out.println("pi  = " + pi);
            //System.out.println("k   = " + k);

            fp = mfac.random(4); //mfac.univariate(0,2); //mfac.getONE();

            //System.out.println("a   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("d   = " + d);
            //System.out.println("c   = " + c);
            //System.out.println("ap  = " + ap);
            //System.out.println("bp  = " + bp);
            //System.out.println("dp  = " + dp);
            //System.out.println("cp  = " + cp);

            List<GenPolynomial<ModInteger>> A = new ArrayList<GenPolynomial<ModInteger>>();
            List<GenPolynomial<ModInteger>> As = new ArrayList<GenPolynomial<ModInteger>>();
            A.add(ap);
            A.add(bp);
            A.add(dp);
            //A.add(mfac.parse("(x - 4)"));
            //A.add(mfac.parse("(x - 5)"));
            //System.out.println("A  = " + A);
            List<GenPolynomial<ModInteger>> A2 = new ArrayList<GenPolynomial<ModInteger>>();
            List<GenPolynomial<ModInteger>> As2 = new ArrayList<GenPolynomial<ModInteger>>();
            //System.out.println("A2 = " + A2);

            long tq = System.currentTimeMillis();
            try {
                A2.add(ap);
                A2.add(bp);
                List<GenPolynomial<ModInteger>> L = HenselUtil.<ModInteger> liftDiophant(ap, bp, fp, k);
                //System.out.println("lift(a,b) = " + L[0] + ", " + L[1] + "\n");

                lift = HenselUtil.<ModInteger> liftDiophant(A2, fp, k); 
                tq = System.currentTimeMillis() - tq;

                //System.out.println("");
                //System.out.println("lift(a,b) = " + L[0] + ", " + L[1] );
                //System.out.println("lift = " + lift);

                As = PolyUtil.fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(dfac,
                        lift));
                //System.out.println("As   = " + As);

                boolean il = HenselUtil.<ModInteger> isDiophantLift(A2, As, fp);
                //System.out.println("islift = " + il);
                assertTrue("lift(s0,s1,s2) mod p^k) = 1: ", il);

                As2.add(L.get(0));
                As2.add(L.get(1));
                As2 = PolyUtil.fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(dfac,
                        As2));
                //System.out.println("As2   = " + As2);

                il = HenselUtil.<ModInteger> isDiophantLift(A2, As2, fp);
                //System.out.println("islift = " + il);
                assertTrue("lift(s a + t b mod p^k) = 1: ", il);
            } catch (NoLiftingException e) {
                // ok fail(""+e);
            }
            //System.out.println("time = " + tq);
        }
    }


    /**
     * Test Hensel lifting new list version.
     * 
     */
    public void testHenselLiftingList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("268435399");
        //p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);
        //.multiply(p).multiply(p).multiply(p);

        // BigInteger mi = m;
        ModIntegerRing pm = new ModIntegerRing(p, true);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, 1, to,
                new String[] { "x" });

        dfac = new GenPolynomialRing<BigInteger>(m, mfac);
        GreatestCommonDivisorAbstract<BigInteger> ufd = GCDFactory.getProxy(m);
        BigInteger one = m.getONE();

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> dp;
        GenPolynomial<ModInteger> ep;
        List<GenPolynomial<ModInteger>> lift;
        GenPolynomial<ModInteger> s;
        GenPolynomial<ModInteger> t;

        for (int i = 1; i < 2; i++) { // 70 better for quadratic
            //a = dfac.random(kl + 30 * i, ll + 5, el + 3, q).abs();
            a = dfac.parse("(x^3 + 20 x^2 - 313131)");
            //a = dfac.parse("(x^6 - 24 x^2 - 17)");
            //a = dfac.parse("(x^6 + 48)");
            b = dfac.random(kl + 30 * i, ll + 5, el + 5, q).abs();
            //b = dfac.parse("(x^4 + 23 x^3 - 32)");
            //b = dfac.parse("(x^7 + 1448)");
            //b = dfac.parse("(x^14 + 44)");
            if (!a.leadingBaseCoefficient().isUnit()) {
                ExpVector e = a.leadingExpVector();
                a.doPutToMap(e, one);
            }
            if (!b.leadingBaseCoefficient().isUnit()) {
                ExpVector e = b.leadingExpVector();
                b.doPutToMap(e, one);
            }
            e = ufd.baseGcd(a, b);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
            }
            if (a.degree(0) < 1) {
                a = dfac.parse("(x^3 + 20 x^2 - 313131)");
            }
            if (b.degree(0) < 1) {
                b = dfac.parse("(x^4 + 23 x^3 - 32)");
            }
            ap = PolyUtil.fromIntegerCoefficients(mfac, a);
            if (!a.degreeVector().equals(ap.degreeVector())) {
                continue;
            }
            bp = PolyUtil.fromIntegerCoefficients(mfac, b);
            if (!b.degreeVector().equals(bp.degreeVector())) {
                continue;
            }
            ep = ap.gcd(bp);
            //System.out.println("ep  = " + ep);
            if (!ep.isONE()) {
                continue;
            }
            d = dfac.random(kl + 30 * i, ll + 5, el + 4, q).abs();
            //d = dfac.parse("(x^2 + 22 x - 33)");
            if (!d.leadingBaseCoefficient().isUnit()) {
                ExpVector e = d.leadingExpVector();
                d.doPutToMap(e, one);
            }
            e = ufd.baseGcd(a, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                a = PolyUtil.<BigInteger> basePseudoDivide(a, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            e = ufd.baseGcd(b, d);
            //System.out.println("e   = " + e);
            if (!e.isONE()) {
                b = PolyUtil.<BigInteger> basePseudoDivide(b, e);
                d = PolyUtil.<BigInteger> basePseudoDivide(d, e);
            }
            if (d.degree(0) < 1) {
                d = dfac.parse("(x^2 + 22 x - 33)");
                //continue;
            }
            dp = PolyUtil.fromIntegerCoefficients(mfac, d);
            if (!d.degreeVector().equals(dp.degreeVector())) {
                continue;
            }

            c = a.multiply(b).multiply(d);
            cp = PolyUtil.fromIntegerCoefficients(mfac, c);
            if (!c.degreeVector().equals(cp.degreeVector())) {
                continue;
            }

            //c = dfac.parse("( (x^6 + 48) * (x^14 + 44) )");

            BigInteger mi;
            BigInteger an = a.maxNorm();
            BigInteger bn = b.maxNorm();
            if (an.compareTo(bn) > 0) {
                mi = an;
            } else {
                mi = bn;
            }
            BigInteger cn = c.maxNorm();
            if (cn.compareTo(mi) > 0) {
                mi = cn;
            }
            BigInteger dn = d.maxNorm();
            if (dn.compareTo(mi) > 0) {
                mi = dn;
            }
            long k = 1;
            BigInteger pi = m;
            while (pi.compareTo(mi) < 0) {
                k++;
                pi = pi.multiply(m);
            }
            //System.out.println("mi  = " + mi);
            //System.out.println("pi  = " + pi);
            //System.out.println("k   = " + k);

            //System.out.println("a   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("d   = " + d);
            //System.out.println("c   = " + c);
            //System.out.println("ap  = " + ap);
            //System.out.println("bp  = " + bp);
            //System.out.println("dp  = " + dp);
            //System.out.println("cp  = " + cp);

            List<GenPolynomial<ModInteger>> A = new ArrayList<GenPolynomial<ModInteger>>();
            List<GenPolynomial<ModInteger>> As = new ArrayList<GenPolynomial<ModInteger>>();
            A.add(ap);
            A.add(bp);
            A.add(dp);
            //A.add(mfac.parse("(x^3 + 26602528)"));
            //A.add(mfac.parse("(31493559 x^3 + 69993768)"));
            //A.add(mfac.parse("(121154481 x^7 + 268435398)"));
            //A.add(mfac.parse("(151258699 x^7 + 90435272)"));
            //monic: x^3 + 26602528 , x^3 + 241832871 , x^7 + 230524583 , x^7 + 37910816

            //A.add( mfac.parse("((x^3 + 26602528)*(31493559 x^3 + 69993768))") );
            //A.add( mfac.parse("((121154481 x^7 + 268435398)*(151258699 x^7 + 90435272))") );
            //System.out.println("A  = " + A);
            A = PolyUtil.monic(A);
            //System.out.println("A  = " + A);

            long tq = System.currentTimeMillis();
            try {
                lift = HenselUtil.<ModInteger> liftHenselMonic(c, A, k);
                tq = System.currentTimeMillis() - tq;

                //System.out.println("\nk  = " + k);
                //System.out.println("c  = " + c);
                //System.out.println("A  = " + A);
                //System.out.println("Ai = [" + a + ", " + b + ", " + d + "]");
                //System.out.println("lift = " + lift);

                List<GenPolynomial<BigInteger>> L = PolyUtil.integerFromModularCoefficients(dfac, lift);
                //System.out.println("L  = " + L);

                //ModularRingFactory<ModInteger> mcfac = (ModularRingFactory<ModInteger>) lift.get(0).ring.coFac;
                //GenPolynomialRing<ModInteger> mfac1 = new GenPolynomialRing<ModInteger>(mcfac, mfac);
                //System.out.println("\nmcfac  = " + mcfac);

                boolean ih = HenselUtil.isHenselLift(c, m, pi, L);
                //System.out.println("ih = " + ih);

                assertTrue("prod(lift(L)) = c: " + c, ih);

                //System.out.println("lift1*lift2 = " + lift.get(0).multiply(lift.get(1)));
                //System.out.println("lift3*lift4 = " + lift.get(2).multiply(lift.get(3)));
                //System.out.println("L1*L2 = " + L.get(0).multiply(L.get(1)));
                //System.out.println("L3*L4 = " + L.get(2).multiply(L.get(3)));

            } catch (NoLiftingException e) {
                // ok fail(""+e);
            }
            //System.out.println("time = " + tq);
        }
    }

}
