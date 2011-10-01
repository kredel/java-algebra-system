/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * TermOrderOptimization tests with JUnit.
 * @author Heinz Kredel
 */

public class TermOrderOptimizationTest extends TestCase {


    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }


    /**
     * Constructs a <CODE>TermOrderOptimizationTest</CODE> object.
     * @param name String.
     */
    public TermOrderOptimizationTest(String name) {
        super(name);
    }


    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(TermOrderOptimizationTest.class);
        return suite;
    }


    protected void setUp() {
    }


    protected void tearDown() {
    }


    /**
     * Test permutations.
     */
    public void testPermutation() {

        List<Integer> P = new ArrayList<Integer>(); 
        P.add(2);
        P.add(1);
        P.add(4);
        P.add(0);
        P.add(3);
        //System.out.println("P = " + P);

        List<Integer> S = TermOrderOptimization.inversePermutation(P); 
        //System.out.println("S = " + S);
        assertFalse("P != id", TermOrderOptimization.isIdentityPermutation(P));
        assertFalse("S != id", TermOrderOptimization.isIdentityPermutation(S));

        List<Integer> T = TermOrderOptimization.multiplyPermutation(P,S); 
        //System.out.println("T = " + T);
        List<Integer> U = TermOrderOptimization.multiplyPermutation(S,P); 
        //System.out.println("U = " + U);

        assertTrue("T == id", TermOrderOptimization.isIdentityPermutation(T));
        assertTrue("U == id", TermOrderOptimization.isIdentityPermutation(U));
    }

}
