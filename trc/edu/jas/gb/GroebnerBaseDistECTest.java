/*
 * $Id$
 */

package edu.jas.gb;


//import edu.jas.poly.GroebnerBase;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator; //import org.apache.log4j.Logger;

import edu.jas.kern.ComputerThreads;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;
import edu.jas.util.ExecutableServer;


/**
 * Distributed GroebnerBase tests with JUnit.
 * @author Heinz Kredel
 */

public class GroebnerBaseDistECTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBaseDistECTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        //ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GroebnerBaseDistECTest</CODE> object.
     * @param name String.
     */
    public GroebnerBaseDistECTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBaseDistECTest.class);
        return suite;
    }


    int port = 55711;


    String host = "localhost";


    String mfile = "examples/machines.localhost"; // contains localhost


    GenPolynomialRing<BigRational> fac;


    List<GenPolynomial<BigRational>> L;


    PolynomialList<BigRational> F;


    List<GenPolynomial<BigRational>> G;


    GroebnerBase<BigRational> bbseq;


    GroebnerBaseAbstract<BigRational> bbdist;


    GroebnerBaseAbstract<BigRational> bbdists;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    int rl = 3; //4; //3; 


    int kl = 4;


    int ll = 7;


    int el = 3;


    float q = 0.2f; //0.4f


    int threads = 2;


    ExecutableServer es1;


    ExecutableServer es2;


    @Override
    protected void setUp() {
        es1 = new ExecutableServer(4712); // == machines.localhost:4712
        es1.init();
        es2 = new ExecutableServer(4711); // == machines.localhost:4711
        es2.init();
        //System.out.println("es1 = " + es1);
        //System.out.println("es2 = " + es2);
        BigRational coeff = new BigRational(9);
        fac = new GenPolynomialRing<BigRational>(coeff, rl);
        a = b = c = d = e = null;
        bbseq = new GroebnerBaseSeq<BigRational>();
        bbdist = new GroebnerBaseDistributedEC<BigRational>(mfile, threads, port);
        //bbdists = new GroebnerBaseDistributedEC<BigRational>(mfile, threads, new OrderedSyzPairlist<BigRational>(), port);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        bbseq = null;
        bbdist.terminate();
        bbdist = null;
        //bbdists.terminate();
        //bbdists = null;
        es1.terminate();
        es2.terminate();
        es1 = null;
        es2 = null;
        ComputerThreads.terminate();
    }


    /**
     * Test distributed GBase corner cases.
     */
    public void testDistributedGBaseCorner() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.getZERO();

        L.add(a);
        L = bbdist.GB(L);
        assertTrue("isGB( { a } ): " + L, bbseq.isGB(L));
        assertTrue("L == {}: " + L, L.isEmpty());

        b = fac.getONE();

        L.add(b);
        L = bbdist.GB(L);
        assertTrue("isGB( { a } ): " + L, bbseq.isGB(L));
        assertTrue("L == {1}: " + L, L.size()==1);
    }


    /**
     * Test distributed GBase.
     */
    public void testDistributedGBase() {
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        L.add(a);
        L = bbdist.GB(L);
        assertTrue("isGB( { a } ): " + L, bbseq.isGB(L));

        L.add(b);
        //System.out.println("L = " + L.size() );
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b } ): " + L, bbseq.isGB(L));

        L.add(c);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c } ): " + L, bbseq.isGB(L));

        L.add(d);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c, d } ): " + L, bbseq.isGB(L));

        L.add(e);
        L = bbdist.GB(L);
        assertTrue("isGB( { a, b, c, d, e } ): " + L, bbseq.isGB(L));
    }


    /**
     * Test compare sequential with distributed GBase.
     */
    public void testSequentialDistributedGBase() {
        List<GenPolynomial<BigRational>> Gs, Gp = null;
        L = new ArrayList<GenPolynomial<BigRational>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.random(kl, ll, el, q);
        e = d; //fac.random(kl, ll, el, q );

        L.add(a);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(b);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(c);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(d);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));

        L = Gs;
        L.add(e);
        Gs = bbseq.GB(L);
        Gp = bbdist.GB(L);
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + L, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + L, Gp.containsAll(Gs));
    }


    /**
     * Test Trinks7 GBase.
     */
    public void testTrinks7GBase() {
        List<GenPolynomial<BigRational>> Gs, Gp = null;
        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
        //exam = "(x3,x4,x5) L " + 
        //       "( (x3^2 - 13974703710478159/3775194259200) , (x4 - 34297/840), (x5^2 - 6389/480), (-4/3 x5^2 + x3^2 + x3 - 833/180) ) ";
        //exam = "(x3,x4,x5) G " +
        //       "( x4^2 + 1809/30976 x3^2 + 190760/17787 x4 + 1755/10648 x3 + 296895202578451/10529038840320 , x3 * x4 - 15/64 x3^2 + 1223/294 x3 - 68247/58240 , x5 - 11/6 x4 - 3/16 x3 - 44162/4851 , x3^3 + 441280/68651 x3^2 + 29361376/2839655 x4 + 7055752657/687196510 x3 + 28334577136/417429285 , -8/9 x3^2 - 2 x5 + x3 - 159/44 )";

        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (IOException e) {
            fail("" + e);
        }
        //System.out.println("F = " + F);

        Gs = bbseq.GB(F.list);
        //System.out.println("Gs = " + Gs);
        Gp = bbdist.GB(F.list);
        //System.out.println("Gp = " + Gp);

        assertTrue("isGB( GB(Trinks7) )", bbseq.isGB(Gp));
        assertTrue("isGB( GB(Trinks7) )", bbseq.isGB(Gs));
        //assertEquals("#GB(Trinks7) == 6", 6, G.size());
        assertTrue("Gs.containsAll(Gp)" + Gs + ", " + Gp + ", " + F, Gs.containsAll(Gp));
        assertTrue("Gp.containsAll(Gs)" + Gs + ", " + Gp + ", " + F, Gp.containsAll(Gs));
        //PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, Gp);
        //System.out.println("G = " + trinks);
    }

}
