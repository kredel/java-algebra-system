/*
 * $Id$
 */

package edu.jas.fd;


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
import edu.jas.poly.TermOrderByName;
import edu.jas.poly.WeylRelationsIterated;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GCD Primitive PRS algorithm tests with JUnit. <b>Note:</b> eventually not in
 * sync with implementation.
 * @author Heinz Kredel
 */

public class GCDPrimitiveQuatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GCDPrimitiveQuatTest</CODE> object.
     * @param name String.
     */
    public GCDPrimitiveQuatTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDPrimitiveQuatTest.class);
        return suite;
    }


    GreatestCommonDivisorAbstract<BigQuaternion> fd;


    TermOrder to = TermOrderByName.INVLEX;


    GenSolvablePolynomialRing<BigQuaternion> qfac;


    //GenSolvablePolynomialRing<GenPolynomial<BigQuaternion>> rfac;
    RecSolvablePolynomialRing<BigQuaternion> rfac;


    GenSolvablePolynomial<BigQuaternion> a, b, a0, b0, c, d, e, f;


    GenSolvablePolynomial<GenPolynomial<BigQuaternion>> ar, br, cr, dr, er, ar0, br0;


    int rl = 4;


    int kl = 2;


    int ll = 2;


    int el = 3;


    float q = 0.25f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        String[] vars = new String[] { "a", "b", "c", "d" };
        BigQuaternionRing cf = new BigQuaternionRing();
        fd = new GreatestCommonDivisorPrimitive<BigQuaternion>(cf);
        System.out.println("fd = " + fd);
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, vars);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        //qfac.addRelations(wl);
        rfac = (RecSolvablePolynomialRing<BigQuaternion>) qfac.recursive(1);
        //System.out.println("qfac = " + qfac);
        //System.out.println("rfac = " + rfac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        fd = null;
        qfac = null;
        rfac = null;
    }


    /**
     * Test quaternion base gcd simple.
     */
    public void xtestQuatBaseGcdPrimitive() {
        String[] uvars = new String[] { "x" };
        BigQuaternionRing cf = new BigQuaternionRing();
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, uvars);
        //System.out.println("qfac = " + qfac.toScript());
        for (int i = 0; i < 3; i++) {
            //System.out.println();
            a = qfac.random(kl + (i), ll + 2 * i, el + 2, q);
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(qfac.univariate(0).power(2));
            b = qfac.random(kl + (i + 1), ll + i, el + 2, q);
            b = (GenSolvablePolynomial<BigQuaternion>) b.sum(qfac.univariate(0));
            c = qfac.random(kl + (i + 1), ll + 1, el + 1, q);
            c = c.multiply(qfac.univariate(0));
            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            a = a.monic();
            b = b.monic();
            c = c.monic();
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            //a = c.multiply(a);
            //b = c.multiply(b);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            d = fd.leftBaseGcd(a, b);
            System.out.println("d  = " + d);
            System.out.println("c  = " + c);

            e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(a, d);
            System.out.println("e = " + e);
            assertTrue("gcd(ca,cb) | ca " + e, e.isZERO());

            e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(b, d);
            System.out.println("e = " + e);
            assertTrue("gcd(ca,cb) | cb " + e, e.isZERO());

            // todo
            //e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(d, c);
            e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(c, d);
            System.out.println("e = " + e);
            assertTrue("c | gcd(ca,cb) " + e, e.isZERO());
        }
    }


    /**
     * Test quaternion univariate recursive left gcd simple.
     */
    //@SuppressWarnings("cast")
    public void xtestRecursiveLeftGCDPrimitive() {
        String[] vars = new String[] { "a", "b" };
        BigQuaternionRing cf = new BigQuaternionRing();
        //GenSolvablePolynomialRing<BigQuaternion> qfac;
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, vars);
        //System.out.println("qfac = " + qfac.toScript());

        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        //qfac.addRelations(wl);
        System.out.println("qfac = " + qfac.toScript());
        rfac = (RecSolvablePolynomialRing<BigQuaternion>) qfac.recursive(1);
        System.out.println("rfac = " + rfac.toScript());

        GreatestCommonDivisorAbstract<BigQuaternion> fd = new GreatestCommonDivisorPrimitive<BigQuaternion>(cf);
        System.out.println("fd = " + fd);

        //kl = 3;
        ll = 3;
        el = 3;

        ar = rfac.random(kl, ll, el + 1, q);
        br = rfac.random(kl, ll, el, q);
        cr = rfac.random(kl, ll, el, q);
        ////cr = (RecSolvablePolynomial<BigQuaternion>) cr.abs();
        //cr = PolyUtil.<BigQuaternion> monic(cr); // leftMonic
        //cr = (RecSolvablePolynomial<BigQuaternion>) fd.recursivePrimitivePart(cr).abs();
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        if (cr.isConstant()) {
            er = rfac.univariate(0);
            System.out.println("univ(0) = " + er);
            cr = (RecSolvablePolynomial<BigQuaternion>) cr.sum(er);
        }
        //System.out.println("cr = " + cr);

        //ar0 = ar;
        //br0 = br;
        //ar = cr.multiply(ar);
        //br = cr.multiply(br);
        ar = ar.multiply(cr);
        br = br.multiply(cr);
        System.out.println("ar = " + ar);
        System.out.println("br = " + br);

        dr = fd.leftRecursiveUnivariateGcd(ar, br);
        System.out.println("cr_r = " + cr);
        System.out.println("dr_r = " + dr);
        //dr = PolyUtil.<BigQuaternion> monic(dr); // leftMonic
        //System.out.println("monic(dr_r) = " + dr);

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(dr, cr);
        System.out.println("er = " + er);
        assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(ar, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | a " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(br, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | b " + er, er.isZERO());
    }


    /**
     * Test quaternion univariate recursive left gcd simple Weyl.
     */
    //@SuppressWarnings("cast")
    public void xtestRecursiveLeftGCDPrimitiveWeyl() {
        String[] vars = new String[] { "a", "b" };
        BigQuaternionRing cf = new BigQuaternionRing();
        //GenSolvablePolynomialRing<BigQuaternion> qfac;
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, vars);
        //System.out.println("qfac = " + qfac.toScript());

        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        qfac.addRelations(wl);
        System.out.println("qfac = " + qfac.toScript());
        rfac = (RecSolvablePolynomialRing<BigQuaternion>) qfac.recursive(1);
        System.out.println("rfac = " + rfac.toScript());

        GreatestCommonDivisorAbstract<BigQuaternion> fd = new GreatestCommonDivisorPrimitive<BigQuaternion>(cf);
        System.out.println("fd = " + fd);

        //kl = 3;
        ll = 3;
        el = 3;

        ar = rfac.random(kl, ll, el + 1, q);
        br = rfac.random(kl, ll, el, q);
        cr = rfac.random(kl, ll, el, q);
        ////cr = (RecSolvablePolynomial<BigQuaternion>) cr.abs();
        ///cr = PolyUtil.<BigQuaternion> monic(cr); // leftMonic
        //cr = (RecSolvablePolynomial<BigQuaternion>) fd.recursivePrimitivePart(cr).abs();
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        if (cr.isConstant()) {
            er = rfac.univariate(0);
            System.out.println("univ(0) = " + er);
            cr = (RecSolvablePolynomial<BigQuaternion>) cr.sum(er);
        }
        //System.out.println("cr = " + cr);

        //ar0 = ar;
        //br0 = br;
        //ar = cr.multiply(ar);
        //br = cr.multiply(br);
        ar = ar.multiply(cr);
        br = br.multiply(cr);
        System.out.println("ar = " + ar);
        System.out.println("br = " + br);

        dr = fd.leftRecursiveUnivariateGcd(ar, br);
        System.out.println("cr_w = " + cr);
        System.out.println("dr_w = " + dr);
        //dr = PolyUtil.<BigQuaternion> monic(dr); // leftMonic
        //System.out.println("monic(dr_w) = " + dr);

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(dr, cr);
        System.out.println("er = " + er);
        assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(ar, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | a " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(br, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | b " + er, er.isZERO());
    }


    /**
     * Test 3 vars arbitrary recursive gcd simple.
     */
    @SuppressWarnings("cast")
    public void testArbitrary3RecursiveGCDPrimitive() {
        String[] cvars = new String[] { "a", "b" };
        String[] vars = new String[] { "c" };
        BigQuaternionRing cf = new BigQuaternionRing();
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, cvars);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        //dfac.addRelations(wl);
        //System.out.println("qfac = " + qfac.toScript());
        rfac = new RecSolvablePolynomialRing<BigQuaternion>(qfac, to, vars);
        System.out.println("rfac = " + rfac.toScript());

        //kl = 3; ll = 2;
        int el = 2;

        ar0 = rfac.random(kl, ll, el + 1, q);
        br0 = rfac.random(kl, ll, el, q);
        cr = rfac.random(kl, ll, el, q);

        //ar0 = rfac.parse(" ");
        //br0 = rfac.parse(" ");
        //cr  = rfac.parse(" ");

        //cr = (RecSolvablePolynomial<BigQuaternion>) fd.recursivePrimitivePart(cr).abs();
        //cr = (RecSolvablePolynomial<BigQuaternion>) cr.monic();
        if (cr.isZERO()) {
            cr = rfac.getONE();
        }
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        //System.out.println("cr = " + cr);

        // left gcd
        ar = ar0.multiply(cr);
        br = br0.multiply(cr);
        System.out.println("ar = " + ar);
        System.out.println("br = " + br);

        dr = fd.leftRecursiveGcd(ar, br);
        System.out.println("cr = " + cr);
        System.out.println("dr = " + dr);

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(dr, cr);
        System.out.println("er = " + er);
        assertTrue("c | gcd(ac,bc): " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(ar, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(ac,bc) | ac: " + er, er.isZERO());

        er = FDUtil.<BigQuaternion> recursiveSparsePseudoRemainder(br, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(ac,bc) | bc: " + er, er.isZERO());
    }

}
