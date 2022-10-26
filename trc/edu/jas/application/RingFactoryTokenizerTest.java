/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInt;
import edu.jas.arith.ModIntRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * RingFactoryTokenizer tests with JUnit.
 * @author Heinz Kredel
 */

public class RingFactoryTokenizerTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RingFactoryTokenizerTest</CODE> object.
     * @param name String.
     */
    public RingFactoryTokenizerTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RingFactoryTokenizerTest.class);
        return suite;
    }


    RingFactory fac; // unused


    GenPolynomialRing pfac;


    GenSolvablePolynomialRing spfac;


    RingFactoryTokenizer parser;


    Reader source;


    @Override
    protected void setUp() {
        fac = null;
        pfac = null;
        parser = null;
        source = null;
    }


    @Override
    protected void tearDown() {
        fac = null;
        pfac = null;
        parser = null;
        source = null;
    }


    /**
     * Test rational polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testBigRational() {
        String exam = "Rat(x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3/4 - 6/8 ), "
                        + "( 1 x + x^3 + 1/3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigRational> f = null;
        try {
            f = (GenPolynomialRing<BigRational>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigRational fac = new BigRational(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigRational>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }


    /**
     * Test integer polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testBigInteger() {
        String exam = "Int(x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3 2 - 6 ), "
                        + "( 1 x + x^3 + 3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigInteger> f = null;
        try {
            f = (GenPolynomialRing<BigInteger>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigInteger fac = new BigInteger(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigInteger>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }


    /**
     * Test modular integer polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testModInteger() {
        String exam = "Mod 19 (x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3 2 - 6 + 19 ), "
                        + "( 1 x + x^3 + 3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<ModInteger> f = null;
        try {
            f = (GenPolynomialRing<ModInteger>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        ModIntRing fac = new ModIntRing(19);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<ModInt>(fac, nvar, tord, vars);
        //System.out.println("pfac   = " + pfac);
        //System.out.println("f.ring = " + f.ring);
        assertEquals("pfac == f", pfac, f);

        ModLongRing lfac = new ModLongRing(19);
        assertFalse("fac != lfac", fac.equals(lfac));
        assertFalse("lfac != f.coFac", lfac.equals(f.coFac));
    }


    /**
     * Test complex polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testBigComplex() {
        String exam = "Complex(x,y,z) L " + "( " + "( 1i0 ), " + "( 0i0 ), " + "( 3/4i2 - 6/8i2 ), "
                        + "( 1i0 x + x^3 + 1i3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigComplex> f = null;
        try {
            f = (GenPolynomialRing<BigComplex>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigComplex fac = new BigComplex(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigComplex>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }


    /**
     * Test decimal polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testBigDecimal() {
        String exam = "D(x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 0.25 * 0.25 - 0.25^2 ), "
                        + "( 1 x + x^3 + 0.3333333333333333333333 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigDecimal> f = null;
        try {
            f = (GenPolynomialRing<BigDecimal>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigDecimal fac = new BigDecimal(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigDecimal>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }


    /**
     * Test quaternion polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testBigQuaternion() {
        String exam = "Quat(x,y,z) L " + "( " + "( 1i0j0k0 ), " + "( 0i0j0k0 ), "
                        + "( 3/4i2j1k3 - 6/8i2j1k3 ), " + "( 1 x + x^3 + 1i2j3k4 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigQuaternion> f = null;
        try {
            f = (GenPolynomialRing<BigQuaternion>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigQuaternionRing fac = new BigQuaternionRing();
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigQuaternion>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }


    /**
     * Test rational function coefficients.
     */
    @SuppressWarnings("unchecked")
    public void testRationalFunction() {
        String exam = "RatFunc(x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3/4 - 6/8 ), "
                        + "( 1 x + x^3 + 1/3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<Quotient<BigInteger>> f = null;
        try {
            f = (GenPolynomialRing<Quotient<BigInteger>>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigInteger fac = new BigInteger(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigInteger>(fac, nvar, tord, vars);

        QuotientRing<BigInteger> qfac = new QuotientRing<BigInteger>(pfac);
        //System.out.println("qfac = " + qfac.toScript());
        //System.out.println("f.coFac = " + f.ring.coFac.toScript());
        assertEquals("qfac == f.coFac", qfac, f.coFac);
    }


    /**
     * Test integral function coefficients.
     */
    @SuppressWarnings("unchecked")
    public void testIntegralFunction() {
        String exam = "IntFunc(x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3/4 - 6/8 ), "
                        + "( 1 x + x^3 + 1/3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<GenPolynomial<BigRational>> f = null;
        try {
            f = (GenPolynomialRing<GenPolynomial<BigRational>>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigRational fac = new BigRational(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigRational>(fac, nvar, tord, vars);

        //System.out.println("pfac = " + pfac.toScript());
        //System.out.println("f.coFac = " + f.coFac.toScript());
        assertEquals("pfac == f.coFac", pfac, f.coFac);
    }


    /**
     * Test modular function coefficients.
     */
    @SuppressWarnings("unchecked")
    public void testModularFunction() {
        String exam = "ModFunc 32003 (x,y,z) L " + "( " + "( 1 ), " + "( 0 ), " + "( 3/4 - 6/8 ), "
                        + "( 1 x + x^3 + 1/3 y z - x^3 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<Quotient<ModInteger>> f = null;
        try {
            f = (GenPolynomialRing<Quotient<ModInteger>>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        ModIntegerRing fac = new ModIntegerRing(32003);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<ModInteger>(fac, nvar, tord, vars);

        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        //System.out.println("qfac = " + qfac.toScript());
        //System.out.println("f.coFac = " + f.ring.toScript());
        assertEquals("qfac == f.coFac", qfac, f.coFac);
    }


    /**
     * Test rational solvable polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testSolvableBigRational() {
        String exam = "Rat(x,y,z) L " + "RelationTable " + "( " + " ( z ), ( y ), ( y z - 1 ) " + ") " + "( "
                        + " ( 1 ), " + " ( 0 ), " + " ( 3/4 - 6/8 ), " + " ( 1 x + x^3 + 1/3 y z - x^3 ) "
                        + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenSolvablePolynomialRing<BigRational> f = null;
        try {
            f = (GenSolvablePolynomialRing<BigRational>) parser.nextSolvablePolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigRational fac = new BigRational(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvar, tord, vars);
        List<GenSolvablePolynomial<BigRational>> rel = new ArrayList<GenSolvablePolynomial<BigRational>>(3);
        rel.add(spfac.parse("z"));
        rel.add(spfac.parse("y"));
        rel.add(spfac.parse("y z - 1"));
        spfac.addSolvRelations(rel);
        assertEquals("spfac == f", spfac, f);
        //System.out.println("spfac = " + spfac);
        //System.out.println("spfac.table = " + spfac.table);
    }


    /**
     * Test mod integer solvable polynomial.
     */
    @SuppressWarnings("unchecked")
    public void testSolvableModInteger() {
        String exam = "Mod 19 (x,y,z) L " + "RelationTable " + "( " + " ( z ), ( y ), ( y z - 1 ) " + ") "
                        + "( " + "( 1 ), " + "( 0 ), " + "( 3 2 - 6 + 19 ), " + "( 1 x + x^3 + 3 y z - x^3 ) "
                        + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenSolvablePolynomialRing<ModInteger> f = null;
        try {
            f = (GenSolvablePolynomialRing<ModInteger>) parser.nextSolvablePolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        ModIntRing fac = new ModIntRing(19);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        spfac = new GenSolvablePolynomialRing<ModInt>(fac, nvar, tord, vars);
        List<GenSolvablePolynomial<ModLong>> rel = new ArrayList<GenSolvablePolynomial<ModLong>>(3);
        rel.add(spfac.parse("z"));
        rel.add(spfac.parse("y"));
        rel.add(spfac.parse("y z - 1"));
        spfac.addSolvRelations(rel);
        assertEquals("spfac == f", spfac, f);
        //System.out.println("spfac = " + spfac);
        //System.out.println("spfac.table = " + spfac.table);
    }


    /**
     * Test integer polynomial module.
     */
    @SuppressWarnings("unchecked")
    public void testBigIntegerModule() {
        String exam = "Int(x,y,z) L " + "( " + " ( " + "  ( 1 ), " + "  ( 0 ), " + "  ( 3 2 - 6 ), "
                        + "  ( 1 x + x^3 + 3 y z - x^3 ) " + " ), " + " ( ( 1 ), ( 0 ) ) " + ")";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigInteger> m = null;
        try {
            m = (GenPolynomialRing<BigInteger>) parser.nextSubModuleRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigInteger fac = new BigInteger(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigInteger>(fac, nvar, tord, vars);
        assertEquals("pfac == m.ring", pfac, m);
    }


    /**
     * Test rational solvable polynomial module.
     */
    @SuppressWarnings("unchecked")
    public void testBigRationalSolvableModule() {
        String exam = "Rat(x,y,z) L " + "RelationTable " + "( " + " ( z ), ( y ), ( y z - 1 ) " + ") " + "( "
                        + " ( " + "  ( 1 ), " + "  ( 0 ), " + "  ( 3/4 - 6/8 ), "
                        + "  ( 1 x + x^3 + 1/3 y z - x^3 ) " + " ), " + " ( ( x ), ( 1 ), ( 0 ) ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenSolvablePolynomialRing<BigRational> m = null;
        try {
            m = (GenSolvablePolynomialRing<BigRational>) parser.nextSolvableSubModuleRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigRational fac = new BigRational(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvar, tord, vars);
        List<GenSolvablePolynomial<ModLong>> rel = new ArrayList<GenSolvablePolynomial<ModLong>>(3);
        rel.add(spfac.parse("z"));
        rel.add(spfac.parse("y"));
        rel.add(spfac.parse("y z - 1"));
        spfac.addSolvRelations(rel);
        assertEquals("spfac == m.ring", spfac, m);
    }


    /**
     * Test rational polynomial with generic coefficients.
     */
    @SuppressWarnings("unchecked")
    public void testBigRationalGeneric() {
        String exam = "Rat(x,y,z) L " + "( " + "( 1^3 ), " + "( 0^3 ), " + "( { 3/4 }^2 - 6/8^2 ), "
                        + "( { 1 }^2 x + x^3 + 1/3 y z - x^3 ), "
                        + "( 1.0001 - 0.0001 + { 0.25 }**2 - 1/4^2 ) " + " )";
        source = new StringReader(exam);
        parser = new RingFactoryTokenizer(source);
        GenPolynomialRing<BigRational> f = null;
        try {
            f = (GenPolynomialRing<BigRational>) parser.nextPolynomialRing();
        } catch (IOException e) {
            fail("" + e);
        } catch (ClassCastException e) {
            fail("" + e);
        }

        BigRational fac = new BigRational(0);
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x", "y", "z" };
        int nvar = vars.length;
        pfac = new GenPolynomialRing<BigRational>(fac, nvar, tord, vars);
        assertEquals("pfac == f", pfac, f);
    }

}
