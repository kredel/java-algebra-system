/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
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
        TestSuite suite = new TestSuite(RecSolvablePolynomialTest.class);
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


    @Override
    protected void setUp() {
        cfac = new BigRational(1);
        cring = new GenPolynomialRing<BigRational>(cfac, tord, cvars);
        //sring = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(cring,rl,tord);
        ring = new RecSolvablePolynomialRing<BigRational>(cring, tord, vars);
        RelationGenerator<GenPolynomial<BigRational>> wl = new WeylRelations<GenPolynomial<BigRational>>();
        //wl.generate(ring);
        ring.addRelations(wl);
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

        a = new RecSolvablePolynomial<BigRational>(ring);
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
        for (GenPolynomial<GenPolynomial<BigRational>> g : ring.generators()) {
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
        c = (RecSolvablePolynomial<BigRational>) a.subtract(a);
        assertTrue("a-a = 0", c.isZERO());

        b = (RecSolvablePolynomial<BigRational>) a.sum(a);
        c = (RecSolvablePolynomial<BigRational>) b.subtract(a);
        assertEquals("a+a-a = a", c, a);

        b = ring.random(kl, ll, el, q);
        c = (RecSolvablePolynomial<BigRational>) b.sum(a);
        d = (RecSolvablePolynomial<BigRational>) a.sum(b);
        assertEquals("a+b = b+a", c, d);

        c = ring.random(kl, ll, el, q);
        d = (RecSolvablePolynomial<BigRational>) a.sum(b.sum(c));
        e = (RecSolvablePolynomial<BigRational>) a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        ExpVector u = ExpVector.EVRAND(rl, el, q);
        GenPolynomial<BigRational> x = cring.random(kl);
        //System.out.println("x = " + x);
        //System.out.println("u = " + u);

        b = ring.getONE().multiply(x, u);
        c = (RecSolvablePolynomial<BigRational>) a.sum(b);
        d = (RecSolvablePolynomial<BigRational>) a.sum(x, u);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (RecSolvablePolynomial<BigRational>) a.subtract(b);
        d = (RecSolvablePolynomial<BigRational>) a.subtract(x, u);
        assertEquals("a-p(x,u) = a-(x,u)", c, d);

        a = ring.getZERO();
        b = ring.getONE().multiply(x, u);
        c = (RecSolvablePolynomial<BigRational>) b.sum(a);
        d = (RecSolvablePolynomial<BigRational>) a.sum(x, u);
        assertEquals("a+p(x,u) = a+(x,u)", c, d);

        c = (RecSolvablePolynomial<BigRational>) a.subtract(b);
        d = (RecSolvablePolynomial<BigRational>) a.subtract(x, u);
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
        GenPolynomial<BigRational> xp = new GenPolynomial<BigRational>(cring, x);
        d = (RecSolvablePolynomial<BigRational>) a.multiply(xp);
        assertTrue("monic(a) = a*(1/ldcf(ldcf(a)))", d.leadingBaseCoefficient().leadingBaseCoefficient()
                        .isONE());

        d = (RecSolvablePolynomial<BigRational>) a.monic();
        assertTrue("a.monic(): ", d.leadingBaseCoefficient().isONE() || d.leadingBaseCoefficient().equals(a.leadingBaseCoefficient()));
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

        ring = new RecSolvablePolynomialRing<BigRational>(cring, ring);
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
        assertEquals("ba == ab: ", c, d);
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = ring.random(kl, ll, el, q);
        b = ring.random(kl, ll, el, q);
        c = ring.random(kl, ll, el, q);

        d = a.multiply((RecSolvablePolynomial<BigRational>) b.sum(c));
        e = (RecSolvablePolynomial<BigRational>) a.multiply(b).sum(a.multiply(c));
        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test solvable coefficient ring.
     */
    public void testSolvableCoeffs() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,
                        tord, cvars);
        RelationGenerator<BigRational> wlc = new WeylRelations<BigRational>();
        //wlc.generate(csring);
        csring.addRelations(wlc);
        assertTrue("# relations == 1", csring.table.size() == 1);
        assertFalse("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        ring = new RecSolvablePolynomialRing<BigRational>(csring, ring);
        RelationGenerator<GenPolynomial<BigRational>> wl = new WeylRelations<GenPolynomial<BigRational>>();
        //wl.generate(ring);
        ring.addRelations(wl);
        assertTrue("# relations == 2", ring.table.size() == 2);
        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        RecSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
        //System.out.println("r1 = " + r1);
        //System.out.println("r2 = " + r2);
        //System.out.println("rp = " + rp);
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);

        table = ring.table;
        //System.out.println("ring = " + ring);

        assertFalse("isCommutative()", ring.isCommutative());
        assertTrue("isAssociative()", ring.isAssociative());

        List<GenPolynomial<GenPolynomial<BigRational>>> gens = ring.generators();
        for (GenPolynomial<GenPolynomial<BigRational>> x : gens) {
            GenSolvablePolynomial<GenPolynomial<BigRational>> xx = (GenSolvablePolynomial<GenPolynomial<BigRational>>) x;
            a = new RecSolvablePolynomial<BigRational>(ring, xx);
            //System.out.println("a = " + a);
            for (GenPolynomial<GenPolynomial<BigRational>> y : gens) {
                GenSolvablePolynomial<GenPolynomial<BigRational>> yy = (GenSolvablePolynomial<GenPolynomial<BigRational>>) y;
                b = new RecSolvablePolynomial<BigRational>(ring, yy);
                //System.out.println("b = " + b);
                c = a.multiply(b);
                //System.out.println("gens:" + a + " * " + b + " = " + c);
                ExpVector ev = a.leadingExpVector().sum(b.leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingExpVector().equals(ev));
                ev = a.leadingBaseCoefficient().leadingExpVector()
                                .sum(b.leadingBaseCoefficient().leadingExpVector());
                assertTrue("LT(a)*LT(b) == LT(c)", c.leadingBaseCoefficient().leadingExpVector().equals(ev));
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

        RecSolvablePolynomialRing<BigRational> ring2 
           = new RecSolvablePolynomialRing<BigRational>(ring.coFac, ring);
        ring2.table.addSolvRelations(ring.table.relationList());
        ring2.coeffTable.addSolvRelations(ring.coeffTable.relationList());

        //System.out.println("ring2.table.rels = " + ring2.table.relationList());
        //System.out.println("ring2.coeffTable.rels = " + ring2.coeffTable.relationList());
        assertEquals("ring.table == ring2.table: ", ring.table, ring2.table);
        assertEquals("ring.coeffTable == ring2.coeffTable: ", ring.coeffTable, ring2.coeffTable);
    }


    /**
     * Test extension and contraction for Weyl relations.
     */
    public void testExtendContractWeyl() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,
                        tord, cvars);
        RelationGenerator<BigRational> wlc = new WeylRelations<BigRational>();
        //wlc.generate(csring);
        csring.addRelations(wlc);
        assertFalse("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        RecSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);

        int k = rl;
        RecSolvablePolynomialRing<BigRational> pfe = ring.extend(k);
        //System.out.println("pfe = " + pfe);
        RecSolvablePolynomialRing<BigRational> pfec = pfe.contract(k);
        //System.out.println("pfec = " + pfec);
        assertEquals("ring == pfec", ring, pfec);

        RecSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        RecSolvablePolynomial<BigRational> ae = (RecSolvablePolynomial<BigRational>) a.extend(pfe, 0, 0);
        //System.out.println("ae = " + ae);

        Map<ExpVector, GenPolynomial<GenPolynomial<BigRational>>> m = ae.contract(pfec);
        List<GenPolynomial<GenPolynomial<BigRational>>> ml = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>(
                        m.values());
        GenPolynomial<GenPolynomial<BigRational>> aec = ml.get(0);
        //System.out.println("ae  = " + ae);
        //System.out.println("aec = " + aec);
        assertEquals("a == aec", a, aec);
    }


    /**
     * Test distribute and recursion for Weyl relations.
     */
    public void testDistRecWeyl() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,
                        tord, cvars);
        RelationGenerator<BigRational> wlc = new WeylRelations<BigRational>();
        csring.addRelations(wlc);
        assertFalse("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        ring = new RecSolvablePolynomialRing<BigRational>(csring, tord, vars);
        RelationGenerator<GenPolynomial<BigRational>> wl = new WeylRelations<GenPolynomial<BigRational>>();
        ring.addRelations(wl);

        // first distribute solvable polynomial only
        GenSolvablePolynomialRing<BigRational> fring =  (GenSolvablePolynomialRing) ring;
        GenSolvablePolynomialRing<BigRational> pfd = fring.<BigRational>distribute();
        //System.out.println("pfd = " + pfd.toScript());
        RecSolvablePolynomialRing<BigRational> pfdr 
            = (RecSolvablePolynomialRing<BigRational>) pfd.recursive(ring.nvar);
        //System.out.println("pfdr = " + pfdr.toScript());
        //System.out.println("ring = " + ring.toScript());
        assertEquals("ring == pfdr", ring, pfdr);

        RecSolvablePolynomial<BigRational> a = ring.random(kl, 2*ll, el, 2.0f*q);
        //System.out.println("a   = " + a);

        GenSolvablePolynomial<BigRational> ad 
            = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> distribute(pfd, a);
        //System.out.println("ad  = " + ad);

        GenSolvablePolynomial<GenPolynomial<BigRational>> adr 
            = (GenSolvablePolynomial<GenPolynomial<BigRational>>) PolyUtil.<BigRational> recursive(pfdr,ad);
        //System.out.println("adr = " + adr);
        assertEquals("a == adr", a, adr);

        // now recursive solvable polynials with coefficient relations:
        RecSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);
        //System.out.println("ring = " + ring.toScript());

        GenSolvablePolynomialRing<BigRational> pfrd = RecSolvablePolynomialRing.<BigRational> distribute(ring);
        //System.out.println("pfrd = " + pfrd.toScript());
        RecSolvablePolynomialRing<BigRational> pfrdr 
            = (RecSolvablePolynomialRing<BigRational>) pfrd.recursive(ring.nvar);
        //System.out.println("pfrdr = " + pfrdr.toScript());
        //System.out.println("ring = " + ring.toScript());
        assertEquals("ring == pfrdr", ring, pfrdr);

        //System.out.println("a   = " + a);
        ad = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> distribute(pfrd, a);
        //System.out.println("ad  = " + ad);
        adr = (GenSolvablePolynomial<GenPolynomial<BigRational>>) PolyUtil.<BigRational> recursive(pfrdr,ad);
        //System.out.println("adr = " + adr);
        assertEquals("a == adr", a, adr);
    }


    /**
     * Test reversion for Weyl relations.
     */
    public void testReverseWeyl() {
        GenSolvablePolynomialRing<BigRational> csring = new GenSolvablePolynomialRing<BigRational>(cfac,
                        tord, cvars);
        RelationGenerator<BigRational> wlc = new WeylRelations<BigRational>();
        //wlc.generate(csring);
        csring.addRelations(wlc);
        assertFalse("isCommutative()", csring.isCommutative());
        assertTrue("isAssociative()", csring.isAssociative());

        RecSolvablePolynomial<BigRational> r1 = ring.parse("x");
        GenSolvablePolynomial<BigRational> r2 = csring.parse("b");
        RecSolvablePolynomial<BigRational> rp = ring.parse("b x + a");
        ring.coeffTable.update(r1.leadingExpVector(), r2.leadingExpVector(), rp);

        RecSolvablePolynomialRing<BigRational> pfr = ring.reverse();
        RecSolvablePolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", ring, pfrr);
        //System.out.println("ring = " + ring);
        //System.out.println("pfr = " + pfr);

        RecSolvablePolynomial<BigRational> a = ring.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        RecSolvablePolynomial<BigRational> ar = (RecSolvablePolynomial<BigRational>) a.reverse(pfr);
        RecSolvablePolynomial<BigRational> arr = (RecSolvablePolynomial<BigRational>) ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);
    }


    /**
     * Test recursive for Weyl relations.
     */
    public void testRecursiveWeyl() {
        String[] svars = new String[] { "w", "x", "y", "z" };
        GenSolvablePolynomialRing<BigRational> sring = new GenSolvablePolynomialRing<BigRational>(cfac, tord,
                        svars);
        RelationGenerator<BigRational> wlc = new WeylRelations<BigRational>(sring);
        //wlc.generate(sring);
        sring.addRelations(wlc);
        assertFalse("isCommutative()", sring.isCommutative());
        assertTrue("isAssociative()", sring.isAssociative());
        //System.out.println("sring = " + sring.toScript());

        GenSolvablePolynomialRing<GenPolynomial<BigRational>> rsring = sring.recursive(2); // 1,2,3
        //System.out.println("rsring = " + rsring.toScript());

        GenSolvablePolynomial<BigRational> ad, bd, cd, dd;
        RecSolvablePolynomial<BigRational> ar, br, cr, dr;
        ad = sring.random(kl, ll, el, q);
        bd = sring.random(kl, ll, el, q);
        //ad = sring.parse("7/2 y^2 * z"); // - 15/2 w^2 + 262/225");
        //bd = sring.parse("-10/13 x "); //+ 413/150");
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
        //System.out.println("cr.ring = " + cr.ring.toScript());

        dr = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> recursive(rsring, cd);
        //System.out.println("dr = " + dr);

        assertEquals("dr.ring == cr.ring", dr.ring, cr.ring);
        assertEquals("dr == cr", dr, cr);

        dd = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> distribute(sring, cr);
        //System.out.println("dd = " + dd);
        assertEquals("dd == cd", dd, cd);
    }


    /**
     * Test recursive for iterated Weyl relations.
     */
    public void testRecursiveIteratedWeyl() {
        String[] svars = new String[] { "w", "x", "y", "z" };
        GenSolvablePolynomialRing<BigRational> sring = new GenSolvablePolynomialRing<BigRational>(cfac, tord,
                        svars);
        RelationGenerator<BigRational> wlc = new WeylRelationsIterated<BigRational>();
        //wlc.generate(sring);
        sring.addRelations(wlc);
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
