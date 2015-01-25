/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;
// import org.apache.log4j.Logger;
import edu.jas.gb.SolvableGroebnerBaseAbstract;
import edu.jas.gb.SolvableGroebnerBaseParallel;


/**
 * Module solvable Groebner Bases parallel class. Implements module solvable
 * Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBasePar<C extends RingElem<C>> extends ModSolvableGroebnerBaseSeq<C> {


    //private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBasePar.class);


    //private final boolean debug = logger.isDebugEnabled();


    /*
     * Used Solvable Groebner base algorithm.
     */
    //protected final SolvableGroebnerBaseAbstract<C> sbb;


    /**
     * Constructor.
     */
    public ModSolvableGroebnerBasePar() {
        this(new SolvableGroebnerBaseParallel<C>());
    }


    /**
     * Constructor.
     * @param sbb parallel solvable Groebner base algorithm.
     */
    public ModSolvableGroebnerBasePar(SolvableGroebnerBaseAbstract<C> sbb) {
        super(sbb);
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        sbb.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        return sbb.cancel();
    }

}
