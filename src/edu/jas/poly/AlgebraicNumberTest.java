
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;

//import edu.jas.structure.RingElem;
import edu.jas.structure.NotInvertibleException;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


/**
 * AlgebraicNumber Test using JUnit. 
 * @author Heinz Kredel.
 */

public class AlgebraicNumberTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>AlgebraicNumberTest</CODE> object.
     * @param name String.
     */
    public AlgebraicNumberTest(String name) {
        super(name);
    }

    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(AlgebraicNumberTest.class);
        return suite;
    }

    //private final static int bitlen = 100;

    AlgebraicNumberRing<BigRational> fac;
    GenPolynomialRing<BigRational> mfac;

    AlgebraicNumber< BigRational > a;
    AlgebraicNumber< BigRational > b;
    AlgebraicNumber< BigRational > c;
    AlgebraicNumber< BigRational > d;
    AlgebraicNumber< BigRational > e;

    int rl = 1; 
    int kl = 10;
    int ll = 10;
    int el = ll;
    float q = 0.5f;

    protected void setUp() {
        a = b = c = d = e = null;
        BigRational bi = new BigRational(1);
        //bi.setAllIterator();
        bi.setNoDuplicatesIterator();
        mfac = new GenPolynomialRing<BigRational>( bi, rl );
        GenPolynomial<BigRational> mo = mfac.random(kl,ll,7,q);
        while ( mo.isConstant() ) {
            mo = mfac.random(kl,ll,5,q);
        }
        fac = new AlgebraicNumberRing<BigRational>( mo.monic() );
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        c = fac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.getVal() = " + c.getVal());
        assertTrue("length( c ) = 1", c.getVal().length() == 1);
        assertTrue("isZERO( c )", !c.isZERO() );
        assertTrue("isONE( c )", c.isONE() );

        d = fac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.getVal() = " + d.getVal());
        assertTrue("length( d ) = 0", d.getVal().length() == 0);
        assertTrue("isZERO( d )", d.isZERO() );
        assertTrue("isONE( d )", !d.isONE() );
    }


    /**
     * Test random polynomial.
     * 
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            a = fac.random(el);
            //System.out.println("a = " + a);
            if ( a.isZERO() || a.isONE() ) {
                continue;
            }
            // fac.random(rl+i, kl*(i+1), ll+2*i, el+i, q );
            assertTrue("length( a"+i+" ) <> 0", a.getVal().length() >= 0);
            assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
            assertTrue(" not isONE( a"+i+" )", !a.isONE() );
        }
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        a = fac.random(ll);
        b = fac.random(ll);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a",a,d);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a",c,d);

        c = fac.random(ll);
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
     * 
     */
    public void testMultiplication() {
        a = fac.random(ll);
        assertTrue("not isZERO( a )", !a.isZERO() );

        b = fac.random(ll);
        assertTrue("not isZERO( b )", !b.isZERO() );

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO() );
        assertTrue("not isZERO( d )", !d.isZERO() );

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

        assertTrue("a*b = b*a", c.equals(d) );
        assertEquals("a*b = b*a",c,d);

        c = fac.random(ll);
        //System.out.println("c = " + c);
        d = a.multiply( b.multiply(c) );
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c",d,e);
        assertTrue("a(bc) = (ab)c", d.equals(e) );

        c = a.multiply( fac.getONE() );
        d = fac.getONE().multiply( a );
        assertEquals("a*1 = 1*a",c,d);

        c = a.inverse();
        d = c.multiply(a);
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*1/a = 1",fac.getONE(),d);

        try {
            a = fac.getZERO().inverse();
            fail("0 invertible");
        } catch(NotInvertibleException expected) {
            // ok
        }

        GenPolynomial<BigRational> dp = fac.modul;
        GenPolynomial<BigRational> cp = fac.modul.multiply(a.val.monic());
        //System.out.println("dp = " + dp);
        //System.out.println("cp = " + cp);
        fac = new AlgebraicNumberRing<BigRational>( cp );
        a = new AlgebraicNumber<BigRational>(fac,a.val.monic());
        assertFalse("a !unit mod m*a", a.isUnit());

        try {
            b = a.inverse();
            fail("invertible " + a);
        } catch (AlgebraicNotInvertibleException expected) {
            //ok
            //expected.printStackTrace();
            //System.out.println("expected = " + expected);
            assertTrue("f  = " + cp,    expected.f.equals(cp));
            assertTrue("f1 = " + a.val, expected.f1.equals(a.val));
            assertTrue("f2 = " + dp,    expected.f2.equals(dp));
            assertTrue("f  =  f1*f2 ",  expected.f.equals(expected.f1.multiply(expected.f2)));
        } catch (NotInvertibleException e) {
            //e.printStackTrace();
            fail("wrong exception " + e);
        }
    }


    /**
     * Test distributive law.
     * 
     */
    public void testDistributive() {
        a = fac.random( ll );
        b = fac.random( ll );
        c = fac.random( ll );

        d = a.multiply( b.sum(c) );
        e = a.multiply( b ).sum( a.multiply(c) );

        assertEquals("a(b+c) = ab+ac",d,e);
    }


    /**
     * Test enumerator.
     * 
     */
    public void testEnumerator() {
        //System.out.println("fac = " + fac);
        long s = 0L;
        long t = 0L;
        Set<AlgebraicNumber<BigRational>> elems = new HashSet<AlgebraicNumber<BigRational>>(49);
        //Iterator<AlgebraicNumber<BigRational>> iter = fac.iterator();
        for ( AlgebraicNumber<BigRational> an : fac ) {
            t++;
            if ( elems.contains(an) ) {
                //System.out.println("dup(an) = " + an);
                s++;
            } else { 
                //System.out.println("an = " + an);
                elems.add(an);
            }
            if ( t >= 100 ) {
                break;
            }
        }
        //System.out.println("elems = " + elems);
        assertTrue("#elems " + t + ", t = " + elems.size(), t == elems.size() );
        assertTrue("#elems " + t + ", t = " + elems.size(), s == 0L );
    }

}
