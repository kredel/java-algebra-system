/*
 * $Id$
 */

package edu.jas.fd;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * (Non-unique) factorization domain greatest common divisor algorithm
 * interface.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface GreatestCommonDivisor<C extends GcdRingElem<C>> extends Serializable {


    /**
     * GenSolvablePolynomial left greatest common divisor.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    public GenSolvablePolynomial<C> leftGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial right greatest common divisor.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    public GenSolvablePolynomial<C> rightGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial left least common multiple.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return lcm(P,S) with lcm(P,S) = P'*P = S'*S.
     */
    public GenSolvablePolynomial<C> leftLcm(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial right least common multiple.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return lcm(P,S) with lcm(P,S) = P*P' = S*S'.
     */
    public GenSolvablePolynomial<C> rightLcm(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial right content.
     * @param P GenSolvablePolynomial.
     * @return cont(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> rightContent(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial right primitive part.
     * @param P GenSolvablePolynomial.
     * @return pp(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> rightPrimitivePart(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial left content.
     * @param P GenSolvablePolynomial.
     * @return cont(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> leftContent(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial left primitive part.
     * @param P GenSolvablePolynomial.
     * @return pp(P) with cont(P)*pp(P) = P.
     */
    public GenSolvablePolynomial<C> leftPrimitivePart(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial left co-prime list.
     * @param A list of GenSolvablePolynomials.
     * @return B with leftGcd(b,c) = 1 for all b != c in B and for all
     *         non-constant a in A there exists b in B with b|a. B does not
     *         contain zero or constant polynomials.
     */
    public List<GenSolvablePolynomial<C>> leftCoPrime(List<GenSolvablePolynomial<C>> A);


    /**
     * GenSolvablePolynomial test for left co-prime list.
     * @param A list of GenSolvablePolynomials.
     * @return true if leftGcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isLeftCoPrime(List<GenSolvablePolynomial<C>> A);

}
