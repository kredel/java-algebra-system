/*
 * $Id$
 */

package edu.jas.util;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.apache.log4j.BasicConfigurator;

/**
 * TaggedSocketChannel tests with JUnit.
 * @author Heinz Kredel.
 */

public class TaggedSocketChannelTest extends TestCase {

   public static void main (String[] args) {
       BasicConfigurator.configure();
       junit.textui.TestRunner.run( suite() );
   }

   public TaggedSocketChannelTest(String name) {
          super(name);
   }

   public static Test suite() {
     TestSuite suite= new TestSuite(TaggedSocketChannelTest.class);
     return suite;
   }

   private ChannelFactory cf;
   private SocketChannel sc1;
   private SocketChannel sc2;
   private TaggedSocketChannel tsc1;
   private TaggedSocketChannel tsc2;
   private String host;
   private int port;

   final Integer tag1 = Integer.valueOf(1);


    protected void setUp() {
        host = "localhost";
        port = 4711;
        cf = new ChannelFactory(port);       
        cf.init();
        try {
            sc1 = cf.getChannel(host,port);
            sc2 = cf.getChannel();
            tsc1 = new TaggedSocketChannel(sc1);
            tsc1.init();
            tsc2 = new TaggedSocketChannel(sc2);
            tsc2.init();
        } catch(IOException e) {
            fail("IOException"+e);
        } catch (InterruptedException e) {
            fail("InterruptedException"+e);
        }
    }

    protected void tearDown() {
        cf.terminate();
        tsc1.close();
        tsc2.close();
        sc1.close();
        sc2.close();
        try {
            Thread.sleep(1);
        } catch(InterruptedException e) {
            fail("InterruptedException"+e);
        }
    }

   public void testTaggedSocketChannel0() {
       // test setUp() and tearDown()
   }


   public void testTaggedSocketChannel1() {
       Object o = new IllegalArgumentException("leer");
       Integer err = Integer.valueOf(-1);
       try {
           tsc1.send(err,o);
           fail("no Exception thrown");
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(IllegalArgumentException e) {
           // ok
       }
       err = null;
       try {
           tsc1.send(err,o);
           fail("no Exception thrown");
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(IllegalArgumentException e) {
           // ok
       }
   }


   public void testTaggedSocketChannel2() {
       Object o = Integer.valueOf(0);
       try {
           tsc1.send(tag1,o);
           assertEquals(o,tsc2.receive(tag1));
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(InterruptedException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }


   public void testTaggedSocketChannel3() {
       Object o = Integer.valueOf(0);
       try {
           tsc1.send(tag1,o);
           tsc2.send(tag1,o);
           assertEquals(o,tsc1.receive(tag1));
           assertEquals(o,tsc2.receive(tag1));
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(InterruptedException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }


   public void testTaggedSocketChannel4() {
       int n = 10;
       Object o;
       try {
           for (int i = 0; i < n; i++) {
               o = Integer.valueOf(i);
               tsc1.send(tag1,o);
           }
           assertEquals("#tags == 0 ", 0, tsc1.tagSize());
           for (int i = 0; i < n; i++) {
               o = Integer.valueOf(i);
               assertEquals(o,tsc2.receive(tag1));
           }
           assertTrue("#tags == 1 ", tsc2.tagSize() == 1);
           assertTrue("#messages == 0 ", tsc1.messages() == 0);
           assertTrue("#messages == 0 ", tsc2.messages() == 0);
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(InterruptedException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }


   public void testTaggedSocketChannel5() {
       int n = 10;
       String msg = "Hello_";
       Integer o;
       try {
           for (int i = 0; i < n; i++) {
               o = Integer.valueOf(i);
               tsc1.send(o,msg+i);
           }
           assertTrue("#tags == 0 ", tsc1.tagSize() == 0);
           assertTrue("#messages == 0 ", tsc1.messages() == 0);
           assertTrue("#messages >= 0 ", tsc2.messages() >= 0); // not all 10 arrive in time
           for (int i = 0; i < n; i++) {
               o = Integer.valueOf(i);
               assertEquals(msg+i,tsc2.receive(o));
           }
           //System.out.println("tsc2 = " + tsc2);
           assertTrue("#tags == 10 ", tsc2.tagSize() == 10);
           assertTrue("#messages == 0 ", tsc2.messages() <= 1);
       } catch(IOException e) {
           fail("Exception"+e);
       } catch(InterruptedException e) {
           fail("Exception"+e);
       } catch(ClassNotFoundException e) {
           fail("Exception"+e);
       }
   }

}

