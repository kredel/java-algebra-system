/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;


/**
 * RelationTable tests with JUnit.
 * @author Heinz Kredel.
 */

public class RelationTableTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RelationTableTest</CODE> object.
     * @param name String.
     */
    public RelationTableTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RelationTableTest.class);
        return suite;
    }


    RelationTable<BigRational> table;


    GenSolvablePolynomialRing<BigRational> ring;


    int rl = 6;


    @Override
    protected void setUp() {
        BigRational cfac = new BigRational(1);
        ring = new GenSolvablePolynomialRing<BigRational>(cfac, rl);
        table = ring.table; // non null
    }


    @Override
    protected void tearDown() {
        table = null;
        ring = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        table = new RelationTable<BigRational>(ring);
        assertEquals("size() = 0", 0, table.size());
        assertEquals("ring == table.ring", ring, table.ring);

        String s = "RelationTable[]";
        String t = table.toString();
        assertEquals("RelationTable[]", s, t);
    }


    /**
     * Test update one key.
     */
    public void testUpdateOneKey() {
        table = ring.table;
        assertEquals("size() = 0", 0, table.size());

        //ExpVector z = ring.evzero;
        ExpVector e = ExpVector.create(rl, 2, 1);
        ExpVector f = ExpVector.create(rl, 3, 1); // insert in empty

        ExpVector ef = e.sum(f);

        GenSolvablePolynomial<BigRational> a = ring.getONE();
        GenSolvablePolynomial<BigRational> b = ring.getONE().multiply(ef);
        GenSolvablePolynomial<BigRational> rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 1", 1, table.size());

        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 1); // insert in beginning

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 2", 2, table.size());

        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 2);

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 3", 3, table.size());

        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 4);

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 4", 4, table.size());

        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 3); // insert in middle

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 5", 5, table.size());

        //System.out.println("table = " + table);
    }


    /**
     * Test update more keys.
     */
    public void testUpdateKeys() {
        table = ring.table;
        assertEquals("size() = 0", 0, table.size());

        //ExpVector z = ring.evzero;
        ExpVector e = ExpVector.create(rl, 2, 1);
        ExpVector f = ExpVector.create(rl, 3, 1); // insert in empty

        ExpVector ef = e.sum(f);

        GenSolvablePolynomial<BigRational> a = ring.getONE();
        GenSolvablePolynomial<BigRational> b = ring.getONE().multiply(ef);
        GenSolvablePolynomial<BigRational> rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 1", 1, table.size());

        e = ExpVector.create(rl, 0, 1);
        f = ExpVector.create(rl, 2, 1);

        ef = e.sum(f);

        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 2", 2, table.size());

        e = ExpVector.create(rl, 2, 1);
        f = ExpVector.create(rl, 4, 1);

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 3", 3, table.size());

        //System.out.println("table = " + table);
    }


    /**
     * Test lookup one key.
     */
    public void testLookupOneKey() {
        table = ring.table;
        assertEquals("size() = 0", 0, table.size());

        //ExpVector z = ring.evzero;
        ExpVector e = ExpVector.create(rl, 2, 1);
        ExpVector f = ExpVector.create(rl, 3, 1); // insert in empty

        ExpVector ef = e.sum(f);
        GenSolvablePolynomial<BigRational> a = ring.getONE();
        GenSolvablePolynomial<BigRational> b = ring.getONE().multiply(ef);
        GenSolvablePolynomial<BigRational> rel = (GenSolvablePolynomial<BigRational>) a.sum(b);
        GenSolvablePolynomial<BigRational> r1 = rel;

        table.update(e, f, rel);
        assertEquals("size() = 1", 1, table.size());

        TableRelation<BigRational> r = table.lookup(e, f);
        //System.out.println("relation = " + r);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 1); // insert in beginning

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 2", 2, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 2);

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 3", 3, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 4);

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 4", 4, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 2, 2);
        f = ExpVector.create(rl, 3, 3); // insert in middle

        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 5", 5, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        // lookup only
        e = ExpVector.create(rl, 2, 1);
        f = ExpVector.create(rl, 3, 1);

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", r1, r.p);

        //System.out.println("table = " + table);
    }


    /**
     * Test lookup keys.
     */
    public void testLookupKeys() {
        table = ring.table;
        assertEquals("size() = 0", 0, table.size());

        //ExpVector z = ring.evzero;
        ExpVector e = ExpVector.create(rl, 2, 1);
        ExpVector f = ExpVector.create(rl, 3, 1);

        ExpVector ef = e.sum(f);
        GenSolvablePolynomial<BigRational> a = ring.getONE();
        GenSolvablePolynomial<BigRational> b = ring.getONE().multiply(ef);
        GenSolvablePolynomial<BigRational> rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 1", 1, table.size());

        TableRelation<BigRational> r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 0, 1);
        f = ExpVector.create(rl, 2, 1);
        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 2", 2, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        e = ExpVector.create(rl, 2, 1);
        f = ExpVector.create(rl, 4, 1);
        ef = e.sum(f);
        b = ring.getONE().multiply(ef);
        rel = (GenSolvablePolynomial<BigRational>) a.sum(b);

        table.update(e, f, rel);
        assertEquals("size() = 3", 3, table.size());

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = p", rel, r.p);


        //System.out.println("table = " + table);
    }


    /**
     * Test lookup symmetric products.
     */
    public void testSymmetric() {
        table = ring.table;
        assertEquals("size() = 0", 0, table.size());

        //ExpVector z = ring.evzero;
        ExpVector e = ExpVector.create(rl, 2, 1);
        ExpVector f = ExpVector.create(rl, 3, 1);

        ExpVector ef = e.sum(f);

        //GenSolvablePolynomial<BigRational> a = ring.getONE();
        GenSolvablePolynomial<BigRational> b = ring.getONE().multiply(ef);
        //GenSolvablePolynomial<BigRational> rel 
        //   = (GenSolvablePolynomial<BigRational>)a.add(b);

        TableRelation<BigRational> r = table.lookup(e, f);
        //System.out.println("relation = " + r);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = b", b, r.p);


        e = ExpVector.create(rl, 0, 1);
        f = ExpVector.create(rl, 2, 1);
        ef = e.sum(f);
        b = ring.getONE().multiply(ef);

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = b", b, r.p);


        e = ExpVector.create(rl, 2, 1);
        f = ExpVector.create(rl, 4, 1);
        ef = e.sum(f);
        b = ring.getONE().multiply(ef);

        r = table.lookup(e, f);
        assertEquals("e = e", null, r.e);
        assertEquals("f = f", null, r.f);
        assertEquals("p = b", b, r.p);

        assertEquals("size() = 0", 0, table.size());
        //System.out.println("table = " + table);
    }


    /**
     * Test to relation list and back.
     */
    public void testRelationList() {
        List<GenSolvablePolynomial<BigRational>> rels = table.relationList();
        assertEquals("list.size() = 0", 0, rels.size());

        table.addSolvRelations(rels);
        assertEquals("size() = 0", 0, table.size());

        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(ring);
        rels = table.relationList();
        assertEquals("list.size() = r/2", rl / 2, rels.size() / 3);

        GenSolvablePolynomialRing<BigRational> ring2;
        ring2 = new GenSolvablePolynomialRing<BigRational>(ring.coFac, ring);
        assertEquals("size() = 0", 0, ring2.table.size());
        ring2.table.addSolvRelations(rels);

        //System.out.println("table1 = " + ring.table.toScript());
        //System.out.println("table2 = " + ring2.table.toScript());
        assertEquals("size() = r/2", rl / 2, ring2.table.size());
        assertEquals("rel1 == rel2: ", ring.table, ring2.table);
    }

}
