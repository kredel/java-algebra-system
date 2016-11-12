/*
 * $Id$
 */

package edu.jas.arith;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * ModInteger and PrimeList tests with JUnit.
 * @author Heinz Kredel.
 */

public class PrimeTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PrimeTest</CODE> object.
     * @param name String
     */
    public PrimeTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PrimeTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test prime list.
     */
    public void testPrime() {
        PrimeList primes = new PrimeList();
        //System.out.println("primes = " + primes);

        //assertTrue("all primes ", primes.checkPrimes() );

        int i = 0;
        //System.out.println("primes = ");
        for (java.math.BigInteger p : primes) {
            assertFalse("p != null", p == null);
            //System.out.print("" + p);
            if (i++ > 50) {
                break;
            }
            //System.out.print(", ");
        }
        //System.out.println();

        //System.out.println("primes = " + primes);

        assertTrue("all primes ", primes.checkPrimes());
    }


    /**
     * Test Mersenne prime list.
     */
    public void testMersennePrime() {
        PrimeList primes = new PrimeList(PrimeList.Range.mersenne);
        //System.out.println("primes = " + primes);

        //assertTrue("all primes ", primes.checkPrimes() );

        int i = 1;
        //System.out.println("primes = ");
        for (java.math.BigInteger p : primes) {
            assertFalse("p != null", p == null);
            //System.out.println(i + " = " + p);
            if (i++ > 23) {
                break;
            }
            //System.out.print(", ");
        }
        //System.out.println();

        //System.out.println("primes = " + primes);

        assertTrue("all primes ", primes.checkPrimes(15));
    }


    /**
     * Test small prime list.
     */
    public void testSmallPrime() {
        List<Long> sp = PrimeInteger.smallPrimes(1, 500);
        System.out.println("sp = " + sp);
        for (Long p : sp) {
	    java.math.BigInteger P = new java.math.BigInteger(p.toString());
            assertTrue("isPrime: " + p, P.isProbablePrime(16) );
        }
    }


    /**
     * Test factorize integer.
     */
    public void testFactorInteger() {
        SortedMap<Long, Integer> ff;

        ff = PrimeInteger.IFACT(2 * 3 * 5 * 7 * 2 * 9 * 10 * 19 * 811);
        System.out.println("ff = " + ff);
        assertEquals("factors: ", ff.size(), 6 );
        for (Long p : ff.keySet()) {
	    java.math.BigInteger P = new java.math.BigInteger(p.toString());
            assertTrue("isPrime: " + p, P.isProbablePrime(16) );
        }

        ff = PrimeInteger.IFACT(991 * 997 * 811 + 1);
        System.out.println("ff = " + ff);
        assertEquals("factors: ", ff.size(), 3 );
        for (Long p : ff.keySet()) {
	    java.math.BigInteger P = new java.math.BigInteger(p.toString());
            assertTrue("isPrime: " + p, P.isProbablePrime(16) );
        }

        //getLongPrime(15, 135)
        ff = PrimeInteger.IFACT(
                        (new BigInteger(2)).power(29).subtract(BigInteger.valueOf(133)).getVal().intValue());
        System.out.println("ff = " + ff);
        assertEquals("factors: ", ff.size(), 1 );
        for (Long p : ff.keySet()) {
	    java.math.BigInteger P = new java.math.BigInteger(p.toString());
            assertTrue("isPrime: " + p, P.isProbablePrime(16) );
        }

        ff = PrimeInteger.IFACT( (new BigInteger(2)).power(59).subtract(BigInteger.valueOf(55)).getVal().longValue() );
        System.out.println("ff = " + ff);
        assertEquals("factors: ", ff.size(), 1 );
        for (Long p : ff.keySet()) {
	    java.math.BigInteger P = new java.math.BigInteger(p.toString());
            assertTrue("isPrime: " + p, P.isProbablePrime(16) );
        }
    }

}
