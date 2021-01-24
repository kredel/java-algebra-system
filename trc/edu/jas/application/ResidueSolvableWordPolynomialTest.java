/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenWordPolynomial;
import edu.jas.poly.GenWordPolynomialRing;
import edu.jas.poly.RecSolvableWordPolynomial;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;


/**
 * BigRational coefficients ResidueSolvableWordPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class ResidueSolvableWordPolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ResidueSolvableWordPolynomialTest</CODE> object.
     * @param name String.
     */
    public ResidueSolvableWordPolynomialTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ResidueSolvableWordPolynomialTest.class);
        return suite;
    }


    ResidueSolvableWordPolynomial<BigRational> a, b, c, d, e, f, x1, x2;


    int rl = 4;


    int kl = 2;


    int ll = 3;


    int el = 2;


    float q = 0.3f;


    String[] cvars = new String[] { "a", "b" };


    String[] vars = new String[] { "w", "x", "y", "z" };


    WordResidueRing<BigRational> rring;


    WordIdeal<BigRational> wideal;


    ResidueSolvableWordPolynomialRing<BigRational> ring;


    BigRational cfac;


    GenWordPolynomialRing<BigRational> wring;


    GenPolynomialRing<BigRational> cring;


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        wring = new GenWordPolynomialRing<BigRational>(cfac, cvars);
        //RelationGenerator<BigRational> wc = new WeylRelations<BigRational>();
        //not possible: wring.addRelations(wc); 
        List<GenWordPolynomial<BigRational>> il = new ArrayList<GenWordPolynomial<BigRational>>();
        //GenWordPolynomial<BigRational> p1 = wring.parse("b - a^2"); // not associative
        //GenWordPolynomial<BigRational> p1 = wring.parse("b - a^3"); // not associative
        //GenWordPolynomial<BigRational> p1 = wring.parse("b a - 1"); // Weyl relation, result not assoc
        //GenWordPolynomial<BigRational> p1 = wring.parse("a b - 1"); // isAssoc?
        GenWordPolynomial<BigRational> p1 = wring.parse("b a - a b"); // commutative, okay
        il.add(p1);
        //p1 = wring.parse("a - b^5");
        //il.add(p1);
        //System.out.println("il = " + il);
        wideal = new WordIdeal<BigRational>(wring, il);
        //System.out.println("wideal = " + wideal.toScript());
        wideal = wideal.GB();
        //System.out.println("twosided wideal = " + wideal.toScript());
        if (wideal.isONE()) {
            System.out.println("twosided wideal = " + wideal.toScript());
            throw new IllegalArgumentException("ideal is one");
        }
        rring = new WordResidueRing<BigRational>(wideal);
        //System.out.println("rring = " + rring.toScript());
        ring = new ResidueSolvableWordPolynomialRing<BigRational>(rring, tord, vars);
        RelationGenerator<WordResidue<BigRational>> wl = new WeylRelations<WordResidue<BigRational>>();
        wl.generate(ring);
        List<GenSolvablePolynomial<WordResidue<BigRational>>> qrel = ring.table.relationList();
        //System.out.println("qrel = " + qrel);
        List<GenSolvablePolynomial<GenWordPolynomial<BigRational>>> prel = new ArrayList<GenSolvablePolynomial<GenWordPolynomial<BigRational>>>();
        for (GenSolvablePolynomial<WordResidue<BigRational>> q : qrel) {
            GenSolvablePolynomial<GenWordPolynomial<BigRational>> p = ring.toPolyCoefficients(q);
            prel.add(p);
        }
        //System.out.println("prel = " + prel);
        ring.polCoeff.table.addSolvRelations(prel);
        //System.out.println("ring = " + ring.toScript());
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        ring = null;
        a = b = c = d = e = null;
    }


    /**
     * Test constructor, generators and properties.
     */
    public void testConstructor() {
        assertFalse("not commutative", ring.isCommutative());
        assertTrue("associative", ring.isAssociative());

        a = new ResidueSolvableWordPolynomial<BigRational>(ring);
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
        for (GenPolynomial<WordResidue<BigRational>> g : ring.generators()) {
            //System.out.print("g = " + g + ", ");
            assertFalse("not isZERO( g )", g.isZERO());
        }
        //System.out.println("");
    }


    /**
     * Test random polynomial and conversion.
     */
    public void testRandom() {
        for (int i = 0; i < 3; i++) {
            // a = ring.random(ll+2*i);
            a = ring.random(kl * (i + 1), ll + 2 * i, el + i, q);
            //System.out.println("a = " + a);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());

            RecSolvableWordPolynomial<BigRational> b = ring.toPolyCoefficients(a);
            c = ring.fromPolyCoefficients(b);
            assertEquals("res(poly(a)) == a", a, c);
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = ring.random(kl, ll, el, q);
        c = (ResidueSolvableWordPolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (ResidueSolvableWordPolynomial<BigRational>) a.sum(a);
        c = (ResidueSolvableWordPolynomial<BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (ResidueSolvableWordPolynomial<BigRational>) b.sum(a);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = ring.random(kl, ll, el, q);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.sum(b.sum(c));
        e = (ResidueSolvableWordPolynomial<BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.random(rl, el, q);
        WordResidue<BigRational> x = rring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (ResidueSolvableWordPolynomial<BigRational>) a.sum(b);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (ResidueSolvableWordPolynomial<BigRational>) a.subtract(b);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (ResidueSolvableWordPolynomial<BigRational>) b.sum(a);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (ResidueSolvableWordPolynomial<BigRational>) a.subtract(b);
        d = (ResidueSolvableWordPolynomial<BigRational>) a.subtract(x, u);
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
        //a = ring.parse("( ( a + 11/5 ) )");
        //b = ring.parse("( ( a + 35/6 ) )");
        //c = ring.parse("( ( b a + 11/24 a ) )");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("d-e = " + d.subtract(e));
        assertEquals("a(bc) = (ab)c", d, e);

        d = (ResidueSolvableWordPolynomial<BigRational>) a.monic();
        //System.out.println("d = " + d);
        assertTrue("a.monic(): " + d, d.leadingBaseCoefficient().isONE()
                   || d.leadingBaseCoefficient().abs().equals(a.leadingBaseCoefficient().abs()));
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);

        d = a.multiply((ResidueSolvableWordPolynomial<BigRational>) b.sum(c));
        e = (ResidueSolvableWordPolynomial<BigRational>) a.multiply(b).sum(a.multiply(c));
        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test word coefficient ring.
     */
    public void testWordCoeffsRelations() {
        assertTrue("# relations == 2", ring.table.size() == 2);
        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());
        //System.out.println("ring = " + ring.toScript());

        ResidueSolvableWordPolynomial<BigRational> r1 = ring.parse("x");
        GenWordPolynomial<BigRational> r2 = wring.parse("a");
        ResidueSolvableWordPolynomial<BigRational> rp = ring.parse("a x + b");
        GenSolvablePolynomial<GenWordPolynomial<BigRational>> pp = ring.toPolyCoefficients(rp);
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        //System.out.println("pp = " + pp);
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingWord().leadingExpVector(), pp);

        //System.out.println("ring = " + ring.toScript());

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<WordResidue<BigRational>>> gens = ring.generators();
        for (GenPolynomial<WordResidue<BigRational>> x : gens) {
            GenSolvablePolynomial<WordResidue<BigRational>> xx = (GenSolvablePolynomial<WordResidue<BigRational>>) x;
            a = new ResidueSolvableWordPolynomial<BigRational>(ring, xx);
            for (GenPolynomial<WordResidue<BigRational>> y : gens) {
                GenSolvablePolynomial<WordResidue<BigRational>> yy = (GenSolvablePolynomial<WordResidue<BigRational>>) y;
                b = new ResidueSolvableWordPolynomial<BigRational>(ring, yy);
                c = a.multiply(b);
                //System.out.println("gens:" + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                // not always true
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
                // not true
                //ev = a.leadingBaseCoefficient().val.leadingWord().leadingExpVector()
                //                .sum(b.leadingBaseCoefficient().val.leadingWord().leadingExpVector());
                //assertTrue("LT(lc(a))*LT(lc(b)) == LT(lc(c))", c.leadingBaseCoefficient().val.leadingWord()
                //                .leadingExpVector().equals(ev));
            }
        }
        //System.out.println("ring = " + ring.toScript());

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
    }


    /**
     * Test extension and contraction for Weyl relations.
     */
    public void testExtendContractWeyl() {
        ResidueSolvableWordPolynomial<BigRational> r1 = ring.parse("x");
        GenWordPolynomial<BigRational> r2 = wring.parse("a");
        ResidueSolvableWordPolynomial<BigRational> rp = ring.parse("a x + b");
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingWord().leadingExpVector(),
                                        ring.toPolyCoefficients(rp));

        int k = rl;
        ResidueSolvableWordPolynomialRing<BigRational> pfe = ring.extend(k);
        //System.out.println("pfe = " + pfe);
        ResidueSolvableWordPolynomialRing<BigRational> pfec = pfe.contract(k);
        //System.out.println("pfec = " + pfec);
        assertEquals("ring == pfec", ring, pfec);

        ResidueSolvableWordPolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        ResidueSolvableWordPolynomial<BigRational> ae = (ResidueSolvableWordPolynomial<BigRational>) a
            .extend(pfe, 0, 0);
        //System.out.println("ae = " + ae);

        Map<ExpVector, GenPolynomial<WordResidue<BigRational>>> m = ae.contract(pfec);
        List<GenPolynomial<WordResidue<BigRational>>> ml = new ArrayList<GenPolynomial<WordResidue<BigRational>>>(
                                                                                                                  m.values());
        GenPolynomial<WordResidue<BigRational>> aec = ml.get(0);
        //System.out.println("ae  = " + ae);
        //System.out.println("aec = " + aec);
        assertEquals("a == aec", a, aec);
    }


    /**
     * Test reversion for Weyl relations.
     */
    public void testReverseWeyl() {
        ResidueSolvableWordPolynomial<BigRational> r1 = ring.parse("x");
        GenWordPolynomial<BigRational> r2 = wring.parse("a");
        ResidueSolvableWordPolynomial<BigRational> rp = ring.parse("a x + b");
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingWord().leadingExpVector(),
                                        ring.toPolyCoefficients(rp));

        ResidueSolvableWordPolynomialRing<BigRational> pfr = ring.reverse();
        ResidueSolvableWordPolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", ring, pfrr);
        //System.out.println("ring = " + ring);
        //System.out.println("pfr = " + pfr);

        ResidueSolvableWordPolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        ResidueSolvableWordPolynomial<BigRational> ar = (ResidueSolvableWordPolynomial<BigRational>) a
            .reverse(pfr);
        ResidueSolvableWordPolynomial<BigRational> arr = (ResidueSolvableWordPolynomial<BigRational>) ar
            .reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);
    }

}
