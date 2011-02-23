
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
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


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
     * Test construction Q(sqrt(2))(x)(sqrt(x)).
     */
    public void testConstructionF1() {
        builder = ExtensionFieldBuilder.baseField(new BigRational(1));
        System.out.println("builder = " + builder.toString());

        RingFactory fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("w2", "w2^2 - 2");
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        builder = builder.transcendentExtension("x");
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("wx", "wx^2 - { x }"); // how to know number of { }
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        int s = fac.generators().size();
        System.out.println("gens    = " + fac.generators());
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(2);
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }


    /**
     * Test construction Q(x)(sqrt(2))(sqrt(x)).
     */
    public void testConstructionF2() {
        builder = ExtensionFieldBuilder.baseField(new BigRational(1));
        System.out.println("builder = " + builder.toString());

        RingFactory fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        builder = builder.transcendentExtension("x");
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());
        builder = builder.algebraicExtension("w2", "w2^2 - 2");
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        builder = builder.algebraicExtension("wx", "wx^2 - { { x } }"); // how to know number of { }
        System.out.println("builder = " + builder.toString());

        fac = builder.build();
        System.out.println("fac     = " + fac.toScript());

        List<RingElem> gens = fac.generators();
        int s = gens.size();
        System.out.println("gens    = " + gens);
        assertTrue("#gens == 4 " + s, s == 4 );

        RingElem elem = (RingElem)fac.random(1);
        if ( elem.isZERO() || elem.isONE() ) {
            elem = gens.get(s-1);
	}
        //System.out.println("elem     = " + elem.toScript());
        System.out.println("elem    = " + elem);

        RingElem inv = (RingElem)elem.inverse();
        //System.out.println("inv      = " + inv.toScript());
        System.out.println("inv     = " + inv);

        RingElem a = (RingElem)elem.multiply(inv);
        assertTrue("e / e == 1 " + a, a.isONE() );
    }

}
