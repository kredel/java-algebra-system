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

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.WeylRelations;


/**
 * SolvableLocal over BigRational GenSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class SolvableLocalTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableLocalTest</CODE> object.
     * @param name String.
     */
    public SolvableLocalTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableLocalTest.class);
        return suite;
    }


    SolvableLocalRing<BigRational> efac;


    GenSolvablePolynomialRing<BigRational> mfac;


    SolvableIdeal<BigRational> id;


    SolvableLocal<BigRational> a, b, c, d, e;


    SolvableLocal<BigRational> az, bz, cz, dz, ez;


    int rl = 4;


    int kl = 3;


    int ll = 3; //6;


    int el = 3;


    float q = 0.2f;


    int il = ( rl == 1 ? 1 : 2 ); 


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "w", "x", "y", "z" };
        mfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(mfac);
        if (!mfac.isAssociative() ) {
           System.out.println("ring not associative: " + mfac);
        }
        //id = genRandomIdeal();
        id = genIdealA();
        //System.out.println("id = " + id);
        assert !id.isONE() : "id = " + id;
        efac = new SolvableLocalRing<BigRational>(id);
        //System.out.println("efac = " + efac);
    }


    protected SolvableIdeal<BigRational> genRandomIdeal() {
        List<GenSolvablePolynomial<BigRational>> F;
        do {
            F = new ArrayList<GenSolvablePolynomial<BigRational>>( il );
            for ( int i = 0; i < il; i++ ) {
                GenSolvablePolynomial<BigRational> mo = mfac.random(kl,ll,el+1,q);
                while ( mo.isConstant() ) {
                    mo = mfac.random(kl,ll,el+1,q);
                }
                F.add( mo );
            }
            SolvableIdeal<BigRational> id = new SolvableIdeal<BigRational>(mfac,F);
            id.doGB();
        } while (id.isONE());
        return id;
    }


    protected SolvableIdeal<BigRational> genIdealA() {
        GenSolvablePolynomial<BigRational> p;
        List<GenSolvablePolynomial<BigRational>> F;
        F = new ArrayList<GenSolvablePolynomial<BigRational>>( il );
        p = mfac.parse("y^2 - 42/5");
        F.add( p );
        //p = mfac.parse("z^2");
        p = mfac.parse("x^2");
        F.add( p );
        //p = mfac.parse("x^2 - w^2 ");
        //F.add( p );
        SolvableIdeal<BigRational> id = new SolvableIdeal<BigRational>(mfac,F);
        id.doGB();
        return id;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //efac.terminate();
        efac = null;
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

        for (SolvableLocal<BigRational> g : efac.generators()) {
            //System.out.println("g = " + g);
            assertFalse("not isZERO( g )", g.isZERO());
        }
        // solved: not all products are defined 
        assertTrue("isAssociative: ", efac.isAssociative());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            //a = efac.random(ll+i);
            a = efac.random(kl * (i + 1), ll + 2 + 2 * i, el, q);
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
        //a = efac.random(kl, ll, el+1, q);
        //b = efac.random(kl, ll, el+1, q);
        //a = efac.parse("{ 1  | w * x + 25/28  }");
        //b = efac.parse("{ x - 35/18  | x + 2 w - 6  }");
        //!a = efac.parse("{ x - 1/7  | y * z + 7/10  }");
        //!b = efac.parse("{ 1  | w + 3  }");
        a = efac.parse("{ 1  | y * z + 7/10 }");
        b = efac.parse("{ x - 1/7 | w + 3  }");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(efac.getZERO());
        d = a.subtract(efac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = efac.getZERO().sum(a);
        d = efac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0-(-a)", c, d);

        //c = a.sum(b);
        //d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //assertEquals("a+b = b+a", c, d);

        c = a.sum(b);
        //System.out.println("c = " + c);
        d = c.subtract(b);
        //d = c.sum(b.negate());
        //System.out.println("d = " + d);
        //System.out.println("a = " + a);
        assertEquals("(a+b)-b = a", a, d);
        e = d.subtract(a);
        //System.out.println("d-a = " + e);
        assertTrue("((a+b)-b)-a = 0", e.isZERO());

        //c = efac.random(kl,ll,el,q);
        c = new SolvableLocal<BigRational>(efac, mfac.univariate(1, 2));
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
        a = efac.random(kl, ll, el+1, q);
        b = efac.random(kl, ll, el+1, q);
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

        //c = efac.random(kl,ll,el,q);
        c = new SolvableLocal<BigRational>(efac, mfac.univariate(1, 2)); 
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        //System.out.println("d = " + d);
        e = (a.multiply(b)).multiply(c);
        //System.out.println("e = " + e);
        assertEquals("a(bc) = (ab)c", d, e);
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
     * Test parse.
     */
    public void testParse() {
        a = efac.random(kl * 2, ll * 2, el * 2, q * 2);
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
