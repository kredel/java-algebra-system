/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.QLRSolvablePolynomial;
import edu.jas.poly.QLRSolvablePolynomialRing;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.WeylRelationsIterated;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigRational coefficients LocalResidueSolvablePolynomial QLR representation
 * tests with JUnit.
 * @author Heinz Kredel
 */

public class LocalResidueSolvablePolynomialQLRTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {

        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>LocalResidueSolvablePolynomialQLRTest</CODE> object.
     * @param name String.
     */
    public LocalResidueSolvablePolynomialQLRTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LocalResidueSolvablePolynomialQLRTest.class);
        return suite;
    }


    QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> a, b, c, d, e, f, x1, x2;


    int rl = 4;


    int kl = 1;


    int ll = 4;


    int el = 3;


    float q = 0.2f;


    String[] cvars = new String[] { "a", "b" };


    String[] vars = new String[] { "w", "x", "y", "z" };


    QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational> ring;


    BigRational cfac;


    //GenSolvablePolynomialRing<SolvableLocalResidue<BigRational>> sring;


    GenSolvablePolynomialRing<BigRational> cring;


    SolvableLocalResidueRing<BigRational> qcring;


    SolvableIdeal<BigRational> sideal;


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        cring = new GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
        RelationGenerator<BigRational> wc = new WeylRelations<BigRational>();
        cring.addRelations(wc); //wc.generate(cring);
        List<GenSolvablePolynomial<BigRational>> il = new ArrayList<GenSolvablePolynomial<BigRational>>();
        GenSolvablePolynomial<BigRational> p1 = cring.parse("b - a^2");
        il.add(p1);
        //p1 = cring.parse("a - b^5"); 
        //il.add(p1);
        sideal = new SolvableIdeal<BigRational>(cring, il);
        qcring = new SolvableLocalResidueRing<BigRational>(sideal);
        ring = new QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational>(qcring, tord,
                        vars);
        RelationGenerator<SolvableLocalResidue<BigRational>> wl = new WeylRelations<SolvableLocalResidue<BigRational>>();
        ring.addRelations(wl); //wl.generate(ring);
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

        a = new QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>(ring);
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
        for (GenPolynomial<SolvableLocalResidue<BigRational>> g : ring.generators()) {
            //System.out.println("g = " + g + ", ");
            assertFalse("not isZERO( g )", g.isZERO());
        }
        //System.out.println("");
        assertTrue("isAssociative: ", ring.isAssociative());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 5; i++) {
            // a = ring.random(ll+2*i);
            a = ring.random(kl, ll + 2 * i, el + i, q);
            //System.out.println("a = " + a);
            assertTrue("length( a" + i + " ) <> 0", a.length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     */
    @SuppressWarnings("unchecked")
    public void testAddition() {
        a = ring.random(kl + 1, ll, el, q);
        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(a);
        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.sum(a);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(b);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) c.subtract(b);
        //System.out.println("a = " + a);
        //System.out.println("d = " + d);
        assertEquals("a+b-b = a", a, d);

        c = ring.random(kl, ll, el, q);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(b.sum(c));
        e = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.random(rl, el, q);
        SolvableLocalResidue<BigRational> x = qcring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(b);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.subtract(b);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.sum(a);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.subtract(b);
        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
    }


    /**
     * Test multiplication.
     */
    @SuppressWarnings("unchecked")
    public void testMultiplication() {
        //System.out.println("ring = " + ring);
        a = ring.random(kl, ll - 1, el - 1, q);
        //a = ring.parse(" b y z + a w z ");  
        b = ring.random(kl, ll - 1, el - 1, q);
        //b = ring.parse(" w x - b x "); 

        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        c = ring.random(kl, ll - 1, el - 1, q);
        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        assertEquals("a(bc) = (ab)c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        SolvableLocalResidue<BigRational> xp = a.leadingBaseCoefficient().inverse();
        d = a.multiply(xp);
        assertTrue("monic(a) = a*(1/ldcf(ldcf(a)))", d.leadingBaseCoefficient().isONE());
        //System.out.println("a = " + a);
        //System.out.println("d = " + d);

        d = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.monic();
        assertTrue("a.monic(): ", d.leadingBaseCoefficient().isONE());
    }


    /**
     * Test commutative ring.
     */
    @SuppressWarnings("unchecked")
    public void testCommutative() {
        //System.out.println("table = " + ring.table.toString(vars));
        //System.out.println("table = " + ring.table.toScript());
        //System.out.println("ring = " + ring);
        //System.out.println("ring.table = " + ring.table.toScript());
        //assertEquals("table == ring.table: ", table, ring.table); // ?
        assertTrue("# relations == 2", ring.table.size() == 2);

        cring = new GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
        List<GenSolvablePolynomial<BigRational>> il = new ArrayList<GenSolvablePolynomial<BigRational>>();
        GenSolvablePolynomial<BigRational> p1 = cring.parse("b - a^2");
        il.add(p1);
        sideal = new SolvableIdeal<BigRational>(cring, il);
        qcring = new SolvableLocalResidueRing<BigRational>(sideal);
        ring = new QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational>(qcring, ring);
        //table = ring.table;
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("ring = " + ring);

        assertTrue("isCommutative()", ring.isCommutative());
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
        assertEquals("b*a == a*b: ", c, d);
    }


    /**
     * Test distributive law.
     */
    @SuppressWarnings("unchecked")
    public void testDistributive() {
        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);

        d = a.multiply((QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.sum(c));
        e = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a.multiply(b)
                        .sum(a.multiply(c));
        assertEquals("a*(b+c) = a*b+a*c", d, e);
    }


    /**
     * Test solvable coefficient ring.
     */
    @SuppressWarnings("unchecked")
    public void testSolvableCoeffs() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac, tord,
                        cvars);
        //RelationGenerator<BigRational> wc = new WeylRelations<BigRational>();
        //no: csring.addRelations(wc); //wc.generate(csring);
        //assertTrue("# relations == 1", csring.table.size() == 1);
        assertTrue("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        List<GenSolvablePolynomial<BigRational>> il = new ArrayList<GenSolvablePolynomial<BigRational>>();
        GenSolvablePolynomial<BigRational> p1 = csring.parse("b - a^2");
        il.add(p1);
        //p1 = csring.parse("a - b^5");
        //il.add(p1);
        sideal = new SolvableIdeal<BigRational>(csring, il);
        SolvableLocalResidueRing<BigRational> qcsring = new SolvableLocalResidueRing<BigRational>(sideal);
        assertTrue("isCommutative()", qcsring.isCommutative());
        assertTrue("isAssociative()", qcsring.isAssociative());

        ring = new QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational>(qcsring, ring);
        RelationGenerator<SolvableLocalResidue<BigRational>> wl = new WeylRelations<SolvableLocalResidue<BigRational>>();
        ring.addRelations(wl); //wl.generate(ring);
        assertTrue("# relations == 2", ring.table.size() == 2);
        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());
        //System.out.println("ring = " + ring);

        RecSolvablePolynomial<BigRational> r1 = ring.polCoeff.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.polCoeff.parse("b x + a"); // + a
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);
        //System.out.println("ring = " + ring.toScript());
        //System.out.println("ring.polCoeff = " + ring.polCoeff);
        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<SolvableLocalResidue<BigRational>>> gens = ring.generators();
        for (GenPolynomial<SolvableLocalResidue<BigRational>> x : gens) {
            GenSolvablePolynomial<SolvableLocalResidue<BigRational>> xx = (GenSolvablePolynomial<SolvableLocalResidue<BigRational>>) x;
            a = new QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>(ring, xx);
            //System.out.println("a = " + a);
            for (GenPolynomial<SolvableLocalResidue<BigRational>> y : gens) {
                GenSolvablePolynomial<SolvableLocalResidue<BigRational>> yy = (GenSolvablePolynomial<SolvableLocalResidue<BigRational>>) y;
                b = new QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>(ring, yy);
                //System.out.println("b = " + b);
                c = a.multiply(b);
                //System.out.println("gens: " + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
            }
        }
        //System.out.println("=============");
        //a = ring.random(kl, ll, el, q);
        //a = ring.getONE();
        a = ring.parse("x^2 + a b");
        //System.out.println("a = " + a.toScript());
        //b = ring.random(kl, ll, el, q);
        //b = ring.getONE();
        b = ring.parse("a b + a"); // a b^2 + a 
        b = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.inverse();
        //System.out.println("b = " + b.toScript());

        // non-commutative
        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("a = " + a.toScript());
        //System.out.println("b = " + b.toScript());
        //System.out.println("c = " + c.toScript());
        //System.out.println("d = " + d.toScript());
        assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        e = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) b.inverse();
        //System.out.println("e = " + e.toScript());
        assertTrue("b*b^-1 == 1", e.multiply(b).isONE());

        c = e.multiply(c);
        d = d.multiply(e);
        //System.out.println("a = " + a.toScript());
        //System.out.println("b = " + b.toScript());
        //System.out.println("c = " + c.toScript());
        //System.out.println("d = " + d.toScript());
        assertTrue("a == b * 1/b * a", a.equals(c));
        assertTrue("a == a * 1/b * b", a.equals(d));
    }


    /**
     * Test extension and contraction for Weyl relations.
     */
    @SuppressWarnings("unchecked")
    public void testExtendContractWeyl() {
        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = cring.parse("a");
        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> rp = ring.parse("a x + b");
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(),
                        ring.toPolyCoefficients(rp));

        int k = rl;
        QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational> pfe = ring.extend(k);
        //System.out.println("pfe = " + pfe);
        QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational> pfec = pfe.contract(k);
        //System.out.println("pfec = " + pfec);
        assertEquals("ring == pfec", ring, pfec);

        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> ae = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a
                        .extend(pfe, 0, 0);
        //System.out.println("ae = " + ae);

        Map<ExpVector, GenPolynomial<SolvableLocalResidue<BigRational>>> m = ae.contract(pfec);
        List<GenPolynomial<SolvableLocalResidue<BigRational>>> ml = new ArrayList<GenPolynomial<SolvableLocalResidue<BigRational>>>(
                        m.values());
        GenPolynomial<SolvableLocalResidue<BigRational>> aec = ml.get(0);
        //System.out.println("ae  = " + ae);
        //System.out.println("aec = " + aec);
        assertEquals("a == aec", a, aec);
    }


    /**
     * Test reversion for Weyl relations.
     */
    @SuppressWarnings("unchecked")
    public void testReverseWeyl() {
        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = cring.parse("a");
        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> rp = ring.parse("a x + b");
        ring.polCoeff.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(),
                        ring.toPolyCoefficients(rp));

        QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational> pfr = ring.reverse();
        QLRSolvablePolynomialRing<SolvableLocalResidue<BigRational>, BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", ring, pfrr);
        //System.out.println("ring = " + ring);
        //System.out.println("pfr = " + pfr);

        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> ar = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) a
                        .reverse(pfr);
        QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational> arr = (QLRSolvablePolynomial<SolvableLocalResidue<BigRational>, BigRational>) ar
                        .reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);
    }


    /**
     * Test recursive for Weyl relations.
     */
    @SuppressWarnings("unchecked")
    public void testRecursiveWeyl() {
        GenSolvablePolynomialRing<GenPolynomial<SolvableLocalResidue<BigRational>>> rsring = ring
                        .recursive(2); // 1,2,3
        //System.out.println("rsring = " + rsring.toScript());

        GenSolvablePolynomial<SolvableLocalResidue<BigRational>> ad, bd, cd, dd;
        RecSolvablePolynomial<SolvableLocalResidue<BigRational>> ar, br, cr, dr;
        ad = ring.random(kl, ll, el, q);
        bd = ring.random(kl, ll, el, q);
        //ad = sring.parse("7/2 y^2 * z"); // - 15/2 w^2 + 262/225");
        //bd = sring.parse("-10/13 x "); //+ 413/150");
        //ad = (GenSolvablePolynomial<BigRational>) ad.monic();
        //bd = (GenSolvablePolynomial<BigRational>) bd.monic();

        //System.out.println("ad = " + ad);
        //System.out.println("bd = " + bd);

        cd = ad.multiply(bd);
        //System.out.println("cd = " + cd);

        ar = (RecSolvablePolynomial<SolvableLocalResidue<BigRational>>) PolyUtil
                        .<SolvableLocalResidue<BigRational>> recursive(rsring, ad);
        br = (RecSolvablePolynomial<SolvableLocalResidue<BigRational>>) PolyUtil
                        .<SolvableLocalResidue<BigRational>> recursive(rsring, bd);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);
        //System.out.println("cr.ring = " + cr.ring.toScript());

        dr = (RecSolvablePolynomial<SolvableLocalResidue<BigRational>>) PolyUtil
                        .<SolvableLocalResidue<BigRational>> recursive(rsring, cd);
        //System.out.println("dr = " + dr);

        assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
        assertEquals("dr == cr", dr, cr);

        dd = (GenSolvablePolynomial<SolvableLocalResidue<BigRational>>) PolyUtil
                        .<SolvableLocalResidue<BigRational>> distribute(ring, cr);
        // //System.out.println("dd = " + dd);
        assertEquals("dd == cd", dd, cd);
    }


    /*
     * Test recursive for iterated Weyl relations.
     */
    public void testRecursiveIteratedWeyl() {
        String[] svars = new String[] { "w", "x", "y", "z" };
        GenSolvablePolynomialRing<BigRational> sring = new GenSolvablePolynomialRing<BigRational>(cfac, tord,
                        svars);
        RelationGenerator<BigRational> wlc = new WeylRelationsIterated<BigRational>();
        wlc.generate(sring);
        assertFalse("isCommutative()", sring.isCommutative());
        assertTrue("isAssociative()", sring.isAssociative());
        //System.out.println("sring = " + sring.toScript());

        GenSolvablePolynomialRing<GenPolynomial<BigRational>> rsring = sring.recursive(2); // 1,2,3
        //System.out.println("rsring = " + rsring); //.toScript());
        //System.out.println("rsring = " + rsring.toScript());

        GenSolvablePolynomial<BigRational> ad, bd, cd, dd;
        RecSolvablePolynomial<BigRational> ar, br, cr, dr;
        ad = sring.random(kl, ll, el, q);
        bd = sring.random(kl, ll, el, q);
        //ad = (GenSolvablePolynomial<BigRational>) ad.monic();
        //bd = (GenSolvablePolynomial<BigRational>) bd.monic();

        //System.out.println("ad = " + ad);
        //System.out.println("bd = " + bd);

        cd = ad.multiply(bd);
        //System.out.println("cd = " + cd);

        ar = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> recursive(rsring, ad);
        br = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> recursive(rsring, bd);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        dr = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> recursive(rsring, cd);
        //System.out.println("dr = " + dr);

        assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
        assertEquals("dr == cr", dr, cr);

        dd = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> distribute(sring, cr);
        //System.out.println("dd = " + dd);
        assertEquals("dd == cd", dd, cd);
    }

}
