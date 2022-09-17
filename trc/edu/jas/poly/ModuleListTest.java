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
 * ModuleList tests with JUnit.
 * @author Heinz Kredel
 */
public class ModuleListTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ModuleListTest</CODE> object.
     * @param name String.
     */
    public ModuleListTest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ModuleListTest.class);
        return suite;
    }


    ModuleList<BigRational> m;


    PolynomialList<BigRational> p;


    GenPolynomial<BigRational> a, b, c, d, e;


    BigRational cfac;


    GenPolynomialRing<BigRational> pfac;


    int rl = 4;


    int kl = 4;


    int ll = 4;


    int el = 5;


    float q = 0.5f;

    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new BigRational(1);
        TermOrder tord = new TermOrder();
        pfac = new GenPolynomialRing<BigRational>(cfac, rl, tord);
        m = null;
        p = null;
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
        p = new PolynomialList<BigRational>(pfac, (List<GenPolynomial<BigRational>>) null);
        assertTrue("p = 0", p.list == null);

        m = new ModuleList<BigRational>(pfac, (List<List<GenPolynomial<BigRational>>>) null);
        assertTrue("m = 0", m.list == null);
    }


    /**
     * Test polynomial list.
     */
    public void testPolynomialList() {
        List<GenPolynomial<BigRational>> l = new ArrayList<GenPolynomial<BigRational>>();
        for (int i = 0; i < 7; i++) {
            a = pfac.random(kl, ll + i, el, q);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
            l.add(a);
        }
        p = new PolynomialList<BigRational>(pfac, l);
        //System.out.println("p = "+p);

        assertTrue("p == p", p.equals(p));
        assertEquals("p.length", 7, p.list.size());
    }


    /**
     * Test module list.
     */
    public void testModuleList() {
        List<List<GenPolynomial<BigRational>>> l = new ArrayList<List<GenPolynomial<BigRational>>>();
        for (int i = 0; i < 4; i++) {
            List<GenPolynomial<BigRational>> r = new ArrayList<GenPolynomial<BigRational>>();
            for (int j = 0; j < 3; j++) {
                a = pfac.random(kl, ll, el, q);
                if (a.isZERO() || a.isONE()) {
                    continue;
                }
                assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
                assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
                assertTrue(" not isONE( a" + i + " )", !a.isONE());
                r.add(a);
            }
            l.add(r);
        }
        m = new ModuleList<BigRational>(pfac, l);
        //System.out.println("m = "+m);
        assertTrue("m == m", m.equals(m));
        assertEquals("m.length", 4, m.list.size());

        List<Integer> perm = new ArrayList<Integer>(pfac.nvar);
        for (int i = 0; i < pfac.nvar; i++) {
            perm.add(i);
        }
        OptimizedModuleList op = new OptimizedModuleList<BigRational>(perm, pfac, l);
        assertTrue("op == op", op.equals(op));
        assertFalse("op != m", op.equals(m));
        assertEquals("#var == #perm", pfac.nvar, op.perm.size());
    }


    /**
     * Test module and polynomial list.
     */
    public void testModulePolynomialList() {
        List<List<GenPolynomial<BigRational>>> l = new ArrayList<List<GenPolynomial<BigRational>>>();
        for (int i = 0; i < 4; i++) {
            List<GenPolynomial<BigRational>> r = new ArrayList<GenPolynomial<BigRational>>();
            for (int j = 0; j < 3; j++) {
                a = pfac.random(kl, ll, el, q);
                if (a.isZERO() || a.isONE()) {
                    continue;
                }
                assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
                assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
                assertTrue(" not isONE( a" + i + " )", !a.isONE());
                r.add(a);
            }
            l.add(r);
        }
        m = new ModuleList<BigRational>(pfac, l);
        //System.out.println("m = "+m);
        assertTrue("m == m", m.equals(m));
        assertEquals("m.length", 4, m.list.size());

        p = m.getPolynomialList();
        //System.out.println("p = "+p);
        assertTrue("p == p", p.equals(p));
        assertEquals("p.length", 4, p.list.size());
    }


    /**
     * Test module and polynomial and module list.
     */
    public void testModulePolynomialModuleList() {
        List<List<GenPolynomial<BigRational>>> l = new ArrayList<List<GenPolynomial<BigRational>>>();
        for (int i = 0; i < 4; i++) {
            List<GenPolynomial<BigRational>> r = new ArrayList<GenPolynomial<BigRational>>();
            for (int j = 0; j < 3; j++) {
                a = pfac.random(kl, ll, el, q);
                if (a.isZERO() || a.isONE()) {
                    continue;
                }
                assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
                assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
                assertTrue(" not isONE( a" + i + " )", !a.isONE());
                r.add(a);
            }
            l.add(r);
        }
        m = new ModuleList<BigRational>(pfac, l);
        //System.out.println("m = "+m);
        assertTrue("m == m", m.equals(m));
        assertEquals("m.length", 4, m.list.size());

        p = m.getPolynomialList();
        //System.out.println("p = "+p);
        assertTrue("p == p", p.equals(p));
        assertEquals("p.length", 4, p.list.size());

        ModuleList<BigRational> m2 = null;
        m2 = p.getModuleList(3);
        //System.out.println("m2 = "+m2);
        assertTrue("m2 == m2", m2.equals(m2));
        assertEquals("m2.length", 4, m2.list.size());

        assertTrue("m == m2", m.equals(m2));
    }


    /**
     * Test module and polynomial and module and polynomial list.
     */
    public void testModulePolynomialModuleListPolynomial() {
        List<List<GenPolynomial<BigRational>>> l = new ArrayList<List<GenPolynomial<BigRational>>>();
        for (int i = 0; i < 4; i++) {
            List<GenPolynomial<BigRational>> r = new ArrayList<GenPolynomial<BigRational>>();
            for (int j = 0; j < 3; j++) {
                a = pfac.random(kl, ll, el, q);
                if (a.isZERO() || a.isONE()) {
                    continue;
                }
                assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
                assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
                assertTrue(" not isONE( a" + i + " )", !a.isONE());
                r.add(a);
            }
            l.add(r);
        }
        m = new ModuleList<BigRational>(pfac, l);
        //System.out.println("m = "+m);
        assertTrue("m == m", m.equals(m));
        assertEquals("m.length", 4, m.list.size());

        p = m.getPolynomialList();
        //System.out.println("p = "+p);
        assertTrue("p == p", p.equals(p));
        assertEquals("p.length", 4, p.list.size());

        ModuleList<BigRational> m2 = null;
        m2 = p.getModuleList(3);
        //System.out.println("m2 = "+m2);
        assertTrue("m2 == m2", m2.equals(m2));
        assertEquals("m2.length", 4, m2.list.size());

        assertTrue("m == m2", m.equals(m2));

        PolynomialList<BigRational> p2 = null;
        p2 = m2.getPolynomialList();
        //System.out.println("p2 = "+p2);
        assertTrue("p2 == p2", p2.equals(p2));
        assertEquals("p2.length", 4, p2.list.size());

        assertTrue("p == p2", p.list.equals(p2.list));
    }

}
