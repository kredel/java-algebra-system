/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * PolynomialList Test using JUnit.
 * @author Heinz Kredel
 */

public class PolynomialListTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PolynomialListTest</CODE> object.
     * @param name String.
     */
    public PolynomialListTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolynomialListTest.class);
        return suite;
    }

    GenPolynomialRing<BigRational> fac;


    PolynomialList<BigRational> m;


    PolynomialList<BigRational> p;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 4;


    int kl = 4;


    int ll = 4;


    int el = 5;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        m = null;
        p = null;
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        m = null;
        p = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        p = new PolynomialList<BigRational>(fac, (List<GenPolynomial<BigRational>>) null);
        assertTrue("p = 0", p.list == null);

        m = new PolynomialList<BigRational>(fac, new ArrayList<GenPolynomial<BigRational>>());
        assertTrue("m = 0", m.list != null);
        assertTrue("m.size() == 0", m.list.size() == 0);

        String s = m.toScript();
        //System.out.println("m.toScript: " + s + ", " + s.length());
        assertEquals("#s == 60: " + s, s.length(), 60);
    }


    /**
     * Test polynomial list.
     */
    public void testPolynomialList() {
        List<GenPolynomial<BigRational>> l = new ArrayList<GenPolynomial<BigRational>>();
        for (int i = 0; i < 7; i++) {
            a = fac.random(ll + i);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
            l.add(a);
        }
        p = new PolynomialList<BigRational>(fac, l);
        //System.out.println("p = "+p);

        assertTrue("p == p", p.equals(p));
        assertEquals("p.length", 7, p.list.size());
    }


    /**
     * Test ordered polynomial list.
     */
    public void testOrderedPolynomialList() {
        List<GenPolynomial<BigRational>> l = new ArrayList<GenPolynomial<BigRational>>();
        for (int i = 0; i < 7; i++) {
            a = fac.random(ll + i);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
            l.add(a);
        }
        p = new PolynomialList<BigRational>(fac, l);
        //System.out.println("p = "+p);

        m = new OrderedPolynomialList<BigRational>(fac, p.list);
        //System.out.println("m = "+m);

        assertTrue("p == m", p.equals(m));
        assertTrue("m != p", !m.equals(p));
        assertEquals("p.length", 7, p.list.size());
        assertEquals("m.length", 7, m.list.size());
    }


    /**
     * Test homogeneous polynomial list.
     */
    public void testHomogeneousPolynomialList() {
        List<GenPolynomial<BigRational>> l = new ArrayList<GenPolynomial<BigRational>>();
        for (int i = 0; i < 7; i++) {
            a = fac.random(ll + i);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
            l.add(a);
        }
        p = new PolynomialList<BigRational>(fac, l);
        //System.out.println("p = "+p);

        PolynomialList<BigRational> h = p.homogenize();
        //System.out.println("h = "+h);
        assertTrue("h is homogen", h.isHomogeneous());

        PolynomialList<BigRational> pp = h.deHomogenize();
        //System.out.println("pp = "+pp);
        assertTrue("p == pp", p.equals(pp));
    }

}
