
/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;
import java.util.Collection;
import java.util.SortedMap;


import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import static edu.jas.poly.PolyUtil.distribute;
import static edu.jas.poly.PolyUtil.recursive;


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisor<C extends GcdRingElem<C> > {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisor.class);


    /**
     * GenPolynomial pseudo divide.
     * For univariate polynomials or exact division.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return quotient with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #divide(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> basePseudoDivide( GenPolynomial<C> P, 
                                              GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P.isZERO() || S.isONE() ) {
            return P;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> q = S.ring.getZERO().clone();
        GenPolynomial<C> r = P;

        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            //System.out.println("f = " + f);
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                //System.out.println("a = " + a + ", f = " + f);
                C x = a.remainder(c);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   //System.out.println("y = " + y);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial pseudo remainder.
     * For univariate polynomials.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> basePseudoRemainder( GenPolynomial<C> P, 
                                                 GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                C x = a.remainder(c);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   //System.out.println("y = " + y);
                   h = S.multiply( y, f ); // coeff a
                } else {
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo remainder.
     * For univariate polynomials.
     * @param P GenPolynomial.
     * @param s nonzero coefficient.
     * @return coefficient wise remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> baseRemainderPoly( GenPolynomial<C> P, 
                                               C s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        GenPolynomial<C> h = P.ring.getZERO().clone();
        Map<ExpVector,C> hm = h.getMap();
        GenPolynomial<C> r = P; 
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey(); //r.leadingExpVector();
            C a = m.getValue(); //r.leadingBaseCoefficient();
            C x = a.remainder(s);
            hm.put(f,x);
        }
        return h;
    }


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
        return d; 
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
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> baseGcd( GenPolynomial<C> P,
                                     GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar > 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " no univariate polynomial");
           //logger.info("pseudoGcd only for univaraite polynomials");
           // guess
           //return P.ring.getONE();
        }
        long e = P.leadingExpVector().getVal(0);
        long f = S.leadingExpVector().getVal(0);
        GenPolynomial<C> q;
        GenPolynomial<C> r;
        if ( f > e ) {
           r = P;
           q = S;
           long g = f;
           f = e;
           e = g;
        } else {
           q = P;
           r = S;
        }
        r = r.abs();
        q = q.abs();
        C a = baseContent( r );
        C b = baseContent( q );
        C c = a.gcd(b);
        r = r.divide(a);
        q = q.divide(b);
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        GenPolynomial<C> x;
        while ( !r.isZERO() ) {
            x = basePseudoRemainder(q,r);
            //System.out.println("x  = " + x);
            q = r;
            r = basePrimitivePart( x );
        }
        //System.out.println("q  = " + q);
        return q.multiply(c).abs(); 
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * Division by coefficient ring element.
     * @param P recursive GenPolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursiveDivide( GenPolynomial<GenPolynomial<C>> P, 
                            GenPolynomial<C> s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( s.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> pv = p.getMap();
        //System.out.println("P  = " + P);
        //System.out.println("s  = " + s);
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m1 : P.getMap().entrySet() ) {
            GenPolynomial<C> c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            GenPolynomial<C> c = basePseudoDivide(c1,s);
            if ( ! basePseudoRemainder(c1,s).isZERO() ) {
                System.out.println("c1 = " + c1);
                System.out.println("s  = " + s);
                System.out.println("c  = " + c);
                System.out.println("r  = " + basePseudoRemainder(c1,s));
                throw new RuntimeException(this.getClass().getName()
                                           + " remainder non zero");
            }
            pv.put( e1, c ); // or m1.setValue( c )
        }
        //System.out.println("p  = " + p);
        return p;
    }


    /**
     * GenPolynomial pseudo remainder.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursivePseudoRemainder( GenPolynomial<GenPolynomial<C>> P, 
                                     GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                r = r.multiply( c );
                h = S.multiply( a, f );
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
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
        GenPolynomial<GenPolynomial<C>> pp = recursiveDivide(P,d); 
        return pp;
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> 
        recursiveGcd( GenPolynomial<GenPolynomial<C>> P,
                      GenPolynomial<GenPolynomial<C>> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar > 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " no univariate polynomial");
        }
        long e = P.leadingExpVector().getVal(0);
        long f = S.leadingExpVector().getVal(0);
        GenPolynomial<GenPolynomial<C>> q;
        GenPolynomial<GenPolynomial<C>> r;
        if ( f > e ) {
           r = P;
           q = S;
           long g = f;
           f = e;
           e = g;
        } else {
           q = P;
           r = S;
        }
        r = r.abs();
        q = q.abs();
        GenPolynomial<C> a = recursiveContent(r);
        GenPolynomial<C> b = recursiveContent(q);
        //System.out.println("rgcd a = " + a);
        //System.out.println("rgcd b = " + b);

        GenPolynomial<C> c = gcd(a,b); // go to recursion
        //System.out.println("rgcd c = " + c);
        r = recursiveDivide(r,a);
        q = recursiveDivide(q,b);
        if ( r.isONE() ) {
           return r.multiply(c);
        }
        if ( q.isONE() ) {
           return q.multiply(c);
        }
        //System.out.println("rgcd q = " + q);
        //System.out.println("rgcd r = " + r);

        GenPolynomial<GenPolynomial<C>> x;
        while ( !r.isZERO() ) {
            x = recursivePseudoRemainder(q,r);
            //System.out.println("rpg x = " + x);
            q = r;
            r = recursivePrimitivePart( x );
            //System.out.println("rpg r = " + r);
        }
        //System.out.println("rgcd q = " + q);
        return q.multiply(c).abs();
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


    /**
     * GenPolynomial greatest comon divisor.
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
            //System.out.println("nvar = 1 ");
            return baseGcd(P,S);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(cfac,1);

        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> Sr = recursive(rfac,S);

        GenPolynomial<GenPolynomial<C>> Dr = recursiveGcd( Pr, Sr );

        GenPolynomial<C> D = distribute( pfac, Dr );
        return D;
    }

}
