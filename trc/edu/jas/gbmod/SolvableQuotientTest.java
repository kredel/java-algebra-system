/*
 * $Id$
 */

package edu.jas.gbmod;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.RelationGenerator;


/**
 * SolvableQuotient over BigRational GenSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class SolvableQuotientTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableQuotientTest</CODE> object.
     * @param name String.
     */
    public SolvableQuotientTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableQuotientTest.class);
        return suite;
    }


    SolvableQuotientRing<BigRational> zFac;


    SolvableQuotientRing<BigRational> efac;


    GenSolvablePolynomialRing<BigRational> mfac;


    SolvableQuotient<BigRational> a, b, c, d, e;


    SolvableQuotient<BigRational> az, bz, cz, dz, ez;


    int rl = 4;


    int kl = 2;


    int ll = 3; //6;


    int el = 2;


    float q = 0.2f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "w", "x", "y", "z" };
        mfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(mfac);
        efac = new SolvableQuotientRing<BigRational>(mfac);
        zFac = new SolvableQuotientRing<BigRational>(mfac);
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

        for (SolvableQuotient<BigRational> g : efac.generators()) {
            //System.out.println("g = " + g);
            assertFalse("not isZERO( g )", g.isZERO());
        }
        //wrong, solved: 
        assertTrue("isAssociative: ", efac.isAssociative());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            //a = efac.random(ll+i);
            a = efac.random(kl * (i + 1), ll + i, el, q);
            //System.out.println("a = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.num.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
            assertEquals("a == a: ", a, a);
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

        c = a.sum(efac.getZERO());
        d = a.subtract(efac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = efac.getZERO().sum(a);
        d = efac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);

        c = a.sum(b);
        d = c.subtract(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        //c = efac.random(kl,ll,el,q);
        c = new SolvableQuotient<BigRational>(efac, mfac.univariate(1, 2)); //efac.random(kl,ll,el,q);
        //System.out.println("c = " + c);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertEquals("c+(a+b) = (c+a)+b", d, e);
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        a = efac.random(kl, ll, el, q);
        b = efac.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.multiply(efac.getONE());
        d = efac.getONE().multiply(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*1 = 1*a", c, a);
        assertEquals("a*1 = 1*a", c, d);

        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //non-com assertFalse("a*b = b*a", c.equals(d) );
        //e = d.subtract(c);
        //non-com assertFalse("not isZERO( a*b-b*a ) " + e, e.isZERO() );

        c = new SolvableQuotient<BigRational>(efac, mfac.univariate(1, 2)); //efac.random(kl,ll,el,q);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        //System.out.println("d = " + d);
        e = (a.multiply(b)).multiply(c);
        //System.out.println("e = " + e);
        assertEquals("a(bc) = (ab)c", d, e);
        //assertTrue("a(bc) = (ab)c", d.equals(e) );
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
     * Test addition without syzygy gcd.
     */
    public void testAdditionGcd() {
        long te, tz;

        a = efac.random(kl, ll, el, q);
        b = efac.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        az = new SolvableQuotient<BigRational>(zFac, a.num, a.den, true);
        bz = new SolvableQuotient<BigRational>(zFac, b.num, b.den, true);
        if (false) {
            return;
        }

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

        if (tz >= 0L||te >= 0L) { // true, findbugs
            return;
        }
        System.out.println("te = " + te);
        System.out.println("tz = " + tz);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        c = efac.random(kl, ll, el, q);
        cz = new SolvableQuotient<BigRational>(zFac, c.num, c.den, true);


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
     * Test parse.
     */
    public void testParse() {
        a = efac.random(kl, ll, el + 1, q);
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

}
