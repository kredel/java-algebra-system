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

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.WeylRelations;


/**
 * BigRational coefficients ResidueSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class ResidueSolvablePolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ResidueSolvablePolynomialTest</CODE> object.
     * @param name String.
     */
    public ResidueSolvablePolynomialTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ResidueSolvablePolynomialTest.class);
        return suite;
    }


    ResidueSolvablePolynomial<BigRational> a, b, c, d, e, f, x1, x2;


    int rl = 4;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.3f;


    String[] cvars = new String[] { "a", "b" };


    String[] vars = new String[] { "w", "x", "y", "z" };


    RelationTable<SolvableResidue<BigRational>> table;


    SolvableResidueRing<BigRational> rring;


    SolvableIdeal<BigRational> sideal;


    ResidueSolvablePolynomialRing<BigRational> ring;


    BigRational cfac;


    //GenSolvablePolynomialRing<SolvableResidue<BigRational>> sring;
    GenSolvablePolynomialRing<BigRational> sring;


    GenPolynomialRing<BigRational> cring;


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        //cring = new GenPolynomialRing<BigRational>(cfac, tord, cvars);
        sring = new GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
        RelationGenerator<BigRational> wc = new WeylRelations<BigRational>();
        wc.generate(sring);
        List<GenSolvablePolynomial<BigRational>> il = new ArrayList<GenSolvablePolynomial<BigRational>>();
        GenSolvablePolynomial<BigRational> p1 = sring.parse("b - a^2");
        il.add(p1);
        //p1 = sring.parse("a - b^5");
        //il.add(p1);
        sideal = new SolvableIdeal<BigRational>(sring, il);
        rring = new SolvableResidueRing<BigRational>(sideal);
        ring = new ResidueSolvablePolynomialRing<BigRational>(rring, tord, vars);
        RelationGenerator<SolvableResidue<BigRational>> wl = new WeylRelations<SolvableResidue<BigRational>>();
        wl.generate(ring);
        table = ring.table;
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

        a = new ResidueSolvablePolynomial<BigRational>(ring);
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
        for (GenPolynomial<SolvableResidue<BigRational>> g : ring.generators()) {
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
        c = (ResidueSolvablePolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (ResidueSolvablePolynomial<BigRational>) a.sum(a);
        c = (ResidueSolvablePolynomial<BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (ResidueSolvablePolynomial<BigRational>) b.sum(a);
        d = (ResidueSolvablePolynomial<BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = ring.random(kl, ll, el, q);
        d = (ResidueSolvablePolynomial<BigRational>) a.sum(b.sum(c));
        e = (ResidueSolvablePolynomial<BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.EVRAND(rl, el, q);
        SolvableResidue<BigRational> x = rring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (ResidueSolvablePolynomial<BigRational>) a.sum(b);
        d = (ResidueSolvablePolynomial<BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (ResidueSolvablePolynomial<BigRational>) a.subtract(b);
        d = (ResidueSolvablePolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (ResidueSolvablePolynomial<BigRational>) b.sum(a);
        d = (ResidueSolvablePolynomial<BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (ResidueSolvablePolynomial<BigRational>) a.subtract(b);
        d = (ResidueSolvablePolynomial<BigRational>) a.subtract(x, u);
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

        d = (ResidueSolvablePolynomial<BigRational>) a.monic();
        //System.out.println("d = " + d);
        assertTrue("a.monic(): " + d, d.leadingBaseCoefficient().isONE()
                        || d.leadingBaseCoefficient().equals(a.leadingBaseCoefficient()));
    }


    /**
     * Test partially commutative ring.
     */
    public void testPartCommutative() {
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("table = " + table.toScript());
        //System.out.println("ring = " + ring);
        //System.out.println("ring.table = " + ring.table.toScript());
        //assertEquals("table == ring.table: ", table, ring.table); // ?
        assertTrue("# relations == 2", ring.table.size() == 2);

        ring = new ResidueSolvablePolynomialRing<BigRational>(rring, ring);
        table = ring.table;
        //System.out.println("table = " + table.toString(vars));
        //System.out.println("ring = " + ring);

        assertTrue("isCommutative()", ring.isCommutative() || !rring.isCommutative());
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

        d = a.multiply((ResidueSolvablePolynomial<BigRational>) b.sum(c));
        e = (ResidueSolvablePolynomial<BigRational>) a.multiply(b).sum(a.multiply(c));
        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test solvable coefficient ring.
     */
    public void testSolvableCoeffs() {
        assertTrue("# relations == 2", ring.table.size() == 2);
        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        ResidueSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = sring.parse("a");
        ResidueSolvablePolynomial<BigRational> rp = ring.parse("a x + b");
        GenSolvablePolynomial<GenPolynomial<BigRational>> pp = ring.toPolyCoefficients(rp);
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        //System.out.println("pp = " + pp);
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), pp);

        //table = ring.table;
        //System.out.println("ring = " + ring);

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<SolvableResidue<BigRational>>> gens = ring.generators();
        for (GenPolynomial<SolvableResidue<BigRational>> x : gens) {
            GenSolvablePolynomial<SolvableResidue<BigRational>> xx = (GenSolvablePolynomial<SolvableResidue<BigRational>>) x;
            a = new ResidueSolvablePolynomial<BigRational>(ring, xx);
            for (GenPolynomial<SolvableResidue<BigRational>> y : gens) {
                GenSolvablePolynomial<SolvableResidue<BigRational>> yy = (GenSolvablePolynomial<SolvableResidue<BigRational>>) y;
                b = new ResidueSolvablePolynomial<BigRational>(ring, yy);
                c = a.multiply(b);
                //System.out.println("gens:" + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
                ev = a.leadingBaseCoefficient().val.leadingExpVector().sum(
                                b.leadingBaseCoefficient().val.leadingExpVector());
                assertTrue("LT(lc(a))*LT(lc(b)) == LT(lc(c))", c.leadingBaseCoefficient().val
                                .leadingExpVector().equals(ev));
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
    }


    /**
     * Test extension and contraction for Weyl relations.
     */
    public void testExtendContractWeyl() {
        ResidueSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = sring.parse("a");
        ResidueSolvablePolynomial<BigRational> rp = ring.parse("a x + b");
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), ring.toPolyCoefficients(rp));

        int k = rl;
        ResidueSolvablePolynomialRing<BigRational> pfe = ring.extend(k);
        //System.out.println("pfe = " + pfe);
        ResidueSolvablePolynomialRing<BigRational> pfec = pfe.contract(k);
        //System.out.println("pfec = " + pfec);
        assertEquals("ring == pfec", ring, pfec);

        ResidueSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        ResidueSolvablePolynomial<BigRational> ae = (ResidueSolvablePolynomial<BigRational>) a.extend(pfe, 0,
                        0);
        //System.out.println("ae = " + ae);

        Map<ExpVector, GenPolynomial<SolvableResidue<BigRational>>> m = ae.contract(pfec);
        List<GenPolynomial<SolvableResidue<BigRational>>> ml = new ArrayList<GenPolynomial<SolvableResidue<BigRational>>>(
                        m.values());
        GenPolynomial<SolvableResidue<BigRational>> aec = ml.get(0);
        //System.out.println("ae  = " + ae);
        //System.out.println("aec = " + aec);
        assertEquals("a == aec", a, aec);
    }


    /**
     * Test reversion for Weyl relations.
     */
    public void testReverseWeyl() {
        ResidueSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = sring.parse("a");
        ResidueSolvablePolynomial<BigRational> rp = ring.parse("a x + b");
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), ring.toPolyCoefficients(rp));

        ResidueSolvablePolynomialRing<BigRational> pfr = ring.reverse();
        ResidueSolvablePolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", ring, pfrr);
        //System.out.println("ring = " + ring);
        //System.out.println("pfr = " + pfr);

        ResidueSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        ResidueSolvablePolynomial<BigRational> ar = (ResidueSolvablePolynomial<BigRational>) a.reverse(pfr);
        ResidueSolvablePolynomial<BigRational> arr = (ResidueSolvablePolynomial<BigRational>) ar
                        .reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);
    }


    /**
     * Test recursive for Weyl relations.
     */
    public void testRecursiveWeyl() {
        GenSolvablePolynomialRing<GenPolynomial<SolvableResidue<BigRational>>> rsring = ring.recursive(2); // 1,2,3
        //System.out.println("rsring = " + rsring.toScript());

        GenSolvablePolynomial<SolvableResidue<BigRational>> ad, bd, cd, dd;
        RecSolvablePolynomial<SolvableResidue<BigRational>> ar, br, cr, dr;
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

        ar = (RecSolvablePolynomial<SolvableResidue<BigRational>>) PolyUtil
                        .<SolvableResidue<BigRational>> recursive(rsring, ad);
        br = (RecSolvablePolynomial<SolvableResidue<BigRational>>) PolyUtil
                        .<SolvableResidue<BigRational>> recursive(rsring, bd);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);
        //System.out.println("cr.ring = " + cr.ring.toScript());

        dr = (RecSolvablePolynomial<SolvableResidue<BigRational>>) PolyUtil
                        .<SolvableResidue<BigRational>> recursive(rsring, cd);
        //System.out.println("dr = " + dr);

        assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
        assertEquals("dr == cr", dr, cr);

        dd = (GenSolvablePolynomial<SolvableResidue<BigRational>>) PolyUtil
                        .<SolvableResidue<BigRational>> distribute(ring, cr);
        // //System.out.println("dd = " + dd);
        assertEquals("dd == cd", dd, cd);
    }

}
