/*
 * $Id$
 */

package edu.jas.application;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RootFactory;
import edu.jas.structure.Power;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * ExtensionFieldBuilder tests with JUnit.
 * @author Heinz Kredel.
 */

public class ExtensionFieldBuilderTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ExtensionFieldBuilderTest</CODE> object.
     * @param name String.
     */
    public ExtensionFieldBuilderTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ExtensionFieldBuilderTest.class);
        return suite;
    }


    ExtensionFieldBuilder builder;


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
     * Test construction Q(sqrt(2))(x)(sqrt(x)) by hand.
     */
    public void testConstructionF0() {
        BigRational bf = new BigRational(1);
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf, new String[] { "w2" });
        GenPolynomial<BigRational> a = pf.parse("w2^2 - 2");
        AlgebraicNumberRing<BigRational> af = new AlgebraicNumberRing<BigRational>(a);
        GenPolynomialRing<AlgebraicNumber<BigRational>> tf = new GenPolynomialRing<AlgebraicNumber<BigRational>>(
                        af, new String[] { "x" });
        QuotientRing<AlgebraicNumber<BigRational>> qf = new QuotientRing<AlgebraicNumber<BigRational>>(tf);
        GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>> qaf = new GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>>(
                        qf, new String[] { "wx" });
        GenPolynomial<Quotient<AlgebraicNumber<BigRational>>> b = qaf.parse("wx^2 - x");
        AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>> fac = new AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>>(
                        b);
        //System.out.println("fac = " + fac.toScript());

        List<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> elem = fac.random(2);
        if (elem.isZERO() || elem.isONE()) {
            elem = gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> inv = elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> e = elem.multiply(inv);
        assertTrue("e / e == 1 " + e, e.isONE());
    }


    /**
     * Test construction Q(sqrt(2))(x)(sqrt(x)) by extension field builder.
     */
    public void testConstructionF1() {
        builder = ExtensionFieldBuilder.baseField(new BigRational(1));
        //System.out.println("builder = " + builder.toString());

        RingFactory fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("w2", "w2^2 - 2");
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        builder = builder.transcendentExtension("x");
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("wx", "wx^2 - x"); // number of { } resolved
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        RingElem elem = (RingElem) fac.random(2);
        if (elem.isZERO() || elem.isONE()) {
            elem = (RingElem) gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem inv = (RingElem) elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);

        RingElem a = (RingElem) elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE());
    }


    /**
     * Test construction Q(x)(sqrt(2))(sqrt(x)) by extension field builder.
     */
    public void testConstructionF2() {
        builder = ExtensionFieldBuilder.baseField(new BigRational(1));
        //System.out.println("builder = " + builder.toString());

        RingFactory fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        builder = builder.transcendentExtension("x");
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());
        builder = builder.algebraicExtension("w2", "w2^2 - 2");
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("wx", "wx^2 - x"); // number of { } resolved
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        //System.out.println("fac     = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        RingElem elem = (RingElem) fac.random(1);
        if (elem.isZERO() || elem.isONE()) {
            elem = (RingElem) gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem inv = (RingElem) elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);

        RingElem a = (RingElem) elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE());
    }


    /**
     * Test construction Z_p(sqrt(2))(x)(sqrt(x)) by extension field builder.
     */
    public void testConstructionF3() {
        RingFactory fac = ExtensionFieldBuilder.baseField(new ModLongRing(7)).algebraicExtension("w2",
                        "w2^2 - 3").transcendentExtension("x").algebraicExtension("wx", "wx^7 - x").build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        RingElem elem = (RingElem) fac.random(2);
        if (elem.isZERO() || elem.isONE()) {
            elem = (RingElem) gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem inv = (RingElem) elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);

        RingElem a = (RingElem) elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE());
    }


    /**
     * Test construction Q(+3rt(3))(+sqrt(+3rt(3)))(+5rt(2)) by extension field
     * builder.
     */
    public void testConstructionR1() {
        RingFactory fac = ExtensionFieldBuilder.baseField(new BigRational(1)).realAlgebraicExtension("q",
                        "q^3 - 3", "[1,2]").realAlgebraicExtension("w", "w^2 - q", "[1,2]")
                        .realAlgebraicExtension("s", "s^5 - 2", "[1,2]").build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        RingElem elem = (RingElem) fac.random(2);
        if (elem.isZERO() || elem.isONE()) {
            elem = (RingElem) gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //elem = (RingElem)elem.negate();
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);
        //BigDecimal ed = new BigDecimal(((Rational)elem).getRational());
        //System.out.println("        ~ " + ed);

        RingElem inv = (RingElem) elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);
        //BigDecimal id = new BigDecimal(((Rational)inv).getRational());
        //System.out.println("        ~ " + id);

        RingElem a = (RingElem) elem.multiply(inv);
        //System.out.println("a       = " + a);
        //System.out.println("        ~ " + ed.multiply(id));
        assertTrue("e / e == 1 " + a, a.isONE());
    }


    /**
     * Test construction Q(sqrt(-1))(+3rt(i)) by extension field builder.
     */
    public void testConstructionC1() {
        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(new BigRational(1));
        //System.out.println("cf = " + cf.toScript());
        RingFactory fac = ExtensionFieldBuilder.baseField(cf).complexAlgebraicExtension("w2", "w2^2 + 2",
                        "[-1i0,1i2]")
        //.complexAlgebraicExtension("q3", "q3^3 + { w2 }","[-1i0,1i2]") // not possible, TODO
                        .build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 3 " + s, s == 3);

        RingElem elem = (RingElem) fac.random(2);
        if (elem.isZERO() || elem.isONE()) {
            elem = (RingElem) gens.get(s - 1).sum(gens.get(s - 2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem inv = (RingElem) elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        //System.out.println("inv     = " + inv);

        RingElem a = (RingElem) elem.multiply(inv);
        //System.out.println("a       = " + a);
        assertTrue("e / e == 1 " + a, a.isONE());
    }


    /**
     * Test construction Q(+3rt(3))(+sqrt(+3rt(3)))(+5rt(2))[y] by extension
     * field builder and real root calculation.
     */
    public void testConstructionR2factory() {
        RingFactory fac = ExtensionFieldBuilder.baseField(new BigRational(1)).realAlgebraicExtension("q",
                        "q^3 - 3", "[1,2]").realAlgebraicExtension("w", "w^2 - q", "[1,2]")
                        .realAlgebraicExtension("s", "s^5 - 2", "[1,2]").build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        GenPolynomialRing pfac = new GenPolynomialRing(fac, new String[] { "y" });
        GenPolynomial elem = pfac.parse("y^2 - w s");
        //elem = (RingElem)elem.negate();
        //System.out.println("elem    = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        List<RealAlgebraicNumber> roots = RootFactory.realAlgebraicNumbers(elem);
        //System.out.println("roots   = " + roots);
        assertTrue("#roots == 2 " + roots, roots.size() == 2);
        for (RealAlgebraicNumber root : roots) {
            //BigDecimal id = new BigDecimal(root.getRational());
            //System.out.println("root    = " + root);
            //System.out.println("        ~ " + id);
            RealAlgebraicNumber inv = root.inverse();
            //System.out.println("inv     = " + inv);
            //BigDecimal ivd = new BigDecimal(inv.getRational());
            //System.out.println("        ~ " + ivd);

            RealAlgebraicNumber a = root.multiply(inv);
            //System.out.println("a       = " + a);
            //System.out.println("        ~ " + id.multiply(ivd));
            assertTrue("y / y == 1 " + a, a.isONE());
        }
    }


    /**
     * Test construction by extension
     * field builder and multiple algebraic extension.
     */
    public void testConstructionM1() {
        RingFactory fac = ExtensionFieldBuilder.baseField(new BigRational(1)).algebraicExtension("q,w,s",
                        "( q^3 - 3, w^2 - q, s^5 - 2)").build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        GenPolynomialRing pfac = (GenPolynomialRing) ExtensionFieldBuilder.baseField(fac).polynomialExtension("y").build();
        GenPolynomial elem = pfac.parse("y^2 - w s");
        //System.out.println("elem    = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem r = (RingElem) elem.trailingBaseCoefficient();
        RingElem t = (RingElem) r.inverse();
        RingElem u = (RingElem) r.multiply(t);
        //System.out.println("r       = " + r);
        //System.out.println("t       = " + t);
        //System.out.println("r*t     = " + u);
        assertTrue("r*t == 1: ", u.isONE());

        elem = elem.multiply(elem.negate());
        //System.out.println("elem    = " + elem);
        elem = Power.positivePower(elem,3);
        //System.out.println("elem    = " + elem);
    }


    /**
     * Test construction by extension
     * field builder and multiple transcendent extension.
     */
    public void testConstructionM2() {
        RingFactory fac = ExtensionFieldBuilder.baseField(new BigRational(1)).algebraicExtension("q,w,s",
                        "").build();
        //System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        //System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4);

        GenPolynomialRing pfac = (GenPolynomialRing) ExtensionFieldBuilder.baseField(fac).polynomialExtension("y").build();
        GenPolynomial elem = pfac.parse("y^2 - w s");
        //System.out.println("elem    = " + elem.toScript());
        //System.out.println("elem    = " + elem);

        RingElem r = (RingElem) elem.trailingBaseCoefficient();
        RingElem t = (RingElem) r.inverse();
        RingElem u = (RingElem) r.multiply(t);
        //System.out.println("r       = " + r);
        //System.out.println("t       = " + t);
        //System.out.println("r*t     = " + u);
        assertTrue("r*t == 1: ", u.isONE());

        elem = elem.multiply(elem.negate());
        //System.out.println("elem    = " + elem);
        elem = Power.positivePower(elem,3);
        //System.out.println("elem    = " + elem);
    }

}
