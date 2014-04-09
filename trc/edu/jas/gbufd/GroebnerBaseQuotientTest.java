/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Groebner base sequential quotient fraction free tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBaseQuotientTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBaseQuotientTest.class);


    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GroebnerBaseQuotientTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseQuotientTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseQuotientTest.class);
        return suite;
    }


    GenPolynomialRing<Quotient<BigRational>> fac;


    List<GenPolynomial<Quotient<BigRational>>> L, Lp, Lq;


    PolynomialList<Quotient<BigRational>> F;


    List<GenPolynomial<Quotient<BigRational>>> G, Gp;


    GroebnerBaseAbstract<Quotient<BigRational>> bb;


    GroebnerBaseAbstract<Quotient<BigRational>> bbp;


    GroebnerBaseAbstract<Quotient<BigRational>> bbq;


    GenPolynomial<Quotient<BigRational>> a, b, c, d, e;


    int threads = 2;


    int rl = 4;


    int kl = 1; //7; // 10


    int ll = 3; //7;


    int el = 3; // 4


    float q = 0.2f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(9);
        GenPolynomialRing<BigRational> cf = new GenPolynomialRing<BigRational>(coeff, rl / 2, new String[] {
                "a", "b" });
        QuotientRing<BigRational> qf = new QuotientRing<BigRational>(cf);
        fac = new GenPolynomialRing<Quotient<BigRational>>(qf, rl / 2, new String[] { "x", "y" });
        a = b = c = d = e = null;
        bb = new GroebnerBaseQuotient<BigRational>(qf);
        bbp = new GroebnerBaseQuotient<BigRational>(qf, threads);
        //bbq = new GroebnerBaseSeq<Quotient<BigRational>>();
        bbq = GBFactory.<Quotient<BigRational>> getImplementation(qf);
    }


    @Override
    protected void tearDown() {
        bbp.terminate();
        a = b = c = d = e = null;
        fac = null;
        bb = null;
        bbp = null;
        bbq = null;
        ComputerThreads.terminate();
    }


    /**
     * Test sequential GBase.
     */
    public void testSequentialGBase() {
        L = new ArrayList<GenPolynomial<Quotient<BigRational>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d } )", bb.isMinimalGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d, e } )", bb.isMinimalGB(L));
    }


    /**
     * Test parallel GBase.
     */
    public void testParallelGBase() {
        L = new ArrayList<GenPolynomial<Quotient<BigRational>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);

        L = bbp.GB(L);
        assertTrue("isGB( { a } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L.size() );

        L = bbp.GB(L);
        assertTrue("isGB( { a, b } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( d )", !d.isZERO());
        L.add(d);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d } )", bbp.isMinimalGB(L));

        assertTrue("not isZERO( e )", !e.isZERO());
        L.add(e);

        L = bbp.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bbp.isGB(L));
        assertTrue("isMinimalGB( { a, b, c, d, e } )", bbp.isMinimalGB(L));
    }


    /**
     * Test compare GBases.
     */
    public void testCompareGBase() {
        L = new ArrayList<GenPolynomial<Quotient<BigRational>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = a.sum(b); //fac.random(kl, ll, el, q);
        d = c; //fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO()) {
            return;
        }

        //assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        Lp = bbp.GB(L);
        Lq = bbq.GB(L);
        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a } )", bb.isMinimalGB(L));
        assertEquals("Lp == L: ", Lp, L);
        assertEquals("Lq == L: ", Lq, L);
        //System.out.println("L = " + L);

        L.add(b);
        Lp = bbp.GB(L);
        Lq = bbq.GB(L);
        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b } )", bb.isMinimalGB(L));
        assertEquals("Lp == L: ", Lp, L);
        assertEquals("Lq == L: ", Lq, L);
        //System.out.println("L = " + L);

        L.add(c);
        Lp = bbp.GB(L);
        Lq = bbq.GB(L);
        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));
        assertTrue("isMinimalGB( { a, b, c } )", bb.isMinimalGB(L));
        assertEquals("Lp == L: ", Lp, L);
        assertEquals("Lq == L: ", Lq, L);
        //System.out.println("L = " + L);

        //L.add(d);
        //Lp = bbp.GB(L);
        //Lq = bbq.GB(L);
        //L  = bb.GB(L);
        //assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));
        //assertTrue("isMinimalGB( { a, b, c, d } )", bb.isMinimalGB(L));
        //assertEquals("Lp == L: ", Lp, L);
        //assertEquals("Lq == L: ", Lq, L);
        //System.out.println("L = " + L);
    }

}
