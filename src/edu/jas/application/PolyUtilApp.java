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
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigDecimal;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseSeq;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.PolyUtil;

import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.ComplexRootsAbstract;
import edu.jas.root.RealRootsSturm;
import edu.jas.root.RealRootAbstract;

import edu.jas.util.ListUtil;


/**
 * Polynomial utilities for applications,  
 * for example conversion ExpVector to Product or zero dimensional ideal root computation.
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
        //System.out.println("pfac = " + pfac);
        GenPolynomialRing<Product<Residue<C>>> rf 
            = new GenPolynomialRing<Product<Residue<C>>>(pfac,pr.nvar,pr.tord,pr.getVars());
        GroebnerSystem<C> gs = new GroebnerSystem<C>( CS ); 
        List<GenPolynomial<GenPolynomial<C>>> F = gs.getCGB();
        list = PolyUtilApp.<C>toProductRes(rf, F);
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
        map = new TreeMap<Ideal<C>,PolynomialList<GenPolynomial<C>>>();
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
        List<GenPolynomial<GenPolynomial<C>>> slist;

        List<GenPolynomial<Product<Residue<C>>>> plist = L.list;
        PolynomialList<GenPolynomial<C>> spl;

        RingFactory<Residue<C>> r = pr.getFactory( i );
        ResidueRing<C> rr = (ResidueRing<C>) r;
        GenPolynomialRing<C> cof = rr.ring;
        GenPolynomialRing<GenPolynomial<C>> pfc; 
        pfc = new GenPolynomialRing<GenPolynomial<C>>(cof,L.ring);
        slist = fromProduct( pfc, plist, i );
        spl = new PolynomialList<GenPolynomial<C>>(pfc,slist);
        return spl;
    }


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
               b = b.abs();
               if ( ! list.contains( b ) ) {
                  list.add( b );
               }
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

        GenPolynomial<GenPolynomial<C>> b = pfac.getZERO().clone();
        if ( P == null || P.isZERO() ) {
           return b;
        }

        for ( Map.Entry<ExpVector,Product<Residue<C>>> y: P.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Product<Residue<C>> a = y.getValue();
            Residue<C> r = a.get(i);
            if ( r != null && !r.isZERO() ) {
               GenPolynomial<C> p = r.val;
               if ( p != null && !p.isZERO() ) {
                  b.doPutToMap( e, p );
               }        
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
        StringBuffer sb = new StringBuffer(); //"\nproductSlice ----------------- begin");
        for ( Ideal<C> id: L.keySet() ) {
            sb.append("\n\ncondition == 0:\n");
            sb.append( id.list.toScript() );
            pl = L.get( id );
            sl.addAll( pl.list );
            sb.append("\ncorresponding ideal:\n");
            sb.append( pl.toScript() );
        }
        //List<GenPolynomial<GenPolynomial<C>>> sll 
        //   = new ArrayList<GenPolynomial<GenPolynomial<C>>>( sl );
        //pl = new PolynomialList<GenPolynomial<C>>(pl.ring,sll);
        // sb.append("\nunion = " + pl.toString());
        //sb.append("\nproductSlice ------------------------- end\n");
        return sb.toString();
    }


    /**
     * Product slice to String.
     * @param <C> coefficient type.
     * @param L list of polynomials with product coefficients.
     * @return string represenation of slices of L.
     */
    public static <C extends GcdRingElem<C>>
        String productToString( PolynomialList<Product<Residue<C>>> L ) {
        Map<Ideal<C>,PolynomialList<GenPolynomial<C>>> M;
        M = productSlice( L ); 
        String s = productSliceToString( M );
        return s;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRoots(Ideal<D> I, C eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if ( logger.isInfoEnabled() ) {
            logger.info("univs = " + univs);
        }
	return complexRoots(I,univs,eps);
    }

    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRoots(Ideal<D> I, List<GenPolynomial<D>> univs, C eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
        RingFactory<C> cf = (RingFactory<C>) I.list.ring.coFac;
        ComplexRing<C> cr = new ComplexRing<C>(cf);
        ComplexRootsAbstract<C> cra = new ComplexRootsSturm<C>(cr);
        List<GenPolynomial<Complex<C>>> cunivs = new ArrayList<GenPolynomial<Complex<C>>>();
        for ( GenPolynomial<D> p : univs ) {
            GenPolynomialRing<Complex<C>> pfac = new GenPolynomialRing<Complex<C>>(cr,p.ring);
            //System.out.println("pfac = " + pfac.toScript());
            GenPolynomial<Complex<C>> cp = PolyUtil.<C> toComplex(pfac,(GenPolynomial<C>) p);
            cunivs.add(cp);
            //System.out.println("cp = " + cp);
        }
        for ( int i = 0; i < I.list.ring.nvar; i++ ) {
            List<Complex<BigDecimal>> cri = cra.approximateRoots(cunivs.get(i),eps);
            //System.out.println("cri = " + cri);
            croots.add(cri);
        }
        croots = ListUtil.<Complex<BigDecimal>> tupleFromList( croots );
        return croots;
    }


    /**
     * Construct superset of complex roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of complex roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<Complex<BigDecimal>>> complexRoots(List<IdealWithUniv<D>> Il, C eps) {
        List<List<Complex<BigDecimal>>> croots = new ArrayList<List<Complex<BigDecimal>>>();
	for ( IdealWithUniv<D> I : Il ) {
	    List<List<Complex<BigDecimal>>> cr  = complexRoots(I.ideal,I.upolys,eps); 
	    croots.addAll(cr);
	}
	return croots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRoots(Ideal<D> I, C eps) {
        List<GenPolynomial<D>> univs = I.constructUnivariate();
        if ( logger.isInfoEnabled() ) {
            logger.info("univs = " + univs);
        }
	return realRoots(I,univs,eps);
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param I zero dimensional ideal.
     * @param univs list of univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(G)
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRoots(Ideal<D> I, List<GenPolynomial<D>>univs, C eps) {
        List<List<BigDecimal>> roots = new ArrayList<List<BigDecimal>>();
        RingFactory<C> cf = (RingFactory<C>) I.list.ring.coFac;
        RealRootAbstract<C> rra = new RealRootsSturm<C>();
        for ( int i = 0; i < I.list.ring.nvar; i++ ) {
            List<BigDecimal> rri = rra.approximateRoots((GenPolynomial<C>)univs.get(i),eps);
            //System.out.println("rri = " + rri);
            roots.add(rri);
        }
        //System.out.println("roots-1 = " + roots);
        roots = ListUtil.<BigDecimal> tupleFromList( roots );
        //System.out.println("roots-2 = " + roots);
        return roots;
    }


    /**
     * Construct superset of real roots for zero dimensional ideal(G).
     * @param Il list of zero dimensional ideals with univariate polynomials.
     * @param eps desired precision.
     * @return list of coordinates of real roots for ideal(cap_i(G_i))
     */
    public static <C extends RingElem<C> & Rational, D extends GcdRingElem<D>> 
      List<List<BigDecimal>> realRoots(List<IdealWithUniv<D>> Il, C eps) {
        List<List<BigDecimal>> rroots = new ArrayList<List<BigDecimal>>();
	for ( IdealWithUniv<D> I : Il ) {
	    List<List<BigDecimal>> rr  = realRoots(I.ideal,I.upolys,eps); 
	    rroots.addAll(rr);
	}
	return rroots;
    }


}
