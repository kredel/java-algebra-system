/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Reduction tests with JUnit.
 * @author Heinz Kredel
 */

public class ReductionTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {

        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>ReductionTest</CODE> object.
     * @param name String
     */
    public ReductionTest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ReductionTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;


    GenPolynomial<BigRational> a, b, c, d, e;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F, G;


    int rl = 2;


    int kl = 2;


    int ll = 3;


    int el = 3;


    float q = 0.4f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new GenPolynomialRing<BigRational>(new BigRational(0), rl);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /*
     * Test dummy.
     * 
     public void testDummy() {
     }
     */


    /**
     * Test rational coefficient polynomial parametric reduction,
     * caseDistinction and determination.
     */
    public void testRatPolReduction() {
        RingFactory<BigRational> bi = new BigRational(0);
        GenPolynomialRing<BigRational> pr = new GenPolynomialRing<BigRational>(bi, 2,
                        new String[] { "a", "b" });
        GenPolynomialRing<GenPolynomial<BigRational>> fac = new GenPolynomialRing<GenPolynomial<BigRational>>(
                        pr, rl);

        CReductionSeq<BigRational> cred = new CReductionSeq<BigRational>(bi);

        GenPolynomial<GenPolynomial<BigRational>> a = fac.random(kl, ll, el, q);
        while (a.isZERO()) {
            a = fac.random(kl, ll, el, q).sum(fac.getONE());
        }
        GenPolynomial<GenPolynomial<BigRational>> b = fac.random(kl, ll, el, q);
        while (b.isZERO()) {
            b = fac.random(kl, ll, el, q).subtract(fac.getONE());
        }
        GenPolynomial<GenPolynomial<BigRational>> g = fac.getZERO();

        Map.Entry<ExpVector, GenPolynomial<BigRational>> m = a.leadingMonomial();
        ExpVector e = m.getKey();
        GenPolynomial<BigRational> c = m.getValue();

        GenPolynomial<GenPolynomial<BigRational>> r = fac.getZERO();
        r = r.sum(c, e);
        if (r.isZERO()) {
            r = fac.getONE();
        }

        GenPolynomial<GenPolynomial<BigRational>> w = a.reductum();

        ColorPolynomial<BigRational> p = new ColorPolynomial<BigRational>(g, r, w);
        //System.out.println("p = " + p);
        assertTrue("check(p) ", p.checkInvariant());
        assertTrue("deter(p) ", p.isDetermined());
        //System.out.println("cond != 0: " + p.getConditionNonZero());
        //System.out.println("cond == 0: " + p.getConditionZero());

        p = new ColorPolynomial<BigRational>(r, g, w);
        //System.out.println("p = " + p);
        assertTrue("check(p) ", p.checkInvariant());
        if (!w.isZERO()) {
            assertFalse("deter(p) ", p.isDetermined());
        }
        //System.out.println("cond != 0: " + p.getConditionNonZero());
        //System.out.println("cond == 0: " + p.getConditionZero());

        p = new ColorPolynomial<BigRational>(r, w, g);
        //System.out.println("p = " + p);
        assertTrue("check(p) ", p.checkInvariant());
        assertTrue("deter(p) ", p.isDetermined());
        //System.out.println("cond != 0: " + p.getConditionNonZero());
        //System.out.println("cond == 0: " + p.getConditionZero());

        // wrong test: p = new ColorPolynomial<BigRational>(w,r,g); //(w,g,r); 
        //System.out.println("p = " + p);
        //if ( !w.isZERO() ) {
        //   assertFalse("check(p) ", p.checkInvariant());
        //}
        //assertFalse("deter(p) ", p.isDetermined());
        //assertFalse("p == 0 ", p.isZERO());
        //System.out.println("cond != 0: " + p.getConditionNonZero());
        //System.out.println("cond == 0: " + p.getConditionZero());

        p = new ColorPolynomial<BigRational>(w, g, g);
        //System.out.println("p = " + p);
        assertTrue("check(p) ", p.checkInvariant());
        assertTrue("deter(p) ", p.isDetermined());
        assertTrue("p == 0 ", p.isZERO());
        //System.out.println("cond != 0: " + p.getConditionNonZero());
        //System.out.println("cond == 0: " + p.getConditionZero());

        List<GenPolynomial<BigRational>> i = new ArrayList<GenPolynomial<BigRational>>();
        Ideal<BigRational> id = new Ideal<BigRational>(pr, i);
        List<ColorPolynomial<BigRational>> cp = new ArrayList<ColorPolynomial<BigRational>>();

        Condition<BigRational> cond = new Condition<BigRational>(id);
        ColoredSystem<BigRational> s = new ColoredSystem<BigRational>(cond, cp);
        //System.out.println("s = " + s);

        String z = s.toString() + ", " + s.toScript();
        //System.out.println("z = " + z + ", " + z.length());
        assertTrue("length( string(s) ) >= 0", z.length() >= 0);

        assertTrue("isDetermined ", s.isDetermined());
        assertTrue("checkInvariant ", s.checkInvariant());

        List<ColoredSystem<BigRational>> CS = new ArrayList<ColoredSystem<BigRational>>();
        CS.add(s);
        //System.out.println("CS = " + CS);
        List<ColoredSystem<BigRational>> CSp = CS;

        //System.out.println("\na = " + a);
        //System.out.println("b = " + b + "\n");

        //CS = cred.determine(p);
        //System.out.println("CS = " + CS);
        for (ColoredSystem<BigRational> x : CS) {
            assertTrue("isDetermined ", x.isDetermined());
            assertTrue("checkInvariant ", x.checkInvariant());
        }

        List<GenPolynomial<GenPolynomial<BigRational>>> L;
        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();
        L.add(a);
        L.add(b);

        //System.out.println("\na = " + a);
        //System.out.println("b = " + b + "\n");
        //System.out.println("L = " + L);

        //List<Condition<BigRational>> Ccond;
        //Ccond = cred.caseDistinction(L);
        //for ( Condition<BigRational> cnd : Ccond ) {
        //    System.out.println("" + cnd);
        //}
        //+System.out.println("Ccond = " + Ccond);

        // check if polynomials are determined
        CSp = cred.determine(L);
        //+System.out.println("CSp = " + CSp);
        for (ColoredSystem<BigRational> x : CSp) {
            assertTrue("isDetermined ", x.isDetermined());
            assertTrue("checkInvariant ", x.checkInvariant());
        }

        // check if reduced polynomials are in normalform
        ColorPolynomial<BigRational> q, h;
        //List<ColoredSystem<BigRational>> NCS;
        for (ColoredSystem<BigRational> x : CSp) {
            int k = x.list.size();
            for (int j = 0; j < k; j++) {
                p = x.list.get(j);
                for (int l = j + 1; l < k; l++) {
                    q = x.list.get(l);
                    h = cred.SPolynomial(p, q);
                    //System.out.println("spol(a,b) = " + h);
                    boolean t = true; //cred.isNormalform( x.list, h );
                    //System.out.println("isNF(spol(a,b)) = " + t);
                    h = cred.normalform(x.condition, x.list, h);
                    //System.out.println("NF(spol(a,b)) = " + h);
                    t = cred.isNormalform(x.list, h);
                    //System.out.println("isNF(NF(spol(a,b))) = " + t);
                    assertTrue("isNF(NF(spol(a,b))) ", t);
                    //h = x.condition.reDetermine( h );
                }
            }
        }
    }

}
