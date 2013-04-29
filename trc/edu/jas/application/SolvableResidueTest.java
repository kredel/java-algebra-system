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
//import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.TermOrder;
import edu.jas.structure.NotInvertibleException;


/**
 * SolvableResidue tests with JUnit. 
 * @author Heinz Kredel.
 */

public class SolvableResidueTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>SolvableResidueTest</CODE> object.
     * @param name String.
     */
    public SolvableResidueTest(String name) {
        super(name);
    }

    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(SolvableResidueTest.class);
        return suite;
    }

    SolvableIdeal<BigRational> id;
    SolvableResidueRing<BigRational> fac;
    GenSolvablePolynomialRing<BigRational> mfac;
    List<GenSolvablePolynomial<BigRational>> F;

    SolvableResidue< BigRational > a, b, c, d, e;

    int rl = 4; 
    int kl = 3;
    int ll = 4;
    int el = 2;
    float q = 0.2f;
    int il = ( rl == 1 ? 1 : 2 ); 

    protected void setUp() {
        a = b = c = d = e = null;
        TermOrder to = new TermOrder( TermOrder.INVLEX );
        String[] vars = new String[] { "w", "x", "y", "z" };
        mfac = new GenSolvablePolynomialRing<BigRational>( new BigRational(1), rl, to, vars );
        WeylRelations<BigRational> wl = new WeylRelations<BigRational>(mfac);
        wl.generate();
        if (!mfac.isAssociative() ) {
           System.out.println("ring not associative: " + mfac);
        }
        do {
	    F = new ArrayList<GenSolvablePolynomial<BigRational>>( il );
	    for ( int i = 0; i < il; i++ ) {
		GenSolvablePolynomial<BigRational> mo = mfac.random(kl,ll,el,q);
		while ( mo.isConstant() ) {
		    mo = mfac.random(kl,ll,el,q);
		}
		F.add( mo );
	    }
	    id = new SolvableIdeal<BigRational>(mfac,F);
	    id.doGB();
        } while (id.isONE());
        //System.out.println("id = " + id);
        assert !id.isONE() : "id = " + id;
        fac = new SolvableResidueRing<BigRational>( id );
        //System.out.println("fac = " + fac);
        F = null;
    }

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
        assertTrue("isZERO( d )", d.isZERO() );
        assertTrue("isONE( d )", !d.isONE() );

        for (SolvableResidue<BigRational> g : fac.generators() ) {
            //System.out.println("g = " + g);
            assertFalse("not isZERO( g )", g.isZERO() );
        }
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 1; i < 7; i++) {
            //a = fac.random(ll+i);
            a = fac.random(kl*(i+1), ll+2*i, el+i, q );
            //System.out.println("a = " + a);
            if ( a.isZERO() || a.isONE() ) {
                continue;
            }
            assertTrue("length( a"+i+" ) <> 0", a.val.length() >= 0);
            assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
            assertTrue(" not isONE( a"+i+" )", !a.isONE() );
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(kl,ll,el,q);
        b = fac.random(kl,ll,el,q);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a",a,d);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a",c,d);

        c = fac.random(kl,ll,el,q);
        d = c.sum( a.sum(b) );
        e = c.sum( a ).sum(b);
        assertEquals("c+(a+b) = (c+a)+b",d,e);

        c = a.sum( fac.getZERO() );
        d = a.subtract( fac.getZERO() );
        assertEquals("a+0 = a-0",c,d);

        c = fac.getZERO().sum( a );
        d = fac.getZERO().subtract( a.negate() );
        assertEquals("0+a = 0+(-a)",c,d);
    }


    /**
     * Test object multiplication.
     */
    public void testMultiplication() {
        List<SolvableResidue<BigRational>> g = fac.generators();
        //System.out.println("g = " + g);
        //a = fac.random(kl,ll,el,q);
        a = g.get(1);
        if ( a.isZERO() ) {
            a = fac.getONE(); //return;
        }
        assertTrue("not isZERO( a )", !a.isZERO() );

        b = fac.random(kl,ll,el,q);
        //b = g.get(g.size()-1);
        if ( b.isZERO() ) {
            b = fac.getONE(); //return;
        }
        assertTrue("not isZERO( b )", !b.isZERO() );

        c = a.multiply( fac.getONE() );
        d = fac.getONE().multiply( a );
        assertEquals("a*1 = 1*a",c,d);
        assertEquals("a*1 = 1*a",c,a);

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO() );
        assertTrue("not isZERO( d )", !d.isZERO() );

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //e = d.subtract(c);
        //non-com: assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );
        //non-com: assertEquals("a*b = b*a",c,d);

        c = fac.random(kl,ll+1,el,q);
        //System.out.println("c = " + c);
        d = a.multiply( b.multiply(c) );
        e = a.multiply(b).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e) );
        assertEquals("a(bc) = (ab)c",d,e);
        //assertTrue("a(bc) = (ab)c", d.equals(e) );

        if ( !a.isZERO() ) { // !a.isZERO() isUnit()
            try {
                 c = a.inverse();
                 System.out.println("a = " + a);
                 System.out.println("c = " + c);
                 d = c.multiply(a);
                 System.out.println("d = " + d);
                 assertTrue("a*1/a = 1: " + fac,d.isONE()); 
            } catch (NotInvertibleException e) {
		// can happen
            }
        }

        // d = c.remainder(a);
        // //System.out.println("c = " + c);
        // System.out.println("d = " + d);
        // if ( d.isZERO() ) {
        //     d = c.divide(a);
        //     System.out.println("c = " + c);
        //     System.out.println("d = " + d);
        //     e = a.multiply(d);
        //     System.out.println("e = " + e);
        //     assertEquals("((b*a)/a)*a = b*a",e,c);
        // }
    }

}
