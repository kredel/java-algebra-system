/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.List;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GBFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Module Groebner Bases sequential algorithm. Implements Groebner bases and GB
 * test.
 * @author Heinz Kredel
 */

public class ModGroebnerBasePar<C extends GcdRingElem<C>> extends ModGroebnerBaseSeq<C> {


    //private static final Logger logger = Logger.getLogger(ModGroebnerBasePar.class);


    /*
     * Used Groebner base algorithm.
     */
    //protected final GroebnerBaseAbstract<C> bb;


    /**
     * Constructor.
     * @param cf coefficient ring.
     */
    public ModGroebnerBasePar(RingFactory<C> cf) {
        this(GBFactory.getProxy(cf));
    }


    /**
     * Constructor.
     * @param bb Groebner base algorithm.
     */
    public ModGroebnerBasePar(GroebnerBaseAbstract<C> bb) {
        super(bb);
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
        bb.terminate();
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        return bb.cancel();
    }

}
