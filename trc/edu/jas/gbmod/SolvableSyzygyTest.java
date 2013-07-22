/*
 * $Id$
 */

package edu.jas.gbmod;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseSeq;

import edu.jas.poly.ModuleList;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.RelationTable;


/**
 * SolvableSyzygy tests with JUnit. 
 * @author Heinz Kredel.
 */

public class SolvableSyzygyTest extends TestCase {

    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>SolvableSyzygyTest</CODE> object.
     * @param name String.
     */
    public SolvableSyzygyTest(String name) {
        super(name);
    }


    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(SolvableSyzygyTest.class);
        return suite;
    }


    int port = 4711;
    String host = "localhost";

    BigRational cfac;
    GenSolvablePolynomialRing<BigRational> fac;

    PolynomialList<BigRational> F;
    List<GenSolvablePolynomial<BigRational>> G;

    GenSolvablePolynomial<BigRational> a;
    GenSolvablePolynomial<BigRational> b;
    GenSolvablePolynomial<BigRational> c;
    GenSolvablePolynomial<BigRational> d;
    GenSolvablePolynomial<BigRational> e;
    GenSolvablePolynomial<BigRational> zero;
    GenSolvablePolynomial<BigRational> one;

    TermOrder tord;
    RelationTable table;

    List<GenSolvablePolynomial<BigRational>> L;
    List<List<GenSolvablePolynomial<BigRational>>> K;
    List<GenSolvablePolynomial<BigRational>> V;
    List<List<GenSolvablePolynomial<BigRational>>> W;
    ModuleList<BigRational> M;
    ModuleList<BigRational> N;
    ModuleList<BigRational> Z;

    SolvableGroebnerBaseAbstract<BigRational> sbb;
    ModSolvableGroebnerBase<BigRational> msbb;

    SolvableSyzygyAbstract<BigRational> ssz;

    int rl = 4; //4; //3; 
    int kl = 5;
    int ll = 7;
    int el = 2;
    float q = 0.3f; //0.4f

    protected void setUp() {
        cfac = new BigRational(1);
        tord = new TermOrder();
        fac = new GenSolvablePolynomialRing<BigRational>(cfac,rl,tord);
        table = fac.table; 
        a = b = c = d = e = null;
        L = null;
        K = null;
        V = null;
        do {
            a = fac.random(kl, ll, el, q );
        } while ( a.isZERO() );
        do {
            b = fac.random(kl, ll, el, q );
        } while ( b.isZERO() );
        do {
            c = fac.random(kl, ll, el, q );
        } while ( c.isZERO() );
        do {
            d = fac.random(kl, ll, el, q );
        } while ( d.isZERO() );
        e = d; //fac.random(kl, ll, el, q );
        one = fac.getONE();
        zero = fac.getZERO();
        sbb = new SolvableGroebnerBaseSeq<BigRational>();
        msbb = new ModSolvableGroebnerBaseAbstract<BigRational>();
        ssz = new SolvableSyzygyAbstract<BigRational>();
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        L = null;
        K = null;
        V = null;
        fac = null;
        tord = null;
        table = null;
        sbb = null;
        msbb = null;
        ssz = null;
    }


    /**
     * Test sequential SolvableSyzygy.
     */
    public void testSequentialSolvableSyzygy() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        assertTrue("not isZERO( a )", !a.isZERO() );
        L.add(a);
        assertTrue("isGB( { a } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        assertTrue("is ZR( { a } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( b )", !b.isZERO() );
        L.add(b);
        L = sbb.leftGB(L);
        assertTrue("isGB( { a, b } )", sbb.isLeftGB(L) );
        //System.out.println("\nL = " + L );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( c )", !c.isZERO() );
        L.add(c);
        L = sbb.leftGB(L);
        //System.out.println("\nL = " + L );
        assertTrue("isGB( { a, b, c } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b, c } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( d )", !d.isZERO() );
        L.add(d);
        L = sbb.leftGB(L);
        //System.out.println("\nL = " + L );
        assertTrue("isGB( { a, b, c, d } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b, c, d } )", ssz.isLeftZeroRelation(K,L) );
    }


    /**
     * Test sequential Weyl SolvableSyzygy.
     */
    public void testSequentialWeylSolvableSyzygy() {
        int rloc = 4;
        fac = new GenSolvablePolynomialRing<BigRational>(cfac,rloc);

        WeylRelations<BigRational> wl = new WeylRelations<BigRational>(fac);
        wl.generate();
        table = fac.table;

        a = fac.random(kl, ll, el, q );
        b = fac.random(kl, ll, el, q );
        c = fac.random(kl, ll, el, q );
        d = fac.random(kl, ll, el, q );
        e = d; //fac.random(kl, ll, el, q );

        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        assertTrue("not isZERO( a )", !a.isZERO() );
        L.add(a);
        assertTrue("isGB( { a } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        assertTrue("is ZR( { a } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( b )", !b.isZERO() );
        L.add(b);
        L = sbb.leftGB(L);
        assertTrue("isGB( { a, b } )", sbb.isLeftGB(L) );
        //System.out.println("\nL = " + L );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(K,L) );

        // useless since 1 in GB
        assertTrue("not isZERO( c )", !c.isZERO() );
        L.add(c);
        L = sbb.leftGB(L);
        //System.out.println("\nL = " + L );
        assertTrue("isGB( { a, b, c } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b, c } )", ssz.isLeftZeroRelation(K,L) );

        // useless since 1 in GB
        assertTrue("not isZERO( d )", !d.isZERO() );
        L.add(d);
        L = sbb.leftGB(L);
        //System.out.println("\nL = " + L );
        assertTrue("isGB( { a, b, c, d } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelations( L );
        //System.out.println("\nK = " + K );
        assertTrue("is ZR( { a, b, c, d } )", ssz.isLeftZeroRelation(K,L) );
    }


    /**
     * Test sequential module SolvableSyzygy.
     */
    public void testSequentialModSolvableSyzygy() {
        W = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        assertTrue("not isZERO( a )", !a.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a); V.add(zero); V.add(one);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        assertTrue("isGB( { (a,0,1) } )", msbb.isLeftGB(M) );

        N = msbb.leftGB( M );
        assertTrue("isGB( { (a,0,1) } )", msbb.isLeftGB(N) );

        Z = ssz.leftZeroRelations(N);
        //System.out.println("Z = " + Z);
        assertTrue("is ZR( { a) } )", ssz.isLeftZeroRelation(Z,N) );

        assertTrue("not isZERO( b )", !b.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b); V.add(one); V.add(zero);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        //System.out.println("W = " + W.size() );

        N = msbb.leftGB( M );
        assertTrue("isGB( { a, b } )", msbb.isLeftGB(N) );

        Z = ssz.leftZeroRelations(N);
        //System.out.println("Z = " + Z);
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(Z,N) );

        assertTrue("not isZERO( c )", !c.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c); V.add(one); V.add(zero);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        //System.out.println("W = " + W.size() );

        N = msbb.leftGB( M );
        //System.out.println("GB(M) = " + N);
        assertTrue("isGB( { a,b,c) } )", msbb.isLeftGB(N) );

        Z = ssz.leftZeroRelations(N);
        //System.out.println("Z = " + Z);
        //boolean b = ssz.isLeftZeroRelation(Z,N);
        //System.out.println("boolean = " + b);
        assertTrue("is ZR( { a,b,c } )", ssz.isLeftZeroRelation(Z,N) );
    }


    /**
     * Test sequential arbitrary base Syzygy.
     */
    public void testSequentialArbitrarySyzygy() {
        L = new ArrayList<GenSolvablePolynomial<BigRational>>();

        assertTrue("not isZERO( a )", !a.isZERO() );
        L.add(a);
        assertTrue("isGB( { a } )", sbb.isLeftGB(L) );
        K = ssz.leftZeroRelationsArbitrary( L );
        assertTrue("is ZR( { a } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( b )", !b.isZERO() );
        L.add(b);
        K = ssz.leftZeroRelationsArbitrary( L );
        //System.out.println("\nN = " + N );
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( c )", !c.isZERO() );
        L.add(c);
        K = ssz.leftZeroRelationsArbitrary( L );
        //System.out.println("\nN = " + N );
        assertTrue("is ZR( { a, b, c } )", ssz.isLeftZeroRelation(K,L) );

        assertTrue("not isZERO( d )", !d.isZERO() );
        L.add(d);
        K = ssz.leftZeroRelationsArbitrary( L );
        //System.out.println("\nN = " + N );
        assertTrue("is ZR( { a, b, c, d } )", ssz.isLeftZeroRelation(K,L) );
    }


    /**
     * Test sequential arbitrary base Syzygy, ex CLO 2, p 214 ff.
     */
    @SuppressWarnings("unchecked") 
    public void testSequentialArbitrarySyzygyCLO() {
        PolynomialList<BigRational> F = null;

        String exam = "Rat(x,y) G "
            + "( "  
            + "( x y + x ), " 
            + "( y^2 + 1 ) "
            + ") ";
        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser
            = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigRational>) parser.nextSolvablePolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        L = F.castToSolvableList();
        K = ssz.leftZeroRelationsArbitrary( L );
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(K,L) );
    }


    /**
     * Test sequential arbitrary base Syzygy, ex WA_32.
     */
    @SuppressWarnings("unchecked") 
    public void testSequentialArbitrarySyzygyWA32() {
        PolynomialList<BigRational> F = null;

        String exam = "Rat(e1,e2,e3) L "
            + "RelationTable "
            + "( "
            + " ( e3 ), ( e1 ), ( e1 e3 - e1 ), "
            + " ( e3 ), ( e2 ), ( e2 e3 - e2 ) "
            + ")"
            + "( "  
            + " ( e1 e3^3 + e2^2 ), " 
            + " ( e1^3 e2^2 + e3 ), "
            + " ( e3^3 + e3^2 ) "
            + ") ";
        Reader source = new StringReader( exam );
        GenPolynomialTokenizer parser
            = new GenPolynomialTokenizer( source );
        try {
            F = (PolynomialList<BigRational>) parser.nextSolvablePolynomialSet();
        } catch(ClassCastException e) {
            fail(""+e);
        } catch(IOException e) {
            fail(""+e);
        }
        //System.out.println("F = " + F);

        L = F.castToSolvableList();
        K = ssz.leftZeroRelationsArbitrary( L );
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(K,L) );
    }


    /**
     * Test sequential arbitrary module SolvableSyzygy.
     */
    public void testSequentialArbitraryModSolvableSyzygy() {
        W = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

        assertTrue("not isZERO( a )", !a.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(a); V.add(zero); V.add(one);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        assertTrue("isGB( { (a,0,1) } )", msbb.isLeftGB(M) );

        Z = ssz.leftZeroRelationsArbitrary(M);
        //System.out.println("Z = " + Z);
        assertTrue("is ZR( { a) } )", ssz.isLeftZeroRelation(Z,M) );

        assertTrue("not isZERO( b )", !b.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(b); V.add(one); V.add(zero);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        //System.out.println("W = " + W.size() );

        Z = ssz.leftZeroRelationsArbitrary(M);
        //System.out.println("Z = " + Z);
        assertTrue("is ZR( { a, b } )", ssz.isLeftZeroRelation(Z,M) );

        assertTrue("not isZERO( c )", !c.isZERO() );
        V = new ArrayList<GenSolvablePolynomial<BigRational>>();
        V.add(c); V.add(one); V.add(zero);
        W.add(V);
        M = new ModuleList<BigRational>(fac,W);
        //System.out.println("W = " + W.size() );

        Z = ssz.leftZeroRelationsArbitrary(M);
        //System.out.println("Z = " + Z);
        //boolean b = ssz.isLeftZeroRelation(Z,N);
        //System.out.println("boolean = " + b);
        assertTrue("is ZR( { a,b,c } )", ssz.isLeftZeroRelation(Z,M) );
    }


    /**
     * Test Ore conditions.
     */
    public void testOreConditions() {
        do {
            a = fac.random(kl, ll-1, el, q);
        } while ( a.isZERO() );
        do {
            b = fac.random(kl, ll-1, el, q);
        } while ( b.isZERO() );
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        GenSolvablePolynomial<BigRational>[] oc = ssz.leftOreCond(a,b);
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        c = oc[0].multiply(a);
        d = oc[1].multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("c_0 * a = c_1 * b: ", c, d);
        assertTrue("left Ore condition: ", ssz.isLeftOreCond(a,b,oc));

        oc = ssz.rightOreCond(a,b);
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        c = a.multiply(oc[0]);
        d = b.multiply(oc[1]);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a * c_0 = b * c_1: ", c, d);
        assertTrue("right Ore condition: ", ssz.isRightOreCond(a,b,oc));
    }


    /**
     * Test Ore conditions for residues.
     */
    public void testResidueOreConditions() {
        // construct ideal { x_i^2 - i, ... } for generators x_i
        F = new PolynomialList<BigRational>(fac,fac.generators());
        //System.out.println("F = " + F);
        List<GenSolvablePolynomial<BigRational>> gens = F.castToSolvableList();
        //System.out.println("gens = " + gens);
        L = new ArrayList<GenSolvablePolynomial<BigRational>>(gens.size()-1); 
        long i = 2;
        for ( GenSolvablePolynomial<BigRational> g : gens ) {
	    if ( g.isONE() ) {
                continue;
            }
            GenSolvablePolynomial<BigRational> p = g.multiply(g);
            p = (GenSolvablePolynomial<BigRational>) p.subtract(fac.fromInteger(i++));
            L.add(p);
        }
        //System.out.println("L = " + L);

        do {
            a = fac.random(kl, ll-1, el+1, q);
        } while ( a.isZERO() );
        do {
            b = fac.random(kl, ll-1, el+1, q);
        } while ( b.isZERO() );
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        GenSolvablePolynomial<BigRational>[] oc = ssz.leftOreCond(a,b);
        //System.out.println("oc[0] = " + oc[0]);
        //System.out.println("oc[1] = " + oc[1]);
        c = oc[0].multiply(a);
        d = oc[1].multiply(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("c_0 * a = c_1 * b: ", c, d);
        assertTrue("left Ore condition: ", ssz.isLeftOreCond(a,b,oc));

        // now mod ideal(L):
        GenSolvablePolynomial<BigRational> ar, br, cr, dr, or0, or1;
        ar = sbb.sred.leftNormalform(L,a);
        br = sbb.sred.leftNormalform(L,b);
        //System.out.println("ar = " + ar);
        //System.out.println("br = " + br);
        cr = oc[0].multiply(ar);
        dr = oc[1].multiply(br);
        //System.out.println("cr = " + cr);
        //System.out.println("dr = " + dr);
        //this is not true: assertEquals("c_0 * a = c_1 * b: ", cr, dr);
        cr = sbb.sred.leftNormalform(L,cr);
        dr = sbb.sred.leftNormalform(L,dr);
        //System.out.println("cr = " + cr);
        //System.out.println("dr = " + dr);
        assertEquals("cr_0 * ar = cr_1 * br: ", cr, dr);

        or0 = sbb.sred.leftNormalform(L,oc[0]);
        or1 = sbb.sred.leftNormalform(L,oc[1]);
        //System.out.println("oc0 = " + oc[0]);
        //System.out.println("oc1 = " + oc[1]);
        //System.out.println("or0 = " + or0);
        //System.out.println("or1 = " + or1);
        cr = or0.multiply(ar);
        dr = or1.multiply(br);
        //okay w/o: cr = sbb.sred.leftNormalform(L,cr);
        //okay w/o: dr = sbb.sred.leftNormalform(L,dr);
        //System.out.println("cr = " + cr);
        //System.out.println("dr = " + dr);
        assertEquals("cr_0 * ar = cr_1 * br: ", cr, dr);
        oc[0] = or0;
        oc[1] = or1;
        assertTrue("left Ore condition: ", ssz.isLeftOreCond(ar,br,oc));
    }

}
