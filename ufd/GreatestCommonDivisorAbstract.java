
/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import static edu.jas.poly.PolyUtil.distribute;
import static edu.jas.poly.PolyUtil.recursive;
import edu.jas.poly.PolyUtil;


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public abstract class GreatestCommonDivisorAbstract<C extends GcdRingElem<C> > 
                      implements GreatestCommonDivisor<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorAbstract.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * GenPolynomial base coefficient content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public C baseContent(GenPolynomial<C> P) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P.ring.getZEROCoefficient();
        }
        C d = null;
        for ( C c : P.getMap().values() ) {
            if ( d == null ) {
               d = c;
            } else {
               d = d.gcd(c);
            }
            if ( d.isONE() ) {
               return d; 
            }
        }
        return d.abs(); 
    }


    /**
     * GenPolynomial base coefficient primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        C d = baseContent( P );
        if ( d.isONE() ) {
            return P;
        }
        return P.divide(d);
    }


    /**
     * Univariate GenPolynomial greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<C> baseGcd( GenPolynomial<C> P,
                                              GenPolynomial<C> S );


    /**
     * GenPolynomial polynomial greatest squarefee divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<C> 
           baseSquarefreePart( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        //System.out.println("P  = " + P);
        GenPolynomial<C> pp = basePrimitivePart(P);
        //System.out.println("pp = " + pp);
        if ( pp.leadingExpVector().getVal(0) < 1 ) {
            return pp;
        }
        GenPolynomial<C> d = PolyUtil.<C>baseDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<C> g = baseGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<C> q = PolyUtil.<C>basePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        return q;
    }


    /**
     * GenPolynomial polynomial squarefee factorization.
     * @param P primitive GenPolynomial.
     * @return squarefreeFactors(P).
     */
    public Map<Integer,GenPolynomial<C>> 
           baseSquarefreeFactors( GenPolynomial<C> P ) {
        Map<Integer,GenPolynomial<C>> sfactors 
             = new TreeMap<Integer,GenPolynomial<C>>();
        if ( P == null || P.isZERO() ) {
            return sfactors;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        //System.out.println("P  = " + P);
        GenPolynomial<C> pp = P;
        //pp = basePrimitivePart(pp); // as precondition ?
        //System.out.println("pp = " + pp);
        //if ( pp.leadingExpVector().getVal(0) < 1 ) {
        //    return pp;
        //}

        GenPolynomial<C> d = PolyUtil.<C>baseDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<C> g = baseGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<C> q = PolyUtil.<C>basePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        GenPolynomial<C> y = PolyUtil.<C>basePseudoDivide(d,g);
        //System.out.println("y  = " + y);
        int j = 1;
        if ( g.isConstant() && false ) { // debug
           System.out.println("pp = " + pp);
           System.out.println("d  = " + d);
           System.out.println("g  = " + g);
           System.out.println("q  = " + q);
        }
        while ( g.leadingExpVector().getVal(0) >= 1 /*!g.isONE()*/ ) {
              GenPolynomial<C> c = baseGcd(g,q);
              GenPolynomial<C> z = PolyUtil.<C>basePseudoDivide(q,c);
              if ( z.leadingExpVector().getVal(0) > 0 /*!z.isONE()*/ ) {
                 sfactors.put(j,z);
              }
              j++;
              q = c;
              g = PolyUtil.<C>basePseudoDivide(g,c);
              if ( z.isConstant() && j > 20 ) { // debug
                  System.out.println("j = " + j);
                  System.out.println("c = " + c);
                  System.out.println("z = " + z);
                  System.out.println("g = " + g);
                  System.out.println("q = " + q);
              }
        }
        sfactors.put(j,q);
        return sfactors;
    }


    /**
     * GenPolynomial recursive content.
     * @param P recursive GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> 
           recursiveContent( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P.ring.getZEROCoefficient();
        }
        GenPolynomial<C> d = null;
        for ( GenPolynomial<C> c : P.getMap().values() ) {
            if ( d == null ) {
               d = c;
            } else {
               d = gcd(d,c); // go to recursion
            }
            //System.out.println("d = " + d);
            if ( d.isONE() ) {
               return d; 
            }
        }
        return d.abs(); 
    }


    /**
     * GenPolynomial recursive primitive part.
     * @param P recursive GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<GenPolynomial<C>> 
        recursivePrimitivePart( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomial<C> d = recursiveContent( P );
        if ( d.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> pp = PolyUtil.<C>recursiveDivide(P,d); 
        return pp;
    }


    /**
     * Univariate GenPolynomial recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<GenPolynomial<C>> 
           recursiveGcd( GenPolynomial<GenPolynomial<C>> P,
                         GenPolynomial<GenPolynomial<C>> S );


    /**
     * GenPolynomial recursive polynomial greated squarefee divisor.
     * @param P recursive GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursiveSquarefreePart( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        GenPolynomial<GenPolynomial<C>> pp = recursivePrimitivePart(P);
        //System.out.println("pp  = " + pp);
        if ( pp.leadingExpVector().getVal(0) < 1 ) {
            return pp;
        }
        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C>recursiveDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<GenPolynomial<C>> g = recursiveGcd(pp,d);
        //System.out.println("g  = " + g);

        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C>recursivePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        return q;
    }


    /**
     * GenPolynomial recursive polynomial squarefee factorization.
     * @param P primitive recursive GenPolynomial.
     * @return squarefreeFactors(P).
     */
    public Map<Integer,GenPolynomial<GenPolynomial<C>>> 
           recursiveSquarefreeFactors( GenPolynomial<GenPolynomial<C>> P ) {
        Map<Integer,GenPolynomial<GenPolynomial<C>>> sfactors 
             = new TreeMap<Integer,GenPolynomial<GenPolynomial<C>>>();
        if ( P == null || P.isZERO() ) {
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // recursiveContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        //System.out.println("P  = " + P);
        GenPolynomial<GenPolynomial<C>> pp = P;
        //pp = recursivePrimitivePart(pp); // as precondition ?
        //System.out.println("pp = " + pp);
        //if ( pp.leadingExpVector().getVal(0) < 1 ) {
        //    return pp;
        //}

        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C>recursiveDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<GenPolynomial<C>> g = recursiveGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C>recursivePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        GenPolynomial<GenPolynomial<C>> y = PolyUtil.<C>recursivePseudoDivide(d,g);
        //System.out.println("y  = " + y);
        int j = 1; 
        if ( g.isConstant() && false ) { // debug
           System.out.println("pp = " + pp);
           System.out.println("d  = " + d);
           System.out.println("g  = " + g);
           System.out.println("q  = " + q);
        }
        while ( g.leadingExpVector().getVal(0) >= 1 /*!g.abs().isONE()*/ ) {
              GenPolynomial<GenPolynomial<C>> c = recursiveGcd(g,q);
              GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C>recursivePseudoDivide(q,c);
              if ( z.leadingExpVector().getVal(0) > 0 /*!z.isONE()*/ ) {
                 sfactors.put(j,z);
              }
              //if ( g.leadingExpVector().getVal(0) >= 1 ) {
              //   q = g;
              //   break;
              //}
              j++;
              q = c;
              g = PolyUtil.<C>recursivePseudoDivide(g,c);
              if ( z.isConstant() && j > 20 ) { // debug
                  System.out.println("j = " + j);
                  System.out.println("c = " + c);
                  System.out.println("z = " + z);
                  System.out.println("g = " + g);
                  System.out.println("q = " + q);
              }
        }
        sfactors.put(j,q);
        return sfactors;
    }


    /**
     * GenPolynomial content.
     * Entry driver method.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> 
           content( GenPolynomial<C> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " use baseContent for univariate polynomials");

        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        //System.out.println("P  =" + P);
        //System.out.println("Pr =" + Pr);
        GenPolynomial<C> D = recursiveContent( Pr );
        //System.out.println("D  =" + D);
        return D;
    }


    /**
     * GenPolynomial primitive part.
     * Entry driver method.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> 
        primitivePart( GenPolynomial<C> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= 1 ) {
           return basePrimitivePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> PP = recursivePrimitivePart( Pr );

        GenPolynomial<C> D = distribute( pfac, PP );
        return D;
    }


    /*
     * Coefficient division.
     * Indirection to coefficient method.
     * @param a coefficient.
     * @param b coefficient.
     * @return a/b.
    public C divide( C a, C b ) {
        if ( b == null || b.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");

        }
        if ( a == null || a.isZERO() ) {
            return a;
        }
        return a.divide(b);
    }
     */


    /**
     * GenPolynomial division.
     * Indirection to GenPolynomial method.
     * @param a GenPolynomial.
     * @param b coefficient.
     * @return a/b.
     */
    public GenPolynomial<C> divide( GenPolynomial<C> a, C b ) {
        if ( b == null || b.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");

        }
        if ( a == null || a.isZERO() ) {
            return a;
        }
        return a.divide(b);
    }


    /**
     * Coefficient greatest common divisor.
     * Indirection to coefficient method.
     * @param a coefficient.
     * @param b coefficient.
     * @return gcd(a,b).
     */
    public C gcd( C a, C b ) {
        if ( b == null || b.isZERO() ) {
            return a;
        }
        if ( a == null || a.isZERO() ) {
            return b;
        }
        return a.gcd(b);
    }


    /**
     * GenPolynomial greatest common divisor.
     * Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> 
        gcd( GenPolynomial<C> P,
             GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= 1 ) {
            //System.out.println("P = " + P);
            //System.out.println("S = " + S);
            GenPolynomial<C> T = baseGcd(P,S);
            //System.out.println("T = " + T);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> Sr = recursive(rfac,S);

        //System.out.println("Pr  =" + Pr);
        //System.out.println("Sr  =" + Sr);
        GenPolynomial<GenPolynomial<C>> Dr = recursiveGcd( Pr, Sr );
        //System.out.println("Dr  =" + Dr);
        GenPolynomial<C> D = distribute( pfac, Dr );
        return D;
    }


    /**
     * GenPolynomial least common multiple.
     * Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return lcm(P,S).
     */
    public GenPolynomial<C> 
        lcm( GenPolynomial<C> P,
             GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return S;
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomial<C> C = gcd(P,S);
        GenPolynomial<C> A = P.multiply(S);
        return PolyUtil.<C>basePseudoDivide(A,C);
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * Entry driver method.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<C> 
        squarefreePart( GenPolynomial<C> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= 1 ) {
           return baseSquarefreePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> PP = recursiveSquarefreePart( Pr );

        GenPolynomial<C> D = distribute( pfac, PP );
        return D;
    }


    /**
     * GenPolynomial squarefree factorization.
     * Entry driver method.
     * @param P primitive GenPolynomial.
     * @return squarefreeFactors(P).
     */
    public Map<Integer,GenPolynomial<C>> 
        squarefreeFactors( GenPolynomial<C> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        Map<Integer,GenPolynomial<C>> sfactors = 
            new TreeMap<Integer,GenPolynomial<C>>(); 
        if ( P.isZERO() ) {
           return sfactors;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= 1 ) {
           return baseSquarefreeFactors(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        Map<Integer,GenPolynomial<GenPolynomial<C>>> PP 
           = recursiveSquarefreeFactors( Pr );

        for ( Map.Entry<Integer,GenPolynomial<GenPolynomial<C>>> m : PP.entrySet() ) {
            Integer i = m.getKey();
            GenPolynomial<GenPolynomial<C>> Dr = m.getValue();
            GenPolynomial<C> D = distribute( pfac, Dr );
            sfactors.put(i,D);
        }
        return sfactors;
    }


    /**
     * GenPolynomial resultant.
     * Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     */
    public GenPolynomial<C> 
        resultant( GenPolynomial<C> P,
                   GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return S;
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        // hack
        GreatestCommonDivisorSubres<C> ufd_sr
            = new GreatestCommonDivisorSubres<C>();
        if ( pfac.nvar <= 1 ) {
            //System.out.println("P = " + P);
            //System.out.println("S = " + S);
            GenPolynomial<C> T = ufd_sr.baseResultant(P,S);
            //System.out.println("T = " + T);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> Sr = recursive(rfac,S);

        GenPolynomial<GenPolynomial<C>> Dr = ufd_sr.recursiveResultant( Pr, Sr );
        //System.out.println("Pr  =" + Pr);
        //System.out.println("Sr  =" + Sr);
        //System.out.println("Dr  =" + Dr);
        // GenPolynomial not a GcdRingElem:
        //GreatestCommonDivisorSimple<GenPolynomial<GenPolynomial<C>>> sgcd 
        //  = new GreatestCommonDivisorSimple<GenPolynomial<GenPolynomial<C>>>();
        //GenPolynomial<GenPolynomial<C>> Dr = sgcd.gcd( Pr, Sr );

        GenPolynomial<C> D = distribute( pfac, Dr );
        return D;
    }

}
