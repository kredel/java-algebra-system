/*
 * $Id$
 */

package edu.jas.ufd;


//import java.util.Map;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.poly.ExpVector;
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
        BasicConfigurator.configure();
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


    GreatestCommonDivisorAbstract<BigInteger> ufd;


    GreatestCommonDivisorAbstract<BigInteger> ufd1;


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


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
    String [] vars = { "x", "y", "z" }; 


    int kl = 4;


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
        dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to, vars);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        ufd = null;
        dfac = null;
    }


    /**
     * Test Hensel algorithm gcd.
     */
    public void testHenselGcd() {
        //GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to);
        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);
            ExpVector ev = c.leadingExpVector();
            if ( ev != null ) {
                c.doPutToMap(ev,dfac.coFac.getONE());
            }

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
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
            assertTrue("c | gcd(ac,bc): " + d + ", c = " + c, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | a: " + e + ", d = " + d, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | b: " + e + ", d = " + d, e.isZERO());
        }
    }


    /**
     * Test linear Hensel algorithm gcd.
     */
    public void ytestHenselLinearSubresGcd() {
        ufd1 = new GreatestCommonDivisorHensel<ModInteger>(false);
        //GenPolynomialRing<BigInteger> dfac = new GenPolynomialRing<BigInteger>(cofac, rl, to);

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll + i, el + i, q);
            b = dfac.random(kl, ll + i, el + i, q);
            c = dfac.random(kl, ll + i, el + i, q);
            c = c.multiply(dfac.univariate(0));
            //a = ufd.basePrimitivePart(a);
            //b = ufd.basePrimitivePart(b);
            ExpVector ev = c.leadingExpVector();
            if ( ev != null ) {
                c.doPutToMap(ev,dfac.coFac.getONE());
            }

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }
            assertTrue("length( c" + i + " ) <> 0", c.length() > 0);
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
            e = ufd.gcd(a,b);
            tq = System.currentTimeMillis() - tq;
            //System.out.println("Hensel quadratic, time = " + tq);
            //System.out.println("Hensel linear, time    = " + t);

            c = ufd.basePrimitivePart(c).abs();
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("c  = " + c);
            //System.out.println("d  = " + d);
            //System.out.println("e  = " + e);
            assertTrue("c | gcd(ac,bc): " + d + ", c = " + c, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(a, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | a: " + e + ", d = " + d, e.isZERO());

            e = PolyUtil.<BigInteger> basePseudoRemainder(b, d);
            //System.out.println("e  = " + e);
            assertTrue("gcd(a,b) | b: " + e + ", d = " + d, e.isZERO());
        }
    }


    /**
     * Test Hensel gcd 3 variables.
     */
    public void testHenselGCD3() {
        BigInteger ifa = new BigInteger(1);
        //dfac = new GenPolynomialRing<BigInteger>(ifa, 2, to , new String[] {"x", "y" });
        dfac = new GenPolynomialRing<BigInteger>(ifa, 3, to , new String[] { "x" , "y", "z" });

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el + i, q);
            b = dfac.random(kl, ll, el, q);
            c = dfac.random(kl, ll, el, q);
            // make monic and c with univariate head term
            ExpVector ev = a.leadingExpVector();
            if ( ev != null ) {
                a.doPutToMap(ev,ifa.getONE());
            }
            ev = b.leadingExpVector();
            if ( ev != null ) {
                b.doPutToMap(ev,ifa.getONE());
            }
            ev = c.leadingExpVector();
            if ( ev != null ) {
                //c.doPutToMap(ev,ifa.getONE());
            }
            assertFalse("ev == null ", ev == null);
            if ( ev.dependencyOnVariables().length > 1 ) {
                c = dfac.univariate(1); //getONE();
            }
            //a = dfac.parse(" y^2 + 2 x y - 3 y + x^2 - 3 x - 4 ");
            //b = dfac.parse(" y^2 + 2 x y + 5 y + x^2 + 5 x + 4 ");
            //a = dfac.parse(" x + 2 y + z^2 + 5 ");
            //b = dfac.parse(" x - y - 3 + y z ");
            //c = dfac.parse(" x y + z^2 + y ");
            //a = dfac.parse("7 y^4 - (35 x^3 - 32) y^3 - 160 x^3 y^2 + (105 x^2 - 119) y + (480 x^2 - 544) ");
            //b = dfac.parse(" 7 y^4 + 39 y^3 + 32 y^2 ");
            //c = dfac.parse(" 7 y + 32 ");

            //a = dfac.parse(" ( -13 x^2 ) z^5 + ( 182 x^2 - 143  ) z^3 - ( x^2 * y^3 - 8 x^2 ) z^2 + ( 14 x^2 * y^3 - 11 y^3 - 112 x^2 + 88  ) ");
            //b = dfac.parse(" ( -13 x^3 * y^3 ) z^6 + ( 65 y ) z^4 - ( x^3 * y^6 - 8 x^3 * y^3 - 52 y^3 - 195 y + 156  ) z^3 + ( 5 y^4 - 40 y ) z + ( 4 y^6 + 15 y^4 - 44 y^3 - 120 y + 96  ) ) ");
            //c = dfac.parse(" 13 z^3 + y^3 - 8 ");

            //a = dfac.parse(" 3 z^3 + ( 4 y^2 + 25 x^3 + 16 x^2 + 25  ) z^2 - ( 84 y^4 - 22 x^3 * y^2 + 128 x^2 * y^2 + 138 y^2 - 52 x^6 - 71 x^5 + 35 x^4 - 109 x^3 + 59 x^2 + 18  ) z ");
            //b = dfac.parse(" 10 z^5 + ( 60 y^2 + 40 x^3 + 70 x^2 + 90  ) z^4 + ( 14 x + 3  ) z^2 + ( 84 x * y^2 + 18 y^2 + 56 x^4 + 110 x^3 + 21 x^2 + 126 x + 27  ) z ");
            //c = dfac.parse("z^2 + ( 6 y^2 + 4 x^3 + 7 x^2 + 9  ) z");

            //a = a.abs();
            //b = b.abs();
            //c = c.abs();
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }

            a = a.multiply(c);
            b = b.multiply(c);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            d = ufd.gcd(a, b);
            e = PolyUtil.<BigInteger> basePseudoRemainder(d, c);
            //System.out.println("e = " + e);
            //System.out.println("d = " + d);
            //System.out.println("c = " + c);
            assertTrue("c | gcd(ac,bc) " + e + ", d = " + d, e.isZERO());
        }
    }

}
