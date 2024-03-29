/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.vector.BasicLinAlg;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Reduction / normalform tests with JUnit.
 * @author Heinz Kredel
 */

public class ReductionTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ReductionTest</CODE> object.
     * @param name String
     */
    public ReductionTest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ReductionTest.class);
        return suite;
    }

    //private final static int bitlen = 100;

    GenPolynomialRing<BigRational> fac;


    GenPolynomial<BigRational> a, b, c, d, e;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F, G;


    ReductionSeq<BigRational> red;


    Reduction<BigRational> redpar;


    int rl = 4;


    int kl = 10;


    int ll = 11;


    int el = 5;


    float q = 0.6f;

    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new GenPolynomialRing<BigRational>(new BigRational(0), rl);
        red = new ReductionSeq<BigRational>();
        redpar = new ReductionPar<BigRational>();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        red = null;
        redpar = null;
    }


    /**
     * Test constants and empty list reduction.
     */
    public void testRatReduction0() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        c = fac.getONE();
        d = fac.getZERO();

        e = red.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L.add(c);
        e = red.normalform(L, c);
        assertTrue("isZERO( e )", e.isZERO());

        e = red.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(d);
        e = red.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = red.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());
    }


    /**
     * Test parallel reduction with constants and empty list reduction.
     */
    public void testRatReductionPar0() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        c = fac.getONE();
        d = fac.getZERO();

        e = redpar.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = redpar.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L.add(c);
        e = redpar.normalform(L, c);
        assertTrue("isZERO( e )", e.isZERO());

        e = redpar.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        e = redpar.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());


        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(d);
        e = redpar.normalform(L, c);
        assertTrue("isONE( e )", e.isONE());

        e = redpar.normalform(L, d);
        assertTrue("isZERO( e )", e.isZERO());
    }


    /**
     * Test rational coefficient reduction.
     */
    public void testRatReduction() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);

        e = red.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = red.normalform(L, a);
        assertTrue("isZERO( e ) some times", e.isZERO());

        e = red.SPolynomial(a, b);
        //System.out.println("e = " + e);
        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        ExpVector ee = e.leadingExpVector();
        assertTrue("lcm(lt(a),lt(b)) > lt(e) " + ce + " > " + ee,
                        fac.tord.getAscendComparator().compare(ce, ee) > 0); // findbugs

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);
        assertTrue("isTopRed( a )", red.isTopReducible(L, a));
        assertTrue("isRed( a )", red.isReducible(L, a));
        b = fac.random(kl, ll, el, q);
        L.add(b);
        assertTrue("isTopRed( b )", red.isTopReducible(L, b));
        assertTrue("isRed( b )", red.isReducible(L, b));
        c = fac.random(kl, ll, el, q);
        e = red.normalform(L, c);
        assertTrue("isNF( e )", red.isNormalform(L, e));
    }


    /**
     * Test rational coefficient parallel reduction.
     */
    public void testRatReductionPar() {
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);

        e = redpar.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = redpar.normalform(L, a);
        assertTrue("isZERO( e ) some times", e.isZERO());

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);
        assertTrue("isTopRed( a )", redpar.isTopReducible(L, a));
        assertTrue("isRed( a )", redpar.isReducible(L, a));
        b = fac.random(kl, ll, el, q);
        L.add(b);
        assertTrue("isTopRed( b )", redpar.isTopReducible(L, b));
        assertTrue("isRed( b )", redpar.isReducible(L, b));
        c = fac.random(kl, ll, el, q);
        e = redpar.normalform(L, c);
        assertTrue("isNF( e )", redpar.isNormalform(L, e));
    }


    /**
     * Test complex coefficient reduction.
     */
    public void testComplexReduction() {
        GenPolynomialRing<BigComplex> fac = new GenPolynomialRing<BigComplex>(new BigComplex(0), rl);

        Reduction<BigComplex> cred = new ReductionSeq<BigComplex>();

        GenPolynomial<BigComplex> a = fac.random(kl, ll, el, q);
        GenPolynomial<BigComplex> b = fac.random(kl, ll, el, q);
        GenPolynomial<BigComplex> c;

        assertTrue("not isZERO( a )", !a.isZERO());

        List<GenPolynomial<BigComplex>> L = new ArrayList<GenPolynomial<BigComplex>>();
        L.add(a);

        GenPolynomial<BigComplex> e = cred.normalform(L, a);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = cred.normalform(L, a);
        assertTrue("isZERO( e ) some times", e.isZERO());

        L = new ArrayList<GenPolynomial<BigComplex>>();
        L.add(a);
        assertTrue("isTopRed( a )", cred.isTopReducible(L, a));
        assertTrue("isRed( a )", cred.isReducible(L, a));
        b = fac.random(kl, ll, el, q);
        L.add(b);
        assertTrue("isTopRed( b )", cred.isTopReducible(L, b));
        assertTrue("isRed( b )", cred.isReducible(L, b));
        c = fac.random(kl, ll, el, q);
        e = cred.normalform(L, c);
        assertTrue("isNF( e )", cred.isNormalform(L, e));
    }


    /**
     * Test rational coefficient reduction with recording.
     */
    public void testRatReductionRecording() {
        BasicLinAlg<GenPolynomial<BigRational>> blas = new BasicLinAlg<GenPolynomial<BigRational>>();
        List<GenPolynomial<BigRational>> row = null;

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);
        row = blas.genVector(L.size(), null);
        e = red.normalform(row, L, a);
        assertTrue("isZERO( e )", e.isZERO());
        assertTrue("not isZERO( b )", !b.isZERO());
        assertTrue("is Reduction ", red.isReductionNF(row, L, a, e));

        L.add(b);
        row = blas.genVector(L.size(), null);
        e = red.normalform(row, L, b);
        assertTrue("is Reduction ", red.isReductionNF(row, L, b, e));

        L.add(c);
        row = blas.genVector(L.size(), null);
        e = red.normalform(row, L, c);
        assertTrue("is Reduction ", red.isReductionNF(row, L, c, e));

        L.add(d);
        row = blas.genVector(L.size(), null);
        e = red.normalform(row, L, d);
        assertTrue("is Reduction ", red.isReductionNF(row, L, d, e));

        L = new ArrayList<GenPolynomial<BigRational>>();
        L.add(a);
        L.add(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        row = blas.genVector(L.size(), null);
        e = red.SPolynomial(row, 0, a, 1, b);
        //System.out.println("e = " + e);
        //System.out.println("row = " + row);

        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        assertFalse("lcm(lt(a),lt(b)) != lt(e) ", ce.equals(e.leadingExpVector()));
        assertTrue("is Spol recording: " + e, red.isReductionNF(row, L, e.negate(), fac.getZERO()));
    }


    /**
     * Test integer coefficient e-reduction.
     */
    public void testIntegerEReduction() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(bi, rl);

        EReductionSeq<BigInteger> ered = new EReductionSeq<BigInteger>();

        GenPolynomial<BigInteger> a = fac.random(kl, ll, el, q);
        GenPolynomial<BigInteger> b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        List<GenPolynomial<BigInteger>> L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);

        GenPolynomial<BigInteger> e = ered.normalform(L, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = ered.normalform(L, a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e ) some times", e.isZERO());

        GenPolynomial<BigInteger> c = fac.getONE();
        a = a.sum(c);
        e = ered.normalform(L, a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isONE( e ) some times", e.isONE());

        L = new ArrayList<GenPolynomial<BigInteger>>();
        a = c.multiply(bi.fromInteger(4));
        b = c.multiply(bi.fromInteger(5));
        L.add(a);
        e = ered.normalform(L, b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isONE( e )", e.isONE());

        a = fac.random(kl, ll, el, q); //.abs();
        b = fac.random(kl, ll, el, q); //.abs();
        c = ered.GPolynomial(a, b);
        e = ered.SPolynomial(a, b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);

        BigInteger ci = a.leadingBaseCoefficient().gcd(b.leadingBaseCoefficient());
        assertEquals("gcd(lbc(a),lbc(b)) = lbc(c) ", ci, c.leadingBaseCoefficient());

        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        assertEquals("lcm(lt(a),lt(b)) == lt(c) ", ce, c.leadingExpVector());
        assertFalse("lcm(lt(a),lt(b)) != lt(e) ", ce.equals(e.leadingExpVector()));

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);
        assertTrue("isTopRed( a )", ered.isTopReducible(L, a));
        assertTrue("isRed( a )", ered.isReducible(L, a));
        b = fac.random(kl, ll, el, q);
        L.add(b);
        assertTrue("isTopRed( b )", ered.isTopReducible(L, b));
        assertTrue("isRed( b )", ered.isReducible(L, b));
        c = fac.random(kl, ll, el, q);
        e = ered.normalform(L, c);
        assertTrue("isNF( e )", ered.isNormalform(L, e));
    }


    /**
     * Test integer coefficient e-reduction recording.
     */
    public void testIntegerEReductionRecording() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(bi, rl);

        GenPolynomial<BigInteger> a, b, c, e;

        EReductionSeq<BigInteger> ered = new EReductionSeq<BigInteger>();

        BasicLinAlg<GenPolynomial<BigInteger>> blas = new BasicLinAlg<GenPolynomial<BigInteger>>();
        List<GenPolynomial<BigInteger>> row = null;
        List<GenPolynomial<BigInteger>> L = new ArrayList<GenPolynomial<BigInteger>>();

        a = fac.random(kl, ll, el, q).abs();

        assertTrue("not isZERO( a )", !a.isZERO());

        L.add(a);
        row = blas.genVector(L.size(), null);
        e = ered.normalform(row, L, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        assertTrue("isZERO( e )", e.isZERO());
        assertTrue("is Reduction ", ered.isReductionNF(row, L, a, e));

        row = blas.genVector(L.size(), null);
        c = fac.getONE();
        a = a.sum(c);
        e = ered.normalform(row, L, a);
        assertTrue("isONE( e ) some times", e.isONE());
        assertTrue("is Reduction ", ered.isReductionNF(row, L, a, e));
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);

        row = blas.genVector(L.size(), null);
        L = new ArrayList<GenPolynomial<BigInteger>>();
        a = c.multiply(bi.fromInteger(4));
        b = c.multiply(bi.fromInteger(5));

        L.add(a);
        e = ered.normalform(row, L, b);
        assertTrue("isONE( e )", e.isONE());
        assertTrue("is Reduction ", ered.isReductionNF(row, L, b, e));
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);

        a = fac.random(kl, ll, el, q).abs();
        b = fac.random(kl, ll / 2, el / 2, q).abs();
        c = fac.random(kl, ll * 2, el * 2, q * 1.0f).abs();
        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(b);
        L.add(a);
        row = blas.genVector(L.size(), null);
        e = ered.normalform(row, L, c);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        //assertTrue("isZERO( e )", e.isZERO() );
        assertTrue("is Reduction ", ered.isReductionNF(row, L, c, e));

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);
        L.add(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        row = blas.genVector(2, null);
        c = ered.GPolynomial(row, 0, a, 1, b);
        //System.out.println("c = " + c);
        //System.out.println("row = " + row);

        BigInteger ci = a.leadingBaseCoefficient().gcd(b.leadingBaseCoefficient());
        assertEquals("gcd(lbc(a),lbc(b)) = lbc(c) ", ci, c.leadingBaseCoefficient());
        assertTrue("is eGpol recording: " + c, ered.isReductionNF(row, L, c, fac.getZERO()));

        row = blas.genVector(2, null);
        e = ered.SPolynomial(row, 0, a, 1, b);
        //System.out.println("e = " + e);
        //System.out.println("row = " + row);

        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        assertEquals("lcm(lt(a),lt(b)) == lt(c) ", ce, c.leadingExpVector());
        assertFalse("lcm(lt(a),lt(b)) != lt(e) ", ce.equals(e.leadingExpVector()));

        assertTrue("is eSpol recording: " + e, ered.isReductionNF(row, L, e, fac.getZERO()));
    }


    /**
     * Test integer coefficient d-reduction.
     */
    public void testIntegerDReduction() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(bi, rl);

        DReductionSeq<BigInteger> dred = new DReductionSeq<BigInteger>();

        GenPolynomial<BigInteger> a = fac.random(kl, ll, el, q);
        GenPolynomial<BigInteger> b = fac.random(kl, ll, el, q);

        assertTrue("not isZERO( a )", !a.isZERO());

        List<GenPolynomial<BigInteger>> L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);

        GenPolynomial<BigInteger> e = dred.normalform(L, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO());

        assertTrue("not isZERO( b )", !b.isZERO());

        L.add(b);
        e = dred.normalform(L, a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e ) some times", e.isZERO());

        GenPolynomial<BigInteger> c = fac.getONE();
        a = a.sum(c);
        e = dred.normalform(L, a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isONE( e ) some times", e.isONE());

        L = new ArrayList<GenPolynomial<BigInteger>>();
        a = c.multiply(bi.fromInteger(5));
        L.add(a);
        b = c.multiply(bi.fromInteger(4));
        e = dred.normalform(L, b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("nf(b) = b ", e.equals(b));

        a = fac.random(kl, ll, el, q); //.abs();
        b = fac.random(kl, ll, el, q); //.abs();
        c = dred.GPolynomial(a, b);
        e = dred.SPolynomial(a, b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);

        BigInteger ci = a.leadingBaseCoefficient().gcd(b.leadingBaseCoefficient());
        assertEquals("gcd(lbc(a),lbc(b)) = lbc(c) ", ci, c.leadingBaseCoefficient());

        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        assertEquals("lcm(lt(a),lt(b)) == lt(c) ", ce, c.leadingExpVector());
        assertFalse("lcm(lt(a),lt(b)) != lt(e) ", ce.equals(e.leadingExpVector()));

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);
        assertTrue("isTopRed( a )", dred.isTopReducible(L, a));
        assertTrue("isRed( a )", dred.isReducible(L, a));
        b = fac.random(kl, ll, el, q);
        L.add(b);
        assertTrue("isTopRed( b )", dred.isTopReducible(L, b));
        assertTrue("isRed( b )", dred.isReducible(L, b));
        c = fac.random(kl, ll, el, q);
        e = dred.normalform(L, c);
        assertTrue("isNF( e )", dred.isNormalform(L, e));
    }


    /**
     * Test integer coefficient d-reduction recording.
     */
    public void testIntegerDReductionRecording() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac = new GenPolynomialRing<BigInteger>(bi, rl);

        GenPolynomial<BigInteger> a, b, c, e;

        DReductionSeq<BigInteger> dred = new DReductionSeq<BigInteger>();

        BasicLinAlg<GenPolynomial<BigInteger>> blas = new BasicLinAlg<GenPolynomial<BigInteger>>();
        List<GenPolynomial<BigInteger>> row = null;
        List<GenPolynomial<BigInteger>> L = new ArrayList<GenPolynomial<BigInteger>>();

        a = fac.random(kl, ll, el, q).abs();

        assertTrue("not isZERO( a )", !a.isZERO());

        L.add(a);
        row = blas.genVector(L.size(), null);
        e = dred.normalform(row, L, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        //System.out.println("dred.isReductionNF(row,L,a,e) = " + dred.isReductionNF(row,L,a,e));
        assertTrue("isZERO( e )", e.isZERO());
        assertTrue("is Reduction ", dred.isReductionNF(row, L, a, e));

        row = blas.genVector(L.size(), null);
        c = fac.getONE();
        a = a.sum(c);
        e = dred.normalform(row, L, a);
        assertTrue("isONE( e ) some times", e.isONE());
        assertTrue("is Reduction ", dred.isReductionNF(row, L, a, e));
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        //System.out.println("dred.isReductionNF(row,L,a,e) = " + dred.isReductionNF(row,L,a,e));

        row = blas.genVector(L.size(), null);
        L = new ArrayList<GenPolynomial<BigInteger>>();
        a = c.multiply(bi.fromInteger(4));
        b = c.multiply(bi.fromInteger(5));

        L.add(a);
        e = dred.normalform(row, L, b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        //System.out.println("dred.isReductionNF(row,L,b,e) = " + dred.isReductionNF(row,L,b,e));
        assertEquals("e == b", e, b);
        assertTrue("is Reduction ", dred.isReductionNF(row, L, b, e));

        a = fac.random(kl, ll, el, q).abs();
        b = fac.random(kl, ll / 2, el / 2, q).abs();
        //c = fac.random(kl, ll*2, el*2, q*1.0f ).abs();
        c = a.multiply(b);
        assertTrue("not isZERO( a )", !a.isZERO());

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(b);
        L.add(a);
        row = blas.genVector(L.size(), null);
        e = dred.normalform(row, L, c);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);
        //System.out.println("L = " + L);
        //System.out.println("row = " + row);
        //System.out.println("dred.isReductionNF(row,L,c,e) = " + dred.isReductionNF(row,L,c,e));
        assertTrue("isZERO( e )", e.isZERO());
        assertTrue("is Reduction ", dred.isReductionNF(row, L, c, e));

        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);
        L.add(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        row = blas.genVector(2, null);
        c = dred.GPolynomial(row, 0, a, 1, b);
        //System.out.println("c = " + c);
        //System.out.println("row = " + row);

        BigInteger ci = a.leadingBaseCoefficient().gcd(b.leadingBaseCoefficient());
        assertEquals("gcd(lbc(a),lbc(b)) = lbc(c) ", ci, c.leadingBaseCoefficient());

        assertTrue("is dGpol recording: " + c, dred.isReductionNF(row, L, c, fac.getZERO()));

        row = blas.genVector(2, null);
        e = dred.SPolynomial(row, 0, a, 1, b);
        //System.out.println("e = " + e);
        //System.out.println("row = " + row);

        ExpVector ce = a.leadingExpVector().lcm(b.leadingExpVector());
        assertEquals("lcm(lt(a),lt(b)) == lt(c) ", ce, c.leadingExpVector());
        assertFalse("lcm(lt(a),lt(b)) != lt(e) ", ce.equals(e.leadingExpVector()));

        assertTrue("is dSpol recording: " + e, dred.isReductionNF(row, L, e, fac.getZERO()));
    }

}
