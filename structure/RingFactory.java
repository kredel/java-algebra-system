/*
 * $Id$
 */

package edu.jas.structure;

import java.math.BigInteger;
import java.io.Reader;
import java.io.Serializable;
import java.util.Random;

/**
 * RingFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface RingFactory<C extends RingElem> extends Serializable {


    /**
     * Get the constant zero for the RingElem.
     * @return 0.
     */
    public C getZERO();


    /**
     * Get the constant one for the RingElem.
     * @return 1.
     */
    public C getONE();


    /**
     * Get the RingElem for a.
     * @param a
     * @return a: RingElem.
     */
    public C fromInteger(long a);


    /**
     * Get the RingElem for a.
     * @param a
     * @return a: RingElem.
     */
    public C fromInteger(BigInteger a);


    /**
     * Generate a random RingElem with size less equal to n.
     * @param n
     * @return a random element.
     */
    public C random(int n);


    /**
     * Generate a random RingElem with size less equal to n.
     * @param n
     * @param random is a source for random bits.
     * @return a random element.
     */
    public C random(int n, Random random);


    /**
     * Create a copy of RingElem c.
     * @param c
     * @return a copy of c.
     */
    public C copy(C c);


    /**
     * Parse from String.
     * @param s String.
     * @return a RingElem corresponding to s.
     */
    public C parse(String s);


    /**
     * Parse from Reader.
     * @param r Reader.
     * @return the next RingElem found on r.
     */
    public C parse(Reader r);

}
