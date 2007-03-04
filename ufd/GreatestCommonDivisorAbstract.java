
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


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public abstract class GreatestCommonDivisorAbstract<C extends GcdRingElem<C> > 
                      implements GreatestCommonDivisor<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorAbstract.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * GenPolynomial pseudo divide.
     * For univariate polynomials or exact division.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return quotient with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
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
        GenPolynomial<C> r = P;
        GenPolynomial<C> q = S.ring.getZERO().clone();

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
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
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
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
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
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<C> baseGcd( GenPolynomial<C> P,
                                              GenPolynomial<C> S );


    /**
     * GenPolynomial polynomial derivative main variable.
     * @param P recursive GenPolynomial.
     * @return deriviative(P).
     */
    public GenPolynomial<C> 
           baseDeriviative( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        GenPolynomial<C> d = pfac.getZERO().clone();
        Map<ExpVector,C> dm = d.getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               C a = m.getValue(); 
               C x = a.multiply(cf);
               ExpVector e = new ExpVector( 1, 0, fl-1L );  
               dm.put(e,x);
            }
        }
        return d; 
    }


    /**
     * GenPolynomial polynomial greatest squarefee divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
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
        GenPolynomial<C> d = baseDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<C> g = baseGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<C> q = basePseudoDivide(pp,g);
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

        GenPolynomial<C> d = baseDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<C> g = baseGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<C> q = basePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        GenPolynomial<C> y = basePseudoDivide(d,g);
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
              GenPolynomial<C> z = basePseudoDivide(q,c);
              if ( z.leadingExpVector().getVal(0) > 0 /*!z.isONE()*/ ) {
                 sfactors.put(j,z);
              }
              j++;
              q = c;
              g = basePseudoDivide(g,c);
              if ( z.isConstant() && j > 10 ) { // debug
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
            if ( debug || ! basePseudoRemainder(c1,s).isZERO() ) { // &&
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
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
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
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return quotient with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursivePseudoDivide( GenPolynomial<GenPolynomial<C>> P, 
                                  GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P;
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        GenPolynomial<GenPolynomial<C>> q = S.ring.getZERO().clone();
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                GenPolynomial<C> x = basePseudoRemainder(a,c);
                //System.out.println("x = " + x);
                if ( x.isZERO() ) {
                   GenPolynomial<C> y = basePseudoDivide(a,c);
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
    public abstract GenPolynomial<GenPolynomial<C>> 
           recursiveGcd( GenPolynomial<GenPolynomial<C>> P,
                         GenPolynomial<GenPolynomial<C>> S );


    /**
     * GenPolynomial recursive polynomial derivative main variable.
     * @param P recursive GenPolynomial.
     * @return deriviative(P).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursiveDeriviative( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(this.getClass().getName()
                     + " only for univariate polynomials");
        }
        GenPolynomialRing<C> pr = (GenPolynomialRing<C>)pfac.coFac;
        RingFactory<C> rf = pr.coFac;
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        Map<ExpVector,GenPolynomial<C>> dm = d.getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               GenPolynomial<C> a = m.getValue(); 
               GenPolynomial<C> x = a.multiply(cf);
               ExpVector e = new ExpVector( 1, 0, fl-1L );  
               dm.put(e,x);
            }
        }
        return d; 
    }


    /**
     * GenPolynomial recursive polynomial greated squarefee divisor.
     * @param P recursive GenPolynomial.
     * @return squarefree(P).
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
        GenPolynomial<GenPolynomial<C>> d = recursiveDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<GenPolynomial<C>> g = recursiveGcd(pp,d);
        //System.out.println("g  = " + g);

        GenPolynomial<GenPolynomial<C>> q = recursivePseudoDivide(pp,g);
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

        GenPolynomial<GenPolynomial<C>> d = recursiveDeriviative(pp);
        //System.out.println("d  = " + d);
        GenPolynomial<GenPolynomial<C>> g = recursiveGcd(pp,d);
        //System.out.println("g  = " + g);
        GenPolynomial<GenPolynomial<C>> q = recursivePseudoDivide(pp,g);
        //System.out.println("q  = " + q);
        GenPolynomial<GenPolynomial<C>> y = recursivePseudoDivide(d,g);
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
              GenPolynomial<GenPolynomial<C>> z = recursivePseudoDivide(q,c);
              if ( z.leadingExpVector().getVal(0) > 0 /*!z.isONE()*/ ) {
                 sfactors.put(j,z);
              }
              //if ( g.leadingExpVector().getVal(0) >= 1 ) {
              //   q = g;
              //   break;
              //}
              j++;
              q = c;
              g = recursivePseudoDivide(g,c);
              if ( z.isConstant() && j > 10 ) { // debug
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


    /**
     * GenPolynomial least comon multiple.
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
        GenPolynomial<C> A = P.multiply(S);
        GenPolynomial<C> C = gcd(P,S);
        return basePseudoDivide(A,C);
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * Entry driver method.
     * @param P GenPolynomial.
     * @return squarefree(P).
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

}
