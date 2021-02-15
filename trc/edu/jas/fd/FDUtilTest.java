/*
 * $Id$
 */

package edu.jas.fd;


import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelationsIterated;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * FDUtil tests with JUnit.
 * @author Heinz Kredel
 */

public class FDUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>FDUtilTest</CODE> object.
     * @param name String.
     */
    public FDUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FDUtilTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenSolvablePolynomialRing<BigInteger> dfac;


    GenSolvablePolynomialRing<BigRational> rdfac;


    GenSolvablePolynomialRing<GenPolynomial<BigInteger>> rfac;


    GenSolvablePolynomialRing<GenPolynomial<BigRational>> rrfac;


    RecSolvablePolynomialRing<BigRational> rsfac;


    GenSolvablePolynomial<BigInteger> a, b, c, d, e, f;


    GenSolvablePolynomial<GenPolynomial<BigInteger>> ar, br, cr, dr, er, fr;


    GenSolvablePolynomial<GenPolynomial<BigRational>> arr, brr, abrr, barr, crr, drr, err, frr, x1;


    RecSolvablePolynomial<BigRational> as, bs, cs, ds, es, fs;


    int rl = 4;


    int kl = 2;


    int ll = 4;


    int el = 3;


    float q = 0.35f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        String[] vars = new String[] { "a", "b", "c", "d" };
        dfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1), rl, to, vars);
        RelationGenerator<BigInteger> wl = new WeylRelationsIterated<BigInteger>();
        dfac.addRelations(wl);
        rfac = dfac.recursive(1);
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        RelationGenerator<BigRational> wlr = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wlr);
        rrfac = rdfac.recursive(1);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        dfac = null;
        rfac = null;
    }


    /**
     * Test base pseudo division.
     */
    public void testBasePseudoDivisionExact() {
        //System.out.println("dfac  = " + dfac.toScript());
        do {
            a = dfac.random(kl, ll + 1, el, q);
        } while (a.isZERO());
        //a = dfac.parse(" 3 x^5 + 44 ");
        //System.out.println("a = " + a);

        do {
            b = dfac.random(kl, ll + 1, el, q);
        } while (b.isZERO());
        //a = a.sum(b);
        //b = dfac.parse(" 2 x^2 + 40 ");
        //System.out.println("b = " + b);

        // non commutative
        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("c != 0: ", !c.isZERO());
        assertTrue("d != 0: ", !d.isZERO());

        assertTrue("a*b != b*a", !c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        // divide 
        e = FDUtil.<BigInteger> leftBasePseudoQuotient(c, a);
        //System.out.println("e = " + e);
        assertEquals("b == b*a/a: ", b, e);

        f = FDUtil.<BigInteger> rightBasePseudoQuotient(c, b);
        //System.out.println("f = " + f);
        assertEquals("a == b*a/b: ", a, f);

        e = FDUtil.<BigInteger> rightBasePseudoQuotient(d, a);
        //System.out.println("e = " + e);
        assertEquals("b == a*b/a: ", b, e);

        f = FDUtil.<BigInteger> leftBasePseudoQuotient(d, b);
        //System.out.println("f = " + f);
        assertEquals("a == a*b/b: ", a, f);
    }


    /**
     * Test base pseudo division.
     */
    @SuppressWarnings({ "cast" })
    public void testBasePseudoDivision() {
        String[] names = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigInteger>(new BigInteger(1), to, names);
        GenSolvablePolynomialRing<BigRational> rdfac;
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), dfac);
        //System.out.println("dfac  = " + dfac.toScript());

        do {
            a = dfac.random(kl, ll * 2, el + 1, q);
        } while (a.isZERO());
        //a = dfac.parse(" 3 x^5 + 44 ");
        //System.out.println("a = " + a);

        do {
            b = dfac.random(kl, ll * 2, el + 1, q);
        } while (b.isZERO());
        //a = a.sum(b);
        //b = dfac.parse(" 2 x^2 + 40 ");
        //System.out.println("b = " + b);

        GenPolynomial<BigInteger>[] QR = PolyUtil.<BigInteger> basePseudoQuotientRemainder(a, b);
        c = (GenSolvablePolynomial<BigInteger>) QR[0];
        d = (GenSolvablePolynomial<BigInteger>) QR[1];
        //System.out.println("c   = " + c);
        //System.out.println("d   = " + d);

        boolean t = PolyUtil.<BigInteger> isBasePseudoQuotientRemainder(a, b, c, d);
        assertTrue("lc^n c = e b + f: " + f, t);

        GenSolvablePolynomial<BigInteger>[] QRs = FDUtil.<BigInteger> leftBasePseudoQuotientRemainder(a, b);
        e = QRs[0];
        f = QRs[1];
        //System.out.println("e   = " + e);
        //System.out.println("f   = " + f);

        t = PolyUtil.<BigInteger> isBasePseudoQuotientRemainder(a, b, e, f);
        assertTrue("ore(lc^n) c = e b + f: " + f, t);

        // compare with field coefficients:
        GenSolvablePolynomial<BigRational> ap, bp, cp, dp, ep, fp, qp, rp, rhs;
        ap = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, a);
        bp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, b);
        cp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, c);
        dp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, d);
        ep = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, e);
        fp = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> fromIntegerCoefficients(rdfac, f);
        //System.out.println("ap  = " + ap);
        //System.out.println("bp  = " + bp);
        //System.out.println("cp  = " + cp);
        //System.out.println("dp  = " + dp);
        //System.out.println("ep  = " + ep);
        //System.out.println("fp  = " + fp);

        qp = (GenSolvablePolynomial<BigRational>) ap.divide(bp);
        rp = (GenSolvablePolynomial<BigRational>) ap.remainder(bp);
        //System.out.println("qp  = " + qp);
        //System.out.println("rp  = " + rp);
        GenSolvablePolynomial<BigRational>[] QRr = ap.quotientRemainder(bp);
        assertEquals("qp == QRr[0]: ", qp, QRr[0]);
        assertEquals("rp == QRr[1]: ", rp, QRr[1]);

        rhs = (GenSolvablePolynomial<BigRational>) qp.multiply(bp).sum(rp);
        //System.out.println("qp bp + rp  = " + rhs);
        assertEquals("ap == qp bp + rp: ", ap, rhs);

        assertEquals("cp == qp: ", qp.monic(), cp.monic());
        assertEquals("dp == rp: ", rp.monic(), dp.monic());
        //System.out.println("dp = qp: " + qp.monic().equals(dp.monic()) );
        assertEquals("ep == qp: ", ep.monic(), cp.monic());
        assertEquals("fp == rp: ", fp.monic(), dp.monic());
    }


    /**
     * Test recursive pseudo division.
     * @see edu.jas.ufd.PolyUfdUtilTest#testRecursivePseudoDivisionSparse
     */
    public void testRecursivePseudoDivision() {
        //String[] cnames = new String[] { "x" };
        //String[] mnames = new String[] { "t" };
        String[] names = new String[] { "t", "x", "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, names);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wl);
        rsfac = (RecSolvablePolynomialRing<BigRational>) rdfac.recursive(1);

        // q = q;
        kl = 1;
        ll = 3;

        arr = rrfac.random(kl, ll, el, q);
        //arr = rrfac.parse(" ( t + x + y ) z^2 + ( 2 x - 8 ) y^2 - ( 13 t^4 - 13 t^3 + t^2 + 2 t - 13 ) ");
        brr = rrfac.random(kl, ll, el, q);
        if (brr.isZERO()) {
            brr = rrfac.parse(" ( x - 2 ) z - ( t - y^2 + y ) ");
        }
        //System.out.println("FDQR: arr  = " + arr);
        //System.out.println("FDQR: brr  = " + brr);

        drr = FDUtil.<BigRational> recursivePseudoQuotient(arr, brr);
        crr = FDUtil.<BigRational> recursiveSparsePseudoRemainder(arr, brr);
        //System.out.println("FDQR: qr  = " + drr);
        //System.out.println("FDQR: rr  = " + crr);

        GenSolvablePolynomial<GenPolynomial<BigRational>>[] QR;
        QR = FDUtil.<BigRational> recursivePseudoQuotientRemainder(arr, brr);
        assertEquals("drr == QR[0]: ", drr, QR[0]);
        assertEquals("crr == QR[1]: ", crr, QR[1]);

        boolean t = FDUtil.<BigRational> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("FDQR: ore(lc^n) a == q b + r: " + t);
        assertTrue("ore(lc^n) a = q b + r: " + crr, t); // ?? 
    }


    /**
     * Test recursive division coefficient polynomial.
     */
    public void testLeftAndRightRecursiveDivision() {
        //String[] names = new String[] { "t", "x", "y", "z" };
        String[] names = new String[] { "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, names);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wl);
        rrfac = rdfac.recursive(1);
        //System.out.println("\nrdfac  = " + rdfac.toScript());
        //System.out.println("rrfac  = " + rrfac.toScript());
        GenSolvablePolynomial<GenPolynomial<BigRational>>[] QR;
        boolean t;

        // q = q;
        kl = 2;
        ll = 4;
        el = 5;

        arr = rrfac.random(kl, ll, el + 1, q);
        //arr = rrfac.parse("z^5 - ( 1260/551 y^2 - 143/35 y - 33/100  ) z - ( 1/3 y^2 + 419/299 y - 19/56  )");
        // b * q + r:
        //arr = rrfac.parse("z^5 + z^2 - 1");
        //System.out.println("arr  = " + arr);

        brr = rrfac.random(kl, ll, el, q);
        //brr = rrfac.parse("z^3 - ( 377/140 y^2 + 211/232 y + 1213967/85560  )");
        //brr = rrfac.parse("( y ) z^3 - ( 1 ) z + ( 2 )");
        //System.out.println("brr  = " + brr);

        abrr = arr.multiply(brr);
        //System.out.println("abrr  = " + abrr);

        // exact left division
        drr = FDUtil.<BigRational> recursivePseudoQuotient(abrr, brr);
        crr = FDUtil.<BigRational> recursiveSparsePseudoRemainder(abrr, brr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);

        QR = FDUtil.<BigRational> recursivePseudoQuotientRemainder(abrr, brr);
        assertEquals("drr == QR[0]: ", drr, QR[0]);
        assertEquals("crr == QR[1]: ", crr, QR[1]);

        //t = PolyUtil.<BigRational> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        t = FDUtil.<BigRational> isRecursivePseudoQuotientRemainder(abrr, brr, drr, crr);
        //System.out.println("FDQR: ore(lc^n) a == q b + r: " + t);
        assertTrue("ore(lc^n) a = q b + r: " + crr, t); // ?? 

        barr = brr.multiply(arr);
        //System.out.println("barr  = " + barr);

        // exact right division
        QR = FDUtil.<BigRational> recursiveRightPseudoQuotientRemainder(barr, brr);
        drr = QR[0];
        crr = QR[1];
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        //assertEquals("drr == QR[0]: ", drr, QR[0]);
        //assertEquals("crr == QR[1]: ", crr, QR[1]);

        t = FDUtil.<BigRational> isRecursiveRightPseudoQuotientRemainder(barr, brr, drr, crr);
        //System.out.println("FDQR: a ore(lc^n) == q b + r: " + t);
        assertTrue("a ore(lc^n) = q b + r: " + crr, t); // ?? 

        // left division
        QR = FDUtil.<BigRational> recursivePseudoQuotientRemainder(arr, brr);
        drr = QR[0];
        crr = QR[1];
        t = FDUtil.<BigRational> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        assertTrue("ore(lc^n) a = b q + r: " + crr, t); // ?? 

        // right division
        QR = FDUtil.<BigRational> recursiveRightPseudoQuotientRemainder(arr, brr);
        drr = QR[0];
        crr = QR[1];
        t = FDUtil.<BigRational> isRecursiveRightPseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        assertTrue("ore(lc^n) a = q p + r: " + crr, t); // ?? 
    }


    /**
     * Test recursive right coefficient polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testRightRecursivePolynomial() {
        //String[] names = new String[] { "t", "x", "y", "z" };
        String[] names = new String[] { "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, names);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wl);
        rrfac = rdfac.recursive(1);
        //System.out.println("\nrdfac  = " + rdfac.toScript());
        //System.out.println("rrfac  = " + rrfac.toScript());
        //GenSolvablePolynomialRing<BigRational> cfac = (GenSolvablePolynomialRing) rrfac.coFac;
        //System.out.println("cfac  = " + cfac.toScript());

        // q = q;
        kl = 3;
        ll = 4;
        el = 5;

        arr = rrfac.random(kl, ll, el, q);
        //arr = rrfac.parse(" z { y } ");
        //System.out.println("FDQR: arr  = " + arr);

        brr = arr.rightRecursivePolynomial();
        //System.out.println("FDQR: brr  = " + brr);

        boolean t = arr.isRightRecursivePolynomial(brr);
        assertTrue("arr == eval(brr): ", t);

        GenSolvablePolynomial<BigRational> c = (GenSolvablePolynomial<BigRational>) rrfac
                        .random(kl, ll, el, q).leadingBaseCoefficient();
        //c = cfac.parse("y**2");
        //System.out.println("FDQR: c  = " + c);

        drr = arr.multiply(c); // arr * c = drr
        //System.out.println("FDQR: drr  = " + drr);

        err = FDUtil.<BigRational> recursiveLeftDivide(drr, c); // err * c = drr
        //System.out.println("FDQR: err  = " + err);
        assertEquals("arr == err: ", arr, err);


        //System.out.println("\nFDQR: arr  = " + arr);
        drr = arr.multiplyLeft(c); // c * arr = drr
        //System.out.println("FDQR: drr  = " + drr);

        err = FDUtil.<BigRational> recursiveRightDivide(drr, c); // c * err = drr
        //System.out.println("FDQR: err  = " + err);
        assertEquals("arr == err: ", arr, err);
    }


    /*
     * Test exact division of recursive polynomials.
     */
    @SuppressWarnings({ "cast" })
    public void testRecursiveDivide() {
        rdfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), dfac);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        rdfac.addRelations(wl);
        //System.out.println("rdfac  = " + rdfac.toScript());
        rsfac = (RecSolvablePolynomialRing<BigRational>) rdfac.recursive(1);
        //System.out.println("rsfac  = " + rsfac.toScript());

        assertFalse("isCommutative()", rsfac.isCommutative());
        assertTrue("isAssociative()", rsfac.isAssociative());

        do {
            as = rsfac.random(kl, ll, el, q);
        } while (as.isZERO());
        //System.out.println("as = " + as);

        do {
            bs = rsfac.random(kl, ll, el, q);
        } while (bs.isZERO());
        //System.out.println("bs = " + bs);

        // non commutative
        cs = bs.multiply(as);
        ds = as.multiply(bs);
        //System.out.println("cs = " + cs);
        //System.out.println("ds = " + ds);
        assertTrue("cs != 0: ", !cs.isZERO());
        assertTrue("ds != 0: ", !ds.isZERO());

        //es = (RecSolvablePolynomial<BigRational>) ds.subtract(cs);
        assertTrue("as*bs != bs*as", !cs.equals(ds) || cs.leadingExpVector().equals(ds.leadingExpVector()));

        // divide 
        es = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursivePseudoQuotient(cs, as);
        //System.out.println("es = " + es);
        final int max = 4;
        int i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigRational>) bs.multiplyLeft(as.leadingBaseCoefficient().power(i));
            //System.out.println("lc(a)^"+i+"*b = " + x1);
            if (es.equals(x1)) {
                assertEquals("b == b*a/a: ", es, x1);
                break;
            }
            if (es.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
                // assertEquals("b == b*a/a: ", e, x1);
                System.out.println("fail: b == b*a/a: lc(e)==lc(x1)");
                if (es.abs().equals(bs.abs())) {
                    System.out.println("success via pseudo: b == b*a/a: ");
                }
                break;
            }
        } while (i++ < max);

        fs = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveRightPseudoQuotient(cs, bs);
        //System.out.println("fs = " + fs);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigRational>) as.multiply(bs.leadingBaseCoefficient().power(i));
            //System.out.println("a*lc(b)^"+i+" = " + x1);
            if (fs.equals(x1)) {
                assertEquals("a == b*a/b: ", fs, x1);
                break;
            }
            if (fs.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
                System.out.println("fail: a == b*a/b: lc(f)==lc(x1)");
                if (fs.abs().equals(as.abs())) {
                    System.out.println("success via pseudo: a == b*a/b: ");
                }
                break;
            }
        } while (i++ < max);

        es = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveRightPseudoQuotient(ds, as);
        //System.out.println("es = " + es);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigRational>) bs.multiply(as.leadingBaseCoefficient().power(i));
            //System.out.println("b*lc(a)^"+i+" = " + x1);
            if (es.equals(x1)) {
                assertEquals("b == a*b/a: ", es, x1);
                break;
            }
            if (es.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
                System.out.println("fail: b == a*b/a: lc(e) == lc(x1)");
                if (es.abs().equals(bs.abs())) {
                    //System.out.println("success via pseudo: b == a*b/a: ");
                }
                break;
            }
        } while (i++ < max);

        fs = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursivePseudoQuotient(ds, bs);
        //System.out.println("fs = " + fs);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigRational>) as.multiplyLeft(bs.leadingBaseCoefficient().power(i));
            //System.out.println("lc(b)^"+i+"*a = " + x1);
            if (fs.equals(x1)) {
                assertEquals("a == a*b/b: ", fs, x1);
                break;
            }
            if (fs.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
                System.out.println("fail: a == a*b/b: lc(f)==lc(x1)");
                if (fs.abs().equals(as.abs())) {
                    System.out.println("success via pseudo: a == a*b/b: ");
                }
                break;
            }
        } while (i++ < max);
    }

}
