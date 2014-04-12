/*
 * $Id$
 */

package edu.jas.fd;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * (Non-unique) factorization domain greatest common divisor algorithm interface.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface GreatestCommonDivisor<C extends GcdRingElem<C>> extends Serializable {


    /**
     * GenSolvablePolynomial content.
     * @param P GenSolvablePolynomial.
     * @return cont(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> content(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial primitive part.
     * @param P GenSolvablePolynomial.
     * @return pp(P) with pp(P)*cont(P) = P.
     */
    public GenSolvablePolynomial<C> primitivePart(GenSolvablePolynomial<C> P);


    /**
     * GenSolvablePolynomial greatest comon divisor.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    public GenSolvablePolynomial<C> gcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial least comon multiple.
     * @param P GenSolvablePolynomial.
     * @param S GenSolvablePolynomial.
     * @return lcm(P,S).
     */
    public GenSolvablePolynomial<C> lcm(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S);


    /**
     * GenSolvablePolynomial co-prime list.
     * @param A list of GenSolvablePolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a. B does not contain zero or
     *         constant polynomials.
     */
    public List<GenSolvablePolynomial<C>> coPrime(List<GenSolvablePolynomial<C>> A);


    /**
     * GenSolvablePolynomial test for co-prime list.
     * @param A list of GenSolvablePolynomials.
     * @return true if gcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isCoPrime(List<GenSolvablePolynomial<C>> A);

}
