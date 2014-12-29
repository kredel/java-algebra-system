/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.structure.NotInvertibleException;


/**
 * WordResidue tests with JUnit.
 * @author Heinz Kredel.
 */

public class WordResidueTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>WordResidueTest</CODE> object.
     * @param name String.
     */
    public WordResidueTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(WordResidueTest.class);
        return suite;
    }


    WordIdeal<BigRational> id;


    WordResidueRing<BigRational> fac;


    GenWordPolynomialRing<BigRational> mfac;


    List<GenWordPolynomial<BigRational>> F;


    WordResidue<BigRational> a, b, c, d, e;


    int rl = 4;


    int kl = 3;


    int ll = 4;


    int el = 2;


    int il = (rl == 1 ? 1 : 2);


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        String[] vars = new String[] { "w", "x", "y", "z" };
        mfac = new GenWordPolynomialRing<BigRational>(new BigRational(1), vars);
        //System.out.println("mfac = " + mfac.toScript());
        do {
            F = new ArrayList<GenWordPolynomial<BigRational>>(il);
            for (int i = 0; i < il; i++) {
                GenWordPolynomial<BigRational> mo = mfac.random(kl, ll, el);
                while (mo.isConstant()) {
                    mo = mfac.random(kl, ll, el);
                }
                F.add(mo);
            }
            id = new WordIdeal<BigRational>(mfac, F);
            id.doGB();
        } while (id.isONE());
        //System.out.println("id = " + id);
        assert !id.isONE() : "id = " + id;
        fac = new WordResidueRing<BigRational>(id);
        //System.out.println("fac = " + fac.toScript());
        F = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        id = null;
        mfac = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = fac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.val = " + c.val);
        assertTrue("length( c ) = 1 ", c.val.length() == 1 || id.isONE());
        assertTrue("isZERO( c )", !c.isZERO() || id.isONE());
        assertTrue("isONE( c )", c.isONE() || id.isONE());

        d = fac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.val = " + d.val);
        assertTrue("length( d ) = 0", d.val.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());

        for (WordResidue<BigRational> g : fac.generators()) {
            //System.out.println("g = " + g);
            assertFalse("not isZERO( g )", g.isZERO());
        }
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 1; i < 7; i++) {
            //a = fac.random(ll+i);
            a = fac.random(kl * i / 2, ll + i, el + i / 2);
            //System.out.println("a = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            assertTrue("length( a" + i + " ) <> 0", a.val.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(kl, ll, el + 1);
        b = fac.random(kl, ll, el + 1);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        c = fac.random(kl, ll, el);
        //System.out.println("c = " + c);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        assertEquals("c+(a+b) = (c+a)+b", d, e);

        c = a.sum(fac.getZERO());
        d = a.subtract(fac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = fac.getZERO().sum(a);
        d = fac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        List<WordResidue<BigRational>> g = fac.generators();
        //System.out.println("g = " + g);
        //a = fac.random(kl,ll,el,q);
        a = g.get(1);
        if (a.isZERO()) {
            a = fac.getONE(); //return;
        }
        assertTrue("not isZERO( a )", !a.isZERO());

        b = fac.random(kl, ll, el);
        //b = g.get(g.size()-1);
        if (b.isZERO()) {
            b = fac.getONE(); //return;
        }
        assertTrue("not isZERO( b )", !b.isZERO());
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.multiply(fac.getONE());
        d = fac.getONE().multiply(a);
        assertEquals("a*1 = 1*a", c, d);
        assertEquals("a*1 = 1*a", c, a);

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        assertTrue("not isZERO( d )", !d.isZERO());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //e = d.subtract(c);
        //non-com: assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );
        //non-com: assertEquals("a*b = b*a",c,d);

        c = fac.random(kl, ll + 1, el);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e) );
        assertEquals("a(bc) = (ab)c", d, e);
        //assertTrue("a(bc) = (ab)c", d.equals(e) );

        if (a.isUnit()) { // !a.isZERO() isUnit()
            try {
                c = a.inverse();
                //System.out.println("a = " + a);
                //System.out.println("c = " + c);
                d = c.multiply(a);
                //System.out.println("d = " + d);
                assertTrue("a*1/a = 1: " + fac, d.isONE()); // || true 
            } catch (NotInvertibleException e) {
                // can happen
            } catch (UnsupportedOperationException e) {
            }
        }
    }

}
