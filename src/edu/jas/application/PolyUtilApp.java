/*
 * $Id$
 */

package edu.jas.application;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;
import edu.jas.ring.GroebnerBaseSeq;


/**
 * Polynomial utilities for applications, e.g. 
 * conversion ExpVector to Product
 * @author Heinz Kredel
 */
public class PolyUtilApp<C extends RingElem<C> > {

    private static final Logger logger = Logger.getLogger(PolyUtilApp.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Product representation.
     * @param pfac product polynomial ring factory.
     * @param c coefficient to be used.
     * @param e exponent vector.
     * @return Product represenation of c X^e in the ring pfac.
     */
    public static <C extends RingElem<C>> 
        Product<GenPolynomial<C>> 
        toProduct( ProductRing<GenPolynomial<C>> pfac, 
                   C c, ExpVector e ) {
        SortedMap<Integer,GenPolynomial<C>> elem 
            = new TreeMap<Integer,GenPolynomial<C>>();
        for ( int i = 0; i < e.length(); i++ ) {
            RingFactory<GenPolynomial<C>> rfac = pfac.getFactory(i);
            GenPolynomialRing<C> fac = (GenPolynomialRing<C>) rfac;
            //GenPolynomialRing<C> cfac = fac.ring;
            long a = e.getVal(i);
            GenPolynomial<C> u;
            if ( a == 0 ) {
               u = fac.getONE();
            } else {
               u = fac.univariate(0,a);
            }
            u = u.multiply(c);
            elem.put( i, u );
        }
        return new Product<GenPolynomial<C>>( pfac, elem );
    }


    /**
     * Product representation for algebraic numbers.
     * @param pfac product polynomial ring factory.
     * @param c coefficient to be used.
     * @param e exponent vector.
     * @return Product represenation of c X^e in the ring pfac.
     */
    public static <C extends GcdRingElem<C>> 
        Product<AlgebraicNumber<C>> 
        toProductX( ProductRing<AlgebraicNumber<C>> pfac, 
                    C c, ExpVector e ) {
        SortedMap<Integer,AlgebraicNumber<C>> elem 
            = new TreeMap<Integer,AlgebraicNumber<C>>();
        for ( int i = 0; i < e.length(); i++ ) {
            RingFactory<AlgebraicNumber<C>> rfac = pfac.getFactory(i);
            AlgebraicNumberRing<C> fac = (AlgebraicNumberRing<C>) rfac;
            GenPolynomialRing<C> cfac = fac.ring;
            long a = e.getVal(i);
            GenPolynomial<C> u;
            if ( a == 0 ) {
               u = cfac.getONE();
            } else {
               u = cfac.univariate(0,a);
            }
            u = u.multiply(c);
            AlgebraicNumber<C> an = new AlgebraicNumber<C>(fac,u);
            elem.put( i, an );
        }
        return new Product<AlgebraicNumber<C>>( pfac, elem );
    }


    /**
     * Product representation for algebraic numbers.
     * @param pfac product polynomial ring factory.
     * @param c coefficient to be used.
     * @param e exponent vector.
     * @return Product represenation of c X^e in the ring pfac.
     */
    public static <C extends GcdRingElem<C>> 
        Product<AlgebraicNumber<C>> 
        toProduct( ProductRing<AlgebraicNumber<C>> pfac, 
                   C c, ExpVector e ) {
        SortedMap<Integer,AlgebraicNumber<C>> elem 
            = new TreeMap<Integer,AlgebraicNumber<C>>();
        boolean z = e.isZERO();
        for ( int i = 0; i < e.length(); i++ ) {
            RingFactory<AlgebraicNumber<C>> rfac = pfac.getFactory(i);
            AlgebraicNumberRing<C> fac = (AlgebraicNumberRing<C>) rfac;
            GenPolynomialRing<C> cfac = fac.ring;
            long a = e.getVal(i);
            GenPolynomial<C> u = null;
            /* variant with mostly zero components */
            if ( z ) {
               //not ok: continue; skip  
               u = cfac.getONE();
            } else if ( a > 0 ) {
               u = cfac.univariate(0,a);
            } // else a == 0
            /*
            if ( a == 0 ) {
               u = cfac.getONE();
            } else {
               u = cfac.univariate(0,a);
            } 
            */
            if ( u != null ) {
               u = u.multiply(c);
               AlgebraicNumber<C> an = new AlgebraicNumber<C>(fac,u);
               elem.put( i, an );
            }
        }
        return new Product<AlgebraicNumber<C>>( pfac, elem );
    }


    /**
     * Product representation.
     * @param pfac product polynomial ring factory.
     * @param A polynomial.
     * @return Product represenation of the terms of A in the ring pfac.
     */
    public static <C extends RingElem<C>> 
        Product<GenPolynomial<C>> 
        toProduct( ProductRing<GenPolynomial<C>> pfac, 
                   GenPolynomial<C> A ) {
        Product<GenPolynomial<C>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Product<GenPolynomial<C>> p = toProduct(pfac,a,e);
            //System.out.println("p = " + p);
            P = P.sum( p );
        }
        return P;
    }


