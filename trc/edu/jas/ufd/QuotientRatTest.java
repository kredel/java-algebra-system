
/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.LinAlg;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Quotient over BigRational GenPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class QuotientRatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>QuotientRatTest</CODE> object.
     * @param name String.
     */
    public QuotientRatTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(QuotientRatTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    QuotientRing<BigRational> zFac, efac;


    GenPolynomialRing<BigRational> mfac;


    Quotient<BigRational> a, b, c, d, e;


    Quotient<BigRational> az, bz, cz, dz, ez;


    int rl = 3;


    int kl = 5;


    int ll = 3; //6;


    int el = 2;


    float q = 0.4f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        mfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);
        efac = new QuotientRing<BigRational>(mfac);
        zFac = new QuotientRing<BigRational>(mfac, false);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //efac.terminate();
        efac = null;
        zFac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = efac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.val = " + c.val);
        assertTrue("length( c ) = 1", c.num.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = efac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.val = " + d.val);
        assertTrue("length( d ) = 0", d.num.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            //a = efac.random(ll+i);
            a = efac.random(kl * (i + 1), ll + 2 + 2 * i, el, q);
            //System.out.println("a = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.num.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = efac.random(kl, ll, el, q);
        b = efac.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(b);
        d = c.subtract(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        d = d.monic();
        //System.out.println("d = " + d);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        //System.out.println("monic(d) = " + d.monic());

        c = efac.random(kl, ll, el, q);
        //System.out.println("c = " + c);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertEquals("c+(a+b) = (c+a)+b", d, e);


        c = a.sum(efac.getZERO());
        d = a.subtract(efac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = efac.getZERO().sum(a);
        d = efac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);
    }


    /**
     * Test object multiplication.
     */
    public void testMultiplication() {
        a = efac.random(kl, ll, el, q);
        //assertTrue("not isZERO( a )", !a.isZERO() );

        b = efac.random(kl, ll, el, q);
        //assertTrue("not isZERO( b )", !b.isZERO() );

        c = b.multiply(a);
        d = a.multiply(b);
        //assertTrue("not isZERO( c )", !c.isZERO() );
        //assertTrue("not isZERO( d )", !d.isZERO() );

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO());

        assertTrue("a*b = b*a", c.equals(d));
        assertEquals("a*b = b*a", c, d);

        c = efac.random(kl, ll, el, q);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        c = a.multiply(efac.getONE());
        d = efac.getONE().multiply(a);
        assertEquals("a*1 = 1*a", c, d);

        if (a.isUnit()) {
            c = a.inverse();
            d = c.multiply(a);
            //System.out.println("a = " + a);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            assertTrue("a*1/a = 1", d.isONE());
        }
    }


    /**
     * Test addition with syzygy gcd and euclids gcd.
     */
    public void xtestAdditionGcd() {
        long te, tz;

        a = efac.random(kl, ll, el, q);
        b = efac.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        az = new Quotient<BigRational>(zFac, a.num, a.den, true);
        bz = new Quotient<BigRational>(zFac, b.num, b.den, true);

        te = System.currentTimeMillis();
        c = a.sum(b);
        d = c.subtract(b);
        d = d.monic();
        te = System.currentTimeMillis() - te;
        assertEquals("a+b-b = a", a, d);

        tz = System.currentTimeMillis();
        cz = az.sum(bz);
        dz = cz.subtract(bz);
        dz = dz.monic();
        tz = System.currentTimeMillis() - tz;
        assertEquals("a+b-b = a", az, dz);

        System.out.println("te = " + te);
        System.out.println("tz = " + tz);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        c = efac.random(kl, ll, el, q);
        cz = new Quotient<BigRational>(zFac, c.num, c.den, true);


        te = System.currentTimeMillis();
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        te = System.currentTimeMillis() - te;
        assertEquals("c+(a+b) = (c+a)+b", d, e);

        tz = System.currentTimeMillis();
        dz = cz.sum(az.sum(bz));
        ez = cz.sum(az).sum(bz);
        tz = System.currentTimeMillis() - tz;
        assertEquals("c+(a+b) = (c+a)+b", dz, ez);

        System.out.println("te = " + te);
        System.out.println("tz = " + tz);

        c = a.sum(efac.getZERO());
        d = a.subtract(efac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = efac.getZERO().sum(a);
        d = efac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);
    }


    /**
     * Test parse().
     */
    public void testParse() {
        a = efac.random(kl * 2, ll * 2, el * 2, q * 2);
        //assertTrue("not isZERO( a )", !a.isZERO() );

        //PrettyPrint.setInternal();
        //System.out.println("a = " + a);
        PrettyPrint.setPretty();
        //System.out.println("a = " + a);
        String p = a.toString();
        //System.out.println("p = " + p);
        b = efac.parse(p);
        //System.out.println("b = " + b);

        //c = a.subtract(b);
        //System.out.println("c = " + c);
        assertEquals("parse(a.toSting()) = a", a, b);
    }


    /**
     * Test factorization.
     */
    public void testFactors() {
        a = efac.random(kl, ll, el, q);
        b = efac.random(kl, ll, el, q);
        b = b.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.multiply(b);
        //System.out.println("c = " + c);

        SortedMap<Quotient<BigRational>, Long> factors = PolyUfdUtil.<BigRational> factors(c);
        //System.out.println("factors = " + factors);

        boolean t = PolyUfdUtil.<BigRational> isFactorization(c, factors);
        //System.out.println("t = " + t);
        assertTrue("c == prod(factors): " + c + ", " + factors, t);
    }


    /**
     * Test symbolic row echelon form and LU decomposition. Using an example
     * from
     * <a href="https://github.com/kredel/java-algebra-system/issues/21">Issue
     * #21</a>.
     */
    public void testLinAlg() {
        BigRational cfac = new BigRational(11);
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, new String[] { "a" });
        //System.out.println("pfac = " + pfac.toScript());
        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(pfac);
        //System.out.println("qfac = " + qfac.toScript());
        Quotient<BigRational> a = new Quotient<BigRational>(qfac, pfac.univariate(0));
        //System.out.println("a: " + a.toScript());
        int n = 3;
        GenMatrixRing<Quotient<BigRational>> mfac = new GenMatrixRing<Quotient<BigRational>>(qfac, n, n);
        //System.out.println("mfac = " + mfac.toScript());
        GenMatrixRing<Quotient<BigRational>> tfac = mfac.transpose();
        @SuppressWarnings("unchecked")
        Quotient<BigRational>[][] mm = new Quotient[n][n];
        // ( {{1, a, 2}, {0, 1, 1}, {-1, 1, 1}} )
        mm[0][0] = qfac.fromInteger(1);
        mm[0][1] = a;
        mm[0][2] = qfac.fromInteger(2);

        mm[1][0] = qfac.getZERO();
        mm[1][1] = qfac.fromInteger(1);
        mm[1][2] = qfac.fromInteger(1);

        mm[2][0] = qfac.fromInteger(-1);
        mm[2][1] = qfac.fromInteger(1);
        mm[2][2] = qfac.fromInteger(1);

        GenMatrix<Quotient<BigRational>> A = new GenMatrix<Quotient<BigRational>>(mfac, mm);
        //System.out.println("A:   " + A);

        LinAlg<Quotient<BigRational>> lu = new LinAlg<Quotient<BigRational>>();

        // test rowEchelonForm and rowEchelonFormSparse
        GenMatrix<Quotient<BigRational>> B = lu.rowEchelonForm(A);
        //System.out.println("B:   " + B);
        long r = lu.rankRE(B);
        GenMatrix<Quotient<BigRational>> D = lu.rowEchelonFormSparse(B);
        //System.out.println("D:   " + D);
        assertTrue("rank1 == rank2: ", lu.rankRE(D) == r);

        // test LU decomposition
        A = new GenMatrix<Quotient<BigRational>>(mfac, mm);
        List<Integer> P = lu.decompositionLU(A);
        //System.out.println("P  :   " + P);
        //System.out.println("A  :   " + A.toScript());
        //System.out.println("U  :   " + A.getUpper().toScript());

        // test LU inverse
        GenMatrix<Quotient<BigRational>> I = lu.inverseLU(A, P);
        //System.out.println("I  :   " + I.toScript());

        GenMatrix<Quotient<BigRational>> C = new GenMatrix<Quotient<BigRational>>(mfac, mm);
        GenMatrix<Quotient<BigRational>> CI = C.multiply(I);
        //System.out.println("C*I:   " + CI.toScript());
        assertTrue("C*I == 1: ", CI.isONE());

        GenMatrix<Quotient<BigRational>> C2 = C.sum(C);
        GenMatrix<Quotient<BigRational>> CA = A.divide(C2);
        GenMatrix<Quotient<BigRational>> AC = A.divideLeft(C2);
        //System.out.println("C/A :    " + CA);
        //System.out.println("A\\C :   " + AC);
        assertFalse("C/A != A\\C: ", CA.equals(AC));
    }

}
