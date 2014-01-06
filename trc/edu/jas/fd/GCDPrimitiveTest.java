/*
 * $Id$
 */

package edu.jas.fd;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RecSolvablePolynomial;
import edu.jas.poly.RecSolvablePolynomialRing;
import edu.jas.poly.RelationGenerator;
import edu.jas.poly.TermOrder;
import edu.jas.poly.WeylRelationsIterated;

import edu.jas.ufd.PolyUfdUtil;
import edu.jas.gbmod.SolvableQuotient;
import edu.jas.gbmod.SolvableQuotientRing;
import edu.jas.gbmod.QuotSolvablePolynomial;
import edu.jas.gbmod.QuotSolvablePolynomialRing;

/**
 * GCD Primitive PRS algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDPrimitiveTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GCDPrimitiveTest</CODE> object.
     * @param name String.
     */
    public GCDPrimitiveTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GCDPrimitiveTest.class);
        return suite;
    }


    GreatestCommonDivisorAbstract<BigRational> fd, fds;


    TermOrder to = new TermOrder(TermOrder.INVLEX);
    //TermOrder to = new TermOrder(TermOrder.IGRLEX);


    GenSolvablePolynomialRing<BigRational> dfac;


    //GenSolvablePolynomialRing<BigRational> cfac;


    //GenSolvablePolynomialRing<GenPolynomial<BigRational>> rfac;
    RecSolvablePolynomialRing<BigRational> rfac;


    BigRational ai, bi, ci, di, ei;


    GenSolvablePolynomial<BigRational> a, b, c, d, e, a1, b1;


    GenSolvablePolynomial<GenPolynomial<BigRational>> ar, br, cr, dr, er, sr;


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
        fd = new GreatestCommonDivisorPrimitive<BigRational>();
        fds = new GreatestCommonDivisorSimple<BigRational>();
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
     * Test base gcd primitive.
     */
    public void xtestBaseGcdPrimitive() {
        String[] uvars = new String[] { "x" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), 1, to, uvars);

        for (int i = 0; i < 5; i++) {
            a = dfac.random(kl * (i + 2), ll + 2 * i, el + 2, q);
            b = dfac.random(kl * (i + 2), ll + 2 * i, el + 2, q);
            c = dfac.random(kl * (i + 2), ll + 2, el + 2, q);
            c = c.multiply(dfac.univariate(0));
            if (c.isZERO()) {
                // skip for this turn
                continue;
            }
            //a = fd.basePrimitivePart(a);
            //b = fd.basePrimitivePart(b);
            c = (GenSolvablePolynomial<BigRational>) fd.basePrimitivePart(c).abs();

            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("c  = " + c);
            //assertTrue("length( c" + i + " ) <> 0", c.length() > 0);

            a = a.multiply(c);
            b = b.multiply(c);

            d = fd.baseGcd(a, b);
            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(d, c);
            System.out.println("d  = " + d);
            System.out.println("c  = " + c);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(a, d);
            System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigRational>) PolyUtil.<BigRational> basePseudoRemainder(b, d);
            System.out.println("e = " + e);
            assertTrue("gcd(a,b) | b " + e, e.isZERO());
        }
    }


    /**
     * Test univariate recursive gcd primitive.
     */
    public void xtestRecursiveGCDPrimitive() {
        //String[] vars = new String[] { "a", "b", "c", "d" };
        String[] vars = new String[] { "a", "b" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        System.out.println("dfac = " + dfac.toScript());
        rfac = (RecSolvablePolynomialRing<BigRational>) dfac.recursive(1);
        System.out.println("rfac = " + rfac.toScript());

        RecSolvablePolynomialRing<BigRational> rrfacTemp = rfac;
        GenSolvablePolynomialRing<GenPolynomial<BigRational>> rrfac = rfac;

        GenSolvablePolynomialRing<BigRational> rcfac = (GenSolvablePolynomialRing<BigRational>) rfac.coFac; 
        SolvableQuotientRing<BigRational> qfac = new SolvableQuotientRing<BigRational>(rcfac);
        //GenSolvablePolynomialRing<SolvableQuotient<BigRational>> rqfac 
        //   = new GenSolvablePolynomialRing<SolvableQuotient<BigRational>>(qfac,rrfac);
        QuotSolvablePolynomialRing<BigRational> rqfac 
           = new QuotSolvablePolynomialRing<BigRational>(qfac,rrfac);
        List<GenSolvablePolynomial<GenPolynomial<BigRational>>> rl = rrfacTemp.coeffTable.relationList();
        List<GenPolynomial<GenPolynomial<BigRational>>> rlc = PolynomialList.<GenPolynomial<BigRational>> castToList(rl);
        rqfac.polCoeff.coeffTable.addRelations(rlc);
        //System.out.println("\nrdfac  = " + rdfac.toScript());
        System.out.println("rrfac  = " + rrfac.toScript());
        System.out.println("rcfac  = " + rcfac.toScript());
        System.out.println("qfac   = " + qfac.toScript());
        System.out.println("rqfac  = " + rqfac.toScript());

        //kl = 3; ll = 4; //
        el = 2;

        for (int i = 0; i < 1; i++) { // 3
            ar = rfac.random(kl, ll, el + i, q);
            br = rfac.random(kl, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = (RecSolvablePolynomial<BigRational>) cr.abs();
            cr = (RecSolvablePolynomial<BigRational>) PolyUtil.<BigRational> monic(cr);
            //cr = (RecSolvablePolynomial<BigRational>) fd.recursivePrimitivePart(cr).abs();
            //cr = rfac.getONE();
            //cr = rfac.parse("a+b+c+d");

            ar = rfac.parse("1/3 b^3 - 1/6");
            br = rfac.parse("( -1/2 ) b + 3 a");
            //nok: cr = rfac.parse("b * a - 5 b");
            cr = rfac.parse("a * b - 5 b");
            //cr = rfac.parse("a - 5");

            System.out.println("ar = " + ar);
            System.out.println("br = " + br);
            System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }
            //assertTrue("length( cr " + i + " ) <> 0", cr.length() > 0);

            //ar = cr.multiply(ar); 
            //br = cr.multiply(br);
            ar = ar.multiply(cr);
            br = br.multiply(cr);
            System.out.println("ar = " + ar);
            System.out.println("br = " + br);
            //if (true) return;

            long ts = System.currentTimeMillis();
            //sr = rfac.getONE(); 
            //sr = fds.recursiveUnivariateGcd(ar, br);
            ts = System.currentTimeMillis() - ts;
            //System.out.println("cr = " + cr);

            long tp = System.currentTimeMillis();
            dr = fd.recursiveUnivariateGcd(ar, br);
            tp = System.currentTimeMillis() - tp;
            System.out.println("cr = " + cr);
            System.out.println("dr = " + dr);
            System.out.println("sr = " + sr);
            System.out.println("time: ts = " + ts + ", tp = " + tp);

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            dr, cr);
            System.out.println("er = " + er);
            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            ar, dr);
            System.out.println("er = " + er);
            assertTrue("gcd(a,b) | a " + er, er.isZERO());

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            br, dr);
            System.out.println("er = " + er);
            assertTrue("gcd(a,b) | b " + er, er.isZERO());

            GenSolvablePolynomial<SolvableQuotient<BigRational>> ap, bp, cp, dp, gp, ep, 
                apm, bpm, cpm, dpm, gpm;
            ap = FDUtil.<BigRational> quotientFromIntegralCoefficients(rqfac,ar);
            bp = FDUtil.<BigRational> quotientFromIntegralCoefficients(rqfac,br);
            cp = FDUtil.<BigRational> quotientFromIntegralCoefficients(rqfac,cr);
            dp = FDUtil.<BigRational> quotientFromIntegralCoefficients(rqfac,dr);
            apm = ap.monic();
            bpm = bp.monic();
            cpm = cp.monic();
            dpm = dp.monic();
            System.out.println("ap  = " + ap);
            System.out.println("apm = " + apm);
            System.out.println("bp  = " + bp);
            System.out.println("bpm = " + bpm);
            System.out.println("cp  = " + cp);
            System.out.println("cpm = " + cpm);
            System.out.println("dp  = " + dp);
            System.out.println("dpm = " + dpm);

            GreatestCommonDivisorAbstract<SolvableQuotient<BigRational>> fdq = 
                new GreatestCommonDivisorPrimitive<SolvableQuotient<BigRational>>();
            gp = fdq.baseGcd(ap,bp);
            gpm = gp.monic();
            System.out.println("gp  = " + gp);
            System.out.println("gpm = " + gpm);

            ep = (GenSolvablePolynomial<SolvableQuotient<BigRational>>) FDUtil.<SolvableQuotient<BigRational>> baseSparsePseudoRemainder(gp, dp);
            //System.out.println("ep  = " + ep);
            assertTrue("c | gcd(ac,bc) " + ep, ep.isZERO());

            ep = (GenSolvablePolynomial<SolvableQuotient<BigRational>>) FDUtil.<SolvableQuotient<BigRational>> baseSparsePseudoRemainder(ap, gp);
            //System.out.println("ep  = " + ep);
            assertTrue("gcd(ac,bc)| ac) " + ep, ep.isZERO());

            ep = (GenSolvablePolynomial<SolvableQuotient<BigRational>>) FDUtil.<SolvableQuotient<BigRational>> baseSparsePseudoRemainder(bp, gp);
            //System.out.println("ep  = " + ep);
            assertTrue("gcd(ac,bc)| bc) " + ep, ep.isZERO());
        }
    }


    /**
     * Test arbitrary recursive gcd primitive.
     */
    public void xtestArbitraryRecursiveGCDPrimitive() {
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

        for (int i = 0; i < 1; i++) {
            ar = rfac.random(kl, ll, el, q);
            br = rfac.random(kl, ll, el, q);
            cr = rfac.random(kl, ll, el, q);
            //cr = (RecSolvablePolynomial<BigRational>) fd.recursivePrimitivePart(cr).abs();
            System.out.println("ar = " + ar);
            System.out.println("br = " + br);
            System.out.println("cr = " + cr);

            if (ar.isZERO() || br.isZERO() || cr.isZERO()) {
                // skip for this turn
                continue;
            }

            ar = ar.multiply(cr);
            br = br.multiply(cr);
            System.out.println("ar = " + ar);
            System.out.println("br = " + br);

            dr = fd.recursiveGcd(ar, br);
            System.out.println("cr = " + cr);
            System.out.println("dr = " + dr);

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            dr, cr);
            //System.out.println("er = " + er);
            assertTrue("c | gcd(ac,bc) " + er, er.isZERO());

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            ar, dr);
            //System.out.println("er = " + er);
            assertTrue("gcd(a,b) | a " + er, er.isZERO());

            er = (RecSolvablePolynomial<BigRational>) FDUtil.<BigRational> recursiveSparsePseudoRemainder(
                            br, dr);
            //System.out.println("er = " + er);
            assertTrue("gcd(a,b) | b " + er, er.isZERO());
        }
    }


    /**
     * Test gcd primitive.
     */
    public void testGCDPrimitive() {
        //String[] vars = new String[] { "a", "b", "c", "d" };
        String[] vars = new String[] { "a", "b" };
        dfac = new GenSolvablePolynomialRing<BigRational>(new BigRational(1), to, vars);
        RelationGenerator<BigRational> wl = new WeylRelationsIterated<BigRational>();
        dfac.addRelations(wl);
        System.out.println("dfac = " + dfac.toScript());

        //kl = 3; 
        ll = 4;
        el = 3;

        for (int i = 0; i < 1; i++) {
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

            if (a.isZERO() || b.isZERO() || c.isZERO()) {
                // skip for this turn
                continue;
            }

            a = a.multiply(c);
            b = b.multiply(c);
            System.out.println("a = " + a);
            System.out.println("b = " + b);
            //System.out.println("c = " + c);

            d = fd.gcd(a, b);
            System.out.println("c  = " + c);
            System.out.println("d  = " + d);
            System.out.println("d' = " + d.monic());

            e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(d, c);
            System.out.println("e = " + e);
            assertTrue("c | gcd(ac,bc) " + e, e.isZERO());

            e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(a, d);
            System.out.println("e = " + e);
            assertTrue("gcd(a,b) | a " + e, e.isZERO());
            e = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> baseSparsePseudoRemainder(a, c);
            System.out.println("e = " + e);
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

            // a1 = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(a, d);
            // b1 = (GenSolvablePolynomial<BigRational>) FDUtil.<BigRational> basePseudoQuotient(b, d);
            // d = fd.gcd(a1, b1);
            // System.out.println("c = " + c);
            // System.out.println("d = " + d);
            // System.out.println("a1 = " + a1);
            // System.out.println("b1 = " + b1);
        }
    }

}
