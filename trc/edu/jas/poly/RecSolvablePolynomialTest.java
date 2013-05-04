/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;


/**
 * BigRational coefficients RecSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class RecSolvablePolynomialTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>RecSolvablePolynomialTest</CODE> object.
     * @param name String.
     */
    public RecSolvablePolynomialTest(String name) {
        super(name);
    }

    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(RecSolvablePolynomialTest.class);
        return suite;
    }


    RecSolvablePolynomial<BigRational> a, b, c, d, e, f, x1, x2;

    int rl = 4; 
    int kl = 5;
    int ll = 4;
    int el = 3;
    float q = 0.3f;

    String[] cvars = new String[] { "a", "b" };
    String[] vars = new String[] { "w", "x", "y", "z" };

    RelationTable<GenPolynomial<BigRational>> table;
    RecSolvablePolynomialRing<BigRational> ring;
    BigRational cfac;
    GenSolvablePolynomialRing<GenPolynomial<BigRational>> sring;
    GenPolynomialRing<BigRational> cring;
    TermOrder tord = new TermOrder(TermOrder.INVLEX);

    protected void setUp() {
        cfac = new BigRational(1);
        cring = new GenPolynomialRing<BigRational>(cfac,tord,cvars);
        //sring = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(cring,rl,tord);
        ring = new RecSolvablePolynomialRing<BigRational>(cring,tord,vars);
        WeylRelations<GenPolynomial<BigRational>> wl = new WeylRelations<GenPolynomial<BigRational>>(ring);
        wl.generate();
        table = ring.table;
        a = b = c = d = e = null;
    }

    protected void tearDown() {
        table = null;
        ring = null;
        a = b = c = d = e = null;
    }


    /**
     * Test constructor, generators and properties.
     */
    public void testConstructor() {
        assertFalse("not commutative", ring.isCommutative() );
        assertTrue("associative", ring.isAssociative() );

        a = new RecSolvablePolynomial<BigRational>(ring);
        assertTrue("length( a ) = 0", a.length() == 0);
        assertTrue("isZERO( a )", a.isZERO() );
        assertTrue("isONE( a )", !a.isONE() );

        c = ring.getONE();
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO() );
        assertTrue("isONE( c )", c.isONE() );

        d = ring.getZERO();
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO() );
        assertTrue("isONE( d )", !d.isONE() );
        //System.out.println("d = " + d);

        //System.out.println("");
        for (GenPolynomial<GenPolynomial<BigRational>>  g : ring.generators() ) {
            //System.out.print("g = " + g + ", ");
            assertFalse("not isZERO( g )", g.isZERO() );
        }
        //System.out.println("");
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            // a = ring.random(ll+2*i);
            a = ring.random(kl*(i+1), ll+2*i, el+i, q );
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
        a = ring.random(kl, ll, el, q );
        c = (RecSolvablePolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO() );

        b = (RecSolvablePolynomial<BigRational>)a.sum(a);
        c = (RecSolvablePolynomial<BigRational>)b.subtract(a);
        assertEquals("a+a-a = a",c,a);

        b = ring.random(kl, ll, el, q );
        c = (RecSolvablePolynomial<BigRational>)b.sum(a);
        d = (RecSolvablePolynomial<BigRational>)a.sum(b);
        assertEquals("a+b = b+a",c,d);

        c = ring.random(kl, ll, el, q );
        d = (RecSolvablePolynomial<BigRational>)a.sum(b.sum(c));
        e = (RecSolvablePolynomial<BigRational>)a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c",d,e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.EVRAND(rl,el,q);
        GenPolynomial<BigRational> x = cring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (RecSolvablePolynomial<BigRational>) a.sum(b);
        d = (RecSolvablePolynomial<BigRational>) a.sum(x,u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)",c,d);

        c = (RecSolvablePolynomial<BigRational>)a.subtract(b);
        d = (RecSolvablePolynomial<BigRational>)a.subtract(x,u);
        assertEquals("a-p(x,u) = a-(x,u)",c,d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (RecSolvablePolynomial<BigRational>)b.sum(a);
        d = (RecSolvablePolynomial<BigRational>)a.sum(x,u);
        assertEquals("a+p(x,u) = a+(x,u)",c,d);

        c = (RecSolvablePolynomial<BigRational>)a.subtract(b);
        d = (RecSolvablePolynomial<BigRational>)a.subtract(x,u);
        assertEquals("a-p(x,u) = a-(x,u)",c,d);
    }


    /**
     * Test object multiplication.
     */
    public void testMultiplication() {
        //System.out.println("ring = " + ring);
        a = ring.random(kl, ll, el, q );
        //a = ring.parse(" b y z + a w z ");  
        b = ring.random(kl, ll, el, q );
        //b = ring.parse(" w x - b x "); 

        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("a*b != b*a", ! c.equals(d) && true );

        c = ring.random(kl, ll, el, q );
        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        assertEquals("a(bc) = (ab)c",d,e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        BigRational x = a.leadingBaseCoefficient().leadingBaseCoefficient().inverse();
        GenPolynomial<BigRational> xp = new GenPolynomial<BigRational>(cring,x);
        d = (RecSolvablePolynomial<BigRational>) a.multiply(xp);
        assertTrue("monic(a) = a*(1/ldcf(ldcf(a)))",d.leadingBaseCoefficient().leadingBaseCoefficient().isONE());
    }


    /**
     * Test commutative ring.
     */
    public void testCommutative() {
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("table = " + table.toScript());
        //System.out.println("ring = " + ring);
        //System.out.println("ring.table = " + ring.table.toScript());
        //assertEquals("table == ring.table: ", table, ring.table); // ?
        assertTrue("# relations == 2", ring.table.size() == 2);

        ring = new RecSolvablePolynomialRing<BigRational>(cring,ring);
        table = ring.table;
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("ring = " + ring);

        assertTrue("isCommutative()",ring.isCommutative());
        assertTrue("isAssociative()",ring.isAssociative());

        a = ring.random(kl, ll, el, q );
        //a = ring.parse(" b x y z + a w z ");
        //System.out.println("a = " + a);
        b = ring.random(kl, ll, el, q );
        //b = ring.parse(" w y z - b x ");
        //System.out.println("b = " + b);

        // commutative
        c = b.multiply(a);
        //System.out.println("c = " + c);
        d = a.multiply(b);
        //d = ring.getONE(); 
        //System.out.println("d = " + d);
        assertEquals("ba == ab: ", c, d);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = ring.random(kl,ll,el,q);
        b = ring.random(kl,ll,el,q);
        c = ring.random(kl,ll,el,q);

        d = a.multiply( (RecSolvablePolynomial<BigRational>) b.sum(c) );
        e = (RecSolvablePolynomial<BigRational>) a.multiply(b).sum( a.multiply(c) );
        assertEquals("a(b+c) = ab+ac",d,e);
    }


    /**
     * Test solvable coefficient ring.
     */
    public void testSolvableCoeffs() {
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("table = " + table.toScript());
        //System.out.println("ring = " + ring);
        //System.out.println("ring.table = " + ring.table.toScript());
        //assertEquals("table == ring.table: ", table, ring.table); // ?

        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,tord,cvars);
        WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(csring);
        wlc.generate();
        assertTrue("# relations == 1", csring.table.size() == 1);
        assertFalse("isCommutative()",csring.isCommutative());
        assertTrue("isAssociative()",csring.isAssociative());
        
        ring = new RecSolvablePolynomialRing<BigRational>(csring,ring);
        WeylRelations<GenPolynomial<BigRational>> wl = new WeylRelations<GenPolynomial<BigRational>>(ring);
        wl.generate();
        assertTrue("# relations == 2", ring.table.size() == 2);
        assertFalse("isCommutative()",ring.isCommutative());
        assertTrue("isAssociative()",ring.isAssociative());

        RecSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.parse("b x - a");
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        ring.coeffTable.update(r1.leadingExpVector(),r2.leadingExpVector(),rp);

        table = ring.table;
        //System.out.println("table = " + ring.table.toString(vars));
        //System.out.println("coeffTable = " + ring.coeffTable.toString(vars));
        System.out.println("ring = " + ring);

        assertFalse("isCommutative()",ring.isCommutative());
        assertTrue("isAssociative()",ring.isAssociative());

        List<GenPolynomial<GenPolynomial<BigRational>>> gens = ring.generators();
        for (GenPolynomial<GenPolynomial<BigRational>> x : gens) {
            GenSolvablePolynomial<GenPolynomial<BigRational>> 
               xx = (GenSolvablePolynomial<GenPolynomial<BigRational>>) x;
            a = new RecSolvablePolynomial<BigRational>(ring,xx);
            //System.out.println("a = " + a);
            for (GenPolynomial<GenPolynomial<BigRational>> y : gens) {
                 GenSolvablePolynomial<GenPolynomial<BigRational>> 
                    yy = (GenSolvablePolynomial<GenPolynomial<BigRational>>) y;
                 b = new RecSolvablePolynomial<BigRational>(ring,yy);
                 //System.out.println("b = " + b);
                 c = a.multiply(b);
                 //System.out.println("gens:" + a + " * " + b + " = " + c);
            }
        }

        a = ring.random(kl, ll, el, q );
        //a = ring.getONE();
        //System.out.println("a = " + a);
        b = ring.random(kl, ll, el, q );
        //b = ring.getONE();
        //System.out.println("b = " + b);

        // non-commutative
        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("ba != ab: ", !c.equals(d));
    }

}
