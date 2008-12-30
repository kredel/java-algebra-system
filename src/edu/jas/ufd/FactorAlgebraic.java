
/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.structure.Power;
import edu.jas.structure.GcdRingElem;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.Monomial;

import edu.jas.util.PowerSet;
import edu.jas.util.KsubSet;


/**
 * Algebraic number coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorAlgebraic <C extends GcdRingElem<C>> 
       extends FactorAbstract<AlgebraicNumber<C>> {


    private static final Logger logger = Logger.getLogger(FactorAlgebraic.class);
    private boolean debug = true || logger.isInfoEnabled();


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<AlgebraicNumber<C>>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> 
      baseFactorsSquarefree(GenPolynomial<AlgebraicNumber<C>> P) {
        if ( P == null ) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if ( P.isZERO() ) {
            return factors;
        }
        if ( P.isONE() ) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) {
            throw new RuntimeException(this.getClass().getName()
                    + " only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>)pfac.coFac;
        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac,pfac);
        GenPolynomialRing<C> dfac = cfac.extend(1);
        GenPolynomial<GenPolynomial<C>> Pc = rfac.getZERO().clone();
        for ( Monomial<AlgebraicNumber<C>> m : P ) {
            AlgebraicNumber<C> c = m.c;
            GenPolynomial<C> ac = c.val;
            Pc.doPutToMap(m.e,ac);
        }
        System.out.println("Pc = " + Pc);
        GenPolynomial<GenPolynomial<C>> Ac = rfac.getONE(); //.clone();
        Ac = Ac.multiply(agen);
        System.out.println("Ac = " + Ac);

        GreatestCommonDivisorSubres<C> engine 
            = new GreatestCommonDivisorSubres<C>( /*cfac.coFac*/ );
        //         = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( cfac.coFac );

        FactorRational rfengine = new FactorRational();
        GenPolynomial<BigRational> rRes = null;
        while ( true ) {
            GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveResultant(Pc,Ac);
            System.out.println("Rc = " + Rc);
            GenPolynomial<C> Res = Rc.leadingBaseCoefficient();
            Object o = Res;
            rRes = (GenPolynomial<BigRational>)o;
            System.out.println("rRes = " + rRes);

            boolean sq = rfengine.isSquarefree(rRes);
            System.out.println("sq = " + sq);
            if ( sq ) {
                break;
            } else {
                break; // also
            }
        }
        // new rRes is squarefree
        SortedMap<GenPolynomial<BigRational>,Integer> nfacs = rfengine.baseFactors( rRes );
        System.out.println("nfacs = " + nfacs);

        Object o = pfac.getZERO().clone();
        GenPolynomial<AlgebraicNumber<BigRational>> Ni = (GenPolynomial<AlgebraicNumber<BigRational>>)o;

        o = afac;
        AlgebraicNumberRing<BigRational> rafac = (AlgebraicNumberRing<BigRational>)o;

        o = cfac;
        GenPolynomialRing<BigRational> rcfac = (GenPolynomialRing<BigRational>)o;

        for ( GenPolynomial<BigRational> nfi : nfacs.keySet() ) {
             System.out.println("nfi = " + nfi);
             for ( Monomial<BigRational> m : nfi ) {
                 ExpVector e = m.e;
                 BigRational c = m.c;
                 AlgebraicNumber<BigRational> ac = rafac.getONE();
                 //GenPolynomial<BigRational> pc = rcfac.getONE(); 
                 GenPolynomial<BigRational> pc = rcfac.univariate(0); 
                 pc = pc.multiply( c ); 
                 ac = new AlgebraicNumber<BigRational>( rafac, pc );
                 Ni.doPutToMap(e,ac); 
             }
             System.out.println("Ni = " + Ni);
        }


//         System.out.println("ldcf = " + ldcf);
//         BigInteger bi = BigInteger.ONE;
//         GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(bi,pfac);
//         GenPolynomial<BigInteger> Pi =  PolyUtil.integerFromRationalCoefficients(ifac,P);
//         System.out.println("Pi = " + Pi);
//         FactorInteger faci = new FactorInteger();
//         List<GenPolynomial<BigInteger>> ifacts = faci.baseFactorsSquarefree(Pi);
//         if ( ifacts.size() <= 1 ) {
//             factors.add( P.multiply(ldcf) );
//             return factors;
//         }
//         System.out.println("ifacts = " + ifacts);
//         List<GenPolynomial<AlgebraicNumber<C>>> rfacts = PolyUtil.fromIntegerCoefficients(pfac,ifacts);
//         System.out.println("rfacts = " + rfacts);
//         rfacts = PolyUtil.monic(rfacts);
//         System.out.println("rfacts = " + rfacts);
//         GenPolynomial<AlgebraicNumber<C>> r = rfacts.get(0); 
//         rfacts.remove(r);
//         r = r.multiply(ldcf);
//         rfacts.add(0,r);
//         System.out.println("rfacts = " + rfacts);
//         factors.addAll( rfacts );
        return factors;
    }

}
