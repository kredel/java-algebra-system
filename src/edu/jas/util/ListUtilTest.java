/*
 * $Id$
 */

package edu.jas.util;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;


/**
 * ListUtil tests with JUnit.
 * @author Heinz Kredel.
 */
public class ListUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ListUtilTest</CODE> object.
     * @param name String.
     */
    public ListUtilTest(String name) {
        super(name);
    }


    /**
 */
    public static Test suite() {
        TestSuite suite = new TestSuite(ListUtilTest.class);
        return suite;
    }


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    @Override
    protected void setUp() {
        ai = bi = ci = di = ei = null;
    }


    @Override
    protected void tearDown() {
        ai = bi = ci = di = ei = null;
    }


    /**
     * Test list map.
     * 
     */
    public void testListMap() {
        ai = new BigInteger();
        List<BigInteger> list = new ArrayList<BigInteger>();
        for (int i = 0; i < 10; i++) {
            list.add(ai.random(7));
        }
        bi = ai.getONE();
        List<BigInteger> nl;
        nl = ListUtil.<BigInteger, BigInteger> map(list, new Multiply<BigInteger>(bi));
        assertEquals("list == nl ", list, nl);
    }


    /**
     * Test tuple transpose.
     * 
     */
    public void testTuple() {
        ai = new BigInteger();
        List<List<BigInteger>> tlist = new ArrayList<List<BigInteger>>();
        int s1 = 4;
        int s2 = 3;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= s2;
        }
        //System.out.println("s = " + s);
        for (int i = 0; i < s1; i++) {
            List<BigInteger> list = new ArrayList<BigInteger>();
            for (int j = 0; j < s2; j++) {
                list.add(ai.random(7));
            }
            tlist.add(list);
        }
        //System.out.println("tlist = " + tlist);

        List<List<BigInteger>> ltuple = ListUtil.<BigInteger> tupleFromList(tlist);
        //System.out.println("ltuple = " + ltuple);
        assertTrue("#ltuple == " + s + " ", ltuple.size() == s);

        for (List<BigInteger> t : ltuple) {
            assertTrue("#t == " + s1 + " ", t.size() == s1);
        }
    }

}


/**
 * Internal scalar multiplication functor.
 */
class Multiply<C extends RingElem<C>> implements UnaryFunctor<C, C> {


    C x;


    public Multiply(C x) {
        this.x = x;
    }


    public C eval(C c) {
        return c.multiply(x);
    }
}