    /**
     * Product representation.
     * @param pfac product polynomial ring factory.
     * @param A polynomial.
     * @return Product represenation of the terms of A in the ring pfac.
     */
    public static <C extends GcdRingElem<C>> 
        Product<AlgebraicNumber<C>> 
        toANProduct( ProductRing<AlgebraicNumber<C>> pfac, 
                     GenPolynomial<C> A ) {
        Product<AlgebraicNumber<C>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Product<AlgebraicNumber<C>> p = toProduct(pfac,a,e);
            //System.out.println("p = " + p);
            P = P.sum( p );
        }
        return P;
    }


    /**
     * Product representation.
     * @param fac product polynomial ring factory.
     * @param A polynomial.
     * @return Product represenation of the coefficients of A in the ring fac.
     */
    public static <C extends GcdRingElem<C>> 
        GenPolynomial<Product<AlgebraicNumber<C>>> 
        toANProductCoeff(GenPolynomialRing<Product<AlgebraicNumber<C>>> fac, 
                         GenPolynomial<GenPolynomial<C>> A ) {
        GenPolynomial<Product<AlgebraicNumber<C>>> P = fac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<AlgebraicNumber<C>>> rpfac = fac.coFac;
        ProductRing<AlgebraicNumber<C>> pfac 
            = (ProductRing<AlgebraicNumber<C>>)rpfac;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Product<AlgebraicNumber<C>> p = toANProduct(pfac,a);
            //System.out.println("p = " + p);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param fac product polynomial ring factory.
     * @param L polynomial list.
     * @return Product represenation of the coefficients 
     * of the elements of L in the ring fac.
     */
    public static <C extends GcdRingElem<C>> 
        List<GenPolynomial<Product<AlgebraicNumber<C>>>> 
        toANProductCoeff(GenPolynomialRing<Product<AlgebraicNumber<C>>> fac, 
                         List<GenPolynomial<GenPolynomial<C>>> L ) {
        List<GenPolynomial<Product<AlgebraicNumber<C>>>> K = null;
        if ( L == null ) {
           return K;
        }
        K = new ArrayList<GenPolynomial<Product<AlgebraicNumber<C>>>>( L.size() );
        if ( L.size() == 0 ) {
           return K;
        }
        for ( GenPolynomial<GenPolynomial<C>> a: L ) {
            GenPolynomial<Product<AlgebraicNumber<C>>> b
               = toANProductCoeff( fac, a );
            K.add( b );
        }
        return K;
    }


    /**
     * Product representation.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        Product<C> toProductGen( ProductRing<C> pfac, C c) {

        SortedMap<Integer,C> elem = new TreeMap<Integer,C>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<C> rfac = pfac.getFactory(i);
            C u = rfac.copy( c );
            if ( u != null && !u.isZERO() ) {
               elem.put( i, u );
            }
        }
        return new Product<C>( pfac, elem );
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Product<C>> 
        toProductGen( GenPolynomialRing<Product<C>> pfac, 
                      GenPolynomial<C> A) {

        GenPolynomial<Product<C>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<C>> rpfac = pfac.coFac;
        ProductRing<C> rfac = (ProductRing<C>) rpfac;
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Product<C> p = toProductGen(rfac,a);
            //System.out.println("p = " + p);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Product<C>>> 
        toProductGen( GenPolynomialRing<Product<C>> pfac, 
                      List<GenPolynomial<C>> L) {

        List<GenPolynomial<Product<C>>> 
            list = new ArrayList<GenPolynomial<Product<C>>>();
        if ( L == null || L.size() == 0 ) {
           return list;
        }
        for ( GenPolynomial<C> a : L ) {
            GenPolynomial<Product<C>> b = toProductGen( pfac, a );
            list.add( b );
        }
        return list;
    }



    /**
     * Product representation.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static 
        Product<ModInteger> toProduct( ProductRing<ModInteger> pfac, BigInteger c) {

        SortedMap<Integer,ModInteger> elem = new TreeMap<Integer,ModInteger>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<ModInteger> rfac = pfac.getFactory(i);
            ModIntegerRing fac = (ModIntegerRing) rfac;
            ModInteger u = fac.fromInteger( c.getVal() );
            if ( u != null && !u.isZERO() ) {
               elem.put( i, u );
            }
        }
        return new Product<ModInteger>( pfac, elem );
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static 
        GenPolynomial<Product<ModInteger>> 
        toProduct( GenPolynomialRing<Product<ModInteger>> pfac, 
                   GenPolynomial<BigInteger> A) {

        GenPolynomial<Product<ModInteger>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<ModInteger>> rpfac = pfac.coFac;
        ProductRing<ModInteger> fac = (ProductRing<ModInteger>)rpfac;
        for ( Map.Entry<ExpVector,BigInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigInteger a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Product<ModInteger> p = toProduct(fac,a);
            //System.out.println("p = " + p);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static 
        List<GenPolynomial<Product<ModInteger>>> 
        toProduct( GenPolynomialRing<Product<ModInteger>> pfac, 
                   List<GenPolynomial<BigInteger>> L) {

        List<GenPolynomial<Product<ModInteger>>> 
            list = new ArrayList<GenPolynomial<Product<ModInteger>>>();
        if ( L == null || L.size() == 0 ) {
           return list;
        }
        for ( GenPolynomial<BigInteger> a : L ) {
            GenPolynomial<Product<ModInteger>> b = toProduct( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Product cover decomposition.
     * @param L list of polynomials to be represented.
     * @return Product representaion of L in the polynomial ring pfac.
     */
    public static 
        PolynomialList<Product<Residue<BigRational>>>
        productSimpleDecomposition( List<GenPolynomial<GenPolynomial<BigRational>>> L ) {

        PolynomialList<Product<Residue<BigRational>>> polylist;

        List<GenPolynomial<Product<Residue<BigRational>>>> plist
            = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>( L.size() );
        if ( L == null || L.size() == 0 ) {
           polylist = new PolynomialList<Product<Residue<BigRational>>>( null, plist );
           return polylist;
        }

        /* compute list of lists of polynomials set to zero */
        List<List<GenPolynomial<BigRational>>> 
            list = new ArrayList<List<GenPolynomial<BigRational>>>();
        GenPolynomialRing<GenPolynomial<BigRational>> rfac = null;
        GenPolynomialRing<BigRational> fac = null;
        List<GenPolynomial<BigRational>> 
            li = new ArrayList<GenPolynomial<BigRational>>();
        list.add( li );
        for ( GenPolynomial<GenPolynomial<BigRational>> A : L ) {
            if ( rfac == null && A != null ) {
               rfac = A.ring;
               fac = (GenPolynomialRing<BigRational>)rfac.coFac;
            }
            for ( GenPolynomial<BigRational> a : A.getMap().values() ) {
                // a != 0
                if ( !a.isConstant() ) {
                    System.out.println("a = " + a);
                    List<List<GenPolynomial<BigRational>>> ll;
                    ll = new ArrayList<List<GenPolynomial<BigRational>>>( list );
                    for ( List<GenPolynomial<BigRational>> pr: list ) {
                        //System.out.println("pr = " + pr);
                        List<GenPolynomial<BigRational>> nl;
                        nl = new ArrayList<GenPolynomial<BigRational>>( pr );
                        if ( !nl.contains( a ) ) {
                           nl.add( a );
                           ll.add( nl );
                        }
                    }
                    list = ll;
                }
            }
        }
        //System.out.println("list = ");
        /* compute product ring of residues */
        GroebnerBase<BigRational> bb = new GroebnerBaseSeq<BigRational>(); 
        List<RingFactory<Residue<BigRational>>> 
            lfac = new ArrayList<RingFactory<Residue<BigRational>>>();
        for ( List<GenPolynomial<BigRational>> pr: list ) {
            //System.out.println("\npr    = " + pr);
            if ( pr.size() > 2 ) {
                continue; // i.e. only simple decomposition
            }
            pr = bb.GB( pr );
            //System.out.println("pr.gb = " + pr);
            Ideal<BigRational> I = new Ideal<BigRational>(fac, pr, true);
            //System.out.println("ideal = " + I);
            ResidueRing<BigRational> rr = new ResidueRing<BigRational>( I );
            System.out.println("rr    = " + rr);
            if ( !lfac.contains( rr ) ) {
               lfac.add( rr );
            }
        }
        //System.out.println("lfac = " + lfac);
        ProductRing<Residue<BigRational>> nr 
             = new ProductRing<Residue<BigRational>>( lfac );
        //System.out.println("nr   = " + nr);

        GenPolynomialRing<Product<Residue<BigRational>>> pfac
           = new GenPolynomialRing<Product<Residue<BigRational>>>(nr,rfac);

        plist = toProductRes( pfac, L );
        System.out.println("\nplist ====================== ");
        for ( GenPolynomial<Product<Residue<BigRational>>> p: plist ) {
            System.out.println("\np    = " + p);
        }
        polylist = new PolynomialList<Product<Residue<BigRational>>>( pfac, plist );
        return polylist;
    }


    /**
     * Product cover decomposition.
     * @param L list of polynomials to be represented.
     * @return Product representaion of L in the polynomial ring pfac.
     */
    public static 
        PolynomialList<Product<Residue<BigRational>>>
        productEmptyDecomposition( List<GenPolynomial<GenPolynomial<BigRational>>> L ) {

        PolynomialList<Product<Residue<BigRational>>> polylist;

        List<GenPolynomial<Product<Residue<BigRational>>>> plist
            = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>( L.size() );
        if ( L == null || L.size() == 0 ) {
           polylist = new PolynomialList<Product<Residue<BigRational>>>( null, plist );
           return polylist;
        }

        /* compute list of lists of polynomials set to zero */
        List<List<GenPolynomial<BigRational>>> 
            list = new ArrayList<List<GenPolynomial<BigRational>>>();
        GenPolynomialRing<GenPolynomial<BigRational>> rfac = null;
        GenPolynomialRing<BigRational> fac = null;
        List<GenPolynomial<BigRational>> 
            li = new ArrayList<GenPolynomial<BigRational>>();
        list.add( li );
        for ( GenPolynomial<GenPolynomial<BigRational>> A : L ) {
            if ( rfac == null && A != null ) {
               rfac = A.ring;
               fac = (GenPolynomialRing<BigRational>)rfac.coFac;
               break;
            }
        }
        //System.out.println("list = ");
        /* compute product ring of residues */
        GroebnerBase<BigRational> bb = new GroebnerBaseSeq<BigRational>(); 
        List<RingFactory<Residue<BigRational>>> 
            lfac = new ArrayList<RingFactory<Residue<BigRational>>>();
        for ( List<GenPolynomial<BigRational>> pr: list ) {
            //System.out.println("\npr    = " + pr);
            pr = bb.GB( pr );
            //System.out.println("pr.gb = " + pr);
            Ideal<BigRational> I = new Ideal<BigRational>(fac, pr, true);
            //System.out.println("ideal = " + I);
            ResidueRing<BigRational> rr = new ResidueRing<BigRational>( I );
            System.out.println("rr    = " + rr);
            if ( !lfac.contains( rr ) ) {
               lfac.add( rr );
            }
        }
        //System.out.println("lfac = " + lfac);
        ProductRing<Residue<BigRational>> nr 
             = new ProductRing<Residue<BigRational>>( lfac );
        //System.out.println("nr   = " + nr);

        GenPolynomialRing<Product<Residue<BigRational>>> pfac
           = new GenPolynomialRing<Product<Residue<BigRational>>>(nr,rfac);

        plist = toProductRes( pfac, L );
        System.out.println("\nplist ====================== ");
        for ( GenPolynomial<Product<Residue<BigRational>>> p: plist ) {
            System.out.println("\np    = " + p);
        }
        polylist = new PolynomialList<Product<Residue<BigRational>>>( pfac, plist );
        return polylist;
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static 
        List<GenPolynomial<Product<Residue<BigRational>>>> 
        toProductRes( GenPolynomialRing<Product<Residue<BigRational>>> pfac, 
                      List<GenPolynomial<GenPolynomial<BigRational>>> L) {

        List<GenPolynomial<Product<Residue<BigRational>>>> 
            list = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>();
        if ( L == null || L.size() == 0 ) {
           return list;
        }
        GenPolynomial<Product<Residue<BigRational>>> b;
        for ( GenPolynomial<GenPolynomial<BigRational>> a : L ) {
            b = toProductRes( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Product representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static 
        GenPolynomial<Product<Residue<BigRational>>> 
        toProductRes( GenPolynomialRing<Product<Residue<BigRational>>> pfac, 
                      GenPolynomial<GenPolynomial<BigRational>> A) {

        GenPolynomial<Product<Residue<BigRational>>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<Residue<BigRational>>> rpfac = pfac.coFac;
        ProductRing<Residue<BigRational>> fac = (ProductRing<Residue<BigRational>>)rpfac;
        Product<Residue<BigRational>> p;
        for ( Map.Entry<ExpVector,GenPolynomial<BigRational>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<BigRational> a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            p = toProductRes(fac,a);
            //System.out.println("p = " + p);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static 
        Product<Residue<BigRational>> 
        toProductRes( ProductRing<Residue<BigRational>> pfac, 
                      GenPolynomial<BigRational> c) {

        SortedMap<Integer,Residue<BigRational>> elem = new TreeMap<Integer,Residue<BigRational>>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<Residue<BigRational>> rfac = pfac.getFactory(i);
            ResidueRing<BigRational> fac = (ResidueRing<BigRational>) rfac;
            Residue<BigRational> u = new Residue<BigRational>( fac, c );
                //fac.fromInteger( c.getVal() );
            if ( u != null && !u.isZERO() ) {
               elem.put( i, u );
            }
        }
        return new Product<Residue<BigRational>>( pfac, elem );
    }


    /**
     * Product slice.
     * @param L list of polynomials with  to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static 
        Map<Ideal<BigRational>,PolynomialList<GenPolynomial<BigRational>>>
        productSlice( PolynomialList<Product<Residue<BigRational>>> L ) {

        Map<Ideal<BigRational>,PolynomialList<GenPolynomial<BigRational>>> map;
        RingFactory<Product<Residue<BigRational>>> fpr = L.ring.coFac;
        ProductRing<Residue<BigRational>> pr = (ProductRing<Residue<BigRational>>)fpr;
        int s = pr.length();
        map = new HashMap<Ideal<BigRational>,PolynomialList<GenPolynomial<BigRational>>>();
        List<GenPolynomial<GenPolynomial<BigRational>>> slist;

        List<GenPolynomial<Product<Residue<BigRational>>>> plist = L.list;
        PolynomialList<GenPolynomial<BigRational>> spl;

        for ( int i = 0; i < s; i++ ) {
            RingFactory<Residue<BigRational>> r = pr.getFactory( i );
            ResidueRing<BigRational> rr = (ResidueRing<BigRational>) r;
            Ideal<BigRational> id = rr.ideal;
            GenPolynomialRing<BigRational> cof = rr.ring;
            GenPolynomialRing<GenPolynomial<BigRational>> pfc; 
            pfc = new GenPolynomialRing<GenPolynomial<BigRational>>(cof,L.ring);
            slist = fromProduct( pfc, plist, i );
            spl = new PolynomialList<GenPolynomial<BigRational>>(pfc,slist);
            PolynomialList<GenPolynomial<BigRational>> d = map.get( id );
            if ( d != null ) {
               throw new RuntimeException("ideal exists twice " + id);
            }
            map.put( id, spl );
        }
        return map;
    }


    /**
     * Product slice at i.
     * @param L list of polynomials with  to be represented.
     * @param i index of slice.
     * @return Slice of of L at i.
     */
    public static 
        PolynomialList<GenPolynomial<BigRational>>
        productSlice( PolynomialList<Product<Residue<BigRational>>> L, int i ) {

        RingFactory<Product<Residue<BigRational>>> fpr = L.ring.coFac;
        ProductRing<Residue<BigRational>> pr = (ProductRing<Residue<BigRational>>)fpr;
        int s = pr.length();
        List<GenPolynomial<GenPolynomial<BigRational>>> slist;

        List<GenPolynomial<Product<Residue<BigRational>>>> plist = L.list;
        PolynomialList<GenPolynomial<BigRational>> spl;

        RingFactory<Residue<BigRational>> r = pr.getFactory( i );
        ResidueRing<BigRational> rr = (ResidueRing<BigRational>) r;
        Ideal<BigRational> id = rr.ideal;
        GenPolynomialRing<BigRational> cof = rr.ring;
        GenPolynomialRing<GenPolynomial<BigRational>> pfc; 
        pfc = new GenPolynomialRing<GenPolynomial<BigRational>>(cof,L.ring);
        slist = fromProduct( pfc, plist, i );
        spl = new PolynomialList<GenPolynomial<BigRational>>(pfc,slist);

        return spl;
    }


    /*
     * Product slice at i.
     * @param L list of polynomials with  to be represented.
     * @param i index of slice.
     * @return Slice of of L at i.
     */
    public static <C extends RegularRingElem<C>>
        PolynomialList<C>
        productSliceRaw( PolynomialList<C> L, int i ) {
        return (PolynomialList)productSlice( (PolynomialList)L, i);
    }


    /**
     * From product representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of L in the polynomial ring pfac.
     */
    public static 
        List<GenPolynomial<GenPolynomial<BigRational>>>
        fromProduct( GenPolynomialRing<GenPolynomial<BigRational>> pfac,
                     List<GenPolynomial<Product<Residue<BigRational>>>> L,
                     int i ) {

        List<GenPolynomial<GenPolynomial<BigRational>>> 
            list = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();

        if ( L == null || L.size() == 0 ) {
           return list;
        }
        GenPolynomial<GenPolynomial<BigRational>> b;
        for ( GenPolynomial<Product<Residue<BigRational>>> a : L ) {
            b = fromProduct( pfac, a, i );
            if ( b != null && !b.isZERO() ) {
               list.add( b.abs() );
            }
        }
        return list;
    }


    /**
     * From product representation.
     * @param pfac polynomial ring factory.
     * @param P polynomial to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of P in the polynomial ring pfac.
     */
    public static 
        GenPolynomial<GenPolynomial<BigRational>>
        fromProduct( GenPolynomialRing<GenPolynomial<BigRational>> pfac,
                     GenPolynomial<Product<Residue<BigRational>>> P,
                     int i ) {

        GenPolynomial<GenPolynomial<BigRational>> b = pfac.getZERO();
        if ( P == null || P.isZERO() ) {
           return b;
        }
        RingFactory<GenPolynomial<BigRational>> cf = pfac.coFac;
        GenPolynomialRing<BigRational> fac = (GenPolynomialRing<BigRational>)cf;

        for ( Map.Entry<ExpVector,Product<Residue<BigRational>>> y: P.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Product<Residue<BigRational>> a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);

            Residue<BigRational> r = a.get(i);
            if ( r != null && !r.isZERO() ) {
               GenPolynomial<BigRational> p = r.val;
               //System.out.println("p = " + p);
               b = b.sum( p , e );
            }
        }
        return b;
    }


    /**
     * Product slice to String.
     * @param L list of polynomials with  to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static 
        String
        productSliceToString( Map<Ideal<BigRational>,PolynomialList<GenPolynomial<BigRational>>> L ) {
        StringBuffer sb = new StringBuffer("\nproductSlice ------------------------- begin");
        for ( Ideal<BigRational> id: L.keySet() ) {
            sb.append("\n\ncondition == 0:\n");
            sb.append( id.list.toString() );
            PolynomialList<GenPolynomial<BigRational>> pl = L.get( id );
            //GenPolynomialRing<GenPolynomial<BigRational>> r = pl.ring;
            //List<GenPolynomial<GenPolynomial<BigRational>>> ll = pl.list;
            sb.append("\ncorresponding ideal:\n");
            sb.append( pl.toString() );
        }
        sb.append("\nproductSlice ------------------------- end");
        return sb.toString();
    }


    /**
     * Product cover decomposition.
     * @param P polynomial to be represented.
     * @param L list of already represented polynomials.
     * @return Product representaion of L togehher with P in the polynomial ring pfac.
     */
    public static <C extends RegularRingElem<C>>
        PolynomialList<C>
        productDecomposition( PolynomialList<C> L,
                              GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
           return L;
        }
        // this is a hack for the moment
        PolynomialList<Product<Residue<BigRational>>> Lp = null;
        GenPolynomial<Product<Residue<BigRational>>> Pp = null;
        ProductRing<Residue<BigRational>> pr = null;
        GenPolynomialRing<BigRational> rfac = null;
        GenPolynomialRing<C> fac = L.ring;
        if ( fac.coFac instanceof ProductRing ) {
            ProductRing pp = (ProductRing) fac.coFac;
            if ( pp.getFactory(0) instanceof ResidueRing ) {
                ResidueRing rr = (ResidueRing)pp.getFactory(0); 
                if ( rr.ring.coFac instanceof BigRational ) {
                    Lp = (PolynomialList/*<Product<Residue<BigRational>>>*/)L; 
                    Pp = (GenPolynomial/*<Product<Residue<BigRational>>>*/)P;
                    pr = pp;
                    rfac = rr.ring;
                }
            }
        }
        if ( Lp == null || Pp == null ) {
            throw new RuntimeException("ring not correct");
        }
        boolean changed = false;
        GroebnerBase<BigRational> bb = new GroebnerBaseSeq<BigRational>(); 
        for ( Product<Residue<BigRational>> a : Pp.getMap().values() ) {
            // a != 0
            if ( a.get(0) == null || a.get(0).isZERO() ) {
               continue;
            }
            if ( a.get(0).isConstant() || changed ) {
               break;
            } else {
               System.out.println("decomp a = " + a);
               GenPolynomial<BigRational> A = a.get(0).val;
               for ( int i = 0; i < pr.length() /*&& (!changed)*/; i++ ) {
                   RingFactory<Residue<BigRational>> rrr = pr.getFactory(i); 
                   ResidueRing<BigRational> rr = (ResidueRing<BigRational>)rrr; 
                   Ideal<BigRational> id = rr.ideal;
                   List<GenPolynomial<BigRational>> fl = id.list.list;
                   if ( !id.contains(A) ) {
                      List<GenPolynomial<BigRational>> nl 
                          = new ArrayList<GenPolynomial<BigRational>>( fl );
                      nl.add(A);
                      nl = bb.GB(nl);
                      //System.out.println("decomp nl = " + nl);
                      Ideal<BigRational> I = new Ideal<BigRational>(rfac, nl, true);
                      //System.out.println("ideal = " + I);
                      ResidueRing<BigRational> rrn = new ResidueRing<BigRational>( I );
                      System.out.println("rrn    = " + rrn);
                      //System.out.println("pr     = " + pr);
                      if ( ! pr.containsFactory( rrn ) ) {
                         pr.addFactory( rrn );
                         System.out.println("pr     = " + pr);
                         changed = true;
                         break;
                      }
                   }
               }
            }
        }
        if ( ! changed ) {
           List<GenPolynomial<C>> ll = new ArrayList<GenPolynomial<C>>( L.list );
           ll.add( P );
           PolynomialList<C> M = new PolynomialList<C>(L.ring,ll);
           return M;
        }
        GenPolynomialRing<Product<Residue<BigRational>>> pring = Lp.ring;
        List<GenPolynomial<Product<Residue<BigRational>>>> Npp 
            = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>( Lp.list );
        Npp.add( Pp );
        List<GenPolynomial<Product<Residue<BigRational>>>> Mpp 
            = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>( Npp.size() );
        for ( GenPolynomial<Product<Residue<BigRational>>> b : Npp ) {
            //System.out.println("b      = " + b);
            GenPolynomial<Product<Residue<BigRational>>> n = pring.getZERO();
            for ( Map.Entry<ExpVector,Product<Residue<BigRational>>> m: b.getMap().entrySet() ) {
                ExpVector e = m.getKey();
                Product<Residue<BigRational>> c = m.getValue();
                Product<Residue<BigRational>> d = c.extend(pr);
                n = n.sum( d, e );
            }
            //System.out.println("n      = " + n);
            if ( ! n.isZERO() ) {
               Mpp.add( n );
            }
        }
        PolynomialList<C> M 
          = (PolynomialList)new PolynomialList<Product<Residue<BigRational>>>(pring,Mpp);
        return M;
    }

}
