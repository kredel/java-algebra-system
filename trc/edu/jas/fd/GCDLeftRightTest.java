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
import edu.jas.gbufd.SolvableSyzygyAbstract;
import edu.jas.gbufd.SolvableSyzygySeq;
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
import edu.jas.structure.GcdRingElem;

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


    GenSolvablePolynomial<BigQuaternion> a, b, c, d, e, f, g, h, r, s;


    GenSolvablePolynomial<GenPolynomial<BigQuaternion>> ar, br, cr, dr, er;


    SolvableSyzygyAbstract<BigQuaternion> syz;


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
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
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
        syz = null;
    }


    /**
     * Test base field gcd right.
     */
    public void testBaseGcdRight() {
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
            //c = c.multiplyLeft(cc);
            if (c.isZERO()||cc.isZERO()) {
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
     * Test base field gcd left.
     */
    public void testBaseGcdLeft() {
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
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + 1, el + 2, q);
            b = dfac.random(kl * (i + 1), ll + 1, el + 2, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 1, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            c = (GenSolvablePolynomial<BigQuaternion>) c.abs();
            cc = cfac.random(kl);
            //cc = cfac.getONE(); 
            //c = c.multiplyLeft(cc);
            if (c.isZERO()||cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            // replace - by ~ in coefficents for polynomial tokenizer
            //a = dfac.parse("( 3/2i1/2j1/2k~1/2 ) x^3 - ( 1i~1j0k~2 ) x + ( 11/2i15/2j1/2k~1/2 )");
            //b = dfac.parse("( ~1i2j3k1 ) x^4 + ( 0i1j~2k0 ) x^2 + ( 1/2i~3/2j1/2k3/2 )");
            //c = dfac.parse("( 1/2i1/2j~1/2k1/2 ) x^4 + ( 1i~1j0k0 ) x^3 + 1i1j0k1 x");

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
            System.out.println("c = " + c + ", cc = " + cc); 
            assertTrue("cont.isGCD() ", cont.isGCD());
        }
    }


    /**
     * Test base integer quotient remainder.
     */
    public void testBaseQR() {
        String[] uvars = new String[] { "x" };
        cfac = new BigQuaternionRing(true);
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
        GreatestCommonDivisorAbstract<BigQuaternion> fds = new GreatestCommonDivisorSimple<BigQuaternion>(cfac);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + 1, el + 2, q);
            b = dfac.random(kl * (i + 1), ll + 1, el + 2, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 1, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            c = (GenSolvablePolynomial<BigQuaternion>) c.abs();
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);

            GenSolvablePolynomial<BigQuaternion>[] aqr, bqr;

            aqr = FDUtil.<BigQuaternion> leftBasePseudoQuotientRemainder(a, c);
            bqr = FDUtil.<BigQuaternion> leftBasePseudoQuotientRemainder(b, c);
            d = aqr[0];
            e = bqr[0];

            System.out.println("d  = " + d + ", rem = " + aqr[1]);
            System.out.println("e  = " + e + ", rem = " + bqr[1]);
            assertTrue("a rem == 0: ", aqr[1].isZERO());
            assertTrue("b rem == 0: ", bqr[1].isZERO());

            boolean t;
            f = d.multiply(c);
            System.out.println("f  = " + f);
            System.out.println("a  = " + a);
            t = f.equals(a);
            System.out.println("d*c == a: " + t);

            BigQuaternion qa, qb, oa, ob;
            qa = a.leadingBaseCoefficient();
            qb = f.leadingBaseCoefficient();
            GcdRingElem<BigQuaternion>[] oc = fd.leftOreCond(qa,qb);
            oa = (BigQuaternion)oc[0];
            ob = (BigQuaternion)oc[1];

            a = a.multiplyLeft(oa);
            f = f.multiplyLeft(ob);
            System.out.println("f  = " + f);
            System.out.println("a  = " + a);
            t = f.equals(a);
            System.out.println("d*c == a: " + t);
            assertTrue("d*c == a: ", t);


            g = e.multiply(c);
            System.out.println("g  = " + g);
            System.out.println("b  = " + b);
            t = g.equals(b);
            System.out.println("e*c == b: " + t);

            r = (GenSolvablePolynomial<BigQuaternion>)fds.leftBasePrimitivePart(b).abs();
            s = (GenSolvablePolynomial<BigQuaternion>)fds.leftBasePrimitivePart(g).abs();
            System.out.println("pp(b)  = " + r);
            System.out.println("pp(g)  = " + s);
            assertEquals("pp(b) == pp(g): ", r, s);

            qa = b.leadingBaseCoefficient();
            qb = g.leadingBaseCoefficient();
            oc = fd.leftOreCond(qa,qb);
            oa = (BigQuaternion)oc[0];
            ob = (BigQuaternion)oc[1];

            b = b.multiplyLeft(oa);
            g = g.multiplyLeft(ob);
            System.out.println("g  = " + g);
            System.out.println("b  = " + b);
            t = g.equals(b);
            System.out.println("e*c == b: " + t);
            assertTrue("e*c == b: ", t);
        }
    }
}
