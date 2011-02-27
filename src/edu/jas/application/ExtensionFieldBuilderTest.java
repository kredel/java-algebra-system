/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.arith.Rational;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.ComplexRing;
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
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
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
        TestSuite suite= new TestSuite(ExtensionFieldBuilderTest.class);
        return suite;
    }


    ExtensionFieldBuilder builder;


    protected void setUp() {
        builder = null;
    }


    protected void tearDown() {
        builder = null;
        ComputerThreads.terminate();
    }


    /**
     * Test construction Q(sqrt(2))(x)(sqrt(x)) by hand.
     */
    public void xtestConstructionF0() {
        BigRational bf = new BigRational(1);
        GenPolynomialRing<BigRational> pf = new GenPolynomialRing<BigRational>(bf,new String[]{ "w2" });
        GenPolynomial<BigRational> a = pf.parse("w2^2 - 2");
        AlgebraicNumberRing<BigRational> af = new AlgebraicNumberRing<BigRational>(a);
        GenPolynomialRing<AlgebraicNumber<BigRational>> tf = new GenPolynomialRing<AlgebraicNumber<BigRational>>(af,new String[]{ "x" });
        QuotientRing<AlgebraicNumber<BigRational>> qf = new QuotientRing<AlgebraicNumber<BigRational>>(tf);
        GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>> qaf = new GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>>(qf,new String[]{ "wx" });
        GenPolynomial<Quotient<AlgebraicNumber<BigRational>>> b = qaf.parse("wx^2 - { x }");
        AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>> fac = new AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>>(b);
        System.out.println("fac = " + fac.toScript());

        List<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> elem = fac.random(2);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> inv = elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>> e = elem.multiply(inv);
        assertTrue("e / e == 1 " + e, e.isONE() );
    }


    /**
     * Test construction Q(sqrt(2))(x)(sqrt(x)) by extension field builder.
     */
    public void xtestConstructionF1() {
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

        builder = builder.algebraicExtension("wx", "wx^2 - { x }"); // how to know number of { }
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(2);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = (RingElem)gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }


    /**
     * Test construction Q(x)(sqrt(2))(sqrt(x)) by extension field builder.
     */
    public void xtestConstructionF2() {
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

        builder = builder.algebraicExtension("wx", "wx^2 - { { x } }"); // how to know number of { }
        //System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(1);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = (RingElem)gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }


    /**
     * Test construction Z_p(sqrt(2))(x)(sqrt(x)) by extension field builder.
     */
    public void xtestConstructionF3() {
        RingFactory fac = ExtensionFieldBuilder
                                  .baseField(new ModLongRing(7))
                                  .algebraicExtension("w2", "w2^2 - 3")
                                  .transcendentExtension("x")
                                  .algebraicExtension("wx", "wx^7 - { x }")
                                  .build();
        System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(2);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = (RingElem)gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }


    /**
     * Test construction Q(+sqrt(2))(+3rt(3)) by extension field builder.
     */
    public void testConstructionR1() {
        RingFactory fac = ExtensionFieldBuilder
                                  .baseField(new BigRational(1))
                                  .realAlgebraicExtension("q3", "q3^3 - 3","[1,2]")
                                  .realAlgebraicExtension("w2", "w2^2 - { q3 }","[1,2]")
                                  .realAlgebraicExtension("s2", "s2^2 - { w2 }","[1,2]")
                                  .build();
        System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(2);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = (RingElem)gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem + " =~= " + new BigDecimal(((Rational)elem).getRational()));

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv + " =~= " + new BigDecimal(((Rational)inv).getRational()));

        RingElem a = (RingElem)elem.multiply(inv);
        //System.out.println("a       = " + a + " =~= " + new BigDecimal(((Rational)a).getRational()));
        assertTrue("e / e == 1 " + a, a.isONE() );
    }


    /**
     * Test construction Q(sqrt(-1))(+3rt(i)) by extension field builder.
     */
    public void testConstructionC1() {
        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(new BigRational(1));
        System.out.println("cf = " + cf.toScript());
        RingFactory fac = ExtensionFieldBuilder
                                  .baseField(cf)
                                  .complexAlgebraicExtension("w2", "w2^2 + 2","[-1i0,1i2]")
                                   //.complexAlgebraicExtension("q3", "q3^3 + { w2 }","[-1i0,1i2]")
                                  .build();
        System.out.println("fac = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 3 " + s, s == 3 );

        RingElem elem = (RingElem)fac.random(2);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = (RingElem)gens.get(s-1).sum(gens.get(s-2));
            //elem = (RingElem)gens.get(s-1).multiply(gens.get(s-2));
            elem = (RingElem) elem.multiply(elem);
        }
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        //System.out.println("a       = " + a);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }

}
