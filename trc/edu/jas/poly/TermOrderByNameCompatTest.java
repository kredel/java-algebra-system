/*
 * $Id$
 */

package edu.jas.poly;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigInteger;


/**
 * TermOrderByName compatibility tests with JUnit. Tests different TermOrders.
 * @author Axel Kramer
 */

public class TermOrderByNameCompatTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>TermOrderByNameCompatTest</CODE> object.
     * @param name String.
     */
    public TermOrderByNameCompatTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TermOrderByNameCompatTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test order Lexicographic.
     */
    public void testTermOrderLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.Lexicographic, v);
        // test order Lexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // -10*x^5*y^4*z^2,7*x^2*y^5*z^3,-10*x^2*y*z^5,-7*x*y^5*z^4,6*x*y^4*z^3,6*x*y^3*z^3,3*x*y^2*z,y^4*z,-7*y^2*z,2*z^5 
        assertEquals("( -10 ) x^5 * y^4 * z^2 + 7 x^2 * y^5 * z^3 - 10 x^2 * y * z^5 - 7 x * y^5 * z^4 + 6 x * y^4 * z^3 + 6 x * y^3 * z^3 + 3 x * y^2 * z + y^4 * z - 7 y^2 * z + 2 z^5",
                        p.toString());
    }


    /**
     * Test order NegativeLexicographic.
     */
    public void testTermOrderNegativeLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.NegativeLexicographic, v);
        // test order NegativeLexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // 2*z^5,-7*y^2*z,y^4*z,3*x*y^2*z,6*x*y^3*z^3,6*x*y^4*z^3,-7*x*y^5*z^4,-10*x^2*y*z^5,7*x^2*y^5*z^3,-10*x^5*y^4*z^2
        assertEquals("2 z^5 - 7 y^2 * z + y^4 * z + 3 x * y^2 * z + 6 x * y^3 * z^3 + 6 x * y^4 * z^3 - 7 x * y^5 * z^4 - 10 x^2 * y * z^5 + 7 x^2 * y^5 * z^3 - 10 x^5 * y^4 * z^2",
                        p.toString());

    }


    /**
     * Test order DegreeLexicographic.
     */
    public void testTermOrderDegreeLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";
        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.DegreeLexicographic, v);
        // test order DegreeLexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // -10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^2*y*z^5,6*x*y^4*z^3,6*x*y^3*z^3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z
        assertEquals("( -10 ) x^5 * y^4 * z^2 + 7 x^2 * y^5 * z^3 - 7 x * y^5 * z^4 - 10 x^2 * y * z^5 + 6 x * y^4 * z^3 + 6 x * y^3 * z^3 + y^4 * z + 2 z^5 + 3 x * y^2 * z - 7 y^2 * z",
                        p.toString());

    }


    public void testTermOrderNegativeDegreeReverseLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.NegativeDegreeReverseLexicographic, v);
        // test order NegativeDegreeReverseLexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // -7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,6*x*y^4*z^3,-10*x^2*y*z^5,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^5*y^4*z^2
        assertEquals("( -7 ) y^2 * z + 3 x * y^2 * z + y^4 * z + 2 z^5 + 6 x * y^3 * z^3 + 6 x * y^4 * z^3 - 10 x^2 * y * z^5 + 7 x^2 * y^5 * z^3 - 7 x * y^5 * z^4 - 10 x^5 * y^4 * z^2",
                        p.toString());
    }


    /**
     * Test order DegreeReverseLexicographic.
     */
    public void testTermOrderDegreeReverseLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.DegreeReverseLexicographic, v);
        //System.out.println("pf = " + pf);
        // test order DegreeReverseLexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // -10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,6*x*y^4*z^3,-10*x^2*y*z^5,6*x*y^3*z^3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z
        //degrees       11,           10,          10,          8,            8,          7,    5,    5,        4,       3
        assertEquals("( -10 ) x^5 * y^4 * z^2 + 7 x^2 * y^5 * z^3 - 7 x * y^5 * z^4 + 6 x * y^4 * z^3 - 10 x^2 * y * z^5 + 6 x * y^3 * z^3 + y^4 * z + 2 z^5 + 3 x * y^2 * z - 7 y^2 * z",
                        p.toString());
    }


    /**
     * Test order NegativeDegreeLexicographic.
     */
    public void testTermOrderNegativeDegreeLexicographic() {
        // integers
        BigInteger rf = new BigInteger();
        String[] v = new String[] { "x", "y", "z" };

        String testPolynomial = "-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 + 6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5";

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf,
                        TermOrderByName.NegativeDegreeLexicographic, v);
        //System.out.println("pf = " + pf);
        // test order NegativeDegreeLexicographic of polynomial:
        GenPolynomial<BigInteger> p = pf.parse(testPolynomial);

        // -7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,-10*x^2*y*z^5,6*x*y^4*z^3,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^5*y^4*z^2
        //degrees 3,        4,    5,    5,          7,            8,          8,           10           10,             11  
        assertEquals("( -7 ) y^2 * z + 3 x * y^2 * z + y^4 * z + 2 z^5 + 6 x * y^3 * z^3 - 10 x^2 * y * z^5 + 6 x * y^4 * z^3 + 7 x^2 * y^5 * z^3 - 7 x * y^5 * z^4 - 10 x^5 * y^4 * z^2",
                        p.toString());
    }

}
