/*
 * $Id$
 */

package edu.jas.fd;


import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
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
 * FDUtilQuat tests with JUnit.
 * @author Heinz Kredel
 */

public class FDUtilQuatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>FDUtilQuatTest</CODE> object.
     * @param name String.
     */
    public FDUtilQuatTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FDUtilQuatTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    BigQuaternionRing cfi, cfr;


    GenSolvablePolynomialRing<BigQuaternion> dfac;


    GenSolvablePolynomialRing<BigQuaternion> rdfac;


    GenSolvablePolynomialRing<GenPolynomial<BigQuaternion>> rfac;


    GenSolvablePolynomialRing<GenPolynomial<BigQuaternion>> rrfac;


    RecSolvablePolynomialRing<BigQuaternion> rsfac;


    GenSolvablePolynomial<BigQuaternion> a, b, c, d, e, f;


    GenSolvablePolynomial<GenPolynomial<BigQuaternion>> ar, br, cr, dr, er, fr;


    GenSolvablePolynomial<GenPolynomial<BigQuaternion>> arr, brr, abrr, barr, crr, drr, err, frr, x1;


    RecSolvablePolynomial<BigQuaternion> as, bs, cs, ds, es, fs;


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
        rl = vars.length;
        cfi = new BigQuaternionRing(true);
        cfr = new BigQuaternionRing();
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfi, to, vars);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        dfac.addRelations(wl);
        rfac = dfac.recursive(1);
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, to, vars);
        RelationGenerator<BigQuaternion> wlr = new WeylRelationsIterated<BigQuaternion>();
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
            a = dfac.random(kl, ll + 1, el, q).monic();
        } while (a.isZERO());
        //a = dfac.parse(" 3 x^5 + 44 ");
        //System.out.println("a = " + a);

        do {
            b = dfac.random(kl, ll + 1, el, q).monic();
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
        e = FDUtil.<BigQuaternion> leftBasePseudoQuotient(c, a);
        //System.out.println("e = " + e);
        assertEquals("b == b*a/a: ", b, e);

        f = FDUtil.<BigQuaternion> rightBasePseudoQuotient(c, b);
        //System.out.println("f = " + f);
        assertEquals("a == b*a/b: ", a, f);

        e = FDUtil.<BigQuaternion> rightBasePseudoQuotient(d, a);
        //System.out.println("e = " + e);
        assertEquals("b == a*b/a: ", b, e);

        f = FDUtil.<BigQuaternion> leftBasePseudoQuotient(d, b);
        //System.out.println("f = " + f);
        assertEquals("a == a*b/b: ", a, f);
    }


    /**
     * Test base pseudo quotient remainder.
     */
    public void testBasePseudoQuotientRemainderExact() {
        //System.out.println("dfac  = " + dfac.toScript());
        do {
            a = dfac.random(kl, ll + 1, el, q).monic();
        } while (a.isZERO());
        //a = dfac.parse(" 3 x^5 + 44 ");
        //System.out.println("a = " + a);

        do {
            b = dfac.random(kl, ll + 1, el, q).monic();
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
        e = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(d, a);
        //System.out.println("e = " + e);
        assertTrue("ab = a*x + 0: ", e.isZERO());

        f = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(d, b);
        //System.out.println("f = " + f);
        assertTrue("ab = y*b + 0: ", f.isZERO());

        e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(c, a);
        //System.out.println("e = " + e);
        assertTrue("ba = x*a + 0: ", e.isZERO());

        f = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(c, b);
        //System.out.println("f = " + f);
        assertTrue("ba = b*y + 0: ", f.isZERO());
    }


    /**
     * Test base pseudo division and remainder.
     */
    public void testBasePseudoDivisionRemainder() {
        String[] names = new String[] { "x" };
        GenSolvablePolynomialRing<BigQuaternion> dfac;
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfi, to, names);
        GenSolvablePolynomialRing<BigQuaternion> rdfac;
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, dfac);
        //System.out.println("dfac  = " + dfac.toScript());

        do {
            a = dfac.random(kl, ll * 1, el + 0, q);
        } while (a.isZERO());
        //a = dfac.parse("3 x**5 + 44");
        //System.out.println("a = " + a);

        do {
            b = dfac.random(kl, ll * 2, el + 1, q);
        } while (b.isZERO());
        //a = a.sum(b);
        //b = dfac.parse("2 x**2 + 40");
        //System.out.println("b = " + b);
        //System.out.println("a = " + a);

        GenSolvablePolynomial<BigQuaternion>[] QR = FDUtil.<BigQuaternion> leftBasePseudoQuotientRemainder(a, b);
        c = (GenSolvablePolynomial<BigQuaternion>) QR[0];
        d = (GenSolvablePolynomial<BigQuaternion>) QR[1];
        //System.out.println("c   = " + c);
        //System.out.println("d   = " + d);

        boolean t = FDUtil.<BigQuaternion> isLeftBasePseudoQuotientRemainder(a, b, c, d);
        assertTrue("lc^n c = e b + f: " + f, t);

        QR = FDUtil.<BigQuaternion> rightBasePseudoQuotientRemainder(a, b);
        e = QR[0];
        f = QR[1];
        //System.out.println("e   = " + e);
        //System.out.println("f   = " + f);

        t = FDUtil.<BigQuaternion> isRightBasePseudoQuotientRemainder(a, b, e, f);
        assertTrue("ore(lc^n) c = e b + f: " + f, t);
    }


    /**
     * Test recursive pseudo division.
     * @see edu.jas.ufd.PolyUfdUtilTest#testRecursivePseudoDivisionSparse
     */
    public void testRecursivePseudoDivision() {
        //String[] cnames = new String[] { "x" };
        //String[] mnames = new String[] { "t" };
        String[] names = new String[] { "t", "x", "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, to, names);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        rdfac.addRelations(wl);
        rrfac = (RecSolvablePolynomialRing<BigQuaternion>) rdfac.recursive(1);
        System.out.println("\nrdfac  = " + rdfac.toScript());
        System.out.println("rrfac  = " + rrfac.toScript());

        q = 0.27f;
        kl = 1;
        ll = 3;

        arr = rrfac.random(kl, ll, el, q);
        //arr = rrfac.parse(" ( t + x + y ) z^2 + ( 2 x - 8 ) y^2 - ( 13 t^4 - 13 t^3 + t^2 + 2 t - 13 ) ");
        //arr = rrfac.parse(" ( 131/5 y - 85/84 ) ");
        brr = rrfac.random(kl, ll, el, q);
        //brr = rrfac.parse("( 27/28 x^2 - 31/19 t^2 + 3/5 )");
        if (brr.isZERO()) {
            brr = rrfac.parse(" ( x - 2 ) z - ( t - y^2 + y ) ");
        }
        System.out.println("arr  = " + arr);
        System.out.println("brr  = " + brr);

        drr = FDUtil.<BigQuaternion> recursivePseudoQuotient(arr, brr);
        crr = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(arr, brr);
        System.out.println("qrr  = " + drr);
        System.out.println("crr  = " + crr);

        GenSolvablePolynomial<GenPolynomial<BigQuaternion>>[] QR;
        QR = FDUtil.<BigQuaternion> recursivePseudoQuotientRemainder(arr, brr);
        assertEquals("drr == QR[0]: ", drr, QR[0]);
        assertEquals("crr == QR[1]: ", crr, QR[1]);

        boolean t = FDUtil.<BigQuaternion> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("ore(lc^n) a == q b + r: " + t);
        assertTrue("ore(lc^n) a = q b + r: " + crr, t); // ??
    }


    /**
     * Test recursive division coefficient polynomial.
     */
    public void xtestLeftAndRightRecursiveDivision() { // todo
        //String[] names = new String[] { "t", "x", "y", "z" };
        String[] names = new String[] { "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, to, names);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        rdfac.addRelations(wl);
        rrfac = rdfac.recursive(1);
        //System.out.println("\nrdfac  = " + rdfac.toScript());
        //System.out.println("rrfac  = " + rrfac.toScript());
        GenSolvablePolynomial<GenPolynomial<BigQuaternion>>[] QR;
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
        drr = FDUtil.<BigQuaternion> recursivePseudoQuotient(abrr, brr);
        crr = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(abrr, brr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);

        QR = FDUtil.<BigQuaternion> recursivePseudoQuotientRemainder(abrr, brr);
        assertEquals("drr == QR[0]: ", drr, QR[0]);
        assertEquals("crr == QR[1]: ", crr, QR[1]);

        t = FDUtil.<BigQuaternion> isRecursivePseudoQuotientRemainder(abrr, brr, drr, crr);
        //System.out.println("ore(lc^n) a == q b + r: " + t);
        assertTrue("ore(lc^n) a = q b + r: " + crr, t); // ??

        barr = brr.multiply(arr);
        //System.out.println("barr  = " + barr);

        // exact right division
        QR = FDUtil.<BigQuaternion> recursiveRightPseudoQuotientRemainder(barr, brr);
        drr = QR[0];
        crr = QR[1];
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        //assertEquals("drr == QR[0]: ", drr, QR[0]);
        //assertEquals("crr == QR[1]: ", crr, QR[1]);

        t = FDUtil.<BigQuaternion> isRecursiveRightPseudoQuotientRemainder(barr, brr, drr, crr);
        //System.out.println("FDQR: a ore(lc^n) == q b + r: " + t);
        assertTrue("a ore(lc^n) = q b + r: " + crr, t); // ??

        // left division
        QR = FDUtil.<BigQuaternion> recursivePseudoQuotientRemainder(arr, brr);
        drr = QR[0];
        crr = QR[1];
        t = FDUtil.<BigQuaternion> isRecursivePseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        assertTrue("ore(lc^n) a = b q + r: " + crr, t); // ??

        // right division
        QR = FDUtil.<BigQuaternion> recursiveRightPseudoQuotientRemainder(arr, brr);
        drr = QR[0];
        crr = QR[1];
        t = FDUtil.<BigQuaternion> isRecursiveRightPseudoQuotientRemainder(arr, brr, drr, crr);
        //System.out.println("drr  = " + drr);
        //System.out.println("crr  = " + crr);
        assertTrue("ore(lc^n) a = q p + r: " + crr, t); // ??
    }


    /**
     * Test recursive right coefficient polynomial.
     */
    public void xtestRightRecursivePolynomial() { // todo
        //String[] names = new String[] { "t", "x", "y", "z" };
        String[] names = new String[] { "y", "z" };
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, to, names);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        rdfac.addRelations(wl);
        rrfac = rdfac.recursive(1);
        //System.out.println("\nrdfac  = " + rdfac.toScript());
        //System.out.println("rrfac  = " + rrfac.toScript());
        //GenSolvablePolynomialRing<BigQuaternion> cfac = (GenSolvablePolynomialRing) rrfac.coFac;
        //System.out.println("cfac  = " + cfac.toScript());

        // q = q;
        kl = 3;
        ll = 4;
        el = 5;

        arr = rrfac.random(kl, ll, el, q);
        //arr = rrfac.parse(" z { y } ");
        System.out.println("arr  = " + arr);

        brr = arr.rightRecursivePolynomial();
        System.out.println("brr  = " + brr);

        boolean t = arr.isRightRecursivePolynomial(brr);
        assertTrue("arr == eval(brr): ", t);

        GenSolvablePolynomial<BigQuaternion> c = (GenSolvablePolynomial<BigQuaternion>) rrfac
                        .random(kl, ll, el, q).leadingBaseCoefficient();
        //c = cfac.parse("y**2");
        System.out.println("c  = " + c);

        drr = arr.multiply(c); // arr * c = drr
        System.out.println("drr  = " + drr);

        err = FDUtil.<BigQuaternion> recursiveLeftDivide(drr, c); // err * c = drr
        System.out.println("err  = " + err);
        assertEquals("arr == err: ", arr, err);

        //System.out.println("\nFDQR: arr  = " + arr);
        drr = arr.multiplyLeft(c); // c * arr = drr
        System.out.println("drr  = " + drr);

        err = FDUtil.<BigQuaternion> recursiveRightDivide(drr, c); // c * err = drr
        System.out.println("err  = " + err);
        assertEquals("arr == err: ", arr, err);
    }


    /**
     * Test exact division of recursive polynomials.
     */
    public void testRecursiveDivide() {
        rdfac = new GenSolvablePolynomialRing<BigQuaternion>(cfr, dfac);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        rdfac.addRelations(wl);
        //System.out.println("rdfac  = " + rdfac.toScript());
        rsfac = (RecSolvablePolynomialRing<BigQuaternion>) rdfac.recursive(1);
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

        //es = (RecSolvablePolynomial<BigQuaternion>) ds.subtract(cs);
        assertTrue("as*bs != bs*as", !cs.equals(ds) || cs.leadingExpVector().equals(ds.leadingExpVector()));

        // divide
        es = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursivePseudoQuotient(cs, as);
        //System.out.println("es = " + es);
        final int max = 4;
        int i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigQuaternion>) bs.multiplyLeft(as.leadingBaseCoefficient().power(i));
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

        fs = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursivePseudoQuotient(ds, bs);
        //System.out.println("fs = " + fs);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigQuaternion>) as.multiplyLeft(bs.leadingBaseCoefficient().power(i));
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

        GenSolvablePolynomial<BigQuaternion> bc = (GenSolvablePolynomial<BigQuaternion>) bs.leadingBaseCoefficient();
        ds = (RecSolvablePolynomial<BigQuaternion>) as.multiply(bc);
        fs = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursiveDivide(ds, bc);
        //System.out.println("bc = " + bc);
        //System.out.println("ds = " + ds);
        //System.out.println("fs = " + fs);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigQuaternion>) as.multiply(bc.power(i));
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

        bc = (GenSolvablePolynomial<BigQuaternion>) bs.leadingBaseCoefficient();
        ds = (RecSolvablePolynomial<BigQuaternion>) as.multiply(bc);
        fs = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursiveLeftDivide(ds, bc);
        //System.out.println("bc = " + bc);
        //System.out.println("ds = " + ds);
        //System.out.println("fs = " + fs);
        i = 0;
        do {
            x1 = (RecSolvablePolynomial<BigQuaternion>) as.multiply(bc.power(i));
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

        // todo
        // fs = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursiveRightPseudoQuotient(cs, bs);
        // //System.out.println("fs = " + fs);
        // i = 0;
        // do {
        //     x1 = (RecSolvablePolynomial<BigQuaternion>) as.multiply(bs.leadingBaseCoefficient().power(i));
        //     //System.out.println("a*lc(b)^"+i+" = " + x1);
        //     if (fs.equals(x1)) {
        //         assertEquals("a == b*a/b: ", fs, x1);
        //         break;
        //     }
        //     if (fs.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
        //         System.out.println("fail: a == b*a/b: lc(f)==lc(x1)");
        //         if (fs.abs().equals(as.abs())) {
        //             System.out.println("success via pseudo: a == b*a/b: ");
        //         }
        //         break;
        //     }
        // } while (i++ < max);

        // es = (RecSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> recursiveRightPseudoQuotient(ds, as);
        // //System.out.println("es = " + es);
        // i = 0;
        // do {
        //     x1 = (RecSolvablePolynomial<BigQuaternion>) bs.multiply(as.leadingBaseCoefficient().power(i));
        //     //System.out.println("b*lc(a)^"+i+" = " + x1);
        //     if (es.equals(x1)) {
        //         assertEquals("b == a*b/a: ", es, x1);
        //         break;
        //     }
        //     if (es.leadingBaseCoefficient().equals(x1.leadingBaseCoefficient())) {
        //         System.out.println("fail: b == a*b/a: lc(e) == lc(x1)");
        //         if (es.abs().equals(bs.abs())) {
        //             //System.out.println("success via pseudo: b == a*b/a: ");
        //         }
        //         break;
        //     }
        // } while (i++ < max);
    }

}
