/*
 * $Id$
 */

package edu.jas.ufd;


//import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * GCD Hensel algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDHenselTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDHenselTest</CODE> object.
     * @param name String.
     */
    public GCDHenselTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDHenselTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    GreatestCommonDivisorAbstract<BigInteger> ufd;


    GreatestCommonDivisorAbstract<BigInteger> ufd1;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    //PrimeList primes = new PrimeList();

    BigInteger cofac;


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


    GenPolynomial<BigInteger> ac;


    GenPolynomial<BigInteger> bc;


    GenPolynomial<GenPolynomial<BigInteger>> ar;


    GenPolynomial<GenPolynomial<BigInteger>> br;


    GenPolynomial<GenPolynomial<BigInteger>> cr;


    GenPolynomial<GenPolynomial<BigInteger>> dr;


    GenPolynomial<GenPolynomial<BigInteger>> er;


    GenPolynomial<GenPolynomial<BigInteger>> arc;


    GenPolynomial<GenPolynomial<BigInteger>> brc;


    int rl = 3;


    int kl = 34;


    int ll = 5;


    int el = 4; //3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        cofac = new BigInteger();
        ufd = new GreatestCommonDivisorHensel<ModInteger>();
        dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to);
        cfac = new GenPolynomialRing<BigInteger>(cofac, rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        ufd = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    /**
     * Test univariate Hensel algorithm gcd with subres PRS recursive algorithm.
     */
    public void xtestHenselSubresGcd() {

        GenPolynomial<BigInteger> a;
        GenPolynomial<BigInteger> b;
        GenPolynomial<BigInteger> c;
        GenPolynomial<BigInteger> d;
        GenPolynomial<BigInteger> e;

        GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            a = a.multiply(c); //.multiply(c);
            b = b.multiply(c);

            //System.out.println("a c = " + a);
            //System.out.println("b c = " + b);

            d = ufd.gcd(a, b);
            //d = ufd.baseGcd(a,b);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);
            //System.out.println("e  = " + e);

            assertTrue("c | gcd(ac,bc): " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | a: " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | b: " + e, e.isZERO());
        }
    }


    /**
     * Test univariate linear Hensel algorithm gcd with subres PRS recursive
     * algorithm.
     */
    public void xtestHenselLinearSubresGcd() {

        ufd1 = new GreatestCommonDivisorHensel<ModInteger>(false);

        GenPolynomial<BigInteger> a;
        GenPolynomial<BigInteger> b;
        GenPolynomial<BigInteger> c;
        GenPolynomial<BigInteger> d;
        GenPolynomial<BigInteger> e;

        GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            a = a.multiply(c); //.multiply(c);
            b = b.multiply(c);

            //System.out.println("a c = " + a);
            //System.out.println("b c = " + b);

            long t = System.currentTimeMillis();
            d = ufd1.gcd(a, b);
            t = System.currentTimeMillis() - t;
            //d = ufd.baseGcd(a,b);

            long tq = System.currentTimeMillis();
            //e = ufd.gcd(a,b);
            tq = System.currentTimeMillis() - tq;


            //System.out.println("Hensel quadratic, time = " + tq);
            //System.out.println("Hensel linear, time    = " + t);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);
            //System.out.println("e  = " + e);

            assertTrue("c | gcd(ac,bc): " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | a: " + e, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | b: " + e, e.isZERO());
        }
    }


    /**
     * Test Hensel gcd 2 variables.
     * 
     */
    public void testHenselGCD2() {
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), 2, to , new String[] {"y", "x" });

        for (int i = 0; i < 1; i++) {
            //a = dfac.random(kl, ll, el + i, q);
            //b = dfac.random(kl, ll, el, q);
            //c = dfac.random(kl, ll, el, q);
            a = dfac.parse(" y^2 + 2 x y - 3 y + x^2 - 3 x - 4 ");
            b = dfac.parse(" y^2 + 2 x y + 5 y + x^2 + 5 x + 4 ");
            c = dfac.parse(" x + y + 1 ");
            System.out.println("a = " + a);
            System.out.println("b = " + b);
            System.out.println("c = " + c);

            if (a.isZERO() || b.isZERO() ) { //|| c.isZERO()) {
                // skip for this turn
                continue;
            }
            //assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
            //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
            //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

            //a = a.multiply(c);
            //b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            d = ufd.gcd(a, b);
            System.out.println("d = " + d);

            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            System.out.println("e = " + e);

            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());
        }
    }

}
