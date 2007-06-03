/*
 * $Id$
 */

// from package edu.unima.ky.parallel;
package edu.jas.util;

import junit.framework.*;
import java.io.*;

import org.apache.log4j.BasicConfigurator;

/**
 * SocketChannel Test using JUnit
 *
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 */

public class SocketChannelTest extends TestCase {

   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

   public SocketChannelTest(String name) {
          super(name);
   }

   public static Test suite() {
     TestSuite suite= new TestSuite(SocketChannelTest.class);
     return suite;
   }

   private ChannelFactory cf;
   private SocketChannel sc1;
   private SocketChannel sc2;
   private String host;
   private int port;

   protected void setUp() {
       host = "localhost";
       port = 4711;
       cf = new ChannelFactory(port);       
       try {
	   sc1 = cf.getChannel(host,port);
	   sc2 = cf.getChannel();
       } catch(IOException e) {
           fail("IOException"+e);
       } catch (InterruptedException e) {
	   fail("InterruptedException"+e);
       }
   }

   protected void tearDown() {
       cf.terminate();
       sc1.close();
       sc2.close();
       try {
           Thread.currentThread().sleep(1);
       } catch(InterruptedException e) {
           fail("InterruptedException"+e);
       }
   }

   public void testSocketChannel0() {
       // test setUp() and tearDown()
   }

   public void testSocketChannel1() {
       Object o = new Integer(0);
       try {
           sc1.send(o);
           assertEquals(o,sc2.receive());
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }

   public void testSocketChannel2() {
       Object o = new Integer(0);
       try {
           sc1.send(o);
           sc2.send(o);
           assertEquals(o,sc1.receive());
           assertEquals(o,sc2.receive());
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }

   public void testSocketChannel3() {
       int n = 10;
       Object o;
       try {
           for (int i = 0; i < n; i++) {
               o = new Integer(i);
               sc1.send(o);
           }
           for (int i = 0; i < n; i++) {
               o = new Integer(i);
               assertEquals(o,sc2.receive());
           }
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }

}

