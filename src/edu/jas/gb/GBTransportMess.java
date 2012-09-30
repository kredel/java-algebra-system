/*
 * $Id$
 */

package edu.jas.gb;


import java.io.Serializable;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Distributed GB transport message.  This class and its subclasses
 * are used for transport of polynomials and pairs and as markers in
 * distributed GB algorithms.
 */

public class GBTransportMess implements Serializable {


    public GBTransportMess() {
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return this.getClass().getName();
    }
}


/**
 * Distributed GB transport message for requests.
 */

final class GBTransportMessReq extends GBTransportMess {


    public GBTransportMessReq() {
    }
}


/**
 * Distributed GB transport message for termination.
 */

final class GBTransportMessEnd extends GBTransportMess {


    public GBTransportMessEnd() {
    }
}


/**
 * Distributed GB transport message for polynomial.
 */

final class GBTransportMessPoly<C extends RingElem<C>> extends GBTransportMess {


    /**
     * The polynomial to transport.
     */
    public final GenPolynomial<C> pol;


    /**
     * GBTransportMessPoly.
     * @param p polynomial to transfered.
     */
    public GBTransportMessPoly(GenPolynomial<C> p) {
        this.pol = p;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + pol + " )";
    }
}


/**
 * Distributed GB transport message for pairs.
 */

final class GBTransportMessPair<C extends RingElem<C>> extends GBTransportMess {


    public final Pair<C> pair;


    /**
     * GBTransportMessPair.
     * @param p pair for transfer.
     */
    public GBTransportMessPair(Pair<C> p) {
        this.pair = p;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + pair + " )";
    }
}


/**
 * Distributed GB transport message for index pairs.
 */

final class GBTransportMessPairIndex extends GBTransportMess {


    public final Integer i;


    public final Integer j;


    /**
     * GBTransportMessPairIndex.
     * @param p pair for transport.
     */
    public GBTransportMessPairIndex(Pair p) {
        this(Integer.valueOf(p.i),Integer.valueOf(p.j));
    }


    /**
     * GBTransportMessPairIndex.
     * @param i first index.
     * @param j second index.
     */
    public GBTransportMessPairIndex(int i, int j) {
        this(Integer.valueOf(i),Integer.valueOf(j));
    }


    /**
     * GBTransportMessPairIndex.
     * @param i first index.
     * @param j second index.
     */
    public GBTransportMessPairIndex(Integer i, Integer j) {
        this.i = i;
        this.j = j;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "( " + i + "," + j + " )";
    }

}
