/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelations;


/**
 * BigRational coefficients QuotSolvablePolynomial tests with JUnit.
 * @author Heinz Kredel.
 */

public class QuotSolvablePolynomialTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>QuotSolvablePolynomialTest</CODE> object.
     * @param name String.
     */
    public QuotSolvablePolynomialTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(QuotSolvablePolynomialTest.class);
        return suite;
    }


    QuotSolvablePolynomial<BigRational> a, b, c, d, e, f, x1, x2;


    int rl = 4;


    int kl = 1;


    int ll = 4;


    int el = 3;


    float q = 0.2f;


    String[] cvars = new String[] { "a", "b" };


    String[] vars = new String[] { "w", "x", "y", "z" };


    RelationTable<SolvableQuotient<BigRational>> table;


    QuotSolvablePolynomialRing<BigRational> ring;


    BigRational cfac;


    GenSolvablePolynomialRing<SolvableQuotient<BigRational>> sring;


    GenSolvablePolynomialRing<BigRational> cring;


    SolvableQuotientRing<BigRational> qcring;


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        cring = new GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
        qcring = new SolvableQuotientRing<BigRational>(cring);
        ring = new QuotSolvablePolynomialRing<BigRational>(qcring, tord, vars);
        WeylRelations<SolvableQuotient<BigRational>> wl = new WeylRelations<SolvableQuotient<BigRational>>(
                        ring);
        wl.generate();
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

        a = new QuotSolvablePolynomial<BigRational>(ring);
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
        for (GenPolynomial<SolvableQuotient<BigRational>> g : ring.generators()) {
            //System.out.print("g = " + g + ", ");
            assertFalse("not isZERO( g )", g.isZERO());
        }
        //System.out.println("");
        assertTrue("isAssociative: ", ring.isAssociative());
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
        c = (QuotSolvablePolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (QuotSolvablePolynomial<BigRational>) a.sum(a);
        c = (QuotSolvablePolynomial<BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (QuotSolvablePolynomial<BigRational>) b.sum(a);
        d = (QuotSolvablePolynomial<BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = ring.random(kl, ll, el, q);
        d = (QuotSolvablePolynomial<BigRational>) a.sum(b.sum(c));
        e = (QuotSolvablePolynomial<BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.EVRAND(rl, el, q);
        SolvableQuotient<BigRational> x = qcring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (QuotSolvablePolynomial<BigRational>) a.sum(b);
        d = (QuotSolvablePolynomial<BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (QuotSolvablePolynomial<BigRational>) a.subtract(b);
        d = (QuotSolvablePolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (QuotSolvablePolynomial<BigRational>) b.sum(a);
        d = (QuotSolvablePolynomial<BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (QuotSolvablePolynomial<BigRational>) a.subtract(b);
        d = (QuotSolvablePolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);
    }


    /**
     * Test multiplication.
     */
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

        SolvableQuotient<BigRational> xp = a.leadingBaseCoefficient().inverse();
        d = a.multiply(xp);
        assertTrue("monic(a) = a*(1/ldcf(ldcf(a)))", d.leadingBaseCoefficient().isONE());
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

        ring = new QuotSolvablePolynomialRing<BigRational>(qcring, ring);
        table = ring.table;
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
    public void testDistributive() {
        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);

        d = a.multiply((QuotSolvablePolynomial<BigRational>) b.sum(c));
        e = (QuotSolvablePolynomial<BigRational>) a.multiply(b).sum(a.multiply(c));
        assertEquals("a*(b+c) = a*b+a*c", d, e);
    }


    /**
     * Test solvable coefficient ring.
     */
    public void testSolvableCoeffs() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,
                        tord, cvars);
        WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(csring);
        //no: wlc.generate();
        //assertTrue("# relations == 1", csring.table.size() == 1);
        assertTrue("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        SolvableQuotientRing<BigRational> qcsring = new SolvableQuotientRing<BigRational>(csring);
        //assertFalse("isCommutative()", qcsring.isCommutative());
        assertTrue("isAssociative()", qcsring.isAssociative());

        ring = new QuotSolvablePolynomialRing<BigRational>(qcsring, ring);
        WeylRelations<SolvableQuotient<BigRational>> wl = new WeylRelations<SolvableQuotient<BigRational>>(
                        ring);
        wl.generate();
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
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);

        table = ring.table;
        //System.out.println("ring = " + ring);
        //System.out.println("ring.polCoeff = " + ring.polCoeff);

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<SolvableQuotient<BigRational>>> gens = ring.generators();
        //gens = new ArrayList<GenPolynomial<SolvableQuotient<BigRational>>>();

        for (GenPolynomial<SolvableQuotient<BigRational>> x : gens) {
            GenSolvablePolynomial<SolvableQuotient<BigRational>> xx = (GenSolvablePolynomial<SolvableQuotient<BigRational>>) x;
            a = new QuotSolvablePolynomial<BigRational>(ring, xx);
            //System.out.println("a = " + a);
            for (GenPolynomial<SolvableQuotient<BigRational>> y : gens) {
                GenSolvablePolynomial<SolvableQuotient<BigRational>> yy = (GenSolvablePolynomial<SolvableQuotient<BigRational>>) y;
                b = new QuotSolvablePolynomial<BigRational>(ring, yy);
                //System.out.println("b = " + b);
                c = a.multiply(b);
                //System.out.println("gens: " + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
            }
        }

        //a = ring.random(kl, ll, el, q);
        //a = ring.getONE();
        a = ring.parse("x^2 + a b");
        //System.out.println("a = " + a.toScript());
        //b = ring.random(kl, ll, el, q);
        //b = ring.getONE();
        b = ring.parse("a b^2 + a"); // + a 
        b = (QuotSolvablePolynomial<BigRational>) b.inverse();
        //System.out.println("b = " + b.toScript());

        // non-commutative
        c = b.multiply(a);
        d = a.multiply(b);
        //System.out.println("a = " + a.toScript());
        //System.out.println("b = " + b.toScript());
        //System.out.println("c = " + c.toScript());
        //System.out.println("d = " + d.toScript());
        assertTrue("a*b != b*a", c.equals(d) || c.leadingExpVector().equals(d.leadingExpVector()));

        e = (QuotSolvablePolynomial<BigRational>) b.inverse();
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
     * Test extension and contraction for Weyl relations. public void
     * testExtendContractWeyl() { GenSolvablePolynomialRing<BigRational> csring
     * = new GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
     * WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(csring);
     * wlc.generate(); assertFalse("isCommutative()", csring.isCommutative());
     * assertTrue("isAssociative()", csring.isAssociative());
     * 
     * QuotSolvablePolynomial<BigRational> r1 = ring.parse("x");
     * GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
     * QuotSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
     * ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);
     * 
     * int k = rl; QuotSolvablePolynomialRing<BigRational> pfe = ring.extend(k);
     * //System.out.println("pfe = " + pfe);
     * QuotSolvablePolynomialRing<BigRational> pfec = pfe.contract(k);
     * //System.out.println("pfec = " + pfec); assertEquals("ring == pfec",
     * ring, pfec);
     * 
     * QuotSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
     * //System.out.println("a = " + a);
     * 
     * QuotSolvablePolynomial<BigRational> ae =
     * (QuotSolvablePolynomial<BigRational>) a.extend(pfe, 0, 0);
     * //System.out.println("ae = " + ae);
     * 
     * Map<ExpVector, GenPolynomial<GenPolynomial<BigRational>>> m =
     * ae.contract(pfec); List<GenPolynomial<GenPolynomial<BigRational>>> ml =
     * new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>( m.values());
     * GenPolynomial<GenPolynomial<BigRational>> aec = ml.get(0);
     * //System.out.println("ae  = " + ae); //System.out.println("aec = " +
     * aec); assertEquals("a == aec", a, aec); }
     */


    /**
     * Test reversion for Weyl relations. public void testReverseWeyl() {
     * GenSolvablePolynomialRing<BigRational> csring = new
     * GenSolvablePolynomialRing<BigRational>(cfac, tord, cvars);
     * WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(csring);
     * wlc.generate(); assertFalse("isCommutative()", csring.isCommutative());
     * assertTrue("isAssociative()", csring.isAssociative());
     * 
     * QuotSolvablePolynomial<BigRational> r1 = ring.parse("x");
     * GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
     * QuotSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
     * ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);
     * 
     * QuotSolvablePolynomialRing<BigRational> pfr = ring.reverse();
     * QuotSolvablePolynomialRing<BigRational> pfrr = pfr.reverse();
     * assertEquals("pf == pfrr", ring, pfrr); //System.out.println("ring = " +
     * ring); //System.out.println("pfr = " + pfr);
     * 
     * QuotSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
     * //System.out.println("a = " + a);
     * 
     * QuotSolvablePolynomial<BigRational> ar =
     * (QuotSolvablePolynomial<BigRational>) a.reverse(pfr);
     * QuotSolvablePolynomial<BigRational> arr =
     * (QuotSolvablePolynomial<BigRational>) ar.reverse(pfrr);
     * assertEquals("a == arr", a, arr); //System.out.println("ar = " + ar);
     * //System.out.println("arr = " + arr); }
     */


    /**
     * Test recursive for Weyl relations. public void testRecursiveWeyl() {
     * String[] svars = new String[] { "w", "x", "y", "z" };
     * GenSolvablePolynomialRing<BigRational> sring = new
     * GenSolvablePolynomialRing<BigRational>(cfac, tord, svars);
     * WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(sring);
     * wlc.generate(); assertFalse("isCommutative()", sring.isCommutative());
     * assertTrue("isAssociative()", sring.isAssociative());
     * //System.out.println("sring = " + sring.toScript());
     * 
     * GenSolvablePolynomialRing<GenPolynomial<BigRational>> rsring =
     * sring.recursive(2); // 1,2,3 //System.out.println("rsring = " + rsring);
     * //.toScript()); System.out.println("rsring = " + rsring.toScript());
     * 
     * GenSolvablePolynomial<BigRational> ad, bd, cd, dd;
     * QuotSolvablePolynomial<BigRational> ar, br, cr, dr; ad = sring.random(kl,
     * ll, el, q); bd = sring.random(kl, ll, el, q); //ad =
     * sring.parse("7/2 y^2 * z"); // - 15/2 w^2 + 262/225"); //bd =
     * sring.parse("-10/13 x "); //+ 413/150"); //ad =
     * (GenSolvablePolynomial<BigRational>) ad.monic(); //bd =
     * (GenSolvablePolynomial<BigRational>) bd.monic();
     * 
     * //System.out.println("ad = " + ad); //System.out.println("bd = " + bd);
     * 
     * cd = ad.multiply(bd); //System.out.println("cd = " + cd);
     * 
     * ar = (QuotSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * recursive(rsring, ad); br = (QuotSolvablePolynomial<BigRational>)
     * PolyUtil.<BigRational> recursive(rsring, bd);
     * //System.out.println("ar = " + ar); //System.out.println("br = " + br);
     * 
     * cr = ar.multiply(br); //System.out.println("cr = " + cr);
     * //System.out.println("cr.ring = " + cr.ring.toScript());
     * 
     * dr = (QuotSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * recursive(rsring, cd); //System.out.println("dr = " + dr);
     * 
     * assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
     * assertEquals("dr == cr", dr, cr);
     * 
     * dd = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * distribute(sring, cr); //System.out.println("dd = " + dd);
     * assertEquals("dd == cd", dd, cd); }
     */


    /**
     * Test recursive for iterated Weyl relations. public void
     * testRecursiveIteratedWeyl() { String[] svars = new String[] { "w", "x",
     * "y", "z" }; GenSolvablePolynomialRing<BigRational> sring = new
     * GenSolvablePolynomialRing<BigRational>(cfac, tord, svars);
     * WeylRelations<BigRational> wlc = new WeylRelations<BigRational>(sring);
     * wlc.generateIterated(); assertFalse("isCommutative()",
     * sring.isCommutative()); assertTrue("isAssociative()",
     * sring.isAssociative()); //System.out.println("sring = " +
     * sring.toScript());
     * 
     * GenSolvablePolynomialRing<GenPolynomial<BigRational>> rsring =
     * sring.recursive(2); // 1,2,3 //System.out.println("rsring = " + rsring);
     * //.toScript()); System.out.println("rsring = " + rsring.toScript());
     * 
     * GenSolvablePolynomial<BigRational> ad, bd, cd, dd;
     * QuotSolvablePolynomial<BigRational> ar, br, cr, dr; ad = sring.random(kl,
     * ll, el, q); bd = sring.random(kl, ll, el, q); //ad =
     * (GenSolvablePolynomial<BigRational>) ad.monic(); //bd =
     * (GenSolvablePolynomial<BigRational>) bd.monic();
     * 
     * //System.out.println("ad = " + ad); //System.out.println("bd = " + bd);
     * 
     * cd = ad.multiply(bd); //System.out.println("cd = " + cd);
     * 
     * ar = (QuotSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * recursive(rsring, ad); br = (QuotSolvablePolynomial<BigRational>)
     * PolyUtil.<BigRational> recursive(rsring, bd);
     * //System.out.println("ar = " + ar); //System.out.println("br = " + br);
     * 
     * cr = ar.multiply(br); //System.out.println("cr = " + cr);
     * 
     * dr = (QuotSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * recursive(rsring, cd); //System.out.println("dr = " + dr);
     * 
     * assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
     * assertEquals("dr == cr", dr, cr);
     * 
     * dd = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational>
     * distribute(sring, cr); //System.out.println("dd = " + dd);
     * assertEquals("dd == cd", dd, cd); }
     */

}
