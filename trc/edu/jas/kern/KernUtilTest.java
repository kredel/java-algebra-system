/*
 * $Id$
 */

package edu.jas.kern;


import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 * KernUtil tests with JUnit.
 * @author Heinz Kredel
 */
public class KernUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>KernUtilTest</CODE> object.
     * @param name String.
     */
    public KernUtilTest(String name) {
        super(name);
    }


    /*
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(KernUtilTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test StringUtil.
     */
    public void testStringUtil() {
	StringReader sr = new StringReader(" ein text - noch ein / text { wort } [1, 2, 3] [[1, 4], [2], [3, 5]]");
        String s = StringUtil.nextString(sr);
        //System.out.println(":" + s + ":");
        assertEquals("s==ein", s, "ein");

        s = StringUtil.nextString(sr);
        //System.out.println(":" + s + ":");
        assertEquals("s==text", s, "text");

        s = StringUtil.nextString(sr,'/');
        //System.out.println(":" + s + ":");
        assertEquals("s== - noch ein ", s, "- noch ein");

        try {
            s = StringUtil.nextPairedString(sr,'#','#');
            fail("exception not thrown");
        } catch (IllegalArgumentException e) {
            // pass
        }

        s = StringUtil.nextPairedString(sr,'{','}');
	//System.out.println(":" + s + ":");
        assertEquals("s==wort", s, "wort");

        s = StringUtil.nextPairedString(sr,'[',']');
	//System.out.println(":" + s + ":");
        assertEquals("s==1, 2, 3", s, "1, 2, 3");

        s = StringUtil.nextPairedString(sr,'[',']');
	//System.out.println(":" + s + ":");
        assertEquals("s==[1, 4], [2], [3, 5]", s, "[1, 4], [2], [3, 5]");
    }


    /**
     * Test stack trace.
     */
    public void testStackTrace() {
        String s = StringUtil.selectStackTrace(".*KernUtilTest.*");
        //System.out.println(":" + s + ":");
        assertTrue("s contains KernUtilTest", s.indexOf("KernUtilTest") >= 0);
    }


    /**
     * Test default scripting flags.
     */
    public void testScripting() {
        Scripting.Lang s = Scripting.getLang();
        //System.out.println(":" + s + ":");
        assertEquals("s == Python", s, Scripting.Lang.Python);

        Scripting.CAS c = Scripting.getCAS();
        //System.out.println(":" + c + ":");
        assertEquals("c == JAS", c, Scripting.CAS.JAS);

        int p = Scripting.getPrecision();
        //System.out.println(":" + p + ":");
        assertTrue("p < 0", p < 0);
    }

}
