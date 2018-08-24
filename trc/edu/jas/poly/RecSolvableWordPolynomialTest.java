/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.arith.BigRational;


/**
 * BigRational coefficients RecSolvableWordPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class RecSolvableWordPolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RecSolvableWordPolynomialTest</CODE> object.
     * @param name String.
     */
    public RecSolvableWordPolynomialTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RecSolvableWordPolynomialTest.class);
        return suite;
    }


    RecSolvableWordPolynomial<BigRational> a, b, c, d, e, f, x1, x2;


    int rl = 4;


    int kl = 5;


    int ll = 4;


    int el = 3;


    float q = 0.3f;


    String[] cvars = new String[] { "a", "b" };


    String[] vars = new String[] { "w", "x", "y", "z" };


    RelationTable<GenWordPolynomial<BigRational>> table;


    RecSolvableWordPolynomialRing<BigRational> ring;


    BigRational cfac;


    GenSolvablePolynomialRing<GenWordPolynomial<BigRational>> sring;


    GenWordPolynomialRing<BigRational> cring;


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        cring = new GenWordPolynomialRing<BigRational>(cfac, cvars);
        //sring = new GenSolvablePolynomialRing<GenWordPolynomial<BigRational>>(cring,rl,tord);
        ring = new RecSolvableWordPolynomialRing<BigRational>(cring, tord, vars);
        //System.out.println("ring = " + ring.toScript());
        RelationGenerator<GenWordPolynomial<BigRational>> wl = new WeylRelations<GenWordPolynomial<BigRational>>();
        ring.addRelations(wl);
        table = ring.table;
        //System.out.println("ring = " + ring.toScript());
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        table = null;
        ring = null;
        a = b = c = d = e = null;
    }


    /**
     * Test constructor, generators and properties.
     */
    public void testConstructor() {
        assertFalse("not commutative", ring.isCommutative());
        assertTrue("associative", ring.isAssociative());

        a = new RecSolvableWordPolynomial<BigRational>(ring);
        assertTrue("length( a ) = 0", a.length() == 0);
        assertTrue("isZERO( a )", a.isZERO());
        assertTrue("isONE( a )", !a.isONE());

        c = ring.getONE();
        assertTrue("length( c ) = 1", c.length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = ring.getZERO();
        assertTrue("length( d ) = 0", d.length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
        //System.out.println("d = " + d);

        //System.out.println("");
        for (GenPolynomial<GenWordPolynomial<BigRational>> g : ring.generators()) {
            //System.out.print("g = " + g + ", ");
            assertFalse("not isZERO( g )", g.isZERO());
        }
        //System.out.println("");
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            // a = ring.random(ll+2*i);
            a = ring.random(kl * (i + 1), ll + 2 * i, el + i, q);
            //System.out.println("a = " + a);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = ring.random(kl, ll, el, q);
        c = (RecSolvableWordPolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (RecSolvableWordPolynomial<BigRational>) a.sum(a);
        c = (RecSolvableWordPolynomial<BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (RecSolvableWordPolynomial<BigRational>) b.sum(a);
        d = (RecSolvableWordPolynomial<BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = ring.random(kl, ll, el, q);
        d = (RecSolvableWordPolynomial<BigRational>) a.sum(b.sum(c));
        e = (RecSolvableWordPolynomial<BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.random(rl, el, q);
        GenWordPolynomial<BigRational> x = cring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (RecSolvableWordPolynomial<BigRational>) a.sum(b);
        d = (RecSolvableWordPolynomial<BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (RecSolvableWordPolynomial<BigRational>) a.subtract(b);
        d = (RecSolvableWordPolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (RecSolvableWordPolynomial<BigRational>) b.sum(a);
        d = (RecSolvableWordPolynomial<BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (RecSolvableWordPolynomial<BigRational>) a.subtract(b);
        d = (RecSolvableWordPolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        //System.out.println("ring = " + ring);
        a = ring.random(kl, ll, el, q);
        //a = ring.parse(" b y z + a w z ");  
        b = ring.random(kl, ll, el, q);
        //b = ring.parse(" w x - b x "); 

        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        c = ring.random(kl, ll, el, q);
        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        assertEquals("a(bc) = (ab)c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        BigRational x = a.leadingBaseCoefficient().leadingBaseCoefficient().inverse();
        GenWordPolynomial<BigRational> xp = new GenWordPolynomial<BigRational>(cring, x);
        d = (RecSolvableWordPolynomial<BigRational>) a.multiply(xp);
        assertTrue("monic(a) = a*(1/ldcf(ldcf(a)))", d.leadingBaseCoefficient().leadingBaseCoefficient()
                        .isONE());

        d = (RecSolvableWordPolynomial<BigRational>) a.monic();
        assertTrue("a.monic(): " + d.leadingBaseCoefficient() + ", " + a.leadingBaseCoefficient(),
                   d.leadingBaseCoefficient().isONE()
                   || d.leadingBaseCoefficient().equals(a.leadingBaseCoefficient().abs()));
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

        cring = new GenWordPolynomialRing<BigRational>(cfac, new String[] { "a" });
        ring = new RecSolvableWordPolynomialRing<BigRational>(cring, ring);
        //table = ring.table;
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("ring = " + ring.toScript());

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        a = ring.random(kl, ll, el, q);
        //a = ring.parse(" b x y z + a w z ");
        //System.out.println("a = " + a);
        b = ring.random(kl, ll, el, q);
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
        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);

        d = a.multiply((RecSolvableWordPolynomial<BigRational>) b.sum(c));
        e = (RecSolvableWordPolynomial<BigRational>) a.multiply(b).sum(a.multiply(c));
        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test word coefficient ring.
     */
    public void testWordCoeffs() {
        RecSolvableWordPolynomial<BigRational> r1 = ring.parse("x");
        GenWordPolynomial<BigRational> r2 = ring.parse("b").leadingBaseCoefficient();
        RecSolvableWordPolynomial<BigRational> rp = ring.parse("b x + a");
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingWord().leadingExpVector(), rp);

        //table = ring.table;
        //System.out.println("ring = " + ring.toScript());

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<GenWordPolynomial<BigRational>>> gens = ring.generators();
        for (GenPolynomial<GenWordPolynomial<BigRational>> x : gens) {
            GenSolvablePolynomial<GenWordPolynomial<BigRational>> xx = (GenSolvablePolynomial<GenWordPolynomial<BigRational>>) x;
            a = new RecSolvableWordPolynomial<BigRational>(ring, xx);
            //System.out.println("a = " + a);
            for (GenPolynomial<GenWordPolynomial<BigRational>> y : gens) {
                GenSolvablePolynomial<GenWordPolynomial<BigRational>> yy = (GenSolvablePolynomial<GenWordPolynomial<BigRational>>) y;
                b = new RecSolvableWordPolynomial<BigRational>(ring, yy);
                //System.out.println("b = " + b);
                c = a.multiply(b);
                //System.out.println("gens:" + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
                Word wv = a.leadingBaseCoefficient().leadingWord()
                                .multiply(b.leadingBaseCoefficient().leadingWord());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingBaseCoefficient().leadingWord().equals(wv));
            }
        }

        a = ring.random(kl, ll, el, q);
        //a = ring.getONE();
        //System.out.println("a = " + a);
        b = ring.random(kl, ll, el, q);
        //b = ring.getONE();
        //System.out.println("b = " + b);

        // non-commutative
        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        // relation table list tests
        //System.out.println("ring.table.rels = " + ring.table.relationList());
        //System.out.println("ring.coeffTable.rels = " + ring.coeffTable.relationList());

        RecSolvableWordPolynomialRing<BigRational> ring2 = new RecSolvableWordPolynomialRing<BigRational>(
                        ring.coFac, ring);
        ring2.table.addSolvRelations(ring.table.relationList());
        ring2.coeffTable.addSolvRelations(ring.coeffTable.relationList());

        //System.out.println("ring2.table.rels = " + ring2.table.relationList());
        //System.out.println("ring2.coeffTable.rels = " + ring2.coeffTable.relationList());
        assertEquals("ring.table == ring2.table: ", ring.table, ring2.table);
        assertEquals("ring.coeffTable == ring2.coeffTable: ", ring.coeffTable, ring2.coeffTable);
    }

}
