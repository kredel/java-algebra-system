/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigQuaternion;


/**
 * BigQuaternion coefficients GenSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class QuatGenSolvablePolynomialTest extends TestCase {

    /**
     * main
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>QuatGenSolvablePolynomialTest</CODE> object.
     * @param name String.
     */
    public QuatGenSolvablePolynomialTest(String name) {
        super(name);
    }

    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(QuatGenSolvablePolynomialTest.class);
        return suite;
    }

    BigQuaternion cfac;
    GenSolvablePolynomialRing<BigQuaternion> fac;

    GenSolvablePolynomial<BigQuaternion> a;
    GenSolvablePolynomial<BigQuaternion> b;
    GenSolvablePolynomial<BigQuaternion> c;
    GenSolvablePolynomial<BigQuaternion> d;
    GenSolvablePolynomial<BigQuaternion> e;

    int rl = 6; 
    int kl = 3;
    int ll = 7;
    int el = 5;
    float q = 0.33f;

    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new BigQuaternion(1);
        fac = new GenSolvablePolynomialRing<BigQuaternion>(cfac,rl);
        WeylRelations<BigQuaternion> rel = new WeylRelations<BigQuaternion>(fac);
        rel.generate();
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        cfac = null;
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
     * Test factory.
     */
    public void testFactory() {
        assertFalse("!is comutative", fac.isCommutative() );
        assertFalse("!is field", fac.isField() );
        assertTrue("is associative", fac.isAssociative() );

        List<GenPolynomial<BigQuaternion>> gens = fac.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens = 4+6 ", gens.size() == 10);
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            //a = fac.random(ll);
            a = fac.random(kl, ll+i, el+(5-i), q );
            assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
            assertTrue(" not isONE( a"+i+" )", !a.isONE() );
        }
    }


    /**
     * Test solvable addition.
     */
    public void testAddition() {
        a = fac.random(kl, ll, el, q ); // fac.random(ll);
        b = fac.random(kl, ll, el, q ); // fac.random(ll);

        c = (GenSolvablePolynomial<BigQuaternion>) a.sum(b);
        d = (GenSolvablePolynomial<BigQuaternion>) c.subtract(b);
        assertEquals("a+b-b = a",a,d);

        c = fac.random(kl, ll, el, q ); //fac.random(ll);

        ExpVector u = ExpVector.EVRAND(rl,el,q);
        BigQuaternion x = BigQuaternion.QRAND(kl);

        b = new GenSolvablePolynomial<BigQuaternion>(fac, x, u);
        c = (GenSolvablePolynomial<BigQuaternion>) a.sum(b);
        d = (GenSolvablePolynomial<BigQuaternion>) a.sum(x,u);
        assertEquals("a+p(x,u) = a+(x,u)",c,d);

        c = (GenSolvablePolynomial<BigQuaternion>) a.subtract(b);
        d = (GenSolvablePolynomial<BigQuaternion>) a.subtract(x,u);
        assertEquals("a-p(x,u) = a-(x,u)",c,d);

        a = new GenSolvablePolynomial<BigQuaternion>(fac);
        assertTrue("a == 0",a.isZERO());
    }


    /**
     * Test solvable multiplication.
     */
    public void testMultiplication() {
        a = fac.random(kl, ll, el, q ); //fac.random(ll);
        assertTrue("not isZERO( a )", !a.isZERO() );

        b = fac.random(kl, ll, el, q ); //fac.random(ll);
        assertTrue("not isZERO( b )", !b.isZERO() );

        c = (GenSolvablePolynomial<BigQuaternion>) b.multiply(a);
        d = (GenSolvablePolynomial<BigQuaternion>) a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO() );
        assertTrue("not isZERO( d )", !d.isZERO() );
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        e = (GenSolvablePolynomial<BigQuaternion>) d.subtract(c);
        assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

        assertTrue("a*b = b*a", !c.equals(d) );
        //assertEquals("a*b = b*a",c,d);

        c = fac.random(kl, ll, el, q ); //fac.random(ll);
        //System.out.println("c = " + c);
        d = (GenSolvablePolynomial<BigQuaternion>) a.multiply( b.multiply(c) );
        e = (GenSolvablePolynomial<BigQuaternion>) (a.multiply(b)).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c",d,e);
        assertTrue("a(bc) = (ab)c", d.equals(e) );

        BigQuaternion x = a.leadingBaseCoefficient().inverse();
        c = (GenSolvablePolynomial<BigQuaternion>) a.monic();
        d = (GenSolvablePolynomial<BigQuaternion>) a.multiply(x);
        assertEquals("a.monic() = a(1/ldcf(a))",c,d);

        BigQuaternion y = b.leadingBaseCoefficient().inverse();
        c = (GenSolvablePolynomial<BigQuaternion>) b.monic();
        d = (GenSolvablePolynomial<BigQuaternion>) b.multiply(y);
        assertEquals("b.monic() = b(1/ldcf(b))",c,d);

        e = new GenSolvablePolynomial<BigQuaternion>(fac,y);
        d = (GenSolvablePolynomial<BigQuaternion>) b.multiply(e);
        assertEquals("b.monic() = b(1/ldcf(b))",c,d);
    }


    /**
     * Test solvable left and right multiplication.
     */
    public void testDoubleMultiplication() {
        a = fac.random(kl, 3, el, q ); //fac.random(ll);
        b = fac.random(kl, 2, el, q ); //fac.random(ll);
        c = fac.random(kl, 3, el, q ); //fac.random(ll);

        d = a.multiply(b).multiply(c);
        e = b.multiply(a,c);
        assertEquals("a b c = b.multiply(a,c)",d,e);

        BigQuaternion qa = cfac.random(kl);
        BigQuaternion qb = cfac.random(kl);
        BigQuaternion qc = qa.multiply(qb);
        BigQuaternion qd = qb.multiply(qa);
        // search non commuting qa, qb
        while ( qc.equals(qd) ) {
             qa = cfac.random(kl);
             qb = cfac.random(kl);
             qc = qa.multiply(qb);
             qd = qb.multiply(qa);
        }
        //System.out.println("qa = " + qa);
        //System.out.println("qb = " + qb);
        //System.out.println("qc = " + qc);
        //System.out.println("qd = " + qd);

        a = fac.univariate(0);
        d = a.multiply(qa,qb);
        e = a.multiply(qb,qa);
        //System.out.println("a = " + a);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertFalse("a.multiply(qa,qb) != a.multiply(qb,qq)", d.equals(e));

        // commuting variables
        ExpVector ea = fac.univariate(1).leadingExpVector();
        ExpVector eb = fac.univariate(2).leadingExpVector();
        //System.out.println("ea = " + ea);
        //System.out.println("eb = " + eb);

        d = a.multiply(ea,eb);
        e = a.multiply(eb,ea);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertTrue("a.multiply(ea,eb) == a.multiply(eb,eq)", d.equals(e));

        d = a.multiply(qa,ea,qb,eb);
        e = a.multiply(qb,eb,qa,ea);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertFalse("a.multiply(qa,ea,qb,eb) != a.multiply(qb,eb,qa,ea)", d.equals(e));
    }

}
