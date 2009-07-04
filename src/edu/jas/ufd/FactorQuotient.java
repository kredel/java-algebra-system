/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Rational function coefficients factorization algorithms.
 * This class implements factorization methods for polynomials over rational functions,
 * that is, with coefficients from class <code>application.Quotient</code>.
 * @author Heinz Kredel
 */

public class FactorQuotient<C extends GcdRingElem<C>> extends FactorAbstract<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(FactorQuotient.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * Factorization engine for normal coefficients.
     */
    protected final FactorAbstract<C> iengine;


    /**
     * No argument constructor. 
     */
    protected FactorQuotient() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor. 
     * @param fac coefficient quotient ring factory.
     */
    public FactorQuotient(QuotientRing<C> fac) {
        super( fac );
        iengine = FactorFactory.<C>getImplementation( fac.ring.coFac );
    }


    /**
     * Constructor. 
     * <b>Note:</b> for convenience, to be able to use *polynom*() methods.
     * @param fac coefficient polynomial factory.
     */
    public FactorQuotient(GenPolynomialRing<C> fac) {
        this( new QuotientRing<C>(fac) );
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> baseFactorsSquarefree(GenPolynomial<Quotient<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<Quotient<C>>> factors = new ArrayList<GenPolynomial<Quotient<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<Quotient<C>> Pr = P;
        Quotient<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        QuotientRing<C> qi = (QuotientRing<C>)pfac.coFac;
        GenPolynomialRing<C> ci = qi.ring;
        GenPolynomialRing<GenPolynomial<C>> ifac = new GenPolynomialRing<GenPolynomial<C>>(ci, pfac);
        GenPolynomial<GenPolynomial<C>> Pi = PolyUfdUtil.<C>integralFromQuotientCoefficients(ifac, Pr);
        //System.out.println("Pi = " + Pi);
        // factor in C[x_1,...,x_n][y]
        List<GenPolynomial<GenPolynomial<C>>> ifacts = polynomFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<Quotient<C>>> rfacts = PolyUfdUtil.<C>quotientFromIntegralCoefficients(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        rfacts = PolyUtil.monic(rfacts);
        //System.out.println("rfacts = " + rfacts);
        if ( !ldcf.isONE() ) {
            GenPolynomial<Quotient<C>> r = rfacts.get(0);
            rfacts.remove(r);
            r = r.multiply(ldcf);
            rfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<Quotient<C>>> factorsSquarefree(GenPolynomial<Quotient<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<Quotient<C>>> factors = new ArrayList<GenPolynomial<Quotient<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        GenPolynomial<Quotient<C>> Pr = P;
        Quotient<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pr = Pr.monic();
        }
        QuotientRing<C> qi = (QuotientRing<C>)pfac.coFac;
        GenPolynomialRing<C> ci = qi.ring;
        GenPolynomialRing<GenPolynomial<C>> ifac = new GenPolynomialRing<GenPolynomial<C>>(ci, pfac);
        GenPolynomial<GenPolynomial<C>> Pi = PolyUfdUtil.<C>integralFromQuotientCoefficients(ifac, Pr);
        //System.out.println("Pi = " + Pi);
	// factor in C[x_1,...,x_n][y_1,...,y_m]
        List<GenPolynomial<GenPolynomial<C>>> ifacts = polynomFactorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        List<GenPolynomial<Quotient<C>>> rfacts = PolyUfdUtil.<C>quotientFromIntegralCoefficients(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        rfacts = PolyUtil.monic(rfacts);
        //System.out.println("rfacts = " + rfacts);
        if ( !ldcf.isONE() ) {
            GenPolynomial<Quotient<C>> r = rfacts.get(0);
            rfacts.remove(r);
            r = r.multiply(ldcf);
            rfacts.add(0, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * <b>Note:</b> can not be placed in its own class,
     * since GenPolynomial doesn't implement GcdRingElem.
     * @param P squarefree GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<GenPolynomial<GenPolynomial<C>>> polynomFactorsSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<GenPolynomial<C>>> factors = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        GenPolynomialRing<C> qi = (GenPolynomialRing<C>)pfac.coFac;
        GenPolynomialRing<C> ifac = qi.extend(pfac.nvar);
        GenPolynomial<C> Pi = PolyUtil.<C>distribute(ifac, P);
        //System.out.println("Pi = " + Pi);

        C ldcf = Pi.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            //System.out.println("ldcf = " + ldcf);
            Pi = Pi.monic();
        }

        // factor in C[x_1,...,x_n,y_1,...,y_m]
        List<GenPolynomial<C>> ifacts = iengine.factorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        if ( !ldcf.isONE() ) {
            GenPolynomial<C> r = ifacts.get(0);
            ifacts.remove(r);
            r = r.multiply(ldcf);
            ifacts.add(0, r);
        }
        List<GenPolynomial<GenPolynomial<C>>> rfacts = PolyUtil.<C>recursive(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }

}





