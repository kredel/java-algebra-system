/*
 * $Id$
 */

package edu.jas.fd;


import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.gbufd.SolvableSyzygyAbstract;
import edu.jas.gbufd.SolvableSyzygySeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
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
     * Test base field gcd left - right.
     */
    public void testBaseGcdLeftRight() {
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
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = a.multiply(c).multiplyLeft(cc);
            b = b.multiply(c).multiplyLeft(cc);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.leftRightBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            //System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            assertTrue("cont.isGCD(): " + cont, cont.isGCD());
        }
    }


    /**
     * Test base field gcd right - left.
     */
    public void testBaseGcdRightLeft() {
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
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = c.multiply(cc).multiply(a);
            b = c.multiply(cc).multiply(b);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.rightLeftBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            //System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            assertTrue("cont.isGCD(): " + cont, cont.isGCD());
        }
    }


    /**
     * Test base field left gcd.
     */
    public void testBaseLeftGcd() {
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
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            ////System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = c.multiply(a);
            b = c.multiply(b);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = fd.leftBaseGcd(a, b);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //assertTrue("cont.isGCD(): " + cont, cont.isGCD());

            e = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("e == 0: " + e, e.isZERO());
            f = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(b, d);
            //System.out.println("f = " + f);
            assertTrue("f == 0: " + f, f.isZERO());
        }
    }


    /**
     * Test base field right gcd.
     */
    public void testBaseRightGcd() {
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
            c = c.multiply(cc);
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigQuaternion>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            ////System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = fd.rightBaseGcd(a, b);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //assertTrue("cont.isGCD(): " + cont, cont.isGCD());

            e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("e == 0: " + e, e.isZERO());
            f = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(b, d);
            //System.out.println("f = " + f);
            assertTrue("f == 0: " + f, f.isZERO());
        }
    }


    /**
     * Test base integer gcd left - right.
     */
    public void testBaseIntegerGcdLeftRight() {
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
            if (c.isZERO() || cc.isZERO()) {
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

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = a.multiply(c).multiplyLeft(cc);
            b = b.multiply(c).multiplyLeft(cc);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.leftRightBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            //System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD(): = " + cont.isGCD());
            //System.out.println("r = " + cont.right + ", l=" + cont.left);
            //System.out.println("c = " + c + ", cc = " + cc); 
            assertTrue("cont.isGCD(): " + cont, cont.isGCD());
        }
    }


    /**
     * Test base integer gcd right - left.
     */
    public void testBaseIntegerGcdRightLeft() {
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
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = a.multiply(c).multiplyLeft(cc);
            b = b.multiply(c).multiplyLeft(cc);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            GCDcoFactors<BigQuaternion> cont = fd.rightLeftBaseGcd(a, b);
            //d = (GenSolvablePolynomial<BigQuaternion>) cont.left;
            //System.out.println("cont = " + cont);
            //System.out.println("cont.isGCD() = " + cont.isGCD());
            //System.out.println("r = " + cont.right + ", l=" + cont.left);
            //System.out.println("c = " + c + ", cc = " + cc); 
            assertTrue("cont.isGCD(): " + cont, cont.isGCD());
        }
    }


    /**
     * Test base integral left gcd.
     */
    public void testBaseIntegralLeftGcd() {
        String[] uvars = new String[] { "x" };
        cfac = new BigQuaternionRing(true);
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + i, el + 1, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 1, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 0, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            cc = cfac.random(kl);
            c = c.multiplyLeft(cc);
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            // replace - by ~ in coefficents for polynomial tokenizer
            //a = dfac.parse("( 1i0j~1k0 ) x^2 - ( 9/2i5/2j~1/2k~1/2 ) x + 5/2i5/2j9/2k3/2");
            //b = dfac.parse("1i1j1k1 x^3 + ( 1i~1j1k1 )");
            ////c = dfac.parse("( ~3i11j~3k~1 ) x^2 - ( 5i~10j~5k0 ) x");
            //c = dfac.parse("( ~3i11j~3k~1 ) x - ( 5i~10j~5k0 ) ");

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            ////System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = c.multiply(a);
            b = c.multiply(b);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = fd.leftBaseGcd(a, b);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);

            e = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("e == 0: " + e, e.isZERO());
            f = FDUtil.<BigQuaternion> rightBaseSparsePseudoRemainder(b, d);
            //System.out.println("f = " + f);
            assertTrue("f == 0: " + f, f.isZERO());
        }
    }


    /**
     * Test base integral right gcd.
     */
    public void testBaseIntegralRightGcd() {
        String[] uvars = new String[] { "x" };
        cfac = new BigQuaternionRing(true);
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + i, el + 1, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 1, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 0, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            cc = cfac.random(kl);
            c = c.multiply(cc);
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            // replace - by ~ in coefficents for polynomial tokenizer
            //a = dfac.parse("( 5/2i~1/2j~3/2k~3/2 ) x^2 - ( 1i0j0k~1 )");
            //b = dfac.parse("( 1i~1j1k0 ) x^2 - 1i2j0k0 x + ( 1/2i1/2j~1/2k3/2 )");
            //c = dfac.parse("( 0i~1j~2k~14 ) x");

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            ////System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            d = fd.rightBaseGcd(a, b);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);
            //d = fd.leftBasePrimitivePart(fd.rightBasePrimitivePart(d));
            //System.out.println("d = " + d);

            e = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(a, d);
            //System.out.println("e  = " + e);
            assertTrue("e == 0: " + e, e.isZERO());
            f = FDUtil.<BigQuaternion> leftBaseSparsePseudoRemainder(b, d);
            //System.out.println("f  = " + f);
            assertTrue("f == 0: " + f, f.isZERO());
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
        GreatestCommonDivisorAbstract<BigQuaternion> fds = new GreatestCommonDivisorSimple<BigQuaternion>(
                        cfac);
        //BigQuaternion cc;
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
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);

            GenSolvablePolynomial<BigQuaternion>[] aqr, bqr;

            aqr = FDUtil.<BigQuaternion> leftBasePseudoQuotientRemainder(a, c);
            bqr = FDUtil.<BigQuaternion> leftBasePseudoQuotientRemainder(b, c);
            d = aqr[0];
            e = bqr[0];

            //System.out.println("d  = " + d + ", rem = " + aqr[1]);
            //System.out.println("e  = " + e + ", rem = " + bqr[1]);
            assertTrue("a rem == 0: ", aqr[1].isZERO());
            assertTrue("b rem == 0: ", bqr[1].isZERO());

            boolean t;
            f = d.multiply(c);
            //System.out.println("f  = " + f);
            //System.out.println("a  = " + a);
            t = f.equals(a);
            //System.out.println("d*c == a: " + t);

            BigQuaternion qa, qb, oa, ob;
            qa = a.leadingBaseCoefficient();
            qb = f.leadingBaseCoefficient();
            GcdRingElem<BigQuaternion>[] oc = fd.leftOreCond(qa, qb);
            oa = (BigQuaternion) oc[0];
            ob = (BigQuaternion) oc[1];

            a = a.multiplyLeft(oa);
            f = f.multiplyLeft(ob);
            //System.out.println("f  = " + f);
            //System.out.println("a  = " + a);
            t = f.equals(a);
            //System.out.println("d*c == a: " + t);
            assertTrue("d*c == a: ", t);


            g = e.multiply(c);
            //System.out.println("g  = " + g);
            //System.out.println("b  = " + b);
            t = g.equals(b);
            //System.out.println("e*c == b: " + t);
            assertTrue("e*c == b: ", t || true);

            r = (GenSolvablePolynomial<BigQuaternion>) fds.leftBasePrimitivePart(b).abs();
            s = (GenSolvablePolynomial<BigQuaternion>) fds.leftBasePrimitivePart(g).abs();
            //System.out.println("pp(b)  = " + r);
            //System.out.println("pp(g)  = " + s);
            assertEquals("pp(b) == pp(g): ", r, s);

            qa = b.leadingBaseCoefficient();
            qb = g.leadingBaseCoefficient();
            oc = fd.leftOreCond(qa, qb);
            oa = (BigQuaternion) oc[0];
            ob = (BigQuaternion) oc[1];

            b = b.multiplyLeft(oa);
            g = g.multiplyLeft(ob);
            //System.out.println("g  = " + g);
            //System.out.println("b  = " + b);
            t = g.equals(b);
            //System.out.println("e*c == b: " + t);
            assertTrue("e*c == b: ", t);
        }
    }


    /**
     * Test base integral left / right pseudo division.
     */
    public void testBaseIntegralDivision() {
        String[] uvars = new String[] { "x" };
        cfac = new BigQuaternionRing(true);
        syz = new SolvableSyzygySeq<BigQuaternion>(cfac);
        //System.out.println("syz = " + syz);
        dfac = new GenSolvablePolynomialRing<BigQuaternion>(cfac, 1, to, uvars);
        fd = new GreatestCommonDivisorLR<BigQuaternion>(cfac, syz);
        BigQuaternion cc;
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl * (i + 2), ll + i, el + 1, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 1, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 0, q);
            //c = dfac.getONE(); 
            c = c.multiply(dfac.univariate(0));
            cc = cfac.random(kl);
            c = c.multiplyLeft(cc);
            if (c.isZERO() || cc.isZERO()) {
                // skip for this turn
                continue;
            }
            // replace - by ~ in coefficents for polynomial tokenizer
            //a = dfac.parse("( 1i~3j~3k~3 ) x^3 - ( 9i7j~12k~8 ) x^2 + 20i17j14k10 x + ( 19/2i27/2j~31/2k~7/2 ) ");
            //b = dfac.parse("( 3i2j~1k0 ) x^4 + ( 2i1j~3k~1 ) x^3 + ( 3i0j2k~1 ) x + ( 5/2i3/2j1/2k~5/2 ) ");
            //c = dfac.parse("0i0j4k0 x - ( 1/2i~1/2j~5/2k~5/2 ) ");

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            ////System.out.println("cc = " + cc);
            //System.out.println("c  = " + c);

            //System.out.println("ppl(a) = " + fd.leftPrimitivePart(a));
            //System.out.println("ppl(b) = " + fd.leftPrimitivePart(b));
            //System.out.println("ppr(a) = " + fd.rightPrimitivePart(a));
            //System.out.println("ppr(b) = " + fd.rightPrimitivePart(b));
            //b = fd.rightPrimitivePart(b);

            s = b;
            r = a;
            while (!r.isZERO()) {
                GenSolvablePolynomial<BigQuaternion>[] qr;
                GenSolvablePolynomial<BigQuaternion> x, y, z;
                qr = FDUtil.<BigQuaternion> rightBasePseudoQuotientRemainder(s, r);
                y = qr[0];
                x = qr[1];
                z = (GenSolvablePolynomial<BigQuaternion>) r.multiply(y).sum(x);
                //System.out.println("z = " + z + ", s = " + s);
                BigQuaternion lz, ls;
                lz = z.leadingBaseCoefficient();
                ls = s.leadingBaseCoefficient();
                GcdRingElem[] oc = fd.rightOreCond(lz, ls);
                z = z.multiply((BigQuaternion) oc[0]);
                s = s.multiply((BigQuaternion) oc[1]);
                //System.out.println("z * oa = " + z);
                //System.out.println("s * os = " + s);
                //System.out.println("z * oz == s * os: " + z.equals(s));
                assertEquals("z * oz == s * os: ", z, s);
                s = r;
                r = x;
                //System.out.println("r  = " + r);
            }
            //System.out.println("s  = " + s);
            //s = fd.rightPrimitivePart(s);
            //s = fd.leftPrimitivePart(s);
            //System.out.println("s  = " + s);
            //System.out.println("c  = " + c + ", s==c: " + s.equals(c));
            g = s;

            GenSolvablePolynomial<BigQuaternion>[] qr;
            qr = FDUtil.<BigQuaternion> rightBasePseudoQuotientRemainder(a, g);
            d = qr[0];
            h = (GenSolvablePolynomial<BigQuaternion>) g.multiply(d).sum(qr[1]);
            //System.out.println("h  = " + h);
            //System.out.println("a  = " + a);

            BigQuaternion lh, la, lb;
            lh = h.leadingBaseCoefficient();
            la = a.leadingBaseCoefficient();
            GcdRingElem[] oc = fd.rightOreCond(lh, la);
            h = h.multiply((BigQuaternion) oc[0]);
            s = a.multiply((BigQuaternion) oc[1]);
            assertEquals("h * oh == a * oa: ", h, s);

            //assertTrue("r==0: ", qr[1].isZERO());

            qr = FDUtil.<BigQuaternion> rightBasePseudoQuotientRemainder(b, g);
            e = qr[0];
            h = (GenSolvablePolynomial<BigQuaternion>) g.multiply(e).sum(qr[1]);
            //System.out.println("h  = " + h);
            //System.out.println("b  = " + b);

            lh = h.leadingBaseCoefficient();
            lb = b.leadingBaseCoefficient();
            oc = fd.rightOreCond(lh, lb);
            h = h.multiply((BigQuaternion) oc[0]);
            s = b.multiply((BigQuaternion) oc[1]);
            assertEquals("h * oh == a * oa: ", h, s);

            //System.out.println("d  = " + d + ", rem = " + qr[1]);
            //System.out.println("e  = " + e + ", rem = " + qr[1]);
            f = g.multiply(d);
            g = g.multiply(e);
            //System.out.println("f  = " + f + ", a==f? " + a.equals(f));
            //System.out.println("g  = " + g + ", b==g? " + b.equals(g));

            BigQuaternion lf, lg;
            la = a.leadingBaseCoefficient();
            lb = b.leadingBaseCoefficient();
            lf = f.leadingBaseCoefficient();
            lg = g.leadingBaseCoefficient();

            oc = fd.rightOreCond(la, lf);
            r = a.multiply((BigQuaternion) oc[0]);
            s = f.multiply((BigQuaternion) oc[1]);
            //System.out.println("a * oa = " + r);
            //System.out.println("f * of = " + s);
            //System.out.println("a * oa == f * of: " + r.equals(s));
            assertEquals("a * oa == f * of: ", r, s);

            oc = fd.rightOreCond(lb, lg);
            r = b.multiply((BigQuaternion) oc[0]);
            s = g.multiply((BigQuaternion) oc[1]);
            //System.out.println("b * ob = " + r);
            //System.out.println("g * og = " + s);
            //System.out.println("b * ob == g * og: " + r.equals(s));
            assertEquals("b * ob == g * og: ", r, s);

        }
    }
}
