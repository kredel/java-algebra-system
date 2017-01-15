/*
 * $Id$
 */

package edu.jas.fd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.arith.BigQuaternionInteger;
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
 * GCD LeftRight PRS algorithm tests with JUnit. <b>Note:</b> not in sync with
 * implementation.
 * @author Heinz Kredel
 */

public class GCDLeftRightTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GCDLeftRightTest</CODE> object.
     * @param name String.
     */
    public GCDLeftRightTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDLeftRightTest.class);
        return suite;
    }


    //GreatestCommonDivisorAbstract<BigQuaternion> fd;
    GreatestCommonDivisorLR<BigQuaternion> fd;


    TermOrder to = TermOrderByName.INVLEX;


    BigQuaternionRing cfac;


    GenSolvablePolynomialRing<BigQuaternion> dfac;


    //GenSolvablePolynomialRing<GenPolynomial<BigQuaternion>> rfac;
    RecSolvablePolynomialRing<BigQuaternion> rfac;


    GenSolvablePolynomial<BigQuaternion> a, b, c, d, e, f, g, h;


    GenSolvablePolynomial<GenPolynomial<BigQuaternion>> ar, br, cr, dr, er;


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
        cfac = new BigQuaternionRing();
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac);
        //fd = new GreatestCommonDivisorFake<BigQuaternion>(cfac);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, rl, to, vars);
        RelationGenerator<BigQuaternion> wl = new WeylRelationsIterated<BigQuaternion>();
        dfac.addRelations(wl);
        rfac = (RecSolvablePolynomialRing<BigQuaternion>) dfac.recursive(1);
        //System.out.println("dfac = " + dfac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ar = br = cr = dr = er = null;
        fd = null;
        cfac = null;
        dfac = null;
        rfac = null;
    }


    /**
     * Test base rational gcd right.
     */
    public void xtestBaseGcdRight() {
        String[] uvars = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + i, el + 1, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 1, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 0, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            cc = cfac.random(kl);
            c = c.multiplyLeft(cc);
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("cc = " + cc);
            System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.leftRightBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            assertTrue("cont.isGCD() ", cont.isGCD());

            //e = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> basePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            //assertTrue("gcd(a,b) | a " + e, e.isZERO());

            //e = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> basePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            //assertTrue("gcd(a,b) | b " + e, e.isZERO());
        }
    }


    /**
     * Test base gcd left.
     */
    public void xtestBaseGcdLeft() {
        String[] uvars = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + i, el + 2, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 2, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 1, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            cc = cfac.random(kl);
            c = c.multiplyLeft(cc);
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            System.out.println("a  = " + a);
            System.out.println("b  = " + b); 
            System.out.println("cc = " + cc);
            System.out.println("c  = " + c);

            a = c.multiply(a);
            b = c.multiply(b);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.leftRightBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            assertTrue("cont.isGCD() ", cont.isGCD());

            //e = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> basePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            //assertTrue("gcd(a,b) | a " + e, e.isZERO());

            //e = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> basePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            //assertTrue("gcd(a,b) | b " + e, e.isZERO());
        }
    }


    /**
     * Test base integer gcd right.
     */
    public void testBaseIntegerGcdRight() {
        String[] uvars = new String[] { "x" };
        cfac = new BigQuaternionRing(true);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + 1, el + 2, q);
            b = dfac.random(kl * (i + 1), ll + 1, el + 2, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 1, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            c = (GenSolvablePolynomial<BigQuaternion>) c.abs();
            //cc = cfac.random(kl);
            cc = cfac.getONE(); 
            //c = c.multiplyLeft(cc);
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("cc = " + cc);
            System.out.println("c  = " + c);

            a = a.multiply(c).multiplyLeft(cc);
            b = b.multiply(c).multiplyLeft(cc);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.leftRightBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            System.out.println("r = " + cont.right + ", l=" + cont.left);
            System.out.println("c = " + c); //.monic());
            assertTrue("cont.isGCD() ", cont.isGCD());

            if (true) {
                return;
            }
            GreatestCommonDivisorAbstract<BigQuaternion> fds = new GreatestCommonDivisorSimple<BigQuaternion>(cfac);

            d = fds.leftBaseGcd(a,b);
            System.out.println("d = " + d);
            System.out.println("c = " + c);
            e = fds.rightBaseGcd(a,b);
            System.out.println("d = " + d);
            System.out.println("e = " + e);
        }
    }
}
