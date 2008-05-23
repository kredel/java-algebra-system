/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;

import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.ProductRing;
import edu.jas.structure.Product;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;
import edu.jas.ring.GroebnerBaseAbstract;
import edu.jas.ring.GroebnerBasePseudoSeq;
import edu.jas.ring.OrderedRPairlist;
import edu.jas.ring.RGroebnerBaseSeq;
import edu.jas.ring.RPseudoReduction;
import edu.jas.ring.Pair;
import edu.jas.ring.RPseudoReductionSeq;

import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GCDFactory;

import edu.jas.application.PolyUtilApp;
import edu.jas.application.PolyUtilComp;


/**
 * Comprehensive Groebner Base sequential algorithm.
 * Implements C-Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ComprehensiveGroebnerBaseSeq<C extends GcdRingElem<C>>
             /*extends GroebnerBaseAbstract<GenPolynomial<C>>*/  {


    private static final Logger logger = Logger.getLogger(ComprehensiveGroebnerBaseSeq.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Flag if gcd engine should be used.
     */
    private final boolean notFaithfull = false;


    /**
     * Comprehensive reduction engine.
     */
    protected final CReductionSeq<C> cred;  


    /**
     * Polynomial coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf base coefficient ring factory.
     */
    public ComprehensiveGroebnerBaseSeq(RingFactory<C> rf) {
        this( new CReductionSeq<C>(), rf );
    }


    /**
     * Constructor.
     * @param red C-pseuso-Reduction engine
     * @param rf base coefficient ring factory.
     */
    @SuppressWarnings("unchecked") 
    public ComprehensiveGroebnerBaseSeq(CReductionSeq<C> red, 
                                        RingFactory<C> rf) {
        //super(null); // red not possible since type of type
        cred = red;
        cofac = rf;
        // selection for C but used for R:
        engine = (GreatestCommonDivisorAbstract<R>) GCDFactory.<C>getImplementation( cofac );
        //not used: engine = (GreatestCommonDivisorAbstract<C>)GCDFactory.<R>getProxy( rf );
        // protected final GroebnerBase<Residue<C>> bb;
        // don't know here: bb = new GroebnerBasePseudoSeq<Residue<C>>( cofac );
        //cred = new RComprehensivePseudoReductionSeq<R>();
    }


    /**
     * Comprehensive-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {  
        if ( F == null || F.size() == 0 ) {
           return true;
        }



        return true;
    }


    /**
     * R-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    //@Override
    //@SuppressWarnings("unchecked") 
    public List<GenPolynomial<GenPolynomial<C>>> 
             GB( int modv, 
                 List<GenPolynomial<GenPolynomial<C>>> F ) {  
        if ( F == null ) {
           return F;
        }
        return F;
    }

}
