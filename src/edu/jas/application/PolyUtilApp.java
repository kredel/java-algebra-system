/*
 * $Id$
 */

package edu.jas.application;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


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


}
