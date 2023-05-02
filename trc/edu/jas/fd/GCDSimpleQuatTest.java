/*
 * $Id$
 */

package edu.jas.fd;


import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
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
 * GCD Simple PRS algorithm tests with JUnit. <b>Note:</b> not in sync with
 * implementation.
 * @author Heinz Kredel
 */

public class GCDSimpleQuatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GCDSimpleQuatTest</CODE> object.
     * @param name String.
     */
    public GCDSimpleQuatTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDSimpleQuatTest.class);
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
        fd = new GreatestCommonDivisorSimple<BigQuaternion>(cf);
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, to, vars);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        qfac.addRelations(wl);
        rfac = (RecSolvablePolynomialRing<BigQuaternion>) qfac.recursive(1);
        //System.out.println("qfac = " + qfac);
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
    public void testQuatBaseGcdSimple() {
        String[] uvars = new String[] { "x" };
        BigQuaternionRing cf = new BigQuaternionRing();
        //GenSolvablePolynomialRing<BigQuaternion> qfac;
        qfac = new GenSolvablePolynomialRing<BigQuaternion>(cf, 1, to, uvars);
        System.out.println("qfac = " + qfac.toScript());
        //GenSolvablePolynomial<BigQuaternion> a, b, c, d, e;
        //GreatestCommonDivisorAbstract<BigQuaternion> fd;
        //fd = new GreatestCommonDivisorSimple<BigQuaternion>(cf);
        for (int i = 0; i < 3; i++) {
            //System.out.println();
            a = qfac.random(kl + (i), ll + 2 * i, el + 2, q);
            a = (GenSolvablePolynomial<BigQuaternion>) a.sum(qfac.univariate(0).power(2));
            b = qfac.random(kl + (i + 1), ll + i, el + 2, q);
            b = (GenSolvablePolynomial<BigQuaternion>) b.sum(qfac.univariate(0));
            c = qfac.random(kl + (i + 1), ll + 1, el + 1, q);
            c = c.multiply(qfac.univariate(0));
            if (a.isZERO()||b.isZERO()||c.isZERO()) {
                // skip for this turn
                continue;
            }
            a = a.monic();
            b = b.monic();
            c = c.monic();
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("c  = " + c);

            //a = a.multiply(c);
            //b = b.multiply(c);
            a = c.multiply(a);
            b = c.multiply(b);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            d = fd.leftBaseGcd(a, b);
            e = (GenSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(d, c);
            System.out.println("d  = " + d);
            System.out.println("c  = " + c);
            assertTrue("c | gcd(ca,cb) " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(a, d);
            System.out.println("e = " + e);
            assertTrue("gcd(ca,cb) | ca " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigQuaternion>) FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(b, d);
            System.out.println("e = " + e);
            assertTrue("gcd(ca,cb) | cb " + e, e.isZERO());
        }
    }

}
