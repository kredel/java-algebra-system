/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.RelationTable;

import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * RelationTable Test using JUnit 
 * @author Heinz Kredel.
 */

public class RelationTableTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RelationTableTest</CODE> object.
 * @param name String
 */
   public RelationTableTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RelationTableTest.class);
     return suite;
   }

   RelationTable table;

   protected void setUp() {
       table = null;
   }

   protected void tearDown() {
       table = null;
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     String s = "RelationTable[]";
     String t = table.toString();
     assertEquals("RelationTable[]",s,t);
   }


/**
 * Test update one key
 * 
 */
 public void testUpdateOneKey() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     int rl = 5;
     ExpVector z = new ExpVector(rl);
     ExpVector e = new ExpVector(rl,3,1);
     ExpVector f = new ExpVector(rl,2,1); // insert in empty

     ExpVector ef = ExpVector.EVSUM(e,f);
     Coefficient one = BigRational.ONE;
     OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
     OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     OrderedPolynomial rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 1",1,table.size());

     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,1); // insert in beginning

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 2",2,table.size());

     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,2);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 3",3,table.size());

     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,4);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 4",4,table.size());

     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,3); // insert in middle

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 5",5,table.size());

     //System.out.println("table = " + table);
   }


/**
 * Test update more keys
 * 
 */
 public void testUpdateKeys() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     int rl = 5;
     ExpVector z = new ExpVector(rl);
     ExpVector e = new ExpVector(rl,3,1);
     ExpVector f = new ExpVector(rl,2,1);

     ExpVector ef = ExpVector.EVSUM(e,f);
     Coefficient one = BigRational.ONE;
     OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
     OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     OrderedPolynomial rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 1",1,table.size());

     e = new ExpVector(rl,2,1);
     f = new ExpVector(rl,0,1);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 2",2,table.size());

     e = new ExpVector(rl,4,1);
     f = new ExpVector(rl,2,1);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 3",3,table.size());

     //System.out.println("table = " + table);
   }


/**
 * Test lookup one key
 * 
 */
 public void testLookupOneKey() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     int rl = 5;
     ExpVector z = new ExpVector(rl);
     ExpVector e = new ExpVector(rl,3,1);
     ExpVector f = new ExpVector(rl,2,1); // insert in empty

     ExpVector ef = ExpVector.EVSUM(e,f);
     Coefficient one = BigRational.ONE;
     OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
     OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     OrderedPolynomial rel = a.add(b);
     OrderedPolynomial r1 = rel;

     table.update(e,f,rel);
     assertEquals("size() = 1",1,table.size());

     TableRelation r = table.lookup(e,f,a);
     //System.out.println("relation = " + r);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,1); // insert in beginning

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 2",2,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,2);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 3",3,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,4);

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 4",4,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,3,2);
     f = new ExpVector(rl,2,3); // insert in middle

     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 5",5,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     // lookup only
     e = new ExpVector(rl,3,1);
     f = new ExpVector(rl,2,1); 

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",r1,r.p);

     //System.out.println("table = " + table);
   }

/**
 * Test lookup keys
 * 
 */
 public void testLookupKeys() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     int rl = 5;
     ExpVector z = new ExpVector(rl);
     ExpVector e = new ExpVector(rl,3,1);
     ExpVector f = new ExpVector(rl,2,1);

     ExpVector ef = ExpVector.EVSUM(e,f);
     Coefficient one = BigRational.ONE;
     OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
     OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     OrderedPolynomial rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 1",1,table.size());

     TableRelation r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,2,1);
     f = new ExpVector(rl,0,1);
     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 2",2,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     e = new ExpVector(rl,4,1);
     f = new ExpVector(rl,2,1);
     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);
     rel = a.add(b);

     table.update(e,f,rel);
     assertEquals("size() = 3",3,table.size());

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = p",rel,r.p);


     //System.out.println("table = " + table);
   }


/**
 * Test lookup symmetric products
 * 
 */
 public void testSymmetric() {
     table = new RelationTable();
     assertEquals("size() = 0",0,table.size());

     int rl = 5;
     ExpVector z = new ExpVector(rl);
     ExpVector e = new ExpVector(rl,3,1);
     ExpVector f = new ExpVector(rl,2,1);

     ExpVector ef = ExpVector.EVSUM(e,f);
     Coefficient one = BigRational.ONE;
     OrderedPolynomial a = new RatSolvableOrderedMapPolynomial(table,one,z);
     OrderedPolynomial b = new RatSolvableOrderedMapPolynomial(table,one,ef);

     TableRelation r = table.lookup(e,f,a);
     //System.out.println("relation = " + r);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = b",b,r.p);


     e = new ExpVector(rl,2,1);
     f = new ExpVector(rl,0,1);
     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = b",b,r.p);


     e = new ExpVector(rl,4,1);
     f = new ExpVector(rl,2,1);
     ef = ExpVector.EVSUM(e,f);
     b = new RatSolvableOrderedMapPolynomial(table,one,ef);

     r = table.lookup(e,f,a);
     assertEquals("e = e",null,r.e);
     assertEquals("f = f",null,r.f);
     assertEquals("p = b",b,r.p);

     assertEquals("size() = 0",0,table.size());
     //System.out.println("table = " + table);
   }

}
