/*
 * $Id$
 */

package edu.jas.gbufd;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.structure.RingFactory;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;


/**
 * Reduction tests with JUnit.
 * @author Heinz Kredel.
 */

public class ReductionTest extends TestCase {

    /**
     * main
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }

    /**
     * Constructs a <CODE>ReductionTest</CODE> object.
     * @param name String
     */
    public ReductionTest(String name) {
        super(name);
    }

    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite= new TestSuite(ReductionTest.class);
        return suite;
    }


    GenPolynomialRing<BigRational> fac;

    GenPolynomial<BigRational> a, b, c, d, e;

    List<GenPolynomial<BigRational>> L;
    PolynomialList<BigRational> F, G;

    //ReductionSeq<BigRational> red;
    //Reduction<BigRational> redpar;

    int rl = 4; 
    int kl = 10;
    int ll = 11;
    int el = 5;
    float q = 0.6f;

    protected void setUp() {
        a = b = c = d = e = null;
        fac = new GenPolynomialRing<BigRational>( new BigRational(0), rl );
        //red = new ReductionSeq<BigRational>();
        //redpar = new ReductionPar<BigRational>();
    }

    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        //red = null;
        //redpar = null;
    }


    /**
     * Test rational coefficient r-reduction.
     */
    public void testRationalRReduction() {
        RingFactory<BigRational> bi = new BigRational(0);
        ProductRing<BigRational> pr = new ProductRing<BigRational>(bi,3);

        GenPolynomialRing<Product<BigRational>> fac 
            = new GenPolynomialRing<Product<BigRational>>( pr, rl );

        RReductionSeq<Product<BigRational>> rred 
            = new RReductionSeq<Product<BigRational>>();

        GenPolynomial<Product<BigRational>> a = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigRational>> b = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigRational>> d;

        while ( a.isZERO() ) {
            a = fac.random(kl, ll, el, q );
        }
        while ( b.isZERO() ) {
            b = fac.random(kl, ll, el, q );
        }

        assertTrue("not isZERO( a )", !a.isZERO() );

        List<GenPolynomial<Product<BigRational>>> L 
            = new ArrayList<GenPolynomial<Product<BigRational>>>();
        L.add(a);

        GenPolynomial<Product<BigRational>> e 
            = rred.normalform( L, a );
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", rred.isNormalform(L,e) );

        assertTrue("not isZERO( b )", !b.isZERO() );

        L.add(b);
        e = rred.normalform( L, a );
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", rred.isNormalform(L,e) );

        GenPolynomial<Product<BigRational>> c = fac.getONE();
        a = a.sum(c);
        e = rred.normalform( L, a );
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", rred.isNormalform(L,e) ); 

        L = new ArrayList<GenPolynomial<Product<BigRational>>>();
        L.add( a );
        assertTrue("isTopRed( a )", rred.isTopReducible(L,a) ); 
        assertTrue("isRed( a )", rred.isReducible(L,a) ); 
        //b = fac.random(kl, ll, el, q );
        L.add( b );
        assertTrue("isTopRed( b )", rred.isTopReducible(L,b) ); 
        assertTrue("isRed( b )", rred.isReducible(L,b) ); 
        c = fac.random(kl, ll, el, q );
        e = rred.normalform( L, c );
        assertTrue("isNF( e )", rred.isNormalform(L,e) ); 

        c = rred.booleanClosure(a);
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        assertTrue("isBC( c )", rred.isBooleanClosed(c) ); 

        b = a.subtract(c);
        //System.out.println("b = " + b);
        d = rred.booleanRemainder(a);
        //System.out.println("d = " + d);
        assertEquals("a-BC(a)=BR(a)", b, d ); 

        e = c.sum(d);
        //System.out.println("e = " + e);
        assertEquals("a==BC(a)+BR(a)", a, e ); 

        List<GenPolynomial<Product<BigRational>>> B;
        List<GenPolynomial<Product<BigRational>>> Br;
        L = new ArrayList<GenPolynomial<Product<BigRational>>>();
        L.add( a );
        B  = rred.booleanClosure(L);
        Br = rred.reducedBooleanClosure(L);
        //System.out.println("L  = " + L);
        //System.out.println("B = " + B);
        //System.out.println("Br = " + Br);
        assertTrue("isBC( B )", rred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        L.add( b );
        B  = rred.booleanClosure(L);
        Br = rred.reducedBooleanClosure(L);
        //System.out.println("L = " + L);
        //System.out.println("B = " + B);
        //System.out.println("Br = " + Br);
        assertTrue("isBC( B )", rred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        while ( c.isZERO() ) {
            c = fac.random(kl, ll, el, q );
        }
        L.add( c );
        B  = rred.booleanClosure(L);
        Br = rred.reducedBooleanClosure(L);
        //System.out.println("L = " + L);
        //System.out.println("B = " + B);
        //System.out.println("Br = " + Br);
        assertTrue("isBC( B )", rred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        while ( d.isZERO() ) {
            d = fac.random(kl, ll, el, q );
        }
        L.add( d );
        B  = rred.booleanClosure(L);
        Br = rred.reducedBooleanClosure(L);
        //System.out.println("L = " + L);
        //System.out.println("B = " + B);
        //System.out.println("Br = " + Br);
        assertTrue("isBC( B )", rred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 
    }


    /**
     * Test rational coefficient r-reduction with recording.
     */
    public void testRatRReductionRecording() {
        RingFactory<BigRational> bi = new BigRational(0);
        ProductRing<BigRational> pr = new ProductRing<BigRational>(bi,3);

        GenPolynomialRing<Product<BigRational>> fac 
            = new GenPolynomialRing<Product<BigRational>>( pr, rl );

        RReductionSeq<Product<BigRational>> rred 
            = new RReductionSeq<Product<BigRational>>();

        GenPolynomial<Product<BigRational>> a = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigRational>> b = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigRational>> c, d, e;

        while ( a.isZERO() ) {
            a = fac.random(kl, ll, el, q );
        }
        while ( b.isZERO() ) {
            b = fac.random(kl, ll, el, q );
        }
        c = fac.random(kl, ll, el, q );
        d = fac.random(kl, ll, el, q );

        List<GenPolynomial<Product<BigRational>>> row = null;
        List<GenPolynomial<Product<BigRational>>> L;

        assertTrue("not isZERO( a )", !a.isZERO() );
        assertTrue("not isZERO( b )", !b.isZERO() );

        L = new ArrayList<GenPolynomial<Product<BigRational>>>();

        L.add(a);
        row = new ArrayList<GenPolynomial<Product<BigRational>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        e = rred.normalform( row, L, a );
        //not for regular rings: assertTrue("isZERO( e )", e.isZERO() );

        //System.out.println("row = " + row);
        //System.out.println("L   = " + L);
        //System.out.println("a   = " + a);
        //System.out.println("e   = " + e);

        assertTrue("is Reduction ", rred.isReductionNF(row,L,a,e) );

        L.add(b);
        row = new ArrayList<GenPolynomial<Product<BigRational>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        e = rred.normalform( row, L, b );
        assertTrue("is Reduction ", rred.isReductionNF(row,L,b,e) );

        L.add(c);
        row = new ArrayList<GenPolynomial<Product<BigRational>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        e = rred.normalform( row, L, c );
        assertTrue("is Reduction ", rred.isReductionNF(row,L,c,e) );

        L.add(d);
        row = new ArrayList<GenPolynomial<Product<BigRational>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        e = rred.normalform( row, L, d );
        assertTrue("is Reduction ", rred.isReductionNF(row,L,d,e) );
    }


    /**
     * Test integer coefficient pseudo-reduction.
     */
    public void testIntegerPseudoReduction() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac 
            = new GenPolynomialRing<BigInteger>( bi, rl );

        PseudoReductionSeq<BigInteger> pred = new PseudoReductionSeq<BigInteger>();

        GenPolynomial<BigInteger> a = fac.random(kl, ll, el, q );
        GenPolynomial<BigInteger> b = fac.random(kl, ll, el, q );

        if ( a.isZERO() || b.isZERO() ) {
            return;
        }

        assertTrue("not isZERO( a )", !a.isZERO() );

        List<GenPolynomial<BigInteger>> L 
            = new ArrayList<GenPolynomial<BigInteger>>();
        L.add(a);

        GenPolynomial<BigInteger> e;
        e = pred.normalform( L, a );
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO() );

        assertTrue("not isZERO( b )", !b.isZERO() );

        L.add(b);
        e = pred.normalform( L, a );
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e ) some times", e.isZERO() ); 


        GenPolynomial<BigInteger> c = fac.getONE();
        a = a.sum(c);
        e = pred.normalform( L, a );
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isConstant( e ) some times", e.isConstant() ); 

        L = new ArrayList<GenPolynomial<BigInteger>>();
        a = c.multiply( bi.fromInteger(4) );
        b = c.multiply( bi.fromInteger(5) );
        L.add( a );
        e = pred.normalform( L, b );
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO() ); 

        a = fac.random(kl, ll, el, q ); //.abs();
        b = fac.random(kl, ll, el, q ); //.abs();
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
 
        L = new ArrayList<GenPolynomial<BigInteger>>();
        L.add( a );
        assertTrue("isTopRed( a )", pred.isTopReducible(L,a) ); 
        assertTrue("isRed( a )", pred.isReducible(L,a) ); 
        b = fac.random(kl, ll, el, q );
        L.add( b );
        assertTrue("isTopRed( b )", pred.isTopReducible(L,b) ); 
        assertTrue("isRed( b )", pred.isReducible(L,b) ); 
        c = fac.random(kl, ll, el, q );
        e = pred.normalform( L, c );
        assertTrue("isNF( e )", pred.isNormalform(L,e) ); 
    }


    /**
     * Test integer pseudo coefficient reduction with recording.
     */
    public void testIntReductionRecording() {
        BigInteger bi = new BigInteger(0);
        GenPolynomialRing<BigInteger> fac 
            = new GenPolynomialRing<BigInteger>( bi, rl );

        PseudoReductionSeq<BigInteger> pred = new PseudoReductionSeq<BigInteger>();

        GenPolynomial<BigInteger> a = fac.random(kl, ll, el, q );
        GenPolynomial<BigInteger> b = fac.random(kl, ll, el, q );
        GenPolynomial<BigInteger> c, d, e, f;

        if ( a.isZERO() || b.isZERO() ) {
            return;
        }
        c = fac.random(kl, ll, el+1, q );
        d = fac.random(kl, ll, el+2, q );

        // ------------
        //a = fac.parse(" 1803 x0 * x1^4 - 299 x0^3 * x1^2 - 464 x1^4 + 648 x1^3 + 383 x0^3 + 1633 ");
        //b = fac.parse(" 593 x0^4 * x1^4 - 673 x0^3 * x1^4 + 36 x0^4 + 627 x1^2 + 617 x1 + 668 x0 + 168 ");
        //b = b.multiply( fac.parse(" 10567759154481 " ) );
        //c = a.multiply( fac.parse(" 593 x0^3 - 938267 x0^2 - 435355888 x0 - 202005132032 ") );

        //d = a.multiply( fac.parse(" 3475696715811 x0^3 - 3050126808003 x0^2 - 784946666064 x0 - 202005132032 ") );

        //-------------

        List<GenPolynomial<BigInteger>> row = null;
        List<GenPolynomial<BigInteger>> L;

        PseudoReductionEntry<BigInteger> mf;

        assertTrue("not isZERO( a )", !a.isZERO() );
        assertTrue("not isZERO( b )", !b.isZERO() );

        L = new ArrayList<GenPolynomial<BigInteger>>();

        L.add(a);
        row = new ArrayList<GenPolynomial<BigInteger>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = pred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = pred.normalform( row, L, f );
        assertTrue("isZERO( e )", e.isZERO() );
        assertTrue("is Reduction ", pred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", pred.isReductionNF(row,L,f,e) );

        L.add(b);
        row = new ArrayList<GenPolynomial<BigInteger>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = pred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = pred.normalform( row, L, f );
        assertTrue("is Reduction ", pred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", pred.isReductionNF(row,L,f,e) );

        L.add(c);
        row = new ArrayList<GenPolynomial<BigInteger>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = pred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = pred.normalform( row, L, f );
        assertTrue("is Reduction ", pred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", pred.isReductionNF(row,L,f,e) );

        L.add(d);
        row = new ArrayList<GenPolynomial<BigInteger>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = pred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = pred.normalform( row, L, f );
        assertTrue("is Reduction ", pred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", pred.isReductionNF(row,L,f,e) );
    }


    /**
     * Test integer coefficient pseudo r-reduction.
     */
    public void testIntegerRReduction() {
        RingFactory<BigInteger> bi = new BigInteger(0);
        ProductRing<BigInteger> pr = new ProductRing<BigInteger>(bi,3);

        GenPolynomialRing<Product<BigInteger>> fac 
            = new GenPolynomialRing<Product<BigInteger>>( pr, rl );

        RReductionSeq<Product<BigInteger>> rpred 
            = new RPseudoReductionSeq<Product<BigInteger>>();

        GenPolynomial<Product<BigInteger>> a = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigInteger>> b = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigInteger>> c, d, e;

        while ( a.isZERO() ) {
            a = fac.random(kl, ll, el, q );
        }
        while ( b.isZERO() ) {
            b = fac.random(kl, ll, el, q );
        }

        assertTrue("not isZERO( a )", !a.isZERO() );

        List<GenPolynomial<Product<BigInteger>>> L 
            = new ArrayList<GenPolynomial<Product<BigInteger>>>();
        L.add(a);

        e = rpred.normalform( L, a );
        //System.out.println("a = " + a);
        //System.out.println("e = " + e);
        assertTrue("isNF( e )", rpred.isNormalform(L,e) );

        assertTrue("not isZERO( b )", !b.isZERO() );

        L.add(b);
        e = rpred.normalform( L, a );
        assertTrue("isNF( e )", rpred.isNormalform(L,e) );

        c = fac.getONE();
        a = a.sum(c);
        e = rpred.normalform( L, a );
        assertTrue("isNF( e )", rpred.isNormalform(L,e) ); 

        L = new ArrayList<GenPolynomial<Product<BigInteger>>>();
        L.add( a );
        assertTrue("isTopRed( a )", rpred.isTopReducible(L,a) ); 
        assertTrue("isRed( a )", rpred.isReducible(L,a) ); 
        //b = fac.random(kl, ll, el, q );
        L.add( b );
        assertTrue("isTopRed( b )", rpred.isTopReducible(L,b) ); 
        assertTrue("isRed( b )", rpred.isReducible(L,b) ); 
        c = fac.random(kl, ll, el, q );
        e = rpred.normalform( L, c );
        assertTrue("isNF( e )", rpred.isNormalform(L,e) ); 

        c = rpred.booleanClosure(a);
        //System.out.println("\nboolean closure");
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        assertTrue("isBC( c )", rpred.isBooleanClosed(c) ); 

        b = a.subtract(c);
        //System.out.println("b = " + b);
        d = rpred.booleanRemainder(a);
        //System.out.println("d = " + d);
        assertEquals("a-BC(a)=BR(a)", b, d ); 

        e = c.sum(d);
        //System.out.println("e = " + e);
        assertEquals("a==BC(a)+BR(a)", a, e ); 

        List<GenPolynomial<Product<BigInteger>>> B;
        List<GenPolynomial<Product<BigInteger>>> Br;
        L = new ArrayList<GenPolynomial<Product<BigInteger>>>();
        L.add( a );
        B  = rpred.booleanClosure(L);
        Br = rpred.reducedBooleanClosure(L);
        assertTrue("isBC( B )", rpred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rpred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rpred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        L.add( b );
        B  = rpred.booleanClosure(L);
        Br = rpred.reducedBooleanClosure(L);
        assertTrue("isBC( B )", rpred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rpred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rpred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        L.add( c );
        B  = rpred.booleanClosure(L);
        Br = rpred.reducedBooleanClosure(L);
        assertTrue("isBC( B )", rpred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rpred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rpred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 

        while ( d.isZERO() ) {
            d = fac.random(kl, ll, el, q );
        }
        L.add( d );
        B  = rpred.booleanClosure(L);
        Br = rpred.reducedBooleanClosure(L);
        assertTrue("isBC( B )", rpred.isBooleanClosed(B) ); 
        assertTrue("isBC( Br )", rpred.isReducedBooleanClosed(Br) ); 
        assertTrue("isBC( Br )", rpred.isBooleanClosed(Br) ); 
        //not always: assertEquals("B == Br", B, Br ); 
    }


    /**
     * Test integer pseudo coefficient r-reduction with recording.
     */
    public void testIntRReductionRecording() {
        RingFactory<BigInteger> bi = new BigInteger(0);
        ProductRing<BigInteger> pr = new ProductRing<BigInteger>(bi,3);

        GenPolynomialRing<Product<BigInteger>> fac 
            = new GenPolynomialRing<Product<BigInteger>>( pr, rl );

        RPseudoReductionSeq<Product<BigInteger>> rpred 
            = new RPseudoReductionSeq<Product<BigInteger>>();

        GenPolynomial<Product<BigInteger>> a = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigInteger>> b = fac.random(kl, ll, el, q );
        GenPolynomial<Product<BigInteger>> c, d, e, f;

        while ( a.isZERO() ) {
            a = fac.random(kl, ll, el, q );
        }
        while ( b.isZERO() ) {
            b = fac.random(kl, ll, el, q );
        }
        assertTrue("not isZERO( a )", !a.isZERO() );
        assertTrue("not isZERO( b )", !b.isZERO() );

        c = fac.random(kl, ll, el, q );
        d = fac.random(kl, ll, el, q );

        List<GenPolynomial<Product<BigInteger>>> row = null;
        List<GenPolynomial<Product<BigInteger>>> L;

        PseudoReductionEntry<Product<BigInteger>> mf;

        L = new ArrayList<GenPolynomial<Product<BigInteger>>>();

        L.add(a);
        row = new ArrayList<GenPolynomial<Product<BigInteger>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = rpred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = rpred.normalform( row, L, f );
        //not for regular rings: assertTrue("isZERO( e )", e.isZERO() );
        assertTrue("is Reduction ", rpred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", rpred.isReductionNF(row,L,f,e) );

        L.add(b);
        row = new ArrayList<GenPolynomial<Product<BigInteger>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = rpred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = rpred.normalform( row, L, f );
        assertTrue("is Reduction ", rpred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", rpred.isReductionNF(row,L,f,e) );

        L.add(c);
        row = new ArrayList<GenPolynomial<Product<BigInteger>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = rpred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = rpred.normalform( row, L, f );
        assertTrue("is Reduction ", rpred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", rpred.isReductionNF(row,L,f,e) );

        L.add(d);
        row = new ArrayList<GenPolynomial<Product<BigInteger>>>( L.size() );
        for ( int m = 0; m < L.size(); m++ ) {
            row.add(null);
        }
        mf = rpred.normalformFactor( L, a );
        e = mf.pol;
        f = a.multiply( mf.multiplicator );
        e = rpred.normalform( row, L, f );
        assertTrue("is Reduction ", rpred.isNormalform(L,e) );
        assertTrue("is ReductionNF ", rpred.isReductionNF(row,L,f,e) );
    }

}
