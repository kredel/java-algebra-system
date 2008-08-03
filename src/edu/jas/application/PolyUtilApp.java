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

        GenPolynomial<Product<C>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<C>> rpfac = pfac.coFac;
        ProductRing<C> rfac = (ProductRing<C>) rpfac;
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            Product<C> p = toProductGen(rfac,a);
            if ( p != null && !p.isZERO() ) {
               P.doPutToMap( e, p );
            }        
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

        GenPolynomial<Product<ModInteger>> P = pfac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return P;
        }
        RingFactory<Product<ModInteger>> rpfac = pfac.coFac;
        ProductRing<ModInteger> fac = (ProductRing<ModInteger>)rpfac;
        for ( Map.Entry<ExpVector,BigInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigInteger a = y.getValue();
            Product<ModInteger> p = toProduct(fac,a);
            if ( p != null && !p.isZERO() ) {
               P.doPutToMap( e, p );
            }        
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

        GenPolynomial<Product<Residue<C>>> P = pfac.getZERO().clone();
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
            if ( p != null && !p.isZERO() ) {
               P.doPutToMap( e, p );
            }        
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
     * Product residue representation.
     * @param <C> coefficient type.
     * @param CS list of ColoredSystems from comprehensive GB system.
     * @return Product residue represenation of CS.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Product<Residue<C>>>> 
        toProductRes(List<ColoredSystem<C>> CS) {

        List<GenPolynomial<Product<Residue<C>>>> 
            list = new ArrayList<GenPolynomial<Product<Residue<C>>>>();
        if ( CS == null || CS.size() == 0 ) {
           return list;
        }
        GenPolynomialRing<GenPolynomial<C>> pr = null; 
        List<RingFactory<Residue<C>>> rrl 
            = new ArrayList<RingFactory<Residue<C>>>( CS.size() ); 
        for (ColoredSystem<C> cs : CS) {
            Ideal<C> id = cs.condition.zero;
            ResidueRing<C> r = new ResidueRing<C>(id);
            if ( ! rrl.contains(r) ) {
                rrl.add(r);
            }
            if ( pr == null ) {
                if ( cs.list.size() > 0 ) {
                    pr = cs.list.get(0).green.ring;
                }
            }
        }
        ProductRing<Residue<C>> pfac;
        pfac = new ProductRing<Residue<C>>(rrl);
        System.out.println("pfac = " + pfac);

        GenPolynomialRing<Product<Residue<C>>> rf 
            = new GenPolynomialRing<Product<Residue<C>>>(pfac,pr.nvar,pr.tord,pr.getVars());
        List<GenPolynomial<GenPolynomial<C>>> F 
            = ComprehensiveGroebnerBaseSeq.<C> combineGBsys(CS);

        list = PolyUtilApp.<C> toProductRes(rf, F);
        return list;
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
            if ( b != null && !b.isZERO() ) {
               list.add( b );
            }
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
        GenPolynomial<Residue<C>> P = pfac.getZERO().clone();
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
            if ( p != null && !p.isZERO() ) {
               P.doPutToMap( e, p );
            }
        }
        return P;
    }

}
