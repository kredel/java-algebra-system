/*
 * $Id$
 */

package edu.jas.application;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.gb.DGroebnerBaseSeq;
import edu.jas.gb.EGroebnerBaseSeq;
import edu.jas.gb.GBOptimized;
import edu.jas.gb.GBProxy;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.GroebnerBaseSeqIter;
import edu.jas.gb.GroebnerBaseSigSeqIter;
import edu.jas.gb.GroebnerBaseF5zSigSeqIter;
import edu.jas.gb.GroebnerBaseGGVSigSeqIter;
import edu.jas.gb.GroebnerBaseArriSigSeqIter;
import edu.jas.gb.GroebnerBaseParIter;
import edu.jas.gbufd.GBFactory;
import edu.jas.gbufd.GroebnerBaseFGLM;
import edu.jas.gbufd.GroebnerBaseWalk;
import edu.jas.gbufd.GroebnerBasePseudoSeq;
import edu.jas.gbufd.GroebnerBaseRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * GBAlgorithmBuilder tests with JUnit.
 * @author Heinz Kredel
 */

public class GBAlgorithmBuilderTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GBAlgorithmBuilderTest</CODE> object.
     * @param name String.
     */
    public GBAlgorithmBuilderTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GBAlgorithmBuilderTest.class);
        return suite;
    }


    GBAlgorithmBuilder builder;


    @Override
    protected void setUp() {
        builder = null;
    }


    @Override
    protected void tearDown() {
        builder = null;
        ComputerThreads.terminate();
    }


    /**
     * Test basic construction for BigRational.
     */
    public void testConstructionRational() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseSeq);
    }


    /**
     * Test construction for BigRational and FGLM.
     */
    public void testConstructionRationalFGLM() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.graded();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseFGLM);
    }


    /**
     * Test construction for BigRational and Groebner walk.
     */
    public void testConstructionRationalWalk() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.walk();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseWalk);
    }


    /**
     * Test construction for BigRational and parallel.
     */
    public void testConstructionRationalParallel() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.parallel();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBProxy);

        GBProxy<BigRational> bbp = (GBProxy<BigRational>) bb;
        assertTrue("instance of " + bbp.e1, bbp.e1 instanceof GroebnerBaseSeq);
        assertTrue("instance of " + bbp.e2, bbp.e2 instanceof GroebnerBaseParallel);
    }


    /**
     * Test construction for BigRational fraction free and parallel.
     */
    public void testConstructionRationalFFParallel() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.fractionFree();
        //System.out.println("ab = " + ab);

        ab = ab.parallel();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBProxy);

        GBProxy<BigRational> bbp = (GBProxy<BigRational>) bb;
        assertTrue("instance of " + bbp.e1, bbp.e1 instanceof GroebnerBaseRational);
        assertTrue("instance of " + bbp.e2, bbp.e2 instanceof GroebnerBaseRational);
    }


    /**
     * Test construction for BigRational and optimize.
     */
    public void testConstructionRationalOptimized() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.optimize();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBOptimized);
    }


    /**
     * Test construction for BigRational and fraction free.
     */
    public void testConstructionRationalFF() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.fractionFree();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseRational);
    }


    /**
     * Test basic construction for BigInteger.
     */
    public void testConstructionInteger() {
        BigInteger bf = new BigInteger(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(bf, vars);

        GBAlgorithmBuilder<BigInteger> ab = GBAlgorithmBuilder.<BigInteger> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigInteger> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBasePseudoSeq);
    }


    /**
     * Test construction for d-GB BigInteger.
     */
    public void testConstructionIntegerDGB() {
        BigInteger bf = new BigInteger(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(bf, vars);

        GBAlgorithmBuilder<BigInteger> ab = GBAlgorithmBuilder.<BigInteger> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.domainAlgorithm(GBFactory.Algo.dgb);
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigInteger> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof DGroebnerBaseSeq);
    }


    /**
     * Test construction for e-GB BigInteger.
     */
    public void testConstructionIntegerEGB() {
        BigInteger bf = new BigInteger(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigInteger> pf = new GenPolynomialRing<BigInteger>(bf, vars);

        GBAlgorithmBuilder<BigInteger> ab = GBAlgorithmBuilder.<BigInteger> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.domainAlgorithm(GBFactory.Algo.egb);
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigInteger> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof EGroebnerBaseSeq);
    }


    /**
     * Test construction for BigRational and more.
     */
    public void testConstructionRationalMore() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GroebnerBaseAbstract<BigRational> bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf)
                        .fractionFree().optimize().build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBOptimized);

        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().parallel().optimize().build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBOptimized);

        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().graded().parallel()
                        .optimize().build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBOptimized);
    }


    /**
     * Test construction for BigRational and more and compute.
     */
    public void testConstructionRationalMoreCompute() {
        List<GenPolynomial<BigRational>> cp = ExamplesGeoTheorems.getExample();
        GenPolynomialRing<BigRational> pf = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> bb;
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().parallel().optimize().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).parallel().optimize().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).build();
        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).optimize().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).parallel().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().optimize().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).parallel().fractionFree().optimize().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).optimize().fractionFree().build();
        //bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).fractionFree().optimize().parallel().build();
        //System.out.println("bb = " + bb);
        //assertTrue("instance of " + bb, bb instanceof GBOptimized);

        List<GenPolynomial<BigRational>> gb;
        long t;
        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);

        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);

        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);
        assertTrue("t >= 0: ", t >= 0L); // praise findbugs

        assertTrue("isGB: ", bb.isGB(gb));
        bb.terminate();
        //System.out.println("gb = " + gb);
        //System.out.println("bb = " + bb);
    }


    /**
     * Test construction for BigRational and pairlists and compute.
     */
    public void testConstructionRationalParilistCompute() {
        List<GenPolynomial<BigRational>> cp = ExamplesGeoTheorems.getExample();
        GenPolynomialRing<BigRational> pf = cp.get(0).ring;

        GroebnerBaseAbstract<BigRational> bb;
        List<GenPolynomial<BigRational>> gb;
        long t;

        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).normalPairlist().build();
        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);
        assertTrue("isGB: ", bb.isGB(gb));

        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).syzygyPairlist().build();
        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);
        assertTrue("isGB: ", bb.isGB(gb));

        bb = GBAlgorithmBuilder.<BigRational> polynomialRing(pf).simplePairlist().build();
        t = System.currentTimeMillis();
        gb = bb.GB(cp);
        t = System.currentTimeMillis() - t;
        //System.out.println("time(gb) = " + t);
        assertTrue("isGB: ", bb.isGB(gb));

        assertTrue("t >= 0: ", t >= 0L); // for findbugs
    }


    /**
     * Test construction for BigRational and iterate.
     */
    public void testConstructionRationalIterate() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.iterated();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseSeqIter);

        ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        ab = ab.iterated().parallel();
        //System.out.println("ab = " + ab);

        bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GBProxy);

        GBProxy<BigRational> bbp = (GBProxy<BigRational>) bb;
        assertTrue("instance of " + bbp.e1, bbp.e1 instanceof GroebnerBaseSeqIter);
        assertTrue("instance of " + bbp.e2, bbp.e2 instanceof GroebnerBaseParIter);
    }


    /**
     * Test construction for BigRational and signature based GBs.
     */
    public void testConstructionRationalSignatureBased() {
        BigRational bf = new BigRational(1);
        String[] vars = new String[] { "a", "b", "c" };
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, vars);

        GBAlgorithmBuilder<BigRational> ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        //System.out.println("ab = " + ab);

        ab = ab.F5();
        //System.out.println("ab = " + ab);

        GroebnerBaseAbstract<BigRational> bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseSigSeqIter);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseF5zSigSeqIter);


        ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        ab = ab.GGV();
        //System.out.println("ab = " + ab);

        bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseSigSeqIter);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseGGVSigSeqIter);


        ab = GBAlgorithmBuilder.<BigRational> polynomialRing(pf);
        ab = ab.Arri();
        //System.out.println("ab = " + ab);

        bb = ab.build();
        //System.out.println("bb = " + bb);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseSigSeqIter);
        assertTrue("instance of " + bb, bb instanceof GroebnerBaseArriSigSeqIter);
    }

}
