/*
 * $Id$
 */

package edu.jas.fd;


// import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelationsIterated;


/**
 * GCD Simple PRS algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDSimpleTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDSimpleTest</CODE> object.
     * @param name String.
     */
    public GCDSimpleTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDSimpleTest.class);
        return suite;
    }


    GreatestCommonDivisorAbstract<BigRational> fd;


    TermOrder to = new TermOrder(TermOrder.INVLEX);
    //TermOrder to = new TermOrder(TermOrder.IGRLEX);


    GenSolvablePolynomialRing<BigRational> dfac;


    //GenSolvablePolynomialRing<BigRational> cfac;


    //GenSolvablePolynomialRing<GenPolynomial<BigRational>> rfac;
    RecSolvablePolynomialRing<BigRational> rfac;


    BigRational ai, bi, ci, di, ei;


    GenSolvablePolynomial<BigRational> a, b, c, d, e;


    GenSolvablePolynomial<GenPolynomial<BigRational>> ar, br, cr, dr, er;


    //RecSolvablePolynomial<BigRational> ar, br, cr, dr, er;


    int rl = 4;


    int kl = 2;


    int ll = 2;


    int el = 3;


    float q = 0.25f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        String[] vars = new String[] { "a", "b", "c", "d" };
        fd = new GreatestCommonDivisorSimple<BigRational>();
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        //cfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), rl - 1, to, cvars);
        //rfac = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(cfac, 1, to, uvars);
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(1);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        ar = br = cr = dr = er = null;
        fd = null;
        dfac = null;
        //cfac = null;
        rfac = null;
    }


    /**
     * Test base gcd simple.
     */
    public void xtestBaseGcdSimple() {
        String[] uvars = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), 1, to, uvars);
        for (int i = 0; i < 5; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2, q);
            b = dfac.random(kl * (i + 1), ll + i, el + 2, q);
            c = dfac.random(kl * (i + 1), ll + 1, el + 1, q);
            c = c.multiply(dfac.univariate(0));
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            //c = (GenSolvablePolynomial<BigRational>) fd.basePrimitivePart(c).abs();
            //System.out.println("a  = " + a);
            //System.out.println("b  = " + b);
            //System.out.println("c  = " + c);

            a = a.multiply(c);
            b = b.multiply(c);

            d = fd.baseGcd(a, b);
            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(d, c);
            //System.out.println("d  = " + d);
            //System.out.println("c  = " + c);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(a, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(b, d);
            //System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b " + e, e.isZERO());
        }
    }


    /**
     * Test univariate recursive gcd simple.
     */
    public void testRecursiveGCDSimple() {
        di = new BigRational(1);
        //String[] vars = new String[] { "a", "b", "c", "d" };
        String[] vars = new String[] { "a", "b" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        System.out.println("dfac = " + dfac.toScript());
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(1);
        System.out.println("rfac = " + rfac.toScript());

        //kl = 3; 
        ll = 4; 
        el = 3;

        //ar = rfac.random(kl, ll, el + 1, q);
        //br = rfac.random(kl, ll, el, q);
        //cr = rfac.random(kl, ll, el, q);
        //cr = (RecSolvablePolynomial<BigRational>) cr.abs();
        //cr = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> monic(cr);
        //cr = (RecSolvablePolynomial<BigRational>) fd.recursivePrimitivePart(cr).abs();
        //cr = rfac.getONE();
        //cr = rfac.parse("a+b+c+d");

        //ar = rfac.parse("( ( -31/19 )  ) b^3 - ( 781/260 a - 641/372  )");
        //br = rfac.parse("( ( -1/5 ) a - 1/4  ) b^2 - 11/12  b - ( 47/17 a + 29/30  )");
        //cr = rfac.parse(" ( a + 9/8  ) b + ( 285/208 a + 191/280  )");
        ar = rfac.parse("b^3 - ( a )");
        br = rfac.parse("( a ) b^2 - 1/2 b");
        //cr = rfac.parse("b + ( a )");
        cr = rfac.parse("(a^2 + a) b + 4");

        //ar = rfac.parse("( 2/23 a - 1/2  ) b^3 + 617/672  b^2 - ( 5 a + 307/154  )");
        //br = rfac.parse("( ( -673/330 )  ) b - ( 2/5 a - 566969/1651860  )");
        //cr = rfac.parse("( a - 2287945/213324  )");

        System.out.println("ar = " + ar);
        System.out.println("br = " + br);
        System.out.println("cr = " + cr);

        if (cr.isZERO()) {
            cr = rfac.getONE();
        }
        //ar = cr.multiply(ar);
        //br = cr.multiply(br);
        ar = ar.multiply(cr);
        br = br.multiply(cr);
        System.out.println("ar = " + ar);
        System.out.println("br = " + br);
        //if (true) return;

        dr = fd.recursiveUnivariateGcd(ar, br);
        System.out.println("cr = " + cr);
        System.out.println("dr = " + dr);

        //er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursivePseudoQuotient(dr, cr);
        //System.out.println("dr/cr = " + er);

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(dr, cr);
        //er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveRightSparsePseudoRemainder(dr, cr);
        System.out.println("er = " + er);
        assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(ar, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | a " + er, er.isZERO());

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(br, dr);
        System.out.println("er = " + er);
        assertTrue("gcd(a,b) | b " + er, er.isZERO());
    }


    /**
     * Test arbitrary recursive gcd simple.
     */
    public void xtestArbitraryRecursiveGCDSimple() {
        String[] vars = new String[] { "a", "b", "c", "d" };
        //String[] vars = new String[] { "a", "b" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        System.out.println("dfac = " + dfac.toScript());
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(2);
        System.out.println("rfac = " + rfac.toScript());

        //kl = 3; ll = 2;
        el = 2;

        ar = rfac.random(kl, ll, el + 1, q);
        br = rfac.random(kl, ll, el, q);
        cr = rfac.random(kl, ll, el, q);
        //cr = (RecSolvablePolynomial<BigRational>) fd.recursivePrimitivePart(cr).abs();
        System.out.println("ar = " + ar);
        System.out.println("br = " + br);
        System.out.println("cr = " + cr);

        if (cr.isZERO()) {
            cr = rfac.getONE();
        }

        ar = ar.multiply(cr);
        br = br.multiply(cr);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);

        dr = fd.recursiveGcd(ar, br);
        System.out.println("cr = " + cr);
        System.out.println("dr = " + dr);

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(dr, cr);
        //System.out.println("er = " + er);
        assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(ar, dr);
        //System.out.println("er = " + er);
        assertTrue("gcd(a,b) | a " + er, er.isZERO());

        er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(br, dr);
        //System.out.println("er = " + er);
        assertTrue("gcd(a,b) | b " + er, er.isZERO());
    }


    /**
     * Test gcd simple.
     */
    public void xtestGCDSimple() {
        //String[] vars = new String[] { "a", "b", "c", "d" };
        String[] vars = new String[] { "a", "b" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        System.out.println("dfac = " + dfac.toScript());

        //kl = 3; 
        ll = 4;
        el = 4;

        a = dfac.random(kl, ll, el, q);
        b = dfac.random(kl, ll, el, q);
        c = dfac.random(kl, ll, el, q);
        c = c.multiply(dfac.univariate(0));
        c = c.monic();
        //c = (GenSolvablePolynomial<BigRational>) fd.primitivePart(c).abs();
        a = dfac.parse("1/3 b^3 - 1/6");
        b = dfac.parse("( -1/2 ) b + 3 a");
        //c = dfac.parse("a * b - 5 b");
        c = dfac.parse("a - 5");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        if (c.isZERO()) {
            c = dfac.getONE();
        }

        a = a.multiply(c);
        b = b.multiply(c);
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        //System.out.println("c = " + c);

        d = fd.gcd(a, b);
        System.out.println("c = " + c);
        System.out.println("d = " + d);

        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(d, c);
        //System.out.println("e = " + e);
        //assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(a, d);
        //System.out.println("e = " + e);
        assertTrue("gcd(a,b) | a " + e, e.isZERO());
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(a, c);
        //System.out.println("e = " + e);
        assertTrue("c | ac " + e, e.isZERO());
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(a, d);
        System.out.println("a/d = " + e);
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(a, c);
        System.out.println("a/c = " + e);

        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(b, d);
        //System.out.println("e = " + e);
        assertTrue("gcd(a,b) | b " + e, e.isZERO());
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(b, c);
        //System.out.println("e = " + e);
        assertTrue("c | bc " + e, e.isZERO());
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(b, d);
        System.out.println("b/d = " + e);
        e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(b, c);
        System.out.println("b/c = " + e);
    }

}
