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
 * SolvableLocalResidue over BigRational GenSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */
public class SolvableLocalResidueTest extends TestCase {


    /**
     * main. 
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SolvableLocalResidueTest</CODE> object.
     * @param name String.
     */
    public SolvableLocalResidueTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SolvableLocalResidueTest.class);
        return suite;
    }


    SolvableLocalResidueRing<BigRational> efac;


    GenSolvablePolynomialRing<BigRational> mfac;


    SolvableIdeal<BigRational> id;


    SolvableLocalResidue<BigRational> a, b, c, d, e;


    SolvableLocalResidue<BigRational> az, bz, cz, dz, ez;


    int rl = 4;


    int kl = 2;


    int ll = 3; //6;


    int el = 2;


    float q = 0.15f;


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
        //id = genIdealA();
        id = genPrimeIdealA();
        //System.out.println("id = " + id);
        assert !id.isONE() : "id = " + id;
        efac = new SolvableLocalResidueRing<BigRational>(id);
        //System.out.println("efac = " + efac.toScript());
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


    protected SolvableIdeal<BigRational> genPrimeIdealA() { // well, almost
        GenSolvablePolynomial<BigRational> p;
        List<GenSolvablePolynomial<BigRational>> F;
        F = new ArrayList<GenSolvablePolynomial<BigRational>>( il );
        p = mfac.parse("y^2 + 5");
        F.add( p );
        p = mfac.parse("x^2 + 3");
        F.add( p );
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

        for (SolvableLocalResidue<BigRational> g : efac.generators()) {
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
        for (int i = 0; i < 4; i++) {
            //a = efac.random(ll+i);
            a = efac.random(kl + i, ll + 1, el, q);
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
        //a = efac.parse("{ 1  | w * x + 25/28  }");
        //b = efac.parse("{ x - 35/18  | x + 2 w - 6  }");
        //!a = efac.parse("{ x - 1/7  | y * z + 7/10  }");
        //!b = efac.parse("{ 1  | w + 3  }");
        //a = efac.parse("{ 1  | y * z + 7/10 }");
        //b = efac.parse("{ x - 1/7 | w + 3  }");
        //a = efac.parse("{ ( -21/10 ) z - 5/4 x | x * z - 1/2 x + 1/2 }");
        //b = efac.parse("{ 1 | z^2 - 63/5 }");
        //a = efac.parse("{ 1  | z - 92/105 }");
        //b = efac.parse("{ 1  | x }"); // when not prime
        //a = efac.parse("{ x - 3 | z + 5 }");
        //b = efac.parse("{ w + 2 | z - x * y }");
        //b = new SolvableLocalResidue<BigRational>(efac,efac.ideal.getList().get(0));
        //b = a.negate();
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(efac.getZERO());
        d = a.subtract(efac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = efac.getZERO().sum(a);
        d = efac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0-(-a)", c, d);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        c = a.sum(b);
        //System.out.println("c = " + c);
        d = c.subtract(b);
        //System.out.println("d = " + d);
        //System.out.println("a = " + a);
        assertEquals("(a+b)-b = a: b = " + b, a, d);

        c = efac.random(kl,ll,el,q);
        //c = new SolvableLocalResidue<BigRational>(efac, mfac.univariate(1, 2));
        //c = efac.parse("{ 42/5 }");
        //System.out.println("c = " + c);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e));
        assertEquals("c+(a+b) = (c+a)+b: a = " + a + ", b = " + b + ", c = " + c, d, e);
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
        //non-com TODO 
        //assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()) );

        //c = efac.random(kl,ll,el,q);
        c = new SolvableLocalResidue<BigRational>(efac, mfac.univariate(1, 2)); 
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        //System.out.println("d = " + d);
        e = (a.multiply(b)).multiply(c);
        //System.out.println("e = " + e);
        assertEquals("a(bc) = (ab)c", d, e);
    }


    /**
     * Test inverse.
     */
    public void testInverse() {
        a = efac.random(kl, ll+1, el+1, q*1.15f);
        //System.out.println("a = " + a);
        b = new SolvableLocalResidue<BigRational>(efac, mfac.getONE(), mfac.univariate(1, 2)); 
        //System.out.println("b = " + b);
        a = a.multiply(b);
        //System.out.println("a = " + a);

        if (a.isUnit()) { // true if != 0
            c = a.inverse();
            //System.out.println("c = " + c);
            d = c.multiply(a);
            //d = a.multiply(c);
            //System.out.println("d = " + d);
            assertTrue("1/a * a = 1", d.isONE());
            d = c.inverse();
            //System.out.println("d = " + d);
            //System.out.println("a = " + a);
            //System.out.println("c = " + c);
            //System.out.println("a-d = " + a.subtract(d));
            assertEquals("1/(1/a) = a", a, d);
        }
    }


    /**
     * Test roots (eval mod ideal). todo
     */
    public void testRoot() {
        c = efac.parse("y");
        //System.out.println("c = " + c);
        d = efac.parse("5");
        //System.out.println("d = " + d);
        e = c.multiply(c).sum(d);
        //System.out.println("e = " + e);
        assertTrue("e == 0 mod ideal", e.isZERO());
        for ( GenSolvablePolynomial<BigRational> p : efac.ideal.getList() ) {
             String s = p.toString();
             //System.out.println("s = " + s);
             e = efac.parse( s ); // eval mod ideal
             //System.out.println("e = " + e);
             assertTrue("e == 0 mod ideal", e.isZERO());
        }
    }


    /**
     * Test parse.
     */
    public void testParse() {
        a = efac.random(kl, ll, el, q);
        //PrettyPrint.setInternal();
        //System.out.println("a = " + a);
        PrettyPrint.setPretty();
        //System.out.println("a = " + a);
        String p = a.toString();
        //System.out.println("p = " + p);
        b = efac.parse(p);
        //System.out.println("b = " + b);
        assertEquals("parse(a.toSting()) = a", a, b);
    }

}
