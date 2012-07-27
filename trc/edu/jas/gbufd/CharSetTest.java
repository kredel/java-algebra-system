/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Chararacteristic set tests with JUnit.
 * @author Heinz Kredel.
 */
public class CharSetTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>CharSetTest</CODE> object.
     * @param name String.
     */
    public CharSetTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CharSetTest.class);
        return suite;
    }


    CharacteristicSet<BigRational> cs;


    //TermOrder to = new TermOrder(TermOrder.INVLEX);
    TermOrder to = new TermOrder(TermOrder.IGRLEX);


    int rl = 3;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.29f;


    @Override
    protected void setUp() {
        cs = new CharacteristicSetWu<BigRational>();
    }


    @Override
    protected void tearDown() {
        cs = null;
    }


    /**
     * Test random characteristic set simple.
     */
    public void testCharacteristicSet() {
        CharacteristicSet<BigRational> css = new CharacteristicSetSimple<BigRational>();
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        dfac = new GenPolynomialRing<BigRational>(br, rl, to);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> a, b, c, d, e;
        List<GenPolynomial<BigRational>> F, G, W;

        F = new ArrayList<GenPolynomial<BigRational>>();
        a = dfac.random(kl, ll, el, q * 1.1f);
        b = dfac.random(kl, ll + 2, el, q);
        c = dfac.random(kl, ll, el, q);
        F.add(a);
        F.add(b);
        F.add(c);
        while (F.size() <= rl) {
            F.add(dfac.getZERO()); // make false cs
        }
        //F.add(dfac.fromInteger(17)); // test 1
        //System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, css.isCharacteristicSet(F));

        G = css.characteristicSet(F);
        //System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, css.isCharacteristicSet(G));

        e = PolyGBUtil.<BigRational> topPseudoRemainder(G, a);
        //System.out.println("a = " + a + ", deg_1 = " + a.degree(rl-1));
        //System.out.println("e = " + e + ", deg_1 = " + e.degree(rl-1));
        assertTrue("a rem G: " + e, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true

        e = PolyGBUtil.<BigRational> topPseudoRemainder(G, G.get(0));
        //System.out.println("e = " + e);
        assertTrue("a rem G: " + e + ", G = " + G, e.isZERO());

        e = css.characteristicSetReduction(G, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("a mod G: " + e, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true

        d = dfac.getONE();
        if (!G.contains(d)) {
            d = dfac.random(kl, ll, el, q).sum(d);
            //System.out.println("d = " + d);
            e = PolyGBUtil.<BigRational> topPseudoRemainder(G, d);
            //System.out.println("e = " + e);
            assertFalse("a rem G: " + e, e.isZERO());
            e = css.characteristicSetReduction(G, d);
            //System.out.println("e = " + e);
            assertFalse("a mod G: " + e, e.isZERO());
        }

        // now with Wu
        W = cs.characteristicSet(F);
        //System.out.println("F = " + F);
        //System.out.println("W = " + W);
        assertTrue("isCharacteristicSet: " + W, cs.isCharacteristicSet(W));

        e = PolyGBUtil.<BigRational> topPseudoRemainder(W, a);
        //System.out.println("a = " + a + ", deg = " + a.degree(rl-1));
        //System.out.println("e = " + e + ", deg = " + e.degree(rl-1));
        assertTrue("a rem W: " + e, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true

        e = PolyGBUtil.<BigRational> topPseudoRemainder(W, W.get(0));
        //System.out.println("e = " + e);
        assertTrue("a rem G: " + e + ", W = " + W, e.isZERO());

        e = cs.characteristicSetReduction(W, W.get(W.size() - 1));
        //System.out.println("e = " + e);
        assertTrue("a mod W: " + e, e.isZERO());

        e = cs.characteristicSetReduction(W, a);
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("a mod W: " + e, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true
    }


    /**
     * Test random characteristic set Wu.
     */
    public void testCharacteristicSetWu() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        dfac = new GenPolynomialRing<BigRational>(br, rl, to);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> a, b, c, d, e;
        List<GenPolynomial<BigRational>> F, G;

        F = new ArrayList<GenPolynomial<BigRational>>();
        a = dfac.random(kl, ll, el, q * 1.1f);
        b = dfac.random(kl, ll + 2, el, q);
        c = dfac.random(kl, ll, el, q);
        F.add(a);
        F.add(b);
        F.add(c);
        while (F.size() <= rl) {
            F.add(dfac.getZERO()); // make false cs
        }
        //F.add(dfac.fromInteger(19)); // test 1
        //System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, cs.isCharacteristicSet(F));

        G = cs.characteristicSet(F);
        //System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, cs.isCharacteristicSet(G));

        e = PolyGBUtil.<BigRational> topPseudoRemainder(G, a);
        //System.out.println("e = " + e);
        assertTrue("a rem G: " + e, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true

        e = cs.characteristicSetReduction(G, G.get(G.size() - 1));
        //System.out.println("e = " + e);
        assertTrue("a mod G: " + e, e.isZERO());

        e = cs.characteristicSetReduction(G, G.get(0));
        //System.out.println("e = " + e);
        assertTrue("a mod G: " + e, e.isZERO());

        e = cs.characteristicSetReduction(G, a);
        //System.out.println("e = " + e);
        assertTrue("a mod G: " + e + ", G = " + G, e.isZERO() || e.degree(rl - 1) < a.degree(rl - 1)); // not always true

        d = dfac.getONE();
        if (!G.contains(d)) {
            d = dfac.random(kl, ll, el, q).sum(d);
            //System.out.println("d = " + d);
            e = PolyGBUtil.<BigRational> topPseudoRemainder(G, d);
            //System.out.println("e = " + e);
            assertFalse("a rem G: " + e, e.isZERO());
            e = cs.characteristicSetReduction(G, d);
            //System.out.println("e = " + e);
            assertFalse("a mod G: " + e, e.isZERO());
        }
    }


    /**
     * Test characteristic set, example Circle of Apollonius, from CLO IVA,
     * 1992.
     */
    public void testCharacteristicSetExample() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "u1", "u2", "x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, h4, h5, h6, h7, h8, g, e;
        List<GenPolynomial<BigRational>> F, G;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" 2 x1 - u1 ");
        h2 = dfac.parse(" 2 x2 - u2 ");
        h3 = dfac.parse(" 2 x3 - u1 ");
        h4 = dfac.parse(" 2 x4 - u2 ");
        h5 = dfac.parse(" u2 x5 + u1 x6 - u1 u2 ");
        h6 = dfac.parse(" u1 x5 - u2 x6 ");
        h7 = dfac.parse(" x1^2 - x2^2 - 2 x1 x7 + 2 x2 x8 ");
        h8 = dfac.parse(" x1^2 - 2 x1 x7 - x3^2 + 2 x3 x7 - x4^2 + 2 x4 x8 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);
        F.add(h4);
        F.add(h5);
        F.add(h6);
        F.add(h7);
        F.add(h8);

        //System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, cs.isCharacteristicSet(F));

        G = cs.characteristicSet(F);
        //System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, cs.isCharacteristicSet(G));

        g = dfac.parse("( ( x5 - x7 )**2 + ( x6 - x8 )**2 - ( x1 - x7 )**2 - x8^2 )");
        //g = dfac.parse("-2 x6 * x8 - 2 x5 * x7 + 2 x1 * x7 + x6^2 + x5^2 - x1^2");
        //System.out.println("g = " + g);

        e = cs.characteristicSetReduction(G, g);
        //e = PolyGBUtil.<BigRational> topPseudoRemainder(G, g);
        //System.out.println("e = " + e);
        assertTrue("g mod G: " + e, e.isZERO()); // || true ?not always true

        //GroebnerBaseAbstract<BigRational> bb = GBFactory.getImplementation(br);
        //H = bb.GB(F);
        //System.out.println(" H = " + H);

        //Reduction<BigRational> red = bb.red;
        //f = red.normalform(H, g);
        //System.out.println("fg = " + f);

        //k = red.normalform(H, e);
        //System.out.println("fk' = " + k);

        //System.out.println("fk' == f: " + k.equals(f));
        //System.out.println("fk' - f: " + k.subtract(f));

        //K = red.normalform(H, G);
        //System.out.println("Kg = " + K);
        //L = red.normalform(H, F);
        //System.out.println("Lf = " + L);
        //assertEquals("Kg == Kf: " + L, K, L);
    }


    /**
     * Test characteristic set, example circum-center.
     */
    public void ytestCharacteristicSetExampleCC() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "u1", "u2", "u3", "x1", "x2", "x3" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, g, e;
        List<GenPolynomial<BigRational>> F, G;

        F = new ArrayList<GenPolynomial<BigRational>>();
        // wrong:
        h1 = dfac.parse(" 2 u1 x2 - u1^2 ");
        h2 = dfac.parse(" 2 u2 x2 + 2 u3 x1 - u2^2 - u3^2 ");
        h3 = dfac.parse(" 2 u3 x3 - 2 ( u2 - u1 ) x2 - u2^2 - u3^2 + u1^2 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);

        System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, cs.isCharacteristicSet(F));

        G = cs.characteristicSet(F);
        System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, cs.isCharacteristicSet(G));

        g = dfac.parse(" x3^2 - 2 x3 x1 + x1^2  ");
        //g = dfac.parse(" x3 - x1 ");
        System.out.println("g = " + g);

        e = cs.characteristicSetReduction(G, g);
        //e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,g);
        System.out.println("e = " + e);
        assertTrue("g mod G: " + e, e.isZERO() || e.degree(rl - 1) < g.degree(rl - 1)); // not always true
    }


    /**
     * Test characteristic set, example secant from wang.
     */
    public void testCharacteristicSetExampleSec() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "u1", "u2", "u3", "u4", "y1", "y2", "y3" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3, g, e;
        List<GenPolynomial<BigRational>> F, G;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" 2 u3 y1 - u2^2 + u1^2 - u3^2 ");
        h2 = dfac.parse(" - y2^2 + 2 y1 y2 + u1^2 - u4^2 ");
        h3 = dfac.parse(" - y2 y3 + u3 y3 + y2 u2 - u4 u3 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);

        //System.out.println("F = " + F);
        assertTrue("isCharacteristicSet: " + F, cs.isCharacteristicSet(F)); // already CS

        G = cs.characteristicSet(F);
        //System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, cs.isCharacteristicSet(G));

        g = dfac.parse("( (y3 + u1)^2 (y3 - u1)^2 - ( (y3 - u2)^2 + u3^2 ) ( (y3 - u4)^2 + y2^2 ) )");
        //g = dfac.parse(" x3 - x1 ");
        //System.out.println("g = " + g);

        e = cs.characteristicSetReduction(G, g);
        //e = PolyGBUtil.<BigRational> characteristicSetRemainder(G,g);
        //System.out.println("e = " + e);
        assertTrue("g mod G: " + e, e.isZERO());
    }


    /**
     * Test characteristic set, example ??.
     */
    public void testCharacteristicSetExampleT() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);

        GenPolynomial<BigRational> h1, h2, h3;
        List<GenPolynomial<BigRational>> F, G;

        F = new ArrayList<GenPolynomial<BigRational>>();
        h1 = dfac.parse(" x^2 + y + z - 1 ");
        h2 = dfac.parse(" x + y^2 + z - 1 ");
        h3 = dfac.parse(" x + y + z^2 - 1 ");

        F.add(h1);
        F.add(h2);
        F.add(h3);

        //System.out.println("F = " + F);
        assertFalse("isCharacteristicSet: " + F, cs.isCharacteristicSet(F));

        G = cs.characteristicSet(F);
        //System.out.println("G = " + G);
        assertTrue("isCharacteristicSet: " + G, cs.isCharacteristicSet(G));
    }

}
