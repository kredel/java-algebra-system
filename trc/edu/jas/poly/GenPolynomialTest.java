/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.ForkJoinPool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;
import edu.jas.util.ListUtil;
import edu.jas.util.MapEntry;


/**
 * GenPolynomial tests with JUnit.
 * @author Heinz Kredel
 */

public class GenPolynomialTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenPolynomialTest</CODE> object.
     * @param name String.
     */
    public GenPolynomialTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenPolynomialTest.class);
        return suite;
    }


    int rl = 6;


    int kl = 5;


    int ll = 7;


    int el = 4;


    float q = 0.5f;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test constructors and factory.
     */
    public void testConstructors() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        //BigRational r = rf.fromInteger( 99 );
        // System.out.println("r = " + r);
        //r = rf.random( 9 ).sum(r);
        // System.out.println("r = " + r);
        //assertFalse("r.isZERO() = ", r.isZERO());

        //RingElem<BigRational> re = new BigRational( 3 );
        // System.out.println("re = " + re);
        //rf = (BigRational) re;

        //RingFactory<BigRational> ref = new BigRational( 3 );
        // System.out.println("re = " + re);
        //rf = (BigRational) ref;

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf, 2);
        // System.out.println("pf = " + pf);

        GenPolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);
        p = pf.random(9);
        // System.out.println("p = " + p);
        p = pf.getZERO();
        // System.out.println("p = " + p);

        RingElem<GenPolynomial<BigRational>> pe = new GenPolynomial<BigRational>(pf);
        //System.out.println("pe = " + pe);
        //System.out.println("p.equals(pe) = " + p.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe));
        assertTrue("p.equals(p) = ", p.equals(p));

        pe = pe.sum(p); // why not p = p.add(pe) ?
        //System.out.println("pe = " + pe);
        assertTrue("pe.isZERO() = ", pe.isZERO());
        p = pf.random(9);
        p = p.subtract(p);
        //System.out.println("p = " + p);
        //System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());

        // polynomials over (polynomials over rational numbers)
        GenPolynomialRing<GenPolynomial<BigRational>> ppf = new GenPolynomialRing<GenPolynomial<BigRational>>(
                                                                                                              pf, 3);
        // System.out.println("ppf = " + ppf);

        GenPolynomial<GenPolynomial<BigRational>> pp = ppf.getONE();
        // System.out.println("pp = " + pp);
        pp = ppf.random(2);
        // System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        // System.out.println("pp = " + pp);

        RingElem<GenPolynomial<GenPolynomial<BigRational>>> ppe = new GenPolynomial<GenPolynomial<BigRational>>(
                                                                                                                ppf);
        // System.out.println("ppe = " + ppe);
        // System.out.println("pp.equals(ppe) = " + pp.equals(ppe) );
        // System.out.println("pp.equals(pp) = " + pp.equals(pp) );
        assertTrue("pp.equals(ppe) = ", pp.equals(ppe));
        assertTrue("pp.equals(pp) = ", pp.equals(pp));

        ppe = ppe.sum(pp); // why not pp = pp.add(ppe) ?
        //System.out.println("ppe = " + ppe);
        assertTrue("ppe.isZERO() = ", ppe.isZERO());
        pp = ppf.random(2);
        pp = pp.subtract(pp);
        //System.out.println("pp = " + pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        assertTrue("pp.isZERO() = ", pp.isZERO());

        // polynomials over (polynomials over (polynomials over rational numbers))
        GenPolynomialRing<GenPolynomial<GenPolynomial<BigRational>>> pppf = new GenPolynomialRing<GenPolynomial<GenPolynomial<BigRational>>>(
                                                                                                                                             ppf, 4);
        // System.out.println("pppf = " + pppf);

        GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        ppp = pppf.random(2);
        // System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        // System.out.println("ppp = " + ppp);

        RingElem<GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>>> pppe = new GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>>(
                                                                                                                                               pppf);
        // System.out.println("pppe = " + pppe);
        // System.out.println("ppp.equals(pppe) = " + ppp.equals(pppe) );
        // System.out.println("ppp.equals(ppp) = " + ppp.equals(ppp) );
        assertTrue("ppp.equals(pppe) = ", ppp.equals(pppe));
        assertTrue("ppp.equals(ppp) = ", ppp.equals(ppp));

        pppe = pppe.sum(ppp); // why not ppp = ppp.add(pppe) ?
        // System.out.println("pppe = " + pppe);
        assertTrue("pppe.isZERO() = ", pppe.isZERO());
        ppp = pppf.random(2);
        ppp = ppp.subtract(ppp);
        // System.out.println("ppp = " + ppp);
        // System.out.println("ppp.isZERO() = " + ppp.isZERO());
        assertTrue("ppp.isZERO() = ", ppp.isZERO());

        // some tests
        //GenPolynomial<BigRational> pfx = new GenPolynomial<BigRational>();
        //System.out.println("pfx = " + pfx);
    }


    /**
     * Test extension and contraction.
     */
    public void testExtendContract() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(cf, rl);
        // System.out.println("pf = " + pf);

        GenPolynomial<BigRational> a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        int k = rl;
        GenPolynomialRing<BigRational> pfe = pf.extend(k);
        GenPolynomialRing<BigRational> pfec = pfe.contract(k);
        assertEquals("pf == pfec", pf, pfec);

        GenPolynomial<BigRational> ae = a.extend(pfe, 0, 0);

        Map<ExpVector, GenPolynomial<BigRational>> m = ae.contract(pfec);
        List<GenPolynomial<BigRational>> ml = new ArrayList<GenPolynomial<BigRational>>(m.values());
        GenPolynomial<BigRational> aec = ml.get(0);
        assertEquals("a == aec", a, aec);
        //System.out.println("ae = " + ae);
        //System.out.println("aec = " + aec);
    }


    /**
     * Test reversion.
     */
    public void testReverse() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(cf, rl);
        //System.out.println("pf = " + pf);

        GenPolynomial<BigRational> a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        GenPolynomialRing<BigRational> pfr = pf.reverse();
        GenPolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);
        //System.out.println("pfr = " + pfr);

        GenPolynomial<BigRational> ar = a.reverse(pfr);
        GenPolynomial<BigRational> arr = ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);
    }


    /**
     * Test accessors.
     */
    public void testAccessors() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf, rl);
        // System.out.println("pf = " + pf);

        // test 1
        GenPolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);

        ExpVector e = p.leadingExpVector();
        BigRational c = p.leadingBaseCoefficient();

        GenPolynomial<BigRational> f = new GenPolynomial<BigRational>(pf, c, e);
        assertEquals("1 == 1 ", p, f);

        GenPolynomial<BigRational> r = p.reductum();
        assertTrue("red(1) == 0 ", r.isZERO());

        // test 0
        p = pf.getZERO();
        // System.out.println("p = " + p);
        e = p.leadingExpVector();
        c = p.leadingBaseCoefficient();

        f = new GenPolynomial<BigRational>(pf, c, e);
        assertEquals("0 == 0 ", p, f);

        r = p.reductum();
        assertTrue("red(0) == 0 ", r.isZERO());

        // test random
        p = pf.random(kl, 2 * ll, el, q);
        // System.out.println("p = " + p);
        e = p.leadingExpVector();
        c = p.leadingBaseCoefficient();
        r = p.reductum();

        f = new GenPolynomial<BigRational>(pf, c, e);
        f = r.sum(f);
        assertEquals("p == lm(f)+red(f) ", p, f);

        // test iteration over random
        GenPolynomial<BigRational> g;
        g = p;
        f = pf.getZERO();
        while (!g.isZERO()) {
            e = g.leadingExpVector();
            c = g.leadingBaseCoefficient();
            //System.out.println("c e = " + c + " " + e);
            r = g.reductum();
            f = f.sum(c, e);
            g = r;
        }
        assertEquals("p == lm(f)+lm(red(f))+... ", p, f);
    }


    /**
     * Test homogeneous.
     */
    public void testHomogeneous() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf, 2); //rl);
        // System.out.println("pf = " + pf);

        // test 1
        GenPolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);
        assertTrue("1 is homogeneous " + p, p.isHomogeneous());

        // test 0
        p = pf.getZERO();
        // System.out.println("p = " + p);
        assertTrue("0 is homogeneous " + p, p.isHomogeneous());

        // test random
        p = pf.random(kl, 2 * ll, el, q);
        //p = pf.random(kl,ll,el,q);
        //System.out.println("p = " + p);
        assertFalse("rnd is homogeneous " + p, p.isHomogeneous());

        // make homogeneous
        GenPolynomialRing<BigRational> pfh = pf.extend(1);
        //System.out.println("pfh = " + pfh);
        // remove block order
        TermOrder to = new TermOrder(TermOrder.IGRLEX);
        pfh = new GenPolynomialRing<BigRational>(pfh, to);
        //System.out.println("pfh = " + pfh);

        GenPolynomial<BigRational> ph = p.homogenize(pfh);
        //System.out.println("ph = " + ph);
        assertTrue("ph is homogeneous " + ph, ph.isHomogeneous());
        GenPolynomial<BigRational> ps = ph.deHomogenize(pf);
        //System.out.println("ps = " + ps);
        assertEquals("phs == p ", ps, p); // findbugs
        //System.out.println("p is homogeneous = " + p.isHomogeneous());
        //System.out.println("ph is homogeneous = " + ph.isHomogeneous());

        GenPolynomial<BigRational> s = pf.random(kl, ll, el, q);
        //System.out.println("s = " + s);
        assertFalse("rnd is homogeneous " + s, s.isHomogeneous() && !s.isConstant());

        GenPolynomial<BigRational> sh = s.homogenize(pfh);
        //System.out.println("sh = " + sh);
        assertTrue("sh is homogeneous " + sh, sh.isHomogeneous());
        GenPolynomial<BigRational> ss = sh.deHomogenize(pf);
        //System.out.println("ss = " + ss);
        assertEquals("ss = s ", ss, s);

        GenPolynomial<BigRational> th = ph.multiply(sh);
        //System.out.println("th = " + th);
        assertTrue("th is homogeneous " + th, th.isHomogeneous());

        GenPolynomial<BigRational> t = p.multiply(s);
        //System.out.println("t = " + t);

        GenPolynomial<BigRational> ts = th.deHomogenize(pf);
        //System.out.println("ts = " + ts);
        assertEquals("ts = t ", ts, t);
    }


    /**
     * Test weight homogeneous.
     */
    public void testWeightHomogeneous() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        // weight term order
        long[] weight = new long[] { 2, 3, 4, 5 };
        TermOrder to = TermOrderByName.weightOrder(weight);
        // System.out.println("to = " + to);

        // polynomials over rational numbers
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(rf, 4, to);
        // System.out.println("pf = " + pf.toScript());

        // test 1
        GenPolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);
        assertTrue("1 is weight homogeneous " + p, p.isWeightHomogeneous());

        // test 0
        p = pf.getZERO();
        // System.out.println("p = " + p);
        assertTrue("0 is weight homogeneous " + p, p.isWeightHomogeneous());

        // test random
        p = pf.random(kl, 3 * ll, el, q);
        // System.out.println("p = " + p);
        assertFalse("rnd is weight homogeneous " + p, p.isWeightHomogeneous());

        GenPolynomial<BigRational> pl = p.leadingWeightPolynomial();
        // System.out.println("pl = " + pl);
        assertTrue("lw(rnd) is weight homogeneous " + pl, pl.isWeightHomogeneous());

        GenPolynomial<BigRational> r = pf.random(kl, 3 * ll, el, q);
        // System.out.println("r = " + r);
        assertFalse("rnd is weight homogeneous " + r, r.isWeightHomogeneous());

        GenPolynomial<BigRational> rl = r.leadingWeightPolynomial();
        // System.out.println("rl = " + rl);
        assertTrue("lw(rnd) is weight homogeneous " + rl, rl.isWeightHomogeneous());

        GenPolynomial<BigRational> t = pl.multiply(rl);
        // System.out.println("t = " + t);
        assertTrue("lw(rnd)*lw(rnd) is weight homogeneous " + t, t.isWeightHomogeneous());
    }


    /**
     * Test univariate.
     */
    public void testUnivariate() {
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, rl);
        //System.out.println("pf = " + pf);

        GenPolynomial<BigInteger> a, b, c, d;

        // x**1
        a = pf.univariate(pf.nvar - 1);
        //System.out.println("a = " + a);
        assertTrue("deg(a) = 1: ", a.degree() == 1);
        assertEquals("xi == xi: ", pf.vars[0], a.toString());

        b = pf.univariate(pf.vars[0]);
        //System.out.println("b = " + b);
        assertTrue("deg(b) = 1: ", b.degree() == 1);
        assertEquals("xi == xi: ", pf.vars[0], b.toString());

        c = pf.univariate(0);
        //System.out.println("c = " + c);
        assertTrue("deg(c) = 1: ", c.degree() == 1);
        assertEquals("xi == xi: ", pf.vars[pf.nvar - 1], c.toString());

        d = pf.univariate(pf.vars[pf.nvar - 1]);
        //System.out.println("d = " + d);
        assertTrue("deg(c) = 1: ", c.degree() == 1);
        assertEquals("xi == xi: ", pf.vars[pf.nvar - 1], d.toString());

        // x**7
        a = pf.univariate(pf.nvar - 1, 7);
        //System.out.println("a = " + a);
        assertTrue("deg(a) = 7: ", a.degree() == 7);
        assertEquals("xi == xi: ", pf.vars[0] + "^7", a.toString());

        b = pf.univariate(pf.vars[0], 7);
        //System.out.println("b = " + b);
        assertTrue("deg(b) = 7: ", b.degree() == 7);
        assertEquals("xi == xi: ", pf.vars[0] + "^7", b.toString());

        c = pf.univariate(0, 7);
        //System.out.println("c = " + c);
        assertTrue("deg(c) = 7: ", c.degree() == 7);
        assertEquals("xi == xi: ", pf.vars[pf.nvar - 1] + "^7", c.toString());

        d = pf.univariate(pf.vars[pf.nvar - 1], 7);
        //System.out.println("d = " + d);
        assertTrue("deg(c) = 7: ", c.degree() == 7);
        assertEquals("xi == xi: ", pf.vars[pf.nvar - 1] + "^7", d.toString());
    }


    /**
     * Test iterators.
     */
    public void testIterators() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, rl);
        // System.out.println("pf = " + pf);

        // random polynomial
        GenPolynomial<BigInteger> p = pf.random(kl, 2 * ll, el, q);
        //System.out.println("p = " + p);

        // test monomials
        for (Monomial<BigInteger> m : p) {
            //System.out.println("m = " + m);
            assertFalse("m.c == 0 ", m.coefficient().isZERO());
            assertFalse("m.e < (0) ", m.exponent().signum() < 0);
        }

        // test exponents
        Iterator<ExpVector> et = p.exponentIterator();
        while (et.hasNext()) {
            ExpVector e = et.next();
            //System.out.println("e = " + e);
            assertFalse("e < (0) ", e.signum() < 0);
        }

        // test coefficents
        Iterator<BigInteger> ct = p.coefficientIterator();
        while (ct.hasNext()) {
            BigInteger i = ct.next();
            //System.out.println("i = " + i);
            assertFalse("i == 0 ", i.isZERO());
        }
    }


    /**
     * Test spliterators.
     */
    public void testSpliterators() {
        // integers
        BigInteger rf = new BigInteger();
        BigInteger num = rf.fromInteger(1);
        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, rl);
        //System.out.println("pf = " + pf.toScript());
        // random polynomial
        GenPolynomial<BigInteger> p = pf.random(kl, 22 * ll, el, q);
        //System.out.println("p = " + p.length());
        List<BigInteger> coeffs = new ArrayList<BigInteger>();

        // create spliterator and run on it
        PolySpliterator<BigInteger> psplit = new PolySpliterator<BigInteger>(p.val);
        //System.out.println("ps = " + psplit);
        //System.out.println("ps = "); printCharacteristic(psplit);
        //psplit.forEachRemaining( m -> System.out.print(m.c.toScript() + ", "));
        //System.out.println("\n");
        psplit.forEachRemaining( m -> coeffs.add(m.c.multiply(num)) );
        assertTrue("#coeffs == size: ", p.val.size() == coeffs.size());
        coeffs.clear();
        
        // create spliterator and split it
        //psplit = new PolySpliterator<BigInteger>(p.val);
        Spliterator<Monomial<BigInteger>> split = p.spliterator(); 
        //System.out.println("ps = " + split);
        //PolySpliterator<BigInteger> rest = split.trySplit();
        Spliterator<Monomial<BigInteger>> rest = split.trySplit(); 
        //System.out.println("rest = "); printCharacteristic(rest);
        //System.out.println("ps = "); printCharacteristic(split);

        //System.out.println("rest = " + rest);
        //rest.forEachRemaining( m -> System.out.print(m.c.toScript() + ", "));
        rest.forEachRemaining( m -> coeffs.add(m.c.multiply(num)) );
        //System.out.println("\nps = " + split);
        //split.forEachRemaining( m -> System.out.print(m.c.toScript() + ", "));
        split.forEachRemaining( m -> coeffs.add(m.c.multiply(num)) );
        assertTrue("#coeffs == size: ", p.val.size() == coeffs.size());

        assertTrue("coeffs == p.coefficients: ", ListUtil.<BigInteger>equals(coeffs,p.val.values()));
    }


    void printCharacteristic(Spliterator sp) {
        for (int c = 0x0; c < 0x4000; c++) {
            if (sp.hasCharacteristics(c)) {
                System.out.println(String.format("char: %3x", c));
            }
        }
    }

    
    /**
     * Test coefficient map function.
     */
    public void testMap() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, rl);
        // System.out.println("pf = " + pf);

        // random polynomial
        GenPolynomial<BigInteger> p = pf.random(kl, 2 * ll, el, q);
        //System.out.println("p = " + p);

        // test times 1
        GenPolynomial<BigInteger> q;
        q = p.map(new Multiply<BigInteger>(rf.getONE()));
        assertEquals("p == q ", p, q);

        // test times 0
        q = p.map(new Multiply<BigInteger>(rf.getZERO()));
        assertTrue("q == 0: " + q, q.isZERO());

        // test times -1
        q = p.map(new Multiply<BigInteger>(rf.getONE().negate()));
        assertEquals("p == q ", p.negate(), q);
    }

    
    /**
     * Test streams.
     */
    public void testStreams() {
        // modular integers
        ModIntegerRing rf = new ModIntegerRing(32003);
        ModInteger num = rf.fromInteger(-1);
        // polynomials over integral numbers
        GenPolynomialRing<ModInteger> pf = new GenPolynomialRing<ModInteger>(rf, rl);
        //System.out.println("pf = " + pf.toScript());
        // random polynomial
        GenPolynomial<ModInteger> p = pf.random(kl, 222 * ll, 2+el, q);
        //System.out.println("p = " + p.length());
        GenPolynomial<ModInteger> q;

        // negate coefficients 
        long tn = System.nanoTime();
        q = p.negate();
        tn = System.nanoTime() - tn;
        //System.out.println("q = " + q.length() + ", neg time = " + tn);
        assertTrue("time >= 0 ", tn >= 0);
        //System.out.println("p+q = " + p.sum(q));
        assertTrue("p+q == 0 ", p.sum(q).isZERO());

        // map multiply to coefficients
        long tm = System.nanoTime();
        q = p.map(c -> c.multiply(num));
        tm = System.nanoTime() - tm;
        //System.out.println("q = " + q.length() + ", old time = " + tm);
        assertTrue("time >= 0 ", tm >= 0);
        //System.out.println("p+q = " + p.sum(q));
        assertTrue("p+q == 0 ", p.sum(q).isZERO());

        // map multiply to coefficients stream
        long ts = System.nanoTime();
        q = p.mapOnStream(me -> new MapEntry<ExpVector,ModInteger>(me.getKey(), me.getValue().multiply(num)), false);
        ts = System.nanoTime() - ts;
        //System.out.println("q = " + q.length() + ", seq time = " + ts);
        assertTrue("time >= 0 ", ts >= 0);
        //System.out.println("p+q = " + p.sum(q));
        assertTrue("p+q == 0 ", p.sum(q).isZERO());

        // map multiply to coefficients parallel stream
        long tp = System.nanoTime();
        q = p.mapOnStream(me -> new MapEntry<ExpVector,ModInteger>(me.getKey(), me.getValue().multiply(num)), true);
        tp = System.nanoTime() - tp;
        //System.out.println("q = " + q.length() + ", par time = " + tp);
        assertTrue("time >= 0 ", tp >= 0);
        //System.out.println("p+q = " + p.sum(q));
        assertTrue("p+q == 0 ", p.sum(q).isZERO());
        System.out.println("map time: neg, old, seq, par, = " + tn + ", " + tm + ", " + ts + ", " + tp);

        //System.out.println("ForkJoinPool: " + ForkJoinPool.commonPool());
    }

    
    /**
     * Test bitLength.
     */
    public void testBitLength() {
        // integers
        BigInteger rf = new BigInteger();
        // System.out.println("rf = " + rf);

        // polynomials over integral numbers
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(rf, 5);
        // System.out.println("pf = " + pf);

        GenPolynomial<BigInteger> a, b, c;
        a = pf.getZERO();
        assertEquals("blen(0) = 0", 0, a.bitLength());

        a = pf.getONE();
        assertEquals("blen(1) = 7", 7, a.bitLength());

        // random polynomials
        a = pf.random(kl, 2 * ll, el, q);
        //System.out.println("a = " + a);
        //System.out.println("blen(a) = " + a.bitLength());
        assertTrue("blen(random) >= 0", 0 <= a.bitLength());

        b = pf.random(kl, 2 * ll, el, q);
        //System.out.println("b = " + b);
        //System.out.println("blen(b) = " + b.bitLength());
        assertTrue("blen(random) >= 0", 0 <= b.bitLength());

        //c = a.multiply(b);
        c = a.sum(b);
        //System.out.println("c = " + c);
        //System.out.println("blen(a)+blen(b) = " + (a.bitLength()+b.bitLength()));
        //System.out.println("blen(c) = " + c.bitLength());
        assertTrue("blen(random) >= 0", 0 <= c.bitLength());
        assertTrue("blen(random)+blen(random) >= blen(random+random)",
                   a.bitLength() + b.bitLength() >= c.bitLength());
    }

}


/**
 * Internal scalar multiplication functor.
 */
class Multiply<C extends RingElem<C>> implements UnaryFunctor<C, C> {


    C x;


    public Multiply(C x) {
        this.x = x;
    }


    public C eval(C c) {
        return c.multiply(x);
    }
}
