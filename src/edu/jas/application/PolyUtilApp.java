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
import java.util.Set;
import java.util.TreeSet;

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
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;
import edu.jas.ring.GroebnerBaseSeq;


/**
 * Polynomial utilities for applications,  
 * for example conversion ExpVector to Product.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class PolyUtilApp<C extends RingElem<C> > {

    private static final Logger logger = Logger.getLogger(PolyUtilApp.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Product representation.
     * @param <C> coefficient type.
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
     * @param <C> coefficient type.
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
     * @param <C> coefficient type.
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
     * @param <C> coefficient type.
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
            Product<GenPolynomial<C>> p = toProduct(pfac,a,e);
            P = P.sum( p );
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
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
            Product<AlgebraicNumber<C>> p = toProduct(pfac,a,e);
            P = P.sum( p );
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
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
            Product<AlgebraicNumber<C>> p = toANProduct(pfac,a);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
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
     * @param <C> coefficient type.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        Product<C> 
        toProductGen( ProductRing<C> pfac, C c) {

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
     * @param <C> coefficient type.
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
            Product<C> p = toProductGen(rfac,a);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
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
        Product<ModInteger> toProduct( ProductRing<ModInteger> pfac, 
                                       BigInteger c) {

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
            Product<ModInteger> p = toProduct(fac,a);
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
     * Product simple cover decomposition.
     * @param <C> coefficient type.
     * @param L list of polynomials to be covered.
     * @return simple cover decomposition for L.
     */
    public static <C extends GcdRingElem<C>>
        PolynomialList<Product<Residue<C>>>
        productSimpleDecomposition( List<GenPolynomial<GenPolynomial<C>>> L ) {

        PolynomialList<Product<Residue<C>>> polylist;

        List<GenPolynomial<Product<Residue<C>>>> plist
            = new ArrayList<GenPolynomial<Product<Residue<C>>>>( L.size() );
        if ( L == null || L.size() == 0 ) {
           polylist = new PolynomialList<Product<Residue<C>>>( null, plist );
           return polylist;
        }

        /* compute list of lists of polynomials set to zero */
        List<List<GenPolynomial<C>>> 
            list = new ArrayList<List<GenPolynomial<C>>>();
        GenPolynomialRing<GenPolynomial<C>> rfac = null;
        GenPolynomialRing<C> fac = null;
        List<GenPolynomial<C>> 
            li = new ArrayList<GenPolynomial<C>>();
        list.add( li );
        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
          if ( A == null ) {
               continue;
          }
            if ( rfac == null && A != null ) {
               rfac = A.ring;
               fac = (GenPolynomialRing<C>)rfac.coFac;
            }
            for ( GenPolynomial<C> a : A.getMap().values() ) {
                // a != 0
                if ( !a.isConstant() ) {
                    System.out.println("a = " + a);
                    List<List<GenPolynomial<C>>> ll;
                    ll = new ArrayList<List<GenPolynomial<C>>>( list );
                    for ( List<GenPolynomial<C>> pr: list ) {
                        //System.out.println("pr = " + pr);
                        List<GenPolynomial<C>> nl;
                        nl = new ArrayList<GenPolynomial<C>>( pr );
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
        GroebnerBase<C> bb = new GroebnerBaseSeq<C>(); 
        List<RingFactory<Residue<C>>> 
            lfac = new ArrayList<RingFactory<Residue<C>>>();
        for ( List<GenPolynomial<C>> pr: list ) {
            //System.out.println("\npr    = " + pr);
            if ( pr.size() > 1 ) {
                continue; // i.e. only simple decomposition
            }
            pr = bb.GB( pr );
            //System.out.println("pr.gb = " + pr);
            Ideal<C> I = new Ideal<C>(fac, pr, true);
            //System.out.println("ideal = " + I);
            ResidueRing<C> rr = new ResidueRing<C>( I );
            System.out.println("rr    = " + rr);
            if ( !lfac.contains( rr ) ) {
               lfac.add( rr );
            }
        }
        //System.out.println("lfac = " + lfac);
        ProductRing<Residue<C>> nr 
             = new ProductRing<Residue<C>>( lfac );
        //System.out.println("nr   = " + nr);

        GenPolynomialRing<Product<Residue<C>>> pfac
           = new GenPolynomialRing<Product<Residue<C>>>(nr,rfac);

        plist = toProductRes( pfac, L );
        System.out.println("\nplist ====================== ");
        for ( GenPolynomial<Product<Residue<C>>> p: plist ) {
            System.out.println("\np    = " + p);
        }
        polylist = new PolynomialList<Product<Residue<C>>>( pfac, plist );
        return polylist;
    }


    /**
     * Product empty cover decomposition.
     * @param <C> coefficient type.
     * @param L list of polynomials to be covered.
     * @return empty cover decomposition.
     */
    public static <C extends GcdRingElem<C>>
        PolynomialList<Product<Residue<C>>>
        productEmptyDecomposition( List<GenPolynomial<GenPolynomial<C>>> L ) {

        PolynomialList<Product<Residue<C>>> polylist;

        List<GenPolynomial<Product<Residue<C>>>> plist
            = new ArrayList<GenPolynomial<Product<Residue<C>>>>( L.size() );
        if ( L == null || L.size() == 0 ) {
           polylist = new PolynomialList<Product<Residue<C>>>( null, plist );
           return polylist;
        }

        /* compute list of lists of polynomials set to zero */
        List<List<GenPolynomial<C>>> 
            list = new ArrayList<List<GenPolynomial<C>>>();
        GenPolynomialRing<GenPolynomial<C>> rfac = null;
        GenPolynomialRing<C> fac = null;
        List<GenPolynomial<C>> 
            li = new ArrayList<GenPolynomial<C>>();
        list.add( li );
        for ( GenPolynomial<GenPolynomial<C>> A : L ) {
            if ( rfac == null && A != null ) {
               rfac = A.ring;
               fac = (GenPolynomialRing<C>)rfac.coFac;
               break;
            }
        }
        //System.out.println("list = ");
        /* compute product ring of residues */
        GroebnerBase<C> bb = new GroebnerBaseSeq<C>(); 
        List<RingFactory<Residue<C>>> 
            lfac = new ArrayList<RingFactory<Residue<C>>>();
        for ( List<GenPolynomial<C>> pr: list ) {
            //System.out.println("\npr    = " + pr);
            pr = bb.GB( pr );
            //System.out.println("pr.gb = " + pr);
            Ideal<C> I = new Ideal<C>(fac, pr, true);
            //System.out.println("ideal = " + I);
            ResidueRing<C> rr = new ResidueRing<C>( I );
            System.out.println("rr    = " + rr);
            if ( !lfac.contains( rr ) ) {
               lfac.add( rr );
            }
        }
        //System.out.println("lfac = " + lfac);
        ProductRing<Residue<C>> nr 
             = new ProductRing<Residue<C>>( lfac );
        //System.out.println("nr   = " + nr);

        GenPolynomialRing<Product<Residue<C>>> pfac
           = new GenPolynomialRing<Product<Residue<C>>>(nr,rfac);

        plist = toProductRes( pfac, L );
        System.out.println("\nplist ====================== ");
        for ( GenPolynomial<Product<Residue<C>>> p: plist ) {
            System.out.println("\np    = " + p);
        }
        polylist = new PolynomialList<Product<Residue<C>>>( pfac, plist );
        return polylist;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Product<Residue<C>>>> 
        toProductRes( GenPolynomialRing<Product<Residue<C>>> pfac, 
                      List<GenPolynomial<GenPolynomial<C>>> L) {

        List<GenPolynomial<Product<Residue<C>>>> 
            list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if ( L == null || L.size() == 0 ) {
           return list;
        }
        GenPolynomial<Product<Residue<C>>> b;
        for ( GenPolynomial<GenPolynomial<C>> a : L ) {
            b = toProductRes( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Product represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Product<Residue<C>>> 
        toProductRes( GenPolynomialRing<Product<Residue<C>>> pfac, 
                      GenPolynomial<GenPolynomial<C>> A) {

        GenPolynomial<Product<Residue<C>>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<Residue<C>>> rpfac = pfac.coFac;
        ProductRing<Residue<C>> fac = (ProductRing<Residue<C>>)rpfac;
        Product<Residue<C>> p;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = toProductRes(fac,a);
            P = P.sum( p , e );
        }
        return P;
    }


    /**
     * Product representation.
     * @param <C> coefficient type.
     * @param pfac product ring factory.
     * @param c coefficient to be represented.
     * @return Product represenation of c in the ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        Product<Residue<C>> 
        toProductRes( ProductRing<Residue<C>> pfac, 
                      GenPolynomial<C> c) {

        SortedMap<Integer,Residue<C>> elem = new TreeMap<Integer,Residue<C>>();
        for ( int i = 0; i < pfac.length(); i++ ) {
            RingFactory<Residue<C>> rfac = pfac.getFactory(i);
            ResidueRing<C> fac = (ResidueRing<C>) rfac;
            Residue<C> u = new Residue<C>( fac, c );
                //fac.fromInteger( c.getVal() );
            if ( u != null && !u.isZERO() ) {
               elem.put( i, u );
            }
        }
        return new Product<Residue<C>>( pfac, elem );
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be represented.
     * @return Represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Residue<C>>> 
        toResidue( GenPolynomialRing<Residue<C>> pfac, 
                   List<GenPolynomial<GenPolynomial<C>>> L) {
        List<GenPolynomial<Residue<C>>> 
            list = new ArrayList<GenPolynomial<Residue<C>>>();
        if ( L == null || L.size() == 0 ) {
           return list;
        }
        GenPolynomial<Residue<C>> b;
        for ( GenPolynomial<GenPolynomial<C>> a : L ) {
            b = toResidue( pfac, a );
            list.add( b );
        }
        return list;
    }


    /**
     * Residue coefficient representation.
     * @param pfac polynomial ring factory.
     * @param A polynomial to be represented.
     * @return Represenation of A in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Residue<C>> 
        toResidue( GenPolynomialRing<Residue<C>> pfac, 
                   GenPolynomial<GenPolynomial<C>> A) {
        GenPolynomial<Residue<C>> P = pfac.getZERO();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Residue<C>> rpfac = pfac.coFac;
        ResidueRing<C> fac = (ResidueRing<C>)rpfac;
        Residue<C> p;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            p = new Residue<C>(fac,a);
            P.doPutToMap( e, p );
        }
        return P;
    }


    /**
     * Product slice.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return Slices represenation of L.
     */
    public static <C extends GcdRingElem<C>>
        Map<Ideal<C>,PolynomialList<GenPolynomial<C>>>
        productSlice( PolynomialList<Product<Residue<C>>> L ) {

        Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> map;
        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>)fpr;
        int s = pr.length();
        map = new HashMap<Ideal<C>,PolynomialList<GenPolynomial<C>>>();
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        for ( int i = 0; i < s; i++ ) {
            RingFactory<Residue<C>> r = pr.getFactory( i );
            ResidueRing<C> rr = (ResidueRing<C>) r;
            Ideal<C> id = rr.ideal;
            GenPolynomialRing<C> cof = rr.ring;
            GenPolynomialRing<GenPolynomial<C>> pfc; 
            pfc = new GenPolynomialRing<GenPolynomial<C>>(cof,L.ring);
            slist = fromProduct( pfc, plist, i );
            spl = new PolynomialList<GenPolynomial<C>>(pfc,slist);
            PolynomialList<GenPolynomial<C>> d = map.get( id );
            if ( d != null ) {
               throw new RuntimeException("ideal exists twice " + id);
            }
            map.put( id, spl );
        }
        return map;
    }


    /**
     * Product slice at i.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coeffients.
     * @param i index of slice.
     * @return Slice of of L at i.
     */
    public static <C extends GcdRingElem<C>>
        PolynomialList<GenPolynomial<C>>
        productSlice( PolynomialList<Product<Residue<C>>> L, int i ) {

        RingFactory<Product<Residue<C>>> fpr = L.ring.coFac;
        ProductRing<Residue<C>> pr = (ProductRing<Residue<C>>)fpr;
        int s = pr.length();
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        RingFactory<Residue<C>> r = pr.getFactory( i );
        ResidueRing<C> rr = (ResidueRing<C>) r;
        Ideal<C> id = rr.ideal;
        GenPolynomialRing<C> cof = rr.ring;
        GenPolynomialRing<GenPolynomial<C>> pfc; 
        pfc = new GenPolynomialRing<GenPolynomial<C>>(cof,L.ring);
        slist = fromProduct( pfc, plist, i );
        spl = new PolynomialList<GenPolynomial<C>>(pfc,slist);
        return spl;
    }


    /*
     * Product slice at i.
     * @param <C> coefficient type.
     * @param L list of polynomials with  to be represented.
     * @param i index of slice.
     * @return Slice of of L at i.
    @SuppressWarnings("unchecked") 
    public static <C extends RegularRingElem<C>>
        PolynomialList<C>
        productSliceRaw( PolynomialList<C> L, int i ) {
        return (PolynomialList)productSlice( (PolynomialList)L, i);
    }
     */


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param L list of polynomials to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<GenPolynomial<C>>>
        fromProduct( GenPolynomialRing<GenPolynomial<C>> pfac,
                     List<GenPolynomial<Product<Residue<C>>>> L,
                     int i ) {

        List<GenPolynomial<GenPolynomial<C>>> 
            list = new ArrayList<GenPolynomial<GenPolynomial<C>>>();

        if ( L == null || L.size() == 0 ) {
           return list;
        }
        GenPolynomial<GenPolynomial<C>> b;
        for ( GenPolynomial<Product<Residue<C>>> a : L ) {
            b = fromProduct( pfac, a, i );
            if ( b != null && !b.isZERO() ) {
               list.add( b.abs() );
            }
        }
        return list;
    }


    /**
     * From product representation.
     * @param <C> coefficient type.
     * @param pfac polynomial ring factory.
     * @param P polynomial to be converted from product representation.
     * @param i index of product representation to be taken.
     * @return Represenation of i-slice of P in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<GenPolynomial<C>>
        fromProduct( GenPolynomialRing<GenPolynomial<C>> pfac,
                     GenPolynomial<Product<Residue<C>>> P,
                     int i ) {

        GenPolynomial<GenPolynomial<C>> b = pfac.getZERO();
        if ( P == null || P.isZERO() ) {
           return b;
        }
        RingFactory<GenPolynomial<C>> cf = pfac.coFac;
        GenPolynomialRing<C> fac = (GenPolynomialRing<C>)cf;

        for ( Map.Entry<ExpVector,Product<Residue<C>>> y: P.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Product<Residue<C>> a = y.getValue();
            Residue<C> r = a.get(i);
            if ( r != null && !r.isZERO() ) {
               GenPolynomial<C> p = r.val;
               b = b.sum( p , e );
            }
        }
        return b;
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with  to be represented.
     * @return Product represenation of L in the polynomial ring pfac.
     */
    public static <C extends GcdRingElem<C>>
        String
        productSliceToString( Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> L ) {
        Set<GenPolynomial<GenPolynomial<C>>> sl 
           = new TreeSet<GenPolynomial<GenPolynomial<C>>>();
        PolynomialList<GenPolynomial<C>> pl = null;

        StringBuffer sb = new StringBuffer("\nproductSlice ------------------------- begin");
        for ( Ideal<C> id: L.keySet() ) {
            sb.append("\n\ncondition == 0:\n");
            sb.append( id.list.toString() );
            pl = L.get( id );
            sl.addAll( pl.list );
            sb.append("\ncorresponding ideal:\n");
            sb.append( pl.toString() );
        }
        sb.append("\nproductSlice ------------------------- end\n");

        List<GenPolynomial<GenPolynomial<C>>> sll 
           = new ArrayList<GenPolynomial<GenPolynomial<C>>>( sl );
        pl = new PolynomialList<GenPolynomial<C>>(pl.ring,sll);
        sb.append("\nunion = " + pl.toString());

        return sb.toString();
    }


    /**
     * Product slices union.
     * @param <C> coefficient type.
     * @param M map of ideals to polynomial lists.
     * @return union of all coefficient product slices.
     */
    public static <C extends GcdRingElem<C>>
        PolynomialList<GenPolynomial<C>>
        productSlicesUnion( Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> M ) {
        Set<GenPolynomial<GenPolynomial<C>>> sl 
           = new TreeSet<GenPolynomial<GenPolynomial<C>>>();
        PolynomialList<GenPolynomial<C>> pl = null;
        for ( Ideal<C> id: M.keySet() ) {
            pl = M.get( id );
            sl.addAll( pl.list );
        }
        List<GenPolynomial<GenPolynomial<C>>> sll 
           = new ArrayList<GenPolynomial<GenPolynomial<C>>>( sl );
        pl = new PolynomialList<GenPolynomial<C>>( pl.ring, sll );
        return pl;
    }


    /**
     * Product slices union.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return union of all coefficient product slices.
     */
    public static <C extends GcdRingElem<C>>
        PolynomialList<GenPolynomial<C>>
        productSlicesUnion( PolynomialList<Product<Residue<C>>> L ) {
        return productSlicesUnion( productSlice( L ) );
    }


    /**
     * Product cover decomposition.
     * @param <C> coefficient type.
     * @param L list of polynomials to be covered.
     * @return cover decomposition of L.
     */
    public static <C extends RegularRingElem<C>>
        PolynomialList<C>
        productDecomposition( PolynomialList<C> L ) {
        if ( L == null ) {
           return L;
        }
        List<GenPolynomial<C>> Dp = new ArrayList<GenPolynomial<C>>( L.list );
        PolynomialList<C> D = new PolynomialList<C>( L.ring, Dp );
        int s = L.list.size();
        System.out.println("productDecomposition s  = " + s);
        GenPolynomial<C> p;
        for ( int i = 0; i < s; i++ ) {
            p = D.list.remove(0);
            D = PolyUtilApp.<C>productDecomposition( D, p );
        }
        System.out.println("productDecomposition s* = " + D.list.size());
        return D;
    }


    /**
     * Product cover decomposition.
     * @param <C> coefficient type.
     * @param P polynomial to be covered.
     * @param L list of already covered polynomials.
     * @return cover decomposition of P with respect to L.
     */
    @SuppressWarnings("unchecked") 
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
        System.out.println("productDecomposition pr = " + pr);
        GenPolynomialRing<Product<Residue<BigRational>>> pring = Lp.ring;
        List<GenPolynomial<Product<Residue<BigRational>>>> Npp 
            = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>( Lp.list );
        Npp.add( Pp );
        boolean changed = false;
        GroebnerBase<BigRational> bb = new GroebnerBaseSeq<BigRational>(); 
        int cfi = pr.length(); // fix since Pp not updated
        //System.out.println("cfi = " + cfi);
        for ( int j = 0; j < cfi; j++ ) {
            int cfj = cfi; //pr.length(); 
            for ( Product<Residue<BigRational>> a : Pp.getMap().values() ) {
                // a != 0
                Residue<BigRational> ar = a.get(j);
                if ( ar == null || ar.isZERO() ) {
                    continue; // "green coefficients"
                }
                if ( ar.isConstant() ) {
                    break; // "red coefficients"
                } 
                System.out.println("decomp ar_"+j+" = " + ar);
                // now is white
                GenPolynomial<BigRational> A = ar.val;
                boolean isZero = false;
                // search slice where A is non zero, i.e. "red/white coefficients"
                for ( int i = 0; i < cfj /*pr.length()*/; i++ ) {
                    RingFactory<Residue<BigRational>> rrr = pr.getFactory(i); 
                    ResidueRing<BigRational> rr = (ResidueRing<BigRational>)rrr; 
                    Ideal<BigRational> id = rr.ideal;
                    if ( id.contains(A) ) {
                       System.out.println("isZero @ rr_" + i + " = " + rr.ideal.list.list);
                       // green in this slice
                       continue;
                    }
                    System.out.println("rrr_" + i + " = " + rrr);
                    List<GenPolynomial<BigRational>> fl = id.list.list;
                    //System.out.println("id = " + id);
                    List<GenPolynomial<BigRational>> nl 
                        = new ArrayList<GenPolynomial<BigRational>>( fl );
                    A = rr.engine.squarefreePart( A );
                    nl.add(A);
                    nl = bb.GB(nl); //wrong: done in ResidueRing constructor, not yet required
                    Ideal<BigRational> I = new Ideal<BigRational>(rfac, nl, true);
                    I = I.squarefree();
                    //System.out.println("ideal = " + I);
                    ResidueRing<BigRational> rrn = new ResidueRing<BigRational>( I );
                    //System.out.println("pr     = " + pr);
                    if ( factoryInsert( pr, rrn ) ) {
                       // pr is updated, Pp is not updated
                       if ( pr.length() >= 0 ) {
                          Npp = PolyUtilApp.<Residue<BigRational>>productCoefficientExtension(Npp,j,pr.length()-1); 
                          changed = true;
                       }
                    }
                }
            }
        }
        PolynomialList<C> M = null;
        if ( changed ) {
           M = (PolynomialList)new PolynomialList<Product<Residue<BigRational>>>(pring,Npp);
        } else {
           List<GenPolynomial<C>> ll = new ArrayList<GenPolynomial<C>>( L.list );
           ll.add( P );
           M = new PolynomialList<C>(L.ring,ll);
        }
        return M;
    }


    /**
     * Product factory insert if required.
     * @param <C> coefficient type.
     * @param P product ring over Residue&lt;BigRational&gt;.
     * @param r ResidueRing&lt;BigRational&gt; factory.
     * @return true, if r has been inserted into P, else false.
     */
    public static <C extends GcdRingElem<C>> 
        boolean
        factoryInsert( ProductRing<Residue<C>> P,
                       ResidueRing<C> r ) {
        if ( factoryContainsEquals( P, r ) ) { 
           System.out.println("existing ResidueRing = " + r);
           return false;
        }
        ResidueRing<C> rc = factoryContainsContains(P,r);
        if ( rc == null ) {
           System.out.println("new ResidueRing = " + r);
           P.addFactory( r );
           return true;
        }
        System.out.println("to check ResidueRing   = " + r);
        System.out.println("containing ResidueRing = " + rc);
        if ( true ) {
           System.out.println("skiped -------------------- ");
           return false;
        }
        Ideal<C> irc = rc.ideal;
        Ideal<C> ir  = r.ideal;
        Ideal<C> iq  = ir.quotient(irc);
        System.out.println("quotient ideal = " + iq);
        Ideal<C> iqr  = iq.squarefree();
        if ( irc.contains( iqr ) ) {
           System.out.println("quotient radical contained in Ideal = " + iqr);
           return false;
        }
        ResidueRing<C> rrq = new ResidueRing<C>( iqr );
        if ( factoryContainsEquals( P, rrq ) ) { 
           System.out.println("existing quotient ResidueRing = " + rrq);
           return false;
        }
        System.out.println("new ResidueRing quotient radical = " + rrq);
        P.addFactory( rrq );
        return true;
    }


    /**
     * Product factory contains.
     * @param <C> coefficient type.
     * @param P product ring over Residue&lt;C&gt;.
     * @param r ResidueRing&lt;C&gt; factory.
     * @return true, if r is contained in an residue ring of P, else false.
     */
    public static <C extends GcdRingElem<C>>
        boolean
        factoryContainsEquals( ProductRing<Residue<C>> P,
                               ResidueRing<C> r ) {
        if ( r == null ) {
           return true;
        }
        if ( P == null || P.length() == 0 ) {
           return false;
        }
        Ideal<C> rid = r.ideal;
        if ( rid == null || rid.isZERO() ) {
           return true;
        }
        for ( int i = 0; i < P.length(); i++ ) {
            RingFactory<Residue<C>> rrr = P.getFactory(i); 
            ResidueRing<C> rr = (ResidueRing<C>)rrr; 
            Ideal<C> id = rr.ideal;
            if ( id.equals( rid ) ) {
               return true;
            }
        }
        return false;
    }


    /**
     * Product factory contains.
     * @param <C> coefficient type.
     * @param P product ring over Residue&lt;C&gt;.
     * @param r ResidueRing&lt;C&gt; factory.
     * @return true, if r is contained in an residue ring of P, else false.
     */
    public static <C extends GcdRingElem<C>>
        ResidueRing<C> 
        factoryContainsContains( ProductRing<Residue<C>> P,
                                 ResidueRing<C> r ) {
        if ( r == null ) {
           return r;
        }
        if ( P == null || P.length() == 0 ) {
           return null;
        }
        Ideal<C> rid = r.ideal;
        for ( int i = 0; i < P.length(); i++ ) {
            RingFactory<Residue<C>> rrr = P.getFactory(i); 
            ResidueRing<C> rr = (ResidueRing<C>)rrr; 
            Ideal<C> id = rr.ideal;
            if ( id.contains( rid ) ) {
               return rr;
            }
        }
        return null;
    }


    /**
     * Product factory is contained.
     * @param <C> coefficient type.
     * @param P product ring over Residue&lt;C&gt;.
     * @param r ResidueRing&lt;C&gt; factory.
     * @return true, if an residue ring of P is contained in r, else false.
     */
    public static <C extends GcdRingElem<C>>
        boolean
        factoryIsContained( ProductRing<Residue<C>> P,
                            ResidueRing<C> r ) {
        /*
        if ( r == null ) {
           return false;
        }
        if ( P == null || P.length() == 0 ) {
           return true;
        }
        */
        Ideal<C> rid = r.ideal;
        /*
        if ( rid == null || rid.isZERO() ) {
           return false;
        }
        */
        for ( int i = 0; i < P.length(); i++ ) {
            RingFactory<Residue<C>> rrr = P.getFactory(i); 
            ResidueRing<C> rr = (ResidueRing<C>)rrr; 
            Ideal<C> id = rr.ideal;
            if ( id.isZERO() ) { // is contained
               continue; 
            }
            if ( rid.contains( id ) ) {
               return true;
            }
        }
        return false;
    }


    /**
     * Polynomial product coefficient extension.
     * @param <C> coefficient type.
     * @param A polynomial to be extended.
     * @param i from index.
     * @param j to index.
     * @return Product coefficient extension P.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<Product<C>>
        productCoefficientExtension( GenPolynomial<Product<C>> A, 
                                     int i, int j) {
        if ( A == null || A.isZERO() ) {
           return A;
        }
        GenPolynomial<Product<C>> B = A.ring.getZERO();
        for ( Map.Entry<ExpVector,Product<C>> m: A.getMap().entrySet() ) {
            ExpVector e = m.getKey();
            Product<C> c = m.getValue();
            Product<C> d = c.extend(i,j);
            B = B.sum( d, e );
        }
        return B;
    }


    /**
     * List of polynomials product coefficient extension.
     * @param <C> coefficient type.
     * @param L list of polynomials to be extended.
     * @param i from index.
     * @param j to index.
     * @return Product coefficient extension of the elements of L.
     */
    public static <C extends RingElem<C>>
        List<GenPolynomial<Product<C>>>
        productCoefficientExtension( List<GenPolynomial<Product<C>>> L, 
                                     int i, int j) {
        if ( L == null || L.size() == 0 ) {
           return L;
        }
        List<GenPolynomial<Product<C>>> M 
            = new ArrayList<GenPolynomial<Product<C>>>( L.size() );
        for ( GenPolynomial<Product<C>> a : L ) {
            GenPolynomial<Product<C>> b 
                = PolyUtilApp.<C>productCoefficientExtension(a,i,j);
            if ( ! b.isZERO() ) {
               M.add( b );
            }
        }
        return M;
    }

}
