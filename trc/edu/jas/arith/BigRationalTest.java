/*
 * $Id$
 */

package edu.jas.arith;


import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * BigRational tests with JUnit. 
 * @author Heinz Kredel.
 */

public class BigRationalTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>BigRationalTest</CODE> object.
     * @param name String.
     */
    public BigRationalTest(String name) {
        super(name);
    }

    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(BigRationalTest.class);
        return suite;
    }

    BigRational a;
    BigRational b;
    BigRational c;
    BigRational d;
    BigRational e;

    protected void setUp() {
        a = b = c = d = e = null;
    }

    protected void tearDown() {
        a = b = c = d = e = null;
    }


    /**
     * Test static initialization and constants.
     * 
     */
    public void testConstants() {
        a = BigRational.ZERO;
        b = BigRational.ONE;
        c = BigRational.RNDIF(b,b);

        assertEquals("1-1 = 0",c,a);
        assertTrue("1-1 = 0",c.isZERO());
        assertTrue("1 = 1", b.isONE() );

        a = BigRational.ZERO;
        b = BigRational.ONE;
        c = BigRational.RNDIF(b,b);

        assertEquals("1-1 = 0",c,a);
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstructor() {
        a = new BigRational( "6/8" );
        b = new BigRational( "3/4" );

        assertEquals("6/8 = 3/4",a,b);

        a = new BigRational( "3/-4" );
        b = new BigRational( "-3/4" );

        assertEquals("3/-4 = -3/4",a,b);

        String s = "6/1111111111111111111111111111111111111111111";
        a = new BigRational( s );
        String t = a.toString();

        assertEquals("stringConstr = toString",s,t);

        a = new BigRational( 1 );
        b = new BigRational( -1 );
        c = BigRational.RNSUM(b,a);

        assertTrue("1 = 1", a.isONE() );
        assertEquals("1+(-1) = 0",c,BigRational.ZERO);

        s = "1.500000000";
        a = new BigRational( s );
        b = new BigRational( "3/2" );
        assertEquals("decimalConstr = b ",a,b);

        s = "-1.500000000";
        a = new BigRational( s );
        b = new BigRational( "-3/2" );
        assertEquals("decimalConstr = b ",a,b);

        s = "0.750000000";
        a = new BigRational( s );
        b = new BigRational( "3/4" );
        assertEquals("decimalConstr = b ",a,b);

        s = "0.333333333";
        a = new BigRational( s );
        t = a.toString(9);
        assertEquals("decimalConstr = b " + t,s,t);

        s = "-0.000033333";
        a = new BigRational( s );
        t = a.toString(9);
        assertEquals("decimalConstr = b " + t,s,t);
        //System.out.println("a = " + a);
    }


    /**
     * Test random rationals.
     * 
     */
    public void testRandom() {
        a = BigRational.RNRAND( 500 );
        b = new BigRational( "" + a );
        c = BigRational.RNDIF(b,a);

        assertEquals("a-b = 0",c,BigRational.ZERO);

        d = new BigRational( b.numerator(), b.denominator() );
        assertEquals("sign(a-a) = 0", 0, b.compareTo(d) );
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        a = BigRational.RNRAND( 100 );
        b = BigRational.RNSUM( a, a );
        c = BigRational.RNDIF( b, a );

        assertEquals("a+a-a = a",c,a);
        assertEquals("a+a-a = a",0,BigRational.RNCOMP(c,a));

        d = BigRational.RNSUM( a, BigRational.ZERO );
        assertEquals("a+0 = a",d,a);
        d = BigRational.RNDIF( a, BigRational.ZERO );
        assertEquals("a-0 = a",d,a);
        d = BigRational.RNDIF( a, a );
        assertEquals("a-a = 0",d,BigRational.ZERO);

    }


    /**
     * Test multiplication.
     * 
     */
    public void testMultiplication() {
        a = BigRational.RNRAND( 100 );
        b = BigRational.RNPROD( a, a );
        c = BigRational.RNQ( b, a );

        assertEquals("a*a/a = a",c,a);
        assertEquals("a*a/a = a",0,BigRational.RNCOMP(c,a));

        d = BigRational.RNPROD( a, BigRational.ONE );
        assertEquals("a*1 = a",d,a);
        d = BigRational.RNQ( a, BigRational.ONE );
        assertEquals("a/1 = a",d,a);

        a = BigRational.RNRAND( 100 );
        b = BigRational.RNINV( a );
        c = BigRational.RNPROD( a, b );

        assertTrue("a*1/a = 1", c.isONE() );
    }


    /**
     * Test distributive law.
     * 
     */
    public void testDistributive() {
        BigRational fac = new BigRational();

        a = fac.random( 500 );
        b = fac.random( 500 );
        c = fac.random( 500 );

        d = a.multiply( b.sum(c) );
        e = a.multiply( b ).sum( a.multiply(c) );

        assertEquals("a(b+c) = ab+ac",d,e);
    }


    /**
     * Test iterator.
     */
    public void testIterator() {
        int t = 0;
        BigRational bi = new BigRational();
        bi.setAllIterator();
        BigRational j = null, elem = null;
        for ( BigRational i : bi ) {
            t++;
            //System.out.println("i = " + i);
            if ( t >= 20 ) {
                j = i;
                break;
            }
        }
        assertFalse("j == null ", j == null);
        elem = new BigRational(-2,3);
        assertTrue("j == 10 ", j.equals(elem) );
    }


    /**
     * Test non-negative iterator.
     */
    public void testNNIterator() {
        int t = 0;
        BigRational bi = new BigRational();
        bi.setNonNegativeIterator();
        BigRational j = null, elem = null;
        Iterator<BigRational> iter = bi.iterator();
        while ( iter.hasNext() ) {
            BigRational i = iter.next();
            t++;
            //System.out.println("i = " + i);
            if ( t >= 20 ) {
                j = i;
                break;
            }
        }
        assertFalse("j == null ", j == null);
        elem = new BigRational(4,3);
        assertTrue("j == 10 ", j.equals(elem) );
    }


    /**
     * Test non-negative unique iterator.
     */
    public void testUniqueNNIterator() {
        int t = 0;
        BigRational bi = new BigRational();
        bi.setNonNegativeIterator();
        //bi.setAllIterator();
        BigRational j = null, elem = null;
        Iterator<BigRational> iter = bi.uniqueIterator();
        while ( iter.hasNext() ) {
            BigRational i = iter.next();
            t++;
            //System.out.println("i = " + i);
            if ( t >= 20 ) {
                j = i;
                break;
            }
        }
        assertFalse("j == null ", j == null);
        elem = new BigRational(5,3);
        assertTrue("j == 10 ", j.equals(elem) );
    }

}
