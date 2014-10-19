/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Groebner base recursive pseudo reduction sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class GroebnerBasePseudoRecSeqTest extends TestCase {


    //private static final Logger logger = Logger.getLogger(GroebnerBasePseudoRecSeqTest.class);

    /**
     * main
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>GroebnerBasePseudoRecSeqTest</CODE> object.
     * @param name String.
     */
    public GroebnerBasePseudoRecSeqTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GroebnerBasePseudoRecSeqTest.class);
        return suite;
    }


    GenPolynomialRing<GenPolynomial<BigInteger>> fac;


    List<GenPolynomial<GenPolynomial<BigInteger>>> L;


    PolynomialList<GenPolynomial<BigInteger>> F;


    List<GenPolynomial<GenPolynomial<BigInteger>>> G;


    GroebnerBaseAbstract<GenPolynomial<BigInteger>> bb;


    GroebnerBaseAbstract<GenPolynomial<BigRational>> bbr;


    GenPolynomial<GenPolynomial<BigInteger>> a, b, c, d, e;


    int rl = 2; //4; //3; 


    int kl = 2;


    int ll = 5;


    int el = 4;


    float q = 0.3f; //0.4f


    @Override
    protected void setUp() {
        BigInteger coeff = new BigInteger(9);
        GenPolynomialRing<BigInteger> cofac = new GenPolynomialRing<BigInteger>(coeff, 1);
        fac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cofac, rl);
        a = b = c = d = e = null;
        bb = new GroebnerBasePseudoRecSeq<BigInteger>(cofac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        //bb.terminate();
        fac = null;
        bb = null;
    }


    /**
     * Test recursive sequential GBase.
     */
    public void testRecSequentialGBase() {

        L = new ArrayList<GenPolynomial<GenPolynomial<BigInteger>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = fac.random(kl, ll, el, q);
        d = fac.getZERO(); //fac.random(kl, ll, el, q);
        e = c; //fac.random(kl, ll, el, q );

        L.add(a);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a } )", bb.isGB(L));

        L.add(b);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b } )", bb.isGB(L));

        if (bb.commonZeroTest(L) < 0) {
            L.clear();
        }
        L.add(c);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c } )", bb.isGB(L));

        L.add(d);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d } )", bb.isGB(L));

        L.add(e);
        System.out.println("L = " + L );

        L = bb.GB(L);
        assertTrue("isGB( { a, b, c, d, e } )", bb.isGB(L));
    }


    /**
     * Test Hawes2 GBase.
     */
    @SuppressWarnings("unchecked")
    public void testHawes2GBase() {
        String exam = "IntFunc(a, c, b) (y2, y1, z1, z2, x) G"
                      + "(" 
                      + "( x + 2 y1 z1 + { 3 a } y1^2 + 5 y1^4 + { 2 c } y1 ),"
                      + "( x + 2 y2 z2 + { 3 a } y2^2 + 5 y2^4 + { 2 c } y2 )," 
                      + "( 2 z2 + { 6 a } y2 + 20 y2^3 + { 2 c } )," 
                      + "( 3 z1^2 + y1^2 + { b } )," 
                      + "( 3 z2^2 + y2^2 + { b } )" 
                      + ")";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<GenPolynomial<BigRational>> Fr = null;
        GenPolynomialRing<BigRational> cofac;
        GenPolynomialRing<BigInteger> ifac = null;
        try {
            Fr = parser.nextPolynomialSet();
            cofac = (GenPolynomialRing<BigRational>) Fr.ring.coFac;
            ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),cofac);
            fac = new GenPolynomialRing<GenPolynomial<BigInteger>>(ifac,Fr.ring);
            L = PolyUfdUtil.integerFromRationalCoefficients(fac, Fr.list);
        } catch (ClassCastException e) {
            fail("" + e);
        } catch (IOException e) {
            fail("" + e);
        }
        System.out.println("F = " + L);
        System.out.println("Fr = " + Fr);

        long s, t, q, i;
        t = System.currentTimeMillis();
        G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        assertTrue("isGB( GB(Hawes2) )", bb.isGB(G));
        assertEquals("#GB(Hawes2) == 8", 8, G.size());
        PolynomialList<GenPolynomial<BigInteger>> 
            Gp = new PolynomialList<GenPolynomial<BigInteger>>(fac,G);
        System.out.println("G = " + Gp);
        assertTrue("nonsense ", t >= 0L);


        GenPolynomialRing<BigRational> rfac = (GenPolynomialRing<BigRational>) Fr.ring.coFac;
        List<GenPolynomial<GenPolynomial<BigRational>>> Gr, Kr, Lr = Fr.list;
        //System.out.println("Fr = " + Fr);

        bbr = new GroebnerBasePseudoRecSeq<BigRational>(rfac);
        s = System.currentTimeMillis();
        Gr = bbr.GB(Lr);
        s = System.currentTimeMillis() - s;
        Gr = PolyUtil.<BigRational>monicRec(Gr);
        System.out.println("Gr = " + Gr);

        Kr = PolyUfdUtil.<BigRational>fromIntegerCoefficients(Fr.ring, G);
        Kr = PolyUtil.<BigRational>monicRec(Kr);
        //System.out.println("Kr = " + Kr);

        //List<GenPolynomial<GenPolynomial<BigInteger>>> Li;
        //Li = PolyUfdUtil.integerFromRationalCoefficients(fac, Gr);
        //System.out.println("Li = " + Li);

        assertEquals("ratGB == intGB", Kr, Gr);
        assertTrue("nonsense ", s >= 0L);


        QuotientRing<BigRational> qr = new QuotientRing<BigRational>(rfac);
        GenPolynomialRing<Quotient<BigRational>> rring = new GenPolynomialRing<Quotient<BigRational>>(qr,fac);

        List<GenPolynomial<Quotient<BigRational>>> Gq, Lq, Kq;
        Lq = PolyUfdUtil.<BigRational> quotientFromIntegralCoefficients(rring, Lr);
        Lq = PolyUtil.<Quotient<BigRational>> monic(Lq);

        GroebnerBaseAbstract<Quotient<BigRational>> bbq;
        bbq = new GroebnerBaseSeq<Quotient<BigRational>>(); //qr);

        q = System.currentTimeMillis();
        Gq = bbq.GB(Lq);
        q = System.currentTimeMillis() - q;
        assertTrue("isGB( GB(Hawes2) )", bbq.isGB(Gq));
        assertEquals("#GB(Hawes2) == 8", 8, Gq.size());
        PolynomialList<Quotient<BigRational>> 
            Gpq = new PolynomialList<Quotient<BigRational>>(rring,Gq);
        System.out.println("Gq = " + Gpq);
        assertTrue("nonsense ", q >= 0L);

        Kq = PolyUfdUtil.<BigRational> quotientFromIntegralCoefficients(rring, Gr);
        Kq = PolyUtil.<Quotient<BigRational>> monic(Kq);
        assertEquals("ratGB == quotGB", Kq, Gq);


        QuotientRing<BigInteger> qi = new QuotientRing<BigInteger>(ifac);
        GenPolynomialRing<Quotient<BigInteger>> iring = new GenPolynomialRing<Quotient<BigInteger>>(qi,fac);

        List<GenPolynomial<Quotient<BigInteger>>> Gqi, Lqi, Kqi;
        Lqi = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(iring, L);
        Lqi = PolyUtil.<Quotient<BigInteger>> monic(Lqi);
        System.out.println("Lqi = " + Lqi);

        GroebnerBaseAbstract<Quotient<BigInteger>> bbqi;
        bbqi = new GroebnerBaseSeq<Quotient<BigInteger>>(); //qr);

        i = System.currentTimeMillis();
        Gqi = bbqi.GB(Lqi);
        i = System.currentTimeMillis() - i;
        assertTrue("isGB( GB(Hawes2) )", bbqi.isGB(Gqi));
        assertEquals("#GB(Hawes2) == 8", 8, Gqi.size());
        PolynomialList<Quotient<BigInteger>> 
            Gpqi = new PolynomialList<Quotient<BigInteger>>(iring,Gqi);
        System.out.println("Gpqi = " + Gpqi);
        assertTrue("nonsense ", i >= 0L);

        Kqi = PolyUfdUtil.<BigInteger> quotientFromIntegralCoefficients(iring, G);
        Kqi = PolyUtil.<Quotient<BigInteger>> monic(Kqi);
        assertEquals("quotRatGB == quotIntGB", Gqi, Kqi);

        System.out.println("time: ratGB = " + s + ", intGB = " + t + ", quotRatGB = " + q + ", quotIntGB = " + i);
    }

}
