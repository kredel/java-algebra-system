/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.Roots;


/**
 * BigDecimal coefficients GenPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class DecGenPolynomialTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>RatGenPolynomialTest</CODE> object.
     * @param name String.
     */
    public DecGenPolynomialTest(String name) {
        super(name);
    }

    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(DecGenPolynomialTest.class);
        return suite;
    }

    GenPolynomialRing<BigDecimal> fac;

    GenPolynomial<BigDecimal> a, b, c, d, e;

    int rl = 7; 
    int kl = 10;
    int ll = 10;
    int el = 5;
    float q = 0.5f;

    protected void setUp() {
        a = b = c = d = e = null;
        fac = new GenPolynomialRing<BigDecimal>(new BigDecimal(1),rl);
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }

    
    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = fac.getONE();
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO() );
        assertTrue("isONE( c )", c.isONE() );

        d = fac.getZERO();
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO() );
        assertTrue("isONE( d )", !d.isONE() );
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            //a = fac.random(ll);
            a = fac.random(kl*(i+1), ll+2*i, el+i, q );
            //System.out.println("a = " + a);

            assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
            assertTrue(" not isONE( a"+i+" )", !a.isONE() );
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(ll);
        b = fac.random(ll);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a",a,d);

        c = fac.random(ll);

        ExpVector u = ExpVector.random(rl,el,q);
        BigDecimal x = fac.coFac.random(kl);

        b = new GenPolynomial<BigDecimal>(fac,x, u);
        c = a.sum(b);
        d = a.sum(x,u);
        assertEquals("a+p(x,u) = a+(x,u)",c,d);

        c = a.subtract(b);
        d = a.subtract(x,u);
        assertEquals("a-p(x,u) = a-(x,u)",c,d);

        a = new GenPolynomial<BigDecimal>(fac);
        b = new GenPolynomial<BigDecimal>(fac,x, u);
        c = b.sum(a);
        d = a.sum(x,u);
        assertEquals("a+p(x,u) = a+(x,u)",c,d);

        c = a.subtract(b);
        d = a.subtract(x,u);
        assertEquals("a-p(x,u) = a-(x,u)",c,d);
    }


    /**
     * Test object multiplication.
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

        BigDecimal x = a.leadingBaseCoefficient().inverse();
        c = a.monic();
        d = a.multiply(x);
        assertEquals("a.monic() = a(1/ldcf(a))",c,d);

        BigDecimal y = b.leadingBaseCoefficient().inverse();
        c = b.monic();
        d = b.multiply(y);
        assertEquals("b.monic() = b(1/ldcf(b))",c,d);

        e = new GenPolynomial<BigDecimal>(fac,y);
        d = b.multiply(e);
        assertEquals("b.monic() = b(1/ldcf(b))",c,d);

        d = e.multiply(b);
        assertEquals("b.monic() = (1/ldcf(b) (0))*b",c,d);
    }


    /**
     * Test BLAS level 1.
     */
    public void testBLAS1() {
        a = fac.random(kl,ll,el,q);
        b = fac.random(kl,ll,el,q);
        c = fac.random(kl,3,el*el,q);
        ExpVector ev = ExpVector.random(rl,el,q);
        BigDecimal lc = fac.coFac.random(kl);

        d = a.subtractMultiple(lc,b);
        e = a.subtract( b.multiply(lc) );
        assertEquals("a - (lc) b == a - ((lc) b)",d,e);

        d = a.subtractMultiple(lc,ev,b);
        e = a.subtract( b.multiply(lc,ev) );
        assertEquals("a - (lc ev) b == a - ((lc ev) b)",d,e);

        ExpVector fv = ExpVector.random(rl,el,q);
        BigDecimal tc = fac.coFac.random(kl);

        d = a.scaleSubtractMultiple(tc,lc,ev,b);
        e = a.multiply(tc).subtract( b.multiply(lc,ev) );
        assertEquals("(tc) a - (lc ev) b == ((tc) a - ((lc ev) b))",d,e);

        d = a.scaleSubtractMultiple(tc,fv,lc,ev,b);
        e = a.multiply(tc,fv).subtract( b.multiply(lc,ev) );
        assertEquals("(tc fv) a - (lc ev) b == ((tc fv) a - ((lc ev) b))",d,e);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = fac.random(kl,ll,el,q);
        b = fac.random(kl,ll,el,q);
        c = fac.random(kl,ll,el,q);

        d = a.multiply( b.sum(c) );
        e = a.multiply( b ).sum( a.multiply(c) );

        assertEquals("a(b+c) == ab+ac",d,e);
    }


    /*
     * Test object quotient and remainder.
     * Not meaningful.
     */

    /**
     * Test addition speed.
     */
    public void testAdditionSpeed() {
        int ll = 100;
        long t = 1000;
        boolean print = false;
        int jit = 1;
        for (int j = 1; j < 5; j++) {
            for (int i = 1; i < 5; i++) {
                a = fac.random(kl, i*ll, el, q);
                b = fac.random(kl, ll,   el, q);
                for (int k = 0; k < jit; k++) {
                    long t1 = System.nanoTime();
                    c = a.sum(b);
                    t1 = System.nanoTime() - t1;
                    assertTrue("c != 0", !c.isZERO() );

                    long t2 = System.nanoTime();
                    d = b.sum(a);
                    t2 = System.nanoTime() - t2;
                    assertTrue("d != 0", !d.isZERO() );
                    if (print) {
                        System.out.print("#a = " + a.length() + ", #b = " + b.length() );
                        System.out.println(",\t t1 = " + (t1/t) + ", t2 = " + (t2/t) );
                    }
                    //assertTrue("t2 <= t1", ((t1/t) >= (t2/t)) );
                }
                if (print) System.out.println();
                assertEquals("c == d", c, d );
            }
            ll = 3 * ll;
        }
    }

    
    /**
     * Test absolute norm.
     */
    public void testAbsNorm() {
        BigDecimal r;
        a = fac.getONE().negate();
        //System.out.println("a = " + a);

        r = PolyUtil.<BigDecimal> absNorm(a);
        //System.out.println("r = " + r);
        assertTrue("isONE( absNorm(-1) )", r.isONE() );

        a = fac.random(kl*2, ll+2, el, q );
        //System.out.println("a = " + a);

        r = PolyUtil.<BigDecimal> absNorm(a);
        //System.out.println("r = " + r);
        assertTrue(" not isZERO( absNorm(a) )", !r.isZERO() || a.isZERO() );
    }

}
