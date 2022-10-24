/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingFactory;
import edu.jas.gbufd.RReduction;
import edu.jas.gbufd.RReductionSeq;
import edu.jas.gbufd.RGroebnerBasePseudoSeq;


/**
 * Comprehenssive Groebner base sequential tests with JUnit.
 * @author Heinz Kredel
 */

public class CGBSeqTest extends TestCase {



    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>CGBSeqTest</CODE> object.
     * @param name String.
     */
    public CGBSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CGBSeqTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> cfac;


    GenPolynomialRing<GenPolynomial<BigRational>> fac;


    List<GenPolynomial<GenPolynomial<BigRational>>> L;


    ComprehensiveGroebnerBaseSeq<BigRational> bb;


    GenPolynomial<GenPolynomial<BigRational>> a, b, c, d, e;


    int rl = 2; //4; //3; 


    int kl = 2;


    int ll = 3;


    int el = 3+1;


    float q = 0.3f; //0.4f


    @Override
    protected void setUp() {
        BigRational coeff = new BigRational(kl);
        String[] cv = { "a" }; //, "b" }; 
        cfac = new GenPolynomialRing<BigRational>(coeff, 1, cv);
        String[] v = { "x" }; //, "y" }; 
        fac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac, 1, v);
        a = b = c = d = e = null;
        bb = new ComprehensiveGroebnerBaseSeq<BigRational>(coeff);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        cfac = null;
        bb = null;
    }


    /*
     * Dummy test method for jUnit.
     * 
    public void testDummy() {
    }
     */


    /**
     * Test sequential CGB.
     */
    public void testSequentialCGB() {
        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();
        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = a; //fac.random(kl, ll, el, q );
        d = c; //fac.random(kl, ll, el, q );
        e = d; //fac.random(kl, ll, el, q );

        L = fac.generators();
        //System.out.println("generators: " + L);
        int k = 2;
        if (a.isZERO()) {
            a = L.get(k);
        }
        if (b.isZERO()) {
            b = L.get(k);
        }
        if (c.isZERO()) {
            c = L.get(k);
        }
        if (d.isZERO()) {
            d = L.get(k);
            e = d;
        }

        L.clear();
        assertTrue("not isZERO( a )", !a.isZERO());
        L.add(a);
        //System.out.println("L = " + L);

        L = bb.GB(L);
        //System.out.println("L = " + L);
        assertTrue("isGB( { a } )", bb.isGB(L));
        //System.out.println("isGB: " + L);
        if (L.contains(fac.getONE())) {
            L.clear();
        }
        assertTrue("not isZERO( b )", !b.isZERO());
        L.add(b);
        //System.out.println("L = " + L);

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));
        //System.out.println("L = " + L);

        assertTrue("not isZERO( c )", !c.isZERO());
        L.add(c);
        //System.out.println("L = " + L);

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));
        //System.out.println("L = " + L);
    }


    /**
     * Test Trinks CGB.
     */
    @SuppressWarnings("unchecked")
    public void testTrinks7GBase() {
        PolynomialList<GenPolynomial<BigRational>> F = null;
        List<GenPolynomial<GenPolynomial<BigRational>>> G = null;
        String exam = "IntFunc(b) (S,T,Z,P,W) L "
               + "( "
               + "( 45 P + 35 S - { 165 b + 36 } ), "
               + "( 35 P + 40 Z + 25 T - 27 S ), "
               + "( 15 W + 25 S P + 30 Z - 18 T - { 165 b**2 } ), "
               + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - { 11 b**3 } ), "
               + "( 99 W - { 11 b } S + { 3 b**2 } ), " + "( { b**2 + 33/50 b + 2673/10000 } ) "
               + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<GenPolynomial<BigRational>>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        G = bb.GB(F.list);
        assertTrue("isGB( GB(Trinks7) )", bb.isGB(G));

        //PolynomialList<GenPolynomial<BigRational>> trinks 
        //    = new PolynomialList<GenPolynomial<BigRational>>(F.ring,G);
        //System.out.println("G = " + trinks);
        //System.out.println("G = " + G);
    }


    /**
     * Test Raksanyi &amp; Walter example comprehensive GB and regular GB.
     */
    @SuppressWarnings("unchecked")
    public void testRaksanyiCGBase() {
        PolynomialList<GenPolynomial<BigRational>> F = null;
        String exam = "IntFunc(a1,a2,a3,a4) (x1,x2,x3,x4) L "
               + "( "
               + "( x4 - ( a4 - a2 ) ), "
               + "( x1 + x2 + x3 + x4 - ( a1 + a3 + a4 ) ), "
               + "( x1 * x3 + x1 * x4 + x2 * x3 + x3 * x4 - ( a1 * a4 + a1 * a3 + a3 * a4 ) ), "
               + "( x1 * x3 * x4 - ( a1 * a3 * a4 ) ) "
               + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<GenPolynomial<BigRational>>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F.toScript());

        // as comprehensive Gröbner system
        GroebnerSystem<BigRational> GS;
        List<GenPolynomial<GenPolynomial<BigRational>>> G;

        GS = bb.GBsys(F.list);
        //System.out.println("GBsys(F) = GS = " + GS);
        assertTrue("isGBsys( GBsys(Raksanyi) )", bb.isGB(GS));
        String s = GS.toString() + ", " + GS.toScript();
        //System.out.println("s = " + s + ", " + s.length());
        assertTrue("length( string(GS) ) >= 10500", s.length() >= 10500);

        G = GS.getCGB();
        //System.out.println("cgb( GS ) = " + G);
        assertTrue("isCGB( CGB(Raksanyi) )", bb.isGB(G));

        // as regular ring
        RReductionSeq<Product<Residue<BigRational>>> res;
        RGroebnerBasePseudoSeq<Product<Residue<BigRational>>> rbb;
        List<GenPolynomial<Product<Residue<BigRational>>>> Gr, Grbc, GBr;

        Gr = PolyUtilApp.<BigRational> toProductRes(GS.list); // list of colored systems
        //System.out.println("Gr = " + Gr);
        res = new RReductionSeq<Product<Residue<BigRational>>>();
        Grbc = res.booleanClosure(Gr);
        //System.out.println("Grbc = " + Grbc);

        RingFactory<Product<Residue<BigRational>>> cofac = Grbc.get(0).ring.coFac;
        //System.out.println("cofac = " + cofac.toScript());
        rbb = new RGroebnerBasePseudoSeq<Product<Residue<BigRational>>>(cofac);
        //System.out.println("isGB(Grbc) = " + rbb.isGB(Grbc));
        assertTrue("isGB(Grbc)", rbb.isGB(Grbc));

        GBr = rbb.GB(Grbc);
        //System.out.println("GBr = " + GBr);
        assertTrue("isRGB( RGB(Raksanyi) )", rbb.isGB(GBr));
    }

}
